package com.huy.monthlyfinance;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.huy.monthlyfinance.Database.DAO.ProductGroupDAO;
import com.huy.monthlyfinance.Model.Product;
import com.huy.monthlyfinance.Model.ProductGroup;
import com.huy.monthlyfinance.MyView.BasicAdapter;
import com.huy.monthlyfinance.MyView.Item.ListItem.ProductImageItem;
import com.huy.monthlyfinance.SupportUtils.SupportUtils;

import java.util.ArrayList;

public class ProductGroupActivity extends AppCompatActivity implements View.OnClickListener {
    private ArrayList<ProductImageItem> mImageItems;
    private EditText mGroupName;
    private ImageView mGroupImage;
    private int mCurrentPosition;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = getLayoutInflater();
        setContentView(R.layout.activity_product_group);
        mCurrentPosition = -1;
        mGroupImage = (ImageView) findViewById(R.id.imageGroup);
        mImageItems = MainApplication.getInstance().getListProductsImage();
        BasicAdapter<ProductImageItem> mImagesAdapter = new BasicAdapter<>(mImageItems, R.layout.item_image, inflater);
        GridView gridView = (GridView) findViewById(R.id.gridImages);
        gridView.setAdapter(mImagesAdapter);
        SupportUtils.setGridViewHeight(gridView, 5, (int) SupportUtils.dip2Pixel(ProductGroupActivity.this, 54));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mGroupImage.setVisibility(View.VISIBLE);
                mGroupImage.setImageBitmap(mImageItems.get(i).getBitmap());
                mCurrentPosition = i;
            }
        });
        mGroupName = (EditText) findViewById(R.id.edtProductGroup);
        findViewById(R.id.buttonBack).setOnClickListener(this);
        findViewById(R.id.buttonConfirm).setOnClickListener(this);
        findViewById(R.id.buttonCancel).setOnClickListener(this);
        if (SupportUtils.checkLollipopOrAbove()) {
            getWindow().setStatusBarColor(Color.parseColor("#a24ade"));
        }
    }

    @Override
    public void onClick(View view) {
        Resources resources = getResources();
        Context context = ProductGroupActivity.this;
        switch (view.getId()) {
            case R.id.buttonConfirm:
                ProductGroupDAO productGroupDAO = ProductGroupDAO.getInstance(context);
                /*if (mCurrentPosition > 0) {
                    String name = mGroupName.getText().toString();
                    if (!name.isEmpty()) {
                        boolean result = productGroupDAO.insertProductGroup(
                                new ProductGroup(name, name, mImageItems.get(mCurrentPosition).getDrawableName()));
                        if (result) {
                            Toast.makeText(context, resources.getString(R.string.info_saved), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, resources.getString(R.string.info_saved), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, resources.getString(R.string.error_missing_info), Toast.LENGTH_SHORT).show();
                    }
                }*/
                finish();
                break;
            case R.id.buttonCancel:
                mGroupImage.setVisibility(View.GONE);
                mGroupName.setText("");
                mCurrentPosition = -1;
                break;
            case R.id.buttonBack:
                finish();
                break;
        }
    }
}
