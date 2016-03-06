package duy.phuong.handnote.Fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import java.util.ArrayList;

import duy.phuong.handnote.Listener.BackPressListener;
import duy.phuong.handnote.Listener.RecognitionCallback;
import duy.phuong.handnote.MyView.DrawingView.FingerDrawerView;
import duy.phuong.handnote.R;

/**
 * Created by Phuong on 06/03/2016.
 */
public class CreateNoteFragment extends BaseFragment implements BackPressListener, View.OnClickListener, RecognitionCallback{
    private FingerDrawerView mDrawer;
    private ImageButton mButtonSave, mButtonDelete, mButtonUndo, mButtonRedo, mButtonColor;
    public CreateNoteFragment() {
        mLayoutRes = R.layout.fragment_create_note;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDrawer = (FingerDrawerView) mFragmentView.findViewById(R.id.FingerDrawer);
        mDrawer.setListener(this);
        mButtonSave = (ImageButton) mFragmentView.findViewById(R.id.buttonSave);
        mButtonSave.setOnClickListener(this);
        mButtonColor = (ImageButton) mFragmentView.findViewById(R.id.buttonColor);
        mButtonColor.setOnClickListener(this);
        mButtonDelete = (ImageButton) mFragmentView.findViewById(R.id.buttonDelete);
        mButtonDelete.setOnClickListener(this);
        mButtonUndo = (ImageButton) mFragmentView.findViewById(R.id.buttonUndo);
        mButtonUndo.setOnClickListener(this);
        mButtonRedo = (ImageButton) mFragmentView.findViewById(R.id.buttonRedo);
        mButtonRedo.setOnClickListener(this);
    }

    @Override
    public String fragmentIdentify() {
        return BaseFragment.CREATE_NOTE_FRAGMENT;
    }

    @Override
    public boolean doBack() {
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonSave:
                break;
            case R.id.buttonDelete:
                mDrawer.emptyDrawer();
                break;
            case R.id.buttonColor:
                break;
            case R.id.buttonUndo:
                mDrawer.undo();
                break;
            case R.id.buttonRedo:
                mDrawer.redo();
                break;
        }
    }

    @Override
    public void onRecognizeSuccess(ArrayList<Bitmap> listBitmaps) {

    }
}
