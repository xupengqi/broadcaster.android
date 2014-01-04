package com.broadcaster.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.broadcaster.BaseActivity;
import com.broadcaster.R;
import com.broadcaster.task.TaskAccount;
import com.broadcaster.task.TaskManager;
import com.broadcaster.util.Constants.PROGRESS_TYPE;

public class AccountRegister extends AccountBase {
    //private ListView accounts;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        /*accounts = (ListView) root.findViewById(R.id.login_accounts);
        accounts.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, getAccounts()));

        SimpleAdapter adapter = new SimpleAdapter(getActivity(), getAccounts(),
                android.R.layout.simple_list_item_2,
                new String[] {"title", "subtitle"},
                new int[] {android.R.id.text1,
            android.R.id.text2});
        accounts.setAdapter(adapter);*/

        submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (validate()) {
                    (new TaskManager((BaseActivity)getActivity()))
                    .addTask(new TaskAccount().register(username.getText().toString(), email.getText().toString(), password.getText().toString()))
                    .setProgress(PROGRESS_TYPE.INLINE)
                    .run();
                }
            }
        });

        return root;
    }

    protected boolean validate() {
        if (username.getText().length() == 0) {
            username.setError("Please enter a username");
            return false;
        }

        if (!password.getText().toString().equals(passwordConfirm.getText().toString())) {
            passwordConfirm.setError("Passwords don't match.");
            return false;
        }

        return true;
    }

    @Override
    protected int getViewResource() {
        return R.layout.fragment_account_register;
    }

    /*private List<Map<String, String>> getAccounts() {
        List<Map<String, String>> result = new ArrayList<Map<String, String>>();
        AccountManager mAccountManager = AccountManager.get(getActivity());
        Account[] accounts = mAccountManager.getAccountsByType(null);
        for (int i = 0; i < accounts.length; i++) {
            Map<String, String> datum = new HashMap<String, String>(2);
            datum.put("title", accounts[i].name);
            datum.put("subtitle", accounts[i].type);
            result.add(datum);
        }
        return result;
    }*/
}
