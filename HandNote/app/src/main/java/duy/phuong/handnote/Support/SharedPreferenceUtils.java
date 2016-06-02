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
    private static final String DICTIONARY_LOADED = "DictionaryLoaded";
    public static void setPreferences(Context context) {
        mContext = context;
    }

    public static boolean isViewIntro() {
        SharedPreferences preferences = mContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        return preferences.getBoolean(VIEW_INTRODUCTION, false);
    }

    public static boolean isLoadedDict() {
        SharedPreferences preferences = mContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        return preferences.getBoolean(DICTIONARY_LOADED, false);
    }

    public static String getCurrentName() {
        SharedPreferences preferences = mContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        return preferences.getString(CURRENT_NAME, "");
    }

    public static void viewedIntro(boolean viewed) {
        SharedPreferences preferences = mContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(VIEW_INTRODUCTION, viewed);
        editor.apply();
    }

    public static void loadedDict() {
        SharedPreferences preferences = mContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(DICTIONARY_LOADED, true);
        editor.apply();
    }

    public static void setCurrentName(String name) {
        SharedPreferences preferences = mContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(CURRENT_NAME, name);
        editor.apply();
    }
}
