package nguyenhoang.duy.phuong.helicoptergame;

import android.graphics.Bitmap;

/**
 * Created by Phuong on 09/06/2015.
 */
public class Gold extends MoveableItem {
    private int Value;
    public Gold(int x, int y, int w, int h) {
        super(x, y, w, h);
    }

    @Override
    public void initSprites(int s, int numFrames, Bitmap res) {
        speed = 10;
        Bitmap[] images = new Bitmap[numFrames];
        spritesheet = res;

        for(int i=0; i<images.length; i++){
            images[i] = Bitmap.createBitmap(spritesheet, i*width, 0, width, height);
        }
        animate.setFrames(images);
        animate.setDelay(100);
    }


}
