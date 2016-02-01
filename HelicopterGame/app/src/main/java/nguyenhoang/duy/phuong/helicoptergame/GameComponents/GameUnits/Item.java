package nguyenhoang.duy.phuong.helicoptergame.GameComponents.GameUnits;

import android.graphics.Bitmap;

/**
 * Created by Phuong on 09/06/2015.
 */
public class Item extends MovableUnit {
    private int mKind;
    public Item(int x, int y, int w, int h, int Kind) {
        super(x, y, w, h);
        this.mKind = Kind;
    }
    @Override
    public void initSprites(int s, int numFrames, Bitmap res) {
        mSpeed = 10;
        Bitmap[] images = new Bitmap[numFrames];
        mSpriteSheet = res;

        if(this.mKind == 3){
            for(int i=0; i<images.length; i++){
                images[i] = Bitmap.createBitmap(mSpriteSheet, i*width, 0, width, height);
            }
        }
        else{
            for(int i=0; i<images.length; i++){
                images[i] = Bitmap.createBitmap(mSpriteSheet, 128, 224, width, height);
            }
        }
        mAnimate.setFrames(images);
        mAnimate.setDelay(100);
    }

    public int getKind() {
        return mKind;
    }
}
