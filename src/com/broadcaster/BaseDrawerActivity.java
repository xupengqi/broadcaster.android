package com.broadcaster;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.broadcaster.model.DrawerItemHolder;
import com.broadcaster.util.Constants;
import com.broadcaster.util.Constants.DRAWER_ITEMS;
import com.broadcaster.util.Constants.PROGRESS_TYPE;
import com.broadcaster.view.LocationSettings;

public abstract class BaseDrawerActivity extends BaseActivity {
    protected DrawerLayout mDrawerLayout;
    protected ActionBarDrawerToggle mDrawerToggle;
    protected List<Constants.DRAWER_ITEMS> drawerItems;
    protected TextView drawerUsername;
    protected TextView drawerEmail;

    public ListView drawerList;
    public ArrayAdapter<Constants.DRAWER_ITEMS> drawerAdapter;

    protected RelativeLayout loading;
    protected TextView progressText;
    protected ImageView progressImage;
    protected Button progressCancel;
    protected TextView drawerLogin;
    protected TextView drawerLogout;

    private LocationSettings drawerLocations;
    private String mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerShadow(R.drawable.ic_drawer_shadow, GravityCompat.START);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
                ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                mTitle = getActionBar().getTitle().toString();
                getActionBar().setTitle("Broadcaster");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        drawerList = (ListView) findViewById(R.id.left_drawer_content);
        drawerItems = new ArrayList<Constants.DRAWER_ITEMS>();
        drawerAdapter = new ArrayAdapter<Constants.DRAWER_ITEMS>(this, R.layout.item_drawer, drawerItems) {
            @Override
            public View getView(int pos, View convertView, ViewGroup parent){
                // because when logged in, login item being pushed down, and replaced by settings, style didnt reset
                // find a better way to do this
                if (convertView == null || getItem(pos) == DRAWER_ITEMS.Settings) {
                    convertView = (new DrawerItemHolder(BaseDrawerActivity.this, R.layout.item_drawer)).itemView;
                }

                DrawerItemHolder dih = (DrawerItemHolder)convertView.getTag();
                dih.text.setText(getDrawerItemText(getItem(pos)));
                setDrawerIcon(getItem(pos), dih);
                dih.id.setText(getItem(pos).toString());
                List<Integer> starred = pref.getStarred();
                Constants.DRAWER_ITEMS curItem = getItem(pos);
                if (curItem == Constants.DRAWER_ITEMS.Starred && starred.size() > 0) {
                    dih.num.setText(Integer.toString(starred.size()));
                    dih.num.setVisibility(View.VISIBLE);
                }
                else {
                    dih.num.setVisibility(View.GONE);
                }

                return convertView;
            }
        };

        LayoutInflater mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View drawerFooter = mInflater.inflate(R.layout.module_drawer_footer, null);
        drawerLocations = (LocationSettings) drawerFooter.findViewById(R.id.left_drawer_locations);
        drawerList.addFooterView(drawerFooter);

        drawerList.setAdapter(drawerAdapter);
        drawerList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View itemView, int arg2, long arg3) {
                DrawerItemHolder dih = (DrawerItemHolder)itemView.getTag();
                Constants.DRAWER_ITEMS selectedItem = Constants.DRAWER_ITEMS.valueOf(dih.id.getText().toString());
                processMenuAction(selectedItem);
            }
        });

        drawerUsername = (TextView) findViewById(R.id.left_drawer_username);
        drawerEmail = (TextView) findViewById(R.id.left_drawer_email);
        drawerLogin = (TextView) findViewById(R.id.left_drawer_login);
        drawerLogout = (TextView) findViewById(R.id.left_drawer_logout);
        drawerLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                menuLogin();
            }
        });
        drawerLogout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                menuLogout();
            }
        });
    }

    protected void setDrawerIcon(DRAWER_ITEMS item, DrawerItemHolder dih) {
        switch (item) {
        case NewPost:
            dih.icon.setImageResource(R.drawable.ic_action_new);
            break;
        case Home:
            dih.icon.setImageResource(R.drawable.ic_action_view_as_grid);
            break;
        case MyPosts:
            dih.icon.setImageResource(R.drawable.ic_action_person);
            break;
        case Starred:
            dih.icon.setImageResource(R.drawable.ic_action_important);
            break;
        case Topics:
            dih.icon.setImageResource(R.drawable.ic_action_labels);
            break;
        case Settings:
            dih.icon.setImageResource(R.drawable.ic_action_settings);
            break;
        case Help:
            dih.icon.setImageResource(R.drawable.ic_action_help);
            break;
        }
    }

    @Override
    public void onResume() {
        initDrawerItems(drawerItems);
        drawerAdapter.notifyDataSetChanged();

        if (isLoggedIn()) {
            drawerLogout.setVisibility(View.VISIBLE);
            drawerUsername.setText(pref.getUser().username);
            drawerUsername.setVisibility(View.VISIBLE);
            if (pref.getUser().email.length() > 0) {
                drawerEmail.setText(pref.getUser().email);
                drawerEmail.setVisibility(View.VISIBLE);
            }
        }
        else {
            drawerLogin.setVisibility(View.VISIBLE);
            drawerUsername.setVisibility(View.GONE);
            drawerEmail.setVisibility(View.GONE);
        }

        if (pref.getLocations().size() > 0) {

        }

        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home: // This is called when the Home (Up) button is pressed in the Action Bar.
            mDrawerToggle.onOptionsItemSelected(item);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    protected void refreshPostsFromButton() {}

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    //TODO: REVIEW THIS, FIDN A BETTER WAY
    protected void initProgressElements() {
        loading = (RelativeLayout) findViewById(R.id.loading);
        progressText = (TextView) findViewById(R.id.progressText);
        progressCancel = (Button) findViewById(R.id.progressCancel);
        progressImage = (ImageView) findViewById(R.id.progressImage);

        if (loading == null) {
            showError(this.toString(), "Trying to initialize progress elements but nothing found. Did you include it in the layout file?");
        }

        progressCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                hideProgress(PROGRESS_TYPE.OVERLAY);
            }
        });
    }

    protected CharSequence getDrawerItemText(DRAWER_ITEMS item) {
        switch(item) {
        case MyPosts:
            return "My Posts";
        case NewPost:
            return "New Post";
        default:
            return item.toString();
        }
    }

    protected void initDrawerItems(List<DRAWER_ITEMS> drawerItems) {
        drawerItems.clear();
        drawerItems.add(Constants.DRAWER_ITEMS.NewPost);
        drawerItems.add(Constants.DRAWER_ITEMS.Home);
        drawerItems.add(Constants.DRAWER_ITEMS.Starred);
        if(isLoggedIn()) {
            drawerItems.add(Constants.DRAWER_ITEMS.MyPosts);
        }
        drawerItems.add(Constants.DRAWER_ITEMS.Topics);
        drawerItems.add(Constants.DRAWER_ITEMS.Settings);
        drawerItems.add(Constants.DRAWER_ITEMS.Help);
        drawerLocations.renderSavedLocations();
    }

    protected void processMenuAction(DRAWER_ITEMS selectedItem) {
        mDrawerLayout.closeDrawers();
        switch(selectedItem) {
        case Home:
            menuHome();
            break;
        case Topics:
            menuTopics();
            break;
        case Starred:
            menuStarred();
            break;
        case Settings:
            menuSettings();
            break;
        case MyPosts:
            menuMyPosts();
            break;
        case NewPost:
            menuNewPost();
            break;
        case Help:
            menuHelp();
            break;
        default:
            showError("processMenuAction", "menu item \'"+selectedItem+"\' not recognized.");
            break;
        }
    }

    @Override
    public void invalidateOptionsMenu() {
        super.invalidateOptionsMenu();
    }

    protected String getTagText(String tag) {
        if (tag.equals(Constants.RESERVED_TAG_EVERYTHING)) {
            return "Everything";
        }
        if (tag.equals(Constants.RESERVED_TAG_OWN)) {
            return "My Posts";
        }

        return tag;
    }

    @Override
    public void showProgress(PROGRESS_TYPE type) {
        switch(type) {
        case OVERLAY:
            if (loading != null) {
                loading.setVisibility(View.VISIBLE);
            }
            break;
        default:
            super.showProgress(type);
            break;
        }
    }

    @Override
    public void hideProgress(PROGRESS_TYPE type) {
        switch(type) {
        case OVERLAY:
            if (loading != null) {
                loading.setVisibility(View.GONE);
            }
            break;
        default:
            super.hideProgress(type);
            break;
        }
    }

    @Override
    public void setProgressText(String text) {
        if (progressText != null) {
            progressText.setText(text);
        }
    }

    @Override
    public void setProgressImage(Bitmap image) {
        if (progressImage != null) {
            if (image != null) {
                progressImage.setVisibility(View.VISIBLE);
                progressImage.setImageBitmap(image);
            }
            else {
                progressImage.setVisibility(View.GONE);
            }

        }
    }
}
