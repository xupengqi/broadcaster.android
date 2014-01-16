package com.broadcaster.task;

import android.location.Location;

import com.broadcaster.BaseActivity;
import com.broadcaster.model.LocationObj;
import com.broadcaster.model.ResponseObj;
import com.broadcaster.model.ResponseObj.ResponseError;
import com.broadcaster.util.LocationUtil;
import com.broadcaster.util.LocationUtil.LocationResult;

public class TaskGetLocation extends TaskBase {
    public TaskGetLocation () {
        setProgressText("Loading topics...");
    }

    @Override
    protected void onPostExecute(final TaskManager tm) {
        if (BaseActivity.pref.isLocExpired()) {
            (new LocationUtil()).getLocation(tm.getActivity(), new LocationResult(){
                @Override
                public void gotLocation(Location l) {
                    LocationObj location = new LocationObj("somewhere", l.getLatitude(), l.getLongitude());
                    BaseActivity.pref.setRealLocation(location);

                    (new TaskManager(tm.getActivity()))
                    .addTask(new TaskGetLocationName(location))
                    .setCallback(new TaskListener() {
                        @Override
                        public void postExecute(TaskManager innerTm, ResponseObj response) {
                            TaskGetLocation.super.onPostExecute(tm);
                        }
                    })
                    .run();
                }

                @Override
                public void noLocation() {
                    mResponse = new ResponseObj(ResponseError.createNoLocationError());
                    TaskGetLocation.super.onPostExecute(tm);
                }
            });
        }
        else {
            super.onPostExecute(tm);
        }
    }
}
