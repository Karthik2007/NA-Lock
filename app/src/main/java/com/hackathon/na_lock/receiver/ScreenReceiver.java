package com.hackathon.na_lock.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.hackathon.na_lock.Util.NALog;
import com.hackathon.na_lock.services.AppMonitorService;

/**
 * Created by VINAYKUMAR V on 18-06-2016.
 */
public class ScreenReceiver extends BroadcastReceiver {

    private static final String TAG = "ScreenReceiver";
    public static boolean screenOff = false;

    @Override
    public void onReceive(Context context, Intent intent) {

        NALog.d(TAG,"onReceive screen ");
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            screenOff = true;
            Log.v("SCREEN", "SCREEN TURNED OFF on BroadcastReceiver");
        }
        else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
//            screenOff = false;
            Log.v("SCREEN", "SCREEN TURNED ON on BroadcastReceiver");
        }
        else if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
            screenOff = false;
            Log.v("SCREEN", "In Method:  ACTION_USER_PRESENT");
            //  Handle resuming events
        }
    }
}