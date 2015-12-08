package com.horical.appnote.BaseActivity;

import android.net.Uri;
import android.support.v4.app.FragmentActivity;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Stack;

/**
 * Created by Phuong on 24/07/2015.
 */
public abstract class BaseActivity extends FragmentActivity {
    protected Stack<String> mListFragments = new Stack<String>();
    protected HttpURLConnection mConnection;

    public interface ActivityAction {
        void NewFragment(String fragment_id);
    }

    protected ActivityAction mActivityAction;

    public void setActivityAction(ActivityAction activityAction){
        this.mActivityAction = activityAction;
    }

    protected boolean checkInternetAvailibility() {
        URL url;
        try {
            url = new URL("https://www.google.com/");
            mConnection = (HttpURLConnection) url.openConnection();
            mConnection.setRequestProperty("User-Agent", "Test");
            mConnection.setRequestProperty("Connection", "close");
            mConnection.setConnectTimeout(5000);
            mConnection.setReadTimeout(5000);
            mConnection.connect();
            return mConnection.getResponseCode() == 200;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
