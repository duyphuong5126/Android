package com.horical.appnote.MyView.MyAdapter;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import com.horical.appnote.R;
import com.horical.appnote.MyView.Item.BaseItem;
import com.horical.appnote.MyView.Item.ItemNormal;
import com.horical.appnote.MyView.Item.ItemNormalWithNotification;

/**
 * Created by Phuong on 30/07/2015.
 */
public class SideMenuAdapter extends ArrayAdapter<BaseItem> {
    private ArrayList<BaseItem> mArrDataSource;
    private Activity mActivity;
    public SideMenuAdapter(Activity activity, int resource, ArrayList<BaseItem> objects) {
        super(activity, resource, objects);
        this.mActivity = activity;
        this.mArrDataSource = new ArrayList<>();
        mArrDataSource = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = mActivity.getLayoutInflater();
        convertView = inflater.inflate(mArrDataSource.get(position).getXmlLayout(), null);
        switch (mArrDataSource.get(position).hashCode()){

            case BaseItem.SideMenu_Normal_Item:
                ItemNormal normalItem = (ItemNormal) mArrDataSource.get(position);
                ((TextView) convertView.findViewById(R.id.ItemTitleName)).setText(normalItem.getItemName());
                if (normalItem.isFocused()) {
                    ((ImageView) convertView.findViewById(R.id.MainImage)).setImageBitmap(normalItem.getBitmapIllustrateImageFocus());
                    ((LinearLayout) convertView.findViewById(R.id.isFocus)).setBackgroundColor(Color.parseColor("#075c52"));
                    ((TextView) convertView.findViewById(R.id.ItemTitleName)).setTextColor(Color.parseColor("#075c52"));
                } else {
                    ((ImageView) convertView.findViewById(R.id.MainImage)).setImageBitmap(normalItem.getIllustrateImage());
                    ((LinearLayout) convertView.findViewById(R.id.isFocus)).setBackgroundColor(Color.parseColor("#ffffff"));
                    ((TextView) convertView.findViewById(R.id.ItemTitleName)).setTextColor(Color.parseColor("#000000"));
                }
                break;

            case BaseItem.SideMenu_Notification_Item:
                ItemNormalWithNotification notificationItem = (ItemNormalWithNotification) mArrDataSource.get(position);
                ((TextView) convertView.findViewById(R.id.ItemTitleName)).setText(notificationItem.getItemName());
                TextView notification = (TextView) convertView.findViewById(R.id.NotifycationTextView);
                if (notificationItem.getNotification() == null || notificationItem.getNotification().equals("")) {
                    notification.setVisibility(View.GONE);
                } else {
                    notification.setVisibility(View.VISIBLE);
                    notification.setBackgroundResource(notificationItem.getNotificationBackground());
                    notification.setText(notificationItem.getNotification());
                }
                if (notificationItem.isFocused()) {
                    ((ImageView) convertView.findViewById(R.id.MainImage)).setImageBitmap(notificationItem.getBitmapIllustrateImageFocus());
                    ((LinearLayout) convertView.findViewById(R.id.isFocus)).setBackgroundColor(Color.parseColor("#075c52"));
                    ((TextView) convertView.findViewById(R.id.ItemTitleName)).setTextColor(Color.parseColor("#075c52"));
                } else {
                    ((ImageView) convertView.findViewById(R.id.MainImage)).setImageBitmap(notificationItem.getIllustrateImage());
                    ((LinearLayout) convertView.findViewById(R.id.isFocus)).setBackgroundColor(Color.parseColor("#00000000"));
                    ((TextView) convertView.findViewById(R.id.ItemTitleName)).setTextColor(Color.parseColor("#000000"));
                }
                break;

            default:
                break;
        }
        return convertView;
    }

    public String getStringAt(int position){
        return mArrDataSource.get(position).getNameOfItem();
    }
}
