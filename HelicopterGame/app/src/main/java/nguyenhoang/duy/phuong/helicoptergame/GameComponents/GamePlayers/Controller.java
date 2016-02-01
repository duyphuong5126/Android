package nguyenhoang.duy.phuong.helicoptergame.GameComponents.GamePlayers;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import nguyenhoang.duy.phuong.helicoptergame.GameComponents.GameObject;

/**
 * Created by Phuong on 09/06/2015.
 */
public class Controller extends GameObject {
    protected Bitmap mImage;
    protected int mFunction;

    public Controller(int x, int y, int w, int h, int func){
        this.width = w;
        this.height = h;
        this.x = x;
        this.y = y;
        this.mFunction = func;
    }

    public void initImage(Bitmap bitmap, int x, int y, int w, int h){
        mImage = Bitmap.createBitmap(bitmap, x, y, w, h);
    }

    public int getFunction() {
        return mFunction;
    }
    public void draw(Canvas canvas){
        canvas.drawBitmap(mImage,x,y,null);
    }
}
