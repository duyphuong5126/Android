package com.huy.monthlyfinance.Database.DAO;

import android.content.Context;
import android.database.Cursor;

import com.huy.monthlyfinance.Database.DatabaseHelper;
import com.huy.monthlyfinance.Model.Account;

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
        mValues.put(DatabaseHelper.accountName, account.getAccountType());
        mValues.put(DatabaseHelper.accountName, account.getCurrency());
        mValues.put(DatabaseHelper.accountName, account.getCurrentBalance());
        mValues.put(DatabaseHelper.accountName, account.getAccountName());
        return mWritableDatabase.insert(DatabaseHelper.tblAccount, null, mValues) > 0;
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
}
