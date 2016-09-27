package com.huy.monthlyfinance.MyView.Item.ListItem;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.huy.monthlyfinance.R;

/**
 * Created by Phuong on 11/09/2016.
 */
public class RadialItem extends BaseItem {
    private Bitmap mImage;
    private String mText;
    private int mPosition;
    private OnClickListener mListener;
    private LinearLayout mLayoutFocused;

    public interface OnClickListener {
        void onClick(String data, int position);
        void onLongClick(String data, int position);
    }

    public RadialItem(OnClickListener Listener, String Text, Bitmap Image, int Position) {
        this.mImage = Image;
        this.mText = Text;
        this.mListener = Listener;
        this.mPosition = Position;
    }

    @Override
    public void setView(View view) {
        ImageView imageView = (ImageView) view.findViewById(R.id.imageOption);
        mLayoutFocused = (LinearLayout) view.findViewById(R.id.layoutFocused);
        imageView.setImageBitmap(mImage);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onClick(mText, mPosition);
            }
        });
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mListener.onLongClick(mText, mPosition);
                return false;
            }
        });
    }

    public void setFocused(boolean focused) {
        if (mLayoutFocused != null) {
            mLayoutFocused.setVisibility(focused? View.VISIBLE : View.GONE);
        }
    }
}
