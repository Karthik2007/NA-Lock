package com.hackathon.na_lock.pinlock;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.hackathon.na_lock.Util.Constants;
import com.manusunny.pinlock.ConfirmPinActivity;

public class ConfirmPinHelperActivity extends ConfirmPinActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean isPinCorrect(String pin) {
        String currentPin = getSharedPreferences(Constants.PREF_FILE_NAME, MODE_PRIVATE).getString(Constants.PREF_PIN_CODE, "");// get it from SharedPreferences
        if (!pin.isEmpty() && pin.equals(currentPin))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    public void onForgotPin() {

    }
}
