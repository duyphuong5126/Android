package com.huy.monthlyfinance.MyView.RecylerHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.huy.monthlyfinance.MyView.Item.RecyclerItem.BaseRecyclerItem;

/**
 * Created by Phuong on 24/09/2016.
 */
public class BaseViewHolder extends RecyclerView.ViewHolder {
    private View mView;
    public BaseViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
    }
    public void setUpView(BaseRecyclerItem item) {
        item.setUpUI(mView);
    }
}