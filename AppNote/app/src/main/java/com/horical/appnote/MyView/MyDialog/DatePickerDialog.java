package com.horical.appnote.MyView.MyDialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import com.horical.appnote.R;
import com.horical.appnote.Supports.CalendarUtils;
import com.horical.appnote.Supports.LanguageUtils;

/**
 * Created by trandu on 12/08/2015.
 */
class DatePickerDialog extends Dialog implements View.OnClickListener {

    private DatePicker mDatePicker;
    private Callback mCallback;

    public DatePickerDialog(Context context) {
        super(context);
    }

    DatePickerDialog(Context context, int theme) {
        super(context, theme);
    }

    public DatePickerDialog(Context context, boolean cancelable, OnCancelListener listener) {
        super(context, cancelable, listener);
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.dialog_datepicker);
        this.setTitle(LanguageUtils.getAddNewReminderString());
        init();
    }

    private void init() {
        mDatePicker = (DatePicker) findViewById(R.id.datePicker);
        Button mCancelButton = (Button) findViewById(R.id.btn_cancel);
        Button mOKButton = (Button) findViewById(R.id.btn_ok);
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
                mCallback.setDateForTextView(CalendarUtils.addZero(mDatePicker.getYear()) + "-" +
                        CalendarUtils.addZero(mDatePicker.getMonth() + 1) + "-" +
                        CalendarUtils.addZero(mDatePicker.getDayOfMonth()));
                this.dismiss();
                break;
        }
    }

    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }

    public interface Callback {
        void setDateForTextView(String date);
    }

}
