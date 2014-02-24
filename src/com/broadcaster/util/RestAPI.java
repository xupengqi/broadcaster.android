package com.broadcaster.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.message.BasicNameValuePair;

import android.net.Uri;
import android.net.Uri.Builder;
import android.net.http.AndroidHttpClient;

import com.broadcaster.BaseActivity;
import com.broadcaster.model.AttachObj;
import com.broadcaster.model.GeocodeResponse;
import com.broadcaster.model.LocationObj;
import com.broadcaster.model.PostObj;
import com.broadcaster.model.ResponseObj;
import com.broadcaster.model.ResponseObj.ResponseError;
import com.broadcaster.model.UserObj;
import com.broadcaster.util.Constants.MEDIA_TYPE;
import com.google.gson.Gson;

public class RestAPI {
    private BaseActivity context;
    public String lastResponse = "";
    private Gson gson;

    public RestAPI(BaseActivity c) {
        context = c;
        gson = new Gson();
    }

    public ResponseObj sendError(Exception e) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("data[model]", android.os.Build.MODEL));
        params.add(new BasicNameValuePair("data[manufacture]", android.os.Build.MANUFACTURER));
        params.add(new BasicNameValuePair("data[product]", android.os.Build.PRODUCT));
        params.add(new BasicNameValuePair("data[stacktrace]", ExceptionUtils.getStackTrace(e)));
        return sendRequest(Constants.HTTP_METHOD.POST, "error", "report", params);
    }

    public ResponseObj getPostsByLocation(List<NameValuePair> params) {
        return sendRequest(Constants.HTTP_METHOD.GET, "posts", "byLocation", params);
    }

    public ResponseObj getPostsById(List<NameValuePair> params) {
        return sendRequest(Constants.HTTP_METHOD.GET, "posts", "byId", params);
    }

    public ResponseObj getPostsByUser(List<NameValuePair> params) {
        return sendRequest(Constants.HTTP_METHOD.GET, "posts", "byUser", params);
    }

    public ResponseObj getPostsByParent(List<NameValuePair> params) {
        return sendRequest(Constants.HTTP_METHOD.GET, "posts", "byParent", params);
    }

    public ResponseObj newReply(List<NameValuePair> params) {
        return sendRequest(Constants.HTTP_METHOD.POST, "posts", "reply", params);
    }

    public ResponseObj deletePost(List<NameValuePair> params) {
        return sendRequest(Constants.HTTP_METHOD.DELETE, "posts", "", params);
    }

    public ResponseObj udnoDeletePost(List<NameValuePair> params) {
        return sendRequest(Constants.HTTP_METHOD.POST, "posts", "unDelete", params);
    }

    public ResponseObj newPost(List<NameValuePair> params) {
        return sendRequest(Constants.HTTP_METHOD.POST, "posts", "", params);
    }

    public ResponseObj updatePost(List<NameValuePair> params) {
        return sendRequest(Constants.HTTP_METHOD.PUT, "posts", "update", params);
    }

    public ResponseObj newAttachment(List<NameValuePair> params, File file, Constants.MEDIA_TYPE type) {
        return sendFileRequest("posts", "attach", params, file, type);
    }

    public ResponseObj newThumb(List<NameValuePair> params, File file) {
        return sendFileRequest("posts", "thumb", params, file, MEDIA_TYPE.IMAGE);
    }

    public ResponseObj delAttachment(List<NameValuePair> params) {
        return sendRequest(Constants.HTTP_METHOD.POST, "posts", "deleteAttach", params);
    }

    public ResponseObj login(List<NameValuePair> params) {
        return sendRequest(Constants.HTTP_METHOD.POST, "account", "login", params);
    }

    public ResponseObj loginFB(List<NameValuePair> params) {
        return sendRequest(Constants.HTTP_METHOD.POST, "account", "loginfb", params);
    }

    public ResponseObj loginGPlus(List<NameValuePair> params) {
        return sendRequest(Constants.HTTP_METHOD.POST, "account", "loginGPlus", params);
    }

    public ResponseObj removeGPlus(List<NameValuePair> params) {
        return sendRequest(Constants.HTTP_METHOD.POST, "account", "removeGPlus", params);
    }

    public ResponseObj removeFB(List<NameValuePair> params) {
        return sendRequest(Constants.HTTP_METHOD.POST, "account", "removeFB", params);
    }

    public ResponseObj register(List<NameValuePair> params) {
        return sendRequest(Constants.HTTP_METHOD.POST, "account", "register", params);
    }

    public ResponseObj updateUsername(List<NameValuePair> params) {
        return sendRequest(Constants.HTTP_METHOD.POST, "account", "updateUsername", params);
    }

    public ResponseObj updateEmail(List<NameValuePair> params) {
        return sendRequest(Constants.HTTP_METHOD.POST, "account", "updateEmail", params);
    }

    public ResponseObj updatePassword(List<NameValuePair> params) {
        return sendRequest(Constants.HTTP_METHOD.POST, "account", "updatePassword", params);
    }

    public ResponseObj getTags(List<NameValuePair> params) {
        return sendRequest(Constants.HTTP_METHOD.GET, "tags", "", params);
    }

    public ResponseObj feedback(List<NameValuePair> params) {
        return sendRequest(Constants.HTTP_METHOD.POST, "account", "feedback", params);
    }

    public List<NameValuePair> getPostsByLocationParams(LocationObj location, double radiusInKm, String tag) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("lat", Double.toString(location.latitude)));
        params.add(new BasicNameValuePair("lng", Double.toString(location.longitude)));
        params.add(new BasicNameValuePair("radius", Double.toString(radiusInKm)));
        params.add(new BasicNameValuePair("tag", tag));
        return params;
    }

    public List<NameValuePair> getPostsByParentParams(int parentId) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("parentId", Integer.toString(parentId)));
        return params;
    }

    public List<NameValuePair> getPostByIdParams(String postIds) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("ids", postIds));
        return params;
    }

    public List<NameValuePair> getPostsByUserParams(Integer userId) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("userId", userId.toString()));
        return params;
    }

    public List<NameValuePair> getAfterParams(List<NameValuePair> params, int afterPostId) {
        params.add(new BasicNameValuePair("after", Integer.toString(afterPostId)));
        return params;
    }

    public List<NameValuePair> getTagsParams() {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        return params;
    }

    public List<NameValuePair> getReplyPostParams(UserObj user, PostObj po) {
        List<NameValuePair> params = getCommonPostParams(user, po);
        params.add(new BasicNameValuePair("data[userId]", user.id.toString()));
        params.add(new BasicNameValuePair("data[parentId]", po.parentId.toString()));
        return params;
    }

    public List<NameValuePair> getDeletePostParams(UserObj user, PostObj post) {
        List<NameValuePair> params = getAuthParams(user);
        params.add(new BasicNameValuePair("id", post.id.toString()));
        if(post.parentId != null) {
            params.add(new BasicNameValuePair("parentId", post.parentId.toString()));
        }
        return params;
    }

    public List<NameValuePair> getNewPostParams(UserObj user, PostObj po) {
        List<NameValuePair> params = getCommonPostParams(user, po);
        params.add(new BasicNameValuePair("data[userId]", user.id.toString()));
        return params;
    }

    public List<NameValuePair> getUpdatePostParams(UserObj user, PostObj po) {
        List<NameValuePair> params = getCommonPostParams(user, po);
        params.add(new BasicNameValuePair("id", po.id.toString()));
        return params;
    }

    private List<NameValuePair> getCommonPostParams(UserObj user, PostObj po) {
        List<NameValuePair> params = getAuthParams(user);
        params.add(new BasicNameValuePair("data[title]", StringEscapeUtils.escapeJava(po.getTitle())));
        params.add(new BasicNameValuePair("data[visibility]", Integer.toString(po.visibility)));
        params.add(new BasicNameValuePair("data[location]", po.location));
        params.add(new BasicNameValuePair("data[latitude]", Double.toString(po.latitude)));
        params.add(new BasicNameValuePair("data[longitude]", Double.toString(po.longitude)));
        params.add(new BasicNameValuePair("data[content][text]", StringEscapeUtils.escapeJava(po.getText())));
        if (po.tags != null) {
            params.add(new BasicNameValuePair("tags", po.tags));
        }
        return params;
    }

    //    public List<NameValuePair> getAddAttachmentParams(UserObj user, MEDIA_TYPE type) {
    //        List<NameValuePair> params = getAuthParams(user);
    //        params.add(new BasicNameValuePair("data[content][attachments][0][type]", type.toString()));
    //        return params;
    //    }

    public List<NameValuePair> getNewThumbParams(UserObj user, Integer postId, String attachId) {
        List<NameValuePair> params = getAuthParams(user);
        params.add(new BasicNameValuePair("postId", postId.toString()));
        params.add(new BasicNameValuePair("attachId", attachId));
        return params;
    }

    //    public List<NameValuePair> getDelAttachmentParams(UserObj user, String id) {
    //        List<NameValuePair> params = getAuthParams(user);
    //        params.add(new BasicNameValuePair("data[content][attachments][0][id]", id));
    //        return params;
    //    }

    public List<NameValuePair> getAttachmentParams(UserObj user, AttachObj attachment, Integer postId) {
        List<NameValuePair> params = getAuthParams(user);
        params.add(new BasicNameValuePair("data[postId]", postId.toString()));
        if (attachment.type == MEDIA_TYPE.DELETE) {
            params.add(new BasicNameValuePair("data[content][attachments][0][id]", attachment.id));
        }
        else {
            params.add(new BasicNameValuePair("data[content][attachments][0][type]", attachment.type.toString()));
        }
        return params;
    }

    public List<NameValuePair> getLoginParams(String username, String password) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("username", username));
        params.add(new BasicNameValuePair("password", password));
        return params;
    }

    public List<NameValuePair> getFBLoginParams(String id, String username, String email, String token) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("data[fbId]", id));
        params.add(new BasicNameValuePair("data[username]", username));
        params.add(new BasicNameValuePair("data[email]", email));
        params.add(new BasicNameValuePair("data[token]", token));
        params.add(new BasicNameValuePair("data[usingFb]", "1"));
        return params;
    }

    public List<NameValuePair> getGPlusLoginParams(String id, String username, String email, String token) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("data[gPlusId]", id));
        params.add(new BasicNameValuePair("data[username]", username));
        params.add(new BasicNameValuePair("data[email]", email));
        params.add(new BasicNameValuePair("data[token]", token));
        params.add(new BasicNameValuePair("data[usingGp]", "1"));
        return params;
    }

    public List<NameValuePair> getRegisterParams(String username, String email, String password) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("data[username]", username));
        params.add(new BasicNameValuePair("data[email]", email));
        params.add(new BasicNameValuePair("data[pass]", password));
        return params;
    }

    public List<NameValuePair> getUpdateUsernameParams(UserObj user, String email) {
        List<NameValuePair> params = getAuthParams(user);
        params.add(new BasicNameValuePair("data[username]", email));
        return params;
    }

    public List<NameValuePair> getUpdateEmailParams(UserObj user, String email) {
        List<NameValuePair> params = getAuthParams(user);
        params.add(new BasicNameValuePair("data[email]", email));
        return params;
    }

    public List<NameValuePair> getUpdatePasswordParams(UserObj user, String password) {
        List<NameValuePair> params = getAuthParams(user);
        params.add(new BasicNameValuePair("data[pass]", password));
        return params;
    }

    public List<NameValuePair> getFeedbackParams(String email, String text) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("data[email]", email));
        params.add(new BasicNameValuePair("data[text]", StringEscapeUtils.escapeJava(text)));
        return params;
    }

    public List<NameValuePair> getGeocodeRequestParams(LocationObj loc) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("latlng", loc.latitude+","+loc.longitude));
        params.add(new BasicNameValuePair("sensor", "true"));
        return params;
    }

    public List<NameValuePair> getGeocodeRequestParams(String address) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("address", address));
        params.add(new BasicNameValuePair("sensor", "true"));
        return params;
    }

    public List<NameValuePair> getAuthParams(UserObj user) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("userId", user.id.toString()));
        params.add(new BasicNameValuePair("token", user.token));
        return params;
    }

    private ResponseObj sendRequest(Constants.HTTP_METHOD method, String model, String action, List<NameValuePair> params) {
        ResponseObj response = null;
        
        if (!Util.isNetworkAvailable(context)) {
            response = new ResponseObj(ResponseError.createNoConnectionError());
            return response;
        }
        
        AndroidHttpClient hc = AndroidHttpClient.newInstance(Constants.APP_NAME);
        HttpRequestBase hq;
        try {

            switch(method) {
            case PUT:
            case POST:
                hq = new HttpPost(Constants.host+model+"/"+action);
                ((HttpPost)hq).setEntity(new UrlEncodedFormEntity(params));
                break;
            case DELETE:
                hq = new HttpDelete(getUrl(model, action, params));
                break;
            default:
                hq = new HttpGet(getUrl(model, action, params));
                break;
            }

            ResponseHandler<String> responseHandler=new BasicResponseHandler();
            //Util.debug("HTTP "+method+": "+hq.getURI().toString());
            lastResponse = hc.execute(hq, responseHandler);
            response = gson.fromJson(lastResponse, ResponseObj.class);
        }
        catch (Exception e) {
            response = new ResponseObj(ResponseError.createNoConnectionError());
            //Util.debug("lastResponse", lastResponse);
            e.printStackTrace();
        }
        finally {
            hc.close();
        }

        return response;
    }

    private String getUrl(String model, String action, List<NameValuePair> params) {
        Builder uriBuilder = new Uri.Builder().scheme("http").authority(Constants.authority).path(Constants.appRoot+model+"/"+action);
        for (NameValuePair p : params) {
            uriBuilder.appendQueryParameter(p.getName(), p.getValue());
        }
        return uriBuilder.build().toString();
    }

    private ResponseObj sendFileRequest(String model, String action, List<NameValuePair> params, File file, Constants.MEDIA_TYPE type) {
        ResponseObj response = null;
        
        if (!Util.isNetworkAvailable(context)) {
            response = new ResponseObj(ResponseError.createNoConnectionError());
            return response;
        }
        
        FileEntity fileentity; 
        switch(type) {
        case IMAGE:
            fileentity = new FileEntity(file,"image/*"); 
            break;
        default:
            fileentity = new FileEntity(file,"video/*"); 
            break;
        }
        fileentity.setChunked(true); 
        AndroidHttpClient hc = AndroidHttpClient.newInstance(Constants.APP_NAME);
        try {
            Builder uriBuilder = new Uri.Builder().scheme("http").authority(Constants.authority).path(Constants.appRoot+model+"/"+action);
            for (NameValuePair p : params) {
                uriBuilder.appendQueryParameter(p.getName(), p.getValue());
            }
            HttpPut  hq = new HttpPut (uriBuilder.build().toString());
            hq.setEntity(fileentity);

            ResponseHandler<String> responseHandler=new BasicResponseHandler();
            //Log.i(this.toString(), hq.getURI().toString());
            lastResponse = hc.execute(hq, responseHandler);
            response = gson.fromJson(lastResponse, ResponseObj.class);
        }
        catch (Exception e) {
            response = new ResponseObj(ResponseError.createNoConnectionError());
            Util.logError(context, e);
            //Log.i("lastResponse", lastResponse);
        }
        finally {
            hc.close();
        }

        return response;
    }

    public GeocodeResponse sendGeocodeRequest(List<NameValuePair> params) {
        if (!Util.isNetworkAvailable(context)) {
            return null;
        }
        GeocodeResponse response = null;
        AndroidHttpClient hc = AndroidHttpClient.newInstance(Constants.APP_NAME);
        HttpRequestBase hq;
        try {
            Builder uriBuilder = new Uri.Builder().scheme("http").authority("maps.google.com").path("maps/api/geocode/json");
            for (NameValuePair p : params) {
                uriBuilder.appendQueryParameter(p.getName(), p.getValue());
            }
            hq = new HttpGet(uriBuilder.build().toString());
            ResponseHandler<String> responseHandler=new BasicResponseHandler();
            //Log.i("HTTP REQUEST", hq.getURI().toString());
            lastResponse = hc.execute(hq, responseHandler);
            response = gson.fromJson(lastResponse, GeocodeResponse.class);
        }
        catch (Exception e) {
            Util.logError(context, e);
            //Log.i("lastResponse", lastResponse);
        }
        finally {
            hc.close();
        }

        return response;
    }
}
