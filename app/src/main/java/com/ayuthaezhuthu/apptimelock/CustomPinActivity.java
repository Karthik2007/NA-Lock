package com.ayuthaezhuthu.apptimelock;

import android.content.Intent;
import android.graphics.Color;
import android.widget.TextView;

import com.github.omadahealth.typefaceview.TypefaceTextView;
import com.github.orangegangsters.lollipin.lib.managers.AppLockActivity;

/**
 * Created by karthik on 24-08-2016.
 */
public class CustomPinActivity extends AppLockActivity {


    @Override
    public void showForgotDialog() {

    }

    @Override
    public void onPinFailure(int attempts) {

    }

    @Override
    public void onPinSuccess(int attempts) {
        Intent i = new Intent(this, AppListHomeActivity.class);
        startActivity(i);
    }

    @Override
    public int getContentView() {
        return R.layout.activity_pin_custom;
    }

    @Override
    protected void onResume() {

        TypefaceTextView text = (TypefaceTextView)findViewById(R.id.keyboard_button_textview);
        text.setTextColor(Color.WHITE);
        super.onResume();


    }
}
