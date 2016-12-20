package com.huy.monthlyfinance.Database.DAO;

import android.content.Context;

/**
 * Created by huy nguyen on 9/18/2016.
 */
class AccountDetailDAO extends BaseDAO {
    private static AccountDetailDAO mInstance;
    private AccountDetailDAO(Context context) {
        super(context);
    }

    public static AccountDetailDAO getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new AccountDetailDAO(context);
        }
        return mInstance;
    }
}
