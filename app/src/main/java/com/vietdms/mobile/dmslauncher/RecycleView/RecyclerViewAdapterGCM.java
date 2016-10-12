package com.vietdms.mobile.dmslauncher.RecycleView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vietdms.mobile.dmslauncher.R;

import java.util.List;

import CommonLib.GCM;
import CommonLib.Utils;


/**
 * Created by ${LTG} was born ${10/12/1994}.
 */
public class RecyclerViewAdapterGCM extends RecyclerView.Adapter<RecyclerViewAdapterGCM.ContactViewHolder> {
    private List<GCM> deliveryItems;
    private Context context;

    public RecyclerViewAdapterGCM(List<GCM> deliveryItems, Context context) {
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
        GCM ci = deliveryItems.get(i);
        contactViewHolder.titleGCM.setText(ci.title);
        contactViewHolder.contentGCM.setText(ci.content);
        contactViewHolder.dateGCM.setText(Utils.long2String(ci.date));
        contactViewHolder.titleGCM.setTypeface(null, ci.status == 1 ? Typeface.NORMAL : Typeface.BOLD);

    }

    @Override
    public ContactViewHolder onCreateViewHolder(final ViewGroup viewGroup, final int i) {
        final View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.card_gcm, viewGroup, false);
        return new ContactViewHolder(itemView);
    }


    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        protected TextView titleGCM, contentGCM, dateGCM;
        protected ImageView imageGCM;

        public ContactViewHolder(View v) {
            super(v);
            titleGCM = (TextView) v.findViewById(R.id.title_GCM);
            contentGCM = (TextView) v.findViewById(R.id.content_GCM);
            dateGCM = (TextView) v.findViewById(R.id.dateGCM);
            imageGCM = (ImageView) v.findViewById(R.id.imageGCM);
            //Set clicked in cardview
        }
    }

}

