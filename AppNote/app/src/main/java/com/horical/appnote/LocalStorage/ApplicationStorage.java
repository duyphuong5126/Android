package com.horical.appnote.LocalStorage;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.util.HashMap;

/**
 * Created by Phuong on 15/08/2015.
 */
public class ApplicationStorage extends ContentProvider {
    private static final String PROVIDER_NAME = "com.horical.provider.Database";

    private static final String NOTE_URI = "content://" + PROVIDER_NAME + "/notes";
    private static final Uri uriNOTE = Uri.parse(NOTE_URI);
    private static final String NOTE_DETAILS_URI = "content://" + PROVIDER_NAME + "/note_details";
    private static final Uri uriNOTEDETAILS = Uri.parse(NOTE_DETAILS_URI);
    private static final String NOTE_REMINDERS_URI = "content://" + PROVIDER_NAME + "/note_reminders";


    private static final Uri uriNOTE_REMINDERS = Uri.parse(NOTE_REMINDERS_URI);

    private SQLiteDatabase AppNoteDatabase;
    private static String DATABASE_NAME = "AppNoteDatabase";
    private static int DATABASE_VERSION = 1;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(PROVIDER_NAME, "notes", NoteTable.NOTE);
        uriMatcher.addURI(PROVIDER_NAME, "notes/#", NoteTable.NOTE_ID);
        uriMatcher.addURI(PROVIDER_NAME, "note_details", NoteDetailsTable.NOTE_DETAILS);
        uriMatcher.addURI(PROVIDER_NAME, "note_details/#", NoteDetailsTable.NOTE_DETAILS_ID);
        uriMatcher.addURI(PROVIDER_NAME, "note_details/note_id/#", NoteDetailsTable.NOTE_DETAILS_BY_NOTE_ID);
        uriMatcher.addURI(PROVIDER_NAME, "note_reminders", NoteReminderTable.NOTE_REMINDERS);
        uriMatcher.addURI(PROVIDER_NAME, "note_reminders/#", NoteReminderTable.NOTE_REMINDERS_ID);
        uriMatcher.addURI(PROVIDER_NAME, "note_reminders/reminder_time/*", NoteReminderTable.NOTE_REMINDERS_BY_TIME);
    }

    public static class NoteTable {
        public static final String TABLE_NAME = "Note";

        public static final String ID = "_id";
        public static final String TITLE = "Title";
        public static final String CREATE_AT = "CreateAt";
        public static final String DATE_MODIFIED = "DateModified";
        public static final String USER_ID = "UserID";
        public static final String SERVER_ID = "ServerID";
        public static final String UPLOADED = "Uploaded";
        public static final String SCHEDULED = "Scheduled";

        public static final int NOTE = 1;
        public static final int NOTE_ID = 2;
        public static final int NOTE_STATUS = 3;

        public static final String CREATE_DB_TABLE =
                "CREATE TABLE " + TABLE_NAME +
                        " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "Title TEXT NOT NULL, " +
                        "CreateAt TEXT NOT NULL, " +
                        "DateModified TEXT NOT NULL," +
                        "UserID TEXT NOT NULL," +
                        "ServerID TEXT NOT NULL," +
                        "Scheduled TEXT NOT NULL," +
                        "Uploaded TEXT NOT NULL);";

        public static HashMap<String, String> PROJECTION_MAP;
    }

    public static class NoteDetailsTable {
        public static final String TABLE_NAME = "NoteDetails";

        public static final String ID = "_id";
        public static final String NOTE_ID = "NoteID";
        public static final String TYPE = "Type";
        public static final String CONTENT = "Content";

        public static final String CREATE_DB_TABLE =
                "CREATE TABLE " + TABLE_NAME +
                        " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "NoteID INTEGER NOT NULL, " +
                        "Type TEXT NOT NULL, " +
                        "Content TEXT NOT NULL);";

        public static final int NOTE_DETAILS = 3;
        public static final int NOTE_DETAILS_ID = 4;
        public static final int NOTE_DETAILS_BY_NOTE_ID = 5;

        public static HashMap<String, String> PROJECTION_MAP;

    }

    public static class NoteReminderTable{
        public static final String TABLE_NAME = "NoteReminders";

        public static final String ID = "_id";
        public static final String TIME = "Time";
        public static final String CONTENT = "Content";
        public static final String NOTE_ID = "NoteID";
        public static final String USER_ID = "UserID";
        public static final String REMIND_VOICE = "Voice";
        public static final String UPLOADED = "Uploaded";

        public static final String CREATE_DB_TABLE =
                "CREATE TABLE " + TABLE_NAME +
                        " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "Time TEXT NOT NULL, " +
                        "Content TEXT NOT NULL, " +
                        "NoteID TEXT NOT NULL," +
                        "UserID TEXT NOT NULL," +
                        "Voice TEXT NOT NULL," +
                        "Uploaded TEXT NOT NULL);";

        public static final int NOTE_REMINDERS = 6;
        public static final int NOTE_REMINDERS_ID = 7;
        public static final int NOTE_REMINDERS_BY_TIME = 8;

        public static HashMap<String, String> PROJECTION_MAP;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(NoteTable.CREATE_DB_TABLE);
            sqLiteDatabase.execSQL(NoteDetailsTable.CREATE_DB_TABLE);
            sqLiteDatabase.execSQL(NoteReminderTable.CREATE_DB_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + NoteTable.TABLE_NAME);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + NoteDetailsTable.TABLE_NAME);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + NoteReminderTable.TABLE_NAME);
            onCreate(sqLiteDatabase);
        }
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        AppNoteDatabase = databaseHelper.getWritableDatabase();
        return !(AppNoteDatabase == null);
    }

    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings2, String s2) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        switch (uriMatcher.match(uri)) {
            case NoteTable.NOTE:
                queryBuilder.setTables(NoteTable.TABLE_NAME);
                queryBuilder.setProjectionMap(NoteTable.PROJECTION_MAP);
                break;
            case NoteTable.NOTE_ID:
                queryBuilder.setTables(NoteTable.TABLE_NAME);
                queryBuilder.appendWhere(NoteTable.ID + "=" + uri.getPathSegments().get(1));
                break;
            case NoteDetailsTable.NOTE_DETAILS:
                queryBuilder.setTables(NoteDetailsTable.TABLE_NAME);
                queryBuilder.setProjectionMap(NoteDetailsTable.PROJECTION_MAP);
                break;
            case NoteDetailsTable.NOTE_DETAILS_ID:
                queryBuilder.setTables(NoteDetailsTable.TABLE_NAME);
                queryBuilder.appendWhere(NoteDetailsTable.ID + "=" + uri.getPathSegments().get(1));
                break;
            case NoteDetailsTable.NOTE_DETAILS_BY_NOTE_ID:
                queryBuilder.setTables(NoteDetailsTable.TABLE_NAME);
                queryBuilder.appendWhere(NoteDetailsTable.NOTE_ID + "=" + uri.getPathSegments().get(2));
                break;
            case NoteReminderTable.NOTE_REMINDERS:
                queryBuilder.setTables(NoteReminderTable.TABLE_NAME);
                queryBuilder.setProjectionMap(NoteReminderTable.PROJECTION_MAP);
                break;
            case NoteReminderTable.NOTE_REMINDERS_BY_TIME:
                queryBuilder.setTables(NoteReminderTable.TABLE_NAME);
                queryBuilder.appendWhere(NoteReminderTable.TIME + " LIKE '%" + uri.getPathSegments().get(2) + "%'");
                break;
            default:
                throw new IllegalArgumentException("Unknow URI" + uri);
        }

        Cursor cursor = queryBuilder.query(AppNoteDatabase, strings, s, strings2, null, null, s2);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case NoteTable.NOTE:
                return "vnd.android.cursor.dir/vnd.duyphuong.notes";
            case NoteTable.NOTE_ID:
                return "vnd.android.cursor.item/vnd.duyphuong.notes";
            case NoteDetailsTable.NOTE_DETAILS:
                return "vnd.android.cursor.dir/vnd.duyphuong.note_details";
            case NoteDetailsTable.NOTE_DETAILS_ID:
                return "vnd.android.cursor.item/vnd.duyphuong.note_details";
            default:
                throw new IllegalArgumentException("Unsupport URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        long insertedRow = 0;
        Uri _uri = null;
        switch (uriMatcher.match(uri)) {
            case NoteTable.NOTE:
                insertedRow = AppNoteDatabase.insert(NoteTable.TABLE_NAME, "", contentValues);
                if (insertedRow > 0) {
                    _uri = ContentUris.withAppendedId(uriNOTE, insertedRow);
                    getContext().getContentResolver().notifyChange(_uri, null);
                }
                break;
            case NoteDetailsTable.NOTE_DETAILS:
                insertedRow = AppNoteDatabase.insert(NoteDetailsTable.TABLE_NAME, "", contentValues);
                if (insertedRow > 0) {
                    _uri = ContentUris.withAppendedId(uriNOTEDETAILS, insertedRow);
                    getContext().getContentResolver().notifyChange(_uri, null);
                }
                break;
            case NoteReminderTable.NOTE_REMINDERS:
                insertedRow = AppNoteDatabase.insert(NoteReminderTable.TABLE_NAME, "", contentValues);
                if (insertedRow > 0){
                    _uri = ContentUris.withAppendedId(uriNOTE_REMINDERS, insertedRow);
                    getContext().getContentResolver().notifyChange(_uri, null);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknow URI " + uri);
        }
        return _uri;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        int result = 0;
        switch (uriMatcher.match(uri)) {
            case NoteTable.NOTE:
                result = AppNoteDatabase.delete(NoteTable.TABLE_NAME, s, strings);
                break;
            case NoteTable.NOTE_ID:
                result = AppNoteDatabase.delete(NoteTable.TABLE_NAME, NoteTable.ID + " = " + uri.getPathSegments().get(1) +
                        ((!TextUtils.isEmpty(s)) ? " AND " + s : ""), strings);
                break;
            case NoteDetailsTable.NOTE_DETAILS:
                result = AppNoteDatabase.delete(NoteDetailsTable.TABLE_NAME, s, strings);
                break;
            case NoteDetailsTable.NOTE_DETAILS_ID:
                result = AppNoteDatabase.delete(NoteDetailsTable.TABLE_NAME, NoteDetailsTable.ID + " = " + uri.getPathSegments().get(1) +
                        ((!TextUtils.isEmpty(s)) ? " AND " + s : ""), strings);
                break;
            case NoteDetailsTable.NOTE_DETAILS_BY_NOTE_ID:
                result = AppNoteDatabase.delete(NoteDetailsTable.TABLE_NAME, NoteDetailsTable.NOTE_ID + " = " + uri.getPathSegments().get(2) +
                        ((!TextUtils.isEmpty(s)) ? " AND " + s : ""), strings);
                break;
            case NoteReminderTable.NOTE_REMINDERS_ID:
                result = AppNoteDatabase.delete(NoteReminderTable.TABLE_NAME, NoteReminderTable.ID + " = " +uri.getPathSegments().get(1) +
                         ((!TextUtils.isEmpty(s)) ? " AND " + s : ""), strings);
                break;
            default:
                throw new IllegalArgumentException("Unknow URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return result;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        int result = 0;
        switch (uriMatcher.match(uri)) {
            case NoteTable.NOTE:
                result = AppNoteDatabase.update(NoteTable.TABLE_NAME, contentValues, s, strings);
                break;
            case NoteTable.NOTE_ID:
                result = AppNoteDatabase.update(NoteTable.TABLE_NAME, contentValues,
                        NoteTable.ID + " = " + uri.getPathSegments().get(1) +
                                ((!TextUtils.isEmpty(s)) ? " AND " + s : ""), strings);
                break;
            case NoteDetailsTable.NOTE_DETAILS:
                result = AppNoteDatabase.update(NoteDetailsTable.TABLE_NAME, contentValues, s, strings);
                break;
            case NoteDetailsTable.NOTE_DETAILS_ID:
                result = AppNoteDatabase.update(NoteDetailsTable.TABLE_NAME, contentValues,
                        NoteDetailsTable.ID + " = " + uri.getPathSegments().get(1) +
                                ((!TextUtils.isEmpty(s)) ? " AND " + s : ""), strings);
                break;
            default:
                throw new IllegalArgumentException("Unknow URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return result;
    }

    public static String getProviderName() {
        return PROVIDER_NAME;
    }

    public static String getNoteUri() {
        return NOTE_URI;
    }

    public static Uri getUriNOTE() {
        return uriNOTE;
    }

    public static String getNoteDetailsUri() {
        return NOTE_DETAILS_URI;
    }

    public static Uri getUriNOTEDETAILS() {
        return uriNOTEDETAILS;
    }

    public static Uri getUriNOTE_REMINDERS() {
        return uriNOTE_REMINDERS;
    }

    public static String getNoteRemindersUri() {
        return NOTE_REMINDERS_URI;
    }
}
