package com.hackathon.na_lock.pinlock;



import com.hackathon.na_lock.Util.Constants;
import com.manusunny.pinlock.SetPinActivity;

public class SetPinHelperActivity extends SetPinActivity {


    @Override
    public void onPinSet(String pin) {
        getSharedPreferences(Constants.PREF_FILE_NAME,MODE_PRIVATE).edit().
                putString(Constants.PREF_PIN_CODE,pin).commit();
    }
}
