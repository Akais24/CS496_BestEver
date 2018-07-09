package com.example.q.swipe_tab.Fragment2;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;

import java.io.File;
import java.util.Arrays;

public class OriginImagePagerAdapter extends FragmentStatePagerAdapter {
    String[] mdata;
    String mBasepath;

    private long baseId = 0;

    public OriginImagePagerAdapter(FragmentManager fm, String mBasepath) {
        super(fm);
        this.mBasepath = mBasepath;

        mdata = new File(mBasepath).list();
        if(mdata != null)
            Arrays.sort(mdata, String.CASE_INSENSITIVE_ORDER);
    }

    @Override
    public Fragment getItem(int position) {
        String imagepath = mBasepath + "/" + mdata[position];
        return OriginImage_Fragment.newInstance(imagepath);
    }

    public String getnthpath(int position){
        return mBasepath + "/" + mdata[position];
    }


    @Override
    public int getCount() {
        if(mdata == null){
            return 0;
        }
        return mdata.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mBasepath + "/" + mdata[position];
    }

    @Override
    public int getItemPosition(Object item) {
//        OriginImage_Fragment frag = (OriginImage_Fragment) item;
//        String path = frag.getpath();
//
//        int index = -1;
//        for(int i=0; i<mdata.length; i++){
//            if(mdata[i].equals(path)){
//                index = i;
//                break;
//            }
//        }
//
//        if(index != -1)
//            return index;
        return PagerAdapter.POSITION_NONE;
    }

    public long getItemId(int position) {
        // give an ID different from position when position has been changed
        return baseId + position;
    }
    public void notifyChangeInPosition(int n) {
        // shift the ID returned by getItemId outside the range of all previous fragments
        baseId += getCount() + n;
    }

}
