package com.ayuthaezhuthu.apptimelock.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ayuthaezhuthu.apptimelock.Util.NALog;
import com.ayuthaezhuthu.apptimelock.Util.NAUtils;
import com.ayuthaezhuthu.apptimelock.services.AppMonitorService;
import com.ayuthaezhuthu.apptimelock.services.ResetForgroundTimeService;

public class StartServiceAtBootReceiver extends BroadcastReceiver {

    private static final String TAG = "StartServiceAtBootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        NALog.d(TAG,"start "+intent);
        if (intent != null && intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {


            try {
                Intent i = new Intent(context, AppMonitorService.class);
                context.stopService(i);
                context.startService(i);
            } catch (Exception e) {
                e.printStackTrace();
            }

            NAUtils.setAlarmToResetForegroundTime(context);

            NALog.d(TAG,"last reset time " + NAUtils.getLastResetTime(context));
            long last12TimeInMillis = NAUtils.getLast12CalendarSet();
            NALog.d(TAG,"last 12 time " + last12TimeInMillis);
            if((last12TimeInMillis > NAUtils.getLastResetTime(context)))
            {
                NALog.d(TAG,"reseting start");
                Intent resetIntent = new Intent(context, ResetForgroundTimeService.class);
                context.startService(resetIntent);

            }


        }

    }
}