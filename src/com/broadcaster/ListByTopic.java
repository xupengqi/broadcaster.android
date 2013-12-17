package com.broadcaster;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.broadcaster.model.PostObj;
import com.broadcaster.model.PostViewHolder;
import com.broadcaster.model.ResponseObj;
import com.broadcaster.model.TaskItem;
import com.broadcaster.util.TaskManager;
import com.broadcaster.util.TaskUtil;
import com.broadcaster.util.Util;

public class ListByTopic extends BaseDrawerListActivity {
//    private TextView header2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tag = getIntent().getStringExtra("tag");
        setTitle(getTagText(tag));

//        LayoutInflater mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View listHeader = mInflater.inflate(R.layout.module_list_header, null);
//        TextView header1 = (TextView) listHeader.findViewById(R.id.list_header_1);
//        header2 = (TextView) listHeader.findViewById(R.id.list_header_2);
//        header1.setText(tag);
//        postListView.addHeaderView(listHeader);
        //showLocations = true;
        //renderActionBarLocations();
    }

    @Override
    public void onGetLocation() {
        // refresh currentPosts if  location is changed
        if (currentLocation == null || !currentLocation.equals(pref.getViewingLocation().name)) {
            currentLocation = pref.getViewingLocation().name;
            List<PostObj> cachedPosts = pref.getPosts(tag, currentLocation);
            if (cachedPosts.size() > 0) {
                Util.debug(this, "Rendering from cached posts for tags ["+tag+"] @ ["+currentLocation+"]");
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
    protected String getCurrentTopic() {
        return tag;
    }

    @Override
    protected void onNewIntent (Intent intent) {
        super.onNewIntent(intent);
        tag = getIntent().getStringExtra("tag");
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
    protected PostViewHolder getHolderInstance() {
        return new TagPostViewHolder(this);
    }

    public class TagPostViewHolder extends PostViewHolder {
        public TagPostViewHolder(BaseDrawerListActivity a) {
            super(a);
        }

        @Override
        protected void renderPostTop() {
            super.renderPostTop();
            tag.setVisibility(View.GONE);
        }
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
