package com.broadcaster;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Address;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.broadcaster.model.GeocodeResponse.GRAddress;
import com.broadcaster.model.ResponseObj;
import com.broadcaster.model.UserObj;
import com.broadcaster.task.TaskBase.TaskListener;
import com.broadcaster.task.TaskAccount;
import com.broadcaster.task.TaskManager;
import com.broadcaster.task.TaskUpdateAccount;
import com.broadcaster.util.Constants.PROGRESS_TYPE;
import com.broadcaster.view.LocationSettings;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.plus.PlusClient.OnAccessRevokedListener;

public class Settings extends BaseDrawerActivity {
    private LocationSettings locationSetting;
    private LinearLayout account;
    private LinearLayout otherAccounts;
    private Button removeGPlus;
    private Button removeFB;
    private EditText username;
    private EditText email;
    private EditText password;
    private Button emailUpdate;
    private Button usernameUpdate;
    private Button passwordUpdate;
    private ProgressBar emailProgress;
    private ProgressBar usernameProgress;
    private ProgressBar passwordProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        username = (EditText) findViewById(R.id.settings_username);
        otherAccounts = (LinearLayout) findViewById(R.id.settings_other_accounts);
        removeGPlus = (Button) findViewById(R.id.settings_gplus);
        removeFB = (Button) findViewById(R.id.settings_facebook);
        email = (EditText) findViewById(R.id.settings_email);
        password = (EditText) findViewById(R.id.settings_password);
        emailUpdate = (Button) findViewById(R.id.settings_email_update);
        usernameUpdate = (Button) findViewById(R.id.settings_username_update);
        passwordUpdate = (Button) findViewById(R.id.settings_password_update);
        emailProgress = (ProgressBar) findViewById(R.id.settings_email_progress);
        usernameProgress = (ProgressBar) findViewById(R.id.settings_username_progress);
        passwordProgress = (ProgressBar) findViewById(R.id.settings_password_progress);
        account = (LinearLayout) findViewById(R.id.settings_account);
        TextView starredText = (TextView) findViewById(R.id.settings_starred_text);
        TextView topicsText = (TextView) findViewById(R.id.settings_topics_text);
        Button starred = (Button) findViewById(R.id.settings_starred_button);
        Button topics = (Button) findViewById(R.id.settings_topics_button);
        Button myposts = (Button) findViewById(R.id.settings_my_posts_button);
        locationSetting = (LocationSettings) findViewById(R.id.settings_locations);
        CheckBox errorReport = (CheckBox) findViewById(R.id.settings_error_report);

        errorReport.setChecked(pref.sendErrorAllowed());
        starredText.setText("Starred post: "+pref.getStarred().size());
        topicsText.setText(getTagText(pref.getSelectedTags()));

        removeGPlus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mPlusClient.clearDefaultAccount();
                mPlusClient.revokeAccessAndDisconnect(new OnAccessRevokedListener() {
                    @Override
                    public void onAccessRevoked(ConnectionResult status) {
                        // mPlusClient is now disconnected and access has been revoked.
                        // Trigger app logic to comply with the developer policies
                    }
                });
                (new TaskManager(Settings.this))
                .addTask((new TaskAccount()).removeGoogle())
                .run();
            }
        });
        removeFB.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                (new TaskManager(Settings.this))
                .addTask((new TaskAccount()).removeFB())
                .run();
            }
        });
        starred.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                menuStarred();
            }
        });
        topics.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                menuTopics();
            }
        });
        myposts.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                menuMyPosts();
            }
        });
        usernameUpdate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (!username.getText().toString().equals(pref.getUser().username)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
                    builder.setMessage("You can only change username once, are you sure you want to change to: \""+username.getText()+"\"?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            usernameUpdate.setVisibility(View.INVISIBLE);
                            usernameProgress.setVisibility(View.VISIBLE);
                            (new TaskManager(Settings.this))
                            .addTask((new TaskUpdateAccount()).updateUsername(username.getText().toString()).setCallback(new TaskListener() {
                                @Override
                                public void postExecute(TaskManager tm, ResponseObj response) {
                                    if(!response.hasError()) {
                                        showToast("Username successfully updated.");
                                        UserObj user = pref.getUser();
                                        user.username = username.getText().toString();
                                        user.usernameChange++;
                                        pref.setUser(user);
                                        renderChangeUsername();
                                    }
                                    else {
                                        usernameUpdate.setVisibility(View.VISIBLE);
                                    }
                                    usernameProgress.setVisibility(View.GONE);
                                }
                            }))
                            .setProgress(PROGRESS_TYPE.INLINE)
                            .run();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        });
        emailUpdate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                emailUpdate.setVisibility(View.INVISIBLE);
                emailProgress.setVisibility(View.VISIBLE);
                (new TaskManager(Settings.this))
                .addTask((new TaskUpdateAccount()).updateEmail(email.getText().toString()).setCallback(new TaskListener() {
                    @Override
                    public void postExecute(TaskManager tm, ResponseObj response) {
                        emailUpdate.setVisibility(View.VISIBLE);
                        emailProgress.setVisibility(View.GONE);
                        if(!response.hasError()) {
                            showToast("Email successfully updated.");
                            UserObj user = pref.getUser();
                            user.email = email.getText().toString();
                            pref.setUser(user);
                        }
                    }
                }))
                .run();
            }
        });
        passwordUpdate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                passwordUpdate.setVisibility(View.INVISIBLE);
                passwordProgress.setVisibility(View.VISIBLE);
                (new TaskManager(Settings.this))
                .addTask((new TaskUpdateAccount()).updatePassword(password.getText().toString()).setCallback(new TaskListener() {
                    @Override
                    public void postExecute(TaskManager tm, ResponseObj response) {
                        passwordUpdate.setVisibility(View.VISIBLE);
                        passwordProgress.setVisibility(View.GONE);
                        if(!response.hasError()) {
                            showToast("Password successfully updated.");
                        }
                    }
                }))
                .run();
            }
        });
        errorReport.setOnCheckedChangeListener(new OnCheckedChangeListener () {
            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                pref.setErrorAllowed(arg1);
            }
        });

        locationSetting.renderSavedLocations();
        renderChangeUsername();
    }

    private void renderChangeUsername() {
        if (pref.getUser() == null || pref.getUser().usernameChange > 0) {
            usernameUpdate.setVisibility(View.GONE);
            username.setEnabled(false);
        }
        else {
            usernameUpdate.setVisibility(View.VISIBLE);
            username.setEnabled(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isLoggedIn()) {
            account.setVisibility(View.VISIBLE);
            username.setText(pref.getUser().username);
            email.setText(pref.getUser().email);
            if (pref.getUser().usingFb > 0) {
                removeFB.setVisibility(View.VISIBLE);
                otherAccounts.setVisibility(View.VISIBLE);
            }
            if (pref.getUser().usingGp > 0) {
                removeGPlus.setVisibility(View.VISIBLE);
                otherAccounts.setVisibility(View.VISIBLE);
            }
        }
        else {
            account.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_home).setVisible(true);
        menu.findItem(R.id.menu_settings).setVisible(false);
        showLoginMenuItem(menu);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        menu.setHeaderTitle("Choose a location");
        if (locationSetting.locations != null) {
            for(int i=0; i<locationSetting.locations.size(); i++) {
                Address addr = locationSetting.locations.get(i);
                menu.add(0, i, i, addr.getAddressLine(0));
            }
        }
        else {
            for(int i=0; i<locationSetting.addresses.size(); i++) {
                GRAddress addr = locationSetting.addresses.get(i);
                menu.add(0, i, i, addr.formatted_address);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (locationSetting.locations != null) {
            Address loc = locationSetting.locations.get(item.getItemId());
            locationSetting.addLocation(loc.getAddressLine(0), loc.getLatitude(), loc.getLongitude());
        }
        else {
            GRAddress loc = locationSetting.addresses.get(item.getItemId());
            locationSetting.addLocation(loc.formatted_address, loc.getLat(), loc.getLng());
        }
        return true;
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_settings;
    }
}
