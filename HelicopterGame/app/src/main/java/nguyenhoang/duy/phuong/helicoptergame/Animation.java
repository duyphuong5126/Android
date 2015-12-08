package nguyenhoang.duy.phuong.helicoptergame;

import android.graphics.Bitmap;

/**
 * Created by Phuong on 29/04/2015.
 */
public class Animation {
    private Bitmap[] frames;
    private int currentFrames;
    private long startTime;
    private long delay;
    private boolean playOnce;

    public void setFrames(Bitmap[] frames) {
        this.frames = frames;
        currentFrames = 0;
        startTime = System.nanoTime();
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public void setCurrentFrames(int currentFrames) {
        this.currentFrames = currentFrames;
    }

    public void update(){
        long eslapsed = (System.nanoTime()-startTime)/1000000;
        if(eslapsed>delay){
            currentFrames++;
            startTime = System.nanoTime();
        }
        if(currentFrames == frames.length){
            currentFrames = 0;
            playOnce = true;
        }
    }

    public Bitmap getImage(){
        return frames[currentFrames];
    }

    public int getCurrentFrames() {
        return currentFrames;
    }

    public boolean isPlayOnce() {
        return playOnce;
    }
}
