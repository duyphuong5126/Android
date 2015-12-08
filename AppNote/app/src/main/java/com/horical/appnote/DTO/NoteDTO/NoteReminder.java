package com.horical.appnote.DTO.NoteDTO;

import java.io.Serializable;
import java.util.Calendar;

import com.horical.appnote.LocalStorage.DataConstant;
import com.horical.appnote.Interfaces.NoteDataLine;
import com.horical.appnote.Supports.CalendarUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by dutn on 24/07/2015.
 */
public class NoteReminder implements Serializable, NoteDataLine {

    private int mReminderID;
    private String mTimeComplete;
    private int mStatus;
    private int mNoteId;
    private String mContent;
    private String mVoice;

    public NoteReminder() {
        mTimeComplete = "";
        mNoteId = -1;
        mContent = "";
        mVoice = "";
    }

    public int getReminderID() {
        return mReminderID;
    }

    public void setReminderID(int reminderID) {
        mReminderID = reminderID;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        this.mContent = content;
    }

    public String getTimeComplete() {
        return mTimeComplete;
    }

    public void setTimeComplete(String timeComplete) {
        this.mTimeComplete = timeComplete;
    }

    public int getStatus() {
        return mStatus;
    }

    public void setStatus(int status) {
        this.mStatus = status;
    }

    public int getNoteId() {
        return mNoteId;
    }

    public void setNoteId(int noteId) {
        this.mNoteId = noteId;
    }

    public String getVoice() {
        return mVoice;
    }

    public void setVoice(String Voice) {
        this.mVoice = Voice;
    }

    public int readyToRemind() {
        Calendar calendar1 = CalendarUtils.StringToCalendar(mTimeComplete);
        Calendar calendar2 = CalendarUtils.StringToCalendar(CalendarUtils.getCurrentTime());
        return calendar1.compareTo(calendar2);
    }

    @Override
    public String typeIdentify() {
        return DataConstant.TYPE_REMINDER;
    }

    public String createJSON() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("time", (mTimeComplete != null)?mTimeComplete:"");
            jsonObject.put("content", (mContent != null)?mContent:"");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}
