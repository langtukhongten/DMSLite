package com.vietdms.mobile.dmslauncher.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.vietdms.mobile.dmslauncher.Service.BackgroundService;

import CommonLib.SystemLog;

/**
 * Created by ${LTG} on ${10/12/1994}.
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            SystemLog.addLog(context, SystemLog.Type.PhoneStart,null);
            Intent serviceIntent = new Intent(context, BackgroundService.class);
            context.startService(serviceIntent);
        }
    }
}
