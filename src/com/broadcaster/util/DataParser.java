package com.broadcaster.util;

import java.util.ArrayList;
import java.util.List;

import com.broadcaster.model.PostObj;
import com.broadcaster.model.ResponseObj;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class DataParser {

    public static List<String> parseTags(ResponseObj response) {
        return (new Gson()).fromJson(response.data.get("tags"), new TypeToken<List<String>>(){}.getType());
    }

    public static ArrayList<PostObj> parsePosts(ResponseObj response) {
        ArrayList<PostObj> posts = (new Gson()).fromJson(response.data.get("posts"), new TypeToken<List<PostObj>>(){}.getType());
        return (posts == null) ? new ArrayList<PostObj>() : posts;
    }

    public static PostObj parsePost(ResponseObj response) {
        return (new Gson()).fromJson(response.data.get("post"), PostObj.class);
    }
}
