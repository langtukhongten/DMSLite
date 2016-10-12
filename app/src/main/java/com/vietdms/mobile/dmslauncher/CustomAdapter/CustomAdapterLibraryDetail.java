package com.vietdms.mobile.dmslauncher.CustomAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.vietdms.mobile.dmslauncher.R;

import java.util.List;

import CommonLib.LibraryDetail;

/**
 * Created by Admin on 8/1/2016.
 */
public class CustomAdapterLibraryDetail extends BaseAdapter {
    private static final int IMAGE = 0, PDF = 1, VIDEO = 2;
    private LayoutInflater layoutInflater;
    private List<LibraryDetail> list;
    private Context context;

    public CustomAdapterLibraryDetail(Context context, List<LibraryDetail> list) {
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.list = list;
    }

    public LibraryDetail getObjectClick(int position) {
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
        switch (list.get(i).type) {
            case IMAGE:
                lViewHolder.imageInListView.setImageResource(R.drawable.image_btn);
                Glide.with(context).load(list.get(i).url).error(R.drawable.image_btn).override(200, 200).into(lViewHolder.imageInListView);
                break;
            case PDF:
                lViewHolder.imageInListView.setImageResource(R.drawable.pdf_btn);
                //Glide.with(context).load(list.get(i).url).error(R.drawable.pdf_btn).override(200, 200).into(lViewHolder.imageInListView);
                break;
            case VIDEO:
                lViewHolder.imageInListView.setImageResource(R.drawable.youtube_btn);
                Glide.with(context).load(getThumbUrlYoutube(list.get(i).url)).error(R.drawable.youtube_btn).override(200, 200).into(lViewHolder.imageInListView);

                break;
            default:
                break;
        }

        return view;
    }

    private String getThumbUrlYoutube(String url) {
        String thumb = "http://img.youtube.com/vi/";
        try {
            return thumb + url.split("=")[1] + "/1.jpg";
        } catch (Exception e) {
            e.printStackTrace();
            return url;
        }
    }

    static class ViewHolder {
        TextView textInListView, badgeView;
        ImageView imageInListView;
    }
}
