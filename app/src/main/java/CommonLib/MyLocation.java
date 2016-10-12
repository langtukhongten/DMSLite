package CommonLib;

import android.graphics.Bitmap;

/**
 * Created by My PC on 29/01/2016.
 */
public class MyLocation {
    public double latitude;
    public double longitude;
    public float accuracy;
    public float speed;
    public int distanceMeter;
    public int milisecElapsed;
    public int milisecFreezed;
    public long locationDate;
    public long trackingDate;
    public byte isWifi = (byte)-1; // 0=off, 1=on, else unknown
    public byte is3G = (byte)-1; // 0=off, 1=on, else unknown
    public byte isAirplaneMode = (byte)-1; // 0=off, 1=on, else unknown
    public byte isGPS = (byte)-1; // 0=off, 1=on, else unknown
    public int batteryLevel = -1; // 0-100=ok, else unknown
    public CellInfo cellInfo;
    public String address;
    public byte getType;
    public byte getMethod;
    public String note;
    public String imageUrl;
    public Bitmap imageThumb;
}
