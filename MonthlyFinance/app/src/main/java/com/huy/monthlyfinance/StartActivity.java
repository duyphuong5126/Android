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

import com.huy.monthlyfinance.Database.DAO.AccountDAO;
import com.huy.monthlyfinance.SupportUtils.PreferencesUtils;
import com.huy.monthlyfinance.SupportUtils.SupportUtils;

public class StartActivity extends Activity implements View.OnClickListener{
    private LinearLayout mLayoutLogin;
    private LinearLayout mLayoutInitInfo;
    private AccountDAO mAccountDataSource;
    private EditText mEdtCash;
    private EditText mEdtBank;
    private EditText mEdtCreditCard;
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
        mAccountDataSource = AccountDAO.getInstance(context);

        mLayoutLogin = (LinearLayout) findViewById(R.id.layoutLogin);
        mLayoutInitInfo = (LinearLayout) findViewById(R.id.layoutInitInfo);

        mEdtCash = (EditText) findViewById(R.id.edtCash);
        mEdtBank = (EditText) findViewById(R.id.edtBank);
        mEdtCreditCard = (EditText) findViewById(R.id.edtCreditCard);
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
                PreferencesUtils.setValue(PreferencesUtils.isInfoInitialized, true);
                startMainActivity();
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
}
