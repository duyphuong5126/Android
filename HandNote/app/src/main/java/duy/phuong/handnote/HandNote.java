package duy.phuong.handnote;

import android.app.Application;

import duy.phuong.handnote.Support.SharedPreferenceUtils;

/**
 * Created by Phuong on 09/05/2016.
 */
public class HandNote extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferenceUtils.setPreferences(getApplicationContext());
    }
}
