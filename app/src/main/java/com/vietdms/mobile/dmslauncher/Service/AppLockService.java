package com.vietdms.mobile.dmslauncher.Service;


import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.vietdms.mobile.dmslauncher.Home;
import com.vietdms.mobile.dmslauncher.LockView;

import java.util.HashMap;
import java.util.Map;

import CommonLib.AppManager;
import CommonLib.Const;
import CommonLib.EventPool;
import CommonLib.EventType;
import CommonLib.Model;
import CommonLib.SystemLog;
import Controller.ControlThread;

/**
 * Created by Chu Tien on 4/12/2016.
 */
public class AppLockService extends AccessibilityService {
    private static final String HOMEPACKAGE = "com.vietdms.mobile.dmslauncher";
    private static final String SETTINGS = "com.android.settings";
    private static final String TAG = "AppLock";
    public static String lastFrontAppPkg = "";
    private ActivityManager activityManager;
    public static Map<String, Boolean> mUnlockMap;
    private AppLockService init = null;
    private ComponentName componentName;
    private String temp = "";

    @Override
    public void onDestroy() {
//        try {
        SystemLog.addLog(getApplicationContext(), SystemLog.Type.AppStop, null);
        Intent mStartService = new Intent(getApplicationContext(), BackgroundService.class);
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getService(getApplicationContext(), mPendingIntentId, mStartService, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 10000, mPendingIntent);
        System.exit(0);
        ControlThread.inst().requestStop();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        //Configure these here for compatibility with API 13 and below.
//        try {
        AccessibilityServiceInfo config = new AccessibilityServiceInfo();
        config.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
        config.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        if (Build.VERSION.SDK_INT >= 16)
            //Just in case this helps
            config.flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS;
        setServiceInfo(config);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    private void init() {
        init = new AppLockService();
        mUnlockMap = new HashMap<>();
        activityManager = (ActivityManager) getApplicationContext().getSystemService(ACTIVITY_SERVICE);
    }

    private void unlockAll() {
        mUnlockMap.clear();
    }

    private void showLocker() {
        Intent lockIntent = new Intent(getApplicationContext(), LockView.class);
        lockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle data = new Bundle();
        data.putString("lock", lastFrontAppPkg);
        data.putString("pre", temp);
        lockIntent.putExtra("data", data);
        getApplicationContext().startActivity(lockIntent);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            Log.w(TAG, "BlackList = " + Model.inst().getConfigValue(Const.ConfigKeys.LockApps));
            Log.w(TAG, "WhiteList = " + Model.inst().getConfigValue(Const.ConfigKeys.WhiteListApp));
            boolean isActivity = false;
            try {
                if (init == null) init();
                componentName = new ComponentName(
                        event.getPackageName().toString(),
                        event.getClassName().toString()
                );
                ActivityInfo activityInfo = tryGetActivity(componentName);
                isActivity = activityInfo != null;
            } catch (Exception e) {
                Log.e("onAccessibilityEvent", e.getMessage(), e);
            }

            try {
                if (isActivity) {
                    Log.w("CurrentActivity", componentName.getPackageName());
                    if (!lastFrontAppPkg.equals(componentName.getPackageName())) {
                        if (temp != lastFrontAppPkg && !lastFrontAppPkg.contains(HOMEPACKAGE)) {
                            unlockAll();
                        }
                        temp = lastFrontAppPkg;
                        lastFrontAppPkg = componentName.getPackageName();
                        String whiteListApp = Model.inst().getConfigValue(Const.ConfigKeys.WhiteListApp);
                        if (whiteListApp != null) {
                            if (whiteListApp.length() == 0) {
                                if (!AppManager.inst().isLocked(lastFrontAppPkg)) {
                                    unlockAll();
                                    EventPool.control().enQueue(new EventType.EventRunAppRequest(lastFrontAppPkg));
                                    Log.w("APPLOCK", "Khóa lại ứng dụng");
                                } else {
                                    if (mUnlockMap.get(lastFrontAppPkg) == null || !mUnlockMap.get(lastFrontAppPkg)) {
                                        Log.w("APPLOCK", "Hiện màn hình khóa");
                                        showLocker();
                                    }
                                }
                            } else {
                                if (Model.inst().getConfigValue(Const.ConfigKeys.WhiteListApp).contains(lastFrontAppPkg)) {
                                    unlockAll();
                                    EventPool.control().enQueue(new EventType.EventRunAppRequest(lastFrontAppPkg));
                                    Log.w("APPLOCK", "Nằm trong WhiteList");
                                } else {
                                    if (mUnlockMap.get(lastFrontAppPkg) == null || !mUnlockMap.get(lastFrontAppPkg)) {
                                        if (!lastFrontAppPkg.contains(HOMEPACKAGE)) {
                                            if (Home.isAppLockStop && lastFrontAppPkg.contains(SETTINGS)) {
                                                Log.w("APPLOCK", "Đang thực hiện mở theo yêu cầu app nên không khóa");
                                            } else {
                                                Log.w("APPLOCK", "Khóa hết");
                                                showLocker();
                                            }
                                        }

                                    }
                                }
                            }
                        }


                    }
                }
            } catch (Exception e) {
                Log.e("onAccessibilityEvent", e.getMessage(), e);
            }


        }
    }

    private ActivityInfo tryGetActivity(ComponentName componentName) {
        try {
            return getPackageManager().getActivityInfo(componentName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    @Override
    public void onInterrupt() {

    }
}
