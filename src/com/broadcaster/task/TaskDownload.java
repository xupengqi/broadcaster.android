package com.broadcaster.task;

import java.net.URL;
import java.net.URLConnection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.broadcaster.R;
import com.broadcaster.model.AttachObj;
import com.broadcaster.util.Constants.MEDIA_TYPE;
import com.broadcaster.util.Util;

public class TaskDownload extends TaskBase {
    private AttachObj mAttachment;
    private Bitmap mBitmap;
    private TaskDownloadListener mDownloadListener;
    
    public TaskDownload (AttachObj attachment) {
        mAttachment = attachment;
        setProgressText("Downloading...");
    }

    @Override
    protected TaskManager doInBackground(TaskManager... args) {
        try {
            String url = mAttachment.getFileUrl();
            if (mAttachment.type == MEDIA_TYPE.VIDEO) {
                url = mAttachment.getThumbURL();
            }
            URLConnection connection = new URL(url).openConnection();
            connection.setUseCaches(true);
            mBitmap = BitmapFactory.decodeStream(connection.getInputStream());
        } catch (Exception e) {
            Util.logError(args[0].getActivity(), e);
            if (mAttachment.type == MEDIA_TYPE.VIDEO) {
                mBitmap = BitmapFactory.decodeResource(args[0].getActivity().getResources(), R.drawable.ic_action_video_light);
            }
        }

        return super.doInBackground(args);
    }

    @Override
    protected void onPostExecute(TaskManager tm) {
        if (mDownloadListener != null) {
            mDownloadListener.postExecute(mBitmap);
        }

        super.onPostExecute(tm);
    }

    public TaskBase setCallback(TaskDownloadListener listener) {
        mDownloadListener = listener;
        return this;
    }

    public interface TaskDownloadListener {
        void postExecute(Bitmap bitmap);
    }
}
