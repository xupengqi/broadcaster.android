package com.broadcaster.fragment;

import java.util.List;

import org.apache.http.NameValuePair;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.broadcaster.BaseActivity;
import com.broadcaster.R;
import com.broadcaster.util.AccountTaskListener;
import com.broadcaster.util.TaskUtil;

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
                    List<NameValuePair> params = BaseActivity.api.getRegisterParams(username.getText().toString(), email.getText().toString(), password.getText().toString());
                    TaskUtil.register(parent, new AccountTaskListener(), params);
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
