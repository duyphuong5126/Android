package com.huy.monthlyfinance.Fragments;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ScrollView;
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
import com.huy.monthlyfinance.MyView.BasicRecyclerAdapter;
import com.huy.monthlyfinance.MyView.Item.ListItem.ExpensesItem;
import com.huy.monthlyfinance.MyView.Item.RecyclerItem.GroupProductItem;
import com.huy.monthlyfinance.MyView.Item.ListItem.RadialItem;
import com.huy.monthlyfinance.R;
import com.huy.monthlyfinance.SupportUtils.SupportUtils;
import com.kulik.radial.RadialListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Created by Phuong on 26/08/2016.
 */
public class ExpenseManagerFragment extends BaseFragment implements View.OnClickListener {
    private NavigationListener mNavListener;
    private FrameLayout mLayoutInput;
    private ScrollView mLayoutForm;
    private RecyclerView mListGroupProduct;
    private BasicAdapter<RadialItem> mListRadialAdapter;
    private BasicAdapter<ExpensesItem> mRadialAdapter;
    private ArrayList<RadialItem> mRadialItems;
    private RadialListView mListExpense;

    @Override
    protected int getLayoutXML() {
        return R.layout.fragment_expense_management;
    }

    @Override
    protected void initUI(View view) {
        final Activity activity = getActivity();
        LayoutInflater inflater = activity.getLayoutInflater();

        (view.findViewById(R.id.buttonBack)).setOnClickListener(this);
        (view.findViewById(R.id.buttonLogo)).setOnClickListener(this);

        mLayoutForm = (ScrollView) view.findViewById(R.id.layoutForm);
        mLayoutForm.setOnClickListener(this);
        mListGroupProduct = (RecyclerView) view.findViewById(R.id.listGroupProduct);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
        mListGroupProduct.setLayoutManager(linearLayoutManager);

        Resources resources = activity.getResources();
        mListExpense = (RadialListView) view.findViewById(R.id.listExpenses);
        mLayoutInput = (FrameLayout) view.findViewById(R.id.layoutInput);
        mLayoutInput.setOnClickListener(this);

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
        String[] expenses = {resources.getString(R.string.bill), resources.getString(R.string.health),
                resources.getString(R.string.entertainment), resources.getString(R.string.food),
                resources.getString(R.string.dress), resources.getString(R.string.transport),
                resources.getString(R.string.home), resources.getString(R.string.family), resources.getString(R.string.etc)};
        Collections.addAll(mMonthExpense, expenses);
        PieChart pieChart = (PieChart) view.findViewById(R.id.chartExpensesDetail);
        int[] colors = {Color.parseColor("#3f51b5"), Color.parseColor("#c51162"), Color.parseColor("#8cc152"),
                Color.parseColor("#ff6d00"), Color.parseColor("#f74848"), Color.parseColor("#1eb1fc"),
                Color.parseColor("#6a7f99"), Color.parseColor("#666666"), Color.parseColor("#94d4d4"), };
        addDataToChart(mMonthExpense, mMonthExpenseAmount, pieChart, "This month expenses", "Expenses", colors);

        ListView listView = (ListView) view.findViewById(R.id.listExpensesDetail);
        ArrayList<ExpensesItem> listExpenses = new ArrayList<>();
        final int[] images = {R.mipmap.ic_bill_white_18dp, R.mipmap.ic_health_care_white_18dp, R.mipmap.ic_entertainment_white_18dp,
                R.mipmap.ic_food_18dp, R.mipmap.ic_dressing_white_18dp, R.mipmap.ic_transport_white_18dp,
                R.mipmap.ic_home_white_18dp, R.mipmap.ic_family_white_18dp, R.mipmap.ic_more_horiz_white_18dp};

        int[] drawables = {R.drawable.circle_dark_blue, R.drawable.circle_dark_red, R.drawable.circle_light_green,
                R.drawable.circle_orange, R.drawable.circle_pink_1, R.drawable.circle_blue_1,
                R.drawable.circle_dark_gray_1, R.drawable.circle_dark_gray_2, R.drawable.circle_blue_2};

        int[] progressDrawables = {R.drawable.progress_style_2, R.drawable.progress_style_3, R.drawable.progress_style_1,
                R.drawable.progress_style_4, R.drawable.progress_style_5, R.drawable.progress_style_6,
                R.drawable.progress_style_7, R.drawable.progress_style_8, R.drawable.progress_style_9};
        writeFakeData(listExpenses, expenses, drawables, images, progressDrawables);

        mRadialAdapter = new BasicAdapter<>(listExpenses, R.layout.item_expense, inflater);
        listView.setAdapter(mRadialAdapter);
        SupportUtils.setListViewHeight(listView);

        mRadialItems = new ArrayList<>();
        mListRadialAdapter = null;
        RadialItem.OnClickListener listener = new RadialItem.OnClickListener() {
            @Override
            public void onClick(String data, int position) {
                Toast.makeText(activity, data + ". Hold to add " + data + " expense", Toast.LENGTH_SHORT).show();
                for (RadialItem radialItem : mRadialItems) {
                    radialItem.setFocused(mRadialItems.indexOf(radialItem) == position);
                }
                mListExpense.invalidate();
            }

            @Override
            public void onLongClick(String data, int position) {
                Toast.makeText(activity, "Add " + data + " expense", Toast.LENGTH_SHORT).show();
                mLayoutForm.setVisibility(View.VISIBLE);
                mLayoutInput.setVisibility(View.GONE);
                for (RadialItem radialItem : mRadialItems) {
                    radialItem.setFocused(mRadialItems.indexOf(radialItem) == position);
                }
            }
        };
        int index = 0;
        mRadialItems.add(new RadialItem(listener, expenses[0], BitmapFactory.decodeResource(resources, R.drawable.receipt), index++));
        mRadialItems.add(new RadialItem(listener, expenses[1], BitmapFactory.decodeResource(resources, R.drawable.stethoscope), index++));
        mRadialItems.add(new RadialItem(listener, expenses[2], BitmapFactory.decodeResource(resources, R.drawable.game_controller), index++));
        mRadialItems.add(new RadialItem(listener, expenses[3], BitmapFactory.decodeResource(resources, R.drawable.turkey), index++));
        mRadialItems.add(new RadialItem(listener, expenses[4], BitmapFactory.decodeResource(resources, R.drawable.shirt), index++));
        mRadialItems.add(new RadialItem(listener, expenses[5], BitmapFactory.decodeResource(resources, R.drawable.car), index++));
        mRadialItems.add(new RadialItem(listener, expenses[6], BitmapFactory.decodeResource(resources, R.drawable.home), index++));
        mRadialItems.add(new RadialItem(listener, expenses[7], BitmapFactory.decodeResource(resources, R.drawable.family), index++));
        mListRadialAdapter =
                new BasicAdapter<>(mRadialItems, R.layout.item_radial, inflater);
        mListExpense.setAdapter(mListRadialAdapter);
        mListExpense.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });
        mListExpense.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayList<GroupProductItem> listGroups = new ArrayList<>();
        for (int i = 0; i < expenses.length; i++) {
            listGroups.add(new GroupProductItem(BitmapFactory.decodeResource(resources, images[i]), expenses[i]));
        }
        BasicRecyclerAdapter<GroupProductItem> recyclerAdapter =
                new BasicRecyclerAdapter<>(listGroups, R.layout.item_group_product);
        mListGroupProduct.setAdapter(recyclerAdapter);
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

    private boolean canGoBack() {
        return mLayoutForm.getVisibility() == View.GONE;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonBack:
                if (canGoBack()) {
                    mNavListener.navBack();
                } else {
                    mLayoutForm.setVisibility(View.GONE);
                }
                break;
            case R.id.buttonLogo:
                mLayoutInput.setVisibility(View.VISIBLE);
                break;
            case R.id.layoutInput:
                mLayoutInput.setVisibility(View.GONE);
                break;
            case R.id.layoutForm:
                mLayoutForm.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }
}
