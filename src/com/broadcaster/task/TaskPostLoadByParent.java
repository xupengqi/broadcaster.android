package com.broadcaster.task;

import com.broadcaster.BaseActivity;

public class TaskPostLoadByParent extends TaskPostLoadBase {
    private Integer mParentId;

    public TaskPostLoadByParent (Integer parentId) {
        mParentId = parentId;
        setProgressText("Loading post...");
    }

    @Override
    protected TaskManager doInBackground(TaskManager... args) {
        if (mAfterId != null) {
            mResponse = BaseActivity.api.getPostsByParent(BaseActivity.api.getAfterParams(BaseActivity.api.getPostsByParentParams(mAfterId), mParentId));
        }
        else {
            mResponse = BaseActivity.api.getPostsByParent(BaseActivity.api.getPostsByParentParams(mParentId));
        }

        return super.doInBackground(args);
    }
}
