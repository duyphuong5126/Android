package com.horical.appnote.MyView.MyAdapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.horical.appnote.LocalStorage.DataConstant;
import com.horical.appnote.Interfaces.OpenMediaInterface;
import com.horical.appnote.R;
import com.horical.appnote.DTO.FileDTO.FileData;
import com.horical.appnote.Supports.SupportUtils;

/**
 * Created by Phuong on 29/07/2015.
 */
public class ListFileAdapter extends ArrayAdapter<FileData> {
    private List<FileData> mListFile;
    private Activity mActivity;
    private String mCurrentFileType;
    private HashMap<Integer, String> mListGroupItem;
    String mFlag = "";

    private View mHeaderView;

    private OpenMediaInterface mOpenMediaInterface;

    public void setOpenMediaInterface(OpenMediaInterface openMediaInterface) {
        mOpenMediaInterface = openMediaInterface;
    }

    public ListFileAdapter(Activity activity, int resource, ArrayList<FileData> listFile, String currentFileType) {
        super(activity, resource, listFile);
        this.mListFile = listFile;
        this.mActivity = activity;
        this.mCurrentFileType = currentFileType;
        this.mListGroupItem = new HashMap<Integer, String>();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = mActivity.getLayoutInflater();
        FileData fileData = mListFile.get(position);
        char c = mListFile.get(position).getFileName().charAt(0);
        if (!SupportUtils.isAlphabet(c)) {
            if (!mFlag.contains("#")) {
                mFlag += "#";
                mListGroupItem.put(position, "#");
            }
        } else {
            String s = String.valueOf(c).toUpperCase();
            if (!mFlag.contains(s)) {
                mFlag += s;
                mListGroupItem.put(position, s);
            }
        }

        switch (mCurrentFileType){
            case DataConstant.TYPE_IMAGE:
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.list_item_file_image, parent, false);
                }
                Bitmap thumbnail = fileData.getThumbnail();
                if (thumbnail != null) {
                    ((ImageView) convertView.findViewById(R.id.FileThumbnail)).setImageBitmap(thumbnail);
                }
                ((TextView) convertView.findViewById(R.id.GroupText)).setText("");
                ((LinearLayout) convertView.findViewById(R.id.layoutDivider)).setVisibility(View.GONE);
                for (Map.Entry<Integer, String> entry : mListGroupItem.entrySet()) {
                    if (position == entry.getKey()) {
                        ((TextView) convertView.findViewById(R.id.GroupText)).setText(entry.getValue());
                        ((LinearLayout) convertView.findViewById(R.id.layoutDivider)).setVisibility(View.VISIBLE);
                    }
                }
                if (position == 0) {
                    ((LinearLayout) convertView.findViewById(R.id.layoutDivider)).setVisibility(View.GONE);
                }
                ((TextView) convertView.findViewById(R.id.FileName)).setText(SupportUtils.getShortName(fileData.getFileName()));
                ((TextView) convertView.findViewById(R.id.FileInfor)).setText(SupportUtils.checkFileSize(fileData.getFilePath()));
                break;
            case DataConstant.TYPE_VIDEOCLIP:
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.list_item_file_video, parent, false);
                }
                Bitmap video_thumbnail = ThumbnailUtils.createVideoThumbnail(fileData.getFilePath(), MediaStore.Video.Thumbnails.MICRO_KIND);
                ImageButton buttonVideoPlayer = (ImageButton) convertView.findViewById(R.id.buttonPlayVideo);
                buttonVideoPlayer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mOpenMediaInterface.OpenFile(mListFile.get(position).getFilePath());
                    }
                });
                ((TextView) convertView.findViewById(R.id.GroupText)).setText("");
                ((LinearLayout) convertView.findViewById(R.id.layoutDivider)).setVisibility(View.GONE);
                for (Map.Entry<Integer, String> entry : mListGroupItem.entrySet()) {
                    if (position == entry.getKey()) {
                        ((TextView) convertView.findViewById(R.id.GroupText)).setText(entry.getValue());
                        ((LinearLayout) convertView.findViewById(R.id.layoutDivider)).setVisibility(View.VISIBLE);
                    }
                }
                if (position == 0) {
                    ((LinearLayout) convertView.findViewById(R.id.layoutDivider)).setVisibility(View.GONE);
                }
                ((ImageView) convertView.findViewById(R.id.FileThumbnail)).setImageBitmap(video_thumbnail);
                ((TextView) convertView.findViewById(R.id.FileName)).setText(SupportUtils.getShortName(fileData.getFileName()));
                ((TextView) convertView.findViewById(R.id.FileInfor)).setText(fileData.getDescirption());
                ((LinearLayout) convertView.findViewById(R.id.layoutButtonDelete)).setVisibility(View.GONE);
                break;
            case DataConstant.TYPE_VOICE:
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.list_item_file_audio, parent, false);
                }
                ImageButton buttonAudioPlayer = (ImageButton) convertView.findViewById(R.id.buttonPlayAudio);
                buttonAudioPlayer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mOpenMediaInterface.OpenFile(mListFile.get(position).getFilePath());
                    }
                });
                ((TextView) convertView.findViewById(R.id.GroupText)).setText("");
                ((LinearLayout) convertView.findViewById(R.id.layoutDivider)).setVisibility(View.GONE);
                for (Map.Entry<Integer, String> entry : mListGroupItem.entrySet()) {
                    if (position == entry.getKey()) {
                        ((TextView) convertView.findViewById(R.id.GroupText)).setText(entry.getValue());
                        ((LinearLayout) convertView.findViewById(R.id.layoutDivider)).setVisibility(View.VISIBLE);
                    }
                }
                if (position == 0) {
                    ((LinearLayout) convertView.findViewById(R.id.layoutDivider)).setVisibility(View.GONE);
                }
                ((TextView) convertView.findViewById(R.id.FileName)).setText(SupportUtils.getShortName(fileData.getFileName()));
                ((TextView) convertView.findViewById(R.id.FileInfor)).setText(fileData.getDescirption());
                ((LinearLayout) convertView.findViewById(R.id.layoutButtonDelete)).setVisibility(View.GONE);
                break;
            default:
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.list_file_item_other, parent, false);
                }
                break;
        }
        return convertView;
    }
}
