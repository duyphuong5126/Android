package duy.phuong.musicsocialnetwork.Support;

import android.content.Context;

/**
 * Created by Phuong on 26/07/2016.
 */
public class SharedPreferences {
    private static Context mContext;

    public static void setContext(Context Context) {
        SharedPreferences.mContext = Context;
    }
}
