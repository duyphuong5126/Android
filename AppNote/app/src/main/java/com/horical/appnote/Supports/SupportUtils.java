package com.horical.appnote.Supports;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.ViewGroup;

import com.horical.appnote.R;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.StringTokenizer;

/**
 * Created by Phuong on 13/08/2015.
 */
public abstract class SupportUtils {
    public enum DataSource {
        FROM_FILE_MANAGER, FROM_GALLERY
    }

    public static DataSource checkdataSource(Uri uri) {
        if (uri.getPath().contains("/mnt/")) return DataSource.FROM_FILE_MANAGER;
        if (uri.getPath().contains("/external/")) return DataSource.FROM_GALLERY;
        return null;
    }

    public static boolean isAlphabet(char c) {
        return ((c >= 65 && c <= 90) || (c >= 97 && c <= 122));
    }

    public static String getNameFromPath(String path) {
        StringTokenizer tokenizer = new StringTokenizer(path, "/");
        String result = "";
        while (tokenizer.hasMoreTokens()) {
            result = tokenizer.nextToken();
        }
        return result;
    }

    public static String MilliSecToTime(int milli) {
        int seconds = (milli / 1000) % 60;
        int minutes = (milli / (1000 * 60)) % 60;
        int hours = (milli / (1000 * 60 * 60)) % 24;
        String result = "";
        result += ((hours > 9) ? hours : "0" + hours) + ":";
        result += ((minutes > 9) ? minutes : "0" + minutes) + ":";
        result += (seconds > 9) ? seconds : "0" + seconds;
        return result;
    }

    public static int getDuration(Uri uri, Activity activity) {
        MediaPlayer mediaPlayer = MediaPlayer.create(activity, uri);
        return mediaPlayer.getDuration();
    }

    public static String convertDateToString(Calendar calendar) {
        return calendar.get(Calendar.DAY_OF_MONTH) + "/" +
                (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.YEAR);
    }

    public static Bitmap resizeBitmap(Bitmap src, int newWidth, int newHeight) {
        int width = src.getWidth();
        int height = src.getHeight();
        float scaleFactorX = (newWidth * 1.f) / width;
        float scaleFactorY = (newHeight * 1.f) / height;

        Matrix resizeMatrix = new Matrix();
        resizeMatrix.postScale(scaleFactorX, scaleFactorY);

        return Bitmap.createBitmap(src, 0, 0, width, height, resizeMatrix, false);
    }

    public static int getRandomID() {
        int ID = (int) System.nanoTime();
        ID *= (ID < 0) ? -1 : 1;
        return ID;
    }

    public static String getShortContent(String content) {
        return (content.length() > 45) ? content.substring(0, 42) + "..." : content;
    }

    public static String getShortName(String name) {
        return (name.length() > 23) ? name.substring(0, 19) + "..." : name;
    }

    public static long getFileSize(String path) {
        File file = new File(path);
        return file.length();
    }

    public static boolean checkBigFile(String path) {
        long fileSize = getFileSize(path);
        return (fileSize / (1024 * 1024)) > 10;
    }

    public static String checkFileSize(String path) {
        float size;
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        File file = new File(path);
        if (file.length() > 1024) {
            size = file.length()*1.f;
            return decimalFormat.format(size/1024) + " Kb";
        } else {
            return file.length() + " byte";
        }
    }

    public static ViewGroup.LayoutParams getScreenParams(Activity activity) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(displaymetrics.heightPixels, displaymetrics.widthPixels);
        layoutParams.height = displaymetrics.heightPixels;
        layoutParams.width = displaymetrics.widthPixels;
        return layoutParams;
    }

    public static float getHeightByWidth(int oldWidth, int oldHeight, int newWidth) {
        return (((newWidth * 1.f) / (oldWidth * 1.f)) * (oldHeight * 1.f));
    }

    public static boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    public static Bitmap getBitmapResource(Activity activity, int resId) {
        Resources resources = activity.getResources();
        return BitmapFactory.decodeResource(resources, resId);
        /*return ((BitmapDrawable) activity.getResources().
                getDrawable(R.drawable.ic_list_black_24dp)).getBitmap();*/
    }
}
