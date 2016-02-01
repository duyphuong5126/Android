package nguyenhoang.duy.phuong.helicoptergame.GameComponents.GameUnits;

import android.graphics.Bitmap;

/**
 * Created by Phuong on 09/06/2015.
 */
public class Bullet extends MovableUnit {
    private boolean Live;
    private boolean Direction;
    public Bullet(int x, int y, int w, int h, boolean Live, boolean direction) {
        super(x, y, w, h);
        this.Live = Live;
        this.Direction = direction;
    }

    @Override
    public void initSprites(int s, int numFrames, Bitmap res) {
        mScore = s;
        mSpeed = 30;
        Bitmap[] images = new Bitmap[numFrames];
        mSpriteSheet = res;

        int offset = 135;
        if(!Direction) offset = 10;

        for(int i=0; i<images.length; i++){
            images[i] = Bitmap.createBitmap(mSpriteSheet, i*width, offset, width, height);
        }
        mAnimate.setFrames(images);
        mAnimate.setDelay((mAnimate.getCurrentFrame() == 0)?2000:0);
    }

    @Override
    public void update() {
        if(Direction) x += mSpeed;
        if(!Direction) x -= mSpeed;
        mAnimate.update();
    }

    public boolean isLive() {
        return Live;
    }

    public void setLive(boolean live) {
        Live = live;
    }
}
