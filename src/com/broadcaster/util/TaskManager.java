package com.broadcaster.util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.apache.http.NameValuePair;

import android.location.Location;
import android.os.AsyncTask;

import com.broadcaster.BaseActivity;
import com.broadcaster.model.LocationObj;
import com.broadcaster.model.ResponseObj;
import com.broadcaster.model.TaskItem;
import com.broadcaster.util.Constants.TASK;
import com.broadcaster.util.Constants.TASK_RESULT;
import com.broadcaster.util.LocationUtil.LocationResult;

public class TaskManager {

    public Map<TASK_RESULT, Object> results;

    public Queue<TaskItem> tasks;
    public BaseActivity activity;
    private TaskListener listener;
    private PrefUtil pref;

    private TaskManager(BaseActivity a, TaskListener l) {
        results = new HashMap<TASK_RESULT, Object>();
        tasks = new LinkedList<TaskItem>();
        pref = new PrefUtil(a);
        activity = a;
        listener = l;
    }

    public static TaskManager getExecuter(BaseActivity a, TaskListener l) {
        return new TaskManager(a, l);
    }

    public TaskManager addTask(TaskItem ti) {
        tasks.add(ti);
        return this;
    }

    public TaskManager addTask(TASK t) {
        tasks.add(new TaskItem(t));
        return this;
    }

    public TaskManager addTask(TASK t, List<NameValuePair> p) {
        tasks.add(new TaskItem(t, p));
        return this;
    }

    public TaskManager addTask(TASK t, List<NameValuePair> p, String e) {
        tasks.add(new TaskItem(t, p, e));
        return this;
    }

    public void putResult(TASK_RESULT r, Object o) {
        results.put(r, o);
    }

    public ResponseObj getResultRawHTTPResponse() {
        Object response = getResult(TASK_RESULT.RAW_HTTP_RESPONSE);
        if (response != null) {
            return (ResponseObj)response;
        }

        return new ResponseObj();
    }

    public Integer getResultPostId() {
        Object postid = getResult(TASK_RESULT.POSTID);
        if (postid != null) {
            return (Integer)postid;
        }

        return null;
    }

    public Object getResult(TASK_RESULT r) {
        if (results.containsKey(r)) {
            return results.get(r);
        }

        return null;
    }

    public boolean hasMoreTask() {
        return (tasks.size() > 0);
    }

    public void begin() {
        if(tasks.isEmpty()) {
            activity.stopLoadingMode();
        }
        else {
            final TaskItem ti = tasks.poll();
            if (activity != null && ti.task != TASK.SEND_ERROR) {
                activity.setProgressText(ti.task);
            }
            switch(ti.task) {
            case GET_REAL_LOCATION:
                getLocation(ti, pref.getRealLocation());
                break;
            case GET_LOCATION:
                getLocation(ti, pref.getViewingLocation());
                break;
            default:
                new TaskRunner(ti).execute(ti);
                break;
            }
        }
    }

    private void getLocation(final TaskItem ti, LocationObj loc) {
        if (loc == null) {
            (new LocationUtil()).getLocation(activity, new LocationResult(){
                @Override
                public void gotLocation(Location l) {
                    LocationObj loc = new LocationObj(null, l.getLatitude(), l.getLongitude());
                    ti.extra = loc;
                    new TaskRunner(ti).execute(ti);
                }

                @Override
                public void noLocation() {
                    new TaskRunner(ti).execute(new TaskItem(TASK.NO_LOCATION));
                }
            });
        }
        else {
            ti.extra = loc;
            new TaskRunner(ti).execute(ti);
        }
    }

    public class TaskRunner extends AsyncTask<TaskItem, Integer, TaskItem> {
        private TaskItem ti;

        public TaskRunner(TaskItem t) {
            ti = t;
        }

        @Override
        protected void onPreExecute() {
            listener.onPreExecute(ti, TaskManager.this);
        }

        @Override
        protected TaskItem doInBackground(TaskItem... args) {
            listener.onExecute(args[0], TaskManager.this);
            return args[0];
        }

        @Override
        protected void onPostExecute(TaskItem ti) {
            listener.onPostExecute(ti, TaskManager.this);
            begin();
        }
    }
}
