package com.hackathon.na_lock;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.hackathon.na_lock.listeners.DialogActionListener;

public class WarningDialogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{
            getSupportActionBar().hide();
        }catch (Exception e)
        {
            //no action bar
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.app_name)
                .setMessage(R.string.usage_limit_warn_message)
                .setPositiveButton(R.string.neutral_btn_text,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                                finish();
                            }
                        }
                ).setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                finish();
            }
        }).setCancelable(false).create().show();
    }

}
