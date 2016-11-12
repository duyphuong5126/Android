package com.huy.monthlyfinance.Database.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.huy.monthlyfinance.Database.DatabaseHelper;

/**
 * Created by Phuong on 02/11/2016.
 */

abstract class BaseDAO {
    private static DatabaseHelper mHelper;
    ContentValues mValues;
    String mMessage;
    static SQLiteDatabase mReadableDatabase;
    static SQLiteDatabase mWritableDatabase;

    BaseDAO(Context context) {
        if (mHelper == null) {
            mHelper = new DatabaseHelper(context);
        }

        if (mValues == null) {
            mValues = new ContentValues();
        }

        if (mReadableDatabase == null) {
            mReadableDatabase = mHelper.getReadableDatabase();
        }
        if (mWritableDatabase == null) {
            mWritableDatabase = mHelper.getWritableDatabase();
        }
    }
}
