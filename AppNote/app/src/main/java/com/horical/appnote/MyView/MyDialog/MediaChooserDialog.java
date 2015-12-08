package com.horical.appnote.MyView.MyDialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.horical.appnote.R;
import com.horical.appnote.Supports.MediaUtils;

/**
 * Created by Phuong on 29/08/2015.
 */
public class MediaChooserDialog extends Dialog implements LinearLayout.OnClickListener {

    public static enum KindOfPlayer{
        DEFAULT_PLAYER, MEDIA_APPLICATION
    }

    public String getMediaPath() {
        return mMediaPath;
    }

    public void setMediaPath(String MediaPath) {
        this.mMediaPath = MediaPath;
    }

    private String mMediaPath;
    private LinearLayout mChooseApplication, mChooseDefault;

    private MediaCallback callback;
    public MediaChooserDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_media_chooser);
        mChooseApplication = (LinearLayout) findViewById(R.id.chooseApplications);
        mChooseApplication.setOnClickListener(this);
        mChooseDefault = (LinearLayout) findViewById(R.id.chooseDefault);
        mChooseDefault.setOnClickListener(this);
    }

    public interface MediaCallback{
        public void OpenMedia(KindOfPlayer kindOfPlayer, String path);
    }

    public void setMediaCallback(MediaCallback mediaCallback){
        this.callback = mediaCallback;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.chooseApplications:
                callback.OpenMedia(KindOfPlayer.MEDIA_APPLICATION, mMediaPath);
                break;
            case R.id.chooseDefault:
                callback.OpenMedia(KindOfPlayer.DEFAULT_PLAYER, mMediaPath);
                break;
        }
        this.dismiss();
    }
}
