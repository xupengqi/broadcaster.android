package com.broadcaster.task;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.http.NameValuePair;

import com.broadcaster.BaseActivity;
import com.broadcaster.PostNew;
import com.broadcaster.model.AttachObj;
import com.broadcaster.model.PostObj;
import com.broadcaster.util.Constants.MEDIA_TYPE;
import com.broadcaster.util.ImageUtil;
import com.broadcaster.util.Util;

public class TaskAttachmentNew extends TaskBase {
    private AttachObj mAttach;
    private PostObj mPost;
    private PostNew mActivity;

    public TaskAttachmentNew(PostNew activity, PostObj post, AttachObj attachment) {
        mPost = post;
        mAttach = attachment;
        mActivity = activity;
        setProgressText("Uploading attachment...");
        try {
            setProgressImage(attachment.getThumb(mActivity));
        } catch (IOException e) {
            Util.logError(mActivity, e);
        }
    }

    @Override
    protected TaskManager doInBackground(TaskManager... args) {
        TaskManager tm = args[0];
        File file = new File(mAttach.fileName); 
        if (mAttach.type == MEDIA_TYPE.IMAGE) {
            try {
                file = ImageUtil.optimizeImage(tm.getActivity(), file, 75);
            } catch (IOException e) {
                Util.logError(tm.getActivity(), e);
            }
        }

        List<NameValuePair> params = BaseActivity.api.getAttachmentParams(BaseActivity.pref.getUser(), mAttach, mPost.id);
        mResponse = BaseActivity.api.newAttachment(params, file, mAttach.type);

        if (mAttach.type == MEDIA_TYPE.VIDEO) {
            try {
                File thumb = ImageUtil.optimizeImage(tm.getActivity(), ImageUtil.createVideoThumb(mAttach.fileName), 75);
                BaseActivity.api.newThumb(BaseActivity.api.getNewThumbParams(BaseActivity.pref.getUser(), mPost.id, mResponse.data.get("attachId").getAsString()), thumb);
            } catch (IOException e) {
                Util.logError(tm.getActivity(), e);
            }
        }

        return super.doInBackground(args);
    }
}
