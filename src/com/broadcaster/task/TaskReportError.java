package com.broadcaster.task;

import com.broadcaster.BaseActivity;

public class TaskReportError extends TaskBase {
    private Exception mException;

    public TaskReportError (Exception e) {
        mException = e;
        setProgressText("Reporting error...");
    }

    @Override
    protected TaskManager doInBackground(TaskManager... args) {
        mResponse = BaseActivity.api.sendError(mException);
        return super.doInBackground(args);
    }
}
