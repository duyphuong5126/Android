package com.huy.monthlyfinance.SupportUtils;

/**
 * Created by Phuong on 08/01/2017.
 */

public class NameValuePair<T extends Object, V extends Object> {
    private T mKey;
    private V mValue;

    public NameValuePair(T Key, V Value) {
        this.mKey = Key;
        this.mValue = Value;
    }

    public NameValuePair setKey(T mKey) {
        this.mKey = mKey;
        return this;
    }

    public NameValuePair setValue(V mValue) {
        this.mValue = mValue;
        return this;
    }

    public T getKey() {
        return mKey;
    }

    public V getValue() {
        return mValue;
    }
}
