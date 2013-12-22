package com.broadcaster.task;

import java.util.List;

import org.apache.http.NameValuePair;

import com.broadcaster.BaseActivity;
import com.broadcaster.model.PostObj;

public class TaskPostDel extends TaskBase {
    private PostObj mPo;

    public TaskPostDel (PostObj po) {
        mPo = po;
        setProgressText("Deleting post...");
    }

    @Override
    protected TaskManager doInBackground(TaskManager... args) {
        List<NameValuePair> params = BaseActivity.api.getDeletePostParams(BaseActivity.pref.getUser(), mPo);
        mResponse = BaseActivity.api.deletePost(params);

        return super.doInBackground(args);
    }

    @Override
    protected void onPostExecute(TaskManager tm) {
        tm.getActivity().showToast("Post deleted.");
        super.onPostExecute(tm);
    }
}
