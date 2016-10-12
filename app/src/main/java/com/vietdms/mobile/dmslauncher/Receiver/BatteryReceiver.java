package com.vietdms.mobile.dmslauncher.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

import CommonLib.Model;

public class BatteryReceiver extends BroadcastReceiver {
    public BatteryReceiver() {

    }
    @Override
    public void onReceive(Context context, Intent intent) {
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        if (level >= 0 && scale > 0) {
            Model.inst().setLastBatteryLevel(level * 100 / scale);
        }
    }
}
