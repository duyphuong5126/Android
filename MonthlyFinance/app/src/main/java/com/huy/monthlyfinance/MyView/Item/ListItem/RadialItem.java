package com.huy.monthlyfinance.MyView.Item.ListItem;

import android.graphics.Bitmap;
import android.os.Bundle;
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

    public RadialItem(OnClickListener listener, String Text, Bitmap Image) {
        this.mImage = Image;
        this.mText = Text;
        this.mListener = listener;
    }

    public interface OnClickListener {
        void onClick(Bundle data);
        void onLongClick(Bundle data);
    }

    private OnClickListener mListener;

    @Override
    public void setView(View view) {
        ImageView imageView = (ImageView) view.findViewById(R.id.imageOption);
        mLayoutFocused = (LinearLayout) view.findViewById(R.id.layoutFocused);
        mLayoutFocused.setVisibility(isFocused?View.VISIBLE : View.GONE);
        imageView.setImageBitmap(mImage);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isFocused = !isFocused;
                mLayoutFocused.setVisibility(isFocused?View.VISIBLE : View.GONE);
                View viewParent = view.getRootView();
                viewParent.invalidate();
                mListener.onClick(null);
            }
        });
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mListener.onLongClick(null);
                return false;
            }
        });
    }

    public void setFocused(boolean focused) {
        isFocused = focused;
    }
}
