package com.vietdms.mobile.dmslauncher.Forms;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.SearchView;

import com.bumptech.glide.util.Util;
import com.vietdms.mobile.dmslauncher.CustomClass.LayoutLoadingManager;
import com.vietdms.mobile.dmslauncher.Home;
import com.vietdms.mobile.dmslauncher.MyMethod;
import com.vietdms.mobile.dmslauncher.R;
import com.vietdms.mobile.dmslauncher.RecycleView.RecyclerItemClickListener;
import com.vietdms.mobile.dmslauncher.RecycleView.RecyclerViewAdapterCustomer;
import com.vietdms.mobile.dmslauncher.SwipeRefreshLayoutBottom;
import com.vietdms.mobile.dmslauncher.databinding.ActivitySelectCustomerBinding;


import java.util.ArrayList;


import CommonLib.Customer;
import CommonLib.EventPool;
import CommonLib.EventType;


public class SelectCustomer extends AppCompatActivity implements RecyclerItemClickListener.OnItemClickListener {
    private static final String TAG = "SelectCustomer";
    ActivitySelectCustomerBinding binding;
    private ArrayList<Customer> customerArrayList;
    private RecyclerViewAdapterCustomer adapterCustomer;
    private Handler handler;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            EventType.EventBase event = EventPool.view().deQueue();
            try {
                while (event != null) {
                    processEvent(event);
                    event = EventPool.view().deQueue();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private int lastRowIdCustomer = -1;
    private String filterCustomer;

    private void processEvent(EventType.EventBase event) {
        switch (event.type) {
            case LoadAllCustomers:
                EventType.EventLoadAllCustomersResult customersResult = (EventType.EventLoadAllCustomersResult) event;
                if (customersResult.success) {
                    customerArrayList.addAll(customersResult.arrCustomer);
                    lastRowIdCustomer = customersResult.arrCustomer.get(customersResult.arrCustomer.size() - 1).id;
                } else {
                    MyMethod.showToast(binding,this, customersResult.message);

                }
                adapterCustomer.setItems(customerArrayList);
                adapterCustomer.notifyDataSetChanged();
                LayoutLoadingManager.Show_OffLoading(binding.SelectCustomerLoadingView);
                break;

            default:
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_select_customer);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("OnFire"));
        Log.w(TAG, "registerReceiver");
        handler = new Handler();
        init();
        getData();
    }

    private void getData() {
        Log.w(TAG, "getData");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                LayoutLoadingManager.Show_OnLoading(binding.SelectCustomerLoadingView, getString(R.string.load_customer), 30);
                EventPool.control().enQueue(new EventType.EventLoadAllCustomersRequest(filterCustomer, lastRowIdCustomer));
            }
        }, 500);

    }

    private void init() {

        LinearLayoutManager managerCustomer = new LinearLayoutManager(this);
        managerCustomer.setOrientation(LinearLayoutManager.VERTICAL);
        binding.recyclerSelectCustomer.setHasFixedSize(true);
        binding.recyclerSelectCustomer.setLayoutManager(managerCustomer);
        customerArrayList = new ArrayList<>();
        adapterCustomer = new RecyclerViewAdapterCustomer(customerArrayList, this);
        binding.recyclerSelectCustomer.setAdapter(adapterCustomer);
        binding.recyclerSelectCustomer.addOnItemTouchListener(new RecyclerItemClickListener(this, this));
        binding.swipeSelectCustomer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                // get the new data from you data source
                // TODO : request data here
                reFreshCustomer();
                // our swipeRefreshLayout needs to be notified when the data is returned in order for it to stop the animation
            }
        });
        // sets the colors used in the refresh animation
        binding.swipeSelectCustomer.setColorSchemeResources(R.color.colorAlert, R.color.colorBlue,
                R.color.colorGreen, R.color.colorOranger);

        binding.loadMoreSelectCustomer.setOnRefreshListener(new SwipeRefreshLayoutBottom.OnRefreshListener() {

            @Override
            public void onRefresh() {
                // get the new data from you data source
                // TODO : request data here
                loadMoreCustomer();
                // our swipeRefreshLayout needs to be notified when the data is returned in order for it to stop the animation
            }
        });
        // sets the colors used in the refresh animation
        binding.loadMoreSelectCustomer.setColorSchemeResources(R.color.colorAlert, R.color.colorBlue,
                R.color.colorGreen, R.color.colorOranger);

        binding.svSelectCustomer.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }


            Handler mHandler = new Handler();

            @Override
            public boolean onQueryTextChange(final String newText) {
                filterCustomer = newText;
                mHandler.removeCallbacksAndMessages(null);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Put your call to the server here (with mQueryString)
                        try {
                            //if (newText.substring(newText.length() - 1).contains(" ")) {
                            reFreshCustomerNotLoading();
                            //}
                        } catch (Exception ignored) {
                        }
                    }
                }, 1000);
                return true;

            }
        });

        ViewTreeObserver vto = binding.svSelectCustomer.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                int heightToolbar = binding.toolbarSelectCustomer.getHeight();
                binding.recyclerSelectCustomer.setPadding(0, heightToolbar, 0, 0);
                ViewTreeObserver obs = binding.svSelectCustomer.getViewTreeObserver();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    obs.removeOnGlobalLayoutListener(this);
                } else {
                    obs.removeGlobalOnLayoutListener(this);
                }
            }

        });
    }

    private void loadMoreCustomer() {
        adapterCustomer.notifyDataSetChanged();

        //load online
        EventPool.control().enQueue(new EventType.EventLoadAllCustomersRequest(filterCustomer, lastRowIdCustomer));

        Home.swipeCustomerBottom.setRefreshing(false);
    }

    private void reFreshCustomerNotLoading() {
        customerArrayList.clear();
        adapterCustomer.notifyDataSetChanged();
        EventPool.control().enQueue(new EventType.EventLoadCustomersRequest(Home.nowRoute, Home.filterCustomer, -1, Home.nowIdCustomer));
        binding.swipeSelectCustomer.setRefreshing(false);
    }

    private synchronized void reFreshCustomer() {
        try {
            customerArrayList.clear();
            adapterCustomer.notifyDataSetChanged();
            //load online
            LayoutLoadingManager.Show_OnLoading(binding.SelectCustomerLoadingView, getString(R.string.load_customer), 30);
            EventPool.control().enQueue(new EventType.EventLoadAllCustomersRequest(filterCustomer, -1));
            binding.swipeSelectCustomer.setRefreshing(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(View view, int position) {

        Customer selected = customerArrayList.get(position);
        Intent resultIntent = new Intent();
        resultIntent.putExtra("Customer", selected.id);
        resultIntent.putExtra("Name",selected.name);

            LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
            //unregisterReceiver(receiver);
            Log.w(TAG, "unregisterReceiver");

        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.w(TAG, "onStop");
        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
            //unregisterReceiver(receiver);
            Log.w(TAG, "unregisterReceiver");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
