package com.horical.appnote.DTO.NoteDTO;

import java.io.Serializable;

import com.horical.appnote.LocalStorage.DataConstant;
import com.horical.appnote.Interfaces.NoteDataLine;

/**
 * Created by dutn on 05/08/2015.
 */
public class NoteText implements Serializable, NoteDataLine {

    private int mNoteID;
    private String mType;
    private String mContent;
    private int mIndex;
    private int mContentIndex;

    public NoteText() {
    }

    public int getContentIndex() {
        return mContentIndex;
    }

    public void setContentIndex(int contentIndex) {
        this.mContentIndex = contentIndex;
    }

    public NoteText(int noteId, String type, String content, int stt) {
        this.mNoteID = noteId;
        this.mType = type;
        this.mContent = content;
        this.mIndex = stt;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        this.mType = type;
    }

    public int getNoteID() {
        return mNoteID;
    }

    public void setNoteID(int mNoteID) {
        this.mNoteID = mNoteID;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        this.mContent = content;
    }

    public int getIndex() {
        return mIndex;
    }

    public void setIndex(int index) {
        this.mIndex = index;
    }

    @Override
    public String typeIdentify() {
        return DataConstant.TYPE_TEXT;
    }

    @Override
    public String toString() {
        return mContent;
    }
}
