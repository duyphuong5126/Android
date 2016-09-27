package com.huy.monthlyfinance.MyView;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huy.monthlyfinance.MyView.Item.RecyclerItem.BaseRecyclerItem;
import com.huy.monthlyfinance.MyView.RecylerHolder.BaseViewHolder;

import java.util.List;

/**
 * Created by Phuong on 24/09/2016.
 */
public class BasicRecyclerAdapter<E extends BaseRecyclerItem> extends RecyclerView.Adapter<BaseViewHolder> {
    private List<E> mList;
    private int mXML;

    public BasicRecyclerAdapter(List<E> List, int XML) {
        this.mList = List;
        this.mXML = XML;
    }


    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(mXML, parent, false);
        return new BaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.setUpView(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
