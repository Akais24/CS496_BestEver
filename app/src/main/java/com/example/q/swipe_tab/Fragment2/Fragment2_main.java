package com.example.q.swipe_tab.Fragment2;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.util.Base64;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

    String server_url = "http://52.231.66.36:60722/api/image";
    ProgressDialog mProgressDialog;

    Gson gson = new Gson();


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
        fab3.setOnClickListener(this);

        int numOfColums = 3;
        rv.setLayoutManager(new GridLayoutManager(getContext(), numOfColums));
        adapter = new mRecyclerAdapter(getContext(), storageDir.getPath(), getActivity());
        rv.setAdapter(adapter);

        mSwipeRefreshLayout = model.findViewById(R.id.swipe_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage("Communicating");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
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
                download();
                anim();
                //Toast.makeText(getContext(), "DOWNLOAD Complete", Toast.LENGTH_SHORT).show();
                break;
            case R.id.fab3:
                upload();
                anim();
                //Toast.makeText(getContext(), "UPLOAD Complete", Toast.LENGTH_SHORT).show();
                break;
        }
    }


    public void upload(){
        SharedPreferences test = getActivity().getSharedPreferences("local", getActivity().MODE_PRIVATE);
        final String username = test.getString("alias", "noname");
        String getlistaddr = server_url + "list/" + username;

        Log.d("Ion Connecting to", getlistaddr);
        final ArrayList<ImageItem> saved = new ArrayList<>();

        mProgressDialog.setMessage("업로드 중입니다");
        mProgressDialog.show();
        Ion.with(getContext())
                .load("GET", getlistaddr)
                .asJsonArray()
                .setCallback(new FutureCallback<JsonArray>() {
                    @Override
                    public void onCompleted(Exception e, JsonArray result) {
                        for (int i = 0; i < result.size(); i++) {
                            ImageItem newone = gson.fromJson(result.get(i), ImageItem.class);
                            saved.add(newone);
                        }
                        String[] mdata = new File(storageDir.getPath()).list();
                        final ArrayList<ImageItem> deletion = getdeletion(saved, mdata);
                        final ArrayList<String> update = getupdate(saved, mdata);

                        final int origin = deletion.size() + update.size();
                        final int[] count = {0};

                        if(origin == 0){
                            mProgressDialog.hide();
                            return;
                        }

                        for(int i=0; i<deletion.size(); i++){
                            Log.d("3333 Deletion", deletion.get(i).name);

                            JsonObject json = new JsonObject();
                            json.addProperty("user_name", username);
                            json.addProperty("name", deletion.get(i).name);

                            final int final_i = i;
                            Ion.with(getContext())
                                    .load("DELETE", server_url)
                                    .setJsonObjectBody(json)
                                    .asJsonObject()
                                    .setCallback(new FutureCallback<JsonObject>() {
                                        @Override
                                        public void onCompleted(Exception e, JsonObject result) {
                                            Log.d("3333 Deletion", deletion.get(final_i).name + " deleted");
                                            count[0]++;
                                            if(count[0] == origin){
                                                mProgressDialog.hide();
                                                onResume();
                                            }
                                            else mProgressDialog.setProgress(100 * count[0] / origin);
                                        }
                                    });
                        }
                        for(int i=0; i<update.size(); i++){
                            Log.d("3333 Update", update.get(i));

                            File originalFile = new File(storageDir.getPath() + "/" + update.get(i));

                            JsonObject json = new JsonObject();
                            json.addProperty("user_name", username);
                            json.addProperty("name", update.get(i));
                            json.addProperty("m_date", originalFile.lastModified());

                            String encodedBase64 = null;
                            try {
                                FileInputStream fileInputStreamReader = new FileInputStream(originalFile);
                                byte[] bytes = new byte[(int)originalFile.length()];
                                fileInputStreamReader.read(bytes);
                                encodedBase64 = Base64.encodeToString(bytes, Base64.DEFAULT);
                            } catch (FileNotFoundException e1) {
                                e.printStackTrace();
                            } catch (IOException e1) {
                                e.printStackTrace();
                            }
                            json.addProperty("data", encodedBase64);

                            final int final_j = i;
                            Ion.with(getContext())
                                    .load("PUT", server_url)
                                    .setJsonObjectBody(json)
                                    .asJsonObject()
                                    .setCallback(new FutureCallback<JsonObject>() {
                                        @Override
                                        public void onCompleted(Exception e, JsonObject result) {
                                            Log.d("3333 UPDATE", update.get(final_j) + " updated");
                                            count[0]++;
                                            if(count[0] == origin){
                                                mProgressDialog.hide();
                                                onResume();
                                            }
                                            else mProgressDialog.setProgress(100 * count[0] / origin);
                                        }
                                    });
                        }

                    }
                });
    }

    public ArrayList<ImageItem> getdeletion(ArrayList<ImageItem> saved, String[] myimages){
        ArrayList<ImageItem> deletion = new ArrayList<>();
        for(int i=0; i<saved.size(); i++){
            if(contain(myimages, saved.get(i).name) == -1) deletion.add(saved.get(i));
        }
        return deletion;
    }

    public int contain(String[] sample, String target){
        for(int i=0; i<sample.length; i++){
            if(sample[i].equals(target)){
                return i;
            }
        }
        return -1;
    }

    public ArrayList<String> getupdate(ArrayList<ImageItem> saved, String[] myimages){
        File f;
        ArrayList<String> update = new ArrayList<>();

        for(int i=0; i<myimages.length; i++){
            ImageItem match = getImageList(saved, myimages[i]);
            if(match == null){
                update.add(myimages[i]);
            }else{
                f = new File(storageDir.getPath() + "/" + myimages[i]);
                long local_m_time = f.lastModified();
                if(match.m_date < local_m_time) update.add(myimages[i]);
            }
        }
        return update;
    }

    public ImageItem getImageList(ArrayList<ImageItem> saved, String name){
        for(int i=0; i<saved.size(); i++){
            if(saved.get(i).name.equals(name)) return saved.get(i);
        }
        return null;
    }

    public void download(){
        SharedPreferences test = getActivity().getSharedPreferences("local", getActivity().MODE_PRIVATE);
        final String username = test.getString("alias", "noname");

        String getlistaddr = server_url + "list/" + username;

        Log.d("Ion Connecting to", getlistaddr);
        mProgressDialog.setMessage("다운로드 중입니다");
        mProgressDialog.show();
        final ArrayList<ImageItem> saved = new ArrayList<>();
        Ion.with(getContext())
            .load("GET", getlistaddr)
            .asJsonArray()
            .setCallback(new FutureCallback<JsonArray>() {
                @Override
                public void onCompleted(Exception e, JsonArray result) {
                    for (int i = 0; i < result.size(); i++) {
                        ImageItem newone = gson.fromJson(result.get(i), ImageItem.class);
                        saved.add(newone);
                    }
                    String[] mdata = new File(storageDir.getPath()).list();
                    final ArrayList<ImageItem> downs = getdownload(saved, mdata);

                    final int origin = downs.size();
                    final int[] rest = {0};

                    if(origin == 0){
                        mProgressDialog.hide();
                        return;
                    }

                    for(int i=0; i<downs.size(); i++){
                        Log.d("3333 DOWNLOAD", downs.get(i).name);
                        String down_url = server_url + "/" + username + "/" + downs.get(i).name;

                        final int final_k = i;
                        Ion.with(getContext())
                                .load("GET", down_url)
                                .asJsonObject()
                                .setCallback(new FutureCallback<JsonObject>() {
                                    @Override
                                    public void onCompleted(Exception e, JsonObject result) {
                                        download_image newdown = gson.fromJson(result, download_image.class);
                                        String file_path = storageDir.getPath() + "/" + downs.get(final_k).name;

                                        final byte[] data = Base64.decode(newdown.data, Base64.DEFAULT);
                                        Bitmap decodedBitmap = BitmapFactory.decodeByteArray (data, 0, data.length);

                                        File fileCacheItem = new File(file_path);
                                        OutputStream out = null;

                                        try {
                                            fileCacheItem.createNewFile();
                                            out = new FileOutputStream(fileCacheItem);
                                            decodedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                                            out.close();
                                        }catch (Exception e3) {
                                            e.printStackTrace();
                                        }


                                        rest[0]++;
                                        if(rest[0] == origin){
                                            onResume();
                                            mProgressDialog.hide();
                                        }
                                        else mProgressDialog.setProgress(100 * rest[0] / origin);

                                    }
                                });
                    }
                }
            });
    }

    public ArrayList<ImageItem> getdownload(ArrayList<ImageItem> saved, String[] myimages){
        File f;
        ArrayList<ImageItem> downs = new ArrayList<>();
        for(int i=0; i<saved.size(); i++){
            int index = contain(myimages, saved.get(i).name);

            if(index == -1) downs.add(saved.get(i));
            else{
                f = new File(storageDir.getPath() + "/" + myimages[index]);
                long local_m_time = f.lastModified();

                ImageItem match = saved.get(i);
                if(match.m_date > local_m_time) downs.add(match);
            }
        }
        return downs;
    }


    public void anim() {
        if (isFabOpen) {
            fab.setVisibility(View.VISIBLE);
            fab_x.setVisibility(View.INVISIBLE);
            fab1_ex.startAnimation(fab_close);
            fab2_ex.startAnimation(fab_close);;
            fab3_ex.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            fab3.setClickable(false);
            cover.setVisibility(View.INVISIBLE);
            isFabOpen = false;
        } else {
            fab.setVisibility(View.INVISIBLE);
            fab_x.setVisibility(View.VISIBLE);
            fab1_ex.startAnimation(fab_open);
            fab2_ex.startAnimation(fab_open);
            fab3_ex.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            fab3.setClickable(true);
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
