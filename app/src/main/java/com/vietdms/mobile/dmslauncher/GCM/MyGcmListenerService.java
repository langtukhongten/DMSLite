package com.vietdms.mobile.dmslauncher.GCM;

/**
 * Created by ${LTG} on ${10/12/1994}.
 */

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.youtube.player.internal.w;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.vietdms.mobile.dmslauncher.MyMethod;
import com.vietdms.mobile.dmslauncher.Service.BackgroundService;
import com.vietdms.mobile.dmslauncher.Service.MessageService;

import CommonLib.AlarmTimer;
import CommonLib.Const;
import CommonLib.EventPool;
import CommonLib.EventType;
import CommonLib.LocationDetector;
import CommonLib.Model;
import CommonLib.SystemLog;
import Controller.NetworkTransaction;

public class MyGcmListenerService extends FirebaseMessagingService {

    private static final String TAG = "MyGcmListenerService";
    private Context context;
    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;
    /**
     * Called when message is received.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        context = getApplicationContext();
        powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "wakeLock");
        wakeLock.acquire();
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "FCM Message Id: " + remoteMessage.getMessageId());
        Log.d(TAG, "FCM Notification Message: " + remoteMessage.getNotification());
        Log.d(TAG, "FCM Data Message: " + remoteMessage.getData());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            new ProcessGcmMessage().execute(remoteMessage.getData().get("message"));
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            String message = remoteMessage.getNotification().getBody();

            Log.d(TAG, "Message FCM: " + message);
            new ProcessGcmMessage().execute(message);
        }

        wakeLock.release();
    }
    // [END receive_message]

    private class ProcessGcmMessage extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String message = params[0];
            int pos = message.indexOf(' ');
            if (pos > 0) {
                String typeStr = message.substring(0, pos);
                String dataStr = message.substring(pos);
                long dataLong = 0;
                Const.GCMMessageType type = Const.GCMMessageType.NotifyMessage;
                try {
                    int typeValue = Integer.parseInt(typeStr);
                    boolean matched = false;
                    for (int j = 0; j < Const.GCMMessageType.values().length; j++) {
                        if (Const.GCMMessageType.values()[j].ordinal() == typeValue) {
                            type = Const.GCMMessageType.values()[j];
                            matched = true;
                            break;
                        }
                    }
                    if (!matched) {
                        Log.w("ProcessGcmMessage", "unhandled type " + typeValue);
                        return null;
                    }
                    dataLong = Long.parseLong(dataStr);
                } catch (Exception ex) {
                }
                switch (type) {
                    case NotifyMessage:
                        MyMethod.sendNotification(getApplicationContext(), dataStr);
                        EventPool.view().enQueue(new EventType.EventGCMMessageToView(dataStr, Model.getServerTime()));
                        break;
                    case SystemLog:
                        if (dataLong == 0 || Math.abs(Model.getServerTime() - dataLong) < Model.inst().getConfigValue(Const.ConfigKeys.GcmCommandTimeout, Const.DefaultGcmCommandTimeoutInSeconds) * 1000) {
                            if (!NetworkTransaction.inst(context).sendSystemLog()) {
                                SystemLog.inst().addLog(SystemLog.Type.Error, "Không thể gửi nhật ký hệ thống theo yêu cầu");
                            }
                        }
                        break;
                    case SystemInfo:
                        if (dataLong == 0 || Math.abs(Model.getServerTime() - dataLong) < Model.inst().getConfigValue(Const.ConfigKeys.GcmCommandTimeout, Const.DefaultGcmCommandTimeoutInSeconds) * 1000) {
                            if (!NetworkTransaction.inst(context).sendSystemInfo()) {
                                SystemLog.inst().addLog(SystemLog.Type.Error, "Không thể gửi thông tin toàn hệ thống theo yêu cầu");
                            }
                        }
                        break;
                    case SendTracking:
                        if (dataLong == 0 || Math.abs(Model.getServerTime() - dataLong) < Model.inst().getConfigValue(Const.ConfigKeys.GcmCommandTimeout, Const.DefaultGcmCommandTimeoutInSeconds) * 1000) {
                            if (!NetworkTransaction.inst(context).sendTracking(false)) {
                                SystemLog.inst().addLog(SystemLog.Type.Error, "Không thể gửi thông tin tracking theo yêu cầu");
                            }
                        }
                        break;
                    case RestartApp:
                        if (dataLong == 0 || Math.abs(Model.getServerTime() - dataLong) < Model.inst().getConfigValue(Const.ConfigKeys.GcmCommandTimeout, Const.DefaultGcmCommandTimeoutInSeconds) * 1000) {
                            BackgroundService.stopInstance();
                        }
                        break;
                    case StartRealtimeTracking:
                        if (dataLong == 0 || Math.abs(Model.getServerTime() - dataLong) < Model.inst().getConfigValue(Const.ConfigKeys.GcmCommandTimeout, Const.DefaultGcmCommandTimeoutInSeconds) * 1000 && Model.inst().getConfigValue(Const.ConfigKeys.isActive,1) == 1) {
                            Model.inst().setConfigValue(Const.ConfigKeys.RealtimeTrackingTime, "60");
                            LocationDetector.inst().updateRealtime(true);
                            NetworkTransaction.inst(context).sendTracking(true);
                            Model.inst().setLastRealtimeClock();
                            AlarmTimer.inst().setRealtimeTimer(Model.inst().getConfigValue(Const.ConfigKeys.RealtimeTrackingInterval, Const.DefaultRealtimeTrackingIntervalInSeconds));
                        }
                        break;
                    case StopRealtimeTracking:
                        if (dataLong == 0 || Math.abs(Model.getServerTime() - dataLong) < Model.inst().getConfigValue(Const.ConfigKeys.GcmCommandTimeout, Const.DefaultGcmCommandTimeoutInSeconds) * 1000 && Model.inst().getConfigValue(Const.ConfigKeys.isActive,1) == 1) {
                            Model.inst().setConfigValue(Const.ConfigKeys.RealtimeTrackingTime, "0");
                            LocationDetector.inst().updateRealtime(false);
                        }
                        break;
                    case Transaction:
                        //Tin nhắn giao dịch
                        if(MyMethod.isCanNotice()) {
                            Log.w("FIREBASE TRANSACTION", dataStr);
                            Intent messageService = new Intent(context, MessageService.class);
                            messageService.putExtra("MessageBody", dataStr);
                            messageService.putExtra("API", Const.UpdateVersion);
                            context.startService(messageService);
                        }
                        break;
                }
            }
            return null;
        }
    }
    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */

}