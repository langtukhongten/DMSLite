package com.vietdms.mobile.dmslauncher.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import CommonLib.AppManager;

/**
 * Created by Chu Tien on 3/28/2016.
 */
public class AppChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        String packageName = intent.getData().getEncodedSchemeSpecificPart();
        switch (intent.getAction())
        {
           case Intent.ACTION_PACKAGE_REMOVED:
               Log.w("AppChangeReceiver", "Gỡ bỏ : " + packageName);
               if (intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)) {
                   Log.w("AppChangeReceiver", "EXTRA_REPLACING");
               }
               else {
                   AppManager.inst().remPackage(packageName);
               }
               break;
           case Intent.ACTION_PACKAGE_ADDED:
               Log.w("AppChangeReceiver", "Cài mới : " + packageName);
               if (intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)) {
                   Log.w("AppChangeReceiver", "EXTRA_REPLACING");
               }
               else {
                   AppManager.inst().addPackage(packageName);
               }
               break;
           default:
               Log.w("AppChangeReceiver", "Chưa rõ : " + packageName);
               break;
        }
    }
}
