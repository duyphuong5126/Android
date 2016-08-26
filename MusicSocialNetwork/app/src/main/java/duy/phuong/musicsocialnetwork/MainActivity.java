package duy.phuong.musicsocialnetwork;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

import duy.phuong.musicsocialnetwork.Base.BaseFragment;
import duy.phuong.musicsocialnetwork.Data.Music;
import duy.phuong.musicsocialnetwork.Listener.MainListener;
import duy.phuong.musicsocialnetwork.Listener.PlayerListener;
import duy.phuong.musicsocialnetwork.MediaPlayerScreen.MusicFragment;
import duy.phuong.musicsocialnetwork.Support.SupportUtil;
import duy.phuong.musicsocialnetwork.View.FragmentAdapter;

public class MainActivity extends FragmentActivity implements MainListener {
    private static final int READ_EXTERNAL_CODE = 1;
    private ArrayList<Music> mMusics;
    private Music mCurrentMusic;
    private PlayerListener mPlayerListener;
    private ViewPager mFragmentPager;
    private FragmentAdapter mFragmentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final LinearLayout mLayoutIntro = (LinearLayout) findViewById(R.id.layoutIntro);
        mFragmentPager = (ViewPager) findViewById(R.id.fragmentPager);
        mFragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), this);
        mFragmentPager.setAdapter(mFragmentAdapter);
        if (mPlayerListener == null) {
            for (int i = 0; i < mFragmentAdapter.getCount(); i++) {
                BaseFragment baseFragment = (BaseFragment) mFragmentAdapter.getItem(i);
                if (baseFragment != null) {
                    if (baseFragment.getName().equals(MusicFragment.class.getName())) {
                        mPlayerListener = (MusicFragment) baseFragment;
                    }
                }
            }
        }
        mFragmentPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                BaseFragment baseFragment = (BaseFragment) mFragmentAdapter.instantiateItem(mFragmentPager, position);
                baseFragment.setStatusBarColor();
                if (baseFragment.getName().equals(MusicFragment.class.getName())) {
                    if (mCurrentMusic != null) {
                        baseFragment.getArguments().putSerializable("Music", mCurrentMusic);
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        (findViewById(R.id.buttonViewedIntro)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLayoutIntro.setVisibility(View.GONE);
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void initMusicList() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_CODE);
            }
        } else {
            loadMusics();
        }
    }
    @Override
    public void loadMusics() {
        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.ALBUM,
                        MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DATA},
                null, null, "LOWER(" + MediaStore.Audio.Media.TITLE + ") ASC");
        if (mMusics == null) {
            mMusics = new ArrayList<>();
        }
        mMusics.clear();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                    mMusics.add(new Music(
                            cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)),
                            cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)),
                            cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)) / 1000,
                            Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, "" + id),
                            SupportUtil.getAudioThumbnail(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)))
                    ));
                } while (cursor.moveToNext());
            }
        }
    }


    @Override
    public ArrayList<Music> getMusics() {
        if (mMusics == null || mMusics.size() == 0) {
            initMusicList();
        }
        return mMusics;
    }

    @Override
    public void playMusic(Music music) {
        mCurrentMusic = music;
        mFragmentPager.setCurrentItem(mFragmentPager.getCurrentItem() + 1, true);
        if (mPlayerListener != null) {
            mPlayerListener.playMusic(mCurrentMusic);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case READ_EXTERNAL_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadMusics();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Toast.makeText(MainActivity.this, "Destroy", Toast.LENGTH_SHORT).show();
    }
}
