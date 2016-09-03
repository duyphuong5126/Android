package com.huy.monthlyfinance.MyView;

/**
 * Created by Phuong on 02/09/2016.
 */
public class SideMenuItem {
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
}
