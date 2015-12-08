package com.horical.appnote.ServerStorage.Callback;

import com.horical.appnote.DTO.BaseDTO;
import com.horical.appnote.ServerStorage.Response.NoteResponse;
import com.parse.ParseException;

import java.util.ArrayList;

/**
 * Created by Phuong on 13/11/2015.
 */
public abstract class DownloadDataCallback<T extends BaseDTO> {
    public abstract void onDownloadSuccess(ArrayList<T> data);
    public abstract void onFail(NoteResponse response);
}
