package duy.phuong.handnote.MyView.DrawingView;

import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Region;

import java.util.ArrayList;

import duy.phuong.handnote.Support.SupportUtils;

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

    public boolean isOverlay(MyPath path) {
        return mRect.intersect(path.getRect());
    }

    public boolean isIntersect(MyPath myPath, int width, int height) {
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
        return SupportUtils.checkIntersect(path1, path2, width, height);
    }
}
