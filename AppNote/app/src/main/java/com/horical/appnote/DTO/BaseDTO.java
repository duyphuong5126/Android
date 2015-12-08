package com.horical.appnote.DTO;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Phuong on 24/07/2015.
 */
public abstract class BaseDTO<T> implements Serializable {
    public static final int NOTE_OBJECT = 1;
    public static final int ACCOUNT_OBJECT = 2;
    public static final int CALENDAR_OBJECT = 3;
    public static final int FILE_OBJECT = 4;

    protected int ID;

    public abstract int checkTypeDTO();

    public abstract T parse(JSONObject jsonObject) throws JSONException;

    public abstract String createJSON();
}
