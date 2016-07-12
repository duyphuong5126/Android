package duy.phuong.musicsocialnetwork.Base;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import duy.phuong.musicsocialnetwork.Listener.MainListener;

/**
 * Created by Phuong on 11/07/2016.
 */
public abstract class BaseFragment extends Fragment {
    protected Activity mActivity;
    protected MainListener mListener;
    protected int mXML;
    protected int mStatusBarColor;
    public abstract String getName();

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(mXML, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setStatusBarColor();
    }

    public void setListener(MainListener Listener) {
        this.mListener = Listener;
    }

    protected void setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mActivity.getWindow().setStatusBarColor(mStatusBarColor);
        }
    }
}
