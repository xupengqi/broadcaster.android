package com.broadcaster.task;

import java.util.List;

import org.apache.http.NameValuePair;

import com.broadcaster.BaseActivity;
import com.broadcaster.model.AttachObj;
import com.broadcaster.model.PostObj;

public class TaskAttachmentDel extends TaskBase {
    private PostObj mPost;
    private AttachObj mAttach;

    public TaskAttachmentDel(PostObj post, AttachObj attachment) {
        mPost = post;
        mAttach = attachment;
        setProgressText("Removing attachment...");
    }

    @Override
    protected TaskManager doInBackground(TaskManager... args) {
        List<NameValuePair> params = BaseActivity.api.getAttachmentParams(BaseActivity.pref.getUser(), mAttach, mPost.id);
        mResponse = BaseActivity.api.delAttachment(params);
        return super.doInBackground(args);
    }
}