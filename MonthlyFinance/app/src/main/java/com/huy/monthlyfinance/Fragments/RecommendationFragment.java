package com.huy.monthlyfinance.Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.huy.monthlyfinance.R;

/**
 * Created by Phuong on 15/12/2016.
 */

public class RecommendationFragment extends BaseFragment {
    @Override
    protected int getLayoutXML() {
        return R.layout.fragment_recommendation;
    }

    @Override
    protected void onPrepare() {

    }

    @Override
    protected void initUI(View view) {

    }

    @Override
    protected void setStatusBarColor() {
        mListener.setStatusBarColor(getSideMenuColor());
    }

    @Override
    protected int getSideMenuColor() {
        return Color.parseColor("#FFCC0033");
    }

    @Override
    protected void fragmentReady(Bundle savedInstanceState) {

    }

    @Override
    protected boolean canGoBack() {
        return false;
    }
}
