package nguyenhoang.duy.phuong.helicoptergame.GameComponents.GameUnits;

import android.graphics.Bitmap;

/**
 * Created by Phuong on 09/06/2015.
 */
public class Gold extends MovableUnit {
    public Gold(int x, int y, int w, int h) {
        super(x, y, w, h);
    }

    @Override
    public void initSprites(int s, int numFrames, Bitmap res) {
        mSpeed = 10;
        Bitmap[] images = new Bitmap[numFrames];
        mSpriteSheet = res;

        for(int i=0; i<images.length; i++){
            images[i] = Bitmap.createBitmap(mSpriteSheet, i*width, 0, width, height);
        }
        mAnimate.setFrames(images);
        mAnimate.setDelay(100);
    }


}
