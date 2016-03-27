package duy.phuong.handnote.Fragment;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import duy.phuong.handnote.Listener.MainListener;
import duy.phuong.handnote.MyView.DrawingView.FingerDrawerView;

/**
 * Created by Phuong on 23/11/2015.
 */
public abstract class BaseFragment extends Fragment implements FingerDrawerView.GetDisplayListener{
    public static final String DRAWING_FRAGMENT = "DrawingFragment";
    public static final String MAIN_FRAGMENT = "MainFragment";
    public static final String LEARNING_FRAGMENT = "LearningFragment";
    public static final String CREATE_NOTE_FRAGMENT = "CreateNoteFragment";
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

    @Override
    public DisplayMetrics getScreenResolution() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        if (displayMetrics.heightPixels <= 480) {
            mActivity.finish();
        }
        return displayMetrics;
    }
}
