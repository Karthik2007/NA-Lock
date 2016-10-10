package com.ayuthaezhuthu.apptimelock;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class LockerActivity extends AppCompatActivity {



    private TextView blockMsgView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        blockMsgView = (TextView) findViewById(R.id.blockMessage);
        String appName = null;
        if(getIntent() != null)
        {
            appName= getIntent().getStringExtra("appName");
        }

        if(appName != null)
        {
            blockMsgView.setText(appName+" " + getString(R.string.usage_limit_block_message));
        }
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
