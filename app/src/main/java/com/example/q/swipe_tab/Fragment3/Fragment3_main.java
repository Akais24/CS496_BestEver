package com.example.q.swipe_tab.Fragment3;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.q.swipe_tab.R;

import java.util.Random;

public class Fragment3_main extends Fragment implements View.OnClickListener {
    private TextView tvPoints;
    private ImageView img;

    private int points = 0;
    private int life = 0;

    public static Fragment3_main newInstance(){
        return new Fragment3_main();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate((savedInstanceState));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        //return super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment3, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInsatnceState) {
        super.onActivityCreated(savedInsatnceState);
        View model = getView();

        tvPoints = model.findViewById(R.id.tvpoints);
        img = model.findViewById(R.id.imgCookie);
        img.setOnClickListener(this);

        SharedPreferences test = getActivity().getSharedPreferences("local", getActivity().MODE_PRIVATE);
        points = test.getInt("point", 0);
        life = test.getInt("life", getrandomlife());
    }

    @Override
    public void onClick(final View v) {
        Context mContext = getContext();
        switch (v.getId()) {
            case R.id.imgCookie:
                Animation a = AnimationUtils.loadAnimation(mContext, R.anim.cookie_animation);
                a.setAnimationListener(new Fragment3_SimpleAnimationListener(){
                    @Override
                    public void onAnimationEnd(Animation animation){
                        coockieClick();
                    }
                });
                v.startAnimation(a);
                break;
        }
    }

    private void coockieClick() {
        points++;
        life--;

        if(life == 0){
            int max_life = getrandomlife();
            life = max_life;
            Toast.makeText(getContext(), "restt life is " + String.valueOf(max_life), Toast.LENGTH_SHORT).show();
        }

        tvPoints.setText(Integer.toString(points));

        SharedPreferences test = getActivity().getSharedPreferences("local", getActivity().MODE_PRIVATE);
        SharedPreferences.Editor editor = test.edit();
        editor.putInt("point", points);
        editor.putInt("life", life);
        editor.commit();
    }

    public int getrandomlife(){
        return new Random().nextInt(20) + 40;
    }

}

