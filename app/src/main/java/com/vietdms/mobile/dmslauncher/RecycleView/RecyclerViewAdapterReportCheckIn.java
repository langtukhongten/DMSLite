package com.vietdms.mobile.dmslauncher.RecycleView;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.vietdms.mobile.dmslauncher.Home;
import com.vietdms.mobile.dmslauncher.MyMethod;
import com.vietdms.mobile.dmslauncher.R;

import java.util.ArrayList;
import java.util.List;

import CommonLib.Order;
import CommonLib.ReportCheckIn;
import CommonLib.Utils;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Admin on 7/9/2016.
 */
public class RecyclerViewAdapterReportCheckIn extends RecyclerView.Adapter<RecyclerViewAdapterReportCheckIn.ContactViewHolder> implements Filterable {
    private List<ReportCheckIn> ReportCheckInList;
    private List<ReportCheckIn> ReportCheckInListFilter;
    private Context context;
    private ItemFilter mFilter = new ItemFilter();

    public RecyclerViewAdapterReportCheckIn(List<ReportCheckIn> ReportCheckInList, Context context) {
        this.ReportCheckInList = ReportCheckInList;
        this.ReportCheckInListFilter = ReportCheckInList;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return ReportCheckInList.size();
    }

    @Override
    public void onBindViewHolder(ContactViewHolder contactViewHolder, int i) {
        Home.visibleTitleRC++;
        if (Home.visibleTitleRC == 1) {
            MyMethod.setVisible(contactViewHolder.title);
        } else {
            MyMethod.setGone(contactViewHolder.title);
        }

        ReportCheckIn rc = ReportCheckInList.get(i);
        contactViewHolder.employee.setText(rc.employee);
        contactViewHolder.store.setText(rc.store + "");
        contactViewHolder.smaller15.setText(rc.smaller15 + "");
        contactViewHolder.bigger15.setText(rc.bigger15 + "");
        contactViewHolder.order.setText(rc.order + "");
        contactViewHolder.sales.setText(Utils.formatFloat((float) rc.sales));
    }

    @Override
    public ContactViewHolder onCreateViewHolder(final ViewGroup viewGroup, final int i) {
        final View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.card_report_check_in, viewGroup, false);
        return new ContactViewHolder(itemView);
    }


    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        protected TextView employee, store, smaller15, bigger15, order, sales;
        protected LinearLayout title;

        public ContactViewHolder(View v) {
            super(v);
            employee = (TextView) v.findViewById(R.id.report_employee);
            store = (TextView) v.findViewById(R.id.report_store_amout);
            smaller15 = (TextView) v.findViewById(R.id.report_smaller_15_minute);
            bigger15 = (TextView) v.findViewById(R.id.report_bigger_15_minute);
            order = (TextView) v.findViewById(R.id.report_order_amount);
            sales = (TextView) v.findViewById(R.id.report_sales_amount);
            title = (LinearLayout) v.findViewById(R.id.title_report_check_in);
            //Set clicked in cardview
        }
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = Utils.unAccent(constraint.toString()).toLowerCase();

            FilterResults results = new FilterResults();

            final List<ReportCheckIn> list = ReportCheckInList;

            int count = list.size();
            final ArrayList<ReportCheckIn> nlist = new ArrayList<>(count);

            ReportCheckIn filterableReportCheckIn;

            for (int i = 0; i < count; i++) {
                filterableReportCheckIn = list.get(i);
                if (Utils.unAccent(filterableReportCheckIn.employee).toLowerCase().contains(filterString)) {
                    nlist.add(filterableReportCheckIn);
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            ReportCheckInListFilter = (ArrayList<ReportCheckIn>) results.values;
            notifyDataSetChanged();
        }

    }

    public void setItems(ArrayList<ReportCheckIn> list) {
        this.ReportCheckInList = list;
        this.ReportCheckInListFilter = list;
    }
}


