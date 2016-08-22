package com.hackathon.na_lock.adapter;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.hackathon.na_lock.AppListHomeActivity;
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

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        public ImageView icon;
        public TextView appName, duration;
        public ToggleButton mSwitch;
        public App appItem;
        public View mView;


        public MyViewHolder(View view) {
            super(view);

            mView = view;
            icon = (ImageView) view.findViewById(R.id.appIcon);
            appName = (TextView) view.findViewById(R.id.appName);
            mSwitch = (ToggleButton) view.findViewById(R.id.toggle);
            duration = (TextView) view.findViewById(R.id.duration);

          // mView.setOnCreateContextMenuListener(this);

        }


        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

            //menu.setHeaderTitle("Select The Action");
            menu.add(0, v.getId(), 0, " Delete");//groupId, itemId, order, title
            // menu.add(0, v.getId(), 0, "SMS");
        }


    }


    public interface OnItemClickListener {
        void onItemClick(View view,App app);
        void onSwitchClick(View view,App app);

    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public AppRecyclerAdapter(List<App> appList, Context context) {
        this.mAppList = appList;
        this.mContext = context;
    }

    public void setAppList(List<App> mAppList) {
        this.mAppList = mAppList;
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
        if(app.getAppIcon() != null) {
            holder.icon.setImageDrawable(app.getAppIcon());
        }
        else {
            Drawable icon = null;
            try {
                icon = mContext.getPackageManager().getApplicationIcon(app.getPackageName());
                holder.icon.setImageDrawable(icon);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                holder.icon.setVisibility(View.GONE);
            }


        }
        if (app.isRestricted()) {
            holder.mSwitch.setChecked(true);
        } else {
            holder.mSwitch.setChecked(false);
        }

        if(mContext instanceof AppListHomeActivity)
            holder.duration.setText("Usage Limit: " + NAUtils.convertToMin(app.getRestrictionTime()));

        holder.mSwitch.setTag(app);

        holder.mSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(holder.mSwitch.isChecked())
                {
                    holder.mSwitch.setChecked(false);
                    if (mItemClickListener != null)
                        mItemClickListener.onSwitchClick(v, holder.appItem);
                }else {
                    NALockDbHelper.getInstance(mContext).disableAppRestriction(holder.appItem.getPackageName());
                    app.setRestricted(false);
                    //below condition code has to be changed since it is unstable
                    /*if(mContext instanceof AppListHomeActivity)
                    {
                        mAppList.remove(app);
                        notifyDataSetChanged();
                    }*/
                }

            }
        });

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                        if (mItemClickListener != null)
                            mItemClickListener.onItemClick(v, holder.appItem);
            }
        });

        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                v.setBackgroundColor(mContext.getResources().getColor(R.color.colorOverlayBackground));
                return false;
            }
        });


    }

    private void addRestrictApp(App appItem) {

        appItem.setRestricted(true);
        appItem.setRestrictionTime(30 * Utils.MIN_IN_MILLSEC);
        appItem.setForegroundTime(0);
        NALockDbHelper.getInstance(mContext).insertAppForRestriction(appItem, mContext);
    }

    @Override
    public int getItemCount() {

        if (mAppList != null)
            return mAppList.size();
        else
            return 0;
    }
}