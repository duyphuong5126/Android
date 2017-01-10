package com.huy.monthlyfinance.Fragments;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huy.monthlyfinance.Listener.DataChangeListener;
import com.huy.monthlyfinance.Listener.MainListener;
import com.huy.monthlyfinance.Listener.NavigationListener;

/**
 * Created by Phuong on 25/08/2016.
 */
public abstract class BaseFragment extends Fragment implements DataChangeListener{
    protected NavigationListener mNavListener;
    protected MainListener mListener;
    private static String mCurrent;

    public static String getCurrent() {
        return mCurrent;
    }

    public static void setCurrent(String Current) {
        BaseFragment.mCurrent = Current;
    }

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
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mListener.toggleProgress(true);
            }

            @Override
            protected Void doInBackground(Void... voids) {
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                mListener.toggleProgress(false);
            }
        }.execute();
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

    public final void setListener(MainListener Listener) {
        this.mListener = Listener;
    }

    public final void setNavListener(NavigationListener mNavListener) {
        this.mNavListener = mNavListener;
    }

    protected abstract boolean canGoBack();
}
