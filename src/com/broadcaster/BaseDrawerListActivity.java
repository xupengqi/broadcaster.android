package com.broadcaster;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.broadcaster.model.PostObj;
import com.broadcaster.model.PostViewHolder;
import com.broadcaster.model.ResponseObj;
import com.broadcaster.model.TaskItem;
import com.broadcaster.task.TaskGetLocation;
import com.broadcaster.task.TaskGetTopics;
import com.broadcaster.task.TaskPostDel;
import com.broadcaster.task.TaskPostLoadBase;
import com.broadcaster.task.TaskPostUnDel;
import com.broadcaster.util.Constants;
import com.broadcaster.util.Constants.POST_LIST_TYPE;
import com.broadcaster.util.Constants.PROGRESS_TYPE;
import com.broadcaster.util.TaskListener;
import com.broadcaster.util.TaskManager;
import com.broadcaster.util.Util;
import com.broadcaster.view.ListViewWithOverScroll;
import com.broadcaster.view.ListViewWithOverScroll.OnOverScrollActionListener;

public class BaseDrawerListActivity extends BaseDrawerActivity {
    protected POST_LIST_TYPE mListType;
    protected String currentLocation;
    protected List<PostObj> currentPosts;
    protected ArrayAdapter<PostObj> postListAdapter;
    protected ListViewWithOverScroll postListView;
    protected PostViewHolder headerViewHolder;
    protected View footerView;
    protected LinearLayout footerProgress;
    protected TextView footerText;
    protected Button footerButton;
    //    protected Spinner locations;
    //    protected List<String> locationNames;
    //    protected ArrayAdapter<String> locationsAdapter;
    //    protected int viewingLocationPosition;

    protected Spinner topicsSpinner;
    protected ArrayAdapter<String> topicsSpinnerAdapter;
    protected List<String> topicSpinnerItems;
    protected boolean spinnerCreated;
    protected boolean haveMoreToLoad = true;
    //    protected boolean showLocations = false;

    private ProgressBar actionBarProgressBar;
    private TextView actionBarProgressText;
    private boolean mActionBarProgressIndeterminate = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // setup listview
        postListView = (ListViewWithOverScroll)findViewById(R.id.posts);
        currentPosts = new ArrayList<PostObj>();
        topicSpinnerItems = new ArrayList<String>();

        LayoutInflater mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footerView = mInflater.inflate(R.layout.module_list_footer, null);
        postListView.addFooterView(footerView);
        footerProgress = (LinearLayout) footerView.findViewById(R.id.footer_progress);
        footerText = (TextView) footerView.findViewById(R.id.footer_text);
        footerButton = (Button) footerView.findViewById(R.id.footer_button);

        // setup adapter with no posts
        postListAdapter = new ArrayAdapter<PostObj>(this, R.layout.item_post, currentPosts) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = getHolderInstance().post;
                }

                PostViewHolder pvh = (PostViewHolder)convertView.getTag();
                try {
                    pvh.renderPost(getItem(position));
                } catch (Exception e) {
                    Util.logError(BaseDrawerListActivity.this, e);
                }

                // check for same post id will result in updated post not being refreshed
                /*int currentPostId = 0;
                    if(pvh.id.getText().toString().length() > 0) {
                        currentPostId = Integer.parseInt(pvh.id.getText().toString());
                    }
                    if(currentPostId != getItem(position).id) {
                        try {
                            pvh.renderPost(getItem(position));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }*/

                return convertView;
            }
        };

        postListView.setAdapter(postListAdapter);

        // setup onscroll listener
        postListView.setOnOverScrollActionListener(new OnOverScrollActionListener () {
            @Override
            public void onOverScrollReload() {
                postListView.disableOnTouch();
                refreshPosts(); //TODO: CALLING THIS WILL CALL showProgressAction AGAIN WHICH IS ALREADY CALLED IN onOverScrollStart
            }

            @Override
            public void onOverScrollLoadMore() {
                if (haveMoreToLoad) {
                    loadMorePosts();
                }
            }

            @Override
            public void onOverScrollStart() {
                mActionBarProgressIndeterminate = false;
                showProgress(PROGRESS_TYPE.ACTION);
                mActionBarProgressIndeterminate = true;
            }

            @Override
            public void onOverScrollCancel() {
                hideProgress(PROGRESS_TYPE.ACTION);
            }

            @Override
            public void onOverScrollConfirm(double p) {
                int width = (int) (Util.getWindowWidth(BaseDrawerListActivity.this)*p);
                actionBarProgressBar.setLayoutParams(new LinearLayout.LayoutParams(width, LayoutParams.WRAP_CONTENT));
            }
        });

        footerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                loadMorePosts();
            }
        });

        registerForContextMenu(postListView);
        getActionBar().setTitle("");

        //        locationNames = new ArrayList<String>();
    }

    @Override
    public void onStart() {
        super.onStart();
        loadPosts();
    }

    @Override
    public void onResume() {
        super.onResume();
        //        if (locations != null) {
        //            updateLocations();
        //            locationsAdapter.notifyDataSetChanged();
        //            viewingLocationPosition = translateFromViewingPosition(pref.getViewingLocationPosition());
        //            locations.setSelection(viewingLocationPosition);
        //        }
    }

    /*protected void renderActionBarLocations() {
        viewingLocationPosition = translateFromViewingPosition(pref.getViewingLocationPosition());
        updateLocations();
        LayoutInflater inflator = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        locationsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, locationNames);
        locations = (Spinner) inflator.inflate(R.layout.module_spinner, null);
        locations.setAdapter(locationsAdapter);
        locations.setSelection(viewingLocationPosition);
        locations.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                if (arg2 == locationNames.size()-1) {
                    menuSettings();
                }
                else if (viewingLocationPosition != arg2){
                    pref.setViewingLocation(translateToViewingPosition(arg2));
                    TaskUtil.refreshPostsAndStarAndFinish(BaseDrawerListActivity.this, listener);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) { }
        });

        getActionBar().setCustomView(locations);
        getActionBar().setDisplayShowCustomEnabled(true);
    }

    private void updateLocations() {
        List<LocationObj> savedLocations = pref.getLocations();
        locationNames.clear();
        locationNames.add("Current Location");
        for (LocationObj loc : savedLocations) {
            locationNames.add(loc.name);
        }
        locationNames.add("Edit locations");
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        if (!(this instanceof ListByParent) && !(this instanceof ListByUser)) {
            MenuItem topicsMenuItem = menu.findItem(R.id.menu_go_to_topic);
            topicsSpinner = (Spinner)topicsMenuItem.getActionView().findViewById(R.id.simple_spinner);
            constructTopicsSpinner();
            topicsSpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, topicSpinnerItems);
            topicsSpinner.setAdapter(topicsSpinnerAdapter);
            topicsSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    String selectedItem = topicSpinnerItems.get(arg2);
                    if(spinnerCreated && !selectedItem.equals(getCurrentTopic())) {
                        Constants.SYSTEM_TOPICS systemTopic = null;
                        try {
                            systemTopic = Constants.SYSTEM_TOPICS.valueOf(selectedItem);
                            switch(systemTopic) {
                            case Homepage:
                                menuHome();
                                break;
                            case Starred:
                                menuStarred();
                                break;
                            case MyPosts:
                                menuMyPosts();
                                break;
                            default:
                                break;
                            }
                            return;
                        }
                        catch (Exception e) {
                            Util.logError(BaseDrawerListActivity.this, e);
                        }

                        // can't go to a user from spinner, so only thing left here is a topic
                        Intent intent = new Intent(BaseDrawerListActivity.this, ListByTopic.class);
                        intent.putExtra("tag", selectedItem);
                        startActivity(intent);
                    }
                    else {
                        spinnerCreated = true;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) { }
            });
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        selectCurrentTopic();
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void invalidateOptionsMenu() {
        spinnerCreated = false;
        super.invalidateOptionsMenu();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_menu, menu);

        // Get the info on which item was selected
        // AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;

        // Get the Adapter behind your ListView (this assumes you're using
        // a ListActivity; if you're not, you'll have to store the Adapter yourself
        // in some way that can be accessed here.)
        // ListAdapter adapter = posts.getAdapter();

        // Retrieve the item that was clicked on
        // Object item = adapter.getItem(info.position);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
        case R.id.menu_edit:
            updatePost((PostObj)postListAdapter.getItem(info.position - postListView.getHeaderViewsCount()));
            return true;
        case R.id.menu_delete:
            PostObj postToDelete = (PostObj)postListAdapter.getItem(info.position - postListView.getHeaderViewsCount());
            postToDelete.deleted = true;

            (new com.broadcaster.task.TaskManager(this))
            .addTask(new TaskPostDel(postToDelete))
            .run();

            updatePostsList();
            return true;
        default:
            return super.onContextItemSelected(item);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_refresh:
            refreshPosts();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_list;
    }
    
    protected void constructTopicsSpinner() {
        topicSpinnerItems.clear();
        topicSpinnerItems.add(Constants.SYSTEM_TOPICS.Homepage.toString());
        topicSpinnerItems.add(Constants.SYSTEM_TOPICS.Starred.toString());
        if(isLoggedIn()) {
            topicSpinnerItems.add(Constants.SYSTEM_TOPICS.MyPosts.toString());
        }

        String[] topics = pref.getAllTags().split(",");
        for (String topic : topics) {
            topicSpinnerItems.add(topic);
        }    
    }

    private void selectCurrentTopic() {
        if (topicsSpinner != null) {
            int i=0;
            for(String topic : topicSpinnerItems) {
                if (topic.equals(getCurrentTopic())) {
                    topicsSpinner.setSelection(i);
                    break;
                }
                i++;
            }
        }
    }

    protected PostViewHolder getHolderInstance() {
        return new PostViewHolder(this);
    }

    public void updatePostsList(List<PostObj> loadedPosts, boolean append, boolean saveToCache) {
        if (!append) {
            currentPosts.clear();
        }
        currentPosts.addAll(loadedPosts);
        if(currentPosts.size() < Constants.POST_PAGE_SIZE) {
            haveMoreToLoad = false;
            hideProgress(PROGRESS_TYPE.INLINE);
        }
        else {
            haveMoreToLoad = true;
            showProgress(PROGRESS_TYPE.INLINE);
        }
        updatePostsList(saveToCache);
    }

    protected void updatePostsList() {
        updatePostsList(true);
    }

    protected void updatePostsList(boolean saveToCache) {
        postListAdapter.notifyDataSetChanged();
        if (saveToCache) {
            pref.setPosts(mListType, currentPosts);
        }
    }

    protected void updatePost(PostObj post) {
        Intent i = new Intent(this, PostEdit.class);
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        i.putExtra("postObj", (Serializable)post);
        startActivityForResult(i, 0);
    }

    public void undoDelete(PostObj post) {
        post.deleted = false;
        (new com.broadcaster.task.TaskManager(this))
        .addTask(new TaskPostUnDel(post))
        .setProgress(PROGRESS_TYPE.ACTION)
        .run();
        updatePostsList();
    }

    public class ActivityListTaskListenerBase extends TaskListener {

        @Override
        public void onPostExecute(TaskItem ti, TaskManager mgr) {
            super.onPostExecute(ti, mgr);

            ResponseObj response = mgr.getResultRawHTTPResponse();

            if(response.hasError()) {
                showError(this+":onPostExecute", response.getError());
            }
            else {
            }
        }
    }

    protected int getLastId() {
        int lastId = 0;
        if (currentPosts.size() > 0){
            lastId = currentPosts.get(currentPosts.size()-1).id;
        }
        return lastId;
    }

    // TODO: LINE actionBarProgressText.setText(getTaskMessage(task)); 
    //10-27 19:05:00.762: E/AndroidRuntime(30769): Caused by: android.view.ViewRootImpl$CalledFromWrongThreadException: Only the original thread that created a view hierarchy can touch its views.
    //10-27 19:05:00.762: E/AndroidRuntime(30769):    at com.broadcaster.BaseDrawerListActivity.setProgressText(BaseDrawerListActivity.java:347)
    //10-27 19:05:00.762: E/AndroidRuntime(30769):    at com.broadcaster.util.TaskManager.begin(TaskManager.java:104)
    //10-27 19:05:00.762: E/AndroidRuntime(30769):    at com.broadcaster.util.Util.logError(Util.java:66)
    //10-27 19:05:00.762: E/AndroidRuntime(30769):    at com.broadcaster.obj.AttachObj$5.onExecute(AttachObj.java:183)
    //10-27 19:05:00.762: E/AndroidRuntime(30769):    at com.broadcaster.util.TaskManager$TaskRunner.doInBackground(TaskManager.java:156)
    //10-27 19:05:00.762: E/AndroidRuntime(30769):    at com.broadcaster.util.TaskManager$TaskRunner.doInBackground(TaskManager.java:1)
    @Override
    public void setProgressText(String text) {
        super.setProgressText(text);
        if (footerText != null) {
            footerText.setText(text);
        }
        //TODO: BaseDrawerTabAtivity should also have action bar progress?
        if (actionBarProgressText != null) {
            actionBarProgressText.setText(text);
        }
    }

    protected void loadPosts() {
        List<PostObj> cachedPosts = pref.getPosts(getCurrentListType());
        if (cachedPosts.size() > 0) {
            Util.debug(this, "Rendering from cached posts for tags ["+getCurrentListType()+"]");
            updatePostsList(cachedPosts, false, false);
        }
        else {
            Util.debug(this, "Refreshing posts.");
            (new com.broadcaster.task.TaskManager(BaseDrawerListActivity.this))
            .addTask(getLoadPostTask())
            .setProgress(PROGRESS_TYPE.INLINE)
            .run();
        }
    }

    @Override
    public void showProgress(PROGRESS_TYPE type) {
        switch(type) {
        case ACTION:
            getActionBar().setCustomView(R.layout.module_cab);
            getActionBar().setDisplayShowHomeEnabled(false);
            getActionBar().setDisplayShowCustomEnabled(true);
            //getActionBar().setDisplayShowTitleEnabled(false);
            reloadStart = true;
            invalidateOptionsMenu(); // this will call onPrepareOptionsMenu()
            actionBarProgressText = (TextView) getActionBar().getCustomView().findViewById(R.id.action_bar_text);
            actionBarProgressBar = (ProgressBar) getActionBar().getCustomView().findViewById(R.id.action_bar_progress);
            actionBarProgressBar.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            actionBarProgressBar.setIndeterminate(mActionBarProgressIndeterminate);
            break;
        case INLINE:
            if (footerProgress != null) {
                footerProgress.setVisibility(View.VISIBLE);
                footerButton.setVisibility(View.GONE);
            }
            break;
        default:
            break;
        }
        super.showProgress(type);
    }

    //TODO: SET BREAKPOINT HERE, THIS IS CALLED TOO MANY TIMES
    @Override
    public void hideProgress(PROGRESS_TYPE type) {
        switch(type) {
        case ACTION:
            if (reloadStart) {
                reloadStart = false;
                getActionBar().setDisplayShowHomeEnabled(true);
                getActionBar().setDisplayShowCustomEnabled(false);
                actionBarProgressBar.setIndeterminate(false);
                invalidateOptionsMenu();
                actionBarProgressText.setText(R.string.action_bar_swipe);
                postListView.enableOnTouch();
            }
            break;
        case INLINE:
            if (footerProgress != null) {
                footerProgress.setVisibility(View.GONE);
                footerButton.setVisibility(View.VISIBLE);
            }
            break;
        default:
            break;
        }
        super.hideProgress(type);
    }

    protected void loadMorePosts() {
        TaskPostLoadBase task = getLoadPostTask();
        task.setAfterId(getLastId());

        (new com.broadcaster.task.TaskManager(this))
        .addTask(task)
        .setProgress(PROGRESS_TYPE.INLINE)
        .run();
    }

    private void refreshPosts() {
        (new com.broadcaster.task.TaskManager(this))
        .addTask(new TaskGetLocation())
        .addTask((new TaskGetTopics()).setCallback(new com.broadcaster.task.TaskBase.TaskListener() {
            @Override
            public void postExecute(com.broadcaster.task.TaskManager tm, ResponseObj response) {
                constructTopicsSpinner();
                invalidateOptionsMenu();
            }
        }))
        .addTask(getLoadPostTask())
        .setProgress(PROGRESS_TYPE.ACTION)
        .run();
    }

    protected TaskPostLoadBase getLoadPostTask() {
        throw new UnsupportedOperationException();
    }

    protected TaskPostLoadBase getLoadMorePostTask() {
        throw new UnsupportedOperationException();
    }

    protected String getCurrentTopic() {
        throw new UnsupportedOperationException();
    }

    protected POST_LIST_TYPE getCurrentListType() {
        throw new UnsupportedOperationException();
    }
}
