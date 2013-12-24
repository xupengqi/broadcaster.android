package com.broadcaster;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.broadcaster.fragment.BaseFragment;
import com.broadcaster.util.Constants.PROGRESS_TYPE;
import com.broadcaster.util.PagerAdapterBase;

public class BaseDrawerTabActivity extends BaseDrawerActivity {
    protected ViewPager mViewPager;
    protected List<Fragment> currentFragments;
    protected TabListener tabListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentFragments = new ArrayList<Fragment>();
        initFragments();
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(new PagerAdapterBase(getSupportFragmentManager(), currentFragments));
        mViewPager.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        BaseDrawerTabActivity.this.onPageSelected(position);
                        changePage(position);
                    }
                });

        tabListener = new TabListener() {
            @Override
            public void onTabReselected(Tab tab, FragmentTransaction ft) { }

            @Override
            public void onTabSelected(Tab tab, FragmentTransaction ft) {
                mViewPager.setCurrentItem(tab.getPosition());
                changePage(tab.getPosition());
            }

            @Override
            public void onTabUnselected(Tab tab, FragmentTransaction ft) {}
        };

        getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        initTabs();

        initProgressElements();
    }

    protected void changePage(int position) { }

    protected void initTabs() {
        throw new UnsupportedOperationException();
    }

    protected void initFragments() {
        throw new UnsupportedOperationException();
    }

    public void onPageSelected(int position) {
        getActionBar().setSelectedNavigationItem(position);
    }
    @Override
    public void showProgress(PROGRESS_TYPE type) {
        switch(type) {
        case ACTION:
            break;
        case INLINE:
            if (currentFragments != null) {
                //super.startLoadingMode();
                //getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
                BaseFragment fragment = (BaseFragment) currentFragments.get(mViewPager.getCurrentItem());
                fragment.startLoadingMode();
                hideKeyboard();
            }
            break;
        default:
            super.showProgress(type);
            break;
        }
    }

    @Override
    public void hideProgress(PROGRESS_TYPE type) {
        switch(type) {
        case ACTION:
            break;
        case INLINE:
            if (currentFragments != null) {
                //super.stopLoadingMode();
                //getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
                BaseFragment fragment = (BaseFragment) currentFragments.get(mViewPager.getCurrentItem());
                fragment.stopLoadingMode();
            }
            break;
        default:
            super.hideProgress(type);
            break;
        }
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_drawer_pager;
    }
}
