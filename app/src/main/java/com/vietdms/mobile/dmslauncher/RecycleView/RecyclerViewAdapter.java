package com.vietdms.mobile.dmslauncher.RecycleView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vietdms.mobile.dmslauncher.R;

import java.util.List;

import CommonLib.OrderDeliveryItem;


/**
 * Created by ${LTG} was born ${10/12/1994}.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ContactViewHolder> {
    private List<OrderDeliveryItem> deliveryItems;
    private Context context;

    public RecyclerViewAdapter(List<OrderDeliveryItem> deliveryItems, Context context) {
        this.deliveryItems = deliveryItems;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return deliveryItems.size();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ContactViewHolder contactViewHolder, int i) {
        OrderDeliveryItem ci = deliveryItems.get(i);
        contactViewHolder.orderNo.setText(ci.orderNo);
        contactViewHolder.nameAddress.setText(ci.nameAddress);
        contactViewHolder.orderPrice.setText(ci.orderPrice + context.getString(R.string.money));
        contactViewHolder.orderDescription.setText(ci.description);
    }

    @Override
    public ContactViewHolder onCreateViewHolder(final ViewGroup viewGroup, final int i) {
        final View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.card_order, viewGroup, false);
        return new ContactViewHolder(itemView);
    }


    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        protected TextView orderNo, nameAddress, orderPrice, orderDescription;

        public ContactViewHolder(View v) {
            super(v);
            orderNo = (TextView) v.findViewById(R.id.order_no);
            nameAddress = (TextView) v.findViewById(R.id.order_name_address);
            orderPrice = (TextView) v.findViewById(R.id.order_price);
            orderDescription = (TextView) v.findViewById(R.id.order_description);
            //Set clicked in cardview
        }
    }

}

