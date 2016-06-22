package duy.phuong.handnote.Fragment;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import duy.phuong.handnote.Listener.MainListener;
import duy.phuong.handnote.MyView.DrawingView.FingerDrawerView;

/**
 * Created by Phuong on 23/11/2015.
 */
public abstract class BaseFragment extends Fragment implements FingerDrawerView.GetDisplayListener {
    public static final String DRAWING_FRAGMENT = "DrawingFragment";
    public static final String MAIN_FRAGMENT = "MainFragment";
    public static final String LEARNING_FRAGMENT = "LearningFragment";
    public static final String CREATE_NOTE_FRAGMENT = "CreateNoteFragment";
    public static final String VIEW_NOTE_FRAGMENT = "ViewNoteFragment";
    public static final String TEMPLATES_FRAGMENT = "TemplatesFragment";
    public static final String WEB_FRAGMENT = "WebFragment";
    public static final String TRANSLATE_FRAGMENT = "TranslateFragment";
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
        return displayMetrics;
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
