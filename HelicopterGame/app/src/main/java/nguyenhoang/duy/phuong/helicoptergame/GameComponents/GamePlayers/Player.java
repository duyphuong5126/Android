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
    protected long startTime;
    protected boolean Disapear;
    private int mStatus;
    private int Life;

    public Player(int w, int h){
        dy = 0;
        mScore = 0;
        height = h;
        width = w;
        mStatus = 0;
        Life = 1;
    }

    public void initSprites(Bitmap res, int numFrames){
        Bitmap[] images = new Bitmap[numFrames];
        mSpriteSheet = res;
        for (int i=0; i<images.length; i++) {
            images[i] = Bitmap.createBitmap(mSpriteSheet, i*width, 0, width, height);
        }
        mAnimate.setFrames(images);
        mAnimate.setDelay(10);
        startTime = System.nanoTime();
    }

    public void setUp(boolean up) {
        this.Up = up;
    }
    public void update(GamePanel gamePanel){
        long eslapsed = (System.nanoTime() - startTime)/1000000;
        if(eslapsed>100){
            mScore++;
            startTime = System.nanoTime();
        }
        mAnimate.update();

        if(Up){
            dy -= 1;
        }
        else{
            dy += 1;
        }
        if(dy>14) dy = 14;
        if(dy<-14) dy = -14;

        y += dy*2;
        if(y > gamePanel.getHeight() - height) y = gamePanel.getHeight() - height;
        if(y < 10) y = 10;
    }

    public void draw(Canvas canvas){
        canvas.drawBitmap(mAnimate.getImage(),x,y,null);
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
    public void resetScore(){
        mScore =0;
        mStatus = 0;
    }
    public void resetDY(){ dy=0; }

    public void setScore(int score) {
        this.mScore = score;
    }

    public boolean isDissapear() {
        return Disapear;
    }

    public void setDissapear(boolean dissapear) {
        this.Disapear = dissapear;
    }

    public void setStatus(int status) {
        this.mStatus = status;
    }

    public int getStatus() {
        return mStatus;
    }

    public int getLife() {
        return Life;
    }

    public void setLife(int life) {
        Life = life;
    }
}
