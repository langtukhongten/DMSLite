package com.vietdms.mobile.dmslauncher.RecycleView;

import android.content.Context;
import android.support.v4.content.ContextCompat;
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

import CommonLib.Order;
import CommonLib.Utils;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Admin on 5/26/2016.
 */
public class RecyclerViewAdapterOrder extends RecyclerView.Adapter<RecyclerViewAdapterOrder.ContactViewHolder> implements Filterable {
    private List<Order> OrderList;
    private List<Order> OrderListFilter;
    private Context context;
    private ItemFilter mFilter = new ItemFilter();

    public RecyclerViewAdapterOrder(List<Order> OrderList, Context context) {
        this.OrderList = OrderList;
        this.OrderListFilter = OrderList;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return OrderListFilter.size();
    }

    @Override
    public void onBindViewHolder(ContactViewHolder contactViewHolder, int i) {
        Order o = OrderListFilter.get(i);
        contactViewHolder.orderNo.setText(o.no_);
        contactViewHolder.orderName.setText(o.name);
        contactViewHolder.orderTime.setText(Utils.long2HourMinute(o.time));
        contactViewHolder.orderEmployee.setText(o.employeeName);
        contactViewHolder.orderAmount.setText(Utils.formatFloat(o.amount) + context.getString(R.string.money));
        if (o.status == 0 || o.status == 2) {
            contactViewHolder.orderStatus.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(context, R.drawable.edit_btn), null);
        } else if (o.status == 1 || o.status == 3) {
            contactViewHolder.orderStatus.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(context, R.drawable.order_check_btn), null);

        } else if (o.status == 4) {
            contactViewHolder.orderStatus.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(context, R.drawable.order_cancel_btn), null);
        }
        contactViewHolder.orderStatus.setText(Utils.statusOrder(o.status));

        Glide.with(context).load(MyMethod.getUrlCustomerImage(o.imageUrl)).error(R.drawable.noavatar_btn).override(150, 150).into(contactViewHolder.orderSerialNo);
    }

    @Override
    public ContactViewHolder onCreateViewHolder(final ViewGroup viewGroup, final int i) {
        final View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.card_order_main, viewGroup, false);
        return new ContactViewHolder(itemView);
    }


    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        protected TextView orderNo, orderTime, orderAmount, orderStatus, orderName, orderEmployee;
        protected CircleImageView orderSerialNo;

        public ContactViewHolder(View v) {
            super(v);
            orderNo = (TextView) v.findViewById(R.id.order_main_no);
            orderName = (TextView) v.findViewById(R.id.order_main_name);
            orderTime = (TextView) v.findViewById(R.id.order_main_time);
            orderAmount = (TextView) v.findViewById(R.id.order_main_amount);
            orderStatus = (TextView) v.findViewById(R.id.order_main_status);
            orderSerialNo = (CircleImageView) v.findViewById(R.id.order_main_serial_no);
            orderEmployee = (TextView) v.findViewById(R.id.order_main_employee);
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

            final List<Order> list = OrderList;

            int count = list.size();
            final ArrayList<Order> nlist = new ArrayList<>(count);

            Order filterableOrder;

            for (int i = 0; i < count; i++) {
                filterableOrder = list.get(i);
                if (Utils.unAccent(filterableOrder.name).toLowerCase().contains(filterString)) {
                    nlist.add(filterableOrder);
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            OrderListFilter = (ArrayList<Order>) results.values;
            notifyDataSetChanged();
        }

    }

    public void setItems(ArrayList<Order> list) {
        this.OrderList = list;
        this.OrderListFilter = list;
    }
}

