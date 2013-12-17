package com.broadcaster.view;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.broadcaster.BaseActivity;
import com.broadcaster.util.Constants.MEDIA_TYPE;
import com.broadcaster.util.PathUtil;
import com.broadcaster.util.Util;

public class AudioCaptureButton extends Button {
    private Uri currentRecording;
    private MediaRecorder mRecorder = null;
    private OnNewCaptureListener mOnNewCapture = null;
    private Context mContext;

    OnTouchListener onTouch = new OnTouchListener() {

        @Override
        public boolean onTouch(View view, MotionEvent motion) {
            switch(motion.getAction()) {
            case MotionEvent.ACTION_DOWN:
                AudioCaptureButton.this.startRecording();
                return false;
            case MotionEvent.ACTION_UP:
                AudioCaptureButton.this.stopRecording();
                return false;
            default:
                return false;
            }
        }

    };

    public AudioCaptureButton(Context context) {
        super(context);

        setText("Audio");
        setOnTouchListener(onTouch);
        mContext = context;
    }
    public AudioCaptureButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        setText("Audio");
        setOnTouchListener(onTouch);
        mContext = context;
    }
    public AudioCaptureButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setText("Audio");
        setOnTouchListener(onTouch);
        mContext = context;
    }

    public void startRecording() {
        currentRecording = PathUtil.getMediaPath(MEDIA_TYPE.AUDIO);
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        mRecorder.setOutputFile(currentRecording.getPath());
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);

        try {
            mRecorder.prepare();
        } catch (Exception e) {
            Util.logError((BaseActivity) this.getContext(), e);
        }
        mRecorder.start();
    }

    public void stopRecording() {
        try {
            mRecorder.stop();
            
            MediaPlayer mp = MediaPlayer.create(mContext, currentRecording);
            if (mp.getDuration() < 1000) {
                ((BaseActivity)mContext).showToast("Recording too short! Hold down the record button while recording.");
            }
            else if(mOnNewCapture != null) {
                mOnNewCapture.onNewCapture(currentRecording);
            }
        }
        catch (Exception e) {
            Util.logError((BaseActivity) this.getContext(), e);
        }
        finally {
            mRecorder.release();
            mRecorder = null;
        }

    }

    public void setOnNewCaptureListener(OnNewCaptureListener listener) {
        mOnNewCapture = listener;
    }

    public interface OnNewCaptureListener {
        public void onNewCapture(Uri newRecording);
    }
}
