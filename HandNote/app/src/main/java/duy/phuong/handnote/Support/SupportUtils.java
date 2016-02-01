package duy.phuong.handnote.Support;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Build;

import java.util.ArrayList;

/**
 * Created by Phuong on 28/11/2015.
 */
public abstract class SupportUtils {
    public static Bitmap cropBitmap(Bitmap src, int x, int y, int w, int h) {
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        for (int i = x; i < x + w; i++ ) {
            for (int j = y; j < y + h; j++ ) {
                if (i >= 0 && j >= 0) {
                    bitmap.setPixel(((i - x) >= 0)?i - x:0, ((j - y) >= 0)?j - y:0, src.getPixel(i, j));
                }
            }
        }
        return bitmap;
    }

    public static boolean checkIntersect(Path p1, Path p2, int width, int height) {
        Paint tempPaint = new Paint();
        tempPaint.setColor(Color.BLACK);
        tempPaint.setPathEffect(new CornerPathEffect(10));
        tempPaint.setStyle(Paint.Style.STROKE);
        tempPaint.setStrokeCap(Paint.Cap.ROUND);
        tempPaint.setStrokeJoin(Paint.Join.ROUND);
        tempPaint.setAntiAlias(true);
        tempPaint.setDither(true);
        tempPaint.setStrokeWidth(5f);

        Bitmap bmp1 = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c1 = new Canvas(bmp1);
        c1.drawColor(Color.WHITE);
        c1.drawPath(p1, tempPaint);
        Bitmap bmp2 = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c2 = new Canvas(bmp2);
        c2.drawColor(Color.WHITE);
        c2.drawPath(p2, tempPaint);

        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++) {
                if (bmp1.getPixel(i, j) == Color.BLACK && bmp2.getPixel(i, j) == Color.BLACK) {
                    return true;
                }
            }

        return false;
    }

    public static Bitmap createEmptyBitmap(Bitmap src) {
        Bitmap bitmap = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);
        for (int i = 0; i < src.getWidth(); i++ ) {
            for (int j = 0; j < src.getHeight(); j++ ) {
                bitmap.setPixel(i, j, Color.WHITE);
            }
        }
        return bitmap;
    }

    public static int max(int a, int b) {
        return (a >= b)?a:b;
    }

    public static int min(int a, int b) {
        return (a <= b)?a:b;
    }
}
