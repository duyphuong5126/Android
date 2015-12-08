package com.horical.appnote.DTO.FileDTO;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;

import com.horical.appnote.DTO.BaseDTO;
import com.horical.appnote.LocalStorage.DataConstant;
import com.horical.appnote.Interfaces.DataInterface;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Phuong on 29/07/2015.
 */
public class FileData extends BaseDTO implements DataInterface<FileData> {
    private String mFileName;
    private String mDescirption;
    private String mFilePath;
    private String mFileType;

    private Bitmap mThumbnail;

    public FileData(String fileName) {
        this.mFileName = fileName;
    }

    public FileData(String FileName, String Descirption, String FilePath, String FileType) {
        this.mFileName = FileName;
        this.mDescirption = Descirption;
        this.mFilePath = FilePath;
        this.mFileType = FileType;
        this.mThumbnail = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(mFilePath), 96, 96);
    }

    public String getFileName() {
        return mFileName;
    }

    public String getDescirption() {
        return mDescirption;
    }

    public String getFilePath() {
        return mFilePath;
    }
    public Bitmap getThumbnail() {
        return mThumbnail;
    }

    public Bitmap getFileThumbnail() {
        if (mFileType.equals(DataConstant.TYPE_IMAGE)) {
            return BitmapFactory.decodeFile(this.mFilePath);
        } else {
            if (mFileType.equals(DataConstant.TYPE_VIDEOCLIP)) {
                return ThumbnailUtils.createVideoThumbnail(this.mFilePath, MediaStore.Video.Thumbnails.MINI_KIND);
            } else {
                return null;
            }
        }
    }

    @Override
    public int checkTypeDTO() {
        return BaseDTO.FILE_OBJECT;
    }

    @Override
    public Object parse(JSONObject jsonObject) throws JSONException {
        return null;
    }


    @Override
    public String createJSON() {
        return null;
    }

    @Override
    public int compare(FileData file) {
        if (this.mFileName.equals(file.getFileName())) {
            return 0;
        } else {
            int length = (mFileName.length() >= file.getFileName().length())?file.getFileName().length():mFileName.length();
            for (int i = 0; i < length; i++) {
                if (mFileName.charAt(i) > file.getFileName().charAt(i)) {
                    return 1;
                }
                if (mFileName.charAt(i) < file.getFileName().charAt(i)) {
                    return -1;
                }
            }
            return (mFileName.length() > file.getFileName().length())?1:-1;
        }
    }
}
