package com.broadcaster.task;

import java.util.List;

import org.apache.http.NameValuePair;

import com.broadcaster.BaseActivity;
import com.broadcaster.model.AttachObj;
import com.broadcaster.util.Constants.TASK_RESULT;

public class TaskAttachmentDel extends TaskBase {
    private AttachObj mAttach;

    public TaskAttachmentDel(AttachObj attachment) {
        mAttach = attachment;
        setProgressText("Removing attachment...");
    }

    @Override
    protected TaskManager doInBackground(TaskManager... args) {
        TaskManager tm = args[0];
        String postId = tm.getResult(TASK_RESULT.POSTID).toString();
        List<NameValuePair> params = BaseActivity.api.getAttachmentParams(BaseActivity.pref.getUser(), mAttach, postId);
        mResponse = BaseActivity.api.delAttachment(params);
        return super.doInBackground(args);
    }
}