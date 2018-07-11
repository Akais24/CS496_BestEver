package com.example.q.swipe_tab;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.login.LoginManager;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import jp.wasabeef.glide.transformations.CropSquareTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class MyProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_iMAGE = 2;
    SharedPreferences test;

    TextView alias;
    ImageView profile_image;
    ImageView change;
    private Uri mImageCaptureUri;

    File storageDir = new File(Environment.getExternalStorageDirectory() + "/Pictures", "hw1_test");

    String server_url = "http://52.231.66.36:60722/api/profile/";
    ProgressDialog mProgressDialog;

    Gson gson = new Gson();

    MultiTransformation multi = new MultiTransformation(
            new CropSquareTransformation(),
            new RoundedCornersTransformation(400, 0)
    );


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        test = getSharedPreferences("local", MODE_PRIVATE);
        final String username = test.getString("alias", "noname");
        final boolean isprofile = test.getBoolean("profile", false);
        boolean profileset = test.getBoolean("profileset", false);

        alias = findViewById(R.id.alias);
        alias.setText(username);
        change = findViewById(R.id.change_image);
        profile_image = findViewById(R.id.profile_image);
        String target = test.getString("profile_path", null);
        if(target != null) {
            final Uri path = Uri.parse(target);
            if (path != null) {
                Glide.with(getApplicationContext()).load(path)
                        .apply(RequestOptions.overrideOf(400, 400))
                        .apply(RequestOptions.bitmapTransform(multi))
                        .into(profile_image);
            }
        }

        CardView logout = findViewById(R.id.logout_btn);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainactivity = (MainActivity) MainActivity.main_activity;
                mainactivity.finish();

                SharedPreferences.Editor editor = test.edit();
                editor.remove("alias");
                editor.remove("life");
                editor.remove("point");
                editor.commit();

                LoginManager.getInstance().logOut();

                Intent login = new Intent(MyProfileActivity.this, LoginActivity.class);
                startActivity(login);
                finish();
            }
        });

        change.setOnClickListener(this);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Communicating");

        Log.d("44444", "Reset profile " + String.valueOf(profileset) + String.valueOf(isprofile));
        if(!profileset && isprofile){
            mProgressDialog.show();
            Ion.with(this)
                    .load("GET", server_url + username)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            profile newdown = gson.fromJson(result, profile.class);
                            if(newdown.data.equals("undefined")) return;

                            File storageDir2 = new File(Environment.getExternalStorageDirectory() + "/Pictures", "hw1_test_temp");
                            if(!storageDir2.exists()) storageDir2.mkdir();
                            String file_path = storageDir2.getPath() + "/" + "profile.jpg";

                            final byte[] data = Base64.decode(newdown.data, Base64.DEFAULT);
                            Bitmap decodedBitmap = BitmapFactory.decodeByteArray (data, 0, data.length);

                            File fileCacheItem = new File(file_path);
                            OutputStream out = null;

                            try {
                                fileCacheItem.createNewFile();
                                out = new FileOutputStream(fileCacheItem);
                                decodedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                                out.close();
                                Glide.with(getApplicationContext()).load(fileCacheItem)
                                        .apply(RequestOptions.overrideOf(400, 400))
                                        .apply(RequestOptions.bitmapTransform(multi))
                                        .into(profile_image);
                                SharedPreferences.Editor edit = test.edit();
                                edit.putBoolean("profileset", true);
                                edit.commit();

                            }catch (Exception e3) {
                                e3.printStackTrace();
                            }

                            mProgressDialog.hide();
                        }
                    });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.change_image:
                Intent get = new Intent(Intent.ACTION_PICK);
                get.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(get, PICK_FROM_ALBUM);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_OK) return;

        switch (requestCode){
            case PICK_FROM_ALBUM:
                mProgressDialog.show();
                mImageCaptureUri = data.getData();

                Log.d("44444", mImageCaptureUri.getPath());
                Glide.with(getApplicationContext()).load(mImageCaptureUri)
                        .apply(RequestOptions.overrideOf(400, 400))
                        .apply(RequestOptions.bitmapTransform(multi))
                        .into(profile_image);

                SharedPreferences test = getSharedPreferences("local", MODE_PRIVATE);
                final String username = test.getString("alias", "noname");
                SharedPreferences.Editor edit = test.edit();
                edit.putString("profile_path", String.valueOf(mImageCaptureUri));
                edit.putBoolean("profile", true);
                edit.putBoolean("profileset", true);
                edit.commit();

                JsonObject json = new JsonObject();
                try {
                    Bitmap bm = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageCaptureUri);
                    File fileCacheItem = new File(storageDir.getPath() + "/temp_profile.jpg");
                    OutputStream out = null;
                    fileCacheItem.createNewFile();
                    out = new FileOutputStream(fileCacheItem);
                    bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.close();

                    String encodedBase64 = null;
                    FileInputStream fileInputStreamReader = new FileInputStream(fileCacheItem);
                    byte[] bytes = new byte[(int)fileCacheItem.length()];
                    fileInputStreamReader.read(bytes);
                    encodedBase64 = Base64.encodeToString(bytes, Base64.DEFAULT);
                    json.addProperty("data", encodedBase64);
                    fileCacheItem.delete();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Ion.with(this)
                        .load("PUT", server_url + username)
                        .setJsonObjectBody(json)
                        .asJsonObject()
                        .setCallback(new FutureCallback<JsonObject>() {
                            @Override
                            public void onCompleted(Exception e, JsonObject result) {
                                Log.d("4444", String.valueOf(result));
                                response_message newone = gson.fromJson(result, response_message.class);
                                if(!newone.result.equals("Success")){
                                    Toast.makeText(getApplicationContext(), "서버에 프로필 사진 업로드에 실패하였습니다", Toast.LENGTH_SHORT).show();
                                }
                                mProgressDialog.hide();
                            }
                        });
                break;
            case CROP_FROM_iMAGE:
                Log.d("44444", "anser for crop");
                final Bundle extras = data.getExtras();
                String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+
                        "/SmartWheel/"+System.currentTimeMillis()+".jpg";
                if(extras != null){
                    Bitmap photo = extras.getParcelable("data"); // CROP된 BITMAP
                    profile_image.setImageBitmap(photo); // 레이아웃의 이미지칸에 CROP된 BITMAP을 보여줌
                    break;
                }
                File f = new File(mImageCaptureUri.getPath());
                if(f.exists()) f.delete();
                break;
        }

    }
}
