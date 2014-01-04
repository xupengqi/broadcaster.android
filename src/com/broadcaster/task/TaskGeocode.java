package com.broadcaster.task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.location.Address;
import android.location.Geocoder;

import com.broadcaster.BaseActivity;
import com.broadcaster.model.GeocodeResponse.GRAddress;

public class TaskGeocode extends TaskBase {
    private String mLocation;
    private TaskGeocodeListener mGeocodeListener;
    private List<Address> mLocations;
    private List<GRAddress> mAddresses;
    
    public TaskGeocode (String location) {
        mLocation = location;
        setProgressText("Getting address names...");
    }

    @Override
    protected TaskManager doInBackground(TaskManager... args) {
        try {
            Geocoder gcd = new Geocoder(args[0].getActivity(), Locale.getDefault());
            mLocations = gcd.getFromLocationName(mLocation, 10);
        } catch (IOException e) {
            mAddresses = BaseActivity.api.sendGeocodeRequest(BaseActivity.api.getGeocodeRequestParams(mLocation)).results;
        }

        return super.doInBackground(args);
    }

    @Override
    protected void onPostExecute(TaskManager tm) {
        if (mGeocodeListener != null) {
            if (mLocations != null) {
                mGeocodeListener.postExecute1(mLocations);
            }
            if (mAddresses != null) {
                mGeocodeListener.postExecute2(mAddresses);
            }
        }

        super.onPostExecute(tm);
    }

    //TODO: REMOVE TM.RESULTS AND UPDATE OTHERS TO USE CUSTOMIZED CALLBACK METHODS
    public TaskBase setCallback(TaskGeocodeListener listener) {
        mGeocodeListener = listener;
        return this;
    }

    public interface TaskGeocodeListener {
        void postExecute1(List<Address> locations);
        void postExecute2(List<GRAddress> addresses);
    }
}
