package com.broadcaster.util;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PagerAdapterBase extends FragmentStatePagerAdapter {
    private List<Fragment> mFragments;

    public PagerAdapterBase(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        mFragments = fragments;
    }

    @Override
    public Fragment getItem(int i) {
        return mFragments.get(i);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    // NOTE: DO NOT TRY TO DYNAMICALLY UDPATE PAGES
    // IF MUST DO, USE THE CODE BELOW, BUT FRAGMENTS WILL BE RECREATED OFTEN
//    @Override
//    public int getItemPosition(Object object) {
//        for (int i=0; i < currentFragments.size(); i++) {
//            if (object == currentFragments.get(i)) {
//                return i;
//            }
//        }
//        return POSITION_NONE;
//    }
}