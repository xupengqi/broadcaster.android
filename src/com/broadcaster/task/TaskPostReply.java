package com.broadcaster.task;

import java.util.List;

import org.apache.http.NameValuePair;

import com.broadcaster.BaseActivity;
import com.broadcaster.model.PostObj;

public class TaskPostReply extends TaskBase {
    private PostObj mPo;

    public TaskPostReply (PostObj po) {
        mPo = po;
        setProgressText("Submitting reply...");
    }

    @Override
    protected TaskManager doInBackground(TaskManager... args) {
        List<NameValuePair> params = BaseActivity.api.getReplyPostParams(BaseActivity.pref.getUser(), mPo);
        mResponse = BaseActivity.api.newReply(params);

        return super.doInBackground(args);
    }
}
