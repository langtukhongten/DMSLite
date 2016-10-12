package com.vietdms.mobile.dmslauncher.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.vietdms.mobile.dmslauncher.CustomAdapter.CustomAdapterListIssue;
import com.vietdms.mobile.dmslauncher.CustomAdapter.CustomAdapterListTransaction;
import com.vietdms.mobile.dmslauncher.GetSet.InfoTransaction;
import com.vietdms.mobile.dmslauncher.GetSet.Issues;
import com.vietdms.mobile.dmslauncher.R;

import java.util.ArrayList;

/**
 * Created by Chu Tien on 4/19/2016.
 */
public class TransactionFragment extends Fragment {


    private Context context;
    private ListView listTransactions, listIssue;
    private ArrayList<InfoTransaction> infoTransactionArrayList;
    private ArrayList<Issues> issuesArrayList;
    private CustomAdapterListTransaction adapterListTransaction;
    private CustomAdapterListIssue adapterListIssue;

    public TransactionFragment() {
    }

    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_transaction, container, false);
        getId(v);
        event();
        return v;
    }

    private void event() {
        infoTransactionArrayList = new ArrayList<>();
        issuesArrayList = new ArrayList<>();
        //Test
        InfoTransaction infoTransaction = new InfoTransaction();
        infoTransaction.day = "10/12/2016";
        infoTransaction.content = "Đi giao hàng cho Google";
        infoTransaction.status = 1;
        infoTransaction.staff = "Nguyễn Xuân Tiên";
        infoTransactionArrayList.add(infoTransaction);
        Issues issues = new Issues();
        issues.day = "10/12/2016";
        issues.content = "Đi giao hàng cho FPT";
        issues.status = 2;
        issues.staff = "Nguyễn Hà Đông";
        issues.detail = "Không bay được";
        issuesArrayList.add(issues);
        adapterListTransaction = new CustomAdapterListTransaction(
                this.getActivity(),
                R.layout.list_transaction_info,
                infoTransactionArrayList);
        listTransactions.setAdapter(adapterListTransaction);
        listTransactions.setScrollingCacheEnabled(false);
        adapterListIssue = new CustomAdapterListIssue(
                this.getActivity(),
                R.layout.list_issue,
                issuesArrayList);
        listIssue.setAdapter(adapterListIssue);
        listIssue.setScrollingCacheEnabled(false);
    }


    private void getId(View v) {
        context = getContext();
        listIssue = (ListView) v.findViewById(R.id.dialog_listview_issue);
        listTransactions = (ListView) v.findViewById(R.id.dialog_listview_transaction);

    }


}
