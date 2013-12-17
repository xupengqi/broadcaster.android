package com.broadcaster;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;

import com.broadcaster.R;
import com.broadcaster.model.TaskItem;
import com.broadcaster.util.TagsListAdapter;
import com.broadcaster.util.TaskListener;
import com.broadcaster.util.TaskManager;
import com.broadcaster.util.TaskUtil;

public class Topics extends BaseDrawerActivity {
    protected Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initProgressElements();
        TaskUtil.getAllTags(this, new ActivityTopicsTaskListener());
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_home).setVisible(true);
        menu.findItem(R.id.menu_refresh).setVisible(true);
        showLoginMenuItem(menu);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_refresh:
            pref.clearAllTags();
            TaskUtil.getAllTags(this, new ActivityTopicsTaskListener());
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    protected void renderTags() {
        LayoutInflater mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final ListView tagsList = (ListView) findViewById(R.id.tags_list);
        View topicsHeader = mInflater.inflate(R.layout.item_topics_tag, null);
        final CheckBox headerTag = (CheckBox) topicsHeader.findViewById(R.id.tag_text);
        headerTag.setText("Everything");
        tagsList.addHeaderView(topicsHeader);
        headerTag.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                pref.setUseEverything(arg1);
                if (arg1) {
                    headerTag.setTextColor(0xFF000000);
                }
                else {
                    headerTag.setTextColor(0xFFCCCCCC);
                }
                tagsList.invalidateViews();
            }
        });
        if (pref.getUseEverything()) {
            headerTag.setChecked(true);
        }
        else {
            headerTag.setTextColor(0xFFCCCCCC);
        }

        TagsListAdapter arrayAdapter = new TagsListAdapter(this, tagsList, headerTag, pref.getAllTags().split(","));
        tagsList.setAdapter(arrayAdapter);
    }

    public class ActivityTopicsTaskListener extends TaskListener {
        @Override
        public void onPostExecute(TaskItem ti, TaskManager mgr) {
            super.onPostExecute(ti, mgr);
            switch(ti.task) {
            case GET_TAGS:
                renderTags();
                break;
            default:
                break;
            }
        }
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_topics;
    }
}