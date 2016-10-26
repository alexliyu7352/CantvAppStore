package cn.can.tvlib.ui.view.recyclerview;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import java.util.HashSet;
import java.util.Set;

import static android.R.attr.id;
import static android.R.attr.tag;

/**
 * Created by zhangbingyuan on 2016/10/15.
 */

/**
 * ================================================
 * 作    者：zhangbingyuan
 * 版    本：1.0
 * 创建日期：2016.10.12
 * 描    述：RecyclerView.Adapter封装，主要用于TV端ViewPager效果
 *          注1：配合RecyclerView使用时，必须配合LinearLayoutManager使用
 * 修订历史：
 * <p>
 * 1.0  zhangbingyuan
 * ================================================
 */
public abstract class CanViewPagerStateAdapter extends RecyclerView.Adapter<CanViewPagerStateAdapter.FragmentViewHolder> {

    private static final String TAG = "ViewPagerStateAdapter";

    public static final int VIEW_ID_OFFSET = 0x100;

    public interface OnPageChangeListener{
        void onChanged(int oldPage, int newPage);
    }

    private RecyclerView mAttachedView;
    protected final FragmentManager mFragmentManager;
    private FragmentTransaction mCurTransaction = null;
    private Set<Integer> mIds = new HashSet<>();
    private SparseArray<Fragment.SavedState> mStates = new SparseArray<>();
    private ViewTreeObserver.OnGlobalFocusChangeListener mGlobalFocusChangeListener;
    private RecyclerView.OnScrollListener mScrollListener;
    private OnPageChangeListener mPageChangeListener;
    private int mCurrPage;

    public CanViewPagerStateAdapter(FragmentManager fm) {
        mFragmentManager = fm;
        setHasStableIds(true);
    }

    @Override
    final public long getItemId(int position) {
        return position + VIEW_ID_OFFSET;
    }

    @Override
    public final FragmentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        FrameLayout fl = new FrameLayout(parent.getContext());
        fl.setFocusable(false);
        fl.setClickable(false);
        fl.setClipChildren(false);
        fl.setClipToPadding(false);
        fl.setLayoutParams(new RecyclerView.LayoutParams(-1, -1));

        int tagId = View.generateViewId();
        mIds.add(id);
        fl.setId(tagId);
        return new FragmentViewHolder(fl);
    }

    @Override
    final public void onBindViewHolder(FragmentViewHolder holder, int position) {
        // do nothing
    }

    @Override
    public void onViewRecycled(FragmentViewHolder holder) {
        Fragment f = mFragmentManager.findFragmentByTag(createFragmentTag(holder.getAdapterPosition()) + "");
        if (f != null) {
            if (mCurTransaction == null) {
                mCurTransaction = mFragmentManager.beginTransaction();
            }
            mStates.put(tag, mFragmentManager.saveFragmentInstanceState(f));
            mCurTransaction.remove(f);
            mCurTransaction.commitAllowingStateLoss();
            mCurTransaction = null;
            mFragmentManager.executePendingTransactions();
        }
        if (holder.itemView instanceof ViewGroup) {
            ((ViewGroup) holder.itemView).removeAllViews();
        }
        super.onViewRecycled(holder);
    }

    protected int createFragmentTag(int position) {
        long itemId = getItemId(position);
        if (itemId == RecyclerView.NO_ID) {
            return position + 1;
        } else {
            return (int) itemId;
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager == null) {
            throw new NullPointerException("CanGridLayoutManager of recyclerView must be initalized.");
        }
        if (layoutManager instanceof LinearLayoutManager == false) {
            throw new NullPointerException("The type of layoutManager must be LinearLayoutManager.");
        }
        mAttachedView = recyclerView;
        initGlobalFocusChangeListener();
        mAttachedView.getViewTreeObserver().addOnGlobalFocusChangeListener(mGlobalFocusChangeListener);
        initScrollListener();
        mAttachedView.addOnScrollListener(mScrollListener);
    }

    private void initScrollListener() {
        if(mScrollListener != null){
            return;
        }
        mScrollListener = new RecyclerView.OnScrollListener() {

            private boolean callbackFlag = false;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if(newState == RecyclerView.SCROLL_STATE_SETTLING){
                    callbackFlag = false;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int newPosi = mCurrPage;
                if(dy > 0){
                    newPosi = ((LinearLayoutManager) mAttachedView.getLayoutManager()).findLastVisibleItemPosition();
                } else {
                    newPosi = ((LinearLayoutManager) mAttachedView.getLayoutManager()).findFirstVisibleItemPosition();
                }
                if(callbackFlag == false && newPosi != mCurrPage){
                    callbackFlag = true;
                    int oldPosi = mCurrPage;
                    mCurrPage = newPosi;
                    if(mPageChangeListener != null){
                        mPageChangeListener.onChanged(oldPosi, newPosi);
                    }
                }
            }
        };
    }

    private void initGlobalFocusChangeListener() {
        if (mGlobalFocusChangeListener != null) {
            return;
        }
        mGlobalFocusChangeListener = new ViewTreeObserver.OnGlobalFocusChangeListener() {
            @Override
            public void onGlobalFocusChanged(View oldFocus, View newFocus) {
                View containingItemView = mAttachedView.findContainingItemView(newFocus);
                if (containingItemView != null) {
                    mAttachedView.smoothScrollToPosition(mAttachedView.getChildAdapterPosition(containingItemView));
                }
            }
        };
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        mAttachedView.getViewTreeObserver().removeOnGlobalFocusChangeListener(mGlobalFocusChangeListener);
        mAttachedView.removeOnScrollListener(mScrollListener);
        mAttachedView = null;
        super.onDetachedFromRecyclerView(recyclerView);
    }

    private boolean hasAttachedToView() {
        return mAttachedView != null;
    }

    public void setOnPageChangeListener(OnPageChangeListener pageChangeListener) {
        mPageChangeListener = pageChangeListener;
    }

    public void selectPage(int position) {
        selectPage(position, true);
    }

    public void selectPage(int position, boolean scrollTo) {
        if(!hasAttachedToView() || position < 0 || position >= getItemCount()){
            Log.w(TAG, "Failed to selectPage.[position is illegal]");
            return;
        }
        if(scrollTo){
            mAttachedView.smoothScrollToPosition(position);
        } else {
            mAttachedView.scrollToPosition(position);
        }
    }

    public int getCurrentPage() {
        return mCurrPage;
    }

    public abstract Fragment getItem(int position, Fragment.SavedState savedState);

    public abstract void onDestroyItem(int position, Fragment fragment);

    public class FragmentViewHolder extends RecyclerView.ViewHolder implements View.OnAttachStateChangeListener {

        public FragmentViewHolder(View itemView) {
            super(itemView);
            itemView.addOnAttachStateChangeListener(this);
        }

        @Override
        public void onViewAttachedToWindow(View v) {
            if (mCurTransaction == null) {
                mCurTransaction = mFragmentManager.beginTransaction();
            }
            final int tagId = createFragmentTag(getLayoutPosition());
            final Fragment fragmentInAdapter = getItem(getLayoutPosition(), mStates.get(tagId));
            if (fragmentInAdapter != null) {
                mCurTransaction.replace(itemView.getId(), fragmentInAdapter, tagId + "");
                mCurTransaction.commitAllowingStateLoss();
                mCurTransaction = null;
                mFragmentManager.executePendingTransactions();
            }
        }

        @Override
        public void onViewDetachedFromWindow(View v) {
            final int tagId = createFragmentTag(getLayoutPosition());
            Fragment fragment = mFragmentManager.findFragmentByTag(tagId + "");
            if (fragment == null) {
                return;
            }
            if (mCurTransaction == null) {
                mCurTransaction = mFragmentManager.beginTransaction();
            }
            mStates.put(tagId, mFragmentManager.saveFragmentInstanceState(fragment));
            mCurTransaction.remove(fragment);
            mCurTransaction.commitAllowingStateLoss();
            mCurTransaction = null;
            mFragmentManager.executePendingTransactions();
            onDestroyItem(getLayoutPosition(), fragment);
        }
    }
}
