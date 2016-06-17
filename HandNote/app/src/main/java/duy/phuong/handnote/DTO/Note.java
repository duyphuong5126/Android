package duy.phuong.handnote.DTO;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by Phuong on 16/04/2016.
 */
public class Note implements Serializable {
    public Bitmap mImage;
    public String mBitmapPath;
    public String mContent;
    public String mContentPath;
    public boolean Focused;
}
