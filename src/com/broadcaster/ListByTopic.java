package com.broadcaster;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.broadcaster.model.PostViewHolder;
import com.broadcaster.task.TaskPostLoadBase;
import com.broadcaster.task.TaskPostLoadByTopic;
import com.broadcaster.util.Constants.POST_LIST_TYPE;

public class ListByTopic extends BaseDrawerListActivity {
    //private TextView header2;
    private String mTopic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTopic = getIntent().getStringExtra("tag");
        setTitle(getTagText(mTopic));

        //LayoutInflater mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //View listHeader = mInflater.inflate(R.layout.module_list_header, null);
        //TextView header1 = (TextView) listHeader.findViewById(R.id.list_header_1);
        //header2 = (TextView) listHeader.findViewById(R.id.list_header_2);
        //header1.setText(tag);
        //postListView.addHeaderView(listHeader);
        //showLocations = true;
        //renderActionBarLocations();
        //currentLocation = pref.getViewingLocation().name;
        //header2.setText("near "+currentLocation);
    }

    @Override
    protected String getCurrentTopic() {
        return mTopic;
    }

    @Override
    protected POST_LIST_TYPE getCurrentListType() {
        return POST_LIST_TYPE.TOPIC;
    }

    @Override
    protected void onNewIntent (Intent intent) {
        super.onNewIntent(intent);
        mTopic = getIntent().getStringExtra("tag");
        loadPosts();
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
    protected TaskPostLoadBase getLoadPostTask() {
        return new TaskPostLoadByTopic(pref.getViewingLocation(), pref.getRadiusInKm(), mTopic);
    }
}
