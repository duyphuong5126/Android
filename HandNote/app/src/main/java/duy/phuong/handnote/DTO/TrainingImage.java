package duy.phuong.handnote.DTO;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Phuong on 02/03/2016.
 */
public class TrainingImage implements Parcelable{
    private Bitmap mBitmap;
    private String mName;

    public TrainingImage(Bitmap mBitmap, String mName) {
        this.mBitmap = mBitmap;
        this.mName = mName;
    }

    protected TrainingImage(Parcel in) {
        mBitmap = in.readParcelable(Bitmap.class.getClassLoader());
        mName = in.readString();
    }

    public static final Creator<TrainingImage> CREATOR = new Creator<TrainingImage>() {
        @Override
        public TrainingImage createFromParcel(Parcel in) {
            return new TrainingImage(in);
        }

        @Override
        public TrainingImage[] newArray(int size) {
            return new TrainingImage[size];
        }
    };

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public String getName() {
        return mName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mBitmap, flags);
        dest.writeString(mName);
    }
}
