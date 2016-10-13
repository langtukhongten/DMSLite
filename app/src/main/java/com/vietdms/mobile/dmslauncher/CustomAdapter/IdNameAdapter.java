package com.vietdms.mobile.dmslauncher.CustomAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.vietdms.mobile.dmslauncher.R;

import java.util.List;

import CommonLib.Status;

/**
 * Created by chutien on 10/13/16.
 */


public class IdNameAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private List<Status> list;
    private Context context;

    public IdNameAdapter(Context context, List<Status> list) {
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.list = list;
    }

    public Status getObjectClick(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
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
        lViewHolder.name.setText(list.get(i).name);
        return view;
    }

    static class ViewHolder {
        CheckedTextView name;
    }
}
