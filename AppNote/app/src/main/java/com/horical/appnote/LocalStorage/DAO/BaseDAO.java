package com.horical.appnote.LocalStorage.DAO;

import android.app.Activity;
import android.content.ContentResolver;

/**
 * Created by Phuong on 27/08/2015.
 */
public abstract class BaseDAO {
    protected Activity mActivity;
    protected ContentResolver mContentResolver;

    protected void init(Activity activity){
        this.mActivity = activity;
        this.mContentResolver = activity.getContentResolver();
    }

    abstract public void Send();
    abstract public void Receive();
    abstract public void Parse();
}
