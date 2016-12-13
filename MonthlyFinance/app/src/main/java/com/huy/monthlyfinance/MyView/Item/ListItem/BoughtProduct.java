package com.huy.monthlyfinance.MyView.Item.ListItem;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.huy.monthlyfinance.Model.Product;
import com.huy.monthlyfinance.R;
import com.huy.monthlyfinance.SupportUtils.SupportUtils;

/**
 * Created by Phuong on 05/10/2016.
 */

public class BoughtProduct extends BaseItem {
    private Bitmap mImage;
    private Product mData;
    private double mPrice;
    private boolean isNew;

    public BoughtProduct(Bitmap mImage, double mPrice, boolean isNew, Product mData) {
        this.mImage = mImage;
        this.mData = mData;
        this.mPrice = mPrice;
        this.isNew = isNew;
    }

    @Override
    public void setView(View view) {
        ImageView imageView = (ImageView) view.findViewById(R.id.imageIcon);
        imageView.setImageBitmap(mImage);
        TextView textName = (TextView) view.findViewById(R.id.textName);
        String name = "";
        String countryCode = SupportUtils.getCountryCode();
        if (countryCode.toLowerCase().contains("us")) {
            name = mData.getProductNameEN();
            if (name == null) {
                name = mData.getProductNameVI();
            }
            if (name.isEmpty()) {
                name = mData.getProductNameVI();
            }
        } else {
            name = mData.getProductNameVI();
            if (name == null) {
                name = mData.getProductNameEN();
            }
            if (name.isEmpty()) {
                name = mData.getProductNameEN();
            }
        }
        textName.setText(name);
        TextView textPrice = (TextView) view.findViewById(R.id.textNumber);
        textPrice.setText(String.valueOf(mPrice));
        view.findViewById(R.id.iconNew).setVisibility(isNew ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass().equals(BoughtProduct.class)) {
            BoughtProduct product = (BoughtProduct) obj;
            return mData.getProductNameEN().equals(product.mData.getProductNameEN())
                    && mData.getUnitCalculation().equals(product.mData.getUnitCalculation());
        }
        return false;
    }

    public double getPrice() {
        return mPrice;
    }

    public void setPrice(double Price) {
        this.mPrice = Price;
    }

    public String getName() {
        return mData.getProductNameEN();
    }

    public Bitmap getImage() {
        return mImage;
    }

    public Product getData() {
        return mData;
    }
}
