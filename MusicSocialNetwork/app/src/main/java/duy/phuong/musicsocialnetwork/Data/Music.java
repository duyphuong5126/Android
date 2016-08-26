package duy.phuong.musicsocialnetwork.Data;

import android.graphics.Bitmap;
import android.net.Uri;

import java.io.Serializable;

/**
 * Created by Phuong on 16/07/2016.
 */
public class Music implements Serializable{
    public String mName;
    public String mAlbum;
    public String mArtist;
    public int mDuration;
    public Uri mURI;
    public Bitmap mCover;

    public Music(String Name, String Album, String Artist, int Duration, Uri URI, Bitmap Cover) {
        this.mName = Name;
        this.mAlbum = Album;
        this.mArtist = Artist;
        this.mDuration = Duration;
        this.mURI = URI;
        this.mCover = Cover;
    }

    @Override
    public boolean equals(Object o) {
        if (o != null) {
            if (o.getClass().equals(Music.class)) {
                Music music = (Music) o;
                return (music.mName.equals(this.mName) && music.mAlbum.equals(this.mAlbum)
                        && music.mDuration == this.mDuration && music.mURI.equals(this.mURI));
            }
        }
        return false;
    }
}
