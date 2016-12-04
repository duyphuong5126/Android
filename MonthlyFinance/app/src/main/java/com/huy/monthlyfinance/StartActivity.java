package com.huy.monthlyfinance;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.huy.monthlyfinance.Database.DAO.AccountDAO;
import com.huy.monthlyfinance.Database.DAO.UserDAO;
import com.huy.monthlyfinance.Model.Account;
import com.huy.monthlyfinance.Model.User;
import com.huy.monthlyfinance.SupportUtils.PreferencesUtils;
import com.huy.monthlyfinance.SupportUtils.SupportUtils;

public class StartActivity extends Activity implements View.OnClickListener{
    private LinearLayout mLayoutLogin;
    private LinearLayout mLayoutInitInfo;
    private AccountDAO mAccountDataSource;
    private EditText mEdtCash;
    private EditText mEdtBank;
    private EditText mEdtCreditCard;
    private EditText mEdtEmail;
    private EditText mEdtCurrency;
    private boolean isInfoInitialized;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Context context = getApplicationContext();
        if (!PreferencesUtils.isInitialized()) {
            PreferencesUtils.init(context);
        }
        PreferencesUtils.setValue(PreferencesUtils.isInfoInitialized, false);
        mAccountDataSource = AccountDAO.getInstance(context);

        mLayoutLogin = (LinearLayout) findViewById(R.id.layoutLogin);
        mLayoutInitInfo = (LinearLayout) findViewById(R.id.layoutInitInfo);

        mEdtCash = (EditText) findViewById(R.id.edtCash);
        mEdtBank = (EditText) findViewById(R.id.edtBank);
        mEdtCreditCard = (EditText) findViewById(R.id.edtCreditCard);
        mEdtEmail = (EditText) findViewById(R.id.edtEmail);
        mEdtCurrency = (EditText) findViewById(R.id.edtCurrency);
        findViewById(R.id.buttonConfirm).setOnClickListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onStart() {
        super.onStart();
        if (SupportUtils.checkLollipopOrAbove()) {
            getWindow().setStatusBarColor(Color.parseColor("#a24ade"));
        }
        if (isInfoInitialized()) {
            startMainActivity();
        }
    }

    private boolean isInfoInitialized() {
        return PreferencesUtils.getBoolean(PreferencesUtils.isInfoInitialized, false);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonConfirm:
                if (doUpdateBalances()) {
                    PreferencesUtils.setValue(PreferencesUtils.isInfoInitialized, true);
                    startMainActivity();
                }
                break;
            default:
                break;
        }
    }

    private void startMainActivity() {
        Intent activityMain = new Intent(StartActivity.this, MainActivity.class);
        startActivity(activityMain);
        this.finish();
    }

    private boolean doUpdateBalances() {
        String email = mEdtEmail.getText().toString();
        String currency = mEdtCurrency.getText().toString();
        String cash = mEdtCash.getText().toString();
        String bank = mEdtBank.getText().toString();
        String credit = mEdtCreditCard.getText().toString();
        if (!cash.isEmpty() && !bank.isEmpty() && ! credit.isEmpty()) {
            int cashBalance = Integer.valueOf(cash);
            int bankBalance = Integer.valueOf(bank);
            int creditBalance = Integer.valueOf(credit);
            int min = 1000;
            if (currency.equals("VND")) {
                if (cashBalance / min < 1 || bankBalance / min < 1 || creditBalance / min < 1) {
                    Toast.makeText(StartActivity.this, "Your balance(s) is lower than minimum limit", Toast.LENGTH_SHORT).show();
                    return false;
                } else if ((cashBalance % min != 500 && cashBalance % min != 0) ||
                        (bankBalance % min != 500 && bankBalance % min != 0) ||
                        (cashBalance % min != 500 && creditBalance % min != 0)){
                    Toast.makeText(StartActivity.this, "Your balance(s) has invalid denominations", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }

            UserDAO userDAO = UserDAO.getInstance(StartActivity.this);
            if (userDAO.insertUser(new User(email, "", email))) {
                int userId = userDAO.getUserId(email);
                PreferencesUtils.setValue(PreferencesUtils.CURRENT_EMAIL, email);
                PreferencesUtils.setValue(PreferencesUtils.CURRENCY, currency);
                AccountDAO accountDAO = AccountDAO.getInstance(StartActivity.this);
                if (userId >= 0) {
                    boolean insertCash = accountDAO.insertAccount(
                            new Account("cash", "cash", currency, cashBalance, cashBalance, "activated", String.valueOf(userId), true));
                    boolean insertBank = accountDAO.insertAccount(
                            new Account("bank", "bank", currency, bankBalance, bankBalance, "activated", String.valueOf(userId), true));
                    boolean insertCredit = accountDAO.insertAccount(
                            new Account("credit_card", "credit_card", currency, creditBalance, creditBalance, "activated", String.valueOf(userId), true));
                    boolean success = insertCash && insertBank && insertCredit;
                    if (success) {
                        Toast.makeText(StartActivity.this, "Your info is saved", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(StartActivity.this, "Failed to store you data", Toast.LENGTH_SHORT).show();
                        accountDAO.deleteAllAccounts();
                        userDAO.deleteAllUsers();
                    }
                    return success;
                }
            }
        }
        return false;
    }
}
