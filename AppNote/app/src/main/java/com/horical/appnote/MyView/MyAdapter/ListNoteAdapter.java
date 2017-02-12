package com.horical.appnote.MyView.MyAdapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.horical.appnote.DTO.NoteDTO.NoteData;
import com.horical.appnote.DTO.NoteDTO.NoteSummary;
import com.horical.appnote.Interfaces.NoteDataLine;
import com.horical.appnote.MyView.NoteDataView.FormattedText;
import com.horical.appnote.R;
import com.horical.appnote.Supports.CalendarUtils;
import com.horical.appnote.Supports.SupportUtils;

import java.util.ArrayList;

/**
 * Created by Phuong on 27/08/2015.
 */
public class ListNoteAdapter extends ArrayAdapter<NoteData> {
    private ArrayList<NoteData> mListNoteData;
    private Activity mActivity;

    public ListNoteAdapter(Activity activity, int resource, ArrayList<NoteData> listNoteData) {
        super(activity, resource, listNoteData);
        this.mListNoteData = new ArrayList<>();
        this.mListNoteData = listNoteData;
        this.mActivity = activity;
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        NoteData noteData = mListNoteData.get(position);
        LayoutInflater inflater = this.mActivity.getLayoutInflater();
        NoteSummary noteSummary = noteData.getNoteSummary();
        //if this position isn't group item
        if (noteData.getNoteData() == null){
            convertView = inflater.inflate(R.layout.list_note_group_item, null);
            ((TextView) convertView.findViewById(R.id.tvGroup)).
                    setText(CalendarUtils.checkTimeStamp(CalendarUtils.getDateFromString(noteSummary.getCreatedAt())));
        } else {
            convertView = inflater.inflate(R.layout.list_note_item, null);
            ArrayList<NoteDataLine> list = noteData.getNoteData();
            ((TextView) convertView.findViewById(R.id.tvNoteTitle)).
                    setText(SupportUtils.getShortContent(FormattedText.getContentFromFormatedJSON(noteSummary.getTitle())));
            ((TextView) convertView.findViewById(R.id.tvCreateDate)).setText(noteSummary.getCreatedAt());
            if (list != null && list.size() > 0) {
                ((TextView) convertView.findViewById(R.id.tvNoteShortContent)).
                        setText(SupportUtils.getShortContent(FormattedText.getContentFromFormatedJSON(list.get(0).toString())));
            }
            if (!noteSummary.isScheduled()) {
                convertView.findViewById(R.id.imageScheduled).setVisibility(View.GONE);
            }
            if (position >= mListNoteData.size() - 1) {
                convertView.findViewById(R.id.noteDivider).setVisibility(View.GONE);
            }
        }
        return convertView;
    }

    @Override
    public boolean isEnabled(int position) {
        return !(mListNoteData.get(position).getNoteData() == null || mListNoteData.get(position).getNoteData().size() <= 0);
    }
}
