package com.horical.appnote.ServerStorage.Entities;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

/**
 * Created by Phuong on 05/11/2015.
 */

@ParseClassName("User")
public class UserObject extends ParseObject {
    public static final String USER_ID = "UserId";
    public static final String USERNAME = "UserName";
    public static final String PASSWORD = "Password";
    public static final String DISPLAYNAME = "DisplayName";
    public static final String EMAIL = "Email";
    public static final String AVATAR = "Avatar";

    public void setUserID(String id) {
        put(USER_ID, id);
    }

    public void setUsername(String username) {
        put(USERNAME, username);
    }

    public void setPassword(String password) {
        put(PASSWORD, password);
    }

    public void setDisplayname(String displayname) {
        put(DISPLAYNAME, displayname);
    }

    public void setEmail(String email) {
        put(EMAIL, email);
    }

    public void setAvatar(ParseFile avatar) {
        put(AVATAR, avatar);
    }

    public String getUserId() {
        return getString(USER_ID);
    }

    public String getUsername() {
        return getString(USERNAME);
    }

    public String getPassword() {
        return getString(PASSWORD);
    }

    public String getDisplayname() {
        return getString(DISPLAYNAME);
    }

    public String getEmail() {
        return getString(EMAIL);
    }

    public ParseFile getAvatar() {
        return getParseFile(AVATAR);
    }
}
