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

    public static void reply(BaseActivity activity, TaskListener listener) {
        TaskManager.getExecuter(activity, listener)
        .addTask(TASK.ADD_REPLY)
        .addTask(TASK.LOAD_POSTS)
        .begin();
    }

//    public static void deleteParentPost(BaseActivity activity, TaskListener listener, List<NameValuePair> params) {
//        TaskManager.getExecuter(activity, listener)
//        .addTask(TASK.DELETE_POST, params)
//        .addTask(TASK.FINISH)
//        .begin();
//    }
//
//    public static void deletePost(BaseActivity activity, TaskListener listener, List<NameValuePair> params) {
//        TaskManager.getExecuter(activity, listener)
//        .addTask(TASK.DELETE_POST, params)
//        .begin();
//    }

//    public static void undoDelete(BaseActivity activity, TaskListener listener, List<NameValuePair> params) {
//        TaskManager.getExecuter(activity, listener)
//        .addTask(TASK.UNDO_DELETE_POST, params)
//        .begin();
//    }

    public static void getViewingLocation(BaseActivity activity, TaskListener listener) {
        TaskManager.getExecuter(activity, listener)
        .addTask(TASK.SHOW_PROGRESS)
        .addTask(TASK.GET_TAGS)
        .addTask(TASK.GET_LOCATION)
        .begin();
    }

    public static void getRealLocation(BaseActivity activity, TaskListener listener) {
        TaskManager.getExecuter(activity, listener)
        .addTask(TASK.SHOW_PROGRESS)
        .addTask(TASK.GET_TAGS)
        .addTask(TASK.GET_REAL_LOCATION)
        .begin();
    }

    public static void getAddress(BaseActivity activity, TaskListener listener, String address) {
        TaskManager.getExecuter(activity, listener)
        .addTask(TASK.GET_ADDRESS, null, address)
        .begin();
    }

    // TODO :CREAET/UPDATE POST LOCATION
    /*public static void createPost(BaseActivity activity, TaskListener listener, List<TaskItem> attachments) {
        TaskManager tm = TaskManager.getExecuter(activity, listener)
                .addTask(TASK.SHOW_PROGRESS)
                .addTask(TASK.GET_LOCATION)
                .addTask(TASK.ADD_POST);

        for(TaskItem ti : attachments) {
            tm.addTask(ti);
        }
        tm.addTask(TASK.FINISH).begin();
    }*/

    /*public static void updatePost(BaseActivity activity, TaskListener listener, List<TaskItem> attachments) {
        TaskManager tm = TaskManager.getExecuter(activity, listener)
                .addTask(TASK.SHOW_PROGRESS)
                .addTask(TASK.GET_LOCATION)
                .addTask(TASK.UPDATE_POST);

        for(TaskItem ti : attachments) {
            tm.addTask(ti);
        }
        tm.addTask(TASK.FINISH).begin();
    }*/

    public static void loadMorePosts(BaseActivity activity, TaskListener listener) {
        TaskManager.getExecuter(activity, listener)
        .addTask(TASK.GET_LOCATION)
        .addTask(TASK.LOAD_MORE_POSTS)
        .begin();
    }

    public static void refreshPosts(BaseActivity activity, TaskListener listener) {
        TaskManager.getExecuter(activity, listener)
        .addTask(TASK.GET_TAGS)
        .addTask(TASK.GET_LOCATION)
        .addTask(TASK.LOAD_POSTS)
        .begin();
    }

    public static void loadPosts(BaseActivity activity, TaskListener listener) {
        TaskManager.getExecuter(activity, listener)
        .addTask(TASK.LOAD_POSTS)
        .begin();
    }

    public static void refreshPostsFromCache(BaseActivity activity, TaskListener listener) {
        TaskManager.getExecuter(activity, listener)
        .addTask(TASK.LOAD_POSTS_FROM_CACHE)
        .begin();
    }

    public static void refreshPostsAndFinish(BaseActivity activity, TaskListener listener) {
        TaskManager.getExecuter(activity, listener)
        .addTask(TASK.GET_TAGS)
        .addTask(TASK.GET_LOCATION)
        .addTask(TASK.LOAD_POSTS)
        .addTask(TASK.STOP_LOADING_ACTION)
        .begin();
    }

    public static void refreshPostsAndStarAndFinish(BaseActivity activity, TaskListener listener) {
        TaskManager.getExecuter(activity, listener)
        .addTask(TASK.START_LOADING_ACTION)
        .addTask(TASK.GET_TAGS)
        .addTask(TASK.GET_LOCATION)
        .addTask(TASK.LOAD_POSTS)
        .addTask(TASK.STOP_LOADING_ACTION)
        .begin();
    }

    public static void login(BaseActivity activity, TaskListener listener, List<NameValuePair> params) {
        TaskManager.getExecuter(activity, listener)
        .addTask(TASK.SHOW_PROGRESS)
        .addTask(TASK.LOGIN, params)
        .begin();
    }

    public static void register(BaseActivity activity, TaskListener listener, List<NameValuePair> params) {
        TaskManager.getExecuter(activity, listener)
        .addTask(TASK.SHOW_PROGRESS)
        .addTask(TASK.REGISTER, params)
        .begin();
    }

    public static void getAllTags(BaseActivity activity, TaskListener listener) {
        TaskManager.getExecuter(activity, listener)
        .addTask(TASK.SHOW_PROGRESS)
        .addTask(TASK.GET_TAGS)
        .begin();
    }

    public static void feedback(BaseActivity activity, TaskListener listener, List<NameValuePair> params) {
        TaskManager.getExecuter(activity, listener)
        .addTask(TASK.SHOW_PROGRESS)
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