package nguyenhoang.duy.phuong.helicoptergame.GameComponents.GameUnits;

import android.graphics.Bitmap;

/**
 * Created by Phuong on 09/06/2015.
 */
public class Missile extends MovableUnit {
    private boolean Live;

    public Missile(int x, int y, int w, int h) {
        super(x, y, w, h);
        this.Live = true;
    }

    @Override
    public void initSprites(int s, int numFrames, Bitmap res) {
        mScore = s;
        mSpeed = 5 + mRandom.nextInt(10);
        //cap missile mSpeed
        if (mSpeed > 35) {
            mSpeed = 35;
        }
        Bitmap[] images = new Bitmap[numFrames];
        mSpriteSheet = res;
        for (int i = 0; i < images.length; i++) {
            images[i] = Bitmap.createBitmap(mSpriteSheet, 0, i * height, width, height);
        }
        mAnimate.setFrames(images);
        mAnimate.setDelay(100 - mSpeed);
    }

    public boolean isLive() {
        return Live;
    }

    public void setLive(boolean live) {
        Live = live;
    }
}

