package com.broadcaster;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.broadcaster.fragment.HelpFAQ;
import com.broadcaster.fragment.HelpFeedback;
import com.broadcaster.fragment.HelpGettingStarted;
import com.broadcaster.model.ResponseObj;
import com.broadcaster.model.TaskItem;
import com.broadcaster.util.Constants.TASK_RESULT;
import com.broadcaster.util.TaskListener;
import com.broadcaster.util.TaskManager;
import com.broadcaster.util.TaskUtil;

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
            TaskUtil.feedback(this, new HelpTaskListener(), ((HelpFeedback) currentFragments.get(1)).getFeedbackParams());
            hideKeyboard();
            return true;
        case R.id.menu_start:
            menuHome();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    public class HelpTaskListener extends TaskListener {
        @Override
        public void onExecute(TaskItem ti, TaskManager mgr) {
            ResponseObj response;
            switch(ti.task) {
            case FEEDBACK:
                response = api.feedback(ti.params);
                mgr.putResult(TASK_RESULT.RAW_HTTP_RESPONSE, response);
                break;
            default:
                break;
            }
        }

        @Override
        public void onPostExecute(TaskItem ti, TaskManager mgr) {
            ResponseObj response = mgr.getResultRawHTTPResponse();
            switch(ti.task) {
            case FEEDBACK:
                if(response.hasError()) {
                    showError(this.toString(), response.getError());
                }
                else {
                    showToast("Thanks for your feedback!");
                    finish();
                }
                break;
            default:
                break;
            }
        }
    }
}
