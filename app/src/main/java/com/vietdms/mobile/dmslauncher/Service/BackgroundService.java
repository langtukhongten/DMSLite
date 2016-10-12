package com.vietdms.mobile.dmslauncher.Service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import CommonLib.SystemLog;
import Controller.ControlThread;

public class BackgroundService extends Service {

    public BackgroundService() {
        synchronized (syncObj) {
            instance = this;
        }
    }

    private static Object syncObj = new Object();
    private static BackgroundService instance = null;
    public static void stopInstance() {
        synchronized (syncObj) {
            if (instance != null) {
                instance.stopSelf();
            }
        }
    }

    @Override
    public void onCreate() {
        Log.i("BackgroundService", "onCreate");
        ControlThread.inst().init(getApplicationContext());
    }

    @Override
    public void onDestroy() {
        Log.i("BackgroundService", "onDestroy");
        SystemLog.addLog(getApplicationContext(), SystemLog.Type.AppStop, null);
        Intent mStartService = new Intent(getApplicationContext(), BackgroundService.class);
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getService(getApplicationContext(), mPendingIntentId, mStartService, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 10000, mPendingIntent);
        System.exit(0);
        ControlThread.inst().requestStop();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("BackgroundService", "onStartCommand");
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
