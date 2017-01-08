package com.huy.monthlyfinance;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

import com.huy.monthlyfinance.Database.DAO.AccountDAO;
import com.huy.monthlyfinance.Database.DAO.ProductDAO;
import com.huy.monthlyfinance.Database.DAO.ProductDetailDAO;
import com.huy.monthlyfinance.Database.DAO.ProductGroupDAO;
import com.huy.monthlyfinance.Database.DatabaseHelper;
import com.huy.monthlyfinance.Listener.DataChangeListener;
import com.huy.monthlyfinance.Model.Account;
import com.huy.monthlyfinance.Model.Product;
import com.huy.monthlyfinance.Model.ProductDetail;
import com.huy.monthlyfinance.Model.ProductGroup;
import com.huy.monthlyfinance.SupportUtils.SupportUtils;

import java.util.ArrayList;

/**
 * Created by Phuong on 07/11/2016.
 */

public class MainApplication extends Application {
    private ArrayList<ProductGroup> mProductGroups;
    private ArrayList<Product> mProducts;
    private ArrayList<Account> mAccounts;
    private ArrayList<DataChangeListener> mListeners;
    private ArrayList<ProductDetail> mProductDetails;

    private static MainApplication mInstance;

    public static MainApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (mInstance == null) {
            mInstance = this;
        }

        Context context = getApplicationContext();
        Resources res = context.getResources();
        //context.deleteDatabase(DatabaseHelper.DATABASE_NAME);
        ProductGroupDAO mProductGroupDAO = ProductGroupDAO.getInstance(context);
        mProductGroups = new ArrayList<>();
        mProductGroups.addAll(mProductGroupDAO.getAllProductGroup());
        if (mProductGroupDAO.getAllProductGroup().isEmpty()) {
            mProductGroups.add(new ProductGroup(SupportUtils.getStringLocalized(context, "en", R.string.bill),
                    SupportUtils.getStringLocalized(context, "vi", R.string.bill), res.getResourceEntryName(R.drawable.receipt)));
            mProductGroups.add(new ProductGroup(SupportUtils.getStringLocalized(context, "en", R.string.health),
                    SupportUtils.getStringLocalized(context, "vi", R.string.health), res.getResourceEntryName(R.drawable.stethoscope)));
            mProductGroups.add(new ProductGroup(SupportUtils.getStringLocalized(context, "en", R.string.entertainment),
                    SupportUtils.getStringLocalized(context, "vi", R.string.entertainment), res.getResourceEntryName(R.drawable.game_controller)));
            mProductGroups.add(new ProductGroup(SupportUtils.getStringLocalized(context, "en", R.string.food),
                    SupportUtils.getStringLocalized(context, "vi", R.string.food), res.getResourceEntryName(R.drawable.turkey)));
            mProductGroups.add(new ProductGroup(SupportUtils.getStringLocalized(context, "en", R.string.dress),
                    SupportUtils.getStringLocalized(context, "vi", R.string.dress), res.getResourceEntryName(R.drawable.shirt)));
            mProductGroups.add(new ProductGroup(SupportUtils.getStringLocalized(context, "en", R.string.transport),
                    SupportUtils.getStringLocalized(context, "vi", R.string.transport), res.getResourceEntryName(R.drawable.car)));
            mProductGroups.add(new ProductGroup(SupportUtils.getStringLocalized(context, "en", R.string.home),
                    SupportUtils.getStringLocalized(context, "vi", R.string.home), res.getResourceEntryName(R.drawable.home)));
            mProductGroups.add(new ProductGroup(SupportUtils.getStringLocalized(context, "en", R.string.family),
                    SupportUtils.getStringLocalized(context, "vi", R.string.family), res.getResourceEntryName(R.drawable.family)));
            mProductGroups.add(new ProductGroup(SupportUtils.getStringLocalized(context, "en", R.string.etc),
                    SupportUtils.getStringLocalized(context, "vi", R.string.etc), res.getResourceEntryName(R.mipmap.ic_more_horiz_white_24dp)));
            boolean result = true;
            for (int i = 0; i < mProductGroups.size() && result; i++) {
                result = mProductGroupDAO.insertProductGroup(mProductGroups.get(i));
                int id = mProductGroupDAO.getGroupIDByName(mProductGroups.get(i).getGroupNameEN());
                mProductGroups.get(i).setProductGroupID(String.valueOf(id));
            }
        }

        ProductDAO mProductDAO = ProductDAO.getInstance(context);
        mProducts = new ArrayList<>();
        mProducts.addAll(mProductDAO.getAllProduct());
        if (mProducts.isEmpty()) {
            mProductDAO.doInsertTblProduct(new Product(SupportUtils.getStringLocalized(context, "en", R.string.sample_1),
                    SupportUtils.getStringLocalized(context, "vi", R.string.sample_1),
                    String.valueOf(mProductGroupDAO.getGroupIDByName(mProductGroups.get(3).getGroupNameEN())), "",
                    res.getResourceEntryName(R.drawable.turkey)));
            mProductDAO.doInsertTblProduct(new Product(SupportUtils.getStringLocalized(context, "en", R.string.sample_2),
                    SupportUtils.getStringLocalized(context, "vi", R.string.sample_2),
                    String.valueOf(mProductGroupDAO.getGroupIDByName(mProductGroups.get(3).getGroupNameEN())), "",
                    res.getResourceEntryName(R.drawable.salad)));
            mProductDAO.doInsertTblProduct(new Product(SupportUtils.getStringLocalized(context, "en", R.string.sample_3),
                    SupportUtils.getStringLocalized(context, "vi", R.string.sample_3),
                    String.valueOf(mProductGroupDAO.getGroupIDByName(mProductGroups.get(3).getGroupNameEN())), "",
                    res.getResourceEntryName(R.drawable.hamburguer)));
            mProductDAO.doInsertTblProduct(new Product(SupportUtils.getStringLocalized(context, "en", R.string.sample_4),
                    SupportUtils.getStringLocalized(context, "vi", R.string.sample_4),
                    String.valueOf(mProductGroupDAO.getGroupIDByName(mProductGroups.get(3).getGroupNameEN())), "",
                    res.getResourceEntryName(R.drawable.rice)));
            mProductDAO.doInsertTblProduct(new Product(SupportUtils.getStringLocalized(context, "en", R.string.sample_5),
                    SupportUtils.getStringLocalized(context, "vi", R.string.sample_5),
                    String.valueOf(mProductGroupDAO.getGroupIDByName(mProductGroups.get(3).getGroupNameEN())), "",
                    res.getResourceEntryName(R.drawable.can)));
            mProductDAO.doInsertTblProduct(new Product(SupportUtils.getStringLocalized(context, "en", R.string.sample_6),
                    SupportUtils.getStringLocalized(context, "vi", R.string.sample_6),
                    String.valueOf(mProductGroupDAO.getGroupIDByName(mProductGroups.get(4).getGroupNameEN())), "",
                    res.getResourceEntryName(R.drawable.shirt)));
            mProductDAO.doInsertTblProduct(new Product(SupportUtils.getStringLocalized(context, "en", R.string.sample_7),
                    SupportUtils.getStringLocalized(context, "vi", R.string.sample_7),
                    String.valueOf(mProductGroupDAO.getGroupIDByName(mProductGroups.get(4).getGroupNameEN())), "",
                    res.getResourceEntryName(R.drawable.shoe)));
            mProductDAO.doInsertTblProduct(new Product(SupportUtils.getStringLocalized(context, "en", R.string.sample_8),
                    SupportUtils.getStringLocalized(context, "vi", R.string.sample_8),
                    String.valueOf(mProductGroupDAO.getGroupIDByName(mProductGroups.get(4).getGroupNameEN())), "",
                    res.getResourceEntryName(R.drawable.dress)));
            mProductDAO.doInsertTblProduct(new Product(SupportUtils.getStringLocalized(context, "en", R.string.sample_9),
                    SupportUtils.getStringLocalized(context, "vi", R.string.sample_9),
                    String.valueOf(mProductGroupDAO.getGroupIDByName(mProductGroups.get(4).getGroupNameEN())), "",
                    res.getResourceEntryName(R.drawable.jacket)));
            mProductDAO.doInsertTblProduct(new Product(SupportUtils.getStringLocalized(context, "en", R.string.sample_10),
                    SupportUtils.getStringLocalized(context, "vi", R.string.sample_10),
                    String.valueOf(mProductGroupDAO.getGroupIDByName(mProductGroups.get(4).getGroupNameEN())), "",
                    res.getResourceEntryName(R.drawable.pants)));
            mProductDAO.doInsertTblProduct(new Product(SupportUtils.getStringLocalized(context, "en", R.string.sample_11),
                    SupportUtils.getStringLocalized(context, "vi", R.string.sample_11),
                    String.valueOf(mProductGroupDAO.getGroupIDByName(mProductGroups.get(7).getGroupNameEN())), "",
                    res.getResourceEntryName(R.drawable.copier)));
            mProductDAO.doInsertTblProduct(new Product(SupportUtils.getStringLocalized(context, "en", R.string.sample_12),
                    SupportUtils.getStringLocalized(context, "vi", R.string.sample_12),
                    String.valueOf(mProductGroupDAO.getGroupIDByName(mProductGroups.get(7).getGroupNameEN())), "",
                    res.getResourceEntryName(R.drawable.bookshelf)));
            mProductDAO.doInsertTblProduct(new Product(SupportUtils.getStringLocalized(context, "en", R.string.sample_13),
                    SupportUtils.getStringLocalized(context, "vi", R.string.sample_13),
                    String.valueOf(mProductGroupDAO.getGroupIDByName(mProductGroups.get(7).getGroupNameEN())), "",
                    res.getResourceEntryName(R.drawable.writing_tool)));
            mProductDAO.doInsertTblProduct(new Product(SupportUtils.getStringLocalized(context, "en", R.string.sample_14),
                    SupportUtils.getStringLocalized(context, "vi", R.string.sample_14),
                    String.valueOf(mProductGroupDAO.getGroupIDByName(mProductGroups.get(7).getGroupNameEN())), "",
                    res.getResourceEntryName(R.drawable.desktop_computer)));
            mProductDAO.doInsertTblProduct(new Product(SupportUtils.getStringLocalized(context, "en", R.string.sample_15),
                    SupportUtils.getStringLocalized(context, "vi", R.string.sample_15),
                    String.valueOf(mProductGroupDAO.getGroupIDByName(mProductGroups.get(7).getGroupNameEN())), "",
                    res.getResourceEntryName(R.drawable.laptop)));
            mProductDAO.doInsertTblProduct(new Product(SupportUtils.getStringLocalized(context, "en", R.string.sample_16),
                    SupportUtils.getStringLocalized(context, "vi", R.string.sample_16),
                    String.valueOf(mProductGroupDAO.getGroupIDByName(mProductGroups.get(7).getGroupNameEN())), "",
                    res.getResourceEntryName(R.drawable.smartphone)));
            mProductDAO.doInsertTblProduct(new Product(SupportUtils.getStringLocalized(context, "en", R.string.sample_17),
                    SupportUtils.getStringLocalized(context, "vi", R.string.sample_17),
                    String.valueOf(mProductGroupDAO.getGroupIDByName(mProductGroups.get(7).getGroupNameEN())), "",
                    res.getResourceEntryName(R.drawable.smartwatch)));
            mProductDAO.doInsertTblProduct(new Product(SupportUtils.getStringLocalized(context, "en", R.string.sample_18),
                    SupportUtils.getStringLocalized(context, "vi", R.string.sample_18),
                    String.valueOf(mProductGroupDAO.getGroupIDByName(mProductGroups.get(7).getGroupNameEN())), "",
                    res.getResourceEntryName(R.drawable.mouse)));
            mProductDAO.doInsertTblProduct(new Product(SupportUtils.getStringLocalized(context, "en", R.string.sample_19),
                    SupportUtils.getStringLocalized(context, "vi", R.string.sample_19),
                    String.valueOf(mProductGroupDAO.getGroupIDByName(mProductGroups.get(7).getGroupNameEN())), "",
                    res.getResourceEntryName(R.drawable.camera)));
            mProductDAO.doInsertTblProduct(new Product(SupportUtils.getStringLocalized(context, "en", R.string.sample_20),
                    SupportUtils.getStringLocalized(context, "vi", R.string.sample_20),
                    String.valueOf(mProductGroupDAO.getGroupIDByName(mProductGroups.get(7).getGroupNameEN())), "",
                    res.getResourceEntryName(R.drawable.pendrive)));
            mProductDAO.doInsertTblProduct(new Product(SupportUtils.getStringLocalized(context, "en", R.string.sample_21),
                    SupportUtils.getStringLocalized(context, "vi", R.string.sample_21),
                    String.valueOf(mProductGroupDAO.getGroupIDByName(mProductGroups.get(7).getGroupNameEN())), "",
                    res.getResourceEntryName(R.drawable.headset)));
            mProductDAO.doInsertTblProduct(new Product(SupportUtils.getStringLocalized(context, "en", R.string.sample_22),
                    SupportUtils.getStringLocalized(context, "vi", R.string.sample_22),
                    String.valueOf(mProductGroupDAO.getGroupIDByName(mProductGroups.get(7).getGroupNameEN())), "",
                    res.getResourceEntryName(R.drawable.desk_lamp)));
            mProductDAO.doInsertTblProduct(new Product(SupportUtils.getStringLocalized(context, "en", R.string.sample_23),
                    SupportUtils.getStringLocalized(context, "vi", R.string.sample_23),
                    String.valueOf(mProductGroupDAO.getGroupIDByName(mProductGroups.get(7).getGroupNameEN())), "",
                    res.getResourceEntryName(R.drawable.cooler)));
            mProductDAO.doInsertTblProduct(new Product(SupportUtils.getStringLocalized(context, "en", R.string.sample_24),
                    SupportUtils.getStringLocalized(context, "vi", R.string.sample_24),
                    String.valueOf(mProductGroupDAO.getGroupIDByName(mProductGroups.get(7).getGroupNameEN())), "",
                    res.getResourceEntryName(R.drawable.television)));
            mProductDAO.doInsertTblProduct(new Product(SupportUtils.getStringLocalized(context, "en", R.string.sample_25),
                    SupportUtils.getStringLocalized(context, "vi", R.string.sample_25),
                    String.valueOf(mProductGroupDAO.getGroupIDByName(mProductGroups.get(5).getGroupNameEN())), "",
                    res.getResourceEntryName(R.drawable.gas_pipe)));
            mProductDAO.doInsertTblProduct(new Product(SupportUtils.getStringLocalized(context, "en", R.string.sample_26),
                    SupportUtils.getStringLocalized(context, "vi", R.string.sample_26),
                    String.valueOf(mProductGroupDAO.getGroupIDByName(mProductGroups.get(5).getGroupNameEN())), "",
                    res.getResourceEntryName(R.drawable.gas_station)));
            mProductDAO.doInsertTblProduct(new Product(SupportUtils.getStringLocalized(context, "en", R.string.sample_27),
                    SupportUtils.getStringLocalized(context, "vi", R.string.sample_27),
                    String.valueOf(mProductGroupDAO.getGroupIDByName(mProductGroups.get(5).getGroupNameEN())), "",
                    res.getResourceEntryName(R.drawable.oil)));
            mProductDAO.doInsertTblProduct(new Product(SupportUtils.getStringLocalized(context, "en", R.string.sample_28),
                    SupportUtils.getStringLocalized(context, "vi", R.string.sample_28),
                    String.valueOf(mProductGroupDAO.getGroupIDByName(mProductGroups.get(1).getGroupNameEN())), "",
                    res.getResourceEntryName(R.drawable.band_aid)));
            mProductDAO.doInsertTblProduct(new Product(SupportUtils.getStringLocalized(context, "en", R.string.sample_29),
                    SupportUtils.getStringLocalized(context, "vi", R.string.sample_29),
                    String.valueOf(mProductGroupDAO.getGroupIDByName(mProductGroups.get(1).getGroupNameEN())), "",
                    res.getResourceEntryName(R.drawable.syringe)));
            mProductDAO.doInsertTblProduct(new Product(SupportUtils.getStringLocalized(context, "en", R.string.sample_30),
                    SupportUtils.getStringLocalized(context, "vi", R.string.sample_30),
                    String.valueOf(mProductGroupDAO.getGroupIDByName(mProductGroups.get(1).getGroupNameEN())), "",
                    res.getResourceEntryName(R.drawable.pills)));
        }
        if (mListeners == null) {
            mListeners = new ArrayList<>();
        }
    }

    public ArrayList<ProductGroup> getProductGroups() {
        return mProductGroups;
    }

    public ArrayList<Product> getProducts() {
        return mProducts;
    }

    public ArrayList<Account> getAccounts() {
        if (mAccounts == null) {
            mAccounts = new ArrayList<>();
        }
        if (mAccounts.isEmpty()) {
            mAccounts.addAll(AccountDAO.getInstance(getApplicationContext()).getAllAccounts());
        }
        return mAccounts;
    }

    public ArrayList<ProductDetail> getProductDetails() {
        if (mProductDetails == null) {
            mProductDetails = new ArrayList<>();
        }
        if (mProductDetails.isEmpty()) {
            mProductDetails.addAll(ProductDetailDAO.getInstance(getApplicationContext()).getAllDetails());
        }
        return mProductDetails;
    }

    public void refreshAllData() {
        Context context = getApplicationContext();
        if (mProductGroups == null) {
            mProductGroups = new ArrayList<>();
        }
        mProductGroups.clear();
        mProductGroups.addAll(ProductGroupDAO.getInstance(context).getAllProductGroup());
        if (mProducts == null) {
            mProducts = new ArrayList<>();
        }
        mProducts.clear();
        mProducts.addAll(ProductDAO.getInstance(context).getAllProduct());
        if (mAccounts == null) {
            mAccounts = new ArrayList<>();
        }
        mAccounts.clear();
        mAccounts.addAll(AccountDAO.getInstance(context).getAllAccounts());
        if (mProductDetails == null) {
            mProductDetails = new ArrayList<>();
        }
        mProductDetails.clear();
        mProductDetails.addAll(ProductDetailDAO.getInstance(getApplicationContext()).getAllDetails());
        for (DataChangeListener listener : mListeners) {
            listener.refreshData();
        }
    }

    public void registerDataListener(DataChangeListener listener) {
        if (listener != null) {
            mListeners.add(listener);
        }
    }
}
