package com.vietdms.mobile.dmslauncher.Forms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.vietdms.mobile.dmslauncher.MyMethod;
import com.vietdms.mobile.dmslauncher.R;
import com.vietdms.mobile.dmslauncher.databinding.ActivityMapsBinding;

import java.util.concurrent.TimeUnit;

import CommonLib.EventPool;
import CommonLib.EventType;
import CommonLib.LocationDetector;
import CommonLib.Model;
import CommonLib.TrackingItem;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {

    private static final String TAG = "MapsActivity";
    private GoogleMap googleMap;
    private ActivityMapsBinding binding;
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
    private Location location;//vị trí
    private String address;//tên đường

    private void processEvent(EventType.EventBase event) {
        switch (event.type) {
            case HighPrecisionLocation:
                EventType.EventLoadHighPrecisionLocationResult locationResult = (EventType.EventLoadHighPrecisionLocationResult) event;
                if (locationResult.location != null) {
                    location = locationResult.location;
                    MyMethod.loadMap(googleMap, location, this, true);
                    binding.content.txtAccuracy.setText(this.getString(R.string.accuracy) + location.getAccuracy() + " m");
                    binding.fab.setEnabled(true);
                } else {
                    MyMethod.showToast(this, this.getString(R.string.location_none));
                }
                break;
            default:
                break;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("OnFire"));
        init();
        getData();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.w(TAG, "onStop");
        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
            //unregisterReceiver(receiver);
            Log.w(TAG, "unregisterReceiver");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getData() {

    }

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

        //event
        binding.fab.setOnClickListener(this);
        binding.fabCancel.setOnClickListener(this);
        binding.content.fabGetAgain.setOnClickListener(this);
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
        //Lấy vị trí
        TrackingItem lastTrackingItemSaved = Model.inst().getLastTrackingValidLocationSaved();

        if (lastTrackingItemSaved != null && TimeUnit.MILLISECONDS.toMinutes(Model.getServerTime() - lastTrackingItemSaved.locationDate) <= 2) {
            if (Model.inst().getLastLocation() != null) {
                location = Model.inst().getLastLocation();
                location.setAccuracy(lastTrackingItemSaved.accuracy);
                location.setLatitude(lastTrackingItemSaved.latitude);
                location.setLongitude(lastTrackingItemSaved.longitude);
                location.setTime(lastTrackingItemSaved.trackingDate);
                MyMethod.loadMap(googleMap, location, this, true);
                binding.content.txtAccuracy.setText(this.getString(R.string.accuracy) + location.getAccuracy() + " m");
                binding.fab.setEnabled(true);
            }

        } else {
            MyMethod.showToast(this, this.getString(R.string.location_wait));
            LocationDetector.inst().setHighPrecision(true);
        }
        // Request map update location
        LocationDetector.inst().startLocationUpdates();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                if (location != null) {
                    address = (MyMethod.getAddress(location.getLatitude(), location.getLongitude(), this));
                    //put Intent back

                    Intent intent = new Intent();
                    intent.putExtra("Address", address);
                    intent.putExtra("Location", location);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    MyMethod.refreshMap(this, googleMap);
                    MyMethod.showToast(this, this.getString(R.string.location_wait));
                    LocationDetector.inst().setHighPrecision(true);
                }

                break;
            case R.id.fabCancel:
                finish();
                break;
            case R.id.fabGetAgain:
                MyMethod.showToast(this, this.getString(R.string.location_wait));
                LocationDetector.inst().setHighPrecision(true);
                break;
            default:
                break;
        }
    }
}
