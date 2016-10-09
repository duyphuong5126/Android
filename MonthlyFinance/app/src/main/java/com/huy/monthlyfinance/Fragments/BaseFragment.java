package com.huy.monthlyfinance.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huy.monthlyfinance.Listener.MainListener;

/**
 * Created by Phuong on 25/08/2016.
 */
public abstract class BaseFragment extends Fragment {
    protected MainListener mListener;

    protected abstract int getLayoutXML();
    protected abstract void onPrepare();
    protected abstract void initUI(View view);
    protected abstract void setStatusBarColor();
    protected abstract int getSideMenuColor();
    protected abstract void fragmentReady(Bundle savedInstanceState);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        onPrepare();
        return inflater.inflate(getLayoutXML(), container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUI(view);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mListener != null) {
            setStatusBarColor();
            this.mListener.changeSideMenuColor(getSideMenuColor());
        }
        this.fragmentReady(savedInstanceState);
    }

    public void setListener(MainListener Listener) {
        this.mListener = Listener;
    }
}
