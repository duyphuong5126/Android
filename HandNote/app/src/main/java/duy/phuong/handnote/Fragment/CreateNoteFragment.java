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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import duy.phuong.handnote.DTO.Character;
import duy.phuong.handnote.DTO.Line;
import duy.phuong.handnote.DTO.Word;
import duy.phuong.handnote.Listener.BackPressListener;
import duy.phuong.handnote.MainActivity;
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
        if (activity.getClass() == MainActivity.class) {
            mListener = (MainActivity) activity;
        }
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
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mDrawer = (FingerDrawerView) mFragmentView.findViewById(R.id.FingerDrawer);
        mDrawer.setListener(this);
        mDrawer.setDisplayListener(this);
        mCurrentRecognized = new HashMap<>();
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
    public void onRecognizeSuccess(ArrayList<Character> listCharacters) {
        if (mCurrentRecognized == null) {
            mCurrentRecognized = new HashMap<>();
        } else {
            mCurrentRecognized.clear();
        }

        ArrayList<Line> lines = mDrawer.getLines();

        if (!lines.isEmpty()) {
            for (Line line : lines) {
                line.mCharacters = new ArrayList<>();
                for (Character character : listCharacters) {
                    if (!character.isSorted) {
                        if (character.mRect.top >= line.mTop && character.mRect.top <= line.mBottom) {
                            line.mCharacters.add(character);
                            character.isSorted = true;
                        }
                    }
                }
            }

            String paragraph = "";
            for (Line line : lines) {
                if (!line.mCharacters.isEmpty()) {
                    boolean end = false;
                    while (!end) {
                        boolean swapped = false;
                        for (int i = 1; i < line.mCharacters.size(); i++) {
                            Character c1 = line.mCharacters.get(i), cp = line.mCharacters.get(i - 1);
                            if (c1.mRect.left < cp.mRect.left) {
                                Collections.swap(line.mCharacters, i, i - 1);
                                swapped = true;
                            }
                        }

                        if (!swapped) {
                            end = true;
                        }
                    }
                    String text = "";
                    int h = Math.abs(line.mBottom - line.mTop);
                    for (int i = 0; i < line.mCharacters.size(); i++) {
                        Character c = line.mCharacters.get(i);
                        Bundle bundle = mRecognizer.recognize(c);
                        int x = bundle.getInt("cordX");
                        int y = bundle.getInt("cordY");
                        Input input = (Input) bundle.getSerializable("input");
                        String result = bundle.getString("result");
                        mCurrentRecognized.put(input, new Point(x, y));
                        Log.d("Result", "bitmap " + i + " :" + result);
                        String character = "";
                        switch (result) {
                            case "C":
                            case "O":
                            case "P":
                            case "S":
                            case "V":
                            case "W":
                            case "X":
                            case "Z":
                                if (c.mRect.height() <= 0.65d * h) {
                                    character = result.toLowerCase();
                                } else {
                                    character = result;
                                }
                                break;

                            default:
                                character = result;
                                break;
                        }
                        text += character;
                    }
                    paragraph += text + " ";
                }
            }
            mTvResult.setText(paragraph);
        }
    }

    private void deleteData() {
        mDrawer.emptyDrawer();
        mTvResult.setText("");
    }
}
