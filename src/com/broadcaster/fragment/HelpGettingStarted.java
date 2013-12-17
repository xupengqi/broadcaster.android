package com.broadcaster.fragment;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.broadcaster.R;

public class HelpGettingStarted extends HelpBase {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        
        TextView header = (TextView) root.findViewById(R.id.getting_started_header_text);
        header.setText(Html.fromHtml(getString(R.string.getting_started_header)));
        
        TextView news = (TextView) root.findViewById(R.id.getting_started_news);
        news.setText(Html.fromHtml(getString(R.string.getting_started_news)));
        
        TextView report = (TextView) root.findViewById(R.id.getting_started_report);
        report.setText(Html.fromHtml(getString(R.string.getting_started_report)));
        
        TextView trade = (TextView) root.findViewById(R.id.getting_started_trade);
        trade.setText(Html.fromHtml(getString(R.string.getting_started_trade)));
        
        TextView organize = (TextView) root.findViewById(R.id.getting_started_organize);
        organize.setText(Html.fromHtml(getString(R.string.getting_started_organize)));
        
        TextView help = (TextView) root.findViewById(R.id.getting_started_help);
        help.setText(Html.fromHtml(getString(R.string.getting_started_help)));

        TextView footer = (TextView) root.findViewById(R.id.getting_started_footer_text);
        footer.setText(Html.fromHtml(getString(R.string.getting_started_footer)));
        return root;
    }

    @Override
    protected int getViewResource() {
        return R.layout.fragment_help_getting_started;
    }
}
