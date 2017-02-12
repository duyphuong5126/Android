package com.horical.appnote.Fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.horical.appnote.DTO.FileDTO.FileData;
import com.horical.appnote.LocalStorage.DataConstant;
import com.horical.appnote.Interfaces.OpenMediaInterface;
import com.horical.appnote.MyView.MyAdapter.ListFileAdapter;
import com.horical.appnote.MyView.MyDialog.MediaChooserDialog;
import com.horical.appnote.R;
import com.horical.appnote.Supports.LanguageUtils;
import com.horical.appnote.Supports.MediaUtils;
import com.horical.appnote.Supports.SupportUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Phuong on 24/09/2015.
 */
public class FilesManagerFragment extends BaseFragment implements MediaChooserDialog.MediaCallback, AdapterView.OnItemClickListener,
        RadioGroup.OnCheckedChangeListener, OpenMediaInterface, ImageButton.OnClickListener {
    private ArrayList<FileData> mListFile;
    private ListFileAdapter mFileAdapter;
    private ListView mLvImages, mLvVideos, mLvAudios;

    private ArrayList<String> mListImageAlphabet, mListVideoAlphabet, mListAudioAlphabet;
    private ArrayAdapter<String> mAlphabetAdapter;
    private ListView mLvImageAlphabets, mLvVideoAlphabets, mLvAudioAlphabets;

    private TextView mTvEmptyView;
    private LinearLayout mLayoutProgress;

    private RadioGroup mTabsControl;
    private RadioButton mTabImage, mTabVideo, mTabAudio;

    private boolean mShowSearchBar;
    private LinearLayout mSearchBar;
    private EditText mEdtSearch;
    private LinearLayout mLayoutSearchHolder;

    private HashMap<String, Bitmap> mListBitmap;

    private MediaChooserDialog mMediaChooser;

    private LinearLayout mLayoutImageView;
    private ImageView mIvSelectedImage;

    private static int mCurrentSelectedImage;

    private Handler mSlideShowHandlerMessage;
    private Runnable mSlideShowThread;
    private boolean mSlideShowRunning;

    private LinearLayout mLayoutTabsControl;

    public FilesManagerFragment() {
        this.mLayout_xml_id = R.layout.fragment_files_manager;
    }

    private String mFlag = "";

    private boolean isNewImageList = true, isNewVideoList = true, isNewAudioList = true;
    private boolean isSearchImage = false, isSearchVideo = false, isSearchAudio = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mListFile = new ArrayList<>();

        mListImageAlphabet = new ArrayList<>();
        mListVideoAlphabet = new ArrayList<>();
        mListAudioAlphabet = new ArrayList<>();

        mListBitmap = new HashMap<>();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mLayoutProgress = (LinearLayout) mFragmentView.findViewById(R.id.layoutProgress);

        mLayoutTabsControl = (LinearLayout) mFragmentView.findViewById(R.id.layoutTabsControl);

        mLayoutImageView = (LinearLayout) mFragmentView.findViewById(R.id.layoutViewImage);
        FrameLayout mLayoutSelectedImage = (FrameLayout) mFragmentView.findViewById(R.id.layoutSelectedImage);
        mLayoutSelectedImage.setOnClickListener(this);
        mIvSelectedImage = (ImageView) mFragmentView.findViewById(R.id.selectedImage);
        mIvSelectedImage.setOnClickListener(this);
        ImageButton mBtnNextImage = (ImageButton) mFragmentView.findViewById(R.id.buttonImageNext);
        mBtnNextImage.setOnClickListener(this);
        ImageButton mBtnPreviousImage = (ImageButton) mFragmentView.findViewById(R.id.buttonImageBefore);
        mBtnPreviousImage.setOnClickListener(this);
        ImageButton mBtnSlideShow = (ImageButton) mFragmentView.findViewById(R.id.buttonImageSlideShow);
        mBtnSlideShow.setOnClickListener(this);
        ImageButton mBtnCloseImageViewer = (ImageButton) mFragmentView.findViewById(R.id.buttonCloseImageViewer);
        mBtnCloseImageViewer.setOnClickListener(this);

        mMediaChooser = new MediaChooserDialog(mActivity);
        mMediaChooser.setMediaCallback(this);

        mSearchBar = (LinearLayout) mFragmentView.findViewById(R.id.SearchBar);
        mEdtSearch = (EditText) mFragmentView.findViewById(R.id.edtSearchFile);
        mLayoutSearchHolder = (LinearLayout) mFragmentView.findViewById(R.id.searchHolder);

        mTabsControl = (RadioGroup) mFragmentView.findViewById(R.id.rdTabs);
        mTabsControl.setOnCheckedChangeListener(this);

        mTabImage = (RadioButton) mFragmentView.findViewById(R.id.buttonImage);
        mTabVideo = (RadioButton) mFragmentView.findViewById(R.id.buttonVideo);
        mTabAudio = (RadioButton) mFragmentView.findViewById(R.id.buttonAudio);


        mTvEmptyView = (TextView) mFragmentView.findViewById(R.id.tvEmptyList);


        mLvImages = (ListView) mFragmentView.findViewById(R.id.ListImages);
        mLvVideos = (ListView) mFragmentView.findViewById(R.id.ListVideos);
        mLvAudios = (ListView) mFragmentView.findViewById(R.id.ListAudios);
    }

    @Override
    public void onStart() {
        super.onStart();

        mTvEmptyView.setText(LanguageUtils.getNothingListFileString());

        mTabImage.setText(LanguageUtils.getImageString());
        mTabAudio.setText(LanguageUtils.getAudioString());

        mLvImageAlphabets = (ListView) mFragmentView.findViewById(R.id.ListImageAlphabet);
        mLvVideoAlphabets = (ListView) mFragmentView.findViewById(R.id.ListVideoAlphabet);
        mLvAudioAlphabets = (ListView) mFragmentView.findViewById(R.id.ListAudioAlphabet);

        mAlphabetAdapter = new ArrayAdapter<>(mActivity, R.layout.list_alphabet, mListImageAlphabet);

        mLvImageAlphabets.setAdapter(mAlphabetAdapter);
        mLvImageAlphabets.setOnItemClickListener(this);

        this.reloadListImages();
        isNewImageList = false;

        mEdtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String search_type;
                switch (mTabsControl.getCheckedRadioButtonId()) {
                    case R.id.buttonImage:
                        search_type = DataConstant.TYPE_IMAGE;
                        searchFileByName(editable.toString());
                        mFileAdapter = new ListFileAdapter(mActivity, 0, mListFile, search_type);
                        mFileAdapter.setOpenMediaInterface(FilesManagerFragment.this);
                        mLvImages.setAdapter(mFileAdapter);
                        break;
                    case R.id.buttonVideo:
                        search_type = DataConstant.TYPE_VIDEOCLIP;
                        searchFileByName(editable.toString());
                        mFileAdapter = new ListFileAdapter(mActivity, 0, mListFile, search_type);
                        mFileAdapter.setOpenMediaInterface(FilesManagerFragment.this);
                        mLvVideos.setAdapter(mFileAdapter);
                        break;
                    case R.id.buttonAudio:
                        search_type = DataConstant.TYPE_VOICE;
                        searchFileByName(editable.toString());
                        mFileAdapter = new ListFileAdapter(mActivity, 0, mListFile, search_type);
                        mFileAdapter.setOpenMediaInterface(FilesManagerFragment.this);
                        mLvAudios.setAdapter(mFileAdapter);
                        break;
                }
            }
        });

        mSlideShowHandlerMessage = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                mIvSelectedImage.setImageBitmap(getBitmap(mCurrentSelectedImage));
                mCurrentSelectedImage++;
                if (mCurrentSelectedImage >= mListBitmap.size()) {
                    mCurrentSelectedImage = 0;
                    stopSlideShow();
                }
            }
        };
        mSlideShowThread = new Runnable() {
            @Override
            public void run() {
                if (mSlideShowRunning) {
                    Message message = mSlideShowHandlerMessage.obtainMessage();
                    mSlideShowHandlerMessage.sendMessage(message);
                    mSlideShowHandlerMessage.postDelayed(this, 5000);
                }
            }
        };
    }

    public void reloadListImages() {
        final Handler handler = new Handler();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (!mMainInterface.isReloadingFiles()) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initImageList();
                        }
                    });
                } else {
                    handler.postDelayed(this, 5000);
                }
            }
        });
        thread.start();
    }


    public void reloadListVideos() {
        final Handler handler = new Handler();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (!mMainInterface.isReloadingFiles()) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initVideoList();
                        }
                    });
                } else {
                    handler.postDelayed(this, 5000);
                }
            }
        });
        thread.start();
    }

    public void reloadListAudios() {
        final Handler handler = new Handler();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (!mMainInterface.isReloadingFiles()) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initAudioList();
                        }
                    });
                } else {
                    handler.postDelayed(this, 5000);
                }
            }
        });
        thread.start();
    }

    private void initView(String type, ArrayList<FileData> source) {
        mFileAdapter = new ListFileAdapter(mActivity, 0, source, type);
        mFileAdapter.setOpenMediaInterface(this);

        switch (type) {
            case DataConstant.TYPE_IMAGE:
                mLvImages.setAdapter(mFileAdapter);
                mLvImages.setOnItemClickListener(this);
                break;
            case DataConstant.TYPE_VIDEOCLIP:
                mLvVideos.setAdapter(mFileAdapter);
                mLvVideos.setOnItemClickListener(this);
                break;
            case DataConstant.TYPE_VOICE:
                mLvAudios.setAdapter(mFileAdapter);
                mLvAudios.setOnItemClickListener(this);
                break;
        }
    }

    public void initImageList() {
        mLayoutProgress.setVisibility(View.VISIBLE);

        mListFile.clear();
        mListImageAlphabet.clear();


        final  ArrayList<FileData> Sources = new ArrayList<>();
        this.initView(DataConstant.TYPE_IMAGE, Sources);

        mFlag = "";

        AsyncTask<Void, FileData, Void> asyncTask = new AsyncTask<Void, FileData, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                for (FileData fileData : mMainInterface.getAllFiles(DataConstant.TYPE_IMAGE)) {
                    publishProgress(fileData);
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(FileData... values) {
                super.onProgressUpdate(values);

                mListFile.add(values[0]);
                Sources.add(values[0]);
                mListBitmap.put(values[0].getFilePath(), getSuitableSizeBitmap(values[0].getFilePath()));
                if (SupportUtils.isAlphabet(values[0].getFileName().charAt(0))) {
                    String s = values[0].getFileName().substring(0, 1).toUpperCase();
                    if (!mFlag.contains(s)) {
                        mFlag += s;
                        mListImageAlphabet.add(s);
                    }
                } else {
                    if (!mFlag.contains("#")) {
                        mFlag += "#";
                        mListImageAlphabet.add(0, "#");
                    }
                }
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                mLayoutProgress.setVisibility(View.GONE);
                if (!mListImageAlphabet.isEmpty()) {
                    mTvEmptyView.setVisibility(View.GONE);
                }

                sortListAlphabet();
                mAlphabetAdapter = new ArrayAdapter<>(mActivity, R.layout.list_alphabet, mListImageAlphabet);
                mLvImageAlphabets.setAdapter(mAlphabetAdapter);
                mFileAdapter.notifyDataSetChanged();
            }
        };
        asyncTask.execute();
    }

    public void initVideoList() {
        mLayoutProgress.setVisibility(View.VISIBLE);

        mListFile.clear();
        mListVideoAlphabet.clear();

        final  ArrayList<FileData> Sources = new ArrayList<>();
        this.initView(DataConstant.TYPE_VIDEOCLIP, Sources);

        mFlag = "";

        AsyncTask<Void, FileData, Void> asyncTask = new AsyncTask<Void, FileData, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                for (FileData fileData : mMainInterface.getAllFiles(DataConstant.TYPE_VIDEOCLIP)) {
                    publishProgress(fileData);
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(FileData... values) {
                super.onProgressUpdate(values);
                mListFile.add(values[0]);
                Sources.add(values[0]);
                if (SupportUtils.isAlphabet(values[0].getFileName().charAt(0))) {
                    String s = values[0].getFileName().substring(0, 1).toUpperCase();
                    if (!mFlag.contains(s)) {
                        mFlag += s;
                        mListVideoAlphabet.add(s);
                    }
                } else {
                    if (!mFlag.contains("#")) {
                        mFlag += "#";
                        mListVideoAlphabet.add(0, "#");
                    }
                }
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                mLayoutProgress.setVisibility(View.GONE);
                if (!mListVideoAlphabet.isEmpty()) {
                    mTvEmptyView.setVisibility(View.GONE);
                }

                sortListAlphabet();
                mAlphabetAdapter = new ArrayAdapter<>(mActivity, R.layout.list_alphabet, mListVideoAlphabet);
                mLvVideoAlphabets.setAdapter(mAlphabetAdapter);
                mFileAdapter.notifyDataSetChanged();
            }
        };
        asyncTask.execute();
    }

    public void initAudioList() {
        mLayoutProgress.setVisibility(View.VISIBLE);

        mListFile.clear();
        mListAudioAlphabet.clear();

        final  ArrayList<FileData> Sources = new ArrayList<>();
        this.initView(DataConstant.TYPE_VOICE, Sources);

        mFlag = "";

        AsyncTask<Void, FileData, Void> asyncTask = new AsyncTask<Void, FileData, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                for (FileData fileData : mMainInterface.getAllFiles(DataConstant.TYPE_VOICE)) {
                    publishProgress(fileData);
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(FileData... values) {
                super.onProgressUpdate(values);
                mListFile.add(values[0]);
                Sources.add(values[0]);
                if (SupportUtils.isAlphabet(values[0].getFileName().charAt(0))) {
                    String s = values[0].getFileName().substring(0, 1).toUpperCase();
                    if (!mFlag.contains(s)) {
                        mFlag += s;
                        mListAudioAlphabet.add(s);
                    }
                } else {
                    if (!mFlag.contains("#")) {
                        mFlag += "#";
                        mListAudioAlphabet.add(0, "#");
                    }
                }
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                mLayoutProgress.setVisibility(View.GONE);
                if (!mListAudioAlphabet.isEmpty()) {
                    mTvEmptyView.setVisibility(View.GONE);
                }

                sortListAlphabet();
                mAlphabetAdapter = new ArrayAdapter<>(mActivity, R.layout.list_alphabet, mListAudioAlphabet);
                mLvAudioAlphabets.setAdapter(mAlphabetAdapter);
                mFileAdapter.notifyDataSetChanged();
            }
        };
        asyncTask.execute();
    }

    public void sortListAlphabet() {
        for (int i = 0; i < mListImageAlphabet.size() - 1; i++)
            for (int j = i + 1; j < mListImageAlphabet.size(); j++) {
                if (mListImageAlphabet.get(i).charAt(0) > mListImageAlphabet.get(j).charAt(0)) {
                    Collections.swap(mListImageAlphabet, i, j);
                }
            }
        for (int i = 0; i < mListVideoAlphabet.size() - 1; i++)
            for (int j = i + 1; j < mListVideoAlphabet.size(); j++) {
                if (mListVideoAlphabet.get(i).charAt(0) > mListVideoAlphabet.get(j).charAt(0)) {
                    Collections.swap(mListVideoAlphabet, i, j);
                }
            }
        for (int i = 0; i < mListAudioAlphabet.size() - 1; i++)
            for (int j = i + 1; j < mListAudioAlphabet.size(); j++) {
                if (mListAudioAlphabet.get(i).charAt(0) > mListAudioAlphabet.get(j).charAt(0)) {
                    Collections.swap(mListAudioAlphabet, i, j);
                }
            }
    }

    public void toggleSearchBar() {
        mShowSearchBar = !mShowSearchBar;
        showHideSearchbar();
        switch (mTabsControl.getCheckedRadioButtonId()) {
            case R.id.buttonImage:
                isSearchImage = !isSearchImage;
                break;
            case R.id.buttonVideo:
                isSearchVideo = !isSearchVideo;
                break;
            case R.id.buttonAudio:
                isSearchAudio = !isSearchAudio;
                break;
        }
    }

    private void showHideSearchbar() {
        if (mShowSearchBar) {
            mSearchBar.setVisibility(View.VISIBLE);
            mLayoutSearchHolder.setVisibility(View.VISIBLE);
        } else {
            mSearchBar.setVisibility(View.GONE);
            mLayoutSearchHolder.setVisibility(View.GONE);
        }
    }

    @Override
    public void updateUI() {
        this.showHideSearchbar();
        if (mSlideShowRunning) {
            mLayoutTabsControl.setVisibility(View.GONE);
        } else {
            mLayoutTabsControl.setVisibility(View.VISIBLE);
        }
    }

    private void resetTabs() {
        switch (mTabsControl.getCheckedRadioButtonId()) {
            case R.id.buttonImage:
                mLvImages.setVisibility(View.VISIBLE);
                mLvVideos.setVisibility(View.GONE);
                mLvAudios.setVisibility(View.GONE);
                mLvImageAlphabets.setVisibility(View.VISIBLE);
                mLvVideoAlphabets.setVisibility(View.GONE);
                mLvAudioAlphabets.setVisibility(View.GONE);
                if (isSearchImage || isNewImageList) {
                    this.reloadListImages();
                    isSearchImage = false;
                    isNewImageList = false;
                }
                mAlphabetAdapter = new ArrayAdapter<>(mActivity, R.layout.list_alphabet, mListImageAlphabet);
                mLvImageAlphabets.setAdapter(mAlphabetAdapter);
                mLvImageAlphabets.setOnItemClickListener(this);
                break;
            case R.id.buttonVideo:
                mLvVideos.setVisibility(View.VISIBLE);
                mLvImages.setVisibility(View.GONE);
                mLvAudios.setVisibility(View.GONE);
                mLvVideoAlphabets.setVisibility(View.VISIBLE);
                mLvImageAlphabets.setVisibility(View.GONE);
                mLvAudioAlphabets.setVisibility(View.GONE);
                if (isSearchVideo || isNewVideoList) {
                    this.reloadListVideos();
                    isSearchVideo = false;
                    isNewVideoList = false;
                }
                mAlphabetAdapter = new ArrayAdapter<>(mActivity, R.layout.list_alphabet, mListVideoAlphabet);
                mLvVideoAlphabets.setAdapter(mAlphabetAdapter);
                mLvVideoAlphabets.setOnItemClickListener(this);
                break;
            case R.id.buttonAudio:
                mLvAudios.setVisibility(View.VISIBLE);
                mLvVideos.setVisibility(View.GONE);
                mLvImages.setVisibility(View.GONE);
                mLvAudioAlphabets.setVisibility(View.VISIBLE);
                mLvVideoAlphabets.setVisibility(View.GONE);
                mLvImageAlphabets.setVisibility(View.GONE);
                if (isSearchAudio || isNewAudioList) {
                    this.reloadListAudios();
                    isSearchAudio = false;
                    isNewAudioList = false;
                }
                mAlphabetAdapter = new ArrayAdapter<>(mActivity, R.layout.list_alphabet, mListAudioAlphabet);
                mLvAudioAlphabets.setAdapter(mAlphabetAdapter);
                mLvAudioAlphabets.setOnItemClickListener(this);
                break;
        }
        mLayoutImageView.setVisibility(View.GONE);
    }

    @Override
    public void OpenMedia(MediaChooserDialog.KindOfPlayer kindOfPlayer, String path) {
        switch (kindOfPlayer) {
            case MEDIA_APPLICATION:
                MediaUtils.openMedia(path);
                break;
            case DEFAULT_PLAYER:
                mMainInterface.PlayMedia(path);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId()) {
            case R.id.ListImageAlphabet:
                String image_alphabet = mListImageAlphabet.get(i);
                int image_position = this.findFilePositionByAlphabet(mMainInterface.getAllFiles(DataConstant.TYPE_IMAGE), image_alphabet);
                if (image_position >= 0) {
                    mLvImages.smoothScrollToPosition(image_position);
                }
                break;
            case R.id.ListVideoAlphabet:
                String video_alphabet = mListVideoAlphabet.get(i);
                int video_position = this.findFilePositionByAlphabet(mMainInterface.getAllFiles(DataConstant.TYPE_VIDEOCLIP), video_alphabet);
                if (video_position >= 0) {
                    mLvVideos.smoothScrollToPosition(video_position);
                }
                break;
            case R.id.ListAudioAlphabet:
                String audio_alphabet = mListAudioAlphabet.get(i);
                int audio_position = this.findFilePositionByAlphabet(mMainInterface.getAllFiles(DataConstant.TYPE_VOICE), audio_alphabet);
                if (audio_position >= 0) {
                    mLvAudios.smoothScrollToPosition(audio_position);
                }
                break;
            case R.id.ListImages:
                if (mTabsControl.getCheckedRadioButtonId() == R.id.buttonImage) {
                    mCurrentSelectedImage = i;
                    mIvSelectedImage.setImageBitmap(getBitmap(mCurrentSelectedImage));
                    mLayoutImageView.setVisibility(View.VISIBLE);
                }
                break;
            default:
                break;
        }
    }

    private Bitmap getBitmap(int i) {
        for (Map.Entry<String, Bitmap> entry : mListBitmap.entrySet()) {
            if (entry.getKey().equals(mListFile.get(i).getFilePath())) {
                return entry.getValue();
            }
        }
        return null;
    }

    private int findFilePositionByAlphabet(ArrayList<FileData> arrayList, String alphabet) {
        if (!alphabet.equals("#")) {
            for (int i = 0; i < arrayList.size(); i++) {
                String a2 = String.valueOf(arrayList.get(i).getFileName().charAt(0));
                if (alphabet.toLowerCase().equals(a2.toLowerCase())) {
                    return i;
                }
            }
            return -1;
        } else {
            for (int i = 0; i < arrayList.size(); i++) {
                char c = arrayList.get(i).getFileName().charAt(0);
                if ((c >= 32 && c <= 64) || (c >= 91 && c < 96) || (c >= 123 && c <= 127)) {
                    return i;
                }
            }
            return -1;
        }
    }

    private void searchFileByName(String name) {
        mListFile.clear();
        ArrayList<FileData> source = null;
        switch (mTabsControl.getCheckedRadioButtonId()) {
            case R.id.buttonImage:
                source = mMainInterface.getAllFiles(DataConstant.TYPE_IMAGE);
                break;
            case R.id.buttonVideo:
                source = mMainInterface.getAllFiles(DataConstant.TYPE_VIDEOCLIP);
                break;
            case R.id.buttonAudio:
                source = mMainInterface.getAllFiles(DataConstant.TYPE_VOICE);
                break;
        }
        if (source != null) {
            for (FileData data : source) {
                if (data.getFileName().contains(name)) {
                    mListFile.add(data);
                }
            }
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        updateUI();
        resetTabs();
        mEdtSearch.setText("");
        mSearchBar.setVisibility(View.GONE);
    }

    @Override
    public void OpenFile(String path) {
        mMediaChooser.setMediaPath(path);
        mMediaChooser.setTitle(LanguageUtils.getChooseMediaPlayerString());
        mMediaChooser.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonImageBefore:
                stopSlideShow();
                mCurrentSelectedImage--;
                if (mCurrentSelectedImage < 0) {
                    mCurrentSelectedImage = 0;
                }
                mIvSelectedImage.setImageBitmap(getBitmap(mCurrentSelectedImage));
                break;
            case R.id.buttonImageNext:
                stopSlideShow();
                mCurrentSelectedImage++;
                if (mCurrentSelectedImage >= mListBitmap.size()) {
                    mCurrentSelectedImage = mListBitmap.size() - 1;
                }
                mIvSelectedImage.setImageBitmap(getBitmap(mCurrentSelectedImage));
                break;
            case R.id.layoutSelectedImage:
                stopSlideShow();
                mLayoutImageView.setVisibility(View.GONE);
                break;
            case R.id.buttonCloseImageViewer:
                stopSlideShow();
                mLayoutImageView.setVisibility(View.GONE);
                break;
            case R.id.selectedImage:
                mLayoutImageView.setVisibility(View.VISIBLE);
                stopSlideShow();
                break;
            case R.id.buttonImageSlideShow:
                mLayoutImageView.setVisibility(View.VISIBLE);
                if (!mSlideShowRunning) {
                    mCurrentSelectedImage = 0;
                    startSlideShow();
                } else {
                    Toast.makeText(mActivity, LanguageUtils.getSlideShowBegunString(), Toast.LENGTH_SHORT).show();
                }
                break;
        }
        this.updateUI();
    }

    private void clearMemory() {
        mListBitmap.clear();
        mListFile.clear();
        mListImageAlphabet.clear();
        mListVideoAlphabet.clear();
        mListAudioAlphabet.clear();
    }

    private void startSlideShow() {
        mSlideShowRunning = true;
        mSlideShowThread.run();
        updateUI();
    }

    private void stopSlideShow() {
        if (mSlideShowRunning) {
            mCurrentSelectedImage--;
        }
        mSlideShowRunning = false;
        updateUI();
    }

    @Override
    public void onStop() {
        super.onStop();
        clearMemory();
    }

    private Bitmap getSuitableSizeBitmap(String path) {
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        ViewGroup.LayoutParams LayoutParams = SupportUtils.getScreenParams(mActivity);
        int w = (bitmap.getWidth() > LayoutParams.width) ? LayoutParams.width : bitmap.getWidth();
        int h = (bitmap.getHeight() > (LayoutParams.height / 2)) ?
                (int) SupportUtils.getHeightByWidth(bitmap.getWidth(), bitmap.getHeight(), w) : bitmap.getHeight();

        return SupportUtils.resizeBitmap(bitmap, w, h);
    }

}
