package com.huy.monthlyfinance.MyView.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huy.monthlyfinance.MyView.SideMenuItem;
import com.huy.monthlyfinance.R;

import java.util.List;

/**
 * Created by Phuong on 26/08/2016.
 */
public class SideMenuAdapter extends BaseAdapter {
    private List<SideMenuItem> mListItems;
    private int mXML;
    private LayoutInflater mInflater;

    public SideMenuAdapter(List<SideMenuItem> ListItems, int xml, LayoutInflater inflater) {
        this.mListItems = ListItems;
        mXML = xml;
        mInflater = inflater;
    }

    @Override
    public int getCount() {
        return mListItems != null ? mListItems.size() : 0;
    }

    @Override
    public Object getItem(int i) {
        return mListItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        SideMenuItem item = mListItems.get(i);
        if (view == null) {
            view = mInflater.inflate(mXML, viewGroup, false);
        }
        ImageView imageIcon = (ImageView) view.findViewById(R.id.imageIcon);
        imageIcon.setImageResource(item.getImageIcon());
        TextView textName = (TextView) view.findViewById(R.id.textName);
        textName.setText(item.getTextName());
        TextView textNameFocused = (TextView) view.findViewById(R.id.textNameFocused);
        textNameFocused.setText(item.getTextName());
        LinearLayout layoutFocused = (LinearLayout) view.findViewById(R.id.layoutFocused);
        layoutFocused.setVisibility(item.isFocused() ? View.VISIBLE : View.GONE);
        LinearLayout layoutName = (LinearLayout) view.findViewById(R.id.layoutName);
        layoutName.setVisibility(item.isFocused() ? View.GONE : View.VISIBLE);
        LinearLayout layoutNameFocused = (LinearLayout) view.findViewById(R.id.layoutNameFocused);
        layoutNameFocused.setVisibility(item.isFocused() ? View.VISIBLE : View.GONE);
        return view;
    }
}
