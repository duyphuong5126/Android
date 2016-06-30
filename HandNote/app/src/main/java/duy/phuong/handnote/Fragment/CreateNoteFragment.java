package duy.phuong.handnote.Fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import duy.phuong.handnote.DAO.LocalStorage;
import duy.phuong.handnote.DTO.Character;
import duy.phuong.handnote.DTO.Line;
import duy.phuong.handnote.Listener.BackPressListener;
import duy.phuong.handnote.MainActivity;
import duy.phuong.handnote.MyView.ColorPicker.ColorPicker;
import duy.phuong.handnote.MyView.DrawingView.FingerDrawerView;
import duy.phuong.handnote.R;
import duy.phuong.handnote.Recognizer.BitmapProcessor;
import duy.phuong.handnote.Recognizer.ImageToText;
import duy.phuong.handnote.Recognizer.MachineLearning.Input;
import duy.phuong.handnote.Recognizer.Recognizer;
import duy.phuong.handnote.Support.SupportUtils;

/**
 * Created by Phuong on 06/03/2016.
 */
public class CreateNoteFragment extends BaseFragment implements BackPressListener, View.OnClickListener, BitmapProcessor.DetectCharactersCallback,
        ColorPicker.OnColorChangedListener {
    private FingerDrawerView mDrawer;
    private TextView mTvResult;
    private LinearLayout mLayoutProgress;

    private ColorPicker mColorPicker;
    private AlertDialog mDialogChangeColor;

    private Recognizer mRecognizer;

    private HashMap<Input, Point> mCurrentRecognized;

    private LocalStorage mLocalStorage;

    public CreateNoteFragment() {
        mLayoutRes = R.layout.fragment_create_note;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity.getClass() == MainActivity.class) {
            mListener = (MainActivity) activity;
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageButton mButtonSave = (ImageButton) mFragmentView.findViewById(R.id.buttonSave);
        mButtonSave.setOnClickListener(this);
        ImageButton mButtonColor = (ImageButton) mFragmentView.findViewById(R.id.buttonColor);
        mButtonColor.setOnClickListener(this);
        ImageButton mButtonDelete = (ImageButton) mFragmentView.findViewById(R.id.buttonDelete);
        mButtonDelete.setOnClickListener(this);
        ImageButton mButtonUndo = (ImageButton) mFragmentView.findViewById(R.id.buttonUndo);
        mButtonUndo.setOnClickListener(this);
        ImageButton mButtonRedo = (ImageButton) mFragmentView.findViewById(R.id.buttonRedo);
        mButtonRedo.setOnClickListener(this);
        mTvResult = (TextView) mFragmentView.findViewById(R.id.tvResult);
        CheckBox mCheckSplit = (CheckBox) mFragmentView.findViewById(R.id.ckcSplit);
        mCheckSplit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mDrawer.setSplit();
            }
        });
        mLayoutProgress = (LinearLayout) mFragmentView.findViewById(R.id.viewProgress);

        mLocalStorage = new LocalStorage(mActivity);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mDrawer = (FingerDrawerView) mFragmentView.findViewById(R.id.FingerDrawer);
        mDrawer.setListener(this);
        mDrawer.setDisplayListener(this);
        mDrawer.setDefault();
        mCurrentRecognized = new HashMap<>();
        mRecognizer = new Recognizer(mListener.getGlobalSOM(), mListener.getMapNames());
        initColorPicker();
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

                    String imagePath = SupportUtils.saveImageWithPath(mDrawer.getContent(), "Image", "image", ".png");
                    String contentPath = SupportUtils.writeFileWithPath(mTvResult.getText().toString(), "Note", "content " + System.nanoTime() +
                            ".txt");
                    mLocalStorage.insertNote(imagePath, contentPath);

                    Toast.makeText(mActivity, "Update successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mActivity, "Nothing to update", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.buttonDelete:
                deleteData();
                break;
            case R.id.buttonColor:
                mDialogChangeColor.show();
                break;
            case R.id.buttonUndo:
                mDrawer.undo(new FingerDrawerView.UndoRedoCallback() {
                    @Override
                    public void canUndoRedo(boolean possibility) {
                        Toast.makeText(mActivity, "Undo is aborted because the latest action isn't completed yet", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void emptyStack() {
                        mTvResult.setText("");
                    }
                });
                break;
            case R.id.buttonRedo:
                mDrawer.redo(new FingerDrawerView.UndoRedoCallback() {
                    @Override
                    public void canUndoRedo(boolean possibility) {
                        Toast.makeText(mActivity, "Redo is aborted because the latest action isn't completed yet", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void emptyStack() {
                        Toast.makeText(mActivity, "Nothing to redo", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case R.id.buttonColorBlack:
            case R.id.buttonColorRed:
            case R.id.buttonColorGreen:
            case R.id.buttonColorBlue:
            case R.id.buttonColorYellow:
            case R.id.buttonColorGray:
            case R.id.buttonColorPink:
            case R.id.buttonColorLime:
            case R.id.buttonColorBlueSky:
            case R.id.buttonColorGold:
                Button button = (Button) v;
                mColorPicker.setCenterColor(Color.parseColor(button.getText().toString()));
                break;
            default:
                break;
        }
    }

    @Override
    public void onBeginDetect(Bundle bundle) {
        mLayoutProgress.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDetectSuccess(final ArrayList<Character> listCharacters) {
        if (mCurrentRecognized == null) {
            mCurrentRecognized = new HashMap<>();
        } else {
            mCurrentRecognized.clear();
        }
        if (listCharacters.size() > 0) {
            Toast.makeText(mActivity, "Characters count: " + listCharacters.size(), Toast.LENGTH_SHORT).show();

            final ArrayList<Line> currentLines = mDrawer.getLines();
            Log.d("List char", "" + listCharacters.size());

            mLayoutProgress.setVisibility(View.VISIBLE);
            ImageToText imageToText = new ImageToText(mListener.getGlobalSOM(), mListener.getMapNames());
            imageToText.imageToText(currentLines, listCharacters, new ImageToText.ConvertingCompleteCallback() {
                @Override
                public void convertingComplete(String result, HashMap<Input, Point> map) {
                    mCurrentRecognized.putAll(map);
                    mTvResult.setText(result);
                    mLayoutProgress.setVisibility(View.GONE);
                }
            });
        } else {
            mLayoutProgress.setVisibility(View.GONE);
        }
    }

    private void deleteData() {
        mDrawer.emptyDrawer();
        mTvResult.setText("");
    }

    private void initColorPicker() {
        if (mColorPicker == null) {
            mColorPicker = new ColorPicker(mActivity, this, 0xffffff);
            View view = LayoutInflater.from(mActivity).inflate(R.layout.color_picker_layout, null);
            view.findViewById(R.id.buttonColorBlack).setOnClickListener(this);
            view.findViewById(R.id.buttonColorRed).setOnClickListener(this);
            view.findViewById(R.id.buttonColorGreen).setOnClickListener(this);
            view.findViewById(R.id.buttonColorBlue).setOnClickListener(this);
            view.findViewById(R.id.buttonColorYellow).setOnClickListener(this);
            view.findViewById(R.id.buttonColorGray).setOnClickListener(this);
            view.findViewById(R.id.buttonColorPink).setOnClickListener(this);
            view.findViewById(R.id.buttonColorLime).setOnClickListener(this);
            view.findViewById(R.id.buttonColorBlueSky).setOnClickListener(this);
            view.findViewById(R.id.buttonColorGold).setOnClickListener(this);
            ((LinearLayout) view.findViewById(R.id.colorPickerLayout)).addView(mColorPicker.getColorPickerView());
            AlertDialog.Builder mDialogBuilderChangeColor = new AlertDialog.Builder(mActivity);
            mDialogBuilderChangeColor.setView(view);
            mDialogBuilderChangeColor.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            mDialogChangeColor = mDialogBuilderChangeColor.create();
        }
    }

    @Override
    public void colorChanged(int color) {
        Toast.makeText(mActivity, "Color's changed", Toast.LENGTH_SHORT).show();
        mDialogChangeColor.cancel();
        mDrawer.setPaintColor(color);
    }

    @Override
    public void onPause() {
        super.onPause();
        mDrawer.setPaintColor(Color.BLACK);
    }
}
