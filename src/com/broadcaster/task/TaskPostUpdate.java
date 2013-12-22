package com.broadcaster.task;

import java.util.List;

import org.apache.http.NameValuePair;

import com.broadcaster.BaseActivity;
import com.broadcaster.model.PostObj;
import com.broadcaster.util.Constants.TASK_RESULT;

public class TaskPostUpdate extends TaskBase {
    private PostObj mPo;

    public TaskPostUpdate (PostObj po) {
        mPo = po;
        setProgressText("Updating new post...");
    }

    @Override
    protected TaskManager doInBackground(TaskManager... args) {
        List<NameValuePair> params = BaseActivity.api.getUpdatePostParams(BaseActivity.pref.getUser(), mPo);
        mResponse = BaseActivity.api.updatePost(params);

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
