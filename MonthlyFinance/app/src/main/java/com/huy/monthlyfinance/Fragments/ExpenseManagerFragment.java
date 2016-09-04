package com.huy.monthlyfinance.Fragments;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.huy.monthlyfinance.Listener.NavigationListener;
import com.huy.monthlyfinance.MyView.BasicAdapter;
import com.huy.monthlyfinance.MyView.Item.ExpensesItem;
import com.huy.monthlyfinance.R;
import com.huy.monthlyfinance.SupportUtils.SupportUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

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
        barDataSet.setValueTextColor(Color.WHITE);

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
        barChart.setDescription("");
        barChart.setGridBackgroundColor(Color.parseColor("#5f7c89"));

        ArrayList<Integer> listColors = new ArrayList<>();
        for (int color : ColorTemplate.COLORFUL_COLORS) {
            listColors.add(color);
        }
        for (int color : ColorTemplate.JOYFUL_COLORS) {
            listColors.add(color);
        }
        for (int color : ColorTemplate.LIBERTY_COLORS) {
            listColors.add(color);
        }
        for (int color : ColorTemplate.PASTEL_COLORS) {
            listColors.add(color);
        }
        for (int color : ColorTemplate.VORDIPLOM_COLORS) {
            listColors.add(color);
        }
        barDataSet.setColors(listColors);

        barChart.getLegend().setTextColor(Color.WHITE);
        barChart.animateY(1500);

        float[] mMonthExpenseAmount = {10.5f, 20f, 10f, 5.5f, 14f, 5f, 10f, 10f, 15f};
        ArrayList<String> mMonthExpense = new ArrayList<>();
        String[] expenses = {"Living services", "Health", "Entertainment",
                "Food", "Dress", "Transport", "House expenses", "Family", "Etc"};
        Collections.addAll(mMonthExpense, expenses);
        PieChart pieChart = (PieChart) view.findViewById(R.id.chartExpensesDetail);
        int[] colors = {Color.parseColor("#3f51b5"), Color.parseColor("#c51162"), Color.parseColor("#8cc152"),
                Color.parseColor("#ff6d00"), Color.parseColor("#f74848"), Color.parseColor("#1eb1fc"),
                Color.parseColor("#6a7f99"), Color.parseColor("#666666"), Color.parseColor("#94d4d4"), };
        addDataToChart(mMonthExpense, mMonthExpenseAmount, pieChart, "This month expenses", "Expenses", colors);

        ListView listView = (ListView) view.findViewById(R.id.listExpensesDetail);
        ArrayList<ExpensesItem> listExpenses = new ArrayList<>();
        int[] images = {R.mipmap.ic_bill_white_18dp, R.mipmap.ic_health_care_white_18dp, R.mipmap.ic_entertainment_white_18dp,
                R.mipmap.ic_food_18dp, R.mipmap.ic_dressing_white_18dp, R.mipmap.ic_transport_white_18dp,
                R.mipmap.ic_home_white_18dp, R.mipmap.ic_family_white_18dp, R.mipmap.ic_more_horiz_white_18dp};

        int[] drawables = {R.drawable.circle_dark_blue, R.drawable.circle_dark_red, R.drawable.circle_light_green,
                R.drawable.circle_orange, R.drawable.circle_pink_1, R.drawable.circle_blue_1,
                R.drawable.circle_dark_gray_1, R.drawable.circle_dark_gray_2, R.drawable.circle_blue_2};

        int[] progressDrawables = {R.drawable.progress_style_2, R.drawable.progress_style_3, R.drawable.progress_style_1,
                R.drawable.progress_style_4, R.drawable.progress_style_5, R.drawable.progress_style_6,
                R.drawable.progress_style_7, R.drawable.progress_style_8, R.drawable.progress_style_9};
        writeFakeData(listExpenses, expenses, drawables, images, progressDrawables);

        BasicAdapter<ExpensesItem> adapter = new BasicAdapter<>(listExpenses, R.layout.item_expense,
                getActivity().getLayoutInflater());
        listView.setAdapter(adapter);
        SupportUtils.setListViewHeight(listView);
    }
    private void writeFakeData(ArrayList<ExpensesItem> listExpenses, String[] expenses,
                               int[] drawables, int[] images, int[] progressDrawables) {
        if (drawables.length != images.length || drawables.length != expenses.length) {
            return;
        }
        Random random = new Random();
        for (int i = 0; i < expenses.length; i++) {
            int max = random.nextInt(950) + 50;
            int current = max - random.nextInt(max);
            listExpenses.add(new ExpensesItem(getActivity(), expenses[i] + "($ " + current + ")", "$ " + max, max, current,
                    images[i], drawables[i], progressDrawables[i]));
        }
    }
    private void addDataToChart(final ArrayList<String> xValues, final float[] yValuesData, PieChart chart,
                                final String textOnNothingSelected, String chartTitle, int[] colors) {
        chart.setUsePercentValues(true);
        chart.setDescription("");
        chart.setDrawHoleEnabled(true);
        chart.setHoleColorTransparent(true);
        chart.setHoleRadius(80);
        chart.setTransparentCircleRadius(10);
        chart.setRotationAngle(0);
        chart.setRotationEnabled(true);
        ArrayList<Entry> yValues = new ArrayList<>();
        for (int i = 0; i < yValuesData.length; i++) {
            yValues.add(new Entry(yValuesData[i], i));
        }

        PieDataSet pieDataSet = new PieDataSet(yValues, chartTitle);
        pieDataSet.setSliceSpace(3);
        pieDataSet.setSelectionShift(5);

        pieDataSet.setColors(colors);

        PieData pieData = new PieData(xValues, pieDataSet);
        pieData.setValueFormatter(new PercentFormatter());
        pieData.setDrawValues(false);

        chart.setData(pieData);
        chart.highlightValue(null);
        chart.getLegend().setEnabled(false);
        chart.setDrawSliceText(false);
        chart.setCenterText("Total expenses this month: 800USD");
        chart.invalidate();

        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                Toast.makeText(getActivity(), xValues.get(e.getXIndex()) + ": " + e.getVal(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected() {
                Toast.makeText(getActivity(), textOnNothingSelected, Toast.LENGTH_SHORT).show();
            }
        });
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
