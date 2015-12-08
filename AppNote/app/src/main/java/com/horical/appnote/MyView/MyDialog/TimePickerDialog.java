package com.horical.appnote.MyView.MyDialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

import com.horical.appnote.R;
import com.horical.appnote.Supports.CalendarUtils;
import com.horical.appnote.Supports.LanguageUtils;

/**
 * Created by trandu on 12/08/2015.
 */
public class TimePickerDialog extends Dialog implements View.OnClickListener {

    private TimePicker mTimePicker;
    private Button mCancelButton;
    private Button mOKButton;
    private Callback mCallback;

    public TimePickerDialog(Activity context) {
        super(context);
    }

    public TimePickerDialog(Context context, int theme) {
        super(context, theme);
    }

    public TimePickerDialog(Context context, boolean cancelable, OnCancelListener listener) {
        super(context, cancelable, listener);
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.dialog_timepicker);
        this.setTitle(LanguageUtils.getAddNewReminderString());
        init();
    }

    public void init() {
        mTimePicker = (TimePicker) findViewById(R.id.timePicker);
        mCancelButton = (Button) findViewById(R.id.btn_cancel);
        mOKButton = (Button) findViewById(R.id.btn_ok);
        mCancelButton.setOnClickListener(this);
        mOKButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                this.dismiss();
                break;
            case R.id.btn_ok:
                mCallback.setTimeForTextView(CalendarUtils.addZero(mTimePicker.getCurrentHour()) + ":" + CalendarUtils.addZero(mTimePicker.getCurrentMinute()));
                this.dismiss();
                break;
        }
    }

    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }

    public interface Callback {
        void setTimeForTextView(String time);
    }

}
