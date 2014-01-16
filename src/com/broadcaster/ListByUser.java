package com.broadcaster;

import android.os.Bundle;
import android.view.Menu;

import com.broadcaster.task.TaskPostLoadBase;
import com.broadcaster.task.TaskPostLoadByUser;
import com.broadcaster.util.Constants.POST_LIST_TYPE;

public class ListByUser extends BaseDrawerListActivity {
    private String username;
    private Integer userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        username = getIntent().getStringExtra("userName");
        userId =  getIntent().getIntExtra("userId", 0);
        setTitle(getTagText(username));
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_home).setVisible(true);
        menu.findItem(R.id.menu_new_post).setVisible(true);
        menu.findItem(R.id.menu_refresh).setVisible(true);
        showLoginMenuItem(menu);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected String getCurrentTopic() {
        return username;
    }

    @Override
    protected POST_LIST_TYPE getCurrentListType() {
        return POST_LIST_TYPE.USER;
    }

    @Override
    protected TaskPostLoadBase getLoadPostTask() {
        return new TaskPostLoadByUser(userId);
    }
}
