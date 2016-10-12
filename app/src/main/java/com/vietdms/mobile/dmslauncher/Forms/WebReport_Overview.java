package com.vietdms.mobile.dmslauncher.Forms;

/**
 * Created by CuongPh on 17/07/2016.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.vietdms.mobile.dmslauncher.databinding.LayoutReportWebBinding;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import CommonLib.Const;
import CommonLib.Model;

/**
 * Created by CuongPh on 17/07/2016.
 */
public class WebReport_Overview extends AppCompatActivity implements View.OnClickListener, CalendarDatePickerDialogFragment.OnDateSetListener {
    private static final java.lang.String FRAG_TAG_DATE_PICKER = "Date_Picker";
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "On Data Fired", Toast.LENGTH_SHORT).show();
        }
    };
    private String url = "";
    private long toDateReportWeb;
    private Date toReportWeb;

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

    Context context;
    LayoutReportWebBinding binding;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this.getApplicationContext();
        setContentView(R.layout.layout_report_web);
        binding = DataBindingUtil.setContentView(this, R.layout.layout_report_web);
        event();


    }

    private void event() {
        LayoutLoadingManager.Show_OnLoading(binding.ReportWebLoadingView, context.getString(R.string.load_report_web), 30);
        url = Const.HttpReportEmployeeWork + Model.inst().getConfigValue(Const.ConfigKeys.ID_Device, 0);
        binding.webview.clearCache(true);
        binding.webview.getSettings().setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT >= 19) {
            binding.webview.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            binding.webview.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        binding.webview.loadUrl(url);
        binding.webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d("Webview", "Finished");
                LayoutLoadingManager.Show_OffLoading(binding.ReportWebLoadingView);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                Log.d("Webview", "error: " + error.toString());
            }
        });
        binding.reportWebLoad.setOnClickListener(this);
        binding.btnSelectDayReport.setOnClickListener(this);
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.report_web_load:
                url = Const.HttpReportEmployeeWork + Model.inst().getConfigValue(Const.ConfigKeys.ID_Device, 0) + Const.DateParameter + toDateReportWeb;
                binding.webview.loadUrl(url);
                LayoutLoadingManager.Show_OnLoading(binding.ReportWebLoadingView, context.getString(R.string.load_report_web), 30);
                binding.webview.setWebViewClient(new WebViewClient() {
                    public void onPageFinished(WebView view, String url) {
                        // do your stuff here
                        LayoutLoadingManager.Show_OffLoading(binding.ReportWebLoadingView);
                    }
                });
                break;
            case R.id.btn_select_day_report:
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
        toReportWeb = new Date(year - 1900, monthOfYear, dayOfMonth);
        toDateReportWeb = toReportWeb.getTime();
        binding.btnSelectDayReport.setText(formatDate.format(toDateReportWeb));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
