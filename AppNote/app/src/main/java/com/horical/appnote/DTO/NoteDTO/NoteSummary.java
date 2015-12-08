package com.horical.appnote.DTO.NoteDTO;

import java.io.Serializable;

/**
 * Created by dutn on 23/07/2015.
 */
public class NoteSummary implements Serializable {

    private int mID;
    private String mTitle;
    private String mCreatedAt;
    private String mModifiedAt;
    private boolean mUploaded;
    private String mServerID;

    public boolean isScheduled() {
        return mScheduled;
    }

    public void setScheduled(boolean Scheduled) {
        this.mScheduled = Scheduled;
    }

    private boolean mScheduled;

    public String getServerID() {
        return mServerID;
    }

    public void setServerID(String ServerID) {
        this.mServerID = ServerID;
    }

    public NoteSummary() {
    }

    public NoteSummary(String title, String createdAt, String modifiedAt) {
        this.mTitle = title;
        this.mCreatedAt = createdAt;
        this.mModifiedAt = modifiedAt;
        this.mUploaded = false;
        this.mServerID = "";
        this.mScheduled = false;
    }

    public NoteSummary(int id, String title, String createdAt, String modifiedAt) {
        this.mID = id;
        this.mTitle = title;
        this.mCreatedAt = createdAt;
        this.mModifiedAt = modifiedAt;
        this.mUploaded = false;
        this.mServerID = "";
        this.mScheduled = false;
    }

    public NoteSummary(String Title, String CreatedAt, String ModifiedAt, boolean Uploaded, String ServerID) {
        this.mTitle = Title;
        this.mCreatedAt = CreatedAt;
        this.mModifiedAt = ModifiedAt;
        this.mUploaded = Uploaded;
        this.mServerID = ServerID;
        this.mScheduled = false;
    }

    public void setUploaded(boolean Uploaded) {
        this.mUploaded = Uploaded;
    }

    public boolean isUploaded() {
        return mUploaded;
    }

    public int getID() {
        return mID;
    }

    public void setID(int id) {
        this.mID = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public String getModifiedAt() {
        return mModifiedAt;
    }

    public void setModifiedAt(String modifiedAt) {
        this.mModifiedAt = modifiedAt;
    }

    public String getCreatedAt() {
        return mCreatedAt;
    }

    public void setCreatedAt(String createdAt) {
        this.mCreatedAt = createdAt;
    }

    @Override
    public String toString() {
        return "Note{" +
                "id=" + mID +
                ", title='" + mTitle + '\'' +
                ", createdAt='" + mCreatedAt + '\'' +
                ", modifiedAt='" + mModifiedAt + '\'' +
                '}';
    }
}
