package com.huy.monthlyfinance.MyView.Item.ListItem;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.huy.monthlyfinance.R;

/**
 * Created by Phuong on 05/10/2016.
 */

public class BoughtProduct extends BaseItem {
    private Bitmap mImage;
    private String mName;
    private String mUnit;
    private double mPrice;
    private int mAmount;
    private boolean isNew;

    public BoughtProduct(Bitmap Image, String Name, String Unit, double Price, int Amount, boolean isNew) {
        this.mImage = Image;
        this.mName = Name;
        this.mUnit = Unit;
        this.mPrice = Price;
        this.mAmount = Amount;
        this.isNew = isNew;
    }

    public BoughtProduct(Bitmap Image, String Name, String Unit, double Price, boolean isNew) {
        this.mImage = Image;
        this.mName = Name;
        this.mUnit = Unit;
        this.mPrice = Price;
        this.isNew = isNew;
    }

    @Override
    public void setView(View view) {
        ImageView imageView = (ImageView) view.findViewById(R.id.imageIcon);
        imageView.setImageBitmap(mImage);
        TextView textName = (TextView) view.findViewById(R.id.textName);
        textName.setText(mName);
        TextView textPrice = (TextView) view.findViewById(R.id.textNumber);
        textPrice.setText(String.valueOf(mPrice));
        view.findViewById(R.id.iconNew).setVisibility(isNew ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean equals(Object obj) {
        BoughtProduct product = (BoughtProduct) obj;
        if (product != null) {
            return mName.equals(product.mName) && mUnit.equals(product.mUnit);
        }
        return super.equals(obj);
    }

    public int getAmount() {
        return mAmount;
    }

    public void setAmount(int Amount) {
        this.mAmount = Amount;
    }

    public double getPrice() {
        return mPrice;
    }

    public void setPrice(double Price) {
        this.mPrice = Price;
    }

    public String getName() {
        return mName;
    }
    public Bitmap getImage() {
        return mImage;
    }
}
