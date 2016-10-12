package CommonLib;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.vietdms.mobile.dmslauncher.Fragment.RightFragment;
import com.vietdms.mobile.dmslauncher.Home;
import com.vietdms.mobile.dmslauncher.MyMethod;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by My PC on 05/12/2015.
 */
public class LocationDetector implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private static LocationDetector instance = null;

    private LocationDetector() {
        super();
    }

    public synchronized static LocationDetector inst() {
        if (instance == null) {
            instance = new LocationDetector();
            Log.d("LocationDetector", "Create new instance");
        }
        return instance;
    }

    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    private GoogleApiClient mGoogleApiClient = null;
    protected LocationRequest mLocationRequest;
    private Geocoder geocoder = null;

    public synchronized boolean start(Context context) {
        try {
            if (geocoder == null) {
                geocoder = new Geocoder(context, Locale.getDefault());
            }
            if (mGoogleApiClient == null) {
                mGoogleApiClient = new GoogleApiClient.Builder(context)
                        .addApi(LocationServices.API)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .build();
            }
            mGoogleApiClient.connect();
            createLocationRequest();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public synchronized void stop() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private boolean isEnabled = false;

    public synchronized void updateRequest(boolean enable, boolean allowDuplicate) {
        if (isEnabled == enable && !allowDuplicate) return;
        handler.sendEmptyMessage(enable ? 1 : 0);
    }

    public synchronized void updateRealtime(boolean realtime) {
        if (isRealtime == realtime) return;
        isRealtime = realtime;
        updateRequest(true, true);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    setRequest(false);
                    break;
                case 1:
                    if (Model.inst().getStatusWorking() == Const.StatusWorking.Tracking) {
                        setRequest(true);
                    }
                    break;
            }
        }
    };

    private boolean isRealtime = false;
    private boolean isBoosted = false;
    private boolean isHighPrecision = false;
    private int nTryHighPrecision = 0;

    public synchronized boolean setBoosted(boolean boosted) {
        if (isBoosted == boosted) return true;
        isBoosted = boosted;
        if (isRealtime) return true;
        return setRequest(true);
    }

    public synchronized boolean setHighPrecision(boolean highPrecision) {
        if (isHighPrecision == highPrecision) return true;
        isHighPrecision = highPrecision;
        if (isRealtime) return true;
        return setRequest(true);
    }

    public synchronized boolean setRequest(boolean enable) {
        Log.i("GoogleApiClient", "setRequest enable=" + enable + " isRealtime=" + isRealtime + " isHighPrecision=" + isHighPrecision + " isBoosted=" + isBoosted);
        try {
            try {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
                isEnabled = false;
            } catch (Exception ex) {
            }
            if (enable) {
                int intervalRealtime = isRealtime ? Model.inst().getConfigValue(Const.ConfigKeys.RealtimeTrackingInterval, Const.DefaultRealtimeTrackingIntervalInSeconds) * Model.inst().getConfigValue(Const.ConfigKeys.RealtimeTrackingIntervalLocationMultiplier, Const.DefaultRealtimeTrackingIntervalLocationMultiplier) : 0;
                int interval = intervalRealtime > 0 ? intervalRealtime : isHighPrecision ? Const.HighPrecisionIntervalInSeconds : Model.inst().getNextWokingTimer() == 0 ? (isBoosted ? Model.inst().getConfigValue(Const.ConfigKeys.AlarmIntervalBoosted, Const.DefaultAlarmIntervalBoostedInSeconds) : Model.inst().getConfigValue(Const.ConfigKeys.AlarmIntervalNormal, Const.DefaultAlarmIntervalNormalInSeconds)) : 0;
                if (interval > 0) {
                    LocationRequest mLocationRequest = new LocationRequest();
                    mLocationRequest.setInterval(interval * 1000);
                    mLocationRequest.setFastestInterval(1000);
                    mLocationRequest.setPriority((isRealtime || isHighPrecision) ? LocationRequest.PRIORITY_HIGH_ACCURACY : Model.inst().getLocationAccuracy());
                    try {
                        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
                        isEnabled = true;
                    } catch (SecurityException ex) {
                        Log.e("GoogleApiClient", "setRequest access denied");
                        SystemLog.inst().addLog(SystemLog.Type.Exception, ex.toString());
                        if (isHighPrecision) {
                            EventPool.view().enQueue(new EventType.EventLoadHighPrecisionLocationResult(null, "Ứng dụng không được quyền lấy vị trí chính xác"));
                            if (Model.inst().getStatusWorking() == Const.StatusWorking.Tracking && Model.inst().getNextWokingTimer() == 0) {
                                setHighPrecision(false);
                            } else {
                                setRequest(false);
                            }
                        }
                        return false;
                    } catch (Exception ex) {
                        Log.e("GoogleApiClient", "setRequest error " + ex.toString());
                        SystemLog.inst().addLog(SystemLog.Type.Exception, ex.toString());
                        if (isHighPrecision) {
                            EventPool.view().enQueue(new EventType.EventLoadHighPrecisionLocationResult(null, "Không thể lấy vị trí chính xác ở thời điểm này"));
                            if (Model.inst().getStatusWorking() == Const.StatusWorking.Tracking && Model.inst().getNextWokingTimer() == 0) {
                                setHighPrecision(false);
                            } else {
                                setRequest(false);
                            }
                        }
                        return false;
                    }
                } else {
                    Log.w("GoogleApiClient", "cannot setRequest because interval=" + interval);
                }
            } else {
                isRealtime = false;
                isBoosted = false;
                isHighPrecision = false;
                nTryHighPrecision = 0;
            }
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i("GoogleApiClient", "Connected to Google Play services!");
        try {
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                Model.inst().setLastLocation(mLastLocation);
                showUpdateLocation();
            } else {
                Log.w("GoogleApiClient", "cannot get current location");
            }
            if (Model.inst().getStatusWorking() == Const.StatusWorking.Tracking) {
                setRequest(true);
            }
        } catch (SecurityException ex) {
            Log.e("GoogleApiClient", "access denied");
            SystemLog.inst().addLog(SystemLog.Type.Exception, ex.toString());
        } catch (Exception ex) {
            Log.e("GoogleApiClient", "error " + ex.toString());
            SystemLog.inst().addLog(SystemLog.Type.Exception, ex.toString());
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.w("GoogleApiClient", "The connection has been interrupted. Cause:" + cause);
        SystemLog.inst().addLog(SystemLog.Type.Warning, "GoogleApiClient: The connection has been interrupted. Cause:" + cause);
        // Disable any UI components that depend on Google APIs
        // until onConnected() is called.
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.w("GoogleApiClient", "onConnectionFailed:" + result.getErrorMessage());
        SystemLog.inst().addLog(SystemLog.Type.Warning, "GoogleApiClient: Connect failed. Cause:" + result.getErrorMessage());
        // This callback is important for handling errors that
        // may occur while attempting to connect with Google.
        //
        // More about this in the 'Handle Connection Failures' section.
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.w("onLocationChanged", location.toString());
        if (location != null) {
            if (MyMethod.isLocationUpdate) {
                EventPool.view().enQueue(new EventType.EventLocationUpdateResult(location, ""));
            }
            if (isHighPrecision) {
                if (location.getAccuracy() <= Model.inst().getConfigValue(Const.ConfigKeys.HighPrecisionAccuracy, Const.DefaultHighPrecisionAccuracy)) {
                    nTryHighPrecision = 0;
                    EventPool.view().enQueue(new EventType.EventLoadHighPrecisionLocationResult(location, ""));
                    if (Model.inst().getStatusWorking() == Const.StatusWorking.Tracking && Model.inst().getNextWokingTimer() == 0) {
                        setHighPrecision(false);
                    } else {
                        setRequest(false);
                    }
                } else {
                    nTryHighPrecision++;
                    if (nTryHighPrecision > Const.MaxRetryHighPrecision) {
                        nTryHighPrecision = 0;
                        EventPool.view().enQueue(new EventType.EventLoadHighPrecisionLocationResult(location, ""));
                        if (Model.inst().getStatusWorking() == Const.StatusWorking.Tracking && Model.inst().getNextWokingTimer() == 0) {
                            setHighPrecision(false);
                        } else {
                            setRequest(false);
                        }
                    }
                }
            }
            //Tự động rời cửa hàng
            if (Home.locationInStore != null && Model.inst().getLastLocation() != null && MyMethod.isVisible(Home.bindingRight.inStore.linearInStore)) {
                float distance = Home.locationInStore.distanceTo(Model.inst().getLastLocation());
                float distanceCheckOut = Model.inst().getConfigValue(Const.ConfigKeys.DistanceCheckOutCustomer) == null ? 100 : Float.parseFloat(Model.inst().getConfigValue(Const.ConfigKeys.DistanceCheckOutCustomer));
                if (MyMethod.isInStore && distance >= distanceCheckOut + Home.locationInStore.getAccuracy()) {
                    Home.nowTransactionLine.id_customer = RightFragment.nowCustomer.id;
                    Home.nowTransactionLine.status = 99;
                    Home.nowTransactionLine.note = "Rời cửa hàng";
                    Home.nowTransactionLine.location_ref_id = (int) LocalDB.inst().addTracking(new TrackingItem(Home.locationInStore, (byte) 2));
                    Home.nowTransactionLine.id_ExtNo_ = 0;
                    Home.nowTransactionLine.id_transaction_define = Const.TransactionType.CheckOut.getValue();
                    Home.nowTransactionLine.latitude = Home.locationInStore.getLatitude();
                    Home.nowTransactionLine.longitude = Home.locationInStore.getLongitude();
                    EventPool.control().enQueue(new EventType.EventSendTransactionRequest(Home.nowTransactionLine));
                    Home.bindingHome.btnComeBack.setText("KHÁCH HÀNG");
                    MyMethod.isInStore = false;

                }
            }
        } else {
            if (isHighPrecision) {
                EventPool.view().enQueue(new EventType.EventLoadHighPrecisionLocationResult(null, "Không thể lấy vị trí theo yêu cầu"));
                if (Model.inst().getStatusWorking() == Const.StatusWorking.Tracking && Model.inst().getNextWokingTimer() == 0) {
                    setHighPrecision(false);
                } else {
                    setRequest(false);
                }
            }
        }
        Model.inst().setLastLocation(location);
        showUpdateLocation();
        if (!isRealtime && !MyMethod.isLocationUpdate && Model.inst().getLocationAccuracy() != LocationRequest.PRIORITY_HIGH_ACCURACY)
            stopLocationUpdates_Core();
    }

    private long lastTimeGetAddress = -1;

    public String getAddress(double latitude, double longitude, float accuracy) {
        if (accuracy <= Model.inst().getConfigValue(Const.ConfigKeys.MaxPrecisionGetAddress, Const.DefaultMaxPrecisionGetAddress)) {
            int time = (int) ((SystemClock.elapsedRealtime() - lastTimeGetAddress) / 1000);
            if (lastTimeGetAddress < 0 || time > Model.inst().getConfigValue(Const.ConfigKeys.MaxTimeGetLocationAddress, Const.DefaultMaxTimeGetLocationAddressInSeconds)) {
                lastTimeGetAddress = SystemClock.elapsedRealtime();
                try {
                    List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                    if (addresses.size() == 0)
                        return null;
                    String add = "";
                    Address obj = addresses.get(0);
                    for (int i = 0; i < obj.getMaxAddressLineIndex(); i++)
                        add += obj.getAddressLine(i) + " ";
                    Log.i("getAddress", add);
                    return add;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Log.w("getAddress", "skip get address because of frequency");
            }
        } else {
            Log.w("getAddress", "skip get address because of location accuracy");
        }
        return "";
    }

    private void showUpdateLocation() {
        Location mLastLocation = Model.inst().getLastLocation();
        String lat = String.valueOf(mLastLocation.getLatitude());
        String lon = String.valueOf(mLastLocation.getLongitude());
        String accuracy = String.valueOf(mLastLocation.getAccuracy());
        String speed = String.valueOf(mLastLocation.getSpeed());
        String time = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS").format(new Date(mLastLocation.getTime()));
        Log.i("GoogleApiClient", "location:" + lat + "," + lon + " accuracy:" + accuracy + " speed:" + speed + " time:" + time);
    }

    public synchronized void startLocationUpdates() {
        try {
            MyMethod.isLocationUpdate = true;
            if (mGoogleApiClient.isConnected()) {
                try {
                    LocationServices.FusedLocationApi.requestLocationUpdates(
                            mGoogleApiClient, mLocationRequest, this);
                    Log.i("LocationDetector", "StartUpdate");
                } catch (SecurityException e) {
                    Log.e("LocationDetector", e.getMessage());
                }

            }
        } catch (Exception e) {
            Log.d("startLocationUpdates", e.getMessage());
        }
    }

    public synchronized void stopLocationUpdates() {
        try {
            if (mGoogleApiClient.isConnected() && MyMethod.isLocationUpdate) {
                MyMethod.isLocationUpdate = false;
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
                Log.i("LocationDetector", "StopUpdate");
                LocationDetector.inst().updateRequest(true, true);
            }
        } catch (Exception e) {
            Log.d("stopLocationUpdates", e.getMessage());
        }
    }

    public synchronized void stopLocationUpdates_Core() {
        try {
            if (mGoogleApiClient.isConnected()) {
                MyMethod.isLocationUpdate = false;
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
                Log.i("LocationDetector", "StopUpdate_Core");
                LocationDetector.inst().updateRequest(true, true);
            }
        } catch (Exception e) {
            Log.d("stopLocationUpdates", e.getMessage());
        }
    }
}
