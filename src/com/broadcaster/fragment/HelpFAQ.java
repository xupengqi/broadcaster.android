package com.broadcaster.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.broadcaster.R;

public class HelpFAQ extends HelpBase {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        return root;
    }

    @Override
    protected int getViewResource() {
        return R.layout.fragment_help_faq;
    }
}
