package com.broadcaster.util;

import java.util.Date;
import java.util.Locale;

public class DateUtil {
	public static String getAgo(Date created) {
        Date now = new Date();
        long diffInSeconds = (now.getTime() - created.getTime()) / 1000;

        long diff[] = new long[] { 0, 0, 0, 0 };
        diff[3] = (diffInSeconds >= 60 ? diffInSeconds % 60 : diffInSeconds);
        diff[2] = (diffInSeconds = (diffInSeconds / 60)) >= 60 ? diffInSeconds % 60 : diffInSeconds;
        diff[1] = (diffInSeconds = (diffInSeconds / 60)) >= 24 ? diffInSeconds % 24 : diffInSeconds;
        diff[0] = (diffInSeconds = (diffInSeconds / 24));

        if(diff[0] > 0) {
            return String.format(Locale.US, "%dd%s ago", diff[0], diff[0] > 1 ? "" : "");
        }
        
        if(diff[1] > 0) {
            return String.format(Locale.US, "%dh%s ago", diff[1], diff[1] > 1 ? "" : "");
        }
        
        if(diff[2] > 0) {
            return String.format(Locale.US, "%dm%s ago", diff[2], diff[2] > 1 ? "" : "");
        }
        
        if(diff[3] > 0) {
            return String.format(Locale.US, "%ds%s ago", diff[3], diff[3] > 1 ? "" : "");
        }
        
        return "1s ago";
    }
}
