package CommonLib;

import android.location.Location;
import android.os.Bundle;

/**
 * Created by My PC on 29/12/2015.
 */
public class TrackingItem {
    public TrackingItem(Location location, byte getMethod) {
        if (location != null && (getMethod > 0 || Math.abs(location.getTime() - System.currentTimeMillis()) < Model.inst().getConfigValue(Const.ConfigKeys.DropLocationTime, Const.DefaultDropLocationTimeInSeconds) * 1000)) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            accuracy = location.getAccuracy();
            speed = location.getSpeed();
            Bundle extras = location.getExtras();
            if (extras != null) {
                distanceMeter = extras.getInt("distanceMeter", 0);
                milisecElapsed = extras.getInt("milisecElapsed", 0);
                note = extras.getString("address", "");
            }
            locationDate = location.getTime();
        }
        trackingDate = Model.inst().getServerTime();
        cellInfo = PhoneState.inst().getCellInfo();
        batteryLevel = Model.inst().getLastBatteryLevel();
        isWifi = PhoneState.inst().isWifi();
        is3G = PhoneState.inst().is3G();
        isGPS = PhoneState.inst().isGPS();
        isAirplaneMode = PhoneState.inst().isAirplaneMode();
        this.getMethod = getMethod;
    }
    public TrackingItem(){ }
    public int rowID = -1;
    public String deviceId = Model.inst().getDeviceId();
    public String visitedId;
    public byte visitedType;
    public double latitude;
    public double longitude;
    public float accuracy;
    public float speed;
    public int distanceMeter;
    public int milisecElapsed;
    public long locationDate;
    public long trackingDate;
    public String note;
    public byte getType;
    public byte getMethod;
    public byte isWifi = -1;
    public byte is3G = -1;
    public byte isAirplaneMode = -1;
    public byte isGPS = -1;
    public int batteryLevel = -1;
    public CellInfo cellInfo;
}
