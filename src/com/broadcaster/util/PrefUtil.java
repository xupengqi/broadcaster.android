package com.broadcaster.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.broadcaster.BaseActivity;
import com.broadcaster.model.LocationObj;
import com.broadcaster.model.PostObj;
import com.broadcaster.model.UserObj;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

public class PrefUtil {
    private BaseActivity context;
    
    private SharedPreferences sharedPref;
    private Gson gson;
    private JsonParser parser;
    private final String KEY_FIRST = "FIRST";
    private final String KEY_USER = "USER";
    private final String KEY_LAST_USER = "LAST_USER";
    private final String KEY_POSTS_PREFIX = "POSTS_";
    private final String KEY_SELECTED_TAGS = "SELECTED_TAGS";
    private final String KEY_USE_EVERYTHING = "USE_EVERYTHING";
    private final String KEY_STARRED = "STARRED";
    private final String KEY_MYTOPICS = "MYTOPICS";
    private final String KEY_ALLTAGS = "ALLTAGS";
    private final String KEY_REAL_LOCATION = "REAL_LOCATION";
    private final String KEY_LOCATION_VIEWING = "LOCATION_VIEWING";
    private final String KEY_LOCATIONS = "LOCATIONS";
    private final String KEY_RADIUS = "RADIUS";
    private final String KEY_ERROR = "ERROR";
    private final String KEY_SEND_ERROR = "ERROR_SEND";

    public PrefUtil(BaseActivity c) {
        context = c;
        sharedPref = c.getSharedPreferences("grid", Context.MODE_PRIVATE);
        gson = new Gson();
        parser = new JsonParser();
    }

    public PrefUtil(SharedPreferences sp) {
        sharedPref = sp;
        gson = new Gson();
        parser = new JsonParser();
    }
    
    public boolean justInstalled() {
        boolean firstTimer = sharedPref.getBoolean(KEY_FIRST, true);
        if (firstTimer) {
            Editor editor = sharedPref.edit();
            editor.putBoolean(KEY_FIRST, false);
            editor.commit();
        }
        return firstTimer;
    }

    private String getPostKey(String tag, String location) {
        return KEY_POSTS_PREFIX+tag+"@"+location;
    }


    public void setPosts(String tag, String location, List<PostObj> posts) {
        Editor editor = sharedPref.edit();
        editor.putString(getPostKey(tag, location), gson.toJson(posts));
        editor.commit();
    }
    public List<PostObj> getPosts(String tag, String location) {
        List<PostObj> posts = new ArrayList<PostObj>();
        try {
            JsonArray arr = parser.parse(sharedPref.getString(getPostKey(tag, location), "[]")).getAsJsonArray();
            for (JsonElement jsonElement : arr) {
                posts.add(gson.fromJson(jsonElement, PostObj.class));
            }
        }
        catch (Exception e) {
            Util.logError(context, e);
        }
        return posts;
    }

    public void addRemoveStarred(Integer id) {
        List<Integer> starred = getStarred();
        if (!starred.contains(id)) {
            starred.add(id);
        }
        else {
            starred.remove(id);
        }
        setStarred(starred);
    }

    private void setStarred(List<Integer> starred) {
        Editor editor = sharedPref.edit();
        editor.putString(KEY_STARRED, gson.toJson(starred));
        editor.commit();
    }

    public List<Integer> getStarred() {
        List<Integer> starred = new ArrayList<Integer>();
        try {
            JsonArray arr = parser.parse(sharedPref.getString(KEY_STARRED, "[]")).getAsJsonArray();
            for (JsonElement jsonElement : arr) {
                starred.add(gson.fromJson(jsonElement, Integer.class));
            }
        }
        catch (Exception e) {
            Util.logError(context, e);
        }
        return starred;
    }

    public boolean isStarred(int id) {
        List<Integer> starred = getStarred();
        return starred.contains(id);
    }

    public void addMyTopics(String topic) {
        List<String> topics = getMyTopics();
        if (!topics.contains(topics)) {
            topics.add(topic);
        }

        Editor editor = sharedPref.edit();
        editor.putString(KEY_MYTOPICS, gson.toJson(topics));
        editor.commit();
    }

    public List<String> getMyTopics() {
        List<String> topics = new ArrayList<String>();
        try {
            JsonArray arr = parser.parse(sharedPref.getString(KEY_MYTOPICS, "[]")).getAsJsonArray();
            for (JsonElement jsonElement : arr) {
                topics.add(gson.fromJson(jsonElement, String.class));
            }
        }
        catch (Exception e) {
            Util.logError(context, e);
        }
        return topics;
    }

    public UserObj getUser() {
        String userStr = sharedPref.getString(KEY_USER, null);
        if (userStr != null) {
            JsonObject userJson = parser.parse(userStr).getAsJsonObject();
            return gson.fromJson(userJson, UserObj.class);
        }
        return null;
    }

    public String getLastLoginUsername() {
        return sharedPref.getString(KEY_LAST_USER, "");
    }

    public void setUser(UserObj user) {
        Editor editor = sharedPref.edit();
        editor.putString(KEY_USER, gson.toJson(user));
        editor.putString(KEY_LAST_USER, user.username);
        editor.commit();
    }

    public void setSelectedTags(String tags) {
        Editor editor = sharedPref.edit();
        editor.putString(KEY_SELECTED_TAGS, tags);
        editor.commit();
    }

    public String getSelectedTags() {
        if(getUseEverything()) {
            return Constants.RESERVED_TAG_EVERYTHING;
        }
        return sharedPref.getString(KEY_SELECTED_TAGS, "");
    }

    public void clearSelectedTags() {
        Editor editor = sharedPref.edit();
        editor.remove(KEY_SELECTED_TAGS);
        editor.commit();
    }

    public boolean getUseEverything() {
        return sharedPref.getBoolean(KEY_USE_EVERYTHING, true);
    }

    public void setUseEverything(boolean everything) {
        Util.debug("Use everything: "+everything);
        Editor editor = sharedPref.edit();
        editor.putBoolean(KEY_USE_EVERYTHING, everything);
        editor.commit();
    }

    public void setAllTags(String allTags) {
        Util.debug("GOT ALL TAGS");
        Editor editor = sharedPref.edit();
        editor.putString(KEY_ALLTAGS, allTags);
        editor.commit();
    }

    public String getAllTags() {
        return sharedPref.getString(KEY_ALLTAGS, null);
    }

    public void clearAllTags() {
        Editor editor = sharedPref.edit();
        editor.remove(KEY_ALLTAGS);
        editor.commit();
    }

    public void setRealLocation(LocationObj l) {
        Editor editor = sharedPref.edit();
        editor.putString(KEY_REAL_LOCATION, gson.toJson(l));
        editor.commit();
        Util.debug("got new location: "+l.name);
    }

    public LocationObj getRealLocation() {
        String locStr = sharedPref.getString(KEY_REAL_LOCATION, null);
        if (locStr == null) {
            return null;
        }
        JsonObject locJson = parser.parse(locStr).getAsJsonObject();
        LocationObj loc = gson.fromJson(locJson, LocationObj.class);

        Long timeNow = (new Date()).getTime();
        if (loc.exp <= timeNow) {
            Util.debug("location expired "+((timeNow-loc.exp)/1000)+" seconds ago: "+loc.name);
            return null;
        }

        Util.debug("location will expire in "+((loc.exp-timeNow)/1000)+" seconds: "+loc.name);
        return loc;
    }
    
    public void clearRealLocation() {
        Editor editor = sharedPref.edit();
        editor.remove(KEY_REAL_LOCATION);
        editor.commit();
    }

    public void setViewingLocation(int position) {
        Editor editor = sharedPref.edit();
        editor.putInt(KEY_LOCATION_VIEWING, position);
        editor.commit();
    }

    public LocationObj getViewingLocation() {
        int position = getViewingLocationPosition();
        if (position >= 0 && position < getLocations().size()) {
            return getLocations().get(position);
        }
        return getRealLocation();
    }

    public void addLocation(LocationObj loc) {
        List<LocationObj> locations = getLocations();
        locations.add(loc);
        setLocations(locations);
    }

    public void removeLocation(int position) {
        List<LocationObj> locations = getLocations();
        locations.remove(position);
        setLocations(locations);
    }
    
    public Integer getViewingLocationPosition() {
        return sharedPref.getInt(KEY_LOCATION_VIEWING, -1);
    }

    public List<LocationObj> getLocations() {
        String location = sharedPref.getString(KEY_LOCATIONS, "[]");
        return gson.fromJson(location, new TypeToken<List<LocationObj>>(){}.getType());
    }

    private void setLocations(List<LocationObj> locations) {
        Editor editor = sharedPref.edit();
        editor.putString(KEY_LOCATIONS, gson.toJson(locations));
        editor.commit();
    }

    public double getRadiusInKm() {
        return sharedPref.getFloat(KEY_RADIUS, 50);
    }

    public void setRadiusInKm(float seekBarToRadius) {
        Util.debug("changed radius to: "+seekBarToRadius+"km");
        Editor editor = sharedPref.edit();
        editor.putFloat(KEY_RADIUS, seekBarToRadius);
        editor.commit();
    }
    
    public void setErrorAllowed(boolean allowed) {
        Editor editor = sharedPref.edit();
        editor.putBoolean(KEY_SEND_ERROR, allowed);
        editor.commit();
    }
    
    public boolean sendErrorAllowed() {
        return sharedPref.getBoolean(KEY_SEND_ERROR, true);
    }
    
    public void addError(Exception e) {
        List<String> errors = getError();
        errors.add(ExceptionUtils.getStackTrace(e));
        if (errors.size() > 5) {
            errors.remove(0);
        }
        
        Editor editor = sharedPref.edit();
        editor.putString(KEY_ERROR, gson.toJson(errors));
        editor.commit();
    }
    
    public List<String> getError() {
        String errors = sharedPref.getString(KEY_ERROR, "[]");
        return gson.fromJson(errors, new TypeToken<List<String>>(){}.getType());
    }

    public void clearPreference() {
        Editor editor = sharedPref.edit();
        editor.remove(KEY_USER);
        editor.remove(KEY_POSTS_PREFIX+Constants.RESERVED_TAG_OWN);
        editor.commit();
        clearSelectedTags();
        clearRealLocation();
    }
}
