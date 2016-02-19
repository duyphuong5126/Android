package duy.phuong.handnote.MyView;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

import duy.phuong.handnote.R;

/**
 * Created by Phuong on 02/12/2015.
 */
public class BitmapAdapter extends ArrayAdapter<Bitmap> {
    private ArrayList<Bitmap> mListBitmap;
    private Activity mActivity;

    public BitmapAdapter(Activity activity, int resource, ArrayList<Bitmap> list) {
        super(activity, resource);
        mActivity = activity;
        mListBitmap = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mActivity.getLayoutInflater().inflate(R.layout.item_bitmap, null);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.itemBitmap);
        imageView.setImageBitmap(mListBitmap.get(position));
        return convertView;
    }

    @Override
    public int getCount() {
        return mListBitmap.size();
    }

    @Override
    public Bitmap getItem(int position) {
        return mListBitmap.get(position);
    }
}
