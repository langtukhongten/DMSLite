package com.vietdms.mobile.dmslauncher.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.vietdms.mobile.dmslauncher.MyMethod;
import com.vietdms.mobile.dmslauncher.Service.MessageService;

import CommonLib.Const;
import CommonLib.Model;
import CommonLib.PhoneState;
import CommonLib.Utils;

/**
 * Created by ChuTien on 10/7/16.
 */

public class NetworkReceiver extends BroadcastReceiver {
    private final String TAG = "NetworkReceiver";
    private boolean isConnected = false;
    @Override
    public void onReceive(Context context, Intent intent) {
        isNetWorkAvailable(context);
    }

    private boolean isNetWorkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager!=null){
            NetworkInfo[] infos = connectivityManager.getAllNetworkInfo();
            if(infos !=null){
                for(int i = 0; i< infos.length;i++){
                    if(infos[i].getState() == NetworkInfo.State.CONNECTED) {
                        if(!isConnected){
                            Log.w(TAG, "isNetWorkAvailable: Da ket noi mang" );
                            isConnected = true;
                            //Xu ly
                        }
                        return true;
                    }
                }
            }
        }
        if(MyMethod.isFirstOff){
            if (PhoneState.inst().isWifi() != 1) {
                if (PhoneState.inst().is3G() != 1) {
                    if (Model.inst().getStatusWorking() == Const.StatusWorking.Tracking && Model.inst().getNextWokingTimer() == 0 && MyMethod.isCanNotice()) {
                    Log.w(TAG, "isNetWorkAvailable: Ket noi mang tat");
                    Log.w("Message", "thông báo bat mạng ");
                    Intent messageService = new Intent(context, MessageService.class);
                    messageService.putExtra("MessageBody", "CMD►NETWORK►1");
                    messageService.putExtra("API", Const.UpdateVersion);
                    context.startService(messageService);
                }
                }
            }
            MyMethod.isFirstOff =false;

        }

        else{
            MyMethod.isFirstOff = true;
        }
        isConnected =false;
        return false;
    }
}
