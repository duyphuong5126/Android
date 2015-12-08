package com.horical.appnote.Interfaces;

import com.horical.appnote.DTO.CalendarObject;
import com.horical.appnote.DTO.FileDTO.FileData;
import com.horical.appnote.DTO.NoteDTO.NoteData;
import com.horical.appnote.DTO.NoteDTO.NoteSummary;

import java.util.ArrayList;

/**
 * Created by Phuong on 24/07/2015.
 */
public interface MainInterface {
    void ChangeFragment(String fragmentCaller, String fragmentDestination);

    void PassNoteSummary(NoteSummary noteSummary);

    void PassEditInfor(NoteData noteData);
    void CalendarItemClicked(CalendarObject calendarObject);
    void PlayMedia(String path);
    ArrayList<NoteData> getAllNotes();
    void reloadListNote();
    ArrayList<FileData> getAllFiles(String type);
    void reloadListFile(String type);
    void onLogout();
    boolean checkInternetAvailable();
    void reloadListReminder();
    boolean isReloadingFiles();
    void showLoadingEffect(boolean flag, String message);
    void restartApplication();
    NoteData getRecentNote();
    void Back();
}
