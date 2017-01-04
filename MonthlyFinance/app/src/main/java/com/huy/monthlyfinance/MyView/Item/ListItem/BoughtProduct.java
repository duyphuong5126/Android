package com.huy.monthlyfinance.MyView.Item.ListItem;

import android.graphics.Bitmap;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.huy.monthlyfinance.Model.Product;
import com.huy.monthlyfinance.R;
import com.huy.monthlyfinance.SupportUtils.PreferencesUtils;
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
    public void setView(final View view) {
        ImageView imageView = (ImageView) view.findViewById(R.id.imageIcon);
        imageView.setImageBitmap(mImage);
        TextView textName = (TextView) view.findViewById(R.id.textName);
        TextView textCurrency = (TextView) view.findViewById(R.id.textCurrency);
        String name;
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
        final String mCurrency = PreferencesUtils.getString(PreferencesUtils.CURRENCY, "VND");
        textCurrency.setText(mCurrency);
        final EditText textPrice = (EditText) view.findViewById(R.id.textNumber);
        textPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String textCost = editable.toString();
                double cost = textCost.isEmpty() ? 0 : Double.valueOf(textCost);
                if (mCurrency.equals("VND")) {
                    if (cost / SupportUtils.MIN_CURRENCY >= 1) {
                        mPrice = cost;
                        Log.d("Cost", "" + cost);
                    }
                }
            }
        });
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
