package com.horical.appnote.MyView.NoteDataView;

import android.view.View;

import com.horical.appnote.Interfaces.NoteDataLine;

/**
 * Created by Phuong on 12/08/2015.
 */
public class PairDataView {
    private NoteDataLine mNoteDataLine;
    private View mView;

    public PairDataView() {
    }

    public PairDataView(NoteDataLine noteDataLine, View view) {
        this.mNoteDataLine = noteDataLine;
        this.mView = view;
    }

    public NoteDataLine getNoteDataInterface() {
        return mNoteDataLine;
    }

    public void setNoteDataInterface(NoteDataLine noteDataLine) {
        this.mNoteDataLine = noteDataLine;
    }

    public View getView() {
        return mView;
    }

    public void setView(View view) {
        this.mView = view;
    }
}
