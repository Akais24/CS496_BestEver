package com.example.q.swipe_tab;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.login.LoginManager;

public class MyProfileActivity extends AppCompatActivity {
    SharedPreferences test;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        test = getSharedPreferences("local", MODE_PRIVATE);
        final String username = test.getString("alias", "noname");

        TextView alias = findViewById(R.id.alias);
        alias.setText(username);

        Button logout = findViewById(R.id.logout_btn);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainactivity = (MainActivity) MainActivity.main_activity;
                mainactivity.finish();

                SharedPreferences.Editor editor = test.edit();
                editor.remove("alias");

                LoginManager.getInstance().logOut();

                Intent login = new Intent(MyProfileActivity.this, LoginActivity.class);
                startActivity(login);
                finish();

            }
        });
    }

}
