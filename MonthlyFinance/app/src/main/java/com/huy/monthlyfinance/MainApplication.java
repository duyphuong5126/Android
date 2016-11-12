package com.huy.monthlyfinance;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.widget.Toast;

import com.huy.monthlyfinance.Database.DAO.ProductDAO;
import com.huy.monthlyfinance.Database.DAO.ProductGroupDAO;
import com.huy.monthlyfinance.Model.Product;
import com.huy.monthlyfinance.Model.ProductGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by Phuong on 07/11/2016.
 */

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Context context = getApplicationContext();
        Resources res = context.getResources();


        ProductGroupDAO mProductGroup = ProductGroupDAO.getInstance(context);
        ProductDAO mProducts = ProductDAO.getInstance(context);
        ArrayList<ProductGroup> groups = new ArrayList<>();
        groups.add(new ProductGroup("Living services", "Dịch vụ", res.getResourceEntryName(R.drawable.receipt)));
        groups.add(new ProductGroup("Health", "Sức khỏe", res.getResourceEntryName(R.drawable.stethoscope)));
        groups.add(new ProductGroup("Entertainment", "Giải trí", res.getResourceEntryName(R.drawable.game_controller)));
        groups.add(new ProductGroup("Food", "Thực phẩm", res.getResourceEntryName(R.drawable.turkey)));
        groups.add(new ProductGroup("Dress", "May mặc", res.getResourceEntryName(R.drawable.shirt)));
        groups.add(new ProductGroup("Transport", "Giao thông, vận chuyển", res.getResourceEntryName(R.drawable.car)));
        groups.add(new ProductGroup("Home", "Chi phí nhà cửa", res.getResourceEntryName(R.drawable.home)));
        groups.add(new ProductGroup("Family", "Gia đình", res.getResourceEntryName(R.drawable.family)));
        groups.add(new ProductGroup("Etc", "Khác", res.getResourceEntryName(R.mipmap.ic_more_horiz_white_24dp)));
        if (mProductGroup.getAllProductGroup().isEmpty()) {
            boolean result = true;
            for (int i = 0; i < groups.size() && result; i++) {
                result = mProductGroup.insertProductGroup(groups.get(i));
            }
        }

        ArrayList<Product> products = new ArrayList<>();
        products.add(new Product());
        if (mProducts.getAllProduct().isEmpty()) {

        }

    }
}
