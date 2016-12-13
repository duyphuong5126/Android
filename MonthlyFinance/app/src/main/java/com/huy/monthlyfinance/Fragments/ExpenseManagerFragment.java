package com.huy.monthlyfinance.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
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
import com.huy.monthlyfinance.Database.DAO.ExpensesHistoryDAO;
import com.huy.monthlyfinance.Database.DAO.ProductDAO;
import com.huy.monthlyfinance.Database.DAO.ProductDetailDAO;
import com.huy.monthlyfinance.Database.DAO.ProductGroupDAO;
import com.huy.monthlyfinance.Database.DAO.UserDAO;
import com.huy.monthlyfinance.Listener.NavigationListener;
import com.huy.monthlyfinance.MainApplication;
import com.huy.monthlyfinance.Model.Account;
import com.huy.monthlyfinance.Model.ExpensesHistory;
import com.huy.monthlyfinance.Model.Product;
import com.huy.monthlyfinance.Model.ProductDetail;
import com.huy.monthlyfinance.Model.ProductGroup;
import com.huy.monthlyfinance.MyView.BasicAdapter;
import com.huy.monthlyfinance.MyView.Item.ListItem.BoughtProduct;
import com.huy.monthlyfinance.MyView.Item.ListItem.ExpensesItem;
import com.huy.monthlyfinance.MyView.Item.ListItem.ProductDropdownItem;
import com.huy.monthlyfinance.MyView.Item.ListItem.ProductImageItem;
import com.huy.monthlyfinance.MyView.Item.ListItem.RadialItem;
import com.huy.monthlyfinance.R;
import com.huy.monthlyfinance.SupportUtils.PreferencesUtils;
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
    private TextView mEditDate;
    private ImageView mImageProductIcon;
    private int mCurrentGroup;

    private EditText mTextTotalCost;
    private double mTotalCost;
    private String mConcurrency;
    private ProgressBar mCurrentPercentages;
    private double mCurrentBudget;

    private TextView mTextGroupName;
    private ImageView mImageGroup;

    private CalendarView mLayoutPickDate;
    private String mDate;
    private Uri mCapturedImage;

    private Bitmap mProductBitmap;
    private String mProductImageName;

    private boolean isFormOpen;
    private BasicAdapter<ProductDropdownItem> mDropdownAdapter;

    private ArrayList<ProductImageItem> mListProductsImage;
    private FrameLayout mLayoutPickImages;

    private ScrollView mLayoutExpensesStatistic;

    private FrameLayout mLayoutPickAccount;
    private TextView mTextAccount;
    private ArrayList<Account> mListAccount;
    private Account mSelectedAccount;

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
        final Context context = mListener.getContext();
        Resources resources = context.getResources();

        mListAccount = MainApplication.getInstance().getAccounts();
        if (mListAccount != null) {
            if (!mListAccount.isEmpty()) {
                mSelectedAccount = getAccount(SupportUtils.getStringLocalized(context, "en", R.string.cash));
            }
        }

        if (mListProductExample == null) {
            mListProductExample = new ArrayList<>();
        }
        if (mListProductExample.isEmpty()) {
            ArrayList<Product> products = MainApplication.getInstance().getProducts();
            if (!products.isEmpty()) {
                for (Product product : products) {
                    int resId = resources.getIdentifier(product.getProductImage(), "drawable", context.getPackageName());
                    mListProductExample.add(
                            new ProductDropdownItem(BitmapFactory.decodeResource(resources, resId), product, false));
                }
            }
        }

        if (mRadialItems == null) {
            mRadialItems = new ArrayList<>();
        }
        if (mRadialItems.isEmpty()) {
            ArrayList<ProductGroup> productGroups = new ArrayList<>();
            productGroups.addAll(MainApplication.getInstance().getProductGroups());
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
                        Toast.makeText(context, "Press again to access form", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onLongClick(Bundle data) {

                }
            };
            int index = 0;
            for (ProductGroup productGroup : productGroups) {
                int resId = resources.getIdentifier(productGroup.getGroupImage(), "drawable", context.getPackageName());
                mRadialItems.add(new RadialItem(listener, SupportUtils.getCountryCode().toLowerCase().contains("us") ?
                        productGroup.getGroupNameEN() : productGroup.getGroupNameVI(), BitmapFactory.decodeResource(resources, resId),
                        index++));
            }
        }
        ExpensesHistoryDAO.getInstance(getActivity()).getListTransactions();
    }

    @Override
    protected void initUI(View view) {
        final Activity activity = getActivity();
        LayoutInflater inflater = activity.getLayoutInflater();

        mDate = SupportUtils.formatDate(SupportUtils.milliSec2Date(System.currentTimeMillis()), "dd/MM/yyyy");

        mCurrentGroup = 0;

        mConcurrency = "$";
        mTotalCost = 0;

        mLayoutPickAccount = (FrameLayout) view.findViewById(R.id.layoutPickAccount);
        mTextAccount = (TextView) view.findViewById(R.id.txtAccount);
        mSelectedAccount = getAccount(SupportUtils.getStringLocalized(activity, "en", R.string.cash));
        if (mSelectedAccount != null) {
            mTextAccount.setText(mSelectedAccount.getAccountName());
        }
        mTextAccount.setOnClickListener(this);
        view.findViewById(R.id.buttonSelectAccount).setOnClickListener(this);
        view.findViewById(R.id.buttonCloseAccount).setOnClickListener(this);
        view.findViewById(R.id.itemSelectCash).setOnClickListener(this);
        view.findViewById(R.id.itemSelectBank).setOnClickListener(this);
        view.findViewById(R.id.itemSelectCredit).setOnClickListener(this);
        mLayoutExpensesStatistic = (ScrollView) view.findViewById(R.id.layoutExpensesStatistic);
        mLayoutPickImages = (FrameLayout) view.findViewById(R.id.layoutPickImage);
        mCurrentPercentages = (ProgressBar) view.findViewById(R.id.itemProgress);
        mTextTotalCost = (EditText) view.findViewById(R.id.textTotalCost);
        mTextTotalCost.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                double totalCost = editable.toString().isEmpty() ? 0 : Double.valueOf(editable.toString());
                if (totalCost > mCurrentBudget) {
                    Toast.makeText(activity, "Out of budget limit", Toast.LENGTH_SHORT).show();
                } else {
                    mTotalCost = totalCost;
                    mCurrentPercentages.setProgress((int) mTotalCost);
                }
            }
        });
        mTextGroupName = (TextView) view.findViewById(R.id.itemName);
        mImageGroup = (ImageView) view.findViewById(R.id.itemIcon);
        mEditProductName = (EditText) view.findViewById(R.id.edtProductName);
        mEditProductName.setText("");
        mEditProductCost = (EditText) view.findViewById(R.id.edtProductCost);
        mEditProductCost.setText("");
        mEditProductUnit = (EditText) view.findViewById(R.id.edtProductUnit);
        mEditProductUnit.setText("");
        mEditProductUnit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    scrollToView(mLayoutForm, mEditProductAmount);
                }
            }
        });
        mEditProductAmount = (EditText) view.findViewById(R.id.edtProductAmount);
        mEditProductAmount.setText("");
        mEditProductAmount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    scrollToView(mLayoutForm, mEditDate);
                }
            }
        });
        mEditDate = (TextView) view.findViewById(R.id.edtProductDate);
        mEditDate.setText(mDate);
        mEditDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    scrollToView(mLayoutForm, mListBoughtProducts);
                }
            }
        });
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
        view.findViewById(R.id.buttonCloseImages).setOnClickListener(this);

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

        mListRadialAdapter = null;
        mListRadialAdapter =
                new BasicAdapter<>(mRadialItems, R.layout.item_radial, inflater);
        mListExpense.setAdapter(mListRadialAdapter);

        view.findViewById(R.id.buttonSelectProduct).setOnClickListener(this);
        view.findViewById(R.id.buttonSelectUnit).setOnClickListener(this);
        mLayoutSelectProduct = (FrameLayout) view.findViewById(R.id.layoutPickProduct);
        mLayoutSelectDate = (FrameLayout) view.findViewById(R.id.layoutPickDate);
        mLayoutSelectUnit = (FrameLayout) view.findViewById(R.id.layoutPickUnit);
        mListProductExamples = (ListView) view.findViewById(R.id.listProducts);

        mDropdownAdapter = new BasicAdapter<>(mListProductExample, R.layout.item_drop_down_1, inflater);
        mListProductExamples.setAdapter(mDropdownAdapter);
        mListProductExamples.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ProductDropdownItem item = mListProductExample.get(i);
                mEditProductName.setText(item.getProduct().getProductNameEN());
                mProductBitmap = item.getBitmap();
                mImageProductIcon.setImageBitmap(mProductBitmap);
                mProductImageName = item.getProduct().getProductImage();
                item.setFocused(!item.isFocused());
                mEditProductCost.setText("");
                mDropdownAdapter.notifyDataSetChanged();
                scrollToView(mLayoutForm, mEditProductAmount);
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

        if (mListProductsImage == null) {
            mListProductsImage = new ArrayList<>();
        }
        if (mListProductsImage.isEmpty()) {
            int[] drawables = {R.drawable.bike, R.drawable.carousel, R.drawable.chest_of_drawers, R.drawable.dental_care,
                    R.drawable.desk, R.drawable.dog_eating, R.drawable.electricity, R.drawable.glasses,
                    R.drawable.milk, R.drawable.motorcycle, R.drawable.nurse, R.drawable.puzzle,
                    R.drawable.refrigerator, R.drawable.salad_1, R.drawable.sandwich, R.drawable.soccer_ball_variant,
                    R.drawable.socks, R.drawable.sunbed, R.drawable.tap, R.drawable.tea_cup, R.drawable.toilet_paper,
                    R.drawable.travel, R.drawable.underwear, R.drawable.vacuum_cleaner, R.drawable.washing_machine,
                    R.drawable.wifi, R.drawable.wine_glasses, R.drawable.wristwatch
            };
            for (int drawable : drawables) {
                mListProductsImage.add(new ProductImageItem(
                        BitmapFactory.decodeResource(resources, drawable),
                        resources.getResourceEntryName(drawable)
                ));
            }
        }
        BasicAdapter<ProductImageItem> mImagesAdapter = new BasicAdapter<>(mListProductsImage, R.layout.item_image, inflater);
        GridView mListImages = (GridView) view.findViewById(R.id.gridImages);
        mListImages.setAdapter(mImagesAdapter);
        mListImages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mImageProductIcon.setImageBitmap(mListProductsImage.get(i).getBitmap());
                mProductImageName = mListProductsImage.get(i).getDrawableName();
            }
        });
        mListImages.setOnTouchListener(new View.OnTouchListener() {
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
                return false;
            }
        });

        if (mListUnit == null) {
            mListUnit = new ArrayList<>();
        }
        if (mListUnit.isEmpty()) {

        }
        mListUnitExamples = (ListView) view.findViewById(R.id.listUnits);
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
        mCurrentBudget = getCurrentCash();
        mCurrentPercentages.setMax((int) mCurrentBudget);
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
                mProductImageName = product.getData().getProductImage();
                mImageProductIcon.setImageBitmap(mProductBitmap);
                mEditProductCost.setText(String.valueOf(product.getPrice()));
                mLayoutForm.scrollTo(0, 0);
            }
        });
        mListBoughtProducts.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int position = i;
                final Activity activity = getActivity();
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity)
                        .setTitle("Do you want to delete this item?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mListProducts.remove(position);
                                mBoughtProductsAdapter.notifyDataSetChanged();
                                SupportUtils.setListViewHeight(mListBoughtProducts);
                                Toast.makeText(activity, "Deleted", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                alertDialog.create().show();
                return false;
            }
        });
        mLayoutExpensesStatistic.smoothScrollTo(0, (int) (mListExpensesDetail.getY() + SupportUtils.dip2Pixel(getActivity(), 20)));
        mButtonAdd.setOnClickListener(this);
    }

    public void setNavListener(NavigationListener NavListener) {
        this.mNavListener = NavListener;
    }

    @Override
    protected boolean canGoBack() {
        return mLayoutForm.getVisibility() == View.GONE;
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

    private void toggleLayoutProducts(boolean visible) {
        mLayoutSelectProduct.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private boolean isLayoutProductVisible() {
        return mLayoutSelectProduct.getVisibility() == View.VISIBLE;
    }

    private void toggleLayoutImages(boolean visible) {
        mLayoutPickImages.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private boolean isLayoutImagesVisible() {
        return mLayoutPickImages.getVisibility() == View.VISIBLE;
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
        mCurrentBudget = getCurrentCash();
        mTextTotalCost.setText("$0");
        mCurrentPercentages.setProgress(0);
        mCurrentGroup = 0;
        changeCurrentGroup();
        for (ProductDropdownItem item : mListProductExample) {
            item.setFocused(false);
        }
        mDropdownAdapter.notifyDataSetChanged();
        mLayoutPickDate.setDate(System.currentTimeMillis());
    }

    private void storeData() {
        //mListProducts contains all bought products that are added on form. Store all of them into database
        //mTotalCost is the total of all bought products
        //mCurrentBudget is the current cash that not include the mTotalCost. Do mCurrentBudget -= mTotalCost and store it
    }

    private double getCurrentCash() {
        return 1000;
    }

    private void changePercentageProgressStyle() {
        Activity activity = getActivity();
        double percent = mTotalCost / mCurrentBudget;
        if (percent <= 0.25) {
            mCurrentPercentages.setProgressDrawable(ContextCompat.getDrawable(activity, R.drawable.progress_style_1));
        } else if (percent <= 0.5) {
            mCurrentPercentages.setProgressDrawable(ContextCompat.getDrawable(activity, R.drawable.progress_style_1));
        } else if ((percent <= 0.75)) {
            mCurrentPercentages.setProgressDrawable(ContextCompat.getDrawable(activity, R.drawable.progress_style_1));
        } else {
            mCurrentPercentages.setProgressDrawable(ContextCompat.getDrawable(activity, R.drawable.progress_style_1));
        }
    }

    private void scrollToView(final ScrollView scrollView, final View view) {
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.scrollTo(0, view.getTop());
            }
        });
    }

    @Override
    public void onClick(View view) {
        Activity activity = getActivity();
        switch (view.getId()) {
            case R.id.buttonBack:
                if (canGoBack()) {
                    mNavListener.navBack();
                } else {
                    mLayoutForm.setVisibility(View.GONE);
                }
                break;
            case R.id.buttonCloseImages:
                toggleLayoutImages(false);
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
                toggleLayoutImages(false);
                toggleLayoutProducts(true);
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
                toggleLayoutImages(true);
                toggleLayoutProducts(false);
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
                String nameEN = SupportUtils.getCountryCode().toLowerCase().contains("us") ? name : "";
                String nameVI = SupportUtils.getCountryCode().toLowerCase().contains("vi") ? name : "";
                String unit = mEditProductUnit.getText().toString();
                String group = mTextGroupName.getText().toString();
                int groupID = ProductGroupDAO.getInstance(activity).getGroupIDByName(group);
                Drawable drawable = mImageProductIcon.getDrawable();
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                String message = null;
                if (!name.isEmpty()) {
                    Product p = new Product(nameEN, nameVI, String.valueOf(groupID), unit, mProductImageName);
                    if (isProductExisted(p)) {
                        if (ProductDAO.getInstance(activity).doInsertTblProduct(p)) {
                            p.setProductGroupID(String.valueOf(ProductDAO.getInstance(activity).getLatestProductId()));
                            MainApplication.getInstance().getProducts().add(p);
                        }
                    }

                    BoughtProduct product = new BoughtProduct(bitmap, 0, false, p);
                    mListProducts.add(product);
                    mBoughtProductsAdapter.notifyDataSetChanged();
                    SupportUtils.setListViewHeight(mListBoughtProducts);
                } else {
                    message = "You're missing some information";
                }
                if (message != null) {
                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.buttonConfirmExpenses:
                if (!mListProducts.isEmpty()) {
                    ExpensesHistoryDAO expensesHistoryDAO = ExpensesHistoryDAO.getInstance(activity);
                    String date = mEditDate.getText().toString();
                    int userId = UserDAO.getInstance(activity).
                            getUserId(PreferencesUtils.getString(PreferencesUtils.CURRENT_EMAIL, ""));
                    if (!date.isEmpty() && userId >= 0) {
                        ExpensesHistory transaction =
                                new ExpensesHistory(mSelectedAccount.getAccountID(), String.valueOf(userId), date, mTotalCost);
                        if (expensesHistoryDAO.insertTransaction(transaction)) {
                            Toast.makeText(getActivity(), "Transaction's saved", Toast.LENGTH_SHORT).show();
                            int transactionId = expensesHistoryDAO.getLatestTransactionID();
                            if (transactionId > 0) {
                                String id = String.valueOf(transactionId);
                                ProductDetailDAO productDetailDAO = ProductDetailDAO.getInstance(activity);
                                for (BoughtProduct boughtProduct : mListProducts) {
                                    productDetailDAO.insertProductDetail(
                                            new ProductDetail(boughtProduct.getData().getProductID(), id, 0, 0));
                                }
                            }
                        } else {
                            Toast.makeText(getActivity(), "An error occur", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;
            case R.id.buttonCancelExpenses:
                clearForm();
                break;
            case R.id.buttonSelectAccount:
            case R.id.txtAccount:
                mLayoutPickAccount.setVisibility(mLayoutPickAccount.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
                break;
            case R.id.buttonCloseAccount:
                mLayoutPickAccount.setVisibility(View.GONE);
                break;
            case R.id.itemSelectBank:
                mSelectedAccount = getAccount(SupportUtils.getStringLocalized(activity, "en", R.string.bank));
                if (mSelectedAccount != null) {
                    mTextAccount.setText(mSelectedAccount.getAccountName());
                }
                break;
            case R.id.itemSelectCash:
                mSelectedAccount = getAccount(SupportUtils.getStringLocalized(activity, "en", R.string.cash));
                if (mSelectedAccount != null) {
                    mTextAccount.setText(mSelectedAccount.getAccountName());
                }
                break;
            case R.id.itemSelectCredit:
                mSelectedAccount = getAccount(SupportUtils.getStringLocalized(activity, "en", R.string.credit_card));
                if (mSelectedAccount != null) {
                    mTextAccount.setText(mSelectedAccount.getAccountName());
                }
                break;
            default:
                break;
        }
    }

    private void showListImage(boolean isShow) {

    }

    private void intentLoadImage() {
        Intent intentPick = new Intent();
        intentPick.setType("image/*");
        intentPick.setAction(Intent.ACTION_GET_CONTENT);

        File root = new File(Environment.getExternalStorageDirectory() + File.separator + "Captured" + File.separator);
        if (!root.exists()) {
            if (root.mkdirs()) {
                String name = "Captured_" + System.nanoTime();
                File dir = new File(root, name);
                mCapturedImage = Uri.fromFile(dir);

                Intent intentTakePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intentTakePhoto.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImage);

                Intent intentChooser = Intent.createChooser(intentPick, "Select a source");
                intentChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{intentTakePhoto});
                startActivityForResult(intentChooser, SELECT_IMAGE);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case SELECT_IMAGE:
                    boolean isFromCamera = data == null ? true : (data.getAction() == null ? true : data.getAction().equals(MediaStore.ACTION_IMAGE_CAPTURE));
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

    private int insertProduct(Product product) {
        int groupID = ProductGroupDAO.getInstance(getActivity()).getGroupIDByName(mTextGroupName.getText().toString());
        if (groupID >= 0) {
            ProductDAO productDAO = ProductDAO.getInstance(getActivity());
            if (productDAO.doInsertTblProduct(product)) {
                return productDAO.getLatestProductId();
            }
        }
        return -1;
    }

    private boolean isProductExisted(Product product) {
        if (product != null) {
            if (!mListProductExample.isEmpty()) {
                for (ProductDropdownItem item : mListProductExample) {
                    Product p = item.getProduct();
                    boolean checkName = p.getProductNameEN().equals(product.getProductNameEN()) ||
                            p.getProductNameEN().equals(product.getProductNameVI()) ||
                            p.getProductNameVI().equals(product.getProductNameEN()) ||
                            p.getProductNameVI().equals(product.getProductNameVI());

                    if (checkName) {
                        return true;
                    } else {
                        boolean checkImage = p.getProductImage().equals(product.getProductImage());
                        boolean checkUnit = p.getProductImage().equals(product.getProductImage());
                        if (checkImage && checkUnit) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private Account getAccount(String name) {
        for (int i = 0; i < mListAccount.size(); i++) {
            if (mListAccount.get(i).getAccountName().equals(name)) {
                return mListAccount.get(i);
            }
        }
        return null;
    }
}