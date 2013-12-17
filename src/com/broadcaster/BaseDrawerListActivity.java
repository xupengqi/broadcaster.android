package com.broadcaster;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;

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
import com.broadcaster.util.Constants;
import com.broadcaster.util.Constants.TASK;
import com.broadcaster.util.Constants.TASK_RESULT;
import com.broadcaster.util.DataParser;
import com.broadcaster.util.TaskListener;
import com.broadcaster.util.TaskManager;
import com.broadcaster.util.TaskUtil;
import com.broadcaster.util.Util;
import com.broadcaster.view.ListViewWithOverScroll;
import com.broadcaster.view.ListViewWithOverScroll.OnOverScrollActionListener;

public class BaseDrawerListActivity extends BaseDrawerActivity {
    protected String tag;
    protected String currentLocation;
    protected ActivityListTaskListenerBase listener;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        listener = initTaskListener();

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
                contextualActionBarDo();
                TaskUtil.refreshPostsAndFinish(BaseDrawerListActivity.this, listener);
            }

            @Override
            public void onOverScrollLoadMore() {
                if (haveMoreToLoad) {
                    TaskUtil.loadMorePosts(BaseDrawerListActivity.this, listener);
                }
            }

            @Override
            public void onOverScrollStart() {
                contextualActionBarStart();
            }

            @Override
            public void onOverScrollCancel() {
                contextualActionBarCancel();
            }

            @Override
            public void onOverScrollConfirm(double p) {
                int width = (int) (Util.getWindowWidth(BaseDrawerListActivity.this)*p);
                actionBarProgressBar.setLayoutParams(new LinearLayout.LayoutParams(width, LayoutParams.WRAP_CONTENT));
            }
        });

        registerForContextMenu(postListView);
        getActionBar().setTitle("");

//        locationNames = new ArrayList<String>();
    }

    @Override
    public void onStart() {
        super.onStart();
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

        if (!(this instanceof ListByParent)) {
            MenuItem topicsMenuItem = menu.findItem(R.id.menu_go_to_topic);
            topicsSpinner = (Spinner)topicsMenuItem.getActionView().findViewById(R.id.simple_spinner);
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

            //initTopicsSpinnerItems();
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
            updatePost((PostObj)postListAdapter.getItem(info.position-postListView.getHeaderViewsCount()));
            return true;
        case R.id.menu_delete:
            PostObj postToDelete = (PostObj)postListAdapter.getItem(info.position-postListView.getHeaderViewsCount());
            postToDelete.deleted = true;
            List<NameValuePair> params = api.getDeletePostParams(pref.getUser(), postToDelete.id);
            TaskUtil.deletePost(this, listener, params);
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
            TaskUtil.refreshPostsAndStarAndFinish(BaseDrawerListActivity.this, listener);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_list;
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
    public void setProgressText(TASK task) {
        super.setProgressText(task);
        if (footerText != null) {
            footerText.setText(getTaskMessage(task));
        }
        if (actionBarProgressText != null) {
            actionBarProgressText.setText(getTaskMessage(task));
        }
    }

    protected void initTopicsSpinnerItems() {
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

    protected void refreshTopicsSpinner() {
        initTopicsSpinnerItems();
        invalidateOptionsMenu();
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

    public void noMoreToLoad() {
        footerProgress.setVisibility(View.GONE);
        footerButton.setVisibility(View.VISIBLE);
        footerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                loadingMore();
                TaskUtil.loadMorePosts(BaseDrawerListActivity.this, listener);
            }
        });
    }

    public void loadingMore() {
        footerProgress.setVisibility(View.VISIBLE);
        footerButton.setVisibility(View.GONE);
    }

    protected PostViewHolder getHolderInstance() {
        return new PostViewHolder(this);
    }

    protected void updatePostsList() {
        postListAdapter.notifyDataSetChanged();
    }

    protected void updatePost(PostObj post) {
        Intent i = new Intent(this, PostEdit.class);
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        i.putExtra("postObj", (Serializable)post);
        startActivityForResult(i, 0);
    }
    
    public void undoDelete(PostObj post) {
        post.deleted = false;
        List<NameValuePair> params = api.getDeletePostParams(pref.getUser(), post.id);
        TaskUtil.undoDelete(this, listener, params);
        updatePostsList();
    }

    public class ActivityListTaskListenerBase extends TaskListener {
        @Override
        public void onExecute(TaskItem ti, TaskManager mgr) {
            super.onExecute(ti, mgr);

            ResponseObj response;

            switch(ti.task) {
            case LOAD_POSTS:
                response = loadPosts(ti, mgr);
                mgr.putResult(TASK_RESULT.RAW_HTTP_RESPONSE, response);
                break;
            case LOAD_MORE_POSTS:
                response = loadMorePosts(ti, mgr);
                mgr.putResult(TASK_RESULT.RAW_HTTP_RESPONSE, response);
                break;
            case DELETE_POST:
                response = api.deletePost(ti.params);
                mgr.putResult(TASK_RESULT.RAW_HTTP_RESPONSE, response);
                break;
            case UNDO_DELETE_POST:
                response = api.udnoDeletePost(ti.params);
                mgr.putResult(TASK_RESULT.RAW_HTTP_RESPONSE, response);
                break;
            default:
                break;
            }

            super.onExecute(ti, mgr);
        }

        @Override
        public void onPostExecute(TaskItem ti, TaskManager mgr) {
            super.onPostExecute(ti, mgr);

            ResponseObj response = mgr.getResultRawHTTPResponse();
            ArrayList<PostObj> loadedPosts = new ArrayList<PostObj>();

            if(response.hasError()) {
                showError(this+":onPostExecute", response.getError());
            }
            else {
                switch(ti.task) {
                case LOAD_POSTS:
                    loadedPosts = DataParser.parsePosts(response);
                    currentPosts.clear();
                    currentPosts.addAll(loadedPosts);
                    pref.setPosts(tag, location.name, currentPosts);
                    if(loadedPosts.size() < Constants.POST_PAGE_SIZE) {
                        haveMoreToLoad = false;
                        noMoreToLoad();
                    }
                    else {
                        haveMoreToLoad = true;
                        loadingMore();
                    }
                    updatePostsList();
                    break;
                case LOAD_POSTS_FROM_CACHE:
                    currentPosts.clear();
                    currentPosts.addAll(pref.getPosts(tag, location.name));
                    if(loadedPosts.size() < Constants.POST_PAGE_SIZE) {
                        haveMoreToLoad = false;
                        noMoreToLoad();
                    }
                    else {
                        haveMoreToLoad = true;
                        loadingMore();
                    }
                    updatePostsList();
                    break;
                case LOAD_MORE_POSTS:
                    loadedPosts = DataParser.parsePosts(response);
                    currentPosts.addAll(loadedPosts);
                    pref.setPosts(tag, location.name, currentPosts);
                    if(loadedPosts.size() < Constants.POST_PAGE_SIZE) {
                        haveMoreToLoad = false;
                        noMoreToLoad();
                    }
                    else {
                        haveMoreToLoad = true;
                    }
                    updatePostsList();
                    break;
                case START_LOADING_ACTION:
                    contextualActionBarStart();
                    contextualActionBarDo();
                    break;
                case STOP_LOADING_ACTION:
                    contextualActionBarFinish();
                    break;
                case GET_TAGS:
                    refreshTopicsSpinner();
                    break;
                case DELETE_POST:
                    showToast("Post deleted.");
                    break;
                default:
                    break;
                }
            }
        }
    }

    protected ResponseObj loadPosts(TaskItem ti, TaskManager mgr) {
        throw new UnsupportedOperationException();
    }

    protected ResponseObj loadMorePosts(TaskItem ti, TaskManager mgr) {
        throw new UnsupportedOperationException();
    }

    protected String getCurrentTopic() {
        throw new UnsupportedOperationException();
    }

    protected ActivityListTaskListenerBase initTaskListener() {
        return new ActivityListTaskListenerBase();
    }

    protected int getLastId() {
        int lastId = 0;
        if (currentPosts.size() > 0){
            lastId = currentPosts.get(currentPosts.size()-1).id;
        }
        return lastId;
    }

    public void contextualActionBarStart() {
        getActionBar().setCustomView(R.layout.module_cab);
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setDisplayShowCustomEnabled(true);
//      getActionBar().setDisplayShowTitleEnabled(false);
        reloadStart = true;
        invalidateOptionsMenu(); // this will call onPrepareOptionsMenu()
        actionBarProgressBar = (ProgressBar) getActionBar().getCustomView().findViewById(R.id.action_bar_progress);
        actionBarProgressText = (TextView) getActionBar().getCustomView().findViewById(R.id.action_bar_text);
    }

    private void contextualActionBarDo() {
        actionBarProgressText.setText("Reloading...");
        actionBarProgressBar.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        actionBarProgressBar.setIndeterminate(true);
    }

    public void contextualActionBarFinish() {
        contextualActionBarCancel();
        actionBarProgressText.setText(R.string.action_bar_swipe);
        actionBarProgressBar.setIndeterminate(false);
        postListView.enableOnTouch();
    }

    private void contextualActionBarCancel() {
//        getActionBar().setDisplayShowTitleEnabled(true);
//        if (!showLocations) {
//            getActionBar().setDisplayShowCustomEnabled(false);
//        }
//        if (showLocations) {
//            renderActionBarLocations();
//        }
        reloadStart = false;
        getActionBar().setDisplayShowCustomEnabled(false);
        getActionBar().setDisplayShowHomeEnabled(true);
        invalidateOptionsMenu();
    }

//    protected int translateToViewingPosition(int arg2) {
//        if (arg2 == 0) {
//            return -1;
//        }
//        else {
//            return arg2-1;
//        }
//    }

//    private int translateFromViewingPosition(Integer viewingLocationPosition) {
//        if (viewingLocationPosition >= 0) {
//            return viewingLocationPosition+1;
//        }
//        return 0;
//    }
}
