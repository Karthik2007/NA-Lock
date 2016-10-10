package com.ayuthaezhuthu.apptimelock;

import android.content.Intent;

import com.github.orangegangsters.lollipin.lib.managers.AppLockActivity;

/**
 * Created by karthik on 24-08-2016.
 */
public class CustomPinActivity extends AppLockActivity {


    @Override
    public void showForgotDialog() {

    }

    @Override
    public void onPinFailure(int attempts) {

    }

    @Override
    public void onPinSuccess(int attempts) {
        Intent i = new Intent(this, AppListHomeActivity.class);
        startActivity(i);
    }

    @Override
    public int getContentView() {
        return R.layout.activity_pin_custom;
    }
}
