package com.horical.appnote.MyView.MyAdapter;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.horical.appnote.R;
import com.horical.appnote.DTO.NoteDTO.NoteReminder;
import com.horical.appnote.DTO.CalendarObject;
import com.horical.appnote.Supports.CalendarUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by dutn on 27/07/2015.
 */
public class CalendarAdapter extends ArrayAdapter {

    private Activity mActivity;
    private int mLayoutId;
    private ArrayList<CalendarObject> mArrayList;
    private NoteReminder mNoteReminder;
    private List<NoteReminder> mListNoteReminders;

    public CalendarAdapter(Activity context, int layoutId, ArrayList<CalendarObject> arrayList) {
        super(context, layoutId, arrayList);
        this.mActivity = context;
        this.mLayoutId = layoutId;
        this.mArrayList = arrayList;
        this.mListNoteReminders = new ArrayList<NoteReminder>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = mActivity.getLayoutInflater();
        convertView = inflater.inflate(mLayoutId, null);
        CalendarObject obj = mArrayList.get(position);
        TextView tv = (TextView) convertView.findViewById(R.id.numCalendar);
        if (obj.getDay() == CalendarUtils.currentDay() && obj.getShow() == 1) {
            tv.setTextColor(Color.WHITE);
            tv.setTypeface(null, Typeface.BOLD);
            tv.setBackgroundResource(R.drawable.circle_dark_green);
        } else if (obj.getShow() == 0 || obj.getShow() == 2) {
            tv.setTextColor(Color.GRAY);
        } else {
            tv.setTextColor(Color.BLACK);
        }
        for (int i = 0; i < mListNoteReminders.size(); i++) {
            mNoteReminder = mListNoteReminders.get(i);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            try {
                Date d = sdf.parse(mNoteReminder.getTimeComplete());
                int day = d.getDate();
                int month = d.getMonth() + 1;
                int year = d.getYear() + 1900;
                if (obj.getDay() == day && obj.getMonth() == month && obj.getYear() == year) {
                    tv.setTextColor(Color.WHITE);
                    tv.setTypeface(null, Typeface.BOLD);
                    tv.setBackgroundResource(R.drawable.circle_green_float);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        tv.setText(obj.getDay() + "");
        return convertView;
    }
}
