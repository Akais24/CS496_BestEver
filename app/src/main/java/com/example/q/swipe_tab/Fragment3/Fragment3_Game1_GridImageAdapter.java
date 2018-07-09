package com.example.q.swipe_tab.Fragment3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.request.RequestOptions;
import com.example.q.swipe_tab.R;

import java.io.File;
import java.util.List;

import jp.wasabeef.glide.transformations.CropSquareTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class Fragment3_Game1_GridImageAdapter extends BaseAdapter{
    private Context mContext;
    List game_pics;
    String mBasepath;
    LayoutInflater inf;
//
//    boolean game1_firsttouch = true;
//    int game1_firstchoice;
//    View previous_back;

    View[] backlist;
    View[] imagelist;

    public Fragment3_Game1_GridImageAdapter(Context c, List gp, String mbp) {
        mContext = c;
        game_pics = gp;
        mBasepath = mbp;

        backlist = new View[gp.size()];
        imagelist = new View[gp.size()];
        inf = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return game_pics.size();
    }

    public Object getItem(int position) {
        return game_pics.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

//    public View getnthback(int position){
//        return backlist[position];
//    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView == null)
            convertView = inf.inflate(R.layout.fragment3_game1_image_item, null);

        final ImageView back = convertView.findViewById(R.id.back);
        backlist[position] = back;
        back.setScaleType(ImageView.ScaleType.CENTER_CROP);

        MultiTransformation multi = new MultiTransformation(
                new CropSquareTransformation(),
                new RoundedCornersTransformation(100, 0));

        Glide.with(mContext)
                .load(R.drawable.card_back)
                .apply(RequestOptions.overrideOf(300, 300))
                .apply(RequestOptions.bitmapTransform(multi))
                .into(back);

        ImageView iv = convertView.findViewById(R.id.game1_iv_view);
        imagelist[position] = iv;
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);

        Glide.with(mContext)
                .load(new File(mBasepath + "/" + game_pics.get(position)))
                .apply(RequestOptions.overrideOf(300, 300))
                .apply(RequestOptions.bitmapTransform(multi))
                .into(iv);

//        card.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                back.setVisibility(View.INVISIBLE);
//                if(game1_firsttouch){
//                    game1_firstchoice = position;
//                    game1_firsttouch = false;
//                    previous_back = back;
//                }else{
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    if(compareImage(game_pics, game1_firstchoice, position)){
//                        Toast.makeText(mContext, "정답!", Toast.LENGTH_SHORT).show();
//                    }else{
//                        Toast.makeText(mContext, "오답!", Toast.LENGTH_SHORT).show();
//                        back.setVisibility(View.VISIBLE);
//                        previous_back.setVisibility(View.VISIBLE);
//                    }
//                    game1_firstchoice = -1;
//                    game1_firsttouch = true;
//                }
//            }
//        });

        return convertView;
    }

    public boolean compareImage(List path_list, int first, int second){
        String a = (String) path_list.get(first);
        String b = (String) path_list.get(second);
        return a.equals(b);
    }

}
