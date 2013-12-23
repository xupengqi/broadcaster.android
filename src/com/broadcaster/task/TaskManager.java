package com.broadcaster.task;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import com.broadcaster.BaseActivity;
import com.broadcaster.model.ResponseObj;
import com.broadcaster.task.TaskBase.TaskListener;
import com.broadcaster.util.Constants.TASK_RESULT;

public class TaskManager {
    private BaseActivity mActivity;
    private Queue<TaskBase> mTasks;
    private boolean mShowProgressOverlay;
    private boolean mShowProgressAction;
    private Map<TASK_RESULT, Object> mResults;
    private TaskListener mListener;
    
    public TaskManager(BaseActivity a) {
        mActivity = a;
        mTasks = new LinkedList<TaskBase>();
        mResults = new HashMap<TASK_RESULT, Object>();
        mShowProgressOverlay = false;
        mShowProgressAction = false;
    }
    
    public TaskManager addTask(TaskBase task) {
        mTasks.add(task);
        return this;
    }
    
    public TaskManager addTask(Queue<TaskBase> tasks) {
        mTasks.addAll(tasks);
        return this;
    }
    
    public TaskManager showProgressOverlay() {
        mShowProgressOverlay = true;
        return this;
    }
    
    public TaskManager showProgressAction() {
        mShowProgressAction = true;
        return this;
    }
    
    public TaskManager setCallback(TaskListener listener) {
        mListener = listener;
        return this;
    }
    
    public void putResult(TASK_RESULT key, Object value) {
        mResults.put(key, value);
    }
    
    public Object getResult(TASK_RESULT key) {
        return mResults.get(key);
    }
    
    public BaseActivity getActivity() {
        return mActivity;
    }
    
    public void run() {
        if (mShowProgressOverlay) {
            mActivity.showProgressOverlay();
        }
        if (mShowProgressAction) {
            mActivity.showProgressAction();
        }
        
        runNext(null);
    }
    
    public void runNext(ResponseObj response) {
        if(response!= null && response.hasError()) {
            mActivity.showError(mActivity.toString(), response.getError());
            mTasks.clear();
        }
        
        if (mTasks.size() > 0) {
            TaskBase task = mTasks.poll();
            mActivity.setProgressText(task.getProgressText());
            mActivity.setProgressImage(task.getProgressImage());
            task.execute(this);
        }
        else {
            finish();
        }
    }
    
    private void finish() {
        if (mShowProgressOverlay) {
            mActivity.hideProgressOverlay();
        }
        if (mShowProgressAction) {
            mActivity.hideProgressAction();
        }
        if (mListener != null) {
            mListener.postExecute(this, null);
        }
    }
}
