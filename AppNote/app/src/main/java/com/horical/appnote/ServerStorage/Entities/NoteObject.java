package com.horical.appnote.ServerStorage.Entities;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by Phuong on 12/11/2015.
 */
@ParseClassName("NoteObject")
public class NoteObject extends ParseObject {
    public static final String ID = "ID";
    public static final String CONTENT = "Content";
    public static final String USER_ID = "UserId";

    public void setId(String id) {
        put(ID, id);
    }

    public void setContent(String content) {
        put(CONTENT, content);
    }

    public void setUserId(String userId) {
        put(USER_ID, userId);
    }

    public String getContent() {
        return getString(CONTENT);
    }

    public String getId() {
        return getString(ID);
    }

    public String getUserId() {
        return getString(USER_ID);
    }
}
