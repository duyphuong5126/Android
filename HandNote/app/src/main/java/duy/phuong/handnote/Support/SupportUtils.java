package duy.phuong.handnote.Support;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Environment;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Phuong on 28/11/2015.
 */
public abstract class SupportUtils {
    private static final String RootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    private static final String ApplicationDirectory = "/HandNote/";

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

    public static boolean saveImage(Bitmap bitmap, String folderName, String fileType) {
        File dir = new File(RootPath + ApplicationDirectory + folderName);
        dir.mkdir();

        File file = new File(dir, folderName + System.currentTimeMillis() + fileType);
        if (file.exists()) {
            file.delete();
        }

        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
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

    public static String getApplicationDir(String folderName) {
        return "";
    }
}
