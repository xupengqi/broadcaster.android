package com.broadcaster.model;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.broadcaster.BaseActivity;
import com.broadcaster.R;
import com.broadcaster.util.Constants;
import com.broadcaster.util.Constants.MEDIA_TYPE;
import com.broadcaster.util.Constants.TASK;
import com.broadcaster.util.Constants.TASK_RESULT;
import com.broadcaster.util.TaskListener;
import com.broadcaster.util.TaskManager;
import com.broadcaster.util.Util;

public class AttachObj implements Serializable {
    private static final long serialVersionUID = 1L;

    public MEDIA_TYPE type;
    public String dir;
    public String id;
    public Bitmap preview;

    public String fileName;

    public AttachObj() {

    }

    public AttachObj(MEDIA_TYPE t, String f) {
        type = t;
        fileName = f;
    }

    public String getThumbURL() throws MalformedURLException {
        return Constants.host+"data/"+dir+"/t/"+id+".jpg";
    }

    public String getFileUrl() {
        String ext = ".dat";
        switch(type) {
        case IMAGE:
            ext = ".jpg";
            break;
        case VIDEO:
            ext = ".mp4";
            break;
        case AUDIO:
            ext = ".3gp";
            break;
        default:
            break;
        }
        return Constants.host+"data/"+dir+"/"+id+ext;
    }

    public static void renderAttachment(final BaseActivity context, final PostObj post, final String file, final AttachObj attachObj, final MEDIA_TYPE type, final View attachmentsView, final boolean create, final AttachmentInteractListener l) throws MalformedURLException, IOException {
        downloadAttachThumb(context, attachObj, new DownloadImgListener () {
            @Override
            public void onDownload(Bitmap bm) {
                renderAttachment(context, post, file, attachObj, type, bm, attachmentsView, create, l);
            }
        });
    }

    public static void renderAttachment(Activity context, PostObj post, String file, AttachObj attachObj, MEDIA_TYPE type, Object thumbnail, View view, boolean create, final AttachmentInteractListener l) {
        PostObj originalPost = (PostObj) view.getTag();
        // this is to solve the issue where an image is rendered to another post because of moving too fast/lag
        if (post != null && !post.id.equals(originalPost.id)) {
            return;
        }

        LayoutInflater mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View attachGroup = mInflater.inflate(R.layout.item_attachment, null);
        ImageView attachImage = (ImageView) attachGroup.findViewById(R.id.attachment_image);
        ImageView attachRemove = (ImageView) attachGroup.findViewById(R.id.attachment_remove);
        LinearLayout attachmentsView = (LinearLayout) view;

        // this is to solve the same issue as above but duplicated thumbs
        if (post != null && attachmentsView.getChildCount() >= post.getAttachments().size()) {
            return;
        }
        setImage(attachImage, thumbnail);

        // set text
        //button.setTextColor(Color.rgb(255,255,255));
        //button.setText("Play");

        // set the image view styles
        LinearLayout.LayoutParams lp;
        if (create) {
            lp = new LinearLayout.LayoutParams(Util.dpToPixel(context, Constants.THUMB_WIDTH_CREATE), Util.dpToPixel(context,  Constants.THUMB_HEIGHT_CREATE));
            attachRemove.setVisibility(View.VISIBLE);
            attachRemove.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    l.onDelete(v);
                }
            });
        }
        else {
            lp = new LinearLayout.LayoutParams(Util.dpToPixel(context, Constants.THUMB_WIDTH_PREVIEW), Util.dpToPixel(context,  Constants.THUMB_HEIGHT_PREVIEW));
        }
        int padding =  Util.dpToPixel(context, 7);
        int leftMargin = 0;
        if (attachmentsView.getChildCount() > 0) {
            leftMargin = Util.dpToPixel(context, 3);
        }
        lp.setMargins(leftMargin, 0, 0, 0);
        attachImage.setLayoutParams(lp);
        attachImage.setContentDescription(file);
        attachImage.setScaleType(ScaleType.CENTER_CROP);
        attachImage.setCropToPadding(true);
        if (type == MEDIA_TYPE.IMAGE || type == MEDIA_TYPE.VIDEO) {
            attachImage.setBackgroundResource(R.drawable.shadow_black);
        }
        else {
            attachImage.setBackgroundResource(R.drawable.border);
            attachImage.setPadding(150, 150, 150, 150);
        }
        //attachmentView.setMaxHeight(Util.dpToPixel(context, 280));
        //attachmentView.setMaxWidth(Util.dpToPixel(context, 280));
        //attachmentView.setAdjustViewBounds(true);

        attachmentsView.addView(attachGroup);

        // set listener
        attachImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                l.onOpen(v);
            }
        });

        attachImage.setOnLongClickListener(new OnLongClickListener () {
            @Override
            public boolean onLongClick(View v) {
                return l.onDelete(v);
            }
        });
    }

    private static void setImage(ImageView attachmentView, Object thumbnail) {
        // set the image
        if (thumbnail instanceof Bitmap) {
            attachmentView.setImageBitmap((Bitmap) thumbnail);
        }
        else if (thumbnail != null) {
            attachmentView.setImageResource((Integer) thumbnail);
        }
    }

    public interface AttachmentInteractListener {
        public void onOpen(View v);
        public boolean onDelete(View v);
    }

    public static void downloadAttachThumb(final BaseActivity context, final AttachObj att, final DownloadImgListener l) {
        TaskManager.getExecuter(context, new TaskListener() {
            @Override
            public void onExecute(TaskItem ti, TaskManager mgr) {
                Bitmap response = null;
                try {
                    String url = att.getFileUrl();
                    if (att.type == MEDIA_TYPE.VIDEO) {
                        url = att.getThumbURL();
                    }
                    URLConnection connection = new URL(url).openConnection();
                    connection.setUseCaches(true);
                    response = BitmapFactory.decodeStream(connection.getInputStream());
                } catch (Exception e) {
                    Util.logError(context, e);
                    if (att.type == MEDIA_TYPE.VIDEO) {
                        response = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_action_video_light);
                    }
                }
                mgr.putResult(TASK_RESULT.ANY, response);
            }

            @Override
            public void onPostExecute(TaskItem ti, TaskManager mgr) {
                Bitmap thumb = (Bitmap)mgr.getResult(TASK_RESULT.ANY);
                l.onDownload(thumb);
            }
        }).addTask(TASK.DOWNLOAD).begin();
    }

    public interface DownloadImgListener {
        public void onDownload(Bitmap bm);
    }
}
