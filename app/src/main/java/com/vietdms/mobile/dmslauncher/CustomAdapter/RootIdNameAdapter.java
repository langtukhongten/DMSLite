package com.vietdms.mobile.dmslauncher.CustomAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;

import java.util.List;

import CommonLib.IdStatus;

/**
 * Created by chutien on 10/13/16.
 */

public class RootIdNameAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private List<IdStatus> listFilter;
    private Context context;

    public RootIdNameAdapter(Context context, List<IdStatus> list) {
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.listFilter = list;
    }

    public IdStatus getObjectClick(int position) {
        return listFilter.get(position);
    }

    @Override
    public int getCount() {
        return listFilter.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder lViewHolder;
        if (view == null) {
            lViewHolder = new ViewHolder();
            view = layoutInflater.inflate(android.R.layout.simple_list_item_single_choice, viewGroup, false);
            lViewHolder.name = (CheckedTextView) view.findViewById(android.R.id.text1);
            view.setTag(lViewHolder);
        } else {
            lViewHolder = (ViewHolder) view.getTag();
        }
        lViewHolder.name.setText(listFilter.get(i).name);
        return view;
    }

    static class ViewHolder {
        CheckedTextView name;
    }
    public void updateList(List<IdStatus> newlist) {
        listFilter.clear();
        listFilter.addAll(newlist);
        this.notifyDataSetChanged();
    }
}