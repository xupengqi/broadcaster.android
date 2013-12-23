package com.broadcaster.view;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.res.TypedArray;
import android.location.Address;
import android.location.Geocoder;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.broadcaster.BaseActivity;
import com.broadcaster.R;
import com.broadcaster.model.GeocodeResponse;
import com.broadcaster.model.GeocodeResponse.GRAddress;
import com.broadcaster.model.LocationObj;
import com.broadcaster.model.ResponseObj;
import com.broadcaster.model.TaskItem;
import com.broadcaster.task.TaskGetLocation;
import com.broadcaster.util.LocationUtil;
import com.broadcaster.util.TaskListener;
import com.broadcaster.util.TaskManager;
import com.broadcaster.util.TaskUtil;
import com.broadcaster.util.Util;

public class LocationSettings extends LinearLayout {
    private BaseActivity activity;

    private EditText addLocationText;
    private Button addLocationButton;
    private Button currentLocationRefresh;
    private Button currentLocationAdd;
    private RadioButton currentLocationSelect;
    private RadioButton selectedLocation;
    private RadioGroup savedLocationsGroup;
    private RelativeLayout addLocationGroup;
    private LinearLayout radiusGroup;
    private SeekBar radiusBar;
    private TextView radiusText;
    private TextView editLocations;
    public GeocodeResponse gr;
    private List<LocationObj> savedLocations;
    public List<Address> locations;

    private boolean enableAddRemove;
    private boolean enableRadius;
    private boolean enableEdit;

    public LocationSettings(Context context) {
        super(context);
        init(null);
    }

    public LocationSettings(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(attrs);
    }

    public LocationSettings(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        activity = (BaseActivity) this.getContext();

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.LocationSettings);
        enableAddRemove = a.getBoolean(R.styleable.LocationSettings_addRemove, true);
        enableRadius = a.getBoolean(R.styleable.LocationSettings_radius, true);
        enableEdit = a.getBoolean(R.styleable.LocationSettings_edit, false);
        a.recycle();

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.module_locations, this, true);

        currentLocationRefresh = (Button) findViewById(R.id.settings_location_current_refresh);
        currentLocationSelect = (RadioButton) findViewById(R.id.settings_current_location);
        addLocationText = (EditText) findViewById(R.id.settings_location_new_text);
        addLocationButton = (Button) findViewById(R.id.settings_location_new_add);
        currentLocationAdd = (Button) findViewById(R.id.settings_location_current_add);
        radiusBar = (SeekBar) findViewById(R.id.settings_radius_bar);
        radiusText = (TextView) findViewById(R.id.settings_radius_text);
        savedLocationsGroup = (RadioGroup) findViewById(R.id.settings_location_saved);
        addLocationGroup = (RelativeLayout) findViewById(R.id.settings_location_new_group);
        radiusGroup = (LinearLayout) findViewById(R.id.settings_radius_group);
        editLocations = (TextView) findViewById(R.id.settings_location_edit);

        currentLocationAdd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                LocationObj cl = BaseActivity.pref.getRealLocation();
                addLocation(cl.name, cl.latitude, cl.longitude);
            }
        });
        currentLocationSelect.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                setViewingLocation(-1, currentLocationSelect);
            }
        });
        addLocationText.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView arg0, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    getAddress();
                    return true;
                }    
                return false;
            }
        });
        currentLocationRefresh.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //TODO: SHOW ACTIONBAR PROGRESS
                BaseActivity.pref.clearRealLocation();
                getLocation();
            }
        });
        addLocationButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                getAddress();
            }
        });

        radiusText.setText(BaseActivity.pref.getRadiusInKm()+" km");
        radiusBar.setProgress(LocationUtil.radiusToSeekBar(BaseActivity.pref.getRadiusInKm()));
        radiusBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener () {
            @Override
            public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
                radiusText.setText(LocationUtil.seekBarToRadius(arg1)+"Km");
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) { }

            @Override
            public void onStopTrackingTouch(SeekBar arg0) {
                BaseActivity.pref.setRadiusInKm(LocationUtil.seekBarToRadius(radiusBar.getProgress()));
            }
        });

        checkCurrentLocation();

        if (!enableAddRemove) {
            addLocationGroup.setVisibility(View.GONE);
            currentLocationAdd.setVisibility(View.GONE);
        }
        if (!enableRadius) {
            radiusGroup.setVisibility(View.GONE);
        }
        if (enableEdit) {
            editLocations.setVisibility(View.VISIBLE);
            editLocations.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    activity.menuSettings();
                }
            });
        }

        activity.registerForContextMenu(addLocationText);
        getLocation();
    }

    protected void getLocation() {
        (new com.broadcaster.task.TaskManager(activity))
        .addTask(new TaskGetLocation().setCallback(new com.broadcaster.task.TaskBase.TaskListener() {
            @Override
            public void postExecute(com.broadcaster.task.TaskManager tm, ResponseObj response) {
                LocationObj loc = BaseActivity.pref.getRealLocation();
                currentLocationSelect.setText(loc.name);
            }
        }))
        .showProgressAction()
        .run();
    }

    private void checkCurrentLocation() {
        if (BaseActivity.pref.getViewingLocationPosition() < 0) {
            currentLocationSelect.setChecked(true);
            selectedLocation = currentLocationSelect;
        }
    }

    public void renderSavedLocations() {
        checkCurrentLocation();
        savedLocationsGroup.removeAllViews();
        savedLocations = BaseActivity.pref.getLocations();
        for (int i=0; i<savedLocations.size(); i++) {
            renderSavedLocation(i);
        }
    }

    private void setViewingLocation(int i, RadioButton button) {
        BaseActivity.pref.setViewingLocation(i);
        if (selectedLocation != null) {
            selectedLocation.setChecked(false);
        }
        selectedLocation = button;
    }

    private void getAddress() {
        String address = addLocationText.getText().toString();
        if (address.length() > 2) {
            //TODO: SHOW ACTION BAR PROGRESS
            TaskUtil.getAddress(activity, new LocationSettingsListener(), address);
        }
    }

    public void onGetRealLocation() {
        LocationObj loc = BaseActivity.pref.getRealLocation();
        currentLocationSelect.setText(loc.name);
        //TODO: HIDE ACTIONBAR PROGRESS
    }

    public void addLocation(String addressLine, double latitude, double longitude) {
        activity.showToast("Added location: " + addressLine);
        LocationObj newLoc = new LocationObj(addressLine, latitude, longitude);
        savedLocations.add(newLoc);
        BaseActivity.pref.addLocation(newLoc);
        renderSavedLocation(savedLocations.size()-1);
        activity.hideKeyboard();
        addLocationText.setText("");
    }

    private void renderSavedLocation(final int i) {
        LayoutInflater vi = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View locationItemView = vi.inflate(R.layout.item_location, null);
        final RadioButton text = (RadioButton)locationItemView.findViewById(R.id.settings_location_custom_text);
        final Button delete = (Button)locationItemView.findViewById(R.id.settings_location_custom_remove);
        LocationObj location = savedLocations.get(i);
        text.setText(location.name);

        if (BaseActivity.pref.getViewingLocationPosition().equals(i)) {
            text.setChecked(true);
            selectedLocation = text;
        }

        if (!enableAddRemove) {
            delete.setVisibility(View.GONE);
        }

        text.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                setViewingLocation(i, text);
            }
        });
        delete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                BaseActivity.pref.removeLocation(i);
                savedLocationsGroup.removeView(locationItemView);
                int viewingLocationPosition = BaseActivity.pref.getViewingLocationPosition();
                if (viewingLocationPosition > i) {
                    BaseActivity.pref.setViewingLocation(viewingLocationPosition-1);
                }
                if (viewingLocationPosition == i) {
                    currentLocationSelect.setChecked(true);
                    setViewingLocation(-1, currentLocationSelect);
                }
            }
        });
        savedLocationsGroup.addView(locationItemView);
    }

    public class LocationSettingsListener extends TaskListener {
        @Override
        public void onExecute(TaskItem ti, TaskManager mgr) {
            super.onExecute(ti, mgr);

            switch(ti.task) {
            case GET_ADDRESS:
                locations = null;
                gr = null;

                try {
                    Geocoder gcd = new Geocoder(mgr.activity, Locale.getDefault());
                    locations = gcd.getFromLocationName(ti.extra.toString(), 10);
                } catch (IOException e) {
                    Util.logError(mgr.activity, e);
                    Log.i(this.toString(), "---------unable to use geocoder, using http request now---------");
                    gr = BaseActivity.api.sendGeocodeRequest(BaseActivity.api.getGeocodeRequestParams((String) ti.extra));
                }
                break;
            default:
                break;
            }
        }

        @Override
        public void onPostExecute(TaskItem ti, TaskManager mgr) {
            super.onPostExecute(ti, mgr);

            switch(ti.task) {
            case GET_ADDRESS:
                if (locations != null) {
                    if (locations.size() > 1) {
                        activity.openContextMenu(addLocationText);
                    }
                    else if (locations.size() == 1) {
                        Address loc = locations.get(0);
                        addLocation(loc.getAddressLine(0), loc.getLatitude(), loc.getLongitude());
                    }
                    else {
                        activity.showError(this.toString(), "No matching location found.");
                    }
                }
                if (gr != null) {
                    if (gr.results.size() > 1) {
                        activity.openContextMenu(addLocationText);
                    }
                    else if (gr.results.size() == 1) {
                        GRAddress loc = gr.results.get(0);
                        addLocation(loc.formatted_address, loc.getLat(), loc.getLng());
                    }
                    else {
                        activity.showError(this.toString(), "No matching location found.");
                    }
                }
                //TODO: HIDE ACTIONBAR PROGRESS
                break;
            default:
                break;
            }
        }
    }
}
