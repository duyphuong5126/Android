package com.huy.monthlyfinance.MyView.Item.ListItem;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.huy.monthlyfinance.R;

/**
 * Created by Phuong on 30/10/2016.
 */

public class ProductImageItem extends BaseItem {
    private Bitmap mBitmap;

    public ProductImageItem(Bitmap Bitmap) {
        this.mBitmap = Bitmap;
    }

    @Override
    public void setView(View view) {
        ImageView imageView = (ImageView) view.findViewById(R.id.itemImage);
        imageView.setImageBitmap(mBitmap);
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }
}
