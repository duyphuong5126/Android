package duy.phuong.handnote.Fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import duy.phuong.handnote.Listener.BackPressListener;
import duy.phuong.handnote.Listener.RecognitionCallback;
import duy.phuong.handnote.MyView.DrawingView.FingerDrawerView;
import duy.phuong.handnote.R;
import duy.phuong.handnote.RecognitionAPI.BitmapProcessor;
import duy.phuong.handnote.RecognitionAPI.Recognizer;
import duy.phuong.handnote.Support.SupportUtils;

/**
 * Created by Phuong on 06/03/2016.
 */
public class CreateNoteFragment extends BaseFragment implements BackPressListener, View.OnClickListener, RecognitionCallback {
    private FingerDrawerView mDrawer;
    private ImageButton mButtonSave, mButtonDelete, mButtonUndo, mButtonRedo, mButtonColor;
    private BitmapProcessor mBitmapProcessor;
    private TextView mTvResult;

    private Recognizer mRecognizer;

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
        mTvResult = (TextView) mFragmentView.findViewById(R.id.tvResult);

        mBitmapProcessor = new BitmapProcessor();

        mRecognizer = new Recognizer(mListener.getGlobalSOM(), mListener.getMapNames());
    }

    @Override
    public String fragmentIdentify() {
        return BaseFragment.CREATE_NOTE_FRAGMENT;
    }

    @Override
    public boolean doBack() {
        if (!mDrawer.isEmpty()) {
            deleteData();
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonSave:
                break;
            case R.id.buttonDelete:
                deleteData();
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
        String text = "";
        for (int i = 0; i < listBitmaps.size(); i++) {
            String result = mRecognizer.recognize(BitmapProcessor.resizeBitmap(listBitmaps.get(i), 20, 28));
            Log.d("Result", "bitmap " + i + " :" + result);

            String[] list = new String[result.length()];
            for (int j = 0; j < result.length(); j++) {
                list[j] = result.substring(j, j + 1);
            }

            if (list.length > 0) {
                mBitmapProcessor.featureExtraction(listBitmaps.get(i), list);
            }
            text += result + " - ";
        }
        mTvResult.setText(text);
    }

    private void deleteData() {
        mDrawer.emptyDrawer();
        mTvResult.setText("");
    }
}
