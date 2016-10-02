package com.huy.monthlyfinance.Fragments;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import com.huy.monthlyfinance.MyView.Item.ListItem.ProductDropdownItem;
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
    private ListView mListProductExamples;
    private ListView mListUnitExamples;
    private ArrayList<BarEntry> mListBarEntries;
    private BarDataSet mBarDataSet;
    private ArrayList<String> mListBarLabels;
    private BarData mBarData;
    private BarChart mBarChart;
    private ArrayList<Integer> mListBarColors;
    private FrameLayout mLayoutSelectProduct;
    private FrameLayout mLayoutSelectUnit;
    private float[] mMonthExpensePercentages;
    private ArrayList<String> mMonthExpense;
    private String[] mExpenses;
    private PieChart mPieChart;
    private int[] mPieChartColors;
    private ListView mListExpensesDetail;
    private ArrayList<ExpensesItem> mListExpenses;
    private int[] mExpenseImages;
    private int[] mExpenseDrawables;
    private int[] mExpenseProgressDrawables;
    private ArrayList<GroupProductItem> mListProductGroups;
    private ArrayList<ProductDropdownItem> mListProductExample;
    private ArrayList<String> mListUnit;

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

        if (mListBarEntries == null) {
            mListBarEntries = new ArrayList<>();
        }
        if (mListBarEntries.isEmpty()) {
            mListBarEntries.add(new BarEntry(6f, 0));
            mListBarEntries.add(new BarEntry(5f, 1));
            mListBarEntries.add(new BarEntry(8f, 2));
            mListBarEntries.add(new BarEntry(15f, 3));
            mListBarEntries.add(new BarEntry(9f, 4));
            mListBarEntries.add(new BarEntry(11f, 5));
            mListBarEntries.add(new BarEntry(12f, 6));
            mListBarEntries.add(new BarEntry(10f, 7));
            mListBarEntries.add(new BarEntry(14f, 8));
            mListBarEntries.add(new BarEntry(16f, 9));
            mListBarEntries.add(new BarEntry(7f, 10));
            mListBarEntries.add(new BarEntry(17f, 11));
        }

        mBarDataSet = new BarDataSet(mListBarEntries, "Your last 12 month expenses");
        mBarDataSet.setValueTextColor(Color.WHITE);

        if (mListBarLabels == null) {
            mListBarLabels = new ArrayList<>();
        }
        if (mListBarLabels.isEmpty()) {
            mListBarLabels.add("January");
            mListBarLabels.add("February");
            mListBarLabels.add("March");
            mListBarLabels.add("April");
            mListBarLabels.add("May");
            mListBarLabels.add("June");
            mListBarLabels.add("July");
            mListBarLabels.add("August");
            mListBarLabels.add("September");
            mListBarLabels.add("October");
            mListBarLabels.add("November");
            mListBarLabels.add("December");
        }

        mBarData = new BarData(mListBarLabels, mBarDataSet);

        mBarChart = (BarChart) view.findViewById(R.id.chartExpenses);
        mBarChart.setData(mBarData);
        mBarChart.setDescription("");
        mBarChart.setGridBackgroundColor(Color.parseColor("#5f7c89"));

        if (mListBarColors == null) {
            mListBarColors = new ArrayList<>();
        }
        if (mListBarColors.isEmpty()) {
            for (int color : ColorTemplate.COLORFUL_COLORS) {
                mListBarColors.add(color);
            }
            for (int color : ColorTemplate.JOYFUL_COLORS) {
                mListBarColors.add(color);
            }
            for (int color : ColorTemplate.LIBERTY_COLORS) {
                mListBarColors.add(color);
            }
            for (int color : ColorTemplate.PASTEL_COLORS) {
                mListBarColors.add(color);
            }
            for (int color : ColorTemplate.VORDIPLOM_COLORS) {
                mListBarColors.add(color);
            }
        }
        mBarDataSet.setColors(mListBarColors);

        mBarChart.getLegend().setTextColor(Color.WHITE);
        mBarChart.animateY(1500);

        if (mMonthExpensePercentages == null) {
            mMonthExpensePercentages = new float[]{10.5f, 20f, 10f, 5.5f, 14f, 5f, 10f, 10f, 15f};
        }
        if (mMonthExpense == null) {
            mMonthExpense = new ArrayList<>();
        }
        if (mMonthExpense.isEmpty()) {
            mExpenses = new String[]{resources.getString(R.string.bill), resources.getString(R.string.health),
                    resources.getString(R.string.entertainment), resources.getString(R.string.food),
                    resources.getString(R.string.dress), resources.getString(R.string.transport),
                    resources.getString(R.string.home), resources.getString(R.string.family), resources.getString(R.string.etc)};
        }
        Collections.addAll(mMonthExpense, mExpenses);
        mPieChart = (PieChart) view.findViewById(R.id.chartExpensesDetail);
        if (mPieChartColors == null) {
            mPieChartColors = new int[]{Color.parseColor("#3f51b5"), Color.parseColor("#c51162"), Color.parseColor("#8cc152"),
                    Color.parseColor("#ff6d00"), Color.parseColor("#f74848"), Color.parseColor("#1eb1fc"),
                    Color.parseColor("#6a7f99"), Color.parseColor("#666666"), Color.parseColor("#94d4d4"),};
        }
        addDataToChart(mMonthExpense, mMonthExpensePercentages, mPieChart, "This month expenses", "Expenses", mPieChartColors);

        mListExpensesDetail = (ListView) view.findViewById(R.id.listExpensesDetail);
        if (mListExpenses == null) {
            mListExpenses = new ArrayList<>();
        }
        if (mExpenseImages == null) {
            mExpenseImages = new int[]{R.mipmap.ic_bill_white_18dp, R.mipmap.ic_health_care_white_18dp, R.mipmap.ic_entertainment_white_18dp,
                    R.mipmap.ic_food_18dp, R.mipmap.ic_dressing_white_18dp, R.mipmap.ic_transport_white_18dp,
                    R.mipmap.ic_home_white_18dp, R.mipmap.ic_family_white_18dp, R.mipmap.ic_more_horiz_white_18dp};
        }

        if (mExpenseDrawables == null) {
            mExpenseDrawables = new int[]{R.drawable.circle_dark_blue, R.drawable.circle_dark_red, R.drawable.circle_light_green,
                    R.drawable.circle_orange, R.drawable.circle_pink_1, R.drawable.circle_blue_1,
                    R.drawable.circle_dark_gray_1, R.drawable.circle_dark_gray_2, R.drawable.circle_blue_2};
        }

        if (mExpenseProgressDrawables == null) {
            mExpenseProgressDrawables = new int[]{R.drawable.progress_style_2, R.drawable.progress_style_3, R.drawable.progress_style_1,
                    R.drawable.progress_style_4, R.drawable.progress_style_5, R.drawable.progress_style_6,
                    R.drawable.progress_style_7, R.drawable.progress_style_8, R.drawable.progress_style_9};
        }
        if (mListExpenses.isEmpty()) {
            writeFakeData(mListExpenses, mExpenses, mExpenseDrawables, mExpenseImages, mExpenseProgressDrawables);
        }

        mRadialAdapter = new BasicAdapter<>(mListExpenses, R.layout.item_expense, inflater);
        mListExpensesDetail.setAdapter(mRadialAdapter);
        SupportUtils.setListViewHeight(mListExpensesDetail);

        mRadialItems = new ArrayList<>();
        mListRadialAdapter = null;
        if (mRadialItems.isEmpty()) {
            mRadialItems.add(new RadialItem(mExpenses[0], BitmapFactory.decodeResource(resources, R.drawable.receipt)));
            mRadialItems.add(new RadialItem(mExpenses[1], BitmapFactory.decodeResource(resources, R.drawable.stethoscope)));
            mRadialItems.add(new RadialItem(mExpenses[2], BitmapFactory.decodeResource(resources, R.drawable.game_controller)));
            mRadialItems.add(new RadialItem(mExpenses[3], BitmapFactory.decodeResource(resources, R.drawable.turkey)));
            mRadialItems.add(new RadialItem(mExpenses[4], BitmapFactory.decodeResource(resources, R.drawable.shirt)));
            mRadialItems.add(new RadialItem(mExpenses[5], BitmapFactory.decodeResource(resources, R.drawable.car)));
            mRadialItems.add(new RadialItem(mExpenses[6], BitmapFactory.decodeResource(resources, R.drawable.home)));
            mRadialItems.add(new RadialItem(mExpenses[7], BitmapFactory.decodeResource(resources, R.drawable.family)));
        }
        mListRadialAdapter =
                new BasicAdapter<>(mRadialItems, R.layout.item_radial, inflater);
        mListExpense.setAdapter(mListRadialAdapter);
        mListExpense.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                for (int j = 0; j < mRadialItems.size(); j++) {
                    mRadialItems.get(j).setFocused(j == i);
                }
                mListRadialAdapter.notifyDataSetChanged();
            }
        });
        mListExpense.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                mLayoutForm.setVisibility(View.VISIBLE);
                return false;
            }
        });

        if (mListProductGroups == null) {
            mListProductGroups = new ArrayList<>();
        }
        if (mListProductGroups.isEmpty()) {
            for (int i = 0; i < mExpenses.length; i++) {
                mListProductGroups.add(new GroupProductItem(BitmapFactory.decodeResource(resources, mExpenseImages[i]), mExpenses[i]));
            }
        }
        BasicRecyclerAdapter<GroupProductItem> recyclerAdapter =
                new BasicRecyclerAdapter<>(mListProductGroups, R.layout.item_group_product);
        mListGroupProduct.setAdapter(recyclerAdapter);

        view.findViewById(R.id.buttonSelectProduct).setOnClickListener(this);
        view.findViewById(R.id.buttonSelectUnit).setOnClickListener(this);
        mLayoutSelectProduct = (FrameLayout) view.findViewById(R.id.layoutPickProduct);
        mLayoutSelectUnit = (FrameLayout) view.findViewById(R.id.layoutPickUnit);
        mListProductExamples = (ListView) view.findViewById(R.id.listProducts);
        if (mListProductExample == null) {
            mListProductExample = new ArrayList<>();
        }
        if (mListProductExample.isEmpty()) {
            mListProductExample.add(new ProductDropdownItem(BitmapFactory.decodeResource(resources, R.drawable.turkey), "Chicken"));
            mListProductExample.add(new ProductDropdownItem(BitmapFactory.decodeResource(resources, R.drawable.salad), "Vegetable"));
            mListProductExample.add(new ProductDropdownItem(BitmapFactory.decodeResource(resources, R.drawable.hamburguer), "Fast food"));
            mListProductExample.add(new ProductDropdownItem(BitmapFactory.decodeResource(resources, R.drawable.rice), "Rice"));
            mListProductExample.add(new ProductDropdownItem(BitmapFactory.decodeResource(resources, R.drawable.can), "Drink"));
            mListProductExample.add(new ProductDropdownItem(BitmapFactory.decodeResource(resources, R.drawable.shirt), "Shirt"));
            mListProductExample.add(new ProductDropdownItem(BitmapFactory.decodeResource(resources, R.drawable.shoe), "Shoes"));
            mListProductExample.add(new ProductDropdownItem(BitmapFactory.decodeResource(resources, R.drawable.dress), "Dress"));
            mListProductExample.add(new ProductDropdownItem(BitmapFactory.decodeResource(resources, R.drawable.jacket), "Jacket"));
            mListProductExample.add(new ProductDropdownItem(BitmapFactory.decodeResource(resources, R.drawable.copier), "Copier"));
            mListProductExample.add(new ProductDropdownItem(BitmapFactory.decodeResource(resources, R.drawable.pants), "Pants"));
            mListProductExample.add(new ProductDropdownItem(BitmapFactory.decodeResource(resources, R.drawable.bookshelf), "Book"));
            mListProductExample.add(new ProductDropdownItem(BitmapFactory.decodeResource(resources, R.drawable.writing_tool), "Office supplies"));
            mListProductExample.add(new ProductDropdownItem(BitmapFactory.decodeResource(resources, R.drawable.desktop_computer), "Desktop Computer"));
            mListProductExample.add(new ProductDropdownItem(BitmapFactory.decodeResource(resources, R.drawable.laptop), "Laptop"));
            mListProductExample.add(new ProductDropdownItem(BitmapFactory.decodeResource(resources, R.drawable.smartphone), "Mobile phone"));
            mListProductExample.add(new ProductDropdownItem(BitmapFactory.decodeResource(resources, R.drawable.smartwatch), "Watch"));
            mListProductExample.add(new ProductDropdownItem(BitmapFactory.decodeResource(resources, R.drawable.mouse), "Mouse"));
            mListProductExample.add(new ProductDropdownItem(BitmapFactory.decodeResource(resources, R.drawable.camera), "Camera"));
            mListProductExample.add(new ProductDropdownItem(BitmapFactory.decodeResource(resources, R.drawable.pendrive), "USB"));
            mListProductExample.add(new ProductDropdownItem(BitmapFactory.decodeResource(resources, R.drawable.headset), "Headset"));
            mListProductExample.add(new ProductDropdownItem(BitmapFactory.decodeResource(resources, R.drawable.desk_lamp), "Lamp"));
            mListProductExample.add(new ProductDropdownItem(BitmapFactory.decodeResource(resources, R.drawable.cooler), "Fan"));
            mListProductExample.add(new ProductDropdownItem(BitmapFactory.decodeResource(resources, R.drawable.television), "TV"));
            mListProductExample.add(new ProductDropdownItem(BitmapFactory.decodeResource(resources, R.drawable.gas_pipe), "Gas"));
            mListProductExample.add(new ProductDropdownItem(BitmapFactory.decodeResource(resources, R.drawable.gas_station), "Patrol"));
            mListProductExample.add(new ProductDropdownItem(BitmapFactory.decodeResource(resources, R.drawable.oil), "Oil"));
            mListProductExample.add(new ProductDropdownItem(BitmapFactory.decodeResource(resources, R.drawable.band_aid), "Band Aid"));
            mListProductExample.add(new ProductDropdownItem(BitmapFactory.decodeResource(resources, R.drawable.syringe), "Syringe"));
            mListProductExample.add(new ProductDropdownItem(BitmapFactory.decodeResource(resources, R.drawable.pills), "Drug"));
        }
        BasicAdapter<ProductDropdownItem> mDropdownAdapter = new BasicAdapter<>(mListProductExample, R.layout.item_drop_down_1, inflater);
        mListProductExamples.setAdapter(mDropdownAdapter);
        mListProductExamples.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        view.getParent().requestDisallowInterceptTouchEvent(true);
                        break;
                    case MotionEvent.ACTION_UP:
                        view.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                    default:
                        break;
                }
                view.onTouchEvent(motionEvent);
                return true;
            }
        });

        if (mListUnit == null) {
            mListUnit = new ArrayList<>();
        }
        if (mListUnit.isEmpty()) {

        }
        mListUnitExamples = (ListView) view.findViewById(R.id.listUnits);
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

    @Override
    protected void fragmentReady(Bundle savedInstanceState) {

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
            case R.id.buttonSelectProduct:
                mLayoutSelectProduct.setVisibility(mLayoutSelectProduct.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
                break;
            case R.id.buttonSelectUnit:
                if (mListUnit != null) {
                    if (!mListUnit.isEmpty()) {
                        mLayoutSelectUnit.setVisibility(mLayoutSelectUnit.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
                    }
                }
                break;
            default:
                break;
        }
    }
}
