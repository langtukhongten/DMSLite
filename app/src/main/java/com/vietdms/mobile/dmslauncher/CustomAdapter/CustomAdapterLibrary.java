package com.vietdms.mobile.dmslauncher.CustomAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.vietdms.mobile.dmslauncher.MyMethod;
import com.vietdms.mobile.dmslauncher.R;

import java.util.ArrayList;
import java.util.List;

import CommonLib.LibraryGroup;
import CommonLib.LocalDB;
import CommonLib.SurveyHeader;

/**
 * Created by Admin on 8/1/2016.
 */
public class CustomAdapterLibrary extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private List<LibraryGroup> list;
    private Context context;

    public CustomAdapterLibrary(Context context, List<LibraryGroup> list) {
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.list = list;
    }

    public LibraryGroup getObjectClick(int position) {
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
            view = layoutInflater.inflate(R.layout.custom_gridview_menu, viewGroup, false);
            lViewHolder.textInListView = (TextView) view.findViewById(R.id.textView);
            lViewHolder.imageInListView = (ImageView) view.findViewById(R.id.imageView);
            lViewHolder.badgeView = (TextView) view.findViewById(R.id.bagde_view);
            view.setTag(lViewHolder);
        } else {
            lViewHolder = (ViewHolder) view.getTag();
        }
        lViewHolder.textInListView.setText(list.get(i).description);
        Glide.with(context).load(list.get(i).url).error(R.drawable.library_btn).override(200, 200).into(lViewHolder.imageInListView);
        return view;
    }

    static class ViewHolder {
        TextView textInListView, badgeView;
        ImageView imageInListView;
    }
}
