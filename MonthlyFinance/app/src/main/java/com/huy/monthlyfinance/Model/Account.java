package com.huy.monthlyfinance.Model;


/**
 * Created by huy nguyen on 9/16/2016.
 */

public class Account extends BaseDTO{
    private String mAccountID;
    private String mAccountName;
    private String mAccountType;
    private String mCurrency;
    private double mInitialBalance;
    private double mCurrentBalance;
    private String mNote;
    private String mUserID;
    private boolean mActiveStatus;

    public Account() {
        super();
    }
    public Account(String AccountID, String AccountName, String AccountType, String Currency, double InitialBalance, double CurrentBalance,
                   String Note, String UserId, boolean ActiveStatus) {
        super();
        this.mAccountID = AccountID;
        this.mAccountName = AccountName;
        this.mAccountType = AccountType;
        this.mCurrency = Currency;
        this.mInitialBalance = InitialBalance;
        this.mCurrentBalance = CurrentBalance;
        this.mNote = Note;
        this.mUserID = UserId;
        this.mActiveStatus = ActiveStatus;

    }
    public Account(String AccountName, String AccountType, String Currency, double InitialBalance, double CurrentBalance,
                   String Note, String UserId, boolean ActiveStatus) {
        super();
        this.mAccountName = AccountName;
        this.mAccountType = AccountType;
        this.mCurrency = Currency;
        this.mInitialBalance = InitialBalance;
        this.mCurrentBalance = CurrentBalance;
        this.mNote = Note;
        this.mUserID = UserId;
        this.mActiveStatus = ActiveStatus;

    }
    public String getAccountID() {
        return mAccountID;
    }

    public void setAccountID(String mAccountID) {
        this.mAccountID = mAccountID;
    }

    public String getAccountName() {
        return mAccountName;
    }

    public void setAccountName(String mAccountName) {
        this.mAccountName = mAccountName;
    }

    public String getAccountType() {
        return mAccountType;
    }

    public void setAccountType(String mAccountType) {
        this.mAccountType = mAccountType;
    }

    public String getCurrency() {
        return mCurrency;
    }

    public void setCurrency(String mCurrency) {
        this.mCurrency = mCurrency;
    }

    public double getCurrentBalance() {
        return mCurrentBalance;
    }

    public void setCurrentBalance(double mCurrentBalance) {
        this.mCurrentBalance = mCurrentBalance;
    }

    public String getNote() {
        return mNote;
    }

    public void setNote(String mNote) {
        this.mNote = mNote;
    }

    public String getUserID() {
        return mUserID;
    }

    public void setUserID(String mUserID) {
        this.mUserID = mUserID;
    }

    public boolean isActiveStatus() {
        return mActiveStatus;
    }

    public void setActiveStatus(boolean mActiveStatus) {
        this.mActiveStatus = mActiveStatus;
    }

    public double getInitialBalance() {
        return mInitialBalance;
    }

    public void setInitialBalance(double mInitialBalance) {
        this.mInitialBalance = mInitialBalance;
    }
}
