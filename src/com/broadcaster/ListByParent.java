package com.broadcaster;

import java.util.List;

import org.apache.http.NameValuePair;

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
import com.broadcaster.model.TaskItem;
import com.broadcaster.util.Constants;
import com.broadcaster.util.Constants.TASK;
import com.broadcaster.util.Constants.TASK_RESULT;
import com.broadcaster.util.DataParser;
import com.broadcaster.util.TaskManager;
import com.broadcaster.util.TaskUtil;

public class ListByParent extends BaseDrawerListActivity {
    private Integer postId;
    private EditText replyText;
    private RelativeLayout replyBox;
    private Button replySubmit;
    private PostObj post;

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
                    TaskUtil.reply(ListByParent.this, listener);
                }
                else {
                    menuLogin();
                }
            }
        });

        initProgressElements();
        postId = getIntent().getIntExtra("postId", 0);
        tag = "post"+postId;
    }

    @Override
    public void onStart() {
        super.onStart();
        TaskUtil.refreshPosts(this, listener);
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
                updatePost(post);
            }
            else {
                updatePost((PostObj)postListAdapter.getItem(info.position-1));
            }
            return true;
        case R.id.menu_delete:
            if(info.position == 0) {
                List<NameValuePair> params = api.getDeletePostParams(pref.getUser(), post.id, postId);
                TaskUtil.deleteParentPost(this, listener, params);
            }
            else {
                PostObj postToDelete = (PostObj)postListAdapter.getItem(info.position - postListView.getHeaderViewsCount());
                postToDelete.deleted = true;
                List<NameValuePair> params = api.getDeletePostParams(pref.getUser(), postToDelete.id, postId);
                TaskUtil.deletePost(this, listener, params);
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
    public ResponseObj loadPosts(TaskItem ti, TaskManager mgr) {
        return api.getPostsByParent(api.getPostsByParentParams(postId));
    }

    @Override
    public ResponseObj loadMorePosts(TaskItem ti, TaskManager mgr) {
        return api.getPostsByParent(api.getAfterParams(api.getPostsByParentParams(getLastId()), postId));
    }

    @Override
    protected PostViewHolder getHolderInstance() {
        return new CommentViewHolder(this);
    }

    @Override
    protected ActivityListTaskListenerBase initTaskListener() {
        return new ListReplyTaskListener();
    }

    @Override
    public void startLoadingMode() {
        if (replyBox != null) {
            hideKeyboard();
            replyBox.setVisibility(View.GONE);
            footerProgress.setVisibility(View.VISIBLE);
            footerButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void stopLoadingMode() {
        if (replyBox != null) {
            replyBox.setVisibility(View.VISIBLE);
            footerProgress.setVisibility(View.GONE);
            footerButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setProgressText(TASK task) {
        super.setProgressText(task);
        if (footerText != null) {
            footerText.setText(getTaskMessage(task));
        }
    }

    private PostObj constructNewPost() {
        PostObj po = new PostObj();
        po.title = replyText.getText().toString();
        po.visibility = 0;
        po.setLocation(pref.getRealLocation());
        po.parentId = postId;
        return po;
    }

    public class ListReplyTaskListener extends ActivityListTaskListenerBase {
        @Override
        public void onExecute(TaskItem ti, TaskManager mgr) {
            super.onExecute(ti, mgr);
            ResponseObj response;

            switch(ti.task) {
            case ADD_REPLY:
                response = api.newReply(api.getReplyPostParams(pref.getUser(), constructNewPost()));
                mgr.putResult(TASK_RESULT.RAW_HTTP_RESPONSE, response);
                break;
            default:
                break;
            }
        }

        @Override
        public void onPostExecute(TaskItem ti, TaskManager mgr) {
            super.onPostExecute(ti, mgr);

            ResponseObj response = mgr.getResultRawHTTPResponse();
            if(response.hasError()) {
                if(response.getErrorCode().equals(Constants.API_ERRORS.RESOURCE_NOT_FOUND)) {
                    showError(this+":onPostExecute", response.getError());
                    finish();
                }
                if(response.getErrorCode().equals(Constants.API_ERRORS.REQUIRE_LOGIN)) {
                    mgr.tasks.clear();
                    menuLogin();
                }
            }
            else {
                switch(ti.task) {
                case LOAD_POSTS:
                    post = DataParser.parsePost(response);
                    if (headerViewHolder != null) {
                        postListView.removeHeaderView(headerViewHolder.post);
                    }
                    headerViewHolder = new ReplyPostViewHolder(ListByParent.this);
                    headerViewHolder.renderPost(post);
                    headerViewHolder.makeOP();
                    postListView.addHeaderView(headerViewHolder.post);

                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, post.getUrl());
                    sendIntent.setType("text/plain");
                    mShareActionProvider.setShareIntent(sendIntent);
                    break;
                case ADD_REPLY:
                    replyText.setText(null);
                    hideKeyboard();
                    break;
                default:
                    break;
                }
            }
        }
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
}
