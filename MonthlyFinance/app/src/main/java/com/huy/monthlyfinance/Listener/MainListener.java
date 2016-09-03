package com.huy.monthlyfinance.Listener;

/**
 * Created by Phuong on 25/08/2016.
 */
public interface MainListener {
    void toggleSideMenu(boolean isOpen);
    void setStatusBarColor(int color);
    void showFragment(Class c);
}
