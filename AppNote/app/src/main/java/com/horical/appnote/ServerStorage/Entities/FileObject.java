package com.horical.appnote.ServerStorage.Entities;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

/**
 * Created by Phuong on 08/11/2015.
 */
@ParseClassName("FileObject")
public class FileObject extends ParseObject {
    public static final String NAME = "FileName";
    public static final String TYPE = "FileType";
    public static final String CONTENT = "FileContent";
    public static final String USER_ID = "UserId";

    public void setName(String name) {
        put(NAME, name);
    }

    public void setType(String type) {
        put(TYPE, type);
    }

    public void setContent(ParseFile file) {
        put(CONTENT, file);
    }

    public void setUserId(String userId) {
        put(USER_ID, userId);
    }

    public String getUserId() {
        return getString(USER_ID);
    }

    public String getName() {
        return getString(NAME);
    }

    public String getType() {
        return getString(TYPE);
    }

    public ParseFile getContent() {
        return getParseFile(CONTENT);
    }
}
