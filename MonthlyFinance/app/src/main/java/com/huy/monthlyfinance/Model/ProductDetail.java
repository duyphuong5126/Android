package com.huy.monthlyfinance.Model;


/**
 * Created by huy nguyen on 9/16/2016.
 */

public class ProductDetail extends BaseDTO {
    private String mProductDetailID;
    private String mProductID;
    private String mTransactionID;
    private double mProductCost;
    private int mProductQuantity;

    public ProductDetail(String mProductDetailID, String mProductID, String mTransactionID, double mProductCost, int mProductQuantity) {
        this.mProductDetailID = mProductDetailID;
        this.mProductID = mProductID;
        this.mTransactionID = mTransactionID;
        this.mProductCost = mProductCost;
        this.mProductQuantity = mProductQuantity;
    }

    public ProductDetail(String mProductID, String mTransactionID, double mProductCost, int mProductQuantity) {
        this.mProductID = mProductID;
        this.mTransactionID = mTransactionID;
        this.mProductCost = mProductCost;
        this.mProductQuantity = mProductQuantity;
    }

    public String getProductDetailID() {
        return mProductDetailID;
    }

    public void setProductDetailID(String mProductDetailID) {
        this.mProductDetailID = mProductDetailID;
    }

    public String getProductID() {
        return mProductID;
    }

    public void setProductID(String mProductID) {
        this.mProductID = mProductID;
    }

    public double getProductCost() {
        return mProductCost;
    }

    public void setProductCost(double mProductCost) {
        this.mProductCost = mProductCost;
    }

    public String getTransactionID() {
        return mTransactionID;
    }

    public void setTransactionID(String mTransactionID) {
        this.mTransactionID = mTransactionID;
    }

    public int getProductQuantity() {
        return mProductQuantity;
    }

    public void setProductQuantity(int mProductQuantity) {
        this.mProductQuantity = mProductQuantity;
    }
}
