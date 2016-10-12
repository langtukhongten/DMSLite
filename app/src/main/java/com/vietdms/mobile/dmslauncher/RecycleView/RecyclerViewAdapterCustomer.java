package com.vietdms.mobile.dmslauncher.RecycleView;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.vietdms.mobile.dmslauncher.MyMethod;
import com.vietdms.mobile.dmslauncher.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import CommonLib.Customer;
import CommonLib.Model;
import CommonLib.Utils;
import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by ${LTG} was born ${10/12/1994}.
 */
public class RecyclerViewAdapterCustomer extends RecyclerView.Adapter<RecyclerViewAdapterCustomer.ContactViewHolder> implements Filterable {
    private List<Customer> CustomerList;
    private List<Customer> CustomerListFilter;
    private Context context;
    private ItemFilter mFilter = new ItemFilter();

    public RecyclerViewAdapterCustomer(List<Customer> CustomerList, Context context) {
        this.CustomerList = CustomerList;
        this.CustomerListFilter = CustomerList;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return CustomerListFilter.size();
    }

    @Override
    public void onBindViewHolder(ContactViewHolder contactViewHolder, int i) {
        Customer c = CustomerListFilter.get(i);
        contactViewHolder.customerName.setText(c.name);
        contactViewHolder.customerNo.setText(c.no_);
        contactViewHolder.customerAddress.setText(c.address);
        //     contactViewHolder.customerPhoto.setImageBitmap(c.customerPhoto);
        Glide.with(context).load(MyMethod.getUrlCustomerImage(c.imageUrl)).error(R.drawable.noavatar_btn).override(150, 150).into(contactViewHolder.customerStt);
        if (c.latitude == 0) {
            contactViewHolder.customerDistance.setText("chưa có vị trí");
            contactViewHolder.customerDistance.setTextColor(Color.RED);
        } else {
            contactViewHolder.customerDistance.setText(c.distance + " m \n");
            contactViewHolder.customerDistance.setTextColor(Color.BLACK);
        }
        contactViewHolder.customerAmountLastMonth.setText(Utils.formatFloat(c.amount_last_month) + context.getString(R.string.money));
        contactViewHolder.customerLastDayOrder.setText(c.last_order_day == 0 ? "chưa có" : Utils.long2HourMinuteYear(c.last_order_day));
        if (TimeUnit.MILLISECONDS.toHours(Utils.getDayEnd(Model.getServerTime()) - c.last_visited) <= 24) {
            //có giao dịch
            if (TimeUnit.MILLISECONDS.toHours(Utils.getDayEnd(Model.getServerTime()) - c.last_order_day) > 24) {//Chưa có đơn hàng
                contactViewHolder.customer_color.setBackgroundColor(Color.RED);
            } else {
                //Có đơn hàng
                contactViewHolder.customer_color.setBackgroundColor(Color.BLUE);
            }
        } else {
            if (TimeUnit.MILLISECONDS.toHours(Utils.getDayEnd(Model.getServerTime()) - c.last_order_day) <= 24) {//có đơn hàng
                contactViewHolder.customer_color.setBackgroundColor(Color.MAGENTA);
            } else {//Chưa có đơn hàng
                contactViewHolder.customer_color.setBackgroundColor(Color.BLACK);
            }
        }
    }

    @Override
    public ContactViewHolder onCreateViewHolder(final ViewGroup viewGroup, final int i) {
        final View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.card_customer, viewGroup, false);
        return new ContactViewHolder(itemView);
    }


    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        protected TextView customerName, customerNo, customerAddress, customerDistance, customerAmountLastMonth, customerLastDayOrder;
        protected CircleImageView customerStt;
        protected TextView customer_color;

        public ContactViewHolder(View v) {
            super(v);
            customerName = (TextView) v.findViewById(R.id.customer_name);
            customerNo = (TextView) v.findViewById(R.id.customer_no);
            customerAddress = (TextView) v.findViewById(R.id.customer_address);
            customerStt = (CircleImageView) v.findViewById(R.id.customer_stt);
            customerDistance = (TextView) v.findViewById(R.id.customer_distance);
            customerAmountLastMonth = (TextView) v.findViewById(R.id.customer_amout_last_month);
            customerLastDayOrder = (TextView) v.findViewById(R.id.customer_last_day_order);
            customer_color = (TextView) v.findViewById(R.id.customer_color);
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

            final List<Customer> list = CustomerList;

            int count = list.size();
            final ArrayList<Customer> nlist = new ArrayList<>(count);

            Customer filterableCustomer;

            for (int i = 0; i < count; i++) {
                filterableCustomer = list.get(i);
                if (Utils.unAccent(filterableCustomer.name).toLowerCase().contains(filterString)) {
                    nlist.add(filterableCustomer);
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            CustomerListFilter = (ArrayList<Customer>) results.values;
            notifyDataSetChanged();
        }

    }

    public void setItems(ArrayList<Customer> list) {
        this.CustomerList = list;
        this.CustomerListFilter = list;
    }

}

