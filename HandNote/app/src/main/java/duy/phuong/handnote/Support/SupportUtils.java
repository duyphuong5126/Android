package duy.phuong.handnote.Support;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Phuong on 28/11/2015.
 */
public abstract class SupportUtils {
    public static final String RootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static final String ApplicationDirectory = "/HandNote/";

    public static String saveImageWithPath(Bitmap bitmap, String folderName, String fileName, String fileType) {
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
            return "";
        }

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            return file.getAbsolutePath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "";
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
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

    public static String writeFileWithPath(String data, String folder, String name) {
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
            return output.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getStringResource(Context context, int rawId) throws IOException {
        InputStream stream = context.getResources().openRawResource(rawId);
        byte[] data = new byte[stream.available()];
        stream.read(data);
        return new String(data);
    }

    public static String getStringData(String folder, String fileName) {
        StringBuilder builder = new StringBuilder();
        File file = new File(RootPath + ApplicationDirectory + folder + "/" + fileName);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String s = "";
            while ((s = reader.readLine()) != null) {
                builder.append(s);
                builder.append("\r\n");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "";
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        return builder.toString();
    }
    public static String getStringData(String path) {
        StringBuilder builder = new StringBuilder();
        File file = new File(path);
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(file));
            String s = "";
            while ((s = reader.readLine()) != null) {
                builder.append(s);
                builder.append("\r\n");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "";
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        return builder.toString();
    }

    public static boolean deleteFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }
}
