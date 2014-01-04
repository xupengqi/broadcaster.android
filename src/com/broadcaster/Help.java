package com.broadcaster;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.broadcaster.fragment.HelpFAQ;
import com.broadcaster.fragment.HelpFeedback;
import com.broadcaster.fragment.HelpGettingStarted;
import com.broadcaster.task.TaskFeedback;
import com.broadcaster.task.TaskManager;

public class Help extends BaseDrawerTabActivity {
    protected Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initTabs() {
        getActionBar().addTab(getActionBar().newTab().setText("Getting Started").setTabListener(tabListener));
        getActionBar().addTab(getActionBar().newTab().setText("Feedback").setTabListener(tabListener));
        getActionBar().addTab(getActionBar().newTab().setText("FAQ").setTabListener(tabListener));
    }

    @Override
    protected void initFragments() {
        currentFragments.add(new HelpGettingStarted());
        currentFragments.add(new HelpFeedback());
        currentFragments.add(new HelpFAQ());
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        this.menu = menu;
        menu.findItem(R.id.menu_start).setVisible(true);
        menu.findItem(R.id.menu_help).setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void changePage(int position) {
        if (menu == null) {
            return;
        }

        menu.findItem(R.id.menu_submit).setVisible(false);
        menu.findItem(R.id.menu_start).setVisible(false);
        switch(position) {
        case 0:
            menu.findItem(R.id.menu_start).setVisible(true);
            break;
        case 1:
            menu.findItem(R.id.menu_submit).setVisible(true);
            break;
        case 2:
            break;
        default:
            break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_submit:
            String email = ((HelpFeedback) currentFragments.get(1)).getFeedbackEmail();
            String feedback = ((HelpFeedback) currentFragments.get(1)).getFeedbackText();
            (new TaskManager(Help.this))
            .addTask(new TaskFeedback(email, feedback))
            .run();
            hideKeyboard();
            return true;
        case R.id.menu_start:
            menuHome();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
}
