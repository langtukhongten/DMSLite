package com.vietdms.mobile.dmslauncher.CustomAdapter;

/**
 * Created by ${LTG} on ${10/12/1994}.
 */


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;

import com.vietdms.mobile.dmslauncher.R;


/**
 * @author notme
 */
public class DialogLockAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;

    public DialogLockAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return 1;
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
            convertView = layoutInflater.inflate(R.layout.dialog_block, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.editText = (EditText) convertView.findViewById(R.id.edit_pass);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.image_pass);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.imageView.setImageResource(R.drawable.key_white_btn);
        viewHolder.editText.requestFocus();


        return convertView;
    }

    static class ViewHolder {
        EditText editText;
        ImageView imageView;
    }
}
