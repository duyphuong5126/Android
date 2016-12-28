package com.huy.monthlyfinance.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.huy.monthlyfinance.Database.DAO.AccountDAO;
import com.huy.monthlyfinance.MainApplication;
import com.huy.monthlyfinance.Model.Account;
import com.huy.monthlyfinance.MyView.BasicAdapter;
import com.huy.monthlyfinance.MyView.Item.ListItem.TransferItem;
import com.huy.monthlyfinance.R;
import com.huy.monthlyfinance.SupportUtils.PreferencesUtils;
import com.huy.monthlyfinance.SupportUtils.SupportUtils;

import java.util.ArrayList;
import java.util.Date;

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

    private LinearLayout mAddIncomeArea, mAddTransferArea;

    private String mCurrency;
    private EditText mTotalIncome;
    private double mIncome;
    private static final int MIN_CURRENCY = 1000;

    private ProgressBar mProgressCash, mProgressBank, mProgressCredit;
    private EditText mEdtBank, mEdtCash, mEdtCredit;
    private TextView mTextBank, mTextCash, mTextCredit;
    private double mShareBank, mShareCash, mShareCredit, mTotal;

    private boolean isAddIncome, isAddTransfer;

    @Override
    protected int getLayoutXML() {
        return R.layout.fragment_budget;
    }

    @Override
    protected void onPrepare() {
        mListTransfer = new ArrayList<>();
        mListTransfer.add(new TransferItem(100, "Cash", "Bank", 1000, 700, new Date(System.currentTimeMillis())));
        mListTransfer.add(new TransferItem(50, "Cash", "Bank", 900, 800, new Date(System.currentTimeMillis())));
        mListTransfer.add(new TransferItem(10, "Credit", "Cash", 900, 850, new Date(System.currentTimeMillis())));
        mListTransfer.add(new TransferItem(40, "Credit", "Cash", 890, 860, new Date(System.currentTimeMillis())));

        mCurrency = PreferencesUtils.getString(PreferencesUtils.CURRENCY, "VND");
        mTotal = mIncome = mShareBank = mShareCash = mShareCredit = 0;
        Bundle bundle = getArguments();
        if (bundle != null) {
            isAddIncome = bundle.getBoolean("isAddIncome");
            isAddTransfer = bundle.getBoolean("isAddTransfer");
        }
    }

    @Override
    protected void initUI(final View view) {
        final Activity activity = getActivity();
        final Resources resources = activity.getResources();
        mMainScroll = (ScrollView) view.findViewById(R.id.scrollBudget);
        view.findViewById(R.id.layoutButtonAddIncome).setOnClickListener(this);
        view.findViewById(R.id.layoutButtonAddTransfer).setOnClickListener(this);
        view.findViewById(R.id.buttonBack).setOnClickListener(this);

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
                    if (income / MIN_CURRENCY >= 1) {
                        if (income % MIN_CURRENCY == 500 || income % MIN_CURRENCY == 0) {
                            mIncome = income;
                            Toast.makeText(activity, "Income: " + income, Toast.LENGTH_SHORT).show();
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
                        } else {
                            mTotalIncome.removeTextChangedListener(this);
                            mTotalIncome.setText("");
                            mTotalIncome.setText(String.valueOf(mIncome));
                            mTotalIncome.post(new Runnable() {
                                @Override
                                public void run() {
                                    mTotalIncome.setSelection(mTotalIncome.getText().toString().length());
                                }
                            });
                            mTotalIncome.addTextChangedListener(this);
                        }
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
                    if (cash / MIN_CURRENCY >= 1) {
                        if (cash % MIN_CURRENCY == 500 || cash % MIN_CURRENCY == 0) {
                            if (isCorrectDivision()) {
                                mShareCash = cash;
                                Toast.makeText(activity, "Share cash: " + SupportUtils.getNormalDoubleString(cash, "#0,000"), Toast.LENGTH_SHORT).show();
                                mProgressCash.setProgress((int) mShareCash);
                                double percent = (mShareCash / mIncome) * 100;
                                mTextCash.setText(SupportUtils.formatDouble(percent, "#.00") + "%");
                            } else {
                                Toast.makeText(activity, resources.getString(R.string.error_higher_than_income), Toast.LENGTH_SHORT).show();
                                mEdtCash.removeTextChangedListener(this);
                                mEdtCash.setText("");
                                mEdtCash.setText(String.valueOf(mShareCash));
                                mEdtCash.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        mEdtCash.setSelection(mEdtCash.getText().toString().length());
                                    }
                                });
                                mEdtCash.addTextChangedListener(this);
                            }
                        } else {
                            mEdtCash.removeTextChangedListener(this);
                            mEdtCash.setText("");
                            mEdtCash.setText(String.valueOf(mShareCash));
                            mEdtCash.post(new Runnable() {
                                @Override
                                public void run() {
                                    mEdtCash.setSelection(mEdtCash.getText().toString().length());
                                }
                            });
                            mEdtCash.addTextChangedListener(this);
                        }
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
                    if (bank / MIN_CURRENCY >= 1) {
                        if (bank % MIN_CURRENCY == 500 || bank % MIN_CURRENCY == 0) {
                            if (isCorrectDivision()) {
                                mShareBank = bank;
                                Toast.makeText(activity, "Share bank: " + SupportUtils.getNormalDoubleString(bank, "#0,000"), Toast.LENGTH_SHORT).show();
                                mProgressBank.setProgress((int) mShareBank);
                                double percent = (mShareBank / mIncome) * 100;
                                mTextBank.setText(SupportUtils.formatDouble(percent, "#.00") + "%");
                            } else {
                                Toast.makeText(activity, resources.getString(R.string.error_higher_than_income), Toast.LENGTH_SHORT).show();
                                mEdtBank.removeTextChangedListener(this);
                                mEdtBank.setText("");
                                mEdtBank.setText(String.valueOf(mShareBank));
                                mEdtBank.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        mEdtBank.setSelection(mEdtBank.getText().toString().length());
                                    }
                                });
                                mEdtBank.addTextChangedListener(this);
                            }
                        } else {
                            mEdtBank.removeTextChangedListener(this);
                            mEdtBank.setText("");
                            mEdtBank.setText(String.valueOf(mShareBank));
                            mEdtBank.post(new Runnable() {
                                @Override
                                public void run() {
                                    mEdtBank.setSelection(mEdtBank.getText().toString().length());
                                }
                            });
                            mEdtBank.addTextChangedListener(this);
                        }
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
                    if (credit / MIN_CURRENCY >= 1) {
                        if (credit % MIN_CURRENCY == 500 || credit % MIN_CURRENCY == 0) {
                            if (isCorrectDivision()) {
                                mShareCredit = credit;
                                Toast.makeText(activity, "Share credit: " + SupportUtils.getNormalDoubleString(credit, "#0,000"), Toast.LENGTH_SHORT).show();
                                mProgressCredit.setProgress((int) mShareCredit);
                                double percent = (mShareCredit / mIncome) * 100;
                                mTextCredit.setText(SupportUtils.formatDouble(percent, "#.00") + "%");
                            } else {
                                Toast.makeText(activity, resources.getString(R.string.error_higher_than_income), Toast.LENGTH_SHORT).show();
                                mEdtCredit.removeTextChangedListener(this);
                                mEdtCredit.setText("");
                                mEdtCredit.setText(String.valueOf(mShareCredit));
                                mEdtCredit.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        mEdtCredit.setSelection(mEdtCredit.getText().toString().length());
                                    }
                                });
                                mEdtCredit.addTextChangedListener(this);
                            }
                        } else {
                            mEdtCredit.removeTextChangedListener(this);
                            mEdtCredit.setText("");
                            mEdtCredit.setText(String.valueOf(mShareCredit));
                            mEdtCredit.post(new Runnable() {
                                @Override
                                public void run() {
                                    mEdtCredit.setSelection(mEdtCredit.getText().toString().length());
                                }
                            });
                            mEdtCredit.addTextChangedListener(this);
                        }
                    }
                }
            }
        });

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
        mAddTransferArea = (LinearLayout) view.findViewById(R.id.addTransferArea);
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
        SupportUtils.setListViewHeight(mListTransaction);
        mMainScroll.smoothScrollTo(0, 0);
        if (isAddIncome) {
            mAddIncomeArea.setVisibility(View.VISIBLE);
            mAddTransferArea.setVisibility(View.GONE);
        } else if (isAddTransfer) {
            mAddIncomeArea.setVisibility(View.GONE);
            mAddTransferArea.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected boolean canGoBack() {
        return mAddIncomeArea.getVisibility() != View.VISIBLE;
    }

    @Override
    public void onClick(View view) {
        Activity activity = getActivity();
        Resources resources = activity.getResources();
        MainApplication mainApplication = MainApplication.getInstance();
        switch (view.getId()) {
            case R.id.buttonBack:
                if (canGoBack()) {
                    mNavListener.navBack();
                } else {
                    mAddIncomeArea.setVisibility(View.GONE);
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
                break;
            case R.id.layoutButtonAddTransfer:
            case R.id.buttonAddTransfer:
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
                break;
            default:
                break;
        }
    }

    @Override
    public void refreshData() {

    }

    private boolean isCorrectDivision() {
        String cash = mEdtCash.getText().toString();
        String credit = mEdtCredit.getText().toString();
        String bank = mEdtBank.getText().toString();
        String income = mTotalIncome.getText().toString();
        double cashAmount = cash.isEmpty() ? 0 : Double.valueOf(cash);
        double bankAmount = bank.isEmpty() ? 0 : Double.valueOf(bank);
        double creditAmount = credit.isEmpty() ? 0 : Double.valueOf(credit);
        double incomeAmount = cash.isEmpty() ? 0 : Double.valueOf(income);
        return (cashAmount + bankAmount + creditAmount) <= incomeAmount;
    }
}
