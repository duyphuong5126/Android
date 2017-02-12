package com.horical.appnote.LocalStorage.DAO;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.horical.appnote.LocalStorage.ApplicationSharedData;
import com.horical.appnote.LocalStorage.ApplicationStorage;
import com.horical.appnote.LocalStorage.DataConstant;
import com.horical.appnote.DTO.NoteDTO.NoteData;
import com.horical.appnote.DTO.NoteDTO.NoteImage;
import com.horical.appnote.DTO.NoteDTO.NoteSummary;
import com.horical.appnote.DTO.NoteDTO.NoteText;
import com.horical.appnote.DTO.NoteDTO.NoteVideoClip;
import com.horical.appnote.DTO.NoteDTO.NoteVoice;
import com.horical.appnote.Fragments.NewNoteFragment;
import com.horical.appnote.Interfaces.NoteDataLine;
import com.horical.appnote.Supports.FileUtils;
import com.horical.appnote.Supports.LanguageUtils;
import com.horical.appnote.Supports.SupportUtils;

import java.util.ArrayList;

/**
 * Created by Phuong on 26/08/2015.
 */
public class NoteDataDAO extends BaseDAO {
    private NoteData mNoteData;

    private static String uriNoteDetails = ApplicationStorage.getNoteDetailsUri();
    private static final Uri uriNote = ApplicationStorage.getUriNOTE();

    public NoteDataDAO(Activity activity) {
        mNoteData = new NoteData();
        this.init(activity);
    }

    public NoteDataDAO(Activity activity, NoteData noteData) {
        this.mNoteData = new NoteData();
        this.mNoteData = noteData;
        this.init(activity);
    }

    public void setNoteData(NoteData NoteData) {
        this.mNoteData = NoteData;
    }

    public void insertNote(NewNoteFragment.CreateNoteCallback callback) {
        String user_id = ApplicationSharedData.getUserID();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ApplicationStorage.NoteTable.TITLE,
                (mNoteData.getNoteSummary().getTitle() != null)?mNoteData.getNoteSummary().getTitle():"");
        contentValues.put(ApplicationStorage.NoteTable.CREATE_AT,
                (mNoteData.getNoteSummary().getCreatedAt() != null)?mNoteData.getNoteSummary().getCreatedAt():"");
        contentValues.put(ApplicationStorage.NoteTable.DATE_MODIFIED,
                (mNoteData.getNoteSummary().getModifiedAt() != null)?mNoteData.getNoteSummary().getModifiedAt():"");
        contentValues.put(ApplicationStorage.NoteTable.USER_ID, user_id);
        contentValues.put(ApplicationStorage.NoteTable.SERVER_ID,
                (mNoteData.getNoteSummary().getServerID() != null)?mNoteData.getNoteSummary().getServerID():"");
        contentValues.put(ApplicationStorage.NoteTable.UPLOADED, String.valueOf(mNoteData.getNoteSummary().isUploaded()));
        contentValues.put(ApplicationStorage.NoteTable.SCHEDULED, String.valueOf(mNoteData.getNoteSummary().isScheduled()));
        Uri uriResult = this.mContentResolver.
                insert(ApplicationStorage.getUriNOTE(), contentValues);
        if (uriResult != null) {
            int note_id = getIDByUri(uriResult);
            insertNoteDetails(note_id, callback);
        } else {
            callback.onFailure(LanguageUtils.getInsertNoteFailString());
        }
    }

    private int getIDByUri(Uri uri) {
        Cursor cursor = mActivity.getContentResolver().query(uri, null, null, null, ApplicationStorage.NoteTable.TITLE);
        int currentID = 0;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    currentID = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ApplicationStorage.NoteDetailsTable.ID)));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return currentID;
    }

    private void insertNoteDetails(final int note_id, final NewNoteFragment.CreateNoteCallback callback) {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle bundle = msg.getData();
                boolean result = bundle.getBoolean("insertDetails");
                if (result) {
                    callback.onSuccess(LanguageUtils.getInsertNoteSuccessString());
                } else {
                    callback.onFailure(LanguageUtils.getInsertNoteFailString());
                }
            }
        };
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int result = 0;
                int realItems = 0;//number of not-empty items
                for (NoteDataLine noteDataLine : mNoteData.getNoteData()) {
                    if (noteDataLine.typeIdentify().equals(DataConstant.TYPE_TEXT)) {
                        NoteText noteText = (NoteText) noteDataLine;
                        if (!"".equals(noteText.getContent())) {
                            realItems++;
                            result += insertNoteDetails(
                                    note_id, noteText.getType(), noteText.getContent());
                        }
                    } else {
                        String resultPath;
                        switch (noteDataLine.typeIdentify()) {
                            case DataConstant.TYPE_IMAGE:
                                realItems++;
                                NoteImage noteImage = (NoteImage) noteDataLine;
                                resultPath = FileUtils.writeFile(DataConstant.TYPE_IMAGE, noteImage.getFilePath(),
                                        SupportUtils.getNameFromPath(noteImage.getFilePath()));
                                result += insertNoteDetails(
                                        note_id, noteImage.typeIdentify(), resultPath);
                                break;
                            case DataConstant.TYPE_VIDEOCLIP:
                                realItems++;
                                NoteVideoClip noteVideoClip = (NoteVideoClip) noteDataLine;
                                resultPath = FileUtils.writeFile(DataConstant.TYPE_VIDEOCLIP, noteVideoClip.getFilePath(),
                                        SupportUtils.getNameFromPath(noteVideoClip.getFilePath()));
                                result += insertNoteDetails(
                                        note_id, noteVideoClip.typeIdentify(), resultPath);

                                break;
                            case DataConstant.TYPE_VOICE:
                                realItems++;
                                NoteVoice noteVoice = (NoteVoice) noteDataLine;
                                resultPath = FileUtils.writeFile(DataConstant.TYPE_VOICE, noteVoice.getFilePath(),
                                        SupportUtils.getNameFromPath(noteVoice.getFilePath()));
                                result += insertNoteDetails(
                                        note_id, noteVoice.typeIdentify(), resultPath);
                                break;
                            default:
                                break;
                        }
                    }
                }

                Message message = handler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putBoolean("insertDetails", (realItems == result));
                message.setData(bundle);
                handler.sendMessage(message);
            }
        };
        runnable.run();
    }

    private int insertNoteDetails(int noteID, String type, String content) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ApplicationStorage.NoteDetailsTable.NOTE_ID, String.valueOf(noteID));
        contentValues.put(ApplicationStorage.NoteDetailsTable.TYPE, type);
        contentValues.put(ApplicationStorage.NoteDetailsTable.CONTENT, content);

        Uri uriResult = mContentResolver.insert(ApplicationStorage.getUriNOTEDETAILS(), contentValues);
        return (uriResult != null) ? 1 : 0;
    }

    public void updateNote(NewNoteFragment.CreateNoteCallback callback) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ApplicationStorage.NoteTable.TITLE, mNoteData.getNoteSummary().getTitle());
        contentValues.put(ApplicationStorage.NoteTable.DATE_MODIFIED, mNoteData.getNoteSummary().getCreatedAt());

        String uriUpdate = ApplicationStorage.getNoteUri() + "/" + mNoteData.getNoteSummary().getID();
        String uriDelete = ApplicationStorage.getNoteDetailsUri() + "/note_id/" + mNoteData.getNoteSummary().getID();

        int resultUpdate = this.mContentResolver.update(Uri.parse(uriUpdate), contentValues, null, null);
        int resultDelete = this.mContentResolver.delete(Uri.parse(uriDelete), null, null);

        if (resultDelete > 0 && resultUpdate > 0) {
            int note_id = mNoteData.getNoteSummary().getID();
            insertNoteDetails(note_id, callback);
        } else {
            callback.onFailure("Update note failure!");
        }
    }

    public ArrayList<NoteSummary> loadListNote() {
        String[] user_id = new String[1];
        user_id[0] = ApplicationSharedData.getUserID();
        Cursor cursor = this.mActivity.getContentResolver().query(uriNote, null, ApplicationStorage.NoteTable.USER_ID + " = ?", user_id,
                "strftime('%Y-%m-%d', " + ApplicationStorage.NoteTable.DATE_MODIFIED + ") ASC");
        ArrayList<NoteSummary> listNoteSummary = new ArrayList<>();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    NoteSummary noteSummary = new NoteSummary(
                            Integer.parseInt(cursor.getString(cursor.getColumnIndex(ApplicationStorage.NoteTable.ID))),
                            cursor.getString(cursor.getColumnIndex(ApplicationStorage.NoteTable.TITLE)),
                            cursor.getString(cursor.getColumnIndex(ApplicationStorage.NoteTable.CREATE_AT)),
                            cursor.getString(cursor.getColumnIndex(ApplicationStorage.NoteTable.DATE_MODIFIED))
                    );
                    noteSummary.setUploaded(Boolean.valueOf(cursor.getString(cursor.getColumnIndex(ApplicationStorage.NoteTable.UPLOADED))));
                    noteSummary.setScheduled(Boolean.valueOf(cursor.getString(cursor.getColumnIndex(ApplicationStorage.NoteTable.SCHEDULED))));
                    listNoteSummary.add(noteSummary);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return listNoteSummary;
    }

    public ArrayList<NoteDataLine> loadNoteDetails(int noteID) {
        ArrayList<NoteDataLine> listNoteLine = new ArrayList<>();
        Cursor cursor = mContentResolver.query(Uri.parse(uriNoteDetails + "/note_id/" + noteID), null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    switch (cursor.getString(cursor.getColumnIndex(ApplicationStorage.NoteDetailsTable.TYPE))) {
                        case DataConstant.TYPE_TEXT:
                            NoteText noteText = new NoteText(
                                    Integer.parseInt(cursor.getString(cursor.getColumnIndex(ApplicationStorage.NoteDetailsTable.NOTE_ID))),
                                    cursor.getString(cursor.getColumnIndex(ApplicationStorage.NoteDetailsTable.TYPE)),
                                    cursor.getString(cursor.getColumnIndex(ApplicationStorage.NoteDetailsTable.CONTENT)),
                                    Integer.parseInt(cursor.getString(cursor.getColumnIndex(ApplicationStorage.NoteDetailsTable.NOTE_ID)))
                            );
                            noteText.setContentIndex(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ApplicationStorage.NoteDetailsTable.ID))));
                            listNoteLine.add(noteText);
                            break;
                        case DataConstant.TYPE_IMAGE:
                            NoteImage noteImage = new NoteImage(
                                    SupportUtils.getNameFromPath(cursor.getString(cursor.getColumnIndex(ApplicationStorage.NoteDetailsTable.CONTENT))),
                                    cursor.getString(cursor.getColumnIndex(ApplicationStorage.NoteDetailsTable.TYPE)),
                                    cursor.getString(cursor.getColumnIndex(ApplicationStorage.NoteDetailsTable.CONTENT)),
                                    Integer.parseInt(cursor.getString(cursor.getColumnIndex(ApplicationStorage.NoteDetailsTable.NOTE_ID)))
                            );
                            noteImage.setContentIndex(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ApplicationStorage.NoteDetailsTable.ID))));
                            listNoteLine.add(noteImage);
                            break;
                        case DataConstant.TYPE_VIDEOCLIP:
                            NoteVideoClip noteVideoClip = new NoteVideoClip(
                                    SupportUtils.getNameFromPath(cursor.getString(cursor.getColumnIndex(ApplicationStorage.NoteDetailsTable.CONTENT))),
                                    cursor.getString(cursor.getColumnIndex(ApplicationStorage.NoteDetailsTable.CONTENT)),
                                    0,
                                    cursor.getString(cursor.getColumnIndex(ApplicationStorage.NoteDetailsTable.TYPE)),
                                    Integer.parseInt(cursor.getString(cursor.getColumnIndex(ApplicationStorage.NoteDetailsTable.NOTE_ID)))
                            );
                            noteVideoClip.setContentIndex(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ApplicationStorage.NoteDetailsTable.ID))));
                            listNoteLine.add(noteVideoClip);

                            break;
                        case DataConstant.TYPE_VOICE:
                            NoteVoice noteVoice = new NoteVoice(
                                    SupportUtils.getNameFromPath(cursor.getString(cursor.getColumnIndex(ApplicationStorage.NoteDetailsTable.CONTENT))),
                                    cursor.getString(cursor.getColumnIndex(ApplicationStorage.NoteDetailsTable.CONTENT)),
                                    0,
                                    cursor.getString(cursor.getColumnIndex(ApplicationStorage.NoteDetailsTable.TYPE)),
                                    Integer.parseInt(cursor.getString(cursor.getColumnIndex(ApplicationStorage.NoteDetailsTable.NOTE_ID)))
                            );
                            noteVoice.setContentIndex(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ApplicationStorage.NoteDetailsTable.ID))));
                            listNoteLine.add(noteVoice);
                            break;
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return listNoteLine;
    }

    public boolean deleteNote(int noteID) {
        String uri = ApplicationStorage.getNoteUri() + "/" + noteID;
        if (this.deleteNoteDetails(noteID)) {
            int result = mContentResolver.delete(Uri.parse(uri), null, null);
            return result > 0;
        }
        return false;
    }

    private boolean deleteNoteDetails(int noteID) {
        String uriNoteDetails =
                ApplicationStorage.getNoteDetailsUri() + "/note_id/" + noteID;
        int result = mContentResolver.delete(Uri.parse(uriNoteDetails), null, null);
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
