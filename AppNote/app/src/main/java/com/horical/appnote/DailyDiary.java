package com.horical.appnote;

import android.app.Application;

import com.horical.appnote.ServerStorage.Entities.FileObject;
import com.horical.appnote.ServerStorage.Entities.NoteObject;
import com.horical.appnote.ServerStorage.Entities.UserObject;
import com.parse.Parse;
import com.parse.ParseObject;

/**
 * Created by Phuong on 06/11/2015.
 */
public class DailyDiary extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, "6xdZUWgx7MHAaMXMGlpU51jXopWjaYVbGnbaZFMn", "9QRSoORu1eoFPQ2HshoEsqd51mU0D0VFsevN6a6X");

        ParseObject.registerSubclass(UserObject.class);
        ParseObject.registerSubclass(FileObject.class);
        ParseObject.registerSubclass(NoteObject.class);
    }
}
