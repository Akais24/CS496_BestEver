package com.example.q.swipe_tab.Fragment2;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.q.swipe_tab.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Fragment2_main extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private static final int REQUEST_TAKE_PHOTO = 2222;
    private static final int OPEN_ORIGIN_IMAGE = 3333;

    private Animation fab_open, fab_close;
    private Boolean isFabOpen = false;
    private FloatingActionButton fab, fab_x, fab1, fab2, fab3;
    RecyclerView rv;
    SwipeRefreshLayout srl;

    private LinearLayout fab1_ex, fab2_ex, fab3_ex;
    TextView cover;

    String mCurrentPhotoPath = null;
    Uri imageUri;

    mRecyclerAdapter adapter;
    SwipeRefreshLayout mSwipeRefreshLayout;

    boolean isCamera = false;
    boolean isStorage = false;

    File storageDir = new File(Environment.getExternalStorageDirectory() + "/Pictures", "hw1_test");

    public static Fragment2_main newInstance(){
        return new Fragment2_main();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        //return super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment2_floating_recycler, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInsatnceState) {
        super.onActivityCreated(savedInsatnceState);

        View model = getView();

        // Make Floating Action Buttons and relatives
        fab_open = AnimationUtils.loadAnimation(getContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getContext(), R.anim.fab_close);

        cover = model.findViewById(R.id.cover);

        fab = model.findViewById(R.id.fab);
        fab_x = model.findViewById(R.id.fab_x);
        fab1 = model.findViewById(R.id.fab1);
        fab1_ex = model.findViewById(R.id.fab1_ex);
        fab2 = model.findViewById(R.id.fab2);
        fab2_ex = model.findViewById(R.id.fab2_ex);
        fab3 = model.findViewById(R.id.fab3);
        fab3_ex = model.findViewById(R.id.fab3_ex);

        srl = model.findViewById(R.id.swipe_layout);
        rv = model.findViewById(R.id.recycler_view);

        Log.d("CCCCCCCCCCCCC", "reated");
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            cover.setText("저장소 권한을 설정하지 않아 갤러리를 불러올 수 없습니다");
            cover.setVisibility(View.VISIBLE);
            srl.setVisibility(View.INVISIBLE);
            fab.setVisibility(View.INVISIBLE);
            isStorage = false;
            return;
        }else{
            cover.setText("");
            cover.setVisibility(View.INVISIBLE);
            srl.setVisibility(View.VISIBLE);
            fab.setVisibility(View.VISIBLE);
            isStorage = true;
        }

        fab.setOnClickListener(this);
        cover.setOnClickListener(this);
        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);
        fab3    .setOnClickListener(this);

        int numOfColums = 3;
        rv.setLayoutManager(new GridLayoutManager(getContext(), numOfColums));
        adapter = new mRecyclerAdapter(getContext(), storageDir.getPath(), getActivity());
        rv.setAdapter(adapter);

        mSwipeRefreshLayout = model.findViewById(R.id.swipe_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    public void onRefresh(){
        if(isStorage) {
            adapter.refresh();
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    public void onResume() {
        super.onResume();
        if(isStorage) {
            adapter.refresh();
        }
    }

    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.fab:
            case R.id.cover:
                anim();
                break;
            case R.id.fab_x:
                anim();
            case R.id.fab1:
                anim();
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getContext(), "카메라 권한을 허용하지 않아 카메라를 사용할 수 없습니다", Toast.LENGTH_SHORT).show();
                    isCamera = false;
                } else {
                    captureCamera();
                    isCamera = true;
                }
                break;
            case R.id.fab2:
                anim();
                Toast.makeText(getContext(), "ETC function is not complete", Toast.LENGTH_SHORT).show();
                break;
            case R.id.fab3:
                anim();
                Toast.makeText(getContext(), "WEB function is not complete", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void anim() {
        if (isFabOpen) {
            fab.setVisibility(View.VISIBLE);
            fab_x.setVisibility(View.INVISIBLE);
            fab1_ex.startAnimation(fab_close);
            fab2_ex.startAnimation(fab_close);;
            fab3_ex.startAnimation(fab_close);
            fab1_ex.setClickable(false);
            fab2_ex.setClickable(false);
            fab3_ex.setClickable(false);
            cover.setVisibility(View.INVISIBLE);
            isFabOpen = false;
        } else {
            fab.setVisibility(View.INVISIBLE);
            fab_x.setVisibility(View.VISIBLE);
            fab1_ex.startAnimation(fab_open);
            fab2_ex.startAnimation(fab_open);
            fab3_ex.startAnimation(fab_open);
            fab1_ex.setClickable(true);
            fab2_ex.setClickable(true);
            fab3_ex.setClickable(true);
            cover.setVisibility(View.VISIBLE);
            isFabOpen = true;
        }
    }

    private void captureCamera() {
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    Log.e("captureCamera Error", ex.toString());
                }
                if (photoFile != null) {
                    // getUriForFile의 두 번째 인자는 Manifest provier의 authorites와 일치해야 함
                    Uri providerURI = FileProvider.getUriForFile(getContext(), getActivity().getPackageName(), photoFile);
                    imageUri = providerURI;
                    // 인텐트에 전달할 때는 FileProvier의 Return값인 content://로만!!, providerURI의 값에 카메라 데이터를 넣어 보냄
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, providerURI);

                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                }
            }
        } else {
            Toast.makeText(getContext(), "저장공간이 접근 불가능한 기기입니다", Toast.LENGTH_SHORT).show();
        }
    }

    public File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + ".jpg";
        File imageFile;

        if (!storageDir.exists()) {
            Log.i("mCurrentPhotoPath1", storageDir.toString());
            storageDir.mkdirs();
        }

        imageFile = new File(storageDir, imageFileName);
        mCurrentPhotoPath = imageFile.getAbsolutePath();

        return imageFile;
    }

    private void galleryAddPic(){
        Log.i("galleryAddPic", "Call");
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        // 해당 경로에 있는 파일을 객체화(새로 파일을 만든다는 것으로 이해하면 안 됨)
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        getActivity().sendBroadcast(mediaScanIntent);
        Toast.makeText(getContext(), "사진을 앨범에 저장했당", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("REQUEST CODE : ", String.valueOf(requestCode));
        switch (requestCode) {
            case REQUEST_TAKE_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        Log.i("REQUEST_TAKE_PHOTO", "OK");
                        galleryAddPic();

                        Log.d("CheckURI", String.valueOf(imageUri));
                    } catch (Exception e) {
                        Log.e("REQUEST_TAKE_PHOTO", e.toString());
                    }
                } else {
                    Toast.makeText(getContext(), "사진찍기를 취소하였습니다.", Toast.LENGTH_SHORT).show();
                }
                break;
            case OPEN_ORIGIN_IMAGE:
                if(resultCode == Activity.RESULT_OK){
                    int currentpostion = data.getIntExtra("last_position", 1);
                    rv.getLayoutManager().scrollToPosition(currentpostion);
                }
        }
    }
}
