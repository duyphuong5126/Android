package com.horical.appnote.ServerStorage;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Phuong on 06/11/2015.
 */
public abstract class BaseRoute<T extends ParseObject> {
    private Class<T> mType;

    public interface GetDataCallback<T>{
        void onReceiveData(ArrayList<T> data);
        void onError(ParseException e);
    }

    public BaseRoute(Class<T> type) {
        this.mType = type;
    }

    protected void getAllDataFromServer(final GetDataCallback<T> callback) {
        ParseQuery<T> query = ParseQuery.getQuery(mType);
        query.findInBackground(new FindCallback<T>() {
            @Override
            public void done(List<T> objects, ParseException e) {
                if (e == null) {
                    ArrayList<T> data = new ArrayList<T>();
                    if (objects != null) {
                        data.addAll(objects);
                        callback.onReceiveData(data);
                    } else {
                        Log.e("Error", "Get data failure");
                        callback.onError(e);
                    }
                }
            }
        });
    }

    protected void getAllDataFromServer(final GetDataCallback<T> callback, HashMap<String, String> conditions) {
        ParseQuery<T> query = ParseQuery.getQuery(mType);
        if (conditions != null && !conditions.isEmpty()) {
            for (Map.Entry<String, String> entry : conditions.entrySet()) {
                query.whereEqualTo(entry.getKey(), entry.getValue());
            }
        }
        query.findInBackground(new FindCallback<T>() {
            @Override
            public void done(List<T> objects, ParseException e) {
                if (e == null) {
                    ArrayList<T> data = new ArrayList<T>();
                    if (objects != null) {
                        data.addAll(objects);
                        callback.onReceiveData(data);
                    } else {
                        Log.e("Error", "Get data failure");
                        callback.onError(e);
                    }
                }
            }
        });
    }
}
