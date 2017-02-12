package com.horical.appnote.Supports;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.horical.appnote.LocalStorage.ApplicationSharedData;
import com.horical.appnote.LocalStorage.DataConstant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Phuong on 17/08/2015.
 */
public abstract class FileUtils {
    private static final String RootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    private static final String ApplicationDirectory = "/Note/";

    public static String getRootPath() {
        return RootPath;
    }

    private static String getUserFolder() {
        return ApplicationSharedData.getUserID()+"/";
    }

    public static String getApplicationDirectory(String folder) {
        return FileUtils.RootPath + ApplicationDirectory + getUserFolder() + folder + "/";
    }

    public static boolean writeFile(String folder, byte[] data, String name) {
        File fileDir = new File(FileUtils.RootPath + ApplicationDirectory + getUserFolder() + folder);
        fileDir.mkdirs();

        File file = new File(fileDir, name);
        if (file.exists()) {
            return true;
        }

        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));
            bufferedOutputStream.write(data);
            bufferedOutputStream.flush();
            bufferedOutputStream.close();

            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static String writeFile(String folder, String path, String fileName) {
        File fileDir = new File(FileUtils.RootPath + ApplicationDirectory + getUserFolder() + folder);
        fileDir.mkdirs();
        File myFile = new File(fileDir, fileName);//destination
        if (myFile.exists()) {
            return myFile.getAbsolutePath();
        }

        try {
            myFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(myFile));
            BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(path));
            byte[] buff = new byte[32 * 1024];
            int len;
            while ((len = bufferedInputStream.read(buff)) > 0) {
                bufferedOutputStream.write(buff, 0, len);
            }
            bufferedOutputStream.flush();
            bufferedOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return myFile.getPath();
    }

    public static boolean checkFileIsFromContent(String path) {
        return !path.contains("/mnt/sdcard/");
    }

    public static String getPath(Uri uri, Activity activity) {
        int currentAPI = Build.VERSION.SDK_INT;
        if (currentAPI > Build.VERSION_CODES.KITKAT) {
            Cursor cursor = activity.getContentResolver().query(uri, null, null, null, null);
            String path = "";
            if (cursor != null) {
                cursor.moveToFirst();
                String document_id = cursor.getString(0);
                document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
                cursor.close();

                cursor = activity.getContentResolver().query(
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
                if (cursor != null && cursor.moveToFirst()) {
                    path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                }
                if (cursor != null) {
                    cursor.close();
                }
            }

            return path;
        } else {
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = activity.managedQuery(uri, projection, null, null, null);
            activity.startManagingCursor(cursor);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            String path ="";
            if (cursor.moveToFirst()) {
                path =  cursor.getString(column_index);
            }
            return path;
        }
    }

    public static String[] getListFile(String filetype) {
        File file = new File(RootPath + ApplicationDirectory + getUserFolder() + filetype);
        File[] list = file.listFiles();
        if (list == null || list.length == 0) {
            Log.e("Error", "Folder empty");
            return new String[0];
        }
        String[] path = new String[list.length];
        for (int i = 0; i < list.length; i++) {
            path[i] = list[i].getPath();
        }
        return path;
    }

    public static String getStringFromStream(InputStream stream) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder builder = new StringBuilder();
        String line;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                builder.append(line).append("\n");
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    public static boolean deleteFiles(ArrayList<String> file_paths) {
        for (String path : file_paths) {
            File file = new File(path);
            if (file.exists() && !file.isDirectory()) {
                if (!file.delete()) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean fileIsUploaded(Activity activity, String path) {
        try {
            JSONObject jsonObject = new JSONObject(loadStringFileUploadLog(activity));
            if (jsonObject.has("history")) {
                JSONArray jsonArray = jsonObject.getJSONArray("history");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = (JSONObject) jsonArray.get(i);
                    if (object.getString("filePath").equals(path)) {
                        return true;
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static String loadStringFileUploadLog(Activity activity) {
        String result = "";
        try {
            InputStream inputStream = activity.openFileInput("UploadFileLog.txt");
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder builder = new StringBuilder();
                String temp;
                while ((temp = bufferedReader.readLine()) != null) {
                    builder.append(temp);
                }
                result = builder.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static byte[] readFileAsBytes(String path, String type) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        switch (type) {
            case DataConstant.TYPE_IMAGE:
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                if (path.contains(".jpg") || path.contains(".jpeg")) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                } else {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                }
                return stream.toByteArray();
            default:
                File file = new File(path);
                BufferedInputStream bufferedInputStream = null;
                try {
                    bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                int read;
                byte[] buffer = new byte[1024];
                try {
                    if (bufferedInputStream != null) {
                        while ((read = bufferedInputStream.read(buffer)) > 0) {
                            stream.write(buffer, 0, read);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    stream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return stream.toByteArray();
        }
    }
}
