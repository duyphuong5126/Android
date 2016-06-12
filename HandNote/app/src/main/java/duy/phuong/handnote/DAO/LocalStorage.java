package duy.phuong.handnote.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import duy.phuong.handnote.DTO.Note;
import duy.phuong.handnote.Support.SharedPreferenceUtils;
import duy.phuong.handnote.Support.SupportUtils;

/**
 * Created by Phuong on 16/04/2016.
 */
public class LocalStorage extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Local";
    private static final int DATABASE_VERSION = 2;
    private static final String TABLE_NOTE = "tblNote";
    private static final String TABLE_EV_DICTIONARY = "tblEV_Dictionary";
    private static final String Note_Id = "_id";
    private static final String Note_Image = "image";
    private static final String Note_Content = "content";
    private static final String Dict_Id = "_id";
    private static final String Dict_Word = "word";
    private static final String Dict_Pronunciation = "pronunciation";
    private static final String Dict_Definition = "definition";

    private static final String create_tblNote = "create table " + TABLE_NOTE + "(" +
            Note_Id + " integer primary key autoincrement," +
            Note_Content + " text not null," +
            Note_Image + " text ot null"
            + ")";

    private static final String create_tblDictionary = "create table " + TABLE_EV_DICTIONARY + "(" +
            Dict_Id + " integer primary key autoincrement," +
            Dict_Word + " text not null," +
            Dict_Pronunciation + " text not null," +
            Dict_Definition + " text ot null"
            + ")";
    public LocalStorage(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(create_tblNote);
        db.execSQL(create_tblDictionary);
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

    public boolean inertEV_DictLine(String word, String pronunciation, String definition, SQLiteDatabase db, ContentValues contentValues) {
        contentValues.clear();
        contentValues.put(Dict_Word, word);
        contentValues.put(Dict_Definition, definition);
        contentValues.put(Dict_Pronunciation, (pronunciation == null) ? "" : pronunciation);
        return db.insert(TABLE_EV_DICTIONARY, null, contentValues) > 0;
    }

    public String findEV_Definition(String word) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "select * from " + TABLE_EV_DICTIONARY + " where " + Dict_Word + " = '" + word + "'";
        Log.d("Sql", sql);
        Cursor cursor = db.rawQuery(sql, null);
        String definition = "";
        String pronunciation = "";
        if (cursor.moveToFirst()) {
            definition = cursor.getString(cursor.getColumnIndex(Dict_Definition));
            pronunciation = cursor.getString(cursor.getColumnIndex(Dict_Pronunciation));
        }
        Log.d("Result dict", "word: " + word + ", pro: " + pronunciation + ", def: " + definition);
        String result = "";
        if (pronunciation.length() > 0) {
            result += pronunciation + "\n";
        }
        result += definition;
        return result;
    }

    public void deleteAllNotes() {
        ArrayList<Note> notes = getListNote();
        for (Note note : notes) {
            SupportUtils.deleteFile(note.mBitmapPath);
            SupportUtils.deleteFile(note.mContentPath);
        }
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NOTE, null ,null);
    }

    public void deleteAllDict(SQLiteDatabase db) {
        if (db == null) {
            SQLiteDatabase database = getWritableDatabase();
            database.delete(TABLE_EV_DICTIONARY, null, null);
        } else {
            db.delete(TABLE_EV_DICTIONARY, null, null);
        }
        SharedPreferenceUtils.loadedDict(false);
    }
}
