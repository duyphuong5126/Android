package com.huy.monthlyfinance.Model;


/**
 * Created by huy nguyen on 9/16/2016.
 */

public class ExpensesHistory extends BaseDTO{
    private String mTransactionID;
    private String mAccountID;
    private String mUserID;
    private String mTransactionDate;
    private double mTransactionCost;

    public ExpensesHistory(String TransactionID, String AccountID, String UserID, String TransactionDate, double TransactionCost) {
        this.mTransactionID = TransactionID;
        this.mAccountID = AccountID;
        this.mUserID = UserID;
        this.mTransactionDate = TransactionDate;
        this.mTransactionCost = TransactionCost;
    }

    public ExpensesHistory(String AccountID, String UserID, String TransactionDate, double TransactionCost) {
        this.mAccountID = AccountID;
        this.mUserID = UserID;
        this.mTransactionDate = TransactionDate;
        this.mTransactionCost = TransactionCost;
    }

    public String getTransactionID() {
        return mTransactionID;
    }

    public void setTransactionID(String mTransactionID) {
        this.mTransactionID = mTransactionID;
    }

    public String getAccountID() {
        return mAccountID;
    }

    public void setAccountID(String mAccountID) {
        this.mAccountID = mAccountID;
    }

    public String getUserID() {
        return mUserID;
    }

    public void setUserID(String mUserID) {
        this.mUserID = mUserID;
    }

    public String getTransactionDate() {
        return mTransactionDate;
    }

    public void setTransactionDate(String mTransactionDate) {
        this.mTransactionDate = mTransactionDate;
    }

    public double getTransactionCost() {
        return mTransactionCost;
    }

    public void setTransactionCost(double mTransactionCost) {
        this.mTransactionCost = mTransactionCost;
    }

}
