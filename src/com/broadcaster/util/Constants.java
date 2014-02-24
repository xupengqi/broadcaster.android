package com.broadcaster.util;

public class Constants {
    public static final String APP_NAME = "Broadcaster";
    public static final int POST_PAGE_SIZE = 10;

//    public static final String authority = "24.6.160.109"; // home
    //public static final String authority = "54.225.66.76"; // amzn
    public static final String authority = "thebroadcaster.me"; // amzn
    //public static final String appRoot = "p1/";
    public static final String appRoot = "broadcaster/";
    public static final String host = "http://"+authority+"/"+appRoot;

    public static enum HTTP_METHOD {GET, POST, PUT, DELETE};

    public static enum MEDIA_TYPE {IMAGE, AUDIO, VIDEO, DELETE};
    
    public static enum PROGRESS_TYPE {OVERLAY, ACTION, INLINE};
    
    public static enum ERROR_CODE {NO_CONNECTION, NO_LOCATION, USERNAME_EXISTS, AUTHENTICATION_FAILED, REQUIRE_LOGIN, INTERNAL_ERROR};
    
    public static enum POST_LIST_TYPE {PREF, TOPIC, USER, STAR, PARENT}

    public static final int REQUEST_MEDIA = 1;
    public static final int REQUEST_PICK_IMAGE = 2;
    public static final int REQUEST_TAKE_IMAGE = 3;
    public static final int REQUEST_PICK_VIDEO = 4;
    public static final int REQUEST_RECORD_VIDEO = 5;
    public static final int REQUEST_NEWPOST = 6;

    public static enum REQUEST_TYPE { IMAGE, VIDEO };

    public static final String RETURN_TO = "returnTo";

    public static final String SERVER_TIMEZONE = "UTC";
    //public static final String SERVER_TIMEZONE = "America/Los_Angeles";

    public static enum DRAWER_ITEMS {NewPost, Home, MyPosts, Starred, Topics, Settings, Help};

    public static enum SYSTEM_TOPICS {Homepage, Starred, MyPosts};

    public static final String RESERVED_TAG_OWN = "[[RESERVED_TAG_OWN]]";
    public static final String RESERVED_TAG_EVERYTHING = "[[RESERVED_TAG_EVERYTHING]]";

    public static int TagKey_Post = -1;

    public static int IMAGE_MAX_SIZE = 1600;

    public static int THUMB_WIDTH_PREVIEW = 310;
    public static int THUMB_HEIGHT_PREVIEW = 280;
    public static int THUMB_WIDTH_CREATE = 150;
    public static int THUMB_HEIGHT_CREATE = 150;

    public static String LOGIN_ACTION = "login_action";
    public static int LOGIN_REGISTER = 2;
    public static int LOGIN_LOGIN = 1;
    public static int LOGIN_RESET = 0;

    public static String LOCATION_UNKNOWN = "Unknown";

    public static final int RADIUS_MIN_KM = 10;
    public static final int RADIUS_STEP = 10;
    
    public static final int SQL_ERROR_DUPLICATE = 1062;
}
