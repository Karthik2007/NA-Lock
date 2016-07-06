package com.hackathon.na_lock;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.hackathon.na_lock.services.AppMonitorService;

public class LockerActivity extends AppCompatActivity {

    long time;
    public  void setTimer(long time1){
        this.time=time1;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        /*try{
            getSupportActionBar().hide();
        }catch (Exception e)
        {
            //no action bar
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.app_name)
                .setMessage(R.string.usage_limit_block_message)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                finish();
            }
        }).setCancelable(false).create().show();*/

    }

    @Override
    public void onBackPressed() {
        //do nothing
    }

    @Override
    protected void onPause() {
        super.onPause();
        finishAndRemoveTask();


    }

    @Override
    protected void onStop() {
        super.onStop();
        finishAndRemoveTask();
    }


}
