package com.broadcaster.task;

import com.broadcaster.BaseActivity;
import com.broadcaster.model.LocationObj;
import com.broadcaster.model.ResponseObj;
import com.broadcaster.model.ResponseObj.ResponseError;

public class TaskPostLoadByTopic extends TaskPostLoadBase {
    private LocationObj mLocation;
    private Double mRadius;
    private String mTags;

    public TaskPostLoadByTopic (LocationObj location, double radius, String pref) {
        mLocation = location;
        mRadius = radius;
        mTags = pref;
        setProgressText("Loading post...");
    }

    @Override
    protected TaskManager doInBackground(TaskManager... args) {
        if (mLocation == null) {
            mResponse = new ResponseObj(ResponseError.createNoLocationError());
        }
        else {
            if (mAfterId != null) {
                mResponse = BaseActivity.api.getPostsByLocation(BaseActivity.api.getAfterParams(BaseActivity.api.getPostsByLocationParams(mLocation, mRadius, mTags), mAfterId));
            }
            else {
                mResponse = BaseActivity.api.getPostsByLocation(BaseActivity.api.getPostsByLocationParams(mLocation, mRadius, mTags));
            }
        }

        return super.doInBackground(args);
    }
}
