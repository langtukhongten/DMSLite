package com.vietdms.mobile.dmslauncher.Forms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;

import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.vietdms.mobile.dmslauncher.CustomAdapter.IdNameAdapter;
import com.vietdms.mobile.dmslauncher.CustomAdapter.RootIdNameAdapter;
import com.vietdms.mobile.dmslauncher.CustomClass.LayoutLoadingManager;
import com.vietdms.mobile.dmslauncher.Home;
import com.vietdms.mobile.dmslauncher.MyMethod;
import com.vietdms.mobile.dmslauncher.R;
import com.vietdms.mobile.dmslauncher.databinding.ActivityReportTransactionGeneralBinding;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import CommonLib.Const;
import CommonLib.EventPool;
import CommonLib.EventType;
import CommonLib.IdStatus;
import CommonLib.Status;

public class ReportTransactionGeneral extends AppCompatActivity  implements View.OnClickListener, CalendarDatePickerDialogFragment.OnDateSetListener, AdapterView.OnItemSelectedListener {
    private static final java.lang.String FROM_DATE_TAG = "FromDate";
    private static final java.lang.String TO_DATE_TAG = "ToDate";
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
    private String url = "";
    private long toDateReportTransactionGeneral, fromDateReportTransactionGeneral;
    private Date toReportTransactionGeneral, fromReportTransactionGeneral;
    Context context;
    ActivityReportTransactionGeneralBinding binding;
    private String branch = "-1",group ="-1";
    private android.os.Handler handler;
    private boolean loadingFinished = true;
    private boolean redirect = false;
    private ArrayList<Status> arrBranch;
    private ArrayList<IdStatus> arrGroup;
    private IdNameAdapter adapterBranch;
    private RootIdNameAdapter adapterGroup;
    private void processEvent(EventType.EventBase event) {
        switch (event.type) {
            case BranchGroup:
                EventType.EventBranchGroupResult eventBranchGroupResult = (EventType.EventBranchGroupResult) event;
                if(eventBranchGroupResult.success){
                    arrBranch.clear();
                    arrGroup.clear();
                    arrBranch.addAll(eventBranchGroupResult.listBranch);
                    arrGroup.addAll(eventBranchGroupResult.listGroup);
                    adapterBranch.notifyDataSetChanged();
                }else{
                    MyMethod.showToast(context,getString(R.string.none_branch_group));
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this.getApplicationContext();
        handler = new Handler();
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("OnFire"));
        binding = DataBindingUtil.setContentView(this, R.layout.activity_report_transaction_general);
        event();

    }

    @Override
    protected void onResume() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Home.BROADCAST_ACTION_VIEW);
        registerReceiver(receiver, filter);
        super.onResume();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(receiver);
        super.onPause();
    }

    private void event() {
        arrBranch = new ArrayList<>();
        arrGroup = new ArrayList<>();
        adapterBranch = new IdNameAdapter(context,arrBranch);
        adapterGroup = new RootIdNameAdapter(context,arrGroup);
        binding.spBranchGeneral.setAdapter(adapterBranch);
        binding.spGroupGeneral.setAdapter(adapterGroup);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                EventPool.control().enQueue(new EventType.EventBranchGroupRequest());
            }
        }, 1000);


        binding.reportTransactionGeneralLoad.setOnClickListener(this);
        binding.btnSelectFromDayReportTransactionGeneral.setOnClickListener(this);
        binding.btnSelectToDayReportTransactionGeneral.setOnClickListener(this);
        binding.spBranchGeneral.setOnItemSelectedListener(this);
        binding.spGroupGeneral.setOnItemSelectedListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.report_transaction_employee_load:
                url = Const.HttpReportTransactionGeneral + branch + Const.GroupParameter+group+ Const.DateFromParameter + fromDateReportTransactionGeneral + Const.DateToParameter + toDateReportTransactionGeneral;
                binding.webviewTransactionGeneral.clearCache(true);
                binding.webviewTransactionGeneral.getSettings().setJavaScriptEnabled(true);
                if (Build.VERSION.SDK_INT >= 19) {
                    binding.webviewTransactionGeneral.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                } else {
                    binding.webviewTransactionGeneral.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                }
                binding.webviewTransactionGeneral.loadUrl(url);
                binding.webviewTransactionGeneral.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        if (!loadingFinished) {
                            redirect = true;
                        }

                        loadingFinished = false;
                        view.loadUrl(url);
                        return true;
                    }

                    public void onPageFinished(WebView view, String url) {
                        // do your stuff here
                        if(!redirect){
                            loadingFinished = true;
                        }

                        if(loadingFinished && !redirect){
                            //HIDE LOADING IT HAS FINISHED
                            LayoutLoadingManager.Show_OffLoading(binding.ReportTransactionGeneraloadingView);
                        } else{
                            redirect = false;
                        }

                    }

                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        loadingFinished = false;
                        //SHOW LOADING IF IT ISNT ALREADY VISIBLE
                        LayoutLoadingManager.Show_OnLoading(binding.ReportTransactionGeneraloadingView, context.getString(R.string.load_report_web), 100);
                    }

                    @Override
                    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                        Log.d("Webview", "error: " + error.toString());
                        LayoutLoadingManager.Show_OffLoading(binding.ReportTransactionGeneraloadingView);
                    }


                });
                break;
            case R.id.btn_select_from_day_report_transaction_general:
                CalendarDatePickerDialogFragment fromDate = new CalendarDatePickerDialogFragment()
                        .setOnDateSetListener(this)
                        .setFirstDayOfWeek(Calendar.SUNDAY)
                        .setThemeDark(false);
                fromDate.show(getSupportFragmentManager(), FROM_DATE_TAG);

                break;
            case R.id.btn_select_to_day_report_transaction_general:
                CalendarDatePickerDialogFragment toDate = new CalendarDatePickerDialogFragment()
                        .setOnDateSetListener(this)
                        .setFirstDayOfWeek(Calendar.SUNDAY)
                        .setThemeDark(false);
                toDate.show(getSupportFragmentManager(), TO_DATE_TAG);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
        switch (dialog.getTag()) {
            case FROM_DATE_TAG:
                DateFormat formatFromDate = new SimpleDateFormat("dd/MM/yy");
                fromReportTransactionGeneral = new Date(year - 1900, monthOfYear, dayOfMonth);
                fromDateReportTransactionGeneral = fromReportTransactionGeneral.getTime();
                binding.btnSelectFromDayReportTransactionGeneral.setText(formatFromDate.format(fromDateReportTransactionGeneral));
                break;
            case TO_DATE_TAG:
                DateFormat formatToDate = new SimpleDateFormat("dd/MM/yy");
                toReportTransactionGeneral = new Date(year - 1900, monthOfYear, dayOfMonth);
                toDateReportTransactionGeneral = toReportTransactionGeneral.getTime();
                binding.btnSelectToDayReportTransactionGeneral.setText(formatToDate.format(toDateReportTransactionGeneral));
                break;
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        switch (adapterView.getId()) {
            case R.id.sp_branch_general:
                if (arrBranch.size() > 0) {
                    if (position == -1)
                        branch = arrBranch.get(0).id + "";
                    else
                        branch = arrBranch.get(position).id + "";
                    adapterGroup.updateByRoot(Integer.parseInt(branch));
                } else {
                    branch = "-1";
                }

                break;
            case R.id.sp_group_general:
                if (arrGroup.size() > 0) {
                    if (position == -1)
                        group = arrGroup.get(0).id + "";
                    else
                        group = arrGroup.get(position).id + "";
                    binding.reportTransactionGeneralLoad.setSoundEffectsEnabled(false);
                    binding.reportTransactionGeneralLoad.performClick();

                } else {
                    branch = "-1";
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
