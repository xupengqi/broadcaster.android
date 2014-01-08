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
    protected Button submit;
    protected EditText username;
    protected EditText email;
    protected EditText password;
    protected EditText passwordConfirm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(getViewResource(), container, false);
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
}
