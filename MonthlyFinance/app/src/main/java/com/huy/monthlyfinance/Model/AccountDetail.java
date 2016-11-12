package com.huy.monthlyfinance.Model;


/**
 * Created by huy nguyen on 9/16/2016.
 */

public class AccountDetail extends BaseDTO {
    private String mAccountDetailID;
    private String mAccountID;
    private double mCurrentBalance;
    private String mTransactionDate;

    public AccountDetail() {
    }

    public AccountDetail(String AccountDetailID, String AccountID, double CurrentBalance, String TransactionDate) {
        this.mAccountDetailID = AccountDetailID;
        this.mAccountID = AccountID;
        this.mCurrentBalance = CurrentBalance;
        this.mTransactionDate = TransactionDate;
    }

    public String getAccountDetailID() {
        return mAccountDetailID;
    }

    public void setAccountDetailID(String mAccountDetailID) {
        this.mAccountDetailID = mAccountDetailID;
    }

    public String getAccountID() {
        return mAccountID;
    }

    public void setAccountID(String mAccountID) {
        this.mAccountID = mAccountID;
    }

    public double getCurrentBalance() {
        return mCurrentBalance;
    }

    public void setCurrentBalance(double mCurrentBalance) {
        this.mCurrentBalance = mCurrentBalance;
    }

    public String getTransactionDate() {
        return mTransactionDate;
    }

    public void setTransactionDate(String mTransactionDate) {
        this.mTransactionDate = mTransactionDate;
    }

}
