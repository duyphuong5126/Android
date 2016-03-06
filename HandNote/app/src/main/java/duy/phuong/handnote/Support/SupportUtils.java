package duy.phuong.handnote.Support;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

/**
 * Created by Phuong on 28/11/2015.
 */
public abstract class SupportUtils {
    public static final String RootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static final String ApplicationDirectory = "/HandNote/";

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

    public static boolean saveImage(Bitmap bitmap, String folderName, String fileName, String fileType) {
        File dir = new File(RootPath + ApplicationDirectory + folderName);
        dir.mkdirs();

        File file = new File(dir, folderName + "_" + fileName + "_" + System.currentTimeMillis() + fileType);
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

    public static ArrayList<String> getListFilePaths(String path) {
        File fileDir = new File(path);
        File[] listFiles = fileDir.listFiles();

        if (listFiles == null || listFiles.length == 0) {
            Log.e("Error", "Get list files failed");
            return new ArrayList<>();
        }

        ArrayList<String> paths = new ArrayList<>();
        for (File file : listFiles) {
            paths.add(file.getAbsolutePath());
        }

        return paths;
    }

    public static String getApplicationDir(String folderName) {
        return "";
    }

    public static boolean emptyDirectory(String path) {
        File fileDir = new File(path);
        File[] listFiles = fileDir.listFiles();

        if (listFiles == null || listFiles.length == 0) {
            Log.e("Error", "Get list files failed");
        } else {
            for (File file : listFiles) {
                if (!file.delete()) {
                    return false;
                }
            }
        }

        return true;
    }

    public static boolean writeFile(String data, String folder, String name) {
        File fileDir = new File(RootPath + ApplicationDirectory + folder);
        fileDir.mkdirs();

        File output = new File(fileDir, name);
        if (output.exists()) {
            output.delete();
        }

        try {
            output.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(output);
            fileOutputStream.write(data.getBytes());
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
