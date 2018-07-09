package com.example.q.swipe_tab.Fragment2;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Spinner;

import com.example.q.swipe_tab.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Fragment2_Origin_Popup extends AppCompatActivity {
    Intent intent;
    String mBasepath;
    int position;

    OriginImagePagerAdapter mOriginAdapter;
    static ViewPager mViewPager;

    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment2_origin_popup);
        intent = getIntent();

        android.support.v7.widget.Toolbar mToolbar = findViewById(R.id.popup_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mBasepath = intent.getStringExtra("directory");
        position = intent.getIntExtra("position", 1);

        mOriginAdapter = new OriginImagePagerAdapter(getSupportFragmentManager(), mBasepath);

        mViewPager = findViewById(R.id.origin_image_pager);
        mViewPager.setAdapter(mOriginAdapter);
        mViewPager.setCurrentItem(position);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent();
        intent.putExtra("last_position", mViewPager.getCurrentItem());
        setResult(RESULT_OK, intent);
        Log.d("last_position : ", String.valueOf(mViewPager.getCurrentItem()));
        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
                finish();
                return true;
            }
            case R.id.menu_detail:
                String image_path = mOriginAdapter.getnthpath(mViewPager.getCurrentItem());
                new AlertDialog.Builder(this, R.style.MyAlertDialogStyle)
                    .setTitle("상세 정보")
                    .setMessage(image_path)
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //finish();
                        }
                    })
                    .setCancelable(false).create().show();
                return true;
            case R.id.menu_deletion:
                new AlertDialog.Builder(this, R.style.MyAlertDialogStyle)
                        .setMessage("삭제하시겠습니까?")
                        .setNeutralButton("네", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                int index = mViewPager.getCurrentItem();
                                String path = mOriginAdapter.getnthpath(index);
                                File target= new File(path);
                                target.delete();

                                Intent intent = getIntent();
                                finish();
                                startActivity(intent);
                            }
                        })
                        .setPositiveButton("아니요", null)
                        .setCancelable(true).create().show();
                return true;
            case R.id.menu_share:
                int index = mViewPager.getCurrentItem();
                String path = mOriginAdapter.getnthpath(index);
                Uri uri = FileProvider.getUriForFile(getApplicationContext(), "com.example.q.swipe_tab", new File(path));

                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                sharingIntent.setType("image/*");

                List<ResolveInfo> resInfo = getPackageManager().queryIntentActivities(sharingIntent, 0);
                if (resInfo.isEmpty()) {
                    Log.i("###", "공유할 수 있는 앱 없음");
                    return true;
                }

                List<Intent> targetedShareIntents = new ArrayList<>();

                for (ResolveInfo resolveInfo : resInfo) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    Intent targetedShareIntent = new Intent(android.content.Intent.ACTION_SEND);
                    targetedShareIntent.setType("image/*");

                    // 페이스북, 카카오톡, 카카오 스토리만 표시
                    if (packageName.contains("com.facebook.katana") || packageName.contains("com.kakao.talk") || packageName.contains("com.kakao.story")) {
                        ComponentName name = new ComponentName(packageName, resolveInfo.activityInfo.name);
                        targetedShareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                        targetedShareIntent.setComponent(name);
                        targetedShareIntent.setPackage(packageName);
                        targetedShareIntents.add(targetedShareIntent);
                    }
                }

                Intent chooserIntent = Intent.createChooser(targetedShareIntents.remove(0), "공유하기");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray(new Parcelable[targetedShareIntents.size()]));
                startActivity(chooserIntent);

//                sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
//                startActivity(Intent.createChooser(sharingIntent, "Share Image Using"));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume(){
        super.onResume();
        mOriginAdapter.notifyDataSetChanged();
    }

}
