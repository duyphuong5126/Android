package com.horical.appnote.Supports;

import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * Created by dutn on 04/08/2015.
 */
public class GestureListener extends GestureDetector.SimpleOnGestureListener {

    private Callback mCallback;


    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        float x1 = e1.getX();
        float x2 = e2.getX();
        if (x1 < x2 - 50) {
            mCallback.previous();
        } else if (x1 > x2 + 50) {
            mCallback.next();
        }
        return false;
    }

    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }

    public interface Callback {

        void previous();

        void next();

    }

}
