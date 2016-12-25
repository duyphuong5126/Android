package com.huy.monthlyfinance.Database.DAO;

import android.content.Context;
import android.database.Cursor;

import com.huy.monthlyfinance.Database.DatabaseHelper;
import com.huy.monthlyfinance.Model.ProductDetail;

import java.util.ArrayList;

/**
 * Created by huy nguyen on 9/18/2016.
 */
public class ProductDetailDAO extends BaseDAO {

    private static ProductDetailDAO mInstance;

    private ProductDetailDAO(Context context) {
        super(context);
    }

    public static ProductDetailDAO getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ProductDetailDAO(context);
        }
        return mInstance;
    }

    public boolean insertProductDetail(ProductDetail productDetail) {
        mValues.clear();
        mValues.put(DatabaseHelper.productID, productDetail.getProductID());
        mValues.put(DatabaseHelper.expenseHistoryID, productDetail.getTransactionID());
        mValues.put(DatabaseHelper.productCost, productDetail.getProductCost());
        mValues.put(DatabaseHelper.productQuantity, productDetail.getProductQuantity());
        return mWritableDatabase.insert(DatabaseHelper.tblProductDetail, null, mValues) > 0;
    }

    public ArrayList<ProductDetail> getAllDetails() {
        String query = "select * from " + DatabaseHelper.tblProductDetail;
        Cursor cursor = mReadableDatabase.rawQuery(query, null);
        ArrayList<ProductDetail> productDetails = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                productDetails.add(new ProductDetail(cursor.getString(cursor.getColumnIndex(DatabaseHelper.productDetailID)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.productID)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.expenseHistoryID)),
                        Double.valueOf(cursor.getString(cursor.getColumnIndex(DatabaseHelper.productCost))),
                        Integer.valueOf(cursor.getString(cursor.getColumnIndex(DatabaseHelper.productQuantity)))));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return productDetails;
    }
}
