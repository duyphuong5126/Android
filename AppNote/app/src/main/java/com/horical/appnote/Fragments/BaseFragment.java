package com.horical.appnote.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.horical.appnote.Interfaces.MainInterface;


/**
 * Created by Phuong on 24/07/2015.
 */
public abstract class BaseFragment extends Fragment{
    public static final String NewNoteFragment = "NewNoteFragment";
    public static final String ListNotesFragment = "ListNotesFragment";
    public static final String ViewNoteFragment = "ViewNoteFragment";
    public static final String CalendarFragment = "CalendarFragment";
    public static final String SettingsFragment = "SettingsFragment";
    public static final String FileManagerFragment = "FileManagerFragment";
    public static final String UpdateNoteFragment = "UpdateNoteFragment";
    public static final String ViewReminderFragment = "ViewReminderFragment";
    public static final String LoginFragment = "LoginFragment";
    public static final String SignUpFragment = "SignUpFragment";
    public static final String ForgotAccountFragment = "ForgotAccountFragment";
    public static final String ViewAccountFragment = "ViewAccountFragment";


    private String mFragmentName;
    protected int mLayout_xml_id;
    protected View mFragmentView;
    protected MainInterface mMainInterface;
    protected Activity mActivity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mMainInterface = (MainInterface) activity;
        this.mActivity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mFragmentView = inflater.inflate(mLayout_xml_id, container, false);
        return mFragmentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void setFragmentName(String fragmentName) {
        this.mFragmentName = fragmentName;
    }

    public String getFragmentName() {
        return this.mFragmentName;
    }

    public void setMainListener(MainInterface listener) {
        mMainInterface = listener;
    }

    public abstract void updateUI();
}
