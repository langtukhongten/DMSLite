package CommonLib;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;

import com.google.android.gms.location.LocationRequest;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

/**
 * Created by My PC on 26/11/2015.
 */
public class Model {
    private static Model instance = null;

    private Model() {
    }

    public synchronized static Model inst() {
        if (instance == null) {
            instance = new Model();
            Log.d("Model", "Create new instance");
        }
        return instance;
    }

    private String deviceId = null;

    public synchronized String getDeviceId() {
        return deviceId;
    }

    public synchronized String getDeviceId(Context context) {
        if (deviceId == null) {
            deviceId = Build.MODEL + "#" + Build.SERIAL + "_";
//            try {
//                Class<?> c = Class.forName("android.os.SystemProperties");
//                Method get = c.getMethod("get", String.class, String.class );
//                deviceId += (String)(get.invoke(c, "ro.serialno", "unknown" ) ) + "_";
//            }
//            catch (Exception ignored) {
//            }
            try {
                deviceId += Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            } catch (Exception ignored) {
            }
            Log.i("getDeviceId", deviceId);
        }
        return deviceId;
    }

    private Const.StatusWorking statusWorking = Const.StatusWorking.Stopped;

    public synchronized Const.StatusWorking getStatusWorking() {
        return statusWorking;
    }

    public synchronized void setStatusWorking(Const.StatusWorking statusWorking) {
        this.statusWorking = statusWorking;
        if (statusWorking == Const.StatusWorking.Stopped) {
            setLogin("", "", "");
        }
    }

    private String fatalError = null;

    public synchronized String getFatalError() {
        return fatalError;
    }

    public synchronized void setFaltalError(String message) {
        fatalError = message;
        setStatusWorking(Const.StatusWorking.Stopped);
    }

    private int lastTryBoosted = 0;
    private Location lastLocation = null;
    private Location lastAccurateLocation = null;

    public synchronized Location getLastLocation() {
        return lastLocation;
    }

    public synchronized Location getLastAccurateLocation() {
        return lastAccurateLocation;
    }

    public synchronized void setLastLocation(Location location) {
        if (location == null) return;
        Bundle extras = location.getExtras();
        if (extras == null) extras = new Bundle();
        if (lastLocation != null) {
            int distanceMeter = (int) location.distanceTo(lastLocation);
            int milisecElapsed = (int) (location.getTime() - lastLocation.getTime());
            if (milisecElapsed > 0) {
                float accuracy = (location.getAccuracy() + lastLocation.getAccuracy()) / 2;
                float speed = (distanceMeter - accuracy / 2) * 1000 / milisecElapsed;
                if (speed > getConfigValue(Const.ConfigKeys.BoostedSpeedMPS, Const.DefaultBoostedSpeedMPS)) {
                    lastTryBoosted = 0;
                    if (getStatusWorking() == Const.StatusWorking.Tracking) {
                        LocationDetector.inst().setBoosted(true);
                    }
                    AlarmTimer.inst().setBoosted(true);
                } else {
                    if (++lastTryBoosted > getConfigValue(Const.ConfigKeys.AlarmIntervalBoostedMultiplier, Const.DefaultAlarmIntervalBoostedMultiplier)) {
                        lastTryBoosted = 0;
                        if (getStatusWorking() == Const.StatusWorking.Tracking) {
                            LocationDetector.inst().setBoosted(false);
                        }
                        AlarmTimer.inst().setBoosted(false);
                    }
                }
                extras.putInt("distanceMeter", distanceMeter);
                extras.putInt("milisecElapsed", milisecElapsed);
            }
        }
        location.setExtras(extras);
        if (location.getAccuracy() <= Model.inst().getConfigValue(Const.ConfigKeys.HighPrecisionAccuracy, Const.DefaultHighPrecisionAccuracy)) {
            lastAccurateLocation = location;
        }
        if (lastTrackingValidLocationSaved == null || Math.abs(lastTrackingValidLocationSaved.trackingDate - getServerTime()) > (isRealtimeValid() ? getConfigValue(Const.ConfigKeys.RealtimeTrackingInterval, Const.DefaultRealtimeTrackingIntervalInSeconds) * getConfigValue(Const.ConfigKeys.RealtimeTrackingIntervalLocationMultiplier, Const.DefaultRealtimeTrackingIntervalLocationMultiplier) : getConfigValue(Const.ConfigKeys.AlarmIntervalBoosted, Const.DefaultAlarmIntervalBoostedInSeconds)) * 1000) {
            new ProcessLocation().execute(location);
        }
        lastLocation = location;
    }

    private TrackingItem lastTrackingItemSaved = null;
    private TrackingItem lastTrackingValidLocationSaved = null;

    public synchronized TrackingItem getLastTrackingItemSaved() {
        return lastTrackingItemSaved;
    }

    public synchronized TrackingItem getLastTrackingValidLocationSaved() {
        return lastTrackingValidLocationSaved;
    }

    public synchronized void setLastTrackingItemSaved(TrackingItem item) {
        if (item != null) {
            lastTrackingItemSaved = item;
            if (item.locationDate > 0) {
                lastTrackingValidLocationSaved = item;
            }
        }
    }

    private class ProcessLocation extends AsyncTask<Location, Void, Location> {

        @Override
        protected Location doInBackground(Location... params) {
            Location location = params[0];
            Bundle extras = location.getExtras();
            if (extras == null) extras = new Bundle();
            String address = LocationDetector.inst().getAddress(location.getLatitude(), location.getLongitude(), location.getAccuracy());
            if (address != null) extras.putString("address", address);
            LocalDB.inst().addTracking(new TrackingItem(location, (byte) 0));
            return location;
        }
    }

    private static long serverTime = -1;
    private static long serverTimeClientTick = -1;

    public static synchronized void setServerTime(long epoch) {
        serverTime = epoch;
        serverTimeClientTick = SystemClock.elapsedRealtime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z"); // the format of your date
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+7")); // give a timezone reference for formating
        Log.i("setServerTime", sdf.format(new Date(serverTime)));
    }

    public static synchronized long getServerTime() {
        if (serverTime >= 0 && serverTimeClientTick >= 0) {
            return serverTime + SystemClock.elapsedRealtime() - serverTimeClientTick;
        }
        return System.currentTimeMillis();
    }


    private int lastBatteryLevel = -1;

    public synchronized void setLastBatteryLevel(int level) {
        if (level >= 0) {
            lastBatteryLevel = level;
        }
        Log.i("setLastBatteryLevel", String.valueOf(lastBatteryLevel));
    }

    public synchronized int getLastBatteryLevel() {
        return lastBatteryLevel;
    }

    private long lastRealtimeClock = 0;

    public synchronized boolean isRealtimeValid() {
        int time = getConfigValue(Const.ConfigKeys.RealtimeTrackingTime, Const.DefaultRealtimeTrackingTimeInSeconds);
        if (time > 0) {
            if (lastRealtimeClock == 0 || time >= (int) ((SystemClock.elapsedRealtime() - lastRealtimeClock) / 1000)) {
                return true;
            }
        }
        return false;
    }

    public synchronized void setLastRealtimeClock() {
        lastRealtimeClock = SystemClock.elapsedRealtime();
    }

    private long lastRealtimeTimer = 0;

    public synchronized int getLastRealtimeTimer(int multiplier) {
        int interval = multiplier * ((multiplier == 1 || getNextWokingTimer() <= 0) ? getConfigValue(Const.ConfigKeys.RealtimeTrackingInterval, Const.DefaultRealtimeTrackingIntervalInSeconds) : getConfigValue(Const.ConfigKeys.RealtimeTrackingIntervalSleeping, Const.DefaultRealtimeTrackingIntervalSleepingInSeconds));
        if (interval > 0) {
            interval -= (int) ((SystemClock.elapsedRealtime() - lastRealtimeTimer) / 1000);
            if (interval < 0) interval = 0;
            return interval;
        }
        return -1;
    }

    public synchronized void setLastRealtimeTimer() {
        lastRealtimeTimer = SystemClock.elapsedRealtime();
    }

    private long lastAlarmTimer = 0;

    public synchronized int getLastAlarmTimer() {
        return (int) ((SystemClock.elapsedRealtime() - lastAlarmTimer) / 1000);
    }

    public synchronized void setLastAlarmTimer() {
        lastAlarmTimer = SystemClock.elapsedRealtime();
    }

    public synchronized int getNextWokingTimer() {
        try {
            int workingTime = getConfigValue(Const.ConfigKeys.WorkingTime, Const.DefaultWorkingTime);
            Date currentDate = Utils.long2date(getServerTime());
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
            calendar.setTime(currentDate);
            int wd = calendar.get(Calendar.DAY_OF_WEEK);
            if (wd >= 1 && wd <= 7) {
                if (Utils.getBit(workingTime, 23 + wd)) {
                    int h = calendar.get(Calendar.HOUR_OF_DAY);
                    if (h >= 0 && h <= 23) {
                        if (Utils.getBit(workingTime, h)) return 0;
                        if (++h >= 24) {
                            h = 0;
                            if (++wd >= 8) wd = 1;
                        }
                        if (Utils.getBit(workingTime, 23 + wd) && Utils.getBit(workingTime, h)) {
                            int m = calendar.get(Calendar.MINUTE);
                            if (m >= 0 && m <= 59) {
                                return (60 - m) * 60;
                            }
                            return 0;
                        }
                        return 60 * 60;
                    }
                } else {
                    if (++wd >= 8) wd = 1;
                    if (Utils.getBit(workingTime, 23 + wd)) {
                        if (Utils.getBit(workingTime, 0)) {
                            int h = calendar.get(Calendar.HOUR_OF_DAY);
                            if (h == 23) {
                                int m = calendar.get(Calendar.MINUTE);
                                if (m >= 0 && m <= 59) {
                                    return (60 - m) * 60;
                                }
                                return 0;
                            }
                        }
                    }
                    return 60 * 60;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    public synchronized void setLogin(String loginToken, String companyName, String employeeName) {
        Log.i("setLogin", companyName + " " + employeeName + " " + loginToken);
        if (loginToken != null) setConfigValue(Const.ConfigKeys.LoginToken, loginToken);
        if (companyName != null) setConfigValue(Const.ConfigKeys.CompanyName, companyName);
        if (employeeName != null) setConfigValue(Const.ConfigKeys.EmployeeName, employeeName);
    }

    public synchronized int getLocationAccuracy() {
        int locationAccuracyMode = getConfigValue(Const.ConfigKeys.LocationAccuracyMode, Const.DefaultLocationAccuracyMode);
        switch (locationAccuracyMode) {
            case 1:
                return LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
            case 2:
                return LocationRequest.PRIORITY_LOW_POWER;
            case 3:
                return LocationRequest.PRIORITY_NO_POWER;
            default:
                return LocationRequest.PRIORITY_HIGH_ACCURACY;
        }
    }

    private HashMap<Const.ConfigKeys, String> mapConfigs = new HashMap<>();

    public int getConfigValue(Const.ConfigKeys key, int defaultValue) {
        String value = getConfigValue(key);
        if (value == null) return defaultValue;
        try {
            return Integer.parseInt(value);
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    public float getConfigValue(Const.ConfigKeys key, float defaultValue) {
        String value = getConfigValue(key);
        if (value == null) return defaultValue;
        try {
            return Float.parseFloat(value);
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    public String getConfigValue(Const.ConfigKeys key) {
        return mapConfigs.get(key);
    }

    public void setConfigValue(Const.ConfigKeys key, String value) {
        mapConfigs.put(key, value);
    }

    public void setConfigValue(Const.ConfigKeys key, float value) {
        mapConfigs.put(key, String.valueOf(value));
    }

    public void setConfigValue(Const.ConfigKeys key, int value) {
        mapConfigs.put(key, String.valueOf(value));
    }

    private int maxRecentApps = Const.MaxRecentApps;

    public synchronized int getMaxRecentApps() {
        return maxRecentApps;
    }

    public synchronized void setMaxRecentApps(int maxRecentApps) {
        this.maxRecentApps = maxRecentApps;
    }

    public synchronized boolean isMenuShow(Const.Menu menu) {
        int showMenus = getConfigValue(Const.ConfigKeys.ShowMenus, Const.DefaultShowMenus);
        return (showMenus & (1 << menu.ordinal())) != 0;
    }
}
