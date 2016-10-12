package com.vietdms.mobile.dmslauncher.CustomAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;


import com.vietdms.mobile.dmslauncher.R;

import java.util.ArrayList;
import java.util.List;

import CommonLib.AppManager;
import CommonLib.Utils;

public class CustomAdapterGripView extends BaseAdapter implements Filterable {
    private LayoutInflater layoutinflater;
    private List<String> listApp;
    private List<String> listAppFilter;
    private Context context;
    private ItemFilter mFilter = new ItemFilter();

    public CustomAdapterGripView(Context context, List<String> customizedListView) {
        this.context = context;
        layoutinflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listApp = customizedListView;
        listAppFilter = customizedListView;
    }

    @Override
    public int getCount() {
        return listAppFilter.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public String getObjectClick(int position) {
        return listAppFilter.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder listViewHolder;
        if (convertView == null) {
            listViewHolder = new ViewHolder();
            convertView = layoutinflater.inflate(R.layout.custom_gripview, parent, false);
            listViewHolder.textInListView = (TextView) convertView.findViewById(R.id.textView);
            listViewHolder.imageInListView = (ImageView) convertView.findViewById(R.id.imageView);
            convertView.setTag(listViewHolder);
        } else {
            listViewHolder = (ViewHolder) convertView.getTag();
        }
        listViewHolder.textInListView.setText(AppManager.inst().getLabel(listAppFilter.get(position)));
        listViewHolder.imageInListView.setImageDrawable(AppManager.inst().getIcon(listAppFilter.get(position)));
        return convertView;
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

            final List<String> list = listApp;

            int count = list.size();
            final ArrayList<String> nlist = new ArrayList<>(count);

            String filterableApps;

            for (int i = 0; i < count; i++) {
                filterableApps = list.get(i);
                if (Utils.unAccent(AppManager.inst().getLabel(filterableApps)).toLowerCase().contains(filterString)) {
                    nlist.add(filterableApps);
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            listAppFilter = (ArrayList<String>) results.values;
            notifyDataSetChanged();
        }

    }

    static class ViewHolder {
        TextView textInListView;
        ImageView imageInListView;
    }
}