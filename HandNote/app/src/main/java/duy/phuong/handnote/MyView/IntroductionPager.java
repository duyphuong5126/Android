package duy.phuong.handnote.MyView;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import duy.phuong.handnote.R;

/**
 * Created by Phuong on 08/05/2016.
 */
public class IntroductionPager extends PagerAdapter {
    private ArrayList<SpannableString> mTitles;
    private ArrayList<SpannableString> mContents;
    private Activity mActivity;
    private int mResLayout;
    public IntroductionPager(Activity activity, ArrayList<SpannableString> titles, ArrayList<SpannableString> contents, int resLayout) {
        mTitles = new ArrayList<>(titles);
        mContents = new ArrayList<>(contents);
        mActivity = activity;
        mResLayout = resLayout;
    }
    @Override
    public int getCount() {
        return mTitles.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = mActivity.getLayoutInflater();
        View view = inflater.inflate(mResLayout, container, false);
        TextView title = (TextView) view.findViewById(R.id.textTitle);
        title.setText(mTitles.get(position));
        title.setMovementMethod(LinkMovementMethod.getInstance());
        TextView content = (TextView) view.findViewById(R.id.textContent);
        content.setText(mContents.get(position));
        content.setMovementMethod(LinkMovementMethod.getInstance());
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}
