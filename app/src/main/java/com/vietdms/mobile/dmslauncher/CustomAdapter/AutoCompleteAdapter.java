package com.vietdms.mobile.dmslauncher.CustomAdapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import CommonLib.Status;

/**
 * Created by chutien on 10/27/16.
 */

public class AutoCompleteAdapter extends ArrayAdapter<Status> {
    private ArrayList<Status> items;
    private ArrayList<Status> itemsAll;
    private ArrayList<Status> suggestions;
    private int viewResourceId;

    @SuppressWarnings("unchecked")
    public AutoCompleteAdapter(Context context, int viewResourceId,
                               ArrayList<Status> items) {
        super(context, viewResourceId, items);
        this.items = items;
        this.itemsAll = (ArrayList<Status>) items.clone();
        this.suggestions = new ArrayList<Status>();
        this.viewResourceId = viewResourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(viewResourceId, null);
        }
        Status product = items.get(position);
        if (product != null) {
            TextView productLabel = (TextView) v.findViewById(android.R.id.text1);
            if (productLabel != null) {
                productLabel.setText(product.name);
            }
        }
        return v;
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    Filter nameFilter = new Filter() {
        public String convertResultToString(Object resultValue) {
            String str = ((Status) (resultValue)).name;
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for (Status product : itemsAll) {
                    if (product.name.toLowerCase()
                            .startsWith(constraint.toString().toLowerCase())) {
                        suggestions.add(product);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            @SuppressWarnings("unchecked")
            ArrayList<Status> filteredList = (ArrayList<Status>) results.values;
            if (results != null && results.count > 0) {
                clear();
                for (Status c : filteredList) {
                    add(c);
                }
                notifyDataSetChanged();
            }
        }
    };

    public void setItems(ArrayList<Status> arrayCustomer) {
        this.items = arrayCustomer;
        this.itemsAll = arrayCustomer;

    }
}