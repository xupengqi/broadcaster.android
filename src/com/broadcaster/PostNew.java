package com.broadcaster;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.broadcaster.fragment.TopicDialog;
import com.broadcaster.model.AttachObj;
import com.broadcaster.model.AttachObj.AttachmentInteractListener;
import com.broadcaster.model.PostObj;
import com.broadcaster.model.ResponseObj;
import com.broadcaster.model.TaskItem;
import com.broadcaster.util.Constants;
import com.broadcaster.util.Constants.MEDIA_TYPE;
import com.broadcaster.util.Constants.TASK;
import com.broadcaster.util.Constants.TASK_RESULT;
import com.broadcaster.util.ImageUtil;
import com.broadcaster.util.PathUtil;
import com.broadcaster.util.TaskListener;
import com.broadcaster.util.TaskManager;
import com.broadcaster.util.TaskUtil;
import com.broadcaster.util.Util;
import com.broadcaster.view.AudioCaptureButton;
import com.broadcaster.view.AudioCaptureButton.OnNewCaptureListener;

public class PostNew extends BaseDrawerActivity {
    private Uri fileUri;
    private EditText locationText; 
    private Button attach;
    private Button attachImage;
    private Button attachVideo;
    private AudioCaptureButton attachAudio;
    private LinearLayout attachGroup;

    protected EditText postTitle;
    protected EditText postText;
    protected Spinner postTag;
    protected TextView postId;
    protected LinearLayout gallery;
    protected ArrayList<AttachObj> attachments;
    protected ArrayAdapter<String> postTagAdapter;
    protected List<String> postTagItems;
    protected Integer prevSelectedTopic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        attachments = new ArrayList<AttachObj>();

        locationText = (EditText)findViewById(R.id.post_new_location);
        attach = (Button)findViewById(R.id.post_new_attach);
        attachImage = (Button)findViewById(R.id.post_new_attach_image);
        attachAudio = (AudioCaptureButton)findViewById(R.id.post_new_attach_audio);
        attachVideo = (Button)findViewById(R.id.post_new_attach_video);
        attachGroup = (LinearLayout)findViewById(R.id.post_new_attach_group);
        gallery = (LinearLayout)findViewById(R.id.post_new_attachments);
        postTitle = (EditText)findViewById(R.id.post_new_title);
        postText = (EditText)findViewById(R.id.post_new_text);
        postTag = (Spinner)findViewById(R.id.post_new_tag);
        postId = (TextView)findViewById(R.id.post_update_id);

        postTagItems = new ArrayList<String>();
        postTagAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, postTagItems);
        postTag.setAdapter(postTagAdapter);
        postTag.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                if (postTagItems.get(arg2).equals("[Custom]")) {
                    showCustsomTopicDialog();
                }
                else {
                    prevSelectedTopic = arg2;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) { }
            
        });
        attach.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (attachGroup.getVisibility() == View.GONE) {
                    attachGroup.setVisibility(View.VISIBLE);
                    attach.setBackground(getResources().getDrawable(R.drawable.ic_action_remove));
                }
                else {
                    hideAttach();
                }
            }
        });
        attachAudio.setOnNewCaptureListener(new OnNewCaptureListener() {
            @Override
            public void onNewCapture(Uri newRecording) {
                insertAudio(newRecording);
            }
        });

        OnClickListener openContext = new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                openContextMenu(arg0);
            }
        };

        refreshTopics("default");
        registerForContextMenu(attachImage);
        registerForContextMenu(attachVideo);
        attachVideo.setTag(Constants.REQUEST_TYPE.VIDEO);
        attachImage.setTag(Constants.REQUEST_TYPE.IMAGE);
        attachImage.setOnClickListener(openContext);
        attachVideo.setOnClickListener(openContext);
        initProgressElements();
        TaskUtil.getRealLocation(this, new NewPostTaskListener());
    }
    
    public void refreshTopics(String selected) {
        postTagItems.clear();
        prevSelectedTopic = 0;
        String[] topics = pref.getAllTags().split(",");
        HashSet<String> topicSet = new HashSet<String>();
        for (String topic : topics) {
            postTagItems.add(topic);
            topicSet.add(topic);
        }    
        List<String> myTopics = pref.getMyTopics();
        for (String topic : myTopics) {
            if (!topicSet.contains(topic)) {
                postTagItems.add(topic);
            }
        }    
        postTagItems.add("[Custom]");
        
        for (int i=0; i<postTagItems.size(); i++) {
            if (postTagItems.get(i).equals(selected)) {
                prevSelectedTopic = i;
                break;
            }
        }

        postTagAdapter.notifyDataSetChanged();
        resetPrevTopic();
    }
    
    public void resetPrevTopic() {
        postTag.setSelection(prevSelectedTopic);
    }

    protected void showCustsomTopicDialog() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        TopicDialog customTopicFragment = new TopicDialog();
        
            // The device is using a large layout, so show the fragment as a dialog
        customTopicFragment.show(fragmentManager, "dialog");
    }

    protected void hideAttach() {
        attachGroup.setVisibility(View.GONE);
        attach.setBackground(getResources().getDrawable(R.drawable.ic_action_new));
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_submit).setVisible(true);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_submit:
            List<TaskItem> attachmentTasks = new ArrayList<TaskItem>();
            for(AttachObj attachment : attachments) {
                MEDIA_TYPE type = attachment.type;
                String file = attachment.fileName;
                String id = attachment.id;
                switch(type) {
                case DELETE:
                    attachmentTasks.add(new TaskItem(TASK.DEL_ATTACHMENT, api.getDelAttachmentParams(pref.getUser(), id), id, type));
                    break;
                default:
                    attachmentTasks.add(new TaskItem(TASK.ADD_ATTACHMENT, api.getAddAttachmentParams(pref.getUser(), type), file, type));
                    break;
                }
            }
            submit(attachmentTasks);
            hideKeyboard();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    protected void submit(List<TaskItem> attachmentTasks) {
        TaskUtil.createPost(PostNew.this, new NewPostTaskListener(), attachmentTasks);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        Object requestTag = v.getTag();
        if (requestTag != null) {
            Constants.REQUEST_TYPE request = Constants.REQUEST_TYPE.valueOf(requestTag.toString());
            switch (request) {
            case VIDEO:
                inflater.inflate(R.menu.video_menu, menu);
                break;
            case IMAGE:
                inflater.inflate(R.menu.image_menu, menu);
                break;
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_pick_image:
            Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, Constants.REQUEST_PICK_IMAGE);
            return true;
        case R.id.menu_take_image:
            if(isIntentAvailable(MediaStore.ACTION_IMAGE_CAPTURE)) {
                fileUri = PathUtil.getMediaPath(MEDIA_TYPE.IMAGE);
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(takePictureIntent, Constants.REQUEST_TAKE_IMAGE);
            }
            else {
                showError(this.toString(), "Camera not available");
            }
            return true;
        case R.id.menu_pick_video:
            Intent mediaChooser = new Intent(Intent.ACTION_GET_CONTENT);
            mediaChooser.setType("video/*"); //mediaChooser.setType("video/*, images/*");
            startActivityForResult(mediaChooser, Constants.REQUEST_PICK_VIDEO);
            return true;
        case R.id.menu_take_video:
            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            fileUri = PathUtil.getMediaPath(MEDIA_TYPE.VIDEO);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the file name
            startActivityForResult(intent, Constants.REQUEST_RECORD_VIDEO);
            return true;
        default:
            return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
        case Constants.REQUEST_PICK_IMAGE:
            if (resultCode == RESULT_OK && data != null) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();
                insertImage(picturePath);
            }
            break;
        case Constants.REQUEST_PICK_VIDEO:
            if (resultCode == RESULT_OK && data != null) {
                Uri dataUri = data.getData();
                String[] filePathColumn = { MediaStore.Video.Media.DATA };
                Cursor cursor = getContentResolver().query(dataUri, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String dataPath = cursor.getString(columnIndex);
                cursor.close();
                insertVideo(dataPath);
            }
            break;
        case Constants.REQUEST_TAKE_IMAGE:
            if (resultCode == RESULT_OK) {
                insertImage(fileUri.getPath());
            }
            break;
        case Constants.REQUEST_RECORD_VIDEO:
            if (resultCode == RESULT_OK) {
                insertVideo(fileUri.getPath());
            }
            break;
        default:
            super.onActivityResult(requestCode, resultCode, data);
            break;
        }
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_post_new;
    }

    @Override
    public void startLoadingMode() {
        super.startLoadingMode();
        getActionBar().hide();
    }

    @Override
    public void stopLoadingMode() {
        super.stopLoadingMode();
        getActionBar().show();
    }

    private void insertAudio(final Uri audioFile) {
        attachments.add(new AttachObj(MEDIA_TYPE.AUDIO, audioFile.getPath()));
        final int attachmentIndex = attachments.size() - 1;
        hideAttach();

        AttachObj.renderAttachment(this, null, audioFile.getPath(), null, MEDIA_TYPE.AUDIO, R.drawable.sound, gallery, true, new AttachmentInteractListener() {
            @Override
            public void onOpen(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(audioFile, "video/*");
                startActivity(intent);
            }

            @Override
            public boolean onDelete(View v) {
                attachments.remove(attachmentIndex);
                removeAttachmentView(v);
                return true;
            }
        });
    }

    private void insertVideo(final String videoFile) {
        attachments.add(new AttachObj(MEDIA_TYPE.VIDEO, videoFile));
        final int attachmentIndex = attachments.size() - 1;
        Bitmap thumnail = ImageUtil.createVideoThumb(videoFile);
        hideAttach();

        AttachObj.renderAttachment(this, null, videoFile, null, MEDIA_TYPE.VIDEO, thumnail, gallery, true, new AttachmentInteractListener() {
            @Override
            public void onOpen(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse("file://" + videoFile), "video/*");
                startActivity(intent);
            }

            @Override
            public boolean onDelete(View v) {
                attachments.remove(attachmentIndex);
                removeAttachmentView(v);
                return true;
            }
        });
    }

    private void insertImage(final String imageFile) {
        attachments.add(new AttachObj(MEDIA_TYPE.IMAGE, imageFile));
        final int attachmentIndex = attachments.size()-1;
        hideAttach();

        Bitmap thumnail = ImageUtil.getThumbnailFromFile(PostNew.this, imageFile, 200);
        AttachObj.renderAttachment(this, null, imageFile, null, MEDIA_TYPE.IMAGE, thumnail, gallery, true, new AttachmentInteractListener() {
            @Override
            public void onOpen(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse("file://" + imageFile), "image/*");
                startActivity(intent);
            }

            @Override
            public boolean onDelete(View v) {
                attachments.remove(attachmentIndex);
                removeAttachmentView(v);
                return true;
            }
        });
    }

    protected boolean isIntentAvailable(String action) {
        final Intent intent = new Intent(action);
        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    protected void newPostComplete(TaskManager mgr) {
        Intent intent = new Intent(PostNew.this, ListByParent.class);
        intent.putExtra("postId", mgr.getResultPostId());
        startActivity(intent);
    }

    protected void setProgressImage(Bitmap image) {
        progressImage.setImageBitmap(image);
    }

    @Override
    public void onGetRealLocation() {
        super.onGetRealLocation();
        locationText.setText(pref.getRealLocation().name);
    }

    public class NewPostTaskListener extends TaskListener {
        @Override
        public void onPreExecute(TaskItem ti, TaskManager mgr) {
            super.onPreExecute(ti, mgr);

            PostObj po;
            switch(ti.task) {
            case ADD_POST:
                po = constructNewPost();
                ti.params = api.getNewPostParams(pref.getUser(), po);
                break;
            case UPDATE_POST:
                po = constructNewPost();
                po.id = Integer.parseInt(postId.getText().toString());
                ti.params = api.getUpdatePostParams(pref.getUser(), po);
                break;
            case ADD_ATTACHMENT:
                String fileName = (String) ti.extra;
                if(fileName != null) {
                    switch(ti.type) {
                    case IMAGE:
                        for(int i=0; i<gallery.getChildCount(); i++) {
                            View iv = (View)gallery.getChildAt(i);
                            if(iv.getContentDescription() == fileName) {
                                iv.buildDrawingCache();
                                setProgressImage(iv.getDrawingCache());
                            }
                        }
                        break;
                    case VIDEO:
                        setProgressImage(ImageUtil.createVideoThumbMini(fileName));
                        break;
                    default:
                        break;
                    }
                }
                break;
            default:
                break;
            }
        }

        @Override
        public void onExecute(TaskItem ti, TaskManager mgr) {
            super.onExecute(ti, mgr);

            ResponseObj response;
            switch(ti.task) {
            case ADD_POST:
                response = api.newPost(ti.params);
                mgr.putResult(TASK_RESULT.RAW_HTTP_RESPONSE, response);
                break;
            case UPDATE_POST:
                response = api.updatePost(ti.params);
                mgr.putResult(TASK_RESULT.RAW_HTTP_RESPONSE, response);
                break;
            case ADD_ATTACHMENT:
                Integer postId = mgr.getResultPostId();
                String filePath = (String)ti.extra;
                File file = new File(filePath); 
                if (ti.type == MEDIA_TYPE.IMAGE) {
                    try {
                        file = ImageUtil.optimizeImage(PostNew.this, file, 75);
                    } catch (IOException e) {
                        Util.logError(PostNew.this, e);
                    }
                }

                ti.params.add(new BasicNameValuePair("data[postId]", Integer.toString(postId)));
                response = api.newAttachment(ti.params, file, ti.type);

                if (ti.type == MEDIA_TYPE.VIDEO) {
                    try {
                        File thumb = ImageUtil.optimizeImage(PostNew.this, ImageUtil.createVideoThumb(filePath), 75);
                        api.newThumb(api.getNewThumbParams(pref.getUser(), postId, response.data.get("attachId").getAsString()), thumb);
                    } catch (IOException e) {
                        Util.logError(PostNew.this, e);
                    }
                }
                mgr.putResult(TASK_RESULT.RAW_HTTP_RESPONSE, response);
                break;
            case DEL_ATTACHMENT:
                ti.params.add(new BasicNameValuePair("data[postId]", Integer.toString(mgr.getResultPostId())));
                response = api.delAttachment(ti.params);
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
                showError("PostNew:postExecuteTask:"+ti.task, response.getError());
                mgr.tasks.clear();
                return;
            }

            switch(ti.task){
            case GET_TAGS:
                //ArrayAdapter<String> tagsAdapter = new ArrayAdapter<String>(PostNew.this, android.R.layout.simple_dropdown_item_1line, pref.getAllTags().split(","));
                //final AutoCompleteTextView tags = (AutoCompleteTextView) findViewById(R.id.post_new_tag);
                //tags.setAdapter(tagsAdapter);
                break;
            case ADD_POST:
            case UPDATE_POST:
                mgr.putResult(TASK_RESULT.POSTID, response.data.get("postId").getAsInt());
                break;
            case FINISH:
                newPostComplete(mgr);
                break;
            default:
                break;
            }
        }
    }

    public PostObj constructNewPost() {
        PostObj po = new PostObj();
        po.title = postTitle.getText().toString();
        po.setText(postText.getText().toString());
        po.visibility = 0;
        po.tags = postTagItems.get(postTag.getSelectedItemPosition());
        po.setLocation(pref.getRealLocation());
        return po;
    }

    protected void removeAttachmentView(View v) {
        ((LinearLayout)v.getParent().getParent()).removeView((LinearLayout)v.getParent());
    }
}
