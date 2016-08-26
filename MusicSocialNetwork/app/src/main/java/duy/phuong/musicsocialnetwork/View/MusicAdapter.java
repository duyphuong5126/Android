package duy.phuong.musicsocialnetwork.View;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import duy.phuong.musicsocialnetwork.Data.Music;
import duy.phuong.musicsocialnetwork.MediaPlayerScreen.PlaybackListener;
import duy.phuong.musicsocialnetwork.R;
import duy.phuong.musicsocialnetwork.Support.SupportUtil;

/**
 * Created by Phuong on 16/07/2016.
 */
public class MusicAdapter extends BaseAdapter {
    private Activity mActivity;
    private List<Music> mMusics;
    private int mXML;

    public MusicAdapter(Activity activity, List<Music> musics, int xml) {
        mActivity = activity;
        mMusics = musics;
        mXML = xml;
    }

    @Override
    public int getCount() {
        return mMusics.size();
    }

    @Override
    public Object getItem(int position) {
        return mMusics.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Music music = mMusics.get(position);
        if (convertView == null) {
            convertView = mActivity.getLayoutInflater().inflate(mXML, parent, false);
        }
        TextView tvName = (TextView) convertView.findViewById(R.id.musicName);
        tvName.setText(music.mName);
        TextView tvAlbum = (TextView) convertView.findViewById(R.id.musicAlbum);
        tvAlbum.setText(music.mAlbum);
        TextView tvDuration = (TextView) convertView.findViewById(R.id.musicDuration);
        tvDuration.setText(SupportUtil.secToTime(music.mDuration));
        return convertView;
    }
}
