package com.horical.appnote.MyView.MyAdapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import com.horical.appnote.R;
import com.horical.appnote.DTO.NoteDTO.NoteReminder;
import com.horical.appnote.Supports.SupportUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by trandu on 11/08/2015.
 */
public class ViewReminderAdapter extends ArrayAdapter implements View.OnClickListener{
    private Activity mActivity;
    private int mLayoutID;
    private ArrayList<NoteReminder> mArrayList;
    private Callback mCallback;
    private HashMap<Integer, Integer> mListRemove;

    @SuppressLint("UseSparseArrays")
    public ViewReminderAdapter(Activity context, int LayoutID, ArrayList<NoteReminder> arrayList) {
        super(context, LayoutID, arrayList);
        this.mActivity = context;
        this.mLayoutID = LayoutID;
        this.mArrayList = arrayList;
        this.mListRemove = new HashMap<>();

        Log.d("ViewRemind", "in");
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        NoteReminder mNoteReminder = mArrayList.get(position);
        LayoutInflater inflater = mActivity.getLayoutInflater();
        if (mNoteReminder.getContent().equals("") && mNoteReminder.getTimeComplete().equals("")) {
            convertView = inflater.inflate(R.layout.list_reminder_fake_item, null);
        } else {
            convertView = inflater.inflate(mLayoutID, null);
            TextView mTextTime = (TextView) convertView.findViewById(R.id.timeReminderTextView);
            TextView mContent = (TextView) convertView.findViewById(R.id.contentReminderTextView);
            CheckBox mCheckBox = (CheckBox) convertView.findViewById(R.id.checkComplete);
            if (mNoteReminder.readyToRemind() <= 0) {
                mCheckBox.setChecked(true);
            }
            ImageButton mImageButton = (ImageButton) convertView.findViewById(R.id.deleteReminderButton);
            mImageButton.setId(SupportUtils.getRandomID());
            if (!mListRemove.containsKey(mImageButton.getId())) {
                mListRemove.put(mImageButton.getId(), mNoteReminder.getReminderID());
            }
            mImageButton.setOnClickListener(this);
            try {
                mTextTime.setText(mNoteReminder.getTimeComplete().substring(mNoteReminder.getTimeComplete().lastIndexOf(" "), mNoteReminder.getTimeComplete().length()));
            } catch (Exception ex) {
            }
            mContent.setTextColor(Color.parseColor("#000000"));
            mContent.setText(mNoteReminder.getContent());
        }
        return convertView;
    }

    @Override
    public void onClick(View view) {
        mCallback.deleteReminder(mListRemove.get(view.getId()));
    }

    public interface Callback {
        void deleteReminder(int id);
    }

    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }

}
