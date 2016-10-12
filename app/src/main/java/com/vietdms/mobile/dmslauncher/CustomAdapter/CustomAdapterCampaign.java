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

import CommonLib.LocalDB;
import CommonLib.SurveyCampaign;
import CommonLib.SurveyLine;

/**
 * Created by Admin on 8/23/2016.
 */
public class CustomAdapterCampaign extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private List<SurveyCampaign> list;
    private Context context;

    public CustomAdapterCampaign(Context context, List<SurveyCampaign> list) {
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.list = list;
    }

    public SurveyCampaign getObjectClick(int position) {
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
        SurveyCampaign campaign = list.get(i);
        lViewHolder.textInListView.setText(campaign.name);
        Glide.with(context).load(campaign.description).error(R.drawable.survey_btn).override(200, 200).into(lViewHolder.imageInListView);


        int countUnsent = LocalDB.inst().countResultUnSent(campaign.id);
        if (countUnsent > 0) {
            lViewHolder.badgeView.setVisibility(View.VISIBLE);
            lViewHolder.badgeView.setText(countUnsent + "");
        } else lViewHolder.badgeView.setVisibility(View.INVISIBLE);
        return view;
    }

    static class ViewHolder {
        TextView textInListView, badgeView;
        ImageView imageInListView;
    }
}
