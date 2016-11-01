package com.vietdms.mobile.dmslauncher.Forms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.vietdms.mobile.dmslauncher.CustomAdapter.CustomAdapterLibrary;
import com.vietdms.mobile.dmslauncher.CustomClass.LayoutLoadingManager;
import com.vietdms.mobile.dmslauncher.MyMethod;
import com.vietdms.mobile.dmslauncher.R;
import com.vietdms.mobile.dmslauncher.databinding.ActivityLibraryBinding;

import java.util.ArrayList;

import CommonLib.EventPool;
import CommonLib.EventType;
import CommonLib.LibraryGroup;
import CommonLib.UserInfo;

public class LibraryActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {
    private static final String TAG = "LibraryActivity";
    private ActivityLibraryBinding binding;
    private CustomAdapterLibrary adapter;
    private ArrayAdapter<String> adapterStaff;
    private ArrayList<LibraryGroup> arrayLib;
    private ArrayList<String> arrayStaff;
    private ArrayList<Integer> arrayStaffSerial;
    private Handler handler;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            EventType.EventBase event = EventPool.view().deQueue();
            try {
                while (event != null) {
                    processEvent(event);
                    event = EventPool.view().deQueue();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                //SystemLog.addLog(context, SystemLog.Type.Exception, ex.toString());
            }

        }
    };

    private void processEvent(EventType.EventBase event) {
        switch (event.type) {
            case LoadFileManagerGroups:
                EventType.EventLoadFileManagerGroupsResult fileManagerGroupsResult = (EventType.EventLoadFileManagerGroupsResult) event;
                if (fileManagerGroupsResult.success) {
                    arrayLib.addAll(fileManagerGroupsResult.arrLibraryGroups);
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(this, fileManagerGroupsResult.message, Toast.LENGTH_SHORT).show();
                }
                break;
            case GetUsers:
                EventType.EventGetUsersResult eventGetUsersResult = (EventType.EventGetUsersResult) event;
                if (eventGetUsersResult.arrayUsers == null || eventGetUsersResult.arrayUsers.length <= 0) {
                    MyMethod.showToast(binding,this, this.getString(R.string.none_staff));
                } else {
                    UserInfo[] temp = eventGetUsersResult.arrayUsers;
                    for (int i = 0; i < temp.length; i++) {
                        arrayStaff.add(temp[i].fullname);
                        arrayStaffSerial.add(temp[i].id_employee);
                    }
                }
                LayoutLoadingManager.Show_OffLoading(binding.LibraryLoadingView);
                adapterStaff.notifyDataSetChanged();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.w(TAG, "onCreate");
        binding = DataBindingUtil.setContentView(this, R.layout.activity_library);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("OnFire"));
        handler = new Handler();
        init();
        getData();
    }

    private void getData() {
        Log.w(TAG, "getData");

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                clearAdapterStaff();
                LayoutLoadingManager.Show_OnLoading(binding.LibraryLoadingView, LibraryActivity.this.getString(R.string.load_employee), 30);
                EventPool.control().enQueue(new EventType.EventGetUsersRequest());
            }
        }, 500);

//        binding.fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                clearAdapter();
//                EventPool.control().enQueue(new EventType.EventLoadFileManagerGroupsRequest(0));
//            }
//        });
    }

    private void clearAdapter() {
        arrayLib.clear();
        adapter.notifyDataSetChanged();
    }

    private void clearAdapterStaff() {
        arrayStaff.clear();
        arrayStaffSerial.clear();
        adapterStaff.notifyDataSetChanged();
    }

    private void init() {

        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationIcon(R.drawable.left_arrow_btn);
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        arrayLib = new ArrayList<>();
        adapter = new CustomAdapterLibrary(this, arrayLib);
        arrayStaff = new ArrayList<>();
        arrayStaffSerial = new ArrayList<>();
        adapterStaff = new ArrayAdapter<>
                (
                        this,
                        R.layout.custom_spinner_route,
                        arrayStaff
                );
        adapterStaff.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        binding.content.gridLibrary.setAdapter(adapter);
        binding.content.gridLibrary.setOnItemClickListener(this);
        binding.spStaffLibrary.setAdapter(adapterStaff);
        binding.spStaffLibrary.setOnItemSelectedListener(this);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        switch (adapterView.getId()) {
            case R.id.gridLibrary:
                //Mở thư mục
                //type = 0 : ảnh = 1: pdf = 2 :youtube
                LibraryGroup temp = adapter.getObjectClick(position);
                Toast.makeText(LibraryActivity.this, "Mở thư mục : " + temp.description, Toast.LENGTH_SHORT).show();
                Intent detail = new Intent(this, LibraryDetailActivity.class);
                detail.putExtra("ID", temp.id);
                startActivity(detail);
                break;
            default:
                break;

        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        switch (adapterView.getId()) {
            case R.id.spStaffLibrary:
                clearAdapter();
                EventPool.control().enQueue(new EventType.EventLoadFileManagerGroupsRequest(arrayStaffSerial.get(position)));
                break;
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
