package com.example.q.swipe_tab.Fragment2;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.request.RequestOptions;
import com.example.q.swipe_tab.R;

import java.io.File;
import java.util.Arrays;

import jp.wasabeef.glide.transformations.CropSquareTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class mRecyclerAdapter extends RecyclerView.Adapter<mRecyclerAdapter.ViewHolder> {
    int OPEN_ORIGIN_IMAMGE = 3333;

    private String[] mdata = new String[0];
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    String TAG = "mRecyclerAdapter => ";
    String mBasePath;

    File file;
    Context mContext;

    Activity Frag2Activity;

    public mRecyclerAdapter(Context context, String inputBasePath, Activity currentActivity){
        mContext = context;
        this.mInflater = LayoutInflater.from(context);
        mBasePath = inputBasePath;
        Frag2Activity = currentActivity;
        file = new File(mBasePath); // 지정 경로의 directory를 File 변수로 받아
        if(!file.exists()){
            if(!file.mkdirs()){
                Log.d(TAG, "failed to create directory");
            }
        }

        mdata = file.list();
        if(mdata != null)
            Arrays.sort(mdata, String.CASE_INSENSITIVE_ORDER);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = mInflater.inflate(R.layout.fragment2_recycler_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        String imagepath = mBasePath + "/" + mdata[position];
        MultiTransformation multi = new MultiTransformation(
                new CropSquareTransformation(),
                new RoundedCornersTransformation(80, 0)
        );

        Glide.with(mContext).load(new File(imagepath))
                .apply(RequestOptions.overrideOf(400, 400))
                .apply(RequestOptions.bitmapTransform(multi))
                .into(holder.img_view);

        holder.img_view.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent origin_intent = new Intent(mContext, Fragment2_Origin_Popup.class);
                origin_intent.putExtra("origin_path", mBasePath + "/" + mdata[position]);

                origin_intent.putExtra("directory", mBasePath);
                origin_intent.putExtra("position", position);

                ((Activity) mContext).startActivityForResult(origin_intent, OPEN_ORIGIN_IMAMGE);
            }

        });

        holder.img_view.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(mContext)
                        .setTitle("알림").setMessage("이 사진을 삭제하시겠습니까?")
                        .setNeutralButton("네", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                File f1 = new File(mBasePath + "/" + mdata[position]);
                                if(f1.delete()){
                                    mdata = file.list();
                                    notifyItemRemoved(position);
                                    Toast.makeText(mContext, "사진이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(mContext, "사진 삭제에 실패했습니다. 사진 파일이 있는지 확인해주십시오.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setPositiveButton("아니요", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //finish();
                            }
                        })
                        .setCancelable(true).create().show();
                return true;
            }

        });
    }

    @Override
    public int getItemCount() {
        if(mdata != null)
            return mdata.length;
        return 0;
    }

    public void refresh() {
        mdata = file.list();
        if(mdata != null)
            Arrays.sort(mdata, String.CASE_INSENSITIVE_ORDER);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView img_view;

        ViewHolder(View itemView) {
            super(itemView);
            img_view = itemView.findViewById(R.id.info_image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null){
                mClickListener.onItemClick(view, getAdapterPosition());
            }
        }
    }
    // convenience method for getting data at click position
    String getItem(int id) {
        return mdata[id];
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

}
