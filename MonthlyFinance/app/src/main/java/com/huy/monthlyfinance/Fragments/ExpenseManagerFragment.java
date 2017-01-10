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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.huy.monthlyfinance.Database.DAO.AccountDAO;
import com.huy.monthlyfinance.Database.DAO.ExpensesHistoryDAO;
import com.huy.monthlyfinance.Database.DAO.ProductDAO;
import com.huy.monthlyfinance.Database.DAO.ProductDetailDAO;
import com.huy.monthlyfinance.Database.DAO.ProductGroupDAO;
import com.huy.monthlyfinance.Database.DAO.UserDAO;
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
import com.huy.monthlyfinance.SupportUtils.NameValuePair;
import com.huy.monthlyfinance.SupportUtils.PreferencesUtils;
import com.huy.monthlyfinance.SupportUtils.SupportUtils;
import com.kulik.radial.RadialListView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Phuong on 26/08/2016.
 */
public class ExpenseManagerFragment extends BaseFragment implements View.OnClickListener {

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
    private ProgressBar mCurrentPercentages;
    private double mCurrentBudget;

    private TextView mTextGroupName;
    private ImageView mImageGroup;

    private CalendarView mLayoutPickDate;
    private String mDate;

    private Bitmap mProductBitmap;
    private String mProductImageName;

    private boolean isFormOpen;
    private BasicAdapter<ProductDropdownItem> mDropdownAdapter;

    private FrameLayout mLayoutPickImages;

    private ScrollView mLayoutExpensesStatistic;

    private FrameLayout mLayoutPickAccount;
    private TextView mTextAccount;
    private ArrayList<Account> mListAccount;
    private Account mSelectedAccount;

    private TextView mTextCurrentBalances;

    private ImageView mIconSelectCash, mIconSelectBank, mIconSelectCredit;
    private GridView mListImages;
    private HashMap<String, Float> mMapExpenses;
    private HashMap<NameValuePair<String, String>, Double> mMapExpenseDetails;

    @Override
    protected int getLayoutXML() {
        return R.layout.fragment_expense_management;
    }

    @Override
    protected void onPrepare() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mListener.toggleProgress(true);
            }

            @Override
            protected Void doInBackground(Void... voids) {
                mDate = SupportUtils.formatDate(SupportUtils.milliSec2Date(System.currentTimeMillis()), "dd/MM/yyyy");

                mCurrentGroup = 0;

                mTotalCost = 0;

                Bundle bundle = getArguments();
                if (bundle != null) {
                    isFormOpen = bundle.getBoolean("isFormOpen");
                }
                final Context context = mListener.getContext();
                final Resources resources = context.getResources();

                mListAccount = MainApplication.getInstance().getAccounts();
                if (mListAccount != null) {
                    if (!mListAccount.isEmpty()) {
                        mSelectedAccount = getAccount(SupportUtils.getStringLocalized(context, "en", R.string.cash));
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
                                Toast.makeText(context, resources.getString(R.string.press_again), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onLongClick(Bundle data) {

                        }
                    };
                    int index = 0;
                    MainApplication mainApplication = MainApplication.getInstance();
                    for (ProductGroup productGroup : productGroups) {
                        mRadialItems.add(new RadialItem(listener, SupportUtils.getCountryCode().toLowerCase().contains("us") ?
                                productGroup.getGroupNameEN() : productGroup.getGroupNameVI(),
                                mainApplication.getRadialBitmap(productGroup.getGroupImage()), index++));
                    }
                }
                ExpensesHistoryDAO.getInstance(getActivity()).getListTransactions();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                mListener.toggleProgress(false);
            }
        }.execute();

    }

    @Override
    protected void initUI(View view) {
        final Activity activity = getActivity();

        mTextCurrentBalances = (TextView) view.findViewById(R.id.textCurrentBalances);
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
        mIconSelectCash = (ImageView) view.findViewById(R.id.iconSelectCash);
        mIconSelectBank = (ImageView) view.findViewById(R.id.iconSelectBank);
        mIconSelectCredit = (ImageView) view.findViewById(R.id.iconSelectCredit);
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
        mButtonAdd.setOnClickListener(this);
        mListBoughtProducts = (ListView) view.findViewById(R.id.listBoughtProducts);

        mLayoutForm = (ScrollView) view.findViewById(R.id.layoutForm);
        mLayoutForm.setOnClickListener(this);

        mListExpense = (RadialListView) view.findViewById(R.id.listExpenses);
        mLayoutInput = (FrameLayout) view.findViewById(R.id.layoutInput);
        mLayoutInput.setOnClickListener(this);

        mBarChart = (BarChart) view.findViewById(R.id.chartExpenses);
        mPieChart = (PieChart) view.findViewById(R.id.chartExpensesDetail);

        mListExpensesDetail = (ListView) view.findViewById(R.id.listExpensesDetail);

        view.findViewById(R.id.buttonSelectProduct).setOnClickListener(this);
        view.findViewById(R.id.buttonSelectUnit).setOnClickListener(this);
        mLayoutSelectProduct = (FrameLayout) view.findViewById(R.id.layoutPickProduct);
        mLayoutSelectDate = (FrameLayout) view.findViewById(R.id.layoutPickDate);
        mLayoutSelectUnit = (FrameLayout) view.findViewById(R.id.layoutPickUnit);
        mListProductExamples = (ListView) view.findViewById(R.id.listProducts);
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
        mListImages = (GridView) view.findViewById(R.id.gridImages);
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
        final Activity activity = getActivity();
        final LayoutInflater inflater = activity.getLayoutInflater();
        final Resources resources = getResources();
        mCurrentBudget = getCurrentCash();
        mCurrentPercentages.setMax((int) mCurrentBudget);
        mCurrentPercentages.setProgress(0);
        toggleForm(isFormOpen);
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mListener.toggleProgress(true);
            }

            @Override
            protected Void doInBackground(Void... voids) {
                mListProducts = new ArrayList<>();
                mBoughtProductsAdapter = new BasicAdapter<>(mListProducts, R.layout.item_added_product, getActivity().getLayoutInflater());
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
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
                                        Toast.makeText(activity, resources.getString(R.string.deleted), Toast.LENGTH_SHORT).show();
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
            }
        }.execute();

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                final ArrayList<ProductImageItem> imageItems = MainApplication.getInstance().getListProductsImage();
                BasicAdapter<ProductImageItem> mImagesAdapter = new BasicAdapter<>(imageItems, R.layout.item_image, inflater);
                mListImages.setAdapter(mImagesAdapter);
                mListImages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        mImageProductIcon.setImageBitmap(imageItems.get(i).getBitmap());
                        mProductImageName = imageItems.get(i).getDrawableName();
                    }
                });
            }
        }.execute();

        mLayoutExpensesStatistic.smoothScrollTo(0, (int) (mListExpensesDetail.getY() + SupportUtils.dip2Pixel(getActivity(), 20)));

        new AsyncTask<Void, Void, StringBuilder>() {
            @Override
            protected StringBuilder doInBackground(Void... voids) {
                StringBuilder builder = new StringBuilder();
                for (Account account : mListAccount) {
                    if (!account.getAccountName().contains(SupportUtils.getStringLocalized(getActivity(), "en", R.string.bank))) {
                        builder.append(account.getAccountName()).append(": ")
                                .append(SupportUtils.getNormalDoubleString((int) account.getCurrentBalance(), "#0,000"))
                                .append(" ");
                    }
                }
                return builder;
            }

            @Override
            protected void onPostExecute(StringBuilder stringBuilder) {
                super.onPostExecute(stringBuilder);
                mTextCurrentBalances.setText(stringBuilder.toString());
            }
        }.execute();

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
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

                if (mMonthExpense == null) {
                    mMonthExpense = new ArrayList<>();
                }
                if (mExpenses == null) {
                    mExpenses = new String[]{resources.getString(R.string.bill), resources.getString(R.string.health),
                            resources.getString(R.string.entertainment), resources.getString(R.string.food),
                            resources.getString(R.string.dress), resources.getString(R.string.transport),
                            resources.getString(R.string.home), resources.getString(R.string.family), resources.getString(R.string.etc)};
                }
                Collections.addAll(mMonthExpense, mExpenses);
                if (mPieChartColors == null) {
                    mPieChartColors = new int[]{Color.parseColor("#3f51b5"), Color.parseColor("#c51162"), Color.parseColor("#8cc152"),
                            Color.parseColor("#ff6d00"), Color.parseColor("#f74848"), Color.parseColor("#1eb1fc"),
                            Color.parseColor("#6a7f99"), Color.parseColor("#666666"), Color.parseColor("#94d4d4"),};
                }
                setUpExpenseChart();

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
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                mBarChart.setData(mBarData);
                mBarChart.setDescription("");
                mBarChart.setGridBackgroundColor(Color.parseColor("#5f7c89"));

                mBarChart.getLegend().setTextColor(Color.WHITE);
                mBarChart.animateY(1500);

                float[] mMonthExpenseAmount = new float[mMapExpenses.size()];
                ArrayList<String> mMonthExpense = new ArrayList<>(mMapExpenses.size());
                int index = 0;
                int total = 0;
                for (Map.Entry<String, Float> entry : mMapExpenses.entrySet()) {
                    mMonthExpense.add(entry.getKey());
                    mMonthExpenseAmount[index] = entry.getValue();
                    total += mMonthExpenseAmount[index];
                    index++;
                }
                String currency = PreferencesUtils.getString(PreferencesUtils.CURRENCY, "VND");
                String title = currency.toLowerCase().contains("vnd") ? (total + " VND") : (currency + " " + total);
                addDataToChart(mMonthExpense, mMonthExpenseAmount, mPieChart, "This month expenses", "Expenses", mPieChartColors,
                        resources.getString(R.string.total_expense_this_month) + ": \n" + title);

                changeCurrentGroup();
                if (mListExpenses.isEmpty()) {
                    writeExpenseDetailsData(mListExpenses, mExpenses, mExpenseDrawables, mExpenseImages, mExpenseProgressDrawables);
                }

                mRadialAdapter = new BasicAdapter<>(mListExpenses, R.layout.item_expense, inflater);
                mListExpensesDetail.setAdapter(mRadialAdapter);
                SupportUtils.setListViewHeight(mListExpensesDetail);

                mListRadialAdapter = null;
                mListRadialAdapter =
                        new BasicAdapter<>(mRadialItems, R.layout.item_radial, inflater);
                mListExpense.setAdapter(mListRadialAdapter);

                final StringBuilder textTotal = new StringBuilder();
                mTextTotalCost.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        textTotal.setLength(0);
                        textTotal.append(charSequence);
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        double totalCost = editable.toString().isEmpty() ? 0 : Double.valueOf(editable.toString());
                        if (totalCost > mCurrentBudget) {
                            Toast.makeText(activity, resources.getString(R.string.error_out_of_budget_limit), Toast.LENGTH_SHORT).show();
                            mTextTotalCost.removeTextChangedListener(this);
                            mTextTotalCost.setText("");
                            mTextTotalCost.setText(textTotal.toString());
                            mTextTotalCost.post(new Runnable() {
                                @Override
                                public void run() {
                                    mTextTotalCost.setSelection(mTextTotalCost.getText().toString().length());
                                }
                            });
                            mTextTotalCost.addTextChangedListener(this);
                        } else {
                            mTotalCost = totalCost;
                            mCurrentPercentages.setProgress((int) mTotalCost);
                            changePercentageProgressStyle();
                        }
                    }
                });

                final ArrayList<ProductDropdownItem> mListProductExample = MainApplication.getInstance().getListProductExample();
                mDropdownAdapter = new BasicAdapter<>(mListProductExample, R.layout.item_drop_down_1, inflater);
                mListProductExamples.setAdapter(mDropdownAdapter);
                mListProductExamples.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        ProductDropdownItem item = mListProductExample.get(i);
                        if (!item.isFocused()) {
                            mEditProductName.setText(item.getProduct().getProductNameEN());
                            mProductBitmap = item.getBitmap();
                            mImageProductIcon.setImageBitmap(mProductBitmap);
                            mProductImageName = item.getProduct().getProductImage();
                            mEditProductCost.setText("");
                        } else {
                            mEditProductName.setText("");
                            mImageProductIcon.setImageResource(R.mipmap.ic_expense_green_1_24dp);
                            mProductImageName = null;
                            mEditProductCost.setText("");
                        }
                        item.setFocused(!item.isFocused());
                        mBoughtProductsAdapter.notifyDataSetChanged();
                        scrollToView(mLayoutForm, mEditProductAmount);
                    }
                });

                mListener.toggleProgress(false);
            }
        }.execute();
    }

    @Override
    protected boolean canGoBack() {
        return mLayoutForm.getVisibility() == View.GONE;
    }

    private void setUpExpenseChart() {
        MainApplication application = MainApplication.getInstance();
        ArrayList<ProductGroup> productGroups = application.getProductGroups();
        ArrayList<ProductDetail> productDetails = application.getProductDetails();
        ArrayList<Product> products = application.getProducts();
        HashMap<String, Double> expenseByGroup = new HashMap<>();
        for (ProductGroup group : productGroups) {
            expenseByGroup.put(group.getProductGroupID(), 0.d);
        }
        for (Product product : products) {
            String groupId = product.getProductGroupID();
            String productId = product.getProductID();
            if (expenseByGroup.containsKey(groupId)) {
                double currentGroupExpense = expenseByGroup.get(product.getProductGroupID());
                for (ProductDetail productDetail : productDetails) {
                    if (productDetail.getProductID().equals(productId)) {
                        currentGroupExpense += productDetail.getProductCost();
                    }
                }
                expenseByGroup.put(groupId, currentGroupExpense);
            }
        }

        if (mMapExpenses == null) {
            mMapExpenses = new HashMap<>();
        }
        mMapExpenses.clear();
        if (mMapExpenseDetails == null) {
            mMapExpenseDetails = new HashMap<>();
        }
        mMapExpenseDetails.clear();
        for (Map.Entry<String, Double> entry : expenseByGroup.entrySet()) {
            String groupId = entry.getKey();
            double expense = entry.getValue();
            Log.d("Data", "group: " + groupId + ", expense: " + expense);
            String groupName = "";
            String country = SupportUtils.getCountryCode();
            for (int i = 0; i < productGroups.size() && groupName.isEmpty(); i++) {
                ProductGroup group = productGroups.get(i);
                if (group.getProductGroupID().equals(groupId)) {
                    groupName = country.toLowerCase().contains("us") ? group.getGroupNameEN() : group.getGroupNameVI();
                }
            }
            mMapExpenses.put(groupName, (float) expense);

            double maxInGroup = Double.MIN_VALUE;
            String productId = "";
            if (!productDetails.isEmpty()) {
                ArrayList<String> productIds = new ArrayList<>();
                for (Product product : products) {
                    if (product.getProductGroupID().equals(groupId)) {
                        productIds.add(product.getProductID());
                    }
                }
                if (!productIds.isEmpty()) {
                    for (ProductDetail productDetail : productDetails) {
                        if (productDetail.getProductCost() > maxInGroup && productIds.contains(productDetail.getProductID())) {
                            productId = productDetail.getProductID();
                            maxInGroup = productDetail.getProductCost();
                        }
                    }
                }
            }
            mMapExpenseDetails.put(new NameValuePair<>(groupId, productId), maxInGroup);
        }
    }

    private void writeExpenseDetailsData(ArrayList<ExpensesItem> listExpenses, String[] expenses,
                                         int[] drawables, int[] images, int[] progressDrawables) {
        Resources resources = MainApplication.getInstance().getResources();
        String mostSale = resources.getString(R.string.most_sale);
        if (drawables.length != images.length || drawables.length != expenses.length
                || mMapExpenseDetails == null || mMapExpenses == null) {
            return;
        }

        String country = SupportUtils.getCountryCode();
        HashMap<String, String> mapExpenseTitle = new HashMap<>();
        for (Map.Entry<NameValuePair<String, String>, Double> entry : mMapExpenseDetails.entrySet()) {
            NameValuePair<String, String> key = entry.getKey();
            for (ProductGroup productGroup : MainApplication.getInstance().getProductGroups()) {
                if (productGroup.getProductGroupID().equals(key.getKey())) {
                    mapExpenseTitle.put(key.getKey(),
                            country.toLowerCase().contains("us") ? productGroup.getGroupNameEN() : productGroup.getGroupNameVI());
                }
            }
        }

        ArrayList<Product> products = MainApplication.getInstance().getProducts();
        for (Map.Entry<NameValuePair<String, String>, Double> entry : mMapExpenseDetails.entrySet()) {
            NameValuePair<String, String> key = entry.getKey();
            String productName = "";
            for (int i = 0; i < products.size() && productName.isEmpty(); i++) {
                if (products.get(i).getProductID().equals(key.getValue())) {
                    productName = country.toLowerCase().contains("us") ?
                            products.get(i).getProductNameEN() : products.get(i).getProductNameVI();
                }
            }
            double current = entry.getValue() == Double.MIN_VALUE ? 0 : entry.getValue();
            double max = 0;
            String nameKey = mapExpenseTitle.get(key.getKey());
            if (mMapExpenses.containsKey(nameKey)) {
                max = mMapExpenses.get(nameKey);
            }
            int index = 0;
            for (int i = 0; i < expenses.length; i++) {
                if (expenses[i].toLowerCase().contains(mapExpenseTitle.get(key.getKey()).toLowerCase())) {
                    index = i;
                }
            }
            String currency = PreferencesUtils.getString(PreferencesUtils.CURRENCY, "vnd");
            String title = "";
            if (current > 0) {
                String textCurrent = SupportUtils.getNormalDoubleString(current, "#0,000");
                title = mostSale + ": " + productName + " " +
                        (currency.toLowerCase().contains("vnd") ? "(" + textCurrent + " vnđ)" : "($ " + textCurrent + ")");
            }
            String textMax = max > 0 ? SupportUtils.getNormalDoubleString(max, "#0,000") : "0";
            String maxTitle = currency.toLowerCase().contains("vnd") ? (textMax + " vnđ") : ("$ " + textMax);

            listExpenses.add(new ExpensesItem(getActivity(), mapExpenseTitle.get(key.getKey()), maxTitle, title, (int) max, (int) current,
                    images[index], drawables[index], progressDrawables[index]));
        }
    }

    private void addDataToChart(final ArrayList<String> xValues, final float[] yValuesData, PieChart chart,
                                final String textOnNothingSelected, String chartTitle, int[] colors, String centerText) {
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
        chart.setCenterText(centerText);
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
        mTextTotalCost.setText("0");
        mCurrentPercentages.setProgress(0);
        mCurrentGroup = 0;
        changeCurrentGroup();
        ArrayList<ProductDropdownItem> mListProductExample = MainApplication.getInstance().getListProductExample();
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
        double result = 0;
        for (Account account : mListAccount) {
            result += account.getCurrentBalance();
        }
        return result;
    }

    private void changePercentageProgressStyle() {
        Activity activity = getActivity();
        double percent = (double) mCurrentPercentages.getProgress() / mCurrentPercentages.getMax();
        if (percent <= 0.25) {
            mCurrentPercentages.setProgressDrawable(ContextCompat.getDrawable(activity, R.drawable.progress_style_1));
        } else if (percent <= 0.5) {
            mCurrentPercentages.setProgressDrawable(ContextCompat.getDrawable(activity, R.drawable.progress_style_11));
        } else if ((percent <= 0.75)) {
            mCurrentPercentages.setProgressDrawable(ContextCompat.getDrawable(activity, R.drawable.progress_style_4));
        } else {
            mCurrentPercentages.setProgressDrawable(ContextCompat.getDrawable(activity, R.drawable.progress_style_10));
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
                String message = null;
                ArrayList<ProductDropdownItem> productDropdownItems = new ArrayList<>();
                ArrayList<ProductDropdownItem> mListProductExample = MainApplication.getInstance().getListProductExample();
                for (ProductDropdownItem productDropdownItem : mListProductExample) {
                    if (productDropdownItem.isFocused()) {
                        BoughtProduct boughtProduct = new BoughtProduct(
                                productDropdownItem.getBitmap(), 0, false, productDropdownItem.getProduct());
                        if (!mListProducts.contains(boughtProduct)) {
                            productDropdownItems.add(productDropdownItem);
                        }
                    }
                }
                if (productDropdownItems.isEmpty()) {
                    String name = mEditProductName.getText().toString();
                    if (!name.isEmpty()) {
                        String nameEN = SupportUtils.getCountryCode().toLowerCase().contains("us") ? name : "";
                        String nameVI = SupportUtils.getCountryCode().toLowerCase().contains("vi") ? name : "";
                        String unit = mEditProductUnit.getText().toString();
                        String group = mTextGroupName.getText().toString();
                        int groupID = ProductGroupDAO.getInstance(activity).getGroupIDByName(group);
                        Drawable drawable = mImageProductIcon.getDrawable();
                        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

                        Product p = new Product(nameEN, nameVI, String.valueOf(groupID), unit, mProductImageName);

                        BoughtProduct boughtProduct = new BoughtProduct(bitmap, 0, false, p);
                        if (!mListProducts.contains(boughtProduct)) {
                            ProductDropdownItem item = new ProductDropdownItem(bitmap, p, false);
                            productDropdownItems.add(item);
                        }
                    } else {
                        message = "You're missing some information";
                    }
                    if (message != null) {
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    }
                }
                if (!productDropdownItems.isEmpty()) {
                    for (ProductDropdownItem item : productDropdownItems) {
                        Product p = item.getProduct();
                        if (!isProductExisted(p)) {
                            if (ProductDAO.getInstance(activity).doInsertTblProduct(p)) {
                                mListProductExample.add(item);
                                p.setProductID(String.valueOf(ProductDAO.getInstance(activity).getLatestProductId()));
                                MainApplication.getInstance().getProducts().add(p);
                            }
                        } else {
                            p.setProductID(getProductID(p.getProductNameEN(), p.getProductNameVI()));
                        }
                        BoughtProduct product = new BoughtProduct(item.getBitmap(), 0, false, p);
                        mListProducts.add(product);
                    }
                    mBoughtProductsAdapter.notifyDataSetChanged();
                    SupportUtils.setListViewHeight(mListBoughtProducts);
                }
                break;
            case R.id.buttonConfirmExpenses:
                message = "";
                if (!mListProducts.isEmpty()) {
                    String cost = mTextTotalCost.getText().toString();
                    double totalCost = cost.isEmpty() ? 0 : Double.valueOf(cost);
                    double cash = 0;
                    double credit = 0;
                    for (Account account : mListAccount) {
                        if (account.getAccountName().contains(
                                SupportUtils.getStringLocalized(activity, "en", R.string.cash)) ||
                                account.getAccountName().contains(
                                        SupportUtils.getStringLocalized(activity, "vi", R.string.cash))) {
                            cash = account.getCurrentBalance();
                        }
                        if (account.getAccountName().contains(
                                SupportUtils.getStringLocalized(activity, "en", R.string.credit_card)) ||
                                account.getAccountName().contains(
                                        SupportUtils.getStringLocalized(activity, "vi", R.string.credit_card))) {
                            credit = account.getCurrentBalance();
                        }
                    }
                    AccountDAO accountDAO = AccountDAO.getInstance(activity);
                    boolean enough = false;
                    if (cash > 0) {
                        double newCash = (cash > totalCost) ? cash - totalCost : 0;
                        double leftOver = (totalCost > cash) ? totalCost - cash : 0;
                        if (leftOver > 0) {
                            double newCredit = (credit > leftOver) ? credit - leftOver : 0;
                            leftOver = (leftOver > credit) ? leftOver - credit : 0;
                            if (leftOver > 0) {
                                message = "Your cash and credit balance is not enough." +
                                        " Please pay some money into them or transfer from bank";
                            } else {
                                message = "Your cash balance is not enough." +
                                        " We had to use your credit account";
                                accountDAO.updateAccount(SupportUtils.getStringLocalized(activity, "en", R.string.credit_card), newCredit);
                                enough = true;
                            }
                        } else {
                            message = "Your cash balance is up to date";
                            enough = true;
                        }
                        if (enough) {
                            accountDAO.updateAccount(SupportUtils.getStringLocalized(activity, "en", R.string.cash), newCash);
                        }
                    } else if (credit > 0) {
                        double newCredit = (credit > totalCost) ? credit - totalCost : 0;
                        double leftOver = (totalCost > credit) ? totalCost - credit : 0;
                        if (leftOver > 0) {
                            message = "Your cash and credit balance is not enough." +
                                    " Please pay some money into them or transfer from bank";
                        } else {
                            message = "Your cash balance is not enough." +
                                    " We had to use your credit account";
                            accountDAO.updateAccount(SupportUtils.getStringLocalized(activity, "en", R.string.credit_card), newCredit);
                            enough = true;
                        }
                    } else {
                        message = "Your cash and credit balance is not enough." +
                                " Please pay some money into them or transfer from bank";
                    }
                    Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
                    if (enough) {
                        ExpensesHistoryDAO expensesHistoryDAO = ExpensesHistoryDAO.getInstance(activity);
                        String date = mEditDate.getText().toString();
                        int userId = UserDAO.getInstance(activity).
                                getUserId(PreferencesUtils.getString(PreferencesUtils.CURRENT_EMAIL, ""));
                        if (!date.isEmpty() && userId >= 0) {
                            ExpensesHistory transaction =
                                    new ExpensesHistory(mSelectedAccount.getAccountID(), String.valueOf(userId), date, mTotalCost);
                            if (expensesHistoryDAO.insertTransaction(transaction)) {
                                message = "Transaction's saved";
                                int transactionId = expensesHistoryDAO.getLatestTransactionID();
                                if (transactionId > 0) {
                                    String id = String.valueOf(transactionId);
                                    ProductDetailDAO productDetailDAO = ProductDetailDAO.getInstance(activity);
                                    for (BoughtProduct boughtProduct : mListProducts) {
                                        productDetailDAO.insertProductDetail(
                                                new ProductDetail(boughtProduct.getData().getProductID(), id, boughtProduct.getPrice(), 0));
                                    }
                                }
                                mLayoutForm.setVisibility(View.GONE);
                                MainApplication.getInstance().refreshAllData();
                            } else {
                                message = "An error occur";
                            }
                        }
                    }
                }
                Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
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
                mIconSelectCash.setVisibility(View.GONE);
                mIconSelectCredit.setVisibility(View.GONE);
                mIconSelectBank.setVisibility(View.VISIBLE);
                break;
            case R.id.itemSelectCash:
                mSelectedAccount = getAccount(SupportUtils.getStringLocalized(activity, "en", R.string.cash));
                if (mSelectedAccount != null) {
                    mTextAccount.setText(mSelectedAccount.getAccountName());
                }
                mIconSelectCash.setVisibility(View.VISIBLE);
                mIconSelectCredit.setVisibility(View.GONE);
                mIconSelectBank.setVisibility(View.GONE);
                break;
            case R.id.itemSelectCredit:
                mSelectedAccount = getAccount(SupportUtils.getStringLocalized(activity, "en", R.string.credit_card));
                if (mSelectedAccount != null) {
                    mTextAccount.setText(mSelectedAccount.getAccountName());
                }
                mIconSelectCash.setVisibility(View.GONE);
                mIconSelectCredit.setVisibility(View.VISIBLE);
                mIconSelectBank.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    private void showListImage(boolean isShow) {

    }

    private String getProductID(String nameEN, String nameVI) {
        ArrayList<ProductDropdownItem> mListProductExample = MainApplication.getInstance().getListProductExample();
        for (ProductDropdownItem productDropdownItem : mListProductExample) {
            Product product = productDropdownItem.getProduct();
            if (product != null) {
                if (product.getProductNameEN().equals(nameEN) || product.getProductNameVI().equals(nameVI)) {
                    return product.getProductID();
                }
            }
        }
        return "";
    }

    private boolean isProductExisted(Product product) {
        if (product != null) {
            ArrayList<ProductDropdownItem> mListProductExample = MainApplication.getInstance().getListProductExample();
            if (!mListProductExample.isEmpty()) {
                for (ProductDropdownItem item : mListProductExample) {
                    Product p = item.getProduct();
                    boolean checkName = p.getProductNameEN().equals(product.getProductNameEN()) ||
                            p.getProductNameEN().equals(product.getProductNameVI()) ||
                            p.getProductNameVI().equals(product.getProductNameEN()) ||
                            p.getProductNameVI().equals(product.getProductNameVI());
                    boolean checkImage = p.getProductImage().equals(product.getProductImage());

                    if (checkName && checkImage) {
                        return true;
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

    @Override
    public void refreshData() {
        Resources resources = MainApplication.getInstance().getResources();
        setUpExpenseChart();
        float[] mMonthExpenseAmount = new float[mMapExpenses.size()];
        ArrayList<String> mMonthExpense = new ArrayList<>(mMapExpenses.size());
        int index = 0;
        int total = 0;
        for (Map.Entry<String, Float> entry : mMapExpenses.entrySet()) {
            mMonthExpense.add(entry.getKey());
            mMonthExpenseAmount[index] = entry.getValue();
            total += mMonthExpenseAmount[index];
            index++;
        }
        if (mPieChart != null) {
            mPieChart.clear();
            String currency = PreferencesUtils.getString(PreferencesUtils.CURRENCY, "VND");
            String title = currency.toLowerCase().contains("vnd") ? (total + " VND") : (currency + " " + total);
            addDataToChart(mMonthExpense, mMonthExpenseAmount, mPieChart, "This month expenses", "Expenses", mPieChartColors,
                    resources.getString(R.string.total_expense_this_month) + ": \n" + title);
        }

        Activity activity = getActivity();
        if (activity != null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            if (mListExpenses == null) {
                mListExpenses = new ArrayList<>();
                mRadialAdapter = new BasicAdapter<>(mListExpenses, R.layout.item_expense, inflater);
                mListExpensesDetail.setAdapter(mRadialAdapter);
            }
            mListExpenses.clear();
            writeExpenseDetailsData(mListExpenses, mExpenses, mExpenseDrawables, mExpenseImages, mExpenseProgressDrawables);
            mRadialAdapter.notifyDataSetChanged();
        }
    }
}