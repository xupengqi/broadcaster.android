package com.broadcaster.model;

import java.util.List;

import org.apache.http.NameValuePair;

import com.broadcaster.util.Constants;
import com.broadcaster.util.Constants.TASK;



public class TaskItem {
    public TASK task;
    public List<NameValuePair> params;
    public Object extra;
    public Constants.MEDIA_TYPE type;

    public TaskItem(TASK t) {
        task = t;
    }

    public TaskItem(TASK t, List<NameValuePair> p) {
        task = t;
        params = p;
    }

    public TaskItem(TASK t, List<NameValuePair> p, String e) {
        task = t;
        params = p;
        extra = e;
    }

    public TaskItem(TASK t, List<NameValuePair> p, String e, Constants.MEDIA_TYPE tt) {
        task = t;
        params = p;
        extra = e;
        type = tt;
    }
}