package duy.phuong.musicsocialnetwork.Base;

import android.app.Application;
import android.content.Intent;

import duy.phuong.musicsocialnetwork.Data.Music;
import duy.phuong.musicsocialnetwork.MediaPlayerScreen.MediaPlayerService;

/**
 * Created by Phuong on 26/07/2016.
 */
public class MusicApplication extends Application {
    private PlayBackListener mListener;

    public void setListener(PlayBackListener Listener) {
        mListener = Listener;
        if (mGetter != null) {
            this.mGetter.setPlayback(Listener);
        }
    }

    public PlayBackListener getListener() {
        return mListener;
    }

    public interface PlayBackListener {
        void changeNowPlaying(Music nowPlaying);
        void play();
        void pause();
        void stop();
    }

    public interface GetPlaybackListener {
        void setPlayback(PlayBackListener playback);
    }

    private GetPlaybackListener mGetter;

    public void setGetter(GetPlaybackListener Getter) {
        this.mGetter = Getter;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Intent mIntent;
        mIntent = new Intent(getApplicationContext(), MediaPlayerService.class);
        startService(mIntent);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
