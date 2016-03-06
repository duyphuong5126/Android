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

/**
 * Created by Phuong on 27/01/2016.
 */
public class MyPath {
    private ArrayList<Point> mListPoint;
    private Rect mRect;
    private boolean mChecked;

    public MyPath(ArrayList<Point> ListPoint) {
        this.mListPoint = ListPoint;
        mChecked = false;
    }

    public boolean isChecked() {
        return mChecked;
    }

    public void setChecked(boolean Checked) {
        this.mChecked = Checked;
    }

    public ArrayList<Point> getListPoint() {
        return mListPoint;
    }

    public void setListPoint(ArrayList<Point> ListPoint) {
        this.mListPoint = ListPoint;
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

    public boolean isIntersect(MyPath myPath, int width, int height, Paint tempPaint) {
        if (myPath != null) {
            Path path1 = new Path();
            Path path2 = new Path();
            boolean first = true;

            for (Point point : myPath.getListPoint()) {
                if (first) {
                    first = false;
                    path1.moveTo(point.x, point.y);
                } else {
                    path1.lineTo(point.x, point.y);
                }
            }

            first = true;

            for (Point point : mListPoint) {
                if (first) {
                    first = false;
                    path2.moveTo(point.x, point.y);
                } else {
                    path2.lineTo(point.x, point.y);
                }
            }

            Bitmap bmp1 = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas c1 = new Canvas(bmp1);
            c1.drawColor(Color.WHITE);
            c1.drawPath(path1, tempPaint);
            Bitmap bmp2 = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas c2 = new Canvas(bmp2);
            c2.drawColor(Color.WHITE);
            c2.drawPath(path2, tempPaint);

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
                        if (bmp1.getPixel(i, j) == Color.BLACK && bmp2.getPixel(i, j) == Color.BLACK) {
                            return true;
                        }
                    }
            }
        }

        return false;
    }
}
