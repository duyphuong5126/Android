package com.huy.monthlyfinance.Listener;

import android.content.Context;
import android.os.Bundle;

import java.util.ArrayList;

/**
 * Created by Phuong on 25/08/2016.
 */
public interface MainListener {
    void toggleSideMenu(boolean isOpen);
    void setStatusBarColor(int color);
    void showFragment(Class c, Bundle bundle);
    void changeSideMenuColor(int color);
    void toggleProgress(boolean isShow);

    ArrayList<Object> getData(Class c);
    Context getContext();
}
