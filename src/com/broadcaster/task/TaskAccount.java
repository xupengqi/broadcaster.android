package com.broadcaster.task;

import android.content.Intent;

import com.broadcaster.Account;
import com.broadcaster.BaseActivity;
import com.broadcaster.PostNew;
import com.broadcaster.model.UserObj;
import com.broadcaster.util.Constants;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.plus.model.people.Person;
import com.google.gson.Gson;

public class TaskAccount extends TaskBase {
    private enum LOGIN_ACTION {LOGIN, LOGINFB, LOGINGOOGLE, RMFB, RMGOOGLE, REGISTER};
    private String mUserId;
    private String mUsername;
    private String mEmail;
    private String mPassword;
    private String mToken;
    private Person mGooglePerson;
    private LOGIN_ACTION mAction;
    
    public TaskAccount login(String username, String password) {
        mAction = LOGIN_ACTION.LOGIN;
        mUsername = username;
        mPassword = password;
        setProgressText("Authenticating with server...");
        return this;
    }
    
    public TaskAccount loginFB(String userid, String username, String email, String token) {
        mAction = LOGIN_ACTION.LOGINFB;
        mUserId = userid;
        mUsername = username;
        mEmail = email;
        mToken = token;
        setProgressText("Authenticating with server...");
        return this;
    }
    
    public TaskAccount loginGoogle(Person googlePerson, String email) {
        mAction = LOGIN_ACTION.LOGINGOOGLE;
        mGooglePerson = googlePerson;
        mEmail = email;
        setProgressText("Authenticating with server...");
        return this;
    }
    
    public TaskAccount removeGoogle() {
        mAction = LOGIN_ACTION.RMGOOGLE;
        setProgressText("Disconnecting Google account...");
        return this;
    }
    
    public TaskAccount removeFB() {
        mAction = LOGIN_ACTION.RMFB;
        setProgressText("Disconnecting Facebook account...");
        return this;
    }

    public TaskAccount register (String username, String email, String password) {
        mAction = LOGIN_ACTION.REGISTER;
        mEmail = email;
        mUsername = username;
        mPassword = password;
        setProgressText("Creating user account...");
        return this;
    }

    @Override
    protected TaskManager doInBackground(TaskManager... args) {
        switch(mAction) {
        case LOGIN:
            mResponse = BaseActivity.api.login(BaseActivity.api.getLoginParams(mUsername, mPassword));
            break;
        case LOGINFB:
            mResponse = BaseActivity.api.loginFB(BaseActivity.api.getFBLoginParams(mUserId, mUsername, mEmail, mToken));
            break;
        case LOGINGOOGLE:
            try {
                mToken = GoogleAuthUtil.getToken(
                        args[0].getActivity(),
                        mEmail,
                        "oauth2:" + Scopes.PLUS_LOGIN);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mResponse = BaseActivity.api.loginGPlus(BaseActivity.api.getGPlusLoginParams(mGooglePerson.getId(), mGooglePerson.getDisplayName(), "", mToken));
            break;
        case RMFB:
            mResponse = BaseActivity.api.removeFB(BaseActivity.api.getAuthParams(BaseActivity.pref.getUser()));
            break;
        case RMGOOGLE:
            mResponse = BaseActivity.api.removeGPlus(BaseActivity.api.getAuthParams(BaseActivity.pref.getUser()));
            break;
        case REGISTER:
            mResponse = BaseActivity.api.register(BaseActivity.api.getRegisterParams(mUsername, mEmail, mPassword));
            break;
        }
        return super.doInBackground(args);
    }

    @Override
    protected void onPostExecute(TaskManager tm) {
        if(!mResponse.hasError()) {
            UserObj user = (new Gson()).fromJson(mResponse.data.get("user"), UserObj.class);
            BaseActivity.pref.setUser(user);
            if (tm.getActivity() instanceof Account) {
                tm.getActivity().finish();
                returnTo(tm);
            }
            else {
                tm.getActivity().refreshActivity();
            }
        }
        super.onPostExecute(tm);
    }

    private void returnTo(TaskManager tm) {
        if(tm.getActivity().getIntent() != null && tm.getActivity().getIntent().getExtras() != null) {
            int request = tm.getActivity().getIntent().getExtras().getInt(Constants.RETURN_TO, 0);

            switch(request) {
            case Constants.REQUEST_NEWPOST:
                Intent i = new Intent(tm.getActivity(), PostNew.class);
                i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                tm.getActivity().startActivity(i);
                break;
            }
        }
    }
}
