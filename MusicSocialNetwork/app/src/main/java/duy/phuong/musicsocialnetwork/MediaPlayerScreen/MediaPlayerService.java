package duy.phuong.musicsocialnetwork.MediaPlayerScreen;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;

import duy.phuong.musicsocialnetwork.Base.MusicApplication;
import duy.phuong.musicsocialnetwork.Data.Music;

/**
 * Created by Phuong on 26/07/2016.
 */
public class MediaPlayerService extends Service implements MusicApplication.PlayBackListener {
    private MediaPlayer mPlayer;

    @Override
    public void onCreate() {
        super.onCreate();
        mPlayer = new MediaPlayer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MusicApplication mApplication = (MusicApplication) getApplication();
        if (mApplication.getListener() == null) {
            mApplication.setListener(this);
        }
        return 1;
    }

    private void initMusic(Music music) {
        stop();
        if (music != null) {
            mPlayer = MediaPlayer.create(getApplicationContext(), music.mURI);
            play();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stop();
    }

    @Override
    public void changeNowPlaying(Music nowPlaying) {
        initMusic(nowPlaying);
    }

    @Override
    public void play() {
        if (mPlayer != null) {
            if (!mPlayer.isPlaying()) {
                mPlayer.start();
            }
        }
    }

    @Override
    public void pause() {
        if (mPlayer != null) {
            if (mPlayer.isPlaying()) {
                mPlayer.pause();
            }
        }
    }

    @Override
    public void stop() {
        if (mPlayer != null) {
            if (mPlayer.isPlaying()) {
                mPlayer.stop();
            }
            mPlayer.release();
        }
    }
}
