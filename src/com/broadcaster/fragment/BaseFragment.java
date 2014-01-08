package com.broadcaster.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.broadcaster.BaseActivity;

public abstract class BaseFragment extends Fragment {
    protected View root;
    protected BaseActivity parent;
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parent = (BaseActivity) getActivity();
    }

    protected boolean isLoggedIn() {
        return (BaseActivity.pref.getUser() != null);
    }

    protected void showError(String source, String error) {
        Log.e(source, error);
        Toast.makeText(parent, error, Toast.LENGTH_LONG).show();
    }
}
