package com.horical.appnote.Background;

import android.app.Service;
import android.content.Intent;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.widget.Toast;

import com.horical.appnote.R;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Phuong on 21/09/2015.
 */
public class MediaService extends Service implements
        MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnErrorListener, MediaPlayer.OnInfoListener, MediaPlayer.OnPreparedListener,
        MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnCompletionListener {

    private MediaPlayer mPlayer;
    private static boolean mRunning;

    private Resources mRes;

    private static String mAudioName;

    private static HashMap<String, Integer> mListAudio;

    public static void setRunning(boolean Running) {
        MediaService.mRunning = Running;
    }

    public static boolean isRunning() {
        return mRunning;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mListAudio  = new HashMap<String, Integer>();
        if (mAudioName == null || mAudioName.equals("")) {
            mAudioName = getApplicationContext().getResources().getString(R.string.my_little_lady);
        }

        mRes = getApplicationContext().getResources();
        Field[] fields = R.raw.class.getFields();
        for (Field field : fields) {
            mListAudio.put(field.getName(), mRes.getIdentifier(field.getName(), "raw", getApplicationContext().getPackageName()));
        }

        mPlayer = MediaPlayer.create(getApplicationContext(),
                (mListAudio.get(mAudioName)> 0)?mListAudio.get(mAudioName):R.raw.my_little_lady);
        mPlayer.setOnCompletionListener(this);
        mPlayer.setOnErrorListener(this);
        mPlayer.setOnInfoListener(this);
        mPlayer.setOnBufferingUpdateListener(this);
        mPlayer.setOnPreparedListener(this);
        mPlayer.setOnSeekCompleteListener(this);
        playMedia();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {

    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        stopMedia();
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
        switch (i) {
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                Toast.makeText(getApplicationContext(), "MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK", Toast.LENGTH_SHORT).show();
                break;
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                Toast.makeText(getApplicationContext(), "MEDIA_ERROR_UNKNOWN", Toast.LENGTH_SHORT).show();
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                Toast.makeText(getApplicationContext(), "MEDIA_ERROR_SERVER_DIED", Toast.LENGTH_SHORT).show();
                break;
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopMedia();
    }

    @Override
    public boolean onInfo(MediaPlayer mediaPlayer, int i, int i2) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        if (!mPlayer.isPlaying()) {
            mPlayer.start();
        }
    }

    @Override
    public void onSeekComplete(MediaPlayer mediaPlayer) {

    }

    public static void setAudioName(String AudioName) {
        MediaService.mAudioName = AudioName;
    }

    public void stopMedia() {
        if (mPlayer.isPlaying()) {
            mPlayer.stop();
            mRunning = false;
        }
    }
    public void playMedia() {
        if (!mPlayer.isPlaying()) {
            mPlayer.start();
            mRunning = true;
            new CountDownTimer(45000, 1000) {
                @Override
                public void onTick(long l) {

                }

                @Override
                public void onFinish() {
                    stopMedia();
                }
            }.start();
        }
    }
}
