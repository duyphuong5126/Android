package nguyenhoang.duy.phuong.helicoptergame.GameComponents.GameUtils;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import nguyenhoang.duy.phuong.helicoptergame.GameComponents.GamePanel;

/**
 * Created by Phuong on 09/06/2015.
 */
public class Background {
    private Bitmap mImage;
    private int mX, mY, mDX;

    public Background(Bitmap bmp){
        this.mImage = bmp;
        mDX = GamePanel.MOVE_SPEED;
    }
    public void update(){
        mX += mDX;
        if(mX < -GamePanel.WIDTH) mX = 0;
    }
    public void draw(Canvas canvas){
        canvas.drawBitmap(mImage, mX, mY, null);
        if(mX < 0) canvas.drawBitmap(mImage, mX + GamePanel.WIDTH, mY, null);
    }
}