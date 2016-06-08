package duy.phuong.handnote.MyView.DrawingView;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Phuong on 27/01/2016.
 */
public class MyPath {
    private ArrayList<Point> mListPoint;
    private Rect mRect;
    private boolean mChecked;
    private boolean mSettled;
    private int mColor;
    private Bitmap mBitmap;
    public Canvas mCanvas;

    public MyPath(ArrayList<Point> ListPoint) {
        this.mListPoint = ListPoint;
        mChecked = false;
        mSettled = false;
    }

    public void createBitmap(int w, int h) {
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int mColor) {
        this.mColor = mColor;
    }

    public boolean isChecked() {
        return mChecked;
    }

    public void setChecked(boolean Checked) {
        this.mChecked = Checked;
    }

    public boolean isSettled() {
        return mSettled;
    }

    public void setSettled(boolean Settled) {
        this.mSettled = Settled;
    }

    public ArrayList<Point> getListPoint() {
        return mListPoint;
    }

    public Rect getRect() {
        return mRect;
    }

    public void initRect() {
        int left = mListPoint.get(0).x;
        int right = mListPoint.get(0).x;
        int top = mListPoint.get(0).y;
        int bot = mListPoint.get(0).y;

        for (int i = 1; i < mListPoint.size(); i++) {
            Point point = mListPoint.get(i);
            if (point.x < left) {
                left = point.x;
            }
            if (point.x > right) {
                right = point.x;
            }
            if (point.y < top) {
                top = point.y;
            }
            if (point.y > bot) {
                bot = point.y;
            }
        }

        mRect = new Rect(left, top, right, bot);
    }

    public boolean isIntersect(MyPath myPath) {
        if (myPath != null) {
            Bitmap bitmap = myPath.getBitmap();
            if (mBitmap.getWidth() == bitmap.getWidth() && mBitmap.getHeight() == bitmap.getHeight()) {
                int width = bitmap.getWidth(), height = bitmap.getHeight();
                if (mRect != null && myPath.getRect() != null) {
                    int top = (mRect.top < myPath.getRect().top) ? mRect.top : myPath.getRect().top;
                    top = (top < 0) ? 0 : top;
                    int right = (mRect.right > myPath.getRect().right) ? mRect.right : myPath.getRect().right;
                    right = (right > width - 1) ? width - 1 : right;
                    int bot = (mRect.bottom > myPath.getRect().bottom) ? mRect.bottom : myPath.getRect().bottom;
                    bot = (bot > height - 1) ? height - 1 : bot;
                    int left = (mRect.left < myPath.getRect().left) ? mRect.left : myPath.getRect().left;
                    left = (left < 0) ? 0 : left;

                    for (int i = left; i <= right; i++)
                        for (int j = top; j <= bot; j++) {
                            if (mBitmap.getPixel(i, j) == Color.BLACK && bitmap.getPixel(i, j) == Color.BLACK) {
                                return true;
                            }
                        }
                }
            }
        }

        return false;
    }
}
