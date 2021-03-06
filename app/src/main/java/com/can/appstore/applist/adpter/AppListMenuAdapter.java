package com.can.appstore.applist.adpter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.can.appstore.R;
import com.can.appstore.entity.Topic;

import java.util.List;

import cn.can.tvlib.ui.view.recyclerview.CanRecyclerViewAdapter;

/**
 * Created by syl on 2016/10/18.
 */

public class AppListMenuAdapter extends CanRecyclerViewAdapter {

    public AppListMenuAdapter(List data) {
        super(data);
    }

    @Override
    protected RecyclerView.ViewHolder generateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_app_list_item, parent, false);
        inflate.setFocusable(true);
        return new ItemViewHolder(inflate);
    }

    @Override
    protected void bindContentData(Object mDatas, RecyclerView.ViewHolder holder, int position) {
        Topic info = (Topic) mDatas;
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        itemViewHolder.mTextView.setText(info.getName());
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView mTextView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView;
        }
    }
}
