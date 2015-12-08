package com.horical.appnote.DTO.NoteDTO;

import java.io.Serializable;

import com.horical.appnote.LocalStorage.DataConstant;
import com.horical.appnote.Interfaces.NoteDataLine;

/**
 * Created by dutn on 24/07/2015.
 */
public class NoteImage implements Serializable, NoteDataLine {

    private String mFileName;
    private String mFilePath;
    private String mFileType;
    private int mNoteId;
    private int mContentIndex;

    public int getContentIndex() {
        return mContentIndex;
    }

    public void setContentIndex(int contentIndex) {
        this.mContentIndex = contentIndex;
    }

    public NoteImage() {
    }

    public NoteImage(String fileName, String fileType, String filePath, int noteId) {
        this.mFileName = fileName;
        this.mFilePath = filePath;
        this.mFileType = fileType;
        this.mNoteId = noteId;
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

    public String getFileType() {
        return mFileType;
    }

    public void setFileType(String fileType) {
        this.mFileType = fileType;
    }

    public int getNoteId() {
        return mNoteId;
    }

    public void setNoteId(int noteId) {
        this.mNoteId = noteId;
    }

    @Override
    public String typeIdentify() {
        return DataConstant.TYPE_IMAGE;
    }

    @Override
    public String toString() {
        return mFilePath;
    }
}
