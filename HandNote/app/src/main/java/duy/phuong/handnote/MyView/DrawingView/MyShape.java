package duy.phuong.handnote.MyView.DrawingView;

import android.graphics.Point;
import android.graphics.Rect;

import java.util.ArrayList;

/**
 * Created by Phuong on 27/01/2016.
 */
public class MyShape {
    private ArrayList<MyPath> mListPaths;
    private Rect mRect;

    public MyShape(ArrayList<MyPath> mListPaths) {
        this.mListPaths = mListPaths;
        mRect = new Rect();
    }

    public MyShape() {
        mListPaths = new ArrayList<>();
        mRect = new Rect();
    }

    public ArrayList<MyPath> getListPaths() {
        return mListPaths;
    }

    public void setListPaths(ArrayList<MyPath> ListPaths) {
        this.mListPaths = ListPaths;
    }

    public void mergeRect() {
        int left = mListPaths.get(0).getRect().left;
        int right = mListPaths.get(0).getRect().right;
        int top = mListPaths.get(0).getRect().top;
        int bot = mListPaths.get(0).getRect().bottom;

        for (int i = 1; i < mListPaths.size(); i++) {
            Rect rect = mListPaths.get(i).getRect();
            if (rect.left < left) {
                left = rect.left;
            }
            if (rect.right > right) {
                right = rect.right;
            }
            if (rect.top < top) {
                top = rect.top;
            }
            if (rect.bottom > bot) {
                bot = rect.bottom;
            }
        }

        mRect = new Rect(left, top, right, bot);
    }

    public Rect getRect() {
        return mRect;
    }
}
