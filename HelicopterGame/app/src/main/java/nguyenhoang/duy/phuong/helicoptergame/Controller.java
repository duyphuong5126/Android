package nguyenhoang.duy.phuong.helicoptergame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by Phuong on 09/06/2015.
 */
public class Controller extends GameObject {
    protected Bitmap image;
    protected int Function;

    public Controller(int x, int y, int w, int h, int func){
        this.width = w;
        this.height = h;
        this.x = x;
        this.y = y;
        this.Function = func;
    }

    public void initImage(Bitmap bitmap, int x, int y, int w, int h){
        image = Bitmap.createBitmap(bitmap, x, y, w, h);
    }

    public int getFunction() {
        return Function;
    }
    public void draw(Canvas canvas){
        canvas.drawBitmap(image,x,y,null);
    }
}
