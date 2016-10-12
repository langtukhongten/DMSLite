package CommonLib;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.os.StatFs;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

import com.vietdms.mobile.dmslauncher.BuildConfig;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by My PC on 05/01/2016.
 */
public class PhoneState {
    private static PhoneState instance = null;

    private PhoneState() {
    }

    public synchronized static PhoneState inst() {
        if (instance == null) {
            instance = new PhoneState();
            Log.d("PhoneState", "Create new instance");
        }
        return instance;
    }

    private TelephonyManager telephonyManager = null;
    private WifiManager wifiManager = null;
    private ConnectivityManager connectivityManager = null;
    private LocationManager locationManager = null;
    private ContentResolver contentResolver = null;
    private PowerManager powerManager = null;
    private ActivityManager activityManager = null;
    private PackageManager packageManager = null;

    public boolean init(Context context) {
        try {
            telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            contentResolver = context.getContentResolver();
            powerManager = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
            activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            packageManager = context.getPackageManager();
            return true;
        }
        catch (Exception ex) {
            SystemLog.inst().addLog(SystemLog.Type.Exception, ex.toString());
            return false;
        }
    }

    public CellInfo getCellInfo() {
        try {
            CellInfo cellInfo = new CellInfo();
            GsmCellLocation cellLocation = (GsmCellLocation) telephonyManager.getCellLocation();
            if (cellLocation != null) {
                cellInfo.cellID = cellLocation.getCid() & 0xffff;
                cellInfo.LAC = cellLocation.getLac() & 0xffff;
            }
            String networkOperator = telephonyManager.getNetworkOperator();
            if (networkOperator != null) {
                cellInfo.MCC = Integer.parseInt(networkOperator.substring(0, 3));
                cellInfo.MNC = Integer.parseInt(networkOperator.substring(3));
            }
            return cellInfo;
        } catch (Exception ex) {
            return null;
        }
    }

    public byte isWifi() {
        try {
            NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            boolean wifiConnected = wifiInfo.getState() == NetworkInfo.State.CONNECTED;
            if (wifiConnected) return 1;
            return 0;
        } catch (Exception ex) {
            return -1;
        }
    }

    public byte is3G() {
        try {
            NetworkInfo mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            boolean dataConnected = mobileInfo.getState() == NetworkInfo.State.CONNECTED;
            if (dataConnected) return 1;
            return 0;
        } catch (Exception ex) {
            return -1;
        }
    }

    public boolean turn3GOnOff(boolean turnOn) {
        Log.i("turning 3G", turnOn ? "on..." : "off...");
        try {
            final Class conmanClass = Class.forName(connectivityManager.getClass().getName());
            final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
            iConnectivityManagerField.setAccessible(true);
            final Object iConnectivityManager = iConnectivityManagerField.get(connectivityManager);
            final Class iConnectivityManagerClass = Class.forName(
                    iConnectivityManager.getClass().getName());
            final Method setMobileDataEnabledMethod = iConnectivityManagerClass
                    .getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
            setMobileDataEnabledMethod.setAccessible(true);

            setMobileDataEnabledMethod.invoke(iConnectivityManager, turnOn);
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    public byte isGPS() {
        try {
            boolean isON = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (isON) return 1;
            return 0;
        } catch (Exception ex) {
            return -1;
        }
    }

    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public byte isAirplaneMode() {
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                boolean isON = Settings.System.getInt(contentResolver, Settings.System.AIRPLANE_MODE_ON, 0) != 0;
                if (isON) return 1;
            } else {
                boolean isON = Settings.Global.getInt(contentResolver, Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
                if (isON) return 1;
            }
            return 0;
        } catch (Exception ex) {
            return -1;
        }
    }

    public String getAllInfoDevice() {
        StringBuilder result = new StringBuilder();
        byte is3G = this.is3G();
        byte isWifi = this.isWifi();
        byte isGPS = this.isGPS();
        byte isAir = this.isAirplaneMode();
        result.append("3G=" + (is3G == 1 ? "On" : is3G == 0 ? "Off" : "Unknown") + " ");
        result.append("Wifi=" + (isWifi == 1 ? "On" : isWifi == 0 ? "Off" : "Unknown") + " ");
        result.append("GPS=" + (isGPS == 1 ? "On" : isGPS == 0 ? "Off" : "Unknown") + " ");
        result.append("Airplane=" + (isAir == 1 ? "On" : isAir == 0 ? "Off" : "Unknown") + " ");
        result.append("Sync="+getSyncOnOf()+" ");
        result.append("Battery="+Model.inst().getLastBatteryLevel()+" ");
        result.append("Battery Saver="+getBatterySavingMode()+"\n");
        result.append("Imei="+getImei()+" ");
        result.append("Model="+Build.MODEL+" ");
        result.append("Serial="+Build.SERIAL+" ");
        result.append("Android version="+Build.VERSION.RELEASE+" ");
        result.append("App version="+ BuildConfig.VERSION_CODE+" ("+BuildConfig.VERSION_NAME + ") ");
        result.append("Storage Available : "+ getAvailableSize()+"\n");

        result.append("ListApp : \n"+getListAppInstalled());
        return result.toString();
    }
    public StringBuilder getAvailableSize(){
        StringBuilder size = new StringBuilder();
        try {
            StatFs stat = new StatFs(Environment.getDataDirectory().getPath());
            long bytesAvailable = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                bytesAvailable = (long)stat.getFreeBlocksLong() * (long)stat.getBlockSizeLong();
            }
            long megAvailable = bytesAvailable / 1048576;

           size.append("Device="+megAvailable+"MB ");
        }
        catch (Exception e) {
            size.append("Device=Unknown ");
        }
        try{
            StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
            long bytesAvailable = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                bytesAvailable = (long)stat.getFreeBlocksLong() * (long)stat.getBlockSizeLong();
            }
            long megAvailable = bytesAvailable / 1048576;
            size.append("SDCard="+megAvailable+"MB ");
        }
        catch (Exception e)
        {
            size.append("SDCard=Unknown ");
        }
           return size;
    }

    public String getImei()
    {
        try{
            return telephonyManager.getDeviceId();
        }catch (Exception e)
        {
            return "Unknown";
        }
    }

    public String getBatterySavingMode() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return powerManager.isPowerSaveMode() ? "On" : "Off";
            }
            else return "Unknown";
        } catch (Exception e) {
            return "Unknown";
        }
    }
    public boolean isBatterySaver(){
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return powerManager.isPowerSaveMode() ? true : false;
            }
            else return false;
        } catch (Exception e) {
            return false;
        }
    }

    public String getSyncOnOf() {
        try {
            return contentResolver.getMasterSyncAutomatically() ? "On" : "Off";

        } catch (Exception e) {
            return "Unknown";
        }
    }

    public String getListAppInstalled() {
        try {
            StringBuilder apps = new StringBuilder();
            Intent i = new Intent(Intent.ACTION_MAIN, null);
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            List<ResolveInfo> availableActivities = packageManager.queryIntentActivities(i, 0);
            for (ResolveInfo ri : availableActivities) {
                if (isRunningApp(ri.activityInfo.packageName))
                    apps.append(ri.activityInfo.packageName + " On \n");
                else apps.append(ri.activityInfo.packageName + " Off \n");
            }
            return apps.toString();
        }
        catch (Exception ex) {
            return ex.toString();
        }
    }

    public boolean isRunningApp(String packageName) {
        try {
            List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
            for (int i = 0; i < procInfos.size(); i++) {
                if (procInfos.get(i).processName.equals(packageName)) {
                    return true;
                }
            }
        }
        catch (Exception ex) {
        }
        return false;
    }
}
