package com.vietdms.mobile.dmslauncher.CustomAdapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.vietdms.mobile.dmslauncher.R;

import java.util.ArrayList;

import CommonLib.UserInfo;
import CommonLib.Utils;

/**
 * Created by ${LTG} on ${10/12/1994}.
 */
public class ArrayUserAdapter extends ArrayAdapter<UserInfo> {
    private Activity activity;
    private ArrayList data;
    LayoutInflater inflater;
    private UserInfo tempValues;

    /*************
     * CustomAdapter Constructor
     *****************/
    public ArrayUserAdapter(
            Activity activitySpinner,
            int textViewResourceId,
            ArrayList objects
    ) {
        super(activitySpinner, textViewResourceId, objects);

        /********** Take passed values **********/
        activity = activitySpinner;
        data = objects;

        /***********  Layout inflator to call external xml layout () **********************/
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        /********** Inflate spinner_rows.xml file for each row ( Defined below ) ************/
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_spinner, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) convertView.findViewById(R.id.nameStaff);
            viewHolder.time = (TextView) convertView.findViewById(R.id.timeStaff);
            viewHolder.batery = (TextView) convertView.findViewById(R.id.bateryStaff);
            viewHolder.imageAir = (ImageView) convertView.findViewById(R.id.imageAir);
            viewHolder.imageNet = (ImageView) convertView.findViewById(R.id.imageNet);
            viewHolder.imageGps = (ImageView) convertView.findViewById(R.id.imageGPS);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        /***** Get each Model object from Arraylist ********/
        tempValues = null;
        tempValues = (UserInfo) data.get(position);


        // Set values for spinner each row
        viewHolder.name.setText(tempValues.fullname);
        viewHolder.time.setText(Utils.long2HourMinute(tempValues.trackingDate));
        viewHolder.batery.setText(tempValues.batteryLevel + "%");
        viewHolder.imageAir.setImageResource(tempValues.isAirplaneMode == 1 ? R.drawable.airmode_on_btn : R.drawable.airmode_off_btn);
        viewHolder.imageGps.setImageResource(tempValues.isGPS == 1 ? R.drawable.gps_on_btn : R.drawable.gps_off_btn);
        viewHolder.imageNet.setImageResource(tempValues.isWifi == 1 || tempValues.is3G == 1 ? R.drawable.network_on_btn : R.drawable.network_off_btn);
        if (tempValues.batteryLevel == 0)
            viewHolder.batery.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(getContext(), R.drawable.batery_0_btn), null, null, null);
        else if (tempValues.batteryLevel < 20)
            viewHolder.batery.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(getContext(), R.drawable.batery_below20_btn), null, null, null);
        else if (tempValues.batteryLevel < 30)
            viewHolder.batery.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(getContext(), R.drawable.batery_low_btn), null, null, null);
        else if (tempValues.batteryLevel < 50)
            viewHolder.batery.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(getContext(), R.drawable.batery_below50_btn), null, null, null);
        else if (tempValues.batteryLevel < 100)
            viewHolder.batery.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(getContext(), R.drawable.batery_top50_btn), null, null, null);
        else
            viewHolder.batery.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(getContext(), R.drawable.batery_full_btn), null, null, null);


        return convertView;
    }

    public void setArrayList(ArrayList<UserInfo> arrStaff) {
        this.data.clear();
        this.data.addAll(arrStaff);
        this.notifyDataSetChanged();
    }

    static class ViewHolder {
        TextView name;
        TextView time;
        TextView batery;
        ImageView imageAir;
        ImageView imageNet;
        ImageView imageGps;
    }

}
