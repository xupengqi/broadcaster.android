package com.broadcaster.model;

import java.io.Serializable;

public class LocationObj implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public double latitude;
    public double longitude;
    public String name;
//    public long exp;
    
    public LocationObj(String n, double lat, double lng) {
        name = n;
        latitude = lat;
        longitude = lng;
//        exp = (new Date()).getTime()+3600000; // 1 hour
    }
}
