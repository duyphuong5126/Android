package com.huy.monthlyfinance.MyView.Item;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.huy.monthlyfinance.MyView.BaseItem;
import com.huy.monthlyfinance.R;

/**
 * Created by Phuong on 05/08/2016.
 */
public class AccountItem extends BaseItem {
    private int mItemXML, mItemIcon, mMax, mProgress, mText1Color;
    private String mItemTitle, mIemText1, mItemText2, mItemTextProgress;
    private boolean mFocused;
    private boolean isShowDivider;

    public AccountItem(int ItemXML, int ItemIcon, int Max, int Progress, int Text1Color,
                       String ItemTitle, String IemText1, String ItemText2, String ItemTextProgress,
                       boolean Focused, boolean isShowDivider) {
        this.mItemXML = ItemXML;
        this.mItemIcon = ItemIcon;
        this.mMax = Max;
        this.mProgress = Progress;
        this.mText1Color = Text1Color;
        this.mItemTitle = ItemTitle;
        this.mIemText1 = IemText1;
        this.mItemText2 = ItemText2;
        this.mItemTextProgress = ItemTextProgress;
        this.mFocused = Focused;
        this.isShowDivider = isShowDivider;
    }

    public int getItemXML() {
        return mItemXML;
    }

    public int getItemIcon() {
        return mItemIcon;
    }

    public int getMax() {
        return mMax;
    }

    public int getProgress() {
        return mProgress;
    }

    public int getText1Color() {
        return mText1Color;
    }

    public String getItemTitle() {
        return mItemTitle;
    }

    public String getIemText1() {
        return mIemText1;
    }

    public String getItemText2() {
        return mItemText2;
    }

    public String getItemTextProgress() {
        return mItemTextProgress;
    }

    public boolean isFocused() {
        return mFocused;
    }

    public void setFocused(boolean Focused) {
        this.mFocused = Focused;
    }

    @Override
    public void setView(View view) {

        view.findViewById(R.id.layoutIcon).setBackgroundResource(getItemXML());

        ImageView itemIcon = (ImageView) view.findViewById(R.id.imageIcon);
        itemIcon.setImageResource(getItemIcon());

        TextView title = (TextView) view.findViewById(R.id.itemTitle);
        title.setText(getItemTitle());

        TextView text1 = (TextView) view.findViewById(R.id.itemText1);
        text1.setText(getIemText1());
        text1.setTextColor(getText1Color());

        TextView text2 = (TextView) view.findViewById(R.id.itemText2);
        text2.setText(getItemText2());

        TextView text3 = (TextView) view.findViewById(R.id.textProgress);
        text3.setText(getItemTextProgress());

        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.itemProgress);
        progressBar.setMax(getMax());
        progressBar.setProgress(getProgress());
        if (!isFocused()) {
            view.findViewById(R.id.layoutProgress).setVisibility(View.GONE);
        } else {
            view.findViewById(R.id.layoutProgress).setVisibility(View.VISIBLE);
        }
        if (!isShowDivider) {
            view.findViewById(R.id.layoutDivider).setVisibility(View.GONE);
        } else {
            view.findViewById(R.id.layoutDivider).setVisibility(View.VISIBLE);
        }
    }

    public void setShowDivider(boolean showDivider) {
        isShowDivider = showDivider;
    }
}
