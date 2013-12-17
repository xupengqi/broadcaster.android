package com.broadcaster.model;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.lang3.StringEscapeUtils;

import com.broadcaster.BaseActivity;
import com.broadcaster.util.Constants;
import com.broadcaster.util.Util;

public class PostObj implements Serializable {
    private static final long serialVersionUID = 1L;

    public Integer id;
    public Integer userId;
    public String username;
    public Integer parentId;
    public String title;
    public PostContent content;
    public int visibility;
    public String location;
    public double latitude;
    public double longitude;
    public int infludence;
    public int comment;
    public String created;
    public String modified;
    public String tags;
    public boolean deleted = false;

    public String getUrl() {
        return Constants.host+"/posts?postId="+id;
    }

    public String getText() {
        if (content == null || content.text == null) {
            return "";
        }
        return StringEscapeUtils.unescapeJava(content.text);
    }
    
    public void setText(String text) {
        if (content == null) {
            content = new PostContent();
        }
        content.text = text;
    }

    public List<AttachObj> getAttachments() {
        if (content == null || content.attachments == null) {
            return new ArrayList<AttachObj>();
        }
        return content.attachments;
    }

    public class PostContent implements Serializable {
        private static final long serialVersionUID = 1L;
        
        public String text;
        public List<AttachObj> attachments = new ArrayList<AttachObj>();
    }

    public void setLocation(LocationObj loc) {
        location = loc.name;
        latitude = loc.latitude;
        longitude = loc.longitude;
    }

    public Date getModified(BaseActivity activity) {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        formatter.setTimeZone(TimeZone.getTimeZone(Constants.SERVER_TIMEZONE));
        try {
            return (Date) formatter.parse(modified);
        } catch (ParseException e) {
            Util.logError(activity, e);
        }
        return null;
    }

    public String getTitle() {
        return StringEscapeUtils.unescapeJava(title);
    }
}
