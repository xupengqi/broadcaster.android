package com.broadcaster.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.broadcaster.R;

public class AccountBase extends BaseFragment {
    protected View root;
    protected View loginProgress;
    protected Button submit;
    protected EditText username;
    protected EditText email;
    protected EditText password;
    protected EditText passwordConfirm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(getViewResource(), container, false);
        loginProgress = root.findViewById(R.id.login_progress);
        submit = (Button) root.findViewById(R.id.login_submit);
        username = (EditText) root.findViewById(R.id.login_user);
        email = (EditText) root.findViewById(R.id.login_email);
        password = (EditText) root.findViewById(R.id.login_pass);
        passwordConfirm = (EditText) root.findViewById(R.id.login_pass_confirm);

        return root;
    }

    protected int getViewResource() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void startLoadingMode() {
        submit.setVisibility(View.GONE);
        loginProgress.setVisibility(View.VISIBLE);
    }

    @Override
    public void stopLoadingMode() {
        loginProgress.setVisibility(View.GONE);
        submit.setVisibility(View.VISIBLE);
    }

    @Override
    public void setProgressText(CharSequence taskMessage) {
        //loginProgressText.setText(taskMessage);
    }

}
