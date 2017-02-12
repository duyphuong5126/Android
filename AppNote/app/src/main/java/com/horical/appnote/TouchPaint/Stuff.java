package com.horical.appnote.TouchPaint;

import android.graphics.Paint;
import android.graphics.Path;

/**
 * Created by Phuong on 16/07/2015.
 */
class Stuff {
    private Paint paint;
    private Path path;
    private boolean Visible;
    Stuff(){
        paint = new Paint();
        path = new Path();
        Visible = true;
    }

    void setPaint(Paint paint) {
        this.paint = paint;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    Paint getPaint() {
        return paint;
    }

    public Path getPath() {
        return path;
    }

    public boolean isVisible() {
        return Visible;
    }

    public void setVisible(boolean visible) {
        Visible = visible;
    }
}
