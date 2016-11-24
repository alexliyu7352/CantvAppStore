package com.can.appstore.myapps.myappsfragmview;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.can.appstore.myapps.utils.MyAppsListDataUtil;

import java.util.ArrayList;
import java.util.List;

import cn.can.tvlib.utils.PackageUtil.AppInfo;

/**
 * Created by wei on 2016/11/9.
 */

public class MyAppsFragPresenter implements MyAppsFramentContract.Presenter {
    private MyAppsFramentContract.View mView;
    private Context mContext;
    private MyAppsListDataUtil mMyAppsListDataUtil;
    private AppInstallReceiver mAppInstallReceiver;

    //主页显示的第三方应用
    private List<AppInfo> mShowList = new ArrayList<AppInfo>(18);
    //系统应用
    List<AppInfo> systemApp;
    //系统应用的icon
    private List<Drawable> mDrawables = new ArrayList<Drawable>();


    public MyAppsFragPresenter(MyAppsFramentContract.View view, Context context) {
        this.mView = view;
        this.mContext = context;
    }

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    @Override
    public void startLoad() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... params) {
                //初始化数据
                mMyAppsListDataUtil = new MyAppsListDataUtil(mContext);
                mShowList = mMyAppsListDataUtil.getShowList(mShowList);
                systemApp = mMyAppsListDataUtil.getSystemApp(null);
                mDrawables = sysAppInfo2Drawble(systemApp, mDrawables);
                Log.i("MYSHOWLIST", "------" + mShowList.size());
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                mView.loadAppInfoSuccess(mShowList);
                mView.loadCustomDataSuccess(mDrawables);
                removeHideApps();
            }
        }.execute();


    }

    //筛选隐藏应用
    public void removeHideApps() {
        systemApp = mMyAppsListDataUtil.removeHideApp(systemApp);
        mDrawables = sysAppInfo2Drawble(systemApp, mDrawables);
        mView.loadCustomDataSuccess(mDrawables);
    }

    private List<Drawable> sysAppInfo2Drawble(List<AppInfo> list, List<Drawable> mDrawablelist) {
        if (mDrawablelist.size() != 0) {
            mDrawablelist.clear();
        }
        for (int i = 0; i < list.size(); i++) {
            mDrawablelist.add(list.get(i).appIcon);
        }
        return mDrawablelist;
    }

    @Override
    public void addListener() {
        registerInstallReceiver();
    }

    /**
     * 注册应用安装卸载的广播
     */
    private void registerInstallReceiver() {
        if (mAppInstallReceiver == null) {
            mAppInstallReceiver = new MyAppsFragPresenter.AppInstallReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_PACKAGE_ADDED);
            filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
            filter.addAction(Intent.ACTION_VIEW);
            filter.addDataScheme("package");
            mContext.registerReceiver(mAppInstallReceiver, filter);
        }
    }


    class AppInstallReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 接收广播：设备上新安装了一个应用程序包后自动启动新安装应用程序。
            if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
                String packageName = intent.getDataString().substring(8);
//                if(!alreadyEdit && MyApp.myAppList.size()<16){
//                    TODO 首页排序
//                }
                startLoad();
            } else if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
                String packageName = intent.getData().getSchemeSpecificPart();
                int position = 0;
                for (int i = 0; i < mShowList.size(); i++) {
                    if (packageName.equals(mShowList.get(i).packageName)) {
                        position = i;
                    }
                }
                removeApp(position);
            }
        }
    }

    @Override
    public void release() {
        if (mMyAppsListDataUtil != null) {
            mMyAppsListDataUtil = null;
        }
        if (mShowList != null) {
            mShowList.clear();
            mShowList = null;
        }
        if (systemApp != null) {
            systemApp.clear();
            systemApp = null;
        }
        if (mDrawables != null) {
            mDrawables.clear();
            mDrawables = null;
        }
    }


    public void topApp(int position) {
        AppInfo appInfo = mShowList.get(position);
        mShowList.remove(position);
        mShowList.add(2, appInfo);
        mView.loadAppInfoSuccess(mShowList);
        mMyAppsListDataUtil.saveShowList(mShowList);
    }

    public void removeApp(int position) {
        mShowList.remove(position);
        mMyAppsListDataUtil.saveShowList(mShowList);
        mView.loadAppInfoSuccess(mShowList);
    }

    public void unRegiestr() {
        if (mAppInstallReceiver != null) {
            mContext.unregisterReceiver(mAppInstallReceiver);
            mAppInstallReceiver = null;
        }
    }
}
