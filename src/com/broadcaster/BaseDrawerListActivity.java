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
import android.widget.Spinner;
import android.widget.TextView;

import com.broadcaster.model.PostObj;
import com.broadcaster.model.PostViewHolder;
import com.broadcaster.model.ResponseObj;
import com.broadcaster.task.TaskBase.TaskListener;
import com.broadcaster.task.TaskGetLocation;
import com.broadcaster.task.TaskGetTopics;
import com.broadcaster.task.TaskManager;
import com.broadcaster.task.TaskPostDel;
import com.broadcaster.task.TaskPostLoadBase;
import com.broadcaster.task.TaskPostUnDel;
import com.broadcaster.util.Constants;
import com.broadcaster.util.Constants.POST_LIST_TYPE;
import com.broadcaster.util.Constants.PROGRESS_TYPE;
import com.broadcaster.util.Util;
import com.broadcaster.view.ListViewWithOverScroll;
import com.broadcaster.view.ListViewWithOverScroll.OnOverScrollActionListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class BaseDrawerListActivity extends BaseDrawerActivity {
    protected ArrayAdapter<PostObj> postListAdapter;
    protected ListViewWithOverScroll postListView;
    protected boolean showLoadMore = true;

    private POST_LIST_TYPE mListType;
    private List<PostObj> currentPosts;
    private Button footerButton;
    private View footerView;
    private LinearLayout footerProgress;
    private TextView footerText;
    private Spinner topicsSpinner;
    private ArrayAdapter<String> topicsSpinnerAdapter;
    private List<String> topicSpinnerItems;
    private boolean spinnerCreated;
    private boolean haveMoreToLoad = true;
    private boolean isLoadingMore = false;

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
        if (showLoadMore) {
            footerButton.setVisibility(View.VISIBLE);
        }

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
                refreshPosts();
            }

            @Override
            public void onOverScrollLoadMore() {
                if (haveMoreToLoad && !isLoadingMore) {
                    loadMorePosts();
                }
            }

            @Override
            public void onOverScrollStart() {
                if (!isShowingActionProgress) {
                    mActionBarProgressIndeterminate = false;
                    showProgress(PROGRESS_TYPE.ACTION);
                    mActionBarProgressIndeterminate = true;
                }
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
    }

    @Override
    public void onStart() {
        super.onStart();
        if (currentPosts.size() == 0) {
            loadPosts();
        }
    }

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

            (new TaskManager(this))
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
        if(loadedPosts.size() < Constants.POST_PAGE_SIZE) {
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
        (new TaskManager(this))
        .addTask(new TaskPostUnDel(post))
        .setProgress(PROGRESS_TYPE.ACTION)
        .run();
        updatePostsList();
    }

    protected int getLastId() {
        int lastId = 0;
        if (currentPosts.size() > 0){
            lastId = currentPosts.get(currentPosts.size()-1).id;
        }
        return lastId;
    }

    protected void loadPosts() {
        List<PostObj> cachedPosts = pref.getPosts(getCurrentListType());
        if (cachedPosts.size() > 0) {
            Util.debug(this, "Rendering from cached posts for tags ["+getCurrentListType()+"]");
            updatePostsList(cachedPosts, false, false);
        }
        else {
            refreshPosts();
        }
    }

    private void refreshPosts() {
        postListView.disableOverscroll();
        (new TaskManager(this))
        .addTask(new TaskGetLocation())
        .addTask((new TaskGetTopics()).setCallback(new TaskListener() {
            @Override
            public void postExecute(TaskManager tm, ResponseObj response) {
                constructTopicsSpinner();
                invalidateOptionsMenu();
            }
        }))
        .addTask(getLoadPostTask())
        .setProgress(PROGRESS_TYPE.ACTION)
        .setCallback(new TaskListener() {
            @Override
            public void postExecute(TaskManager tm, ResponseObj response) {
                updatePostsList(parsePosts(response), false, true);
                postListView.enableOverscroll();
            }
        })
        .run();
    }

    protected void loadMorePosts() {
        isLoadingMore = true;
        (new TaskManager(this))
        .addTask(getLoadPostTask().setAfterId(getLastId()))
        .setProgress(PROGRESS_TYPE.INLINE)
        .setCallback(new TaskListener() {
            @Override
            public void postExecute(TaskManager tm, ResponseObj response) {
                updatePostsList(parsePosts(response), true, true);
                isLoadingMore = false;
            }
        })
        .run();
    }

    @Override
    public void showProgress(PROGRESS_TYPE type) {
        switch(type) {
        case INLINE:
            if (footerProgress != null) {
                footerProgress.setVisibility(View.VISIBLE);
            }
            if (footerButton != null) {
                footerButton.setVisibility(View.GONE);
            }
            break;
        default:
            break;
        }
        super.showProgress(type);
    }

    @Override
    public void hideProgress(PROGRESS_TYPE type) {
        switch(type) {
        case ACTION:
            if (isShowingActionProgress) {
                postListView.enableOnTouch();
            }
            break;
        case INLINE:
            if (footerProgress != null) {
                footerProgress.setVisibility(View.GONE);
            }
            if (footerButton != null && showLoadMore) {
                footerButton.setVisibility(View.VISIBLE);
            }
            break;
        default:
            break;
        }
        super.hideProgress(type);
    }

    @Override
    public void setProgressText(String text) {
        if (footerText != null) {
            footerText.setText(text);
        }
        super.setProgressText(text);
    }

    private ArrayList<PostObj> parsePosts(ResponseObj response) {
        if (response == null) return new ArrayList<PostObj>();
        ArrayList<PostObj> posts = (new Gson()).fromJson(response.data.get("posts"), new TypeToken<List<PostObj>>(){}.getType());
        return (posts == null) ? new ArrayList<PostObj>() : posts;
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
