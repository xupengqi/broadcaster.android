package com.broadcaster;

import com.broadcaster.R;

import android.os.Bundle;
import android.view.Menu;

public class NoLocation extends BaseDrawerActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_nolocation);
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_home).setVisible(true);
        return super.onPrepareOptionsMenu(menu);
    }
}
