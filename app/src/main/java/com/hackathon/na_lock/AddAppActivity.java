package com.hackathon.na_lock;

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
import android.widget.Switch;

import com.hackathon.na_lock.Util.Constants;
import com.hackathon.na_lock.Util.NAUtils;
import com.hackathon.na_lock.adapter.AppRecyclerAdapter;
import com.hackathon.na_lock.databases.NALockDbHelper;
import com.hackathon.na_lock.listeners.DialogActionListener;
import com.hackathon.na_lock.pojo.App;

import java.util.ArrayList;
import java.util.List;

public class AddAppActivity extends AppCompatActivity implements DialogActionListener{

    private static final String TAG = "AddAppActivity";
    private RecyclerView mAppRecyclerView;
    private AppRecyclerAdapter mAdapter;
    private List<App> mAppList,mAppListOfRestrictedApps;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_app);

        mContext = this;
        mAppList = NAUtils.getInstalledApps(mContext);
        mAppListOfRestrictedApps = new ArrayList<>();

        Log.d(TAG, mAppList.size() + "applist " + mAppList.get(0).getAppName());
        mAdapter = new AppRecyclerAdapter(mAppList, mContext);




        mAppRecyclerView = (RecyclerView) findViewById(R.id.app_list_recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mAppRecyclerView.setLayoutManager(mLayoutManager);
        mAppRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAppRecyclerView.setAdapter(mAdapter);
    }


    @Override
    protected void onStart() {
        super.onStart();
        if(!Utils.checkPermission(mContext)) {
            mAdapter.setOnItemClickListener(new AppRecyclerAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, App app) {
                    mAppListOfRestrictedApps.add(app);
                    showDialog();
                }
            });
        }else
            mAdapter.setOnItemClickListener(null);
    }

    void showDialog() {
        DialogFragment newFragment = NADialogFragment.newInstance(
                R.string.msg_usage_permission_dialog);
        newFragment.show(getSupportFragmentManager(), "dialog");
    }

    public void doPositiveClick() {
        // Do stuff here.
        startActivityForResult(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS), Constants.USAGE_PERMISSION_REQ_CODE);
        Log.i("FragmentAlertDialog", "Positive click!");
    }

    public void doNegativeClick() {
        // Do stuff here.
        Log.i("FragmentAlertDialog", "Negative click!");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);//Menu Resource, Menu
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

        switch (requestCode){
            case Constants.USAGE_PERMISSION_REQ_CODE:
                if(mAppListOfRestrictedApps!=null && mAppListOfRestrictedApps.size() > 0 && Utils.checkPermission(mContext))
                {
                    App appToInsert = mAppListOfRestrictedApps.get(0);
                    appToInsert.setRestrictionTime(30 * Utils.MIN_IN_MILLSEC);
                    appToInsert.setForegroundTime(0);
                    appToInsert.setRestricted(true);
                    NALockDbHelper.getInstance(mContext).insertAppForRestriction(appToInsert, mContext);
                }
                break;
            default:
                break;

        }
        Log.d(TAG,"on Activity result" + requestCode);
    }


    private void insertAppsToRestrict()
    {

        for(App appToInsert : mAppListOfRestrictedApps)
        {
            appToInsert.setRestrictionTime(30 * Utils.MIN_IN_MILLSEC);
            appToInsert.setForegroundTime(0);
            appToInsert.setRestricted(true);
            NALockDbHelper.getInstance(mContext).insertAppForRestriction(appToInsert, mContext);
        }
    }
}
