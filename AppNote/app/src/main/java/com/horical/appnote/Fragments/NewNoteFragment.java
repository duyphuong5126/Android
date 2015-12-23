package com.horical.appnote.Fragments;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.horical.appnote.DTO.NoteDTO.NoteData;
import com.horical.appnote.DTO.NoteDTO.NoteImage;
import com.horical.appnote.DTO.NoteDTO.NoteReminder;
import com.horical.appnote.DTO.NoteDTO.NoteSummary;
import com.horical.appnote.DTO.NoteDTO.NoteText;
import com.horical.appnote.DTO.NoteDTO.NoteVideoClip;
import com.horical.appnote.DTO.NoteDTO.NoteVoice;
import com.horical.appnote.LocalStorage.ApplicationSharedData;
import com.horical.appnote.LocalStorage.DAO.NoteDataDAO;
import com.horical.appnote.LocalStorage.DAO.NoteReminderDAO;
import com.horical.appnote.LocalStorage.DataConstant;
import com.horical.appnote.Interfaces.NoteDataLine;
import com.horical.appnote.Interfaces.OpenMediaInterface;
import com.horical.appnote.MyView.MyDialog.AddReminderDialog;
import com.horical.appnote.MyView.MyDialog.MediaChooserDialog;
import com.horical.appnote.MyView.NoteDataView.FormattedText;
import com.horical.appnote.MyView.NoteDataView.MyAudioView;
import com.horical.appnote.MyView.NoteDataView.MyVideoView;
import com.horical.appnote.MyView.NoteDataView.PairDataView;
import com.horical.appnote.R;
import com.horical.appnote.Supports.FileUtils;
import com.horical.appnote.Supports.LanguageUtils;
import com.horical.appnote.Supports.MediaUtils;
import com.horical.appnote.Supports.SupportUtils;
import com.horical.appnote.TouchPaint.TouchPaintActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Stack;


/**
 * Created by Phuong on 24/07/2015.
 */
public class NewNoteFragment extends BaseFragment implements View.OnClickListener, EditText.OnTouchListener,
        AddReminderDialog.ReminderCallback, MediaChooserDialog.MediaCallback, OpenMediaInterface {

    private static final int LOAD_IMAGE_SUCCESS = 1;
    private static final int LOAD_VIDEO_SUCCESS = 2;
    private static final int LOAD_AUDIO_SUCCESS = 3;
    private static final int LOAD_OTHER_FILE_SUCCESS = 4;
    private static final int CREATE_PHOTO = 5;
    private static final int CREATE_VIDEO = 6;
    private static final int CREATE_AUDIO = 7;
    public static final int CREATE_FINGER_PAINT = 8;

    private LinearLayout mLayoutEditorManager, mLayoutContainer, mLayoutAttachFiles;
    private LinearLayout mLayoutProgress;

    private ArrayList<PairDataView> mListContent;
    private Stack<PairDataView> mUndoRedoStack;

    private NoteData mNoteData;

    private NoteSummary mNoteSummary;

    private NoteReminder mNoteReminder;

    private NoteDataDAO mNoteDataDAO;

    private AlertDialog.Builder mBuilder;
    private MediaChooserDialog mMediaChooserDialog;

    private boolean isCreate;

    private Uri mCameraPhoto;

    private AsyncTask<Void, View, Void> mAsyncLoadNoteDetails;

    private ArrayList<FormattedText> mListFormatEditor;

    private TextView mTvCreateNew, mTvAttach;
    private TextView mTvTakePhoto, mTvAudioRecord, mTvVideoCapture, mTvImage, mTvAudio;
    private TextView mTvScreenTitle;

    private ArrayList<String> mContextMenuImageView;
    private int mCurrentImageViewSelected;

    public interface CreateNoteCallback {
        void onSuccess(String message);
        void onFailure(String message);
    }

    public NewNoteFragment() {
        this.mLayout_xml_id = R.layout.fragment_new_note;
        this.mNoteData = new NoteData();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.isCreate = getArguments().getBoolean("isCreate");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mTvCreateNew = (TextView) mFragmentView.findViewById(R.id.tvCreateNew);
        mTvAttach = (TextView) mFragmentView.findViewById(R.id.tvAttach);
        mTvTakePhoto = (TextView) mFragmentView.findViewById(R.id.tvTakePhoto);
        mTvAudioRecord = (TextView) mFragmentView.findViewById(R.id.tvAudioRecord);
        mTvVideoCapture = (TextView) mFragmentView.findViewById(R.id.tvVideoCapture);
        mTvImage = (TextView) mFragmentView.findViewById(R.id.tvImage);
        mTvAudio = (TextView) mFragmentView.findViewById(R.id.tvAudio);
        mTvScreenTitle = (TextView) mFragmentView.findViewById(R.id.tvScreenTitle);

        mTvScreenTitle.setText(LanguageUtils.getYourNoteString());

        this.mListFormatEditor = new ArrayList<FormattedText>();

        this.mContextMenuImageView = new ArrayList<String>();
        for (String resource : mActivity.getResources().getStringArray(R.array.ImageViewContextItem)) {
            mContextMenuImageView.add(resource);
        }

        mNoteSummary = new NoteSummary();

        mBuilder = new AlertDialog.Builder(mActivity);

        mUndoRedoStack = new Stack<PairDataView>();

        mListContent = new ArrayList<PairDataView>();

        mLayoutEditorManager = (LinearLayout) mFragmentView.findViewById(R.id.layoutEditorManager);
        mLayoutContainer = ((LinearLayout) mFragmentView.findViewById(R.id.EditArea));

        //set click listener method for button
        ((ImageButton) mFragmentView.findViewById(R.id.buttonEditor)).setOnClickListener(this);
        ((ImageButton) mFragmentView.findViewById(R.id.buttonAttachFile)).setOnClickListener(this);
        ((ImageButton) mFragmentView.findViewById(R.id.buttonConfirm)).setOnClickListener(this);
        ((ImageButton) mFragmentView.findViewById(R.id.buttonEditorBold)).setOnClickListener(this);
        ((ImageButton) mFragmentView.findViewById(R.id.buttonEditorItalic)).setOnClickListener(this);
        ((ImageButton) mFragmentView.findViewById(R.id.buttonEditorUnderline)).setOnClickListener(this);
        ((ImageButton) mFragmentView.findViewById(R.id.buttonCloseAttachLayout)).setOnClickListener(this);
        ((ImageButton) mFragmentView.findViewById(R.id.buttonReminder)).setOnClickListener(this);
        ((ImageButton) mFragmentView.findViewById(R.id.buttonUndo)).setOnClickListener(this);
        ((ImageButton) mFragmentView.findViewById(R.id.buttonRedo)).setOnClickListener(this);
        ((ImageButton) mFragmentView.findViewById(R.id.buttonMore)).setOnClickListener(this);

        ((LinearLayout) mFragmentView.findViewById(R.id.layoutAttachImage)).setOnClickListener(this);
        ((LinearLayout) mFragmentView.findViewById(R.id.layoutAttachVideo)).setOnClickListener(this);
        ((LinearLayout) mFragmentView.findViewById(R.id.layoutAttachAudio)).setOnClickListener(this);
        ((LinearLayout) mFragmentView.findViewById(R.id.layoutTakePhoto)).setOnClickListener(this);
        ((LinearLayout) mFragmentView.findViewById(R.id.layoutVideoRecorder)).setOnClickListener(this);
        ((LinearLayout) mFragmentView.findViewById(R.id.layoutVoiceRecorder)).setOnClickListener(this);
        ((LinearLayout) mFragmentView.findViewById(R.id.layoutTouchPaint)).setOnClickListener(this);

        mLayoutProgress = (LinearLayout) mFragmentView.findViewById(R.id.layoutProgress);

        EditText edittextNoteContent = (EditText) mFragmentView.findViewById(R.id.editNoteContent);
        edittextNoteContent.setHint(LanguageUtils.getNoteContentHintString());
        EditText edittextNoteTitle = (EditText) mFragmentView.findViewById(R.id.editNoteTitle);
        edittextNoteTitle.setHint(LanguageUtils.getNoteTitleHintString());
        edittextNoteTitle.setOnTouchListener(this);
        edittextNoteContent.setOnTouchListener(this);
        this.addTextWatcher(edittextNoteTitle);
        this.addTextWatcher(edittextNoteContent);
        edittextNoteTitle.requestFocus();

        if (isCreate) {
            mNoteSummary.setID(SupportUtils.getRandomID());
            mListContent.add(new PairDataView(new NoteText(mNoteSummary.getID(), DataConstant.TYPE_TEXT, "con", 0), edittextNoteContent));
            this.mListFormatEditor.add(new FormattedText(edittextNoteTitle));
            this.mListFormatEditor.add(new FormattedText(edittextNoteContent));
        } else {
            mLayoutProgress.setVisibility(View.VISIBLE);
            mNoteData = new NoteData();
            mNoteData = (NoteData) getArguments().getSerializable("NoteData");
            mNoteSummary = mNoteData.getNoteSummary();
            edittextNoteTitle.setText(this.getFormattedEditable(mNoteData.getNoteSummary().getTitle()));

            String format = FormattedText.getFormatFromFormatedJSON(mNoteData.getNoteSummary().getTitle());
            this.mListFormatEditor.add(new FormattedText(edittextNoteTitle, format));
            int start = 0;
            if (!mNoteData.getNoteData().isEmpty()) {
                if (mNoteData.getNoteData().get(0).typeIdentify().equals(DataConstant.TYPE_TEXT)) {
                    NoteText noteText = (NoteText) mNoteData.getNoteData().get(0);
                    edittextNoteContent.setText(this.getFormattedEditable(noteText.getContent()));

                    format = FormattedText.getFormatFromFormatedJSON(noteText.getContent());
                    this.mListFormatEditor.add(new FormattedText(edittextNoteContent, format));
                    start = 1;
                } else {
                    edittextNoteContent.setText("");
                    this.mListFormatEditor.add(new FormattedText(edittextNoteContent));
                }
                mListContent.add(new PairDataView(new NoteText(mNoteSummary.getID(), DataConstant.TYPE_TEXT, "con", 0), edittextNoteContent));
            }
            final int startPos = start;
            mAsyncLoadNoteDetails = new AsyncTask<Void, View, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    for (int i = startPos; i < mNoteData.getNoteData().size(); i++) {
                        switch (mNoteData.getNoteData().get(i).typeIdentify()) {
                            case DataConstant.TYPE_TEXT:
                                NoteText noteText = (NoteText) mNoteData.getNoteData().get(i);
                                publishProgress(createEditText(noteText));
                                break;
                            case DataConstant.TYPE_IMAGE:
                                NoteImage noteImage = (NoteImage) mNoteData.getNoteData().get(i);
                                publishProgress(createImageView(BitmapFactory.decodeFile(noteImage.getFilePath()),
                                        noteImage.getFileName(), noteImage.getFilePath()));
                                break;
                            case DataConstant.TYPE_VIDEOCLIP:
                                NoteVideoClip noteVideoClip = (NoteVideoClip) mNoteData.getNoteData().get(i);
                                publishProgress(
                                        createMyVideoView(noteVideoClip.getFilePath(), noteVideoClip.getFileName(), Uri.parse(noteVideoClip.getFilePath()))
                                );
                                break;
                            case DataConstant.TYPE_VOICE:
                                NoteVoice noteVoice = (NoteVoice) mNoteData.getNoteData().get(i);
                                publishProgress(
                                        createAudioView(noteVoice.getFilePath(), noteVoice.getFileName(), Uri.parse(noteVoice.getFilePath()))
                                );
                                break;
                            default:
                                break;
                        }
                    }
                    return null;
                }

                @Override
                protected void onProgressUpdate(View... values) {
                    super.onProgressUpdate(values);
                    mLayoutContainer.addView(values[0]);
                    addEditText(createEditText(""));
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    mLayoutProgress.setVisibility(View.GONE);
                }
            };

            mAsyncLoadNoteDetails.execute();
        }

        mMediaChooserDialog = new MediaChooserDialog(mActivity);
        mMediaChooserDialog.setMediaCallback(this);
        mMediaChooserDialog.setTitle(LanguageUtils.getChooseMediaPlayerString());
    }

    @Override
    public void onStart() {
        super.onStart();
        mTvCreateNew.setText(LanguageUtils.getCreateNewString());
        mTvAttach.setText(LanguageUtils.getAttachString());
        mTvTakePhoto.setText(LanguageUtils.getTakePhotoString());
        mTvAudioRecord.setText(LanguageUtils.getRecordAudioString());
        mTvVideoCapture.setText(LanguageUtils.getVideoCaptureString());
        mTvImage.setText(LanguageUtils.getImageString());
        mTvAudio.setText(LanguageUtils.getAudioString());
    }

    @Override
    public void updateUI() {

    }

    private Editable getFormattedEditable(String text) {
        String content = FormattedText.getContentFromFormatedJSON(text);
        String format = FormattedText.getFormatFromFormatedJSON(text);
        return FormattedText.getFormattedText(content, format);
    }

    private int getFormatItem(int editID) {
        for (int i = 0; i < mListFormatEditor.size(); i++) {
            if (mListFormatEditor.get(i).getEditText().getId() == editID) {
                return i;
            }
        }
        return -1;
    }

    private void addTextWatcher(final EditText editText) {
        final EditText edit = editText;
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                Log.d("Text change", "start: " + i + " before: " + i2 + " end: " + i3);
                int start = i, before = i2, count = i3;

                int position = getFormatItem(edit.getId());
                if (position >= 0) {
                    if (count > before) {
                        mListFormatEditor.get(position).addFormatItem(start);
                    } else if (before > count) {
                        mListFormatEditor.get(position).removeCharacter(start);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.d("After Text change", "in");
                int position = getFormatItem(edit.getId());
                if (position >= 0) {
                    editable = mListFormatEditor.get(position).getFormattedText();
                }
                return;
            }
        });
    }

    private void intentLoadContent(String fileType, int request_code) {
        Intent intentOpenFile = new Intent();
        intentOpenFile.setType(fileType + "/*");
        intentOpenFile.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intentOpenFile, LanguageUtils.getSelectFileString()), request_code);
    }

    private void intentCapture(String fileType, int request_code) {
        Intent intentCreateFile = new Intent("android.media.action.IMAGE_CAPTURE");
        File output = new File(Environment.getExternalStorageDirectory(), "Captured_" + SupportUtils.getRandomID() + fileType);
        intentCreateFile.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(output));
        mCameraPhoto = Uri.fromFile(output);
        startActivityForResult(Intent.createChooser(intentCreateFile, LanguageUtils.getSelectFileString()), request_code);
    }

    private void intentVideoRecord(int request_code) {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 120);
        startActivityForResult(intent, request_code);
    }
    private void intentAudioRecord(int request_code) {
        Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
        startActivityForResult(intent, request_code);
    }
    private void intentTouchPaint(int request_code) {
        Intent intent = new Intent(mActivity, TouchPaintActivity.class);
        TouchPaintActivity.setListener(new TouchPaintActivity.DrawerListener() {
            @Override
            public void onDrawComplete(String imagePath) {
                String name = SupportUtils.getNameFromPath(imagePath);
                if (imagePath != null && !"".equals(imagePath)) {
                    Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                    addImageView(createImageView(bitmap, name, imagePath));
                }
            }
        });
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == mActivity.RESULT_OK) {
            switch (requestCode) {
                case LOAD_IMAGE_SUCCESS:
                case LOAD_VIDEO_SUCCESS:
                case LOAD_AUDIO_SUCCESS:
                case LOAD_OTHER_FILE_SUCCESS:
                case CREATE_VIDEO:
                case CREATE_AUDIO:
                    Uri uri = data.getData();
                    String path = (FileUtils.checkFileIsFromContent(uri.getPath()) ? FileUtils.getPath(uri, mActivity) : uri.getPath());
                    String fileName = SupportUtils.getNameFromPath(path);
                    if (path != null && !path.equals("")) {
                        switch (requestCode) {
                            case LOAD_IMAGE_SUCCESS:
                                this.addImageView(this.createImageView(BitmapFactory.decodeFile(path), fileName, path));
                                break;
                            case LOAD_VIDEO_SUCCESS:
                            case CREATE_VIDEO:
                                this.addMyVideoView(this.createMyVideoView(path, fileName, uri));
                                break;
                            case LOAD_AUDIO_SUCCESS:
                            case CREATE_AUDIO:
                                this.addMyAudioView(this.createAudioView(path, fileName, uri));
                                break;
                            default:
                                break;
                        }
                        this.addEditText(createEditText(""));
                    } else {
                        Toast.makeText(mActivity, LanguageUtils.getSelectFileErrorString(), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case CREATE_PHOTO:
                    ContentResolver contentResolver = mActivity.getContentResolver();
                    contentResolver.notifyChange(mCameraPhoto, null);
                    try {
                        Bitmap bitmap = Bitmap.createBitmap(MediaStore.Images.Media.getBitmap(contentResolver, mCameraPhoto));
                        this.addImageView(
                                this.createImageView(bitmap, "Photo" + SupportUtils.getRandomID(), mCameraPhoto.getPath())
                        );
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case CREATE_FINGER_PAINT:
                    break;
            }
        }
    }

    private EditText createEditText(String text) {
        EditText editText = new EditText(mActivity);
        editText.setId(SupportUtils.getRandomID());
        editText.setText(text);
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        editText.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        editText.setBackgroundDrawable(mActivity.getResources().getDrawable(R.drawable.transparent));

        mListContent.add(new PairDataView(new NoteText(mNoteSummary.getID(), DataConstant.TYPE_TEXT, "", 0), editText));
        editText.setOnTouchListener(this);

        if (text.length() > 0) {
            this.mListFormatEditor.add(new FormattedText(editText, text.length()));
        } else {
            this.mListFormatEditor.add(new FormattedText(editText));
        }
        this.addTextWatcher(editText);
        return editText;
    }

    private void addEditText(EditText editText) {
        mLayoutContainer.addView(editText);
    }

    private EditText createEditText(NoteText noteText) {
        String content = FormattedText.getContentFromFormatedJSON(noteText.getContent());
        String format = FormattedText.getFormatFromFormatedJSON(noteText.getContent());

        EditText editText = new EditText(mActivity);
        editText.setId(SupportUtils.getRandomID());
        editText.setText(FormattedText.getFormattedText(content, format));
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        editText.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        editText.setBackgroundDrawable(mActivity.getResources().getDrawable(R.drawable.transparent));

        mListContent.add(new PairDataView(new NoteText(mNoteSummary.getID(), DataConstant.TYPE_TEXT, "", 0), editText));
        editText.setOnTouchListener(this);

        if (content.length() > 0) {
            this.mListFormatEditor.add(new FormattedText(editText, format));
        } else {
            this.mListFormatEditor.add(new FormattedText(editText));
        }
        this.addTextWatcher(editText);
        return editText;
    }

    private ImageView createImageView(Bitmap bitmap, String fileName, String path) {
        if (bitmap == null) {
            return null;
        } else {
            ViewGroup.LayoutParams layoutParams = SupportUtils.getScreenParams(mActivity);
            int w = (bitmap.getWidth() > layoutParams.width) ? layoutParams.width : bitmap.getWidth();
            int h = (bitmap.getHeight() > (layoutParams.height / 2)) ?
                    (int) SupportUtils.getHeightByWidth(bitmap.getWidth(), bitmap.getHeight(), w) : bitmap.getHeight();

            Bitmap bmp = SupportUtils.resizeBitmap(bitmap, w, h);
            ImageView imageView = new ImageView(mActivity);
            imageView.setId(SupportUtils.getRandomID());
            imageView.setImageBitmap(bmp);

            mListContent.add(new PairDataView(new NoteImage(fileName, DataConstant.TYPE_IMAGE, path, mNoteSummary.getID()), imageView));

            return imageView;
        }
    }

    private void addImageView(ImageView imageView) {
        if (imageView == null) {
            Toast.makeText(mActivity, LanguageUtils.getLoadImageErrorString(), Toast.LENGTH_SHORT).show();
        } else {
            mLayoutContainer.addView(imageView);
            registerForContextMenu(imageView);
        }
    }

    private View createMyVideoView(String path, String fileName, Uri uri) {
        Bitmap thumb = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Video.Thumbnails.MICRO_KIND);
        if (thumb == null) {
            return null;
        } else {
            MyVideoView layoutVideoInfor = new MyVideoView(mActivity, path);
            layoutVideoInfor.hideDivider();
            layoutVideoInfor.getView().setId(SupportUtils.getRandomID());
            layoutVideoInfor.setImageView(thumb);
            layoutVideoInfor.setTextViewName(fileName);
            layoutVideoInfor.setTextViewInfor(SupportUtils.MilliSecToTime(SupportUtils.getDuration(uri, mActivity)));
            layoutVideoInfor.setOpenFileListener(this);

            layoutVideoInfor.getDeleteThisButton().setOnClickListener(this);
            mListContent.add(new PairDataView
                    (new NoteVideoClip(fileName, path, SupportUtils.getDuration(uri, mActivity),
                            DataConstant.TYPE_VIDEOCLIP, mNoteSummary.getID()), layoutVideoInfor));

            return layoutVideoInfor.getView();
        }
    }

    private void addMyVideoView(View view) {
        if (view == null) {
            Toast.makeText(mActivity, LanguageUtils.getLoadVideoErrorString(), Toast.LENGTH_SHORT).show();
        } else {
            mLayoutContainer.addView(view);
        }
    }

    private View createAudioView(String path, String fileName, Uri uri) {
        MyAudioView layoutAudioInfor = new MyAudioView(mActivity, path);
        layoutAudioInfor.hideDivider();
        layoutAudioInfor.getView().setId(SupportUtils.getRandomID());
        layoutAudioInfor.setTextViewName(fileName);
        layoutAudioInfor.setTextViewInfor(SupportUtils.MilliSecToTime(SupportUtils.getDuration(uri, mActivity)));
        layoutAudioInfor.setOpenFileListener(this);

        layoutAudioInfor.getDeleteThisButton().setOnClickListener(this);
        mListContent.add(new PairDataView(new NoteVoice(fileName, path, SupportUtils.getDuration(uri, mActivity), DataConstant.TYPE_VOICE, mNoteSummary.getID()), layoutAudioInfor));

        return layoutAudioInfor.getView();
    }

    private void addMyAudioView(View view) {
        mLayoutContainer.addView(view);
    }

    private void hideSoftKeyboard() {
        View view = getActivity().getCurrentFocus();
        InputMethodManager input = (InputMethodManager) this.mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        input.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onClick(View view) {
        final int viewID = view.getId();
        switch (viewID) {
            //button click
            case R.id.buttonEditor:
                mLayoutEditorManager.setVisibility((mLayoutEditorManager.getVisibility() == View.GONE) ? View.VISIBLE : View.GONE);
                this.hideSoftKeyboard();
                return;
            case R.id.buttonAttachFile:
                this.toggleArea(R.id.layoutAttachFile);
                this.hideSoftKeyboard();
                return;
            case R.id.buttonConfirm:
                mBuilder.setTitle(LanguageUtils.getConfirmSaveString());
                mBuilder.setPositiveButton(LanguageUtils.getYesString(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        saveNote(true);
                    }
                }).setNegativeButton(LanguageUtils.getNoString(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                }).create().show();
                this.hideSoftKeyboard();
                return;
            case R.id.buttonEditorBold:
                String message = FormattedText.changeCurrentFormat(FormattedText.BOLD);
                Toast.makeText(mActivity, "Current format: " + message, Toast.LENGTH_SHORT).show();
                return;
            case R.id.buttonEditorItalic:
                String message1 = FormattedText.changeCurrentFormat(FormattedText.ITALIC);
                Toast.makeText(mActivity, "Current format: " + message1, Toast.LENGTH_SHORT).show();
                return;
            case R.id.buttonEditorUnderline:
                String message3 = FormattedText.changeCurrentFormat(FormattedText.UNDERLINE);
                Toast.makeText(mActivity, "Current format: " + message3, Toast.LENGTH_SHORT).show();
                return;
            case R.id.buttonCloseAttachLayout:
                this.toggleArea(R.id.layoutAttachFile);
                this.hideSoftKeyboard();
                return;
            case R.id.buttonReminder:
                AddReminderDialog reminderDialog = new AddReminderDialog(mActivity);
                AddReminderDialog.isReminderForNote = true;
                reminderDialog.setReminderCallback(this);
                reminderDialog.show();
                this.hideSoftKeyboard();
                return;
            case R.id.buttonUndo:
                if (mListContent.size() > 0) {
                    PairDataView currentPairUndo = mListContent.get(mListContent.size() - 1);
                    mListContent.remove(mListContent.size() - 1);
                    mUndoRedoStack.push(currentPairUndo);
                    switch (currentPairUndo.getNoteDataInterface().typeIdentify()) {
                        case DataConstant.TYPE_VIDEOCLIP:
                            MyVideoView myVideoView = (MyVideoView) currentPairUndo.getView();
                            myVideoView.getView().setVisibility(View.GONE);
                            break;
                        case DataConstant.TYPE_VOICE:
                            MyAudioView myAudioView = (MyAudioView) currentPairUndo.getView();
                            myAudioView.getView().setVisibility(View.GONE);
                            break;
                        case DataConstant.TYPE_IMAGE:
                        case DataConstant.TYPE_TEXT:
                            currentPairUndo.getView().setVisibility(View.GONE);
                            break;
                        default:
                            break;
                    }
                }
                this.hideSoftKeyboard();
                break;
            case R.id.buttonRedo:
                if (!mUndoRedoStack.empty()) {
                    PairDataView currentPairRedo = mUndoRedoStack.pop();
                    mListContent.add(currentPairRedo);
                    switch (currentPairRedo.getNoteDataInterface().typeIdentify()) {
                        case DataConstant.TYPE_VIDEOCLIP:
                            MyVideoView myVideoView = (MyVideoView) currentPairRedo.getView();
                            myVideoView.getView().setVisibility(View.VISIBLE);
                            break;
                        case DataConstant.TYPE_VOICE:
                            MyAudioView myAudioView = (MyAudioView) currentPairRedo.getView();
                            myAudioView.getView().setVisibility(View.VISIBLE);
                            break;
                        case DataConstant.TYPE_IMAGE:
                        case DataConstant.TYPE_TEXT:
                            currentPairRedo.getView().setVisibility(View.VISIBLE);
                            break;
                        default:
                            break;
                    }
                }
                break;

            //layout click
            case R.id.layoutAttachImage:
                this.intentLoadContent("image", LOAD_IMAGE_SUCCESS);
                this.toggleArea(R.id.layoutAttachFile);
                this.hideSoftKeyboard();
                return;
            case R.id.layoutAttachVideo:
                this.intentLoadContent("video", LOAD_VIDEO_SUCCESS);
                this.toggleArea(R.id.layoutAttachFile);
                this.hideSoftKeyboard();
                return;
            case R.id.layoutAttachAudio:
                this.intentLoadContent("audio", LOAD_AUDIO_SUCCESS);
                this.toggleArea(R.id.layoutAttachFile);
                this.hideSoftKeyboard();
                return;
            case R.id.buttonMore:
                PopupMenu popupMenu = new PopupMenu(mActivity, view);
                popupMenu.getMenuInflater().inflate(LanguageUtils.getCreateNoteMenuRes(), popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.itemRecent:
                                NoteData noteData = mMainInterface.getRecentNote();
                                if (noteData != null && noteData.getNoteData() != null) {
                                    mMainInterface.PassNoteSummary(noteData.getNoteSummary());
                                } else {
                                    Toast.makeText(mActivity, LanguageUtils.getNothingListNoteString(), Toast.LENGTH_SHORT).show();
                                }
                                break;
                            case R.id.itemBack:
                                mMainInterface.Back();
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
                this.hideSoftKeyboard();
                return;
            case R.id.layoutTakePhoto:
                this.intentCapture(".jpg", CREATE_PHOTO);
                this.toggleArea(R.id.layoutAttachFile);
                this.hideSoftKeyboard();
                return;
            case R.id.layoutVideoRecorder:
                intentVideoRecord(CREATE_VIDEO);
                this.toggleArea(R.id.layoutAttachFile);
                return;
            case R.id.layoutVoiceRecorder:
                intentAudioRecord(CREATE_AUDIO);
                this.toggleArea(R.id.layoutAttachFile);
                return;
            case R.id.layoutTouchPaint:
                intentTouchPaint(CREATE_FINGER_PAINT);
                this.toggleArea(R.id.layoutAttachFile);
                return;
            default:
                break;
        }
        this.deleteMyMediaView(view.getId());

    }

    private void saveNote(boolean show) {
        if (show) {
            mLayoutProgress.setVisibility(View.VISIBLE);
            mMainInterface.showLoadingEffect(true, "Save in process...");
        }
        //store note content and note summary
        mNoteSummary.setTitle(getFormatedString(((EditText) mFragmentView.findViewById(R.id.editNoteTitle))));

        ArrayList<NoteDataLine> listData = new ArrayList<>();
        for (PairDataView pairDataView : mListContent) {
            NoteDataLine noteDataLine = pairDataView.getNoteDataInterface();
            View v = pairDataView.getView();
            switch (noteDataLine.typeIdentify()) {
                case DataConstant.TYPE_TEXT:
                    EditText editText = (EditText) v;
                    if (!editText.getText().toString().equals("") &&
                            editText.getText() != null && editText.getVisibility() == View.VISIBLE) {
                        NoteText noteText = (NoteText) pairDataView.getNoteDataInterface();
                        noteText.setContent(getFormatedString(editText));
                        noteText.setType(DataConstant.TYPE_TEXT);
                        listData.add(noteText);
                    }
                    break;
                case DataConstant.TYPE_IMAGE:
                    if (v.getVisibility() == View.VISIBLE) {
                        listData.add((NoteImage) pairDataView.getNoteDataInterface());
                    }
                    break;
                case DataConstant.TYPE_VIDEOCLIP:
                    if (((MyVideoView) pairDataView.getView()).getView().getVisibility() == View.VISIBLE) {
                        listData.add((NoteVideoClip) pairDataView.getNoteDataInterface());
                    }
                    break;
                case DataConstant.TYPE_VOICE:
                    if (((MyAudioView) pairDataView.getView()).getView().getVisibility() == View.VISIBLE) {
                        listData.add((NoteVoice) pairDataView.getNoteDataInterface());
                    }
                    break;
            }
        }

        if (listData.size() > 0) {
            mNoteData.setNoteData(listData);
            if (mNoteSummary.getCreatedAt() == null) {
                mNoteSummary.setCreatedAt(SupportUtils.convertDateToString(Calendar.getInstance()));
            }
            mNoteSummary.setModifiedAt(SupportUtils.convertDateToString(Calendar.getInstance()));
            mNoteData.setNoteSummary(mNoteSummary);

            mNoteDataDAO = new NoteDataDAO(mActivity);
            mNoteDataDAO.setNoteData(mNoteData);

            /*store or update data...*/
            int result = -1;
            if (isCreate) {
                mNoteDataDAO.insertNote(new CreateNoteCallback() {
                    @Override
                    public void onSuccess(String message) {
                        Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(String message) {
                        Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                mNoteDataDAO.updateNote(new CreateNoteCallback() {
                    @Override
                    public void onSuccess(String message) {
                        Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
                        mMainInterface.showLoadingEffect(false, "");
                    }

                    @Override
                    public void onFailure(String message) {
                        Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
                        mMainInterface.showLoadingEffect(false, "");
                    }
                });
            }

            if (mNoteReminder != null) {
                mNoteReminder.setNoteId(result);
                NoteReminderDAO noteReminderDAO = new NoteReminderDAO(mActivity, mNoteReminder);
                if (noteReminderDAO.insertNoteReminder()) {
                    Toast.makeText(mActivity, LanguageUtils.getAddReminderSuccessString(), Toast.LENGTH_SHORT).show();
                    mMainInterface.reloadListReminder();
                } else {
                    Toast.makeText(mActivity, LanguageUtils.getAddReminderFailString(), Toast.LENGTH_LONG).show();
                }
            }
            /*uploadNote();*/
            mNoteData.clear();

            mMainInterface.reloadListFile(DataConstant.TYPE_IMAGE);
            mMainInterface.reloadListFile(DataConstant.TYPE_VIDEOCLIP);
            mMainInterface.reloadListFile(DataConstant.TYPE_VOICE);
            mMainInterface.reloadListNote();
            if (show) {
                mMainInterface.ChangeFragment(BaseFragment.NewNoteFragment, BaseFragment.ListNotesFragment);
            }
        } else {
            if (show) {
                mMainInterface.showLoadingEffect(false, "");
                Toast.makeText(mActivity, LanguageUtils.getEmptyNoteString(), Toast.LENGTH_SHORT).show();
            }
        }
        mLayoutProgress.setVisibility(View.GONE);
    }

    private String getFormatedString(EditText editText) {
        for (FormattedText formattedText : mListFormatEditor) {
            if (formattedText.getEditText().getId() == editText.getId()) {
                return FormattedText.formatString(editText.getText().toString(), formattedText.formatToString());
            }
        }
        return "";
    }

    private void deleteMyMediaView(int id) {
        int selectedPos = -1;
        for (int i = 0; i < mListContent.size(); i++) {
            switch (mListContent.get(i).getNoteDataInterface().typeIdentify()) {
                case DataConstant.TYPE_VIDEOCLIP:
                    MyVideoView myVideoView = (MyVideoView) mListContent.get(i).getView();
                    if (myVideoView.getDeleteThisButton().getId() == id) {
                        myVideoView.getView().setVisibility(View.GONE);
                        selectedPos = i;
                        mUndoRedoStack.push(mListContent.get(i));
                    }
                    break;
                case DataConstant.TYPE_VOICE:
                    MyAudioView myAudioView = (MyAudioView) mListContent.get(i).getView();
                    if (myAudioView.getDeleteThisButton().getId() == id) {
                        myAudioView.getView().setVisibility(View.GONE);
                        selectedPos = i;
                        mUndoRedoStack.push(mListContent.get(i));
                    }
                    break;
                default:
                    break;
            }
        }
        if (selectedPos > 0) {
            mListContent.remove(selectedPos);
        }
    }

    private void toggleArea(int layout_id) {
        View viewSource = (View) mFragmentView.findViewById(layout_id);
        viewSource.setVisibility((viewSource.getVisibility() == View.VISIBLE) ? View.GONE : View.VISIBLE);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        ((LinearLayout) mFragmentView.findViewById(R.id.layoutAttachFile)).setVisibility(View.GONE);
        return false;
    }

    @Override
    public void PassReminder(NoteReminder reminder) {
        if (reminder != null) {
            this.mNoteReminder = new NoteReminder();
            this.mNoteReminder = reminder;
            this.mNoteSummary.setScheduled(true);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle(LanguageUtils.getSelectActionString());
        for (String action : mContextMenuImageView) {
            menu.add(action);
        }
        this.mCurrentImageViewSelected = v.getId();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().toString().equals(LanguageUtils.getDeleteString())) {
            this.deleteMyImageView(this.mCurrentImageViewSelected);
        }
        return super.onContextItemSelected(item);
    }

    private void deleteMyImageView(int currentImageViewSelected) {
        int selectedIndex = -1;
        for (int i = 0; i < mListContent.size(); i++) {
            if (mListContent.get(i).getNoteDataInterface().typeIdentify().equals(DataConstant.TYPE_IMAGE) &&
                    mListContent.get(i).getView().getId() == currentImageViewSelected) {
                selectedIndex = i;
                mListContent.get(i).getView().setVisibility(View.GONE);
            }
        }
        if (selectedIndex >= 0) {
            mUndoRedoStack.push(mListContent.get(selectedIndex));
            mListContent.remove(selectedIndex);
        }
    }

    @Override
    public void OpenMedia(MediaChooserDialog.KindOfPlayer kindOfPlayer, String path) {
        switch (kindOfPlayer) {
            case MEDIA_APPLICATION:
                MediaUtils.openMedia(path);
                return;
            case DEFAULT_PLAYER:
                mMainInterface.PlayMedia(path);
                return;
            default:
                break;
        }
    }

    @Override
    public void OpenFile(String path) {
        mMediaChooserDialog.setMediaPath(path);
        mMediaChooserDialog.show();
    }

    @Override
    public void onStop() {
        if (ApplicationSharedData.isAutoSave()) {
            saveNote(false);
        }
        this.hideSoftKeyboard();
        super.onStop();
    }
}
