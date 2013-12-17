package com.broadcaster.util;

import java.util.HashSet;

import org.apache.commons.lang3.StringUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;

import com.broadcaster.BaseActivity;
import com.broadcaster.R;

public class TagsListAdapter extends ArrayAdapter<String> {
    protected static int mResource = R.layout.item_topics_tag;
    protected HashSet<String> selectedTags;
    protected PrefUtil pref;
    protected OnCheckedChangeListener itemChange;
    protected ListView list;
    protected CheckBox header;

    public TagsListAdapter(Context context, ListView l, CheckBox h, String[] allTags) {
        super(context, mResource, allTags);

        pref = new PrefUtil((BaseActivity) context);
        list = l;
        header = h;
        selectedTags = new HashSet<String>();
        String[] sTags = pref.getSelectedTags().split(",");
        for(String tag : sTags) {
            // when selectedTags = "", it will split into an arry of 1 element
            if (tag.length() > 0 && !tag.equals(Constants.RESERVED_TAG_EVERYTHING)) {
                selectedTags.add(tag);
            }
        }

        itemChange = new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean checked) {
                String tag = arg0.getText().toString();
                if(checked) {
                    Util.debug("added tag " + tag);
                    selectedTags.add(tag);
                }
                else {
                    Util.debug("removed tag " + tag);
                    selectedTags.remove(tag);
                }
                
                String[] selected = new String[selectedTags.size()];
                int i=0;
                for(String s : selectedTags) {
                    selected[i]=s;
                    i++;
                }
                if (pref.getUseEverything()) {
                    list.invalidateViews();
                    header.setTextColor(0xFFCCCCCC);
                    header.setChecked(false);
                }
                pref.setSelectedTags(StringUtils.join(selected,","));
            }
        };
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent){
        LayoutInflater vi = (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = vi.inflate(mResource, null);
        CheckBox text = (CheckBox)convertView.findViewById(R.id.tag_text);
        final String tag = getItem(pos);
        text.setText(tag);

        if (selectedTags.contains(tag)) {
            text.setChecked(true);
        }
        if (pref.getUseEverything()) {
            text.setTextColor(0xFFCCCCCC);
        }
        else {
            text.setTextColor(0xFF000000);
        }

        text.setOnCheckedChangeListener(itemChange);
        return convertView;
    }
}