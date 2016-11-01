package com.vietdms.mobile.dmslauncher.CustomAdapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.vietdms.mobile.dmslauncher.MyMethod;
import com.vietdms.mobile.dmslauncher.R;

import java.util.ArrayList;

import CommonLib.Promotion;
import CommonLib.Utils;

/**
 * Created by chutien on 11/1/16.
 */

public class CustomAdapterPromotion extends ArrayAdapter<Promotion> {
    Context context = null;
    ArrayList<Promotion> arrayList = null;
    int layoutId;

    public CustomAdapterPromotion(Context context, int resource, ArrayList<Promotion> arrayList) {
        super(context, resource);
        this.context = context;
        this.layoutId = resource;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View myView;
        final Promotion promotion = arrayList.get(position);
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            myView = inflater.inflate(layoutId, null);
            holder = new ViewHolder();
            holder.promotion_name = (TextView)
                    myView.findViewById(R.id.promotion_name);
            holder.promotion_no = (TextView)
                    myView.findViewById(R.id.promotion_no);
            holder.promotion_price = (EditText)
                    myView.findViewById(R.id.promotion_input_price);
            holder.promotion_quantity = (EditText)
                    myView.findViewById(R.id.promotion_input_quantity);

            myView.setTag(holder);
        } else {
            myView = convertView;
            holder = (ViewHolder) myView.getTag();

        }
        if (arrayList.size() > 0 && position >= 0) {


            holder.promotion_no.setText(promotion.no_);
            holder.promotion_name.setText(promotion.name);
            holder.promotion_quantity.setText(promotion.quantity+"");
            holder.promotion_price.setText(promotion.price+"");
        }

        return myView;
    }

    static class ViewHolder {
        TextView promotion_no, promotion_name;
        EditText promotion_price, promotion_quantity;
    }
}
