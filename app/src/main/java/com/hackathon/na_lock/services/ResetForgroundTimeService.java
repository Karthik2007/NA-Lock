package com.hackathon.na_lock.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import com.hackathon.na_lock.Util.Constants;
import com.hackathon.na_lock.Util.NALog;
import com.hackathon.na_lock.databases.NALockDbHelper;

public class ResetForgroundTimeService extends IntentService {

    private static final String TAG = "ResetForgroundTimeService";

    public ResetForgroundTimeService() {
        super("ResetForgroundTimeService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        NALog.d(TAG,"started");
        if (intent != null) {
            resetForgroundTimeService();
            setLastResetTime();
        }
    }

    private void setLastResetTime() {
        getSharedPreferences(Constants.PREF_FILE_NAME,MODE_PRIVATE).edit().
                putLong(Constants.PREF_LAST_RESET,System.currentTimeMillis()).commit();
    }

    private void resetForgroundTimeService() {
        NALockDbHelper.getInstance(this).resetTable();
    }
}
