package duy.phuong.handnote.Fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.StringTokenizer;

import duy.phuong.handnote.R;
import duy.phuong.handnote.DTO.TrainingImage;
import duy.phuong.handnote.RecognitionAPI.MachineLearning.PatternLearning;
import duy.phuong.handnote.Support.SupportUtils;

/**
 * Created by Phuong on 28/02/2016.
 */
public class LearningFragment extends BaseFragment implements View.OnClickListener {
    private ListView mListImages;
    private ArrayList<String> mListResourcePaths;
    private ArrayAdapter<String> mListFilesAdapter;

    private LinearLayout mLayoutProcessing;

    private ImageButton mButtonResize, mButtonTrain;

    private int mCurrentImage;

    public LearningFragment() {
        mLayoutRes = R.layout.fragment_learning;
        mListResourcePaths = new ArrayList<>();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListImages = (ListView) mFragmentView.findViewById(R.id.listFiles);
        mListFilesAdapter = new ArrayAdapter<>(mActivity, android.R.layout.simple_list_item_1, mListResourcePaths);
        mListImages.setAdapter(mListFilesAdapter);
        mButtonResize = (ImageButton) mFragmentView.findViewById(R.id.buttonResize);
        mButtonResize.setOnClickListener(this);
        mLayoutProcessing = (LinearLayout) mFragmentView.findViewById(R.id.layoutProcessing);
        mButtonTrain = (ImageButton) mFragmentView.findViewById(R.id.buttonTrain);
        mButtonTrain.setOnClickListener(this);
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

    private Bitmap resizeBitmap(Bitmap bitmap, int newWidth, int newHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonResize:
                if (SupportUtils.emptyDirectory(SupportUtils.RootPath + SupportUtils.ApplicationDirectory + "Train")) {
                    Log.d("Error", "Directory not exist");
                }

                Toast.makeText(mActivity, "Resize images begin!", Toast.LENGTH_SHORT).show();
                mCurrentImage = mListResourcePaths.size();
                for (final String path : mListResourcePaths) {
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            Bitmap bitmap = resizeBitmap(BitmapFactory.decodeFile(path), 20, 28);
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
                final ArrayList<TrainingImage> trainingImages = new ArrayList<>();
                mCurrentImage = mListResourcePaths.size();
                for (final String path : mListResourcePaths) {
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            String name = getNameFromPath(path);
                            String alphabet = name.substring(6, 7);
                            trainingImages.add(new TrainingImage(BitmapFactory.decodeFile(path), alphabet));

                            mCurrentImage--;
                            if (mCurrentImage <= 0) {
                                mCurrentImage = 0;
                                final Dialog dialog = new Dialog(mActivity);
                                dialog.setContentView(R.layout.layout_prompt);
                                final EditText editText = (EditText) dialog.findViewById(R.id.edtNumberOfIterations);
                                Button buttonOK = (Button) dialog.findViewById(R.id.buttonOK);
                                buttonOK.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String data = editText.getText().toString();
                                        int number_of_iterations = (("".equals(data)) ? 0 : Integer.valueOf(data));

                                        if (number_of_iterations <= 0) {
                                            Toast.makeText(mActivity, "Can't start the training", Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(mActivity, "Training begin", Toast.LENGTH_LONG).show();
                                            dialog.dismiss();
                                            PatternLearning patternLearning = new PatternLearning(trainingImages, number_of_iterations);
                                            patternLearning.learn();
                                            Toast.makeText(mActivity, "Training done", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                                Button buttonCancel = (Button) dialog.findViewById(R.id.buttonCancel);
                                buttonCancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        editText.setText("");
                                        dialog.dismiss();
                                    }
                                });
                                dialog.show();
                            }
                        }
                    };
                    runnable.run();
                }
                break;

            default:
                break;
        }
    }

    private boolean checkName(String name) {
        return (name.charAt(6) >= 65 || name.charAt(0) <= 90);
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
