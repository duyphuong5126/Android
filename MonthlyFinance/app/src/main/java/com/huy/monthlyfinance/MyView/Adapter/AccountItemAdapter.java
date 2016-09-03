package com.huy.monthlyfinance.MyView.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.huy.monthlyfinance.MyView.AccountItem;
import com.huy.monthlyfinance.R;

import java.util.List;

/**
 * Created by Phuong on 05/08/2016.
 */
public class AccountItemAdapter extends BaseAdapter {
    private List<AccountItem> mAccounts;
    private Activity mActivity;
    private int mXML;
    private LayoutInflater mInflater;

    public AccountItemAdapter(List<AccountItem> Accounts, Activity Activity, int XML) {
        this.mAccounts = Accounts;
        this.mActivity = Activity;
        this.mXML = XML;
        this.mInflater = mActivity.getLayoutInflater();
    }

    @Override
    public int getCount() {
        return (mAccounts != null) ? mAccounts.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return (mAccounts != null) ? mAccounts.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(mXML, parent, false);
        }
        AccountItem item = mAccounts.get(position);
        convertView.findViewById(R.id.layoutIcon).setBackgroundResource(item.getItemXML());

        ImageView itemIcon = (ImageView) convertView.findViewById(R.id.imageIcon);
        itemIcon.setImageResource(item.getItemIcon());

        TextView title = (TextView) convertView.findViewById(R.id.itemTitle);
        title.setText(item.getItemTitle());

        TextView text1 = (TextView) convertView.findViewById(R.id.itemText1);
        text1.setText(item.getIemText1());
        text1.setTextColor(item.getText1Color());

        TextView text2 = (TextView) convertView.findViewById(R.id.itemText2);
        text2.setText(item.getItemText2());

        TextView text3 = (TextView) convertView.findViewById(R.id.textProgress);
        text3.setText(item.getItemTextProgress());

        ProgressBar progressBar = (ProgressBar) convertView.findViewById(R.id.itemProgress);
        progressBar.setMax(item.getMax());
        progressBar.setProgress(item.getProgress());
        if (!item.isFocused()) {
            convertView.findViewById(R.id.layoutProgress).setVisibility(View.GONE);
        } else {
            convertView.findViewById(R.id.layoutProgress).setVisibility(View.VISIBLE);
        }
        if (position >= getCount() - 1) {
            convertView.findViewById(R.id.layoutDivider).setVisibility(View.GONE);
        } else {
            convertView.findViewById(R.id.layoutDivider).setVisibility(View.VISIBLE);
        }

        return convertView;
    }
}
