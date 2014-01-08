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

import com.broadcaster.model.ResponseObj;
import com.broadcaster.task.TaskBase.TaskListener;
import com.broadcaster.task.TaskGetTopics;
import com.broadcaster.task.TaskManager;
import com.broadcaster.util.Constants.PROGRESS_TYPE;
import com.broadcaster.util.TagsListAdapter;

public class Topics extends BaseDrawerActivity {
    protected Fragment fragment;
    
    private View topicsHeader;
    private CheckBox headerTag;
    private ListView tagsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        topicsHeader = mInflater.inflate(R.layout.item_topics_tag, null);
        
        tagsList = (ListView) findViewById(R.id.tags_list);
        tagsList.addHeaderView(topicsHeader);
        
        headerTag = (CheckBox) topicsHeader.findViewById(R.id.tag_text);
        headerTag.setText("Everything");
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

        initProgressElements();
        refreshTopics();
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
            refreshTopics();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_topics;
    }

    private void refreshTopics() {
        (new TaskManager(this))
        .addTask((new TaskGetTopics()).setCallback(new TaskListener() {
            @Override
            public void postExecute(TaskManager tm, ResponseObj response) {
                renderTags();
            }
        }))
        .setProgress(PROGRESS_TYPE.ACTION)
        .run();
    }

    protected void renderTags() {
        TagsListAdapter arrayAdapter = new TagsListAdapter(this, tagsList, headerTag, pref.getAllTags().split(","));
        tagsList.setAdapter(arrayAdapter);
    }
}