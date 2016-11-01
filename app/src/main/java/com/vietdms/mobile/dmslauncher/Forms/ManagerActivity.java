package com.vietdms.mobile.dmslauncher.Forms;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.vietdms.mobile.dmslauncher.CustomAdapter.ArrayUserAdapter;
import com.vietdms.mobile.dmslauncher.CustomAdapter.CustomAdapterTrackingList;
import com.vietdms.mobile.dmslauncher.CustomClass.LayoutLoadingManager;
import com.vietdms.mobile.dmslauncher.CustomView.LoadingView;
import com.vietdms.mobile.dmslauncher.Home;
import com.vietdms.mobile.dmslauncher.MyMethod;
import com.vietdms.mobile.dmslauncher.R;
import com.vietdms.mobile.dmslauncher.databinding.ActivityManagerBinding;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import CommonLib.Const;
import CommonLib.EventPool;
import CommonLib.EventType;
import CommonLib.Model;
import CommonLib.MyLocation;
import CommonLib.UserInfo;
import CommonLib.Utils;
import fr.ganfra.materialspinner.MaterialSpinner;

public class ManagerActivity extends AppCompatActivity implements OnMapReadyCallback, AdapterView.OnItemSelectedListener, View.OnClickListener, CompoundButton.OnCheckedChangeListener, CalendarDatePickerDialogFragment.OnDateSetListener, GoogleMap.OnMarkerClickListener, AdapterView.OnItemClickListener {

    private static final String TAG = "ManagerActivity";
    private static final String FRAG_TAG_DATE_PICKER = "ManagerActivity";
    private GoogleMap googleMap;
    private ActivityManagerBinding binding;
    private ArrayAdapter<UserInfo> adapterStaff;
    private ArrayList<UserInfo> arrStaff;
    private SupportMapFragment mapFragment;
    private Handler handler;
    private Runnable runnableRealtime;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            EventType.EventBase event = EventPool.view().deQueue();
            try {
                while (event != null) {
                    processEvent(event);
                    event = EventPool.view().deQueue();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private Date from, to;
    public HashMap<Integer, Marker> hashUserMarker;
    private LatLng VIETNAM = new LatLng(17, 107);
    private int timeCount;
    private boolean userNeedTracking;//Người dùng chọn tracking
    private int countRealTime;//Đếm số lần realtime
    private ArrayList<MyLocation> arrDetailTracking;
    private CustomAdapterTrackingList adapterTracking;
    private HashMap<Marker, Float> hashMarker;// Hashmap chứa marker
    private LatLng currentPostition;//Vị trí hiện tại
    private ArrayList<Circle> arrCircles;
    private Marker markerClick;
    private boolean isSeeTracking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.w(TAG, "onCreate");
        binding = DataBindingUtil.setContentView(this, R.layout.activity_manager);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("OnFire"));
        handler = new Handler();
        init();
        getData();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        // Add a marker in Sydney and move the camera
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(VIETNAM, 5.5f));
        googleMap.setOnMarkerClickListener(this);
    }

    void setSpinnerColorWhite(AdapterView<?> parent, int Color) {
        if (MyMethod.isVisible(Home.bindingRight.createCustomer.relaCreateCustomer))
            return;
        TextView textSpinner = null;
        try {
            textSpinner = (TextView) (((RelativeLayout) (((RelativeLayout) ((parent.getChildAt(0)))).getChildAt(0))).getChildAt(0));
        } catch (Exception e) {
            try {
                textSpinner = (TextView) parent.getChildAt(0);
            } catch (Exception ex) {
            }
        }
        try {
            if (textSpinner != null) {
                textSpinner.setTextColor(Color);
                textSpinner.setTextSize(parent.getResources().getDimensionPixelSize(R.dimen.textFontSize4) / parent.getResources().getDisplayMetrics().density);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
        setSpinnerColorWhite(parent, Color.BLACK);
        switch (parent.getId()) {
            case R.id.spStaff://Nếu chọn spinner nhân viên
                if (position >= 0) {
                    binding.content.layoutDate.setVisibility(View.VISIBLE);
                    Date fromDate = Utils.long2date(Model.getServerTime() - 7 * 3600 * 1000);
                    Date toDate = Utils.long2date(Model.getServerTime() + 17 * 3600 * 1000);
                    from = new Date(fromDate.getYear(), fromDate.getMonth(), fromDate.getDate(), 0, 0);
                    to = new Date(toDate.getYear(), toDate.getMonth(), toDate.getDate(), 0, 0);
                    binding.content.txtRangeDate.setText("Lộ trình từ " + Utils.long2Date(from.getTime()) + " đến " + Utils.long2Date(to.getTime()));
                    binding.content.btnFromDate.setText(Utils.long2Date(from.getTime()));
                    binding.content.btnToDate.setText(Utils.long2Date(to.getTime()));
                    Marker temp =
                            hashUserMarker.get(arrStaff.get(position).id_employee);
                    if (temp != null) {
                        temp.showInfoWindow();
                        if (googleMap != null)
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(temp.getPosition(), 15.5f));
                    }
                    //realtime nhan vien
                    binding.content.linearRealtime.setEnabled(true);
                    binding.content.swRealtime.setEnabled(true);
                    List<Integer> arr = new ArrayList<>();
                    arr.add(adapterStaff.getItem(position).id_employee);
                    EventPool.control().enQueue(new EventType.EventRealTimeTrackingRequest(arr));
                } else {
                    //chu ki 20s request 1 lan vi tri tat ca
                    isSeeTracking = false;
                    MyMethod.isRealTime = false;
                    binding.content.linearRealtime.setEnabled(false);
                    binding.content.swRealtime.setEnabled(false);
                    binding.content.txtRangeDate.setText("Vị trí tất cả nhân viên");
                    binding.content.layoutDate.setVisibility(View.GONE);
                    binding.content.linearListTracking.setVisibility(View.GONE);
                    if (MyMethod.isUpdateLocation) {
                        LayoutLoadingManager.Show_OffLoading(binding.content.ManagerLoadingView);
                        drawMapNotLoading(this, mapFragment, arrStaff, binding.content.spStaff, false);
                    } else {
                        if (googleMap != null) googleMap.clear();
                        hashUserMarker.clear();
                        drawMap(binding.content.ManagerLoadingView, this, mapFragment, arrStaff, binding.content.spStaff);
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    protected void onResume() {
        Log.w(TAG, "onResume");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.w(TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.w(TAG, "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.w(TAG, "onStop");
        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
            //unregisterReceiver(receiver);
            Log.w(TAG, "unregisterReceiver");
            handler.removeCallbacks(runnableRealtime);
            Log.w(TAG, "removeCallbacks Realtime");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnFromDate:
                MyMethod.Date = 0;
                CalendarDatePickerDialogFragment dateReport = new CalendarDatePickerDialogFragment()
                        .setOnDateSetListener(this)
                        .setFirstDayOfWeek(Calendar.SUNDAY)
                        .setThemeDark(false);
                dateReport.show(getSupportFragmentManager(), FRAG_TAG_DATE_PICKER);
                break;
            case R.id.btnToDate:
                MyMethod.Date = 1;
                CalendarDatePickerDialogFragment toDate = new CalendarDatePickerDialogFragment()
                        .setOnDateSetListener(this)
                        .setFirstDayOfWeek(Calendar.SUNDAY)
                        .setThemeDark(false);
                toDate.show(getSupportFragmentManager(), FRAG_TAG_DATE_PICKER);
                break;
            case R.id.btnSeeAgain:
                if (isDateOK()) {
                    if (Utils.long2Day(to.getTime() - from.getTime()) <= 3) {
                        isSeeTracking = true;
                        LayoutLoadingManager.Show_OnLoading(binding.content.ManagerLoadingView, this.getString(R.string.load_tracking), 30);
                        binding.content.txtRangeDate.setText("Lộ trình từ " + binding.content.btnFromDate.getText() + " đến " + binding.content.btnToDate.getText());
                        EventPool.control().enQueue(new EventType.EventGetLocationsRequest(((UserInfo) binding.content.spStaff.getSelectedItem()).id_employee, from.getTime(), to.getTime() + 24 * 3600 * 1000 - 1, 0, true));
                    } else
                        MyMethod.showToast(binding,this, this.getString(R.string.see_again_date_limit));
                }

                break;
            case R.id.btnSeeAll:
                if (isDateOK()) {
                    if (Utils.long2Day(to.getTime() - from.getTime()) <= 1) {
                        isSeeTracking = true;
                        LayoutLoadingManager.Show_OnLoading(binding.content.ManagerLoadingView, this.getString(R.string.load_tracking_all), 30);
                        binding.content.txtRangeDate.setText("Lộ trình từ " + binding.content.btnFromDate.getText() + " đến " + binding.content.btnToDate.getText());
                        EventPool.control().enQueue(new EventType.EventGetLocationsRequest(((UserInfo) binding.content.spStaff.getSelectedItem()).id_employee, from.getTime(), to.getTime() + 24 * 3600 * 1000 - 1, 0, false));
                    } else
                        MyMethod.showToast(binding,this, this.getString(R.string.see_all_date_limit));
                }
                break;
            case R.id.btn_zoom_in_map:
                //Zoom map len
                if (googleMap != null) {
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentPostition == null ? googleMap.getCameraPosition().target : currentPostition, Const.ZoomInMapLevel);
                    googleMap.animateCamera(cameraUpdate);
                }
                break;
            case R.id.btn_zoom_out_map:
                //Zoom map xuong
                if (googleMap != null) {
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentPostition == null ? googleMap.getCameraPosition().target : currentPostition, Const.ZoomOutMapLevel);
                    googleMap.animateCamera(cameraUpdate);
                }
                break;
            case R.id.txtHideTracking:
                binding.content.linearListTracking.setVisibility(View.GONE);
                binding.content.txtShowTracking.setVisibility(View.VISIBLE);
                binding.content.txtHideTracking.setVisibility(View.INVISIBLE);
                break;
            case R.id.txtShowTracking:
                binding.content.linearListTracking.setVisibility(View.VISIBLE);
                binding.content.txtHideTracking.setVisibility(View.VISIBLE);
                binding.content.txtShowTracking.setVisibility(View.INVISIBLE);
                break;
            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        switch (compoundButton.getId()) {
            case R.id.swRealtime:
                if (isChecked) {
                    //Bật realtime
                    userNeedTracking = true;
                    MyMethod.isRealTime = true;
                } else {
                    //Tắt realtime
                    userNeedTracking = false;
                    MyMethod.isRealTime = false;
                }
                break;
        }
    }

    @Override
    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
        DateFormat formatDate = new SimpleDateFormat("dd/MM/yy");
        switch (MyMethod.Date) {
            case 0:
                //from date set
                from = new Date(year - 1900, monthOfYear, dayOfMonth);
                binding.content.btnFromDate.setText(formatDate.format(from.getTime()));
                break;
            case 1:
                //to date set
                to = new Date(year - 1900, monthOfYear, dayOfMonth);
                binding.content.btnToDate.setText(formatDate.format(to.getTime()));
                break;
            default:
                break;
        }
    }

    //METHOD
    private void init() {
        Log.w(TAG, "init");
        setSupportActionBar(binding.toolbar);
        try {
            getSupportActionBar().setIcon(R.drawable.manager_btn);
        } catch (NullPointerException e) {
            Log.w(TAG, "getSupportActionBar() " + e.toString());
        }
        binding.toolbar.setNavigationIcon(R.drawable.left_arrow_btn);
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        arrStaff = new ArrayList();
        arrDetailTracking = new ArrayList<>();
        arrCircles = new ArrayList<>();
        adapterStaff = new ArrayUserAdapter(this, R.layout.custom_adapterstaff, arrStaff);
        adapterTracking = new CustomAdapterTrackingList(
                this,
                R.layout.list_tracking,// lấy custom layout
                arrDetailTracking/*thiết lập data_ source*/);
        binding.content.spStaff.setAdapter(adapterStaff);
        binding.content.listTrackingDetail.setAdapter(adapterTracking);
        hashUserMarker = new HashMap<>();
        hashMarker = new HashMap<>();

        //register event
        binding.content.spStaff.setOnItemSelectedListener(this);
        binding.content.btnFromDate.setOnClickListener(this);
        binding.content.btnToDate.setOnClickListener(this);
        binding.content.btnSeeAgain.setOnClickListener(this);
        binding.content.btnSeeAll.setOnClickListener(this);
        binding.content.btnZoomInMap.setOnClickListener(this);
        binding.content.btnZoomOutMap.setOnClickListener(this);
        binding.content.swRealtime.setOnCheckedChangeListener(this);
        binding.content.listTrackingDetail.setOnItemClickListener(this);
        binding.content.txtHideTracking.setOnClickListener(this);
        binding.content.txtShowTracking.setOnClickListener(this);
    }

    private void getData() {
        Log.w(TAG, "getData");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                LayoutLoadingManager.Show_OnLoading(binding.content.ManagerLoadingView, getString(R.string.load_employee), 30);
                EventPool.control().enQueue(new EventType.EventGetUsersRequest());
            }
        }, 1000);
        timeCount = 0;
        final int timeRealAll = Const.checkStateUpdateLocation;
        final int timeRealOne = Const.checkStateRealtime;
        runnableRealtime = new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                Log.v("QueueTimerView", "RealTime");
                try {
                    timeCount++;
                    if (timeCount % timeRealAll == 0) {
                        if (!MyMethod.isRealTime && !isSeeTracking) {
                            Log.w("REALTIME", "TẤT CẢ");
                            MyMethod.isUpdateLocation = true;
                            EventPool.control().enQueue(new EventType.EventGetUsersRequest());
                        }
                    }
                    //Nếu đang tracking
                    if (timeCount % timeRealOne == 0) {
                        if (MyMethod.isRealTime) {//Gửi get location mới nh
                            Log.w("REALTIME", adapterStaff.getItem(binding.content.spStaff.getSelectedItemPosition() - 1).fullname);
                            EventPool.control().enQueue(new EventType.EventGetLastLocationRequest(adapterStaff.getItem(binding.content.spStaff.getSelectedItemPosition() - 1).id_employee));
                        }
                    }


                } catch (Exception ex) {
                }
                handler.postDelayed(this, 1000);
            }
        };
        handler.postDelayed(runnableRealtime, 1000);


    }

    private void clearAdapter() {
        arrStaff.clear();
        adapterStaff.notifyDataSetChanged();
    }

    private boolean isDateOK() {
        if (from == null) {
            MyMethod.showToast(binding,this, this.getString(R.string.please_select_from_date));
            return false;
        }
        if (to == null) {
            MyMethod.showToast(binding,this, this.getString(R.string.please_select_to_date));
            return false;
        }
        return true;
    }


    private synchronized void drawMapNotLoading(final Context context, final SupportMapFragment map, final ArrayList<UserInfo> infoArrayList, final MaterialSpinner spinner, final boolean isMoveMap) {
        try {
            map.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {

                    new LoadingAllNotLoading(googleMap, context, infoArrayList, spinner, isMoveMap).execute();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private synchronized void drawMap(final LoadingView loadingView, final Context context, final SupportMapFragment map, final ArrayList<UserInfo> infoArrayList, final MaterialSpinner spinner) {
        try {
            map.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {

                    new LoadingAll(loadingView, googleMap, context, infoArrayList, spinner).execute();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private synchronized void drawMap(final LoadingView loadingView, final Context context, final SupportMapFragment map, final MyLocation[] arrayLocations, final ArrayList<UserInfo> infoArrayList, final MaterialSpinner spinner) {
        try {
            map.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    //new LoadingAll(loadingView, googleMap, context, infoArrayList, spinner).execute();
                    new Loading(loadingView, googleMap, context, arrayLocations, spinner).execute();

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        try {
            int position = MyMethod.getIndex(binding.content.spStaff, marker.getTitle().split("\n")[0]) + 1;
            binding.content.spStaff.setSelection(position);
        } catch (Exception e) {
            Log.e("onMarkerClick", e.getMessage());
        }
        for (int i = 0; i < arrCircles.size(); i++) {
            arrCircles.get(i).remove();
        }
        double accuracy;
        try {
            accuracy = (double) hashMarker.get(marker);
        } catch (Exception e) {
            Log.e("onMarkerClick", e.getMessage(), e);
            accuracy = 0;
        }
        if (accuracy != 0) {
            CircleOptions co = new CircleOptions();
            co.center(marker.getPosition());
            co.radius(accuracy);
            co.fillColor(Color.parseColor("#4D2E9AFE")); // mờ 70%
            co.strokeColor(Color.parseColor("#2E9AFE"));
            co.strokeWidth(1.0f);
            arrCircles.add(googleMap.addCircle(co));
        }

        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
        switch (parent.getId()) {
            case R.id.listTrackingDetail:
                MyLocation location = (MyLocation) parent.getAdapter().getItem(position);
                if (markerClick != null) markerClick.remove();
                markerClick =
                        googleMap.addMarker(new MarkerOptions()
                                .position(new LatLng(location.latitude, location.longitude))
                                .title(location.address + "\n" + Utils.long2String(location.locationDate))
                                .snippet("Thời gian dừng : " + Utils.minute2String((location.milisecFreezed / 60000))));
                markerClick.showInfoWindow();
                break;
            default:
                break;
        }
    }

    private class Loading extends AsyncTask<PolylineOptions, Void, PolylineOptions> {
        LoadingView lv;
        GoogleMap googleMap;
        Context context;
        boolean flag = true;// kiểm tra nếu ở 1 chỗ
        MyLocation[] arrayLocations;
        ArrayList<MyLocation> myLocations = new ArrayList<>();
        ArrayList<String> myAddress = new ArrayList<>();
        ArrayList<MarkerOptions> myMarker = new ArrayList<>();
        ArrayList<Float> myAccuracy = new ArrayList<>();
        MaterialSpinner spinner;


        public Loading(LoadingView loadingView, GoogleMap googleMap, Context context, MyLocation[] arrayLocations, MaterialSpinner spinner) {
            lv = loadingView;
            this.googleMap = googleMap;
            this.context = context;
            this.arrayLocations = arrayLocations;
            this.spinner = spinner;
        }

        @Override
        protected PolylineOptions doInBackground(PolylineOptions... params) {
            ArrayList<LatLng> points = new ArrayList<>();
            PolylineOptions lineOptions = new PolylineOptions();

            for (MyLocation location : arrayLocations) {
                if (location.locationDate != 0 && location.distanceMeter != 0) {
                    points.add(new LatLng(location.latitude, location.longitude));
                    flag = false;
                    myLocations.add(location);
                    if (location.address == null || location.address.isEmpty()) {
                        location.address = MyMethod.getAddress(location.latitude, location.longitude, context);
                    }
                    myAddress.add(location.address);
                    if (myLocations.size() == 1) {
                        if (location.milisecFreezed / 60000 > 0) {
                            myMarker.add(new MarkerOptions()
                                    .position(new LatLng(location.latitude, location.longitude))
                                    .title(myAddress.get(myAddress.size() - 1) + "\n" + Utils.long2String(location.locationDate))
                                    .snippet("Điểm đầu - Thời gian dừng : " + Utils.minute2String((location.milisecFreezed / 60000)))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_btn)));
                        } else {
                            if (location.milisecElapsed != 0) {
                                myMarker.add(new MarkerOptions()
                                        .position(new LatLng(location.latitude, location.longitude))
                                        .title(myAddress.get(myAddress.size() - 1) + "\n" + Utils.long2String(location.locationDate))
                                        .snippet("Điểm đầu - " + "Tốc độ : " + location.distanceMeter * 3600 / location.milisecElapsed + "km/h ")
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_btn)));
                            } else {
                                myMarker.add(new MarkerOptions()
                                        .position(new LatLng(location.latitude, location.longitude))
                                        .title(myAddress.get(myAddress.size() - 1) + "\n" + Utils.long2String(location.locationDate))
                                        .snippet("Điểm đầu - " + "Tốc độ : " + "0 km/h ")
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_btn)));
                            }
                        }

                        myAccuracy.add(location.accuracy);
                    } else {
                        if ((myLocations.get(myLocations.size() - 1).milisecFreezed / 60000) > 0) {
                            myMarker.add(new MarkerOptions()
                                    .position(new LatLng(location.latitude, location.longitude))
                                    .title(myAddress.get(myAddress.size() - 1) + "\n" + Utils.long2String(location.locationDate))
                                    .snippet("Thời gian dừng : " + Utils.minute2String((myLocations.get(myLocations.size() - 1).milisecFreezed / 60000)))
                            );
                            myAccuracy.add(location.accuracy);
                        } else {
                            if (location.milisecElapsed != 0) {
                                myMarker.add(new MarkerOptions()
                                        .position(new LatLng(location.latitude, location.longitude))
                                        .title(myAddress.get(myAddress.size() - 1) + "\n" + Utils.long2String(location.locationDate))
                                        .snippet("Tốc độ : " + location.distanceMeter * 3600 / location.milisecElapsed + "km/h ")
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.dot_btn)));
                            } else {
                                myMarker.add(new MarkerOptions()
                                        .position(new LatLng(location.latitude, location.longitude))
                                        .title(myAddress.get(myAddress.size() - 1) + "\n" + Utils.long2String(location.locationDate))
                                        .snippet("Tốc độ : " + "0 km/h ")
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.dot_btn)));
                            }
                            myAccuracy.add(location.accuracy);
                        }

                    }
                    // }
                }
            }
            if (flag) {
                myMarker.add(new MarkerOptions()
                        .position(new LatLng(arrayLocations[0].latitude, arrayLocations[0].longitude))
                        .title(MyMethod.getAddress(arrayLocations[0].latitude, arrayLocations[0].longitude, context))
                        .snippet("Điểm cuối : " + Utils.minute2String((arrayLocations[0].milisecFreezed / 60000)))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.here_btn)));
                myAccuracy.add(arrayLocations[0].accuracy);
            }
            lineOptions.color(Color.BLUE);
            lineOptions.addAll(points);
            lineOptions.width(3);
            return lineOptions;
        }

        @Override
        protected void onPostExecute(PolylineOptions result) {
            googleMap.addPolyline(result);
            for (int i = 0; i < myMarker.size(); i++) {
                if (!flag && i == myMarker.size() - 1) {
                    String snippet = myMarker.get(i).getSnippet();
                    myMarker.get(i).snippet("Điểm cuối - " + snippet).icon(BitmapDescriptorFactory.fromResource(R.drawable.here_btn));
                    Marker markerEnd = googleMap.addMarker(myMarker.get(i));
                    markerEnd.showInfoWindow();
                    currentPostition = markerEnd.getPosition();
                    hashMarker.put(markerEnd, myAccuracy.get(i));
                } else if (!flag && i == myMarker.size() - 1) {
                    hashMarker.put(googleMap.addMarker(myMarker.get(i)), myAccuracy.get(i));
                } else if (myMarker.get(i).getIcon() == null) {
                    myMarker.get(i).icon(BitmapDescriptorFactory.fromBitmap(MyMethod.drawText(context, String.valueOf(i), R.drawable.stop_btn, Color.RED)));
                    hashMarker.put(googleMap.addMarker(myMarker.get(i)), myAccuracy.get(i));
                } else {
                    hashMarker.put(googleMap.addMarker(myMarker.get(i)), myAccuracy.get(i));
                }
            }
            CameraPosition newCamPos;
            if (!flag) {
                newCamPos = new CameraPosition(new LatLng(myLocations.get(myLocations.size() - 1).latitude, myLocations.get(myLocations.size() - 1).longitude),
                        13f,
                        googleMap.getCameraPosition().tilt, //use old tilt
                        googleMap.getCameraPosition().bearing); //use old bearing
            } else {
                newCamPos = new CameraPosition(new LatLng(arrayLocations[arrayLocations.length - 1].latitude, arrayLocations[arrayLocations.length - 1].longitude),
                        13f,
                        googleMap.getCameraPosition().tilt, //use old tilt
                        googleMap.getCameraPosition().bearing); //use old bearing
            }
            googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                @Override
                public View getInfoWindow(Marker arg0) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    View v = LayoutInflater.from(context).inflate(R.layout.custom_info_window, null);
                    TextView title = (TextView) v.findViewById(R.id.title);
                    title.setTextColor(Color.BLACK);
                    title.setText(marker.getTitle());
                    TextView snippet = (TextView) v.findViewById(R.id.snippet);
                    snippet.setTextColor(Color.BLACK);
                    snippet.setText(marker.getSnippet());
                    return v;
                }
            });
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(newCamPos), 4000, null);
            spinner.setEnabled(true);
            lv.setLoading(false);

        }


    }

    private class LoadingAll extends AsyncTask<Void, Void, Void> {
        LoadingView lv;
        GoogleMap googleMap;
        Context context;
        ArrayList<UserInfo> infoArrayList = new ArrayList<>();
        MaterialSpinner spinner;
        ArrayList<MarkerOptions> myMarkers = new ArrayList<>();
        ArrayList<Float> myAccuracy = new ArrayList<>();
        HashMap<Integer, Integer> tempId = new HashMap<>();//ew

        public LoadingAll(LoadingView loadingView, GoogleMap googleMap, Context context, ArrayList<UserInfo> infoArrayList, MaterialSpinner spinner) {
            lv = loadingView;
            this.googleMap = googleMap;
            this.context = context;
            this.infoArrayList = infoArrayList;
            this.spinner = spinner;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            for (int i = 0; i < myMarkers.size(); i++) {
                Marker marker = googleMap.addMarker(myMarkers.get(i));
                hashUserMarker.put(tempId.get(i), marker);
                hashMarker.put(marker, myAccuracy.get(i));
            }
            googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                @Override
                public View getInfoWindow(Marker arg0) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    View v = LayoutInflater.from(context).inflate(R.layout.custom_info_window, null);
                    TextView title = (TextView) v.findViewById(R.id.title);
                    title.setTextColor(Color.BLACK);
                    title.setText(marker.getTitle());
                    TextView snippet = (TextView) v.findViewById(R.id.snippet);
                    snippet.setTextColor(Color.BLACK);
                    snippet.setText(marker.getSnippet());
                    return v;
                }
            });
            googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    if (marker.getSnippet().contains(context.getString(R.string.newlocation))) {
                        int position = getIndex(spinner, marker.getTitle().split("\n")[0]) + 1;
                        spinner.setSelection(position);
                    }
                }

                private int getIndex(MaterialSpinner spinner, String s) {
                    int index = 0;
                    try {
                        for (int i = 0; i < spinner.getAdapter().getCount() - 1; i++) {
                            if (((UserInfo) spinner.getItemAtPosition(i)).fullname.equalsIgnoreCase(s)) {
                                index = i;
                                break;
                            }
                        }
                    } catch (Exception e) {
                        Log.e("getIndex", e.toString());
                        return index;
                    }
                    return index;
                }
            });
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(17, 107), 5.5f);
            googleMap.animateCamera(cameraUpdate);
            spinner.setEnabled(true);
            lv.setLoading(false);
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... params) {
            for (int i = 0; i < infoArrayList.size(); i++) {
                if (infoArrayList.get(i).locationDate > 0) {
                    String address = infoArrayList.get(i).address;
                    if (address == null || address.isEmpty()) {
                        address = MyMethod.getAddress(infoArrayList.get(i).latitude, infoArrayList.get(i).longitude, context);
                    }

                    myMarkers.add(new MarkerOptions()
                            .position(new LatLng(infoArrayList.get(i).latitude, infoArrayList.get(i).longitude))
                            .title(infoArrayList.get(i).fullname + "\n" + address + "\n" + Utils.long2String(infoArrayList.get(i).trackingDate))
                            .snippet(context.getString(R.string.newlocation))
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_dms_btn)));
                    tempId.put(myMarkers.size() - 1, infoArrayList.get(i).id_employee);
                    myAccuracy.add(infoArrayList.get(i).accuracy);
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            spinner.setEnabled(false);
        }
    }

    private class LoadingAllNotLoading extends AsyncTask<Void, Void, Void> {
        GoogleMap googleMap;
        Context context;
        ArrayList<UserInfo> infoArrayList = new ArrayList<>();
        MaterialSpinner spinner;
        ArrayList<MarkerOptions> myMarkers = new ArrayList<>();
        ArrayList<Float> myAccuracy = new ArrayList<>();
        HashMap<Integer, Integer> tempId = new HashMap<>();
        boolean isMoveMap;

        public LoadingAllNotLoading(GoogleMap googleMap, Context context, ArrayList<UserInfo> infoArrayList, MaterialSpinner spinner, boolean isMoveMap) {
            this.googleMap = googleMap;
            this.context = context;
            this.infoArrayList = infoArrayList;
            this.spinner = spinner;
            this.isMoveMap = isMoveMap;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (googleMap != null) googleMap.clear();
            for (int i = 0; i < myMarkers.size(); i++) {
                Marker marker = googleMap.addMarker(myMarkers.get(i));
                hashUserMarker.put(tempId.get(i), marker);
                hashMarker.put(marker, myAccuracy.get(i));
            }
            googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                @Override
                public View getInfoWindow(Marker arg0) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    View v = LayoutInflater.from(context).inflate(R.layout.custom_info_window, null);
                    TextView title = (TextView) v.findViewById(R.id.title);
                    title.setTextColor(Color.BLACK);
                    title.setText(marker.getTitle());
                    TextView snippet = (TextView) v.findViewById(R.id.snippet);
                    snippet.setTextColor(Color.BLACK);
                    snippet.setText(marker.getSnippet());
                    return v;
                }
            });
            googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    if (marker.getSnippet().contains(context.getString(R.string.newlocation))) {
                        int position = getIndex(spinner, marker.getTitle().split("\n")[0]) + 1;
                        spinner.setSelection(position);
                    }
                }

                private int getIndex(MaterialSpinner spinner, String s) {
                    int index = 0;
                    try {
                        for (int i = 0; i < spinner.getAdapter().getCount() - 1; i++) {
                            if (((UserInfo) spinner.getItemAtPosition(i)).fullname.equalsIgnoreCase(s)) {
                                index = i;
                                break;
                            }
                        }
                    } catch (Exception e) {
                        Log.e("getIndex", e.toString());
                        return index;
                    }
                    return index;
                }
            });
            if (isMoveMap) {
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(17, 107), 5.5f);
                googleMap.animateCamera(cameraUpdate);
            }

            spinner.setEnabled(true);
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... params) {
            for (int i = 0; i < infoArrayList.size(); i++) {
                if (infoArrayList.get(i).locationDate > 0) {
                    String address = infoArrayList.get(i).address;
                    if (address == null || address.isEmpty()) {
                        address = MyMethod.getAddress(infoArrayList.get(i).latitude, infoArrayList.get(i).longitude, context);
                    }
                    myMarkers.add(new MarkerOptions()
                            .position(new LatLng(infoArrayList.get(i).latitude, infoArrayList.get(i).longitude))
                            .title(infoArrayList.get(i).fullname + "\n" + address + "\n" + Utils.long2String(infoArrayList.get(i).trackingDate))
                            .snippet(context.getString(R.string.newlocation))
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_dms_btn)));
                    tempId.put(myMarkers.size() - 1, infoArrayList.get(i).id_employee);
                    myAccuracy.add(infoArrayList.get(i).accuracy);
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            spinner.setEnabled(false);
        }
    }

    //Hứng kết quả trả về từ control
    private void processEvent(EventType.EventBase event) {
        switch (event.type) {
            case GetUsers:
                EventType.EventGetUsersResult eventGetUsersResult = (EventType.EventGetUsersResult) event;
                if (binding.content.spStaff.getSelectedItemPosition() <= 0) {
                    onItemSelected(binding.content.spStaff, null, -1, 0);
                } else {
                    binding.content.spStaff.setSelection(0);
                }
                arrStaff.clear();
                if (eventGetUsersResult.arrayUsers == null || eventGetUsersResult.arrayUsers.length <= 0) {
                    MyMethod.showToast(binding,this, this.getString(R.string.none_staff));
                } else {
                    Collections.addAll(arrStaff, eventGetUsersResult.arrayUsers);
                    if (arrStaff.size() == 1) {
                        onItemSelected(binding.content.spStaff, null, 0, 0);
                        binding.content.spStaff.setSelection(1);
                        binding.content.spStaff.setVisibility(View.GONE);
                    } else {
                        onItemSelected(binding.content.spStaff, null, -1, 0);
                    }

                }
                //LayoutLoadingManager.Show_OffLoading(binding.content.ManagerLoadingView);
                if (!MyMethod.isUpdateLocation) {// Nếu không phải đang realtime
                    adapterStaff.notifyDataSetChanged();
                }

                break;
            case RealTimeTracking:
                EventType.EventRealTimeTrackingResult realTimeTrackingResult = (EventType.EventRealTimeTrackingResult) event;
                if (realTimeTrackingResult != null && realTimeTrackingResult.success) {
                    MyMethod.isRealTime = true;
                } else {
                    MyMethod.isRealTime = false;
                    MyMethod.showToast(binding,this, realTimeTrackingResult.message);
                }
                break;
            case GetLastLocation:
                EventType.EventGetLastLocationResult getLastLocationResult = (EventType.EventGetLastLocationResult) event;
                if (getLastLocationResult != null && getLastLocationResult.success) {
                    UserInfo user = getLastLocationResult.user;
                    Marker temp = hashUserMarker.get(user.id_employee);
                    temp.setTitle(user.fullname + "\n" + user.address + "\n" + Utils.long2String(user.trackingDate));
                    temp.setSnippet(Utils.long2OverTime(user.locationDate) + "- sai số: " + (int) user.accuracy + "m");
                    MyMethod.animateMarker(temp, user, false, googleMap);
                    countRealTime++;
                    if (countRealTime == 3) {//Nếu lấy 3 lần vị trí thì ngưng realtime
                        if (userNeedTracking) {//Nếu người dùng chọn tracking live
                            MyMethod.isRealTime = true;
                        } else {
                            MyMethod.isRealTime = false;
                        }
                        countRealTime = 0;


                    }
                } else {
                    MyMethod.showToast(binding,this, getLastLocationResult.message);
                }
                LayoutLoadingManager.Show_OffLoading(binding.content.ManagerLoadingView);
                break;
            case GetLocations:
                EventType.EventGetLocationsResult arrLocations = (EventType.EventGetLocationsResult) event;
                if (arrLocations.arrayLocations != null && arrLocations.arrayLocations.length > 0) {
                    if (googleMap != null) googleMap.clear();
                    drawMap(binding.content.ManagerLoadingView, this, mapFragment, arrLocations.arrayLocations, arrStaff, binding.content.spStaff);
                    arrDetailTracking.clear();
                    for (int i = arrLocations.arrayLocations.length - 1; i >= 0; i--)
                        arrDetailTracking.add(arrLocations.arrayLocations[i]);
                    //Quãng đường di chuyển
                    int totalMeter = 0;
                    for (int i = arrLocations.arrayLocations.length - 1; i >= 0; i--) {
                        MyLocation temp = arrLocations.arrayLocations[i];
                        totalMeter = totalMeter + temp.distanceMeter;
                        arrDetailTracking.add(temp);
                    }
                    adapterTracking.notifyDataSetChanged();
                    binding.content.txtTrackingMeter.setText(this.getString(R.string.distanceMeter) + " " + Utils.formatMeter(totalMeter));
                    binding.content.linearListTracking.setVisibility(View.VISIBLE);
                } else {
                    binding.content.linearListTracking.setVisibility(View.GONE);
                    MyMethod.showToast(binding,this, arrLocations.message);
                }
                LayoutLoadingManager.Show_OffLoading(binding.content.ManagerLoadingView);

                break;

            default:
                break;
        }
    }

}
