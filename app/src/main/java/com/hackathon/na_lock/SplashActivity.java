package com.hackathon.na_lock;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.hackathon.na_lock.Util.Constants;
import com.hackathon.na_lock.Util.NAUtils;
import com.hackathon.na_lock.pinlock.ConfirmPinHelperActivity;
import com.hackathon.na_lock.pinlock.SetPinHelperActivity;
import com.manusunny.pinlock.PinListener;

public class SplashActivity extends AppCompatActivity {


    // Splash screen timer
    private static int SPLASH_TIME_OUT = 2000;
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
            SPLASH_TIME_OUT = 0;
        }
        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {





                if(NAUtils.isPinSet(mContext))
                {
                    Intent confirmPinIntent = new Intent(SplashActivity.this, ConfirmPinHelperActivity.class);
                    startActivityForResult(confirmPinIntent,CONFIRM_PIN_REQ);
                }
                else
                {
                    Intent setPinIntent = new Intent(SplashActivity.this, SetPinHelperActivity.class);
                    startActivityForResult(setPinIntent,SET_PIN_REQ);
                }


               /* // This method will be executed once the timer is over
                // Start your app main activity
                Intent i = new Intent(SplashActivity.this, AppListHomeActivity.class);
                startActivity(i);

                // close this activity
                finish();*/
            }
        }, SPLASH_TIME_OUT);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SET_PIN_REQ) {
            switch (resultCode) {
                case PinListener.SUCCESS:
                    getSharedPreferences(Constants.PREF_FILE_NAME,MODE_PRIVATE).edit().
                            putBoolean(Constants.PREF_IS_PIN_SET,true).commit();
                    Intent i = new Intent(SplashActivity.this, AppListHomeActivity.class);
                    startActivity(i);
                    break;
                case PinListener.CANCELLED:
                    //finish();
                    break;
                default:
                    //finish();
                    break;
            }
            finish();
        }

        if(requestCode == CONFIRM_PIN_REQ) {
            switch (resultCode) {
                case PinListener.SUCCESS:
                    Intent i = new Intent(SplashActivity.this, AppListHomeActivity.class);
                    startActivity(i);
                    break;
                case PinListener.CANCELLED:
                    //finish();
                    break;
                case PinListener.FORGOT:
                    break;
                default:
                    //finish();
                    break;
            }
            finish();
        }
    }
}
