package com.vietdms.mobile.dmslauncher.CustomAdapter;

/**
 * Created by Chu Tien on 4/21/2016.
 */

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.vietdms.mobile.dmslauncher.GetSet.Issues;
import com.vietdms.mobile.dmslauncher.R;

import java.util.ArrayList;

/**
 * Created by DMSv4 on 12/7/2015.
 */
public class CustomAdapterListIssue extends ArrayAdapter<Issues> {
    Activity context = null;
    ArrayList<Issues> myArray = null;
    int layoutId;

    public CustomAdapterListIssue(Activity context, int layoutId, ArrayList<Issues> arr) {
        super(context, layoutId, arr);
        this.context = context;
        this.layoutId = layoutId;
        this.myArray = arr;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Issues emp = myArray.get(position);
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater =
                    context.getLayoutInflater();
            convertView = inflater.inflate(layoutId, null);
            holder = new ViewHolder();
            holder.txtDay = (TextView) convertView.findViewById(R.id.txtDayIssue);
            holder.txtContent = (TextView) convertView.findViewById(R.id.txtContentIssue);
            holder.txtStatus = (TextView) convertView.findViewById(R.id.txtStatusIssue);
            holder.txtStaff = (TextView) convertView.findViewById(R.id.txtStaffIssue);
            holder.txtDetail = (TextView) convertView.findViewById(R.id.txtDetailIssue);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtDay.setText(emp.day);
        holder.txtContent.setText(emp.content);
        switch (emp.status) {
            case 0:
                holder.txtStatus.setText("Trạng thái " + emp.status);
                break;
            case 1:
                holder.txtStatus.setText("Trạng thái " + emp.status);
                break;
            case 2:
                holder.txtStatus.setText("Trạng thái " + emp.status);
                break;
            default:
                holder.txtStatus.setText("Trạng thái " + emp.status);
                break;
        }

        holder.txtStaff.setText(emp.staff);
        holder.txtDetail.setText(emp.detail);
        return convertView;
    }

    static class ViewHolder {
        TextView txtDay, txtContent, txtStatus, txtStaff, txtDetail;
    }


}
