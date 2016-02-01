package nguyenhoang.duy.phuong.helicoptergame.GameComponents.GameEffects;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import nguyenhoang.duy.phuong.helicoptergame.GameComponents.GameObject;

/**
 * Created by Phuong on 09/06/2015.
 */
public class SmokePuff extends GameObject {
    private int mRadius;
    public SmokePuff(int x, int y){
        mRadius = 5;
        super.x = x;
        super.y = y;
    }
    public void update(){
        x -= 10;
    }
    public void draw(Canvas canvas){
        Paint paint = new Paint();
        paint.setColor(Color.GRAY);
        paint.setStyle(Paint.Style.FILL);

        canvas.drawCircle(x- mRadius, y- mRadius, mRadius, paint);
        canvas.drawCircle(x- mRadius +2, y- mRadius -2, mRadius, paint);
        canvas.drawCircle(x- mRadius +4, y- mRadius +1, mRadius, paint);
    }
}
