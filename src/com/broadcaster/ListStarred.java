package com.broadcaster;

import org.apache.commons.lang3.StringUtils;

import android.os.Bundle;
import android.view.Menu;

import com.broadcaster.R;
import com.broadcaster.model.ResponseObj;
import com.broadcaster.model.TaskItem;
import com.broadcaster.util.Constants;
import com.broadcaster.util.TaskManager;
import com.broadcaster.util.TaskUtil;

public class ListStarred extends BaseDrawerListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tag = Constants.RESERVED_TAG_STARRED;
    }

    @Override
    public void onStart() {
        super.onStart();
        TaskUtil.refreshPosts(this, listener);
    }

    @Override
    protected String getCurrentTopic() {
        return Constants.SYSTEM_TOPICS.Starred.toString();
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
        return api.getPostsById(api.getPostByIdParams(StringUtils.join(pref.getStarred(), ",")));
    }

    @Override
    public ResponseObj loadMorePosts(TaskItem ti, TaskManager mgr) {
        return null;
    }
}
