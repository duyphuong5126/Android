package com.horical.appnote.LocalStorage;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.horical.appnote.Supports.LanguageUtils;

/**
 * Created by Phuong on 03/09/2015.
 */
public abstract class ApplicationSharedData {
    private static final String PreferencesName = "login_token";

    private static final String USERNAME = "username";
    private static final String USER_ID = "id";
    private static final String AVATAR = "avatar";
    private static final String EMAIL = "email";
    private static final String DISPLAY_NAME = "displayname";
    private static final String LANGUAGE = "language";
    private static final String AUTO_SAVE = "autosave";
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;

    public static void initResource(Context context) {
        mContext = context;
    }

    public static boolean isInitResource() {
        return mContext != null;
    }

    public static boolean saveSession(String username, String email, String id, String display_name, String avatar){
        SharedPreferences preferences = mContext.getSharedPreferences(PreferencesName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.putString(USERNAME, username);
        editor.putString(EMAIL, email);
        editor.putString(USER_ID, id);
        editor.putString(DISPLAY_NAME, display_name);
        if (avatar != null && !avatar.equals("")) {
            editor.putString(AVATAR, avatar);
        }
        return editor.commit();
    }

    public static void setLanguage(String language) {
        SharedPreferences preferences = mContext.getSharedPreferences(PreferencesName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(LANGUAGE, language);
        editor.apply();
    }

    public static void setAutoSave(boolean auto) {
        SharedPreferences preferences = mContext.getSharedPreferences(PreferencesName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(AUTO_SAVE, String.valueOf(auto));
        editor.apply();
    }

    public static boolean isAutoSave() {
        SharedPreferences preferences = mContext.getSharedPreferences(PreferencesName, Context.MODE_PRIVATE);
        return Boolean.valueOf(preferences.getString(AUTO_SAVE, "false"));
    }

    public static String getUser(){
        SharedPreferences preferences = mContext.getSharedPreferences(PreferencesName, Context.MODE_PRIVATE);
        return preferences.getString(USERNAME, "");
    }

    public static String getUserID(){
        SharedPreferences preferences = mContext.getSharedPreferences(PreferencesName, Context.MODE_PRIVATE);
        return preferences.getString(USER_ID, "");
    }

    public static String getAVATAR() {
        SharedPreferences preferences = mContext.getSharedPreferences(PreferencesName, Context.MODE_PRIVATE);
        return preferences.getString(AVATAR, "");
    }

    public static String getEmail() {
        SharedPreferences preferences = mContext.getSharedPreferences(PreferencesName, Context.MODE_PRIVATE);
        return preferences.getString(EMAIL, "");
    }

    public static String getDisplayname() {
        SharedPreferences preferences = mContext.getSharedPreferences(PreferencesName, Context.MODE_PRIVATE);
        return preferences.getString(DISPLAY_NAME, "");
    }

    public static String getLanguage() {
        SharedPreferences preferences = mContext.getSharedPreferences(PreferencesName, Context.MODE_PRIVATE);
        return preferences.getString(LANGUAGE, LanguageUtils.ENGLISH);
    }

    public static boolean clearSession(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(PreferencesName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.putString(USERNAME, "");
        return editor.commit();
    }
}
