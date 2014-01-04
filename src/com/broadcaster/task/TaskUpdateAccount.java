package com.broadcaster.task;

import java.util.List;

import org.apache.http.NameValuePair;

import com.broadcaster.BaseActivity;

public class TaskUpdateAccount extends TaskBase {
    private String mUsername;
    private String mEmail;
    private String mPassword;
    
    public TaskUpdateAccount updateUsername(String username) {
        mUsername = username;
        setProgressText("Updating username...");
        return this;
    }
    
    public TaskUpdateAccount updateEmail(String email) {
        mEmail = email;
        setProgressText("Updating email...");
        return this;
    }
    
    public TaskUpdateAccount updatePassword(String password) {
        mPassword = password;
        setProgressText("Updating password...");
        return this;
    }

    @Override
    protected TaskManager doInBackground(TaskManager... args) {
        if (mUsername != null) {
            List<NameValuePair> params = BaseActivity.api.getUpdateUsernameParams(BaseActivity.pref.getUser(), mUsername);
            mResponse = BaseActivity.api.updateUsername(params);
        }
        if (mEmail != null) {
            List<NameValuePair> params = BaseActivity.api.getUpdateEmailParams(BaseActivity.pref.getUser(), mUsername);
            mResponse = BaseActivity.api.updateEmail(params);
        }
        if (mPassword != null) {
            List<NameValuePair> params = BaseActivity.api.getUpdatePasswordParams(BaseActivity.pref.getUser(), mUsername);
            mResponse = BaseActivity.api.updatePassword(params);
        }
        return super.doInBackground(args);
    }
}
