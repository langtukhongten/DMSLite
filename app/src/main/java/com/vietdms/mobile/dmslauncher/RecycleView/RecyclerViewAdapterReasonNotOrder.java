package com.vietdms.mobile.dmslauncher.RecycleView;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.vietdms.mobile.dmslauncher.R;

import java.util.ArrayList;
import java.util.List;

import CommonLib.ReasonNotOrder;

/**
 * Created by Admin on 7/26/2016.
 */
public class RecyclerViewAdapterReasonNotOrder extends RecyclerView.Adapter<RecyclerViewAdapterReasonNotOrder.ContactViewHolder> {
    private List<ReasonNotOrder> reasonNotOrderList;
    private Context context;

    public RecyclerViewAdapterReasonNotOrder(List<ReasonNotOrder> reasonNotOrderList, Context context) {
        this.reasonNotOrderList = reasonNotOrderList;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return reasonNotOrderList.size();
    }

    @Override
    public void onBindViewHolder(final ContactViewHolder contactViewHolder, final int i) {
        final ReasonNotOrder c = reasonNotOrderList.get(i);
        contactViewHolder.reasonSerial.setText(i + " - ");
        contactViewHolder.reasonContent.setText(c.content + ((c.type == 1) ? " (Camera)" : ""));
        contactViewHolder.reasonContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contactViewHolder.reasonCheckBox.performClick();
            }
        });
        contactViewHolder.reasonCheckBox.setOnClickListener(new View.OnClickListener() {
            //hack instead because setOnChecked can't call notifyDataSetChanged()
            @Override
            public void onClick(View v) {
                c.isSelected = contactViewHolder.reasonCheckBox.isChecked();
                for (int j = 0; j < reasonNotOrderList.size(); j++) {
                    if (i != j) {
                        reasonNotOrderList.get(j).isSelected = false;
                    }
                }
                notifyDataSetChanged();
            }
        });
        contactViewHolder.reasonCheckBox.setChecked(c.isSelected);
    }

    @Override
    public ContactViewHolder onCreateViewHolder(final ViewGroup viewGroup, final int i) {
        final View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.card_reason_not_order, viewGroup, false);
        return new ContactViewHolder(itemView);
    }


    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        protected TextView reasonContent, reasonSerial;
        protected RadioButton reasonCheckBox;

        public ContactViewHolder(View v) {
            super(v);
            reasonContent = (TextView) v.findViewById(R.id.reason_text);
            reasonSerial = (TextView) v.findViewById(R.id.reason_serial);
            reasonCheckBox = (RadioButton) v.findViewById(R.id.reason_check_box);
            //Set clicked in cardview
        }
    }


    public void setItems(ArrayList<ReasonNotOrder> list) {
        this.reasonNotOrderList = list;
    }
}
