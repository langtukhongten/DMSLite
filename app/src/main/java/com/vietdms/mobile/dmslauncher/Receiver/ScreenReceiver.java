package com.vietdms.mobile.dmslauncher.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.vietdms.mobile.dmslauncher.Home;
import com.vietdms.mobile.dmslauncher.MyMethod;
import com.vietdms.mobile.dmslauncher.R;

import CommonLib.EventPool;
import CommonLib.EventType;
import CommonLib.LocationDetector;
import CommonLib.PhoneState;
import CommonLib.SystemLog;

public class ScreenReceiver extends BroadcastReceiver {
    private Boolean isWifi, is3G, isGPS, isAirplaneMode, isBatterySaver;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            Log.i("ScreenReceiver", "Screen ON");
            EventPool.control().enQueue(new EventType.EventBase(EventType.Type.AlarmTrigger));
            if (Home.bindingRight != null) {
                if ( MyMethod.isVisible(Home.bindingRight.mapCheckInCustomer.relaMapCheckinOrder)) {
                    LocationDetector.inst().startLocationUpdates();
                }
            }
            if (Home.bindingCenter != null) {
                UpdateIconStatus(context);
            }
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            Log.i("ScreenReceiver", "Screen OFF");
            if (Home.location != null) {
                if ( MyMethod.isVisible(Home.bindingRight.mapCheckInCustomer.relaMapCheckinOrder)) {
                    LocationDetector.inst().stopLocationUpdates();
                }
            }
        }
    }

    private void UpdateIconStatus(Context context) {
        if (true) {
            if (isWifi == null) {
                isWifi = PhoneState.inst().isWifi() == 1;
            } else {
                if (PhoneState.inst().isWifi() == 1) {
                    if (!isWifi) {
                        SystemLog.addLog(context, SystemLog.Type.SwitchOnWifi, null);
                        isWifi = true;
                    }
                    if (Home.bindingCenter.btnTurnOn3G != null)
                        Home.bindingCenter.btnTurnOn3G.setImageResource(R.drawable.internet_on_btn);
                } else {
                    if (isWifi) {
                        SystemLog.addLog(context, SystemLog.Type.SwitchOffWifi, null);
                        isWifi = false;
                    }
                    if (is3G == null) {
                        is3G = PhoneState.inst().is3G() == 1;
                    } else {
                        if (PhoneState.inst().is3G() == 1) {
                            if (!is3G) {
                                SystemLog.addLog(context, SystemLog.Type.SwitchOn3G, null);
                                is3G = true;
                            }
                            if (Home.bindingCenter.btnTurnOn3G != null)
                                Home.bindingCenter.btnTurnOn3G.setImageResource(R.drawable.internet_on_btn);
                        } else {
                            if (is3G) {
                                SystemLog.addLog(context, SystemLog.Type.SwitchOff3G, null);
                                is3G = false;
                            }
                            if (Home.bindingCenter.btnTurnOn3G != null)
                                Home.bindingCenter.btnTurnOn3G.setImageResource(R.drawable.internet_off_btn);
                        }
                    }
                }
            }
            if (isGPS == null) {
                isGPS = PhoneState.inst().isGPS() == 1;
            } else {
                if (PhoneState.inst().isGPS() == 1) {
                    if (!isGPS) {
                        SystemLog.addLog(context, SystemLog.Type.SwitchOnGps, null);
                        isGPS = true;
                    }
                    if (Home.bindingCenter.btnTurnOnGps != null)
                        Home.bindingCenter.btnTurnOnGps.setImageResource(R.drawable.gps_on_btn);
                } else {
                    if (isGPS) {
                        SystemLog.addLog(context, SystemLog.Type.SwitchOffGps, null);
                        isGPS = false;
                    }
                    if (Home.bindingCenter.btnTurnOnGps != null)
                        Home.bindingCenter.btnTurnOnGps.setImageResource(R.drawable.gps_off_btn);
                }
            }
            if (isAirplaneMode == null) {
                isAirplaneMode = PhoneState.inst().isAirplaneMode() == 1;
            } else {
                if (PhoneState.inst().isAirplaneMode() == 1) {
                    if (!isAirplaneMode) {
                        SystemLog.addLog(context, SystemLog.Type.SwitchOnAirplaneMode, null);
                        isAirplaneMode = true;
                    }
                    if (Home.bindingCenter.btnTurnOffAirMode != null)
                        Home.bindingCenter.btnTurnOffAirMode.setImageResource(R.drawable.airmode_on_btn);
                } else {
                    if (isAirplaneMode) {
                        SystemLog.addLog(context, SystemLog.Type.SwitchOffAirplaneMode, null);
                        isAirplaneMode = false;
                    }
                    if (Home.bindingCenter.btnTurnOffAirMode != null)
                        Home.bindingCenter.btnTurnOffAirMode.setImageResource(R.drawable.air_mode_off_btn);
                }
            }
            if (isBatterySaver == null) {
                isBatterySaver = PhoneState.inst().isBatterySaver();
            } else {
                if (PhoneState.inst().isBatterySaver()) {
                    if (!isBatterySaver) {
                        SystemLog.addLog(context, SystemLog.Type.SwitchOnBatterySaver, null);
                        isBatterySaver = true;
                    }
                } else {
                    if (isBatterySaver) {
                        SystemLog.addLog(context, SystemLog.Type.SwitchOffBatterySaver, null);
                        isBatterySaver = false;
                    }
                }
            }
        }

    }
}
