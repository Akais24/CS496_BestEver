package com.example.q.swipe_tab.Fragment2;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.q.swipe_tab.R;

import java.io.File;

import uk.co.senab.photoview.PhotoViewAttacher;

public class OriginImage_Fragment extends Fragment {

    String imagepath;
    PhotoViewAttacher mAttacher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment2_origin_image_item, container, false);
        imagepath = getArguments().getString("imagepath");

        ImageView iv_view = v.findViewById(R.id.iv_view);
        mAttacher = new PhotoViewAttacher(iv_view);
        Glide.with(getContext()).load(new File(imagepath)).into(iv_view);

        return v;
    }

    public static OriginImage_Fragment newInstance(String imagepath){
        OriginImage_Fragment f = new OriginImage_Fragment();
        Bundle b = new Bundle();
        b.putString("imagepath", imagepath);
        f.setArguments(b);

        return f;
    }

    public String getpath(){
        return imagepath;
    }
}
