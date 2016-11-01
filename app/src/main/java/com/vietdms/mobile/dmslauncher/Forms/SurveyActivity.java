package com.vietdms.mobile.dmslauncher.Forms;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.vietdms.mobile.dmslauncher.CustomAdapter.CustomAdapterCampaign;
import com.vietdms.mobile.dmslauncher.CustomClass.LayoutLoadingManager;
import com.vietdms.mobile.dmslauncher.CustomView.AnimDownloadProgressButton;
import com.vietdms.mobile.dmslauncher.Home;
import com.vietdms.mobile.dmslauncher.MyMethod;
import com.vietdms.mobile.dmslauncher.R;
import com.vietdms.mobile.dmslauncher.databinding.ActivitySurveyBinding;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import CommonLib.Customer;
import CommonLib.EventPool;
import CommonLib.EventType;
import CommonLib.LocalDB;
import CommonLib.Model;
import CommonLib.PhoneState;
import CommonLib.SurveyCampaign;
import CommonLib.SurveyHeader;
import CommonLib.SurveyLine;
import CommonLib.SurveyResult;
import CommonLib.TransactionLine;

public class SurveyActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {
    private static final String TAG = "SurveyActivity";
    private static final int REQUESTSEND = 100;
    private ActivitySurveyBinding binding;
    private CustomAdapterCampaign adapter;
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
    private ArrayList<SurveyCampaign> arrSurveyCampaigns;
    private ArrayList<SurveyHeader> arrSurveyHeaders;
    private ArrayList<SurveyLine> arrSurveyLines;
    private SurveyCampaign selectedSurvey;
    private int ID_Customer = 0;
    private int Root_Customer = 0;
    private int lastRowIdSyncSurvey;
    private List<Integer> ids;

    private void processEvent(EventType.EventBase event) {
        switch (event.type) {
            case SyncSurveyDetail:
                EventType.EventSyncSurveyDetailResult syncSurveyDetailResult = (EventType.EventSyncSurveyDetailResult) event;
                if (syncSurveyDetailResult != null && syncSurveyDetailResult.success) {
                    int type = syncSurveyDetailResult.type;
                    int lastId = syncSurveyDetailResult.lastId;
                    int countData = syncSurveyDetailResult.countData;// Tổng số dòng cần load

                    ArrayList<Object> arrData = syncSurveyDetailResult.arrData;
                    switch (type) {
                        case 0://Header
                            for (int i = 0; i < arrData.size(); i++) {
                                SurveyHeader header = (SurveyHeader) arrData.get(i);
                                LocalDB.inst().addHeader(header);
                                //Home.bindingRight.setting.btnSyncData.setProgressText("Khách hàng", LocalDB.inst().countCustomer(-1) * 100 / countData);
                            }
                            lastRowIdSyncSurvey = ((SurveyHeader) arrData.get(arrData.size() - 1)).id;
                            if (lastRowIdSyncSurvey > lastId) {//Nếu chưa hết thì mần tiếp
                                EventPool.control().enQueue(new EventType.EventSyncSurveyDetailRequest(0, lastRowIdSyncSurvey, ids));
                            } else {//hết rồi thì load line
                                ids = MyMethod.getListID(1, arrData);
                                EventPool.control().enQueue(new EventType.EventSyncSurveyDetailRequest(1, -1, ids));
                            }
                            break;
                        case 1://Line
                            for (int i = 0; i < arrData.size(); i++) {
                                SurveyLine line = (SurveyLine) arrData.get(i);
                                LocalDB.inst().addLine(line);
                                //Home.bindingRight.setting.btnSyncData.setProgressText("Khách hàng", LocalDB.inst().countCustomer(-1) * 100 / countData);
                            }
                            lastRowIdSyncSurvey = ((SurveyLine) arrData.get(arrData.size() - 1)).id;
                            if (lastRowIdSyncSurvey > lastId) {//Nếu chưa hết thì mần tiếp
                                EventPool.control().enQueue(new EventType.EventSyncSurveyDetailRequest(1, lastRowIdSyncSurvey, ids));
                            } else {//hết rồi thì load result
                                ids = MyMethod.getListID(2, arrData);
                                EventPool.control().enQueue(new EventType.EventSyncSurveyDetailRequest(2, -1, ids));
                            }
                            break;
                        case 2://Result
                            for (int i = 0; i < arrData.size(); i++) {
                                SurveyResult result = (SurveyResult) arrData.get(i);
                                LocalDB.inst().addResult(result, 0);
                                //Home.bindingRight.setting.btnSyncData.setProgressText("Khách hàng", LocalDB.inst().countCustomer(-1) * 100 / countData);
                            }
                            lastRowIdSyncSurvey = ((SurveyResult) arrData.get(arrData.size() - 1)).id;
                            if (lastRowIdSyncSurvey > lastId) {//Nếu chưa hết thì mần tiếp
                                EventPool.control().enQueue(new EventType.EventSyncSurveyDetailRequest(2, lastRowIdSyncSurvey, ids));
                            } else {//hết rồi thì xong
                                MyMethod.showToast(binding,this, getString(R.string.sync_data_finish));
                            }
                            break;
                        default:
                            break;
                    }


                } else {
                    if (syncSurveyDetailResult.type == 0) {//Nếu ko load được header thì load line
                        EventPool.control().enQueue(new EventType.EventSyncDataRequest(1, -1));
                    } else if (syncSurveyDetailResult.type == 1) {//Nếu ko load được line thì load result
                        EventPool.control().enQueue(new EventType.EventSyncDataRequest(2, -1));
                    } else {
                        MyMethod.showToast(binding,this, syncSurveyDetailResult.message);
                    }
                }
                LayoutLoadingManager.Show_OffLoading(binding.content.loading);
                break;
            case SyncSurvey:
                EventType.EventSyncSurveyResult syncSurveyResult = (EventType.EventSyncSurveyResult) event;
                if (syncSurveyResult != null && syncSurveyResult.success) {
                    int lastId = syncSurveyResult.lastId;
                    int countData = syncSurveyResult.countData;// Tổng số dòng cần load

                    ArrayList<Object> arrData = syncSurveyResult.arrData;
                    //Campaign
                    //Lưu vào SQLite
                    for (int i = 0; i < arrData.size(); i++) {
                        SurveyCampaign campaign = (SurveyCampaign) arrData.get(i);
                        LocalDB.inst().addCampaign(campaign);
                    }
                    lastRowIdSyncSurvey = ((SurveyCampaign) arrData.get(arrData.size() - 1)).id;
                    if (lastRowIdSyncSurvey > lastId) {//Nếu chưa hết thì mần tiếp
                        EventPool.control().enQueue(new EventType.EventSyncSurveyRequest(lastRowIdSyncSurvey));
                    } else {
                        //Hết rồi thì load detail
                        arrSurveyCampaigns.addAll(LocalDB.inst().getListCampaigns());
                        adapter.notifyDataSetChanged();
                        ids = MyMethod.getListID(0, arrData);
                        EventPool.control().enQueue(new EventType.EventSyncSurveyDetailRequest(0, -1, ids));
                    }


                } else {

                    MyMethod.showToast(binding,this, syncSurveyResult.message);

                }
                break;
            case LoadSurveyCampaign:
                EventType.EventLoadSurveyCampaignResult eventLoadSurveyCampaignResult = (EventType.EventLoadSurveyCampaignResult) event;
                if (eventLoadSurveyCampaignResult.success) {
                    arrSurveyCampaigns.addAll(eventLoadSurveyCampaignResult.arrSurveyCampaigns);
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(this, eventLoadSurveyCampaignResult.message, Toast.LENGTH_SHORT).show();
                }
                LayoutLoadingManager.Show_OffLoading(binding.content.loading);
                break;
            case LoadSurveyData:
                EventType.EventLoadSurveyDataResult eventLoadSurveyDataResult = (EventType.EventLoadSurveyDataResult) event;
                if (eventLoadSurveyDataResult.success) {
                    arrSurveyHeaders.addAll(eventLoadSurveyDataResult.arrSurveyHeaders);
                    arrSurveyLines.addAll(eventLoadSurveyDataResult.arrSurveyLines);
                    Intent intent = new Intent(this, SurveyQAActivity.class);
                    intent.putExtra("Campaign", selectedSurvey);
                    intent.putParcelableArrayListExtra("Headers", arrSurveyHeaders);
                    intent.putParcelableArrayListExtra("Lines", arrSurveyLines);
                    intent.putExtra("IDCustomer", ID_Customer);
                    intent.putExtra("RootIDCustomer", Root_Customer);
                    startActivityForResult(intent,REQUESTSEND);
                } else {
                    Toast.makeText(this, eventLoadSurveyDataResult.message, Toast.LENGTH_SHORT).show();
                }
                break;
            case SendSurveyData:
                EventType.EventSendSurveyDataResult eventSendSurveyDataResult = (EventType.EventSendSurveyDataResult) event;
                if (eventSendSurveyDataResult.success) {
                    MyMethod.showToast(binding,this, getString(R.string.send_survey_success));
                    LocalDB.inst().updateResult(1);


                } else {
                    MyMethod.showToast(binding,this, eventSendSurveyDataResult.message);
                }
                LayoutLoadingManager.Show_OffLoading(binding.content.loading);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==1){
            //Neu gui thanh cong thi finish ve instore
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_survey);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("OnFire"));
        handler = new Handler();
        getId();
        //getData();

    }

    private void getId() {
        setSupportActionBar(binding.toolbar);
        //instance object
        arrSurveyCampaigns = new ArrayList<>();
        arrSurveyHeaders = new ArrayList<>();
        arrSurveyLines = new ArrayList<>();
        adapter = new CustomAdapterCampaign(this, arrSurveyCampaigns);
        binding.content.gridSurvey.setAdapter(adapter);
        binding.content.gridSurvey.setOnItemClickListener(this);
        binding.btnSyncSurvey.setOnClickListener(this);
    }


    @Override
    protected void onResume() {
        Log.w(TAG, "onResume");
        super.onResume();
        try {
            ID_Customer = getIntent().getIntExtra("ID", -1);
            Root_Customer = getIntent().getIntExtra("RootID", -1);
            if (ID_Customer <= 0) {//Là khách hàng lẻ
                ID_Customer = getRandomCustomer();//Tạo mã ngẫu nhiên cho khách hàng
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("OnFire"));
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                arrSurveyCampaigns.clear();
                adapter.notifyDataSetChanged();
                //Load campaign
                arrSurveyCampaigns.clear();
                adapter.notifyDataSetChanged();
                if (LocalDB.inst().countSurvey() > 0) {
                    //Có dữ liệu thì load offline
                    Log.w(TAG, "Load Offline");
                    arrSurveyCampaigns.addAll(LocalDB.inst().getListCampaigns());
                    adapter.notifyDataSetChanged();

                } else {
                    Log.w(TAG, "Sync Survey");
                    //Xử lí gửi result rồi mới đồng bộ
                    if (LocalDB.inst().countResultUnSent() > 0) {
                        LayoutLoadingManager.Show_OnLoading(binding.content.loading, getString(R.string.sending), 30);
                        EventPool.control().enQueue(new EventType.EventSendSurveyDataRequest(LocalDB.inst().getResultUnsent(), Root_Customer));
                    }
                    LocalDB.inst().deleteSyncSurvey();
                    LayoutLoadingManager.Show_OnLoading(binding.content.loading, getApplicationContext().getString(R.string.load_survey), 30);
                    EventPool.control().enQueue(new EventType.EventSyncSurveyRequest(-1));
                }
            }
        }, 1000);
    }

    private int getRandomCustomer() {
//        String result = "";
//        final Random random = new Random();
//        final Set<Integer> intSet = new HashSet<>();
//        while (intSet.size() < 1) {
//            intSet.add(random.nextInt(9) + 1);
//        }
//        final int[] ints = new int[intSet.size()];
//        final Iterator<Integer> iter = intSet.iterator();
//        for (int i = 0; iter.hasNext(); ++i) {
//            result += "" + iter.next();
//        }
        return (int) Model.getServerTime();
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
            Log.w(TAG, "unregisterReceiver");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        switch (adapterView.getId()) {
            case R.id.gridSurvey:
                selectedSurvey = adapter.getObjectClick(position);
                arrSurveyHeaders.clear();
                arrSurveyLines.clear();
                arrSurveyHeaders.addAll(LocalDB.inst().loadSurveyHeader(selectedSurvey.id));
                List<Integer> idHeaders = MyMethod.getListIDHeader(arrSurveyHeaders);
                arrSurveyLines.addAll(LocalDB.inst().loadSurveyLine(idHeaders, ID_Customer));
                if (arrSurveyHeaders.size() != 0 && arrSurveyLines.size() != 0) {
                    Log.w(TAG, "Header size: " + arrSurveyHeaders.size());
                    Log.w(TAG, "Line size: " + arrSurveyLines.size());
                    Intent intent = new Intent(this, SurveyQAActivity.class);
                    intent.putExtra("Campaign", selectedSurvey);
                    intent.putParcelableArrayListExtra("Headers", arrSurveyHeaders);
                    intent.putParcelableArrayListExtra("Lines", arrSurveyLines);
                    intent.putExtra("IDCustomer", ID_Customer);
                    intent.putExtra("RootIDCustomer", Root_Customer);
                    startActivityForResult(intent,REQUESTSEND);
                } else {
                    MyMethod.showToast(binding,this, getString(R.string.data_survey_empty));
                }
                //EventPool.control().enQueue(new EventType.EventLoadSurveyDataRequest(ID_Customer, Root_Customer, selectedSurvey.id));
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_sync_survey:
                //Xử lí đồng bộ survey
                if (PhoneState.inst().isWifi() != 1 && PhoneState.inst().is3G() != 1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(this.getString(R.string.please_open_network))
                            .setCancelable(true)
                            .setPositiveButton(this.getString(R.string.wifi), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Home.isAppLockStop = true;
                                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                                    dialog.dismiss();
                                }
                            }).setNegativeButton(this.getString(R.string.threeG), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
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
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(getString(R.string.confirm_sync_survey))
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    //Gửi transaction
                                    //Xử lí gửi result
                                    List<Integer> idHeaders = MyMethod.getListIDHeader(arrSurveyHeaders);
                                    arrSurveyLines.addAll(LocalDB.inst().loadSurveyLine(idHeaders, ID_Customer));
                                    if (LocalDB.inst().countResultUnSent() > 0) {
                                        EventPool.control().enQueue(new EventType.EventSendSurveyDataRequest(LocalDB.inst().getResultUnsent(), Root_Customer));
                                    }
                                    arrSurveyCampaigns.clear();
                                    adapter.notifyDataSetChanged();

                                    //Xử lý gửi transaction customer
                                    ArrayList<TransactionLine> dataTransaction = LocalDB.inst().getTransactionUnsent();
                                    int countCustomerUnSent = LocalDB.inst().countCustomerUnSent();
                                    int countTransactionUnsent = dataTransaction.size();
                                    if (countTransactionUnsent > 0) {
                                        ArrayList<Object> arrData = new ArrayList<>(countTransactionUnsent);
                                        arrData.addAll(dataTransaction);
                                        EventPool.control().enQueue(new EventType.EventUpdateDataRequest(arrData, 0));
                                    } else if (countCustomerUnSent > 0) {
                                        ArrayList<Customer> dataCustomer = LocalDB.inst().getCustomerUnsent();
                                        ArrayList<Object> arrData = new ArrayList<>(countCustomerUnSent);
                                        arrData.addAll(dataCustomer);
                                        EventPool.control().enQueue(new EventType.EventUpdateDataRequest(arrData, 1));
                                    } else {
                                        MyMethod.showToast(binding,getApplicationContext() , getString(R.string.none_data_need_update));
                                        Home.bindingRight.setting.btnUpdateData.setState(AnimDownloadProgressButton.NORMAL);
                                    }

                                    //Xử lí load dữ
                                    LocalDB.inst().deleteSyncSurvey();
                                    EventPool.control().enQueue(new EventType.EventSyncSurveyRequest(-1));

//
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }

                break;
            default:
                break;
        }
    }
}
