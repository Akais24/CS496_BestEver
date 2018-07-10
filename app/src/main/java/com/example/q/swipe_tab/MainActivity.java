package com.example.q.swipe_tab;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.q.swipe_tab.Fragment1.Fragment1;
import com.example.q.swipe_tab.Fragment2.Fragment2_main;
import com.example.q.swipe_tab.Fragment3.Fragment3_main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSION = 1111;

    private ViewPager mViewPager;
    android.support.v7.app.ActionBar bar;
    private FragmentManager fm;
    private ArrayList<Fragment> fList;

    TabLayout tabs;

    private long backKeyPressedTime = 0;
    private Toast toast;

    private String[] permissions;

    public static Activity main_activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        main_activity = MainActivity.this;

        permissions = new String[3];
        permissions[0] = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        permissions[1] =  Manifest.permission.CAMERA;
        permissions[2] = Manifest.permission.INTERNET;
        checkPermission();

        setContentView(R.layout.main_actionbar);

        // Define the viewpager to swipe
        mViewPager = findViewById(R.id.pager);
        fm = getSupportFragmentManager();

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        bar = getSupportActionBar();
        bar.setDisplayShowTitleEnabled(true);
        bar.setTitle("CS496_BestEver");

        tabs = findViewById(R.id.tabs);
        tabs.addTab(tabs.newTab().setText("Contact"));
        tabs.addTab(tabs.newTab().setText("Gallery"));
        tabs.addTab(tabs.newTab().setText("C TAB"));
        tabs.setupWithViewPager(mViewPager);

        fList = new ArrayList<Fragment>();
        fList.add(Fragment1.newInstance());
        fList.add(Fragment2_main.newInstance());
        fList.add(Fragment3_main.newInstance());

        CustomFragmentPagerAdapter adapter = new CustomFragmentPagerAdapter(fm, fList);
        mViewPager.setAdapter(adapter);

        tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                mViewPager.setCurrentItem(position);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void checkPermission() {
        if (checkselfpermission(permissions)) {
            // 처음 호출시엔 if()안의 부분은 false로 리턴 됨 -> else{..}의 요청으로 넘어감
            if (checkfirstpermission(permissions)) {
                new AlertDialog.Builder(this)
                        .setTitle("알림")
                        .setMessage("일부 권한이 거부되었습니다. 사용을 원하시면 설정에서 해당 권한을 직접 허용하셔야 합니다.")
                        .setNeutralButton("설정", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:" + getPackageName()));
                                startActivity(intent);
                            }
                        })
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //finish();
                            }
                        })
                        .setCancelable(false).create().show();
            } else {
                ActivityCompat.requestPermissions(this, permissions, MY_PERMISSION);
            }
        }
    }

    private boolean checkselfpermission(String[] permissions){
        for(int i=0; i<permissions.length; i++){
            if (ContextCompat.checkSelfPermission(this, permissions[i]) != PackageManager.PERMISSION_GRANTED)
                return true;
        }
        return false;
    }

    private boolean checkfirstpermission(String[] permissions){
        for(int i=0; i<permissions.length; i++){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i]))
                return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION:
                for (int grantResult : grantResults) {
                    // grantResults[] : 허용된 권한은 0, 거부한 권한은 -1
                    if (grantResult < 0) {
                        Toast.makeText(this, "해당 권한을 활성화 하셔야 합니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                // 허용했다면 이 부분에서..

                break;
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("MAIN REQUEST CODE : ", String.valueOf(requestCode));
        fList.get(1).onActivityResult(requestCode, resultCode, data);
    }

    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            showGuide();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            toast.cancel();

            finish();
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    public void showGuide() {
        toast = Toast.makeText(getApplicationContext(), "한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
        toast.show();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_detail:
                Intent profile = new Intent(MainActivity.this, MyProfileActivity.class);
                startActivity(profile);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}