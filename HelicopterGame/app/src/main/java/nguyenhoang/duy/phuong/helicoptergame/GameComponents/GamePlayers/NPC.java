package nguyenhoang.duy.phuong.helicoptergame.GameComponents.GamePlayers;

import android.graphics.Bitmap;

import nguyenhoang.duy.phuong.helicoptergame.GameComponents.GamePanel;

/**
 * Created by Phuong on 09/06/2015.
 */
public class NPC extends Enemy {
    private int mSpeed;
    private int mPosition;

    public NPC(int w, int h, int positionX, int positionY) {
        super(w, h);
        this.x = positionX;
        this.y = positionY;
        this.mPosition = positionY / 160;
        this.Disapear = true;
    }

    @Override
    public void initSprites(Bitmap res, int numFrames) {
        Bitmap[] images = new Bitmap[numFrames];
        mSpriteSheet = res;
        for (int i = 0; i < images.length; i++) {
            images[i] = Bitmap.createBitmap(mSpriteSheet, i * width, 0, width, height);
        }

        mAnimate.setFrames(images);
        mAnimate.setDelay(20);
        startTime = System.nanoTime();
    }

    @Override
    public void update(GamePanel gamePanel) {
        x -= mSpeed;
        mAnimate.update();
    }

    public int getPosition() {
        return mPosition;
    }

    public void setSpeed(int speed) {
        this.mSpeed = speed;
    }

    public int getSpeed() {
        return mSpeed;
    }
}
