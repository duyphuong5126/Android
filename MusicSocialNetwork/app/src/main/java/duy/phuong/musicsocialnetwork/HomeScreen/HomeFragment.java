package duy.phuong.musicsocialnetwork.HomeScreen;

import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

import duy.phuong.musicsocialnetwork.Base.BaseFragment;
import duy.phuong.musicsocialnetwork.Data.Music;
import duy.phuong.musicsocialnetwork.R;
import duy.phuong.musicsocialnetwork.View.MusicAdapter;

/**
 * Created by Phuong on 12/07/2016.
 */
public class HomeFragment extends BaseFragment {
    private ArrayList<Music> mMusics;
    private ListView mListMusics;
    public HomeFragment() {
        mXML = R.layout.fragment_home;
        mStatusBarColor = Color.parseColor("#009688");
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListMusics = (ListView) view.findViewById(R.id.listMusic);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mMusics = mListener.getMusics();
        MusicAdapter musicAdapter = new MusicAdapter(mActivity, mMusics, R.layout.item_music);
        mListMusics.setAdapter(musicAdapter);
        setListViewHeight(mListMusics);
    }

    @Override
    public String getName() {
        return HomeFragment.class.getName();
    }
}
