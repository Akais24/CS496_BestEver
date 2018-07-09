package com.example.q.swipe_tab;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

public class CustomFragmentPagerAdapter extends FragmentStatePagerAdapter {

        private String[] VIEW_MAPNTOP_TITLES = {"TAB1", "TAB2", "TAB3"};
        private ArrayList<Fragment> fList;

        // Adapter constructor
    public CustomFragmentPagerAdapter(FragmentManager fm, ArrayList<Fragment> fList){
            super(fm);
            this.fList = fList;
        }

        // return tab title
        @Override
        public CharSequence getPageTitle(int position){
        return VIEW_MAPNTOP_TITLES[position];
    }

    // call the fragment
    @Override
    public Fragment getItem(int position){
        return this.fList.get(position);
    }

    @Override
    public int getCount(){
        return fList.size();
    }
}
