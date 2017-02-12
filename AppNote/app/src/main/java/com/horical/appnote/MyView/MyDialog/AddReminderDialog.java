package com.horical.appnote.MyView.MyDialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.horical.appnote.R;
import com.horical.appnote.DTO.NoteDTO.NoteReminder;
import com.horical.appnote.Supports.LanguageUtils;

/**
 * Created by trandu on 09/08/2015.
 */
public class AddReminderDialog extends Dialog implements View.OnClickListener, TimePickerDialog.Callback, DatePickerDialog.Callback,
        ChooseReminderVoiceDialog.Callback
{

    private TextView mDateTextView, mTimeTextView, mVoiceTextView;
    private EditText mContentEditText;
    private NoteReminder mNoteReminder;
    private Callback mCallback;
    private ReminderCallback mReminderCallback;

    public static boolean isReminderForNote = false;

    public AddReminderDialog(Context context) {
        super(context);
    }

    public AddReminderDialog(Context context, int theme) {
        super(context, theme);
    }

    protected AddReminderDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_add_reminder);
        setTitle(LanguageUtils.getCreateReminderString());
        init();
    }

    private void init() {
        DisplayMetrics mDisplayMetrics = getContext().getResources().getDisplayMetrics();

        TextView mTimeTextViewTitle = (TextView) findViewById(R.id.timeTextViewTitle);
        mTimeTextViewTitle.setText(LanguageUtils.getTimeString());

        TextView mDateTextViewTitle = (TextView) findViewById(R.id.dateTextViewTitle);
        mDateTextViewTitle.setText(LanguageUtils.getDateString());

        Window window = this.getWindow();
        if (window != null) {
            window.setLayout(mDisplayMetrics.widthPixels, LinearLayout.LayoutParams.WRAP_CONTENT);
        }
        mDateTextView = (TextView) findViewById(R.id.dateTextView);
        mTimeTextView = (TextView) findViewById(R.id.timeTextView);
        mVoiceTextView= (TextView) findViewById(R.id.VoiceName);

        Button mCancelButton = (Button) findViewById(R.id.dialog_add_reminder_cancel);
        mCancelButton.setText(LanguageUtils.getCancelString());

        Button mOKButton = (Button) findViewById(R.id.dialog_add_reminder_ok);

        mContentEditText = (EditText) findViewById(R.id.dialog_add_reminder_content);
        mContentEditText.setHint(LanguageUtils.getContentString());

        Button mGetVoiceButton = (Button) findViewById(R.id.BrowseVoice);
        mGetVoiceButton.setText(LanguageUtils.getBrowseVoiceString());
        mGetVoiceButton.setOnClickListener(this);
        mCancelButton.setOnClickListener(this);
        mOKButton.setOnClickListener(this);
        mTimeTextView.setOnClickListener(this);
        mDateTextView.setOnClickListener(this);
        if (mCallback != null){
            mCallback.setDateForTextView(mDateTextView);
            mCallback.setTimeForTextView(mTimeTextView);
        }
        mNoteReminder = new NoteReminder();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_add_reminder_cancel:
                this.cancel();
                break;
            case R.id.dialog_add_reminder_ok:
                mNoteReminder.setTimeComplete(mDateTextView.getText() + " " + mTimeTextView.getText());
                mNoteReminder.setContent(mContentEditText.getText().toString());
                mNoteReminder.setStatus(1);
                if (!isReminderForNote){
                    boolean result = mCallback.addReminder(mNoteReminder);
                    this.dismiss();
                    if (result){
                        Toast.makeText(getContext(), LanguageUtils.getReminderSavedString(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), LanguageUtils.getReminderExistedString(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    mReminderCallback.PassReminder(mNoteReminder);
                    this.dismiss();
                    AddReminderDialog.isReminderForNote = false;
                }
                break;
            case R.id.dateTextView:
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), 0);
                datePickerDialog.setCallback(this);
                datePickerDialog.show();
                break;
            case R.id.timeTextView:
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), 0);
                timePickerDialog.setCallback(this);
                timePickerDialog.show();
                break;
            case R.id.BrowseVoice:
                ChooseReminderVoiceDialog reminderVoiceDialog = new ChooseReminderVoiceDialog(getContext());
                reminderVoiceDialog.setTitle(LanguageUtils.getSelectAudioString());
                reminderVoiceDialog.setCallback(this);
                reminderVoiceDialog.show();
        }
    }

    @Override
    public void setTimeForTextView(String time) {
        mTimeTextView.setText(time);
    }

    @Override
    public void setDateForTextView(String date) {
        mDateTextView.setText(date);
    }

    @Override
    public void setAudioName(String name) {
        mVoiceTextView.setText(name);
        mNoteReminder.setVoice(name);
    }

    public interface Callback {
        void setDateForTextView(TextView tv);

        void setTimeForTextView(TextView tv);

        boolean addReminder(NoteReminder noteReminder);
    }

    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }

    public interface ReminderCallback{
        void PassReminder(NoteReminder reminder);
    }

    public void setReminderCallback(ReminderCallback reminderCallback){
        this.mReminderCallback = reminderCallback;
    }
}
