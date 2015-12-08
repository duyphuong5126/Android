package com.horical.appnote.LocalStorage;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.horical.appnote.Fragments.SettingFragment;
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
    private static final String DISPLAYNAME = "displayname";
    private static final String LANGUAGE = "language";
    private static Activity mActivity;

    public static void initResource(Activity activity) {
        mActivity = activity;
    }

    public static boolean saveSession(String username, String email, String id, String displayname, String avatar){
        SharedPreferences preferences = mActivity.getSharedPreferences(PreferencesName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.putString(USERNAME, username);
        editor.putString(EMAIL, email);
        editor.putString(USER_ID, id);
        editor.putString(DISPLAYNAME, displayname);
        if (avatar != null && !avatar.equals("")) {
            editor.putString(AVATAR, avatar);
        }
        return editor.commit();
    }

    public static void setLanguage(String language) {
        SharedPreferences preferences = mActivity.getSharedPreferences(PreferencesName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(LANGUAGE, language);
        editor.commit();
    }

    public static String getUser(){
        SharedPreferences preferences = mActivity.getSharedPreferences(PreferencesName, Context.MODE_PRIVATE);
        return preferences.getString(USERNAME, "");
    }

    public static String getUserID(){
        SharedPreferences preferences = mActivity.getSharedPreferences(PreferencesName, Context.MODE_PRIVATE);
        return preferences.getString(USER_ID, "");
    }

    public static String getAVATAR() {
        SharedPreferences preferences = mActivity.getSharedPreferences(PreferencesName, Context.MODE_PRIVATE);
        return preferences.getString(AVATAR, "");
    }

    public static String getEmail() {
        SharedPreferences preferences = mActivity.getSharedPreferences(PreferencesName, Context.MODE_PRIVATE);
        return preferences.getString(EMAIL, "");
    }

    public static String getDisplayname() {
        SharedPreferences preferences = mActivity.getSharedPreferences(PreferencesName, Context.MODE_PRIVATE);
        return preferences.getString(DISPLAYNAME, "");
    }

    public static String getLanguage() {
        SharedPreferences preferences = mActivity.getSharedPreferences(PreferencesName, Context.MODE_PRIVATE);
        return preferences.getString(LANGUAGE, LanguageUtils.ENGLISH);
    }

    public static boolean clearSession(){
        SharedPreferences sharedPreferences = mActivity.getSharedPreferences(PreferencesName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.putString(USERNAME, "");
        return editor.commit();
    }
}
