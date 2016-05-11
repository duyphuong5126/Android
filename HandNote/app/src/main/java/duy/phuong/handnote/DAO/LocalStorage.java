package duy.phuong.handnote.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import duy.phuong.handnote.DTO.Note;
import duy.phuong.handnote.Support.SupportUtils;

/**
 * Created by Phuong on 16/04/2016.
 */
public class LocalStorage extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Local";
    private static final int DATABASE_VERSION = 2;
    private static final String TABLE_NOTE = "tblNote";
    private static final String Note_Id = "_id";
    private static final String Note_Image = "image";
    private static final String Note_Content = "content";

    private static final String create_tblNote = "create table " + TABLE_NOTE + "(" +
            Note_Id + " integer primary key autoincrement," +
            Note_Content + " text not null," +
            Note_Image + " text ot null"
            + ")";
    public LocalStorage(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(create_tblNote);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME);
        onCreate(db);
    }

    public boolean insertNote(String imagePath, String notePath) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Note_Image, imagePath);
        contentValues.put(Note_Content, notePath);
        return db.insert(TABLE_NOTE, null, contentValues) >= 0;
    }

    public ArrayList<Note> getListNote() {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<Note> notes = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from " + TABLE_NOTE, null);
        if (cursor.moveToFirst()) {
            do {
                Note note = new Note();
                note.mBitmapPath = cursor.getString(cursor.getColumnIndex(Note_Image));

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inDither = false;
                options.inPurgeable = true;
                options.inInputShareable = true;
                Bitmap src = BitmapFactory.decodeFile(note.mBitmapPath, options);
                note.mContentPath = cursor.getString(cursor.getColumnIndex(Note_Content));
                note.mContent = SupportUtils.getStringData(note.mContentPath);
                if (src != null) {
                    note.mImage = ThumbnailUtils.extractThumbnail(src, 60, 60);
                    notes.add(note);
                } else {
                    deleteNote(note);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return notes;
    }

    public boolean deleteNote(Note note) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_NOTE, Note_Image + " = '" + note.mBitmapPath + "' and " + Note_Content + " = '" + note.mContentPath + "'", null) >= 0;
    }
}
