package com.huy.monthlyfinance.MyView.Item.ListItem;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.huy.monthlyfinance.Model.Product;
import com.huy.monthlyfinance.R;
import com.huy.monthlyfinance.SupportUtils.SupportUtils;

import java.util.Random;

/**
 * Created by Phuong on 16/12/2016.
 */

public class BoughtProduct_1 extends BaseItem {
    private Bitmap mBitmap;
    private Product mItem;
    private String mGroup;
    private static final int[] mCircleDrawables = {R.drawable.circle_blue_1, R.drawable.circle_blue_2, R.drawable.circle_dark_blue,
            R.drawable.circle_dark_gray_1, R.drawable.circle_dark_gray_2, R.drawable.circle_light_green_1, R.drawable.circle_light_green_2,
            R.drawable.circle_dark_red, R.drawable.circle_orange, R.drawable.circle_pink_1, R.drawable.circle_gray};

    public BoughtProduct_1(Bitmap bitmap, Product item, String group) {
        this.mBitmap = bitmap;
        this.mItem = item;
        this.mGroup = group;
    }

    @Override
    public void setView(View view) {
        ImageView imageView = (ImageView) view.findViewById(R.id.imgIcon);
        imageView.setImageBitmap(mBitmap);
        int max = mCircleDrawables.length - 1;
        Random random = new Random();
        imageView.setBackgroundResource(mCircleDrawables[random.nextInt(max)]);
        TextView txtName = (TextView) view.findViewById(R.id.txtName);
        TextView txtGroup = (TextView) view.findViewById(R.id.txtGroup);
        txtName.setText(SupportUtils.getCountryCode().toLowerCase().contains("us") ? mItem.getProductNameEN() : mItem.getProductNameVI());
        txtGroup.setText(mGroup);
    }
}
