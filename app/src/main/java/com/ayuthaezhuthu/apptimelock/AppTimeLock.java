package com.ayuthaezhuthu.apptimelock;

import android.app.Application;

import com.github.orangegangsters.lollipin.lib.managers.LockManager;

/**
 * Created by karthik on 24-08-2016.
 */
public class AppTimeLock extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LockManager<CustomPinActivity> lockManager = LockManager.getInstance();
        lockManager.enableAppLock(this, CustomPinActivity.class);
        lockManager.getAppLock().setShouldShowForgot(false);
        lockManager.getAppLock().setTimeout(100);
        lockManager.getAppLock().setLogoId(R.mipmap.app_icon_lock);
    }
}
