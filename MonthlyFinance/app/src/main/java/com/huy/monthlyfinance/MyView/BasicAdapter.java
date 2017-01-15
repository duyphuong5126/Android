package com.huy.monthlyfinance.MyView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.huy.monthlyfinance.MyView.Item.ListItem.BaseItem;

import java.util.List;

/**
 * Created by Phuong on 03/09/2016.
 */
public class BasicAdapter<T extends BaseItem> extends BaseAdapter{
    private List<T> mList;
    private int mLayoutRes;
    private LayoutInflater mInflater;

    public BasicAdapter(List<T> List, int LayoutRes, LayoutInflater Inflater) {
        this.mList = List;
        this.mLayoutRes = LayoutRes;
        this.mInflater = Inflater;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public T getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = mInflater.inflate(mLayoutRes, viewGroup, false);
        }
        mList.get(i).setView(view);
        return view;
    }
}
