package com.horical.appnote.ServerStorage.Callback;

import com.horical.appnote.ServerStorage.Response.BaseResponse;
import com.parse.ParseException;

/**
 * Created by Phuong on 06/11/2015.
 */
public abstract class BaseCallback<T extends BaseResponse> {
    public abstract void onSuccess(T response);
    public abstract void onFail(ParseException e);
}
