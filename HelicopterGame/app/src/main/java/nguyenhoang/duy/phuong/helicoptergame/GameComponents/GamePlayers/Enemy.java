package nguyenhoang.duy.phuong.helicoptergame.GameComponents.GamePlayers;

import android.graphics.Bitmap;

import java.util.Random;

import nguyenhoang.duy.phuong.helicoptergame.GameComponents.GamePanel;

/**
 * Created by Phuong on 09/06/2015.
 */
public class Enemy extends Player {
    protected int mHealthPoints;
    protected int mHealthPointsPool;
    protected long mDelay;
    protected Random mRandom = new Random();
    public Enemy(int w, int h) {
        super(w, h);
        Up = mRandom.nextBoolean();
        mDelay = 0;
        this.mHealthPoints = 0;
        this.mHealthPointsPool = 0;
    }

    public void initSprites(Bitmap[] res) {
        mAnimate.setFrames(res);
        mAnimate.setDelay(10);
        mStartTime = System.nanoTime();
    }

    @Override
    public void update(GamePanel gamePanel) {
        if(mDelay > 0){
            if((System.nanoTime() - mDelay)/1000000 > 1000) mDelay = 0;
        }
        else{
            if(Up){
                y -= 10;
            }
            else{
                y += 10;
            }
            if(y > gamePanel.getHeight() - (gamePanel.getHeight() - gamePanel.getDefaultHeight()) - height - 80){
                y = gamePanel.getHeight() - (gamePanel.getHeight() - gamePanel.getDefaultHeight()) - height - 80;
                Up = mRandom.nextBoolean();
            }
            if(y < 30){
                y = 30;
                Up = mRandom.nextBoolean();
            }
            if(mRandom.nextInt(4) == 1){
                mDelay = System.nanoTime();
            }
        }
        mAnimate.update();
    }

    public int getHealthPoints() {
        return mHealthPoints;
    }

    public void setHealthPoints(int healthPoints) {
        mHealthPoints = healthPoints;
    }

    public int getHealthPointsPool() {
        return mHealthPointsPool;
    }

    public void setHealthPointsPool(int healthPointsPool) {
        mHealthPointsPool = healthPointsPool;
    }

    public float getPecentagesOfHP(){
        return ((float) this.mHealthPoints)/((float) this.mHealthPointsPool);
    }
}
