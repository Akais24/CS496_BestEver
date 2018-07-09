package com.example.q.swipe_tab.Fragment3;

import android.animation.ObjectAnimator;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.AbsoluteLayout;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.q.swipe_tab.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Fragment3_main extends Fragment implements View.OnClickListener {
    TextView cover;
    Button btn1, btn2, btn3, start_btn;
    View game1_layout, game2_layout, game3_layout;

    AlertDialog dialog;
    Toast toast;
    File storageDir = new File(Environment.getExternalStorageDirectory() + "/Pictures", "hw1_test");

    Vibrator vibe;
    long basetime;

    boolean game1_firsttouch = true;
    int game1_firstchoice;
    int game1_correctnum;
    ArrayList<Integer> game1_correct_list;
    View game1_target;
    View game1_backside;

    TextView timer_view;
    TextView game1_try_view;
    int game1_try_count;


    ImageView[] game2_img_list = new ImageView[16];
    TextView game2_round_view, game2_timer_view;
    Button up_btn, left_btn, right_btn, down_btn, center_btn;
    int game2_round, game2_correct_num;
    int[] game2_problem_list, game2_image_index_list;

    //Game3
    ImageButton drag;
    Button reset;
    ImageView drop;
    TextView text,sucess, timer;
    Random r;
    int total1, total2 , failure , suc = 0;
    DisplayMetrics displaymetrics;
    int width, height;
    CountDownTimer countdowntimer = null;

    AbsoluteLayout.LayoutParams absParams;



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
        vibe = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);

        cover = model.findViewById(R.id.cover);
        btn1 = model.findViewById(R.id.btn1);
        btn2 = model.findViewById(R.id.btn2);
        btn3 = model.findViewById(R.id.btn3);
        start_btn = model.findViewById(R.id.start);

        game1_layout = model.findViewById(R.id.Game1_layout);
        game2_layout = model.findViewById(R.id.Game2_layout);
        game3_layout = model.findViewById(R.id.Game3_layout);

        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);

        drag = (ImageButton)model.findViewById(R.id.one);
        drop = (ImageView) model.findViewById(R.id.imageView2);
        text = (TextView)model.findViewById(R.id.Total);
        sucess = (TextView)model.findViewById(R.id.Sucess);
        reset = (Button)model.findViewById(R.id.ballpos);
        timer = model.findViewById(R.id.timer);

        absParams = (AbsoluteLayout.LayoutParams)drag.getLayoutParams();
        r = new Random();
        displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        width = displaymetrics.widthPixels * 2 / 3;
        height = displaymetrics.heightPixels * 3 / 5;
    }

    public void onClick(final View v) {
        boolean isvibe = true;
        boolean ishaptic = false;
        switch (v.getId()) {
            case R.id.btn1:
                dialog = new AlertDialog.Builder(getContext())
                    .setMessage("Game 1을 실행하시겠습니까?\n(주의!! 이전에 플레이하던 게임의 데이터는 사라집니다)")
                    .setNeutralButton("네", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            setGone();
                            btn1.setAlpha(.3f);
                            btn1.setClickable(false);
                            cover.setVisibility(View.VISIBLE);
                            prepareGame1();
                        }
                    }).setPositiveButton("아니요", null).setCancelable(true).show();
                ((TextView) dialog.findViewById(android.R.id.message)).setGravity(Gravity.CENTER);
                dialog.show();
                break;
            case R.id.btn2:
                dialog = new AlertDialog.Builder(getContext())
                    .setMessage("Game 2를 실행하시겠습니까?\n(주의!! 이전에 플레이하던 게임의 데이터는 사라집니다)")
                    .setNeutralButton("네", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            setGone();
                            btn2.setAlpha(.3f);
                            btn2.setClickable(false);
                            cover.setVisibility(View.VISIBLE);
                            prepareGame2();
                        }
                    }).setPositiveButton("아니요", null).setCancelable(true).show();
                ((TextView) dialog.findViewById(android.R.id.message)).setGravity(Gravity.CENTER);
                dialog.show();
                break;
            case R.id.btn3:
                dialog = new AlertDialog.Builder(getContext())
                        .setMessage("Game 3을 실행하시겠습니까?\n(주의!! 이전에 플레이하던 게임의 데이터는 사라집니다)")
                        .setNeutralButton("네", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                setGone();
                                btn3.setAlpha(.3f);
                                btn3.setClickable(false);
                                cover.setVisibility(View.VISIBLE);
                                prepareGame3();
                            }
                        }).setPositiveButton("아니요", null).setCancelable(true).show();
                ((TextView) dialog.findViewById(android.R.id.message)).setGravity(Gravity.CENTER);
                dialog.show();
                break;
            case R.id.up_btn:
                if (ishaptic)
                    v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                if (isvibe)
                    vibe.vibrate(100);
                game2_check(1);
                break;
            case R.id.left_btn:
                if (ishaptic)
                    v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                if (isvibe)
                    vibe.vibrate(100);
                game2_check(2);
                break;
            case R.id.center_btn:
                if (ishaptic)
                    v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                if (isvibe)
                    vibe.vibrate(100);
                game2_check(3);
                break;
            case R.id.right_btn:
                if (ishaptic)
                    v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                if (isvibe)
                    vibe.vibrate(100);
                game2_check(4);
                break;
            case R.id.down_btn:
                if (ishaptic)
                    v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                if (isvibe)
                    vibe.vibrate(100);
                game2_check(5);
                break;
        }
    }

    public void setGone(){
        cover.setText("게임을 골라주세요");
        cover.setVisibility(View.GONE);
        game1_layout.setVisibility(View.GONE);
        game2_layout.setVisibility(View.GONE);
        game3_layout.setVisibility(View.GONE);

        btn1.setClickable(true);
        btn2.setClickable(true);
        btn3.setClickable(true);
        btn1.setAlpha(1);
        btn2.setAlpha(1);
        btn3.setAlpha(1);
        start_btn.setVisibility(View.INVISIBLE);
    }

    public void prepareGame1(){
        final String[] mdata = storageDir.list();

        GridView gv = getView().findViewById(R.id.game1_grid_view);
        gv.setVisibility(View.INVISIBLE);

        if((mdata == null) || (mdata.length < 8)){
            cover.setText("Game 1을 플레이하기에\n사진 수가 부족합니다");

            game1_layout.setVisibility(View.GONE);
            btn1.setClickable(false);
            btn2.setClickable(true);
            btn3.setClickable(true);
            btn1.setAlpha(0.3f);
            btn2.setAlpha(1);
            btn3.setAlpha(1);
            return;
        }
        cover.setText("");

        start_btn.setVisibility(View.VISIBLE);
        start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start_btn.setVisibility(View.INVISIBLE);
                cover.setVisibility(View.INVISIBLE);
                startGame1(mdata);
            }
        });
    }

    public void startGame1(String[] mdata){
        game1_layout.setVisibility(View.VISIBLE);
        final List path_list = new ArrayList();
        int index = 0;
        while(path_list.size() < 16){
            Log.d("33333 : ", String.valueOf(index));
            if(!path_list.contains(mdata[index])){
                path_list.add(mdata[index]);
                path_list.add(mdata[index]);
            }
            index += 1;
        }
        Collections.shuffle(path_list);

        final View model = getView();
        final View infobar = model.findViewById(R.id.info_bar);
        infobar.setVisibility(View.VISIBLE);

        final GridView gv = model.findViewById(R.id.game1_grid_view);
        gv.setVisibility(View.VISIBLE);
        Fragment3_Game1_GridImageAdapter mGadapter = new Fragment3_Game1_GridImageAdapter(getContext(), path_list, storageDir.getPath());
        gv.setAdapter(mGadapter);
        gv.setVerticalScrollBarEnabled(false);

        game1_firsttouch = true;
        game1_firstchoice = -1;
        game1_correctnum = 0;
        game1_correct_list = new ArrayList<>();
        game1_try_count = 0;

//        FlipBackAsyncTask prepare = new FlipBackAsyncTask();
//        prepare.execute(gv.getChildAt(0));
//
//        for(int i=1; i<path_list.size(); i++) {
//            //simple_flip_back(gv.getChildAt(i));
//            prepare.doInBackground(gv.getChildAt(i));
//            Log.d("?????????", "??????");
////            try {
////                Thread.sleep(200);
////            } catch (InterruptedException e) {
////                e.printStackTrace();
////            }
//        }

        ///////// Start Timer and try count ////////
        timer_view = model.findViewById(R.id.time_out);
        game1_try_view = model.findViewById(R.id.try_count);

        basetime = SystemClock.elapsedRealtime();
        MyTimer.sendEmptyMessage(0);
        //////////////////////////////

        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(game1_correct_list.indexOf(position) != -1)
                    return;

                simple_flip_back(gv.getChildAt(position));
                if(game1_firsttouch){
                    game1_firstchoice = position;
                    game1_firsttouch = false;
                }else{
                    game1_try_count += 1;
                    game1_try_view.setText(String.valueOf(game1_try_count));
                    if(compareImage(path_list, game1_firstchoice, position)){
                        showtoast("정답!");
                        game1_correct_list.add(position);
                        game1_correct_list.add(game1_firstchoice);
                        game1_correctnum += 1;
                        if(game1_correctnum == 8) {
                            endgame1();
                            gv.setVisibility(View.INVISIBLE);
                        }
                    }else{
                        showtoast("오답!");
                        vibe.vibrate(200);
                        simple_flip(gv.getChildAt(position));
                        simple_flip(gv.getChildAt(game1_firstchoice));
                    }
                    game1_firstchoice = -1;
                    game1_firsttouch = true;
                }
            }
        });
    }

    public void endgame1(){
        vibe.vibrate(500);

        final View infobar = getView().findViewById(R.id.info_bar);
        infobar.setVisibility(View.INVISIBLE);
        String text= "게임 클리어!!";
        if(toast != null)
            toast.cancel();
        toast = Toast.makeText(getContext(), text, Toast.LENGTH_LONG);
        toast.show();

        String endmessage = String.format("%s 동안\n%d 회의 시도로 클리어하셨습니다", getTimeOut(), game1_try_count);
        new AlertDialog.Builder(getContext())
                .setTitle("클리어!")
                .setMessage(endmessage)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //finish();
                    }
                })
                .setCancelable(true).create().show();
        setGone();
        cover.setText("클리어를 축하합니다\n다시 게임을 골라주세요");
        cover.setVisibility(View.VISIBLE);
    }

    public void prepareGame2(){
        cover.setText("");

        start_btn.setVisibility(View.VISIBLE);
        start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start_btn.setVisibility(View.INVISIBLE);
                cover.setVisibility(View.INVISIBLE);
                game2_layout.setVisibility(View.VISIBLE);
                startGame2();
            }
        });
    }

    public boolean compareImage(List path_list, int first, int second){
        String a = (String) path_list.get(first);
        String b = (String) path_list.get(second);
        return a.equals(b);
    }

    public void startGame2(){

        View model2 = getView();

        game2_img_list[0] = model2.findViewById(R.id.prob0);
        game2_img_list[1] = model2.findViewById(R.id.prob1);
        game2_img_list[2] = model2.findViewById(R.id.prob2);
        game2_img_list[3] = model2.findViewById(R.id.prob3);
        game2_img_list[4] = model2.findViewById(R.id.prob4);
        game2_img_list[5] = model2.findViewById(R.id.prob5);
        game2_img_list[6] = model2.findViewById(R.id.prob6);
        game2_img_list[7] = model2.findViewById(R.id.prob7);
        game2_img_list[8] = model2.findViewById(R.id.prob10);
        game2_img_list[9] = model2.findViewById(R.id.prob11);
        game2_img_list[10] = model2.findViewById(R.id.prob12);
        game2_img_list[11] = model2.findViewById(R.id.prob13);
        game2_img_list[12] = model2.findViewById(R.id.prob14);
        game2_img_list[13] = model2.findViewById(R.id.prob15);
        game2_img_list[14] = model2.findViewById(R.id.prob16);
        game2_img_list[15] = model2.findViewById(R.id.prob17);

        up_btn = model2.findViewById(R.id.up_btn);
        up_btn.setOnClickListener(this);
        left_btn = model2.findViewById(R.id.left_btn);
        left_btn.setOnClickListener(this);
        right_btn = model2.findViewById(R.id.right_btn);
        right_btn.setOnClickListener(this);
        down_btn = model2.findViewById(R.id.down_btn);
        down_btn.setOnClickListener(this);
        center_btn = model2.findViewById(R.id.center_btn);
        center_btn.setOnClickListener(this);

        ///////// Start Timer and try count ////////
        game2_timer_view = model2.findViewById(R.id.game2_time_out);
        game2_round_view = model2.findViewById(R.id.round_count);

        basetime = SystemClock.elapsedRealtime();
        Game2_Timer.sendEmptyMessage(0);
        //////////////////////////////

        View infobar2 = model2.findViewById(R.id.info_bar2);
        infobar2.setVisibility(View.VISIBLE);

        game2_round = 1;

        game2_start_newround(game2_round);

    }

    private void game2_start_newround(int round) {
        game2_round_view.setText(String.valueOf(round));
        if(round > 10)
            round = 10;
        int img_num = (int) (4 + 4 * Math.ceil((round - 1) / 3));

        game2_make_index_list(img_num);

        Random rnd = new Random();
        game2_problem_list = new int[img_num];

        for(int i=0; i<16; i++){
            game2_img_list[i].setImageResource(0);
        }

        for(int i =0; i<img_num; i++){
            game2_problem_list[i] = rnd.nextInt(5) + 1;
            game2_set_image(game2_img_list[game2_image_index_list[i]], game2_problem_list[i]);
        }
        game2_correct_num = 0;
        //game2_clear_round();
    }

    public void game2_make_index_list(int num){
        game2_image_index_list = new int[num];
        if(num == 4){
            game2_image_index_list[0] = 2;
            game2_image_index_list[1] = 3;
            game2_image_index_list[2] = 4;
            game2_image_index_list[3] = 5;
        }else if(num == 12){
            game2_image_index_list[0] = 1;
            game2_image_index_list[1] = 2;
            game2_image_index_list[2] = 3;
            game2_image_index_list[3] = 4;
            game2_image_index_list[4] = 5;
            game2_image_index_list[5] = 6;
            game2_image_index_list[6] = 9;
            game2_image_index_list[7] = 10;
            game2_image_index_list[8] = 11;
            game2_image_index_list[9] = 12;
            game2_image_index_list[10] = 13;
            game2_image_index_list[11] = 14;
        }else{
            for(int i = 0; i<num; i++){
                game2_image_index_list[i] = i;
            }
        }
    }

    public void game2_set_image(ImageView target, int index){
        if(index == 1)
            target.setImageResource(R.drawable.up_circle);
        else if(index == 2)
            target.setImageResource(R.drawable.left_circle);
        else if(index == 3)
            target.setImageResource(R.drawable.center_circle);
        else if(index == 4)
            target.setImageResource(R.drawable.right_circle);
        else if(index == 5)
            target.setImageResource(R.drawable.down_circle);

        else if(index == 6)
            target.setImageResource(R.drawable.up_circle_blue);
        else if(index == 7)
            target.setImageResource(R.drawable.left_circle_blue);
        else if(index == 8)
            target.setImageResource(R.drawable.center_circle_blue);
        else if(index == 9)
            target.setImageResource(R.drawable.right_circle_blue);
        else if(index == 10)
            target.setImageResource(R.drawable.down_circle_blue);


    }

    private void game2_check(int click){
        Log.d("2222222222222222", String.valueOf(game2_correct_num * 100 + game2_problem_list[game2_correct_num] * 10 + click));
        if(game2_problem_list[game2_correct_num] != click){
            endgame2();
            vibe.vibrate(500);
            return;
        }
        game2_set_image(game2_img_list[game2_image_index_list[game2_correct_num]], game2_problem_list[game2_correct_num] + 5);
        game2_correct_num += 1;
        if(game2_correct_num == game2_image_index_list.length)
            game2_clear_round();
    }

    private void endgame2(){
        final View infobar2 = getView().findViewById(R.id.info_bar2);
        infobar2.setVisibility(View.INVISIBLE);
        String text= "게임 오버!!";
        if(toast != null)
            toast.cancel();
        toast = Toast.makeText(getContext(), text, Toast.LENGTH_LONG);
        toast.show();

        String endmessage = String.format("%s 동안의 플레이를 통해\n%d 라운드까지 진행하셨습니다", getTimeOut(), game2_round);
        new AlertDialog.Builder(getContext())
                .setTitle("종료!")
                .setMessage(endmessage)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //finish();
                    }
                })
                .setCancelable(true).create().show();
        setGone();
        cover.setText("다시 게임을 골라주세요");
        cover.setVisibility(View.VISIBLE);
    }

    private void game2_clear_round() {
        game2_round += 1;
        game2_start_newround(game2_round);
    }

    public void prepareGame3() {

        start_btn.setVisibility(View.VISIBLE);
        start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start_btn.setVisibility(View.INVISIBLE);
                cover.setVisibility(View.INVISIBLE);
                game3_layout.setVisibility(View.VISIBLE);
                startGame3();
            }
        });
    }

    public void startGame3() {
        total1=0;
        total2=0;
        suc=0;
        failure=0;

        countdowntimer = new CountDownTimer(11000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int progress = (int) (millisUntilFinished/1000);
                timer.setText(Integer.toString(progress));

                drop.setOnDragListener(new View.OnDragListener() {
                    @Override
                    public boolean onDrag(View v, DragEvent event) {
                        // TODO Auto-generated method stub
                        final int action = event.getAction();
                        switch(action) {

                            case DragEvent.ACTION_DRAG_STARTED:
                                break;

                            case DragEvent.ACTION_DRAG_EXITED:
                                break;

                            case DragEvent.ACTION_DRAG_ENTERED:
                                break;

                            case DragEvent.ACTION_DROP:{
                                total1 = total1 +1 +total2;
                                suc = suc +1;
                                sucess.setText("성공 횟수 :"+suc);
                                text.setText("던진 횟수: "+total1);
                                absParams.x =  r.nextInt(width+50);
                                absParams.y =  r.nextInt(height+50);
                                drag.setLayoutParams(absParams);

                                return(true);
                            }

                            case DragEvent.ACTION_DRAG_ENDED:{
                                total2 = total2 +1;
                                failure=failure+1;
                                sucess.setText("성공 횟수 :"+suc);
                                text.setText("던진 횟수: "+total2);
                                absParams.x =  r.nextInt(width) ;
                                absParams.y =  r.nextInt(height);
                                drag.setLayoutParams(absParams);
                                return(true);
                            }

                            default:
                                break;
                        }
                        return true;
                    }});
                drag.setOnTouchListener(new View.OnTouchListener() {

                    @Override
                    public boolean onTouch(View v, MotionEvent arg1) {
                        // TODO Auto-generated method stub
                        ClipData data = ClipData.newPlainText("", "");
                        View.DragShadowBuilder shadow = new View.DragShadowBuilder(drag);
                        v.startDrag(data, shadow, null, 0);
                        return false;
                    }
                });

                reset.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch(v.getId()) {
                            case R.id.ballpos:
                                absParams.x =  r.nextInt(width) ;
                                absParams.y =  r.nextInt(height);
                                drag.setLayoutParams(absParams);
                                break;
                        }
                    }
                });
            }

            @Override
            public void onFinish() {
                endgame3();
            }
        };
        countdowntimer.start();
    }

    private void endgame3() {
        //final View infobar3 = getView().findViewById(R.id.info_bar3);
        //infobar3.setVisibility(View.INVISIBLE);

        String end = String.format("총 던진 횟수 : %d회 \n 성공 횟수 : %d회 \n 실패 횟수 : %d회", total2, suc, total2-suc);

        new android.app.AlertDialog.Builder(getContext())
                .setTitle("시간 끝!")
                .setMessage(end)
                .setPositiveButton("확인", null)
                .setCancelable(false).create().show();
        setGone();
        cover.setText("다시 게임을 골라주세요");
        cover.setVisibility(View.VISIBLE);
    }
//}


    public void simple_flip(final View target){
        final View flipping = target.findViewById(R.id.back);
//        getActivity().runOnUiThread(new Thread(new Runnable() {
//            @Override
//            public void run() {
//                ObjectAnimator animator = ObjectAnimator.ofFloat(flipping, "rotationY", -90F, 0F);
//                animator.setDuration(500);
//                animator.setInterpolator(new AccelerateInterpolator());
//                animator.start();
//            }
//        }));
        ObjectAnimator animator = ObjectAnimator.ofFloat(flipping, "rotationY", -90F, 0F);
        animator.setDuration(300);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.start();
    }

    public void simple_flip_back(final View target){
        final View flipping = target.findViewById(R.id.back);
//        getActivity().runOnUiThread(new Thread(new Runnable() {
//            @Override
//            public void run() {
//                ObjectAnimator animator = ObjectAnimator.ofFloat(flipping, "rotationY", 0, 90F);
//                animator.setDuration(500);
//                animator.setInterpolator(new AccelerateInterpolator());
//                animator.start();
//            }
//        }));
        ObjectAnimator animator = ObjectAnimator.ofFloat(flipping, "rotationY", 0, 90F);
        animator.setDuration(300);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.start();
    }

    public void showtoast(String text){
        if(toast != null)
            toast.cancel();
        toast = Toast.makeText(getContext(), text, Toast.LENGTH_SHORT);
        toast.show();
    }

    Handler MyTimer = new Handler(){
        public void handleMessage(Message msg){
            timer_view.setText(getTimeOut());
            MyTimer.sendEmptyMessage(0);
        }
    };

    Handler Game2_Timer = new Handler(){
        public void handleMessage(Message msg){
            game2_timer_view.setText(getTimeOut());
            Game2_Timer.sendEmptyMessage(0);
        }
    };

    String getTimeOut(){
        long now = SystemClock.elapsedRealtime();
        long outTime = now - basetime;
        String easy_outTime = String.format("%02d:%02d:%02d", outTime/1000 / 60, (outTime/1000)%60,(outTime%1000)/10);
        return easy_outTime;
    }


    public class FlipBackAsyncTask extends AsyncTask<View, Void, View>{

        @Override
        protected View doInBackground(View... views) {
            return views[0];
        }

        @Override
        protected void onPostExecute(View view) {
            // doInBackground 에서 받아온 total 값 사용 장소 }
            ObjectAnimator animator = ObjectAnimator.ofFloat(view, "rotationY", 0, 90F);
            animator.setDuration(300);
            animator.setInterpolator(new AccelerateInterpolator());
            animator.start();
        }
    }

}

