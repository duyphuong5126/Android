package com.huy.monthlyfinance.Fragments;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.huy.monthlyfinance.Listener.NavigationListener;
import com.huy.monthlyfinance.R;

import java.util.ArrayList;

/**
 * Created by Phuong on 26/08/2016.
 */
public class ExpenseManagerFragment extends BaseFragment implements View.OnClickListener {
    private NavigationListener mNavListener;
    @Override
    protected int getLayoutXML() {
        return R.layout.fragment_expense_management;
    }

    @Override
    protected void initUI(View view) {
        Context context = getActivity();
        ImageButton buttonBack = (ImageButton) view.findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(this);
        ArrayList<BarEntry> listBarEntries = new ArrayList<>();
        listBarEntries.add(new BarEntry(6f, 0));
        listBarEntries.add(new BarEntry(5f, 1));
        listBarEntries.add(new BarEntry(8f, 2));
        listBarEntries.add(new BarEntry(15f, 3));
        listBarEntries.add(new BarEntry(9f, 4));
        listBarEntries.add(new BarEntry(11f, 5));
        listBarEntries.add(new BarEntry(12f, 6));
        listBarEntries.add(new BarEntry(10f, 7));
        listBarEntries.add(new BarEntry(14f, 8));
        listBarEntries.add(new BarEntry(16f, 9));
        listBarEntries.add(new BarEntry(7f, 10));
        listBarEntries.add(new BarEntry(17f, 11));

        BarDataSet barDataSet = new BarDataSet(listBarEntries, "Your last 12 month expenses");

        ArrayList<String> listLabels = new ArrayList<>();
        listLabels.add("January");
        listLabels.add("February");
        listLabels.add("March");
        listLabels.add("April");
        listLabels.add("May");
        listLabels.add("June");
        listLabels.add("July");
        listLabels.add("August");
        listLabels.add("September");
        listLabels.add("October");
        listLabels.add("November");
        listLabels.add("December");

        BarData barData = new BarData(listLabels, barDataSet);
        BarChart barChart = (BarChart) view.findViewById(R.id.chartExpenses);
        barChart.setData(barData);
        barChart.setDescription("Monthly expense");
        barChart.setDrawGridBackground(false);
    }

    @Override
    protected void setStatusBarColor() {
        mListener.setStatusBarColor(Color.parseColor("#5f7c89"));
    }

    public void setNavListener(NavigationListener NavListener) {
        this.mNavListener = NavListener;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonBack:
                mNavListener.navBack();
                break;
            default:
                break;
        }
    }
}
