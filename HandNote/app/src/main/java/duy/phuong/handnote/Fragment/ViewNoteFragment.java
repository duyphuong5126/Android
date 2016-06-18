package duy.phuong.handnote.Fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.Serializable;

import duy.phuong.handnote.DTO.Note;
import duy.phuong.handnote.Listener.BackPressListener;
import duy.phuong.handnote.R;

/**
 * Created by Phuong on 17/04/2016.
 */
public class ViewNoteFragment extends BaseFragment implements BackPressListener, MainFragment.ShowNoteListener, Serializable{
    private Note mNote;
    private ImageView mNoteImage;
    private TextView mNoteContent;
    private TextView mNoteEmpty;
    public ViewNoteFragment() {
        mLayoutRes = R.layout.fragment_view_note;
    }

    public void setNote(Note Note) {
        this.mNote = Note;
        if (mNote != null) {
            if (mNoteImage != null) {
                Bitmap bitmap = BitmapFactory.decodeFile(mNote.mBitmapPath);
                mNoteImage.setImageBitmap(bitmap);
            }
            if (mNoteContent != null) {
                mNoteContent.setText(mNote.mContent);
            }
        }
    }

    public Note getNote() {
        return mNote;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mNoteImage = (ImageView) mFragmentView.findViewById(R.id.noteImage);
        mNoteContent = (TextView) mFragmentView.findViewById(R.id.noteContent);
        mNoteEmpty = (TextView) mFragmentView.findViewById(R.id.textEmpty);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mListener != null) {
            mNote = mListener.getCurrentNote();
        }
        if (mNote != null) {
            showNote();
        } else {
            mNoteEmpty.setVisibility(View.VISIBLE);
            mNoteImage.setVisibility(View.GONE);
            mNoteContent.setVisibility(View.GONE);
        }
    }

    @Override
    public String fragmentIdentify() {
        return VIEW_NOTE_FRAGMENT;
    }

    @Override
    public boolean doBack() {
        return false;
    }

    @Override
    public void showNote(Note note) {
        if (note != null) {
            mNote = note;
            showNote();
        } else {
            mNoteEmpty.setVisibility(View.VISIBLE);
            mNoteImage.setVisibility(View.GONE);
            mNoteContent.setVisibility(View.GONE);
        }
    }

    private void showNote() {
        mNoteEmpty.setVisibility(View.GONE);
        mNoteImage.setVisibility(View.VISIBLE);
        mNoteContent.setVisibility(View.VISIBLE);
        Bitmap bitmap = BitmapFactory.decodeFile(mNote.mBitmapPath);
        mNoteImage.setImageBitmap(bitmap);
        mNoteContent.setText(mNote.mContent);
    }
}
