package com.hackathon.na_lock.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.hackathon.na_lock.R;
import com.hackathon.na_lock.Util.NAUtils;
import com.hackathon.na_lock.Utils;
import com.hackathon.na_lock.databases.NALockDbHelper;
import com.hackathon.na_lock.pojo.App;

import java.util.List;

/**
 * Created by karthik on 26-05-2016.
 */
public class AppRecyclerAdapter extends RecyclerView.Adapter<AppRecyclerAdapter.MyViewHolder> {

    private static final String TAG = "AppRecyclerAdapter";
    private List<App> mAppList;
    private Context mContext;
    private OnItemClickListener mItemClickListener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView icon;
        public TextView appName, duration;
        public Switch mSwitch;
        public App appItem;
        public View mView;


        public MyViewHolder(View view) {
            super(view);

            mView = view;
            icon = (ImageView) view.findViewById(R.id.appIcon);
            appName = (TextView) view.findViewById(R.id.appName);
            mSwitch = (Switch) view.findViewById(R.id.toggle);
            duration = (TextView) view.findViewById(R.id.duration);
            //view.setOnClickListener(this);

        }


    }


    public interface OnItemClickListener {
        public void onItemClick(View view,App app);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public AppRecyclerAdapter(List<App> appList, Context context) {
        this.mAppList = appList;
        this.mContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.app_list_row, parent, false);
        /*itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,"itemClicked",Toast.LENGTH_LONG).show();
            }
        });*/

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final App app = mAppList.get(position);

        holder.appItem = app;
        holder.appName.setText(app.getAppName());
        if(app.getAppIcon() != null)
            holder.icon.setImageDrawable(app.getAppIcon());
        else
            holder.icon.setVisibility(View.GONE);
        if (app.isRestricted()) {
            holder.mSwitch.setChecked(true);
            holder.duration.setText("Usage Limit: " + NAUtils.convertToMin(app.getRestrictionTime()));
        } else {
            holder.mSwitch.setChecked(false);
        }

        holder.mSwitch.setTag(app);

        holder.mSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                App appToInsert = (App) v.getTag();
                appToInsert.setRestricted(((Switch) v).isChecked());


                if (appToInsert.isRestricted()) {
                    if(!Utils.checkPermission(mContext))
                        mItemClickListener.onItemClick(v,holder.appItem);
                    else {
                        appToInsert.setRestrictionTime(30 * Utils.MIN_IN_MILLSEC);
                        appToInsert.setForegroundTime(0);
                        NALockDbHelper.getInstance(mContext).insertAppForRestriction(appToInsert, mContext);
                    }

                } else{
                    //here goes the update enabled instead of deleting thee entry
                    NALockDbHelper.getInstance(mContext).deleteApp(appToInsert.getPackageName());
                }
            }
        });

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mItemClickListener.onItemClick(v,holder.appItem);
            }
        });


    }

    @Override
    public int getItemCount() {

        if (mAppList != null)
            return mAppList.size();
        else
            return 0;
    }
}