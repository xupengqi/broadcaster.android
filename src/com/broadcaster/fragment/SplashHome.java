package com.broadcaster.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.broadcaster.R;

public class SplashHome extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_splash_home, container, false);
        return root;
    }

    public void hideProgress() {
        ProgressBar p = (ProgressBar) root.findViewById(R.id.splash_loading);
        p.setVisibility(View.GONE);
    }
}
