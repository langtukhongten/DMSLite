package com.vietdms.mobile.dmslauncher.Service;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.util.Util;
import com.vietdms.mobile.dmslauncher.CustomClass.LayoutLoadingManager;
import com.vietdms.mobile.dmslauncher.Fragment.RightFragment;
import com.vietdms.mobile.dmslauncher.Home;
import com.vietdms.mobile.dmslauncher.MyMethod;
import com.vietdms.mobile.dmslauncher.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import CommonLib.Const;
import CommonLib.EventPool;
import CommonLib.EventType;
import CommonLib.LocalDB;
import CommonLib.Model;
import CommonLib.PhoneState;
import CommonLib.Utils;
import Controller.ControlThread;

/**
 * Created by Admin on 7/18/2016.
 */
public class MessageService extends Service {
    private static final String HOME_PACKAGE = "com.vietdms.mobile.dmslauncher";
    private static final String TAG = "MessageService";
    private static final int TRANSACTION = 1;
    private static final int GPS = 2;
    private static final int ThreeG = 3;
    private static final int UPDATE = 4;
    private static final int TransactionWorking = 5;
    private static final int NetWork2G = 7;
    private static final int MESSAGE = 6;

    private WindowManager windowManager;
    private WindowManager.LayoutParams params;
    private Context context;
    private LinearLayout main, lltop;
    private String sender = "";//người gửi
    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;
    private ImageView icon;
    private TextView title, info, message;
    protected int lastId = 0;
    protected long timeOldMessage = 0;
    protected long timeOld3G = 0;
    protected long timeOld2G = 0;
    protected long timeOldTransaction = 0;
    protected long timeOldGPS = 0;
    private static final String TIME_OLD_2G = "2G";
    private static final String TIME_OLD_3G = "3G";
    private static final String TIME_OLD_GPS = "GPS";
    private static final String TIME_OLD_TRANSACTION = "Transaction";
    private static final String TIME_OLD_MESSAGE = "Message";
    private Button btnRead, btnClose;
    private boolean flagUpdate, flagGPS, flag3G, flagTransaction, flag2G;
    private String sendby;//người gửi
    private String content;//nội dung
    private ProgressBar downloadProgressBar;
    private TextView labelDownload;
    private int api;
    private NotificationManager notificationManager;
    public static final int NOTIFICATION_ID = 0;
    private int id_transaction_manager;
    private SharedPreferences prefs = null;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onCreate() {
        super.onCreate();
        this.context = getApplicationContext();
        prefs = getSharedPreferences("com.vietdms.mobile.dmslauncher", MODE_PRIVATE);
        loadValue();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "wakeLock");
        notificationManager =
                (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
        main = new LinearLayout(this);
        downloadProgressBar = new ProgressBar(context);
        labelDownload = new TextView(this);
        LinearLayout.LayoutParams mainParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mainParams.setMargins(20, 0, 20, 0);
        main.setLayoutParams(mainParams);
        main.setBackgroundColor(Color.WHITE);
        main.setBackground(ContextCompat.getDrawable(context, R.drawable.dialog_full_holo_light));
        main.setOrientation(LinearLayout.VERTICAL);
//        //endregion
        //region Create linearLayout Top
        lltop = new LinearLayout(this);
        LinearLayout.LayoutParams lltopParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lltop.setLayoutParams(lltopParams);
        lltop.setOrientation(LinearLayout.HORIZONTAL);
        icon = new ImageView(this);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        icon.setLayoutParams(iconParams);
        icon.setPadding(5, 5, 5, 5);
        icon.setImageResource(R.drawable.logo_btn);
        title = new TextView(this);
        title.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 2));
        title.setText(context.getString(R.string.notice_from_dms));
        title.setTextColor(Color.BLACK);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        title.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.places_ic_clear, 0);
        lltop.addView(icon);
        lltop.addView(title);
        //endregion
        main.addView(lltop);
        LinearLayout line = new LinearLayout(this);
        LinearLayout.LayoutParams linela = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
        line.setLayoutParams(linela);

        line.setBackgroundColor(Color.DKGRAY);
        main.addView(line);
        info = new TextView(this);
        info.setGravity(Gravity.LEFT);
        info.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        info.setMinLines(2);
        info.setSingleLine(false);
        info.setLines(2);
        info.setMaxLines(4);
        info.setTextColor(Color.BLACK);
        info.setPadding(20, 5, 0, 15);
        info.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        main.addView(info);

        LinearLayout lyMessage = new LinearLayout(this);
        LinearLayout.LayoutParams lyParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lyParams.setMargins(30, 0, 10, 0);
        lyMessage.setLayoutParams(lyParams);
        lyMessage.setOrientation(LinearLayout.HORIZONTAL);
        message = new TextView(this);
        message.setPadding(0, 15, 0, 30);
        message.setTypeface(null, Typeface.BOLD);
        message.setGravity(Gravity.LEFT);
        message.setMinLines(2);
        message.setMaxLines(4);
        message.setTextColor(Color.BLACK);
        message.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        downloadProgressBar.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        labelDownload.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        labelDownload.setTextColor(Color.BLUE);
        lyMessage.addView(message);
        lyMessage.addView(downloadProgressBar);
        lyMessage.addView(labelDownload);
        main.addView(lyMessage);

        LinearLayout line2 = new LinearLayout(this);
        LinearLayout.LayoutParams linela2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
        line2.setPadding(0, 0, 0, 10);
        line2.setBackgroundColor(Color.DKGRAY);
        line2.setLayoutParams(linela2);
        main.addView(line2);

        LinearLayout llbutton = new LinearLayout(this);
        llbutton.setPadding(0, 20, 0, 0);
        llbutton.setOrientation(LinearLayout.HORIZONTAL);
        llbutton.setBackgroundColor(Color.WHITE);
        btnRead = new Button(this);
        btnRead.setText(context.getString(R.string.read));
        btnRead.setBackgroundColor(Color.DKGRAY);
        LinearLayout.LayoutParams btnReadLayout = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1);
        btnReadLayout.setMargins(2,0,2,2);
        btnRead.setLayoutParams(btnReadLayout);
        LinearLayout line3 = new LinearLayout(this);
        LinearLayout.LayoutParams linela3 = new LinearLayout.LayoutParams(1, ViewGroup.LayoutParams.MATCH_PARENT);

        line3.setBackgroundColor(Color.WHITE);
        line3.setLayoutParams(linela3);
        btnClose = new Button(this);
        btnClose.setText(context.getString(R.string.close));
        btnClose.setBackgroundColor(Color.DKGRAY);
        LinearLayout.LayoutParams btnCloseLayout = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1);
        btnCloseLayout.setMargins(2,0,2,2);
        btnClose.setLayoutParams(btnCloseLayout);
        llbutton.addView(btnRead);
        llbutton.addView(line3);
        llbutton.addView(btnClose);
        main.addView(llbutton);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displaymetrics);
        int screenWidth = displaymetrics.widthPixels;
        int width = (int) (screenWidth * 0.95f);
        params = new WindowManager.LayoutParams(
                width,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD,
                PixelFormat.TRANSPARENT
        );
        title.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (event.getX() >= (title.getWidth() - title.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here
                        btnClose.performClick();
                        return true;
                    }
                }
                return false;
            }
        });
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                windowManager.removeView(main);
            }
        });
        btnRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyMethod.IDFromMessageService = lastId;
                if (flagUpdate) {//Nếu cập nhật
                    downloadProgressBar.setVisibility(View.VISIBLE);
                    labelDownload.setVisibility(View.VISIBLE);
                    final DownloadTask downloadTask = new DownloadTask(context, downloadProgressBar, labelDownload, windowManager, main);
                    downloadTask.execute(content);
                } else if (flagGPS) {//Nếu bật GPS
                    windowManager.removeView(main);
                    Home.isAppLockStop = true;
                    Intent igps = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    igps.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(igps);
                } else if (flag3G) {//Nếu bật 3G
                    windowManager.removeView(main);
                    Home.isAppLockStop = true;
                    try {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.setComponent(new ComponentName("com.android.settings",
                                "com.android.settings.Settings$DataUsageSummaryActivity"));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
                    }
                } else if (flagTransaction) {
                    windowManager.removeView(main);
                    if (currentApp().contains(HOME_PACKAGE)) {
                        if (Home.bindingHome != null) Home.bindingHome.viewpager.setCurrentItem(2);
                        Home.LayoutMyManager.ShowLayout(RightFragment.Layouts.Transaction);
                        if (Home.bindingRight != null) {
                            Home.bindingRight.transaction.sptransactionStatus.setSelection(2);
                        }
                        LayoutLoadingManager.Show_OnLoading(Home.bindingRight.transaction.TransactionLoadingView, context.getString(R.string.load_transaction), 30);
                        MyMethod.isLoadTransactionInMessage = true;
                        EventPool.control().enQueue(new EventType.EventLoadTransactionsRequest(-1, Model.getServerTime(), -1, "", false, Const.TransactionStatus.Working.getValue()));
                    } else {
                        Intent dialogIntent = new Intent(context, Home.class);
                        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        dialogIntent.putExtra("Command", TransactionWorking + "ß" + lastId);
                        startActivity(dialogIntent);

                    }
                } else if (flag2G) {
                    windowManager.removeView(main);

                } else {
                    windowManager.removeView(main);
                    if (currentApp().contains(HOME_PACKAGE)) {
                        if (Home.bindingHome != null) Home.bindingHome.viewpager.setCurrentItem(2);
                        Home.LayoutMyManager.ShowLayout(RightFragment.Layouts.Transaction);
                        LayoutLoadingManager.Show_OnLoading(Home.bindingRight.transaction.TransactionLoadingView, context.getString(R.string.load_transaction), 30);
                        MyMethod.isLoadTransactionInMessage = true;
                        EventPool.control().enQueue(new EventType.EventLoadTransactionsRequest(lastId, Model.getServerTime(), -1, "", true, Const.TransactionStatus.All.getValue()));
                    } else {
                        Intent dialogIntent = new Intent(context, Home.class);
                        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(dialogIntent);
                        if (Home.bindingHome != null) Home.bindingHome.viewpager.setCurrentItem(2);
                        try {
                            Home.LayoutMyManager.ShowLayout(RightFragment.Layouts.Transaction);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        LayoutLoadingManager.Show_OnLoading(Home.bindingRight.transaction.TransactionLoadingView, context.getString(R.string.load_transaction), 30);
                        MyMethod.isLoadTransactionInMessage = true;
                        EventPool.control().enQueue(new EventType.EventLoadTransactionsRequest(lastId, Model.getServerTime(), -1, "", true, Const.TransactionStatus.All.getValue()));
                    }
                }
            }
        });


    }

    private String currentApp() {
        ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
        ComponentName componentInfo = taskInfo.get(0).topActivity;
        return componentInfo.getPackageName() == null ? "" : componentInfo.getPackageName();
    }

    public void stopMessage() {
        onDestroy();
    }

    private String formatTime(long epochMs) {
        Date date = new Date(epochMs + 25200000);
        DateFormat df = new SimpleDateFormat("dd/MM HH:mm");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        return df.format(date);
    }

    private long long2Minute(long epochMs) {
        return TimeUnit.MILLISECONDS.toMinutes(epochMs);
    }

    private void loadValue(){
        try {
            prefs = getSharedPreferences("com.vietdms.mobile.dmslauncher", MODE_PRIVATE);
            timeOld2G = prefs.getLong(TIME_OLD_2G,0);
            timeOld3G = prefs.getLong(TIME_OLD_3G,0);
            timeOldGPS = prefs.getLong(TIME_OLD_GPS,0);
            timeOldMessage = prefs.getLong(TIME_OLD_MESSAGE,0);
            timeOldTransaction = prefs.getLong(TIME_OLD_TRANSACTION,0);
            Log.w(TAG, "loadValue: 2G:" + timeOld2G +" 3G:"+timeOld3G+" GPS:"+timeOldGPS+" Message:"+timeOldMessage+" Transaction"+timeOldTransaction );
        }catch (Exception e){
            Log.w(TAG, "loadValue: "+e.toString() );
        }
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        try {
            Bundle extras = intent.getExtras();
            if (extras == null) {
                Log.d("Service", "null");
            } else {
                Log.d("Service", "not null");
                api = (int) extras.get("API");
                sender = (String) extras.get("MessageBody");
                Log.w(TAG, "API hiện tại: " + api);
                Log.w(TAG, "Tin nhắn: " + sender);
                try {
                    int id_message = Integer.parseInt(sender.split("ß")[0].trim());
                    int sendby = Integer.parseInt(sender.split("ß")[2].trim());
                    if (id_message != lastId)//Tin nhắn mơi
                    {
                        lastId = id_message;
                        showMessage();
                    } else {//Tin nhắn cũ
                        Log.w(TAG, "onStartCommand: Tin nhắn cũ");
                        if (long2Minute(System.currentTimeMillis() - timeOldMessage) >= 5 )//Sau 5 phút hoac tin nhan gui giao dich topic lại báo
                        {
                            showMessage();
                        }
                    }
                } catch (NumberFormatException e) {//Tin nhắn bất thường (bỏ qua id)
                    if (sender.contains("⌠")) {
                        //logout khẩn cấp đá đít
                        LocalDB.inst().deleteConfig();
                        ControlThread.inst().logout("Đăng xuất do tuyến đã được đăng nhập bằng người khác");
                    }
                    if (sender.contains("Þ")) {
                        showMessage();
                    } else if (sender.contains("►TRANSACTION►")) {
                        Log.w(TAG, "onStartCommand: giao dịch chưa kết thúc cũ");
                        if (long2Minute(System.currentTimeMillis() - timeOldTransaction) >= 60)//Sau 60 phút lại báo
                        {
                            showMessage();
                        }
                    } else if (sender.contains("►2G►")) {
                        Log.w(TAG, "onStartCommand: Yêu cầu 2G cũ");
                        if (long2Minute(System.currentTimeMillis() - timeOld2G) >= 15)//Sau 15 phút lại báo
                        {
                            showMessage();
                        }

                    } else if (sender.contains("►NETWORK")) {
                        Log.w(TAG, "onStartCommand: Yêu cầu 3G cũ");
                        if (PhoneState.inst().isWifi() != 1) {
                            if (PhoneState.inst().is3G() != 1) {
                                if (sender.contains("1")) {
                                    showMessage();
                                } else if (long2Minute(System.currentTimeMillis() - timeOld3G) >= 60)//Sau 30 phút lại báo
                                {
                                    showMessage();
                                }
                            }
                        }
                    } else if (sender.contains("►GPS")) {
                        Log.w(TAG, "onStartCommand: Yêu cầu GPS cũ");
                        if (long2Minute(System.currentTimeMillis() - timeOldGPS) >= 30)//Sau 30 phút lại báo
                        {
                            showMessage();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return START_STICKY;
    }

    private synchronized void showMessage() {
        boolean flagShow = false;
        downloadProgressBar.setVisibility(View.GONE);
        labelDownload.setVisibility(View.GONE);
        params.gravity = Gravity.CENTER;
        params.x = WindowManager.LayoutParams.MATCH_PARENT;
        params.y = WindowManager.LayoutParams.WRAP_CONTENT;
        wakeLock.acquire();
        if (!sender.isEmpty()) {
            if (sender.contains("ß")) {
                String[] sendStr = sender.split("ß");
                long time = Long.parseLong(sendStr[1]);
                sendby = sendStr[2];
                content = sendStr[3];
                if (sendby.equals("-1"))//Cập nhật phần mềm
                {
                    int apiUpdate = Integer.parseInt(sender.split("ß")[4]);
                    if (apiUpdate != api && apiUpdate != 0) {
                        flagShow = true;
                        flagUpdate = true;
                        flagGPS = false;
                        flag3G = false;
                        flag2G = false;
                        flagTransaction = false;
                        btnRead.setText("Cài đặt");
                        btnClose.setText("Để sau");
                        info.setText(formatTime(time) + " - " + "Từ VietDMS API : " + apiUpdate);
                        message.setText("Có phiên bản mới!\nVui lòng cập nhật");
                        sendNotification("Yêu cầu cập nhật phần mềm", UPDATE);
                        Log.w("MessageService", "Hiện yêu cầu cập nhật");
                    } else {//Gửi lệnh đã cập nhật
                        Log.w("MessageService", "Đã là phiên bản mới! Gửi yêu cầu đọc");
                        flagShow = false;
                        EventPool.control().enQueue(new EventType.EventLoadTransactionLinesRequest(lastId));
                    }
                } else {
                    Log.w("MessageService", "Thông báo giao dịch");
                    flagShow = true;
                    flagUpdate = false;
                    flagGPS = false;
                    flag3G = false;
                    flag2G = false;
                    flagTransaction = false;
                    btnRead.setText("Đọc");
                    btnClose.setText("Đóng");
                    info.setText(formatTime(time) + " - " + sendby);
                    message.setText(content);
                    sendNotification(content, TRANSACTION);
                }
                timeOldMessage = System.currentTimeMillis();// đặt lại thời gian nhận tin
                Log.w(TAG, "showMessage: Message sẽ báo lại lúc "+ Utils.long2HourMinuteSecond(timeOldMessage));
                prefs.edit().putLong(TIME_OLD_MESSAGE,timeOldMessage).commit();

            } else if (sender.contains("Þ")) {
                String[] sendStr = sender.split("Þ");
                long time = Long.parseLong(sendStr[3]);
                sendby = sendStr[0];
                content = sendStr[1];
                id_transaction_manager = Integer.parseInt(sendStr[2]);
                Log.w("MessageService", "Thông báo giao dịch");
                flagShow = true;
                flagUpdate = false;
                flagGPS = false;
                flag3G = false;
                flag2G = false;
                flagTransaction = true;
                btnRead.setText("Xem GD");
                btnClose.setText("Để sau");
                info.setText(sendby + " gửi cho bạn một thông báo trong giao dịch ");
                message.setText("Thông báo từ " + sendby);
                sendNotification("Thông báo từ " + sendby, TransactionWorking);
                info.setText(formatTime(time) + " - Tin nhắn từ " + sendby);
                message.setText(content);
                sendNotification(content, TRANSACTION);
            } else if (sender.contains("►")) {//tin nhắn lệnh
                String command = sender.split("►")[1];
                switch (command) {
                    case "GPS":
                        flagShow = true;
                        flagUpdate = false;
                        flagGPS = true;
                        flag3G = false;
                        flag2G = false;
                        flagTransaction = false;
                        btnRead.setText("Bật GPS");
                        btnClose.setText("Để sau");
                        info.setText("GPS của bạn đang tắt!");
                        message.setText("Vui lòng bật GPS.");
                        sendNotification("Vui lòng bật GPS.", GPS);
                        timeOldGPS = System.currentTimeMillis();// đặt lại thời gian nhận tin
                        Log.w(TAG, "showMessage: GPS sẽ báo lại lúc "+ Utils.long2HourMinuteSecond(timeOldGPS));
                        prefs.edit().putLong(TIME_OLD_GPS,timeOldGPS).commit();
                        break;
                    case "NETWORK":
                        flagShow = true;
                        flagUpdate = false;
                        flagGPS = false;
                        flagTransaction = false;
                        flag3G = true;
                        flag2G = false;
                        btnRead.setText("Bật 3G");
                        btnClose.setText("Để sau");
                        info.setText("3G của bạn đang tắt!");
                        message.setText("Vui lòng bật 3G.");
                        sendNotification("Vui lòng bật 3G.", ThreeG);
                        timeOld3G = System.currentTimeMillis();// đặt lại thời gian nhận tin
                        Log.w(TAG, "showMessage: 3G sẽ báo lại lúc "+ Utils.long2HourMinuteSecond(timeOld3G));
                        prefs.edit().putLong(TIME_OLD_3G,timeOld3G).commit();
                        break;
                    case "TRANSACTION":
                        flagShow = true;
                        flagUpdate = false;
                        flagGPS = false;
                        flag3G = false;
                        flagTransaction = true;
                        flag2G = false;
                        btnRead.setText("Xem GD");
                        btnClose.setText("Để sau");
                        info.setText(getString(R.string.you_have) + sender.split("►")[2] + getString(R.string.transaction_none_finish));
                        message.setText(getString(R.string.notify_transaction_not_finish));
                        sendNotification(getString(R.string.transaction_not_finish), TransactionWorking);
                        timeOldTransaction = System.currentTimeMillis();// đặt lại thời gian nhận tin
                        Log.w(TAG, "showMessage: Transaction sẽ báo lại lúc "+ Utils.long2DateFull(timeOldTransaction));
                        prefs.edit().putLong(TIME_OLD_TRANSACTION,timeOldTransaction).commit();
                        break;
                    case "2G":
                        flagShow = true;
                        flagUpdate = false;
                        flagGPS = false;
                        flag3G = false;
                        flagTransaction = false;
                        flag2G = true;
                        btnRead.setText(getString(R.string.ok));
                        btnClose.setText("");
                        info.setText(getString(R.string.notify_slow_network));
                        message.setText(getString(R.string.area_not_network));
                        sendNotification(getString(R.string.notify_slow_network), NetWork2G);
                        timeOld2G = System.currentTimeMillis();// đặt lại thời gian nhận tin
                        Log.w(TAG, "showMessage: 2G sẽ báo lại lúc "+ Utils.long2HourMinuteSecond(timeOld2G));
                        prefs.edit().putLong(TIME_OLD_2G,timeOld2G).commit();
                        break;

                }
            } else {
                info.setText(getString(R.string.no_info));
                message.setText(getString(R.string.no_content));
                sendNotification(getString(R.string.no_content), TRANSACTION);
            }
            if (main.getWindowToken() == null && flagShow) {
                params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                params.dimAmount = 0.5f;
                windowManager.addView(main, params);
            }
            wakeLock.release();

        }
    }

    private void sendNotification(String content, int type) {
        Intent intent = new Intent(context, Home.class);
        intent.putExtra("Command", type + "ß" + lastId);
        if (type == 4) MyMethod.tempLink = sender;
        PendingIntent pendingIntent = PendingIntent.getActivity(context, NOTIFICATION_ID, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.notice_btn)
                .setContentTitle("Thông báo bởi eDMS")
                .setContentText(content)
                .setAutoCancel(false)
                .setSound(defaultSoundUri)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setLights(Color.RED, 3000, 3000)
                .setContentIntent(pendingIntent);
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

class DownloadTask extends AsyncTask<String, Integer, String> {

    private Context context;
    private PowerManager.WakeLock mWakeLock;
    String UrlDownload = "";
    private ProgressBar mProgressBar;
    private WindowManager windowManager;
    private LinearLayout main;
    private TextView label;

    public DownloadTask(Context context, ProgressBar mProgressBar, TextView label, WindowManager windowManager, LinearLayout main) {
        this.context = context;
        this.mProgressBar = mProgressBar;
        this.label = label;
        this.windowManager = windowManager;
        this.main = main;
    }

    @Override
    protected String doInBackground(String... sUrl) {
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(sUrl[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "Server returned HTTP " + connection.getResponseCode()
                        + " " + connection.getResponseMessage();
            }

            // this will be useful to display download percentage
            // might be -1: server did not report the length
            int fileLength = connection.getContentLength();
            String[] savePath = sUrl[0].split("\\/");
            String name = savePath[savePath.length - 1];
            // download the file
            input = connection.getInputStream();
            UrlDownload = "/sdcard/" + name;
            output = new FileOutputStream(UrlDownload);

            byte data[] = new byte[4096];
            long total = 0;
            int count;
            int OldPercent = 0;
            while ((count = input.read(data)) != -1) {
                // allow canceling with back button
                if (isCancelled()) {
                    input.close();
                    return null;
                }
                total += count;
                // publishing the progress....

                if (fileLength > 0) // only if total length is known
                {
                    int newPercent = (int) (total * 100 / fileLength);
                    if (newPercent > OldPercent) {
                        OldPercent = newPercent;
                        publishProgress(newPercent);
                    }
                }
                output.write(data, 0, count);
            }
        } catch (Exception e) {
            return e.toString();
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }

            if (connection != null)
                connection.disconnect();
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // take CPU lock to prevent CPU from going off if the user
        // presses the power button during download
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                getClass().getName());
        mWakeLock.acquire();

    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
        //Toast.makeText(context,"Downloading...: "+progress[0] +" %", Toast.LENGTH_SHORT).show();
        // if we get here, length is known, now set indeterminate to false
        mProgressBar.setIndeterminate(false);
        mProgressBar.setMax(100);
        mProgressBar.setProgress(progress[0]);
        label.setText(progress[0] + "%/100%");
    }

    @Override
    protected void onPostExecute(String result) {
        mWakeLock.release();
        mProgressBar.setVisibility(View.GONE);
        label.setVisibility(View.GONE);
        if (result != null)
            Toast.makeText(context, "Download error: " + result, Toast.LENGTH_LONG).show();
        else {
            Toast.makeText(context, "File downloaded", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            String filename = UrlDownload;
            Uri uri = Uri.fromFile(new File(filename));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            try {
                context.startActivity(intent);
                windowManager.removeView(main);
                ((NotificationManager) context.getSystemService(Service.NOTIFICATION_SERVICE)).cancel(0);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
