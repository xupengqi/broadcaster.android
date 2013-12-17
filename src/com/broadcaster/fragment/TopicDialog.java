package com.broadcaster.fragment;

import com.broadcaster.BaseActivity;
import com.broadcaster.PostNew;
import com.broadcaster.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.widget.EditText;

public class TopicDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Enter your topic:")
               .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       EditText newTopic = (EditText) getDialog().findViewById(R.id.post_new_topic);
                       BaseActivity.pref.addMyTopics(newTopic.getText().toString());
                       ((PostNew)getActivity()).refreshTopics(newTopic.getText().toString());
                   }
               })
               .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       TopicDialog.this.getDialog().cancel();
                       ((PostNew)getActivity()).resetPrevTopic();
                   }
               })
               .setView(inflater.inflate(R.layout.fragment_topic, null));  
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
