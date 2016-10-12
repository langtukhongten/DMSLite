package com.vietdms.mobile.dmslauncher.RecycleView;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.vietdms.mobile.dmslauncher.R;

import java.util.ArrayList;
import java.util.List;

import CommonLib.AppManager;
import CommonLib.ApproVal;
import CommonLib.Model;
import CommonLib.Product;
import CommonLib.Utils;


/**
 * Created by ${LTG} was born ${10/12/1994}.
 */
public class RecyclerViewAdapterApproval extends RecyclerView.Adapter<RecyclerViewAdapterApproval.ContactViewHolder> implements Filterable {
    private List<ApproVal> ApproValList;
    private List<ApproVal> ApproValListFilter;
    private Context context;
    private ItemFilter mFilter = new ItemFilter();

    public RecyclerViewAdapterApproval(List<ApproVal> ApproValList, Context context) {
        this.ApproValList = ApproValList;
        this.ApproValListFilter = ApproValList;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return ApproValListFilter.size();
    }

    @Override
    public void onBindViewHolder(ContactViewHolder contactViewHolder, int i) {
        ApproVal c = ApproValListFilter.get(i);
        contactViewHolder.appName.setText(c.appName);
        contactViewHolder.employeeName.setText(c.employeeName);
        contactViewHolder.date.setText(Utils.long2String(c.created_date));
        contactViewHolder.description.setText(c.description);
        contactViewHolder.status.setText(Utils.statusApproval(c.status));
        contactViewHolder.photo.setImageDrawable(AppManager.inst().getIcon(c.packageName));
    }

    @Override
    public ContactViewHolder onCreateViewHolder(final ViewGroup viewGroup, final int i) {
        final View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.card_approval, viewGroup, false);
        return new ContactViewHolder(itemView);
    }


    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        protected TextView employeeName,appName, date,description ,status;
        protected ImageView photo;

        public ContactViewHolder(View v) {
            super(v);
            appName = (TextView) v.findViewById(R.id.approval_app_name);
            employeeName = (TextView) v.findViewById(R.id.approval_employee_name);
            date = (TextView) v.findViewById(R.id.approval_date);
            photo = (ImageView) v.findViewById(R.id.approval_photo);
            status = (TextView) v.findViewById(R.id.approval_status);
            description = (TextView) v.findViewById(R.id.approval_description);

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

            final List<ApproVal> list = ApproValList;

            int count = list.size();
            final ArrayList<ApproVal> nlist = new ArrayList<>(count);

            ApproVal filterableApproVal;

            for (int i = 0; i < count; i++) {
                filterableApproVal = list.get(i);
                if (Utils.unAccent(filterableApproVal.employeeName).toLowerCase().contains(filterString)) {
                    nlist.add(filterableApproVal);
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            ApproValListFilter = (ArrayList<ApproVal>) results.values;
            notifyDataSetChanged();
        }

    }

    public void setItems(ArrayList<ApproVal> list) {
        this.ApproValList = list;
        this.ApproValListFilter = list;
    }
}

