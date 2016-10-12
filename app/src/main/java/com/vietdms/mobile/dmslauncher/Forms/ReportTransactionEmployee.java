package com.vietdms.mobile.dmslauncher.Forms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.vietdms.mobile.dmslauncher.CustomClass.LayoutLoadingManager;
import com.vietdms.mobile.dmslauncher.Home;
import com.vietdms.mobile.dmslauncher.R;
import com.vietdms.mobile.dmslauncher.databinding.ActivityReportRouteBinding;
import com.vietdms.mobile.dmslauncher.databinding.ActivityReportTransactionEmployeeBinding;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import CommonLib.Const;

public class ReportTransactionEmployee extends AppCompatActivity  implements View.OnClickListener, CalendarDatePickerDialogFragment.OnDateSetListener {
    private static final java.lang.String FRAG_TAG_DATE_PICKER = "Date_Picker";
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "On Data Fired", Toast.LENGTH_SHORT).show();
        }
    };
    private String url = "";
    private long toDateReportTransactionEmployee,fromDateReportTransactionEmployee;
    private Date toReportTransactionEmployee,fromReportTransactionEmployee;
    Context context;
    ActivityReportTransactionEmployeeBinding binding;
    private String id_employee="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this.getApplicationContext();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_report_transaction_employee);
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
        LayoutLoadingManager.Show_OnLoading(binding.ReportTransactionEmployeeLoadingView, context.getString(R.string.load_report_web), 30);
        url = Const.HttpReportTransactionEmployee + "177" + Const.DateFromParameter + fromDateReportTransactionEmployee + Const.DateToParameter + toDateReportTransactionEmployee;

        binding.webviewTransactionEmployee.clearCache(true);
        binding.webviewTransactionEmployee.getSettings().setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT >= 19) {
            binding.webviewTransactionEmployee.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            binding.webviewTransactionEmployee.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        binding.webviewTransactionEmployee.loadUrl(url);
        binding.webviewTransactionEmployee.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d("Webview", "Finished");
                LayoutLoadingManager.Show_OffLoading(binding.ReportTransactionEmployeeLoadingView);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                Log.d("Webview", "error: " + error.toString());
            }
        });
        binding.reportTransactionEmployeeLoad.setOnClickListener(this);
        binding.btnSelectDayReportTransactionEmployee.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.report_transaction_employee_load:
                url = Const.HttpReportTransactionEmployee + "177" + Const.DateFromParameter + fromDateReportTransactionEmployee + Const.DateToParameter + toDateReportTransactionEmployee;
                binding.webviewTransactionEmployee.loadUrl(url);
                LayoutLoadingManager.Show_OnLoading(binding.ReportTransactionEmployeeLoadingView, context.getString(R.string.load_report_web), 30);
                binding.webviewTransactionEmployee.setWebViewClient(new WebViewClient() {
                    public void onPageFinished(WebView view, String url) {
                        // do your stuff here
                        LayoutLoadingManager.Show_OffLoading(binding.ReportTransactionEmployeeLoadingView);
                    }
                });
                break;
            case R.id.btn_select_day_report_transaction_employee:
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
        toReportTransactionEmployee = new Date(year - 1900, monthOfYear, dayOfMonth);
        toDateReportTransactionEmployee = toReportTransactionEmployee.getTime();
        binding.btnSelectDayReportTransactionEmployee.setText(formatDate.format(toDateReportTransactionEmployee));
    }
}

