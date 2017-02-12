package com.horical.appnote.LocalStorage.DAO;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.horical.appnote.DTO.NoteDTO.NoteReminder;
import com.horical.appnote.LocalStorage.ApplicationSharedData;
import com.horical.appnote.LocalStorage.ApplicationStorage;

import java.util.ArrayList;

/**
 * Created by Phuong on 26/08/2015.
 */
public class NoteReminderDAO extends BaseDAO {
    private NoteReminder mNoteReminder;

    public NoteReminderDAO(Activity activity) {
        this.init(activity);
    }

    public NoteReminderDAO(Activity activity, NoteReminder noteReminder) {
        this.init(activity);
        this.mNoteReminder = noteReminder;
    }

    public boolean insertNoteReminder() {
        String user_id = ApplicationSharedData.getUserID();
        if (!getAllRemindersByTime(mNoteReminder.getTimeComplete()).isEmpty()) {
            return false;
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(ApplicationStorage.NoteReminderTable.TIME, mNoteReminder.getTimeComplete());
            contentValues.put(ApplicationStorage.NoteReminderTable.CONTENT, mNoteReminder.getContent());
            contentValues.put(ApplicationStorage.NoteReminderTable.NOTE_ID, mNoteReminder.getNoteId());
            contentValues.put(ApplicationStorage.NoteReminderTable.USER_ID, user_id);
            contentValues.put(ApplicationStorage.NoteReminderTable.REMIND_VOICE, mNoteReminder.getVoice());
            contentValues.put(ApplicationStorage.NoteReminderTable.UPLOADED, "false");

            Uri uriResult = this.mContentResolver.insert(ApplicationStorage.getUriNOTE_REMINDERS(), contentValues);
            return uriResult != null;
        }
    }

    public ArrayList<NoteReminder> getAllRemindersByTime(String dateSpecified) {
        String[] user_id = new String[1];
        user_id[0] = ApplicationSharedData.getUserID();
        String uri = ApplicationStorage.getNoteRemindersUri();
        if (dateSpecified != null && !dateSpecified.equals("")) {
            uri = ApplicationStorage.getNoteRemindersUri() + "/reminder_time/" + dateSpecified;
        }
        ArrayList<NoteReminder> noteReminders = new ArrayList<>();
        Cursor cursor = this.mContentResolver.query
                (Uri.parse(uri), null, ApplicationStorage.NoteReminderTable.USER_ID + " = ?", user_id, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    NoteReminder noteReminder = new NoteReminder();
                    noteReminder.setReminderID(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ApplicationStorage.NoteReminderTable.ID))));
                    noteReminder.setNoteId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ApplicationStorage.NoteReminderTable.NOTE_ID))));
                    noteReminder.setContent(cursor.getString(cursor.getColumnIndex(ApplicationStorage.NoteReminderTable.CONTENT)));
                    noteReminder.setTimeComplete(cursor.getString(cursor.getColumnIndex(ApplicationStorage.NoteReminderTable.TIME)));
                    noteReminder.setVoice(cursor.getString(cursor.getColumnIndex(ApplicationStorage.NoteReminderTable.REMIND_VOICE)));
                    noteReminders.add(noteReminder);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return noteReminders;
    }

    public ArrayList<NoteReminder> getAllReminders() {
        return this.getAllRemindersByTime(null);
    }



    public boolean deleteReminder(int id) {
        String uri = ApplicationStorage.getNoteRemindersUri() + "/" + id;
        int result = this.mContentResolver.delete(Uri.parse(uri), null, null);
        return result > 0;
    }

    @Override
    public void Send() {

    }

    @Override
    public void Receive() {

    }

    @Override
    public void Parse() {

    }
}
