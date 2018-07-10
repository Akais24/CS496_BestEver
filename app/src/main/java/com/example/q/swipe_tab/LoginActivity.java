package com.example.q.swipe_tab;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {
    private CallbackManager callbackManager;

    String server_url = "http://52.231.66.36:60722/api/";
    ProgressDialog mProgressDialog;

    Gson gson = new Gson();

    public static Activity loginactivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences test = getSharedPreferences("local", MODE_PRIVATE);
        final String username = test.getString("alias", "noname");
        if(!username.equals("noname")){
            Intent main = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(main);
            finish();
        }

        setContentView(R.layout.activity_login);
        loginactivity = LoginActivity.this;

        callbackManager = CallbackManager.Factory.create();

        mProgressDialog = new ProgressDialog(this);

        LoginButton loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest graphRequest
                        = GraphRequest.newMeRequest(
                        loginResult.getAccessToken()
                        , new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.v("result", object.toString());
                                String unique_id = null;
                                try {
                                    unique_id = object.getString("id");
                                    Log.v("result_id", object.getString("id"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                if(unique_id == null){
                                    Toast.makeText(getApplicationContext(),"로그인이 정상적으로 이루어지지 않았습니다", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                mProgressDialog.setMessage("서버에 접속중입니다");
                                mProgressDialog.show();

                                String id_url = server_url + "id/" + unique_id;
                                final String finalUnique_id = unique_id;
                                Ion.with(getApplicationContext())
                                        .load("GET", id_url)
                                        .asJsonObject()
                                        .setCallback(new FutureCallback<JsonObject>() {
                                            @Override
                                            public void onCompleted(Exception e, JsonObject result) {
                                                response_message newone = gson.fromJson(result, response_message.class);
                                                if(newone.result.equals("Failure")){
                                                    Log.d("hahaha", "no user");
                                                    Intent newuser = new Intent(LoginActivity.this, NewUserActivity.class);
                                                    newuser.putExtra("unique_id", finalUnique_id);
                                                    startActivity(newuser);
                                                    mProgressDialog.hide();
                                                }
                                                if(newone.result.equals("Success")){
                                                    Log.d("hahaha", String.valueOf(result));
                                                    Log.d("hahaha", "find user : " + newone.content);
                                                    mProgressDialog.hide();

                                                    SharedPreferences pref = getSharedPreferences("local", Activity.MODE_PRIVATE);
                                                    SharedPreferences.Editor editor = pref.edit();
                                                    editor.putString("alias", newone.content);
                                                    editor.commit();

                                                    Intent main = new Intent(LoginActivity.this, MainActivity.class);
                                                    startActivity(main);
                                                    finish();
                                                }
                                            }
                                        });
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();
            }

            @Override
            public void onCancel() {
                Log.e("Login Canceled", "");
            }

            @Override
            public void onError(FacebookException error) {
                Log.e("LoginErr", error.toString());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
