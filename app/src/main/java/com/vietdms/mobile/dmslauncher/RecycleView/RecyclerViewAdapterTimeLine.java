package com.vietdms.mobile.dmslauncher.RecycleView;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
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

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import CommonLib.Product;
import CommonLib.TimeLine;
import CommonLib.Utils;

/**
 * Created by Admin on 6/20/2016.
 */
public class RecyclerViewAdapterTimeLine extends RecyclerView.Adapter<RecyclerViewAdapterTimeLine.ContactViewHolder> implements Filterable {
    private List<TimeLine> timeLineList;
    private List<TimeLine> timeLinesFilter;
    private Context context;
    private ItemFilter mFilter = new ItemFilter();
    private static Typeface normal, bold, italic;

    public RecyclerViewAdapterTimeLine(List<TimeLine> TimeLineList, Context context) {
        this.timeLineList = TimeLineList;
        this.timeLinesFilter = TimeLineList;
        this.context = context;
        if (normal == null)
            normal = Typeface.createFromAsset(context.getAssets(), "fonts/normal.ttf");
        if (bold == null) bold = Typeface.createFromAsset(context.getAssets(), "fonts/bold.ttf");
        if (italic == null)
            italic = Typeface.createFromAsset(context.getAssets(), "fonts/italic.ttf");
    }

    @Override
    public int getItemCount() {
        return timeLinesFilter.size();
    }

    @Override
    public void onBindViewHolder(ContactViewHolder contactViewHolder, int i) {
        TimeLine tl = timeLinesFilter.get(i);
        contactViewHolder.name.setText(tl.name);
        contactViewHolder.time.setText(Utils.long2OverTime(tl.time));
        if (tl.distance == 0)
            contactViewHolder.distance.setText("-");
        else
            contactViewHolder.distance.setText(Utils.formatFloat(tl.distance) + " m");
        contactViewHolder.action.setText(tl.action + " " + context.getString(R.string.at));
        contactViewHolder.customer.setText(tl.address);
        contactViewHolder.address.setText(tl.customer);
        if (tl.image != null) contactViewHolder.image.setImageBitmap(tl.image);
    }

    @Override
    public ContactViewHolder onCreateViewHolder(final ViewGroup viewGroup, final int i) {
        final View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.card_timeline, viewGroup, false);
        return new ContactViewHolder(itemView);
    }


    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        protected TextView name, time, distance, action, customer, address;
        protected ImageView image;

        public ContactViewHolder(View v) {
            super(v);
            name = (TextView) v.findViewById(R.id.nameTimeline);
            time = (TextView) v.findViewById(R.id.timeTimeLine);
            distance = (TextView) v.findViewById(R.id.distanceTimeLine);
            action = (TextView) v.findViewById(R.id.actionTimeLine);
            customer = (TextView) v.findViewById(R.id.customerTimeLine);
            address = (TextView) v.findViewById(R.id.addressTimeLine);
            image = (ImageView) v.findViewById(R.id.imageTimeLine);
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

            final List<TimeLine> list = timeLineList;

            int count = list.size();
            final ArrayList<TimeLine> nlist = new ArrayList<>(count);

            TimeLine filterableTimeLine;

            for (int i = 0; i < count; i++) {
                filterableTimeLine = list.get(i);
                if (Utils.unAccent(filterableTimeLine.name).toLowerCase().contains(filterString)) {
                    nlist.add(filterableTimeLine);
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            timeLinesFilter = (ArrayList<TimeLine>) results.values;
            notifyDataSetChanged();
        }

    }

    public void setItems(ArrayList<TimeLine> list) {
        this.timeLineList = list;
        this.timeLinesFilter = list;
    }
}
