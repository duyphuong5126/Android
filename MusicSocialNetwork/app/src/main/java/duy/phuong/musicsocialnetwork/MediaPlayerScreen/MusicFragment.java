package duy.phuong.musicsocialnetwork.MediaPlayerScreen;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import duy.phuong.musicsocialnetwork.Base.BaseFragment;
import duy.phuong.musicsocialnetwork.Base.MusicApplication;
import duy.phuong.musicsocialnetwork.Data.Music;
import duy.phuong.musicsocialnetwork.Listener.PlayerListener;
import duy.phuong.musicsocialnetwork.R;

/**
 * Created by Phuong on 12/07/2016.
 */
public class MusicFragment extends BaseFragment implements PlayerListener, View.OnClickListener, MusicApplication.GetPlaybackListener {
    private Music mNowPlaying;
    private MediaPlayer mMediaPlayer;
    private ImageButton mButtonPlay, mButtonPause, mButtonPrev, mButtonNext, mButtonMainPlay;
    private ImageView mImageCover;
    private LinearLayout mLayoutPlay, mLayoutPause;
    private TextView mTextMusicName, mTextAlbum, mTextArtist;

    private MusicApplication.PlayBackListener mListener;

    public MusicFragment() {
        mXML = R.layout.fragment_music;
        mStatusBarColor = Color.parseColor("#4052b5");
        setArguments(new Bundle());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mButtonPlay = (ImageButton) view.findViewById(R.id.buttonPlay);
        mButtonPlay.setOnClickListener(this);
        mButtonPause = (ImageButton) view.findViewById(R.id.buttonPause);
        mButtonPause.setOnClickListener(this);
        mButtonPrev = (ImageButton) view.findViewById(R.id.buttonPrevious);
        mButtonPrev.setOnClickListener(this);
        mButtonNext = (ImageButton) view.findViewById(R.id.buttonNext);
        mButtonNext.setOnClickListener(this);
        mButtonMainPlay = (ImageButton) view.findViewById(R.id.buttonMainPlay);
        mButtonMainPlay.setOnClickListener(this);
        mTextMusicName = (TextView) view.findViewById(R.id.textMusicName);
        mTextAlbum = (TextView) view.findViewById(R.id.textAlbum);
        mTextArtist = (TextView) view.findViewById(R.id.textArtist);
        mImageCover = (ImageView) view.findViewById(R.id.imageCover);
        mLayoutPlay = (LinearLayout) view.findViewById(R.id.layoutPlay);
        mLayoutPause = (LinearLayout) view.findViewById(R.id.layoutPause);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() != null) {
            mNowPlaying = (Music) getArguments().getSerializable("Music");
        }

        MusicApplication application = (MusicApplication) mActivity.getApplication();
        application.setGetter(this);
        mListener = application.getListener();
    }

    private void switchMode(boolean play) {
        mLayoutPause.setVisibility(play ? View.VISIBLE : View.GONE);
        mLayoutPlay.setVisibility(!play ? View.VISIBLE : View.GONE);
    }

    @Override
    public String getName() {
        return MusicFragment.class.getName();
    }

    @Override
    public void playMusic(Music music) {
        if (music.equals(mNowPlaying)) {
            Toast.makeText(mActivity, "Media player is playing it now", Toast.LENGTH_SHORT).show();
        } else {
            mNowPlaying = music;
            /*if (mMediaPlayer != null) {
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.pause();
                    mMediaPlayer.release();
                }
            }
            mMediaPlayer = MediaPlayer.create(mActivity, mNowPlaying.mURI);
            mMediaPlayer.start();*/

            mListener.changeNowPlaying(mNowPlaying);

            mTextMusicName.setText(mNowPlaying.mName);
            mTextArtist.setText(mNowPlaying.mArtist);
            mTextAlbum.setText(mNowPlaying.mAlbum);
            if (mNowPlaying.mCover != null) {
                mImageCover.setImageBitmap(mNowPlaying.mCover);
            } else {
                mImageCover.setImageResource(R.drawable.res);
            }
            switchMode(true);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonMainPlay:
                break;
            case R.id.buttonPlay:
                if (mListener != null) {
                    switchMode(true);
                    mListener.play();
                } else {
                    Toast.makeText(mActivity, "Media player's initialized yet", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.buttonNext:
                break;
            case R.id.buttonPrevious:
                break;
            case R.id.buttonPause:
                if (mListener != null) {
                    switchMode(true);
                    mListener.pause();
                } else {
                    Toast.makeText(mActivity, "Media player's initialized yet", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void setPlayback(MusicApplication.PlayBackListener playback) {
        mListener = playback;
    }
}
