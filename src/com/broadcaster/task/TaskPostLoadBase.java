package com.broadcaster.task;

import java.util.ArrayList;
import java.util.List;

import com.broadcaster.BaseDrawerListActivity;
import com.broadcaster.model.PostObj;
import com.broadcaster.model.ResponseObj;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class TaskPostLoadBase extends TaskBase {
    protected Integer mAfterId;

    @Override
    protected void onPostExecute(TaskManager tm) {
        if (mAfterId == null) {
            ((BaseDrawerListActivity)tm.getActivity()).updatePostsList(parsePosts(mResponse), false, true);
        }
        else {
            ((BaseDrawerListActivity)tm.getActivity()).updatePostsList(parsePosts(mResponse), true, true);
        }
        
        super.onPostExecute(tm);
    }
    
    public TaskPostLoadBase setAfterId(Integer afterId) {
        mAfterId = afterId;
        return this;
    }

    private ArrayList<PostObj> parsePosts(ResponseObj response) {
        ArrayList<PostObj> posts = (new Gson()).fromJson(response.data.get("posts"), new TypeToken<List<PostObj>>(){}.getType());
        return (posts == null) ? new ArrayList<PostObj>() : posts;
    }
}
