package com.broadcaster.util;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.broadcaster.BaseActivity;
import com.broadcaster.NoLocation;
import com.broadcaster.model.GeocodeResponse;
import com.broadcaster.model.LocationObj;
import com.broadcaster.model.ResponseObj;
import com.broadcaster.model.TaskItem;


public class TaskListener {

    public TaskListener() {
    }

    public void onPreExecute(TaskItem ti, TaskManager mgr) {
        switch(ti.task) {
        case SHOW_PROGRESS:
            mgr.activity.startLoadingMode();
            break;
        default:
            break;
        }
    };

    public void onExecute(TaskItem ti, TaskManager mgr) {
        ResponseObj response;
        switch(ti.task) {
        case GET_REAL_LOCATION:
        case GET_LOCATION:
            LocationObj loc = (LocationObj) ti.extra;
            if (loc.name == null) {
                try {
                    Geocoder gcd = new Geocoder(mgr.activity, Locale.getDefault());
                    List<Address> addresses = gcd.getFromLocation(loc.latitude, loc.longitude, 1);
                    if (addresses.size() > 0) {
                        loc.name = addresses.get(0).getLocality();
                        BaseActivity.pref.setRealLocation(loc);
                    }
                } catch (IOException e) {
                    Log.i(this.toString(), "---------UNABLE TO GET LOCATION NAME USING GEOCODER, USING PLAN B---------");
                    Util.logError(mgr.activity, e);
                    GeocodeResponse gr = BaseActivity.api.sendGeocodeRequest(BaseActivity.api.getGeocodeRequestParams(loc));
                    loc.name = gr.getLocality();
                    BaseActivity.pref.setRealLocation(loc);
                }
            }
            break;
        case GET_TAGS:
            if(BaseActivity.pref.getAllTags() == null) {
                response = BaseActivity.api.getTags(BaseActivity.api.getTagsParams());
                List<String> tags = DataParser.parseTags(response);
                BaseActivity.pref.setAllTags(StringUtils.join(tags,","));
            }
            break;
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
            case GET_REAL_LOCATION:
                mgr.activity.onGetRealLocation();
                break;
            case GET_LOCATION:
                mgr.activity.location = BaseActivity.pref.getViewingLocation();
                mgr.activity.onGetLocation();
                break;
            case NO_LOCATION:
                mgr.tasks.clear();
                Intent i = new Intent(mgr.activity, NoLocation.class);
                i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                mgr.activity.startActivity(i);
                mgr.activity.finish();
                break;
            case FINISH:
                mgr.activity.finish();
                break;
            default:
                break;
            }
        }
    }
}
