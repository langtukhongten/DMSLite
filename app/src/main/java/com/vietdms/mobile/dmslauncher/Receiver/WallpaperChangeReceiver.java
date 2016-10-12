package com.vietdms.mobile.dmslauncher.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;

import com.vietdms.mobile.dmslauncher.Home;
import com.vietdms.mobile.dmslauncher.MyMethod;
import com.vietdms.mobile.dmslauncher.R;

/**
 * Created by Chu Tien on 4/5/2016.
 */
public class WallpaperChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                Home.bindingHome.relaBackground.setBackground(MyMethod.getBackground(context));
                Home.tintManager.setStatusBarTintDrawable(MyMethod.getStatusBackground(context));
                Home.tintManager.setNavigationBarTintDrawable(MyMethod.getNavigationBackground(context));
                Home.bindingHome.relaBackground.invalidate();
                Drawable statusbar = MyMethod.getStatusBackground(context);
                Bitmap statusbitmap = ((BitmapDrawable) statusbar).getBitmap();
                Bitmap status = Home.ProcessingBitmap(statusbitmap, Home.get_ninepatch(R.drawable.bgblack70, statusbitmap.getWidth(), statusbitmap.getHeight(), context));
                Home.tintManager.setStatusBarTintDrawable(new BitmapDrawable(context.getResources(), status));
                Drawable navbar = MyMethod.getNavigationBackground(context);
                Bitmap navbitmap = ((BitmapDrawable) navbar).getBitmap();
                Bitmap nav = Home.ProcessingBitmap(navbitmap, Home.get_ninepatch(R.drawable.bgblack70, navbitmap.getWidth(), navbitmap.getHeight(), context));
                Home.tintManager.setNavigationBarTintDrawable(new BitmapDrawable(context.getResources(), nav));
            }
        } catch (Exception e) {
            Log.e("WALLPAPER", e.toString());
        }

    }
}
