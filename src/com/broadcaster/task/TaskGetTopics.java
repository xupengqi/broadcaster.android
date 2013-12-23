package com.broadcaster.task;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;

import com.broadcaster.BaseActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class TaskGetTopics extends TaskBase {
    public TaskGetTopics () {
        setProgressText("Loading topics...");
    }

    @Override
    protected TaskManager doInBackground(TaskManager... args) {
        List<NameValuePair> params = BaseActivity.api.getTagsParams();
        mResponse = BaseActivity.api.getTags(params);

        return super.doInBackground(args);
    }

    @Override
    protected void onPostExecute(TaskManager tm) {
        List<String> tags = (new Gson()).fromJson(mResponse.data.get("tags"), new TypeToken<List<String>>(){}.getType());
        BaseActivity.pref.setAllTags(StringUtils.join(tags,","));
        super.onPostExecute(tm);
    }
}
