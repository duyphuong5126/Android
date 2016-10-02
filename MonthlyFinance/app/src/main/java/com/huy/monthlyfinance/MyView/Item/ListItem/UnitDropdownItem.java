package com.huy.monthlyfinance.MyView.Item.ListItem;

import android.view.View;
import android.widget.TextView;

import com.huy.monthlyfinance.R;

/**
 * Created by Phuong on 02/10/2016.
 */

public class UnitDropdownItem extends BaseItem {
    private String mUnitName;

    public UnitDropdownItem(String UnitName) {
        this.mUnitName = UnitName;
    }

    @Override
    public void setView(View view) {
        ((TextView) view.findViewById(R.id.itemName)).setText(mUnitName);
    }
}
