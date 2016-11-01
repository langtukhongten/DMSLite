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
import com.vietdms.mobile.dmslauncher.Home;
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
    private static final int RESULT_SELECT_CUSTOMER = 1;
    private ActivitySendTransactionBinding binding;
    private static final String TAG = "SendTransaction";
    private Handler handler;
    private ArrayAdapter<String> adapterStaff;
    private AutoCompleteAdapter adapterCustomer;
    private ArrayList<String> arrayStaff;
    private ArrayList<Status> arrayCustomer;
    private ArrayList<Integer> arrayStaffSerial;
    private int nowIdStaff = 0, nowIdCustomer = 0;
    private String nowNameCustomer = "";
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
                    MyMethod.showToast(binding
                            , this, this.getString(R.string.none_staff));
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
                LayoutLoadingManager.Show_OffLoading(binding.LoadingEmployee);
                break;
            case SendTransactionMessage:
                EventType.EventSendTransactionMessageResult eventSendTransactionMessageResult = (EventType.EventSendTransactionMessageResult) event;
                MyMethod.showToast(Home.bindingRight, this, eventSendTransactionMessageResult.message);
                LayoutLoadingManager.Show_OffLoading(binding.SendTransactionLoading);
                finish();
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
        Log.w(TAG, "registerReceiver");
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
        binding.spStaff.setOnItemSelectedListener(this);
        binding.btnSendTransaction.setOnClickListener(this);
        binding.btnSelectCustomer.setOnClickListener(this);
        binding.imgRefreshEmployee.setOnClickListener(this);
    }


    private void getData() {
        Log.w(TAG, "getData");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                clearAdapterStaff();
                LayoutLoadingManager.Show_OnLoading(binding.LoadingEmployee, getString(R.string.load_employee), 30);
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
                if (isDataOK()) {
                    sendTransaction(nowIdStaff, nowIdCustomer, binding.editTransactionContent.getText().toString(), binding.editTransactionNote.getText().toString(), binding.editTransactionPhone.getText().toString());

                }
                break;
            case R.id.btn_select_customer:
                Intent selectCustomer = new Intent(this, SelectCustomer.class);
                startActivityForResult(selectCustomer, RESULT_SELECT_CUSTOMER);
                break;
            case R.id.img_refresh_employee:
                LayoutLoadingManager.Show_OnLoading(binding.LoadingEmployee, getString(R.string.load_employee), 30);
                EventPool.control().enQueue(new EventType.EventGetUsersRequest());
                break;
            default:
                break;
        }
    }

    private boolean isDataOK() {
        if (binding.editTransactionNote.getText().toString().isEmpty()) {
            MyMethod.showToast(binding, this, getString(R.string.require_please_input));
            binding.editTransactionNote.requestFocus();
            return false;
        } else if (binding.editTransactionPhone.getText().toString().isEmpty()) {
            MyMethod.showToast(binding, this, getString(R.string.require_please_input));
            binding.editTransactionPhone.requestFocus();
            return false;
        } else if (binding.editTransactionContent.getText().toString().isEmpty()) {
            MyMethod.showToast(binding, this, getString(R.string.require_please_input));
            binding.editTransactionContent.requestFocus();
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_SELECT_CUSTOMER) {
            if (resultCode == RESULT_OK) {
                nowIdCustomer = data.getIntExtra("Customer", 0);
                nowNameCustomer = data.getStringExtra("Name");
                binding.btnSelectCustomer.setText(nowNameCustomer);
                try {
                    LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("OnFire"));
                    Log.w(TAG, "registerReceiver");
                } catch (Exception e) {
                    Log.w(TAG, e.toString());
                }
            }
        }
    }

    private synchronized void sendTransaction(int nowIdStaff, int nowIdCustomer, String content, String note, String phone) {
        LayoutLoadingManager.Show_OnLoading(binding.SendTransactionLoading, getString(R.string.sending), 30);
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
        Toast.makeText(this, ((Status) adapterView.getAdapter().getItem(position)).name, Toast.LENGTH_SHORT).show();
    }
}
