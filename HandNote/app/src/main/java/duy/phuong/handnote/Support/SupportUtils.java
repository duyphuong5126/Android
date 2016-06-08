package duy.phuong.handnote.Support;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.Objects;

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

    public static String getDirectoriesPath(Uri uri, Context context) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            if (cursor.moveToFirst()) {
                return getFolderPath(cursor.getString(index));
            }
            cursor.close();
        }
        return uri.getPath();
    }

    public static String getPath(Uri uri, Context context) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            if (cursor.moveToFirst()) {
                return cursor.getString(index);
            }
            cursor.close();
        }
        return uri.getPath();
    }

    private static String getFolderPath(String path) {
        String[] strings = path.split("/");
        String folder = "";
        for (int i = 0; i < strings.length - 1; i++) {
            folder += strings[i];
            if (i < strings.length - 2) {
                folder += "/";
            }
        }
        return folder;
    }

    public static Bitmap getAvatar() {
        ArrayList<String> list = getListFilePaths(RootPath + ApplicationDirectory + "Avatar");
        if (list != null) {
            for (String s : list) {
                Bitmap bitmap = BitmapFactory.decodeFile(s);
                if (bitmap != null) {
                    return bitmap;
                }
            }
        }
        return null;
    }

    public static void deleteAvatar() {
        ArrayList<String> list = getListFilePaths(RootPath + ApplicationDirectory + "Avatar");
        if (list != null) {
            for (String s : list) {
                File file = new File(s);
                file.deleteOnExit();
            }
        }
    }

    public static String getFormattedTime(long time) {
        long sec = time / 1000;
        if (sec <= 0) {
            return "" + time + " ms";
        } else {
            long ms = time % 1000;
            long minutes = sec / 60;
            if (minutes <= 0) {
                return "" + sec + " s " + ms + " ms";
            } else {
                long s = sec % 60;
                long hours = minutes / 60;
                if (hours <= 0) {
                    return "" + minutes + " m " + s + " s " + ms + " ms";
                } else {
                    long m = minutes % 60;
                    long days = hours / 24;
                    if (days <= 0) {
                        return "" + hours + " h " + m + " m " + s + " s " + ms + " ms";
                    } else {
                        long h = hours % 24;
                        return days + " days " + h + " h " + m + " m " + s + " s " + ms + " ms";
                    }
                }
            }
        }
    }
}