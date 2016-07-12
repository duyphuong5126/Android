package duy.phuong.musicsocialnetwork.View;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import duy.phuong.musicsocialnetwork.HomeScreen.HomeFragment;
import duy.phuong.musicsocialnetwork.MusicScreen.MusicFragment;

/**
 * Created by Phuong on 12/07/2016.
 */
public class FragmentAdapter extends FragmentPagerAdapter {
    public FragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new HomeFragment();
            case 1:
                return new MusicFragment();
            default:
                return new MusicFragment();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
