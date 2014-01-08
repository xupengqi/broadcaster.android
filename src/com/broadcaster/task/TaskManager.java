package com.broadcaster.task;

import java.util.LinkedList;
import java.util.Queue;

import com.broadcaster.BaseActivity;
import com.broadcaster.model.ResponseObj;
import com.broadcaster.task.TaskBase.TaskListener;
import com.broadcaster.util.Constants.PROGRESS_TYPE;

public class TaskManager {
    private BaseActivity mActivity;
    private Queue<TaskBase> mTasks;
    private PROGRESS_TYPE mProgressType;
    private TaskListener mListener;
    
    public TaskManager(BaseActivity a) {
        mActivity = a;
        mTasks = new LinkedList<TaskBase>();
    }
    
    public TaskManager addTask(TaskBase task) {
        mTasks.add(task);
        return this;
    }
    
    public TaskManager addTask(Queue<TaskBase> tasks) {
        mTasks.addAll(tasks);
        return this;
    }
    
    public TaskManager setProgress(PROGRESS_TYPE type) {
        mProgressType = type;
        return this;
    }
    
    public TaskManager setCallback(TaskListener listener) {
        mListener = listener;
        return this;
    }
    
    public BaseActivity getActivity() {
        return mActivity;
    }
    
    public void run() {
        if (mProgressType != null) {
            mActivity.showProgress(mProgressType);
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
            finish(response);
        }
    }
    
    private void finish(ResponseObj response) {
        if (mProgressType != null) {
            mActivity.hideProgress(mProgressType);
        }
        if (mListener != null) {
            mListener.postExecute(this, response);
        }
    }
}
