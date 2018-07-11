package com.example.q.swipe_tab.Fragment3;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.q.swipe_tab.R;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.Random;

import tyrantgit.explosionfield.ExplosionField;

public class Fragment3_main extends Fragment implements View.OnClickListener {
    private static final int CODE_JANG = 0;
    private static final int CODE_NORMAL = 1;

    private TextView tvPoints;
    private ImageView img;
    private Button reset;
    private Button ranking;

    private int points = 0;
    private int life = 0;

    private ExplosionField mExplosionField;

    Handler han;
    boolean is_jang = false;

    String server_url = "http://52.231.66.36:60722/api/";
    ProgressDialog mProgressDialog;
    Gson gson = new Gson();

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
        reset = model.findViewById(R.id.reset_btn);
        ranking = model.findViewById(R.id.ranking);

        img.setOnClickListener(this);
        reset.setOnClickListener(this);
        ranking.setOnClickListener(this);

        SharedPreferences test = getActivity().getSharedPreferences("local", getActivity().MODE_PRIVATE);
        points = test.getInt("point", 0);
        tvPoints.setText(String.valueOf(points));
        life = test.getInt("life", getrandomlife());
        if(life < 1) life = getrandomlife();

        mExplosionField = ExplosionField.attach2Window(getActivity());

        han = new Handler(){
          @Override
          public void handleMessage(Message msg){
              img.animate().setDuration(100).setStartDelay(200).scaleX(1.0f).scaleY(1.0f).alpha(1.0f).start();
              switch (msg.what){
                  case CODE_JANG:
                      img.setImageResource(R.drawable.special);
                      coockie_back(img);
                      break;
                  case CODE_NORMAL:
                      img.setImageResource(R.drawable.cookie);
                      coockie_back(img);
                      break;
              }
          }
        };

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage("Communicating");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
    }

    @Override
    public void onClick(final View v) {
        Context mContext = getContext();
        switch (v.getId()) {
            case R.id.imgCookie:
                Log.d("444444 life ", String.valueOf(life));
                Animation a = AnimationUtils.loadAnimation(mContext, R.anim.cookie_animation);
                a.setAnimationListener(new Fragment3_SimpleAnimationListener(){
                    @Override
                    public void onAnimationStart(Animation animation){
                        if(life == 1) img.setClickable(false);

                        coockieClick((ImageView) v);
                    }
                });
                v.startAnimation(a);
                break;
            case R.id.reset_btn:
                mExplosionField.clear();

                life = getrandomlife();
                coockie_back(img);
                break;
            case R.id.ranking:
                mProgressDialog.setMessage("서버에 접속하는 중입니다");
                mProgressDialog.show();

                SharedPreferences test = getActivity().getSharedPreferences("local", getActivity().MODE_PRIVATE);
                String name = test.getString("alias", "noname");
                JsonObject json = new JsonObject();
                json.addProperty("point", points);

                Ion.with(getContext())
                        .load("PUT", server_url + "ranking/" + name)
                        .setJsonObjectBody(json)
                        .asJsonArray()
                        .setCallback(new FutureCallback<JsonArray>() {
                            @Override
                            public void onCompleted(Exception e, JsonArray result) {
                                final ArrayList<rankingItem> saved = new ArrayList<>();
                                Ion.with(getContext())
                                        .load("GET", server_url + "ranking")
                                        .asJsonArray()
                                        .setCallback(new FutureCallback<JsonArray>() {
                                            @Override
                                            public void onCompleted(Exception e, JsonArray result) {
                                                for (int i = 0; i < result.size(); i++) {
                                                    rankingItem newone = gson.fromJson(result.get(i), rankingItem.class);
                                                    saved.add(newone);
                                                }

                                                String msg = "";

                                                for(int i=0; i<saved.size();i++){
                                                    msg += String.valueOf(i+1) + "등 : " + saved.get(i).alias + " (" + saved.get(i).point + "점)\n";
                                                }
                                                mProgressDialog.hide();
                                                msg += "랭킹진입을 위해 노력해주세요!";
                                                new AlertDialog.Builder(getContext())
                                                        .setTitle("현재 랭킹")
                                                        .setMessage(msg)
                                                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                                //finish();
                                                            }
                                                        })
                                                        .setCancelable(false).create().show();

                                            }
                                        });
                            }
                        });




                break;
        }
    }

    private void coockieClick(ImageView v) {
        if(is_jang) Toast.makeText(getContext(), "장의장님을 놓치셨습니다!", Toast.LENGTH_LONG).show();

        points++;
        life--;

        if(life == 0){
            SharedPreferences test = getActivity().getSharedPreferences("local", getActivity().MODE_PRIVATE);
            String name = test.getString("alias", "noname");

            Log.d("55555", server_url + "ranking/" + name);

            JsonObject json = new JsonObject();
            json.addProperty("point", points);

            Ion.with(getContext())
                    .load("PUT", server_url + "ranking/" + name)
                    .setJsonObjectBody(json)
                    .asJsonArray()
                    .setCallback(new FutureCallback<JsonArray>() {
                        @Override
                        public void onCompleted(Exception e, JsonArray result) {
                            fragment3_response newone = gson.fromJson(result, fragment3_response.class);
                        }
                    });

            Log.d("44444", "0 life detect");
            mExplosionField.explode(v);

            life = getrandomlife();
            int r = new Random().nextInt(100);
            if(r == 0) {
                life = 1;
                is_jang = true;
                img.setClickable(true);
                han.sendEmptyMessage(CODE_JANG);
            }else{
                is_jang = false;
                han.sendEmptyMessage(CODE_NORMAL);
            }
        }

        tvPoints.setText(Integer.toString(points));

        SharedPreferences test = getActivity().getSharedPreferences("local", getActivity().MODE_PRIVATE);
        SharedPreferences.Editor editor = test.edit();
        editor.putInt("point", points);
        editor.putInt("life", life);
        editor.commit();
    }

    private void coockie_back(ImageView v) {
//        Toast.makeText(getContext(), "rest life is " + String.valueOf(max_life), Toast.LENGTH_SHORT).show();

        Animation back = AnimationUtils.loadAnimation(getContext(), R.anim.cookie_back_anim);
        back.setAnimationListener(new Fragment3_SimpleAnimationListener(){
            @Override
            public void onAnimationEnd(Animation animation){
                img.setClickable(true);
            }
        });
        v.startAnimation(back);
    }

    public int getrandomlife(){
        return new Random().nextInt(20) + 20;
    }

}

