package com.vietdms.mobile.dmslauncher.CustomClass;

import com.vietdms.mobile.dmslauncher.CustomView.LoadingView;
import com.vietdms.mobile.dmslauncher.Home;


import java.util.ArrayList;

/**
 * Created by Cuongph on 09/06/2016.
 */
public class LayoutLoadingManager {
    static ArrayList<LayoutLoadingItem> lstLoading = new ArrayList<>();

    public static void Show_OnLoading(LoadingView item, String text, int timeOut) {

        if (item.getLoading())
            return;
        LayoutLoadingItem FindItem = null;
        for (LayoutLoadingItem load : lstLoading
                ) {
            if (load.getLoadView() == item) {
                FindItem = load;
                break;
            }
        }
        if (FindItem != null) {
            FindItem.setOnLoading(true, text);
        } else {
            LayoutLoadingItem newItem = new LayoutLoadingItem(item, text, timeOut);
            lstLoading.add(newItem);
            newItem.setOnLoading(true, text);

        }
    }

    public static void Show_OffLoading(LoadingView item) {
        LayoutLoadingItem FindItem = null;
        for (LayoutLoadingItem load : lstLoading
                ) {
            if (load.getLoadView() == item) {
                FindItem = load;
                break;
            }
        }
        if (FindItem != null) {
            FindItem.setOnLoading(false);
        }
    }
}
