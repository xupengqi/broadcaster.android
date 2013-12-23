package com.broadcaster.util;

import com.broadcaster.model.ResponseObj;
import com.broadcaster.model.TaskItem;


public class TaskListener {

    public TaskListener() {
    }

    public void onExecute(TaskItem ti, TaskManager mgr) {
//        ResponseObj response;
        switch(ti.task) {
//        case GET_REAL_LOCATION:
//        case GET_LOCATION:
//            break;
//        case GET_TAGS:
//            break;
        default:
            break;
        }
    }

    public void onPostExecute(TaskItem ti, TaskManager mgr) {
        ResponseObj response = mgr.getResultRawHTTPResponse();
        if(response.hasError()) {
            //Log.e("TaskListener", response.getError());
            //Toast.makeText(mgr.activity, response.getError(), Toast.LENGTH_LONG).show();
        }
        else {
            switch(ti.task) {
//            case GET_REAL_LOCATION:
//                mgr.activity.onGetRealLocation();
//                break;
//            case GET_LOCATION:
//                mgr.activity.location = BaseActivity.pref.getViewingLocation();
//                mgr.activity.onGetLocation();
//                break;
//            case NO_LOCATION:
//                break;
            default:
                break;
            }
        }
    }
}
