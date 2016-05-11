package duy.phuong.handnote.MyView;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import duy.phuong.handnote.DTO.SideMenuItem;
import duy.phuong.handnote.R;

/**
 * Created by Phuong on 10/05/2016.
 */
public class SideMenuAdapter extends BaseAdapter {
    private ArrayList<SideMenuItem> mItems;
    private Activity mActivity;
    private int mResLayout;

    public SideMenuAdapter(ArrayList<SideMenuItem> Items, Activity activity, int resLayout) {
        mItems = Items;
        mActivity = activity; mResLayout = resLayout;
    }
    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SideMenuItem item = mItems.get(position);
        LayoutInflater inflater = mActivity.getLayoutInflater();
        convertView = inflater.inflate(mResLayout, parent, false);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.itemIcon);
        imageView.setImageResource((item.mFocused) ? item.mIconFocused : item.mIcon);
        TextView textView = (TextView) convertView.findViewById(R.id.itemTitle);
        textView.setText(item.mTitle); textView.setTextColor((item.mFocused) ? Color.parseColor("#FFCC0033") : Color.BLACK);
        LinearLayout layoutFocused = (LinearLayout) convertView.findViewById(R.id.checkFocused);
        layoutFocused.setVisibility(item.mFocused ? View.VISIBLE : View.GONE);
        ImageView imgChevron = (ImageView) convertView.findViewById(R.id.imgChevron);
        imgChevron.setVisibility(item.mFocused ? View.VISIBLE : View.GONE);
        return convertView;
    }
}
