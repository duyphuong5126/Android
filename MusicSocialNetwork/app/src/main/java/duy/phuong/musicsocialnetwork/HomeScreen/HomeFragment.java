package duy.phuong.musicsocialnetwork.HomeScreen;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import duy.phuong.musicsocialnetwork.Base.BaseFragment;
import duy.phuong.musicsocialnetwork.Data.Music;
import duy.phuong.musicsocialnetwork.R;
import duy.phuong.musicsocialnetwork.View.MusicAdapter;

/**
 * Created by Phuong on 12/07/2016.
 */
public class HomeFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener{
    private ArrayList<Music> mMusics;
    private ListView mListMusics;
    private MusicAdapter mMusicAdapter;
    private SwipeRefreshLayout mRefresher;

    public HomeFragment() {
        mXML = R.layout.fragment_home;
        mStatusBarColor = Color.parseColor("#009688");
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListMusics = (ListView) view.findViewById(R.id.listMusic);
        mRefresher = (SwipeRefreshLayout) view.findViewById(R.id.swipeMusic);
        mRefresher.setDistanceToTriggerSync(50);
        mRefresher.setColorSchemeColors(Color.parseColor("#009688"), Color.parseColor("#009688"), Color.parseColor("#4052b5"));
        mRefresher.setSize(SwipeRefreshLayout.LARGE);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mMusics = mListener.getMusics();
        mMusicAdapter = new MusicAdapter(mActivity, mMusics, R.layout.item_music);
        mListMusics.setAdapter(mMusicAdapter);
        mMusicAdapter.notifyDataSetChanged();
        setListViewHeight(mListMusics);
        mListMusics.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mListener.playMusic(mMusics.get(position));
            }
        });
        mRefresher.setOnRefreshListener(this);
    }

    @Override
    public String getName() {
        return HomeFragment.class.getName();
    }

    @Override
    public void onRefresh() {
        mRefresher.postDelayed(new Runnable() {
            @Override
            public void run() {
                mRefresher.setRefreshing(true);
                mListener.loadMusics();
                mMusicAdapter.notifyDataSetChanged();
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mRefresher.setRefreshing(false);
                        Toast.makeText(mActivity, "Load music done!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }, 2000);
    }
}
