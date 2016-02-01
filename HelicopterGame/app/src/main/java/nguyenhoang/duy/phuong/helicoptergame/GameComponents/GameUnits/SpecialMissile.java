package nguyenhoang.duy.phuong.helicoptergame.GameComponents.GameUnits;

import android.graphics.Bitmap;

/**
 * Created by Phuong on 09/06/2015.
 */
public class SpecialMissile extends MovableUnit {
    private String mType;
    private int mNew;
    private boolean Live;
    public SpecialMissile(int x, int y, int w, int h, String type, int speed) {
        super(x, y, w, h);
        mNew = 0;
        this.mType = type;
        this.mSpeed = speed;
        Live = true;
    }

    @Override
    public void initSprites(int s, int numFrames, Bitmap res) {
        mScore = s;
        //cap missile mSpeed
        if(mSpeed >50) mSpeed = 50;
        Bitmap[] images = new Bitmap[numFrames];
        mSpriteSheet = res;

        int posY = mRandom.nextInt(3);

        for(int i=0; i<images.length; i++){
            images[i] = Bitmap.createBitmap(mSpriteSheet, i*width, posY*height, width, height);
        }
        mAnimate.setFrames(images);
        mAnimate.setDelay(100- mSpeed);
    }

    public void setType(String type) {
        this.mType = type;
    }

    @Override
    public void update() {
        x -= (mSpeed +10);
        y += (mType.equals("Normal"))?0:((mType.equals("Top_Left"))? mSpeed :-mSpeed);
    }

    public void setNew(int New) {
        this.mNew = New;
    }
    public int getNew(){
        return mNew;
    }

    public boolean isLive() {
        return Live;
    }

    public void setLive(boolean live) {
        Live = live;
    }
}
