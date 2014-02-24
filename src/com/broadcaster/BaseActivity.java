package com.broadcaster;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.broadcaster.task.TaskAccount;
import com.broadcaster.task.TaskManager;
import com.broadcaster.util.Constants;
import com.broadcaster.util.Constants.PROGRESS_TYPE;
import com.broadcaster.util.PrefUtil;
import com.broadcaster.util.RestAPI;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.model.people.Person;

public abstract class BaseActivity extends FragmentActivity implements ConnectionCallbacks, OnConnectionFailedListener  {
    public static RestAPI api;
    public static PrefUtil pref;
    protected boolean isShowingActionProgress = false;

    protected UiLifecycleHelper uiHelper;
    protected static Session session;

    public ProgressDialog mConnectionProgressDialog;
    public PlusClient mPlusClient;
    public ConnectionResult mConnectionResult;
    public static final int REQUEST_CODE_RESOLVE_ERR = 9000;

//    public LocationObj location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (api == null) {
            api = new RestAPI(this);
        }
        if (pref == null) {
            pref = new PrefUtil(this);
        }
        setContentView(getContentView());
        super.onCreate(savedInstanceState);

        uiHelper = new UiLifecycleHelper(this, new Session.StatusCallback() {
            @Override
            public void call(Session s, SessionState state, Exception exception) {
                //Util.whatsMyHash(BaseActivity.this);
                session = s;
                if (state.isOpened() && !isLoggedIn()) {
                    showProgress(PROGRESS_TYPE.OVERLAY);
                    Request.newMeRequest(s, new Request.GraphUserCallback() {
                        @Override
                        public void onCompleted(GraphUser user, Response response) {
                            if (user != null) {
                                (new TaskManager(BaseActivity.this))
                                .addTask((new TaskAccount()).loginFB(user.getId(), user.getUsername(), user.asMap().get("email").toString(), session.getAccessToken()))
                                .setProgress(PROGRESS_TYPE.OVERLAY)
                                .run();
                            }
                        }
                    }).executeAsync();
                } else if (state.isClosed()) {
                    menuLogout();
                }
            }
        });
        uiHelper.onCreate(savedInstanceState);

        mPlusClient = new PlusClient.Builder(this, this, this)
        .setScopes(Scopes.PLUS_LOGIN)
        .setVisibleActivities("http://schemas.google.com/AddActivity", "http://schemas.google.com/BuyActivity")
        .build();
        // Progress bar to be displayed if the connection failure is not resolved.
        mConnectionProgressDialog = new ProgressDialog(this);
        mConnectionProgressDialog.setMessage("Signing in...");

        //overridePendingTransition(R.anim.slide_in_rtl, R.anim.slide_out_rtl);
    }

    @Override
    public void onStart() {
        super.onStart();
        invalidateOptionsMenu(); 
        mPlusClient.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_RESOLVE_ERR && resultCode == RESULT_OK) {
            mConnectionResult = null;
            mPlusClient.connect();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPlusClient.disconnect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //overridePendingTransition(R.anim.slide_in_ltr, R.anim.slide_out_ltr);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(isShowingActionProgress) {
            for (int i = 0; i < menu.size(); i++) {
                MenuItem item = menu.getItem(i);
                item.setVisible(false);
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_login:
            menuLogin();
            return true;
        case R.id.menu_register:
            menuRegister();
            return true;
        case R.id.menu_logout:
            menuLogout();
            return true;
        case R.id.menu_settings:
            menuSettings();
            return true;
        case R.id.menu_new_post:
            menuNewPost();
            return true;
        case R.id.menu_home:
            menuHome();
            return true;
        case R.id.menu_help:
            menuHelp();
            return true;
        default:
            return false;
        }
    }

    public void menuStarred() {
        Map<String, String> extras = new HashMap<String, String>();
        extras.put("postIds", StringUtils.join(pref.getStarred(),","));
        startSingleTopActivity(ListById.class, extras, true);
    }

    public void menuTopics() {
        startSingleTopActivity(Topics.class);
    }

    public void menuSettings() {
        startSingleTopActivity(Settings.class);
    }

    protected void menuHelp() {
        startSingleTopActivity(Help.class);
    }

    protected void menuHome() {
        startSingleTopActivity(ListByPref.class);
    }

    protected void menuNewPost() {
        if(isLoggedIn()) {
            startSingleTopActivity(PostNew.class);
        }
        else {
            menuLogin(Constants.REQUEST_NEWPOST);
        }
    }

    protected void menuLogin() {
        Intent i = new Intent(this, Account.class);
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        i.putExtra(Constants.LOGIN_ACTION, Constants.LOGIN_LOGIN);
        startActivity(i);
    }

    protected void menuLogin(int requestNewpost) {
        Intent i = new Intent(this, Account.class);
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        i.putExtra(Constants.LOGIN_ACTION, Constants.LOGIN_LOGIN);
        i.putExtra(Constants.RETURN_TO, Constants.REQUEST_NEWPOST);
        startActivity(i);
    }

    protected void menuRegister() {
        Intent i = new Intent(this, Account.class);
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        i.putExtra(Constants.LOGIN_ACTION, Constants.LOGIN_REGISTER);
        startActivity(i);
    }

    protected void menuLogout() {
        pref.clearPreference();
        refreshActivity();

        // fb logout
        if (session != null) {
            session.closeAndClearTokenInformation();
        }

        // google+ logout
        if (mPlusClient.isConnected()) {
            mPlusClient.clearDefaultAccount();
            mPlusClient.disconnect();
            mPlusClient.connect();
        }
    }

    public void refreshActivity() {
        finish();
        startActivity(getIntent());
    }

    public void menuMyPosts() {
        Intent intent = new Intent(this, ListByUser.class);
        intent.putExtra("userId", pref.getUser().id);
        intent.putExtra("userName", pref.getUser().username);
        startActivity(intent);
    }

    protected void startSingleTopActivity(Class<?> c, Map<String, String> extras, boolean singleTop) {
        Intent i = new Intent(this, c);
        if (singleTop) {
            i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        }
        for(String key : extras.keySet()) {
            i.putExtra(key, extras.get(key));
        }
        startActivity(i);
    }

    protected void startSingleTopActivity(Class<?> c) {
        startSingleTopActivity(c, new HashMap<String, String>(), true);
    }

    public boolean isLoggedIn() {
        return (pref.getUser() != null);
    }

    public void showError(String source, String error) {
        //Log.e(source, error);
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }

    public void showToast(String toast) {
        Toast.makeText(this, toast, Toast.LENGTH_LONG).show();
    }

    public void hideKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE); 
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS); 
        }
    }

    protected void showLoginMenuItem(Menu menu) {
        if(isLoggedIn()) {
            menu.findItem(R.id.menu_logout).setVisible(true);
        }
        else {
            menu.findItem(R.id.menu_login).setVisible(true);
            menu.findItem(R.id.menu_register).setVisible(true);
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) { //TODO: SET UP DISASSOCATE FOR G+, set up server access
        // We've resolved any connection errors.
        mConnectionProgressDialog.dismiss();
        if(!isLoggedIn()) {
            Person me = mPlusClient.getCurrentPerson();
            (new TaskManager(BaseActivity.this))
            .addTask((new TaskAccount()).loginGoogle(me, mPlusClient.getAccountName()))
            .setProgress(PROGRESS_TYPE.OVERLAY)
            .run();
        }
    }

    @Override
    public void onDisconnected() { }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (mConnectionProgressDialog.isShowing()) {
            // The user clicked the sign-in button already. Start to resolve
            // connection errors. Wait until onConnected() to dismiss the
            // connection dialog.
            if (result.hasResolution()) {
                try {
                    result.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
                } catch (SendIntentException e) {
                    mPlusClient.connect();
                }
            }
        }

        // Save the intent so that we can start an activity when the user clicks
        // the sign-in button.
        mConnectionResult = result;
    }

    public void showProgress(PROGRESS_TYPE type) {
        throw new UnsupportedOperationException();
    }

    public void hideProgress(PROGRESS_TYPE type) {
        throw new UnsupportedOperationException();
    }

    public void setProgressText(String text) {
        throw new UnsupportedOperationException();
    }
    
    public void setProgressImage(Bitmap image) {
        throw new UnsupportedOperationException();
    }

    protected int getContentView() {
        throw new UnsupportedOperationException();
    }
}
