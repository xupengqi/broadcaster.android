package com.broadcaster;

import java.util.List;

import android.os.Bundle;
import android.view.Menu;

import com.broadcaster.task.TaskPostLoadBase;
import com.broadcaster.task.TaskPostLoadByTopic;
import com.broadcaster.util.Constants;
import com.broadcaster.util.Constants.DRAWER_ITEMS;
import com.broadcaster.util.Constants.POST_LIST_TYPE;

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
        //currentLocation = pref.getViewingLocation().name;
        //header2.setText("near "+currentLocation);
        initProgressElements();
        //showLocations = true;

        /*if (pref.justInstalled()) {
            menuHelp();
        }*/
    }

    @Override
    protected String getCurrentTopic() {
        return Constants.SYSTEM_TOPICS.Homepage.toString();
    }

    @Override
    protected POST_LIST_TYPE getCurrentListType() {
        return POST_LIST_TYPE.PREF;
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
    protected TaskPostLoadBase getLoadPostTask() {
        return new TaskPostLoadByTopic(pref.getViewingLocation(), pref.getRadiusInKm(), pref.getSelectedTags());
    }
}
