package com.vietdms.mobile.dmslauncher.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import CommonLib.EventPool;
import CommonLib.EventType;
import CommonLib.Model;
import CommonLib.WakeLock;

/**
 * Created by DMSv4 on 12/22/2015.
 */
public class AlarmReceiverRealtime extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            WakeLock.inst().acquire();
            Model.inst().setLastRealtimeTimer();
            EventPool.control().enQueue(new EventType.EventBase(EventType.Type.AlarmTriggerRealtime));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}