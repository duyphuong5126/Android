package com.horical.appnote.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.horical.appnote.DTO.CalendarObject;
import com.horical.appnote.Interfaces.MainInterface;
import com.horical.appnote.MyView.MyAdapter.CalendarAdapter;
import com.horical.appnote.R;
import com.horical.appnote.Supports.CalendarUtils;
import com.horical.appnote.Supports.GestureListener;
import com.horical.appnote.Supports.SupportUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by trandu on 10/08/2015.
 */
public class CalendarFragment extends BaseFragment implements View.OnClickListener, AdapterView.OnItemClickListener, GestureListener.Callback, View.OnTouchListener {

    private static final String TAG = CalendarFragment.class.getSimpleName();
    private String mDayOfWeek[] = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};

    private TextView mNowTextView, mMonthYearTextView;
    private GridView mGridView, mCalendarHeader;

    private ArrayList<CalendarObject> mCalendarList;
    private CalendarAdapter mCalendarAdapter;
    private ArrayAdapter mAdapter;
    private ImageButton mArrowLeftButton, mArrowRightButton;

    private Calendar mCalendar;
    private int mMonth, mYear;

    private GestureDetector mGestureDetector;
    private GestureListener mGestureListener;
    private Animation mAnimation;

    private MainInterface mCallback;

    public CalendarFragment() {

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCalendar = new GregorianCalendar();
        mGestureListener = new GestureListener();
        mGestureListener.setCallback(this);
        mGestureDetector = new GestureDetector(getActivity(), mGestureListener);
        mCalendarList = new ArrayList(Arrays.asList(mDayOfWeek));
        mAdapter = new ArrayAdapter(getActivity(), R.layout.list_calendar_header, mCalendarList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle bundle) {
        bundle = getArguments();
        View view = inflater.inflate(R.layout.fragment_calendar, parent, false);
        mNowTextView = (TextView) view.findViewById(R.id.nowTextView);
        mNowTextView.setText(SupportUtils.convertDateToString(Calendar.getInstance()));
        mMonthYearTextView = (TextView) view.findViewById(R.id.monthyearTextView);
        mCalendarHeader = (GridView) view.findViewById(R.id.calendarHeaderGridView);
        mGridView = (GridView) view.findViewById(R.id.calendarGridView);
        mArrowLeftButton = (ImageButton) view.findViewById(R.id.arrow_left);
        mArrowRightButton = (ImageButton) view.findViewById(R.id.arrow_right);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle bundle) {
        mCalendarHeader.setAdapter(mAdapter);
        mMonth = mCalendar.get(Calendar.MONTH);
        mYear = mCalendar.get(Calendar.YEAR);
        mCalendar.set(Calendar.DAY_OF_MONTH, 1);
        mCalendarList = CalendarUtils.setCalendarToArrayList(mCalendar);
        mMonthYearTextView.setText((mCalendar.get(Calendar.MONTH) + 1) + "/" + mCalendar.get(Calendar.YEAR));
        mCalendarAdapter = new CalendarAdapter(getActivity(), R.layout.list_calendar_item, mCalendarList);
        mGridView.setAdapter(mCalendarAdapter);
        mGridView.setOnItemClickListener(this);
        mArrowLeftButton.setOnClickListener(this);
        mArrowRightButton.setOnClickListener(this);
        mGridView.setLongClickable(true);
        mGridView.setOnTouchListener(this);
        view.setLongClickable(true);
        view.setOnTouchListener(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void updateUI() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.arrow_left:
                previous();
                break;
            case R.id.arrow_right:
                next();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        final CalendarObject obj = mCalendarList.get(position);
        if (obj.getShow() == 2) {
            next();
        } else if (obj.getShow() == 0) {
            previous();
        } else {
            mCallback.CalendarItemClicked(obj);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        return false;
    }

    @Override
    public void next() {
        mMonth++;
        if (mMonth > 11) {
            mYear++;
            mMonth = 0;
        }
        mCalendar.set(Calendar.MONTH, mMonth);
        mCalendar.set(Calendar.YEAR, mYear);
        mMonthYearTextView.setText((mCalendar.get(Calendar.MONTH) + 1) + "/" + mCalendar.get(Calendar.YEAR));
        mCalendarList.clear();
        mCalendarList.addAll(CalendarUtils.setCalendarToArrayList(mCalendar));
        mCalendarAdapter.notifyDataSetChanged();
        mAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.bounce_right);
        mGridView.startAnimation(mAnimation);
    }

    @Override
    public void previous() {
        mMonth--;
        if (mMonth < 0) {
            mYear--;
            mMonth = 11;
        }
        mCalendar.set(Calendar.MONTH, mMonth);
        mCalendar.set(Calendar.YEAR, mYear);
        mMonthYearTextView.setText((mCalendar.get(Calendar.MONTH) + 1) + "/" + mCalendar.get(Calendar.YEAR));
        mCalendarList.clear();
        mCalendarList.addAll(CalendarUtils.setCalendarToArrayList(mCalendar));
        mCalendarAdapter.notifyDataSetChanged();
        mAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.bounce_left);
        mGridView.startAnimation(mAnimation);
    }
    public void setCallback(MainInterface callback){
        this.mCallback = callback;
    }

}

