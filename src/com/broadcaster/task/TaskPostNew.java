package com.broadcaster.task;

import java.util.List;

import org.apache.http.NameValuePair;

import com.broadcaster.BaseActivity;
import com.broadcaster.model.PostObj;
import com.broadcaster.util.Constants.TASK_RESULT;

public class TaskPostNew extends TaskBase {
    private PostObj mPo;

    public TaskPostNew (PostObj po) {
        mPo = po;
        setProgressText("Creating new post...");
    }

    @Override
    protected TaskManager doInBackground(TaskManager... args) {
        List<NameValuePair> params = BaseActivity.api.getNewPostParams(BaseActivity.pref.getUser(), mPo);
        mResponse = BaseActivity.api.newPost(params);

        return super.doInBackground(args);
    }

    @Override
    protected void onPostExecute(TaskManager tm) {
        if (!mResponse.hasError()) {
            tm.putResult(TASK_RESULT.POSTID, mResponse.data.get("postId").getAsInt());
        }
        super.onPostExecute(tm);
    }
}
