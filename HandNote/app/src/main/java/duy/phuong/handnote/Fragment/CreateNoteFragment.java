package duy.phuong.handnote.Fragment;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import duy.phuong.handnote.DTO.FloatingImage;
import duy.phuong.handnote.Listener.BackPressListener;
import duy.phuong.handnote.MyView.DrawingView.FingerDrawerView;
import duy.phuong.handnote.R;
import duy.phuong.handnote.RecognitionAPI.BitmapProcessor;
import duy.phuong.handnote.RecognitionAPI.MachineLearning.Input;
import duy.phuong.handnote.RecognitionAPI.Recognizer;

/**
 * Created by Phuong on 06/03/2016.
 */
public class CreateNoteFragment extends BaseFragment implements BackPressListener, View.OnClickListener, BitmapProcessor.RecognitionCallback {
    private FingerDrawerView mDrawer;
    private ImageButton mButtonSave, mButtonDelete, mButtonUndo, mButtonRedo, mButtonColor;
    private TextView mTvResult;

    private Recognizer mRecognizer;

    private HashMap<Input, Point> mCurrentRecognized;

    public CreateNoteFragment() {
        mLayoutRes = R.layout.fragment_create_note;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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

        mRecognizer = new Recognizer(mListener.getGlobalSOM(), mListener.getMapNames());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mDrawer = (FingerDrawerView) mFragmentView.findViewById(R.id.FingerDrawer);
        mDrawer.setListener(this);
        mDrawer.setDisplayListener(this);
        mCurrentRecognized = new HashMap<>();
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
                if (!mCurrentRecognized.isEmpty()) {
                    for (Map.Entry<Input, Point> entry : mCurrentRecognized.entrySet()) {
                        Point point = entry.getValue();
                        mRecognizer.updateSOM(entry.getKey(), point.x, point.y);
                    }
                    mRecognizer.overrideData();
                    mListener.initSOM();
                    mRecognizer = new Recognizer(mListener.getGlobalSOM(), mListener.getMapNames());
                    Toast.makeText(mActivity, "Update successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mActivity, "Nothing to update", Toast.LENGTH_SHORT).show();
                }
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
    public void onRecognizeSuccess(ArrayList<FloatingImage> listBitmaps) {
        String text = "";
        if (mCurrentRecognized == null) {
            mCurrentRecognized = new HashMap<>();
        } else {
            mCurrentRecognized.clear();
        }
        for (int i = 0; i < listBitmaps.size(); i++) {
            Bundle bundle = mRecognizer.recognize(listBitmaps.get(i));
            int x = bundle.getInt("cordX"); int y = bundle.getInt("cordY");
            Input input = (Input) bundle.getSerializable("input");
            String result = bundle.getString("result");
            mCurrentRecognized.put(input, new Point(x, y));
            Log.d("Result", "bitmap " + i + " :" + result);
            text += result;
        }
        mTvResult.setText(text);
    }

    private void deleteData() {
        mDrawer.emptyDrawer();
        mTvResult.setText("");
    }
/*
    private void updatePaintWidth() {
        DisplayMetrics metrics = mListener.getScreenResolution();
        int height = metrics.heightPixels;
        int width = metrics.widthPixels;
        if (height >= 480) {
            if (height <= 800) {
                FingerDrawerView.CurrentPaintSize = 5f;
            } else {
                if (height <= 854) {
                    FingerDrawerView.CurrentPaintSize = 6f;
                } else {
                    if (height <= 960) {
                        FingerDrawerView.CurrentPaintSize = 9f;
                    } else {
                        if (height <= 1024) {
                            FingerDrawerView.CurrentPaintSize = 10f;
                        } else {
                            if (height <= 1280) {
                                switch (width) {
                                    case 800:
                                        FingerDrawerView.CurrentPaintSize = 13f;
                                        break;
                                    case 768:
                                        FingerDrawerView.CurrentPaintSize = 12f;
                                        break;
                                    default:
                                        FingerDrawerView.CurrentPaintSize = 11f;
                                        break;
                                }
                            } else {
                                if (height <= 1400) {
                                    FingerDrawerView.CurrentPaintSize = 15f;
                                } else {
                                    if (height <= 1824) {
                                        FingerDrawerView.CurrentPaintSize = 17f;
                                    } else {
                                        if (height <= 1920) {
                                            FingerDrawerView.CurrentPaintSize = (width <= 1200) ? 19f : 20f;
                                        } else {
                                            FingerDrawerView.CurrentPaintSize = 25f;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }*/
}
