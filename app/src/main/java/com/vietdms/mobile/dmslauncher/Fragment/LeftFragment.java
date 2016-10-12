package com.vietdms.mobile.dmslauncher.Fragment;


import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Utils;
import com.vietdms.mobile.dmslauncher.CustomAdapter.ArrayUserAdapter;
import com.vietdms.mobile.dmslauncher.Home;
import com.vietdms.mobile.dmslauncher.R;
import com.vietdms.mobile.dmslauncher.RecycleView.RecyclerViewAdapterTimeLine;
import com.vietdms.mobile.dmslauncher.databinding.FragmentLeftBinding;

import java.util.ArrayList;

import CommonLib.BI_ReportCompare;
import CommonLib.EventPool;
import CommonLib.EventType;
import CommonLib.UserInfo;
import fr.ganfra.materialspinner.MaterialSpinner;

/**
 * Created by DMSv4 on 12/3/2015.
 */
public class LeftFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {


    private Context context;
    private static Context _context;
    private LineData data;
    public static ArrayList<BI_ReportCompare> arrDataReport = new ArrayList<>();
    private ArrayList<String> arrStyle;
    public static ArrayAdapter<String> adapterStyle;
    public static int LastSelectedStaff = -1;
    public static int LastStyleSelectedStaff = 0;

    public LeftFragment() {
    }

    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Home.bindingLeft = DataBindingUtil.inflate(inflater, R.layout.fragment_left, container, false);
        View v = Home.bindingLeft.getRoot();
        context = getContext();
        _context = context;
        event(v);
        return v;
    }

    public static void UpdateVisitedView(ArrayList<BI_ReportCompare> arrData) {
        arrDataReport = arrData;
        if (Home.bindingLeft.pieViewCountVisit == null)
            return;
        if (arrData.size() > 0) {
            BI_ReportCompare iBIreport = arrData.get(0);
            float percen = ((float) iBIreport.Working.INumVisit / iBIreport.Tagert.INumVisit) * 100;
            Home.bindingLeft.leftfragVisit.setText(iBIreport.Working.INumVisit + "/" + iBIreport.Tagert.INumVisit);
            SetPercentPie(_context, percen, Home.bindingLeft.pieViewCountVisit);
            float percenOrder = ((float) iBIreport.Working.INumOrdered / iBIreport.Tagert.INumOrdered) * 100;
            Home.bindingLeft.txtleftFragOrder.setText(iBIreport.Working.INumOrdered + "/" + iBIreport.Tagert.INumOrdered);
            SetPercentPie(_context, percenOrder, Home.bindingLeft.pieViewCountOrder);
            float percenAmount = (float) ((float) iBIreport.Working.IAmount / iBIreport.Tagert.IAmount) * 100;
            Home.bindingLeft.txtleftfragAmount.setText((iBIreport.Working.IAmount) / 1000000 + "/" + (iBIreport.Tagert.IAmount / 1000000));
            SetPercentPie(_context, percenAmount, Home.bindingLeft.pieViewAmount);
            float percenSKU = ((float) iBIreport.Working.ISKU / iBIreport.Tagert.ISKU) * 100;
            Home.bindingLeft.txtLeftFragSKU.setText(iBIreport.Working.ISKU + "/" + iBIreport.Tagert.ISKU);
            SetPercentPie(_context, percenSKU, Home.bindingLeft.pieViewSKU);
            setDataBarChar(arrData, 0);
        }
    }

    static void SetPercentPie(Context context, float Percen, az.plainpie.PieView pieView) {
        pieView.setmPercentage(Percen);
        if (Percen < 25) {
            pieView.setPercentageBackgroundColor(ContextCompat.getColor(context, R.color.colorRed));
        } else if (Percen < 50) {
            pieView.setPercentageBackgroundColor(ContextCompat.getColor(context, R.color.colorYellow));
        } else {
            pieView.setPercentageBackgroundColor(ContextCompat.getColor(context, R.color.colorBlueDark));
        }
    }

    private void event(View v) {
        Home.bindingLeft.pieViewCountVisit.setOnClickListener(this);
        Home.bindingLeft.pieViewCountOrder.setOnClickListener(this);
        Home.bindingLeft.pieViewAmount.setOnClickListener(this);
        Home.bindingLeft.pieViewSKU.setOnClickListener(this);
        EventPool.control().enQueue(new EventType.EventGetUsersRequest());
        Home.bindingLeft.pieViewCountVisit.setPercentageBackgroundColor(ContextCompat.getColor(context, R.color.colorRed));
        Home.bindingLeft.pieViewCountVisit.setmPercentage(30);
        Home.bindingLeft.pieViewCountVisit.setInnerText("30%");
        if (Home.adapterStaff == null)
            Home.adapterStaff = new ArrayUserAdapter(this.getActivity(), R.layout.custom_adapterstaff, Home.arrStaff);
        Home.bindingLeft.lineChartDS.setPinchZoom(true);
        Home.bindingLeft.lineChartDS.setDescription("Doanh số bán hàng trong tháng");

        if (arrStyle == null) arrStyle = new ArrayList<>();
        if (Home.timelinesArrayList == null) Home.timelinesArrayList = new ArrayList<>();
        arrStyle.add("Ngày");
        arrStyle.add("Tháng");
        adapterStyle = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, arrStyle);
        Home.adapterTimeLine = new RecyclerViewAdapterTimeLine(Home.timelinesArrayList, context);
        adapterStyle.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Home.bindingLeft.spSelectStaffBI.setAdapter(Home.adapterStaff);
        Home.bindingLeft.spStyleViewBI.setAdapter(adapterStyle);
        Home.bindingLeft.spSelectStaffBI.setOnItemSelectedListener(this);
        Home.bindingLeft.spStyleViewBI.setOnItemSelectedListener(this);
        //lineChartView.addData();


    }


    private void getId(View v) {

    }

    static ArrayList<String> xSoNgayBaoCao = new ArrayList<String>();

    static void getBI_ByDate(ArrayList<BI_ReportCompare> arrData) {
        xSoNgayBaoCao.clear();
        long currentDate = System.currentTimeMillis();
        for (int i = 0; i < arrData.size(); i++) {
            String sDate = arrData.get(i).sDate;
            xSoNgayBaoCao.add(sDate);
        }
        //Call Server For GetData

    }

    void ClearBI() {
        arrDataReport.clear();
        Home.bindingLeft.BarChartDS.clear();
        Home.bindingLeft.txtLeftFragSKU.setText("0/0");
        Home.bindingLeft.txtleftfragAmount.setText("0/0");
        Home.bindingLeft.leftfragVisit.setText("0/0");
        Home.bindingLeft.txtleftFragOrder.setText("0/0");
        Home.bindingLeft.pieViewSKU.setmPercentage(0);
        Home.bindingLeft.pieViewCountVisit.setmPercentage(0);
        Home.bindingLeft.pieViewAmount.setmPercentage(0);
        Home.bindingLeft.pieViewCountOrder.setmPercentage(0);

    }

    private static void setDataBarChar(ArrayList<BI_ReportCompare> arrData, int index) {
        getBI_ByDate(arrData);
        Home.bindingLeft.BarChartDS.clear();
        String TextBieuDo = " chỉ tiêu và thực hiện";
        ArrayList<BarEntry> yValChiTieu = new ArrayList<BarEntry>();
        ArrayList<BarEntry> yValThuchien = new ArrayList<BarEntry>();
        switch (index) {
            case 0:
                TextBieuDo = "Ghé thăm" + TextBieuDo;
                break;
            case 1:
                TextBieuDo = "Đơn hàng" + TextBieuDo;
                break;
            case 2:
                TextBieuDo = "Doanh Số" + TextBieuDo + " (triệu).";
                break;
            case 3:
                TextBieuDo = "SKU" + TextBieuDo;
                break;
        }
        for (int i = 0; i < arrData.size(); i++) {
            float val = 0f;
            switch (index) {
                case 0:
                    val = arrData.get(i).Tagert.INumVisit;
                    break;
                case 1:
                    val = arrData.get(i).Tagert.INumOrdered;
                    break;
                case 2:
                    val = (float) arrData.get(i).Tagert.IAmount / 1000000;
                    break;
                case 3:
                    val = arrData.get(i).Tagert.ISKU;
                    break;
            }

            yValChiTieu.add(new BarEntry(val, i));
        }

        for (int i = 0; i < arrData.size(); i++) {
            float val = 0f;
            switch (index) {
                case 0:
                    val = arrData.get(i).Working.INumVisit;
                    break;
                case 1:
                    val = arrData.get(i).Working.INumOrdered;
                    break;
                case 2:
                    val = (float) arrData.get(i).Working.IAmount / 1000000;
                    break;
                case 3:
                    val = arrData.get(i).Working.ISKU;
                    break;
            }
            yValThuchien.add(new BarEntry(val, i));
        }


        BarDataSet set1, set2, set3;

        if (Home.bindingLeft.BarChartDS.getData() != null &&
                Home.bindingLeft.BarChartDS.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) Home.bindingLeft.BarChartDS.getData().getDataSetByIndex(0);
            set2 = (BarDataSet) Home.bindingLeft.BarChartDS.getData().getDataSetByIndex(1);
            set1.setLabel("Chỉ tiêu");
            set1.setColor(ContextCompat.getColor(_context, R.color.colorBlue));
            set2.setLabel("Thưc hiện");
            set2.setColor(ContextCompat.getColor(_context, R.color.colorGreen));
            set1.setYVals(yValChiTieu);
            set2.setYVals(yValThuchien);
            Home.bindingLeft.BarChartDS.getData().setXVals(xSoNgayBaoCao);
            Home.bindingLeft.BarChartDS.getData().notifyDataChanged();
            Home.bindingLeft.BarChartDS.notifyDataSetChanged();
        } else {
            // create 3 datasets with different types
            set1 = new BarDataSet(yValChiTieu, "Chỉ tiêu");
            // set1.setColors(ColorTemplate.createColors(getApplicationContext(),
            // ColorTemplate.FRESH_COLORS));
            set1.setColor(ContextCompat.getColor(_context, R.color.colorBlue));
            set2 = new BarDataSet(yValThuchien, "Thực hiện");
            set2.setColor(ContextCompat.getColor(_context, R.color.colorGreen));

            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set1);
            dataSets.add(set2);

            BarData data = new BarData(xSoNgayBaoCao, dataSets);
//        data.setValueFormatter(new LargeValueFormatter());

            // add space between the dataset groups in percent of bar-width
            data.setGroupSpace(80f);
            //data.setValueTypeface(tf);
            Home.bindingLeft.BarChartDS.setData(data);
        }
        Home.bindingLeft.BarChartDS.setDescription(TextBieuDo);
        Home.bindingLeft.BarChartDS.animateXY(2000, 2000);
        Home.bindingLeft.BarChartDS.invalidate();
    }

    public static void ResetChar() {
        if (Home.bindingLeft.BarChartDS != null)
            Home.bindingLeft.BarChartDS.clear();
    }

    private void setDataLineChar(int count, float range) {

        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            xVals.add((i) + "");
        }

        ArrayList<Entry> yVals1 = new ArrayList<Entry>();

        for (int i = 0; i < count; i++) {
            float mult = range;
            float val = (float) (Math.random() * mult) + 50;// + (float)
            // ((mult *
            // 0.1) / 10);
            yVals1.add(new Entry(val, i));
        }

        ArrayList<Entry> yVals2 = new ArrayList<Entry>();

        for (int i = 0; i < count; i++) {
            float mult = range;
            float val = (float) (Math.random() * mult) + 50;// + (float)
            // ((mult *
            // 0.1) / 10);
            yVals2.add(new Entry(val, i));
        }

        LineDataSet set1, set2;

        if (Home.bindingLeft.lineChartDS.getData() != null &&
                Home.bindingLeft.lineChartDS.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) Home.bindingLeft.lineChartDS.getData().getDataSetByIndex(0);
            set2 = (LineDataSet) Home.bindingLeft.lineChartDS.getData().getDataSetByIndex(1);
            set1.setYVals(yVals1);
            set2.setYVals(yVals1);
            Home.bindingLeft.lineChartDS.getData().setXVals(xVals);
            Home.bindingLeft.lineChartDS.getData().notifyDataChanged();
            Home.bindingLeft.lineChartDS.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(yVals1, "Chỉ tiêu");

            set1.setAxisDependency(YAxis.AxisDependency.LEFT);
            set1.setColor(ColorTemplate.getHoloBlue());
            set1.setCircleColor(Color.WHITE);
            set1.setLineWidth(2f);
            set1.setCircleRadius(3f);
            set1.setFillAlpha(65);
            set1.setFillColor(ColorTemplate.getHoloBlue());
            set1.setHighLightColor(Color.rgb(244, 117, 117));
            set1.setDrawCircleHole(false);
            //set1.setFillFormatter(new MyFillFormatter(0f));
            //set1.setDrawHorizontalHighlightIndicator(false);
            //set1.setVisible(false);
            //set1.setCircleHoleColor(Color.WHITE);

            // create a dataset and give it a type
            set2 = new LineDataSet(yVals2, "Thực hiện");
            set2.setAxisDependency(YAxis.AxisDependency.RIGHT);
            set2.setColor(Color.RED);
            set2.setCircleColor(Color.WHITE);
            set2.setLineWidth(2f);
            set2.setCircleRadius(3f);
            set2.setFillAlpha(65);
            set2.setFillColor(Color.RED);
            set2.setDrawCircleHole(false);
            set2.setHighLightColor(Color.rgb(244, 117, 117));
            //set2.setFillFormatter(new MyFillFormatter(900f));

            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(set1); // add the datasets
            dataSets.add(set2);

            // create a data object with the datasets
            LineData data = new LineData(xVals, dataSets);
            data.setValueTextColor(Color.WHITE);
            data.setValueTextSize(9f);

            // set data
            Home.bindingLeft.lineChartDS.setData(data);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pieViewCountVisit:
                setDataBarChar(LeftFragment.arrDataReport, 0);
                break;
            case R.id.pieViewCountOrder:
                setDataBarChar(LeftFragment.arrDataReport, 1);
                break;
            case R.id.pieViewAmount:
                setDataBarChar(LeftFragment.arrDataReport, 2);
                break;
            case R.id.pieViewSKU:
                setDataBarChar(LeftFragment.arrDataReport, 3);
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        long currentDate = System.currentTimeMillis();
        switch (parent.getId()) {
            case R.id.spSelectStaff_BI:
                ClearBI();
                LastSelectedStaff = position;


                if (position != -1) {
                    switch (parent.getId()) {
                        case R.id.spSelectStaff_BI:
                            if (position >= 0) {
                                LastSelectedStaff = Home.arrStaff.get(position).id_employee;
                            }
                            break;
                    }
                }
                EventPool.control().enQueue(new EventType.EventLoadBI_DailyReportRequest(currentDate, LastSelectedStaff, LastStyleSelectedStaff));
                break;
            case R.id.spStyleView_BI:
                LastStyleSelectedStaff = position;
                EventPool.control().enQueue(new EventType.EventLoadBI_DailyReportRequest(currentDate, LastSelectedStaff, LastStyleSelectedStaff));
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
