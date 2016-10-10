package com.ayuthaezhuthu.apptimelock;

import android.app.AlertDialog;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.ayuthaezhuthu.apptimelock.Util.Constants;
import com.ayuthaezhuthu.apptimelock.Util.NALog;
import com.ayuthaezhuthu.apptimelock.Util.NAUtils;
import com.ayuthaezhuthu.apptimelock.adapter.AppRecyclerAdapter;
import com.ayuthaezhuthu.apptimelock.databases.NALockDbHelper;
import com.ayuthaezhuthu.apptimelock.listeners.DialogActionListener;
import com.ayuthaezhuthu.apptimelock.pojo.App;
import com.ayuthaezhuthu.apptimelock.services.AppMonitorService;

import java.util.List;

public class AppListHomeActivity extends AppCompatActivity implements DialogActionListener {

    private RecyclerView mRestrictedAppRecycler;
    private AppRecyclerAdapter mRestrictedAdapter;
    private FloatingActionButton addButton;
    private List<App> mRestritedAppList;
    private Context mContext;
    private TextView noListMsg;
    private static App appToInsert = null;
    private static ToggleButton toggleBtn;

    private boolean showedDialog = false;

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




        if(NAUtils.getLastResetTime(mContext) == 0) {
            getSharedPreferences(Constants.PREF_FILE_NAME, MODE_PRIVATE).edit().
                    putLong(Constants.PREF_LAST_RESET, System.currentTimeMillis()).commit();
        }

        NAUtils.setAlarmToResetForegroundTime(mContext);

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


        startService(intent);
    }



    @Override
    protected void onResume() {
        super.onResume();

        //loads the list of apps Restricted
        loadList();

        // if permission is not granted ,shows dialog to user to enable it
        if (!Utils.doIHavePermission(mContext)  && !showedDialog){
            //&& !Utils.checkPermission(mContext)
            NALog.d(TAG,"showing dialog");
            showDialog();
        }

    }

    public void loadList() {
        mRestritedAppList = NALockDbHelper.getInstance(mContext).getRestrictedApps();//NALockDbHelper.getInstance(mContext).getRestrictedApps();

        if (mRestritedAppList == null || mRestritedAppList.isEmpty())
            noListMsg.setVisibility(View.VISIBLE);
        else {
            noListMsg.setVisibility(View.INVISIBLE);
            mRestrictedAdapter = new AppRecyclerAdapter(mRestritedAppList, mContext);
            mRestrictedAppRecycler.setAdapter(mRestrictedAdapter);

            mRestrictedAdapter.setOnItemClickListener(mAdapterListener);
        }
    }


    private AppRecyclerAdapter.OnItemClickListener mAdapterListener = new AppRecyclerAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View view, App app) {
            if(app.isRestricted())
                showSetDurationDialog(app,true,null);
            else
                showSetDurationDialog(app,false,view.findViewById(R.id.toggle));
                //onSwitchClick(view.findViewById(R.id.toggle),app);
        }

        @Override
        public void onSwitchClick(View view, App app) {
            if(Utils.checkPermission(mContext)) {
                insertAppsToRestrict(app);
                app.setRestricted(true);
                ((ToggleButton)view).setChecked(true);
            }
            else{
                toggleBtn = (ToggleButton)view;
                appToInsert = app;
                showDialog();
            }
        }
    };
    void showDialog() {
        showedDialog = true;
         mDialogFragment = NADialogFragment.newInstance(
                R.string.msg_usage_permission_dialog);
         mDialogFragment.show(getSupportFragmentManager(), "dialog");
    }

    public void doPositiveClick() {
        if(mDialogFragment != null)
            mDialogFragment.dismiss();
        Intent permissionIntent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        startActivityForResult(permissionIntent,Constants.USAGE_PERMISSION_REQ_CODE);
        Log.i("FragmentAlertDialog", "Positive click!");
    }

    public void doNegativeClick() {

        if(mDialogFragment != null)
            mDialogFragment.dismiss();
        appToInsert = null;
        Log.i("FragmentAlertDialog", "Negative click!");
    }

    public void showSetDurationDialog(final App app, final boolean isRestricted , final View toggleView) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        Log.d(TAG, "position " + app.getAppName());

        View view = ((LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE)).
                inflate(R.layout.duration_custom_dialog,null);
        alertDialog.setView(view);
        //AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();


        alertDialog.setCancelable(true);

        final EditText input = (EditText) view.findViewById(R.id.duration);

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

                            if(isRestricted) {
                                NALockDbHelper.getInstance(mContext).updateAppRestrictionTime(duration *Utils.MIN_IN_MILLSEC, app.getPackageName());
                                loadList();
                            }
                            else {
                                app.setRestrictionTime(duration*Utils.MIN_IN_MILLSEC);
                                mAdapterListener.onSwitchClick(toggleView,app);
                            }


                        }
                    }
                });


        // closed

        // Showing Alert Message
        alertDialog.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Constants.USAGE_PERMISSION_REQ_CODE:
                if (appToInsert != null && Utils.checkPermission(mContext)) {
                    insertAppsToRestrict(appToInsert);
                    toggleBtn.setChecked(true);
                }
                appToInsert = null;
                break;
            default:
                appToInsert = null;
                break;

        }
        Log.d(TAG, "on Activity result" + requestCode);
    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if(mDialogFragment != null)
        {
            mDialogFragment.dismiss();
        }
        super.onSaveInstanceState(outState);
    }


    private void insertAppsToRestrict(App appToInsert) {

        if(appToInsert.getRestrictionTime() == 0)
            appToInsert.setRestrictionTime(30 * Utils.MIN_IN_MILLSEC);
        //NALog.d(TAG,"restriction time ", appToInsert.getRestrictionTime());
        appToInsert.setForegroundTime(0);
        appToInsert.setRestricted(true);
        NALockDbHelper.getInstance(mContext).insertAppForRestriction(appToInsert, mContext);
        loadList();
       // mRestrictedAdapter.notifyDataSetChanged();

    }
}
