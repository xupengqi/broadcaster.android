package com.broadcaster.model;

import java.io.IOException;
import java.net.MalformedURLException;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.broadcaster.BaseActivity;
import com.broadcaster.BaseDrawerListActivity;
import com.broadcaster.ListByParent;
import com.broadcaster.ListByTopic;
import com.broadcaster.ListByUser;
import com.broadcaster.R;
import com.broadcaster.model.AttachObj.AttachmentInteractListener;
import com.broadcaster.util.Constants.MEDIA_TYPE;
import com.broadcaster.util.DateUtil;
import com.broadcaster.util.Util;

public class PostViewHolder {
    protected BaseDrawerListActivity activity;
    
    public TextView id;
    public View post;
    protected TextView user;
    protected TextView title;
    protected TextView text;
    protected TextView time;
    protected TextView tag;
    protected TextView tag2;
    protected TextView location;
    protected ImageView postEdit;
    protected LinearLayout attachmentsView;
    protected HorizontalScrollView attachmentsViewContainer;
    protected LinearLayout postAction;
    protected TextView comment;
    protected Button share;
    protected TextView star;
    protected LinearLayout containerTop;
    protected FrameLayout containerMid;
    protected LinearLayout containerBottom;
    protected LinearLayout postAttr;
    protected View attachmentsPlaceholder;
    protected LinearLayout postDeletedGroup;
    protected Button postUndoDelete;
    protected boolean limitTextLength = true;
    
    private PostObj postItem;

    public PostViewHolder(BaseDrawerListActivity a) {
        LayoutInflater mInflater = (LayoutInflater)a.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        post = mInflater.inflate(R.layout.item_post, null);

        init(a, post);

        post.setTag(this);
    }

    public PostViewHolder(BaseDrawerListActivity a, View root) {
        init(a, root);
    }

    public void init(BaseDrawerListActivity a, View root) {
        activity = a;

        id = (TextView) root.findViewById(R.id.item_post_id);
        user = (TextView) root.findViewById(R.id.item_post_user);
        title = (TextView) root.findViewById(R.id.item_post_title);
        text = (TextView) root.findViewById(R.id.item_post_text);
        time = (TextView) root.findViewById(R.id.item_post_created);
        tag = (TextView) root.findViewById(R.id.item_post_tag);
        tag2 = (TextView) root.findViewById(R.id.item_post_tag2);
        location = (TextView) root.findViewById(R.id.item_post_location);
        postEdit = (ImageView)root.findViewById(R.id.item_post_edit);
        attachmentsView = (LinearLayout)root.findViewById(R.id.item_post_attachments);
        attachmentsViewContainer = (HorizontalScrollView) root.findViewById(R.id.item_post_attachments_scroll);
        attachmentsPlaceholder = root.findViewById(R.id.attachment_placeholder);
        postAction = (LinearLayout)root.findViewById(R.id.item_post_action);
        containerTop = (LinearLayout)root.findViewById(R.id.item_post_container_top);
        containerMid = (FrameLayout)root.findViewById(R.id.item_post_container_mid);
        containerBottom = (LinearLayout)root.findViewById(R.id.item_post_container_bottom);
        comment = (TextView)root.findViewById(R.id.item_post_comment);
        share = (Button)root.findViewById(R.id.item_post_share);
        star = (TextView)root.findViewById(R.id.item_post_star);
        postAttr = (LinearLayout)root.findViewById(R.id.item_post_attr_group);
        postDeletedGroup = (LinearLayout)root.findViewById(R.id.item_post_container_deleted);
        postUndoDelete = (Button)root.findViewById(R.id.item_post_undo_delete);

        registerClickListener();
        tag.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View tagView) {
                Intent intent = new Intent(activity, ListByTopic.class);
                intent.putExtra("tag", ((TextView)tagView).getText());
                activity.startActivity(intent);
            }
        });
        postEdit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                activity.openContextMenu(arg0);
            }
        });
        share.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                share();
            }
        });
        star.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                BaseActivity.pref.addRemoveStarred(postItem.id);
                activity.drawerAdapter.notifyDataSetChanged();
                renderPostStarred();
            }
        });
        postAttr.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(activity, ListByUser.class);
                intent.putExtra("userId", postItem.userId);
                intent.putExtra("userName", postItem.username);
                activity.startActivity(intent);
            }
        });
    }

    protected void share() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, postItem.getUrl());
        sendIntent.setType("text/plain");
        activity.startActivity(Intent.createChooser(sendIntent, "Share this post to..."));
    }

    protected void registerClickListener() {
        //post.setOnClickListener(new PostItemOnClickListener());
        comment.setOnClickListener(new PostItemOnClickListener());
    }

    public class PostItemOnClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            int postId = Integer.parseInt(id.getText().toString());
            Intent intent = new Intent(activity, ListByParent.class);
            intent.putExtra("postId", postId);
            activity.startActivity(intent);
        }
    }

    public void renderPost(PostObj item) {
        postItem = item;
        try {
            if (item.deleted) {
                renderDeleted();
            }
            else {
                renderActive();
                renderPostTop();
                renderPostMainText(limitTextLength);
                renderPostAttachments();
                renderPostInfo();
                renderPostActions();
            }
        } catch (Exception e) {
            Util.logError(activity, e);
        }
    }

    public void makeOP() {
        LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layout.setMargins(0, 0, 0, 0);
        containerTop.setLayoutParams(layout);
        containerMid.setLayoutParams(layout);
        containerBottom.setLayoutParams(layout);
        containerTop.setBackgroundColor(0xFFFFFFFF);
        containerMid.setBackgroundColor(0xFFFFFFFF);
        containerBottom.setBackgroundColor(0xFFFFFFFF);
        attachmentsPlaceholder.setVisibility(View.GONE);
        tag.setBackgroundResource(R.drawable.list_item_op_tag_bg);
        postAction.setBackgroundResource(R.drawable.list_item_op_action_bg);
        tag.setVisibility(View.VISIBLE);
        comment.setOnClickListener(null);
    }
    
    protected void renderDeleted() {
        postDeletedGroup.setVisibility(View.VISIBLE);
        containerTop.setVisibility(View.GONE);
        containerMid.setVisibility(View.GONE);
        containerBottom.setVisibility(View.GONE);
        postUndoDelete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                activity.undoDelete(postItem);
            }
        });
    }

    protected void renderActive() {
        postDeletedGroup.setVisibility(View.GONE);
        containerTop.setVisibility(View.VISIBLE);
        containerMid.setVisibility(View.VISIBLE);
        containerBottom.setVisibility(View.VISIBLE);
    }

    protected void renderPostTop() {
        if(postItem.tags != null) {
            tag.setText(postItem.tags);
            tag.setVisibility(View.VISIBLE);
        }
        else {
            tag.setText("");
            tag.setVisibility(View.GONE);
        }

        if(BaseActivity.pref.getUser() != null && postItem.userId.equals(BaseActivity.pref.getUser().id)) {
            postEdit.setVisibility(View.VISIBLE);
        }
        else {
            postEdit.setVisibility(View.GONE);
        }

    }

    private void renderPostMainText(boolean limText) {
        if(postItem.getTitle().length() > 0) {
            title.setText(postItem.getTitle());
            title.setVisibility(View.VISIBLE);
        }
        else {
            title.setVisibility(View.GONE);
        }

        if(postItem.getText().length() > 0) {
            String tempText = postItem.getText();
            if(limText && postItem.getText().length() > 120) {
                tempText = tempText.substring(0, 120)+"...";
            }
            text.setText(tempText);
            text.setVisibility(View.VISIBLE);
        }
        else {
            text.setVisibility(View.GONE);
        }
    }

    private void renderPostAttachments() throws MalformedURLException, IOException {
        attachmentsView.removeAllViews();
        attachmentsView.setTag(postItem);
        
        if (postItem.getAttachments().size() == 0) {
            attachmentsPlaceholder.setVisibility(View.GONE);
            return;
        }

        attachmentsPlaceholder.setVisibility(View.VISIBLE);

        for(int i=0; i<postItem.getAttachments().size(); i++) {
            final AttachObj a = postItem.getAttachments().get(i);

            switch(a.type) {
            case IMAGE:
                AttachObj.renderAttachment(activity, postItem, a.getFileUrl(), a, MEDIA_TYPE.IMAGE, attachmentsView, false, new AttachmentInteractListener() {

                    @Override
                    public void onOpen(View v) {
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW); 
                        intent.setDataAndType(Uri.parse(a.getFileUrl()),"image/*"); 
                        activity.startActivity(intent); 
                    }

                    @Override
                    public boolean onDelete(View v) {
                        return false;
                    }

                });
                break;
            case AUDIO:
                AttachObj.renderAttachment(activity, postItem, a.getFileUrl(), a, MEDIA_TYPE.AUDIO, R.drawable.sound, attachmentsView, false, new AttachmentInteractListener() {

                    @Override
                    public void onOpen(View v) {
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW); 
                        intent.setDataAndType(Uri.parse(a.getFileUrl()),"video/*"); 
                        activity.startActivity(intent); 
                    }

                    @Override
                    public boolean onDelete(View v) {
                        return false;
                    }

                });
                break;
            case VIDEO:
                AttachObj.renderAttachment(activity, postItem, a.getFileUrl(), a, MEDIA_TYPE.VIDEO, attachmentsView, false, new AttachmentInteractListener() {

                    @Override
                    public void onOpen(View v) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.parse(a.getFileUrl()), "video/*");
                        activity.startActivity(intent);
                    }

                    @Override
                    public boolean onDelete(View v) {
                        return false;
                    }

                });
                break;
            default:
                break;
            }
        }
    }

    protected void renderPostInfo() {
        id.setText(Integer.toString(postItem.id));
        user.setText(postItem.username);
        time.setText(DateUtil.getAgo(postItem.getModified(activity)));
        
        float[] distance = new float[3];
        Location.distanceBetween(BaseActivity.pref.getViewingLocation().latitude, BaseActivity.pref.getViewingLocation().longitude, postItem.latitude, postItem.longitude, distance);
        //location.setText(postItem.location+" ("+Math.floor(distance[0]/1000)+"km)");
        location.setText(postItem.location);
        if (postItem.tags != null && postItem.tags.length() > 0 ) {
            tag2.setText("to "+postItem.tags);
            tag2.setVisibility(View.VISIBLE);
        }
        else {
            tag2.setVisibility(View.GONE);
        }
    }

    protected void renderPostActions() {
        if (postItem.parentId != null) {
            comment.setVisibility(View.GONE);
        }
        else {
            comment.setText("Comment ("+postItem.comment+")");
            comment.setVisibility(View.VISIBLE);
        }
        renderPostStarred();
    }

    protected void renderPostStarred() {
        if (BaseActivity.pref.isStarred(postItem.id)) {
            star.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_important, 0, 0, 0);
        }
        else {
            star.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_not_important, 0, 0, 0);
        }
    }
}