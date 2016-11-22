package com.vietdms.mobile.dmslauncher.CustomAdapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.google.android.gms.vision.text.Text;
import com.vietdms.mobile.dmslauncher.R;

import java.text.CollationElementIterator;
import java.util.ArrayList;

import CommonLib.OrderDetail;
import CommonLib.Utils;

/**
 * Created by Admin on 5/25/2016.
 */
public class OrderListProductAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<OrderDetail> OrderDetails;
    private static LayoutInflater inflater = null;

    public OrderListProductAdapter(Activity a, ArrayList<OrderDetail> OrderDetails) {
        activity = a;
        this.OrderDetails = OrderDetails;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        View vi = convertView;
        if (convertView == null)
            vi = inflater.inflate(R.layout.list_order_product, null);
        LinearLayout lyItem = (LinearLayout)vi.findViewById(R.id.layoutItemOrderDetail);
        TextView name = (TextView) vi.findViewById(R.id.row_product_name); // title
        TextView no = (TextView) vi.findViewById(R.id.row_product_no); // artist name
        TextView stt = (TextView) vi.findViewById(R.id.row_product_txtSTT); // artist name
        TextView quantity = (TextView) vi.findViewById(R.id.row_product_quantity); // duration
        TextView price = (TextView) vi.findViewById(R.id.row_product_price); // thumb image
        TextView promotion = (TextView) vi.findViewById(R.id.row_promotion);// promotion
        TextView percent = (TextView) vi.findViewById(R.id.row_discount_percent);//discount percent
        TextView dis_amount = (TextView) vi.findViewById(R.id.row_discount_amount);//discount amount
        TextView amount = (TextView) vi.findViewById(R.id.row_amount);//amount
        OrderDetail po;
        po = OrderDetails.get(position);

        // Setting all values in listview

        name.setText(po.name);
        promotion.setText(po.promotionNo_+"  ");
        no.setText(po.itemNo_);
        quantity.setText(po.quantity + "");
        if(po.itemType==1)
        {
            lyItem.setBackgroundColor(Color.CYAN);
            price.setTextColor(Color.BLUE);
            price.setText(R.string.freeItem);
        }
        else
        {
            lyItem.setBackgroundColor(Color.WHITE);
            price.setTextColor(Color.BLACK);
            price.setText(Utils.formatFloat(po.unitprice));
        }
        if(po.discountPercent > 0)
        {
            percent.setTextColor(Color.RED);
            percent.setText("CK: " + Utils.formatFloat(po.discountPercent) + "%");
        }
        else {
            percent.setTextColor(Color.BLACK);
            percent.setText("-");
        }
        if(po.discountAmount>0)
        {
            dis_amount.setTextColor(Color.RED);
            dis_amount.setText("Tiền CK: " + Utils.formatFloat(po.discountAmount));
        }
        else {
            dis_amount.setTextColor(Color.BLACK);
            dis_amount.setText("-");
        }
        amount.setText(Utils.formatFloat(getAmount(po)));
        int istt = position + 1;
        stt.setText(istt + ".");
        return vi;
    }

    private float getAmount(OrderDetail detail) {
        float result = ((detail.quantity * detail.unitprice) - detail.discountAmount) *((100-detail.discountPercent)/100);
        return result;
    }

    public void setItems(ArrayList<OrderDetail> list) {
        this.OrderDetails = list;
    }
}