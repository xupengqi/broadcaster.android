package com.broadcaster;

import java.util.List;

import android.os.Bundle;
import android.view.Menu;

import com.broadcaster.model.PostObj;
import com.broadcaster.model.ResponseObj;
import com.broadcaster.model.TaskItem;
import com.broadcaster.util.Constants;
import com.broadcaster.util.Constants.DRAWER_ITEMS;
import com.broadcaster.util.TaskManager;
import com.broadcaster.util.TaskUtil;
import com.broadcaster.util.Util;

public class ListByPref extends BaseDrawerListActivity {
//    private TextView header2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //LayoutInflater mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //View listHeader = mInflater.inflate(R.layout.module_list_header, null);
        //TextView header1 = (TextView) listHeader.findViewById(R.id.list_header_1);
        //header2 = (TextView) listHeader.findViewById(R.id.list_header_2);
        //header1.setText(getTagText(pref.getSelectedTags()));
        //postListView.addHeaderView(listHeader);
        initProgressElements();
        //showLocations = true;
        //renderActionBarLocations();
        
        /*if (pref.justInstalled()) {
            menuHelp();
        }*/
    }

    @Override
    protected String getCurrentTopic() {
        return Constants.SYSTEM_TOPICS.Homepage.toString();
    }

    @Override
    public void onGetLocation() {
        // refresh currentPosts if tag set has changed or location is changed
        // use empty string for everything
        if (!pref.getSelectedTags().equals(tag) || pref.getViewingLocation() == null || !currentLocation.equals(pref.getViewingLocation().name)) {
            currentLocation = pref.getViewingLocation().name;
            tag = pref.getSelectedTags();
            List<PostObj> cachedPosts = pref.getPosts(tag, currentLocation);
            if (cachedPosts.size() > 0) {
                Util.debug(this, "Rendering from cached posts for tags ["+tag+"] @ ["+location.name+"]");
                TaskUtil.refreshPostsFromCache(this, listener);
            }
            else {
                Util.debug(this, "Refreshing posts.");
                TaskUtil.loadPosts(this, listener);
            }
//            header2.setText("near "+currentLocation);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        TaskUtil.getViewingLocation(this, listener);
    }

    @Override
    protected void initDrawerItems(List<DRAWER_ITEMS> drawerItems) {
        super.initDrawerItems(drawerItems);
        drawerItems.remove(Constants.DRAWER_ITEMS.Home);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_go_to_topic).setVisible(true);
        menu.findItem(R.id.menu_new_post).setVisible(true);
        menu.findItem(R.id.menu_refresh).setVisible(true);
        showLoginMenuItem(menu);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public ResponseObj loadPosts(TaskItem ti, TaskManager mgr) {
        return api.getPostsByLocation(api.getPostsByLocationParams(location, pref.getRadiusInKm(), tag));
    }

    @Override
    public ResponseObj loadMorePosts(TaskItem ti, TaskManager mgr) {
        return api.getPostsByLocation(api.getAfterParams(api.getPostsByLocationParams(location, pref.getRadiusInKm(), tag), getLastId()));
    }
}
