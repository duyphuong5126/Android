package com.horical.appnote.MyView.Item;

import com.horical.appnote.R;
import com.horical.appnote.Supports.LanguageUtils;

/**
 * Created by Phuong on 30/07/2015.
 */
public class ItemNormalWithNotification extends ItemNormal {
    private String mNotification;

    public ItemNormalWithNotification() {
        this.mXmlLayout = R.layout.list_menu_item_normal_with_notification;
        this.mKindOfItem = SideMenu_Notification_Item;
    }

    public String getNotification() {
        if (mNameOfItem.equals(LanguageUtils.getCalendarString())) {
            int calendar_value = Integer.valueOf(mNotification);
            if (calendar_value == 0) {
                return "";
            } else {
                return (calendar_value < 5)?calendar_value+"":calendar_value+"+";
            }
        }
        if (mNameOfItem.equals(LanguageUtils.getFileManagerString())) {
            int file_value = Integer.valueOf((mNotification == null)?"0":mNotification);
            if (file_value == 0) {
                return "";
            }
            else {
                return (file_value < 20)?file_value+"":file_value+"+";
            }
        }
        return "";
    }

    public void setNotification(String stNotification) {
        this.mNotification = stNotification;
    }

    public int getNotificationBackground() {
        if (mNameOfItem.equals(LanguageUtils.getCalendarString())) {
            int calendar_value = Integer.valueOf(mNotification);
            if (calendar_value < 5) {
                return R.drawable.circle_green_bg;
            } else {
                if (calendar_value < 10) {
                    return R.drawable.circle_yellow_bg;
                } else {
                    return R.drawable.circle_red_bg;
                }
            }
        }
        if (mNameOfItem.equals(LanguageUtils.getFileManagerString())) {
            int file_value = Integer.valueOf(mNotification);
            if (file_value < 20) {
                return R.drawable.circle_green_bg;
            } else {
                if (file_value < 50) {
                    return R.drawable.circle_yellow_bg;
                } else {
                    return R.drawable.circle_red_bg;
                }
            }
        }
        return -1;
    }
}
