package com.huy.monthlyfinance.Database.DAO;

import android.content.Context;
import android.database.Cursor;

import com.huy.monthlyfinance.Database.DatabaseHelper;
import com.huy.monthlyfinance.Model.Account;

import java.util.ArrayList;

/**
 * Created by huy nguyen on 9/18/2016.
 */
public class AccountDAO extends BaseDAO{
    private AccountDAO(Context context) {
        super(context);
    }

    private static AccountDAO mInstance;

    public static AccountDAO getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new AccountDAO(context);
        }
        return mInstance;
    }

    public boolean insertAccount(Account account) {
        mValues.clear();
        mValues.put(DatabaseHelper.accountName, account.getAccountName());
        mValues.put(DatabaseHelper.accountType, account.getAccountType());
        mValues.put(DatabaseHelper.accountCurrency, account.getCurrency());
        mValues.put(DatabaseHelper.accountCurrentBalance, account.getCurrentBalance());
        mValues.put(DatabaseHelper.accountInitBalance, account.getInitialBalance());
        mValues.put(DatabaseHelper.userID, account.getUserID());
        mValues.put(DatabaseHelper.accountState, account.isActive());
        mValues.put(DatabaseHelper.accountNote, account.getNote());
        return mWritableDatabase.insert(DatabaseHelper.tblAccount, null, mValues) > 0;
    }

    public void deleteAllAccounts() {
        mWritableDatabase.delete(DatabaseHelper.tblAccount, null ,null);
    }

    public boolean isAccountDataAvailable() {
        String query = "select * from " + DatabaseHelper.tblAccount;
        int count = 0;
        Cursor cursor = mReadableDatabase.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                count++;
            } while (cursor.moveToNext());
        }
        cursor.close();
        return count == 3;
    }

    public ArrayList<Account> getAllAccounts() {
        ArrayList<Account> accounts = new ArrayList<>();
        String query = "select * from " + DatabaseHelper.tblAccount;
        Cursor cursor = mReadableDatabase.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                accounts.add(new Account(cursor.getString(cursor.getColumnIndex(DatabaseHelper.accountID)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.accountName)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.accountType)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.accountCurrency)),
                        cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.accountInitBalance)),
                        cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.accountCurrentBalance)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.accountNote)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.userID)),
                        Boolean.valueOf(cursor.getString(cursor.getColumnIndex(DatabaseHelper.accountState)))));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return accounts;
    }

    public boolean updateAccount(String accountName, double value) {
        mValues.clear();
        mValues.put(DatabaseHelper.accountCurrentBalance, value);
        return mWritableDatabase.update(DatabaseHelper.tblAccount, mValues,
                DatabaseHelper.accountName + " = ?", new String[]{accountName}) > 0;
    }
}
