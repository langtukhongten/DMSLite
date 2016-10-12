package com.vietdms.mobile.dmslauncher.CustomClass;

import android.os.CountDownTimer;
import android.util.Log;


import com.vietdms.mobile.dmslauncher.CustomView.LoadingView;

import Controller.ControlThread;

/**
 * Created by Cuongph on 09/06/2016.
 */
public class LayoutLoadingItem {
    private LoadingView loadView;
    int iTimeOut = 30000;
    String Text = "";
    CountDownTimer timerStop;

    public LayoutLoadingItem(LoadingView item, String text, int timeOut) {
        this.iTimeOut = timeOut * 1000;
        this.loadView = item;
        if (text.length() > 0)
            Text = text;
        this.getLoadView().setText(text);
    }

    public void setOnLoading(boolean isLoading) {
        getLoadView().setLoading(isLoading);
        if (isLoading) {
            new CountDownTimer(iTimeOut, 1000) {

                public void onTick(long millisUntilFinished) {
                    if (Text.length() > 0)
                        getLoadView().setText(Text + " (" + ((iTimeOut - millisUntilFinished) / 1000) + ")");
                }

                public void onFinish() {
                    if (ControlThread.inst().getState() == Thread.State.TERMINATED) {
                        Log.w("LTG", "Control bị ngủm:");
                        ControlThread.inst().restart();
                    }
                    getLoadView().setLoading(false);
                }
            }.start();
        } else {
            timerStop.cancel();
        }
    }

    public void setOnLoading(boolean isLoading, String text) {
        if (text.length() > 0) {
            Text = text;
            getLoadView().setText(text);
        }
        getLoadView().setLoading(isLoading);
        if (isLoading) {
            timerStop = new CountDownTimer(iTimeOut, 1000) {

                public void onTick(long millisUntilFinished) {
                    if (Text.length() > 0)
                        getLoadView().setText(Text + " (" + ((iTimeOut - millisUntilFinished) / 1000) + ")");
                }

                public void onFinish() {
                    ControlThread.inst().checkConnect();
                    if (ControlThread.inst().getState() == Thread.State.TERMINATED) {
                        Log.w("LTG", "Control bị ngủm:");
                        ControlThread.inst().restart();
                    }
                    getLoadView().setLoading(false);

                }
            };
            timerStop.start();
        } else {
            timerStop.cancel();
        }
    }

    public LoadingView getLoadView() {
        return loadView;
    }
}
