package duy.phuong.musicsocialnetwork.View;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import duy.phuong.musicsocialnetwork.Base.BaseFragment;
import duy.phuong.musicsocialnetwork.HomeScreen.HomeFragment;
import duy.phuong.musicsocialnetwork.Listener.MainListener;
import duy.phuong.musicsocialnetwork.MediaPlayerScreen.MusicFragment;

/**
 * Created by Phuong on 12/07/2016.
 */
public class FragmentAdapter extends FragmentPagerAdapter {
    private MainListener mListener;
    private HomeFragment mHomeFragment;
    private MusicFragment mMusicFragment;
    public FragmentAdapter(FragmentManager fm, MainListener listener) {
        super(fm);
        mListener = listener;
        mHomeFragment = new HomeFragment();
        mMusicFragment = new MusicFragment();
    }

    @Override
    public Fragment getItem(int position) {
        BaseFragment baseFragment;
        switch (position) {
            case 0:
                baseFragment = mHomeFragment;
                break;
            case 1:
                baseFragment = mMusicFragment;
                break;
            default:
                baseFragment = mHomeFragment;
                break;
        }
        if (mListener != null && baseFragment.getListener() == null) {
            baseFragment.setListener(mListener);
        }

        return baseFragment;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
