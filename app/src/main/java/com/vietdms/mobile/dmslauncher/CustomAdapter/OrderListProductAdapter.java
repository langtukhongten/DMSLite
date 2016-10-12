package com.vietdms.mobile.dmslauncher.CustomAdapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.vietdms.mobile.dmslauncher.R;

import java.util.ArrayList;

import CommonLib.OrderDetail;
import CommonLib.Utils;

/**
 * Created by Admin on 5/25/2016.
 */
public class OrderListProductAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<OrderDetail> OrderDetails;
    private static LayoutInflater inflater=null;

    public OrderListProductAdapter(Activity a, ArrayList<OrderDetail> OrderDetails ) {
        activity = a;
        this.OrderDetails=OrderDetails;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return OrderDetails.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.list_order_product, null);

        TextView name = (TextView)vi.findViewById(R.id.row_product_name); // title
        TextView no = (TextView)vi.findViewById(R.id.row_product_no); // artist name
        TextView stt = (TextView)vi.findViewById(R.id.row_product_txtSTT); // artist name
        TextView quantity = (TextView)vi.findViewById(R.id.row_product_quantity); // duration
        TextView price=(TextView) vi.findViewById(R.id.row_product_price); // thumb image

      OrderDetail po;
        po = OrderDetails.get(position);

        // Setting all values in listview
        name.setText(po.name);
        no.setText(po.itemNo_);
        quantity.setText(po.quantity+"");
        price.setText(Utils.formatFloat(po.unitprice)+" VNĐ");
        int istt = position+1;
        stt.setText(istt+".");
        return vi;
    }
    public void setItems(ArrayList<OrderDetail> list) {
        this.OrderDetails = list;
    }
}