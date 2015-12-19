package duy.phuong.handnote.Fragment;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import duy.phuong.handnote.Listener.MainListener;

/**
 * Created by Phuong on 23/11/2015.
 */
public abstract class BaseFragment extends Fragment {
    public static final String CREATE_TEXT_FRAGMENT = "CreateTextFragment";
    protected Activity mActivity;
    protected View mFragmentView;
    protected int mLayoutRes;
    protected MainListener mListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mActivity = getActivity();
        mFragmentView = inflater.inflate(mLayoutRes, container, false);
        return mFragmentView;
    }

    public void setListener(MainListener Listener) {
        this.mListener = Listener;
    }

    public abstract String fragmentIdentify();
}
