package com.vietdms.mobile.dmslauncher.CustomClass;

import android.content.Context;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;

import com.vietdms.mobile.dmslauncher.Fragment.RightFragment;
import com.vietdms.mobile.dmslauncher.Home;
import com.vietdms.mobile.dmslauncher.MyMethod;

import java.util.ArrayList;

/**
 * Created by Cuongph on 27/05/2016.
 */
public class LayoutShow {
    ArrayList<LayoutManagerObject> LayoutManager;

    public LayoutShow() {
        LayoutManager = new ArrayList<>();
    }

    public void AddLayout(LayoutManagerObject Layout) {
        if (Layout.LayoutView != null)
            if (Layout.LName != RightFragment.Layouts.Main)
                Layout.LayoutView.setVisibility(View.GONE);
        this.LayoutManager.add(Layout);
    }

    public void ShowDialog(RightFragment.Layouts Name) {
        for (LayoutManagerObject layout : LayoutManager
                ) {
            if (layout.LName.equals(Name)) {
                if (layout.LayoutView == null)
                    continue;
                layout.LayoutView.setVisibility(View.VISIBLE);
                break;
            }
        }
    }

    public void HideDialog(RightFragment.Layouts Name) {
        for(LayoutManagerObject layout : LayoutManager) {
            if(layout.LName.equals(Name)) {
                if (layout.LayoutView == null)
                    continue;

                layout.LayoutView.setVisibility(View.INVISIBLE);
                break;
            }
        }

    }



    public void ShowLayout(RightFragment.Layouts Name) {
        if(Name == RightFragment.Layouts.Setting){
            RightFragment.mWakeLock.acquire();
            Log.w( "ShowLayout: ", "Acquire WakeLock");
        }else{
            if(RightFragment.mWakeLock.isHeld())
            {
                RightFragment.mWakeLock.release() ;
                Log.w( "ShowLayout: ", "Release WakeLock");
            }
        }
        if (Name == RightFragment.Layouts.Main) destroyVariable();
        MyMethod.setVisible(Home.bindingHome.txtTile);
        MyMethod.setGone(Home.bindingHome.btnComeBack);
        for (LayoutManagerObject layout : LayoutManager
                ) {
            if (layout.LName.equals(Name)) {
                if (layout.LayoutView == null)
                    continue;
                layout.LayoutView.setVisibility(View.VISIBLE);
//                if (Home.locationManager != null && Home.locationManager != null)
//                    Home.locationManager.removeUpdates(Home.locationListener);
            } else {
                if (layout.LayoutView != null)
                    layout.LayoutView.setVisibility(View.GONE);
            }
        }
    }

    private void destroyVariable() {
        if (Home.orderDetailArrayList != null) {
            Home.orderDetailArrayList.clear();
            Home.orderListProductAdapter.notifyDataSetChanged();
        }
        if (Home.timelinesArrayList != null) Home.timelinesArrayList.clear();

    }

    public void ShowLayout(RightFragment.Layouts Name, Context context, int menu_layout) {
        for (LayoutManagerObject layout : LayoutManager
                ) {
            if (layout.LName.equals(Name)) {
                if (layout.LayoutView == null)
                    continue;
                layout.LayoutView.setVisibility(View.VISIBLE);
            } else {
                if (layout.LayoutView != null)
                    layout.LayoutView.setVisibility(View.GONE);
            }
        }
    }
}
