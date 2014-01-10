package com.broadcaster;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ShareActionProvider;

import com.broadcaster.model.PostObj;
import com.broadcaster.model.PostViewHolder;
import com.broadcaster.model.ResponseObj;
import com.broadcaster.task.TaskBase.TaskListener;
import com.broadcaster.task.TaskManager;
import com.broadcaster.task.TaskPostDel;
import com.broadcaster.task.TaskPostLoadBase;
import com.broadcaster.task.TaskPostLoadByParent;
import com.broadcaster.task.TaskPostReply;
import com.broadcaster.util.Constants.ERROR_CODE;
import com.broadcaster.util.Constants.POST_LIST_TYPE;
import com.broadcaster.util.Constants.PROGRESS_TYPE;
import com.google.gson.Gson;

public class ListByParent extends BaseDrawerListActivity {
    private Integer postId;
    private EditText replyText;
    private RelativeLayout replyBox;
    private Button replySubmit;
    private PostObj mPost;
    private PostViewHolder headerViewHolder;

    protected ShareActionProvider mShareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        replyBox = (RelativeLayout)findViewById(R.id.reply_action);
        replyText = (EditText)findViewById(R.id.replyText);
        replySubmit = (Button)findViewById(R.id.replySubmit);
        replySubmit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (isLoggedIn()) {
                    if (isValidReply()) {
                        hideKeyboard();

                        (new TaskManager(ListByParent.this))
                        .addTask((new TaskPostReply(constructNewPost())).setCallback(new TaskListener() {
                            @Override
                            public void postExecute(TaskManager tm, ResponseObj response) {
                                if(response.getErrorCode() == ERROR_CODE.REQUIRE_LOGIN) {
                                    menuLogin();
                                }
                                replyText.setText(null);
                                loadMorePosts();
                            }
                        }))
                        .setProgress(PROGRESS_TYPE.INLINE)
                        .run();
                    }
                }
                else {
                    menuLogin();
                }
            }
        });

        initProgressElements();
        postId = getIntent().getIntExtra("postId", 0);
    }

    protected boolean isValidReply() {
        if (replyText.getText().length() == 0) {
            replyText.setError("Please enter some text.");
            return false;
        }
        return true;
    }

    @Override
    protected POST_LIST_TYPE getCurrentListType() {
        return POST_LIST_TYPE.PARENT;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.menu_share);

        // Fetch and store ShareActionProvider
        mShareActionProvider = (ShareActionProvider) item.getActionProvider();
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //menu.findItem(R.id.menu_share).setVisible(true);
        menu.findItem(R.id.menu_new_post).setVisible(true);
        menu.findItem(R.id.menu_refresh).setVisible(true);
        menu.findItem(R.id.menu_home).setVisible(true);
        showLoginMenuItem(menu);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
        case R.id.menu_edit:
            if(info.position == 0) {
                updatePost(mPost);
            }
            else {
                updatePost((PostObj)postListAdapter.getItem(info.position-1));
            }
            return true;
        case R.id.menu_delete:
            if(info.position == 0) {
                (new TaskManager(this))
                .addTask((new TaskPostDel(mPost)).setCallback(new TaskListener() {
                    @Override
                    public void postExecute(TaskManager tm, ResponseObj response) {
                        finish();
                    }
                }))
                .run();
            }
            else {
                PostObj postToDelete = (PostObj)postListAdapter.getItem(info.position - postListView.getHeaderViewsCount());
                postToDelete.deleted = true;
                (new TaskManager(this))
                .addTask(new TaskPostDel(postToDelete))
                .run();
                updatePostsList();
            }
            return true;
        default:
            return super.onContextItemSelected(item);
        }
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_comments;
    }

    @Override
    protected PostViewHolder getHolderInstance() {
        return new CommentViewHolder(this);
    }

    @Override
    public void showProgress(PROGRESS_TYPE type) {
        switch(type) {
        case INLINE:
            hideKeyboard();
            if (replyBox != null) {
                replyBox.setVisibility(View.GONE);
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
        case INLINE:
            if (replyBox != null) {
                replyBox.setVisibility(View.VISIBLE);
            }
            break;
        default:
            break;
        }
        super.hideProgress(type);
    }

    private PostObj constructNewPost() {
        PostObj po = new PostObj();
        po.title = replyText.getText().toString();
        po.visibility = 0;
        po.setLocation(pref.getRealLocation());
        po.parentId = postId;
        return po;
    }

    public class CommentViewHolder extends PostViewHolder {
        public CommentViewHolder(BaseDrawerListActivity a) {
            super(a);
        }

        // remove on click action for each list item
        @Override
        protected void registerClickListener() { }

        @Override
        protected void renderPostActions() {
            postAction.setVisibility(View.GONE);
        }
    }

    public class ReplyPostViewHolder extends PostViewHolder {
        public ReplyPostViewHolder(BaseDrawerListActivity a) {
            super(a);
            limitTextLength = false;
        }

        // remove on click action for each list item
        @Override
        protected void registerClickListener() { }

        @Override
        protected void renderPostTop() {
            super.renderPostTop();
            tag.setVisibility(View.GONE);
        }
    }

    public void loadHeaderPost(PostObj p) {
        mPost = p;
        if (mPost == null || mPost.id == null) {
            showError(this.toString(), "Post not found.");
            finish();
            return;
        }

        if (headerViewHolder != null) {
            postListView.removeHeaderView(headerViewHolder.post);
        }
        headerViewHolder = new ReplyPostViewHolder(ListByParent.this);
        headerViewHolder.renderPost(mPost);
        headerViewHolder.makeOP();
        postListView.addHeaderView(headerViewHolder.post);

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, mPost.getUrl());
        sendIntent.setType("text/plain");
        mShareActionProvider.setShareIntent(sendIntent);
    }

    @Override
    protected TaskPostLoadBase getLoadPostTask() {
        return (TaskPostLoadBase) (new TaskPostLoadByParent(postId)).setCallback(new TaskListener() {
            @Override
            public void postExecute(TaskManager tm, ResponseObj response) {
                PostObj post = (new Gson()).fromJson(response.data.get("post"), PostObj.class);
                loadHeaderPost(post);
            }
        });
    }
}
