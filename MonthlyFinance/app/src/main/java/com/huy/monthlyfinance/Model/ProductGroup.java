package com.huy.monthlyfinance.Model;

/**
 * Created by huy nguyen on 9/16/2016.
 */
public class ProductGroup extends BaseDTO{
    private String mProductGroupID;
    private String mGroupNameEN;
    private String mGroupNameVI;
    private String mGroupImage;

    public ProductGroup() {

    }

    public ProductGroup(String ProductGroupID, String GroupNameEN, String GroupNameVI, String GroupImage) {
        this.mProductGroupID = ProductGroupID;
        this.mGroupNameEN = GroupNameEN;
        this.mGroupNameVI = GroupNameVI;
        this.mGroupImage = GroupImage;
    }

    public ProductGroup(String GroupNameEN, String GroupNameVI, String GroupImage) {
        this.mGroupNameEN = GroupNameEN;
        this.mGroupNameVI = GroupNameVI;
        this.mGroupImage = GroupImage;
    }

    public String getProductGroupID() {
        return mProductGroupID;
    }

    public void setProductGroupID(String mProductGroupID) {
        this.mProductGroupID = mProductGroupID;
    }

    public String getGroupName() {
        return mGroupNameEN;
    }

    public void setGroupName(String mGroupName) {
        this.mGroupNameEN = mGroupName;
    }

    public String getGroupImage() {
        return mGroupImage;
    }

    public void setGroupImage(String mGroupImage) {
        this.mGroupImage = mGroupImage;
    }

    public String getGroupNameVI() {
        return mGroupNameVI;
    }

    public void setGroupNameVI(String mGroupNameVI) {
        this.mGroupNameVI = mGroupNameVI;
    }
}