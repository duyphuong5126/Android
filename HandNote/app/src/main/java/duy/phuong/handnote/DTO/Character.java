package duy.phuong.handnote.DTO;

import android.graphics.Bitmap;
import android.graphics.Rect;

import java.util.ArrayList;

import duy.phuong.handnote.MyView.DrawingView.MyShape;

/**
 * Created by Phuong on 14/03/2016.
 */
public class Character {
    public MyShape mMyShape;
    public Bitmap mBitmap;
    public int mParentWidth;
    public int mParentHeight;
    public ArrayList<Bitmap> mListContours;
    public Rect mRect;
    public ArrayList<Rect> mListRectContour;
}
