package com.huy.monthlyfinance.Model;


/**
 * Created by huy nguyen on 9/16/2016.
 */

public class ExpensesHistory extends BaseDTO{
    private String mTransactionID;
    private String mProductDetailID;
    private String mAccountID;
    private String mUserID;
    private String mTransactionDate;
    private double mTransactionCost;

    public ExpensesHistory() {
        super();
    }

    public ExpensesHistory(String TransactionID, String ProductDetailID, String AccountID, String UserID, String TransactionDate, double TransactionCost) {
        super();
        this.mTransactionID = TransactionID;
        this.mProductDetailID = ProductDetailID;
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

    public String getProductDetailID() {
        return mProductDetailID;
    }

    public void setProductDetailID(String mProductDetailID) {
        this.mProductDetailID = mProductDetailID;
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
