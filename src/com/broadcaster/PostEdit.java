package com.broadcaster;

import java.io.IOException;
import java.net.MalformedURLException;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.broadcaster.model.AttachObj;
import com.broadcaster.model.AttachObj.AttachmentInteractListener;
import com.broadcaster.model.PostObj;
import com.broadcaster.task.TaskBase;
import com.broadcaster.task.TaskPostUpdate;
import com.broadcaster.util.Constants.MEDIA_TYPE;
import com.broadcaster.util.Util;

public class PostEdit extends PostNew {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPost = (PostObj)getIntent().getExtras().getSerializable("postObj");
        gallery.setTag(mPost);

        if(mPost.tags != null) { // parent post
            for (int i=0; i<postTagItems.size(); i++) {
                if (postTagItems.get(i).equals(mPost.tags)) {
                    postTag.setSelection(i);
                }
            }
            postTitle.setText(mPost.getTitle());
            postText.setText(mPost.getText());
        }
        else { // child post
            postTag.setVisibility(View.GONE);
            postTitle.setText(mPost.getTitle());
            postText.setVisibility(View.GONE);
            attach.setVisibility(View.GONE);
        }

        for (int i = 0; i < mPost.getAttachments().size(); i++) {
            AttachObj attach = mPost.getAttachments().get(i);
            try {
                switch(attach.type) {
                case IMAGE:
                    insertImage(mPost, attach);
                    break;
                case AUDIO:
                    insertAudio(mPost, attach);
                    break;
                case VIDEO:
                    insertVideo(mPost, attach);
                    break;
                case DELETE:
                    break;
                }
            } catch (Exception e) {
                Util.logError(this, e);
            }
        }
        
        postId.setText(mPost.id.toString());
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_submit).setTitle("Save");
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected TaskBase getPostTask() {
        return new TaskPostUpdate(mPost);
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

    protected void deleteAttachment(View v, AttachObj attach) {
        attach.type = MEDIA_TYPE.DELETE;
        attachments.add(attach);
        removeAttachmentView(v);
    }
}
