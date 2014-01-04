package com.broadcaster.task;

import java.util.List;

import org.apache.http.NameValuePair;

import com.broadcaster.BaseActivity;

public class TaskFeedback extends TaskBase {
    private String mFeedback;
    private String mEmail;

    public TaskFeedback (String email, String feedback) {
        mEmail = email;
        mFeedback = feedback;
        setProgressText("Sending feedback...");
    }

    @Override
    protected TaskManager doInBackground(TaskManager... args) {
        List<NameValuePair> params = BaseActivity.api.getFeedbackParams(mEmail, mFeedback);
        mResponse = BaseActivity.api.feedback(params);
        return super.doInBackground(args);
    }

    @Override
    protected void onPostExecute(TaskManager tm) {
        tm.getActivity().showToast("Thanks for your feedback!");
        super.onPostExecute(tm);
    }
}
