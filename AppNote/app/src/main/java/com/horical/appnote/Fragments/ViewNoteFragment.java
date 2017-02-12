package com.horical.appnote.Fragments;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import com.horical.appnote.BaseActivity.BaseActivity;
import com.horical.appnote.LocalStorage.DAO.NoteDataDAO;
import com.horical.appnote.LocalStorage.DataConstant;
import com.horical.appnote.DTO.NoteDTO.NoteData;
import com.horical.appnote.DTO.NoteDTO.NoteImage;
import com.horical.appnote.DTO.NoteDTO.NoteSummary;
import com.horical.appnote.DTO.NoteDTO.NoteText;
import com.horical.appnote.DTO.NoteDTO.NoteVideoClip;
import com.horical.appnote.DTO.NoteDTO.NoteVoice;
import com.horical.appnote.Interfaces.MainInterface;
import com.horical.appnote.Interfaces.NoteDataLine;
import com.horical.appnote.Interfaces.OpenMediaInterface;
import com.horical.appnote.MyView.MyDialog.MediaChooserDialog;
import com.horical.appnote.MyView.NoteDataView.FormattedText;
import com.horical.appnote.MyView.NoteDataView.MyAudioView;
import com.horical.appnote.MyView.NoteDataView.MyVideoView;
import com.horical.appnote.MyView.NoteDataView.PairDataView;
import com.horical.appnote.R;
import com.horical.appnote.Supports.LanguageUtils;
import com.horical.appnote.Supports.MediaUtils;
import com.horical.appnote.Supports.SupportUtils;

/**
 * Created by Phuong on 30/07/2015.
 */
public class ViewNoteFragment extends BaseFragment implements ImageButton.OnClickListener, MediaChooserDialog.MediaCallback ,
        OpenMediaInterface
{

    private ArrayList<NoteDataLine> mListNoteLine;
    private LinearLayout mLayoutContainer, mLayoutProgress;
    private ArrayList<PairDataView> mListContent;

    private BaseActivity.ActivityAction mAction;
    private MainInterface mCallback;

    private NoteSummary mNoteSummary;

    private HashMap<String, Bitmap> mListBitmap;

    private AsyncTask<Void, View, String> mLoadNoteLinesAsync;
    private AsyncTask<Void, Void, Void> mLoadBitmapAsync;

    public ViewNoteFragment() {
        this.mLayout_xml_id = R.layout.fragment_view_note;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mActivity = activity;
        mNoteSummary = new NoteSummary();
        mNoteSummary = (NoteSummary) getArguments().getSerializable("NoteSummary");
        mAction = (BaseActivity.ActivityAction) activity;
        mCallback = (MainInterface) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NoteDataDAO mNoteDataDAO = new NoteDataDAO(mActivity);
        mListNoteLine = new ArrayList<>();
        mListNoteLine = mNoteDataDAO.loadNoteDetails(mNoteSummary.getID());
        mListContent = new ArrayList<>();
        mListBitmap = new HashMap<>();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mLayoutContainer = (LinearLayout) mFragmentView.findViewById(R.id.layoutContainer);
        mLayoutProgress = (LinearLayout) mFragmentView.findViewById(R.id.layoutProgress);
        mLayoutProgress.setVisibility(View.VISIBLE);

        this.initImage();

        String Json = mNoteSummary.getTitle();
        String Content = FormattedText.getContentFromFormatedJSON(Json);
        String Format = FormattedText.getFormatFromFormatedJSON(Json);
        ((TextView) mFragmentView.findViewById(R.id.textviewNoteTitle)).setText(FormattedText.getFormattedText(Content, Format));

        ImageButton mButtonBack = (ImageButton) mFragmentView.findViewById(R.id.buttonViewNote_Back);
        mButtonBack.setOnClickListener(this);
        ImageButton mButtonEdit = (ImageButton) mFragmentView.findViewById(R.id.buttonViewNote_Edit);
        mButtonEdit.setOnClickListener(this);

        final ViewGroup.LayoutParams layoutParams =
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        mLoadNoteLinesAsync = new AsyncTask<Void, View, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                String json;
                String content;
                String format;
                for (NoteDataLine noteLine : mListNoteLine) {
                    switch (noteLine.typeIdentify()) {
                        case DataConstant.TYPE_TEXT:
                            json = ((NoteText) noteLine).getContent();
                            content = FormattedText.getContentFromFormatedJSON(json);
                            format = FormattedText.getFormatFromFormatedJSON(json);
                            TextView textView = new TextView(mActivity);
                            textView.setLayoutParams(layoutParams);
                            textView.setText(FormattedText.getFormattedText(content, format));

                            mListContent.add(new PairDataView(noteLine, textView));

                            publishProgress(textView);
                            break;
                        case DataConstant.TYPE_IMAGE:
                            NoteImage noteImage = (NoteImage) noteLine;
                            Bitmap bitmap;
                            if (mListBitmap.containsKey(noteImage.getFilePath())) {
                                bitmap = mListBitmap.get(noteImage.getFilePath());
                            } else {
                                bitmap = BitmapFactory.decodeFile(noteImage.getFilePath());
                            }
                            ViewGroup.LayoutParams LayoutParams = SupportUtils.getScreenParams(mActivity);
                            int w = (bitmap.getWidth() > LayoutParams.width) ? LayoutParams.width : bitmap.getWidth();
                            int h = (bitmap.getHeight() > (LayoutParams.height / 2)) ?
                                    (int) SupportUtils.getHeightByWidth(bitmap.getWidth(), bitmap.getHeight(), w) : bitmap.getHeight();

                            Bitmap bmp = SupportUtils.resizeBitmap(bitmap, w, h);
                            ImageView imageView = new ImageView(mActivity);
                            imageView.setId(SupportUtils.getRandomID());
                            imageView.setImageBitmap(bmp);

                            mListContent.add(new PairDataView(new NoteImage(noteImage.getFileName(),
                                    DataConstant.TYPE_IMAGE, noteImage.getFilePath(), mNoteSummary.getID()), imageView));

                            publishProgress(imageView);
                            break;
                        case DataConstant.TYPE_VIDEOCLIP:
                            NoteVideoClip noteVideoClip = (NoteVideoClip) noteLine;

                            Bitmap video_thumbnail;
                            if (mListBitmap.containsKey(noteVideoClip.getFilePath())) {
                                video_thumbnail = mListBitmap.get(noteVideoClip.getFilePath());
                            } else {
                                video_thumbnail = BitmapFactory.decodeFile(noteVideoClip.getFilePath());
                            }
                            MyVideoView myVideoView = new MyVideoView(mActivity, noteVideoClip.getFilePath());
                            myVideoView.setImageView(video_thumbnail);
                            myVideoView.setTextViewName(noteVideoClip.getFileName());
                            myVideoView.setTextViewInfor(
                                    SupportUtils.MilliSecToTime(
                                            SupportUtils.getDuration(Uri.parse(noteVideoClip.getFilePath()), mActivity)));
                            myVideoView.setOpenFileListener(ViewNoteFragment.this);

                            myVideoView.hideDeleteButton();
                            myVideoView.hideDivider();

                            mListContent.add(new PairDataView(noteLine, myVideoView));

                            publishProgress(myVideoView.getView());
                            break;
                        case DataConstant.TYPE_VOICE:
                            NoteVoice noteVoice = (NoteVoice) noteLine;
                            MyAudioView myAudioView = new MyAudioView(mActivity, noteVoice.getFilePath());
                            myAudioView.setTextViewName(noteVoice.getFileName());
                            myAudioView.setTextViewInfo(
                                    SupportUtils.MilliSecToTime(
                                            SupportUtils.getDuration(Uri.parse(noteVoice.getFilePath()), mActivity)));
                            myAudioView.setOpenFileListener(ViewNoteFragment.this);

                            myAudioView.hideDeleteButton();
                            myAudioView.hideDivider();

                            mListContent.add(new PairDataView(noteLine, myAudioView));

                            publishProgress(myAudioView.getView());
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

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                mLayoutProgress.setVisibility(View.GONE);
            }
        };

    }

    @Override
    public void updateUI() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonViewNote_Back:
                mMainInterface.Back();
                return ;
            case R.id.buttonViewNote_Edit:
                NoteData noteData = new NoteData();
                noteData.setNoteSummary(mNoteSummary);
                noteData.setNoteData(mListNoteLine);
                mCallback.PassEditInfo(noteData);
                return;
            default:
                break;
        }
    }

    @Override
    public void OpenMedia(MediaChooserDialog.KindOfPlayer kindOfPlayer, String path) {
        switch (kindOfPlayer){
            case MEDIA_APPLICATION:
                MediaUtils.openMedia(path);
                break;
            case DEFAULT_PLAYER:
                mMainInterface.PlayMedia(path);
                break;
        }
    }

    private void initImage() {
        mLayoutProgress.setVisibility(View.VISIBLE);
        mLoadBitmapAsync = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                for (NoteDataLine line : mListNoteLine) {
                    switch (line.typeIdentify()) {
                        case DataConstant.TYPE_IMAGE:
                            NoteImage noteImage = (NoteImage) line;
                            mListBitmap.put(noteImage.getFilePath(), getSuitableSizeBitmap(noteImage.getFilePath()));
                            break;

                        case DataConstant.TYPE_VIDEOCLIP:
                            NoteVideoClip noteVideoClip = (NoteVideoClip) line;
                            mListBitmap.put(noteVideoClip.getFilePath(),
                                    ThumbnailUtils.createVideoThumbnail(
                                            noteVideoClip.getFilePath(), MediaStore.Video.Thumbnails.MICRO_KIND));
                            break;
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                mLoadNoteLinesAsync.execute();
                mLayoutProgress.setVisibility(View.GONE);
            }
        };
        mLoadBitmapAsync.execute();
    }

    private Bitmap resizeImage(Bitmap bitmap){
        ViewGroup.LayoutParams layoutParams = SupportUtils.getScreenParams(mActivity);
        int w = (bitmap.getWidth() > layoutParams.width)?layoutParams.width:bitmap.getWidth();
        int h = (bitmap.getHeight() > (layoutParams.height/2))?(layoutParams.height/2):bitmap.getHeight();
        return SupportUtils.resizeBitmap(bitmap, w, h);
    }

    private Bitmap getSuitableSizeBitmap(String path) {
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        ViewGroup.LayoutParams LayoutParams = SupportUtils.getScreenParams(mActivity);
        int w = (bitmap.getWidth() > LayoutParams.width) ? LayoutParams.width : bitmap.getWidth();
        int h = (bitmap.getHeight() > (LayoutParams.height / 2)) ?
                (int) SupportUtils.getHeightByWidth(bitmap.getWidth(), bitmap.getHeight(), w) : bitmap.getHeight();

        return SupportUtils.resizeBitmap(bitmap, w, h);
    }

    @Override
    public void OpenFile(String path) {
        MediaChooserDialog mediaChooserDialog = new MediaChooserDialog(mActivity);
        mediaChooserDialog.setMediaCallback(this);
        mediaChooserDialog.setTitle(LanguageUtils.getChooseMediaPlayerString());
        mediaChooserDialog.setMediaPath(path);
        mediaChooserDialog.show();
    }
}
