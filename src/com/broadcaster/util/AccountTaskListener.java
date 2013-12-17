package com.broadcaster.util;

import android.content.Intent;

import com.broadcaster.Account;
import com.broadcaster.BaseActivity;
import com.broadcaster.PostNew;
import com.broadcaster.model.ResponseObj;
import com.broadcaster.model.TaskItem;
import com.broadcaster.model.UserObj;
import com.broadcaster.util.Constants.TASK_RESULT;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.plus.model.people.Person;
import com.google.gson.Gson;

public class AccountTaskListener extends TaskListener {
    @Override
    public void onExecute(TaskItem ti, TaskManager mgr) {
        UserObj user = BaseActivity.pref.getUser();
        ResponseObj response;
        switch(ti.task) {
        case LOGIN:
            response = BaseActivity.api.login(ti.params);
            processLoginResponse(mgr, response);
            break;
        case REGISTER:
            response = BaseActivity.api.register(ti.params);
            processLoginResponse(mgr, response);
            break;
        case LOGIN_FB:
            response = BaseActivity.api.loginFB(ti.params);
            processLoginResponse(mgr, response);
            break;
        case LOGIN_GPLUS:
            try {
                String token = GoogleAuthUtil.getToken(
                        mgr.activity,
                        ti.extra.toString(),
                        "oauth2:" + Scopes.PLUS_LOGIN);
                Person me = mgr.activity.mPlusClient.getCurrentPerson();
                response = BaseActivity.api.loginGPlus(BaseActivity.api.getGPlusLoginParams(me.getId(), me.getDisplayName(), "", token));
                processLoginResponse(mgr, response);
            } catch (Exception e) {
                e.printStackTrace();
            }
            break;
        case REMOVE_GPLUS:
            response = BaseActivity.api.removeGPlus(BaseActivity.api.getAuthParams(user));
            processLoginResponse(mgr, response);
            break;
        case REMOVE_FB:
            response = BaseActivity.api.removeFB(BaseActivity.api.getAuthParams(user));
            processLoginResponse(mgr, response);
            break;
        default:
            break;
        }
    }

    private void processLoginResponse(TaskManager mgr, ResponseObj response) {
        mgr.putResult(TASK_RESULT.RAW_HTTP_RESPONSE, response);
        if(!response.hasError()) {
            login(mgr.activity, response);
        }
    }

    @Override
    public void onPostExecute(TaskItem ti, TaskManager mgr) {
        ResponseObj response = mgr.getResultRawHTTPResponse();
        if(response.hasError()) {
            mgr.activity.showError(this.toString(), response.getReadableError(ti.task));
        }
    }

    private void login(BaseActivity activity, ResponseObj response) {
        UserObj user = (new Gson()).fromJson(response.data.get("user"), UserObj.class);
        BaseActivity.pref.setUser(user);
        if (activity instanceof Account) {
            activity.finish();
            returnTo(activity);
        }
        else {
            activity.refreshActivity();
        }
    }

    private void returnTo(BaseActivity activity) {
        if(activity.getIntent() != null && activity.getIntent().getExtras() != null) {
            int request = activity.getIntent().getExtras().getInt(Constants.RETURN_TO, 0);

            switch(request) {
            case Constants.REQUEST_NEWPOST:
                Intent i = new Intent(activity, PostNew.class);
                i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                activity.startActivity(i);
                break;
            }
        }
    }
}