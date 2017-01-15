package com.huy.monthlyfinance.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.huy.monthlyfinance.Database.DAO.AccountDAO;
import com.huy.monthlyfinance.MainApplication;
import com.huy.monthlyfinance.Model.Account;
import com.huy.monthlyfinance.MyView.BasicAdapter;
import com.huy.monthlyfinance.MyView.Item.ListItem.TransferItem;
import com.huy.monthlyfinance.R;
import com.huy.monthlyfinance.SupportUtils.PreferencesUtils;
import com.huy.monthlyfinance.SupportUtils.SupportUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Phuong on 19/11/2016.
 */

public class BudgetFragment extends BaseFragment implements View.OnClickListener {
    private LinearLayout mLayoutAdd;
    private Animation mAnimationForward, mAnimationBackward;
    private Animation mAnimationRotateForward30, mAnimationRotateBackward30;
    private LinearLayout mLayoutQuickAdd;

    private ArrayList<TransferItem> mListTransfer;
    private BasicAdapter<TransferItem> mTransferAdapter;
    private ListView mListTransaction;

    private ScrollView mMainScroll;

    private LinearLayout mAddIncomeArea;
    private ScrollView mAddTransferArea;

    private String mCurrency;
    private EditText mTotalIncome;
    private double mIncome;

    private ProgressBar mProgressCash, mProgressBank, mProgressCredit;
    private EditText mEdtBank, mEdtCash, mEdtCredit;
    private TextView mTextBank, mTextCash, mTextCredit;
    private double mShareBank, mShareCash, mShareCredit;

    private boolean isAddIncome, isAddTransfer;

    private ArrayList<Account> mListAccounts;
    private double mTotalPayable;

    private TextView mTextCurrentBank, mTextInitBank, mTextCurrentCash, mTextInitCash, mTextCurrentCredit, mTextInitCredit, mTextTotalPayable;

    private PieChart mChartBudget;
    private TextView mTextSource, mTextTarget;
    private FrameLayout mLayoutSelectSource, mLayoutSelectTarget;
    private ProgressBar mProgressSource;
    private EditText mEdtSource;
    private TextView mTextSourcePercent;

    private static final String CASH = "CASH";
    private static final String BANK = "BANK";
    private static final String CREDIT = "CREDIT";

    private HashMap<String, String> mMapAccountSelector;

    private String mSourceTitle;
    private String mTargetTitle;
    private String mSource;
    private String mTarget;
    private double mMaxSource;
    private double mAmountSource;

    private ImageView mIconSelectBankSource, mIconSelectCashSource, mIconSelectCreditSource;
    private ImageView mIconSelectBankTarget, mIconSelectCashTarget, mIconSelectCreditTarget;

    @Override
    protected int getLayoutXML() {
        return R.layout.fragment_budget;
    }

    @Override
    protected void onPrepare() {
        final Resources resources = getActivity().getResources();

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mListener.toggleProgress(true);
            }

            @Override
            protected Void doInBackground(Void... voids) {

                mListTransfer = new ArrayList<>();
                /*mListTransfer.add(new TransferItem(100, "Cash", "Bank", 1000, 700, new Date(System.currentTimeMillis())));
                mListTransfer.add(new TransferItem(50, "Cash", "Bank", 900, 800, new Date(System.currentTimeMillis())));
                mListTransfer.add(new TransferItem(10, "Credit", "Cash", 900, 850, new Date(System.currentTimeMillis())));
                mListTransfer.add(new TransferItem(40, "Credit", "Cash", 890, 860, new Date(System.currentTimeMillis())));*/

                mCurrency = PreferencesUtils.getString(PreferencesUtils.CURRENCY, "VND");
                mIncome = mShareBank = mShareCash = mShareCredit = 0;
                Bundle bundle = getArguments();
                if (bundle != null) {
                    isAddIncome = bundle.getBoolean("isAddIncome");
                    isAddTransfer = bundle.getBoolean("isAddTransfer");
                }

                if (mListAccounts == null) {
                    mListAccounts = new ArrayList<>();
                }
                if (mListAccounts.isEmpty()) {
                    mListAccounts.addAll(MainApplication.getInstance().getAccounts());
                }
                mTotalPayable = 0;
                if (!mListAccounts.isEmpty()) {
                    for (Account account : mListAccounts) {
                        if (!account.getAccountName().toUpperCase().contains(
                                SupportUtils.getStringLocalized(getActivity(), "en", R.string.bank).toUpperCase())) {
                            mTotalPayable += account.getCurrentBalance();
                        }
                    }
                }

                mMaxSource = 0;
                mAmountSource = 0;

                mSourceTitle = resources.getString(R.string.source);
                mTargetTitle = resources.getString(R.string.target);
                mMapAccountSelector = new HashMap<>();
                mMapAccountSelector.put(CASH, "");
                mMapAccountSelector.put(BANK, "");
                mMapAccountSelector.put(CREDIT, "");
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                mListener.toggleProgress(false);
            }
        }.execute();
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initUI(final View view) {
        final Activity activity = getActivity();
        final Resources resources = activity.getResources();
        mMainScroll = (ScrollView) view.findViewById(R.id.scrollBudget);
        view.findViewById(R.id.layoutButtonAddIncome).setOnClickListener(this);
        view.findViewById(R.id.layoutButtonAddTransfer).setOnClickListener(this);
        view.findViewById(R.id.buttonBack).setOnClickListener(this);
        mChartBudget = (PieChart) view.findViewById(R.id.chartBudget);

        mTextCurrentBank = (TextView) view.findViewById(R.id.itemBankText1);
        mTextInitBank = (TextView) view.findViewById(R.id.itemBankText2);
        mTextCurrentCash = (TextView) view.findViewById(R.id.itemCashText1);
        mTextInitCash = (TextView) view.findViewById(R.id.itemCashText2);
        mTextCurrentCredit = (TextView) view.findViewById(R.id.itemCreditText1);
        mTextInitCredit = (TextView) view.findViewById(R.id.itemCreditText2);
        mTextTotalPayable = (TextView) view.findViewById(R.id.textTotalPlayable);
        mProgressBank = (ProgressBar) view.findViewById(R.id.progressShareBank);
        mProgressCash = (ProgressBar) view.findViewById(R.id.progressShareCash);
        mProgressCredit = (ProgressBar) view.findViewById(R.id.progressShareCredit);
        mEdtBank = (EditText) view.findViewById(R.id.edtBank);
        mEdtCash = (EditText) view.findViewById(R.id.edtCash);
        mEdtCredit = (EditText) view.findViewById(R.id.edtCreditCard);
        mTextBank = (TextView) view.findViewById(R.id.textShareBank);
        mTextCash = (TextView) view.findViewById(R.id.textShareCash);
        mTextCredit = (TextView) view.findViewById(R.id.textShareCredit);
        mTotalIncome = (EditText) view.findViewById(R.id.edtTotalIncome);
        mTotalIncome.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String totalIncome = editable.toString();
                double income = totalIncome.isEmpty() ? 0 : Double.valueOf(totalIncome);
                if (mCurrency.equals("VND")) {
                    if (income / SupportUtils.MIN_CURRENCY >= 1) {
                        mIncome = income;
                        mProgressBank.setMax((int) mIncome);
                        mProgressCredit.setMax((int) mIncome);
                        mProgressCash.setMax((int) mIncome);
                        mProgressBank.setProgress(0);
                        mProgressCredit.setProgress(0);
                        mProgressCash.setProgress(0);
                        mEdtCash.setText("");
                        mEdtBank.setText("");
                        mEdtCredit.setText("");
                        mTextCredit.setText("");
                        mTextBank.setText("");
                        mTextCash.setText("");
                    }
                }
            }
        });
        mEdtBank = (EditText) view.findViewById(R.id.edtShareBank);
        mEdtCash = (EditText) view.findViewById(R.id.edtShareCash);
        mEdtCredit = (EditText) view.findViewById(R.id.edtShareCredit);
        mEdtCash.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void afterTextChanged(Editable editable) {
                String totalCash = editable.toString();
                double cash = totalCash.isEmpty() ? 0 : Double.valueOf(totalCash);
                if (mCurrency.equals("VND")) {
                    if (cash / SupportUtils.MIN_CURRENCY >= 1) {
                        if (isCorrectDivision()) {
                            mShareCash = cash;
                            mProgressCash.setProgress((int) mShareCash);
                            double percent = (mShareCash / mIncome) * 100;
                            mTextCash.setText(SupportUtils.formatDouble(percent, "#.00") + "%");
                        } else {
                            Toast.makeText(activity, resources.getString(R.string.error_higher_than_income), Toast.LENGTH_SHORT).show();
                            mEdtCash.removeTextChangedListener(this);
                            mEdtCash.setText("");
                            mEdtCash.setText(SupportUtils.getNormalDoubleString(mShareCash, "#000"));
                            mEdtCash.post(new Runnable() {
                                @Override
                                public void run() {
                                    mEdtCash.setSelection(mEdtCash.getText().toString().length());
                                }
                            });
                            mEdtCash.addTextChangedListener(this);
                        }
                    } else {
                        mTextCash.setText("%");
                        mProgressCash.setProgress(0);
                    }
                }
            }
        });
        mEdtBank.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void afterTextChanged(Editable editable) {
                String totalBank = editable.toString();
                double bank = totalBank.isEmpty() ? 0 : Double.valueOf(totalBank);
                if (mCurrency.equals("VND")) {
                    if (bank / SupportUtils.MIN_CURRENCY >= 1) {
                        if (isCorrectDivision()) {
                            mShareBank = bank;
                            mProgressBank.setProgress((int) mShareBank);
                            double percent = (mShareBank / mIncome) * 100;
                            mTextBank.setText(SupportUtils.formatDouble(percent, "#.00") + "%");
                        } else {
                            Toast.makeText(activity, resources.getString(R.string.error_higher_than_income), Toast.LENGTH_SHORT).show();
                            mEdtBank.removeTextChangedListener(this);
                            mEdtBank.setText("");
                            mEdtBank.setText(SupportUtils.getNormalDoubleString(mShareBank, "#000"));
                            mEdtBank.post(new Runnable() {
                                @Override
                                public void run() {
                                    mEdtBank.setSelection(mEdtBank.getText().toString().length());
                                }
                            });
                            mEdtBank.addTextChangedListener(this);
                        }
                    } else {
                        mTextBank.setText("%");
                        mProgressBank.setProgress(0);
                    }
                }
            }
        });
        mEdtCredit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void afterTextChanged(Editable editable) {
                String totalCredit = editable.toString();
                double credit = totalCredit.isEmpty() ? 0 : Double.valueOf(totalCredit);
                if (mCurrency.equals("VND")) {
                    if (credit / SupportUtils.MIN_CURRENCY >= 1) {
                        if (isCorrectDivision()) {
                            mShareCredit = credit;
                            mProgressCredit.setProgress((int) mShareCredit);
                            double percent = (mShareCredit / mIncome) * 100;
                            mTextCredit.setText(SupportUtils.formatDouble(percent, "#.00") + "%");
                        } else {
                            Toast.makeText(activity, resources.getString(R.string.error_higher_than_income), Toast.LENGTH_SHORT).show();
                            mEdtCredit.removeTextChangedListener(this);
                            mEdtCredit.setText("");
                            mEdtCredit.setText(SupportUtils.getNormalDoubleString(mShareCredit, "#000"));
                            mEdtCredit.post(new Runnable() {
                                @Override
                                public void run() {
                                    mEdtCredit.setSelection(mEdtCredit.getText().toString().length());
                                }
                            });
                            mEdtCredit.addTextChangedListener(this);
                        }
                    } else {
                        mTextCredit.setText("%");
                        mProgressCredit.setProgress(0);
                    }
                }
            }
        });

        mLayoutSelectSource = (FrameLayout) view.findViewById(R.id.layoutPickAccountSource);
        mLayoutSelectTarget = (FrameLayout) view.findViewById(R.id.layoutPickAccountTarget);
        view.findViewById(R.id.textSourceSelector).setOnClickListener(this);
        view.findViewById(R.id.textTargetSelector).setOnClickListener(this);
        view.findViewById(R.id.buttonCloseAccountSource).setOnClickListener(this);
        view.findViewById(R.id.buttonCloseAccountTarget).setOnClickListener(this);
        view.findViewById(R.id.transferConfirm).setOnClickListener(this);
        view.findViewById(R.id.transferCancel).setOnClickListener(this);

        mTextSourcePercent = (TextView) view.findViewById(R.id.textSourcePercent);
        mTextSource = (TextView) view.findViewById(R.id.textSourceTitle);
        mTextTarget = (TextView) view.findViewById(R.id.textTargetTitle);
        mProgressSource = (ProgressBar) view.findViewById(R.id.progressSource);
        mEdtSource = (EditText) view.findViewById(R.id.edtSource);
        mEdtSource.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void afterTextChanged(Editable editable) {
                String totalCash = editable.toString();
                double amount = totalCash.isEmpty() ? 0 : Double.valueOf(totalCash);
                if (mCurrency.equals("VND")) {
                    if (amount / SupportUtils.MIN_CURRENCY >= 1) {
                        if (amount <= mMaxSource) {
                            mAmountSource = amount;
                            double percent = (mAmountSource / mMaxSource) * 100;
                            mTextSourcePercent.setText(SupportUtils.formatDouble(percent, "#.00") + "%");
                            mProgressSource.setProgress((int) mAmountSource);
                            changePercentageProgressStyle(mProgressSource);
                        } else {
                            Toast.makeText(activity, resources.getString(R.string.error_higher_than_max), Toast.LENGTH_SHORT).show();
                            mEdtSource.removeTextChangedListener(this);
                            mEdtSource.setText("");
                            mEdtSource.setText(SupportUtils.getNormalDoubleString(mAmountSource, "#000"));
                            mEdtSource.post(new Runnable() {
                                @Override
                                public void run() {
                                    mEdtSource.setSelection(mEdtSource.getText().toString().length());
                                }
                            });
                            mEdtSource.addTextChangedListener(this);
                        }
                    }
                }
            }
        });

        view.findViewById(R.id.itemSelectBankSource).setOnClickListener(this);
        mIconSelectBankSource = (ImageView) view.findViewById(R.id.iconSelectBankSource);
        view.findViewById(R.id.itemSelectCashSource).setOnClickListener(this);
        mIconSelectCashSource = (ImageView) view.findViewById(R.id.iconSelectCashSource);
        view.findViewById(R.id.itemSelectCreditSource).setOnClickListener(this);
        mIconSelectCreditSource = (ImageView) view.findViewById(R.id.iconSelectCreditSource);
        view.findViewById(R.id.itemSelectBankTarget).setOnClickListener(this);
        mIconSelectBankTarget = (ImageView) view.findViewById(R.id.iconSelectBankTarget);
        view.findViewById(R.id.itemSelectCashTarget).setOnClickListener(this);
        mIconSelectCashTarget = (ImageView) view.findViewById(R.id.iconSelectCashTarget);
        view.findViewById(R.id.itemSelectCreditTarget).setOnClickListener(this);
        mIconSelectCreditTarget = (ImageView) view.findViewById(R.id.iconSelectCreditTarget);
        final ImageButton mButtonAddTransfer = (ImageButton) view.findViewById(R.id.buttonAddTransfer);
        mButtonAddTransfer.setOnClickListener(this);
        final ImageButton mButtonAddCash = (ImageButton) view.findViewById(R.id.buttonAddCash);
        mButtonAddCash.setOnClickListener(this);
        view.findViewById(R.id.incomeConfirm).setOnClickListener(this);
        view.findViewById(R.id.incomeCancel).setOnClickListener(this);
        RadioGroup mTabHost = (RadioGroup) view.findViewById(R.id.tabHost);
        final RadioButton buttonTabCash = (RadioButton) view.findViewById(R.id.buttonTabCash);
        final RadioButton buttonTabBank = (RadioButton) view.findViewById(R.id.buttonTabBank);
        final RadioButton buttonTabCredit = (RadioButton) view.findViewById(R.id.buttonTabCredit);
        mTabHost.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                buttonTabCash.setChecked(R.id.buttonTabCash == i);
                buttonTabBank.setChecked(R.id.buttonTabBank == i);
                buttonTabCredit.setChecked(R.id.buttonTabCredit == i);
                switch (i) {
                    case R.id.buttonTabCash:
                        view.findViewById(R.id.tabCash).setVisibility(View.VISIBLE);
                        view.findViewById(R.id.tabBank).setVisibility(View.GONE);
                        view.findViewById(R.id.tabCredit).setVisibility(View.GONE);
                        break;
                    case R.id.buttonTabBank:
                        view.findViewById(R.id.tabCash).setVisibility(View.GONE);
                        view.findViewById(R.id.tabBank).setVisibility(View.VISIBLE);
                        view.findViewById(R.id.tabCredit).setVisibility(View.GONE);
                        break;
                    default:
                        view.findViewById(R.id.tabCash).setVisibility(View.GONE);
                        view.findViewById(R.id.tabBank).setVisibility(View.GONE);
                        view.findViewById(R.id.tabCredit).setVisibility(View.VISIBLE);
                        break;
                }
            }
        });
        view.findViewById(R.id.buttonAdd).setOnClickListener(this);
        mLayoutQuickAdd = (LinearLayout) view.findViewById(R.id.layoutQuickAddSelect);
        mLayoutQuickAdd.setOnClickListener(this);
        mLayoutAdd = (LinearLayout) view.findViewById(R.id.layoutButtonAdd);
        mLayoutAdd.setOnClickListener(this);
        mAnimationRotateForward30 = new RotateAnimation(0.0f, 30.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        mAnimationRotateForward30.setDuration(1000);
        mAnimationRotateForward30.setRepeatCount(0);
        mAnimationRotateForward30.setRepeatMode(Animation.REVERSE);
        mAnimationRotateForward30.setFillAfter(true);
        mAnimationRotateBackward30 = new RotateAnimation(0.0f, -30.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        mAnimationRotateBackward30.setDuration(1000);
        mAnimationRotateBackward30.setRepeatCount(0);
        mAnimationRotateBackward30.setRepeatMode(Animation.REVERSE);
        mAnimationRotateBackward30.setFillAfter(true);

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
                mLayoutQuickAdd.setVisibility(View.VISIBLE);
                mButtonAddTransfer.startAnimation(mAnimationRotateForward30);
                mButtonAddCash.startAnimation(mAnimationRotateForward30);
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
                mLayoutQuickAdd.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mButtonAddTransfer.startAnimation(mAnimationRotateBackward30);
                mButtonAddCash.startAnimation(mAnimationRotateBackward30);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        LayoutInflater inflater = getActivity().getLayoutInflater();
        mTransferAdapter = new BasicAdapter<>(mListTransfer, R.layout.item_transfer, inflater);
        mListTransaction = (ListView) view.findViewById(R.id.listTransaction);
        mListTransaction.setAdapter(mTransferAdapter);

        mAddIncomeArea = (LinearLayout) view.findViewById(R.id.addIncomeArea);
        mAddTransferArea = (ScrollView) view.findViewById(R.id.addTransferArea);

        updateAccount();
    }

    @Override
    protected void setStatusBarColor() {
        mListener.setStatusBarColor(Color.parseColor("#8bc34a"));
    }

    @Override
    protected int getSideMenuColor() {
        return Color.parseColor("#8bc34a");
    }

    @Override
    protected void fragmentReady(Bundle savedInstanceState) {
        Activity activity = getActivity();
        Resources resources = activity.getResources();
        SupportUtils.setListViewHeight(mListTransaction);
        mMainScroll.smoothScrollTo(0, 0);
        if (isAddIncome) {
            mAddIncomeArea.setVisibility(View.VISIBLE);
            mAddTransferArea.setVisibility(View.GONE);
        } else if (isAddTransfer) {
            mAddIncomeArea.setVisibility(View.GONE);
            mAddTransferArea.setVisibility(View.VISIBLE);
        }

        if (!mListAccounts.isEmpty()) {
            ArrayList<String> listNames = new ArrayList<>();
            ArrayList<Float> listValues = new ArrayList<>();
            for (Account account : mListAccounts) {
                String name = account.getAccountName();
                if (name.contains(SupportUtils.getStringLocalized(activity, "en", R.string.cash))) {
                    listNames.add(resources.getString(R.string.cash));
                } else if (name.contains(SupportUtils.getStringLocalized(activity, "en", R.string.bank))) {
                    listNames.add(resources.getString(R.string.cash));
                } else {
                    listNames.add(resources.getString(R.string.credit_card));
                }
                listValues.add((float) account.getCurrentBalance());
            }
            addDataToChart(listNames, listValues, mChartBudget, "Chart budget", "Current budget");
        }
    }

    @Override
    protected boolean canGoBack() {
        return (mAddIncomeArea.getVisibility() != View.VISIBLE && mAddTransferArea.getVisibility() != View.VISIBLE);
    }

    @Override
    public void onClick(View view) {
        Activity activity = getActivity();
        Resources resources = activity.getResources();
        MainApplication mainApplication = MainApplication.getInstance();
        String selectedItem = resources.getString(R.string.selected_item);
        switch (view.getId()) {
            case R.id.buttonBack:
                if (canGoBack()) {
                    mNavListener.navBack();
                } else {
                    mAddIncomeArea.setVisibility(View.GONE);
                    mAddTransferArea.setVisibility(View.GONE);
                }
                break;
            case R.id.buttonAdd:
            case R.id.layoutButtonAdd:
                mLayoutAdd.startAnimation(mAnimationForward);
                break;
            case R.id.layoutQuickAddSelect:
                mLayoutAdd.startAnimation(mAnimationBackward);
                break;
            case R.id.layoutButtonAddIncome:
            case R.id.buttonAddCash:
                mLayoutAdd.startAnimation(mAnimationBackward);
                mAddIncomeArea.setVisibility(View.VISIBLE);
                mAddTransferArea.setVisibility(View.GONE);
                break;
            case R.id.layoutButtonAddTransfer:
            case R.id.buttonAddTransfer:
                mLayoutAdd.startAnimation(mAnimationBackward);
                mAddIncomeArea.setVisibility(View.GONE);
                mAddTransferArea.setVisibility(View.VISIBLE);
                break;
            case R.id.incomeConfirm:
                if (isCorrectDivision()) {
                    String cash = mEdtCash.getText().toString();
                    String credit = mEdtCredit.getText().toString();
                    String bank = mEdtBank.getText().toString();
                    double cashAmount = cash.isEmpty() ? 0 : Double.valueOf(cash);
                    double bankAmount = bank.isEmpty() ? 0 : Double.valueOf(bank);
                    double creditAmount = credit.isEmpty() ? 0 : Double.valueOf(credit);
                    ArrayList<Account> accounts = mainApplication.getAccounts();
                    String cashLocal = SupportUtils.getStringLocalized(activity, "en", R.string.cash);
                    String bankLocal = SupportUtils.getStringLocalized(activity, "en", R.string.bank);
                    String creditLocal = SupportUtils.getStringLocalized(activity, "en", R.string.credit_card);
                    AccountDAO accountDAO = AccountDAO.getInstance(activity);
                    if (accountDAO != null) {
                        boolean cashUpdated = false, bankUpdated = false, creditUpdated = false;
                        for (Account account : accounts) {
                            if (account.getAccountName().contains(cashLocal)) {
                                double newCash = account.getCurrentBalance() + cashAmount;
                                cashUpdated = accountDAO.updateAccount(cashLocal, newCash);
                            } else if (account.getAccountName().contains(bankLocal)) {
                                double newBank = account.getCurrentBalance() + bankAmount;
                                bankUpdated = accountDAO.updateAccount(bankLocal, newBank);
                            } else {
                                double newCredit = account.getCurrentBalance() + creditAmount;
                                creditUpdated = accountDAO.updateAccount(creditLocal, newCredit);
                            }
                        }
                        if (cashUpdated && bankUpdated && creditUpdated) {
                            Toast.makeText(activity, resources.getString(R.string.info_saved), Toast.LENGTH_SHORT).show();
                            mainApplication.refreshAllData();
                            mAddIncomeArea.setVisibility(View.GONE);
                        } else {
                            Toast.makeText(activity, resources.getString(R.string.info_save_failed), Toast.LENGTH_SHORT).show();
                        }
                    }

                }
                break;
            case R.id.incomeCancel:
                mProgressBank.setMax(0);
                mProgressCredit.setMax(0);
                mProgressCash.setMax(0);
                mProgressBank.setProgress(0);
                mProgressCredit.setProgress(0);
                mProgressCash.setProgress(0);
                mEdtCash.setText("");
                mEdtBank.setText("");
                mEdtCredit.setText("");
                mTextCredit.setText("");
                mTextBank.setText("");
                mTextCash.setText("");
                mIncome = 0;
                mShareBank = 0;
                mShareCash = 0;
                mShareCredit = 0;
                mTotalIncome.setText("");
                break;
            case R.id.textSourceSelector:
                mLayoutSelectSource.setVisibility(View.VISIBLE);
                break;
            case R.id.textTargetSelector:
                mLayoutSelectTarget.setVisibility(View.VISIBLE);
                break;
            case R.id.buttonCloseAccountSource:
                mLayoutSelectSource.setVisibility(View.GONE);
                break;
            case R.id.buttonCloseAccountTarget:
                mLayoutSelectTarget.setVisibility(View.GONE);
                break;
            case R.id.itemSelectBankSource:
                if (mMapAccountSelector.get(BANK).equals(mTargetTitle)) {
                    Toast.makeText(activity, selectedItem, Toast.LENGTH_SHORT).show();
                    mMapAccountSelector.put(BANK, "");
                } else {
                    mSource = BANK;
                    mMapAccountSelector.put(BANK, mSourceTitle);
                    mIconSelectBankSource.setVisibility(View.VISIBLE);
                    mIconSelectBankTarget.setVisibility(View.GONE);
                    mIconSelectCashSource.setVisibility(View.GONE);
                    mIconSelectCreditSource.setVisibility(View.GONE);
                    mTextSource.setText(mSourceTitle + ": " + resources.getString(R.string.bank));
                    boolean progressApplied = false;
                    for (int i = 0; i < mListAccounts.size() && !progressApplied; i++) {
                        Account account = mListAccounts.get(i);
                        if (account.getAccountName().toLowerCase().contains(BANK.toLowerCase())) {
                            mMaxSource = account.getCurrentBalance();
                            mProgressSource.setMax((int) mMaxSource);
                            progressApplied = true;
                        }
                    }
                }
                break;
            case R.id.itemSelectCashSource:
                if (mMapAccountSelector.get(CASH).equals(mTargetTitle)) {
                    Toast.makeText(activity, selectedItem, Toast.LENGTH_SHORT).show();
                    mMapAccountSelector.put(CASH, "");
                } else {
                    mSource = CASH;
                    mMapAccountSelector.put(CASH, mSourceTitle);
                    mIconSelectCashSource.setVisibility(View.VISIBLE);
                    mIconSelectCashTarget.setVisibility(View.GONE);
                    mIconSelectBankSource.setVisibility(View.GONE);
                    mIconSelectCreditSource.setVisibility(View.GONE);
                    mTextSource.setText(mSourceTitle + ": " + resources.getString(R.string.cash));
                    boolean progressApplied = false;
                    for (int i = 0; i < mListAccounts.size() && !progressApplied; i++) {
                        Account account = mListAccounts.get(i);
                        if (account.getAccountName().toLowerCase().contains(CASH.toLowerCase())) {
                            mMaxSource = account.getCurrentBalance();
                            mProgressSource.setMax((int) mMaxSource);
                            progressApplied = true;
                        }
                    }
                }
                break;
            case R.id.itemSelectCreditSource:
                if (mMapAccountSelector.get(CREDIT).equals(mTargetTitle)) {
                    Toast.makeText(activity, selectedItem, Toast.LENGTH_SHORT).show();
                    mMapAccountSelector.put(CREDIT, "");
                } else {
                    mSource = CREDIT;
                    mMapAccountSelector.put(CREDIT, mSourceTitle);
                    mIconSelectCreditSource.setVisibility(View.VISIBLE);
                    mIconSelectCreditTarget.setVisibility(View.GONE);
                    mIconSelectCashSource.setVisibility(View.GONE);
                    mIconSelectBankSource.setVisibility(View.GONE);
                    mTextSource.setText(mSourceTitle + ": " + resources.getString(R.string.credit_card));
                    boolean progressApplied = false;
                    for (int i = 0; i < mListAccounts.size() && !progressApplied; i++) {
                        Account account = mListAccounts.get(i);
                        if (account.getAccountName().toLowerCase().contains(CREDIT.toLowerCase())) {
                            mMaxSource = account.getCurrentBalance();
                            mProgressSource.setMax((int) mMaxSource);
                            progressApplied = true;
                        }
                    }
                }
                break;
            case R.id.itemSelectBankTarget:
                if (mMapAccountSelector.get(BANK).equals(mSourceTitle)) {
                    Toast.makeText(activity, selectedItem, Toast.LENGTH_SHORT).show();
                    mMapAccountSelector.put(BANK, "");
                } else {
                    mTarget = BANK;
                    mMapAccountSelector.put(BANK, mTargetTitle);
                    mIconSelectBankTarget.setVisibility(View.VISIBLE);
                    mIconSelectBankSource.setVisibility(View.GONE);
                    mIconSelectCashTarget.setVisibility(View.GONE);
                    mIconSelectCreditTarget.setVisibility(View.GONE);
                    mTextTarget.setText(mTargetTitle + ": " + resources.getString(R.string.bank));
                }
                break;
            case R.id.itemSelectCashTarget:
                if (mMapAccountSelector.get(CASH).equals(mSourceTitle)) {
                    Toast.makeText(activity, selectedItem, Toast.LENGTH_SHORT).show();
                    mMapAccountSelector.put(CASH, "");
                } else {
                    mSource = CASH;
                    mMapAccountSelector.put(CASH, mTargetTitle);
                    mIconSelectCashTarget.setVisibility(View.VISIBLE);
                    mIconSelectCashSource.setVisibility(View.GONE);
                    mIconSelectBankTarget.setVisibility(View.GONE);
                    mIconSelectCreditTarget.setVisibility(View.GONE);
                    mTextTarget.setText(mTargetTitle + ": " + resources.getString(R.string.cash));
                }
                break;
            case R.id.itemSelectCreditTarget:
                if (mMapAccountSelector.get(CREDIT).equals(mSourceTitle)) {
                    Toast.makeText(activity, selectedItem, Toast.LENGTH_SHORT).show();
                    mMapAccountSelector.put(CREDIT, "");
                } else {
                    mSource = CREDIT;
                    mMapAccountSelector.put(CREDIT, mTargetTitle);
                    mIconSelectCreditTarget.setVisibility(View.VISIBLE);
                    mIconSelectCreditSource.setVisibility(View.GONE);
                    mIconSelectCashTarget.setVisibility(View.GONE);
                    mIconSelectBankTarget.setVisibility(View.GONE);
                    mTextTarget.setText(mTargetTitle + ": " + resources.getString(R.string.credit_card));
                }
                break;
            case R.id.transferConfirm:
                String source = "";
                String target = "";
                double oldSourceBalance = 0, newSourceBalance = 0;
                double oldTargetBalance = 0, newTargetBalance = 0;
                for (Map.Entry<String, String> entry : mMapAccountSelector.entrySet()) {
                    if (entry.getValue().toUpperCase().contains(mSourceTitle.toUpperCase())) {
                        if (entry.getKey().toUpperCase().contains(BANK)) {
                            source = SupportUtils.getStringLocalized(activity, "en", R.string.bank);
                        } else if (entry.getKey().toUpperCase().contains(CASH)) {
                            source = SupportUtils.getStringLocalized(activity, "en", R.string.cash);
                        } else {
                            source = SupportUtils.getStringLocalized(activity, "en", R.string.credit_card);
                        }
                    }

                    if (entry.getValue().toUpperCase().contains(mTargetTitle.toUpperCase())) {
                        if (entry.getKey().toUpperCase().contains(BANK)) {
                            target = SupportUtils.getStringLocalized(activity, "en", R.string.bank);
                        } else if (entry.getKey().toUpperCase().contains(CASH)) {
                            target = SupportUtils.getStringLocalized(activity, "en", R.string.cash);
                        } else {
                            target = SupportUtils.getStringLocalized(activity, "en", R.string.credit_card);
                        }
                    }
                }
                if (!source.isEmpty() && !target.isEmpty()) {
                    for (Account account : mListAccounts) {
                        if (account.getAccountName().toUpperCase().contains(source.toUpperCase())) {
                            oldSourceBalance = account.getCurrentBalance();
                            newSourceBalance = oldSourceBalance - mAmountSource;
                        }
                        if (account.getAccountName().toUpperCase().contains(target.toUpperCase())) {
                            oldTargetBalance = account.getCurrentBalance();
                            newTargetBalance = oldTargetBalance + mAmountSource;
                        }
                    }
                    AccountDAO accountDAO = AccountDAO.getInstance(activity);
                    if (accountDAO != null) {
                        Log.d("Amount", "" + mAmountSource);
                        Log.d("Source", source + ", old: " + oldSourceBalance + ", new: " + newSourceBalance);
                        Log.d("Target", target + ", old: " + oldTargetBalance + ", new: " + newTargetBalance);
                        boolean updateSource = accountDAO.updateAccount(source, newSourceBalance);
                        boolean updateTarget = accountDAO.updateAccount(target, newTargetBalance);
                        if (updateSource && updateTarget) {
                            Toast.makeText(activity, resources.getString(R.string.info_saved), Toast.LENGTH_SHORT).show();
                            mainApplication.refreshAllData();
                            mAddTransferArea.setVisibility(View.GONE);
                        } else {
                            Toast.makeText(activity, resources.getString(R.string.info_save_failed), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(activity, resources.getString(R.string.error_not_pick_account), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.transferCancel:
                mEdtSource.setText("");
                mProgressSource.setProgress(0);
                mProgressSource.setMax(0);
                changePercentageProgressStyle(mProgressSource);
                mTextSource.setText(mSourceTitle);
                mTextTarget.setText(mTargetTitle);
                mTextSourcePercent.setText("%");
                mMaxSource = 0;
                mAmountSource = 0;
                mMapAccountSelector.clear();
                mMapAccountSelector.put(BANK, "");
                mMapAccountSelector.put(CASH, "");
                mMapAccountSelector.put(CREDIT, "");
                mIconSelectBankSource.setVisibility(View.GONE);
                mIconSelectCashSource.setVisibility(View.GONE);
                mIconSelectCreditSource.setVisibility(View.GONE);
                mIconSelectBankTarget.setVisibility(View.GONE);
                mIconSelectCashTarget.setVisibility(View.GONE);
                mIconSelectCreditTarget.setVisibility(View.GONE);
                mLayoutSelectTarget.setVisibility(View.GONE);
                mLayoutSelectSource.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    @Override
    public void refreshData() {
        if (mListAccounts == null) {
            mListAccounts = new ArrayList<>();
        }
        mListAccounts.clear();
        mListAccounts.addAll(MainApplication.getInstance().getAccounts());

        updateAccount();
    }

    @SuppressLint("SetTextI18n")
    private void updateAccount() {
        Context context = MainApplication.getInstance().getApplicationContext();
        mTotalPayable = 0;
        if (!mListAccounts.isEmpty()) {
            for (Account account : mListAccounts) {
                if (!account.getAccountName().toUpperCase().contains(
                        SupportUtils.getStringLocalized(context, "en", R.string.bank).toUpperCase())) {
                    mTotalPayable += account.getCurrentBalance();
                }
            }
        }
        String totalPayable = SupportUtils.getNormalDoubleString(mTotalPayable, "#0,000");
        String currency = PreferencesUtils.getString(PreferencesUtils.CURRENCY, "VND");
        if (mTextTotalPayable != null) {
            if (currency.contains("VND")) {
                mTextTotalPayable.setText(totalPayable + " VNĐ");
            } else {
                mTextTotalPayable.setText("$" + totalPayable);
            }
        }
        if (mTextCurrentBank != null && mTextInitBank != null &&
                mTextCurrentCash != null && mTextInitCash != null &&
                mTextCurrentCredit != null && mTextInitCredit != null) {
            for (Account account : mListAccounts) {
                if (account.getAccountName().contains(SupportUtils.getStringLocalized(context, "en", R.string.cash))) {
                    String currentCash = SupportUtils.getNormalDoubleString(account.getCurrentBalance(), "#0,000");
                    String initCash = SupportUtils.getNormalDoubleString(account.getInitialBalance(), "#0,000");
                    if (currency.contains("VND")) {
                        mTextCurrentCash.setText(currentCash + " VNĐ");
                        mTextInitCash.setText(initCash + " VNĐ");
                    } else {
                        mTextCurrentCash.setText("$" + currentCash);
                        mTextInitCash.setText("$" + initCash);
                    }
                } else if (account.getAccountName().contains(SupportUtils.getStringLocalized(context, "en", R.string.credit_card))) {
                    String currentCredit = SupportUtils.getNormalDoubleString(account.getCurrentBalance(), "#0,000");
                    String initCredit = SupportUtils.getNormalDoubleString(account.getInitialBalance(), "#0,000");
                    if (currency.contains("VND")) {
                        mTextCurrentCredit.setText(currentCredit + " VNĐ");
                        mTextInitCredit.setText(initCredit + " VNĐ");
                    } else {
                        mTextCurrentCredit.setText("$" + currentCredit);
                        mTextInitCredit.setText("$" + initCredit);
                    }
                } else {
                    String currentBank = SupportUtils.getNormalDoubleString(account.getCurrentBalance(), "#0,000");
                    String initBank = SupportUtils.getNormalDoubleString(account.getInitialBalance(), "#0,000");
                    if (currency.contains("VND")) {
                        mTextCurrentBank.setText(currentBank + " VNĐ");
                        mTextInitBank.setText(initBank + " VNĐ");
                    } else {
                        mTextCurrentBank.setText("$" + currentBank);
                        mTextInitBank.setText("$" + initBank);
                    }
                }
            }
        }
    }

    private boolean isCorrectDivision() {
        String cash = mEdtCash.getText().toString();
        String credit = mEdtCredit.getText().toString();
        String bank = mEdtBank.getText().toString();
        double cashAmount = cash.isEmpty() ? 0 : Double.valueOf(cash);
        double bankAmount = bank.isEmpty() ? 0 : Double.valueOf(bank);
        double creditAmount = credit.isEmpty() ? 0 : Double.valueOf(credit);
        return (cashAmount + bankAmount + creditAmount) <= mIncome;
    }

    private void addDataToChart(final ArrayList<String> xValues, final ArrayList<Float> yValuesData, PieChart chart,
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
        for (int i = 0; i < yValuesData.size(); i++) {
            yValues.add(new Entry(yValuesData.get(i), i));
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

    private void changePercentageProgressStyle(ProgressBar mCurrentPercentages) {
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
}
