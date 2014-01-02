package com.broadcaster.util;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;

public class LocationUtil {
    private boolean haveLocation = false;
    private boolean gps_enabled = false;
    private boolean network_enabled = false;
    private Handler h = new Handler();

    public void getLocation(Context c, final LocationResult r) {
        final LocationManager locationManager = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);

        try{gps_enabled=locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);}catch(Exception ex){}
        try{network_enabled=locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);}catch(Exception ex){}

        if(!gps_enabled && !network_enabled)
            r.noLocation();

        final LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                haveLocation = true;
                locationManager.removeUpdates(this);
                r.gotLocation(location);
            }
            public void onProviderDisabled(String provider) {}
            public void onProviderEnabled(String provider) {}
            public void onStatusChanged(String provider, int status, Bundle extras) {}
        };

        if(gps_enabled)
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        if(network_enabled)
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

        final Timer timer = new Timer();
        timer.schedule(new TimerTask (){
            public void run() {
                timer.cancel();
                h.post(new Runnable() {
                    public void run() {
                        if(!haveLocation) {
                            locationManager.removeUpdates(locationListener);

                            Location net_loc=null, gps_loc=null;
                            if(gps_enabled)
                                gps_loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if(network_enabled)
                                net_loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                            if(gps_loc!=null && net_loc!=null) {
                                if(gps_loc.getTime() > net_loc.getTime())
                                    r.gotLocation(gps_loc);
                                else
                                    r.gotLocation(net_loc);
                            }
                            else if(gps_loc!=null){
                                r.gotLocation(gps_loc);
                            }
                            else if(net_loc!=null){
                                r.gotLocation(net_loc);
                            }
                            else {
                                r.noLocation();
                            }
                        }
                    }
                });
            }
        } ,5*1000);
    }

    public static abstract class LocationResult{
        public abstract void gotLocation(Location location);
        public abstract void noLocation();
    }

    public static float seekBarToRadius(int seekBarVal) {
        return Constants.RADIUS_MIN_KM+seekBarVal*Constants.RADIUS_STEP;
    }

    public static int radiusToSeekBar(double radiusInKm) {
        return (int) (radiusInKm/Constants.RADIUS_STEP - 1);
    }
}