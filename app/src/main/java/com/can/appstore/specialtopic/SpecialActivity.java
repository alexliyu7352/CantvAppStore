package com.can.appstore.specialtopic;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.can.appstore.R;
import com.can.appstore.base.BaseActivity;
import com.can.appstore.entity.SpecialTopic;
import com.can.appstore.specialdetail.SpecialDetailActivity;
import com.can.appstore.specialtopic.adapter.SpecialAdapter;

import java.util.List;

import cn.can.tvlib.imageloader.ImageLoader;
import cn.can.tvlib.ui.focus.FocusMoveUtil;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerView;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewAdapter;
import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewDivider;
import cn.can.tvlib.utils.NetworkUtils;

import static com.can.appstore.R.id.special_recyclerview;

public class SpecialActivity extends BaseActivity implements SpecialContract.SubjectView {

    private static final String TAG = "SpecialActivity";
    private static final float FOCUS_SCALE = 1.0f;
    public static final int COLUMN_COUNT = 4;
    public static final int FOCUS_IMAGE = R.mipmap.image_focus;
    public static final int FOCUS_BUTTON = R.mipmap.btn_focus;
    public static final int DELAY_MILLIS = 500;

    private TextView mRowTv, mRemindTv;
    private RelativeLayout mRemindLayout;
    private Button mRetryBtn;

    private CanRecyclerView mRecyclerView;
    private SpecialAdapter mAdapter;
    private GridLayoutManager mLayoutManager;

    private FocusMoveUtil mFocusMoveUtils;

    private SpecialContract.SpecialPresenter mPresenter;

    private Runnable mFocusMoveRunnable, mResolveFirstRunnable;
    private Handler mHandler;
    private View mCurrFocusView;

    private String noDataStr, netErrorStr;

    private int mFocusType = FOCUS_IMAGE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_special_topic);
        initView();
        initRunnable();
        setListener();
        initData();
    }

    private void initView() {
        CanRecyclerViewDivider itemDecoration = new CanRecyclerViewDivider(Color.TRANSPARENT,
                getResources().getDimensionPixelSize(R.dimen.px24),
                getResources().getDimensionPixelSize(R.dimen.px40));

        mRowTv = (TextView) findViewById(R.id.special_row_tv);
        mRemindTv = (TextView) findViewById(R.id.special_remind_tv);
        mRemindLayout = (RelativeLayout) findViewById(R.id.special_remind_layout);
        mRetryBtn = (Button) findViewById(R.id.special_retry_btn);

        mLayoutManager = new GridLayoutManager(this, COLUMN_COUNT);
        mRecyclerView = (CanRecyclerView) findViewById(special_recyclerview);
        mRecyclerView.addItemDecoration(itemDecoration);
        mRecyclerView.setLayoutManager(mLayoutManager);

    }

    private void initRunnable() {
        mFocusMoveRunnable = new Runnable() {
            @Override
            public void run() {
                if (mCurrFocusView != null && mFocusMoveUtils != null) {
                    mFocusMoveUtils.startMoveFocus(mCurrFocusView, FOCUS_SCALE);
                }
            }
        };

        mResolveFirstRunnable = new Runnable() {
            @Override
            public void run() {
                View firstView = mRecyclerView.getChildAt(0);
                if (firstView != null) {
                    firstView.requestFocus();
                    mFocusMoveUtils.setFocusView(firstView);
                }
                mFocusMoveUtils.showFocus();
            }
        };

        mHandler = new Handler();
    }

    private void setListener() {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int scrollDy=0;
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (RecyclerView.SCROLL_STATE_IDLE == newState) {
                    if (!isDestroyed()) {
                        ImageLoader.getInstance().resumeTask(SpecialActivity.this);
                        int lastPos = mLayoutManager.findLastVisibleItemPosition();
                        if(scrollDy>0){
                            mPresenter.loadMore(lastPos);
                        }
                    }
                } else {
                    ImageLoader.getInstance().pauseTask(SpecialActivity.this);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dx == 0 && dy == 0) {
                    scrollDy=0;
                    return;
                }
                scrollDy=dy;
                mHandler.removeCallbacks(mFocusMoveRunnable);
                mHandler.postDelayed(mFocusMoveRunnable, 50);
            }
        });
        //重试
        mRetryBtn.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (FOCUS_BUTTON != mFocusType) {
                        mFocusType = FOCUS_BUTTON;
                        mFocusMoveUtils.setFocusRes(getContext(), mFocusType);
                    }
                    mCurrFocusView = v;
                    mHandler.removeCallbacks(mFocusMoveRunnable);
                    mHandler.post(mFocusMoveRunnable);
                }
            }
        });

        mRetryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetworkUtils.isNetworkConnected(getContext().getApplicationContext())) {
                    showToast(R.string.network_connection_disconnect);
                    return;
                }
                mFocusMoveUtils.hideFocus();
                mPresenter.startLoad();
            }
        });
    }

    private void initData() {
        noDataStr = getString(R.string.no_data);
        netErrorStr = getString(R.string.network_error);

        mFocusMoveUtils = new FocusMoveUtil(this, getWindow().getDecorView().findViewById(android.R.id.content), R.mipmap.image_focus);
        SpecialContract.SpecialPresenter presenter = new SpecialPresenterImpl(this);
        presenter.startLoad();
    }

    @Override
    public void refreshData(List<SpecialTopic> data) {
        mAdapter = new SpecialAdapter(data, this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnFocusChangeListener(new CanRecyclerViewAdapter.OnFocusChangeListener() {
            @Override
            public void onItemFocusChanged(View view, int position, boolean hasFocus) {
                if (hasFocus) {
                    mCurrFocusView = view;
                    if (FOCUS_IMAGE != mFocusType) {
                        mFocusType = FOCUS_IMAGE;
                        mFocusMoveUtils.setFocusRes(getContext(), mFocusType);
                    }
                    mHandler.removeCallbacks(mFocusMoveRunnable);
                    mHandler.postDelayed(mFocusMoveRunnable, 30);
                    view.setSelected(true);
                    if (mPresenter != null) {
                        mPresenter.onItemFocused(position);
                    }
                } else {
                    view.setSelected(false);
                }
            }
        });
        mAdapter.setOnItemClickListener(new CanRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onClick(View view, int position, Object data) {
                if(!NetworkUtils.isNetworkConnected(getContext())){
                    // TODO: 2016/11/22
                    showToast(R.string.network_connection_disconnect);
                    return;
                }
                SpecialTopic topic= (SpecialTopic) data;
                if(topic!=null&&topic.getId()!=null){
                    Intent intent=new Intent(SpecialActivity.this, SpecialDetailActivity.class);
                    intent.putExtra(SpecialDetailActivity.EXTRA_TOPIC_ID,topic.getId());
                    startActivity(intent);
                }else{
                    showToast(R.string.data_error);
                }
            }
        });
        resolveFirstFocus();
    }

    @Override
    public void refreshRowNum(CharSequence row) {
        mRowTv.setText(row);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFocusMoveUtils.release();
        mPresenter.release();
        mPresenter = null;
    }

    @Override
    public void setPresenter(SpecialContract.SpecialPresenter presenter) {
        mPresenter = presenter;
    }

    @Override
    protected void onStop() {
        super.onStop();
        mRecyclerView.removeCallbacks(mResolveFirstRunnable);
        mHandler.removeCallbacksAndMessages(null);
    }

    private void resolveFirstFocus() {
        mRecyclerView.postDelayed(mResolveFirstRunnable, DELAY_MILLIS);
    }

    @Override
    public void showNoDataView() {
        mRemindTv.setText(noDataStr);
        mRecyclerView.setVisibility(View.INVISIBLE);
        mHandler.removeCallbacks(mFocusMoveRunnable);
        mFocusMoveUtils.setFocusView(mRetryBtn);
        mFocusMoveUtils.hideFocusForShowDelay(30);
        mRemindLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void showRetryView() {
        mRemindTv.setText(netErrorStr);
        mRecyclerView.setVisibility(View.INVISIBLE);
        mHandler.removeCallbacks(mFocusMoveRunnable);
        mFocusMoveUtils.setFocusView(mRetryBtn);
        mFocusMoveUtils.hideFocusForShowDelay(30);
        mRemindLayout.setVisibility(View.VISIBLE);

    }

    @Override
    public void hideRetryView() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mRemindLayout.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onLoadMore(int startInsertPos, int endInsertPos) {
        if (mAdapter != null) {
            mAdapter.notifyItemRangeInserted(startInsertPos, endInsertPos);
        }
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, SpecialActivity.class);
        context.startActivity(intent);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (KeyEvent.ACTION_DOWN == event.getAction() && KeyEvent.KEYCODE_DPAD_DOWN == event.getKeyCode()) {
            if (mPresenter != null) {
                mPresenter.remindNoData();
            }
        }
        return super.dispatchKeyEvent(event);
    }
}
