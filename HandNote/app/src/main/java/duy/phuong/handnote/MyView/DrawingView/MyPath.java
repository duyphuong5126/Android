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

    public MyPath(ArrayList<Point> ListPoint) {
        this.mListPoint = ListPoint;
        mChecked = false;
        mSettled = false;
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

    private void initPath(Path path, ArrayList<Point> points) {
        boolean first = true;
        if (points != null && !points.isEmpty()) {
            if (points.size() % 2 == 0) {
                if (points.size() % 4 == 0) {
                    for (int i = 0; i < points.size(); i += 4) {
                        Point point = points.get(i);
                        if (first) {
                            first = false;
                            path.moveTo(point.x, point.y);
                        } else {
                            path.lineTo(point.x, point.y);
                        }
                        point = points.get(i + 1);
                        path.lineTo(point.x, point.y);
                        point = points.get(i + 2);
                        path.lineTo(point.x, point.y);
                        point = points.get(i + 3);
                        path.lineTo(point.x, point.y);
                    }
                    return;
                }

                if (points.size() % 6 == 0) {
                    for (int i = 0; i < points.size(); i += 6) {
                        Point point = points.get(i);
                        if (first) {
                            first = false;
                            path.moveTo(point.x, point.y);
                        } else {
                            path.lineTo(point.x, point.y);
                        }
                        point = points.get(i + 1);
                        path.lineTo(point.x, point.y);
                        point = points.get(i + 2);
                        path.lineTo(point.x, point.y);
                        point = points.get(i + 3);
                        path.lineTo(point.x, point.y);
                        point = points.get(i + 4);
                        path.lineTo(point.x, point.y);
                        point = points.get(i + 5);
                        path.lineTo(point.x, point.y);
                    }
                    return;
                }
                for (int i = 0; i < points.size(); i += 2) {
                    Point point = points.get(i);
                    if (first) {
                        first = false;
                        path.moveTo(point.x, point.y);
                    } else {
                        path.lineTo(point.x, point.y);
                    }
                    point = points.get(i + 1);
                    path.lineTo(point.x, point.y);
                }
            } else {
                if (points.size() % 3 == 0) {
                    for (int i = 0; i < points.size(); i += 3) {
                        Point point = points.get(i);
                        if (first) {
                            first = false;
                            path.moveTo(point.x, point.y);
                        } else {
                            path.lineTo(point.x, point.y);
                        }
                        point = points.get(i + 1);
                        path.lineTo(point.x, point.y);
                        point = points.get(i + 2);
                        path.lineTo(point.x, point.y);
                    }
                    return;
                }

                if (points.size() % 5 == 0) {
                    for (int i = 0; i < points.size(); i += 5) {
                        Point point = points.get(i);
                        if (first) {
                            first = false;
                            path.moveTo(point.x, point.y);
                        } else {
                            path.lineTo(point.x, point.y);
                        }
                        point = points.get(i + 1);
                        path.lineTo(point.x, point.y);
                        point = points.get(i + 2);
                        path.lineTo(point.x, point.y);
                        point = points.get(i + 3);
                        path.lineTo(point.x, point.y);
                        point = points.get(i + 4);
                        path.lineTo(point.x, point.y);
                    }
                    return;
                }

                if (points.size() % 7 == 0) {
                    for (int i = 0; i < points.size(); i += 7) {
                        Point point = points.get(i);
                        if (first) {
                            first = false;
                            path.moveTo(point.x, point.y);
                        } else {
                            path.lineTo(point.x, point.y);
                        }
                        point = points.get(i + 1);
                        path.lineTo(point.x, point.y);
                        point = points.get(i + 2);
                        path.lineTo(point.x, point.y);
                        point = points.get(i + 3);
                        path.lineTo(point.x, point.y);
                        point = points.get(i + 4);
                        path.lineTo(point.x, point.y);
                        point = points.get(i + 5);
                        path.lineTo(point.x, point.y);
                        point = points.get(i + 6);
                        path.lineTo(point.x, point.y);
                    }
                    return;
                }
                for (int i = 0; i < points.size(); i++) {
                    Point point = points.get(i);
                    if (first) {
                        first = false;
                        path.moveTo(point.x, point.y);
                    } else {
                        path.lineTo(point.x, point.y);
                    }
                }
            }
        }
    }

    public boolean isIntersect(MyPath myPath, int width, int height, Paint tempPaint) {
        if (myPath != null) {
            Path path1 = new Path();
            initPath(path1, mListPoint);
            Path path2 = new Path();
            initPath(path2, myPath.getListPoint());

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
