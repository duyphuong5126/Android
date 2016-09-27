package com.huy.monthlyfinance.MyView.Item.ListItem;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.huy.monthlyfinance.R;

/**
 * Created by Phuong on 03/09/2016.
 */
public class ExpensesItem extends BaseItem {
    private String mText1;
    private String mText2;
    private int mMax;
    private int mCurrent;
    private int mImage;
    private int mDrawable;
    private int mProgressDrawable;
    private static Context mContext;

    public ExpensesItem(Context context, String Text1, String Text2, int Max, int Current, int Image, int Drawable, int ProgressDrawable) {
        this.mText1 = Text1;
        this.mText2 = Text2;
        this.mMax = Max;
        this.mCurrent = Current;
        this.mImage = Image;
        this.mDrawable = Drawable;
        this.mProgressDrawable = ProgressDrawable;
        if (mContext == null) {
            mContext = context;
        }
    }

    public String getText1() {
        return mText1;
    }

    public String getText2() {
        return mText2;
    }

    public int getMax() {
        return mMax;
    }

    public int getCurrent() {
        return mCurrent;
    }

    public int getImage() {
        return mImage;
    }

    public int getDrawable() {
        return mDrawable;
    }

    public int getProgressDrawable() {
        return mProgressDrawable;
    }

    @Override
    public void setView(View view) {
        ImageButton icon = (ImageButton) view.findViewById(R.id.buttonIcon);
        icon.setImageResource(mImage);
        icon.setBackgroundResource(mDrawable);
        TextView field1 = (TextView) view.findViewById(R.id.textField1);
        field1.setText(mText1);
        TextView field2 = (TextView) view.findViewById(R.id.textField2);
        field2.setText(mText2);
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.itemProgress);
        progressBar.setMax(mMax);
        progressBar.setProgress(mCurrent);
        progressBar.setProgressDrawable(mContext.getResources().getDrawable(mProgressDrawable));
    }
}
