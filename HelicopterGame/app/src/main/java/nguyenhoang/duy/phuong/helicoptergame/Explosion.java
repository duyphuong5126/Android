package nguyenhoang.duy.phuong.helicoptergame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by Phuong on 09/06/2015.
 */
public class Explosion {
    private int x;
    private int y;
    private int width;
    private int height;
    private int row;
    private Animation animate = new Animation();
    private Bitmap spritesheet;
    private boolean move;

    public Explosion(Bitmap res, int x, int y, int w, int h, int numFrames){
        this.x = x;
        this.y = y;

        this.width = w;
        this.height = h;

        this.move = false;

        Bitmap[] images = new Bitmap[numFrames];
        spritesheet = res;

        for(int i=0; i<images.length; i++){
            if(i%5==0&&i>row) row++;
            images[i] = Bitmap.createBitmap(spritesheet, (i-(5*row))*width, row*height, width, height);
        }
        animate.setFrames(images);
        animate.setDelay(10);
    }
    public void draw(Canvas canvas){
        if(!animate.isPlayOnce()){
            canvas.drawBitmap(animate.getImage(), x, y, null);
        }
    }
    public void update(){
        if(!animate.isPlayOnce()){
            animate.update();
        }
        if(move) x -= 30;
    }
    public boolean isDisappear(){
        return animate.isPlayOnce();
    }

    public int getHeight() {
        return height;
    }

    public void setMove(boolean move) {
        this.move = move;
    }
}
