package nguyenhoang.duy.phuong.helicoptergame;

import android.graphics.Bitmap;

/**
 * Created by Phuong on 09/06/2015.
 */
public class Item extends MoveableItem {
    private int Kind;
    public Item(int x, int y, int w, int h, int Kind) {
        super(x, y, w, h);
        this.Kind = Kind;
    }
    @Override
    public void initSprites(int s, int numFrames, Bitmap res) {
        speed = 10;
        Bitmap[] images = new Bitmap[numFrames];
        spritesheet = res;

        if(this.Kind == 3){
            for(int i=0; i<images.length; i++){
                images[i] = Bitmap.createBitmap(spritesheet, i*width, 0, width, height);
            }
        }
        else{
            for(int i=0; i<images.length; i++){
                images[i] = Bitmap.createBitmap(spritesheet, 128, 224, width, height);
            }
        }
        animate.setFrames(images);
        animate.setDelay(100);
    }

    public int getKind() {
        return Kind;
    }
}
