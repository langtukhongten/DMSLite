package com.vietdms.mobile.dmslauncher.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import CommonLib.SystemLog;

/**
 * Created by Dms.Tien on 03/03/2016.
 */
public class ShutDownReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SystemLog.addLog(context, SystemLog.Type.PhoneStop, null);
    }
}
