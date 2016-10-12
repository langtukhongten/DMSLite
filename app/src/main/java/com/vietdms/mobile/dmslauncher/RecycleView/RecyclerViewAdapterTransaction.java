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

import CommonLib.Transaction;
import CommonLib.Utils;
import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by ${LTG} was born ${10/12/1994}.
 */
public class RecyclerViewAdapterTransaction extends RecyclerView.Adapter<RecyclerViewAdapterTransaction.ContactViewHolder> implements Filterable {
    private List<Transaction> transactionList;
    private List<Transaction> transactionListFilter;
    private Context context;
    private ItemFilter mFilter = new ItemFilter();

    public RecyclerViewAdapterTransaction(List<Transaction> transactionList, Context context) {
        this.transactionList = transactionList;
        this.transactionListFilter = transactionList;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return transactionListFilter.size();
    }

    @Override
    public void onBindViewHolder(ContactViewHolder contactViewHolder, int i) {
        Transaction t = transactionListFilter.get(i);
        contactViewHolder.transactionNo.setText(t.no_);
        contactViewHolder.transactionName.setText(t.description);
        contactViewHolder.transactionAddress.setText(t.trans_address);
        contactViewHolder.transactionPhone.setText(t.phone_no_);
        contactViewHolder.transactionStatus.setImageDrawable(Utils.statusTransaction(context, t.status));
        contactViewHolder.transactionDate.setText(Utils.long2OverTime(t.modifieddate));
        contactViewHolder.transactionNote.setText(t.note+" status: "+t.status);
        i++;
        int color;
        TextDrawable photoDrawable = null;
        if (t.root_status == 99 || t.root_status == 1) {
            color = 2;
        } else {
            if (t.is_read) {
                color = 1;
            } else color = 0;
        }
        switch (color) {
            case 0:
                photoDrawable = TextDrawable.builder()
                        .buildRound(i + "", Color.parseColor("#FFC000")); // radius in px
                break;
            case 1:
                photoDrawable = TextDrawable.builder()
                        .buildRound(i + "", Color.GREEN); // radius in px
                break;
            case 2:
                photoDrawable = TextDrawable.builder()
                        .buildRound(i + "", Color.parseColor("#bdbdbd")); // radius in px
                break;
        }
        contactViewHolder.transactionStt.setImageDrawable(photoDrawable);
    }

    @Override
    public ContactViewHolder onCreateViewHolder(final ViewGroup viewGroup, final int i) {
        final View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.card_transaction, viewGroup, false);
        return new ContactViewHolder(itemView);
    }


    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        protected TextView transactionName, transactionNo, transactionAddress, transactionPhone, transactionNote, transactionDate;
        protected ImageView transactionStt;
        protected CircleImageView transactionStatus;

        public ContactViewHolder(View v) {
            super(v);
            transactionStt = (ImageView) v.findViewById(R.id.transaction_stt);
            transactionName = (TextView) v.findViewById(R.id.transaction_name);
            transactionNo = (TextView) v.findViewById(R.id.transaction_no);
            transactionAddress = (TextView) v.findViewById(R.id.transaction_address);
            transactionPhone = (TextView) v.findViewById(R.id.transaction_phone);
            transactionNote = (TextView) v.findViewById(R.id.transaction_note);
            transactionStatus = (CircleImageView) v.findViewById(R.id.transaction_status);
            transactionDate = (TextView) v.findViewById(R.id.transaction_time);
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

            final List<Transaction> list = transactionList;

            int count = list.size();
            final ArrayList<Transaction> nlist = new ArrayList<>(count);

            Transaction filterableTransaction;

            for (int i = 0; i < count; i++) {
                filterableTransaction = list.get(i);
                if (Utils.unAccent(filterableTransaction.description).toLowerCase().contains(filterString)) {
                    nlist.add(filterableTransaction);
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            transactionListFilter = (ArrayList<Transaction>) results.values;
            notifyDataSetChanged();
        }

    }

    public void setItems(ArrayList<Transaction> list) {
        this.transactionList = list;
        this.transactionListFilter = list;
    }
}

