package duy.phuong.handnote.DTO;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Phuong on 02/03/2016.
 */
public class StandardImage {
    public static final int WIDTH = 20;
    public static final int HEIGHT = 28;
    private Bitmap mBitmap;
    private String mName;

    public StandardImage(Bitmap mBitmap, String mName) {
        this.mBitmap = mBitmap;
        this.mName = mName;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public String getName() {
        return mName;
    }
}
