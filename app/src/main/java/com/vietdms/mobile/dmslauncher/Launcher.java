package com.vietdms.mobile.dmslauncher;

import android.support.multidex.MultiDexApplication;

import cat.ereza.customactivityoncrash.CustomActivityOnCrash;


/**
 * Created by Dms.Tien on 3/1/2016.
 */
public class Launcher extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        CustomActivityOnCrash.install(this);
        CustomActivityOnCrash.setErrorActivityClass(CustomErrorActivity.class);
    }
}
