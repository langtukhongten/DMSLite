package com.vietdms.mobile.dmslauncher.CustomAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.vietdms.mobile.dmslauncher.MyMethod;
import com.vietdms.mobile.dmslauncher.R;

import java.util.List;

/**
 * Created by Chu Tien on 4/13/2016.
 */
public class CustomAdapterMenu extends BaseAdapter {
    private LayoutInflater layoutinflater;
    private List<String> listMenu;
    private Context context;

    public CustomAdapterMenu(Context context, List<String> customizedListView) {
        this.context = context;
        layoutinflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listMenu = customizedListView;
    }

    public String getObjectClick(int position) {
        return listMenu.get(position);
    }

    @Override
    public int getCount() {
        return listMenu.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder listViewHolder;
        if (convertView == null) {
            listViewHolder = new ViewHolder();
            convertView = layoutinflater.inflate(R.layout.custom_gridview_menu, parent, false);
            listViewHolder.textInListView = (TextView) convertView.findViewById(R.id.textView);
            listViewHolder.imageInListView = (ImageView) convertView.findViewById(R.id.imageView);
            listViewHolder.badgeView = (TextView) convertView.findViewById(R.id.bagde_view);
            convertView.setTag(listViewHolder);
        } else {
            listViewHolder = (ViewHolder) convertView.getTag();
        }
        listViewHolder.textInListView.setText(listMenu.get(position));
        listViewHolder.imageInListView.setImageDrawable(MyMethod.getIconMenu(listMenu.get(position), context));
        if (MyMethod.getBadgeNumMenu(listMenu.get(position)) > 0) {
            listViewHolder.badgeView.setVisibility(View.VISIBLE);
            listViewHolder.badgeView.setText(MyMethod.getBadgeNumMenu(listMenu.get(position)) + "");
        } else {
            listViewHolder.badgeView.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }

    static class ViewHolder {
        TextView textInListView, badgeView;
        ImageView imageInListView;
    }
}
