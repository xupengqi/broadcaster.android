package com.broadcaster.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.net.Uri;
import android.os.Environment;

import com.broadcaster.util.Constants.MEDIA_TYPE;

public class PathUtil {

    public static Uri getMediaPath(MEDIA_TYPE type) {
        File root = Environment.getExternalStorageDirectory();
        File dirApp = new File(root, Constants.APP_NAME+"/"+type);

        if (! dirApp.exists()){
            dirApp.mkdirs();
        }

        String ext = ".dat";
        switch(type) {
        case VIDEO:
            ext = ".mp4";
            break;
        case AUDIO:
            ext = ".3gp";
            break;
        case IMAGE:
            ext = ".jpg";
            break;
        default:
            break;
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        return Uri.fromFile(new File(dirApp.getPath() + File.separator + timeStamp + ext));
    }
}
