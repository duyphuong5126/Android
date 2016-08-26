package duy.phuong.musicsocialnetwork.MediaPlayerScreen;

/**
 * Created by Phuong on 25/07/2016.
 */
public interface PlaybackListener {
    void onStart();
    void updateProgress();
    void onPause();
    void onResume();
    void onStop();
}
