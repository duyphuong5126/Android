package nguyenhoang.duy.phuong.helicoptergame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by Phuong on 09/06/2015.
 */
public class Player extends GameObject{
    protected Bitmap spritesheet;
    protected int score;
    protected boolean up;
    protected boolean playing;
    protected Animation animate = new Animation();
    protected long startTime;
    protected boolean dissapear;
    private int status;
    private int Life;

    public Player(int w, int h){
        dy = 0;
        score = 0;
        height = h;
        width = w;
        status = 0;
        Life = 1;
    }

    public void initSprites(Bitmap res, int numFrames){
        Bitmap[] images = new Bitmap[numFrames];
        spritesheet = res;
        for (int i=0; i<images.length; i++) {
            images[i] = Bitmap.createBitmap(spritesheet, i*width, 0, width, height);
        }
        animate.setFrames(images);
        animate.setDelay(10);
        startTime = System.nanoTime();
    }

    public void setUp(boolean up) {
        this.up = up;
    }
    public void update(GamePanel gamePanel){
        long eslapsed = (System.nanoTime() - startTime)/1000000;
        if(eslapsed>100){
            score++;
            startTime = System.nanoTime();
        }
        animate.update();

        if(up){
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
        canvas.drawBitmap(animate.getImage(),x,y,null);
    }

    public int getScore() {
        return score;
    }

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }
    public void resetScore(){
        score=0;
        status = 0;
    }
    public void resetDY(){ dy=0; }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean isDissapear() {
        return dissapear;
    }

    public void setDissapear(boolean dissapear) {
        this.dissapear = dissapear;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public int getLife() {
        return Life;
    }

    public void setLife(int life) {
        Life = life;
    }
}
