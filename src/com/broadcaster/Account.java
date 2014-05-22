package com.broadcaster;

import android.os.Bundle;

import com.broadcaster.fragment.AccountLogin;
import com.broadcaster.fragment.AccountPass;
import com.broadcaster.fragment.AccountRegister;
import com.broadcaster.util.Constants;

public class Account extends BaseDrawerTabActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int loginAction = getIntent().getExtras().getInt(Constants.LOGIN_ACTION);
        mViewPager.setCurrentItem(loginAction);
    }

    @Override
    protected void initTabs() {
        getActionBar().addTab(getActionBar().newTab().setText("Password?").setTabListener(tabListener));
        getActionBar().addTab(getActionBar().newTab().setText("Login").setTabListener(tabListener));
        getActionBar().addTab(getActionBar().newTab().setText("New User").setTabListener(tabListener));
    }

    @Override
    protected void initFragments() {
        currentFragments.add(new AccountPass());
        currentFragments.add(new AccountLogin());
        currentFragments.add(new AccountRegister());
    }
}
