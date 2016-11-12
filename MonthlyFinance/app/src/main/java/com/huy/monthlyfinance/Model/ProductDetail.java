package com.huy.monthlyfinance.Model;


/**
 * Created by huy nguyen on 9/16/2016.
 */

public class ProductDetail extends BaseDTO {

    private String mProductDetailID;

    private String mProductID;

    private double mProductCost;

    public ProductDetail( ) {
        super();
    }

    public ProductDetail(String ProductDetailID, String ProductID, double ProductCost) {
        super();
        this.mProductDetailID = ProductDetailID;
        this.mProductID = ProductID;
        this.mProductCost = ProductCost;
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
}
