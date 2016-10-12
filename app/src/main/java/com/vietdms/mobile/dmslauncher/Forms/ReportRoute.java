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
import android.widget.Toast;

import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.vietdms.mobile.dmslauncher.CustomAdapter.ArrayUserAdapter;
import com.vietdms.mobile.dmslauncher.CustomClass.LayoutLoadingManager;
import com.vietdms.mobile.dmslauncher.Home;
import com.vietdms.mobile.dmslauncher.MyMethod;
import com.vietdms.mobile.dmslauncher.R;
import com.vietdms.mobile.dmslauncher.databinding.ActivityReportRouteBinding;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import CommonLib.Const;
import CommonLib.EventPool;
import CommonLib.EventType;
import CommonLib.Model;
import CommonLib.UserInfo;


public class ReportRoute extends AppCompatActivity implements View.OnClickListener, CalendarDatePickerDialogFragment.OnDateSetListener, AdapterView.OnItemSelectedListener {
    private static final java.lang.String FRAG_TAG_DATE_PICKER = "Date_Picker";
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
    private long toDateReportRoute;
    private Date toReportRoute;
    Context context;
    ActivityReportRouteBinding binding;
    private String id_employee = "";
    private ArrayUserAdapter adapterStaff;
    private ArrayList<UserInfo> arrayStaff;
    private Handler handler;

    private void processEvent(EventType.EventBase event) {
        switch (event.type) {
            case GetUsers:
                EventType.EventGetUsersResult eventGetUsersResult = (EventType.EventGetUsersResult) event;
                arrayStaff.clear();
                if (eventGetUsersResult.arrayUsers == null || eventGetUsersResult.arrayUsers.length <= 0) {
                    MyMethod.showToast(context, context.getString(R.string.none_staff));
                } else {
                    Collections.addAll(arrayStaff, eventGetUsersResult.arrayUsers);

                }
                adapterStaff.notifyDataSetChanged();

                url = Const.HttpReportRoute + arrayStaff.get(0).id_employee + Const.DateParameter + toDateReportRoute;
                binding.webviewRoute.clearCache(true);
                binding.webviewRoute.getSettings().setJavaScriptEnabled(true);
                if (Build.VERSION.SDK_INT >= 19) {
                    binding.webviewRoute.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                } else {
                    binding.webviewRoute.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                }
                binding.webviewRoute.loadUrl(url);
                binding.webviewRoute.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        super.onPageStarted(view, url, favicon);
                        LayoutLoadingManager.Show_OnLoading(binding.ReportRouteLoadingView, context.getString(R.string.load_report_web), 30);
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        Log.d("Webview", "Finished");
                        LayoutLoadingManager.Show_OffLoading(binding.ReportRouteLoadingView);
                    }

                    @Override
                    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                        super.onReceivedError(view, request, error);
                        Log.d("Webview", "error: " + error.toString());
                    }
                });

                break;
            default:
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this.getApplicationContext();
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("OnFire"));
        handler = new Handler();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_report_route);
        event();

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
        arrayStaff = new ArrayList<>();
        adapterStaff = new ArrayUserAdapter(this, R.layout.custom_adapterstaff, arrayStaff);
        binding.spStaffReport.setAdapter(adapterStaff);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                EventPool.control().enQueue(new EventType.EventGetUsersRequest());
            }
        }, 1000);


        binding.reportRouteLoad.setOnClickListener(this);
        binding.btnSelectDayReportRoute.setOnClickListener(this);
        binding.spStaffReport.setOnItemSelectedListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.report_route_load:
                url = Const.HttpReportRoute + id_employee + Const.DateParameter + toDateReportRoute;
                binding.webviewRoute.loadUrl(url);
                LayoutLoadingManager.Show_OnLoading(binding.ReportRouteLoadingView, context.getString(R.string.load_report_web), 100);
                binding.webviewRoute.setWebViewClient(new WebViewClient() {
                    public void onPageFinished(WebView view, String url) {
                        // do your stuff here
                        LayoutLoadingManager.Show_OffLoading(binding.ReportRouteLoadingView);
                    }

                    @Override
                    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                        super.onReceivedError(view, request, error);
                        Log.d("Webview", "error: " + error.toString());
                    }
                });
                break;
            case R.id.btn_select_day_report_route:
                CalendarDatePickerDialogFragment fromDate = new CalendarDatePickerDialogFragment()
                        .setOnDateSetListener(this)
                        .setFirstDayOfWeek(Calendar.SUNDAY)
                        .setThemeDark(false);
                fromDate.show(getSupportFragmentManager(), FRAG_TAG_DATE_PICKER);
                break;
            default:
                break;
        }
    }

    @Override
    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
        DateFormat formatDate = new SimpleDateFormat("dd/MM/yy");
        toReportRoute = new Date(year - 1900, monthOfYear, dayOfMonth);
        toDateReportRoute = toReportRoute.getTime();
        binding.btnSelectDayReportRoute.setText(formatDate.format(toDateReportRoute));
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        switch (adapterView.getId()) {
            case R.id.spStaff_Report_:
                if (position == -1) {
                    if (arrayStaff.size() > 0) {
                        id_employee = arrayStaff.get(0).id_employee + "";
                    }

                } else {
                    id_employee = arrayStaff.get(position).id_employee + "";
                }

                binding.reportRouteLoad.setSoundEffectsEnabled(false);
                binding.reportRouteLoad.performClick();
                break;
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
