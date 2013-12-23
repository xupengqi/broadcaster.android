package com.broadcaster.util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.apache.http.NameValuePair;

import android.os.AsyncTask;

import com.broadcaster.BaseActivity;
import com.broadcaster.model.ResponseObj;
import com.broadcaster.model.TaskItem;
import com.broadcaster.util.Constants.TASK;
import com.broadcaster.util.Constants.TASK_RESULT;

public class TaskManager {

    public Map<TASK_RESULT, Object> results;

    public Queue<TaskItem> tasks;
    public BaseActivity activity;
    private TaskListener listener;

    private TaskManager(BaseActivity a, TaskListener l) {
        results = new HashMap<TASK_RESULT, Object>();
        tasks = new LinkedList<TaskItem>();
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
            activity.hideProgressOverlay();
        }
        else {
            final TaskItem ti = tasks.poll();
            switch(ti.task) {
            default:
                new TaskRunner(ti).execute(ti);
                break;
            }
        }
    }

    public class TaskRunner extends AsyncTask<TaskItem, Integer, TaskItem> {

        public TaskRunner(TaskItem t) {
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
