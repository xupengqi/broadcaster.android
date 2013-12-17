package com.broadcaster;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.broadcaster.model.AttachObj;
import com.broadcaster.model.PostObj;
import com.broadcaster.model.TaskItem;
import com.broadcaster.model.AttachObj.AttachmentInteractListener;
import com.broadcaster.util.Constants.MEDIA_TYPE;
import com.broadcaster.util.TaskManager;
import com.broadcaster.util.TaskUtil;
import com.broadcaster.util.Util;

public class PostEdit extends PostNew {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PostObj post = (PostObj)getIntent().getExtras().getSerializable("postObj");
        gallery.setTag(post);

        if(post.tags != null) {
            for (int i=0; i<postTagItems.size(); i++) {
                if (postTagItems.get(i).equals(post.tags)) {
                    postTag.setSelection(i);
                }
            }
            postText.setText(post.getText());
        }
        else {
            postTag.setVisibility(View.GONE);
            postTitle.setText(post.getTitle());
            postText.setVisibility(View.GONE);
        }

        //if (post.content.attachments != null) {
        for (int i = 0; i < post.getAttachments().size(); i++) {
            AttachObj attach = post.getAttachments().get(i);
            try {
                switch(attach.type) {
                case IMAGE:
                    insertImage(post, attach);
                    break;
                case AUDIO:
                    insertAudio(post, attach);
                    break;
                case VIDEO:
                    insertVideo(post, attach);
                    break;
                case DELETE:
                    break;
                }
            } catch (Exception e) {
                Util.logError(this, e);
            }
        }
        
        postId.setText(post.id.toString());
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_submit).setTitle("Save");
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void submit(List<TaskItem> attachmentTasks) {
        TaskUtil.updatePost(PostEdit.this, new NewPostTaskListener(), attachmentTasks);
    }

    private void insertAudio(final PostObj post, final AttachObj attach) throws MalformedURLException, IOException {
        hideAttach();
        
        AttachObj.renderAttachment(this, post, attach.getFileUrl(), attach, MEDIA_TYPE.AUDIO, R.drawable.sound, gallery, true, new AttachmentInteractListener() {
            @Override
            public void onOpen(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(attach.getFileUrl()), "video/*");
                startActivity(intent);
            }

            @Override
            public boolean onDelete(View v) {
                deleteAttachment(v, attach);
                return true;
            }
        });
    }

    private void insertVideo(final PostObj post, final AttachObj attach) throws MalformedURLException, IOException {
        hideAttach();
        
        AttachObj.renderAttachment(this, post, attach.getFileUrl(), attach, MEDIA_TYPE.VIDEO, gallery, true, new AttachmentInteractListener() {
            @Override
            public void onOpen(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(attach.getFileUrl()), "video/*");
                startActivity(intent);
            }

            @Override
            public boolean onDelete(View v) {
                deleteAttachment(v, attach);
                return true;
            }
        });
    }

    private void insertImage(final PostObj post, final AttachObj attach) throws MalformedURLException, IOException {
        hideAttach();
        
        AttachObj.renderAttachment(this, post, attach.getFileUrl(), attach, MEDIA_TYPE.IMAGE, gallery, true, new AttachmentInteractListener() {
            @Override
            public void onOpen(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW); 
                intent.setDataAndType(Uri.parse(attach.getFileUrl()),"image/*"); 
                startActivity(intent); 
            }

            @Override
            public boolean onDelete(View v) {
                deleteAttachment(v, attach);
                return true;
            }
        });
    }

    @Override
    protected void newPostComplete(TaskManager mgr) {
        finish();
    }

    protected void deleteAttachment(View v, AttachObj attach) {
        attach.type = MEDIA_TYPE.DELETE;
        attachments.add(attach);
        removeAttachmentView(v);
    }
}
