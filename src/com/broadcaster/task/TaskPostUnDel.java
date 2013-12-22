package com.broadcaster.task;

import java.util.List;

import org.apache.http.NameValuePair;

import com.broadcaster.BaseActivity;
import com.broadcaster.model.PostObj;

public class TaskPostUnDel extends TaskBase {
    private PostObj mPo;

    public TaskPostUnDel (PostObj po) {
        mPo = po;
        setProgressText("Reviving post...");
    }

    @Override
    protected TaskManager doInBackground(TaskManager... args) {
        List<NameValuePair> params = BaseActivity.api.getDeletePostParams(BaseActivity.pref.getUser(), mPo);
        mResponse = BaseActivity.api.udnoDeletePost(params);

        return super.doInBackground(args);
    }

    @Override
    protected void onPostExecute(TaskManager tm) {
        tm.getActivity().showToast("Post revived.");
        super.onPostExecute(tm);
    }
}
