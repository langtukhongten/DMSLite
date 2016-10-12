package com.vietdms.mobile.dmslauncher.Fragment;

/**
 * Created by Chu Tien on 4/19/2016.
 */

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vietdms.mobile.dmslauncher.R;


/**
 * Created by Chu Tien on 4/19/2016.
 */
public class SalesFragment extends Fragment {


    private Context context;
    private TextView txtSaleTwoMonthAverageOverOrder, txtThisMonthOverOrder, txtSKU, txtFirstMonth, txtSecondMonth, txtThreeMonth, txtValueFirstMonth, txtValueSecondMonth, txtValueThreeMonth;

    public SalesFragment() {
    }

    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sales, container, false);
        getId(v);
        event(v);
        return v;
    }

    private void event(View v) {
    }


    private void getId(View v) {
        context = getContext();
        txtSaleTwoMonthAverageOverOrder = (TextView) v.findViewById(R.id.txtSaleTwoMonthAverageOverOrder);
        txtThisMonthOverOrder = (TextView) v.findViewById(R.id.txtThisMonthOverOrder);
        txtSKU = (TextView) v.findViewById(R.id.txtSKU);
        txtFirstMonth = (TextView) v.findViewById(R.id.txtFirstMonth);
        txtSecondMonth = (TextView) v.findViewById(R.id.txtSecondMonth);
        txtThreeMonth = (TextView) v.findViewById(R.id.txtThreeMonth);
        txtValueFirstMonth = (TextView) v.findViewById(R.id.txtValueFirstMonth);
        txtValueSecondMonth = (TextView) v.findViewById(R.id.txtValueSecondMonth);
        txtValueThreeMonth = (TextView) v.findViewById(R.id.txtValueThreeMonth);
    }


}
