package com.can.appstore.myapps.allappsview;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.can.appstore.R;
import com.can.appstore.base.BaseActivity;
import com.can.appstore.myapps.adapter.AllAppsRecyclerViewAdapter;
import com.can.appstore.widgets.CanDialog;

import java.util.List;

import cn.can.tvlib.common.pm.PackageUtil.AppInfo;
import cn.can.tvlib.ui.focus.FocusMoveUtil;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerView;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewAdapter;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewDivider;


/**
 * Created by wei on 2016/10/26.
 */

public class AllAppsActivity extends BaseActivity implements AllAppsContract.View {

    public static final int UNINSTALL_LAST_POSITION_DELAYE = 490;//卸载最后一个位置延时请求焦点
    public static String TAG = "AllAppsActivity";
    private List<AppInfo> allAppList = null;
    private CanRecyclerView mAllAppsRecyclerView;
    private AllAppsRecyclerViewAdapter mAdapter;
    private AllAppsPresenter mAllAppsPresenter;
    private TextView tvCurRows;
    private TextView tvTotalRows;
    private LinearLayout ll_edit;
    //item的操作按钮
    private Button butStrartapp;
    private Button butUninstall;
    //卸载对话框
    private CanDialog mCanDialog;
    //焦点框和焦点处理
    private FocusMoveUtil focusMoveUtil;
    private View mFocusedListChild;
    private MyFocusRunnable myFocusRunnable;
    private CanRecyclerViewAdapter.OnFocusChangeListener myFocusChangesListener;

    /**
     * 打开全部应用
     * *
     *
     * @param context
     */
    public static void actionStart(Context context) {
        Intent intent = new Intent(context, AllAppsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myapps_allapps);
        mAllAppsRecyclerView = (CanRecyclerView) findViewById(R.id.allapps_recyclerview);
        tvCurRows = (TextView) findViewById(R.id.allapps_tv_currows);
        tvTotalRows = (TextView) findViewById(R.id.allapps_tv_totalrows);
        mAllAppsPresenter = new AllAppsPresenter(this, AllAppsActivity.this);
        mAllAppsPresenter.startLoad();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAllAppsPresenter != null) {
            mAllAppsPresenter.addListener();
        }
    }

    private void initView() {
        focusMoveUtil = new FocusMoveUtil(this, getWindow().getDecorView(), R.drawable.btn_focus);
        myFocusRunnable = new MyFocusRunnable();
        measureFocusActiveRegion();
        mAllAppsRecyclerView.setLayoutManager(new CanRecyclerView.CanGridLayoutManager(this, 5, GridLayoutManager.VERTICAL, false));
        mAllAppsRecyclerView.setKeyCodeEffectInterval(260);
        mAllAppsRecyclerView.addItemDecoration(new CanRecyclerViewDivider(android.R.color.transparent, 40, 0));
    }

    private void measureFocusActiveRegion() {
        mAllAppsRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                int[] location = new int[2];
                mAllAppsRecyclerView.getLocationInWindow(location);
                //noinspection deprecation
                focusMoveUtil.setFocusActiveRegion(0, location[1] + mAllAppsRecyclerView.getPaddingTop(), getWindowManager().
                        getDefaultDisplay().getWidth(), location[1] + mAllAppsRecyclerView.getMeasuredHeight()
                        - getResources().getDimensionPixelSize(R.dimen.px40));
            }
        });
    }

    @Override
    public void loadAllAppInfoSuccess(List<AppInfo> infoList) {
        Log.d(TAG, "loadAllAppInfoSuccess");
        if (mAdapter == null) {
            allAppList = infoList;
            mAdapter = new AllAppsRecyclerViewAdapter(infoList);
            baseSetting();
            addFocusListener();
        } else {
            mAdapter.notifyDataSetChanged();
        }
        //设置右上角总行数
        int total = mAllAppsPresenter.calculateCurTotalRows();
        tvTotalRows.setText("/" + total + "行");
    }

    /**
     * 卸载最后一个位置的应用时，让其上一个应用item获取焦点
     *
     * @param position
     */
    @Override
    public void uninstallLastPosition(final int position) {
        Log.d(TAG, "uninstallLastPosition,卸载最后一个应用");
        focusMoveUtil.hideFocusForShowDelay(600);
        mAllAppsRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                View curItem = mAllAppsRecyclerView.getChildAt(position);
                if (curItem != null) {
                    curItem.setFocusable(true);
                    curItem.requestFocus();
                }
            }
        }, UNINSTALL_LAST_POSITION_DELAYE);

    }

    private void baseSetting() {
        mAllAppsRecyclerView.setAdapter(mAdapter);
        mAdapter.setItemKeyEventListener(new MyOnItemKeyEventListener());
        mAdapter.setOnItemClickListener(new myOnItemClickListener());

        focusMoveUtil.hideFocusForShowDelay(50);
        mAllAppsRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                View childAt = mAllAppsRecyclerView.getChildAt(0);
                focusMoveUtil.setFocusView(childAt);
                childAt.requestFocus();
            }
        }, 50);
        mAllAppsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                myFocusRunnable.run();
            }
        });
    }

    private void editItem(final View item, final int position) {
        Log.d(TAG, "editItem" + "POSITION" + position);
        butStrartapp.requestFocus();
        butStrartapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAllAppsPresenter.startApp(position);
                hideEditView(item);
            }
        });
        butStrartapp.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if (butUninstall.getVisibility() == View.GONE) {
                    if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                        return true;
                    }
                }
                if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                    return true;
                }
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    hideEditView(item);
                    return true;
                }
                return false;
            }
        });

        butUninstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAllAppsPresenter.getUninstallAppInfo(position);
                hideEditView(item);
            }
        });
        butUninstall.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                    return true;
                }
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    hideEditView(item);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void showUninstallDialog(AppInfo app) {
        Log.d(TAG, "showUninstallDialog" + "app" + app.toString());
        String ok = getResources().getString(R.string.ok);
        String cancle = getResources().getString(R.string.cancle);
        String makesureUninstall = getResources().getString(R.string.makesure_uninstall_apk);
        Drawable mIcon = app.appIcon;
        final String mName = app.appName;
        final String mPackName = app.packageName;
        if (mCanDialog != null) {
            mCanDialog.dismiss();
            mCanDialog.release();
            mCanDialog = null;
        }
        mCanDialog = new CanDialog(this);
        mCanDialog.setIcon(mIcon)
                .setTitle(mName)
                .setTitleMessage(makesureUninstall)
                .setNegativeButton(cancle)
                .setPositiveButton(ok)
                .setOnCanBtnClickListener(new CanDialog.OnClickListener() {
                    @Override
                    public void onClickPositive() {
                        silentUninstall(mName, mPackName);
                        dismissUninstallDialog();
                    }

                    @Override
                    public void onClickNegative() {
                        dismissUninstallDialog();
                    }
                });
        mCanDialog.show();
    }

    private void dismissUninstallDialog() {
        if (mCanDialog != null) {
            mCanDialog.dismiss();
        }
    }

    private void silentUninstall(String name, String packname) {
        Log.d(TAG, "silentUninstall" + "packname" + packname + "name" + name);
        mAllAppsPresenter.silentUninstall(name, packname);
    }


    @Override
    public void setPresenter(Object presenter) {

    }


    @Override
    protected void onHomeKeyDown() {
        if (mCanDialog != null) {
            mCanDialog.dismiss();
            mCanDialog.release();
            mCanDialog = null;
        }
        super.onHomeKeyDown();
    }

    private void addFocusListener() {
        myFocusChangesListener = new CanRecyclerViewAdapter.OnFocusChangeListener() {
            @Override
            public void onItemFocusChanged(View view, int position, boolean hasFocus) {
                if (hasFocus) {
                    mFocusedListChild = view;
                    myFocusRunnable.run();
                    int cur = mAllAppsPresenter.calculateCurRows(position);
                    tvCurRows.setText(cur + "");
                }
            }
        };
        mAdapter.setOnFocusChangeListener(myFocusChangesListener);
    }

    public void hideEditView(View item) {
        item.requestFocus();
        ll_edit.setVisibility(View.GONE);
        mAdapter.setOnFocusChangeListener(myFocusChangesListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAllAppsPresenter != null) {
            mAllAppsPresenter.unRegiestr();
        }
    }

    @Override
    protected void onDestroy() {
        if (mCanDialog != null) {
            mCanDialog.dismiss();
            mCanDialog.release();
            mCanDialog = null;
        }
        focusMoveUtil.release();
        mAllAppsPresenter.release();
        super.onDestroy();
    }

    private class myOnItemClickListener implements CanRecyclerViewAdapter.OnItemClickListener {
        @Override
        public void onClick(View view, int position, Object data) {
            mAllAppsPresenter.startApp(position);
        }
    }

    private class MyFocusRunnable implements Runnable {
        @Override
        public void run() {
            if (mFocusedListChild != null) {
                focusMoveUtil.startMoveFocus(mFocusedListChild, 2);
            }
        }
    }

    private class MyOnItemKeyEventListener implements CanRecyclerViewAdapter.OnItemKeyEventListener {
        @Override
        public boolean onItemKeyEvent(int position, View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_MENU) {
                mAdapter.setOnFocusChangeListener(null);
                ll_edit = (LinearLayout) v.findViewById(R.id.allapps_ll_edit);
                butStrartapp = (Button) ll_edit.findViewById(R.id.allapps_but_startapp);
                butUninstall = (Button) ll_edit.findViewById(R.id.allapps_but_uninstallapp);
                ll_edit.setVisibility(View.VISIBLE);
                if (allAppList.get(position).isSystemApp) {
                    butUninstall.setVisibility(View.GONE);
                } else {
                    butUninstall.setVisibility(View.VISIBLE);
                }
                editItem(v, position);
            }
            return false;
        }
    }
}
