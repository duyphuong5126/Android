package duy.phuong.handnote.Fragment;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import duy.phuong.handnote.DTO.Note;
import duy.phuong.handnote.Listener.BackPressListener;
import duy.phuong.handnote.R;

/**
 * Created by Phuong on 17/04/2016.
 */
public class ViewNoteFragment extends BaseFragment implements BackPressListener{
    private Note mNote;
    private ImageView mNoteImage;
    private TextView mNoteContent;
    public ViewNoteFragment() {
        mLayoutRes = R.layout.fragment_view_note;
    }

    public void setNote(Note Note) {
        this.mNote = Note;
        if (mNote != null) {
            if (mNoteImage != null) {
                mNoteImage.setImageBitmap(mNote.mImage);
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
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mListener != null) {
            mNote = mListener.getCurrentNote();
        }
        if (mNote != null) {
            mNoteImage.setImageBitmap(mNote.mImage);
            mNoteContent.setText(mNote.mContent);
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
}
