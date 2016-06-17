package com.hackathon.na_lock;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.hackathon.na_lock.adapter.AppRecyclerAdapter;
import com.hackathon.na_lock.databases.NALockDbHelper;
import com.hackathon.na_lock.listeners.DialogActionListener;
import com.hackathon.na_lock.pojo.App;
import com.hackathon.na_lock.services.AppMonitorService;
import com.hackathon.na_lock.services.ResetForgroundTimeService;

import java.util.Calendar;
import java.util.List;

public class AppListHomeActivity extends AppCompatActivity implements DialogActionListener {


    private RecyclerView mRestrictedAppRecycler;
    private AppRecyclerAdapter mRestrictedAdapter;
    private FloatingActionButton addButton;
    private List<App> mRestritedAppList;
    private Context mContext;
    private TextView noListMsg;

    DialogFragment mDialogFragment;

    private static final String TAG = "AppListHomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list_home);

        mContext = this;

        Intent intent = new Intent(this, AppMonitorService.class);
        try {
            stopService(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        startService(intent);

        setAlarmToResetForegroundTime(mContext);

        mRestrictedAppRecycler = (RecyclerView) findViewById(R.id.restricted_app_list_recyclerView);
        addButton = (FloatingActionButton) findViewById(R.id.fab_add);
        noListMsg = (TextView) findViewById(R.id.no_list_message);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addIntent = new Intent(mContext, AddAppActivity.class);
                startActivity(addIntent);
            }
        });


        // mRestritedAppList = new NALockDbHelper(mContext).getRestrictedApps();
        //mRestrictedAdapter = new AppRecyclerAdapter(mRestritedAppList,mContext);
        RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(getApplicationContext());
        mRestrictedAppRecycler.setLayoutManager(mLayoutManager1);
        mRestrictedAppRecycler.setItemAnimator(new DefaultItemAnimator());
        //mRestrictedAppRecycler.setAdapter(mRestrictedAdapter);

        if (!Utils.checkPermission(mContext))
            showDialog();

    }



    @Override
    protected void onResume() {
        super.onResume();

        loadList();



        Log.d(TAG, "on Resume ");


    }

    public void loadList() {
        mRestritedAppList = NALockDbHelper.getInstance(mContext).getRestrictedApps();//NALockDbHelper.getInstance(mContext).getRestrictedApps();

        if (mRestritedAppList != null)
            noListMsg.setVisibility(View.INVISIBLE);
        else
            noListMsg.setVisibility(View.VISIBLE);
        mRestrictedAdapter = new AppRecyclerAdapter(mRestritedAppList, mContext);
        mRestrictedAppRecycler.setAdapter(mRestrictedAdapter);

        mRestrictedAdapter.setOnItemClickListener(new AppRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, App app) {
                showSetDurationDialog(app);
            }
        });
    }

    void showDialog() {
         mDialogFragment = NADialogFragment.newInstance(
                R.string.msg_usage_permission_dialog);
         mDialogFragment.show(getSupportFragmentManager(), "dialog");
    }

    public void doPositiveClick() {

        mDialogFragment.dismiss();
        startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
        Log.i("FragmentAlertDialog", "Positive click!");
    }

    public void doNegativeClick() {

        mDialogFragment.dismiss();
        Log.i("FragmentAlertDialog", "Negative click!");
    }

    public void showSetDurationDialog(final App app) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        Log.d(TAG, "position " + app.getAppName());

        //AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();

        // Setting Dialog Title
        alertDialog.setTitle("Duration");

        // Setting Dialog Message
        alertDialog.setMessage("Enter Time in minutes");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        alertDialog.setView(input);

        // Setting Icon to Dialog
        //alertDialog.setIcon(R.drawable.key);

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("Set",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog

                        if (input.getText().toString().length() > 0) {
                            long duration = Long.parseLong(input.getText().toString());
                            Log.d(TAG, "duration " + duration);

                            NALockDbHelper.getInstance(mContext).updateAppRestriction(duration * 60 * 1000,app.getPackageName());

                            loadList();
                        }
                    }
                });


        // closed

        // Showing Alert Message
        alertDialog.show();
    }

    public void setAlarmToResetForegroundTime(Context context) {

        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, ResetForgroundTimeService.class);
        PendingIntent alarmIntent = PendingIntent.getService(context, 1, intent, 0);
        Log.d(TAG, "getCalendarSet().getTimeInMillis() is " + getCalendarSet().getTimeInMillis());
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, getCalendarSet().getTimeInMillis(), 24 * Utils.HOUR_IN_MILLISECONDS, alarmIntent);
    }

    private Calendar getCalendarSet() {
        Calendar calNow = Calendar.getInstance();
        Calendar calSet = (Calendar) calNow.clone();

        //for production
        calSet.set(Calendar.HOUR_OF_DAY, 0);//00:05AM
        calSet.set(Calendar.MINUTE, 1);
        calSet.set(Calendar.SECOND, 0);
        calSet.set(Calendar.MILLISECOND, 0);

        if (calSet.compareTo(calNow) <= 0) {
            //Today Set time passed, count to tomorrow
            calSet.add(Calendar.DATE, 1);
        }

        return calSet;
    }

}
