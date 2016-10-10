package com.ayuthaezhuthu.apptimelock.services;

import android.app.IntentService;
import android.content.Intent;

import com.ayuthaezhuthu.apptimelock.Util.Constants;
import com.ayuthaezhuthu.apptimelock.Util.NALog;
import com.ayuthaezhuthu.apptimelock.Util.NAUtils;
import com.ayuthaezhuthu.apptimelock.databases.NALockDbHelper;

public class ResetForgroundTimeService extends IntentService {

    private static final String TAG = "ResetForgroundTimeService";

    public ResetForgroundTimeService() {
        super("ResetForgroundTimeService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        NALog.d(TAG,"started");

        NAUtils.setAlarmToResetForegroundTime(this);

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
