package com.broadcaster.model;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.broadcaster.R;

public class DrawerItemHolder {
    protected Activity activity;
    public View itemView;
    public TextView text;
    public TextView id;
    public TextView num;
    public ImageView icon;
    
    public DrawerItemHolder(Activity a, int res) {
        activity = a;
        LayoutInflater mInflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        itemView = mInflater.inflate(res, null);
        itemView.setTag(this);

        text = (TextView)itemView.findViewById(R.id.drawer_text);
        id = (TextView)itemView.findViewById(R.id.drawer_item_id);
        num = (TextView)itemView.findViewById(R.id.drawer_text_right);
        icon = (ImageView)itemView.findViewById(R.id.drawer_icon);
    }
}
