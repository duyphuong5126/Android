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

    public static final String RIGHT = "RIGHT";
    public static final String LEFT = "LEFT";
    public static final String UP = "UP";
    public static final String DOWN = "DOWN";
    public static final String RIGHT_UP = "RIGHT_UP";
    public static final String RIGHT_DOWN = "RIGHT_DOWN";
    public static final String LEFT_UP = "LEFT_UP";
    public static final String LEFT_DOWN = "LEFT_DOWN";
    public static final String STABLE = "STABLE";

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

    public String[] getDirectedChildPaths() {
        String[] strings = new String[mListPoint.size() - 1];
        for (int i = 0; i < mListPoint.size() && i + 1 < mListPoint.size(); i++) {
            strings[i] = checkDirection(mListPoint.get(i), mListPoint.get(i + 1));
        }
        return strings;
    }

    public String checkDirection(Point point1, Point point2) {
        if (point1.x == point2.x) {
            if (point1.y == point2.y) {
                return STABLE;
            } else {
                return (point1.y > point2.y) ? UP : DOWN;
            }
        } else {
            if (point1.x < point2.x) {
                if (point1.y == point2.y) {
                    return RIGHT;
                } else {
                    return (point1.y > point2.y) ? RIGHT_UP : RIGHT_DOWN;
                }
            } else {
                if (point1.y == point2.y) {
                    return LEFT;
                } else {
                    return (point1.y > point2.y) ? LEFT_UP : LEFT_DOWN;
                }
            }
        }
    }
}
