package nguyenhoang.duy.phuong.helicoptergame;

import android.graphics.Bitmap;

/**
 * Created by Phuong on 09/06/2015.
 */
public class SpecialMissile extends MoveableItem {
    private String type;
    private int New;
    private boolean Live;
    public SpecialMissile(int x, int y, int w, int h, String type, int speed) {
        super(x, y, w, h);
        New = 0;
        this.type = type;
        this.speed = speed;
        Live = true;
    }

    @Override
    public void initSprites(int s, int numFrames, Bitmap res) {
        score = s;
        //cap missile speed
        if(speed>50) speed = 50;
        Bitmap[] images = new Bitmap[numFrames];
        spritesheet = res;

        int posY = rd.nextInt(3);

        for(int i=0; i<images.length; i++){
            images[i] = Bitmap.createBitmap(spritesheet, i*width, posY*height, width, height);
        }
        animate.setFrames(images);
        animate.setDelay(100-speed);
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public void update() {
        x -= (speed+10);
        y += (type.equals("Normal"))?0:((type.equals("Top_Left"))?speed:-speed);
    }

    public void setNew(int New) {
        this.New = New;
    }
    public int getNew(){
        return New;
    }

    public boolean isLive() {
        return Live;
    }

    public void setLive(boolean live) {
        Live = live;
    }
}
