package com.huy.monthlyfinance.Fragments;

import android.graphics.Color;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.huy.monthlyfinance.MyView.AccountItem;
import com.huy.monthlyfinance.MyView.AccountItemAdapter;
import com.huy.monthlyfinance.R;
import com.huy.monthlyfinance.SupportUtils.SupportUtils;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Phuong on 25/08/2016.
 */
public class OverViewFragment extends BaseFragment implements View.OnClickListener{
    private FrameLayout mLayoutQuickAdd;
    private LinearLayout mLayoutQuickAddSelect;
    private Animation mAnimationForward, mAnimationBackward;

    @Override
    protected int getLayoutXML() {
        return R.layout.fragment_over_view;
    }

    @Override
    protected void initUI(View view) {
        ImageButton mButtonOpenSideMenu = (ImageButton) view.findViewById(R.id.buttonOpenSideMenu);
        mButtonOpenSideMenu.setOnClickListener(this);
        ImageButton mButtonQuickAdd = (ImageButton) view.findViewById(R.id.buttonQuickAdd);
        mButtonQuickAdd.setOnClickListener(this);
        mLayoutQuickAddSelect = (LinearLayout) view.findViewById(R.id.layoutQuickAddSelect);
        mLayoutQuickAddSelect.setOnClickListener(this);
        mLayoutQuickAdd = (FrameLayout) view.findViewById(R.id.layoutQuickAdd);
        float[] mMonthExpenseAmount = {40.5f, 20, 10, 5.5f, 24};
        String[] mMonthExpense = {"Food", "Bill", "Stuff", "Etc", "Dress"};
        float[] mMonthCashFlowAmount = {37.5f, 62.5f};
        String[] mMonthCashFlow = {"Income", "Expense"};
        PieChart mMonthlyExpenseChart = (PieChart) view.findViewById(R.id.chartMonthExpense);
        PieChart mMonthlyCashFlowChart = (PieChart) view.findViewById(R.id.chartMonthCashFlow);
        addDataToChart(new ArrayList<>(Arrays.asList(mMonthExpense)), mMonthExpenseAmount, mMonthlyExpenseChart,
                "This month expense chart", "Monthly Expenses");
        addDataToChart(new ArrayList<>(Arrays.asList(mMonthCashFlow)), mMonthCashFlowAmount, mMonthlyCashFlowChart
                , "This month cash flow chart", "Month Cash Flow");

        mAnimationForward = new RotateAnimation(0.0f, 45.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        mAnimationForward.setDuration(500);
        mAnimationForward.setRepeatCount(0);
        mAnimationForward.setRepeatMode(Animation.REVERSE);
        mAnimationForward.setFillAfter(true);
        mAnimationForward.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mLayoutQuickAddSelect.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mAnimationBackward = new RotateAnimation(45.0f, 0.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mAnimationBackward.setDuration(500);
        mAnimationBackward.setRepeatCount(0);
        mAnimationBackward.setRepeatMode(Animation.REVERSE);
        mAnimationBackward.setFillAfter(true);
        mAnimationBackward.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mLayoutQuickAddSelect.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        final ListView mListAccountItems = (ListView) view.findViewById(R.id.listAccountItems);
        final ArrayList<AccountItem> mAccountItems = new ArrayList<>();
        final AccountItemAdapter adapter = new AccountItemAdapter(mAccountItems, getActivity(), R.layout.item_account);
        mListAccountItems.setAdapter(adapter);
        mAccountItems.add(new AccountItem(
                R.drawable.circle_dark_blue, R.mipmap.ic_wallet_filled_money_tool_24dp, 100, 40, Color.parseColor("#88c03f"),
                "Cash", "$3000.05", "Initial Balance: $700", "Spent/ Budget: $50.00/ $700.00", false
        ));
        mAccountItems.add(new AccountItem(
                R.drawable.circle_orange, R.mipmap.ic_bank, 200, 30, Color.parseColor("#88c03f"),
                "Bank", "$1980.05", "Initial Balance: $2000", "Spent/ Budget: $50.00/ $700.00", false
        ));
        mAccountItems.add(new AccountItem(
                R.drawable.circle_dark_red, R.mipmap.ic_credit_cards_24dp, 100, 70, Color.parseColor("#f74848"),
                "Credit Card", "-$10.00", "Initial Balance: $100", "Spent/ Budget: $50.00/ $700.00", false
        ));
        /*mAccountItems.add(new AccountItem(
                R.drawable.circle_light_green_1, R.mipmap.ic_more_horiz_white_24dp, 100, 70, Color.parseColor("#88c03f"),
                "Non-USD Account", "$10.00", "1 Account", "Spent/ Budget: $50.00/ $700.00", false
        ));*/
        adapter.notifyDataSetChanged();
        SupportUtils.setListViewHeight(mListAccountItems);
        mListAccountItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mAccountItems.get(position).setFocused(!mAccountItems.get(position).isFocused());
                adapter.notifyDataSetChanged();
                SupportUtils.setListViewHeight(mListAccountItems);
            }
        });
    }

    @Override
    protected void setStatusBarColor() {
        mListener.setStatusBarColor(Color.parseColor("#3f51b5"));
    }

    private void addDataToChart(final ArrayList<String> xValues, final float[] yValuesData, PieChart chart,
                                final String textOnNothingSelected, String chartTitle) {
        chart.setUsePercentValues(true);
        chart.setDescription("");
        chart.setDrawHoleEnabled(true);
        chart.setHoleColorTransparent(true);
        chart.setHoleRadius(7);
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

        ArrayList<Integer> colors = new ArrayList<>();
        for (int c : ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(c);
        }
        for (int c : ColorTemplate.JOYFUL_COLORS) {
            colors.add(c);
        }
        for (int c : ColorTemplate.COLORFUL_COLORS) {
            colors.add(c);
        }
        for (int c : ColorTemplate.LIBERTY_COLORS) {
            colors.add(c);
        }
        for (int c : ColorTemplate.PASTEL_COLORS) {
            colors.add(c);
        }

        colors.add(ColorTemplate.getHoloBlue());
        pieDataSet.setColors(colors);

        PieData pieData = new PieData(xValues, pieDataSet);
        pieData.setValueFormatter(new PercentFormatter());
        pieData.setValueTextSize(11f);
        pieData.setValueTextColor(Color.BLACK);

        chart.setData(pieData);
        chart.highlightValue(null);
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonQuickAdd:
                mLayoutQuickAdd.startAnimation(mAnimationForward);
                break;
            case R.id.layoutQuickAddSelect:
                mLayoutQuickAdd.startAnimation(mAnimationBackward);
                break;
            case R.id.buttonOpenSideMenu:
                mListener.toggleSideMenu(true);
                break;
            default:
                break;
        }
    }
}
