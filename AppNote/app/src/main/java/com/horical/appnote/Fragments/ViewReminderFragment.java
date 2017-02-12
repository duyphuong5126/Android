package com.horical.appnote.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.horical.appnote.LocalStorage.DAO.NoteReminderDAO;
import com.horical.appnote.MyView.MyDialog.AddReminderDialog;
import com.horical.appnote.R;
import com.horical.appnote.MyView.MyAdapter.ViewReminderAdapter;
import com.horical.appnote.DTO.NoteDTO.NoteReminder;
import com.horical.appnote.Supports.CalendarUtils;
import com.horical.appnote.Supports.LanguageUtils;

import java.util.ArrayList;

/**
 * Created by trandu on 10/08/2015.
 */

public class ViewReminderFragment extends BaseFragment implements AdapterView.OnItemClickListener, View.OnClickListener,
        AddReminderDialog.Callback, ViewReminderAdapter.Callback {

    private TextView mDateTextView, mNoticeTextView;
    private ListView mListView;
    private ArrayList<NoteReminder> mArrayList;
    private ImageButton mAddReminderButton;
    private String mDateSelected;

    private NoteReminderDAO mNoteReminderDAO;

    public ViewReminderFragment(){

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle bundle) {
        View view = inflater.inflate(R.layout.fragment_view_reminder, parent, false);
        mListView = (ListView) view.findViewById(R.id.view_reminder_listview);
        mAddReminderButton = (ImageButton) view.findViewById(R.id.btn_float);
        mDateTextView = (TextView) view.findViewById(R.id.dateTextView);
        mNoticeTextView = (TextView) view.findViewById(R.id.noticeTextView);

        mNoteReminderDAO = new NoteReminderDAO(mActivity);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        bundle = getArguments();
        mDateSelected = CalendarUtils.addZero(bundle.getInt("year")) +
                "-" + CalendarUtils.addZero(bundle.getInt("month")) +
                "-" + CalendarUtils.addZero(bundle.getInt("day"));
        mDateTextView.setText(mDateSelected);
        this.refreshListReminder();
        mListView.setOnItemClickListener(this);
        mAddReminderButton.setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
    }

    @Override
    public void updateUI() {

    }

    @Override
    public void onItemClick(AdapterView parent, View view, int position, long id) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_float:
                showDialogAddReminder();
                break;
        }
    }

    public void check() {
        if (mArrayList.size() != 0) {
            mNoticeTextView.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
        } else {
            mListView.setVisibility(View.GONE);
            mNoticeTextView.setVisibility(View.VISIBLE);
        }
    }

    public void showDialogAddReminder() {
        AddReminderDialog mAddReminderDialog = new AddReminderDialog(getActivity());
        mAddReminderDialog.setCallback(this);
        mAddReminderDialog.show();
    }


    @Override
    public void setDateForTextView(TextView tv) {
        tv.setText(mDateSelected);
    }
    @Override
    public void setTimeForTextView(TextView tv) {
        tv.setText(CalendarUtils.getTimeNow().trim());
    }

    @Override
    public boolean addReminder(NoteReminder noteReminder) {
        mNoteReminderDAO = new NoteReminderDAO(mActivity, noteReminder);
        boolean addResult = mNoteReminderDAO.insertNoteReminder();
        this.refreshListReminder();
        mMainInterface.reloadListReminder();
        return addResult;
    }

    public void refreshListReminder() {
        int mCurrentFakeItem = 0;
        mArrayList = new ArrayList<>();
        int mNumOfFakeItem = 10;
        for (int i = 0; i< mNumOfFakeItem; i++) {
            mArrayList.add(new NoteReminder());
        }
        for (NoteReminder noteReminder : mNoteReminderDAO.getAllRemindersByTime(mDateSelected)) {
            if (mCurrentFakeItem >= mNumOfFakeItem) {
                mArrayList.add(noteReminder);
            } else {
                mArrayList.set(mCurrentFakeItem, noteReminder);
                mCurrentFakeItem++;
            }
        }
        try {
            ViewReminderAdapter mAdapter = new ViewReminderAdapter(getActivity(), R.layout.list_reminder_item, mArrayList);
            mAdapter.setCallback(this);
            mListView.setAdapter(mAdapter);
            check();
        } catch (NullPointerException e) {
            Toast.makeText(mActivity, LanguageUtils.getEmptyListReminderString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void deleteReminder(int id) {
        mNoteReminderDAO.deleteReminder(id);
        this.refreshListReminder();
        mMainInterface.reloadListReminder();
    }
}
