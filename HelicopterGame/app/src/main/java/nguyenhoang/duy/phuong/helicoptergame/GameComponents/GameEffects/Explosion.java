package nguyenhoang.duy.phuong.helicoptergame.GameComponents.GameEffects;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import nguyenhoang.duy.phuong.helicoptergame.GameComponents.GameUtils.Animation;

/**
 * Created by Phuong on 09/06/2015.
 */
public class Explosion {
    private int mX;
    private int mY;
    private int mWidth;
    private int mHeight;
    private int mRow;
    private Animation mAnimate = new Animation();
    private Bitmap mSpriteSheet;
    private boolean Move;

    public Explosion(Bitmap res, int x, int y, int w, int h, int numFrames){
        this.mX = x;
        this.mY = y;

        this.mWidth = w;
        this.mHeight = h;

        this.Move = false;

        Bitmap[] images = new Bitmap[numFrames];
        mSpriteSheet = res;

        for(int i=0; i<images.length; i++){
            if(i%5==0&&i> mRow) mRow++;
            images[i] = Bitmap.createBitmap(mSpriteSheet, (i-(5* mRow))* mWidth, mRow * mHeight, mWidth, mHeight);
        }
        mAnimate.setFrames(images);
        mAnimate.setDelay(10);
    }
    public void draw(Canvas canvas){
        if(!mAnimate.isPlayOnce()){
            canvas.drawBitmap(mAnimate.getImage(), mX, mY, null);
        }
    }
    public void update(){
        if(!mAnimate.isPlayOnce()){
            mAnimate.update();
        }
        if(Move) mX -= 30;
    }
    public boolean isDisappear(){
        return mAnimate.isPlayOnce();
    }

    public int getHeight() {
        return mHeight;
    }

    public void setMove(boolean move) {
        this.Move = move;
    }
}
