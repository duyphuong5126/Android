package com.huy.monthlyfinance.Database.DAO;

import android.content.Context;
import android.database.Cursor;

import com.huy.monthlyfinance.Database.DatabaseHelper;
import com.huy.monthlyfinance.Model.ExpensesHistory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huy nguyen on 9/18/2016.
 */
public class ExpensesHistoryDAO extends BaseDAO {
    private static ExpensesHistoryDAO mInstance;
    private ExpensesHistoryDAO(Context context) {
        super(context);
    }
    public static ExpensesHistoryDAO getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ExpensesHistoryDAO(context);
        }
        return mInstance;
    }

    public boolean insertTransaction(ExpensesHistory history) {
        mValues.clear();
        mValues.put(DatabaseHelper.expenseDate, history.getTransactionDate());
        mValues.put(DatabaseHelper.expenseTotalCost, history.getTransactionCost());
        mValues.put(DatabaseHelper.accountID, history.getAccountID());
        mValues.put(DatabaseHelper.userID, history.getUserID());
        return mWritableDatabase.insert(DatabaseHelper.tblExpensesHistory, null, mValues) > 0;
    }

    public int getLatestTransactionID() {
        String sql = "select " + DatabaseHelper.expenseHistoryID + " from " + DatabaseHelper.tblExpensesHistory
                + " where " + DatabaseHelper.expenseHistoryID + " = ( select max(" + DatabaseHelper.expenseHistoryID + ")" +
                " from " + DatabaseHelper.tblExpensesHistory +")";
        int id = -1;
        Cursor cursor = mReadableDatabase.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.expenseHistoryID));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return id;
    }

    public List<ExpensesHistory> getListTransactions() {
        ArrayList<ExpensesHistory> list = new ArrayList<>();
        String sql = "select * from " + DatabaseHelper.tblExpensesHistory;
        Cursor cursor = mWritableDatabase.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                list.add(new ExpensesHistory(cursor.getString(cursor.getColumnIndex(DatabaseHelper.expenseHistoryID)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.accountID)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.userID)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.expenseDate)),
                        cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.expenseTotalCost))));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }
}
