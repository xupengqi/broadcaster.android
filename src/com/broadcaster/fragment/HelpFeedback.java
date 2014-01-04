package com.broadcaster.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.broadcaster.R;

public class HelpFeedback extends HelpBase {
    protected EditText text;
    protected EditText email;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        email = (EditText) root.findViewById(R.id.feedback_email);
        text = (EditText) root.findViewById(R.id.feedback_text);
        
        return root;
    }

    @Override
    protected int getViewResource() {
        return R.layout.fragment_help_feedback;
    }
    
    public String getFeedbackText() {
        return text.getText().toString();
    }
    
    public String getFeedbackEmail() {
        return email.getText().toString();
    }
}
