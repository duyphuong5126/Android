package com.huy.monthlyfinance.MyView.Item.ListItem;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huy.monthlyfinance.R;

/**
 * Created by Phuong on 02/09/2016.
 */
public class SideMenuItem extends BaseItem {
    private int mImageIcon;
    private String mTextName;
    private String mTextExtra;

    private boolean isFocused;

    public SideMenuItem(int ImageIcon, String TextName, String TextExtra) {
        this.mImageIcon = ImageIcon;
        this.mTextName = TextName;
        this.mTextExtra = TextExtra;
    }

    public int getImageIcon() {
        return mImageIcon;
    }

    public String getTextName() {
        return mTextName;
    }

    public String getTextExtra() {
        return mTextExtra;
    }

    public boolean isFocused() {
        return isFocused;
    }

    public void setFocused(boolean focused) {
        isFocused = focused;
    }

    @Override
    public void setView(View view) {
        ImageView imageIcon = (ImageView) view.findViewById(R.id.imageIcon);
        imageIcon.setImageResource(getImageIcon());
        TextView textName = (TextView) view.findViewById(R.id.textName);
        textName.setText(getTextName());
        TextView textNameFocused = (TextView) view.findViewById(R.id.textNameFocused);
        textNameFocused.setText(getTextName());
        LinearLayout layoutFocused = (LinearLayout) view.findViewById(R.id.layoutFocused);
        layoutFocused.setVisibility(isFocused() ? View.VISIBLE : View.GONE);
        LinearLayout layoutName = (LinearLayout) view.findViewById(R.id.layoutName);
        layoutName.setVisibility(isFocused() ? View.GONE : View.VISIBLE);
        LinearLayout layoutNameFocused = (LinearLayout) view.findViewById(R.id.layoutNameFocused);
        layoutNameFocused.setVisibility(isFocused() ? View.VISIBLE : View.GONE);
    }
}
