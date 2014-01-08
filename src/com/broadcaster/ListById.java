package com.broadcaster;

import org.apache.commons.lang3.StringUtils;

import android.os.Bundle;
import android.view.Menu;

import com.broadcaster.task.TaskPostLoadBase;
import com.broadcaster.task.TaskPostLoadById;
import com.broadcaster.util.Constants;
import com.broadcaster.util.Constants.POST_LIST_TYPE;

public class ListById extends BaseDrawerListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        footerButton = null;
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
    protected String getCurrentTopic() {
        return Constants.SYSTEM_TOPICS.Starred.toString();
    }

    @Override
    protected POST_LIST_TYPE getCurrentListType() {
        return POST_LIST_TYPE.STAR;
    }

    @Override
    protected TaskPostLoadBase getLoadPostTask() {
        return new TaskPostLoadById(StringUtils.join(pref.getStarred(), ","));
    }
}
