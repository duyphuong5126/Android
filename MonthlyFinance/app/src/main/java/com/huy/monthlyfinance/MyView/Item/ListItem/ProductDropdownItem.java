package com.huy.monthlyfinance.MyView.Item.ListItem;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.huy.monthlyfinance.R;

/**
 * Created by Phuong on 01/10/2016.
 */

public class ProductDropdownItem extends BaseItem {
    private Bitmap mBitmap;
    private String mName;

    public ProductDropdownItem(Bitmap Bitmap, String Name) {
        this.mBitmap = Bitmap;
        this.mName = Name;
    }

    @Override
    public void setView(View view) {
        ImageView imageView = (ImageView) view.findViewById(R.id.imageIcon);
        imageView.setImageBitmap(mBitmap);
        TextView textView = (TextView) view.findViewById(R.id.textName);
        textView.setText(mName);
        final ImageButton iconCheck = (ImageButton) view.findViewById(R.id.iconCheck);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iconCheck.setVisibility(iconCheck.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
            }
        });
    }
}
