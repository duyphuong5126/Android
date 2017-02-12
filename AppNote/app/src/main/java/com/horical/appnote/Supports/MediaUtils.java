package com.horical.appnote.Supports;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.widget.ImageButton;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Phuong on 25/08/2015.
 */
public abstract class MediaUtils {

    private static Map<String, ImageButton> listPlayMediaButton;

    @SuppressLint("StaticFieldLeak")
    private static Activity activity;

    public static void setActivity(Activity activity) {
        MediaUtils.listPlayMediaButton = new HashMap<>();
        MediaUtils.activity = activity;
    }

    public static void storeButtonReference(String dataPath, ImageButton button) {
        for (Map.Entry<String, ImageButton> entry : listPlayMediaButton.entrySet()) {
            if (entry.getKey().equals(dataPath)) {
                return;
            }
        }
        listPlayMediaButton.put(dataPath, button);
    }

    public static void openMedia(String path) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(path));
        intent.setDataAndType(Uri.parse(path), "video/*");
        activity.startActivity(Intent.createChooser(intent, "Choose a media player: "));
    }
}
