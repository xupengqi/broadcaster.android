package com.broadcaster.task;

import com.broadcaster.BaseActivity;

public class TaskPostLoadByUser extends TaskPostLoadBase {
    private Integer mUserId;

    public TaskPostLoadByUser (Integer userId) {
        mUserId = userId;
        setProgressText("Loading post...");
    }

    @Override
    protected TaskManager doInBackground(TaskManager... args) {
        if (mAfterId != null) {
            mResponse = BaseActivity.api.getPostsByUser(BaseActivity.api.getAfterParams(BaseActivity.api.getPostsByUserParams(mUserId), mAfterId));
        }
        else {
            mResponse = BaseActivity.api.getPostsByUser(BaseActivity.api.getPostsByUserParams(mUserId));
        }

        return super.doInBackground(args);
    }
}
