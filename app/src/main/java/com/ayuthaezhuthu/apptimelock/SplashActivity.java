package com.ayuthaezhuthu.apptimelock;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.ayuthaezhuthu.apptimelock.Util.Constants;
import com.ayuthaezhuthu.apptimelock.Util.NALog;
import com.ayuthaezhuthu.apptimelock.Util.NAUtils;
import com.github.orangegangsters.lollipin.lib.PinActivity;
import com.github.orangegangsters.lollipin.lib.managers.AppLock;
import com.github.orangegangsters.lollipin.lib.managers.LockManager;


public class SplashActivity extends PinActivity {


    // Splash screen timer
    private static int SPLASH_TIME_OUT = 1000;
    private static int SET_PIN_REQ = 245;
    private static int CONFIRM_PIN_REQ = 204;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mContext = this;

        if(NAUtils.isPinSet(mContext))
        {
            startPinCreation();
        }
        else {
            new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer.
             */

                @Override
                public void run() {
                    startPinCreation();
                }
            }, SPLASH_TIME_OUT);
        }
    }


    private void startPinCreation()
    {
        LockManager<CustomPinActivity> lockManager = LockManager.getInstance();
        if(!lockManager.getAppLock().isPasscodeSet())
        {
            Intent intent = new Intent(mContext, CustomPinActivity.class);
            intent.putExtra(AppLock.EXTRA_TYPE, AppLock.ENABLE_PINLOCK);
            startActivityForResult(intent, SET_PIN_REQ);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        NALog.d("splash","req "+ requestCode +" res "+resultCode);
        if(requestCode == SET_PIN_REQ) {
            switch (resultCode) {
                case -1:
                    getSharedPreferences(Constants.PREF_FILE_NAME,MODE_PRIVATE).edit().
                            putBoolean(Constants.PREF_IS_PIN_SET,true).commit();
                    Intent i = new Intent(SplashActivity.this, AppListHomeActivity.class);
                    startActivity(i);
                    break;
                default:
                    //finish();
                    break;
            }
            finish();
        }


    }
}
