package com.broadcaster.task;

import com.broadcaster.BaseActivity;

public class TaskPostLoadById extends TaskPostLoadBase {
    private String mPostIds;

    public TaskPostLoadById (String postIds) {
        mPostIds = postIds;
        setProgressText("Loading post...");
    }

    @Override
    protected TaskManager doInBackground(TaskManager... args) {
        if (mAfterId == null) {
            mResponse = BaseActivity.api.getPostsById(BaseActivity.api.getPostByIdParams(mPostIds));
        }
        return super.doInBackground(args);
    }
}