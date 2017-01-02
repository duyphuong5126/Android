package com.huy.monthlyfinance.Fragments;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.huy.monthlyfinance.Database.DAO.ProductDAO;
import com.huy.monthlyfinance.Database.DAO.ProductDetailDAO;
import com.huy.monthlyfinance.Database.DAO.ProductGroupDAO;
import com.huy.monthlyfinance.Model.Product;
import com.huy.monthlyfinance.MyView.BasicAdapter;
import com.huy.monthlyfinance.MyView.Item.ListItem.BoughtProduct_1;
import com.huy.monthlyfinance.ProcessData.Apriori;
import com.huy.monthlyfinance.R;
import com.huy.monthlyfinance.SupportUtils.SupportUtils;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

/**
 * Created by Phuong on 15/12/2016.
 */

public class RecommendationFragment extends BaseFragment implements View.OnClickListener {
    private ArrayList<Product> mProducts;
    private ArrayList<BoughtProduct_1> mBoughtProducts;
    private ArrayList<BoughtProduct_1> mBoughtProductsForSearch;
    private ArrayList<BoughtProduct_1> mListRecommends;

    private ListView mListBoughtProducts;
    private ListView mListRecommendation;
    private BasicAdapter<BoughtProduct_1> mBoughtProductAdapter;
    private BasicAdapter<BoughtProduct_1> mRecommendAdapter;
    private EditText mEdtName;
    private EditText mEdtAccurate, mEdtSupport;
    private FrameLayout mLayoutEditAccurate;
    private LinearLayout mLayoutRecommend;
    private Apriori mApriori;
    private ScrollView mScrollRecommend;
    private TextView mTextRecommend;

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
        if (mListRecommendation == null) {
            mListRecommends = new ArrayList<>();
        }
        mListRecommends.clear();
    }

    @Override
    protected void initUI(View view) {
        mListBoughtProducts = (ListView) view.findViewById(R.id.listBoughtProducts);
        mListRecommendation = (ListView) view.findViewById(R.id.listRecommendProducts);
        mEdtAccurate = (EditText) view.findViewById(R.id.edtAccurate);
        mEdtSupport = (EditText) view.findViewById(R.id.edtSupportLevel);
        mEdtName = (EditText) view.findViewById(R.id.edtProduct);
        view.findViewById(R.id.buttonMore).setOnClickListener(this);
        view.findViewById(R.id.buttonBack).setOnClickListener(this);
        view.findViewById(R.id.buttonCloseAccurate).setOnClickListener(this);
        view.findViewById(R.id.buttonRefresh).setOnClickListener(this);
        mLayoutEditAccurate = (FrameLayout) view.findViewById(R.id.layoutEditAccurate);
        mLayoutRecommend = (LinearLayout) view.findViewById(R.id.layoutRecommendResult);
        mScrollRecommend = (ScrollView) view.findViewById(R.id.scrollRecommend);
        mTextRecommend = (TextView) view.findViewById(R.id.txtRecommend);
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
        final Activity activity = getActivity();
        Bundle bundle = getArguments();
        if (bundle != null) {
            boolean openFormSupport = bundle.getBoolean("isOpenSupportForm");
            if (openFormSupport) {
                mLayoutEditAccurate.setVisibility(View.VISIBLE);
            }
        }
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
                ArrayList<BoughtProduct_1> listRecommend = recommend(mBoughtProductsForSearch.get(i), mBoughtProducts);
                if (!listRecommend.isEmpty()) {
                    mListRecommends.clear();
                    mListRecommends.addAll(listRecommend);
                    mRecommendAdapter.notifyDataSetChanged();
                    SupportUtils.setListViewHeight(mListRecommendation);
                    mLayoutRecommend.setVisibility(View.VISIBLE);
                    mBoughtProductAdapter.notifyDataSetChanged();
                    mScrollRecommend.post(new Runnable() {
                        @Override
                        public void run() {
                            mScrollRecommend.fullScroll(View.FOCUS_DOWN);
                        }
                    });
                } else {
                    mLayoutRecommend.setVisibility(View.GONE);
                }
            }
        });

        mRecommendAdapter = new BasicAdapter<>(mListRecommends, R.layout.item_product, activity.getLayoutInflater());
        mListRecommendation.setAdapter(mRecommendAdapter);
        mRecommendAdapter.notifyDataSetChanged();
        SupportUtils.setListViewHeight(mListRecommendation);
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
                String name = SupportUtils.unicode2NonUnicode(editable.toString()).replaceAll("[^\\x00-\\x7F]", "");
                for (BoughtProduct_1 boughtProduct : mBoughtProducts) {
                    Product product = boughtProduct.getItem();
                    String nameViNonUnicode = SupportUtils.unicode2NonUnicode(product.getProductNameVI());
                    nameViNonUnicode = nameViNonUnicode.replaceAll("[^\\x00-\\x7F]", "");
                    if (product.getProductNameEN().toUpperCase().contains(name.toUpperCase()) ||
                            nameViNonUnicode.toUpperCase().contains(name.toUpperCase())) {
                        mBoughtProductsForSearch.add(boughtProduct);
                    }
                }
                mBoughtProductAdapter.notifyDataSetChanged();
            }
        });

        TextWatcher accurateWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                double minSupport = mEdtSupport.getText().toString().isEmpty() ? 0 : Double.valueOf(mEdtSupport.getText().toString());
                double minAccurate = mEdtAccurate.getText().toString().isEmpty() ? 0 : Double.valueOf(mEdtAccurate.getText().toString());
                initAlgorithm(minSupport, minAccurate);
            }
        };

        mEdtAccurate.addTextChangedListener(accurateWatcher);
        mEdtSupport.addTextChangedListener(accurateWatcher);

        double minSupport = mEdtSupport.getText().toString().isEmpty() ? 0 : Double.valueOf(mEdtSupport.getText().toString());
        double minAccurate = mEdtAccurate.getText().toString().isEmpty() ? 0 : Double.valueOf(mEdtAccurate.getText().toString());
        initAlgorithm(minSupport, minAccurate);
    }

    private void initAlgorithm(double minSupport, double minAccurate) {
        final Activity activity = getActivity();
        final Resources resources = activity.getResources();
        mApriori = Apriori.getInstance(minSupport, minAccurate).setExecuteListener(new Apriori.AprioriListener() {
            @Override
            public void onBegin() {
                mListener.toggleProgress(true);
            }

            @Override
            public void onSuccess() {
                Toast.makeText(activity, resources.getString(R.string.done_init_algorithm), Toast.LENGTH_SHORT).show();
                mListener.toggleProgress(false);
            }

            @Override
            public void onError(String message) {
                mListener.toggleProgress(false);
                Toast.makeText(activity, resources.getString(R.string.error_logcat), Toast.LENGTH_SHORT).show();
                Log.d("Apriori error", message);
            }
        }).initialize(ProductDetailDAO.getInstance(activity).getAllDetails());
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
            case R.id.buttonRefresh:
                if (mApriori.isInitialized()) {
                    mApriori.execute();
                } else {

                }
                break;
            default:
                break;
        }
    }

    private ArrayList<BoughtProduct_1> recommend(BoughtProduct_1 product, ArrayList<BoughtProduct_1> source) {
        ArrayList<BoughtProduct_1> products = new ArrayList<>();
        /*if (source != null) {
            Random random = new Random();
            int max = random.nextInt(source.size() > 1 ? source.size() - 1 : 0);
            int seed = max;
            while (max > 0) {
                int id = random.nextInt(seed);
                products.add(source.get(id));
                max--;
            }
        }*/
        Activity activity = getActivity();
        Resources resources = activity.getResources();
        if (product != null && mApriori != null) {
            Set<Integer> productIds = mApriori.generateFrequentAfterCF(Integer.valueOf(product.getItem().getProductID()));
            if (productIds != null) {
                for (int id : productIds) {
                    Product p = ProductDAO.getInstance(activity).getAllProductById(id);
                    int resId = resources.getIdentifier(p.getProductImage(), "drawable", activity.getPackageName());
                    Bitmap bitmap = BitmapFactory.decodeResource(resources, resId);
                    String group = ProductGroupDAO.getInstance(activity).getGroupNameByID(p.getProductGroupID());
                    p.getProductImage();
                    products.add(new BoughtProduct_1(bitmap, p, group, false));
                }
            }
        }
        return products;
    }

    @Override
    public void refreshData() {

    }
}
