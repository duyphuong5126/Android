package duy.phuong.musicsocialnetwork.Data;

/**
 * Created by Phuong on 16/07/2016.
 */
public class Music {
    public String mName;
    public String mAlbum;
    public String mArtist;
    public int mDuration;

    public Music(String Name, String Album, String Artist, int Duration) {
        this.mName = Name;
        this.mAlbum = Album;
        this.mArtist = Artist;
        this.mDuration = Duration;
    }
}
