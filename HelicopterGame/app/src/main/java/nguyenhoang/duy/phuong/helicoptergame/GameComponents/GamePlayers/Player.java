package nguyenhoang.duy.phuong.helicoptergame.GameComponents.GamePlayers;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import nguyenhoang.duy.phuong.helicoptergame.GameComponents.GameUtils.Animation;
import nguyenhoang.duy.phuong.helicoptergame.GameComponents.GameObject;
import nguyenhoang.duy.phuong.helicoptergame.GameComponents.GamePanel;

/**
 * Created by Phuong on 09/06/2015.
 */
public class Player extends GameObject {
    protected Bitmap mSpriteSheet;
    protected int mScore;
    protected boolean Up;
    protected boolean Playing;
    protected Animation mAnimate = new Animation();
    protected long mStartTime;
    protected boolean Disappear;
    private int mStatus;
    private int mLife;

    public Player(int w, int h) {
        dy = 0;
        mScore = 0;
        height = h;
        width = w;
        mStatus = 0;
        mLife = 1;
    }

    public void initSprites(Bitmap res, int numFrames) {
        Bitmap[] images = new Bitmap[numFrames];
        mSpriteSheet = res;
        for (int i = 0; i < images.length; i++) {
            images[i] = Bitmap.createBitmap(mSpriteSheet, i * width, 0, width, height);
        }
        mAnimate.setFrames(images);
        mAnimate.setDelay(10);
        mStartTime = System.nanoTime();
    }

    public void setUp(boolean up) {
        this.Up = up;
    }

    public void update(GamePanel gamePanel) {
        long elapsed = (System.nanoTime() - mStartTime) / 1000000;
        if (elapsed > 100) {
            mScore++;
            mStartTime = System.nanoTime();
        }
        mAnimate.update();

        if (Up) {
            dy -= 0.35d;
        } else {
            dy += 0.35d;
        }
        if (dy > 14) dy = 14;
        if (dy < -14) dy = -14;

        y += dy * 2;
        if (y > gamePanel.getHeight() - height) y = gamePanel.getHeight() - height;
        if (y < 10) y = 10;
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(mAnimate.getImage(), x, y, null);
    }

    public int getScore() {
        return mScore;
    }

    public boolean isPlaying() {
        return Playing;
    }

    public void setPlaying(boolean playing) {
        this.Playing = playing;
    }

    public void resetScore() {
        mScore = 0;
        mStatus = 0;
    }

    public void resetDY() {
        dy = 0;
    }

    public void setScore(int score) {
        this.mScore = score;
    }

    public boolean isDisappear() {
        return Disappear;
    }

    public void setDisappear(boolean disappear) {
        this.Disappear = disappear;
    }

    public void setStatus(int status) {
        this.mStatus = status;
    }

    public int getStatus() {
        return mStatus;
    }

    public int getLife() {
        return mLife;
    }

    public void setLife(int life) {
        mLife = life;
    }
}
