package com.broadcaster.task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.location.Address;
import android.location.Geocoder;

import com.broadcaster.BaseActivity;
import com.broadcaster.model.GeocodeResponse;
import com.broadcaster.model.LocationObj;
import com.broadcaster.util.Util;

public class TaskGetLocationName extends TaskBase {
    private LocationObj mLocation;
    
    public TaskGetLocationName (LocationObj location) {
        mLocation = location;
        setProgressText("Getting location name...");
    }

    @Override
    protected TaskManager doInBackground(TaskManager... args) {
        try {
            Geocoder gcd = new Geocoder(args[0].getActivity(), Locale.getDefault());
            List<Address> addresses = gcd.getFromLocation(mLocation.latitude, mLocation.longitude, 1);
            if (addresses.size() > 0) {
                mLocation.name = addresses.get(0).getLocality();
                BaseActivity.pref.setRealLocation(mLocation);
            }
        } catch (IOException e) {
            if (Util.isNetworkAvailable(args[0].getActivity())) {
                GeocodeResponse gr = BaseActivity.api.sendGeocodeRequest(BaseActivity.api.getGeocodeRequestParams(mLocation));
                mLocation.name = gr.getLocality();
                BaseActivity.pref.setRealLocation(mLocation);
            }
        }

        return super.doInBackground(args);
    }

}
