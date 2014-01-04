package com.broadcaster.fragment;

import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.broadcaster.BaseActivity;
import com.broadcaster.R;
import com.broadcaster.task.TaskAccount;
import com.broadcaster.task.TaskManager;

public class AccountLogin extends AccountBase {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                (new TaskManager((BaseActivity)getActivity()))
                .addTask((new TaskAccount()).login(username.getText().toString(), password.getText().toString()))
                .run();
            }
        });

        username.setText(BaseActivity.pref.getLastLoginUsername());
        if (username.getText().length() > 0) {
            //password.requestFocus();
        }
        
        root.findViewById(R.id.login_googleplus).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                BaseActivity activity = ((BaseActivity)getActivity());
                if (!activity.mPlusClient.isConnected()) {
                    if (activity.mConnectionResult == null) {
                        activity.mConnectionProgressDialog.show();
                    } else {
                        try {
                            activity.mConnectionResult.startResolutionForResult(activity, BaseActivity.REQUEST_CODE_RESOLVE_ERR);
                        } catch (SendIntentException e) {
                            // Try connecting again.
                            activity.mConnectionResult = null;
                            activity.mPlusClient.connect();
                        }
                    }
                }
            }
        });

        return root;
    }

    @Override
    protected int getViewResource() {
        return R.layout.fragment_account_login;
    }
}
