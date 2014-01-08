package com.broadcaster.task;


public class TaskPostLoadBase extends TaskBase {
    protected Integer mAfterId;

    public TaskPostLoadBase setAfterId(Integer afterId) {
        mAfterId = afterId;
        return this;
    }
}
