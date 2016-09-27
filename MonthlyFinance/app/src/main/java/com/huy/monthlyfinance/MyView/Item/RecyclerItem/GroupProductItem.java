package com.huy.monthlyfinance.MyView.Item.RecyclerItem;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.huy.monthlyfinance.R;

/**
 * Created by Phuong on 24/09/2016.
 */
public class GroupProductItem extends BaseRecyclerItem {
    private Bitmap mIcon;
    private String mName;

    private ImageButton mItemIcon;
    private TextView mItemName;

    public GroupProductItem(Bitmap Icon, String Name) {
        this.mIcon = Icon;
        this.mName = Name;
    }

    @Override
    public void setUpUI(View view) {
        if (mItemIcon == null) {
            mItemIcon = (ImageButton) view.findViewById(R.id.itemIcon);
        }
        if (mItemName == null) {
            mItemName = (TextView) view.findViewById(R.id.itemName);
        }
        mItemIcon.setImageBitmap(mIcon);
        mItemName.setText(mName);
    }
}
