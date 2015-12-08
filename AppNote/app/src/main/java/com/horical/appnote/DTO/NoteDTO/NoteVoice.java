package com.horical.appnote.DTO.NoteDTO;

import java.io.Serializable;

import com.horical.appnote.LocalStorage.DataConstant;
import com.horical.appnote.Interfaces.NoteDataLine;

/**
 * Created by dutn on 24/07/2015.
 */
public class NoteVoice implements Serializable, NoteDataLine {

    private String mFileName;
    private String mFilePath;
    private int mDuration;
    private String mFileType;
    private int mNoteID;
    private int mContentIndex;

    public int getContentIndex() {
        return mContentIndex;
    }

    public void setContentIndex(int contentIndex) {
        this.mContentIndex = contentIndex;
    }

    public String getFileName() {
        return mFileName;
    }

    public void setFileName(String fileName) {
        this.mFileName = fileName;
    }

    public String getFilePath() {
        return mFilePath;
    }

    public void setFilePath(String filePath) {
        this.mFilePath = filePath;
    }

    public int getDuration() {
        return mDuration;
    }

    public void setDuration(int duration) {
        this.mDuration = duration;
    }

    public String getFileType() {
        return mFileType;
    }

    public void setFileType(String fileType) {
        this.mFileType = fileType;
    }

    public int getNoteID() {
        return mNoteID;
    }

    public void setNoteID(int mNoteID) {
        this.mNoteID = mNoteID;
    }

    public NoteVoice(String fileName, String filePath, int duration, String fileType, int noteId) {
        this.mFileName = fileName;
        this.mFilePath = filePath;
        this.mDuration = duration;
        this.mFileType = fileType;
        this.mNoteID = noteId;
    }

    public NoteVoice(String filePath) {
        this.mFilePath = filePath;
        this.mFileName = "";
        this.mDuration = 0;
        this.mFileType = "";
        this.mNoteID = -1;
    }

    @Override
    public String typeIdentify() {
        return DataConstant.TYPE_VOICE;
    }

    @Override
    public String toString() {
        return mFilePath;
    }
}
