package com.huy.monthlyfinance.Fragments;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.huy.monthlyfinance.Database.DAO.ProductDAO;
import com.huy.monthlyfinance.Database.DAO.ProductGroupDAO;
import com.huy.monthlyfinance.Model.Product;
import com.huy.monthlyfinance.MyView.BasicAdapter;
import com.huy.monthlyfinance.MyView.Item.ListItem.BoughtProduct_1;
import com.huy.monthlyfinance.R;
import com.huy.monthlyfinance.SupportUtils.SupportUtils;

import java.util.ArrayList;

/**
 * Created by Phuong on 15/12/2016.
 */

public class RecommendationFragment extends BaseFragment implements View.OnClickListener {
    private ArrayList<Product> mProducts;
    private ArrayList<BoughtProduct_1> mBoughtProducts;
    private ArrayList<BoughtProduct_1> mBoughtProductsForSearch;

    private ListView mListBoughtProducts;
    private BasicAdapter<BoughtProduct_1> mBoughtProductAdapter;
    private EditText mEdtName;
    private EditText mEdtAccurate;
    private LinearLayout mLayoutEditAccurate;

    @Override
    protected int getLayoutXML() {
        return R.layout.fragment_recommendation;
    }

    @Override
    protected void onPrepare() {
        Activity activity = getActivity();
        Resources resources = activity.getResources();
        ProductDAO productDAO = ProductDAO.getInstance(getActivity());
        ProductGroupDAO productGroupDAO = ProductGroupDAO.getInstance(activity);
        if (mProducts == null) {
            mProducts = new ArrayList<>();
        }
        if (mProducts.isEmpty()) {
            mProducts.addAll(productDAO.getBoughtProducts());
        }
        if (mBoughtProducts == null) {
            mBoughtProducts = new ArrayList<>();
        }
        if (mBoughtProducts.isEmpty()) {
            for (Product product : mProducts) {
                int resId = resources.getIdentifier(product.getProductImage(), "drawable", activity.getPackageName());
                mBoughtProducts.add(new BoughtProduct_1(BitmapFactory.decodeResource(resources, resId),
                        product, productGroupDAO.getGroupNameByID(product.getProductGroupID()), false));
            }
        }
        if (mBoughtProductsForSearch == null) {
            mBoughtProductsForSearch = new ArrayList<>();
        }
        mBoughtProductsForSearch.clear();
        mBoughtProductsForSearch.addAll(mBoughtProducts);
    }

    @Override
    protected void initUI(View view) {
        mListBoughtProducts = (ListView) view.findViewById(R.id.listBoughtProducts);
        mEdtAccurate = (EditText) view.findViewById(R.id.edtAccurate);
        mEdtName = (EditText) view.findViewById(R.id.edtProduct);
        view.findViewById(R.id.buttonMore).setOnClickListener(this);
        view.findViewById(R.id.buttonBack).setOnClickListener(this);
        view.findViewById(R.id.buttonCloseAccurate).setOnClickListener(this);
        mLayoutEditAccurate = (LinearLayout) view.findViewById(R.id.layoutEditAccurate);
    }

    @Override
    protected void setStatusBarColor() {
        mListener.setStatusBarColor(getSideMenuColor());
    }

    @Override
    protected int getSideMenuColor() {
        return Color.parseColor("#FFCC0033");
    }

    @Override
    protected void fragmentReady(Bundle savedInstanceState) {
        Activity activity = getActivity();
        mBoughtProductAdapter = new BasicAdapter<>(mBoughtProductsForSearch, R.layout.item_product, activity.getLayoutInflater());
        mListBoughtProducts.setAdapter(mBoughtProductAdapter);
        mBoughtProductAdapter.notifyDataSetChanged();
        SupportUtils.setListViewHeight(mListBoughtProducts);
        mListBoughtProducts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                for (int id = 0; id < mBoughtProductsForSearch.size(); id++) {
                    mBoughtProductsForSearch.get(id).setFocused(id == i);
                }
                mBoughtProductAdapter.notifyDataSetChanged();
            }
        });
        mEdtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mBoughtProductsForSearch.clear();
                mBoughtProductAdapter.notifyDataSetChanged();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String name = editable.toString();
                for (BoughtProduct_1 boughtProduct : mBoughtProducts) {
                    Product product = boughtProduct.getItem();
                    if (product.getProductNameEN().contains(name.replaceAll("[^\\x00-\\x7F]", "")) ||
                            product.getProductNameVI().contains(name)) {
                        mBoughtProductsForSearch.add(boughtProduct);
                    }
                }
                mBoughtProductAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected boolean canGoBack() {
        return mLayoutEditAccurate.getVisibility() == View.GONE;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonMore:
                mLayoutEditAccurate.setVisibility(View.VISIBLE);
                break;
            case R.id.buttonCloseAccurate:
                mLayoutEditAccurate.setVisibility(View.GONE);
                break;
            case R.id.buttonBack:
                if (canGoBack()) {
                    mNavListener.navBack();
                } else {
                    mLayoutEditAccurate.setVisibility(View.GONE);
                }
                break;
        }
    }
}
