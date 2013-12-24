package com.broadcaster;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.broadcaster.fragment.SplashHome;
import com.broadcaster.fragment.SplashTopics;
import com.broadcaster.model.TaskItem;
import com.broadcaster.util.Constants.TASK;
import com.broadcaster.util.PagerAdapterBase;
import com.broadcaster.util.TaskListener;
import com.broadcaster.util.TaskManager;
import com.broadcaster.view.PageIndicator;

public class Splash extends BaseActivity {
    public PageIndicator indicator;
    protected ViewPager mViewPager;
    protected List<Fragment> currentFragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentFragments = new ArrayList<Fragment>();
        currentFragments.add(new SplashHome());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(new PagerAdapterBase(getSupportFragmentManager(), currentFragments));
        mViewPager.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        Splash.this.onPageSelected(position);
                    }
                });
        
        TaskManager.getExecuter(this, new SplashTaskListener())
        .addTask(TASK.SPLASH_PREPARE)
        .begin();
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_splash;
    }

    public void onPageSelected(int position) {
        indicator.setIndicator(position);
    }
    

    public class SplashTaskListener extends TaskListener {
        @Override
        public void onExecute(TaskItem ti, TaskManager mgr) {
            super.onExecute(ti, mgr);

            switch(ti.task) {
            case SPLASH_PREPARE:
//                mgr.addTask(TASK.GET_TAGS)
//                .addTask(TASK.SPLASH_FINISH);
                /*if (!pref.hasSelectedTags()) {
                    mgr.addTask(TASK.GET_TAGS)
                    .addTask(TASK.SPLASH_SETUP);
                }
                else {
                    mgr.addTask(TASK.GET_TAGS)
                    .addTask(TASK.SPLASH_FINISH);
                }*/
                break;
            case SPLASH_FINISH:
                startActivity(new Intent(Splash.this, ListByPref.class));
                finish();
                break;
            default:
                break;
            }
        }

        @Override
        public void onPostExecute(TaskItem ti, TaskManager mgr) {
            super.onPostExecute(ti, mgr);

            switch(ti.task) {
            case SPLASH_SETUP:
                currentFragments.add(new SplashTopics());
                indicator = (PageIndicator) findViewById(R.id.indicator);
                indicator.initIndicators(currentFragments.size());
                indicator.setVisibility(View.VISIBLE);
                mViewPager.getAdapter().notifyDataSetChanged();
                mViewPager.setCurrentItem(1);
                SplashHome home = (SplashHome) currentFragments.get(0);
                home.hideProgress();
                break;
            default:
                break;
            }
        }
    }
}
