package nguyenhoang.duy.phuong.helicoptergame.GameComponents.GameUtils;

import android.graphics.Bitmap;

/**
 * Created by Phuong on 29/04/2015.
 */
public class Animation {
    private Bitmap[] mFrames;
    private int mCurrentFrame;
    private long mStartTime;
    private long mDelay;
    private boolean PlayOnce;

    public void setFrames(Bitmap[] frames) {
        this.mFrames = frames;
        mCurrentFrame = 0;
        mStartTime = System.nanoTime();
    }

    public void setDelay(long delay) {
        this.mDelay = delay;
    }

    public void setCurrentFrames(int currentFrames) {
        this.mCurrentFrame = currentFrames;
    }

    public void update() {
        long elapsed = (System.nanoTime() - mStartTime) / 1000000;
        if (elapsed > mDelay) {
            mCurrentFrame++;
            mStartTime = System.nanoTime();
        }
        if (mCurrentFrame == mFrames.length) {
            mCurrentFrame = 0;
            PlayOnce = true;
        }
    }

    public Bitmap getImage() {
        return mFrames[mCurrentFrame];
    }

    public int getCurrentFrame() {
        return mCurrentFrame;
    }

    public boolean isPlayOnce() {
        return PlayOnce;
    }
}
