package nguyenhoang.duy.phuong.helicoptergame.GameComponents;

import android.graphics.Rect;

/**
 * Created by Phuong on 09/06/2015.
 */
public abstract class GameObject {
    protected int x;
    protected int y;
    protected double dx;
    protected double dy;
    protected int width;
    protected int height;

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Rect getRect(){
        return new Rect(x, y, x+width, y+height);
    }
}
