package com.broadcaster.util;

import java.util.List;

import org.apache.http.NameValuePair;

import com.broadcaster.BaseActivity;
import com.broadcaster.util.Constants.TASK;

public class TaskUtil {
    public static void removeGPlus(BaseActivity activity, TaskListener listener) {
        TaskManager.getExecuter(activity, listener)
        .addTask(TASK.REMOVE_GPLUS)
        .begin();
    }

    public static void removeFB(BaseActivity activity, TaskListener listener) {
        TaskManager.getExecuter(activity, listener)
        .addTask(TASK.REMOVE_FB)
        .begin();
    }

//    public static void getViewingLocation(BaseActivity activity, TaskListener listener) {
//        TaskManager.getExecuter(activity, listener)
//        .addTask(TASK.SHOW_PROGRESS)
//        .addTask(TASK.GET_TAGS)
//        .addTask(TASK.GET_LOCATION)
//        .begin();
//    }

//    public static void getRealLocation(BaseActivity activity, TaskListener listener) {
//        TaskManager.getExecuter(activity, listener)
//        .addTask(TASK.SHOW_PROGRESS)
//        .addTask(TASK.GET_TAGS)
//        .addTask(TASK.GET_REAL_LOCATION)
//        .begin();
//    }

    public static void getAddress(BaseActivity activity, TaskListener listener, String address) {
        TaskManager.getExecuter(activity, listener)
        .addTask(TASK.GET_ADDRESS, null, address)
        .begin();
    }

    // TODO: LOCATION AND TAGS DON'T EXPIRE BY GET, BUT UPDATE REGULARY BY BG PROCESS OR MANUAL USER REFRESH (LIST RERRESH BUTOTON)
//    public static void refreshPostsAndStarAndFinish(BaseActivity activity, TaskListener listener) {
//        TaskManager.getExecuter(activity, listener)
//        .addTask(TASK.START_LOADING_ACTION)
//        .addTask(TASK.GET_TAGS)
//        .addTask(TASK.GET_LOCATION)
//        .addTask(TASK.LOAD_POSTS)
//        .addTask(TASK.STOP_LOADING_ACTION)
//        .begin();
//    }

    public static void login(BaseActivity activity, TaskListener listener, List<NameValuePair> params) {
        TaskManager.getExecuter(activity, listener)
//        .addTask(TASK.SHOW_PROGRESS)
        .addTask(TASK.LOGIN, params)
        .begin();
    }

    public static void register(BaseActivity activity, TaskListener listener, List<NameValuePair> params) {
        TaskManager.getExecuter(activity, listener)
//        .addTask(TASK.SHOW_PROGRESS)
        .addTask(TASK.REGISTER, params)
        .begin();
    }

    public static void feedback(BaseActivity activity, TaskListener listener, List<NameValuePair> params) {
        TaskManager.getExecuter(activity, listener)
//        .addTask(TASK.SHOW_PROGRESS)
        .addTask(TASK.FEEDBACK, params)
        .begin();
    }
    
    public static void updateUsername(BaseActivity activity, TaskListener listener, List<NameValuePair> params) {
        TaskManager.getExecuter(activity, listener)
        .addTask(TASK.UPDATE_USERNAME, params)
        .begin();
    }

    public static void updateEmail(BaseActivity activity, TaskListener listener, List<NameValuePair> params) {
        TaskManager.getExecuter(activity, listener)
        .addTask(TASK.UPDATE_EMAIL, params)
        .begin();
    }

    public static void updatePassword(BaseActivity activity, TaskListener listener, List<NameValuePair> params) {
        TaskManager.getExecuter(activity, listener)
        .addTask(TASK.UPDATE_PASSWORD, params)
        .begin();
    }

    public static void loginFBUser(BaseActivity activity, TaskListener listener, String id, String username, String email, String accessToken) {
        TaskManager.getExecuter(activity, listener)
        .addTask(TASK.LOGIN_FB, BaseActivity.api.getFBLoginParams(id, username, email, accessToken))
        .begin();
    }

    public static void loginGPlusUser(BaseActivity activity, TaskListener listener, String accountName) {
        TaskManager.getExecuter(activity, listener)
        .addTask(TASK.LOGIN_GPLUS, null, accountName)
        .begin();
    }
}