package com.huy.monthlyfinance.Database.DAO;

import android.content.Context;
import android.database.Cursor;

import com.huy.monthlyfinance.Database.DatabaseHelper;
import com.huy.monthlyfinance.Model.ProductGroup;
import com.huy.monthlyfinance.SupportUtils.SupportUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huy nguyen on 9/18/2016.
 */
public class ProductGroupDAO extends BaseDAO {
    private static ProductGroupDAO mInstance;

    //constructor
    private ProductGroupDAO(Context context) {
        super(context);
    }

    public static ProductGroupDAO getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ProductGroupDAO(context);
        }
        return mInstance;
    }

    // ham them moi mot nhom san pham
    public boolean insertProductGroup(ProductGroup productGroup) {
        mValues.put(DatabaseHelper.productGroupNameEN, productGroup.getGroupNameEN());
        mValues.put(DatabaseHelper.productGroupNameVI, productGroup.getGroupNameVI());
        mValues.put(DatabaseHelper.productGroupImage, productGroup.getGroupImage());
        boolean result = mWritableDatabase.insert(DatabaseHelper.tblProductGroup, null, mValues) > 0;
        if (result) {
            mMessage = "insert successful";
        } else {
            mMessage = "Fail to insert in Nhomsanpham";
        }
        mValues.clear();
        return result;
    }

    // ham cap nhat nhom san pham
    public int updateProductGroup(ProductGroup n) {
        mValues.put(DatabaseHelper.productGroupNameVI, n.getGroupNameEN());
        mValues.put(DatabaseHelper.productImage, n.getGroupImage());
        int result = mReadableDatabase.update(DatabaseHelper.tblProductGroup, mValues, DatabaseHelper.productGroupID + "=?",
                new String[]{String.valueOf(n.getProductGroupID())});
        mValues.clear();
        return result;
    }

    // ham xoa mot nhom san pham
    public void delete(ProductGroup n) {
        mReadableDatabase.delete(DatabaseHelper.tblProductGroup, DatabaseHelper.productGroupID + "=?",
                new String[]{String.valueOf(n.getProductGroupID())});
        mValues.clear();
    }

    // ham lay danh sach nhom san pham
    public List<ProductGroup> getAllProductGroup() {
        List<ProductGroup> productGroupList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + DatabaseHelper.tblProductGroup;
        Cursor cursor = mReadableDatabase.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                ProductGroup productGroup = new ProductGroup();
                productGroup.setProductGroupID(String.valueOf(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.productGroupID))));
                productGroup.setGroupNameEN(cursor.getString(cursor.getColumnIndex(DatabaseHelper.productGroupNameEN)));
                productGroup.setGroupNameVI(cursor.getString(cursor.getColumnIndex(DatabaseHelper.productGroupNameVI)));
                productGroup.setGroupImage(cursor.getString(cursor.getColumnIndex(DatabaseHelper.productGroupImage)));
                // Adding contact to list
                productGroupList.add(productGroup);
            } while (cursor.moveToNext());
        }
        cursor.close();
        mValues.clear();
        // return nhom san pham list
        return productGroupList;
    }

    public int getGroupIDByName(String name) {
        String selectQuery = "select " + DatabaseHelper.productGroupID + " from " + DatabaseHelper.tblProductGroup
                + " where " + DatabaseHelper.productGroupNameVI + " = '" + name + "' or " +
                DatabaseHelper.productGroupNameEN + " = '" + name + "'";
        Cursor cursor = mReadableDatabase.rawQuery(selectQuery, null);
        int id = -1;
        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.productGroupID));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return id;
    }

    public String getGroupNameByID(String id) {
        String selectQuery = "select * from " + DatabaseHelper.tblProductGroup
                + " where " + DatabaseHelper.productGroupID + " = " + id;
        Cursor cursor = mReadableDatabase.rawQuery(selectQuery, null);
        String nameEN = "";
        String nameVI = "";
        if (cursor.moveToFirst()) {
            do {
                nameEN = cursor.getString(cursor.getColumnIndex(DatabaseHelper.productGroupNameEN));
                nameVI = cursor.getString(cursor.getColumnIndex(DatabaseHelper.productGroupNameVI));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return (SupportUtils.getCountryCode().toLowerCase().contains("us") ? nameEN : nameVI);
    }
}
