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
import android.widget.ListAdapter;
import android.widget.ListView;

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

    public void setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mActivity.getWindow().setStatusBarColor(mStatusBarColor);
        }
    }
    public static void setListViewHeight(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        int desiredWidth, resultHeight = 0;
        ViewGroup.LayoutParams params;
        View view = null;
        if (listAdapter == null) {
            return;
        }
        desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0) {
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            resultHeight += view.getMeasuredHeight();
        }
        params = listView.getLayoutParams();
        params.height = resultHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
}
