package duy.phuong.handnote.Fragment;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import duy.phuong.handnote.DAO.LocalStorage;
import duy.phuong.handnote.DTO.Character;
import duy.phuong.handnote.DTO.Line;
import duy.phuong.handnote.Listener.BackPressListener;
import duy.phuong.handnote.MyView.DrawingView.FingerDrawerView;
import duy.phuong.handnote.R;
import duy.phuong.handnote.Recognizer.BitmapProcessor;
import duy.phuong.handnote.Recognizer.ImageToText;
import duy.phuong.handnote.Recognizer.MachineLearning.Input;
import duy.phuong.handnote.Support.SharedPreferenceUtils;

/**
 * Created by Phuong on 25/05/2016.
 */
public class TranslateFragment extends BaseFragment implements BackPressListener, BitmapProcessor.DetectCharactersCallback, View.OnClickListener {
    private FingerDrawerView mDrawer;
    private LocalStorage mStorage;
    private TextView mTvDefinition;
    private LinearLayout mLayoutProcess;

    private AlertDialog mDialog;

    private LinearLayout mLayoutInstalling;
    private ProgressBar mProgress;
    private TextView mTextProgress;
    private int mProgressNumber, mMax;

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
        mLayoutProcess = (LinearLayout) mFragmentView.findViewById(R.id.layoutProcessing);

        mLayoutInstalling = (LinearLayout) mFragmentView.findViewById(R.id.layoutInstalling);
        mProgress = (ProgressBar) mFragmentView.findViewById(R.id.Progress);
        mTextProgress = (TextView) mFragmentView.findViewById(R.id.textProgress);

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mActivity);
        mBuilder.setTitle("You haven't installed dictionary yet. Install it now?").setNegativeButton("Skip", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(mActivity, "You couldn't use translate feature before installed dictionary", Toast.LENGTH_LONG).show();
                mLayoutProcess.setVisibility(View.GONE);
                mActivity.onBackPressed();
            }
        }).setPositiveButton("Install", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                initDict();
            }
        });
        mDialog = mBuilder.create();
        if (!SharedPreferenceUtils.isLoadedDict()) {
            if (mDialog != null) {
                mDialog.show();
            }
        } else {
            Toast.makeText(mActivity, "This version only support English - Vietnamese for offline translation", Toast.LENGTH_LONG).show();
        }
    }


    private void initDict() {
        final LocalStorage localStorage = new LocalStorage(mActivity);
        final SQLiteDatabase db = localStorage.getWritableDatabase();
        final ContentValues contentValues = new ContentValues();
        Resources resources = getResources();
        final InputStream inputStream = resources.openRawResource(R.raw.eng_vi);
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        localStorage.deleteAllDict(db);
        AsyncTask<Void, Integer, Void> mEVTask = new AsyncTask<Void, Integer, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mLayoutProcess.setVisibility(View.VISIBLE);
                if (mDialog != null) {
                    if (mDialog.isShowing()) {
                        mDialog.cancel();
                    }
                }
                try {
                    mMax = inputStream.available();
                    mProgress.setMax(mMax);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mLayoutInstalling.setVisibility(View.VISIBLE);
                mProgressNumber = 0;
                Toast.makeText(mActivity, "Installation begin...", Toast.LENGTH_LONG).show();
            }

            @Override
            protected Void doInBackground(Void... params) {
                String line;
                try {
                    while ((line = reader.readLine()) != null) {
                        StringTokenizer tokenizer = new StringTokenizer(line, "#");
                        if (tokenizer.countTokens() == 2) {
                            String word = tokenizer.nextToken();
                            String definition = tokenizer.nextToken();
                            Log.d("Infor", "w: " + word + ", def: " + definition);
                            Log.d("Insert result", String.valueOf(localStorage.inertEV_DictLine(word, null, definition, db, contentValues)));
                        } else {
                            if (tokenizer.countTokens() == 3) {
                                String word = tokenizer.nextToken();
                                String pronunciation = tokenizer.nextToken();
                                String definition = tokenizer.nextToken();
                                Log.d("Infor", "w: " + word + ", pro: " + pronunciation + ", def: " + definition);
                                Log.d("Insert result", String.valueOf(localStorage.inertEV_DictLine(word, pronunciation, definition, db, contentValues)));
                            }
                        }
                        publishProgress(line.getBytes().length);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
                mProgressNumber += values[0];
                mTextProgress.setText("Installing " + Math.round((((double) mProgressNumber) / mMax) * 100) + "%, please wait...");
                mProgress.setProgress(mProgressNumber);
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                mLayoutInstalling.setVisibility(View.GONE);
                mLayoutProcess.setVisibility(View.GONE);
                SharedPreferenceUtils.loadedDict(true);
            }
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mEVTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            mEVTask.execute();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
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
    public void onBeginDetect(Bundle bundle) {
        mLayoutProcess.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDetectSuccess(final ArrayList<Character> listCharacters) {
        if (listCharacters.size() > 0) {
            final ArrayList<Line> currentLines = mDrawer.getLines();
            mLayoutProcess.setVisibility(View.VISIBLE);
            ImageToText imageToText = new ImageToText(mListener.getGlobalSOM(), mListener.getMapNames());
            imageToText.imageToText(currentLines, listCharacters, new ImageToText.ConvertingCompleteCallback() {
                @Override
                public void convertingComplete(String result, HashMap<Input, Point> map) {
                    String def = mStorage.findEV_Definition(result.toLowerCase().replace(" ", ""));
                    String p = result.toLowerCase() + " " + def;
                    if (def.length() > 0) {
                        p = p.replace("* ", "\n\t");
                        p = p.replace("|-", ": ");
                        p = p.replace("|=", "\n\t\t");
                        p = p.replace("|+", ": (dẫn xuất) ");
                        p = p.replace("|", "");
                        mTvDefinition.setText(p);
                    } else {
                        mTvDefinition.setText("Can not find definition for '" + result + "'");
                    }
                    mLayoutProcess.setVisibility(View.GONE);
                }
            });
        } else {
            mLayoutProcess.setVisibility(View.GONE);
        }
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
                mDrawer.undo(new FingerDrawerView.UndoRedoCallback() {
                    @Override
                    public void canUndoRedo(boolean possibility) {
                        Toast.makeText(mActivity, "Redo is aborted because the latest action isn't completed yet", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void emptyStack() {
                        mTvDefinition.setText("");
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
            default:
                break;
        }
    }
}
