package com.hackathon.na_lock.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.hackathon.na_lock.Util.NAUtils;
import com.hackathon.na_lock.services.AppMonitorService;
import com.hackathon.na_lock.services.ResetForgroundTimeService;

public class StartServiceAtBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent != null && intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {


            try {
                Intent i = new Intent(context, AppMonitorService.class);
                context.stopService(intent);
                context.startService(i);
            } catch (Exception e) {
                e.printStackTrace();
            }

            NAUtils.setAlarmToResetForegroundTime(context);

            if((System.currentTimeMillis()- 24*60*60*100 > NAUtils.getLastResetTime(context)))
            {
                Intent resetIntent = new Intent(context, ResetForgroundTimeService.class);
                context.startService(resetIntent);

            }


        }

    }
}
