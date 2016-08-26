package com.huy.monthlyfinance.MyView;

/**
 * Created by Phuong on 05/08/2016.
 */
public class AccountItem {
    private int mItemXML, mItemIcon, mMax, mProgress, mText1Color;
    private String mItemTitle, mIemText1, mItemText2, mItemTextProgress;
    private boolean mFocused;

    public AccountItem(int ItemXML, int ItemIcon, int Max, int Progress, int Text1Color,
                       String ItemTitle, String IemText1, String ItemText2, String ItemTextProgress,
                       boolean Focused) {
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
}
