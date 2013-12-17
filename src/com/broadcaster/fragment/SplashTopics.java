package com.broadcaster.fragment;

import java.util.HashSet;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.broadcaster.BaseActivity;
import com.broadcaster.ListByPref;
import com.broadcaster.R;
import com.broadcaster.util.TagsListAdapter;

public class SplashTopics extends BaseFragment {
    protected HashSet<String> selectedTags;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        root = inflater.inflate(R.layout.fragment_splash_topics, container, false);

        ListView tagsList = (ListView) root.findViewById(R.id.tags_list);
        TagsListAdapter arrayAdapter = new TagsListAdapter(this.getActivity(), tagsList, null, BaseActivity.pref.getAllTags().split(","));
        tagsList.setAdapter(arrayAdapter);

        Button next = (Button) root.findViewById(R.id.splash_next_button);
        next.setOnClickListener(new OnClickListener () {
            @Override
            public void onClick(View arg0) {
                startActivity(new Intent(SplashTopics.this.getActivity(), ListByPref.class));
                SplashTopics.this.getActivity().finish();
            }
        });

        // pref is only set when a change is being made, so if user click on next directly,
        // need to make sure next time we remember what user selected
        // by setting the default selected tags to selected tags
        BaseActivity.pref.setSelectedTags(BaseActivity.pref.getSelectedTags());

        return root;
    }

}
