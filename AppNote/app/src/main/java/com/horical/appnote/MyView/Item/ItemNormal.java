package com.horical.appnote.MyView.Item;

import android.graphics.Bitmap;

import com.horical.appnote.R;

/**
 * Created by Phuong on 30/07/2015.
 */
public class ItemNormal extends BaseItem {
    protected Bitmap mBitmapIllustrateImage;
    protected Bitmap mBitmapIllustrateImageFocus;
    protected String mItemName;

    public Bitmap getBitmapIllustrateImageFocus() {
        return mBitmapIllustrateImageFocus;
    }

    public void setBitmapIllustrateImageFocus(Bitmap BitmapIllustrateImageFocus) {
        this.mBitmapIllustrateImageFocus = BitmapIllustrateImageFocus;
    }

    public ItemNormal() {
        this.mXmlLayout = R.layout.list_menu_item_normal;
        this.mKindOfItem = BaseItem.SideMenu_Normal_Item;
    }

    public Bitmap getIllustrateImage() {
        return mBitmapIllustrateImage;
    }

    public void setIllustrateImage(Bitmap bmIllustrateImage) {
        this.mBitmapIllustrateImage = bmIllustrateImage;
    }

    public String getItemName() {
        return mItemName;
    }

    public void setItemName(String stItemName) {
        this.mItemName = stItemName;
    }

}
