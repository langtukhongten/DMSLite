package CommonLib;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;

import com.vietdms.mobile.dmslauncher.Receiver.AlarmReceiver;
import com.vietdms.mobile.dmslauncher.Receiver.AlarmReceiverRealtime;

/**
 * Created by My PC on 22/01/2016.
 */
public class AlarmTimer {
    private static AlarmTimer instance = null;
    private AlarmTimer() {
    }
    public synchronized static AlarmTimer inst(){
        if (instance == null) {
            instance = new AlarmTimer();
            Log.d("AlarmTimer", "Create new instance");
        }
        return instance;
    }

    private AlarmManager alarmManager = null;
    private PendingIntent pendingIntent = null;
    private PendingIntent realtimeIntent = null;
    private boolean isBoosted = false;

    public synchronized boolean start(Context context) {
        try {
            if (alarmManager == null) {
                alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            }
            if (pendingIntent == null) {
                Intent intent = new Intent(context, AlarmReceiver.class);
                pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
            }
            if (realtimeIntent == null) {
                Intent intent = new Intent(context, AlarmReceiverRealtime.class);
                realtimeIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
            }
            if (alarmManager == null || pendingIntent == null || realtimeIntent == null) return false;
        }
        catch (Exception ex) {
            return false;
        }
        setRealtimeTimer(Model.inst().getNextWokingTimer() > 0 ? Model.inst().getConfigValue(Const.ConfigKeys.RealtimeTrackingIntervalSleeping, Const.DefaultRealtimeTrackingIntervalSleepingInSeconds) : Model.inst().getConfigValue(Const.ConfigKeys.RealtimeTrackingInterval, Const.DefaultRealtimeTrackingIntervalInSeconds));
        return setTimer(3);
    }

    public synchronized boolean setRealtimeTimer(int sec) {
        Log.i("AlarmTimer", "setRealtimeTimer " + sec + "s");
        if (sec >= 0) {
            try {
                if (Build.VERSION.SDK_INT < 19) {
                    alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + sec * 1000, realtimeIntent);
                }
                else {
                    alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + sec * 1000, realtimeIntent);
                }
                return true;
            } catch (Exception ex) {
            }
        }
        return false;
    }

    public synchronized void clrRealtimeTimer() {
        Log.i("AlarmTimer", "clrRealtimeTimer");
        try {
            alarmManager.cancel(realtimeIntent);
        }
        catch (Exception ex) { }
    }

    public synchronized void setBoosted(boolean isBoosted) {
        if (this.isBoosted == isBoosted) return;
        Log.i("AlarmTimer", "setBoosted from " + this.isBoosted + " to " + isBoosted);
        this.isBoosted = isBoosted;
    }

    public synchronized boolean continueTimer() {
        int nextWorkingTimer = Model.inst().getNextWokingTimer();
        if (nextWorkingTimer <= 0) {
            nextWorkingTimer = isBoosted ? Model.inst().getConfigValue(Const.ConfigKeys.AlarmIntervalBoosted, Const.DefaultAlarmIntervalBoostedInSeconds) : Model.inst().getConfigValue(Const.ConfigKeys.AlarmIntervalNormal, Const.DefaultAlarmIntervalNormalInSeconds);
        }
        nextWorkingTimer -= Model.inst().getLastAlarmTimer();
        if (nextWorkingTimer < 0) nextWorkingTimer = 0;
        return setTimer(nextWorkingTimer);
    }

    public synchronized boolean setTimer(int sec) {
        Log.i("AlarmTimer", "setTimer " + sec + "s");
        if (sec >= 0) {
            try {
                if (Build.VERSION.SDK_INT < 19) {
                    alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + sec * 1000, pendingIntent);
                }
                else {
                    alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + sec * 1000, pendingIntent);
                }
            } catch (Exception ex) {
                return false;
            }
            return true;
        }
        return false;
    }

    public synchronized void clrTimer() {
        Log.i("AlarmTimer", "clrTimer");
        try {
            alarmManager.cancel(pendingIntent);
        }
        catch (Exception ex) { }
    }
}

