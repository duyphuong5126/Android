package duy.phuong.musicsocialnetwork.HomeScreen;

import android.graphics.Color;

import duy.phuong.musicsocialnetwork.Base.BaseFragment;
import duy.phuong.musicsocialnetwork.R;

/**
 * Created by Phuong on 12/07/2016.
 */
public class HomeFragment extends BaseFragment {
    public HomeFragment() {
        mXML = R.layout.fragment_home;
        mStatusBarColor = Color.parseColor("#69b78c");
    }
    @Override
    public String getName() {
        return HomeFragment.class.getName();
    }
}
