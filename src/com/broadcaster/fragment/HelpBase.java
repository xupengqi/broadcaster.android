package com.broadcaster.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class HelpBase extends BaseFragment {
    protected View root;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(getViewResource(), container, false);

        return root;
    }

    protected int getViewResource() {
        throw new UnsupportedOperationException();
    }
}
