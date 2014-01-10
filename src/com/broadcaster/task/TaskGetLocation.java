package com.broadcaster.task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import com.broadcaster.BaseActivity;
import com.broadcaster.model.GeocodeResponse;
import com.broadcaster.model.LocationObj;
import com.broadcaster.util.LocationUtil;
import com.broadcaster.util.LocationUtil.LocationResult;
import com.broadcaster.util.Util;

public class TaskGetLocation extends TaskBase {
    public TaskGetLocation () {
        setProgressText("Loading topics...");
    }

    @Override
    protected void onPostExecute(final TaskManager tm) {
        (new LocationUtil()).getLocation(tm.getActivity(), new LocationResult(){
            @Override
            public void gotLocation(Location l) {
                //TODO: MOVE THIS TO TASK, OFF MAIN THREAD
                LocationObj loc = new LocationObj(null, l.getLatitude(), l.getLongitude());
                try {
                    Geocoder gcd = new Geocoder(tm.getActivity(), Locale.getDefault());
                    List<Address> addresses = gcd.getFromLocation(loc.latitude, loc.longitude, 1);
                    if (addresses.size() > 0) {
                        loc.name = addresses.get(0).getLocality();
                        BaseActivity.pref.setRealLocation(loc);
                    }
                } catch (IOException e) {
                    Util.logError(tm.getActivity(), e);
                    if (Util.isNetworkAvailable(tm.getActivity())) {
                        GeocodeResponse gr = BaseActivity.api.sendGeocodeRequest(BaseActivity.api.getGeocodeRequestParams(loc));
                        loc.name = gr.getLocality();
                        BaseActivity.pref.setRealLocation(loc);
                    }
                    else {
                        //TODO: HANDLE NO LOCATION NAME
                    }
                }
                TaskGetLocation.super.onPostExecute(tm);
            }

            @Override
            public void noLocation() {
                //TODO: HANDLE NO LOCATION
//                mgr.tasks.clear();
//                Intent i = new Intent(mgr.activity, NoLocation.class);
//                i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                mgr.activity.startActivity(i);
//                mgr.activity.finish();
            }
        });
    }
}
