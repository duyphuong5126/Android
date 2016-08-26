package com.huy.monthlyfinance;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.FragmentManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.LinearLayout;

import com.huy.monthlyfinance.Fragments.BaseFragment;
import com.huy.monthlyfinance.Fragments.OverViewFragment;
import com.huy.monthlyfinance.Listener.MainListener;
import com.huy.monthlyfinance.SupportUtils.SupportUtils;

import java.util.TreeMap;

public class MainActivity extends Activity implements View.OnClickListener, MainListener {
    private DrawerLayout mDrawer;
    private LinearLayout mLayoutSideMenu;
    private TreeMap<String, BaseFragment> mFragments;
    private FragmentManager mManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDrawer = (DrawerLayout) findViewById(R.id.mainLayout);
        mLayoutSideMenu = (LinearLayout) findViewById(R.id.layoutSideMenu);

        mFragments = new TreeMap<>();
        OverViewFragment overViewFragment = new OverViewFragment();
        overViewFragment.setListener(this);
        this.addFragment(overViewFragment);

        mManager = getFragmentManager();
        mManager.beginTransaction().add(R.id.layoutFragmentsContainer, overViewFragment, overViewFragment.getClass().getName()).commit();
    }

    private void addFragment(BaseFragment fragment) {
        if (mFragments != null) {
            mFragments.put(fragment.getClass().getName(), fragment);
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void toggleSideMenu(boolean isOpen) {
        if (isOpen) {
            mDrawer.openDrawer(mLayoutSideMenu, true);
        } else {
            mDrawer.closeDrawer(mLayoutSideMenu, true);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void setStatusBarColor(int color) {
        if (SupportUtils.checkLollipopOrAbove()) {
            getWindow().setStatusBarColor(color);
        }
    }

    protected void showFragment(String fragmentName) {
        mManager.beginTransaction().replace(R.id.layoutFragmentsContainer, mFragments.get(fragmentName), fragmentName).commit();
    }
}
