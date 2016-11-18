package com.can.appstore.myapps.allappsview;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.can.appstore.R;
import com.can.appstore.myapps.adapter.AllAppsRecyclerViewAdapter;
import com.can.appstore.widgets.CanDialog;

import java.util.List;

import cn.can.tvlib.ui.focus.FocusMoveUtil;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerView;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewAdapter;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewDivider;
import cn.can.tvlib.utils.PackageUtil.AppInfo;
import retrofit2.http.HEAD;

/**
 * Created by wei on 2016/10/26.
 */

public class AllAppsActivity extends Activity implements AllAppsContract.View {

    List<AppInfo> allAppList = null;
    CanRecyclerView mAllAppsRecyclerView;
    AllAppsRecyclerViewAdapter mAdapter;

    TextView tvCurRows;
    TextView tvTotalRows;
    LinearLayout ll_edit;

    //焦点框
    FocusMoveUtil focusMoveUtil;
    //    FocusScaleUtil focusScaleUtil;
    private View mFocusedListChild;
    private MyFocusRunnable myFocusRunnable;
    private boolean focusSearchFailed;
    CanRecyclerViewAdapter.OnFocusChangeListener myFocusChangesListener;

    AllAppsPresenter mAllAppsPresenter;


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
        //        focusScaleUtil = new FocusScaleUtil();
        myFocusRunnable = new MyFocusRunnable();
        mAllAppsRecyclerView.setLayoutManager(new CanRecyclerView.CanGridLayoutManager(this, 5, GridLayoutManager.VERTICAL, false), new CanRecyclerView.OnFocusSearchCallback() {
            @Override
            public void onSuccess(View view, View focused, int focusDirection, RecyclerView.Recycler recycler, RecyclerView.State state) {
                focusSearchFailed = false;
            }

            @Override
            public void onFail(View focused, int focusDirection, RecyclerView.Recycler recycler, RecyclerView.State state) {
                focusSearchFailed = false;
            }
        });
        mAllAppsRecyclerView.addItemDecoration(new CanRecyclerViewDivider(android.R.color.transparent, 40, 0));
    }


    @Override
    public void loadAllAppInfoSuccess(List<AppInfo> infoList) {
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
        tvTotalRows.setText(total + "行");
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
    }

    Button butStrartapp;
    Button butUninstall;

    private void editItem(final View item, final int position) {

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

    CanDialog mCanDialog;

    @Override
    public void showUninstallDialog(AppInfo app) {
        String ok = "确定";
        String cancle = "取消";
        String makesureUninstall = "您确定删除该应用吗？";
        Drawable mIcon = app.appIcon;
        final String mName = app.appName;
        final String mPackName = app.packageName;
        mCanDialog = new CanDialog(this);
        mCanDialog.setmIvDialogTitle(mIcon)
                .setmTvDialogTitle(mName)
                .setmTvDialogTopLeftContent(makesureUninstall)
                .setmBtnDialogNegative(cancle)
                .setmBtnDialogPositive(ok)
                .setOnCanBtnClickListener(new CanDialog.OnCanBtnClickListener() {
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

    }


    @Override
    public void showLoading() {
        mAllAppsPresenter.showLoading("加载中，请稍后...");
    }

    @Override
    public void hideLoading() {
        mAllAppsPresenter.hideLoading();
    }

    @Override
    public void onClickHomeKey() {
        finish();
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
                //                focusScaleUtil.scaleToLarge(mFocusedListChild);
                if (focusSearchFailed) {
                    focusMoveUtil.startMoveFocus(mFocusedListChild);
                } else {
                    focusMoveUtil.startMoveFocus(mFocusedListChild, 0);
                }
            }
        }
    }


    private void addFocusListener() {
        myFocusChangesListener = new CanRecyclerViewAdapter.OnFocusChangeListener() {
            @Override
            public void onItemFocusChanged(View view, int position, boolean hasFocus) {
                if (hasFocus) {
                    mFocusedListChild = view;
                    mAllAppsRecyclerView.postDelayed(myFocusRunnable, 50);
                    int cur = mAllAppsPresenter.calculateCurRows(position);
                    tvCurRows.setText(cur + "/");
                } else {
                    //                    focusScaleUtil.scaleToNormal();
                }
            }
        };
        mAdapter.setOnFocusChangeListener(myFocusChangesListener);
    }

    private class MyOnItemKeyEventListener implements CanRecyclerViewAdapter.OnItemKeyEventListener {
        @Override
        public boolean onItemKeyEvent(int position, View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_MENU) {
                //判断系统应用机制 TODO
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
        focusMoveUtil.release();
        mAllAppsPresenter.release();
        super.onDestroy();
    }
}
