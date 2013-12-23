package com.broadcaster.task;

import com.broadcaster.model.ResponseObj;

import android.graphics.Bitmap;
import android.os.AsyncTask;

public class TaskBase extends AsyncTask<TaskManager, Integer, TaskManager> {
    protected String mProgressText;
    protected Bitmap mProgressImage;
    protected ResponseObj mResponse;
    protected TaskListener mListener;

    @Override
    protected void onPreExecute() { }

    @Override
    protected TaskManager doInBackground(TaskManager... args) {
        return args[0];
    }

    @Override
    protected void onPostExecute(TaskManager tm) {
        if (mListener != null) {
            mListener.postExecute(tm, mResponse);
        }

        tm.runNext(mResponse);
    }

    protected void setProgressText(String text) {
        mProgressText = text;
    }

    public String getProgressText() {
        return mProgressText;
    }

    public void setProgressImage(Bitmap image) {
        mProgressImage = image;
    }

    public Bitmap getProgressImage() {
        return mProgressImage;
    }

    public TaskBase setCallback(TaskListener listener) {
        mListener = listener;
        return this;
    }

    public interface TaskListener {
        void postExecute(TaskManager tm, ResponseObj response);
    }
}