package duy.phuong.handnote.MyView.DrawingView;

import android.graphics.Point;
import android.graphics.Rect;

import java.util.ArrayList;

/**
 * Created by Phuong on 27/01/2016.
 */
public class MyShape {
    private ArrayList<MyPath> mListPaths;

    public MyShape(ArrayList<MyPath> mListPaths) {
        this.mListPaths = mListPaths;
    }

    public MyShape() {
        mListPaths = new ArrayList<>();
    }

    public ArrayList<MyPath> getListPaths() {
        return mListPaths;
    }

    public int getWidth() {
        ArrayList<MyPath> list = mListPaths;
        int minX = (list.isEmpty()) ? 0 : list.get(0).getListPoint().get(0).x;
        int maxX = minX;
        for (MyPath myPath : mListPaths) {
            for (Point point : myPath.getListPoint()) {
                if (minX > point.x) {
                    minX = point.x;
                }

                if (maxX < point.x) {
                    maxX = point.x;
                }
            }
        }

        return Math.abs(maxX - minX);
    }

    public int getHeight() {
        ArrayList<MyPath> list = mListPaths;
        int minY = (list.isEmpty()) ? 0 : list.get(0).getListPoint().get(0).y;
        int maxY = minY;
        for (MyPath myPath : mListPaths) {
            for (Point point : myPath.getListPoint()) {
                if (minY > point.y) {
                    minY = point.y;
                }

                if (maxY < point.y) {
                    maxY = point.y;
                }
            }
        }

        return Math.abs(maxY - minY);
    }

    public Rect getRect() {
        int left = mListPaths.get(0).getListPoint().get(0).x; int right = left;
        int top = mListPaths.get(0).getListPoint().get(0).y; int bot = top;
        for (MyPath myPath : mListPaths)
            for (Point point : myPath.getListPoint()) {
                if (left > point.x) {
                    left = point.x;
                }

                if (right < point.x) {
                    right = point.x;
                }
                if (top > point.y) {
                    top = point.y;
                }

                if (bot < point.y) {
                    bot = point.y;
                }
            }
        return new Rect(left, top, right, bot);
    }
}
