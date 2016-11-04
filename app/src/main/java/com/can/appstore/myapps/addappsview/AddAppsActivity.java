package com.can.appstore.myapps.addappsview;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.can.appstore.R;
import com.can.appstore.myapps.adapter.AddAppsRvAdapter;
import com.can.appstore.myapps.model.AppInfo;
import com.can.appstore.search.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import cn.can.tvlib.ui.focus.FocusMoveUtil;
import cn.can.tvlib.ui.focus.FocusScaleUtil;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerView;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewAdapter;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewDivider;

/**
 * Created by wei on 2016/10/26.
 */

public class AddAppsActivity extends Activity implements AddAppsContract.View{
    //数据源
    private ArrayList<AppInfo> mSelectAppInfo;
    int canSelect = 0;
    //
    AddAppsPresenter mAddAppsPresenter;
    AddAppsRvAdapter mAddAppsRecyclerViewAdapter ;

    //焦点框
    FocusMoveUtil mFocusMoveUtil ;
    FocusScaleUtil mFocusScaleUtil;
    View mFocusChild;
    MyFocusRunnable  mFocusRunnable;
    private boolean focusSearchFailed;


    //布局控件
    Button addBut;
    TextView  tv_select;
    TextView  tv_canSelect;
    TextView  tv_curRows;
    TextView  tv_totalRows;
    CanRecyclerView mAddRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myapps_addapps);

        mFocusMoveUtil = new FocusMoveUtil(this,getWindow().getDecorView(),R.drawable.btn_focus);
        mFocusScaleUtil = new FocusScaleUtil();
        mFocusRunnable = new MyFocusRunnable();

        mAddAppsPresenter = new AddAppsPresenter(this,AddAppsActivity.this);
        mAddAppsPresenter.startLoad();

        initView();
    }


    private  void initView(){
        addBut = (Button) findViewById(R.id.bt_batch_addapps);
        tv_canSelect = (TextView)findViewById(R.id.tv_canadd_cut);
        tv_select = (TextView) findViewById(R.id.tv_addselect_count);
        tv_curRows = (TextView) findViewById(R.id.addapps_tv_currows);
        tv_totalRows = (TextView)findViewById(R.id.addapps_tv_totalrows);

        mAddRecyclerView = (CanRecyclerView)findViewById(R.id.addapps_recyclerview);

        mAddRecyclerView.setLayoutManager(new CanRecyclerView.CanGridLayoutManager(this,4, GridLayoutManager.VERTICAL,false),new  CanRecyclerView.OnFocusSearchCallback(){
            @Override
            public void onSuccess(View view, View focused, int focusDirection, RecyclerView.Recycler recycler, RecyclerView.State state) {
                focusSearchFailed = false;
            }
            @Override
            public void onFail(View focused, int focusDirection, RecyclerView.Recycler recycler, RecyclerView.State state) {
                focusSearchFailed = false;
            }
        } );
        mAddRecyclerView.addItemDecoration(new CanRecyclerViewDivider(android.R.color.transparent,40,85));

    }

    @Override
    public void showLoading() {
        mAddAppsPresenter.showLoading("加载中，请稍等...");
    }

    @Override
    public void hideLoading() {
        mAddAppsPresenter.hideLoading();
    }

    @Override
    public void onClickHomeKey() {
        finish();
    }

    @Override
    public void loadAllAppInfoSuccess(List<AppInfo> infoList) {
        if(mAddAppsRecyclerViewAdapter == null){
            mAddAppsRecyclerViewAdapter = new AddAppsRvAdapter(infoList);
            //设置右上角总行数
            tv_curRows.setText("0");
            int totalRows = 0 ;
            if(infoList.size() % 4 == 0) {
                totalRows = infoList.size() / 4;
            }else{
                totalRows = infoList.size() / 4 + 1;
            }
            tv_totalRows.setText("/"+totalRows+"行");
            mAddAppsPresenter.canSelectCount();
            baseSetting();
            addViewListener();
        }else{
            mAddAppsRecyclerViewAdapter.notifyDataSetChanged();
        }


    }

    private void baseSetting() {
        mAddRecyclerView.setAdapter(mAddAppsRecyclerViewAdapter);
        mFocusMoveUtil.hideFocusForShowDelay(50);
        mAddRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                View childAt = mAddRecyclerView.getChildAt(0);
                if (childAt != null) {
                    mFocusMoveUtil.setFocusView(childAt);
                    childAt.requestFocus();
                } else {
                    addBut.requestFocus();
                }
            }
        }, 50);
    }

    @Override
    public void showCanSelectCount(int cansel, int alreadyshow) {
        this.canSelect = cansel;
        tv_canSelect.setText("已添加"+alreadyshow+"个，还可以添加"+cansel+"个");
    }

    /**
     * 保存选择添加的app
     * @param list
     */
    @Override
    public void saveSelectInfo(List<AppInfo> list) {
        mAddAppsPresenter.saveSelectlist(list);
    }

    class MyFocusRunnable implements Runnable {
        @Override
        public void run() {
            if(mFocusChild != null){
                mFocusScaleUtil.scaleToLarge(mFocusChild);
                if(focusSearchFailed){
                    mFocusMoveUtil.startMoveFocus(mFocusChild,1.1f);
                }else{
                    mFocusMoveUtil.startMoveFocus(mFocusChild,1.1F,0);
                }
            }
        }
    }




    private void addViewListener() {
        //按钮的点击事件
        addBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSelectAppInfo == null || mSelectAppInfo.size() == 0){
                    ToastUtil.toastShort("您没有选择任何应用");
                }else{
                    //TODO 存
                    saveSelectInfo(mSelectAppInfo);
                    finish();
                }
            }
        });
        //按钮的焦点监听
        addBut.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mFocusMoveUtil.startMoveFocus(addBut, 1.1f);
                    mFocusScaleUtil.scaleToLarge(addBut);
                    tv_curRows.setText("0");
                } else {
                    mFocusScaleUtil.scaleToNormal(addBut);
                }
            }
        });
        //RecyclerView的点击事件
        mAddAppsRecyclerViewAdapter.setOnItemClickListener(new CanRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onClick(View view, int position, Object data) {
                ImageView imSelect = (ImageView)view.findViewById(R.id.addapps_iv_check);
                AppInfo app= (AppInfo)data;
                if(mSelectAppInfo == null){
                    mSelectAppInfo = new ArrayList<AppInfo>();
                }
                if(mSelectAppInfo.size() >= canSelect ){
                    if(mSelectAppInfo.contains(app)){
                        imSelect.setBackgroundResource(R.drawable.unselect);
                        mSelectAppInfo.remove(app);
                    }else{
                        ToastUtil.toastShort("当前桌面已满，无法继续添加");
                    }
                }else{
                    if(mSelectAppInfo.contains(app)){
                        imSelect.setBackgroundResource(R.drawable.unselect);
                        mSelectAppInfo.remove(app);
                    }else{
                        imSelect.setBackgroundResource(R.drawable.select);
                        mSelectAppInfo.add(app);
                    }
                }
                tv_select.setText(""+mSelectAppInfo.size());
            }
        });
        //RecyclerView的焦点事件
        mAddAppsRecyclerViewAdapter.setOnFocusChangeListener(new CanRecyclerViewAdapter.OnFocusChangeListener() {
            @Override
            public void onItemFocusChanged(View view, int position, boolean hasFocus) {
                if(hasFocus){
                    mFocusChild = view;
                    mAddRecyclerView.postDelayed(mFocusRunnable,50);
                    int curRows = position / 4 + 1;
                    tv_curRows.setText(""+curRows);
                }else{
                    mFocusScaleUtil.scaleToNormal();
                }
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mAddAppsPresenter!= null){
            mAddAppsPresenter.addListener();
        }
    }
}