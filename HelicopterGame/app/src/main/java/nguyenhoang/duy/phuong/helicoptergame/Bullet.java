package nguyenhoang.duy.phuong.helicoptergame;

import android.graphics.Bitmap;

/**
 * Created by Phuong on 09/06/2015.
 */
public class Bullet extends MoveableItem {
    private boolean Live;
    private boolean direction;
    public Bullet(int x, int y, int w, int h, boolean Live, boolean direction) {
        super(x, y, w, h);
        this.Live = Live;
        this.direction = direction;
    }

    @Override
    public void initSprites(int s, int numFrames, Bitmap res) {
        score = s;
        speed = 30;
        Bitmap[] images = new Bitmap[numFrames];
        spritesheet = res;

        int offset = 135;
        if(!direction) offset = 10;

        for(int i=0; i<images.length; i++){
            images[i] = Bitmap.createBitmap(spritesheet, i*width, offset, width, height);
        }
        animate.setFrames(images);
        animate.setDelay((animate.getCurrentFrames() == 0)?2000:0);
    }

    @Override
    public void update() {
        if(direction) x += speed;
        if(!direction) x -= speed;
        animate.update();
    }

    public boolean isLive() {
        return Live;
    }

    public void setLive(boolean live) {
        Live = live;
    }
}
