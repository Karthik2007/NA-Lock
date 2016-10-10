package com.ayuthaezhuthu.apptimelock.Util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;

import com.ayuthaezhuthu.apptimelock.Utils;
import com.ayuthaezhuthu.apptimelock.databases.NALockDbHelper;
import com.ayuthaezhuthu.apptimelock.pojo.App;
import com.ayuthaezhuthu.apptimelock.services.ResetForgroundTimeService;

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

        StringBuilder time = new StringBuilder();

        if (millisec >= (60 * 60 * 1000)) {

            time.append("" + millisec / (60 * 60 * 1000) + " hour");
            millisec = millisec%(60*60*1000);

        }

        if (millisec >= (60 * 1000)) {
            time.append( " " + millisec / (60 * 1000) + " mins");
        }
        return time.toString();
    }



    public static void setAlarmToResetForegroundTime(Context context) {


        NALog.d(TAG,"setting alarm for reset");
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        NALog.d(TAG, "getCalendarSet().getTimeInMillis() is " + getCalendarSet().getTimeInMillis());
        //alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP,
          //      getCalendarSet().getTimeInMillis(),AlarmManager.INTERVAL_DAY, getResetAlarmIntent(context));
        alarmMgr.setExact(AlarmManager.RTC_WAKEUP,getCalendarSet().getTimeInMillis(),getResetAlarmIntent(context));
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
        calSet.set(Calendar.HOUR_OF_DAY, 0);//00:01AM
        calSet.set(Calendar.MINUTE, 1);
        calSet.set(Calendar.SECOND, 0);
        calSet.set(Calendar.MILLISECOND, 0);

        if (calSet.compareTo(calNow) <= 0) {
        //Today Set time passed, count to tomorrow
        calSet.add(Calendar.DATE, 1);
        }

        return calSet;
        }

/**
 * get the last passed 12 o clock time in millisecs
 * @return millisecs
 */
public static long getLast12CalendarSet() {
        Calendar cal12 = Calendar.getInstance();


        cal12.set(Calendar.HOUR_OF_DAY, 0);//00:01AM
        cal12.set(Calendar.MINUTE, 1);
        cal12.set(Calendar.SECOND, 0);
        cal12.set(Calendar.MILLISECOND, 0);
        return cal12.getTimeInMillis();
        }


public static long getLastResetTime(Context context)
        {
        return context.getSharedPreferences(Constants.PREF_FILE_NAME,Context.MODE_PRIVATE).
        getLong(Constants.PREF_LAST_RESET,0);
        }


public static boolean isPinSet(Context context)
        {
        return context.getSharedPreferences(Constants.PREF_FILE_NAME,Context.MODE_PRIVATE).
        getBoolean(Constants.PREF_IS_PIN_SET,false);
        }
        }
