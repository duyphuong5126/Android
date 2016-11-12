package com.huy.monthlyfinance.Database.DAO;

import android.content.Context;
import android.database.Cursor;

import com.huy.monthlyfinance.Database.DatabaseHelper;
import com.huy.monthlyfinance.Model.User;

/**
 * Created by huy nguyen on 9/18/2016.
 */
public class UserDAO extends BaseDAO{
    private UserDAO(Context context) {
        super(context);
    }

    private static UserDAO mInstance;

    public static UserDAO getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new UserDAO(context);
        }
        return mInstance;
    }

    public boolean insertUser(User user) {
        mValues.clear();
        mValues.put(DatabaseHelper.userLoginName, user.getUserName());
        mValues.put(DatabaseHelper.userPassword, user.getUserName());
        mValues.put(DatabaseHelper.userEmail, user.getEmail());
        return mWritableDatabase.insert(DatabaseHelper.tblUser, null, mValues) > 0;
    }

    public boolean checkUserExisted(User user) {
        String query = "select * from " + DatabaseHelper.tblUser +
                " where " + DatabaseHelper.userLoginName + " = '" + user.getUserName() + "' and " +
                DatabaseHelper.userPassword + " = '" + user.getPassword() + "'";
        Cursor cursor = mReadableDatabase.rawQuery(query, null);
        boolean result = cursor.moveToFirst();
        cursor.close();
        return result;
    }
}
