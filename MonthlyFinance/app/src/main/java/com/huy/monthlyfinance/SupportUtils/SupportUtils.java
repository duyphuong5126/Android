package com.huy.monthlyfinance.SupportUtils;

import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Created by Phuong on 25/08/2016.
 */
public class SupportUtils {
    private static final boolean IS_LOLLIPOP = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;

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

    public static boolean checkLollipopOrAbove() {
        return IS_LOLLIPOP;
    }
}
