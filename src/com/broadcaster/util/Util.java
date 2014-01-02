package com.broadcaster.util;

import java.security.MessageDigest;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import com.broadcaster.BaseActivity;
import com.broadcaster.model.TaskItem;
import com.broadcaster.util.Constants.TASK;

public class Util {


    public static int getWindowHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.y;
    }

    public static int getWindowWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    // http://developer.android.com/guide/practices/screens_support.html#screen-independence
    public static int dpToPixel(Context c, float dp) {
        // Get the screen's density scale
        final float scale = c.getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return  (int) (dp * scale + 0.5f);
    }

    public static void debug(String msg) { 
        Log.i("***", msg);
    }

    public static void debug(String source, String msg) { 
        Log.i("***"+source, msg);
    }

    public static void debug(Object source, String msg) { 
        Log.i("***"+source.getClass().getSimpleName(), msg);
    }

    public static void logError(BaseActivity context, final Exception e) {
        if (BaseActivity.pref.sendErrorAllowed()) {
            TaskManager.getExecuter(context, new TaskListener() {
                @Override
                public void onExecute(TaskItem ti, TaskManager mgr) {
                    BaseActivity.api.sendError(e);
                }
            }).addTask(TASK.SEND_ERROR).begin();
        }
        else {
            BaseActivity.pref.addError(e);
        }
        e.printStackTrace();
    }
    
    public static void whatsMyHash(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo("com.broadcaster", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String sign=Base64.encodeToString(md.digest(), Base64.DEFAULT);
                Log.e("MY KEY HASH:", sign);
                Toast.makeText(context.getApplicationContext(),sign,     Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager 
        = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
