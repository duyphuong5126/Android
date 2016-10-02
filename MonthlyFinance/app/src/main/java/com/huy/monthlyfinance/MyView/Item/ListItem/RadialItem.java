package com.huy.monthlyfinance.MyView.Item.ListItem;

import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.huy.monthlyfinance.R;

/**
 * Created by Phuong on 11/09/2016.
 */
public class RadialItem extends BaseItem {
    private Bitmap mImage;
    private String mText;
    private boolean isFocused;
    private LinearLayout mLayoutFocused;

    public RadialItem(String Text, Bitmap Image) {
        this.mImage = Image;
        this.mText = Text;
    }

    @Override
    public void setView(View view) {
        ImageView imageView = (ImageView) view.findViewById(R.id.imageOption);
        mLayoutFocused = (LinearLayout) view.findViewById(R.id.layoutFocused);
        mLayoutFocused.setVisibility(isFocused?View.VISIBLE : View.GONE);
        imageView.setImageBitmap(mImage);
    }

    public void setFocused(boolean focused) {
        isFocused = focused;
    }
}
