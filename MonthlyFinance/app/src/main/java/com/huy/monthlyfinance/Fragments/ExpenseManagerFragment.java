package com.huy.monthlyfinance.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
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
import com.huy.monthlyfinance.MyView.Item.ListItem.BoughtProduct;
import com.huy.monthlyfinance.MyView.Item.ListItem.ExpensesItem;
import com.huy.monthlyfinance.MyView.Item.ListItem.ProductDropdownItem;
import com.huy.monthlyfinance.MyView.Item.ListItem.RadialItem;
import com.huy.monthlyfinance.R;
import com.huy.monthlyfinance.SupportUtils.SupportUtils;
import com.kulik.radial.RadialListView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Phuong on 26/08/2016.
 */
public class ExpenseManagerFragment extends BaseFragment implements View.OnClickListener {
    private static final int SELECT_IMAGE = 1;

    private NavigationListener mNavListener;
    private FrameLayout mLayoutInput;
    private ScrollView mLayoutForm;
    private BasicAdapter<RadialItem> mListRadialAdapter;
    private BasicAdapter<ExpensesItem> mRadialAdapter;
    private ArrayList<RadialItem> mRadialItems;
    private RadialListView mListExpense;
    private TextView mTextRadialProduct;
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
    private FrameLayout mLayoutSelectDate;
    private float[] mMonthExpensePercentages;
    private ArrayList<String> mMonthExpense;
    private String[] mExpenses;
    private PieChart mPieChart;
    private int[] mPieChartColors;
    private ListView mListExpensesDetail;
    private ArrayList<ExpensesItem> mListExpenses;
    private int[] mExpenseImages;
    private Bitmap[] mExpenseBitmap;
    private int[] mExpenseDrawables;
    private int[] mExpenseProgressDrawables;
    private ArrayList<ProductDropdownItem> mListProductExample;
    private ArrayList<String> mListUnit;

    private Button mButtonAdd;
    private ArrayList<BoughtProduct> mListProducts;
    private BasicAdapter<BoughtProduct> mBoughtProductsAdapter;
    private ListView mListBoughtProducts;

    private EditText mEditProductName;
    private EditText mEditProductCost;
    private EditText mEditProductUnit;
    private EditText mEditProductAmount;
    private EditText mEditDate;
    private ImageView mImageProductIcon;
    private int mCurrentGroup;

    private TextView mTextTotalCost;
    private double mTotalCost;
    private String mConcurrency;
    private ProgressBar mCurrentPercentages;
    private double mCurrentCash;

    private TextView mTextGroupName;
    private ImageView mImageGroup;

    private CalendarView mLayoutPickDate;
    private String mDate;
    private Uri mCapturedImage;

    private Bitmap mProductBitmap;

    private boolean isFormOpen;

    @Override
    protected int getLayoutXML() {
        return R.layout.fragment_expense_management;
    }

    @Override
    protected void onPrepare() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            isFormOpen = bundle.getBoolean("isFormOpen");
        }
    }

    @Override
    protected void initUI(View view) {
        final Activity activity = getActivity();
        LayoutInflater inflater = activity.getLayoutInflater();

        mDate = SupportUtils.formatDate(SupportUtils.milliSec2Date(System.currentTimeMillis()), "dd/MM/yyyy");

        mCurrentGroup = 0;

        mConcurrency = "$";
        mTotalCost = 0;

        mCurrentPercentages = (ProgressBar) view.findViewById(R.id.itemProgress);
        mTextTotalCost = (TextView) view.findViewById(R.id.textTotalCost);
        mTextGroupName = (TextView) view.findViewById(R.id.itemName);
        mImageGroup = (ImageView) view.findViewById(R.id.itemIcon);
        mEditProductName = (EditText) view.findViewById(R.id.edtProductName);
        mEditProductName.setText("");
        mEditProductCost = (EditText) view.findViewById(R.id.edtProductCost);
        mEditProductCost.setText("");
        mEditProductUnit = (EditText) view.findViewById(R.id.edtProductUnit);
        mEditProductUnit.setText("");
        mEditProductAmount = (EditText) view.findViewById(R.id.edtProductAmount);
        mEditProductAmount.setText("");
        mEditDate = (EditText) view.findViewById(R.id.edtProductDate);
        mEditDate.setText(mDate);
        mImageProductIcon = (ImageView) view.findViewById(R.id.imageProductIcon);
        mTextRadialProduct = (TextView) view.findViewById(R.id.textGroupName);
        mLayoutPickDate = (CalendarView) view.findViewById(R.id.datePicker);
        mLayoutPickDate.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                mDate = i2 + "/" + (i1 + 1) + "/" + i;
                mEditDate.setText(mDate);
            }
        });

        mEditDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                toggleLayoutPickDate(true);
                return false;
            }
        });

        view.findViewById(R.id.buttonBack).setOnClickListener(this);
        view.findViewById(R.id.buttonLogo).setOnClickListener(this);
        view.findViewById(R.id.buttonCloseProducts).setOnClickListener(this);
        view.findViewById(R.id.buttonPrevGroup).setOnClickListener(this);
        view.findViewById(R.id.buttonNextGroup).setOnClickListener(this);
        view.findViewById(R.id.buttonCloseDate).setOnClickListener(this);
        view.findViewById(R.id.buttonLoadImage).setOnClickListener(this);
        view.findViewById(R.id.buttonConfirmExpenses).setOnClickListener(this);
        view.findViewById(R.id.buttonCancelExpenses).setOnClickListener(this);

        mButtonAdd = (Button) view.findViewById(R.id.buttonAdd);
        mListBoughtProducts = (ListView) view.findViewById(R.id.listBoughtProducts);

        mLayoutForm = (ScrollView) view.findViewById(R.id.layoutForm);
        mLayoutForm.setOnClickListener(this);

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
        if (mExpenseImages == null || mExpenseBitmap == null) {
            mExpenseImages = new int[]{R.mipmap.ic_bill_white_18dp, R.mipmap.ic_health_care_white_18dp, R.mipmap.ic_entertainment_white_18dp,
                    R.mipmap.ic_food_18dp, R.mipmap.ic_dressing_white_18dp, R.mipmap.ic_transport_white_18dp,
                    R.mipmap.ic_home_white_18dp, R.mipmap.ic_family_white_18dp, R.mipmap.ic_more_horiz_white_18dp};

            mExpenseBitmap = new Bitmap[mExpenseImages.length];
            for (int i = 0; i < mExpenseImages.length; i++) {
                mExpenseBitmap[i] = BitmapFactory.decodeResource(resources, mExpenseImages[i]);
            }
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
            RadialItem.OnClickListener listener = new RadialItem.OnClickListener() {
                @Override
                public void onClick(Bundle data) {
                    if (data != null) {
                        isFormOpen = data.getBoolean("isFormOpen");
                        mTextRadialProduct.setText(data.getString("itemSelected"));
                        mCurrentGroup = data.getInt("pos");
                    }
                    changeCurrentGroup();
                    if (isFormOpen) {
                        toggleForm(true);
                    } else {
                        Toast.makeText(activity, "Press again to access form", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onLongClick(Bundle data) {

                }
            };
            int index = 0;
            mRadialItems.add(new RadialItem(listener, mExpenses[0], BitmapFactory.decodeResource(resources, R.drawable.receipt), index++));
            mRadialItems.add(new RadialItem(listener, mExpenses[1], BitmapFactory.decodeResource(resources, R.drawable.stethoscope), index++));
            mRadialItems.add(new RadialItem(listener, mExpenses[2], BitmapFactory.decodeResource(resources, R.drawable.game_controller), index++));
            mRadialItems.add(new RadialItem(listener, mExpenses[3], BitmapFactory.decodeResource(resources, R.drawable.turkey), index++));
            mRadialItems.add(new RadialItem(listener, mExpenses[4], BitmapFactory.decodeResource(resources, R.drawable.shirt), index++));
            mRadialItems.add(new RadialItem(listener, mExpenses[5], BitmapFactory.decodeResource(resources, R.drawable.car), index++));
            mRadialItems.add(new RadialItem(listener, mExpenses[6], BitmapFactory.decodeResource(resources, R.drawable.home), index++));
            mRadialItems.add(new RadialItem(listener, mExpenses[7], BitmapFactory.decodeResource(resources, R.drawable.family), index++));
        }
        mListRadialAdapter =
                new BasicAdapter<>(mRadialItems, R.layout.item_radial, inflater);
        mListExpense.setAdapter(mListRadialAdapter);

        view.findViewById(R.id.buttonSelectProduct).setOnClickListener(this);
        view.findViewById(R.id.buttonSelectUnit).setOnClickListener(this);
        mLayoutSelectProduct = (FrameLayout) view.findViewById(R.id.layoutPickProduct);
        mLayoutSelectDate = (FrameLayout) view.findViewById(R.id.layoutPickDate);
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
        final BasicAdapter<ProductDropdownItem> mDropdownAdapter = new BasicAdapter<>(mListProductExample, R.layout.item_drop_down_1, inflater);
        mListProductExamples.setAdapter(mDropdownAdapter);
        mListProductExamples.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ProductDropdownItem item = mListProductExample.get(i);
                mEditProductName.setText(item.getName());
                mProductBitmap = item.getBitmap();
                mImageProductIcon.setImageBitmap(mProductBitmap);
                item.setFocused(!item.isFocused());
                mEditProductCost.setText("");
                mDropdownAdapter.notifyDataSetChanged();
            }
        });
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
    protected int getSideMenuColor() {
        return Color.parseColor("#5f7c89");
    }

    @Override
    protected void fragmentReady(Bundle savedInstanceState) {
        mCurrentCash = getCurrentCash();
        mCurrentPercentages.setMax((int) mCurrentCash);
        mCurrentPercentages.setProgress(0);
        toggleForm(isFormOpen);
        changeCurrentGroup();
        mListProducts = new ArrayList<>();
        mBoughtProductsAdapter = new BasicAdapter<>(mListProducts, R.layout.item_added_product, getActivity().getLayoutInflater());
        mListBoughtProducts.setAdapter(mBoughtProductsAdapter);
        mListBoughtProducts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                BoughtProduct product = mListProducts.get(i);
                mEditProductName.setText(product.getName());
                mProductBitmap = product.getImage();
                mImageProductIcon.setImageBitmap(mProductBitmap);
                mEditProductCost.setText(String.valueOf(product.getPrice()));
                mLayoutForm.scrollTo(0, 0);
            }
        });
        mButtonAdd.setOnClickListener(this);
    }

    public void setNavListener(NavigationListener NavListener) {
        this.mNavListener = NavListener;
    }

    private boolean canGoBack() {
        return mLayoutForm.getVisibility() == View.GONE;
    }

    private void toggleLayoutProducts(boolean visible) {
        mLayoutSelectProduct.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private boolean isLayoutProductVisible() {
        return mLayoutSelectProduct.getVisibility() == View.VISIBLE;
    }

    private void toggleLayoutPickDate(boolean visible) {
        mLayoutSelectDate.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private boolean isLayoutPickDateVisible() {
        return mLayoutSelectDate.getVisibility() == View.VISIBLE;
    }

    private void changeCurrentGroup() {
        mTextGroupName.setText(mRadialItems.get(mCurrentGroup).getText());
        mImageGroup.setImageBitmap(mExpenseBitmap[mCurrentGroup]);
    }

    private void toggleForm(boolean open) {
        mLayoutForm.setVisibility(open ? View.VISIBLE : View.GONE);
    }

    private void clearForm() {
        mEditDate.setText("");
        mEditProductCost.setText("");
        mEditProductName.setText("");
        mEditProductAmount.setText("");
        mEditProductUnit.setText("");
        mImageProductIcon.setImageResource(R.mipmap.ic_expense_green_1_24dp);
        mListProducts.clear();
        mBoughtProductsAdapter.notifyDataSetChanged();
        mCurrentCash = getCurrentCash();
        mTextTotalCost.setText("$0");
        mCurrentPercentages.setProgress(0);
        mCurrentGroup = 0;
        changeCurrentGroup();
    }

    private void storeData() {
        //mListProducts contains all bought products that are added on form. Store all of them into database
        //mTotalCost is the total of all bought products
        //mCurrentCash is the current cash that not include the mTotalCost. Do mCurrentCash -= mTotalCost and store it
    }

    private double getCurrentCash() {
        return 1000;
    }

    private void changePercentageProgressStyle() {
        Resources resources = getResources();
        double percent = mTotalCost / mCurrentCash;
        if (percent <= 0.25) {
            mCurrentPercentages.setProgressDrawable(resources.getDrawable(R.drawable.progress_style_1));
        } else if (percent <= 0.5) {
            mCurrentPercentages.setProgressDrawable(resources.getDrawable(R.drawable.progress_style_11));
        } else if ((percent <= 0.75)) {
            mCurrentPercentages.setProgressDrawable(resources.getDrawable(R.drawable.progress_style_4));
        } else {
            mCurrentPercentages.setProgressDrawable(resources.getDrawable(R.drawable.progress_style_10));
        }
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
                toggleLayoutProducts(!isLayoutProductVisible());
                break;
            case R.id.buttonCloseProducts:
                toggleLayoutProducts(false);
                break;
            case R.id.buttonCloseDate:
                toggleLayoutPickDate(false);
                break;
            case R.id.buttonPrevGroup:
                mCurrentGroup--;
                if (mCurrentGroup < 0) {
                    mCurrentGroup = 0;
                }
                changeCurrentGroup();
                break;
            case R.id.buttonLoadImage:
                intentLoadImage();
                break;
            case R.id.buttonNextGroup:
                mCurrentGroup++;
                if (mCurrentGroup >= mRadialItems.size()) {
                    mCurrentGroup = mRadialItems.size() - 1;
                }
                changeCurrentGroup();
                break;
            case R.id.buttonSelectUnit:
                if (mListUnit != null) {
                    if (!mListUnit.isEmpty()) {
                        mLayoutSelectUnit.setVisibility(mLayoutSelectUnit.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
                    }
                }
                break;
            case R.id.buttonAdd:
                String name = mEditProductName.getText().toString();
                String price = mEditProductCost.getText().toString();
                String unit = mEditProductUnit.getText().toString();
                Drawable drawable = mImageProductIcon.getDrawable();
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                double num_price = Double.valueOf(price.length() > 0 ? price : "0");
                String message = null;
                if (num_price > 0 && !name.isEmpty()) {
                    BoughtProduct product = new BoughtProduct(bitmap, name, unit, num_price, false);
                    boolean existed = false;
                    if (mTotalCost + num_price> mCurrentCash) {
                        message = "Current total exceeds your cash limit";
                    } else {
                        for (int i = 0; i < mListProducts.size() && !existed; i++) {
                            BoughtProduct boughtProduct = mListProducts.get(i);
                            existed = boughtProduct.equals(product);
                            if (existed) {
                                mTotalCost += num_price - boughtProduct.getPrice();
                                boughtProduct.setPrice(num_price);
                            }
                        }
                        if (existed) {
                            message = "This item is already existed";
                        } else {
                            mListProducts.add(product);
                            mTotalCost += product.getPrice();
                        }
                        mTextTotalCost.setText(mConcurrency + mTotalCost);
                        mBoughtProductsAdapter.notifyDataSetChanged();
                        mCurrentPercentages.setProgress((int) mTotalCost);
                        changePercentageProgressStyle();
                        SupportUtils.setListViewHeight(mListBoughtProducts);
                    }
                } else {
                    message = "You're missing some information";
                }
                if (message != null) {
                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.buttonConfirmExpenses:
                break;
            case R.id.buttonCancelExpenses:
                clearForm();
                break;
            default:
                break;
        }
    }

    private void intentLoadImage() {
        Intent intentPick = new Intent();
        intentPick.setType("image/*");
        intentPick.setAction(Intent.ACTION_GET_CONTENT);

        File root = new File(Environment.getExternalStorageDirectory() + File.separator + "Captured" + File.separator);
        if (!root.exists()) {
            root.mkdirs();
        }
        String name = "Captured_" + System.nanoTime();
        File dir = new File(root, name);
        mCapturedImage = Uri.fromFile(dir);

        Intent intentTakePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intentTakePhoto.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImage);

        Intent intentChooser = Intent.createChooser(intentPick, "Select a source");
        intentChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{intentTakePhoto});
        startActivityForResult(intentChooser, SELECT_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case SELECT_IMAGE:
                    boolean isFromCamera;
                    if (data == null) {
                        isFromCamera = true;
                    } else {
                        if (data.getAction() == null) {
                            isFromCamera = true;
                        } else {
                            isFromCamera = data.getAction().equals(MediaStore.ACTION_IMAGE_CAPTURE);
                        }
                    }

                    String path;
                    if (isFromCamera) {
                        path = SupportUtils.getPath(mCapturedImage, getActivity().getApplicationContext());
                    } else {
                        path = SupportUtils.getPath(data.getData(), getActivity().getApplicationContext());
                    }
                    if (path != null) {
                        mProductBitmap = BitmapFactory.decodeFile(path);
                        mImageProductIcon.setImageBitmap(mProductBitmap);
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
