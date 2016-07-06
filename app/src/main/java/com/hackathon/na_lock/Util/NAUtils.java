package com.hackathon.na_lock.Util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.hackathon.na_lock.Utils;
import com.hackathon.na_lock.databases.NALockDbHelper;
import com.hackathon.na_lock.pojo.App;
import com.hackathon.na_lock.services.ResetForgroundTimeService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by karthik on 27-05-2016.
 */
public class NAUtils {

    private static final String TAG = "NAUtils" ;

    public static List<App> getInstalledApps(Context context) {
        List<String> resPackageNames = NALockDbHelper.getInstance(context).getRestrictedAppNames();
        List<App> res = new ArrayList<App>();
        List<PackageInfo> packs = context.getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packs.size(); i++) {
            PackageInfo p = packs.get(i);


            if (context.getPackageManager().getLaunchIntentForPackage(p.applicationInfo.packageName) != null
                    && (resPackageNames == null || !resPackageNames.contains(p.packageName))
                    && !p.packageName.equals(context.getPackageName())) {

                String appName = p.applicationInfo.loadLabel(context.getPackageManager()).toString();
                String packageName = p.packageName;
                Drawable icon = p.applicationInfo.loadIcon(context.getPackageManager());
                res.add(new App(icon, packageName, appName));

            }
        }
        return res;
    }

    public static String convertToMin(long millisec) {
        if (millisec >= (60 * 60 * 1000)) {
            return "" + millisec / (60 * 60 * 1000) + " hour";
        } else if (millisec >= (60 * 1000)) {
            return "" + millisec / (60 * 1000) + " mins";
        } else if (millisec < (60 * 1000)) {
            return "" + millisec / 1000 + " second";
        }
        return "";
    }



    public static void setAlarmToResetForegroundTime(Context context) {

        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Log.d(TAG, "getCalendarSet().getTimeInMillis() is " + getCalendarSet().getTimeInMillis());
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP,
                getCalendarSet().getTimeInMillis(), 24 * Utils.HOUR_IN_MILLISECONDS, getResetAlarmIntent(context));
    }


    public static  PendingIntent getResetAlarmIntent(Context context)
    {
        Intent intent = new Intent(context, ResetForgroundTimeService.class);
        PendingIntent alarmIntent = PendingIntent.getService(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        return alarmIntent;
    }


    public static Calendar getCalendarSet() {
        Calendar calNow = Calendar.getInstance();
        Calendar calSet = (Calendar) calNow.clone();

        //for production
        calSet.set(Calendar.HOUR_OF_DAY, 0);//00:05AM
        calSet.set(Calendar.MINUTE, 1);
        calSet.set(Calendar.SECOND, 0);
        calSet.set(Calendar.MILLISECOND, 0);

        if (calSet.compareTo(calNow) <= 0) {
            //Today Set time passed, count to tomorrow
            calSet.add(Calendar.DATE, 1);
        }

        return calSet;
    }


    public static long getLastResetTime(Context context)
    {
       return context.getSharedPreferences(Constants.PREF_FILE_NAME,Context.MODE_PRIVATE).
               getLong(Constants.PREF_LAST_RESET,0);
    }
}
