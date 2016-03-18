package duy.phuong.handnote.Fragment;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.StringTokenizer;

import duy.phuong.handnote.Listener.LearningListener;
import duy.phuong.handnote.R;
import duy.phuong.handnote.DTO.StandardImage;
import duy.phuong.handnote.RecognitionAPI.BitmapProcessor;
import duy.phuong.handnote.RecognitionAPI.MachineLearning.PatternLearning;
import duy.phuong.handnote.Support.SupportUtils;

/**
 * Created by Phuong on 28/02/2016.
 */
public class LearningFragment extends BaseFragment implements View.OnClickListener {
    private ListView mListImages;
    private ArrayList<String> mListResourcePaths;
    private ArrayAdapter<String> mListFilesAdapter;
    private ScrollView mScrollProgress;

    private LinearLayout mLayoutProgressing, mLayoutProgressInfo;

    private ImageButton mButtonResize, mButtonTrain;

    private int mCurrentImage;
    private Dialog mDialog;

    private boolean mShowErrorLogs;

    public LearningFragment() {
        mLayoutRes = R.layout.fragment_learning;
        mListResourcePaths = new ArrayList<>();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListImages = (ListView) mFragmentView.findViewById(R.id.listFiles);
        mListFilesAdapter = new ArrayAdapter<>(mActivity, R.layout.item_text, mListResourcePaths);
        mListImages.setAdapter(mListFilesAdapter);
        mButtonResize = (ImageButton) mFragmentView.findViewById(R.id.buttonResize);
        mButtonResize.setOnClickListener(this);
        mLayoutProgressing = (LinearLayout) mFragmentView.findViewById(R.id.layoutProcessing);
        mLayoutProgressing.setOnClickListener(this);
        mLayoutProgressInfo = (LinearLayout) mFragmentView.findViewById(R.id.layoutInfo);
        mButtonTrain = (ImageButton) mFragmentView.findViewById(R.id.buttonTrain);
        mButtonTrain.setOnClickListener(this);
        mScrollProgress = (ScrollView) mFragmentView.findViewById(R.id.scrProgress);
        mDialog = new Dialog(mActivity);
        mDialog.setContentView(R.layout.layout_prompt);
        ((CheckBox) mDialog.findViewById(R.id.checkLearningMethod)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mShowErrorLogs = isChecked;
            }
        });
        mDialog.setTitle("Training information");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListResourcePaths.addAll(SupportUtils.getListFilePaths("mnt/sdcard/Download"));
        mCurrentImage = 0;
        switchMode();
    }

    private void switchMode() {
        ArrayList<String> trainPaths = new ArrayList<>();
        trainPaths.addAll(SupportUtils.getListFilePaths(SupportUtils.RootPath + SupportUtils.ApplicationDirectory + "Train"));

        if (!trainPaths.isEmpty()) {
            mButtonResize.setVisibility(View.GONE);
            mButtonTrain.setVisibility(View.VISIBLE);

            mListResourcePaths.clear();
            mListResourcePaths.addAll(trainPaths);
        } else {
            mButtonResize.setVisibility(View.VISIBLE);
            mButtonTrain.setVisibility(View.GONE);
        }
        mListFilesAdapter.notifyDataSetChanged();
    }

    @Override
    public String fragmentIdentify() {
        return BaseFragment.LEARNING_FRAGMENT;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonResize:
                if (SupportUtils.emptyDirectory(SupportUtils.RootPath + SupportUtils.ApplicationDirectory + "Train")) {
                    Log.e("Error", "Directory not exist");
                }

                Toast.makeText(mActivity, "Resize images begin!", Toast.LENGTH_SHORT).show();
                mCurrentImage = mListResourcePaths.size();
                for (final String path : mListResourcePaths) {
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            Bitmap bitmap = BitmapProcessor.resizeBitmap(BitmapFactory.decodeFile(path), 20, 28);
                            String name = "";
                            StringTokenizer tokenizer = new StringTokenizer(path, "/");
                            while (tokenizer.hasMoreTokens()) {
                                name = tokenizer.nextToken();
                            }
                            if (!SupportUtils.saveImage(bitmap, "Train", name, ".png")) {
                                Toast.makeText(mActivity, "Save images error", Toast.LENGTH_SHORT).show();
                            }
                            mCurrentImage--;
                            if (mCurrentImage <= 0) {
                                mCurrentImage = 0;
                                Toast.makeText(mActivity, "Resize images done!", Toast.LENGTH_SHORT).show();
                                switchMode();
                            }
                        }
                    };
                    runnable.run();
                }
                break;

            case R.id.buttonTrain:
                final ArrayList<StandardImage> standardImages = new ArrayList<>();
                mCurrentImage = mListResourcePaths.size();
                Toast.makeText(mActivity, "Loading resources, please wait!", Toast.LENGTH_LONG).show();
                final Handler handler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        if (mCurrentImage <= 0) {
                            mCurrentImage = 0;
                            final EditText editText = (EditText) mDialog.findViewById(R.id.edtNumberOfIterations);
                            Button buttonOK = (Button) mDialog.findViewById(R.id.buttonOK);
                            buttonOK.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String data = editText.getText().toString();
                                    int number_of_iterations = (("".equals(data)) ? 0 : Integer.valueOf(data));
                                    mDialog.cancel();

                                    if (number_of_iterations <= 0) {
                                        Toast.makeText(mActivity, "Can't start the training", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(mActivity, "Training begin", Toast.LENGTH_LONG).show();
                                        if (mShowErrorLogs) {
                                            learningWithLog(standardImages, number_of_iterations);
                                        } else {
                                            learningWithNoLog(standardImages, number_of_iterations);
                                        }
                                    }
                                }
                            });
                            Button buttonCancel = (Button) mDialog.findViewById(R.id.buttonCancel);
                            buttonCancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    editText.setText("");
                                    mDialog.dismiss();
                                }
                            });
                            mDialog.show();
                        }
                    }
                };
                for (final String path : mListResourcePaths) {
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            String name = getNameFromPath(path);
                            String alphabet = name.substring(6, 7);
                            standardImages.add(new StandardImage(BitmapFactory.decodeFile(path), alphabet));
                            mCurrentImage--;
                            handler.sendMessage(handler.obtainMessage());
                        }
                    };
                    runnable.run();
                }
                break;

            case R.id.layoutProcessing:
                break;

            default:
                break;
        }
    }

    private void learningWithNoLog(ArrayList<StandardImage> standardImages, int number_of_iterations) {
        PatternLearning patternLearning = new PatternLearning(standardImages, number_of_iterations);
        patternLearning.learn();
    }

    private void learningWithLog(ArrayList<StandardImage> standardImages, int number_of_iterations) {
        mLayoutProgressing.setVisibility(View.VISIBLE);
        final LayoutInflater inflater = mActivity.getLayoutInflater();
        PatternLearning patternLearning = new PatternLearning(standardImages, number_of_iterations);
        patternLearning.learn(new LearningListener() {
            @Override
            public void updateEpoch(Bundle bundle) {
                if (bundle != null) {
                    View epochView = inflater.inflate(R.layout.item_epoch, null);
                    TextView tvEpoch = (TextView) epochView.findViewById(R.id.tvEpoch);
                    tvEpoch.setText("Epoch: " + bundle.getInt("Epoch"));
                    mLayoutProgressInfo.addView(epochView);

                    String info = bundle.getString("ListName");
                    if (info != null && !"".equals(info)) {
                        View infoView = inflater.inflate(R.layout.item_infor_text, null);
                        TextView tvInfo = (TextView) infoView.findViewById(R.id.tvInfoText);
                        tvInfo.setText(info);
                        mLayoutProgressInfo.addView(infoView);
                    }

                    mScrollProgress.post(new Runnable() {
                        @Override
                        public void run() {
                            mScrollProgress.fullScroll(View.FOCUS_DOWN);
                        }
                    });
                }
            }

            @Override
            public void finish() {
                Toast.makeText(mActivity, "Training done", Toast.LENGTH_LONG).show();
                mLayoutProgressing.setVisibility(View.GONE);
            }
        });
    }

    private String getNameFromPath(String path) {
        String result = "";
        StringTokenizer tokenizer = new StringTokenizer(path, "/");
        while (tokenizer.hasMoreTokens()) {
            result = tokenizer.nextToken();
        }
        return result;
    }
}
