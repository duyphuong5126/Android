package duy.phuong.handnote;

import android.app.Application;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import duy.phuong.handnote.DAO.LocalStorage;
import duy.phuong.handnote.Support.SharedPreferenceUtils;

/**
 * Created by Phuong on 09/05/2016.
 */
public class HandNote extends Application {
    private LocalStorage mStorage;
    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferenceUtils.setPreferences(getApplicationContext());
        mStorage = new LocalStorage(getApplicationContext());
    }
    public boolean checkInternetAvailability() {
        HttpURLConnection mConnection;
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

    public void deleteAllNote() {
        mStorage.deleteAllNotes();
    }
}
