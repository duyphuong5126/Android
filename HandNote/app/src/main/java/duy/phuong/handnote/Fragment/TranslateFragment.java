package duy.phuong.handnote.Fragment;

import android.graphics.Color;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import duy.phuong.handnote.DAO.LocalStorage;
import duy.phuong.handnote.DTO.Character;
import duy.phuong.handnote.DTO.Line;
import duy.phuong.handnote.Listener.BackPressListener;
import duy.phuong.handnote.MyView.DrawingView.FingerDrawerView;
import duy.phuong.handnote.R;
import duy.phuong.handnote.Recognizer.BitmapProcessor;
import duy.phuong.handnote.Recognizer.ImageToText;
import duy.phuong.handnote.Recognizer.MachineLearning.Input;

/**
 * Created by Phuong on 25/05/2016.
 */
public class TranslateFragment extends BaseFragment implements BackPressListener, BitmapProcessor.RecognitionCallback, View.OnClickListener{
    private FingerDrawerView mDrawer;
    private LocalStorage mStorage;
    private TextView mTvDefinition;
    public TranslateFragment() {
        mLayoutRes = R.layout.fragment_dictionary;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mDrawer = (FingerDrawerView) mFragmentView.findViewById(R.id.inputSurface);
        mDrawer.setListener(this);
        mDrawer.setDisplayListener(this);
        ImageButton buttonDelete = (ImageButton) mFragmentView.findViewById(R.id.buttonDelete);
        buttonDelete.setOnClickListener(this);
        ImageButton buttonUndo = (ImageButton) mFragmentView.findViewById(R.id.buttonUndo);
        buttonUndo.setOnClickListener(this);
        ImageButton buttonRedo = (ImageButton) mFragmentView.findViewById(R.id.buttonRedo);
        buttonRedo.setOnClickListener(this);
        mStorage = new LocalStorage(mActivity);
        mTvDefinition = (TextView) mFragmentView.findViewById(R.id.textDefinition);
        Toast.makeText(mActivity, "This version only support English - Vietnamese for offline translation", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStart() {
        super.onStart();
        mDrawer.setPaintColor(Color.BLACK);
    }

    @Override
    public String fragmentIdentify() {
        return TRANSLATE_FRAGMENT;
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
    public void onRecognizeSuccess(final ArrayList<Character> listCharacters) {
        final ArrayList<Line> currentLines = mDrawer.getLines();
        final String[] paragraph = {""};
        Log.d("List char", "" + listCharacters.size());

        AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... params) {
                ImageToText imageToText = new ImageToText(mListener.getGlobalSOM(), mListener.getMapNames());
                imageToText.imageToText(currentLines, listCharacters, new ImageToText.ConvertingCompleteCallback() {
                    @Override
                    public void convertingComplete(String result, HashMap<Input, Point> map) {
                        paragraph[0] = result;
                    }
                });
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                String def = mStorage.findEV_Definition(paragraph[0].toLowerCase().replace(" ", ""));
                String p = paragraph[0].toLowerCase() + " " + def;
                if (def.length() > 0) {
                    p = p.replace("* ", "\n\t");
                    p = p.replace("|-", ": ");
                    p = p.replace("|=", "\n\t\t");
                    p = p.replace("|+", ": (dẫn xuất) ");
                    p = p.replace("|", "");
                    Log.d("Text", p);
                    mTvDefinition.setText(p);
                } else {
                    mTvDefinition.setText("Can not find definition for '" + paragraph[0] + "'");
                }
            }
        };
        asyncTask.execute();
    }


    private void deleteData() {
        if (!mTvDefinition.getText().toString().equals("Definition")) {
            mTvDefinition.setText("Definition");
        }
        mDrawer.emptyDrawer();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonDelete:
                deleteData();
                break;
            case R.id.buttonUndo:
                mDrawer.undo();
                break;
            case R.id.buttonRedo:
                mDrawer.redo();
                break;
            default:
                break;
        }
    }
}
