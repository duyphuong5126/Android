package nguyenhoang.duy.phuong.helicoptergame;

import android.graphics.Bitmap;

/**
 * Created by Phuong on 09/06/2015.
 */
public class Missile extends MoveableItem {
    private boolean Live;
    public Missile(int x, int y, int w, int h){
        super(x, y, w, h);
        this.Live = true;
    }

    @Override
    public void initSprites(int s, int numFrames, Bitmap res) {
        score = s;
        speed = 10 + (int) (rd.nextDouble()*score/10);
        //cap missile speed
        if(speed>50) speed = 50;
        Bitmap[] images = new Bitmap[numFrames];
        spritesheet = res;
        for(int i=0; i<images.length; i++){
            images[i] = Bitmap.createBitmap(spritesheet, 0, i*height, width, height);
        }
        animate.setFrames(images);
        animate.setDelay(100-speed);
    }

    @Override
    public void setSpeed(int speed) {
        super.setSpeed(speed);
    }

    public boolean isLive() {
        return Live;
    }

    public void setLive(boolean live) {
        Live = live;
    }
}

