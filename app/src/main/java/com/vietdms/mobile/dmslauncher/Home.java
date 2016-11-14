package com.vietdms.mobile.dmslauncher;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.bluetooth.BluetoothDevice;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.location.Location;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.vietdms.mobile.dmslauncher.CustomAdapter.ArrayUserAdapter;
import com.vietdms.mobile.dmslauncher.CustomAdapter.CustomAdapterGripView;
import com.vietdms.mobile.dmslauncher.CustomAdapter.OrderListProductAdapter;
import com.vietdms.mobile.dmslauncher.CustomClass.LayoutLoadingManager;
import com.vietdms.mobile.dmslauncher.CustomClass.LayoutShow;
import com.vietdms.mobile.dmslauncher.CustomView.LoadingView;
import com.vietdms.mobile.dmslauncher.Fragment.CenterFragment;
import com.vietdms.mobile.dmslauncher.Fragment.LeftFragment;
import com.vietdms.mobile.dmslauncher.Fragment.RightFragment;
import com.vietdms.mobile.dmslauncher.Fragment.ViewPagerAdapter;
import com.vietdms.mobile.dmslauncher.FCM.RegistrationIntentService;
import com.vietdms.mobile.dmslauncher.Receiver.BatteryReceiver;
import com.vietdms.mobile.dmslauncher.Receiver.DMSDeviceAdminReceiver;
import com.vietdms.mobile.dmslauncher.Receiver.ScreenReceiver;
import com.vietdms.mobile.dmslauncher.RecycleView.RecyclerItemClickListener;
import com.vietdms.mobile.dmslauncher.RecycleView.RecyclerViewAdapterTimeLine;
import com.vietdms.mobile.dmslauncher.Service.BackgroundService;
import com.vietdms.mobile.dmslauncher.Service.MessageService;
import com.vietdms.mobile.dmslauncher.databinding.ActivityMainBinding;
import com.vietdms.mobile.dmslauncher.databinding.FragmentCenterBinding;
import com.vietdms.mobile.dmslauncher.databinding.FragmentLeftBinding;
import com.vietdms.mobile.dmslauncher.databinding.FragmentRightBinding;
import com.zj.btsdk.BluetoothService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import CommonLib.Const;
import CommonLib.EventPool;
import CommonLib.EventType;
import CommonLib.LocalDB;
import CommonLib.LocationDetector;
import CommonLib.Model;
import CommonLib.OrderDetail;
import CommonLib.PhoneState;
import CommonLib.SystemLog;
import CommonLib.TimeLine;
import CommonLib.TrackingItem;
import CommonLib.TransactionLine;
import CommonLib.UserInfo;

public class Home extends AppCompatActivity implements ViewPager.OnPageChangeListener, RecyclerItemClickListener.OnItemClickListener, View.OnClickListener {

    protected PowerManager.WakeLock mWakeLock;
    private static final String TAG = "Home";
    private static final String LOCKAPP_REQUEST = "Accessibility";
    private SharedPreferences prefs = null;
    //Binding
    public static FragmentCenterBinding bindingCenter;
    public static ActivityMainBinding bindingHome;
    public static FragmentLeftBinding bindingLeft;
    public static FragmentRightBinding bindingRight;
    //RUNABLE

    public static Runnable runnableStore;
    public static Handler handler;


    //GCM
    public static boolean isAppLockStop = false; // Dừng Applock
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG_GCM = "GCM_LOG";
    public static final int ACTION_PHOTO_CUSTOMER = 1012;
    public static final int ACTION_PHOTO_CUSTOMER_UPDATE = 2525;
    public static int nowRoute = 0;
    public static float nowAmount;
    public static float nowAmountSale;
    public static float nowDiscount;
    public static LayoutShow LayoutMyManager;
    public static int positionOrder;
    public static Marker markerNow;
    public static Polyline lineNow;
    public static long nowIdExtNo;//Mã đơn hàng vừa gửi lên
    public static boolean isUpdateCustomerInMap;// Cập nhật khách hàng trong (true) ? Bản đồ ghé thăm : Màn hình sửa khách hàng
    public static int nowCity; // Mã thành phố
    public static int nowCounty; // Mã tỉnh
    public static String nowSerialno; // Mã ngẫu nhiên khách hàng
    public static ArrayList<TimeLine> timelinesArrayList; // danh sách timeline
    public static TransactionLine nowTransactionLine = new TransactionLine();
    public static RecyclerViewAdapterTimeLine adapterTimeLine;
    public static int lastIdTimeLine = -1;
    public static int nowWorkingTime; // WorkingTme
    public static String filterCustomer = "";
    public static String filterProduct = "";
    public static LoadingView loadingApprovalButton;
    public static Location locationInStore;//Vị trí cửa hàng đang ở
    public static Marker endMarker;
    public static int visibleTitleRC = 0;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private ScreenReceiver mScreenReceiver;
    private BatteryReceiver mBatteryReceiver;
    public static int nowIdCustomer = -1;//Mã nhân viên đang chọn trong Màn hình Khách hàng
    //---
    public static String BROADCAST_ACTION_CONTROL = "Forms.OnEvent";
    public static String BROADCAST_ACTION_CVIEW = "Forms.OnEvent.C2View";
    public static String BROADCAST_ACTION_VIEW = "Forms.OnEvent.View";
    static final int ACTIVATION_REQUEST = 47; // identifies our request id
    public static ArrayList<UserInfo> arrStaff = new ArrayList<>();
    public static ArrayUserAdapter adapterStaff;
    public static ArrayList<String> allItems = new ArrayList<>();
    public static ArrayList<String> recentItems = new ArrayList<>();
    public static CustomAdapterGripView adapterGripView, adapterRecentGripView;
    public static TextView txtAddressIn;
    public static EditText editCheckIn;
    public static DevicePolicyManager devicePolicyManager;
    public static Location location = null;
    private ComponentName dmsDeviceAdmin;
    public static PackageManager manager;
    public static RelativeLayout relativeRight, relativeCheckIn;
    public static Bitmap bitmapImage = null;
    private Context context;
    public static SystemBarTintManager tintManager;
    public static String error = "";
    public static SwipeRefreshLayout swipeTransaction, swipeProduct, swipeApproval,
            swipeProductOfOrder, swipeOrderMain, swipeCustomer, swipeTimeLine, swipeInventoryEmployee,
            swipeReportCheckIn;
    public static SwipeRefreshLayoutBottom swipeOrderMainBottom, swipeReportCheckInBottom, swipeTransactionBottom,
            swipeTimeLineBottom, swipeCustomerBottom, swipeInventoryEmployeeBottom;
    public static LinearLayout llDeal;
    public static SupportMapFragment mapUpdateFragment, mapCustomerViewFragment, mapCustomerCheckinFragment;
    public static ImageView imagePhotoIn;
    public static SupportMapFragment mapFragment, mapOrderFragment;

    public static int positionGCM, positionTransaction, positionCustomer, positionProduct;
    public static Button btnOrderAddProduct, btnOrderSaveSend, btnOrderProductCancel, btnOrderProductAccept;
    public static TextView txtOrderAmountSale, txtOrderAmount, txtOrderDiscount, txtOrderCustomerName, txtOrderCustomerAddress, txtOrderProductAmountItem;
    public static ImageView img_pre_sale, img_pre_sale_detail;
    public static EditText edOrderNote;
    public static CheckBox cbOrderPreSale;
    public static ListView lstOrderProduct;
    public static HashMap<String, Integer> hashListQuantity = new HashMap<>();
    public static HashMap<String, ArrayList<EditText>> hashViewPromotion = new HashMap<>();
    public static HashMap<String, Float> hashListPrice = new HashMap<>();
    public static HashMap<Integer, OrderDetail> hashOrderLine = new HashMap<>();
    public static ArrayList<OrderDetail> orderDetailArrayList = new ArrayList<>();
    public static OrderListProductAdapter orderListProductAdapter, orderListOrderDetailAdapter;
    private boolean flag;
    //ORDER DETAIL
    public static TextView txtOrderDetailNoName, txtOrderDetailTime, txtOrderDetailAmount, txtOrderDetailAmountSale, txtOrderDetailDiscount;
    public static EditText edOrderDetailNote;
    public static ListView lstOrderDetail;
    //MAP CUSTOMER CHECKIN
    public static Button btnMapCustomerUpdateLocation, btnMapCustomerUpdateImage;
    //Android 6.0 permission
    final private int REQUEST_CODE_ASK_MULTIPE_PERMISSIONS = 9900;
    private static Boolean isWifi = null;
    private static Boolean is3G = null;
    private static Boolean isGPS = null;
    private static Boolean isAirplaneMode = null;
    private static Boolean isBatterySaver = null;

    //BLUETOOTH PRINTER
    public static BluetoothService mService = null;
    public static BluetoothDevice con_dev = null;

    //CURRENT LOCATION
    public static LatLng currentPostition;
    RightFragment rightFragment;
    LeftFragment leftFragment;
    CenterFragment centerFragment;
    public static ViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences("com.vietdms.mobile.dmslauncher", MODE_PRIVATE);
        bindingHome = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mService = new BluetoothService(this, mHandler);
        if (mService.isAvailable() == false) {
            Toast.makeText(this, "Vui lòng bật bluetooth", Toast.LENGTH_LONG).show();
            finish();
        }
        tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setNavigationBarTintEnabled(true);
        startService(new Intent(this, BackgroundService.class));
        Awake();
        init();
        getDataFromIntent(getIntent());
        Log.w(TAG, "getDataFromIntent: OnCreate");
        Log.w(TAG, "onCreate");
    }

    private void getDataFromIntent(Intent intent) {//Nếu được bật từ 1 intent khác thì xử lý lệnh
        String receiver = intent.getStringExtra("Command");
        Log.w(TAG, "getDataFromIntent: " + receiver);
        if (receiver != null) {
            int command = 0, lastId = 0;
            long ref_id = 0;
            if (!receiver.isEmpty()) {
                if (receiver.contains("ß")) {
                    command = Integer.parseInt(receiver.split("ß")[0]);
                    if (command != 8) {
                        //neu k phai don hang huy
                        lastId = Integer.parseInt(receiver.split("ß")[1]);
                    } else {
                        ref_id = Long.parseLong(receiver.split("ß")[1]);
                    }
                }
            }
            switch (command) {
                case 1://Load giao dịch mới
                    Log.w(TAG, "getDataFromIntent: Load giao dịch mới");
                    if (Home.bindingHome != null) {
                        try {
                            Home.bindingHome.viewpager.setCurrentItem(1);
                            Home.LayoutMyManager.ShowLayout(RightFragment.Layouts.Transaction);
                            rightFragment.transactionArrayList.clear();
                            rightFragment.adapterTransaction.notifyDataSetChanged();
                            LayoutLoadingManager.Show_OnLoading(Home.bindingRight.transaction.TransactionLoadingView, context.getString(R.string.load_transaction), 30);
                            MyMethod.isLoadTransactionInMessage = true;
                            EventPool.control().enQueue(new EventType.EventLoadTransactionsRequest(lastId, Model.getServerTime(), -1, "", true, Const.TransactionStatus.All.getValue()));
                            MyMethod.IDFromMessageService = lastId;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case 2:
                    Log.w(TAG, "getDataFromIntent: GPS");
                    Home.isAppLockStop = true;
                    Intent igps = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    igps.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(igps);
                    break;
                case 3:
                    Log.w(TAG, "getDataFromIntent: 3G");
                    Home.isAppLockStop = true;
                    try {
                        Intent intent1 = new Intent(Intent.ACTION_MAIN);
                        intent1.setComponent(new ComponentName("com.android.settings",
                                "com.android.settings.Settings$DataUsageSummaryActivity"));
                        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent1);
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
                    }
                    break;
                case 4:
                    Log.w(TAG, "getDataFromIntent: UPDATE");
                    if (MyMethod.isCanNotice()) {
                        Intent messageService = new Intent(context, MessageService.class);
                        messageService.putExtra("MessageBody", MyMethod.tempLink);
                        messageService.putExtra("API", Const.UpdateVersion);
                        context.startService(messageService);
                    }
                    break;
                case 5:
                    Log.w(TAG, "getDataFromIntent: Load giao dịch chua ket thuc");
                    if (Home.bindingHome != null) {
                        try {
                            Home.bindingHome.viewpager.setCurrentItem(1);
                            Home.LayoutMyManager.ShowLayout(RightFragment.Layouts.Transaction);
                            rightFragment.transactionArrayList.clear();
                            rightFragment.adapterTransaction.notifyDataSetChanged();
//                            LayoutLoadingManager.Show_OnLoading(Home.loadingTransaction, context.getString(R.string.load_transaction), 30);
//                            EventPool.control().enQueue(new EventType.EventLoadTransactionsRequest(lastId, Model.getServerTime(), -1, "", true));
                            Home.bindingRight.transaction.sptransactionStatus.setSelection(2);
                            Home.bindingRight.transaction.transactionLoad.performClick();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case 8:
                    Log.w(TAG, "getDataFromIntent: Load Don hang huy");
                    if (Home.bindingHome != null) {
                        try {
                            Home.bindingHome.viewpager.setCurrentItem(1);
                            Home.LayoutMyManager.ShowLayout(RightFragment.Layouts.OrderDetail);
                            LayoutLoadingManager.Show_OnLoading(Home.bindingRight.orderDetail.OrderDetailLoading, context.getString(R.string.load_order_detail), 30);
                            EventPool.control().enQueue(new EventType.EventLoadOrderDetailsRequest(ref_id, 0));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }


    private void UpdateIconStatus() {
        if (true) {
            try {
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BluetoothService.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            Toast.makeText(getApplicationContext(), "Kết nối máy in thành công",
                                    Toast.LENGTH_SHORT).show();
                            break;
                        case BluetoothService.STATE_CONNECTING:
                            break;
                        case BluetoothService.STATE_LISTEN:
                        case BluetoothService.STATE_NONE:
                            break;
                    }
                    break;
                case BluetoothService.MESSAGE_CONNECTION_LOST:
                    Toast.makeText(getApplicationContext(), "Mất kết nối",
                            Toast.LENGTH_SHORT).show();
                    break;
                case BluetoothService.MESSAGE_UNABLE_CONNECT:
                    Toast.makeText(getApplicationContext(), "Vui lòng thử lại",
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }

    };

    private int maxRecentApps() {
        return (MyMethod.pxToDp(Home.bindingCenter.relaGridRecentApp.getHeight(), context) / 96) * 4;
    }

    private int sizeDeal() {
        return (Home.llDeal.getWidth() > Home.llDeal.getHeight() ? Home.llDeal.getHeight() : Home.llDeal.getWidth());
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            Model.inst().setMaxRecentApps(maxRecentApps());
            EventPool.control().enQueue(new EventType.EventListAppRequest(false));
            EventPool.control().enQueue(new EventType.EventListAppRequest(true));

//            Home.arcDeal.getLayoutParams().width = sizeDeal();
//            Home.arcDeal.getLayoutParams().height = sizeDeal();
        }
        super.onWindowFocusChanged(hasFocus);
    }


    private void Awake() {//startup
        context = getApplicationContext();
        //GCM
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(MyMethod.SENT_TOKEN_TO_SERVER, false);
            }
        };

        if (checkPlayServices()) {
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
        try {
            mScreenReceiver = new ScreenReceiver();
            registerReceiver(mScreenReceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));
            registerReceiver(mScreenReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
            mBatteryReceiver = new BatteryReceiver();
            Intent batteryIntent = registerReceiver(mBatteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            if (level >= 0 && scale > 0) {
                Model.inst().setLastBatteryLevel(level * 100 / scale);
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);

    }


    private void init() {// go to in this heart
        setSupportActionBar(bindingHome.toolbar);
        int id = bindingHome.searchView.getContext()
                .getResources()
                .getIdentifier("android:id/search_src_text", null, null);
        //Create viewpager with three fragment
        bindingHome.btnComeBack.setOnClickListener(this);
        bindingHome.txtTile.setOnClickListener(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            bindingHome.relaBackground.setBackground(MyMethod.getBackground(context));
        }
        setupViewPager(bindingHome.viewpager);
        manager = getPackageManager();
        //Get permission ADMIN DEVICE
        //getPermission();
        //set event main menu
        insertPermission();


    }

    private void insertPermission() {
        List<String> permissionsNeeded = new ArrayList<>();

        final List<String> permissionsList = new ArrayList<>();
        if (!addPermission(permissionsList, android.Manifest.permission.ACCESS_FINE_LOCATION))
            permissionsNeeded.add("GPS");
        if (!addPermission(permissionsList, android.Manifest.permission.CAMERA))
            permissionsNeeded.add("CAMERA");
        if (!addPermission(permissionsList, android.Manifest.permission.WRITE_EXTERNAL_STORAGE))
            permissionsNeeded.add("STORAGE");

        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                String message = context.getString(R.string.permission_ask) + " " + permissionsNeeded.get(0);
                for (int i = 1; i < permissionsNeeded.size(); i++)
                    message = message + ", " + permissionsNeeded.get(i);
                showMessageOKCancel(message,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    requestPermissions(permissionsList.toArray(new String[permissionsList.size()]), REQUEST_CODE_ASK_MULTIPE_PERMISSIONS);
                                }
                            }
                        });
                return;
            }
        }

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(Home.this)
                .setMessage(message)
                .setPositiveButton(context.getString(R.string.ok), okListener)
                .setNegativeButton(context.getString(R.string.cancel), null)
                .create()
                .show();
    }

    private boolean addPermission(List<String> permissionsList, String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(permission);
                return false;
            }
        }
        return true;
    }

    private void setupViewPager(final ViewPager viewPager) {// setup view page fragment
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        //leftFragment = new LeftFragment();
        //adapter.addFragment(leftFragment, getString(R.string.app_dms));
        centerFragment = new CenterFragment();
        adapter.addFragment(centerFragment, getString(R.string.home));
        rightFragment = new RightFragment();
        adapter.addFragment(rightFragment, getString(R.string.app_dms));
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
        viewPager.addOnPageChangeListener(this);
        //Set current page is center fragment
        viewPager.setCurrentItem(0);
    }


    private void getPermission() {// open activity get permission ADMIN DEVICE

        devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        dmsDeviceAdmin = new ComponentName(this, DMSDeviceAdminReceiver.class);
        try {
            Intent intent = new Intent(
                    DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                    dmsDeviceAdmin);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                    getString(R.string.permissionlabel));
            startActivityForResult(intent, ACTIVATION_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onItemClick(View view, int position) {

    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


        MyMethod.closeFocus(getCurrentFocus());
    }

    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case 0:
                bindingHome.btnComeBack.setText(context.getString(R.string.dashboard));
                bindingHome.txtTile.setVisibility(View.VISIBLE);
                bindingHome.btnComeBack.setVisibility(View.GONE);
                bindingHome.txtTile.setText(context.getString(R.string.dashboard));
                long currentDate = System.currentTimeMillis();
                //EventPool.control().enQueue(new EventType.EventLoadBI_DailyReportRequest(currentDate, LeftFragment.LastSelectedStaff, LeftFragment.LastStyleSelectedStaff));
                break;
            case 1:

                UpdateIconStatus();
                rightFragment.updateMenu();
                bindingHome.txtTile.setVisibility(View.VISIBLE);
                bindingHome.btnComeBack.setVisibility(View.GONE);
                if (Home.bindingCenter != null) {
                    if (Home.bindingCenter.listApps.linearListApp != null)
                        if (MyMethod.isVisible(Home.bindingCenter.listApps.linearListApp)) {
                            bindingHome.txtTile.setText(getString(R.string.list_app));
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                bindingHome.txtTile.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context, android.R.drawable.ic_menu_search), null, null, null);
                            }
                        } else bindingHome.txtTile.setText(getString(R.string.home));
                }

                break;
            case 2:


                if (MyMethod.isVisible(Home.bindingRight.relaLayoutMain)) {
                    bindingHome.txtTile.setVisibility(View.VISIBLE);
                    bindingHome.txtTile.setText(getString(R.string.app_dms));
                    bindingHome.txtTile.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    bindingHome.btnComeBack.setVisibility(View.GONE);
                } else {
                    bindingHome.btnComeBack.setVisibility(View.GONE);
                    bindingHome.txtTile.setVisibility(View.VISIBLE);
                }
                bindingHome.searchView.setVisibility(View.GONE);


                break;
            default:
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        switch (state) {
            case ViewPager.SCROLL_STATE_IDLE:
                if (!flag && bindingHome.viewpager.getCurrentItem() == 1 && !MyMethod.isVisible(bindingRight.relaLayoutMain) && !MyMethod.isVisible(Home.bindingRight.login.relativeLayoutLogin))
                    onBackPressed();
                break;
            case ViewPager.SCROLL_STATE_DRAGGING:
                flag = false;
                break;
            case ViewPager.SCROLL_STATE_SETTLING:
                flag = true;
                break;
            default:
                break;
        }

    }

    //LIFE CYCLE
    @Override
    protected void onPause() {
        Log.w(TAG, "onPause");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        try {
            context.unregisterReceiver(mBatteryReceiver);
            context.unregisterReceiver(mScreenReceiver);
        } catch (IllegalArgumentException e) {
        }
        super.onPause();
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG_GCM, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    public static Bitmap ProcessingBitmap(Bitmap bm1, Bitmap bm2) {
        Bitmap newBitmap = null;

        int w;
        if (bm1.getWidth() >= bm2.getWidth()) {
            w = bm1.getWidth();
        } else {
            w = bm2.getWidth();
        }

        int h;
        if (bm1.getHeight() >= bm2.getHeight()) {
            h = bm1.getHeight();
        } else {
            h = bm2.getHeight();
        }

        Bitmap.Config config = bm1.getConfig();
        if (config == null) {
            config = Bitmap.Config.ARGB_8888;
        }
        newBitmap = Bitmap.createBitmap(w, h, config);
        Canvas newCanvas = new Canvas(newBitmap);
        newCanvas.drawBitmap(bm1, 0, 0, null);
        Paint paint = new Paint();
        paint.setAlpha(128);
        newCanvas.drawBitmap(bm2, 0, 0, paint);
        return newBitmap;
    }

    @Override
    protected void onResume() {
        try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                bindingHome.relaBackground.setBackground(MyMethod.getBackground(context));
                Home.bindingRight.checkin.relaLayoutCheckin.setBackgroundColor(Color.TRANSPARENT);
                Home.relativeRight.setBackgroundColor(Color.TRANSPARENT);
                Home.bindingCenter.relaMain.setBackgroundColor(Color.TRANSPARENT);
                Drawable statusbar = MyMethod.getStatusBackground(getApplicationContext());
                Bitmap statusbitmap = ((BitmapDrawable) statusbar).getBitmap();
                Bitmap status = ProcessingBitmap(statusbitmap, get_ninepatch(R.drawable.bgblack70, statusbitmap.getWidth(), statusbitmap.getHeight(), context));
                tintManager.setStatusBarTintDrawable(new BitmapDrawable(getResources(), status));
                Drawable navbar = MyMethod.getNavigationBackground(getApplicationContext());
                Bitmap navbitmap = ((BitmapDrawable) navbar).getBitmap();
                Bitmap nav = ProcessingBitmap(navbitmap, get_ninepatch(R.drawable.bgblack70, navbitmap.getWidth(), navbitmap.getHeight(), context));
                tintManager.setNavigationBarTintDrawable(new BitmapDrawable(getResources(), nav));
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            try {
                Drawable statusbar = MyMethod.getStatusBackground(getApplicationContext());
                Bitmap statusbitmap = ((BitmapDrawable) statusbar).getBitmap();
                Bitmap status = ProcessingBitmap(statusbitmap, get_ninepatch(R.drawable.bgblack70, statusbitmap.getWidth(), statusbitmap.getHeight(), context));
                tintManager.setStatusBarTintDrawable(new BitmapDrawable(getResources(), status));
                Drawable navbar = MyMethod.getNavigationBackground(getApplicationContext());
                Bitmap navbitmap = ((BitmapDrawable) navbar).getBitmap();
                Bitmap nav = ProcessingBitmap(navbitmap, get_ninepatch(R.drawable.bgblack70, navbitmap.getWidth(), navbitmap.getHeight(), context));
                tintManager.setNavigationBarTintDrawable(new BitmapDrawable(getResources(), nav));
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }

//        try {
//            if (!MyMethod.checkAdminActive(devicePolicyManager, dmsDeviceAdmin)) getPermission();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        Log.w(TAG, "onResume");
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(MyMethod.REGISTRATION_COMPLETE));


    }


    public static Bitmap get_ninepatch(int id, int x, int y, Context context) {
        // id is a resource id for a valid ninepatch

        Bitmap bitmap = BitmapFactory.decodeResource(
                context.getResources(), id);

        byte[] chunk = bitmap.getNinePatchChunk();
        NinePatchDrawable np_drawable = new NinePatchDrawable(bitmap,
                chunk, new Rect(), null);
        np_drawable.setBounds(0, 0, x, y);

        Bitmap output_bitmap = Bitmap.createBitmap(x, y, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output_bitmap);
        np_drawable.draw(canvas);

        return output_bitmap;
    }

    @Override
    protected void onRestart() {
        Log.w(TAG, "onRestart");

        super.onRestart();
    }

    @Override
    protected void onStart() {
        Log.w(TAG, "onStart");
        super.onStart();

    }

    @Override
    protected void onDestroy() {
        Log.w(TAG, "onDestroy");

        super.onDestroy();
        if (mService != null)
            mService.stop();
        mService = null;
    }

    @Override
    protected void onStop() {
        Log.w(TAG, "onStop");
        //Background Service
        Intent mStartService = new Intent(getApplicationContext(), BackgroundService.class);
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getService(getApplicationContext(), mPendingIntentId, mStartService, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 10000, mPendingIntent);
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ACTIVATION_REQUEST:
                if (resultCode != Activity.RESULT_OK) {
                    // /If not has permision ADMIN DEVICE then open activity get permision again
                    try {
                        Intent intent = new Intent(
                                DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                                dmsDeviceAdmin);
                        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                                getString(R.string.permissionlabel));
                        startActivityForResult(intent, ACTIVATION_REQUEST);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (!MyMethod.isAccessibilitySettingsOn(context)) {
                    if (isNeedLockApp()) {
                        //Nếu khả năng truy cập chưa bật và máy này cần chức năng khóa app
                        Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
                        startActivityForResult(intent, 0);
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private boolean isNeedLockApp() {//Check cần được khóa ứng dụng
        if (prefs.getBoolean(LOCKAPP_REQUEST, true)) {
            //Nếu chưa được hỏi lần nào thì hỏi
            prefs.edit().putBoolean(LOCKAPP_REQUEST, false).commit();
            if (Model.inst().getConfigValue(Const.ConfigKeys.LockApps) == null) {
                return true;
            }
            if (Model.inst().getConfigValue(Const.ConfigKeys.WhiteListApp) == null) return true;
            return !Model.inst().getConfigValue(Const.ConfigKeys.WhiteListApp).trim().isEmpty() || !Model.inst().getConfigValue(Const.ConfigKeys.LockApps).trim().isEmpty();
        } else {
            //Đã hỏi rồi thì thôi
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPE_PERMISSIONS: {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                //Initial
                perms.put(android.Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                //Fill with result
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                //Check
                if (perms.get(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && perms.get(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                        && perms.get(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                }
            }
        }
    }


    @Override
    public void onBackPressed() {

        switch (bindingHome.viewpager.getCurrentItem()) {
//            case 0:
//                try {
//                    bindingHome.viewpager.setCurrentItem(1);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                break;
            case 0:
                try {
                    if (MyMethod.isVisible(Home.bindingCenter.searchBoxs.llSearchBox)) {
                        MyMethod.setGone(Home.bindingCenter.searchBoxs.llSearchBox);
                        Home.bindingCenter.listApps.swipeContainer.setEnabled(true);
                        bindingHome.appBar.setVisibility(View.VISIBLE);
                        Home.bindingCenter.listApps.linearListApp.setVisibility(View.VISIBLE);
                        Home.bindingHome.searchView.setQuery("", false);
                    } else if (MyMethod.isVisible(Home.bindingCenter.listApps.linearListApp)) {
                        MyMethod.setGone(Home.bindingCenter.listApps.linearListApp);
                        MyMethod.setVisible(Home.bindingCenter.relaLayoutCenter);
                        bindingHome.searchView.setVisibility(View.GONE);
                        bindingHome.txtTile.setVisibility(View.VISIBLE);
                        bindingHome.txtTile.setText(getString(R.string.home));
                        bindingHome.txtTile.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        Home.bindingHome.searchView.setQuery("", false);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case 1:
                try {

                    LocationDetector.inst().stopLocationUpdates();
                    MyMethod.setVisible(Home.bindingHome.txtTile);
                    MyMethod.setGone(bindingHome.btnComeBack);
                    //if in check in
                    if (MyMethod.isVisible(Home.bindingRight.checkin.relaLayoutCheckin)) {
                        if (MyMethod.CheckInCustomer) {
                            Home.bitmapImage = null;
                            bindingHome.txtTile.setText(context.getString(R.string.customer_detail));
                            LayoutMyManager.ShowLayout(RightFragment.Layouts.CustomerDetail);
                        } else {
                            if (MyMethod.isCheckInInTransactionDetail) {//Nếu đang checkin trong chi tiết giao dịch
                                Home.bitmapImage = null;
                                Home.bindingHome.txtTile.setText(context.getString(R.string.transaction_detail));
                                LayoutMyManager.ShowLayout(RightFragment.Layouts.TransactionDetail);
                            } else if (MyMethod.isFinishInTransactionDetail) {
                                Home.bitmapImage = null;
                                Home.bindingHome.txtTile.setText(context.getString(R.string.transaction));
                                LayoutMyManager.ShowLayout(RightFragment.Layouts.Transaction);
                            } else if (MyMethod.isNoteInStore) {
                                Home.bitmapImage = null;
                                Home.bindingHome.txtTile.setText(context.getString(R.string.store));
                                LayoutMyManager.ShowLayout(RightFragment.Layouts.GoStore);
                            } else {
                                Home.bitmapImage = null;
                                Home.bindingHome.txtTile.setText(context.getString(R.string.app_dms));
                                LayoutMyManager.ShowLayout(RightFragment.Layouts.Main);
                            }
                        }

                    }
                    //if in check out
                    else if (MyMethod.isVisible(bindingRight.setting.linearSetting)) {
                        Home.bindingHome.txtTile.setText(context.getString(R.string.app_dms));
                        LayoutMyManager.ShowLayout(RightFragment.Layouts.Main);

                    } else if (MyMethod.isVisible(bindingRight.transaction.linearTransaction)) {
                        if (rightFragment.adapterTransaction != null)
                            rightFragment.adapterTransaction.notifyDataSetChanged();
                        Home.bindingHome.txtTile.setText(context.getString(R.string.app_dms));
                        LayoutMyManager.ShowLayout(RightFragment.Layouts.Main);

                    } else if (MyMethod.isVisible(Home.bindingRight.customer.linearCustomer)) {
                        if (rightFragment.adapterCustomer != null)
                            rightFragment.adapterCustomer.notifyDataSetChanged();
                        Home.bindingHome.txtTile.setText(context.getString(R.string.app_dms));
                        LayoutMyManager.ShowLayout(RightFragment.Layouts.Main);
                    } else if (MyMethod.isVisible(Home.bindingRight.product.linearProduct)) {
                        if (rightFragment.adapterProduct != null)
                            rightFragment.adapterProduct.notifyDataSetChanged();
                        Home.bindingHome.txtTile.setText(context.getString(R.string.app_dms));
                        LayoutMyManager.ShowLayout(RightFragment.Layouts.Main);
                    } else if (MyMethod.isVisible(bindingRight.orderMain.linearOrderMain)) {
                        if (rightFragment.adapterOrder != null)
                            rightFragment.adapterOrder.notifyDataSetChanged();
                        Home.bindingHome.txtTile.setText(context.getString(R.string.app_dms));
                        LayoutMyManager.ShowLayout(RightFragment.Layouts.Main);
                    } else if (MyMethod.isVisible(bindingRight.orderDetail.linearOrderDetail)) {
                        if (MyMethod.isVisible(bindingRight.inputValue.linearInputValue)) {
                            MyMethod.setGone(bindingRight.inputValue.linearInputValue);

                        } else if (MyMethod.isOrderInTransactionLine) {
                            if (MyMethod.isOpenTransactionLineFromStore) {
                                Home.bindingHome.txtTile.setText(context.getString(R.string.customer));
                                LayoutMyManager.ShowLayout(RightFragment.Layouts.GoStore);
                            } else {
                                Home.bindingHome.txtTile.setText(context.getString(R.string.transaction_detail));
                                LayoutMyManager.ShowLayout(RightFragment.Layouts.TransactionDetail);
                            }
                        } else {
                            if (MyMethod.isInventory)
                                Home.bindingHome.txtTile.setText(context.getString(R.string.report_inventory));
                            else if (MyMethod.isLoadOrder)
                                Home.bindingHome.txtTile.setText(context.getString(R.string.order_list));
                            else if (MyMethod.isInventoryEmployee)
                                Home.bindingHome.txtTile.setText(context.getString(R.string.inventory_employee));
                            else if (MyMethod.isInventoryBill)
                                Home.bindingHome.txtTile.setText(context.getString(R.string.inventory_bill));
                            LayoutMyManager.ShowLayout(RightFragment.Layouts.OrderList);
                        }


                    } else if (MyMethod.isVisible(bindingRight.gcm.linearListGcm)) {
                        Home.bindingHome.txtTile.setText(context.getString(R.string.app_dms));
                        LayoutMyManager.ShowLayout(RightFragment.Layouts.Main);

                    } else if (MyMethod.isVisible(Home.bindingRight.mapOrder.mapOrder)) {
                        Home.bindingHome.txtTile.setText(context.getString(R.string.order_list));

                    } else if (MyMethod.isVisible(bindingRight.customerUpdateMap.linearMapUpdate)) {
                        if (MyMethod.isGetLocationCreateCustomer) {
                            Home.bindingHome.txtTile.setText(context.getString(R.string.create_customer));
                            LayoutMyManager.ShowLayout(RightFragment.Layouts.CreateCustomer);
                        } else {
                            Home.bindingHome.txtTile.setText(context.getString(R.string.update));
                            LayoutMyManager.ShowLayout(RightFragment.Layouts.CustomerUpdate);
                        }
                    } else if (MyMethod.isVisible(bindingRight.customerViewMap.linearMapView)) {
                        if (MyMethod.isTransactionMapView) {
                            Home.bindingHome.txtTile.setText(context.getString(R.string.transaction_detail));
                            LayoutMyManager.ShowLayout(RightFragment.Layouts.TransactionDetail);
                        } else {
                            Home.bindingHome.txtTile.setText(context.getString(R.string.order_detail));
                            LayoutMyManager.ShowLayout(RightFragment.Layouts.CustomerDetail);
                        }
                    } else if (MyMethod.isVisible(Home.bindingRight.customerDetail.relaCustomerDetail)) {
                        Home.bindingHome.txtTile.setText(context.getString(R.string.customer));
                        LayoutMyManager.ShowLayout(RightFragment.Layouts.Customer);
                        EventPool.control().enQueue(new EventType.EventLoadCustomersRequest(nowRoute, Home.filterCustomer, -1, nowIdCustomer));
                    } else if (MyMethod.isVisible(Home.bindingRight.transactionDetail.relaTransactionDetail)) {
                        if (MyMethod.isVisible(bindingRight.note.linearInputNote)) {
                            MyMethod.setGone(bindingRight.note.linearInputNote);
                        } else {
                            if (MyMethod.isTransactionDetailOfHistory) {
                                Home.bindingHome.txtTile.setText(context.getString(R.string.history));
                                LayoutMyManager.ShowLayout(RightFragment.Layouts.History);
                            } else {
                                Home.bindingHome.txtTile.setText(context.getString(R.string.transaction));
                                LayoutMyManager.ShowLayout(RightFragment.Layouts.Transaction);
                            }
                        }
                    } else if (MyMethod.isVisible(Home.bindingRight.customerUpdate.relaCustomerUpdate)) {
                        Home.bindingHome.txtTile.setText(rightFragment.nowCustomer.name);
                        LayoutMyManager.ShowLayout(RightFragment.Layouts.CustomerDetail);
                    } else if (MyMethod.isVisible(Home.bindingRight.productDetail.relaProductDetail)) {
                        Home.bindingHome.txtTile.setText(rightFragment.nowProduct.name);
                        LayoutMyManager.ShowLayout(RightFragment.Layouts.Product);
                    } else if (MyMethod.isVisible(bindingRight.order.linearOrder)) {
                        if (MyMethod.isVisible(bindingRight.inputValue.linearInputValue)) {
                            MyMethod.setGone(bindingRight.inputValue.linearInputValue);
                        } else {
                            if (orderDetailArrayList.size() != 0) {//Nếu có sản phẩm thì hỏi có hủy không O.o
                                confirmOrder();
                            } else {//Nếu ko có thì bỏ nuôn :))
                                if (MyMethod.isInventoryInput) {
                                    Home.bindingHome.txtTile.setText(context.getString(R.string.app_dms));
                                    LayoutMyManager.ShowLayout(RightFragment.Layouts.Main);
                                } else if (MyMethod.isOrderPhone) {
                                    Home.bindingHome.txtTile.setText(context.getString(R.string.customer));
                                    LayoutMyManager.ShowLayout(RightFragment.Layouts.Customer);
                                } else {
                                    Home.bindingHome.txtTile.setText(context.getString(R.string.customer));
                                    LayoutMyManager.ShowLayout(RightFragment.Layouts.GoStore);
                                }
                            }
                        }


                    } else if (MyMethod.isVisible(bindingRight.orderProduct.linearProductOfOrder)) {
                        if (MyMethod.isCheckInCustomerTransactionDetail) {
                            Home.bindingHome.txtTile.setText(context.getString(R.string.transaction_detail));
                            LayoutMyManager.ShowLayout(RightFragment.Layouts.MapCustomerCheckIn);
                        } else {
                            if (MyMethod.isVisible(bindingRight.inputValue.linearInputValue)) {
                                MyMethod.setGone(bindingRight.inputValue.linearInputValue);

                            } else {
                                if (MyMethod.isOrderEditing) {
                                    Home.bindingHome.txtTile.setText(context.getString(R.string.order_detail));
                                    LayoutMyManager.ShowLayout(RightFragment.Layouts.OrderDetail);
                                } else {
                                    Home.bindingHome.txtTile.setText(context.getString(R.string.order));
                                    LayoutMyManager.ShowLayout(RightFragment.Layouts.Order);
                                }
                            }
                        }

                    } else if (MyMethod.isVisible(Home.bindingRight.mapCheckInCustomer.relaMapCheckinOrder)) {
                        if (MyMethod.isCheckInCustomerTransactionDetail) {
                            Home.bindingHome.txtTile.setText(context.getString(R.string.transaction_detail));
                            LayoutMyManager.ShowLayout(RightFragment.Layouts.TransactionDetail);
                        } else {
                            if (MyMethod.isMapViewImageLocation) {
                                if (MyMethod.isOpenTransactionLineFromStore) {
                                    Home.bindingHome.txtTile.setText(context.getString(R.string.customer));
                                    LayoutMyManager.ShowLayout(RightFragment.Layouts.GoStore);
                                } else {
                                    Home.bindingHome.txtTile.setText(context.getString(R.string.transaction_detail));
                                    LayoutMyManager.ShowLayout(RightFragment.Layouts.TransactionDetail);
                                }

                            } else {
                                Home.bindingHome.txtTile.setText(context.getString(R.string.customer));
                                LayoutMyManager.ShowLayout(RightFragment.Layouts.Customer);
                            }
                        }

                    } else if (MyMethod.isVisible(bindingRight.inStore.linearInStore)) {
                        if (MyMethod.isHasOrder) {//Nếu có đơn hàng
                            confirmOutStore();
                        } else {//Nếu không có đơn hàng
                            LayoutLoadingManager.Show_OnLoading(Home.bindingRight.reasonNotOrder.ReasonLoadingView, context.getString(R.string.load_reason), 30);
                            Home.LayoutMyManager.ShowLayout(RightFragment.Layouts.ReasonNotOrder);
                            if (LocalDB.inst().countReason() > 0) {//Lấy dữ liệu offline
                                rightFragment.reasonArrayList.clear();
                                rightFragment.reasonArrayList = LocalDB.inst().loadReason();
                                rightFragment.adapterReasonNotOrder.setItems(rightFragment.reasonArrayList);
                                rightFragment.adapterReasonNotOrder.notifyDataSetChanged();
                                LayoutLoadingManager.Show_OffLoading(Home.bindingRight.reasonNotOrder.ReasonLoadingView);
                            } else {
                                //Nếu ko có thì load online
                                EventPool.control().enQueue(new EventType.EventLoadReasonNotOrdersRequest());
                            }

                        }
                    } else if (MyMethod.isVisible(Home.bindingRight.createCustomer.relaCreateCustomer)) {
                        if (MyMethod.isVisible(bindingRight.selectRoute.linearSelectRoute)) {
                            MyMethod.setGone(bindingRight.selectRoute.linearSelectRoute);

                        } else {
                            Home.bindingHome.txtTile.setText(context.getString(R.string.customer));
                            LayoutMyManager.ShowLayout(RightFragment.Layouts.Customer);
                        }
                    } else if (MyMethod.isVisible(bindingRight.history.linearHistory)) {
                        adapterTimeLine.notifyDataSetChanged();
                        Home.bindingHome.txtTile.setText(context.getString(R.string.app_dms));
                        LayoutMyManager.ShowLayout(RightFragment.Layouts.Main);
                    } else if (MyMethod.isVisible(bindingRight.inventoryEmployee.linearInventoryEmployee)) {
                        Home.bindingHome.txtTile.setText(context.getString(R.string.app_dms));
                        LayoutMyManager.ShowLayout(RightFragment.Layouts.Main);
                    } else if (MyMethod.isVisible(bindingRight.approvalAppLock.linearAppprovalAppLock)) {
                        if (MyMethod.isVisible(bindingRight.approvalButton.linearApprovalButton)) {
                            MyMethod.setGone(bindingRight.approvalButton.linearApprovalButton);
                        } else {
                            Home.bindingHome.txtTile.setText(context.getString(R.string.app_dms));
                            LayoutMyManager.ShowLayout(RightFragment.Layouts.Main);
                        }
                    } else if (MyMethod.isVisible(bindingRight.reportWeb.linearReportWeb)) {
                        Home.bindingRight.reportWeb.webview.loadUrl("about:blank");
                        Home.bindingHome.txtTile.setText(context.getString(R.string.app_dms));
                        LayoutMyManager.ShowLayout(RightFragment.Layouts.Main);
                    } else if (MyMethod.isVisible(bindingRight.reportTimeCheckIn.linearReportCheckIn)) {
                        Home.bindingHome.txtTile.setText(context.getString(R.string.app_dms));
                        LayoutMyManager.ShowLayout(RightFragment.Layouts.Main);
                    } else if (MyMethod.isVisible(bindingRight.updateImageCustomer.linearUpdateImage)) {
                        Home.bindingHome.txtTile.setText(context.getString(R.string.customer));
                        LayoutMyManager.ShowLayout(RightFragment.Layouts.MapCustomerCheckIn);
                    } else if (MyMethod.isVisible(bindingRight.reasonNotOrder.linearReasonNotOrder)) {
                        Home.bindingHome.txtTile.setText(context.getString(R.string.go_store));
                        LayoutMyManager.ShowLayout(RightFragment.Layouts.GoStore);
                    } else if (MyMethod.isVisible(bindingRight.loginRoute.linearLogInRoute)) {
                        if (MyMethod.logInRouteInSetting) {
                            //Nếu mở từ màn hình cài đặt
                            Home.bindingHome.txtTile.setText(context.getString(R.string.setting));
                            LayoutMyManager.ShowLayout(RightFragment.Layouts.Setting);
                        } else {
                            //Mở từ màn hình đăng nhập
                            Home.bindingHome.txtTile.setText(context.getString(R.string.log_in));
                            LayoutMyManager.ShowLayout(RightFragment.Layouts.LogIn);
                        }
                    } else if (MyMethod.isVisible(bindingRight.inputValue.linearInputValue)) {
                        MyMethod.setGone(bindingRight.inputValue.linearInputValue);
                        LayoutMyManager.ShowLayout(RightFragment.Layouts.ProductOfOrder);
                    } else {
                        bindingHome.viewpager.setCurrentItem(0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }

    }

    private void confirmOrder() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(context.getString(R.string.confirm_order_cancel));
        alertDialogBuilder.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int arg1) {
                Home.orderDetailArrayList.clear();
                Home.orderListProductAdapter.notifyDataSetChanged();
                Home.txtOrderAmount.setText("0 " + context.getString(R.string.money));
                Home.edOrderNote.setText("");
                Home.txtOrderDiscount.setText("0 " + context.getString(R.string.money));
                Home.txtOrderAmountSale.setText("0 " + context.getString(R.string.money));
                Home.nowAmount = 0;
                Home.nowAmountSale = 0;
                Home.nowDiscount = 0;
                dialog.dismiss();
                if (MyMethod.isInventoryInput) {
                    LayoutMyManager.ShowLayout(RightFragment.Layouts.Main);
                } else
                    LayoutMyManager.ShowLayout(RightFragment.Layouts.GoStore);
            }
        });

        alertDialogBuilder.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void confirmOutStore() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(context.getString(R.string.confirm_out_store));

        alertDialogBuilder.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int arg1) {
                dialog.dismiss();
                if (!MyMethod.isOrderPhone) {//Nếu không phải đặt hàng qua đt thì gửi giao dịch rời cửa hàng
                    RightFragment.flagOutStore = true;
                    if (Home.handler != null && Home.runnableStore != null) {
                        handler.removeCallbacks(runnableStore);
                    }
                    Home.nowTransactionLine.id_customer = rightFragment.nowCustomer.id;
                    Home.nowTransactionLine.status = 99;
                    Home.nowTransactionLine.note = "Rời cửa hàng";
                    Home.nowTransactionLine.location_ref_id = (int) LocalDB.inst().addTracking(new TrackingItem(Home.location, (byte) 2));
                    Home.nowTransactionLine.id_ExtNo_ = 0;
                    Home.nowTransactionLine.id_transaction_define = Const.TransactionType.CheckOut.getValue();
                    Home.nowTransactionLine.latitude = Home.location.getLatitude();
                    Home.nowTransactionLine.longitude = Home.location.getLongitude();
                    EventPool.control().enQueue(new EventType.EventSendTransactionRequest(Home.nowTransactionLine));
                }
                bindingHome.btnComeBack.setText(context.getString(R.string.customer));
                LayoutMyManager.ShowLayout(RightFragment.Layouts.Customer);
            }
        });

        alertDialogBuilder.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    //DETECT PRESS HOME BUTTON
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.w(TAG, "getDataFromIntent: onNewIntent");
        getDataFromIntent(intent);//Nhận dữ liệu từ Message Service
        if (Intent.ACTION_MAIN.equals(intent.getAction())) {
            final boolean alreadyOnHome =
                    ((intent.getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)
                            != Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            if (alreadyOnHome) {
                try {
                    if (Home.bindingCenter.listApps.linearListApp != null)
                        MyMethod.setGone(Home.bindingCenter.listApps.linearListApp);
                    if (bindingHome.appBar != null) bindingHome.appBar.setVisibility(View.VISIBLE);
                    if (Home.bindingCenter.searchBoxs.llSearchBox != null)
                        MyMethod.setGone(Home.bindingCenter.searchBoxs.llSearchBox);
                    if (Home.bindingCenter.relaLayoutCenter != null) {
                        MyMethod.setVisible(Home.bindingCenter.relaLayoutCenter);
                        MyMethod.closeFocus(Home.bindingCenter.relaLayoutCenter);
                    }
                    if (Home.bindingHome.searchView != null)
                        Home.bindingHome.searchView.setQuery("", false);
                    bindingHome.viewpager.setCurrentItem(0);
                    MyMethod.hideKeyboardAll(Home.this);
                    bindingHome.searchView.setVisibility(View.GONE);
                    bindingHome.txtTile.setVisibility(View.VISIBLE);
                    bindingHome.txtTile.setText(getString(R.string.home));
                    bindingHome.txtTile.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnComeBack:
                onBackPressed();
                break;
            case R.id.txtTile:
                if (bindingHome.txtTile.getText().toString().contains(context.getString(R.string.list_app))) {
                    bindingHome.searchView.setVisibility(View.VISIBLE);
                    bindingHome.searchView.setIconified(false);
                    bindingHome.txtTile.setVisibility(View.GONE);
                }
                break;
            default:
                break;
        }
    }


}