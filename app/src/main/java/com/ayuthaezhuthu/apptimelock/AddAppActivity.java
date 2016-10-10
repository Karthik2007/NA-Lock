package com.ayuthaezhuthu.apptimelock;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ToggleButton;

import com.ayuthaezhuthu.apptimelock.Util.Constants;
import com.ayuthaezhuthu.apptimelock.Util.NAUtils;
import com.ayuthaezhuthu.apptimelock.adapter.AppRecyclerAdapter;
import com.ayuthaezhuthu.apptimelock.databases.NALockDbHelper;
import com.ayuthaezhuthu.apptimelock.listeners.DialogActionListener;
import com.ayuthaezhuthu.apptimelock.pojo.App;

import java.util.ArrayList;
import java.util.List;

public class AddAppActivity extends AppCompatActivity implements DialogActionListener {

    private static final String TAG = "AddAppActivity";
    private RecyclerView mAppRecyclerView;
    private AppRecyclerAdapter mAdapter;
    private List<App> mAppList, mAppListOfRestrictedApps;
    private Context mContext;
    private static App appToInsert;
    DialogFragment mDialogFragment;
    private static ToggleButton toggleBtn;
    private MenuItem doneItem = null;
    public static int selectCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_app);
        mContext = this;

        selectCount = 0;
        mAppRecyclerView = (RecyclerView) findViewById(R.id.app_list_recyclerView);





        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mAppRecyclerView.setLayoutManager(mLayoutManager);
        mAppRecyclerView.setItemAnimator(new DefaultItemAnimator());

    }


    @Override
    protected void onStart() {
        super.onStart();

        mAppList = NAUtils.getInstalledApps(mContext);


        Log.d(TAG, mAppList.size() + "applist " + mAppList.get(0).getAppName());
        mAdapter = new AppRecyclerAdapter(mAppList, mContext);

        mAdapter.setOnItemClickListener(new AppRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, App app) {
                ToggleButton switchBtn = (ToggleButton)view.findViewById(R.id.toggle);
                if(!switchBtn.isChecked()) {
                    switchBtn.toggle();
                    onSwitchClick(switchBtn, app);
                }else
                {
                    --selectCount;
                    if(selectCount<=0)
                    {

                        ((Activity)mContext).invalidateOptionsMenu();
                    }
                    NALockDbHelper.getInstance(mContext).disableAppRestriction(app.getPackageName());
                    app.setRestricted(false);
                    switchBtn.toggle();
                }
            }

            @Override
            public void onSwitchClick(View view, App app) {
                ((ToggleButton)view).setChecked(false);
                    ++selectCount;
                    ((Activity)mContext).invalidateOptionsMenu();
                if (Utils.checkPermission(mContext)) {
                    insertAppsToRestrict(app);
                    ((ToggleButton) view).setChecked(true);
                } else {
                    appToInsert = app;
                    toggleBtn = (ToggleButton)view;
                    showDialog();
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        mAppListOfRestrictedApps = new ArrayList<>();

        mAppRecyclerView.setAdapter(mAdapter);
    }

    void showDialog() {
         mDialogFragment = NADialogFragment.newInstance(
                R.string.msg_usage_permission_dialog);
        mDialogFragment.show(getSupportFragmentManager(), "dialog");
    }

    public void doPositiveClick() {
        if(mDialogFragment != null)
            mDialogFragment.dismiss();
        startActivityForResult(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS), Constants.USAGE_PERMISSION_REQ_CODE);
        Log.i("FragmentAlertDialog", "Positive click!");
    }

    public void doNegativeClick() {
        if(mDialogFragment != null)
            mDialogFragment.dismiss();

        appToInsert = null;
        // Do stuff here.
        Log.i("FragmentAlertDialog", "Negative click!");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);//Menu Resource, Menu

        doneItem = menu.findItem(R.id.action_done);
        doneItem.setVisible(selectCount>0);
        //this.invalidateOptionsMenu();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                //insertAppsToRestrict();
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
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
                break;
            default:
                appToInsert = null;
                break;

        }
        Log.d(TAG, "on Activity result" + requestCode);
    }


    private void insertAppsToRestrict(App app) {

        app.setRestrictionTime(30 * Utils.MIN_IN_MILLSEC);
        app.setForegroundTime(0);
        app.setRestricted(true);
        NALockDbHelper.getInstance(mContext).insertAppForRestriction(app, mContext);
        appToInsert = null;

    }
}
