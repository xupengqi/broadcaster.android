package com.broadcaster;

import android.os.Bundle;
import android.view.Menu;

import com.broadcaster.R;
import com.broadcaster.model.ResponseObj;
import com.broadcaster.model.TaskItem;
import com.broadcaster.util.TaskManager;
import com.broadcaster.util.TaskUtil;

public class ListByUser extends BaseDrawerListActivity {
    private String username;
    private Integer userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        username = getIntent().getStringExtra("userName");
        userId =  getIntent().getIntExtra("userId", 0);
        tag = "/u/"+username;
        setTitle(getTagText(tag));
    }

    @Override
    public void onStart() {
        super.onStart();
        TaskUtil.refreshPosts(this, listener);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_go_to_topic).setVisible(true);
        menu.findItem(R.id.menu_home).setVisible(true);
        menu.findItem(R.id.menu_new_post).setVisible(true);
        menu.findItem(R.id.menu_refresh).setVisible(true);
        showLoginMenuItem(menu);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public ResponseObj loadPosts(TaskItem ti, TaskManager mgr) {
        return api.getPostsByUser(api.getPostsByUserParams(userId));
    }

    @Override
    public ResponseObj loadMorePosts(TaskItem ti, TaskManager mgr) {
        return api.getPostsByUser(api.getAfterParams(api.getPostsByUserParams(userId), getLastId()));
    }

    @Override
    protected String getCurrentTopic() {
        return tag;
    }
    
    @Override
    protected void initTopicsSpinnerItems() {
        super.initTopicsSpinnerItems();
        topicSpinnerItems.add(tag);
    }
}
