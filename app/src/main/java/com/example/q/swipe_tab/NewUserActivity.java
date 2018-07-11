package com.example.q.swipe_tab;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class NewUserActivity extends AppCompatActivity {
    Gson gson = new Gson();

    ProgressDialog mProgressDialog;
    String unique_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        final Intent recv = getIntent();
        unique_id = recv.getStringExtra("unique_id");

        final String server_url = "http://52.231.66.36:60722/api/";
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("서버에 연결 중입니다");

        final EditText edittext=(EditText)findViewById(R.id.edittext);
        Button button=(Button)findViewById(R.id.ok_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String alias = String.valueOf(edittext.getText());
                Log.d("ALIAS ", alias);

                final String user_url = server_url + "user/" + alias;

                mProgressDialog.show();
                Ion.with(getApplicationContext())
                        .load("GET", user_url)
                        .asJsonObject()
                        .setCallback(new FutureCallback<JsonObject>() {
                            @Override
                            public void onCompleted(Exception e, JsonObject result) {
                                final response_message newone = gson.fromJson(result, response_message.class);
                                if(newone.result.equals("Failure")){
                                    JsonObject json = new JsonObject();
                                    json.addProperty("fbid", unique_id);
                                    //make user
                                    Ion.with(getApplicationContext())
                                            .load("POST", user_url)
                                            .setJsonObjectBody(json)
                                            .asJsonObject()
                                            .setCallback(new FutureCallback<JsonObject>() {
                                                @Override
                                                public void onCompleted(Exception e, JsonObject result) {
                                                    response_message recv_mess = gson.fromJson(result, response_message.class);
                                                    if(recv_mess.result.equals("Failure")){
                                                        Toast.makeText(getApplicationContext(),"서버에 계정 추가를 실패하였습니다", Toast.LENGTH_SHORT).show();
                                                        mProgressDialog.hide();
                                                    }else if(recv_mess.result.equals("Success")){
                                                        mProgressDialog.hide();
                                                        LoginActivity loginActivity = (LoginActivity) LoginActivity.loginactivity;
                                                        loginActivity.finish();

                                                        SharedPreferences pref = getSharedPreferences("local", Activity.MODE_PRIVATE);
                                                        SharedPreferences.Editor editor = pref.edit();
                                                        editor.putString("alias", alias);
                                                        editor.commit();

                                                        Intent main = new Intent(NewUserActivity.this, MainActivity.class);
                                                        startActivity(main);
                                                        finish();
                                                        //GO to main
                                                    }
                                                }
                                            });
                                }
                                if(newone.result.equals("Success")){
                                    Toast.makeText(getApplicationContext(),"이미 존재하는 닉네임입니다. 다른 닉네임을 입력해주시길 바랍니다", Toast.LENGTH_SHORT).show();
                                    mProgressDialog.hide();
                                }
                            }
                        });
            }
        });
    }
}
