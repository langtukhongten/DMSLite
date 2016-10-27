package com.vietdms.mobile.dmslauncher.Forms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.bumptech.glide.util.Util;
import com.vietdms.mobile.dmslauncher.CustomAdapter.ArrayUserAdapter;
import com.vietdms.mobile.dmslauncher.CustomAdapter.AutoCompleteAdapter;
import com.vietdms.mobile.dmslauncher.CustomAdapter.IdNameAdapter;
import com.vietdms.mobile.dmslauncher.CustomClass.LayoutLoadingManager;
import com.vietdms.mobile.dmslauncher.MyMethod;
import com.vietdms.mobile.dmslauncher.R;
import com.vietdms.mobile.dmslauncher.databinding.ActivitySendTransactionBinding;

import java.util.ArrayList;

import CommonLib.Const;
import CommonLib.Customer;
import CommonLib.EventPool;
import CommonLib.EventType;
import CommonLib.LocalDB;
import CommonLib.Model;
import CommonLib.Status;
import CommonLib.UserInfo;
import CommonLib.Utils;

public class SendTransaction extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener, AdapterView.OnItemClickListener {
    private ActivitySendTransactionBinding binding;
    private static final String TAG = "SendTransaction";
    private Handler handler;
    private ArrayAdapter<String> adapterStaff;
    private AutoCompleteAdapter adapterCustomer;
    private ArrayList<String> arrayStaff;
    private ArrayList<Status> arrayCustomer;
    private ArrayList<Integer> arrayStaffSerial;
    private int nowIdStaff = 0, nowIdCustomer = 0;
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

    private void processEvent(EventType.EventBase event) {
        switch (event.type) {
            case GetUsers:
                EventType.EventGetUsersResult eventGetUsersResult = (EventType.EventGetUsersResult) event;
                if (eventGetUsersResult.arrayUsers == null || eventGetUsersResult.arrayUsers.length <= 0) {
                    MyMethod.showToast(this, this.getString(R.string.none_staff));
                } else {
                    UserInfo[] temp = eventGetUsersResult.arrayUsers;
                    arrayStaff.add("Tất cả");
                    arrayStaffSerial.add(0);
                    for (int i = 0; i < temp.length; i++) {
                        arrayStaff.add(temp[i].fullname);
                        arrayStaffSerial.add(temp[i].id_employee);
                    }
                }
                adapterStaff.notifyDataSetChanged();
                break;
            case LoadAllCustomers:
                EventType.EventLoadAllCustomersResult customersResult = (EventType.EventLoadAllCustomersResult) event;
                if (customersResult.success) {
                    arrayCustomer =customersResult.arrCustomer;


                } else {
                    MyMethod.showToast(this, customersResult.message);

                }
                adapterCustomer.setItems(arrayCustomer);
                adapterCustomer.notifyDataSetChanged();

                break;
            case SendTransactionMessage:
                EventType.EventSendTransactionMessageResult eventSendTransactionMessageResult = (EventType.EventSendTransactionMessageResult) event;
                MyMethod.showToast(this, eventSendTransactionMessageResult.message);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_send_transaction);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("OnFire"));
        handler = new Handler();
        init();
        getData();

    }

    private void init() {


        arrayStaff = new ArrayList<>();
        arrayStaffSerial = new ArrayList<>();
        arrayCustomer = new ArrayList<>();
        adapterStaff = new ArrayAdapter<>
                (
                        this,
                        R.layout.custom_spinner_route,
                        arrayStaff
                );

        adapterStaff.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        adapterCustomer = new AutoCompleteAdapter(this,
                android.R.layout.simple_spinner_dropdown_item, arrayCustomer);
        binding.spStaff.setAdapter(adapterStaff);
        binding.autoCustomer.setAdapter(adapterCustomer);
        binding.spStaff.setOnItemSelectedListener(this);
        binding.btnSendTransaction.setOnClickListener(this);
        binding.autoCustomer.setOnItemClickListener(this);
    }


    private void getData() {
        Log.w(TAG, "getData");

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                clearAdapterStaff();
                EventPool.control().enQueue(new EventType.EventLoadAllCustomersRequest());
                EventPool.control().enQueue(new EventType.EventGetUsersRequest());
            }
        }, 500);

    }

    private void clearAdapterStaff() {
        arrayStaff.clear();
        adapterStaff.notifyDataSetChanged();
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
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        switch (adapterView.getId()) {
            case R.id.spStaff:
                nowIdStaff = arrayStaffSerial.get(position);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_send_transaction:
                sendTransaction(nowIdStaff, nowIdCustomer, binding.editTransactionContent.getText().toString(), binding.editTransactionNote.getText().toString(), binding.editTransactionPhone.getText().toString());
                break;
            default:
                break;
        }
    }

    private synchronized void sendTransaction(int nowIdStaff, int nowIdCustomer, String content, String note, String phone) {
        if (nowIdStaff == 0) {
            //Xu li gui topic
            EventPool.control().enQueue(new EventType.EventSendTransactionMessageRequest(1, nowIdCustomer, nowIdStaff, content, note, phone));
        } else {
            //Xu li gui 1 nguoi
            EventPool.control().enQueue(new EventType.EventSendTransactionMessageRequest(0, nowIdCustomer, nowIdStaff, content, note, phone));

        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        Toast.makeText(this, ((Status)adapterView.getAdapter().getItem(position)).name, Toast.LENGTH_SHORT).show();
    }
}
