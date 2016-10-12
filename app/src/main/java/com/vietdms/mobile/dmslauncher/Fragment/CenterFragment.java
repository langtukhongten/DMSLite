package com.vietdms.mobile.dmslauncher.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;


import com.vietdms.mobile.dmslauncher.CustomAdapter.CustomAdapterGripView;
import com.vietdms.mobile.dmslauncher.CustomView.LoadingView;
import com.vietdms.mobile.dmslauncher.Home;
import com.vietdms.mobile.dmslauncher.MyMethod;
import com.vietdms.mobile.dmslauncher.R;


import java.lang.reflect.Field;
import java.util.Timer;

import CommonLib.PhoneState;
import cat.ereza.customactivityoncrash.CustomActivityOnCrash;

//import jp.wasabeef.blurry.Blurry;

/**
 * Created by DMSv4 on 12/3/2015.
 */
public class CenterFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener, AbsListView.OnScrollListener, Animation.AnimationListener {
    private static final String PACKAGE = "package:";
    private static final int OPENWIFI = 1001;
    private static final int OPEN3G = 1002;
    private static final int OPENGPS = 1003;
    private static final int OPENAIR = 1004;
    private Timer timer = new Timer();
    private Context context;
    private Animation animBounce;

    public CenterFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Home.bindingCenter = DataBindingUtil.inflate(inflater, R.layout.fragment_center, container, false);
        View v = Home.bindingCenter.getRoot();
        context = getContext();
        event();
        firstRun();
        return v;
    }


    private void firstRun() {
        if (PhoneState.inst().is3G() == 1 || PhoneState.inst().isWifi() == 1) {
            Home.bindingCenter.btnTurnOn3G.setImageResource(R.drawable.internet_on_btn);
        } else {
            Home.bindingCenter.btnTurnOn3G.setImageResource(R.drawable.internet_off_btn);
        }
        if (PhoneState.inst().isAirplaneMode() == 1) {
            Home.bindingCenter.btnTurnOffAirMode.setImageResource(R.drawable.airmode_on_btn);
        } else {
            Home.bindingCenter.btnTurnOffAirMode.setImageResource(R.drawable.air_mode_off_btn);
        }
        if (PhoneState.inst().isGPS() == 1) {
            Home.bindingCenter.btnTurnOnGps.setImageResource(R.drawable.gps_on_btn);
        } else {
            Home.bindingCenter.btnTurnOnGps.setImageResource(R.drawable.gps_off_btn);
        }

    }

    private void event() {
        animBounce = AnimationUtils.loadAnimation(context,
                R.anim.bounce);
        Home.bindingCenter.relaLayoutCenter.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Home.bindingHome.txtTile.setText("Đổi hình nền");
                MyMethod.cacheBackground = null;
                MyMethod.showChangeWallpaper(context);
                return false;
            }
        });
        Home.bindingCenter.gridListRecentApp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return event.getAction() == MotionEvent.ACTION_MOVE;
            }
        });
        Home.adapterRecentGripView = new CustomAdapterGripView(context, Home.recentItems);
        Home.bindingCenter.gridListRecentApp.setAdapter(Home.adapterRecentGripView);
        Home.bindingCenter.searchBoxs.gridListAppSearchBox.setAdapter(Home.adapterRecentGripView);
        Home.bindingCenter.gridListRecentApp.setOnItemLongClickListener(this);
        Home.bindingCenter.gridListRecentApp.setOnItemClickListener(this);

        try {
            Field l = Home.bindingCenter.listApps.swipeContainer.getClass().getDeclaredField("mCircleView");
            l.setAccessible(true);
            ImageView imgL = (ImageView) l.get(Home.bindingCenter.listApps.swipeContainer);
            imgL.setImageResource(R.drawable.ic_search_holo_light);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        int id = Home.bindingCenter.searchBoxs.searchBox.getContext()
                .getResources()
                .getIdentifier("android:id/search_src_text", null, null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            Home.bindingCenter.relaMain.setBackgroundColor(Color.TRANSPARENT);
        }
        Home.bindingCenter.txtNotifyFragment.setOnClickListener(this);
        Home.bindingHome.txtTile.setText(context.getText(R.string.home));
        Home.bindingCenter.view3G.setOnClickListener(this);
        Home.bindingCenter.viewGPS.setOnClickListener(this);
        Home.bindingCenter.viewRestart.setOnClickListener(this);
        Home.bindingCenter.viewAir.setOnClickListener(this);
        Home.bindingCenter.btnCall.setOnClickListener(this);
        Home.bindingCenter.btnSmS.setOnClickListener(this);
        Home.bindingCenter.btnEmail.setOnClickListener(this);
        Home.bindingCenter.btnMenu.setOnClickListener(this);
        Home.bindingCenter.btnLock.setOnClickListener(this);
        Home.bindingCenter.btnTurnOffAirMode.setOnClickListener(this);
        Home.bindingCenter.btnTurnOnGps.setOnClickListener(this);
        Home.bindingCenter.btnTurnOn3G.setOnClickListener(this);
        Home.bindingHome.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (Home.adapterGripView != null) Home.adapterGripView.getFilter().filter(newText);
                return false;
            }
        });
        Home.bindingCenter.listApps.swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                // get the new data from you data source
                // TODO : request data here
                openSearchBox();
                // our swipeRefreshLayout needs to be notified when the data is returned in order for it to stop the animation
            }
        });
        // sets the colors used in the refresh animation
        Home.bindingCenter.listApps.swipeContainer.setColorSchemeResources(R.color.colorAlert, R.color.colorBlue,
                R.color.colorGreen, R.color.colorOranger);
        Home.bindingCenter.searchBoxs.llSearchBox.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) MyMethod.setGone(Home.bindingCenter.searchBoxs.llSearchBox);
            }
        });
        Home.bindingCenter.searchBoxs.imgBackSearchBox.setOnClickListener(this);
        Home.bindingCenter.searchBoxs.imgSearchGooglePlay.setOnClickListener(this);
        Home.bindingCenter.searchBoxs.searchBox.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    Home.bindingCenter.searchBoxs.gridListAppSearchBox.setAdapter(Home.adapterRecentGripView);
                } else {
                    MyMethod.setGone(Home.bindingCenter.searchBoxs.titleAppRecent);
                    if (Home.adapterGripView != null)
                        Home.adapterGripView.getFilter().filter(newText);
                    Home.bindingCenter.searchBoxs.gridListAppSearchBox.setAdapter(Home.adapterGripView);
                }
                return false;
            }
        });
        Home.bindingCenter.searchBoxs.gridListAppSearchBox.setOnItemLongClickListener(this);
        Home.bindingCenter.searchBoxs.gridListAppSearchBox.setOnItemClickListener(this);
        Home.bindingCenter.searchBoxs.gridListAppSearchBox.setOnScrollListener(this);
        Home.bindingCenter.listApps.gridListApp.setOnScrollListener(this);
        animBounce.setAnimationListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtNotifyFragment:
                if (!Home.bindingCenter.txtNotifyFragment.getText().toString().equals("Chưa có thông báo mới."))
                    Home.bindingHome.viewpager.setCurrentItem(2);
                break;
            case R.id.img_back_search_box:
                Home.bindingHome.appBar.setVisibility(View.VISIBLE);
                MyMethod.hideKeyboardFromView(context, Home.bindingCenter.searchBoxs.searchBox);
                showLayout(Layouts.ListApp);
                break;
            case R.id.img_search_google_play:
                MyMethod.openCHPlay(context,Home.bindingCenter.searchBoxs.searchBox.getQuery().toString());
                break;
            case R.id.viewAir:
                if (PhoneState.inst().isAirplaneMode() == 1) {
                    Home.isAppLockStop = true;
                    startActivityForResult(new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS), OPENAIR);
                }
                break;
            case R.id.view3G:
                if (PhoneState.inst().is3G() != 1 && PhoneState.inst().isWifi() != 1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage(context.getString(R.string.please_select_connect))
                            .setCancelable(false)
                            .setPositiveButton(context.getString(R.string.wifi), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Home.isAppLockStop = true;
                                    startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS), OPENWIFI);
                                    dialog.dismiss();
                                }
                            }).setNegativeButton(context.getString(R.string.threeG), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Home.isAppLockStop = true;
                            try {
                                Intent intent = new Intent(Intent.ACTION_MAIN);
                                intent.setComponent(new ComponentName("com.android.settings",
                                        "com.android.settings.Settings$DataUsageSummaryActivity"));
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivityForResult(intent, OPEN3G);
                            } catch (ActivityNotFoundException e) {
                                startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), OPEN3G);
                            }
                            dialog.dismiss();
                        }
                    }).setCancelable(true);
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                break;
            case R.id.viewGPS:
                if (PhoneState.inst().isGPS() != 1) {
                    Home.isAppLockStop = true;
                    startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), OPENGPS);
                }
                break;
            case R.id.viewRestart:
                MyMethod.restartApp(context);
                break;
            case R.id.btn_Call:
                MyMethod.callPhone(v.getContext());
                break;
            case R.id.btn_Menu:
                showLayout(Layouts.ListApp);
                if (Home.bindingHome.viewpager.getCurrentItem() != 0)
                    Home.bindingHome.viewpager.setCurrentItem(0);
                Home.adapterGripView = new CustomAdapterGripView(context, Home.allItems);
                Home.bindingCenter.listApps.gridListApp.setAdapter(Home.adapterGripView);
                Home.bindingCenter.listApps.gridListApp.setOnItemLongClickListener(this);
                Home.bindingCenter.listApps.gridListApp.setOnItemClickListener(this);
                break;
            case R.id.btn_SmS:
                MyMethod.showSms(v.getContext());
                break;
            case R.id.btn_Email:
                MyMethod.showGmail(v.getContext());
                break;
            case R.id.btn_Lock:
                MyMethod.lockDevice(context, Home.devicePolicyManager);
                break;
            default:
                break;
        }
    }



    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Uri packageUri = null;
        Intent uninstallIntent = null;
        switch (parent.getId()) {
            case R.id.gridListApp:
                packageUri = Uri.parse(PACKAGE + Home.adapterGripView.getObjectClick(position));
                uninstallIntent =
                        new Intent(Intent.ACTION_DELETE, packageUri);
                startActivity(uninstallIntent);
                break;
            case R.id.gridListRecentApp:
                packageUri = Uri.parse(PACKAGE + Home.adapterRecentGripView.getObjectClick(position));
                uninstallIntent =
                        new Intent(Intent.ACTION_DELETE, packageUri);
                startActivity(uninstallIntent);
                break;
            case R.id.gridListAppSearchBox:
                if (Home.bindingCenter.searchBoxs.searchBox.getQuery().toString().isEmpty()) {
                    packageUri = Uri.parse(PACKAGE + Home.adapterRecentGripView.getObjectClick(position));
                    uninstallIntent =
                            new Intent(Intent.ACTION_DELETE, packageUri);
                } else {
                    packageUri = Uri.parse(PACKAGE + Home.adapterGripView.getObjectClick(position));
                    uninstallIntent =
                            new Intent(Intent.ACTION_DELETE, packageUri);
                }
                Home.bindingCenter.searchBoxs.imgBackSearchBox.performClick();
                startActivity(uninstallIntent);

                break;
        }

        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //MyMethod.runApp(Home.allItems.get(position), Home.manager, view.getRootView().getContext());
        switch (parent.getId()) {
            case R.id.gridListApp:
                try {
                    Intent i = Home.manager.getLaunchIntentForPackage(Home.adapterGripView.getObjectClick(position));
                    context.startActivity(i);
                } catch (Exception e) {
                    Log.e("onItemClick", e.toString());
                }
                break;
            case R.id.gridListRecentApp:
                try {
                    Intent i = Home.manager.getLaunchIntentForPackage(Home.adapterRecentGripView.getObjectClick(position));
                    context.startActivity(i);
                } catch (Exception e) {
                    Log.e("onItemClick", e.toString());
                }
                break;
            case R.id.gridListAppSearchBox:
                try {
                    if (Home.bindingCenter.searchBoxs.searchBox.getQuery().toString().isEmpty()) {
                        Intent i = Home.manager.getLaunchIntentForPackage(Home.adapterRecentGripView.getObjectClick(position));
                        Home.bindingCenter.searchBoxs.imgBackSearchBox.performClick();
                        context.startActivity(i);
                    } else {
                        Intent i = Home.manager.getLaunchIntentForPackage(Home.adapterGripView.getObjectClick(position));
                        Home.bindingCenter.searchBoxs.imgSearchGooglePlay.performClick();
                        context.startActivity(i);
                    }

                } catch (Exception e) {
                    Log.e("onItemClick", e.toString());
                }
                break;
        }


    }

    private void openSearchBox() {
        Home.bindingCenter.searchBoxs.llSearchBox.startAnimation(animBounce);
        showLayout(Layouts.SearchBox);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (view.getId()) {
            case R.id.gridListApp:
                MyMethod.hideKeyboardFromView(context, Home.bindingHome.searchView);
                break;
            case R.id.gridListAppSearchBox:
                MyMethod.hideKeyboardFromView(context, Home.bindingCenter.searchBoxs.searchBox);
                break;
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == OPENWIFI) {
            MyMethod.showToast(context, context.getString(R.string.open_wifi_success));
            Home.isAppLockStop = false;
        }
        if (resultCode == Activity.RESULT_OK && requestCode == OPEN3G) {
            MyMethod.showToast(context, context.getString(R.string.open_3g_success));
            Home.isAppLockStop = false;
        }
        if (resultCode == Activity.RESULT_OK && requestCode == OPENGPS) {
            MyMethod.showToast(context, context.getString(R.string.open_gps_success));
            Home.isAppLockStop = false;
        }
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        Home.bindingCenter.listApps.swipeContainer.setRefreshing(false);
        Home.bindingCenter.searchBoxs.llSearchBox.clearAnimation();
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }


    private enum Layouts {
        Main, SearchBox, ListApp
    }

    private void showLayout(Layouts layout) {
        switch (layout) {
            case Main:
                break;
            case ListApp:
                Home.bindingCenter.searchBoxs.searchBox.setQuery("", false);
                Home.bindingCenter.listApps.swipeContainer.setEnabled(true);
                Home.bindingCenter.listApps.linearListApp.setVisibility(View.VISIBLE);
                Home.bindingCenter.searchBoxs.llSearchBox.setVisibility(View.GONE);
                Home.bindingHome.btnComeBack.setVisibility(View.GONE);
                Home.bindingHome.txtTile.setText(context.getString(R.string.list_app));
                Home.bindingCenter.relaLayoutCenter.setVisibility(View.GONE);
                Home.bindingCenter.listApps.linearListApp.setVisibility(View.VISIBLE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Home.bindingHome.txtTile.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context, android.R.drawable.ic_menu_search), null, null, null);

                }
                break;
            case SearchBox:
                Home.bindingCenter.listApps.swipeContainer.setEnabled(false);
                Home.bindingCenter.searchBoxs.llSearchBox.setVisibility(View.VISIBLE);
                Home.bindingHome.appBar.setVisibility(View.GONE);
                Home.bindingCenter.listApps.linearListApp.setVisibility(View.GONE);
                Home.bindingCenter.searchBoxs.searchBox.setIconified(false);
                break;
            default:
                break;
        }
    }
}
