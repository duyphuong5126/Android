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
}
