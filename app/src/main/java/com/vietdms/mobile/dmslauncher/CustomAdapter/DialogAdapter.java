package com.vietdms.mobile.dmslauncher.CustomAdapter;

/**
 * Created by ${LTG} on ${10/12/1994}.
 */


import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vietdms.mobile.dmslauncher.R;


/**
 * @author alessandro.balocco
 */
public class DialogAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private Byte status;

    public DialogAdapter(Context context, Byte status) {
        layoutInflater = LayoutInflater.from(context);
        this.status = status;
    }

    @Override
    public int getCount() {
        if (status == 1)
            return 4;
        else return 0;
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
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.dialog_menu_custom, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.button = (TextView) convertView.findViewById(R.id.button);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Context context = parent.getContext();
        switch (position) {
            case 0:
                viewHolder.button.setText(context.getString(R.string.order_completed));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    viewHolder.button.setCompoundDrawablesWithIntrinsicBounds(context.getDrawable(R.drawable.success_btn), null, null, null);
                } else
                    viewHolder.button.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context, R.drawable.success_btn), null, null, null);

                break;
            case 1:
                viewHolder.button.setText(context.getString(R.string.order_cancel));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    viewHolder.button.setCompoundDrawablesWithIntrinsicBounds(context.getDrawable(R.drawable.cancel_black_btn), null, null, null);
                } else
                    viewHolder.button.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context, R.drawable.cancel_black_btn), null, null, null);
                break;
            case 2:
                viewHolder.button.setText(context.getString(R.string.other));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    viewHolder.button.setCompoundDrawablesWithIntrinsicBounds(context.getDrawable(R.drawable.more_btn), null, null, null);
                } else
                    viewHolder.button.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context, R.drawable.more_btn), null, null, null);
                break;
            default:
                viewHolder.button.setText(context.getString(R.string.back));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    viewHolder.button.setCompoundDrawablesWithIntrinsicBounds(context.getDrawable(R.drawable.back_black_btn), null, null, null);
                } else
                    viewHolder.button.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context, R.drawable.back_black_btn), null, null, null);
                break;
        }

        return convertView;
    }

    static class ViewHolder {
        TextView button;
    }
}
