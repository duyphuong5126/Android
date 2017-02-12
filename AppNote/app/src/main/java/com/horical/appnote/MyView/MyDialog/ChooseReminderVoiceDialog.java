package com.horical.appnote.MyView.MyDialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.horical.appnote.Background.MediaService;
import com.horical.appnote.R;
import com.horical.appnote.Supports.SupportUtils;

import java.lang.reflect.Field;

/**
 * Created by phuong on 10/10/2015.
 */
class ChooseReminderVoiceDialog extends Dialog {
    private RadioGroup mGroupVoices;
    private Context mContext;
    private Callback mCallback;

    private Intent mReminderService;

    public ChooseReminderVoiceDialog(Activity activity) {
        super(activity);
    }

    ChooseReminderVoiceDialog(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_voice_picker);
        mReminderService = new Intent(mContext, MediaService.class);
        mGroupVoices = (RadioGroup) findViewById(R.id.groupVoices);
        Button mButtonFinish = (Button) findViewById(R.id.buttonFinish);
        Field[] fields = R.raw.class.getFields();
        for (Field field : fields) {
            this.addVoice(field.getName());
        }
        mButtonFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RadioButton radioButton = (RadioButton) findViewById(mGroupVoices.getCheckedRadioButtonId());
                mCallback.setAudioName(radioButton.getText().toString());
                if (MediaService.isRunning()) {
                    mContext.stopService(mReminderService);
                }
                dismiss();
            }
        });
        mGroupVoices.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton radioButton = (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());
                if (MediaService.isRunning()) {
                    mContext.stopService(mReminderService);
                }
                MediaService.setAudioName(radioButton.getText().toString());
                mContext.startService(mReminderService);
            }
        });
    }

    private void addVoice(String name) {
        RadioButton radioButton = new RadioButton(mContext);
        radioButton.setId(SupportUtils.getRandomID());
        radioButton.setLayoutParams(
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        );
        radioButton.setText(name);
        mGroupVoices.addView(radioButton);
    }

    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }

    public interface Callback {
        void setAudioName(String name);
    }
}
