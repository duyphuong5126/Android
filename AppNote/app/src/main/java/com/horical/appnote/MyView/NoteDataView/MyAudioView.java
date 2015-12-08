package com.horical.appnote.MyView.NoteDataView;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.horical.appnote.Interfaces.OpenMediaInterface;
import com.horical.appnote.R;
import com.horical.appnote.Supports.SupportUtils;

/**
 * Created by Phuong on 11/08/2015.
 */
public class MyAudioView extends View {
    private Activity activity;
    private int layout_xml;
    private int buttonPlayer, buttonDeleteThis;
    private int textviewName, textviewInfor;
    private View currentView;
    private int layoutButtonDelete;

    private String mPath;

    private LinearLayout mLayoutButtonDelete;

    private OpenMediaInterface mOpenMediaListener;

    public void setOpenFileListener(OpenMediaInterface openFileListener) {
        this.mOpenMediaListener = openFileListener;
    }

    public MyAudioView(Activity activity, String path) {
        super(activity);
        this.activity = activity;
        this.layout_xml = R.layout.note_line_audio;
        this.currentView = new View(activity);
        this.mPath = path;
        this.init();
    }



    public void init() {
        LayoutInflater inflater = activity.getLayoutInflater();
        currentView = inflater.inflate(layout_xml, null);
        buttonPlayer = SupportUtils.getRandomID();
        buttonDeleteThis = buttonPlayer + 1;
        textviewName = buttonPlayer + 2;
        textviewInfor = buttonPlayer + 3;
        layoutButtonDelete = buttonPlayer + 4;
        ((ImageButton) currentView.findViewById(R.id.buttonPlayAudio)).setId(buttonPlayer);
        ((ImageButton) currentView.findViewById(R.id.buttonDeleteThis)).setId(buttonDeleteThis);
        ((ImageButton) currentView.findViewById(buttonPlayer)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mOpenMediaListener.OpenFile(mPath);
            }
        });
        mLayoutButtonDelete = (LinearLayout) currentView.findViewById(R.id.layoutButtonDelete);
        mLayoutButtonDelete.setId(layoutButtonDelete);
    }

    public void setTextViewName(String name) {
        ((TextView) currentView.findViewById(R.id.FileName)).setText(name);
        ((TextView) currentView.findViewById(R.id.FileName)).setId(textviewName);
    }

    public void setTextViewInfor(String infor) {
        ((TextView) currentView.findViewById(R.id.FileInfor)).setText(infor);
        ((TextView) currentView.findViewById(R.id.FileInfor)).setId(textviewInfor);
    }

    public ImageButton getPlayerButton() {
        return ((ImageButton) currentView.findViewById(buttonPlayer));
    }

    public ImageButton getDeleteThisButton() {
        return ((ImageButton) currentView.findViewById(buttonDeleteThis));
    }

    public View getView() {
        return currentView;
    }

    public void showDivider(){
        ((LinearLayout) currentView.findViewById(R.id.layoutDivider)).setVisibility(View.VISIBLE);
    }

    public void hideDivider(){
        ((LinearLayout) currentView.findViewById(R.id.layoutDivider)).setVisibility(View.GONE);
    }

    public void hideDeleteButton() {
        mLayoutButtonDelete.setVisibility(GONE);
    }
}
