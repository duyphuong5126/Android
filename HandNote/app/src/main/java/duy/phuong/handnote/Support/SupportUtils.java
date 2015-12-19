package duy.phuong.handnote.Support;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;

/**
 * Created by Phuong on 28/11/2015.
 */
public abstract class SupportUtils {
    public static Bitmap cropBitmap(Bitmap src, int x, int y, int w, int h) {
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        for (int i = x; i < x + w; i++ ) {
            for (int j = y; j < y + h; j++ ) {
                bitmap.setPixel(i - x, j - y, src.getPixel(i, j));
            }
        }
        return bitmap;
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
