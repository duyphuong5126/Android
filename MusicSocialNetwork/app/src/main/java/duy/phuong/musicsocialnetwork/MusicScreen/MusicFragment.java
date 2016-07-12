package duy.phuong.musicsocialnetwork.MusicScreen;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;

import duy.phuong.musicsocialnetwork.Base.BaseFragment;
import duy.phuong.musicsocialnetwork.R;

/**
 * Created by Phuong on 12/07/2016.
 */
public class MusicFragment extends BaseFragment {
    public MusicFragment() {
        mXML = R.layout.fragment_music;
        mStatusBarColor = Color.parseColor("#4052b5");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public String getName() {
        return MusicFragment.class.getName();
    }
}
