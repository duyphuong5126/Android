package duy.phuong.handnote.MyView.DrawingView;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import duy.phuong.handnote.R;

/**
 * Created by Phuong on 11/05/2016.
 */
public class BitmapPager extends PagerAdapter {
    private ArrayList<Bitmap> mBitmaps;
    private Activity mActivity;
    private int mResLayout;
    private ArrayList<View> mViews;
    public BitmapPager(Activity activity, ArrayList<Bitmap> bitmaps, int resLayout) {
        mBitmaps = bitmaps;
        mActivity = activity;
        mResLayout = resLayout;
        mViews = new ArrayList<>();
    }

    public void removeAllViews() {
        mViews.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mBitmaps.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals((LinearLayout) object);
    }

    @Override
    public int getItemPosition(Object object) {
        return mViews.indexOf((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = mActivity.getLayoutInflater();
        View view = inflater.inflate(mResLayout, container, false);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageBitmap);
        imageView.setImageBitmap(mBitmaps.get(position));
        container.addView(view);
        mViews.add(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }


}
