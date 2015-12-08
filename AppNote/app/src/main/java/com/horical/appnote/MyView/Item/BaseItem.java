package com.horical.appnote.MyView.Item;

/**
 * Created by Phuong on 30/07/2015.
 */
public abstract class BaseItem {
    public static final int SideMenu_Normal_Item = 1;
    public static final int SideMenu_Notification_Item = 2;

    protected int mXmlLayout;
    protected int mKindOfItem;
    protected String mNameOfItem;
    protected boolean mFocused;

    public boolean isFocused() {
        return mFocused;
    }

    public void setFocused(boolean Focused) {
        this.mFocused = Focused;
    }

    public String getNameOfItem() {
        return mNameOfItem;
    }

    public void setNameOfItem(String nameOfItem) {
        mNameOfItem = nameOfItem;
    }

    public int getKindOfItem() {
        return mKindOfItem;
    }

    public void setKindOfItem(int kindOfItem) {
        mKindOfItem = kindOfItem;
    }

    public int getXmlLayout() {
        return mXmlLayout;
    }

    public void setXmlLayout(int xmlLayout) {
        this.mXmlLayout = xmlLayout;
    }

    @Override
    public int hashCode() {
        return mKindOfItem;
    }
}
