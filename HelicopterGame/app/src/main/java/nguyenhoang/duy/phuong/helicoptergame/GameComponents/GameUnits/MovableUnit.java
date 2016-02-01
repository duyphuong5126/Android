package nguyenhoang.duy.phuong.helicoptergame.GameComponents.GameUnits;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.Random;

import nguyenhoang.duy.phuong.helicoptergame.GameComponents.GameUtils.Animation;
import nguyenhoang.duy.phuong.helicoptergame.GameComponents.GameObject;

/**
 * Created by Phuong on 09/06/2015.
 */
public abstract class MovableUnit extends GameObject {
    protected int mScore;
    protected int mSpeed;
    protected Random mRandom = new Random();
    protected Animation mAnimate = new Animation();
    protected Bitmap mSpriteSheet;

    public MovableUnit(int x, int y, int w, int h) {
        super.x = x;
        super.y = y;
        width = w;
        height = h;
    }

    public abstract void initSprites(int s, int numFrames, Bitmap res);

    public void update() {
        x -= mSpeed;
        mAnimate.update();
    }

    public void draw(Canvas canvas) {
        try {
            canvas.drawBitmap(mAnimate.getImage(), x, y, null);
        } catch (Exception e) {
        }
    }

    @Override
    public int getWidth() {
        return width - 10;
    }

    public void setSpeed(int speed) {
        this.mSpeed = speed;
    }
}
