package duy.phuong.handnote.Support;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Phuong on 09/05/2016.
 */
public class SharedPreferenceUtils {
    private static Context mContext;
    private static final String PREFERENCES_NAME = "Preferences";
    private static final String VIEW_INTRODUCTION = "ViewIntro";
    private static final String CURRENT_NAME = "CurrentName";
    public static void setPreferences(Context context) {
        mContext = context;
    }

    public static boolean isViewIntro() {
        SharedPreferences preferences = mContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        return preferences.getBoolean(VIEW_INTRODUCTION, false);
    }

    public static String getCurrentName() {
        SharedPreferences preferences = mContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        return preferences.getString(CURRENT_NAME, "");
    }

    public static void viewedIntro() {
        SharedPreferences preferences = mContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(VIEW_INTRODUCTION, true);
        editor.apply();
    }

    public static void setCurrentName(String name) {
        SharedPreferences preferences = mContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(CURRENT_NAME, name);
        editor.apply();
    }
}
