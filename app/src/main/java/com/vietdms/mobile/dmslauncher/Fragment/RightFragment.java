package com.vietdms.mobile.dmslauncher.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.Headers;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.vietdms.mobile.dmslauncher.BuildConfig;
import com.vietdms.mobile.dmslauncher.CustomAdapter.ArrayUserAdapter;
import com.vietdms.mobile.dmslauncher.CustomAdapter.CustomAdapterGripView;
import com.vietdms.mobile.dmslauncher.CustomAdapter.CustomAdapterMenu;
import com.vietdms.mobile.dmslauncher.CustomAdapter.OrderListProductAdapter;
import com.vietdms.mobile.dmslauncher.CustomClass.LayoutLoadingManager;
import com.vietdms.mobile.dmslauncher.CustomClass.LayoutManagerObject;
import com.vietdms.mobile.dmslauncher.CustomClass.LayoutShow;
import com.vietdms.mobile.dmslauncher.CustomClass.NumberTextWatcher;
import com.vietdms.mobile.dmslauncher.CustomView.AnimDownloadProgressButton;
import com.vietdms.mobile.dmslauncher.CustomView.LoadingView;
import com.vietdms.mobile.dmslauncher.DeviceBluetooth;
import com.vietdms.mobile.dmslauncher.Forms.LibraryActivity;
import com.vietdms.mobile.dmslauncher.Forms.ManagerActivity;
import com.vietdms.mobile.dmslauncher.Forms.MapsActivity;
import com.vietdms.mobile.dmslauncher.Forms.ReportRoute;
import com.vietdms.mobile.dmslauncher.Forms.ReportTransactionEmployee;
import com.vietdms.mobile.dmslauncher.Forms.ReportTransactionGeneral;
import com.vietdms.mobile.dmslauncher.Forms.SurveyActivity;
import com.vietdms.mobile.dmslauncher.Forms.SurveyQAActivity;
import com.vietdms.mobile.dmslauncher.Forms.WebReport_Overview;
import com.vietdms.mobile.dmslauncher.Home;
import com.vietdms.mobile.dmslauncher.MyMethod;
import com.vietdms.mobile.dmslauncher.R;
import com.vietdms.mobile.dmslauncher.RecycleView.RecyclerItemClickListener;
import com.vietdms.mobile.dmslauncher.RecycleView.RecyclerViewAdapterApproval;
import com.vietdms.mobile.dmslauncher.RecycleView.RecyclerViewAdapterCustomer;
import com.vietdms.mobile.dmslauncher.RecycleView.RecyclerViewAdapterGCM;
import com.vietdms.mobile.dmslauncher.RecycleView.RecyclerViewAdapterOrder;
import com.vietdms.mobile.dmslauncher.RecycleView.RecyclerViewAdapterProduct;
import com.vietdms.mobile.dmslauncher.RecycleView.RecyclerViewAdapterProductEmployee;
import com.vietdms.mobile.dmslauncher.RecycleView.RecyclerViewAdapterProductOfOrder;
import com.vietdms.mobile.dmslauncher.RecycleView.RecyclerViewAdapterReasonNotOrder;
import com.vietdms.mobile.dmslauncher.RecycleView.RecyclerViewAdapterReportCheckIn;
import com.vietdms.mobile.dmslauncher.RecycleView.RecyclerViewAdapterTimeLine;
import com.vietdms.mobile.dmslauncher.RecycleView.RecyclerViewAdapterTransaction;
import com.vietdms.mobile.dmslauncher.RecycleView.RecyclerViewAdapterTransactionLine;
import com.vietdms.mobile.dmslauncher.Service.MessageService;
import com.vietdms.mobile.dmslauncher.SwipeRefreshLayoutBottom;
import com.zj.btsdk.BluetoothService;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Array;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import CommonLib.ApproVal;
import CommonLib.City;
import CommonLib.Const;
import CommonLib.County;
import CommonLib.Customer;
import CommonLib.EventPool;
import CommonLib.EventType;
import CommonLib.GCM;
import CommonLib.LocalDB;
import CommonLib.LocationDetector;
import CommonLib.Model;
import CommonLib.MyLocation;
import CommonLib.NotifyMessage;
import CommonLib.Order;
import CommonLib.OrderDetail;
import CommonLib.PhoneState;
import CommonLib.Product;
import CommonLib.ProductGroup;
import CommonLib.ReasonNotOrder;
import CommonLib.ReportCheckIn;
import CommonLib.Route;
import CommonLib.Status;
import CommonLib.SurveyCampaign;
import CommonLib.SurveyHeader;
import CommonLib.SurveyLine;
import CommonLib.SurveyResult;
import CommonLib.SystemLog;
import CommonLib.TimeLine;
import CommonLib.TrackingItem;
import CommonLib.Transaction;
import CommonLib.TransactionLine;
import CommonLib.UserInfo;
import CommonLib.Utils;
import CommonLib.WakeLock;
import cat.ereza.customactivityoncrash.CustomActivityOnCrash;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by DMSv4 on 12/3/2015.
 */
public class RightFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener, RecyclerItemClickListener.OnItemClickListener, AdapterView.OnItemSelectedListener, CalendarDatePickerDialogFragment.OnDateSetListener, GoogleMap.OnMarkerClickListener, View.OnKeyListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private static final int ACTION_PHOTO_STORE = 911;
    private static final int ACTION_PHOTO_CREATE_CUSTOMER = 199;
    private static final int ACTION_PHOTO_NOT_ORDER = 200;
    private static final int REQUEST_CONNECT_DEVICE = 909;
    private static final int REQUEST_ENABLE_BT = 908;
    private static final int OPENWIFI = 12345;
    private static final int OPEN3G = 12346;
    private static final int OPENGPS = 1123;
    private static final String TAG = "RightFragment";
    public static PowerManager.WakeLock mWakeLock;
    //SELECT ROUTE IN CREATE CUSTOMER
    private CheckBox cns, cnc, t2s, t2c, t3s, t3c, t4s, t4c, t5s, t5c, t6s, t6c, t7s, t7c;
    private Button btn_create_customer_route;
    private int countRealTime;//Đếm số lần realtime
    //Transaction Detail Header
    private TextView txtTransactionHeaderNameNo, txtTransactionHeaderTime, txtTransactionHeaderAddress, txtTransactionHeaderNote, txtTransactionHeaderPhone, txtTransactionHeaderStatus;
    //--
    private static final int ACTION_TAKE_CARMERA = 999;
    private static final int ACTION_MAP = 100;
    private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_name";
    public static GoogleMap googleMap;
    private EditText editName, editPass, editNote, editNoteOrder;
    private TextInputLayout inputLayoutName, inputLayoutPassword, inputLayoutNote, inputLayoutPasswordOld, inputLayoutPasswordNew, inputLayoutPasswordNewAgain;
    private LinearLayoutManager managerOrder = new LinearLayoutManager(getContext());
    private LinearLayoutManager managerReportCheckIn = new LinearLayoutManager(getContext());
    private LinearLayoutManager managerGCM = new LinearLayoutManager(getContext());
    private RecyclerView recyclerReportCheckIn, recyclerOrder, recyclerTransaction, recyclerCustomer,
            recyclerGCM, recyclerProduct, recyclerInventoryEmployee,
            recyclerProductOfOrder, recyclerTransactionLine, recyclerTransactionLineInStore, recyclerApproval;
    private RecyclerViewAdapterGCM adapterGCM;
    public RecyclerViewAdapterTransaction adapterTransaction;
    private RecyclerViewAdapterTransactionLine adapterTransactionLine, adapterTransactionLineInStore;
    public RecyclerViewAdapterCustomer adapterCustomer;
    public RecyclerViewAdapterProduct adapterProduct;
    public RecyclerViewAdapterReasonNotOrder adapterReasonNotOrder;
    private RecyclerViewAdapterApproval adapterApproval;
    private RecyclerViewAdapterProductEmployee adapterInventoryEmployee;
    private RecyclerViewAdapterProductOfOrder adapterProductOfOrder;
    public RecyclerViewAdapterOrder adapterOrder;
    private RecyclerViewAdapterReportCheckIn adapterReportCheckIn;
    private String imagePath, imageStorePath, imageCreateCustomerPath, imageNotOrderPath;
    private Context context;
    private Date from, to, toOrder, fromTransaction, toInventoryEmployee, fromReportCheckIn, toReportWeb, toReportCheckIn;//, toTransaction;
    public static HashMap<Marker, Float> hashMarker = new HashMap<>();
    public static HashMap<Integer, Marker> hashUserMarker = new HashMap<>();
    private ArrayAdapter<String> adapterSpCustomer, adapterSpProductGroup, adapterSpTransactionStatus, adapterSpApprovalStatus,
            adapterSpOrderStatus, adapterSpCity, adapterSpCounty, adapterSpInventoryStock;
    private ArrayList<Circle> arrCircles = new ArrayList<>();
    private ArrayList<MyLocation> arrDetailTracking = new ArrayList<>();

    private ArrayList<String> arrSpTransaction = new ArrayList<>();
    private ArrayList<Route> arrSpRoute = new ArrayList<>();
    private ArrayList<ProductGroup> arrSpProductGroup = new ArrayList<>();
    private ArrayList<Status> arrSpTransactionStatus = new ArrayList<>();
    private ArrayList<Status> arrSpOrderStatus = new ArrayList<>();
    private ArrayList<String> arrSpRouteName = new ArrayList<>();
    private ArrayList<String> arrSpProductGroupName = new ArrayList<>();
    private ArrayList<String> arrSpCityName = new ArrayList<>();
    private ArrayList<String> arrSpCountyName = new ArrayList<>();
    private ArrayList<String> arrSpTransactionStatusName = new ArrayList<>();
    private ArrayList<String> arrSpApprovalStatusName = new ArrayList<>();
    private ArrayList<String> arrSpOrderStatusName = new ArrayList<>();
    private ArrayList<String> arrSpInventoryStockName = new ArrayList<>();
    private String nameCustomer, addressCustomer, phoneCustomer, routeCustomer;
    private Bitmap photoCustomer, photoStore, photoNotOrder, photoCreateCustomer;
    private double latitudeCustomer;
    private double longtitudeCustomer;
    private LatLng latLngCustomer;
    private Bitmap bitmapCustomerDownload;
    private Order nowOrder;
    private Transaction nowTransaction;
    private int lastRowIdOrder = -1;//Vị trí cuối cùng của đơn hàng load được
    private int lastRowIdInventoryEmployee = -1;//Vị trí cuối cùng của tồn kho nhân viên load được
    private int lastRowIdTransaction = -1;//Vị trí cuối cùng của giao dịch load được
    private long toDateOrder = 0;//Giá trị time từ ngày đến ngày trong đơn hàng
    private long toDatenventoryEmployee = 0; // Giá trị time đến ngày trong tồn kho nhân viên
    private long toDateReportCheckIn = 0;
    private long toDateReportWeb = 0;
    private long fromDateReportCheckIn = 0;
    private Button btnToDateOrder;//Từ ngày,đến ngày trong mục đơn hàng
    private Button btnFromDateReportCheckIn, btnDateReportWeb, btnToDateReportCheckIn; // Từ ngày, đến ngày trong mục báo cáo ghé thăm
    private Button btnToDateInventoryEmployee; // Đến ngày trong mục tồn kho nhân viên
    private long fromDateTransaction = 0;// toDateTransaction = 0;//Giá trị time từ ngày đến ngày trong giao dịch
    private Button btnFromDateTransaction;// btnToDateTransaction;//Từ ngày,đến ngày trong mục giao dịch
    private EditText editQuantity, editPrice;// Khung nhập số lượng,đơn giá
    private String nowNo_;//Mã sản phẩm nhập số lượng, đơn giá
    private TextView txtTitleInputValueDialog, txtTitleInputNoteDialog, txtInStoreName, txtInStoreAddress, txtInStorePhone;
    private EditText edInStoreNote;
    private Marker customerMarker;
    public static LatLng latLngTransaction;
    private CircleImageView img_store_camera;
    private String imageUrlImageTransactionLine = "";
    private float accuracyTransactionLine = 0;
    private LatLng latLngTransactionLine = null;
    private long createDateTransactionLine = 0;
    //CREATE CUSTOMER
    private EditText edCustomerNo;
    private ArrayList<City> arrCitys = new ArrayList<>();
    private ArrayList<County> arrCountys = new ArrayList<>();
    private TextInputLayout inputLayoutCustomerName, inputLayoutCustomerAddress, inputLayoutCustomerPhone, inputLayoutCustomerNo;
    //MAP

    //LAYOUT ORDER TITLE


    private int nowIdApproval = -1;//Mã nhân viên đang chọn trong Màn hình Phân quyền truy cập
    private int nowIdEmployeeTransaction = -1;//Mã nhân viên đang chọn trong Màn hình giao dịch
    private int nowIdEmployeeOrder = -1; // Mã nhân viên đang chọn trong Màn hình đơn hàng
    private int nowIdEmployeeReportCheckIn = -1; //Mã nhân viên đang chọn trong Màn hình Báo cáo ghé thăm
    private int nowIdInventoryEmployee = -1; // Mã nhân viên đang chọn trong Tồn kho nhân viên
    private int nowInventoryGroup = -1;
    private String filterTransaction = "";
    private String filterOrderMain = "";
    private String filterTimeline = "";
    private String filterInventoryEmployee = "";
    private int positionEditOrder; // Vị trí lúc nhấn lâu vào item trong đơn hàng sửa
    private String filtersvApproval = "";
    private int lastRowIdApproval = -1;//Vị trí cuối cùng của Approval trong màn hình Phân quyền truy cập
    private int lastRowIdProduct = -1;//id cuối cùng của sản phẩm
    private int lastRowIdCustomer = -1; //id cuối cùng của khách hàng
    private int maxPrice, maxSale, maxQuantity; // so luong ki tu lon nhat khi in

    private int lastRowIdReportCheckIn = -1;
    public static ArrayAdapter<String> adapterStyle;

    private int timeCount; // Đếm thời gian trong hanlder
    public Product nowProduct;
    private ArrayList<Order> ordersArrayList;
    private ArrayList<ReportCheckIn> reportCheckInArrayList;
    private ArrayList<Product> inventoryEmployeesArrayList;
    private ArrayList<Product> arrTempProduct;
    private ArrayList<Order> arrTempOrder;
    private ArrayList<ApproVal> approvalArrayList;
    public ArrayList<ReasonNotOrder> reasonArrayList;
    private ArrayList<Customer> customerArrayList;
    private ArrayList<Product> productArrayList;
    public ArrayList<Transaction> transactionArrayList;
    private ArrayList<TransactionLine> transactionLineArrayList;
    private ArrayList<TransactionLine> transactionLineInStoreArrayList;
    private ArrayList<String> appManager;
    private ArrayList<String> appMenu;
    private ArrayList<String> appReport;
    private static List<GCM> arrGCM;
    private int nowProductGroup;
    private int nowTransactionStatus, nowOrderStatus;
    private ArrayList<OrderDetail> nowOrderDetail;
    private int lastRowIdSyncData;
    private String imagePathCustomer;
    public static Customer nowCustomer;
    private ApproVal nowApproval;
    private CustomAdapterMenu adapterGridListReport, adapterGridListMenu, adapterGridListManger;


    public RightFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.w(TAG, "onCreateView Begin");

        final PowerManager pm = (PowerManager) getContext().getSystemService(Context.POWER_SERVICE);
        this.mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "Screen OFF");
        Home.bindingRight = DataBindingUtil.inflate(inflater, R.layout.fragment_right, container, false);
        View v = Home.bindingRight.getRoot();
        getId(v);
        InitLayout();
        event(v);
        try {
            LocalBroadcastManager.getInstance(context).registerReceiver(receiver, new IntentFilter("OnFire"));
        } catch (Exception e) {
        }
        isRegisterControl = true;
        switch (Model.inst().getStatusWorking()) {
            case Stopped:
                showLayout(Layouts.LogIn, context);
                break;
            case Pending:
                showLayout(Layouts.LogIn, context);
                MyMethod.setVisible(Home.bindingRight.login.linearRestart);
                MyMethod.setGone(Home.bindingRight.login.linearSignIn);
                Home.bindingRight.login.txtMessageLogin.setText(context.getString(R.string.pending));
                break;
            case Tracking:
                showLayout(Layouts.Main, context);
                break;

        }
        Home.bindingRight.setting.txtCompanyName.setText(Model.inst().getConfigValue(Const.ConfigKeys.CompanyName));
        Home.bindingRight.setting.txtEmployeeName.setText(Model.inst().getConfigValue(Const.ConfigKeys.EmployeeName) + Utils.getSaleType(Model.inst().getConfigValue(Const.ConfigKeys.PreSale)));
        Home.bindingRight.setting.txtDeviceName.setText(Build.MODEL + " - DPI: " + (int) (getResources().getDisplayMetrics().density * 160f));
        Home.bindingRight.setting.txtDeviceSerial.setText(Build.SERIAL);
        Home.bindingRight.setting.txtDeviceVersion.setText(context.getString(R.string.android) + Build.VERSION.RELEASE);
        Home.bindingRight.setting.txtAppVersion.setText(BuildConfig.VERSION_NAME + " (" + BuildConfig.VERSION_CODE + ") " + "update " + Const.UpdateVersion);
        Home.bindingRight.setting.txtStatusTracking.setText(Model.inst().getConfigValue(Const.ConfigKeys.isActive, 1) == 0 ? "Không theo dõi" : "Theo dõi");
        Log.d(TAG, "onCreateView end");
        return v;
    }

    private ArrayList<String> arrStyle;

    public void updateMenu() {
        try {
            adapterGridListManger.notifyDataSetChanged();
            adapterGridListMenu.notifyDataSetChanged();
            adapterGridListReport.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void InitLayout() {
        Home.LayoutMyManager = new LayoutShow();
        Home.LayoutMyManager.AddLayout(new LayoutManagerObject(RightFragment.Layouts.CheckIn, Home.bindingRight.checkin.relaLayoutCheckin));
        Home.LayoutMyManager.AddLayout(new LayoutManagerObject(RightFragment.Layouts.Customer, Home.bindingRight.customer.linearCustomer));
        Home.LayoutMyManager.AddLayout(new LayoutManagerObject(RightFragment.Layouts.CustomerDetail, Home.bindingRight.customerDetail.relaCustomerDetail));
        Home.LayoutMyManager.AddLayout(new LayoutManagerObject(RightFragment.Layouts.Product, Home.bindingRight.product.linearProduct));
        Home.LayoutMyManager.AddLayout(new LayoutManagerObject(RightFragment.Layouts.Main, Home.bindingRight.relaLayoutMain));
        Home.LayoutMyManager.AddLayout(new LayoutManagerObject(RightFragment.Layouts.Order, Home.bindingRight.order.linearOrder));
        Home.LayoutMyManager.AddLayout(new LayoutManagerObject(RightFragment.Layouts.ProductOfOrder, Home.bindingRight.orderProduct.linearProductOfOrder));
        Home.LayoutMyManager.AddLayout(new LayoutManagerObject(RightFragment.Layouts.LogIn, Home.bindingRight.login.relativeLayoutLogin));
        Home.LayoutMyManager.AddLayout(new LayoutManagerObject(RightFragment.Layouts.Setting, Home.bindingRight.setting.linearSetting));
        Home.LayoutMyManager.AddLayout(new LayoutManagerObject(RightFragment.Layouts.CustomerUpdate, Home.bindingRight.customerUpdate.relaCustomerUpdate));
        Home.LayoutMyManager.AddLayout(new LayoutManagerObject(RightFragment.Layouts.ProductDetail, Home.bindingRight.productDetail.relaProductDetail));
        Home.LayoutMyManager.AddLayout(new LayoutManagerObject(RightFragment.Layouts.Transaction, Home.bindingRight.transaction.linearTransaction));
        Home.LayoutMyManager.AddLayout(new LayoutManagerObject(Layouts.TransactionDetail, Home.bindingRight.transactionDetail.relaTransactionDetail));
        Home.LayoutMyManager.AddLayout(new LayoutManagerObject(RightFragment.Layouts.MapUpdate, Home.bindingRight.customerUpdateMap.linearMapUpdate));
        Home.LayoutMyManager.AddLayout(new LayoutManagerObject(RightFragment.Layouts.MapCustomerView, Home.bindingRight.customerViewMap.linearMapView));
        Home.LayoutMyManager.AddLayout(new LayoutManagerObject(RightFragment.Layouts.ListGCM, Home.bindingRight.gcm.linearListGcm));
        Home.LayoutMyManager.AddLayout(new LayoutManagerObject(Layouts.OrderList, Home.bindingRight.orderMain.linearOrderMain));
        Home.LayoutMyManager.AddLayout(new LayoutManagerObject(Layouts.OrderDetail, Home.bindingRight.orderDetail.linearOrderDetail));
        Home.LayoutMyManager.AddLayout(new LayoutManagerObject(Layouts.InputValue, Home.bindingRight.inputValue.linearInputValue));
        Home.LayoutMyManager.AddLayout(new LayoutManagerObject(Layouts.InputNote, Home.bindingRight.note.linearInputNote));
        Home.LayoutMyManager.AddLayout(new LayoutManagerObject(Layouts.SelectRoute, Home.bindingRight.selectRoute.linearSelectRoute));
        Home.LayoutMyManager.AddLayout(new LayoutManagerObject(Layouts.MapCustomerCheckIn, Home.bindingRight.mapCheckInCustomer.relaMapCheckinOrder));
        Home.LayoutMyManager.AddLayout(new LayoutManagerObject(Layouts.GoStore, Home.bindingRight.inStore.linearInStore));
        Home.LayoutMyManager.AddLayout(new LayoutManagerObject(Layouts.CreateCustomer, Home.bindingRight.createCustomer.relaCreateCustomer));
        Home.LayoutMyManager.AddLayout(new LayoutManagerObject(Layouts.History, Home.bindingRight.history.linearHistory));
        Home.LayoutMyManager.AddLayout(new LayoutManagerObject(Layouts.InventoryEmployee, Home.bindingRight.inventoryEmployee.linearInventoryEmployee));
        Home.LayoutMyManager.AddLayout(new LayoutManagerObject(Layouts.Approval, Home.bindingRight.approvalAppLock.linearAppprovalAppLock));
        Home.LayoutMyManager.AddLayout(new LayoutManagerObject(Layouts.ApprovalButton, Home.bindingRight.approvalButton.linearApprovalButton));
        Home.LayoutMyManager.AddLayout(new LayoutManagerObject(Layouts.ReportWeb, Home.bindingRight.reportWeb.linearReportWeb));
        Home.LayoutMyManager.AddLayout(new LayoutManagerObject(Layouts.ReportCheckIn, Home.bindingRight.reportTimeCheckIn.linearReportCheckIn));
        Home.LayoutMyManager.AddLayout(new LayoutManagerObject(Layouts.UpdateImage, Home.bindingRight.updateImageCustomer.linearUpdateImage));
        Home.LayoutMyManager.AddLayout(new LayoutManagerObject(Layouts.ReasonNotOrder, Home.bindingRight.reasonNotOrder.linearReasonNotOrder));
        Home.LayoutMyManager.AddLayout(new LayoutManagerObject(Layouts.LogInRoute, Home.bindingRight.loginRoute.linearLogInRoute));


    }


    public void loadMenu(Context context) {
        appManager.clear();
        appMenu.clear();
        appReport.clear();
        if (Model.inst().isMenuShow(Const.Menu.SendReport))
            appManager.add("Ghi nhận");
        if (Model.inst().isMenuShow(Const.Menu.Transactions))
            appManager.add(context.getString(R.string.transaction));
        if (Model.inst().isMenuShow(Const.Menu.Tracking))
            appManager.add("Giám sát");
        if (Model.inst().isMenuShow(Const.Menu.Setting))
            appManager.add(context.getString(R.string.setting));
        if (Model.inst().isMenuShow(Const.Menu.History))
            appManager.add(context.getString(R.string.history));
        if (Model.inst().isMenuShow(Const.Menu.Customers))
            appMenu.add(context.getString(R.string.customer));
        if (Model.inst().isMenuShow(Const.Menu.Products))
            appMenu.add(context.getString(R.string.product));
        if (Model.inst().isMenuShow(Const.Menu.Orders))
            appMenu.add(context.getString(R.string.order));
        if (Model.inst().isMenuShow(Const.Menu.Inventory))
            appMenu.add(context.getString(R.string.inventory));
        if (Model.inst().isMenuShow(Const.Menu.InventoryInPut))
            appManager.add(context.getString(R.string.inventory_input));
        if (Model.inst().isMenuShow(Const.Menu.ApproVal))
            appManager.add(context.getString(R.string.approval));
        if (Model.inst().isMenuShow(Const.Menu.InventoryEmployee))
            appMenu.add(context.getString(R.string.inventory_employee));
        if (Model.inst().isMenuShow(Const.Menu.InventoryBill))
            appMenu.add(context.getString(R.string.inventory_bill));
        if (Model.inst().isMenuShow(Const.Menu.Library))
            appMenu.add(context.getString(R.string.library));
        if (Model.inst().isMenuShow(Const.Menu.Survey)) {
            appMenu.add(context.getString(R.string.survey));
        }
        if (Model.inst().isMenuShow(Const.Menu.ReportWeb))
            appReport.add(context.getString(R.string.report));
        if (Model.inst().isMenuShow(Const.Menu.ReportCheckIn))
            appReport.add(context.getString(R.string.report_check_in));
        if (Model.inst().isMenuShow(Const.Menu.ReportRoute))
            appReport.add(getString(R.string.report_route));
        if (Model.inst().isMenuShow(Const.Menu.ReportTransactionEmployee))
            appReport.add(getString(R.string.report_transaction_employee));
        if (Model.inst().isMenuShow(Const.Menu.ReportTransactionGeneral))
            appReport.add(getString(R.string.report_transaction_general));

        if (appManager.size() > 0) {
            Home.bindingRight.txtManager.setVisibility(View.VISIBLE);
            Home.bindingRight.viewManager.setVisibility(View.VISIBLE);
        }
        if (appMenu.size() > 0) {
            Home.bindingRight.txtListMenu.setVisibility(View.VISIBLE);
            Home.bindingRight.viewListMenu.setVisibility(View.VISIBLE);
        }
        if (appReport.size() > 0) {
            Home.bindingRight.txtListReport.setVisibility(View.VISIBLE);
            Home.bindingRight.viewReport.setVisibility(View.VISIBLE);
        }
        adapterGridListManger.notifyDataSetChanged();
        adapterGridListMenu.notifyDataSetChanged();
        adapterGridListReport.notifyDataSetChanged();
    }


    private void event(final View v) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            Home.relativeCheckIn.setBackground(MyMethod.getBackground(context));
            Home.relativeRight.setBackgroundColor(Color.TRANSPARENT);
        }
        Home.mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        Home.mapOrderFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapOrderFragMent);
        Home.mapUpdateFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapUpdate);
        Home.mapCustomerViewFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapCustomerView);
        Home.mapCustomerCheckinFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapCheckInCustomer);
        Home.adapterStaff = new ArrayUserAdapter(this.getActivity(), R.layout.custom_adapterstaff, Home.arrStaff);
        adapterGridListManger = new CustomAdapterMenu(context, appManager);
        Home.bindingRight.gridMenuManager.setAdapter(adapterGridListManger);
        Home.bindingRight.gridMenuManager.setOnItemClickListener(this);
        adapterGridListMenu = new CustomAdapterMenu(context, appMenu);
        Home.bindingRight.gridListMenu.setAdapter(adapterGridListMenu);
        Home.bindingRight.gridListMenu.setOnItemClickListener(this);
        adapterGridListReport = new CustomAdapterMenu(context, appReport);
        Home.bindingRight.gridReport.setAdapter(adapterGridListReport);
        Home.bindingRight.gridReport.setOnItemClickListener(this);
        Home.bindingRight.mapCheckInCustomer.btnCheckinOrder.setOnClickListener(this);
        Home.adapterStaff.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Home.lstOrderProduct.setOnItemLongClickListener(this);
        Home.bindingRight.reasonNotOrder.btnReasonAccept.setOnClickListener(this);
        Home.bindingRight.reasonNotOrder.btnReasonExit.setOnClickListener(this);
        v.findViewById(R.id.btn_update_location_customer).setOnClickListener(this);
        v.findViewById(R.id.fabOk).setOnClickListener(this);
        v.findViewById(R.id.fabGetAgainOrder).setOnClickListener(this);
        v.findViewById(R.id.img_pre_sale).setOnClickListener(this);
        v.findViewById(R.id.img_pre_sale_detail).setOnClickListener(this);
        v.findViewById(R.id.btn_select_day_report).setOnClickListener(this);
        Home.bindingRight.login.btnSignin.setOnClickListener(this);
        Home.bindingRight.setting.btnUpdateProgram.setOnClickListener(this);
        Home.bindingRight.setting.btnRestartAppSetting.setOnClickListener(this);
        Home.bindingRight.setting.btnPrinterSetting.setOnClickListener(this);
        Home.bindingRight.setting.btnPauseProgram.setOnClickListener(this);
        Home.bindingRight.setting.btnSyncData.setOnClickListener(this);
        Home.bindingRight.setting.btnUpdateData.setOnClickListener(this);
        Home.bindingRight.setting.btnLoginRoute.setOnClickListener(this);
        Home.bindingRight.checkin.btnGetCheckin.setOnClickListener(this);
        Home.bindingRight.checkin.btnGetPhotoCheckin.setOnClickListener(this);
        Home.bindingRight.checkin.btnSaveSendCheckin.setOnClickListener(this);
        Home.bindingRight.login.btnRestartApp.setOnClickListener(this);
        Home.bindingRight.login.btnLoginRouteInLogin.setOnClickListener(this);
        Home.bindingRight.loginRoute.btnSigninRoute.setOnClickListener(this);
        v.findViewById(R.id.btn_restart_clone).setOnClickListener(this);
        v.findViewById(R.id.btn_restart_app_error).setOnClickListener(this);
        v.findViewById(R.id.dialog_customer_checkin).setOnClickListener(this);
        v.findViewById(R.id.dialog_customer_update).setOnClickListener(this);
        v.findViewById(R.id.dialog_customer_name_edit).setOnClickListener(this);
        v.findViewById(R.id.dialog_customer_address_edit).setOnClickListener(this);
        v.findViewById(R.id.dialog_customer_phone_edit).setOnClickListener(this);
        v.findViewById(R.id.dialog_customer_photo_edit).setOnClickListener(this);
        v.findViewById(R.id.dialog_customer_location_edit).setOnClickListener(this);
        v.findViewById(R.id.dialog_customer_update_ok).setOnClickListener(this);
        v.findViewById(R.id.dialog_customer_update_cancel).setOnClickListener(this);
        v.findViewById(R.id.dialog_customer_location_view).setOnClickListener(this);
        v.findViewById(R.id.dialog_customer_phone_call).setOnClickListener(this);
        v.findViewById(R.id.dialog_customer_order).setOnClickListener(this);
        v.findViewById(R.id.dialog_product_back).setOnClickListener(this);
        v.findViewById(R.id.order_customer_save_send).setOnClickListener(this);
        v.findViewById(R.id.order_customer_add_product).setOnClickListener(this);
        v.findViewById(R.id.order_product_accept).setOnClickListener(this);
        v.findViewById(R.id.order_product_cancel).setOnClickListener(this);
        v.findViewById(R.id.order_main_to).setOnClickListener(this);
        v.findViewById(R.id.report_time_from).setOnClickListener(this);
        v.findViewById(R.id.report_time_to).setOnClickListener(this);
        v.findViewById(R.id.inventory_employee_to).setOnClickListener(this);
        v.findViewById(R.id.order_main_load).setOnClickListener(this);
        v.findViewById(R.id.report_web_load).setOnClickListener(this);
        v.findViewById(R.id.customer_load).setOnClickListener(this);
        v.findViewById(R.id.report_time_load).setOnClickListener(this);
        v.findViewById(R.id.inventory_employee_load).setOnClickListener(this);
        v.findViewById(R.id.approval_load).setOnClickListener(this);
        v.findViewById(R.id.transaction_from).setOnClickListener(this);
        v.findViewById(R.id.transaction_to).setOnClickListener(this);
        v.findViewById(R.id.transaction_load).setOnClickListener(this);
        v.findViewById(R.id.btn_accept_input).setOnClickListener(this);
        Home.bindingRight.note.btnAcceptInputNote.setOnClickListener(this);
        v.findViewById(R.id.fabGetAgainCheckInOrder).setOnClickListener(this);
        v.findViewById(R.id.store_camera).setOnClickListener(this);
        v.findViewById(R.id.btn_order_in_store).setOnClickListener(this);
        v.findViewById(R.id.btn_in_store_sample).setOnClickListener(this);
        v.findViewById(R.id.btn_in_stote_display).setOnClickListener(this);
        Home.bindingRight.inStore.btnOrderReport.setOnClickListener(this);
        Home.bindingRight.inStore.btnStoreLibrary.setOnClickListener(this);
        Home.bindingRight.inStore.btnStoreSurveyCustomer.setOnClickListener(this);
        Home.bindingRight.inStore.btnStoreSurveyNotCustomer.setOnClickListener(this);
        v.findViewById(R.id.btn_inventory_report).setOnClickListener(this);
        v.findViewById(R.id.btn_out_store).setOnClickListener(this);
        //v.findViewById(R.id.btn_order_incurred).setOnClickListener(this);
        Home.bindingRight.transactionDetail.refTransactionMap.setOnClickListener(this);
        v.findViewById(R.id.ref_transaction_phonecall).setOnClickListener(this);
        Home.bindingRight.mapCheckInCustomer.btnMapCheckinUpdateImage.setOnClickListener(this);
        Home.bindingRight.mapCheckInCustomer.btnMapCheckinUpdateLocation.setOnClickListener(this);
        Home.bindingRight.mapCheckInCustomer.btnMapCheckinUpdateRoute.setOnClickListener(this);
        v.findViewById(R.id.img_create_customer).setOnClickListener(this);
        v.findViewById(R.id.image_create_customer).setOnClickListener(this);
        v.findViewById(R.id.btn_reset_customer_field).setOnClickListener(this);
        v.findViewById(R.id.btn_create_customer).setOnClickListener(this);
        v.findViewById(R.id.btn_customer_get_location).setOnClickListener(this);
        Home.bindingRight.transactionDetail.btnTransactionNote.setOnClickListener(this);
        Home.bindingRight.transactionDetail.btnTransactionCheckIn.setOnClickListener(this);
        Home.bindingRight.transactionDetail.btnTransactionWrite.setOnClickListener(this);
        Home.bindingRight.transactionDetail.btnAcceptWork.setOnClickListener(this);
        v.findViewById(R.id.btn_customer_select_route).setOnClickListener(this);
        v.findViewById(R.id.btn_accept_select_route).setOnClickListener(this);
        v.findViewById(R.id.btn_accept_approval).setOnClickListener(this);
        v.findViewById(R.id.btn_accept_company_aproval).setOnClickListener(this);
        v.findViewById(R.id.btn_decline_approval).setOnClickListener(this);
        v.findViewById(R.id.update_take_again).setOnClickListener(this);
        v.findViewById(R.id.update_again).setOnClickListener(this);
        btnFromDateTransaction = (Button)
                v.findViewById(R.id.transaction_from);
        btnToDateOrder = (Button)
                v.findViewById(R.id.order_main_to);
        btnFromDateReportCheckIn = (Button)
                v.findViewById(R.id.report_time_from);
        btnToDateReportCheckIn = (Button)
                v.findViewById(R.id.report_time_to);
        btnToDateInventoryEmployee = (Button)
                v.findViewById(R.id.inventory_employee_to);
        btnDateReportWeb = (Button)
                v.findViewById(R.id.btn_select_day_report);

//  btnToDateTransaction = (Button)
//                v.findViewById(R.id.transaction_to);
        //ORDER DETAIl
        v.findViewById(R.id.order_detail_add_product).setOnClickListener(this);
        v.findViewById(R.id.order_detail_save_send).setOnClickListener(this);
        Home.bindingRight.customerUpdate.dialogCustomerPhoto.setOnClickListener(this);
        Home.bindingRight.customerDetail.dialogCustomerPhotoDetail.setOnClickListener(this);
        Home.bindingRight.productDetail.dialogProductPhotoDetail.setOnClickListener(this);
        editName.addTextChangedListener(new MyTextWatcher(editName));
        editPass.addTextChangedListener(new MyTextWatcher(editPass));
        editNote.addTextChangedListener(new MyTextWatcher(editNote));

        editNote.setOnKeyListener(this);
        recyclerOrder.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), this));
        recyclerReportCheckIn.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), this));
        recyclerGCM.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), this));

        recyclerGCM.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        recyclerTransaction.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), this));
        recyclerCustomer.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), this));
        recyclerProduct.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), this));
        recyclerApproval.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), this));
        recyclerInventoryEmployee.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), this));
        recyclerProductOfOrder.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), this));
        //Home.bindingRight.reasonNotOrder.recyclerReasonNotOrder.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), this));
        Home.bindingRight.history.recyclerTimeline.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), this));
        Home.bindingRight.transaction.svTransaction.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }


            Handler mHandler = new Handler();

            @Override
            public boolean onQueryTextChange(final String newText) {
                filterTransaction = newText;

                mHandler.removeCallbacksAndMessages(null);

                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Put your call to the server here (with mQueryString)
                        try {
                            //if (newText.substring(newText.length() - 1).contains(" ")) {
                            reFreshTransaction();
                            //}
                        } catch (Exception ignored) {
                        }
                    }
                }, 1000);
                return true;

            }
        });
        Home.bindingRight.customer.svCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Home.bindingRight.customer.svCustomer.setIconified(false);
            }
        });
        Home.bindingRight.customer.svCustomer.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            Handler mHandler = new Handler();

            @Override
            public boolean onQueryTextChange(final String newText) {
                Home.filterCustomer = newText;

                mHandler.removeCallbacksAndMessages(null);

                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Put your call to the server here (with mQueryString)
                        try {
                            // if (newText.substring(newText.length() - 1).contains(" ")) {
                            reFreshCustomerNotLoading();
                            //}
                        } catch (Exception ignored) {
                        }
                    }
                }, 1000);
                return true;

            }
        });

        Home.bindingRight.product.svProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Home.bindingRight.product.svProduct.setIconified(false);
            }
        });
        Home.bindingRight.product.svProduct.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            Handler mHandler = new Handler();

            @Override
            public boolean onQueryTextChange(final String newText) {
                Home.filterProduct = newText;

                mHandler.removeCallbacksAndMessages(null);

                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Put your call to the server here (with mQueryString)
                        try {
                            //if (newText.substring(newText.length() - 1).contains(" ")) {
                            refreshProductNotLoading();
                            //}
                        } catch (Exception e) {
                        }
                    }
                }, 1000);
                return true;

            }
        });

        Home.bindingRight.approvalAppLock.svApproval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Home.bindingRight.approvalAppLock.svApproval.setIconified(false);
            }
        });
        Home.bindingRight.approvalAppLock.svApproval.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                filtersvApproval = newText;
                Handler mHandler = new Handler();
                mHandler.removeCallbacksAndMessages(null);

                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Put your call to the server here (with mQueryString)
                        EventPool.control().enQueue(new EventType.EventLoadRequestGrantRequest(lastRowIdApproval, nowIdApproval, Home.bindingRight.approvalAppLock.spApprovalStatus.getSelectedItemPosition(), filtersvApproval));
                    }
                }, 1000);
                return true;
            }
        });
        Home.bindingRight.orderProduct.svProductOfOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Home.bindingRight.orderProduct.svProductOfOrder.setIconified(false);
            }
        });
        Home.bindingRight.orderProduct.svProductOfOrder.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (adapterProductOfOrder != null)
                    adapterProductOfOrder.getFilter().filter(newText);
                return false;
            }
        });
        Home.bindingRight.orderMain.svOrderMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Home.bindingRight.orderMain.svOrderMain.setIconified(false);
            }
        });
        Home.bindingRight.orderMain.svOrderMain.setQueryHint(context.getString(R.string.search_order));
        Home.bindingRight.orderMain.svOrderMain.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterOrderMain = newText;
                return false;
            }
        });
        Home.bindingRight.history.svTimeLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Home.bindingRight.history.svTimeLine.setIconified(false);
            }
        });
        Home.bindingRight.history.svTimeLine.setQueryHint(context.getString(R.string.search_timeline));
        Home.bindingRight.history.svTimeLine.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterTimeline = newText;
                return false;
            }
        });
        Home.bindingRight.inventoryEmployee.svInventoryEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Home.bindingRight.inventoryEmployee.svInventoryEmployee.setIconified(false);
            }
        });
        Home.bindingRight.inventoryEmployee.svInventoryEmployee.setQueryHint(context.getString(R.string.search_inventory_employee));
        Home.bindingRight.inventoryEmployee.svInventoryEmployee.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterInventoryEmployee = newText;
                Handler mHandler = new Handler();
                mHandler.removeCallbacksAndMessages(null);

                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Put your call to the server here (with mQueryString)
                        inventoryEmployeesArrayList.clear();
                        EventPool.control().enQueue(new EventType.EventLoadInventoryEmployeesRequest(-1, toDatenventoryEmployee, nowIdInventoryEmployee, nowInventoryGroup, Home.bindingRight.inventoryEmployee.spInventoryStock.getSelectedItemPosition(), filterInventoryEmployee));

                    }
                }, 1000);
                return true;
            }
        });
        arrSpTransaction.add("Trong tuyến");
        arrSpTransaction.add("Tất cả");
        adapterSpCustomer = new ArrayAdapter<>
                (
                        context,
                        R.layout.custom_spinner_route,
                        arrSpRouteName
                );
        adapterSpCustomer.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        adapterSpProductGroup = new ArrayAdapter<>
                (
                        context,
                        R.layout.custom_spinear_product_group,
                        arrSpProductGroupName
                );
        adapterSpCity = new ArrayAdapter<>
                (
                        context,
                        R.layout.custom_spinear_product_group,
                        arrSpCityName
                );
        adapterSpCounty = new ArrayAdapter<>
                (
                        context,
                        R.layout.custom_spinear_product_group,
                        arrSpCountyName
                );
        adapterSpTransactionStatus = new ArrayAdapter<>
                (
                        context,
                        R.layout.custom_spinear_product_group,
                        arrSpTransactionStatusName
                );
        adapterSpApprovalStatus = new ArrayAdapter<>
                (
                        context,
                        R.layout.custom_spinear_product_group,
                        arrSpApprovalStatusName
                );
        adapterSpOrderStatus = new ArrayAdapter<>
                (
                        context,
                        R.layout.custom_spinear_product_group,
                        arrSpOrderStatusName
                );
        adapterSpInventoryStock = new ArrayAdapter<>
                (
                        context,
                        R.layout.custom_spinear_product_group,
                        arrSpInventoryStockName
                );
        adapterSpTransactionStatus.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        adapterSpApprovalStatus.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        adapterSpOrderStatus.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        adapterSpInventoryStock.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        adapterSpProductGroup.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        adapterSpCity.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        adapterSpCounty.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        Home.bindingRight.customer.spCustomer.setAdapter(adapterSpCustomer);
        Home.bindingRight.product.spProductGroup.setAdapter(adapterSpProductGroup);
        Home.bindingRight.inventoryEmployee.spInventoryGroup.setAdapter(adapterSpProductGroup);
        Home.bindingRight.createCustomer.spCustomerCity.setAdapter(adapterSpCity);
        Home.bindingRight.createCustomer.spCustomerCounty.setAdapter(adapterSpCounty);
        Home.bindingRight.transaction.sptransactionStatus.setAdapter(adapterSpTransactionStatus);
        Home.bindingRight.approvalAppLock.spApprovalStatus.setAdapter(adapterSpApprovalStatus);
        Home.bindingRight.orderMain.spOrderStatus.setAdapter(adapterSpOrderStatus);
        Home.bindingRight.inventoryEmployee.spInventoryStock.setAdapter(adapterSpInventoryStock);
        Home.bindingRight.customer.spCustomer.setOnItemSelectedListener(this);
        Home.bindingRight.product.spProductGroup.setOnItemSelectedListener(this);
        Home.bindingRight.inventoryEmployee.spInventoryGroup.setOnItemSelectedListener(this);
        Home.bindingRight.createCustomer.spCustomerCity.setOnItemSelectedListener(this);
        Home.bindingRight.createCustomer.spCustomerCounty.setOnItemSelectedListener(this);
        Home.bindingRight.transaction.sptransactionStatus.setOnItemSelectedListener(this);
        Home.bindingRight.approvalAppLock.spApprovalStatus.setOnItemSelectedListener(this);
        Home.bindingRight.orderMain.spOrderStatus.setOnItemSelectedListener(this);
        Home.bindingRight.inventoryEmployee.spInventoryStock.setOnItemSelectedListener(this);
        Home.swipeTransaction.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                // get the new data from you data source
                // TODO : request data here
                reFreshTransaction();
                // our swipeRefreshLayout needs to be notified when the data is returned in order for it to stop the animation
            }
        });
        // sets the colors used in the refresh animation
        Home.swipeTransaction.setColorSchemeResources(R.color.colorAlert, R.color.colorBlue,
                R.color.colorGreen, R.color.colorOranger);
        // sets the colors used in the refresh animation
        Home.swipeProduct.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                // get the new data from you data source
                // TODO : request data here
                refreshProduct();
                // our swipeRefreshLayout needs to be notified when the data is returned in order for it to stop the animation
            }
        });
        // sets the colors used in the refresh animation
        Home.swipeProduct.setColorSchemeResources(R.color.colorAlert, R.color.colorBlue,
                R.color.colorGreen, R.color.colorOranger);
        Home.swipeApproval.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshApproval();
            }
        });
        Home.swipeApproval.setColorSchemeColors(R.color.colorAlert, R.color.colorBlue,
                R.color.colorGreen, R.color.colorOranger);
        Home.swipeProductOfOrder.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                // get the new data from you data source
                // TODO : request data here
                refreshProductOfOrder();
                // our swipeRefreshLayout needs to be notified when the data is returned in order for it to stop the animation
            }
        });
        // sets the colors used in the refresh animation
        Home.swipeProductOfOrder.setColorSchemeResources(R.color.colorAlert, R.color.colorBlue,
                R.color.colorGreen, R.color.colorOranger);
        Home.swipeOrderMain.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                // get the new data from you data source
                // TODO : request data here
                reFreshOrder();
                // our swipeRefreshLayout needs to be notified when the data is returned in order for it to stop the animation
            }
        });
        // sets the colors used in the refresh animation
        Home.swipeOrderMain.setColorSchemeResources(R.color.colorAlert, R.color.colorBlue,
                R.color.colorGreen, R.color.colorOranger);
        Home.swipeReportCheckIn.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                // get the new data from you data source
                // TODO : request data here
                reFreshReportCheckIn();
                // our swipeRefreshLayout needs to be notified when the data is returned in order for it to stop the animation
            }
        });
        // sets the colors used in the refresh animation
        Home.swipeReportCheckIn.setColorSchemeResources(R.color.colorAlert, R.color.colorBlue,
                R.color.colorGreen, R.color.colorOranger);
        Home.swipeCustomer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                // get the new data from you data source
                // TODO : request data here
                reFreshCustomer();
                // our swipeRefreshLayout needs to be notified when the data is returned in order for it to stop the animation
            }
        });
        // sets the colors used in the refresh animation
        Home.swipeCustomer.setColorSchemeResources(R.color.colorAlert, R.color.colorBlue,
                R.color.colorGreen, R.color.colorOranger);

        Home.swipeOrderMainBottom.setOnRefreshListener(new SwipeRefreshLayoutBottom.OnRefreshListener() {

            @Override
            public void onRefresh() {
                // get the new data from you data source
                // TODO : request data here
                loadMoreOrder();
                // our swipeRefreshLayout needs to be notified when the data is returned in order for it to stop the animation
            }
        });
        // sets the colors used in the refresh animation
        Home.swipeOrderMainBottom.setColorSchemeResources(R.color.colorAlert, R.color.colorBlue,
                R.color.colorGreen, R.color.colorOranger);

        Home.swipeReportCheckInBottom.setOnRefreshListener(new SwipeRefreshLayoutBottom.OnRefreshListener() {

            @Override
            public void onRefresh() {
                // get the new data from you data source
                // TODO : request data here
                loadMoreReportCheckIn();
                // our swipeRefreshLayout needs to be notified when the data is returned in order for it to stop the animation
            }
        });
        // sets the colors used in the refresh animation
        Home.swipeReportCheckInBottom.setColorSchemeResources(R.color.colorAlert, R.color.colorBlue,
                R.color.colorGreen, R.color.colorOranger);

        Home.bindingRight.orderProduct.loadMoreProductOrder.setOnRefreshListener(new SwipeRefreshLayoutBottom.OnRefreshListener() {

            @Override
            public void onRefresh() {
                // get the new data from you data source
                // TODO : request data here
                loadMoreProductOrder();
                // our swipeRefreshLayout needs to be notified when the data is returned in order for it to stop the animation
            }
        });
        Home.bindingRight.product.loadMoreProduct.setOnRefreshListener(new SwipeRefreshLayoutBottom.OnRefreshListener() {

            @Override
            public void onRefresh() {
                // get the new data from you data source
                // TODO : request data here
                loadMoreProduct();
                // our swipeRefreshLayout needs to be notified when the data is returned in order for it to stop the animation
            }
        });
        // sets the colors used in the refresh animation
        Home.bindingRight.product.loadMoreProduct.setColorSchemeResources(R.color.colorAlert, R.color.colorBlue,
                R.color.colorGreen, R.color.colorOranger);
        Home.bindingRight.orderProduct.loadMoreProductOrder.setColorSchemeResources(R.color.colorAlert, R.color.colorBlue,
                R.color.colorGreen, R.color.colorOranger);
        Home.swipeInventoryEmployeeBottom.setOnRefreshListener(new SwipeRefreshLayoutBottom.OnRefreshListener() {

            @Override
            public void onRefresh() {
                // get the new data from you data source
                // TODO : request data here
                loadMoreInventoryEmployee();
                // our swipeRefreshLayout needs to be notified when the data is returned in order for it to stop the animation
            }
        });
        // sets the colors used in the refresh animation
        Home.swipeInventoryEmployeeBottom.setColorSchemeResources(R.color.colorAlert, R.color.colorBlue,
                R.color.colorGreen, R.color.colorOranger);
        Home.swipeCustomerBottom.setOnRefreshListener(new SwipeRefreshLayoutBottom.OnRefreshListener() {

            @Override
            public void onRefresh() {
                // get the new data from you data source
                // TODO : request data here
                loadMoreCustomer();
                // our swipeRefreshLayout needs to be notified when the data is returned in order for it to stop the animation
            }
        });
        // sets the colors used in the refresh animation
        Home.swipeCustomerBottom.setColorSchemeResources(R.color.colorAlert, R.color.colorBlue,
                R.color.colorGreen, R.color.colorOranger);
        Home.swipeTransactionBottom.setOnRefreshListener(new SwipeRefreshLayoutBottom.OnRefreshListener() {

            @Override
            public void onRefresh() {
                // get the new data from you data source
                // TODO : request data here
                loadMoreTransaction();
                // our swipeRefreshLayout needs to be notified when the data is returned in order for it to stop the animation
            }
        });
        // sets the colors used in the refresh animation
        Home.swipeTransactionBottom.setColorSchemeResources(R.color.colorAlert, R.color.colorBlue,
                R.color.colorGreen, R.color.colorOranger);
        //hard code spinner approval status
        arrSpApprovalStatusName.clear();
        Status apC = new Status();
        apC.id = 0;
        apC.name = "Chưa duyệt";
        arrSpApprovalStatusName.add(apC.name);
        Status apD = new Status();
        apD.id = 1;
        apD.name = "Cá nhân";
        arrSpApprovalStatusName.add(apD.name);
        Status apT = new Status();
        apT.id = 2;
        apT.name = "Công ty";
        arrSpApprovalStatusName.add(apT.name);
        Status apTC = new Status();
        apTC.id = 3;
        apTC.name = "Từ chối";
        arrSpApprovalStatusName.add(apTC.name);
        adapterSpApprovalStatus.notifyDataSetChanged();
        //hard code spinner transaction status
        arrSpTransactionStatus.clear();
        arrSpTransactionStatusName.clear();
        Status tradall = new Status();
        tradall.id = -1
        ;
        tradall.name = "Tất cả";
        arrSpTransactionStatus.add(tradall);
        arrSpTransactionStatusName.add(tradall.name);
        Status traded = new Status();
        traded.id = 0;
        traded.name = "Đã giao dịch";
        arrSpTransactionStatus.add(traded);
        arrSpTransactionStatusName.add(traded.name);
        Status needTran = new Status();
        needTran.id = 1;
        needTran.name = "Cần giao dịch";
        arrSpTransactionStatus.add(needTran);
        arrSpTransactionStatusName.add(needTran.name);
        Status notifyTran = new Status();
        notifyTran.id = 2;
        notifyTran.name = "Thông báo";
        arrSpTransactionStatus.add(notifyTran);
        arrSpTransactionStatusName.add(notifyTran.name);
        adapterSpTransactionStatus.notifyDataSetChanged();

        //hard code spinner inventory stock
        arrSpInventoryStockName.clear();
        Status stH = new Status();
        stH.id = 0;
        stH.name = "Hết hàng";
        arrSpInventoryStockName.add(stH.name);
        Status stC = new Status();
        stC.id = 1;
        stC.name = "Còn hàng";
        arrSpInventoryStockName.add(stC.name);
        Status stA = new Status();
        stA.id = 2;
        stA.name = "Tất cả";
        arrSpInventoryStockName.add(stA.name);
        adapterSpInventoryStock.notifyDataSetChanged();
        //hard code spinner order status
        arrSpOrderStatus.clear();
        arrSpOrderStatusName.clear();
        Status odA = new Status();
        odA.id = 99;
        odA.name = "Tất cả";
        arrSpOrderStatus.add(odA);
        arrSpOrderStatusName.add(odA.name);
        Status odN = new Status();
        odN.id = 0;
        odN.name = "Tạo mới";
        arrSpOrderStatus.add(odN);
        arrSpOrderStatusName.add(odN.name);
        Status odY = new Status();
        odY.id = 1;
        odY.name = "Xác nhận";
        arrSpOrderStatus.add(odY);
        arrSpOrderStatusName.add(odY.name);
        Status odO = new Status();
        odO.id = 2;
        odO.name = "Mở lại";
        arrSpOrderStatus.add(odO);
        arrSpOrderStatusName.add(odO.name);

        Status odV = new Status();
        odV.id = 3;
        odV.name = "Duyệt";
        arrSpOrderStatus.add(odV);
        arrSpOrderStatusName.add(odV.name);

        Status odC = new Status();
        odC.id = 4;
        odC.name = "Hủy";
        arrSpOrderStatus.add(odC);
        arrSpOrderStatusName.add(odC.name);

        adapterSpOrderStatus.notifyDataSetChanged();

        if (arrStyle == null) arrStyle = new ArrayList<>();
        if (Home.timelinesArrayList == null) Home.timelinesArrayList = new ArrayList<>();
        arrStyle.add("Bảng tin");
        arrStyle.add("Trạng thái");
        adapterStyle = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, arrStyle);
        Home.adapterTimeLine = new RecyclerViewAdapterTimeLine(Home.timelinesArrayList, context);
        adapterStyle.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if (Home.adapterStaff == null)
            Home.adapterStaff = new ArrayUserAdapter(this.getActivity(), R.layout.custom_adapterstaff, Home.arrStaff);
        Home.bindingRight.history.spSelectStaff.setAdapter(Home.adapterStaff);
        Home.bindingRight.transaction.spStaffTransaction.setAdapter(Home.adapterStaff);
        Home.bindingRight.orderMain.spStaffOrder.setAdapter(Home.adapterStaff);
        Home.bindingRight.customer.spStaffCustomer.setAdapter(Home.adapterStaff);
        Home.bindingRight.reportTimeCheckIn.spStaffReportCheckIn.setAdapter(Home.adapterStaff);
        Home.bindingRight.approvalAppLock.spStaffApproval.setAdapter(Home.adapterStaff);
        Home.bindingRight.inventoryEmployee.spStaffInventoryEmployee.setAdapter(Home.adapterStaff);
        Home.bindingRight.history.spStyleView.setAdapter(adapterStyle);
        Home.bindingRight.history.recyclerTimeline.setAdapter(Home.adapterTimeLine);
        Home.bindingRight.history.spSelectStaff.setOnItemSelectedListener(this);
        Home.bindingRight.transaction.spStaffTransaction.setOnItemSelectedListener(this);
        Home.bindingRight.orderMain.spStaffOrder.setOnItemSelectedListener(this);
        Home.bindingRight.customer.spStaffCustomer.setOnItemSelectedListener(this);
        Home.bindingRight.reportTimeCheckIn.spStaffReportCheckIn.setOnItemSelectedListener(this);
        Home.bindingRight.approvalAppLock.spStaffApproval.setOnItemSelectedListener(this);
        Home.bindingRight.inventoryEmployee.spStaffInventoryEmployee.setOnItemSelectedListener(this);
        Home.bindingRight.history.spStyleView.setOnItemSelectedListener(this);
        Home.swipeTimeLine.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                // get the new data from you data source
                // TODO : request data here
                reFreshTimeLine();
                // our swipeRefreshLayout needs to be notified when the data is returned in order for it to stop the animation
            }
        });
        // sets the colors used in the refresh animation
        Home.swipeTimeLine.setColorSchemeResources(R.color.colorAlert, R.color.colorBlue,
                R.color.colorGreen, R.color.colorOranger);

        Home.swipeInventoryEmployee.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                // get the new data from you data source
                // TODO : request data here
                reFreshInventoryEmployee();
                // our swipeRefreshLayout needs to be notified when the data is returned in order for it to stop the animation
            }
        });
        // sets the colors used in the refresh animation
        Home.swipeInventoryEmployee.setColorSchemeResources(R.color.colorAlert, R.color.colorBlue,
                R.color.colorGreen, R.color.colorOranger);

        Home.swipeTimeLineBottom.setOnRefreshListener(new SwipeRefreshLayoutBottom.OnRefreshListener() {

            @Override
            public void onRefresh() {
                // get the new data from you data source
                // TODO : request data here
                loadMoreTimeLine();
                // our swipeRefreshLayout needs to be notified when the data is returned in order for it to stop the animation
            }
        });
        // sets the colors used in the refresh animation
        Home.swipeTimeLineBottom.setColorSchemeResources(R.color.colorAlert, R.color.colorBlue,
                R.color.colorGreen, R.color.colorOranger);

        //Realtime

    }

    private void reFreshOrder() {

        if (MyMethod.isInventory) {
            ordersArrayList.clear();
            adapterOrder.notifyDataSetChanged();
            EventPool.control().enQueue(new EventType.EventLoadOrdersRequest(-1, toDateOrder, 1, filterOrderMain, nowIdEmployeeOrder));
        } else if (MyMethod.isLoadOrder) {
            ordersArrayList.clear();
            adapterOrder.notifyDataSetChanged();
            EventPool.control().enQueue(new EventType.EventLoadOrdersRequest(-1, toDateOrder, 0, filterOrderMain, nowIdEmployeeOrder));
        } else if (MyMethod.isInventoryEmployee) {
            inventoryEmployeesArrayList.clear();
            adapterInventoryEmployee.notifyDataSetChanged();
            EventPool.control().enQueue(new EventType.EventLoadOrdersRequest(-1, toDateOrder, 3, filterOrderMain, nowIdEmployeeOrder));
        } else if (MyMethod.isInventoryBill) {
            ordersArrayList.clear();
            adapterOrder.notifyDataSetChanged();
            EventPool.control().enQueue(new EventType.EventLoadOrdersRequest(-1, toDateOrder, 2, filterOrderMain, nowIdEmployeeOrder));
        }

        Home.swipeOrderMain.setRefreshing(false);
    }

    private void reFreshReportCheckIn() {
        reportCheckInArrayList.clear();
        adapterReportCheckIn.notifyDataSetChanged();
        LayoutLoadingManager.Show_OnLoading(Home.loadingReportCheckIn, context.getString(R.string.load_report_checkin), 30);
        EventPool.control().enQueue(new EventType.EventLoadReportCheckInsRequest(fromDateReportCheckIn, toDateReportCheckIn, nowIdEmployeeReportCheckIn, -1));
        Home.swipeReportCheckIn.setRefreshing(false);
    }

    private void reFreshTransaction() {
        transactionArrayList.clear();
        transactionLineArrayList.clear();
        adapterTransaction.notifyDataSetChanged();
        MyMethod.isLoadTransactionByID = false;
        MyMethod.isLoadTransactionByIDInMessage = false;
        EventPool.control().enQueue(new EventType.EventLoadTransactionsRequest(-1, fromDateTransaction, nowIdEmployeeTransaction, filterTransaction, false, nowTransactionStatus));
        Home.swipeTransaction.setRefreshing(false);
    }

    private void reFreshCustomer() {
        try {
            customerArrayList.clear();
            adapterCustomer.notifyDataSetChanged();
            if (Home.nowIdCustomer == -1) {
                Home.nowIdCustomer = Home.arrStaff.get(0).id_employee;
            }
            int countCustomer = LocalDB.inst().countCustomer(Home.nowIdCustomer);
            if (countCustomer > 0) {
                //load offlinef
                customerArrayList = LocalDB.inst().loadCustomer(Home.nowRoute, Home.filterCustomer);
                adapterCustomer.setItems(customerArrayList);
                adapterCustomer.notifyDataSetChanged();
            } else if (countCustomer == 0) {
                //load online
                LayoutLoadingManager.Show_OnLoading(Home.loadingCustomer, context.getString(R.string.load_customer), 30);
                EventPool.control().enQueue(new EventType.EventLoadCustomersRequest(Home.nowRoute, Home.filterCustomer, -1, Home.nowIdCustomer));
            } else if (countCustomer == -1) {
                //sync data
                MyMethod.showToast(context, "Yêu cầu đồng bộ dữ liệu");
                LocalDB.inst().deleteSyncData();
                LayoutLoadingManager.Show_OnLoading(Home.loadingCustomer, context.getString(R.string.load_route), 30);
                EventPool.control().enQueue(new EventType.EventSyncDataRequest(0, -1));
                MyMethod.isSyncDating = true;

            }
            Home.swipeCustomer.setRefreshing(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void reFreshCustomerNotLoading() {
        customerArrayList.clear();
        adapterCustomer.notifyDataSetChanged();
        int countCustomer = LocalDB.inst().countCustomer(Home.nowIdCustomer);
        if (countCustomer > 0) {
            //load offline
            customerArrayList = LocalDB.inst().loadCustomer(Home.nowRoute, Home.filterCustomer);
            adapterCustomer.setItems(customerArrayList);
            adapterCustomer.notifyDataSetChanged();
        } else if (countCustomer == 0) {
            //load online
            EventPool.control().enQueue(new EventType.EventLoadCustomersRequest(Home.nowRoute, Home.filterCustomer, -1, Home.nowIdCustomer));
        } else if (countCustomer == -1) {
            //sync data
            MyMethod.showToast(context, "Yêu cầu đồng bộ dữ liệu");
            LocalDB.inst().deleteSyncData();
            LayoutLoadingManager.Show_OnLoading(Home.loadingCustomer, context.getString(R.string.load_route), 30);
            EventPool.control().enQueue(new EventType.EventSyncDataRequest(0, -1));
            MyMethod.isSyncDating = true;
        }
        Home.swipeCustomer.setRefreshing(false);
    }

    private void getId(View v) {
        context = getContext();
        //initializition object
        ordersArrayList = new ArrayList<>();
        reportCheckInArrayList = new ArrayList<>();
        inventoryEmployeesArrayList = new ArrayList<>();
        arrTempOrder = new ArrayList<>();
        approvalArrayList = new ArrayList<>();
        reasonArrayList = new ArrayList<>();
        customerArrayList = new ArrayList<>();
        productArrayList = new ArrayList<>();
        transactionArrayList = new ArrayList<>();
        transactionLineArrayList = new ArrayList<>();
        transactionLineInStoreArrayList = new ArrayList<>();
        appManager = new ArrayList<>();
        appMenu = new ArrayList<>();
        appReport = new ArrayList<>();
        nowOrder = new Order();
        nowTransaction = new Transaction();
        nowOrderDetail = new ArrayList<>();
        Home.nowTransactionLine = new TransactionLine();
        ((AppCompatActivity) getActivity()).setSupportActionBar(Home.bindingRight.reportTimeCheckIn.toolbarReportCheckIn);
        ((AppCompatActivity) getActivity()).setSupportActionBar(Home.bindingRight.orderMain.toolbarOrder);
        ((AppCompatActivity) getActivity()).setSupportActionBar(Home.bindingRight.orderProduct.toolbarOrderProduct);
        ((AppCompatActivity) getActivity()).setSupportActionBar(Home.bindingRight.customer.toolbarCustomer);
        ((AppCompatActivity) getActivity()).setSupportActionBar(Home.bindingRight.product.toolbarProduct);
        ((AppCompatActivity) getActivity()).setSupportActionBar(Home.bindingRight.approvalAppLock.toolbarApproval);
        ((AppCompatActivity) getActivity()).setSupportActionBar(Home.bindingRight.inventoryEmployee.toolbarInventoryEmployee);
        ((AppCompatActivity) getActivity()).setSupportActionBar(Home.bindingRight.transaction.toolbarTransaction);
        Home.bindingRight.orderMain.toolbarOrder.setTitleTextColor(getResources().getColor(android.R.color.white));
        Home.bindingRight.orderProduct.toolbarOrderProduct.setTitleTextColor(getResources().getColor(android.R.color.white));
        Home.bindingRight.customer.toolbarCustomer.setTitleTextColor(getResources().getColor(android.R.color.white));
        Home.bindingRight.product.toolbarProduct.setTitleTextColor(getResources().getColor(android.R.color.white));
        Home.bindingRight.approvalAppLock.toolbarApproval.setTitleTextColor(getResources().getColor(android.R.color.white));
        Home.bindingRight.inventoryEmployee.toolbarInventoryEmployee.setTitleTextColor(getResources().getColor(android.R.color.white));
        Home.bindingRight.transaction.toolbarTransaction.setTitleTextColor(getResources().getColor(android.R.color.white));
        Home.bindingRight.reportTimeCheckIn.toolbarReportCheckIn.setTitleTextColor(getResources().getColor(android.R.color.white));

        Home.loadingReportWeb = (LoadingView) v.findViewById(R.id.ReportWebLoadingView);
        Home.loadingUpdateImage = (LoadingView) v.findViewById(R.id.UpdateImageLoadingView);
        Home.loadingLogin = (LoadingView) v.findViewById(R.id.loginLoadingView);
        Home.loadingMapOrder = (LoadingView) v.findViewById(R.id.MapOrderLoadingView);
        Home.loadingListGCM = (LoadingView) v.findViewById(R.id.ListGCMLoadingView);
        Home.loadingCheckIn = (LoadingView) v.findViewById(R.id.CheckInLoadingView);
        Home.loadingCustomer = (LoadingView) v.findViewById(R.id.CustomerLoadingView);
        Home.loadingProduct = (LoadingView) v.findViewById(R.id.ProductLoadingView);
        Home.loadingOrderMain = (LoadingView) v.findViewById(R.id.OrderMainLoadingView);
        Home.loadingTransaction = (LoadingView) v.findViewById(R.id.TransactionLoadingView);
        Home.loadingTransactionLineInStore = (LoadingView) v.findViewById(R.id.TransactionLineInStoreLoadingView);
        Home.loadingSendOrder = (LoadingView) v.findViewById(R.id.OrderLoadingView);
        Home.loadingProductOfOrder = (LoadingView) v.findViewById(R.id.ProductOfOrderLoadingView);
        Home.loadingUpdateCustomer = (LoadingView) v.findViewById(R.id.UpdateCustomerLoadingView);
        Home.loadingMapCustomerCheckIn = (LoadingView) v.findViewById(R.id.MapCustomerCheckInLoadingView);
        Home.loadingInStore = (LoadingView) v.findViewById(R.id.InStoreLoadingView);
        Home.loadingCreateCustomer = (LoadingView) v.findViewById(R.id.CreateCustomerLoadingView);
        Home.loadingOrderDetail = (LoadingView) v.findViewById(R.id.OrderDetailLoading);
        Home.loadingHistory = (LoadingView) v.findViewById(R.id.HistoryLoadingView);
        Home.loadingInventoryEmployee = (LoadingView) v.findViewById(R.id.InventoryEmployeeLoadingView);
        Home.loadingApprovalAppLock = (LoadingView) v.findViewById(R.id.ApprovalAppLockLoadingView);
        Home.loadingReportCheckIn = (LoadingView) v.findViewById(R.id.ReportCheckInLoadingView);
        Home.loadingApprovalButton = (LoadingView) v.findViewById(R.id.ApprovalButtonLoadingView);
        //ID FOR ORDER
        Home.txtOrderCustomerName = (TextView) v.findViewById(R.id.order_customer_name);
        Home.txtOrderCustomerAddress = (TextView) v.findViewById(R.id.order_customer_address);
        Home.txtOrderProductAmountItem = (TextView) v.findViewById(R.id.order_product_amount_item);
        Home.btnOrderAddProduct = (Button) v.findViewById(R.id.order_customer_add_product);
        Home.btnOrderSaveSend = (Button) v.findViewById(R.id.order_customer_save_send);
        Home.btnOrderProductCancel = (Button) v.findViewById(R.id.order_product_cancel);
        Home.btnOrderProductAccept = (Button) v.findViewById(R.id.order_product_accept);
        Home.txtOrderAmountSale = (TextView) v.findViewById(R.id.order_customer_amount_sale);
        Home.txtOrderDiscount = (TextView) v.findViewById(R.id.order_customer_discount);
        Home.txtOrderAmount = (TextView) v.findViewById(R.id.order_customer_amount);
        Home.edOrderNote = (EditText) v.findViewById(R.id.order_customer_note);
        Home.cbOrderPreSale = (CheckBox) v.findViewById(R.id.order_customer_pre_sale);
        Home.lstOrderProduct = (ListView) v.findViewById(R.id.order_list_product);
        Home.img_pre_sale = (ImageView) v.findViewById(R.id.img_pre_sale);
        Home.img_pre_sale_detail = (ImageView) v.findViewById(R.id.img_pre_sale_detail);
        // END ORDER
        //btnIncurredOrder = (Button) v.findViewById(R.id.btn_order_incurred);
        editNoteOrder = (EditText) v.findViewById(R.id.edit_note_order);
        editName = (EditText) v.findViewById(R.id.input_name);
        editPass = (EditText) v.findViewById(R.id.input_password);
        editNote = (EditText) v.findViewById(R.id.input_note);
        Home.imagePhotoIn = (ImageView) v.findViewById(R.id.imagePhotoIn);
        Home.txtAddressIn = (TextView) v.findViewById(R.id.txtAddressIn);
        Home.editCheckIn = (EditText) v.findViewById(R.id.edit_check_in);
        inputLayoutName = (TextInputLayout) v.findViewById(R.id.input_layout_name);
        inputLayoutPassword = (TextInputLayout) v.findViewById(R.id.input_layout_password);
        inputLayoutNote = (TextInputLayout) v.findViewById(R.id.input_layout_note);
        Home.swipeTransaction = (SwipeRefreshLayout) v.findViewById(R.id.swipe_transaction);
        Home.swipeProduct = (SwipeRefreshLayout) v.findViewById(R.id.swipe_product);
        Home.swipeApproval = (SwipeRefreshLayout) v.findViewById(R.id.swipe_approval);
        Home.swipeProductOfOrder = (SwipeRefreshLayout) v.findViewById(R.id.swipe_product_of_order);
        Home.swipeOrderMain = (SwipeRefreshLayout) v.findViewById(R.id.swipe_order_main);
        Home.swipeReportCheckIn = (SwipeRefreshLayout) v.findViewById(R.id.swipe_report_check_in);
        Home.swipeCustomer = (SwipeRefreshLayout) v.findViewById(R.id.swipe_customer);
        recyclerOrder = (RecyclerView) v.findViewById(R.id.recyclerOrderMain);
        recyclerReportCheckIn = (RecyclerView) v.findViewById(R.id.recyclerReportCheckIn);
        recyclerGCM = (RecyclerView) v.findViewById(R.id.recyclerGCM);
        recyclerGCM.setHasFixedSize(true);
        recyclerOrder.setHasFixedSize(true);
        recyclerReportCheckIn.setHasFixedSize(true);
        recyclerTransaction = (RecyclerView) v.findViewById(R.id.recyclerTransaction);
        recyclerTransactionLine = (RecyclerView) v.findViewById(R.id.recyclerTransactionLine);
        recyclerTransactionLineInStore = (RecyclerView) v.findViewById(R.id.recyclerTransactionLineInStore);
        recyclerCustomer = (RecyclerView) v.findViewById(R.id.recyclerCustomer);
        recyclerProduct = (RecyclerView) v.findViewById(R.id.recyclerProduct);
        recyclerApproval = (RecyclerView) v.findViewById(R.id.recyclerApproval);
        recyclerInventoryEmployee = (RecyclerView) v.findViewById(R.id.recyclerInventoryEmployee);
        recyclerProductOfOrder = (RecyclerView) v.findViewById(R.id.recyclerProductOfOrder);
        recyclerTransaction.setHasFixedSize(true);
        recyclerTransactionLine.setHasFixedSize(true);
        recyclerTransactionLineInStore.setHasFixedSize(true);
        recyclerCustomer.setHasFixedSize(true);
        recyclerProduct.setHasFixedSize(true);
        Home.bindingRight.reasonNotOrder.recyclerReasonNotOrder.setHasFixedSize(true);
        recyclerApproval.setHasFixedSize(true);
        recyclerInventoryEmployee.setHasFixedSize(true);
        recyclerProductOfOrder.setHasFixedSize(true);
        managerOrder.setOrientation(LinearLayoutManager.VERTICAL);
        managerReportCheckIn.setOrientation(LinearLayoutManager.VERTICAL);
        LinearLayoutManager managerTransaction = new LinearLayoutManager(v.getContext());
        managerTransaction.setOrientation(LinearLayoutManager.VERTICAL);
        LinearLayoutManager managerTransactionLine = new LinearLayoutManager(v.getContext());
        managerTransactionLine.setOrientation(LinearLayoutManager.VERTICAL);
        LinearLayoutManager managerTransactionLineInStore = new LinearLayoutManager(v.getContext());
        managerTransactionLineInStore.setOrientation(LinearLayoutManager.VERTICAL);
        LinearLayoutManager managerCustomer = new LinearLayoutManager(v.getContext());
        managerCustomer.setOrientation(LinearLayoutManager.VERTICAL);
        LinearLayoutManager managerProduct = new LinearLayoutManager(v.getContext());
        managerProduct.setOrientation(LinearLayoutManager.VERTICAL);
        LinearLayoutManager managerReasonNotOrder = new LinearLayoutManager(v.getContext());
        managerReasonNotOrder.setOrientation(LinearLayoutManager.VERTICAL);
        LinearLayoutManager managerApproval = new LinearLayoutManager(v.getContext());
        managerApproval.setOrientation(LinearLayoutManager.VERTICAL);
        LinearLayoutManager managerInventoryEmployee = new LinearLayoutManager(v.getContext());
        managerInventoryEmployee.setOrientation(LinearLayoutManager.VERTICAL);
        LinearLayoutManager managerProductOfOrder = new LinearLayoutManager(v.getContext());
        managerProductOfOrder.setOrientation(LinearLayoutManager.VERTICAL);
        LinearLayoutManager managerOrderMain = new LinearLayoutManager(v.getContext());
        managerOrderMain.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerOrder.setLayoutManager(managerOrder);
        recyclerReportCheckIn.setLayoutManager(managerReportCheckIn);
        recyclerGCM.setLayoutManager(managerGCM);
        recyclerTransaction.setLayoutManager(managerTransaction);
        recyclerTransactionLine.setLayoutManager(managerTransactionLine);
        recyclerTransactionLineInStore.setLayoutManager(managerTransactionLineInStore);
        recyclerCustomer.setLayoutManager(managerCustomer);
        recyclerProduct.setLayoutManager(managerProduct);
        Home.bindingRight.reasonNotOrder.recyclerReasonNotOrder.setLayoutManager(managerReasonNotOrder);
        recyclerApproval.setLayoutManager(managerApproval);
        recyclerInventoryEmployee.setLayoutManager(managerInventoryEmployee);
        recyclerProductOfOrder.setLayoutManager(managerProductOfOrder);
        adapterGCM = new RecyclerViewAdapterGCM(arrGCM, v.getContext());
        adapterTransaction = new RecyclerViewAdapterTransaction(transactionArrayList, v.getContext());
        adapterTransactionLine = new RecyclerViewAdapterTransactionLine(transactionLineArrayList, v.getContext());
        adapterTransactionLineInStore = new RecyclerViewAdapterTransactionLine(transactionLineInStoreArrayList, v.getContext());
        adapterCustomer = new RecyclerViewAdapterCustomer(customerArrayList, v.getContext());
        adapterProduct = new RecyclerViewAdapterProduct(productArrayList, v.getContext());
        adapterReasonNotOrder = new RecyclerViewAdapterReasonNotOrder(reasonArrayList, v.getContext());
        adapterApproval = new RecyclerViewAdapterApproval(approvalArrayList, v.getContext());
        adapterInventoryEmployee = new RecyclerViewAdapterProductEmployee(inventoryEmployeesArrayList, v.getContext());
        adapterProductOfOrder = new RecyclerViewAdapterProductOfOrder(productArrayList, v.getContext());
        adapterOrder = new RecyclerViewAdapterOrder(ordersArrayList, v.getContext());
        adapterReportCheckIn = new RecyclerViewAdapterReportCheckIn(reportCheckInArrayList, v.getContext());
        recyclerOrder.setAdapter(adapterOrder);
        recyclerReportCheckIn.setAdapter(adapterReportCheckIn);
        recyclerGCM.setAdapter(adapterGCM);
        recyclerTransaction.setAdapter(adapterTransaction);
        recyclerTransactionLine.setAdapter(adapterTransactionLine);
        recyclerTransactionLineInStore.setAdapter(adapterTransactionLineInStore);
        recyclerCustomer.setAdapter(adapterCustomer);
        recyclerProduct.setAdapter(adapterProduct);
        Home.bindingRight.reasonNotOrder.recyclerReasonNotOrder.setAdapter(adapterReasonNotOrder);
        recyclerApproval.setAdapter(adapterApproval);
        recyclerInventoryEmployee.setAdapter(adapterInventoryEmployee);
        recyclerProductOfOrder.setAdapter(adapterProductOfOrder);
        Home.relativeCheckIn = (RelativeLayout) v.findViewById(R.id.rela_bg_checkin);
        Home.relativeRight = (RelativeLayout) v.findViewById(R.id.rela_bg_right);
        RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        v.setLayoutParams(p);
        v.requestLayout();
        Home.orderListProductAdapter = new OrderListProductAdapter(this.getActivity(), Home.orderDetailArrayList);
        Home.lstOrderProduct.setAdapter(Home.orderListProductAdapter);
        //Order Detail Id
        Home.txtOrderDetailAmount = (TextView) v.findViewById(R.id.order_detail_amount);
        Home.txtOrderDetailAmountSale = (TextView) v.findViewById(R.id.order_detail_amount_sale);
        Home.txtOrderDetailDiscount = (TextView) v.findViewById(R.id.order_detail_discount);
        Home.txtOrderDetailNoName = (TextView) v.findViewById(R.id.order_detail_no_name);
        Home.txtOrderDetailTime = (TextView) v.findViewById(R.id.order_detail_time);
        Home.edOrderDetailNote = (EditText) v.findViewById(R.id.order_detail_note);
        Home.lstOrderDetail = (ListView) v.findViewById(R.id.order_detail_list);
        Home.orderListOrderDetailAdapter = new OrderListProductAdapter(this.getActivity(), nowOrderDetail);
        Home.lstOrderDetail.setAdapter(Home.orderListOrderDetailAdapter);
        editQuantity = (EditText) v.findViewById(R.id.product_input_value);
        editPrice = (EditText) v.findViewById(R.id.product_input_price);
        Home.swipeOrderMainBottom = (SwipeRefreshLayoutBottom) v.findViewById(R.id.refresh_order);
        Home.swipeReportCheckInBottom = (SwipeRefreshLayoutBottom) v.findViewById(R.id.refresh_report_check_in);
        Home.swipeInventoryEmployeeBottom = (SwipeRefreshLayoutBottom) v.findViewById(R.id.load_more_inventory_employee);
        Home.swipeCustomerBottom = (SwipeRefreshLayoutBottom) v.findViewById(R.id.load_more_customer);
        Home.swipeTransactionBottom = (SwipeRefreshLayoutBottom) v.findViewById(R.id.refresh_transaction);
        txtTitleInputValueDialog = (TextView) v.findViewById(R.id.txt_input_title);
        txtTitleInputNoteDialog = (TextView) v.findViewById(R.id.txt_input_title_note);
        txtInStoreName = (TextView) v.findViewById(R.id.store_name);
        txtInStoreAddress = (TextView) v.findViewById(R.id.store_address);
        txtInStorePhone = (TextView) v.findViewById(R.id.store_phone);
        edInStoreNote = (EditText) v.findViewById(R.id.store_note);

        //format input layout
        editPrice.addTextChangedListener(new NumberTextWatcher(editPrice));

        //Transaction Header ID
        txtTransactionHeaderNameNo = (TextView) v.findViewById(R.id.ref_transaction_name_no);
        txtTransactionHeaderAddress = (TextView) v.findViewById(R.id.ref_transaction_address);
        txtTransactionHeaderNote = (TextView) v.findViewById(R.id.ref_transaction_note);
        txtTransactionHeaderPhone = (TextView) v.findViewById(R.id.ref_transaction_phone);
        txtTransactionHeaderStatus = (TextView) v.findViewById(R.id.ref_transaction_status);
        txtTransactionHeaderTime = (TextView) v.findViewById(R.id.ref_transaction_time);

        //IN STORE
        img_store_camera = (CircleImageView) v.findViewById(R.id.store_camera);

        //MAP CUSTOMER CHECKIN
        Home.btnMapCustomerUpdateImage = (Button) v.findViewById(R.id.btn_map_checkin_update_image);
        Home.btnMapCustomerUpdateLocation = (Button) v.findViewById(R.id.btn_map_checkin_update_location);

        //CREATE CUSTOMER
        edCustomerNo = (EditText) v.findViewById(R.id.edCustomerNo);
        inputLayoutCustomerNo = (TextInputLayout) v.findViewById(R.id.input_layout_customer_no);
        inputLayoutCustomerName = (TextInputLayout) v.findViewById(R.id.input_layout_customer_name);
        inputLayoutCustomerAddress = (TextInputLayout) v.findViewById(R.id.input_layout_customer_address);
        inputLayoutCustomerPhone = (TextInputLayout) v.findViewById(R.id.input_layout_customer_phone);
        cns = (CheckBox) v.findViewById(R.id.check8S);
        cnc = (CheckBox) v.findViewById(R.id.check8C);
        t2s = (CheckBox) v.findViewById(R.id.check2S);
        t2c = (CheckBox) v.findViewById(R.id.check2C);
        t3s = (CheckBox) v.findViewById(R.id.check3S);
        t3c = (CheckBox) v.findViewById(R.id.check3C);
        t4s = (CheckBox) v.findViewById(R.id.check4S);
        t4c = (CheckBox) v.findViewById(R.id.check4C);
        t5s = (CheckBox) v.findViewById(R.id.check5S);
        t5c = (CheckBox) v.findViewById(R.id.check5C);
        t6s = (CheckBox) v.findViewById(R.id.check6S);
        t6c = (CheckBox) v.findViewById(R.id.check6C);
        t7s = (CheckBox) v.findViewById(R.id.check7S);
        t7c = (CheckBox) v.findViewById(R.id.check7C);
        btn_create_customer_route = (Button) v.findViewById(R.id.btn_customer_select_route);

        //TITLE LAYOUT ORDER
        Home.swipeTimeLine = (SwipeRefreshLayout) v.findViewById(R.id.swipe_timeline_top);
        Home.swipeInventoryEmployee = (SwipeRefreshLayout) v.findViewById(R.id.swipe_Inventory_Employee);
        Home.swipeTimeLineBottom = (SwipeRefreshLayoutBottom) v.findViewById(R.id.swipe_timeline_bottom);
        Home.bindingRight.history.recyclerTimeline.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(v.getContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        Home.bindingRight.history.recyclerTimeline.setLayoutManager(manager);
        Home.bindingRight.history.toolbarTimeLine.setTitleTextColor(getResources().getColor(android.R.color.white));
        ((AppCompatActivity) getActivity()).setSupportActionBar(Home.bindingRight.history.toolbarTimeLine);
        Home.bindingRight.setting.btnSyncData.setCurrentText(context.getString(R.string.sync_data));
        Home.bindingRight.setting.btnUpdateData.setCurrentText(context.getString(R.string.update_data));
    }


    private void refreshProduct() {
        MyMethod.isOrderEditing = false;
        productArrayList.clear();
        adapterProduct.notifyDataSetChanged();
        int countProduct = LocalDB.inst().countProduct();
        if (countProduct > 0) {
            //load offline
            productArrayList = LocalDB.inst().loadProduct(Home.filterProduct);
            adapterProduct.setItems(productArrayList);
            adapterProduct.notifyDataSetChanged();
        } else if (countProduct == 0) {
            //load online
            EventPool.control().enQueue(new EventType.EventLoadProductsRequest(Const.DocumentType.Sale.getValue(), -1, Home.filterProduct));
        } else if (countProduct == -1) {
            //sync data
            MyMethod.showToast(context, "Yêu cầu đồng bộ dữ liệu");
            LocalDB.inst().deleteSyncData();
            LayoutLoadingManager.Show_OnLoading(Home.loadingCustomer, context.getString(R.string.load_route), 30);
            EventPool.control().enQueue(new EventType.EventSyncDataRequest(0, -1));
            MyMethod.isSyncDating = true;
        }
        Home.swipeProduct.setRefreshing(false);
    }

    private void refreshProductNotLoading() {
        MyMethod.isOrderEditing = false;
        productArrayList.clear();
        adapterProduct.notifyDataSetChanged();
        int countProduct = LocalDB.inst().countProduct();
        if (countProduct > 0) {
            //load offline
            productArrayList = LocalDB.inst().loadProduct(Home.filterProduct);
            adapterProduct.setItems(productArrayList);
            adapterProduct.notifyDataSetChanged();
        } else if (countProduct == 0) {
            //load online
            EventPool.control().enQueue(new EventType.EventLoadProductsRequest(Const.DocumentType.Sale.getValue(), -1, Home.filterProduct));
        } else if (countProduct == -1) {
            //sync data
            MyMethod.showToast(context, "Yêu cầu đồng bộ dữ liệu");
            LocalDB.inst().deleteSyncData();
            LayoutLoadingManager.Show_OnLoading(Home.loadingCustomer, context.getString(R.string.load_route), 30);
            EventPool.control().enQueue(new EventType.EventSyncDataRequest(0, -1));
            MyMethod.isSyncDating = true;
        }
        Home.swipeProduct.setRefreshing(false);
    }

    private void refreshApproval() {
//        EventPool.control().enQueue(new EventType.EventLoad());
        approvalArrayList.clear();
        adapterApproval.notifyDataSetChanged();
        EventPool.control().enQueue(new EventType.EventLoadRequestGrantRequest(-1, nowIdApproval, Home.bindingRight.approvalAppLock.spApprovalStatus.getSelectedItemPosition(), filtersvApproval));
        Home.swipeApproval.setRefreshing(false);
    }

    private void refreshProductOfOrder() {
        MyMethod.isOrderEditing = false;
        productArrayList.clear();
        adapterProductOfOrder.notifyDataSetChanged();
        int countProduct = LocalDB.inst().countProduct();
        if (countProduct > 0) {
            //load offline
            productArrayList = LocalDB.inst().loadProduct(Home.filterProduct);
            adapterProductOfOrder.setItems(productArrayList);
            adapterProductOfOrder.notifyDataSetChanged();
        } else if (countProduct == 0) {
            if (MyMethod.isOrder) { //Đặt hàng
                if (Model.inst().getConfigValue(Const.ConfigKeys.PreSale) == null) {//Nếu null thì Sale
                    EventPool.control().enQueue(new EventType.EventLoadProductsRequest(Const.DocumentType.Sale.getValue(), -1, Home.filterProduct));
                } else {
                    EventPool.control().enQueue(new EventType.EventLoadProductsRequest(Integer.parseInt(Model.inst().getConfigValue(Const.ConfigKeys.PreSale)) == 0 ? Const.DocumentType.Sale.getValue() : Const.DocumentType.SaleEmployee.getValue(), -1, Home.filterProduct));
                }
            } else if (MyMethod.isInventoryInput) {//Đề nghị nhập kho
                EventPool.control().enQueue(new EventType.EventLoadProductsRequest(Const.DocumentType.SaleEmployee.getValue(), -1, Home.filterProduct));
            } else if (MyMethod.isInventoryReport) { // Báo cáo tồn kho
                EventPool.control().enQueue(new EventType.EventLoadProductsRequest(Const.DocumentType.InventoryStore.getValue(), -1, Home.filterProduct));
            } else if (MyMethod.isProductSample) {//Hàng mẫu
                EventPool.control().enQueue(new EventType.EventLoadProductsRequest(Const.DocumentType.ProductSample.getValue(), -1, Home.filterProduct));
            } else if (MyMethod.isProductDisplay) {//Hàng trưng bày
                EventPool.control().enQueue(new EventType.EventLoadProductsRequest(Const.DocumentType.ProductDisplay.getValue(), -1, Home.filterProduct));
            }
        }

        Home.swipeProductOfOrder.setRefreshing(false);

    }

    //Tải thêm đơn hàng
    private void loadMoreOrder() {
        if (MyMethod.isInventory) {
            EventPool.control().enQueue(new EventType.EventLoadOrdersRequest(lastRowIdOrder, toDateOrder, 1, filterOrderMain, nowIdEmployeeOrder));
        } else if (MyMethod.isLoadOrder) {
            EventPool.control().enQueue(new EventType.EventLoadOrdersRequest(lastRowIdOrder, toDateOrder, 0, filterOrderMain, nowIdEmployeeOrder));
        } else if (MyMethod.isInventoryEmployee) {
            EventPool.control().enQueue(new EventType.EventLoadOrdersRequest(lastRowIdOrder, toDateOrder, 3, filterOrderMain, nowIdEmployeeOrder));
        } else if (MyMethod.isInventoryBill) {
            EventPool.control().enQueue(new EventType.EventLoadOrdersRequest(lastRowIdOrder, toDateOrder, 2, filterOrderMain, nowIdEmployeeOrder));
        }

        Home.swipeOrderMainBottom.setRefreshing(false);
    }

    //Tải thêm sản phẩm
    private void loadMoreProduct() {
        int countProduct = LocalDB.inst().countProduct();
        if (countProduct > 0) {
            //load offline
            productArrayList.addAll(LocalDB.inst().loadProduct(Home.filterProduct));
            adapterProduct.setItems(productArrayList);
            lastRowIdProduct = productArrayList.get(productArrayList.size() - 1).id;
            adapterProduct.notifyDataSetChanged();
        } else if (countProduct == 0) {
            //load online
            EventPool.control().enQueue(new EventType.EventLoadProductsRequest(Const.DocumentType.Sale.getValue(), lastRowIdProduct, Home.filterProduct));
        } else if (countProduct == -1) {
            //sync data
            MyMethod.showToast(context, "Yêu cầu đồng bộ dữ liệu");
            LocalDB.inst().deleteSyncData();
            LayoutLoadingManager.Show_OnLoading(Home.loadingCustomer, context.getString(R.string.load_route), 30);
            EventPool.control().enQueue(new EventType.EventSyncDataRequest(0, -1));
            MyMethod.isSyncDating = true;
        }
        Home.bindingRight.product.loadMoreProduct.setRefreshing(false);
    }

    //Tải thêm sản phẩm
    private void loadMoreProductOrder() {
        int countProduct = LocalDB.inst().countProduct();
        if (countProduct > 0) {
            //load offline
            productArrayList.addAll(LocalDB.inst().loadProduct(lastRowIdProduct, Home.filterProduct));
            adapterProductOfOrder.setItems(productArrayList);
            lastRowIdProduct = productArrayList.get(productArrayList.size() - 1).id;
            adapterProductOfOrder.notifyDataSetChanged();
        } else if (countProduct == 0) {
            if (MyMethod.isOrder) { //Đặt hàng
                if (Model.inst().getConfigValue(Const.ConfigKeys.PreSale) == null) {//Nếu null thì Sale
                    EventPool.control().enQueue(new EventType.EventLoadProductsRequest(Const.DocumentType.Sale.getValue(), lastRowIdProduct, Home.filterProduct));
                } else {
                    EventPool.control().enQueue(new EventType.EventLoadProductsRequest(Integer.parseInt(Model.inst().getConfigValue(Const.ConfigKeys.PreSale)) == 0 ? Const.DocumentType.Sale.getValue() : Const.DocumentType.SaleEmployee.getValue(), lastRowIdProduct, Home.filterProduct));
                }
            } else if (MyMethod.isInventoryInput) {//Đề nghị nhập kho
                EventPool.control().enQueue(new EventType.EventLoadProductsRequest(Const.DocumentType.InventoryEmployee.getValue(), lastRowIdProduct, Home.filterProduct));
            } else if (MyMethod.isInventoryReport) { // Báo cáo tồn kho
                EventPool.control().enQueue(new EventType.EventLoadProductsRequest(Const.DocumentType.InventoryStore.getValue(), lastRowIdProduct, Home.filterProduct));
            } else if (MyMethod.isProductSample) {//Hàng mẫu
                EventPool.control().enQueue(new EventType.EventLoadProductsRequest(Const.DocumentType.ProductSample.getValue(), lastRowIdProduct, Home.filterProduct));
            } else if (MyMethod.isProductDisplay) {//Hàng trưng bày
                EventPool.control().enQueue(new EventType.EventLoadProductsRequest(Const.DocumentType.ProductDisplay.getValue(), lastRowIdProduct, Home.filterProduct));
            }
        }
        Home.bindingRight.orderProduct.loadMoreProductOrder.setRefreshing(false);
    }


    //Tải thêm báo cáo ghé thăm
    private void loadMoreReportCheckIn() {
        EventPool.control().enQueue(new EventType.EventLoadReportCheckInsRequest(fromDateReportCheckIn, toDateReportCheckIn, nowIdEmployeeReportCheckIn, lastRowIdReportCheckIn));
        Home.swipeReportCheckInBottom.setRefreshing(false);
    }

    //Tải thêm tồn kho nhân viên
    private void loadMoreInventoryEmployee() {
        EventPool.control().enQueue(new EventType.EventLoadInventoryEmployeesRequest(lastRowIdInventoryEmployee, toDatenventoryEmployee, nowIdInventoryEmployee, nowInventoryGroup, Home.bindingRight.inventoryEmployee.spInventoryStock.getSelectedItemPosition(), filterInventoryEmployee));
        Home.swipeInventoryEmployeeBottom.setRefreshing(false);
    }

    //Tải thêm khách hàng
    private void loadMoreCustomer() {
        adapterCustomer.notifyDataSetChanged();
        int countCustomer = LocalDB.inst().countCustomer(Home.nowIdCustomer);
        if (countCustomer > 0) {
            //load offline
            customerArrayList = LocalDB.inst().loadCustomer(Home.nowRoute, Home.filterCustomer);
            adapterCustomer.notifyDataSetChanged();
        } else if (countCustomer == 0) {
            //load online
            EventPool.control().enQueue(new EventType.EventLoadCustomersRequest(Home.nowRoute, Home.filterCustomer, lastRowIdCustomer, Home.nowIdCustomer));
        } else if (countCustomer == -1) {
            //sync data
            MyMethod.showToast(context, "Yêu cầu đồng bộ dữ liệu");
            LocalDB.inst().deleteSyncData();
            LayoutLoadingManager.Show_OnLoading(Home.loadingCustomer, context.getString(R.string.load_route), 30);
            EventPool.control().enQueue(new EventType.EventSyncDataRequest(0, -1));
            MyMethod.isSyncDating = true;
        }
        Home.swipeCustomerBottom.setRefreshing(false);
    }

    //Tải thêm giao dịch
    private void loadMoreTransaction() {
        MyMethod.isLoadTransactionByID = false;
        adapterTransaction.notifyDataSetChanged();
        MyMethod.isLoadTransactionByIDInMessage = false;
        EventPool.control().enQueue(new EventType.EventLoadTransactionsRequest(lastRowIdTransaction, fromDateTransaction, nowIdEmployeeTransaction, filterTransaction, false, nowTransactionStatus));
        Home.swipeTransactionBottom.setRefreshing(false);
    }

    @Override
    public void onStop() {
        Log.w(TAG, "onStop begin");
        super.onStop();
        Log.d(TAG, "onStop end");
        try {
            LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Log.w(TAG, "onViewStateRestored begin");
        try {
            LocalBroadcastManager.getInstance(context).registerReceiver(receiver, new IntentFilter("OnFire"));
        } catch (Exception e) {
        }
        Log.w(TAG, "onViewStateRestored end");
    }

    boolean isRegisterControl = false;

    @Override
    public void onResume() {
        Log.w(TAG, "onResume begin");
        super.onResume();
        try {
            LocalBroadcastManager.getInstance(context).registerReceiver(receiver, new IntentFilter("OnFire"));
        } catch (Exception e) {
        }
        if (MyMethod.isVisible(Home.bindingRight.inStore.linearInStore)) {
            //Nếu trong màn hình thì refresh list transaction
            if (PhoneState.inst().isWifi() != 1 && PhoneState.inst().is3G() != 1) {
                transactionLineInStoreArrayList.clear();
                adapterTransactionLineInStore.notifyDataSetChanged();
                transactionLineInStoreArrayList = LocalDB.inst().loadTransactionLine(nowCustomer.id);
                Collections.sort(transactionLineArrayList, new Comparator<TransactionLine>() {
                    @Override
                    public int compare(TransactionLine lhs, TransactionLine rhs) {
                        if (lhs.modified_date > rhs.modified_date) {
                            return -1;
                        } else {
                            if (lhs.modified_date == rhs.modified_date) {
                                return 0;
                            } else {
                                return 1;
                            }
                        }
                    }
                });
                adapterTransactionLineInStore.setItems(transactionLineInStoreArrayList);
                adapterTransactionLineInStore.notifyDataSetChanged();
            } else {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        EventPool.control().enQueue(new EventType.EventLoadTransactionLinesInStoreRequest(nowCustomer.id, -1));
                    }
                }, 1000);
            }

        }
        Log.w(TAG, "onResume end");

    }

    @Override
    public void onDestroy() {
        Log.w(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        Log.w(TAG, "onDestroyView");
        super.onDestroyView();
    }

    //region ImageProcess
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {//Nếu thành công
            switch (requestCode) {
                case ACTION_MAP:
                    Toast.makeText(context, "result: " + data, Toast.LENGTH_SHORT).show();
                    Home.location = data.getParcelableExtra("Location");
                    Home.bindingRight.checkin.txtAddressIn.setText(data.getStringExtra("Address"));
                    break;
                case ACTION_TAKE_CARMERA: {
                    BitmapFactory.Options options;
                    try {
                        options = new BitmapFactory.Options();
                        options.inSampleSize = 16;// 1/16 of origin image size from width and height
                        Home.bitmapImage = BitmapFactory.decodeFile(imagePath, options);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Home.imagePhotoIn.setVisibility(View.VISIBLE);
                    Home.imagePhotoIn.setImageBitmap(Home.bitmapImage);
                }
                break;
                case Home.ACTION_PHOTO_CUSTOMER: {
                    BitmapFactory.Options options;
                    try {
                        options = new BitmapFactory.Options();
                        options.inSampleSize = 16;// 1/16 of origin image size from width and height
                        photoCustomer = BitmapFactory.decodeFile(imagePathCustomer, options);
                        nowCustomer.imageThumb = photoCustomer;
                        nowCustomer.imageUrl = imagePathCustomer;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (Home.bindingRight.customerUpdate.dialogCustomerPhoto != null)
                        Home.bindingRight.customerUpdate.dialogCustomerPhoto.setImageBitmap(photoCustomer);
                }
                break;
                case Home.ACTION_PHOTO_CUSTOMER_UPDATE: {
                    BitmapFactory.Options options;
                    try {
                        options = new BitmapFactory.Options();
                        options.inSampleSize = 16;// 1/16 of origin image size from width and height
                        photoCustomer = BitmapFactory.decodeFile(imagePathCustomer, options);
                        nowCustomer.imageThumb = photoCustomer;
                        nowCustomer.imageUrl = imagePathCustomer;
                        Home.isUpdateCustomerInMap = true;
                        MyMethod.UpdateCustomerLocation = false;
                        MyMethod.UpdateCustomerImage = true;
                        MyMethod.UpdateCustomerRoute = false;
                        LayoutLoadingManager.Show_OnLoading(Home.loadingUpdateImage, context.getString(R.string.update_image), 30);
                        EventPool.control().enQueue(new EventType.EventUpdateCustomerRequest(nowCustomer));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (Home.bindingRight.customerUpdate.dialogCustomerPhoto != null)
                        Home.bindingRight.customerUpdate.dialogCustomerPhoto.setImageBitmap(photoCustomer);
                }
                break;
                case ACTION_PHOTO_STORE: {
                    BitmapFactory.Options options;
                    try {
                        options = new BitmapFactory.Options();
                        options.inSampleSize = 16;// 1/16 of origin image size from width and height
                        photoStore = BitmapFactory.decodeFile(imageStorePath, options);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    img_store_camera.setImageBitmap(MyMethod.cropCircleBitmap(photoStore));
                }
                break;
                case ACTION_PHOTO_CREATE_CUSTOMER: {
                    BitmapFactory.Options options;
                    try {
                        options = new BitmapFactory.Options();
                        options.inSampleSize = 16;// 1/16 of origin image size from width and height
                        photoCreateCustomer = BitmapFactory.decodeFile(imageCreateCustomerPath, options);
                        nowCustomer.imageThumb = photoCreateCustomer;
                        nowCustomer.imageUrl = imageCreateCustomerPath;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Home.bindingRight.createCustomer.imageCreateCustomer.setImageBitmap(MyMethod.cropCircleBitmap(photoCreateCustomer));
                }
                break;
                case REQUEST_CONNECT_DEVICE: {
                    String address = data.getExtras()
                            .getString(DeviceBluetooth.EXTRA_DEVICE_ADDRESS);
                    Home.con_dev = Home.mService.getDevByMac(address);
                    Home.mService.connect(Home.con_dev);
                    Home.isAppLockStop = false;
                }
                break;
                case REQUEST_ENABLE_BT: {
                    MyMethod.showToast(context, context.getString(R.string.open_bth_success));
                    Intent serverIntent = new Intent(context, DeviceBluetooth.class);
                    startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                }
                break;
                case OPENWIFI: {
                    MyMethod.showToast(context, context.getString(R.string.open_wifi_success));
                    Home.isAppLockStop = false;
                }
                break;
                case OPEN3G: {
                    MyMethod.showToast(context, context.getString(R.string.open_3g_success));
                    Home.isAppLockStop = false;
                }
                break;
                case OPENGPS: {
                    MyMethod.showToast(context, context.getString(R.string.open_gps_success));
                    Home.isAppLockStop = false;
                }
                break;
                case ACTION_PHOTO_NOT_ORDER: {
                    BitmapFactory.Options options;
                    try {
                        options = new BitmapFactory.Options();
                        options.inSampleSize = 16;// 1/16 of origin image size from width and height
                        photoNotOrder = BitmapFactory.decodeFile(imageNotOrderPath, options);
                        MyMethod.isReportNotOrder = true;
                        MyMethod.CheckInCustomer = false;
                        MyMethod.isFinishInTransactionDetail = false;
                        MyMethod.isCheckInInTransactionDetail = false;
                        MyMethod.isNoteInStore = false;
                        MyMethod.isReportStore = false;
                        MyMethod.isOrderInStorePressed = false;
                        EventPool.control().enQueue(new EventType.EventReportRequest(Const.LocationVisitedType.CheckInStore, nowCustomer.no_, imageNotOrderPath, Home.location, Home.nowTransactionLine.note, photoNotOrder));
                        photoNotOrder = null;
                        imageNotOrderPath = null;

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //Gửi giao dịch ko đặt hàng


                }
                break;
                default:
                    break;
            }
        } else {//Nếu thất bại thì trả về nguyên gốc giá trị

            switch (requestCode) {
                case ACTION_TAKE_CARMERA: {
                    imagePath = "";
                }
                break;
                case Home.ACTION_PHOTO_CUSTOMER: {
                    imagePathCustomer = "";
                }
                break;
                case Home.ACTION_PHOTO_CUSTOMER_UPDATE: {
                    imagePathCustomer = "";
                    Home.bindingHome.txtTile.setText(context.getString(R.string.customer));
                    Home.LayoutMyManager.ShowLayout(RightFragment.Layouts.MapCustomerCheckIn);
                }
                break;
                case ACTION_PHOTO_STORE: {
                    imageStorePath = "";
                }
                break;
                case ACTION_PHOTO_CREATE_CUSTOMER: {
                    imageCreateCustomerPath = "";
                }
                break;
                case REQUEST_CONNECT_DEVICE: {
                }
                break;
                case REQUEST_ENABLE_BT: {
                }
                break;
                case OPENWIFI: {
                }
                break;
                case OPEN3G: {
                }
                break;
                case OPENGPS: {
                }
                break;
                default:
                    break;
            }
        }

    }

    private void takePhoto(PHOTOS photo) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File fileDir = new File(Environment.getExternalStorageDirectory()
                + "/" + Const.FOLDERDATA);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        File carmeraFile = null;
        Uri imageCarmeraUri = null;
        switch (photo) {
            case CheckIn:
                imagePath = Environment.getExternalStorageDirectory() + "/" + Const.FOLDERDATA + "/" + "IN_"
                        + System.currentTimeMillis() + ".jpg";
                carmeraFile = new File(imagePath);
                imageCarmeraUri = Uri.fromFile(carmeraFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT,
                        imageCarmeraUri);
                try {
                    intent.putExtra("return-data", true);

                    this.startActivityForResult(intent, ACTION_TAKE_CARMERA);
                } catch (ActivityNotFoundException e) {
                    // Do nothing for now
                }
                break;
            case CheckOut:
                imagePath = Environment.getExternalStorageDirectory() + "/" + Const.FOLDERDATA + "/" + "OUT_"
                        + System.currentTimeMillis() + ".jpg";
                carmeraFile = new File(imagePath);
                imageCarmeraUri = Uri.fromFile(carmeraFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT,
                        imageCarmeraUri);
                try {
                    intent.putExtra("return-data", true);

                    this.startActivityForResult(intent, ACTION_TAKE_CARMERA);
                } catch (ActivityNotFoundException e) {
                    // Do nothing for now
                }
                break;
            case Store:
                imageStorePath = Environment.getExternalStorageDirectory() + "/" + Const.FOLDERDATA + "/" + "STORE-" + nowCustomer.name
                        + System.currentTimeMillis() + ".jpg";
                carmeraFile = new File(imageStorePath);
                imageCarmeraUri = Uri.fromFile(carmeraFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT,
                        imageCarmeraUri);
                try {
                    intent.putExtra("return-data", true);
                    this.startActivityForResult(intent, ACTION_PHOTO_STORE);
                } catch (ActivityNotFoundException e) {
                    // Do nothing for now
                }
                break;
            case CreateCustomer:
                imageCreateCustomerPath = Environment.getExternalStorageDirectory() + "/" + Const.FOLDERDATA + "/" + Home.nowSerialno
                        + ".jpg";
                carmeraFile = new File(imageCreateCustomerPath);
                imageCarmeraUri = Uri.fromFile(carmeraFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT,
                        imageCarmeraUri);
                try {
                    intent.putExtra("return-data", true);
                    this.startActivityForResult(intent, ACTION_PHOTO_CREATE_CUSTOMER);
                } catch (ActivityNotFoundException e) {
                    // Do nothing for now
                }
                break;
            case Customer:
                imagePathCustomer = Environment.getExternalStorageDirectory() + "/" + Const.FOLDERDATA + "/" + nowCustomer.name
                        + System.currentTimeMillis() + ".jpg";
                carmeraFile = new File(imagePathCustomer);
                imageCarmeraUri = Uri.fromFile(carmeraFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT,
                        imageCarmeraUri);
                try {
                    intent.putExtra("return-data", true);
                    this.startActivityForResult(intent, Home.ACTION_PHOTO_CUSTOMER);
                } catch (ActivityNotFoundException e) {
                    // Do nothing for now
                }
                break;
            case CustomerUpdate:
                imagePathCustomer = Environment.getExternalStorageDirectory() + "/" + Const.FOLDERDATA + "/" + nowCustomer.id
                        + System.currentTimeMillis() + ".jpg";
                carmeraFile = new File(imagePathCustomer);
                imageCarmeraUri = Uri.fromFile(carmeraFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT,
                        imageCarmeraUri);
                Glide.with(context).load(carmeraFile).into(Home.bindingRight.updateImageCustomer.imgUpdateCustomerImage);
                Home.LayoutMyManager.ShowLayout(Layouts.UpdateImage);
                try {
                    intent.putExtra("return-data", true);
                    this.startActivityForResult(intent, Home.ACTION_PHOTO_CUSTOMER_UPDATE);
                } catch (ActivityNotFoundException e) {
                    // Do nothing for now
                }
                break;
            case NotOrder:
                imageNotOrderPath = Environment.getExternalStorageDirectory() + "/" + Const.FOLDERDATA + "/NotOrder" + System.currentTimeMillis()
                        + ".jpg";
                carmeraFile = new File(imageNotOrderPath);
                imageCarmeraUri = Uri.fromFile(carmeraFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT,
                        imageCarmeraUri);
                try {
                    intent.putExtra("return-data", true);
                    this.startActivityForResult(intent, ACTION_PHOTO_NOT_ORDER);
                } catch (ActivityNotFoundException e) {
                    // Do nothing for now
                }
                break;
            default:
                break;
        }


    }

    //endregion
    private boolean isDateOK() {
        if (from == null) {
            MyMethod.showToast(context, context.getString(R.string.please_select_from_date));
            return false;
        }
        if (to == null) {
            MyMethod.showToast(context, context.getString(R.string.please_select_to_date));
            return false;
        }
        return true;
    }

    public static boolean flagOutStore = false;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_accept_work:
                AlertDialog.Builder builderWork = new AlertDialog.Builder(context);
                builderWork.setMessage("Xác nhận nhận công việc  " + nowTransaction.description)
                        .setCancelable(false)
                        .setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //Xu li nhan cong viec
                                LayoutLoadingManager.Show_OnLoading(Home.bindingRight.transactionDetail.TransactionLineLoadingView, getString(R.string.accepting), 30);
                                EventPool.control().enQueue(new EventType.EventAcceptWorkRequest(nowTransaction.rowId));
                                dialog.dismiss();
                            }
                        });
                AlertDialog alertWork = builderWork.create();
                alertWork.show();
                break;
            case R.id.btn_reason_accept:
                //Xử lí chấp nhận lý do rời cửa hàng
                String content = "";
                int id = 0, type = 0;
                ArrayList<ReasonNotOrder> arrayList = reasonArrayList;
                boolean hasReason = false;//biến kiểm tra có lý do hay không
                for (int i = 0; i < arrayList.size(); i++) {
                    if (arrayList.get(i).isSelected) {
                        hasReason = true;
                        content = arrayList.get(i).content;
                        id = arrayList.get(i).id;
                        type = arrayList.get(i).type;
                    }
                }
                if (hasReason) {
                    Home.nowTransactionLine.note = content;
                    Home.nowTransactionLine.id_ExtNo_ = id;
                    Home.nowTransactionLine.id_customer = nowCustomer.id;
                    Home.nowTransactionLine.id_transaction_define = Const.TransactionType.NotOrder.getValue();
                    Home.nowTransactionLine.status = 0;

                    if (Home.location != null) {
                        Home.nowTransactionLine.latitude = Home.location.getLatitude();
                        Home.nowTransactionLine.longitude = Home.location.getLongitude();
                    }
                    if (type == 1) {//Mở chụp ảnh
                        takePhoto(PHOTOS.NotOrder);
                    } else {
                        //Gửi giao dịch ko đặt hàng
                        if (Home.handler != null && Home.runnableStore != null) {
                            Home.handler.removeCallbacks(Home.runnableStore);
                        }
                        if (MyMethod.isCheckInCustomerTransactionDetail) {
                            showLayout(Layouts.TransactionDetail, context);
                            EventPool.control().enQueue(new EventType.EventLoadTransactionLinesRequest(nowTransaction.rowId));
                        } else {
                            showLayout(Layouts.Customer, context);
                        }
                        LayoutLoadingManager.Show_OnLoading(Home.bindingRight.reasonNotOrder.ReasonLoadingView, context.getString(R.string.sending), 30);

                        EventPool.control().enQueue(new EventType.EventSendTransactionRequest(Home.nowTransactionLine));

                    }
                } else {
                    MyMethod.showToast(context, getString(R.string.please_choice_reason));
                }

                break;
            case R.id.btn_reason_exit:
                //Xử lí hủy lý do rời cửa hàng
                Home.bindingHome.btnComeBack.setSoundEffectsEnabled(false);
                Home.bindingHome.btnComeBack.performClick();
                break;

            case R.id.btn_zoom_in_map:
                //Zoom map len
                if (googleMap != null) {
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(Home.currentPostition == null ? googleMap.getCameraPosition().target : Home.currentPostition, Const.ZoomInMapLevel);
                    googleMap.animateCamera(cameraUpdate);
                }
                break;
            case R.id.btn_zoom_out_map:
                //Zoom map len
                if (googleMap != null) {
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(Home.currentPostition == null ? googleMap.getCameraPosition().target : Home.currentPostition, Const.ZoomOutMapLevel);
                    googleMap.animateCamera(cameraUpdate);
                }
                break;
            case R.id.update_again:
                LayoutLoadingManager.Show_OnLoading(Home.loadingUpdateImage, context.getString(R.string.update_image), 30);
                EventPool.control().enQueue(new EventType.EventUpdateCustomerRequest(nowCustomer));
                //gửi lại hình khi lỗi
                break;
            case R.id.update_take_again:
                //chụp lại hình khi lỗi
                takePhoto(PHOTOS.CustomerUpdate);
                break;
            case R.id.img_pre_sale:
                //In đơn hàng
                if (Home.orderDetailArrayList.size() != 0) {
                    //Có dữ liệu thì in
                    printerOrder();
                }
                break;
            case R.id.img_pre_sale_detail:
                //In đơn hàng
                if (Home.orderDetailArrayList.size() != 0) {
                    //Có dữ liệu thì in
                    printerOrder();
                }
                break;
            case R.id.btn_accept_approval:
                //chấp nhận quyền truy cập cho 1 nhân viên
                LayoutLoadingManager.Show_OnLoading(Home.loadingApprovalButton, context.getString(R.string.approving), 30);
                EventPool.control().enQueue(new EventType.EventApprovalApplockRequest(nowApproval.id_request, 1));
                break;
            case R.id.btn_accept_company_aproval:
                //chấp nhận quyền truy cập cho cả công ty
                LayoutLoadingManager.Show_OnLoading(Home.loadingApprovalButton, context.getString(R.string.approving), 30);
                EventPool.control().enQueue(new EventType.EventApprovalApplockRequest(nowApproval.id_request, 2));
                break;
            case R.id.btn_decline_approval:
                //Từ chối quyền truy cập
                LayoutLoadingManager.Show_OnLoading(Home.loadingApprovalButton, context.getString(R.string.approving), 30);
                EventPool.control().enQueue(new EventType.EventApprovalApplockRequest(nowApproval.id_request, 0));
                break;
            case R.id.btn_accept_input_note:
                inputNoteAccept();
                break;
            case R.id.btn_transaction_Note:
                Home.bindingRight.note.edTransactionNote.setText("");
                if (nowTransaction.description == null)
                    txtTitleInputNoteDialog.setText(context.getString(R.string.input_note));
                else txtTitleInputNoteDialog.setText(nowTransaction.description);
                Home.LayoutMyManager.ShowDialog(Layouts.InputNote);
                break;
            case R.id.btn_transaction_check_in:
                //Xu ly ghe tham
                if (imageStorePath != null) imageStorePath = "";
                if (photoStore != null) photoStore = null;
                //--
                nowCustomer = LocalDB.inst().getCustomer(nowTransaction.id_customer);
                updateValueCustomer();
                MyMethod.CheckInCustomer = true;
                MyMethod.isReportNotOrder = false;
                MyMethod.isFinishInTransactionDetail = false;
                MyMethod.isCheckInInTransactionDetail = false;
                MyMethod.isNoteInStore = false;
                MyMethod.isReportStore = false;
                MyMethod.isOrderInStorePressed = false;

                Home.location = null;
                if (googleMap != null) MyMethod.refreshMap(context, googleMap);
                MyMethod.isMapViewImageLocation = false;
                MyMethod.isCheckInCustomerTransactionDetail = true;
                showLayout(Layouts.MapCustomerCheckIn, context);
                Home.mapCustomerCheckinFragment.getMapAsync(this);
                Home.bindingHome.btnComeBack.setText(context.getString(R.string.check_in_customer) + " " + nameCustomer);

                break;
            case R.id.btn_transaction_Write:
                showPopup(POPUP.TransactionAccept, 0);
                break;
            case R.id.btn_customer_get_location:
                //Lấy vị trí khách hàng
                MyMethod.isGetLocationCreateCustomer = true;
                Home.bindingRight.customerUpdateMap.btnUpdateLocationCustomer.setText(context.getString(R.string.get_location));
                Home.LayoutMyManager.ShowLayout(Layouts.MapUpdate);
                Home.mapUpdateFragment.getMapAsync(this);
                MyMethod.refreshMap(context, googleMap);
                MyMethod.showToast(context, context.getString(R.string.location_wait));
                LocationDetector.inst().setHighPrecision(true);
                break;
            case R.id.btn_accept_select_route:
                Home.nowWorkingTime = selectRouteAccept();
                if (MyMethod.selectRouteInCreateCustomer) {
                    //neu trong tao khach hang
                    Home.bindingHome.btnComeBack.setSoundEffectsEnabled(false);
                    Home.bindingHome.btnComeBack.performClick();
                } else {
                    //neu trong cap nhat kh
                    nowCustomer.workingtime = Home.nowWorkingTime;
                    showLayout(Layouts.MapCustomerCheckIn, context);
                    Home.isUpdateCustomerInMap = true;
                    MyMethod.UpdateCustomerLocation = false;
                    MyMethod.UpdateCustomerImage = false;
                    MyMethod.UpdateCustomerRoute = true;
                    LayoutLoadingManager.Show_OnLoading(Home.loadingUpdateCustomer, context.getString(R.string.update_route), 30);

                    EventPool.control().enQueue(new EventType.EventUpdateCustomerRequest(nowCustomer));
                }
                break;
            case R.id.btn_customer_select_route:
                //chọn tuyến bán hàng
                MyMethod.selectRouteInCreateCustomer = true;
                Home.LayoutMyManager.ShowDialog(Layouts.SelectRoute);

                break;
            case R.id.btn_create_customer:
                //Tạo khách hàng
                confirmCreateCustomer();
                break;
            case R.id.btn_reset_customer_field:
                //Nhập lại
                confirmReset();

                break;
            case R.id.image_create_customer:
                //Chụp ảnh tạo khách hàng
                if (photoCreateCustomer == null)//Nếu chưa chụp
                    takePhoto(PHOTOS.CreateCustomer);
                else {//hiện popup
                    showPopup(POPUP.CreateCustomer, 0);
                }
                break;
            case R.id.img_create_customer:
                Home.bindingHome.btnComeBack.setText(context.getString(R.string.create_customer_title));
                //Tạo mã kh ngẫu nhiên
                photoCreateCustomer = null;
                Home.bindingRight.createCustomer.imageCreateCustomer.setImageResource(R.drawable.camera_btn);
                Home.bindingRight.createCustomer.btnCustomerGetLocation.setText(context.getString(R.string.get_location));
                btn_create_customer_route.setText(context.getString(R.string.select_route));
                Home.bindingRight.createCustomer.edCustomerName.setText("");
                Home.bindingRight.createCustomer.edCustomerAddress.setText("");
                Home.bindingRight.createCustomer.edCustomerPhone.setText("");
                Random kh = new Random();
                String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ123456789";
                nowCustomer = new Customer();
                nowCustomer.no_ = "KH" + alphabet.charAt(kh.nextInt(alphabet.length())) + alphabet.charAt(kh.nextInt(alphabet.length())) + Model.getServerTime();
                Home.bindingRight.createCustomer.edCustomerNo.setText(nowCustomer.no_);
                Home.LayoutMyManager.ShowLayout(Layouts.CreateCustomer);
                EventPool.control().enQueue(new EventType.EventLoadCitysRequest());

                break;
            case R.id.btn_map_checkin_update_route:
                //Cap nhat route
                //chọn tuyến bán hàng
                MyMethod.selectRouteInCreateCustomer = false;
                updateSelectedRoute(nowCustomer.workingtime);
                Home.LayoutMyManager.ShowDialog(Layouts.SelectRoute);
                break;
            case R.id.btn_map_checkin_update_location:
                //Cập nhật vị trí khách hàng
                if (Home.location != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Xác nhận cập nhật vị trí cho " + nowCustomer.name)
                            .setCancelable(false)
                            .setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            })
                            .setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    nowCustomer.latitude = Home.location.getLatitude();
                                    nowCustomer.longitude = Home.location.getLongitude();
                                    nowCustomer.imageThumb = null;
                                    nowCustomer.imageUrl = null;
                                    LayoutLoadingManager.Show_OnLoading(Home.loadingMapCustomerCheckIn, context.getString(R.string.update), 30);
                                    Home.isUpdateCustomerInMap = true;
                                    MyMethod.UpdateCustomerLocation = true;
                                    MyMethod.UpdateCustomerImage = false;
                                    MyMethod.UpdateCustomerRoute = false;
                                    EventPool.control().enQueue(new EventType.EventUpdateCustomerRequest(nowCustomer));
                                    dialog.dismiss();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    MyMethod.refreshMap(context, googleMap);
                    MyMethod.showToast(context, context.getString(R.string.location_wait));
                    LocationDetector.inst().setHighPrecision(true);
                }

                break;
            case R.id.btn_map_checkin_update_image:
                //Cập nhật hình ảnh khách hàng
                takePhoto(PHOTOS.CustomerUpdate);
                break;
            case R.id.store_camera:
                // Xử lí chụp hình trong cửa hàng
                if (photoStore == null)//Nếu chưa chụp
                    takePhoto(PHOTOS.Store);
                else {//hiện popup
                    showPopup(POPUP.Store, 0);
                }
                break;
            case R.id.ref_transaction_phonecall:
                //Gọi điện trong chi tiết giao dịch
                MyMethod.callPhone(context, Home.bindingRight.transactionDetail.refTransactionPhone.getText().toString());
                break;
            case R.id.ref_transaction_map:
                try {
                    if (nowTransaction.latitude > 0) {
                        Home.bindingHome.btnComeBack.setText(context.getString(R.string.location) + nowTransaction.description);
                        MyMethod.isTransactionMapView = true;
                        latLngTransaction = new LatLng(nowTransaction.latitude, nowTransaction.longtitude);
                        showLayout(Layouts.MapCustomerView, context);
                        LayoutLoadingManager.Show_OnLoading(Home.loadingMapCustomerCheckIn, context.getString(R.string.load_map), 30);
                        EventPool.control().enQueue(new EventType.EventLoadLocationVisitedRequest(Home.nowTransactionLine.location_ref_id, Home.nowTransactionLine.id_employee));

                    } else MyMethod.showToast(context, context.getString(R.string.location_none));
                } catch (Exception e) {
                    MyMethod.showToast(context, context.getString(R.string.location_none));
                }
                break;
            case R.id.btn_out_store:
                flagOutStore = true;

                //Xử lí rời cửa hàng (gửi event giao dịch không đặt hàng)
                if (MyMethod.isHasOrder) {
                    if (Home.handler != null && Home.runnableStore != null) {
                        Home.handler.removeCallbacks(Home.runnableStore);
                    }
                    android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(context);
                    alertDialogBuilder.setMessage(context.getString(R.string.confirm_out_store));
                    alertDialogBuilder.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int arg1) {
                            dialog.dismiss();
                            if (!MyMethod.isOrderPhone) {//Nếu không phải đặt hàng qua đt thì gửi giao dịch rời cửa hàng
                                if (photoStore != null) {
                                    MyMethod.isReportStore = true;
                                    MyMethod.isFinishInTransactionDetail = false;
                                    MyMethod.isCheckInInTransactionDetail = false;
                                    MyMethod.isNoteInStore = false;
                                    MyMethod.CheckInCustomer = false;
                                    MyMethod.isReportNotOrder = false;
                                    MyMethod.isOrderInStorePressed = false;
                                    EventPool.control().enQueue(new EventType.EventReportRequest(Const.LocationVisitedType.CheckInStore, nowCustomer.no_, imageStorePath, Home.location, edInStoreNote.getText().toString(), photoCustomer));
                                    photoCustomer = null;
                                    imageStorePath = null;
                                    img_store_camera.setImageResource(R.drawable.camera_btn);
                                }
                                if (!MyMethod.isReportedStore) {
                                    Home.nowTransactionLine.location_ref_id = (int) LocalDB.inst().addTracking(new TrackingItem(Home.location, (byte) 2));
                                }
                                Home.nowTransactionLine.id_customer = nowCustomer.id;
                                Home.nowTransactionLine.status = 0;
                                Home.nowTransactionLine.note = "Rời cửa hàng";
                                Home.nowTransactionLine.id_ExtNo_ = 0;
                                Home.nowTransactionLine.id_transaction_define = Const.TransactionType.CheckOut.getValue();
                                Home.nowTransactionLine.latitude = Home.location.getLatitude();
                                Home.nowTransactionLine.longitude = Home.location.getLongitude();
                                if (MyMethod.isCheckInCustomerTransactionDetail) {
                                    showLayout(Layouts.TransactionDetail, context);
                                    EventPool.control().enQueue(new EventType.EventLoadTransactionLinesRequest(nowTransaction.rowId));
                                } else {
                                    showLayout(Layouts.Customer, context);
                                }
                                LayoutLoadingManager.Show_OnLoading(Home.loadingInStore, context.getString(R.string.out_storing), 30);
                                EventPool.control().enQueue(new EventType.EventSendTransactionRequest(Home.nowTransactionLine));
                            } else {
                                if (MyMethod.isCheckInCustomerTransactionDetail) {
                                    showLayout(Layouts.TransactionDetail, context);
                                    EventPool.control().enQueue(new EventType.EventLoadTransactionLinesRequest(nowTransaction.rowId));

                                } else {
                                    showLayout(Layouts.Customer, context);
                                }
                            }
                        }
                    });
                    alertDialogBuilder.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    android.support.v7.app.AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                } else {//Nếu ko có đơn hàng
                    LayoutLoadingManager.Show_OnLoading(Home.bindingRight.reasonNotOrder.ReasonLoadingView, context.getString(R.string.load_reason), 30);
                    Home.LayoutMyManager.ShowLayout(Layouts.ReasonNotOrder);
                    if (LocalDB.inst().countReason() > 0) {//Lấy dữ liệu offline
                        reasonArrayList.clear();
                        reasonArrayList = LocalDB.inst().loadReason();
                        adapterReasonNotOrder.setItems(reasonArrayList);
                        adapterReasonNotOrder.notifyDataSetChanged();
                        LayoutLoadingManager.Show_OffLoading(Home.bindingRight.reasonNotOrder.ReasonLoadingView);
                    } else {
                        //Nếu ko có thì load online
                        EventPool.control().enQueue(new EventType.EventLoadReasonNotOrdersRequest());
                    }
                }
                break;
            case R.id.btn_inventory_report:
                //Xử lí báo cáo tồn kho
                Home.orderDetailArrayList.clear();
                Home.orderListProductAdapter.notifyDataSetChanged();
                updateInventoryView();
                MyMethod.isInventoryReport = true;
                MyMethod.isInventoryInput = false;
                MyMethod.isOrder = false;
                MyMethod.isProductDisplay = false;
                MyMethod.isProductSample = false;
                MyMethod.isOrderIncurred = false;
                if (photoStore != null) {
                    MyMethod.isReportStore = true;
                    MyMethod.CheckInCustomer = false;
                    MyMethod.isFinishInTransactionDetail = false;
                    MyMethod.isCheckInInTransactionDetail = false;
                    MyMethod.isNoteInStore = false;
                    MyMethod.isReportNotOrder = false;
                    MyMethod.isOrderInStorePressed = true;
                    EventPool.control().enQueue(new EventType.EventReportRequest(Const.LocationVisitedType.CheckInStore, nowCustomer.no_, imageStorePath, Home.location, edInStoreNote.getText().toString(), photoCustomer));
                    photoCustomer = null;
                    imageStorePath = null;
                    img_store_camera.setImageResource(R.drawable.camera_btn);
                } else showLayout(Layouts.Order, context);

                break;
//            case R.id.btn_order_incurred:
//                //Phát sinh đơn hàng
//                MyMethod.isOrderIncurred = true;
//                MyMethod.isInventoryReport = false;
//                MyMethod.isInventoryInput = false;
//                MyMethod.isOrder = false;
//                MyMethod.isProductDisplay = false;
//                MyMethod.isProductSample = false;
//                nowOrder.status = 0;
//                nowOrder.note = "Phát sinh đơn hàng tại " + nowCustomer.name;
//                nowOrder.amount = 0;// loại bỏ E khi convert sang chuỗi (big decimal)
//                nowOrder.id_customer = nowCustomer.id;
//                if (Model.inst().getConfigValue(Const.ConfigKeys.PreSale) == null)
//                    nowOrder.document_type = 0;
//                else
//                    nowOrder.document_type = (Integer.parseInt(Model.inst().getConfigValue(Const.ConfigKeys.PreSale)) == 0) ? 0 : 3;
//                LayoutLoadingManager.Show_OnLoading(Home.loadingSendOrder, context.getString(R.string.incurring), 30);
//                EventPool.control().enQueue(new EventType.EventSendOrderRequest(nowOrder, Home.orderDetailArrayList));
//
//                break;
            case R.id.btn_order_report:
                MyMethod.isNoteInStore = true;
                MyMethod.isReportStore = false;
                MyMethod.isOrderInStorePressed = false;
                MyMethod.isReportNotOrder = false;
                MyMethod.CheckInCustomer = false;
                MyMethod.isCheckInInTransactionDetail = false;
                MyMethod.isFinishInTransactionDetail = false;
                showLayout(Layouts.CheckIn, context);
                break;
            case R.id.btn_store_library:
                Intent library = new Intent(context, LibraryActivity.class);
                startActivity(library);
                break;
            case R.id.btn_store_survey_customer:
                Intent surveyCustomer = new Intent(context, SurveyActivity.class);
                surveyCustomer.putExtra("ID", nowCustomer.id);
                surveyCustomer.putExtra("RootID", nowCustomer.id);
                startActivity(surveyCustomer);
                break;
            case R.id.btn_store_survey_not_customer:
                Intent surveyNotCustomer = new Intent(context, SurveyActivity.class);
                surveyNotCustomer.putExtra("ID", 0);
                surveyNotCustomer.putExtra("RootID", nowCustomer.id);
                startActivity(surveyNotCustomer);
                break;
            case R.id.btn_in_store_sample:
                //Hàng mẫu
                Home.orderDetailArrayList.clear();
                Home.orderListProductAdapter.notifyDataSetChanged();
                Home.bindingRight.order.titleDetailOrder.setText(context.getString(R.string.order_sample_title));
                updateOrderView();
                MyMethod.isInventoryReport = false;
                MyMethod.isInventoryInput = false;
                MyMethod.isOrder = false;
                MyMethod.isProductSample = true;
                MyMethod.isProductDisplay = false;

                if (photoStore != null) {
                    MyMethod.isReportStore = true;
                    MyMethod.CheckInCustomer = false;
                    MyMethod.isFinishInTransactionDetail = false;
                    MyMethod.isCheckInInTransactionDetail = false;
                    MyMethod.isNoteInStore = false;
                    MyMethod.isReportNotOrder = false;
                    MyMethod.isOrderInStorePressed = true;
                    EventPool.control().enQueue(new EventType.EventReportRequest(Const.LocationVisitedType.CheckInStore, nowCustomer.no_, imageStorePath, Home.location, edInStoreNote.getText().toString(), photoCustomer));
                    photoCustomer = null;
                    imageStorePath = null;
                    img_store_camera.setImageResource(R.drawable.camera_btn);
                } else {
                    showLayout(Layouts.Order, context);
                }
                break;
            case R.id.btn_in_stote_display:
                //Hàng trưng bày/ poster
                Home.orderDetailArrayList.clear();
                Home.orderListProductAdapter.notifyDataSetChanged();
                Home.bindingRight.order.titleDetailOrder.setText(context.getString(R.string.order_display_title));
                updateOrderView();
                MyMethod.isInventoryReport = false;
                MyMethod.isInventoryInput = false;
                MyMethod.isOrder = false;
                MyMethod.isProductSample = false;
                MyMethod.isProductDisplay = true;
                MyMethod.isOrderIncurred = false;
                if (photoStore != null) {
                    MyMethod.isReportStore = true;
                    MyMethod.isNoteInStore = false;
                    MyMethod.isCheckInInTransactionDetail = false;
                    MyMethod.isReportNotOrder = false;
                    MyMethod.CheckInCustomer = false;
                    MyMethod.isOrderInStorePressed = true;
                    EventPool.control().enQueue(new EventType.EventReportRequest(Const.LocationVisitedType.CheckInStore, nowCustomer.no_, imageStorePath, Home.location, edInStoreNote.getText().toString(), photoCustomer));
                    photoCustomer = null;
                    imageStorePath = null;
                    img_store_camera.setImageResource(R.drawable.camera_btn);
                } else {
                    showLayout(Layouts.Order, context);
                    Home.bindingRight.order.orderCustomerAddProduct.performClick();
                    Home.bindingRight.order.orderCustomerAddProduct.setSoundEffectsEnabled(false);
                }
                break;
            case R.id.btn_order_in_store:
                //Xử lí đặt đơn trong cửa hàng
                Home.nowAmount = 0;//refresh về 0
                Home.nowAmountSale = 0;
                Home.orderDetailArrayList.clear();
                Home.orderListProductAdapter.notifyDataSetChanged();
                Home.bindingRight.order.titleDetailOrder.setText(context.getString(R.string.order_sale_title));
                updateOrderView();
                MyMethod.isInventoryReport = false;
                MyMethod.isInventoryInput = false;
                MyMethod.isOrder = true;
                MyMethod.isProductSample = false;
                MyMethod.isProductDisplay = false;
                MyMethod.isOrderIncurred = false;
                if (photoStore != null) {
                    MyMethod.isReportStore = true;
                    MyMethod.CheckInCustomer = false;
                    MyMethod.isFinishInTransactionDetail = false;
                    MyMethod.isCheckInInTransactionDetail = false;
                    MyMethod.isNoteInStore = false;
                    MyMethod.isReportNotOrder = false;
                    MyMethod.isOrderInStorePressed = true;
                    EventPool.control().enQueue(new EventType.EventReportRequest(Const.LocationVisitedType.CheckInStore, nowCustomer.no_, imageStorePath, Home.location, edInStoreNote.getText().toString(), photoCustomer));
                    photoCustomer = null;
                    imageStorePath = null;
                    img_store_camera.setImageResource(R.drawable.camera_btn);
                } else {
                    showLayout(Layouts.Order, context);
                    Home.bindingRight.order.orderCustomerAddProduct.performClick();
                    Home.bindingRight.order.orderCustomerAddProduct.setSoundEffectsEnabled(false);
                }

                break;
            case R.id.fabGetAgainCheckInOrder:
                LayoutLoadingManager.Show_OnLoading(Home.loadingMapCustomerCheckIn, context.getString(R.string.get_location), 30);
                LocationDetector.inst().setHighPrecision(true);
                break;
            case R.id.btn_checkin_order:
                //XỬ LÍ ĐẶT HÀNG TRONG GHÉ THĂM KHÁCH HÀNG
//                if (Home.locationManager != null)
//                    Home.locationManager.removeUpdates(this);
                //Bật nút phát sinh đơn hàng:
                //btnIncurredOrder.setEnabled(true);
                switch (Home.bindingRight.mapCheckInCustomer.btnCheckinOrder.getText().toString()) {
                    case "Vào cửa hàng":
                        flagOutStore = false;
                        //Đặt cờ kiểm tran có đặt hàng hay không
                        MyMethod.isHasOrder = false;
                        //Gửi ghé thăm
                        updateStoreValue();
                        //region Đếmthờigiantrongcửahàng
                        Home.handler = new Handler();
                        timeCount = 0;
                        Home.runnableStore = new Runnable() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void run() {
                                Log.v("QueueTimerView", "InStore");
                                try {
                                    timeCount++;
                                    String time = Utils.to2Character(TimeUnit.SECONDS.toHours(timeCount)) + ":"
                                            + Utils.to2Character(TimeUnit.SECONDS.toMinutes(timeCount)
                                            - TimeUnit.HOURS.toMinutes(TimeUnit.SECONDS.toHours(timeCount))) + ":"
                                            + Utils.to2Character(TimeUnit.SECONDS.toSeconds(timeCount) -
                                            TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(timeCount)));
                                    Home.bindingRight.inStore.txtTimeInStore.setText(time);

                                } catch (Exception ex) {
                                    SystemLog.addLog(context, SystemLog.Type.Exception, ex.toString());
                                }
                                Home.handler.postDelayed(this, 1000);
                            }
                        };
                        Home.handler.postDelayed(Home.runnableStore, 1000);

                        MyMethod.isInStore = true;//Đặt cờ đang ở trong CH
                        Home.locationInStore = Home.location;
                        MyMethod.isOrderPhone = false;
                        Home.nowTransactionLine.id_transaction = 0;// chưa biết id transaction nên để 0 lên server kiếm
                        Home.nowTransactionLine.id_customer = nowCustomer.id;
                        Home.nowTransactionLine.status = 0;
                        Home.nowTransactionLine.note = "Ghé thăm";
                        Home.nowTransactionLine.location_ref_id = (int) LocalDB.inst().addTracking(new TrackingItem(Home.location, (byte) 2));
                        Home.nowTransactionLine.id_ExtNo_ = 0;
                        Home.nowTransactionLine.id_transaction_define = Const.TransactionType.CheckIn.getValue();
                        Home.nowTransactionLine.latitude = Home.location.getLatitude();
                        Home.nowTransactionLine.longitude = Home.location.getLongitude();
                        Home.nowTransactionLine.create_date = Model.getServerTime();
                        Home.nowTransactionLine.modified_date = Model.getServerTime();
                        if (Home.nowTransactionLine.refNo_ == null || Home.nowTransactionLine.refNo_.isEmpty()) {
                            Home.nowTransactionLine.refNo_ = Utils.createRefNo_(20);
                        }
                        showLayout(Layouts.GoStore, context);
                        LayoutLoadingManager.Show_OnLoading(Home.loadingMapCustomerCheckIn, context.getString(R.string.go_store), 30);
                        EventPool.control().enQueue(new EventType.EventSendTransactionRequest(Home.nowTransactionLine));
                        LayoutLoadingManager.Show_OnLoading(Home.loadingTransactionLineInStore, context.getString(R.string.load_transaction_history), 30);
                        EventPool.control().enQueue(new EventType.EventLoadTransactionLinesInStoreRequest(nowCustomer.id, -1));
                        break;
                    case "Đặt hàng qua điện thoại":
                        //cần xử lý cửavào thẳng đặt hàng không qua cửa hàng
                        Home.nowTransactionLine.id_transaction = 0;// chưa biết id transaction nên để 0 lên server kiếm
                        MyMethod.isOrderPhone = true;
                        Home.orderDetailArrayList.clear();
                        Home.orderListProductAdapter.notifyDataSetChanged();
                        Home.bindingRight.order.titleDetailOrder.setText(context.getString(R.string.order_sale_title));
                        updateOrderView();
                        MyMethod.isInventoryReport = false;
                        MyMethod.isInventoryInput = false;
                        MyMethod.isOrder = true;
                        MyMethod.isProductSample = false;
                        MyMethod.isOrderIncurred = false;
                        MyMethod.isProductDisplay = false;
                        showLayout(Layouts.Order, context);
                        Home.bindingRight.order.orderCustomerAddProduct.performClick();
                        Home.bindingRight.order.orderCustomerAddProduct.setSoundEffectsEnabled(false);
                        break;
                    default:
                        break;
                }
                break;
            case R.id.btn_accept_input:
                MyMethod.hideKeyboardAll(getActivity());
                if (MyMethod.isInputInOrder) {
                    if (MyMethod.isEdit_Product) // Nếu là sửa khi nhấn lâu
                    {
                        editValueAccept(positionEditOrder);
                    } else { //Nếu là nhập từ bên sản phẩm
                        inputValueAccept();
                    }
                } else {
                    inputValueUpdateOrderAccept(positionEditOrder);
                }
                break;

            case R.id.order_detail_add_product:
                Home.hashListQuantity.clear();
                Home.hashListPrice.clear();
                updateListQuantityPrice();//Cập nhật list sp đang có
                showLayout(Layouts.ProductOfOrder, context);

                MyMethod.isProductOfOrder = true;
                MyMethod.isOrderEditing = true;
                int countProduct = LocalDB.inst().countProduct();
                if (countProduct > 0) {
                    //load offline
                    productArrayList = LocalDB.inst().loadProduct(Home.filterProduct);
                    adapterProduct.setItems(productArrayList);
                    adapterProduct.notifyDataSetChanged();
                } else if (countProduct == 0) {
                    LayoutLoadingManager.Show_OnLoading(Home.loadingProductOfOrder, context.getString(R.string.load_product), 30);
                    EventPool.control().enQueue(new EventType.EventLoadProductsRequest(Const.DocumentType.Sale.getValue(), -1, Home.filterProduct));
                }
                //XU LI THEM HANG TRONG ORDER DETAIL
                break;
            case R.id.order_detail_save_send:
                //XU LI GUI DON HANG TRONG ORDER DETAIL
                if (Home.orderDetailArrayList.size() > 0) {
                    nowOrder.note = Home.edOrderDetailNote.getText().toString();
                    nowOrder.amount = Home.nowAmount;// loại bỏ E khi convert sang chuỗi (big decimal)
                    EventPool.control().enQueue(new EventType.EventUpdateOrderRequest(nowOrder, Home.orderDetailArrayList));
                } else MyMethod.showToast(context, context.getString(R.string.please_order));
                break;
            case R.id.dialog_customer_name_edit:
                openEdit(TYPE.NAME);
                break;
            case R.id.dialog_customer_address_edit:
                openEdit(TYPE.ADDRESS);
                break;
            case R.id.dialog_customer_phone_edit:
                openEdit(TYPE.PHONE);
                break;
            case R.id.dialog_customer_photo_edit:
                openEdit(TYPE.PHOTO);
                break;
            case R.id.dialog_customer_location_edit:
                openEdit(TYPE.LOCATION);
                break;
            case R.id.dialog_customer_update_ok:
                updateCustomer();
                break;
            case R.id.dialog_customer_update_cancel:
                Home.bindingHome.btnComeBack.setSoundEffectsEnabled(false);
                Home.bindingHome.btnComeBack.performClick();
                break;
            case R.id.dialog_customer_location_view:
                //XU LI XEM VI TRI KHACH HANG
                Home.bindingHome.btnComeBack.setText(context.getString(R.string.location) + nameCustomer);
                MyMethod.isTransactionMapView = false;
                showLayout(Layouts.MapCustomerView, context);
                Home.mapCustomerViewFragment.getMapAsync(this);
                break;
            case R.id.dialog_customer_phone_call:
                MyMethod.callPhone(context);
                break;
            case R.id.dialog_customer_photo:
                //XU LI XEM HINH ANH
                break;
            case R.id.dialog_product_photo_detail:
                if (!nowProduct.imageUrl.isEmpty())
                    viewImage(Const.LinkItemImage, nowProduct.imageUrl, nowProduct.name);
                break;
            case R.id.dialog_customer_photo_detail:
                //XU LI XEM HINH ANH
                if (!nowCustomer.imageUrl.isEmpty())
                    viewCustomerImage();
                break;
            case R.id.dialog_customer_checkin:
                MyMethod.CheckInCustomer = true;
                MyMethod.isReportStore = false;
                MyMethod.isFinishInTransactionDetail = false;
                MyMethod.isCheckInInTransactionDetail = false;
                MyMethod.isNoteInStore = false;
                MyMethod.isReportNotOrder = false;
                MyMethod.isReportedStore = false;
                Home.location = null;
                MyMethod.refreshMap(context, googleMap);
                MyMethod.isMapViewImageLocation = false;
                MyMethod.isCheckInCustomerTransactionDetail = false;
                showLayout(Layouts.MapCustomerCheckIn, context);
                Home.mapCustomerCheckinFragment.getMapAsync(this);
                Home.bindingHome.btnComeBack.setText(context.getString(R.string.check_in_customer) + " " + nowCustomer.name);
                break;
            case R.id.dialog_customer_order:
                showLayout(Layouts.Order, context);
                break;
            case R.id.dialog_customer_update:
                Home.bindingHome.btnComeBack.setText(context.getString(R.string.update) + " " + nowCustomer.name);
                showLayout(Layouts.CustomerUpdate, context);
                break;
            case R.id.dialog_product_back:
                Home.bindingHome.btnComeBack.setSoundEffectsEnabled(false);
                Home.bindingHome.btnComeBack.performClick();
                break;
            case R.id.order_customer_add_product:
                //XU LI THEM HANG
                Home.hashListQuantity.clear();
                Home.hashListPrice.clear();
                productArrayList.clear();
                adapterProduct.notifyDataSetChanged();
                showLayout(Layouts.ProductOfOrder, context);

                MyMethod.isProductOfOrder = true;
                MyMethod.isOrderEditing = false;
                int countP = LocalDB.inst().countProduct();
                if (countP > 0) {
                    //load offline
                    productArrayList = LocalDB.inst().loadProduct(Home.filterProduct);
                    adapterProductOfOrder.setItems(productArrayList);
                    adapterProductOfOrder.notifyDataSetChanged();
                } else if (countP == 0) {
                    LayoutLoadingManager.Show_OnLoading(Home.loadingProductOfOrder, context.getString(R.string.load_product), 30);
                    if (MyMethod.isOrder) { //Đặt hàng
                        if (Model.inst().getConfigValue(Const.ConfigKeys.PreSale) == null) {//Nếu null thì Sale
                            EventPool.control().enQueue(new EventType.EventLoadProductsRequest(Const.DocumentType.Sale.getValue(), -1, Home.filterProduct));
                        } else {
                            EventPool.control().enQueue(new EventType.EventLoadProductsRequest(Integer.parseInt(Model.inst().getConfigValue(Const.ConfigKeys.PreSale)) == 0 ? Const.DocumentType.Sale.getValue() : Const.DocumentType.SaleEmployee.getValue(), -1, Home.filterProduct));
                        }
                    } else if (MyMethod.isInventoryInput) {//Đề nghị nhập kho
                        EventPool.control().enQueue(new EventType.EventLoadProductsRequest(Const.DocumentType.InventoryEmployee.getValue(), -1, Home.filterProduct));
                    } else if (MyMethod.isInventoryReport) { // Báo cáo tồn kho
                        EventPool.control().enQueue(new EventType.EventLoadProductsRequest(Const.DocumentType.InventoryStore.getValue(), -1, Home.filterProduct));
                    } else if (MyMethod.isProductSample) {//Hàng mẫu
                        EventPool.control().enQueue(new EventType.EventLoadProductsRequest(Const.DocumentType.ProductSample.getValue(), -1, Home.filterProduct));
                    } else if (MyMethod.isProductDisplay) {//Hàng trưng bày
                        EventPool.control().enQueue(new EventType.EventLoadProductsRequest(Const.DocumentType.ProductDisplay.getValue(), -1, Home.filterProduct));
                    }
                }

                break;
            case R.id.order_customer_save_send:
                //XU LI SAVE SEND
                if (Home.orderDetailArrayList.size() > 0) {

                    nowOrder.status = 0;
                    nowOrder.note = Home.edOrderNote.getText().toString();
                    nowOrder.amount = Home.nowAmount;// loại bỏ E khi convert sang chuỗi (big decimal)
                    if (MyMethod.isInventoryReport) {//Báo cáo tồn kho
                        nowOrder.id_customer = nowCustomer.id;
                        nowOrder.document_type = 1;
                    } else if (MyMethod.isOrder) {//Đặ t hàng
                        nowOrder.id_customer = nowCustomer.id;
                        if (Model.inst().getConfigValue(Const.ConfigKeys.PreSale) == null)
                            nowOrder.document_type = 0;
                        else
                            nowOrder.document_type = (Integer.parseInt(Model.inst().getConfigValue(Const.ConfigKeys.PreSale)) == 0) ? 0 : 3;
                    } else if (MyMethod.isInventoryInput) { // Nhập tồn nv
                        nowOrder.id_customer = 0;
                        if (Model.inst().getConfigValue(Const.ConfigKeys.PreSale) == null) {
                            nowOrder.document_type = 4;//nhập kho công ty
                        } else {
                            nowOrder.document_type = (Integer.parseInt(Model.inst().getConfigValue(Const.ConfigKeys.PreSale)) == 0) ? 4 : 2;
                        }
                    } else if (MyMethod.isProductSample) {
                        nowOrder.id_customer = nowCustomer.id;
                        nowOrder.document_type = 4;
                    } else if (MyMethod.isProductDisplay) {
                        nowOrder.id_customer = nowCustomer.id;
                        nowOrder.document_type = 5;
                    }
                    LayoutLoadingManager.Show_OnLoading(Home.loadingSendOrder, context.getString(R.string.sending), 30);
                    EventPool.control().enQueue(new EventType.EventSendOrderRequest(nowOrder, Home.orderDetailArrayList));
                } else MyMethod.showToast(context, context.getString(R.string.please_order));
                break;
            case R.id.order_product_accept:
                acceptOrder();
                Home.bindingHome.btnComeBack.setSoundEffectsEnabled(false);
                Home.bindingHome.btnComeBack.performClick();
                //XU LI ACCEPT
                break;
            case R.id.order_product_cancel:
                //XU LI CANCEL
                cancelOrder();
                Home.bindingHome.btnComeBack.setSoundEffectsEnabled(false);
                Home.bindingHome.btnComeBack.performClick();
                break;
            case R.id.inventory_employee_to:
                MyMethod.Date = 6;
                CalendarDatePickerDialogFragment toInventoryEmployee = new CalendarDatePickerDialogFragment()
                        .setOnDateSetListener(this)
                        .setFirstDayOfWeek(Calendar.SUNDAY)
//                        .setPreselectedDate(towDaysAgo.getYear(), towDaysAgo.getMonthOfYear() - 1, towDaysAgo.getDayOfMonth())
//                        .setDateRange(minDate, null)
                        .setThemeDark(false);
                toInventoryEmployee.show(getFragmentManager(), FRAG_TAG_DATE_PICKER);
                break;
            case R.id.report_time_from:
                MyMethod.Date = 7;
                CalendarDatePickerDialogFragment fromReportCheckIn = new CalendarDatePickerDialogFragment()
                        .setOnDateSetListener(this)
                        .setFirstDayOfWeek(Calendar.SUNDAY)
//                        .setPreselectedDate(towDaysAgo.getYear(), towDaysAgo.getMonthOfYear() - 1, towDaysAgo.getDayOfMonth())
//                        .setDateRange(minDate, null)
                        .setThemeDark(false);
                fromReportCheckIn.show(getFragmentManager(), FRAG_TAG_DATE_PICKER);
                break;
            case R.id.report_time_to:
                MyMethod.Date = 8;
                CalendarDatePickerDialogFragment toReportCheckIn = new CalendarDatePickerDialogFragment()
                        .setOnDateSetListener(this)
                        .setFirstDayOfWeek(Calendar.SUNDAY)
//                        .setPreselectedDate(towDaysAgo.getYear(), towDaysAgo.getMonthOfYear() - 1, towDaysAgo.getDayOfMonth())
//                        .setDateRange(minDate, null)
                        .setThemeDark(false);
                toReportCheckIn.show(getFragmentManager(), FRAG_TAG_DATE_PICKER);
                break;
            case R.id.report_time_load:
                LayoutLoadingManager.Show_OnLoading(Home.loadingReportCheckIn, context.getString(R.string.load_report_checkin), 30);
                reportCheckInArrayList.clear();
                adapterReportCheckIn.notifyDataSetChanged();
                EventPool.control().enQueue(new EventType.EventLoadReportCheckInsRequest(fromDateReportCheckIn, toDateReportCheckIn, nowIdEmployeeReportCheckIn, -1));
                break;
            case R.id.order_main_to:
                MyMethod.Date = 3;
                CalendarDatePickerDialogFragment toOrder = new CalendarDatePickerDialogFragment()
                        .setOnDateSetListener(this)
                        .setFirstDayOfWeek(Calendar.SUNDAY)
//                        .setPreselectedDate(towDaysAgo.getYear(), towDaysAgo.getMonthOfYear() - 1, towDaysAgo.getDayOfMonth())
//                        .setDateRange(minDate, null)
                        .setThemeDark(false);
                toOrder.show(getFragmentManager(), FRAG_TAG_DATE_PICKER);
                break;
            case R.id.customer_load:
                customerArrayList.clear();
                adapterCustomer.notifyDataSetChanged();
                int countCustomer = LocalDB.inst().countCustomer(Home.nowIdCustomer);
                if (countCustomer > 0) {
                    //load offline
                    customerArrayList = LocalDB.inst().loadCustomer(Home.nowRoute, Home.filterCustomer);
                    adapterCustomer.setItems(customerArrayList);
                    adapterCustomer.notifyDataSetChanged();
                } else if (countCustomer == 0) {
                    //load online
                    LayoutLoadingManager.Show_OnLoading(Home.loadingCustomer, context.getString(R.string.load_customer), 30);
                    EventPool.control().enQueue(new EventType.EventLoadCustomersRequest(Home.nowRoute, Home.filterCustomer, -1, Home.nowIdCustomer));
                } else if (countCustomer == -1) {
                    //sync data
                    MyMethod.showToast(context, "Yêu cầu đồng bộ dữ liệu");
                    LocalDB.inst().deleteSyncData();
                    LayoutLoadingManager.Show_OnLoading(Home.loadingCustomer, context.getString(R.string.load_route), 30);
                    EventPool.control().enQueue(new EventType.EventSyncDataRequest(0, -1));
                    MyMethod.isSyncDating = true;
                }
                break;
            case R.id.report_web_load:
                //load report web
                Home.bindingRight.reportWeb.webview.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
                Home.bindingRight.reportWeb.webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
                if (Build.VERSION.SDK_INT >= 19) {
                    Home.bindingRight.reportWeb.webview.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                } else {
                    Home.bindingRight.reportWeb.webview.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                }
                LayoutLoadingManager.Show_OnLoading(Home.loadingReportWeb, context.getString(R.string.load_report_web), 30);
                String url = Const.HttpReportEmployeeWork + Model.inst().getConfigValue(Const.ConfigKeys.ID_Device, 0) + Const.DateParameter + toDateReportWeb;
                Home.bindingRight.reportWeb.webview.loadUrl(url);
                Home.bindingRight.reportWeb.webview.setWebViewClient(new WebViewClient() {

                    public void onPageFinished(WebView view, String url) {
                        // do your stuff here
                        LayoutLoadingManager.Show_OffLoading(Home.loadingReportWeb);
                    }
                });
                break;
            case R.id.order_main_load:
                LayoutLoadingManager.Show_OnLoading(Home.loadingOrderMain, context.getString(R.string.loading), 30);

                if (MyMethod.isInventory) {
                    ordersArrayList.clear();
                    adapterOrder.notifyDataSetChanged();
                    EventPool.control().enQueue(new EventType.EventLoadOrdersRequest(-1, toDateOrder, 1, filterOrderMain, nowIdEmployeeOrder));
                } else if (MyMethod.isLoadOrder) {
                    ordersArrayList.clear();
                    adapterOrder.notifyDataSetChanged();
                    EventPool.control().enQueue(new EventType.EventLoadOrdersRequest(-1, toDateOrder, 0, filterOrderMain, nowIdEmployeeOrder));
                } else if (MyMethod.isInventoryEmployee) {
                    inventoryEmployeesArrayList.clear();
                    adapterInventoryEmployee.notifyDataSetChanged();
                    EventPool.control().enQueue(new EventType.EventLoadOrdersRequest(-1, toDateOrder, 3, filterOrderMain, nowIdEmployeeOrder));
                } else if (MyMethod.isInventoryBill) {
                    ordersArrayList.clear();
                    adapterOrder.notifyDataSetChanged();
                    EventPool.control().enQueue(new EventType.EventLoadOrdersRequest(-1, toDateOrder, 2, filterOrderMain, nowIdEmployeeOrder));
                }

                break;
            case R.id.inventory_employee_load:
                LayoutLoadingManager.Show_OnLoading(Home.loadingInventoryEmployee, context.getString(R.string.load_inventory_employee), 30);
                inventoryEmployeesArrayList.clear();
                EventPool.control().enQueue(new EventType.EventLoadInventoryEmployeesRequest(-1, toDatenventoryEmployee, nowIdInventoryEmployee, nowInventoryGroup, Home.bindingRight.inventoryEmployee.spInventoryStock.getSelectedItemPosition(), filterInventoryEmployee));
                break;
            case R.id.approval_load:
                LayoutLoadingManager.Show_OnLoading(Home.loadingApprovalAppLock, context.getString(R.string.load_approval), 30);
                approvalArrayList.clear();
                adapterApproval.notifyDataSetChanged();
                EventPool.control().enQueue(new EventType.EventLoadRequestGrantRequest(-1, nowIdApproval, Home.bindingRight.approvalAppLock.spApprovalStatus.getSelectedItemPosition(), filtersvApproval));
                break;
            case R.id.transaction_from:
                MyMethod.Date = 4;
                CalendarDatePickerDialogFragment fromTransaction = new CalendarDatePickerDialogFragment()
                        .setOnDateSetListener(this)
                        .setFirstDayOfWeek(Calendar.SUNDAY)
//                        .setPreselectedDate(towDaysAgo.getYear(), towDaysAgo.getMonthOfYear() - 1, towDaysAgo.getDayOfMonth())
//                        .setDateRange(minDate, null)
                        .setThemeDark(false);
                fromTransaction.show(getFragmentManager(), FRAG_TAG_DATE_PICKER);
                break;
            case R.id.transaction_to:
                MyMethod.Date = 5;
                CalendarDatePickerDialogFragment toTransaction = new CalendarDatePickerDialogFragment()
                        .setOnDateSetListener(this)
                        .setFirstDayOfWeek(Calendar.SUNDAY)
//                        .setPreselectedDate(towDaysAgo.getYear(), towDaysAgo.getMonthOfYear() - 1, towDaysAgo.getDayOfMonth())
//                        .setDateRange(minDate, null)
                        .setThemeDark(false);
                toTransaction.show(getFragmentManager(), FRAG_TAG_DATE_PICKER);
                break;
            case R.id.transaction_load:
                LayoutLoadingManager.Show_OnLoading(Home.loadingTransaction, context.getString(R.string.load_transaction), 30);
                transactionArrayList.clear();
                transactionLineArrayList.clear();
                MyMethod.isLoadTransactionByID = false;
                MyMethod.isLoadTransactionByIDInMessage = false;
                EventPool.control().enQueue(new EventType.EventLoadTransactionsRequest(-1, fromDateTransaction, nowIdEmployeeTransaction, filterTransaction, false, nowTransactionStatus));
                break;
            case R.id.btn_update_location_customer:
                if (MyMethod.isGetLocationCreateCustomer) {
                    if (Home.location != null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("Xác nhận lấy vị trí")
                                .setCancelable(false)
                                .setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                })
                                .setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        nowCustomer.latitude = Home.location.getLatitude();
                                        nowCustomer.longitude = Home.location.getLongitude();
                                        Home.bindingRight.createCustomer.btnCustomerGetLocation.setText(Home.markerNow.getSnippet());
                                        if (Home.bindingRight.createCustomer.edCustomerAddress.getText().toString().isEmpty()) { // Nếu chưa đánh địa chỉ thì gán vào fied địa chỉ
                                            Home.bindingRight.createCustomer.edCustomerAddress.setText(Home.markerNow.getSnippet());
                                        }
                                        dialog.dismiss();
                                        Home.bindingHome.btnComeBack.setSoundEffectsEnabled(false);
                                        Home.bindingHome.btnComeBack.performClick();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();

                    } else {
                        MyMethod.refreshMap(context, googleMap);
                        MyMethod.showToast(context, context.getString(R.string.location_wait));
                        LocationDetector.inst().setHighPrecision(true);
                    }
                } else {
                    if (Home.location != null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("Xác nhận cập nhật vị trí cho " + nowCustomer.name)
                                .setCancelable(false)
                                .setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                })
                                .setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        nowCustomer.latitude = Home.location.getLatitude();
                                        nowCustomer.longitude = Home.location.getLongitude();
                                        Home.bindingRight.customerUpdate.dialogCustomerLocation.setText(nowCustomer.latitude + "," + nowCustomer.longitude);
                                        dialog.dismiss();
                                        Home.bindingHome.btnComeBack.setSoundEffectsEnabled(false);
                                        Home.bindingHome.btnComeBack.performClick();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    } else {
                        MyMethod.refreshMap(context, googleMap);
                        MyMethod.showToast(context, context.getString(R.string.location_wait));
                        LocationDetector.inst().setHighPrecision(true);
                    }
                }

                break;
            case R.id.btn_restart_app:
                CustomActivityOnCrash.restartApplicationWithIntent((Activity) context, new Intent(context, Home.class));//restart app
                break;
            case R.id.btn_login_route_in_login:
                Home.bindingHome.txtTile.setText(context.getString(R.string.log_in_route));
                MyMethod.logInRouteInSetting = false;
                Home.LayoutMyManager.ShowLayout(Layouts.LogInRoute);
                break;
            case R.id.btn_signin_route:
                //Xử lí đăng nhập tuyến
                if (isDataLoginRouteOK()) {
                    LayoutLoadingManager.Show_OnLoading(Home.bindingRight.loginRoute.LogInRouteLoading, getString(R.string.log_in_route), 30);
                    EventPool.control().enQueue(new EventType.EventLogInRouteRequest(Home.bindingRight.loginRoute.edUserRoute.getText().toString(), Home.bindingRight.loginRoute.edPassRoute.getText().toString()));
                }
                break;
            case R.id.btn_restart_clone:
                MyMethod.restartApp(context);
                break;
            case R.id.btn_restart_app_setting:
                MyMethod.restartApp(context);
                break;
            case R.id.btn_printer_setting:
                //Cài đặt máy in
                if (Home.mService.isBTopen() == false) {
                    Home.isAppLockStop = true;
                    Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
                } else {
                    Home.isAppLockStop = true;
                    Intent serverIntent = new Intent(context, DeviceBluetooth.class);
                    startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                }
                break;
            case R.id.btn_login_route:
                Home.bindingHome.txtTile.setText(context.getString(R.string.log_in_route));
                MyMethod.logInRouteInSetting = true;
                Home.LayoutMyManager.ShowLayout(Layouts.LogInRoute);
                break;
            case R.id.btn_sync_data:
                //Đồng bộ dữ liệu
                if (!MyMethod.isSyncDating) {
                    ArrayList<TransactionLine> dataTransaction = LocalDB.inst().getTransactionUnsent();
                    int countCustomerUnSent = LocalDB.inst().countCustomerUnSent();
                    int countTransactionUnsent = dataTransaction.size();
                    //int countOrderUnsent = LocalDB.inst().countOrderUnSent();
                    if (countTransactionUnsent > 0) {
                        ArrayList<Object> arrData = new ArrayList<>(countTransactionUnsent);
                        arrData.addAll(dataTransaction);
                        MyMethod.updateBeforeSync = true;
                        EventPool.control().enQueue(new EventType.EventUpdateDataRequest(arrData, 0));
                    } else if (countCustomerUnSent > 0) {
                        ArrayList<Customer> dataCustomer = LocalDB.inst().getCustomerUnsent();
                        ArrayList<Object> arrData = new ArrayList<>(countCustomerUnSent);
                        arrData.addAll(dataCustomer);
                        MyMethod.updateBeforeSync = true;
                        EventPool.control().enQueue(new EventType.EventUpdateDataRequest(arrData, 1));
                    } else {
                        Home.bindingRight.setting.btnSyncData.setState(AnimDownloadProgressButton.DOWNLOADING);
                        Home.bindingRight.setting.btnSyncData.setMaxProgress(100);
                        LocalDB.inst().deleteSyncData();
                        EventPool.control().enQueue(new EventType.EventSyncDataRequest(0, -1));
                        MyMethod.isSyncDating = true;
                    }

                }
                break;
            case R.id.btn_update_data:
                //Cập nhật dữ liệu
                Home.bindingRight.setting.btnUpdateData.setState(AnimDownloadProgressButton.DOWNLOADING);
                Home.bindingRight.setting.btnUpdateData.setMaxProgress(100);
                ArrayList<TransactionLine> dataTransaction = LocalDB.inst().getTransactionUnsent();
                int countCustomerUnSent = LocalDB.inst().countCustomerUnSent();
                int countTransactionUnsent = dataTransaction.size();
                //int countOrderUnsent = LocalDB.inst().countOrderUnSent();
                if (countTransactionUnsent > 0) {
                    ArrayList<Object> arrData = new ArrayList<>(countTransactionUnsent);

                    arrData.addAll(dataTransaction);
                    MyMethod.updateBeforeSync = false;
                    EventPool.control().enQueue(new EventType.EventUpdateDataRequest(arrData, 0));
                } else if (countCustomerUnSent > 0) {
                    ArrayList<Customer> dataCustomer = LocalDB.inst().getCustomerUnsent();
                    ArrayList<Object> arrData = new ArrayList<>(countCustomerUnSent);
                    arrData.addAll(dataCustomer);
                    MyMethod.updateBeforeSync = false;
                    EventPool.control().enQueue(new EventType.EventUpdateDataRequest(arrData, 1));
//                } else if (countOrderUnsent > 0) {
//                    ArrayList<Order> dataOrder = LocalDB.inst().getOrderUnsents();
//                    ArrayList<Object> arrData = new ArrayList<>(countOrderUnsent);
//                    arrData.addAll(dataOrder);
//                    EventPool.control().enQueue(new EventType.EventUpdateDataRequest(arrData, 2));
                } else {
                    MyMethod.showToast(context, getString(R.string.none_data_need_update));
                    Home.bindingRight.setting.btnUpdateData.setState(AnimDownloadProgressButton.NORMAL);
                }
                break;
            case R.id.btn_pause_program:
                Intent serverIntent = new Intent(context, WebReport_Overview.class);
                startActivity(serverIntent);
                break;
            case R.id.btn_restart_app_error:
                MyMethod.restartApp(context);
                break;
            case R.id.fabGetAgainOrder:
                MyMethod.refreshMap(context, googleMap);
                MyMethod.showToast(context, context.getString(R.string.location_wait));
                LocationDetector.inst().setHighPrecision(true);
                break;
            case R.id.fabOk:
                if (editNoteOrder.getText().toString().isEmpty()) {
                    MyMethod.showToast(context, context.getString(R.string.input_note));
                } else if (Home.location != null) {
                    LayoutLoadingManager.Show_OnLoading(Home.loadingMapOrder, "", 30);
                    EventPool.control().enQueue(new EventType.EventSetOrderDeliveryStatusRequest(Home.bindingRight.mapOrder.content.txtOrderNo.getText().toString(), Const.OrderDeliveryStatus.Completed, editNoteOrder.getText().toString(), 0));
                } else {
                    MyMethod.refreshMap(context, googleMap);
                    MyMethod.showToast(context, context.getString(R.string.location_wait));
                    LocationDetector.inst().setHighPrecision(true);
                }
                break;
            case R.id.btn_select_day_report:
                //Chọn ngày xem báo cáo web
                MyMethod.Date = 9;
                CalendarDatePickerDialogFragment fromDate = new CalendarDatePickerDialogFragment()
                        .setOnDateSetListener(this)
                        .setFirstDayOfWeek(Calendar.SUNDAY)
                        .setThemeDark(false);
                fromDate.show(getFragmentManager(), FRAG_TAG_DATE_PICKER);
                break;
            case R.id.btnFromDate:
                MyMethod.Date = 0;
                CalendarDatePickerDialogFragment dateReport = new CalendarDatePickerDialogFragment()
                        .setOnDateSetListener(this)
                        .setFirstDayOfWeek(Calendar.SUNDAY)
                        .setThemeDark(false);
                dateReport.show(getFragmentManager(), FRAG_TAG_DATE_PICKER);
                break;
            case R.id.btnToDate:
                MyMethod.Date = 1;
                CalendarDatePickerDialogFragment toDate = new CalendarDatePickerDialogFragment()
                        .setOnDateSetListener(this)
                        .setFirstDayOfWeek(Calendar.SUNDAY)
                        .setThemeDark(false);
                toDate.show(getFragmentManager(), FRAG_TAG_DATE_PICKER);
                break;
            case R.id.btn_get_checkin:
//                showLayout(Layouts.Map, context);
//                Home.mapFragment.getMapAsync(this);
                startActivityForResult(new Intent(context, MapsActivity.class), ACTION_MAP);
                break;
            case R.id.btn_get_photo_checkin:
                //GET CAMERA PHOTO
                Home.bindingHome.viewpager.getHeight();
                takePhoto(PHOTOS.CheckIn);
                break;
            case R.id.btn_save_send_checkin:
                MyMethod.hideKeyboardFromView(context, editNote);
                if (MyMethod.checkInputInSaveSend(context)) {
                    //SAVE AND SEND CHECK IN DATA
                    if (MyMethod.CheckInCustomer) {
                        Home.txtAddressIn.setText(context.getString(R.string.location_none));
                        LayoutLoadingManager.Show_OnLoading(Home.loadingCheckIn, context.getString(R.string.sending), 30);
                        EventPool.control().enQueue(new EventType.EventReportRequest(Const.LocationVisitedType.CheckInCustomer, nowCustomer.no_, imagePath, Home.location, Home.editCheckIn.getText().toString(), Home.bitmapImage));
                        Home.bitmapImage = null;
                    } else {
                        Home.txtAddressIn.setText(context.getString(R.string.location_none));
                        LayoutLoadingManager.Show_OnLoading(Home.loadingCheckIn, context.getString(R.string.sending), 30);
                        EventPool.control().enQueue(new EventType.EventReportRequest(Const.LocationVisitedType.Unknown, "", imagePath, Home.location, Home.editCheckIn.getText().toString(), Home.bitmapImage));
                        Home.bitmapImage = null;
                    }

                }
                break;
            case R.id.btn_signin:
                submitFormLogin(v);
                break;
        }
    }

    private boolean isDataLoginRouteOK() {
        if (Home.bindingRight.loginRoute.edUserRoute.getText().toString().isEmpty()) {
            MyMethod.showToast(context, getString(R.string.hint_username));
            return false;
        }
        if (Home.bindingRight.loginRoute.edPassRoute.getText().toString().isEmpty()) {
            MyMethod.showToast(context, getString(R.string.hint_password));
            return false;
        }
        return true;
    }

    private void syncData(boolean loading) {
        if (loading) {

        } else {

        }
    }

    private void findMaxLengQuantity(String str) {
        if (str.length() >= maxQuantity)
            maxQuantity = str.length();
    }

    private String printString(String str, int max) {
        int len = str.length();
        int result = max - len;
        String temp = "";
        for (int i = 0; i < result; i++) {
            temp = temp + " ";
        }
        return temp + str;
    }

    private void findMaxLengSale(String str) {
        if (str.length() >= maxSale)
            maxSale = str.length();
    }

    private void findMaxLengPrice(String str) {
        if (str.length() >= maxPrice)
            maxPrice = str.length();
    }


    //XU LI IN QUA BLUETOOTH
    private void printerOrder() {
        if (Home.mService.getState() == BluetoothService.STATE_CONNECTED) {
            int i = 0;
            String customer = Utils.unAccent(Home.txtOrderCustomerName.getText().toString());
            Home.mService.sendMessage(customer, "GBK");
            String add = Utils.unAccent(Home.txtOrderCustomerAddress.getText().toString());
            Home.mService.sendMessage(add, "GBK");
            Home.mService.sendMessage("--------------------------------------------", "GBK");
            for (OrderDetail o : Home.orderDetailArrayList) {
                findMaxLengQuantity(o.quantity + "");
                findMaxLengPrice(o.unitprice + "");
                findMaxLengSale(o.quantity * o.unitprice + "");
            }
            for (OrderDetail o : Home.orderDetailArrayList) {
                i++;
                String msg = "";
                msg = i + "." + Utils.unAccent(o.name) + "-" + o.itemNo_
                        + "\n      " + printString(o.quantity + "", maxQuantity) + "       " +
                        printString(Utils.formatFloat(o.unitprice), maxPrice) + "        "
                        + printString(Utils.formatFloat(o.quantity * o.unitprice), maxSale);
                Home.mService.sendMessage(msg, "GBK");
            }
            Home.mService.sendMessage("--------------------------------------------", "GBK");
            String money = Home.txtOrderAmount.getText().toString();
            String amount = "TONG TIEN : " + money.substring(0, money.length() - 4);
            Home.mService.sendMessage(Utils.unAccent(amount), "GBK");
            String acceptCustomer = "           XAC NHAN KHACH HANG \n            DA NHAN DU HANG\n \n";
            Home.mService.sendMessage(acceptCustomer, "");
        } else {
            MyMethod.showToast(context, context.getString(R.string.please_connect_printer));
        }

    }

    private void updateListQuantityPrice() {// Cập nhật list sản phẩm nếu có
        if (Home.orderDetailArrayList.size() > 0) {
            for (OrderDetail od : Home.orderDetailArrayList) {
                Home.hashListQuantity.put(od.no_, od.quantity);
                Home.hashListPrice.put(od.no_, od.unitprice);
            }
        }

    }

    private void updateSelectedRoute(int workingTime) {
        int i = 0;
        cns.setChecked((workingTime & (1 << i++)) != 0);
        cnc.setChecked((workingTime & (1 << i++)) != 0);
        t2s.setChecked((workingTime & (1 << i++)) != 0);
        t2c.setChecked((workingTime & (1 << i++)) != 0);
        t3s.setChecked((workingTime & (1 << i++)) != 0);
        t3c.setChecked((workingTime & (1 << i++)) != 0);
        t4s.setChecked((workingTime & (1 << i++)) != 0);
        t4c.setChecked((workingTime & (1 << i++)) != 0);
        t5s.setChecked((workingTime & (1 << i++)) != 0);
        t5c.setChecked((workingTime & (1 << i++)) != 0);
        t6s.setChecked((workingTime & (1 << i++)) != 0);
        t6c.setChecked((workingTime & (1 << i++)) != 0);
        t7s.setChecked((workingTime & (1 << i++)) != 0);
        t7c.setChecked((workingTime & (1 << i++)) != 0);
    }

    private int selectRouteAccept() {
        int workingtime = 0;
        String working = "";
        int i = 0;
        if (cns.isChecked()) {
            workingtime |= (1 << i);
            working += "CNS,";
        }
        i++;
        if (cnc.isChecked()) {
            workingtime |= (1 << i);
            working += "CNC,";
        }
        i++;
        if (t2s.isChecked()) {
            workingtime |= (1 << i);
            working += "T2S,";
        }
        i++;
        if (t2c.isChecked()) {
            workingtime |= (1 << i);
            working += "T2C,";
        }
        i++;
        if (t3s.isChecked()) {
            workingtime |= (1 << i);
            working += "T3S,";
        }
        i++;
        if (t3c.isChecked()) {
            workingtime |= (1 << i);
            working += "T3C,";
        }
        i++;
        if (t4s.isChecked()) {
            workingtime |= (1 << i);
            working += "T4S,";
        }
        i++;
        if (t4c.isChecked()) {
            workingtime |= (1 << i);
            working += "T4C,";
        }
        i++;
        if (t5s.isChecked()) {
            workingtime |= (1 << i);
            working += "T5S,";
        }
        i++;
        if (t5c.isChecked()) {
            workingtime |= (1 << i);
            working += "T5C,";
        }
        i++;
        if (t6s.isChecked()) {
            workingtime |= (1 << i);
            working += "T6S,";
        }
        i++;
        if (t6c.isChecked()) {
            workingtime |= (1 << i);
            working += "T6C,";
        }
        i++;
        if (t7s.isChecked()) {
            workingtime |= (1 << i);
            working += "T7S,";
        }
        i++;
        if (t7c.isChecked()) {
            workingtime |= (1 << i);
            working += "T7C,";
        }
        i++;
        btn_create_customer_route.setText(working);
        return workingtime;
    }

    private void inputNoteAccept() {
        if (Home.bindingRight.note.edTransactionNote.getText().toString().isEmpty()) {
            MyMethod.showToast(context, context.getString(R.string.please_input_note));
        } else {
            Home.nowTransactionLine.id_customer = nowTransaction.id_customer;
            Home.nowTransactionLine.status = 0;
            Home.nowTransactionLine.note = Home.bindingRight.note.edTransactionNote.getText().toString();
            Home.nowTransactionLine.id_ExtNo_ = 0;
            Home.nowTransactionLine.id_transaction_define = Const.TransactionType.Note.getValue();
            Home.nowTransactionLine.latitude = nowTransaction.latitude;
            Home.nowTransactionLine.longitude = nowTransaction.longtitude;
            Home.nowTransactionLine.id_transaction = nowTransaction.rowId;
            Home.nowTransactionLine.userid = nowTransaction.userid;
            Home.nowTransactionLine.id_employee = nowTransaction.id_employee;
            showLayout(Layouts.TransactionDetail, context);
            EventPool.control().enQueue(new EventType.EventSendTransactionRequest(Home.nowTransactionLine));
        }
    }

    //Cập nhật lại view cho phù hợp
    private void updateInventoryView() {
        Home.bindingRight.order.titleDetailOrder.setText(context.getString(R.string.detail_inventory));
        Home.bindingRight.order.titleTransDay.setText(context.getString(R.string.report_day));
        MyMethod.setGone(Home.bindingRight.order.titleOrderDiscount);
        MyMethod.setGone(Home.bindingRight.order.titleOrderAmount);
        MyMethod.setGone(Home.txtOrderAmount);
        MyMethod.setGone(Home.txtOrderDiscount);
        Home.btnOrderAddProduct.setText(context.getString(R.string.add_inventory));
    }

    private void updateInventoryInputView() {
        String type = "";
        if (Model.inst().getConfigValue(Const.ConfigKeys.PreSale) == null) {
            type = " công ty";
        } else {
            if (Integer.parseInt(Model.inst().getConfigValue(Const.ConfigKeys.PreSale)) == 0) {
                type = " công ty";
            } else {
                type = " nhân viên";
            }
        }

        Home.bindingRight.order.titleDetailOrder.setText(context.getString(R.string.detail_inventory_input) + type);
        Home.bindingRight.order.titleTransDay.setText(context.getString(R.string.input_day));
        MyMethod.setGone(Home.img_pre_sale);
        MyMethod.setVisible(Home.bindingRight.order.titleOrderDiscount);
        MyMethod.setVisible(Home.bindingRight.order.titleOrderAmount);
        MyMethod.setVisible(Home.txtOrderAmount);
        MyMethod.setVisible(Home.txtOrderDiscount);
        Home.btnOrderAddProduct.setText(context.getString(R.string.add_product));
    }

    private void updateOrderView() {
        Home.bindingRight.order.titleTransDay.setText(context.getString(R.string.trans_day));
        if (Model.inst().getConfigValue(Const.ConfigKeys.PreSale) == null) {
            MyMethod.setInVisible(Home.img_pre_sale);
        } else if (Integer.parseInt(Model.inst().getConfigValue(Const.ConfigKeys.PreSale)) == 0) {
            MyMethod.setInVisible(Home.img_pre_sale);
        } else {
            MyMethod.setVisible(Home.img_pre_sale);
        }
        MyMethod.setVisible(Home.bindingRight.order.titleOrderDiscount);
        MyMethod.setVisible(Home.bindingRight.order.titleOrderAmount);
        MyMethod.setVisible(Home.txtOrderAmount);
        MyMethod.setVisible(Home.txtOrderDiscount);
        Home.btnOrderAddProduct.setText(context.getString(R.string.add_product));
    }

    public static void updateInventoryDetailView(Context context) {//Không cho chỉnh sửa
        Home.bindingRight.orderDetail.titleDetailOrderDetail.setText(context.getString(R.string.detail_inventory));
        Home.bindingRight.orderDetail.titleTransDayDetail.setText(context.getString(R.string.report_day));
        MyMethod.setGone(Home.bindingRight.orderDetail.titleOrderDiscountDetail);
        MyMethod.setGone(Home.bindingRight.orderDetail.titleOrderAmountDetail);
        MyMethod.setGone(Home.bindingRight.orderDetail.orderDetailSaveSend);
        MyMethod.setGone(Home.img_pre_sale);
        MyMethod.setGone(Home.bindingRight.orderDetail.orderDetailAddProduct);
        MyMethod.setGone(Home.txtOrderDetailAmount);
        MyMethod.setGone(Home.txtOrderDetailDiscount);
        Home.edOrderDetailNote.setEnabled(false);
    }

    private static void updateInventoryBillDetailView(Context context) {
        Home.bindingRight.orderDetail.titleDetailOrderDetail.setText(context.getString(R.string.detail_inventory_bill));
        Home.bindingRight.orderDetail.titleTransDayDetail.setText(context.getString(R.string.report_day));
        MyMethod.setGone(Home.bindingRight.orderDetail.titleOrderDiscountDetail);
        MyMethod.setGone(Home.bindingRight.orderDetail.titleOrderAmountDetail);
        MyMethod.setGone(Home.bindingRight.orderDetail.orderDetailSaveSend);
        MyMethod.setGone(Home.img_pre_sale);
        MyMethod.setGone(Home.bindingRight.orderDetail.orderDetailAddProduct);
        MyMethod.setGone(Home.txtOrderDetailAmount);
        MyMethod.setGone(Home.txtOrderDetailDiscount);
        Home.edOrderDetailNote.setEnabled(false);
    }

    private static void updateInventoryEmployeeDetailView(Context context) {
        Home.bindingRight.orderDetail.titleDetailOrderDetail.setText(context.getString(R.string.detail_inventory_employee));
        Home.bindingRight.orderDetail.titleTransDayDetail.setText(context.getString(R.string.report_day));
        MyMethod.setGone(Home.bindingRight.orderDetail.titleOrderDiscountDetail);
        MyMethod.setGone(Home.bindingRight.orderDetail.titleOrderAmountDetail);
        MyMethod.setGone(Home.bindingRight.orderDetail.orderDetailSaveSend);
        MyMethod.setGone(Home.bindingRight.orderDetail.orderDetailAddProduct);
        MyMethod.setGone(Home.img_pre_sale);
        MyMethod.setGone(Home.txtOrderDetailAmount);
        MyMethod.setGone(Home.txtOrderDetailDiscount);
        Home.edOrderDetailNote.setEnabled(false);
    }

    public static void updateOrderDetailView(Context context) {
        Home.bindingRight.orderDetail.titleDetailOrderDetail.setText(context.getString(R.string.order_sale_title));
        Home.bindingRight.orderDetail.titleTransDayDetail.setText(context.getString(R.string.trans_day));
        MyMethod.setVisible(Home.bindingRight.orderDetail.titleOrderDiscountDetail);
        MyMethod.setVisible(Home.bindingRight.orderDetail.titleOrderAmountDetail);
        MyMethod.setVisible(Home.txtOrderDetailAmount);
        if (Model.inst().getConfigValue(Const.ConfigKeys.PreSale) == null) {
            MyMethod.setInVisible(Home.img_pre_sale);
        } else if (Integer.parseInt(Model.inst().getConfigValue(Const.ConfigKeys.PreSale)) == 0) {
            MyMethod.setInVisible(Home.img_pre_sale);
        } else {
            MyMethod.setVisible(Home.img_pre_sale);
        }
        MyMethod.setVisible(Home.txtOrderDetailDiscount);
        Home.edOrderDetailNote.setEnabled(true);

    }

    private void submitCreateCustomer() {
        if (!validateCustomerLocation()) {
            return;
        }
        if (!validateCustomerNo()) {
            return;
        }
        if (!validateCustomerName()) {
            return;
        }
        if (!validateCustomerAddress()) {
            return;
        }
        if (!validateCustomerPhone()) {
            return;
        }
        nowCustomer.city = Home.nowCity;
        nowCustomer.county = Home.nowCounty;
        nowCustomer.workingtime = Home.nowWorkingTime;
        LayoutLoadingManager.Show_OnLoading(Home.loadingCreateCustomer, context.getString(R.string.creating_customer), 30);
        EventPool.control().enQueue(new EventType.EventAddCustomerRequest(nowCustomer));
    }

    private void showPopup(POPUP pop, final int position) {
        PopupMenu popup = null;

        switch (pop) {
            case Store:
                popup = new PopupMenu(context, img_store_camera);
                popup.getMenuInflater()
                        .inflate(R.menu.take_photo, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.take_again:
                                takePhoto(PHOTOS.Store);
                                break;
                            case R.id.delete_photo:
                                imageStorePath = "";
                                photoStore = null;
                                img_store_camera.setImageResource(R.drawable.camera_btn);
                                break;
                            default:
                                break;
                        }
                        return true;
                    }
                });

                break;
            case Order:
                popup = new PopupMenu(context, getViewByPosition(position, Home.lstOrderProduct));
                popup.getMenuInflater()
                        .inflate(R.menu.edit_delete, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.edit:
                                editValue(POPUP.Order, position);
                                break;
                            case R.id.delete:
                                confirmDelete(POPUP.Order, position);
                                break;
                            default:
                                break;
                        }
                        return true;
                    }
                });

                break;

            case OrderDetail:
                popup = new PopupMenu(context, getViewByPosition(position, Home.lstOrderDetail));
                popup.getMenuInflater()
                        .inflate(R.menu.edit_delete, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.edit:
                                editValue(POPUP.OrderDetail, position);
                                break;
                            case R.id.delete:
                                confirmDelete(POPUP.OrderDetail, position);
                                break;
                            default:
                                break;
                        }
                        return true;
                    }
                });

                break;
            case CreateCustomer:
                popup = new PopupMenu(context, Home.bindingRight.createCustomer.imageCreateCustomer);
                popup.getMenuInflater()
                        .inflate(R.menu.take_photo, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.take_again:
                                takePhoto(PHOTOS.CreateCustomer);
                                break;
                            case R.id.delete_photo:
                                imageCreateCustomerPath = "";
                                photoCreateCustomer = null;
                                Home.bindingRight.createCustomer.imageCreateCustomer.setImageResource(R.drawable.camera_btn);
                                break;
                            default:
                                break;
                        }
                        return true;
                    }
                });

                break;
            case TransactionAccept:
                popup = new PopupMenu(context, Home.bindingRight.transactionDetail.btnTransactionWrite);
                popup.getMenuInflater()
                        .inflate(R.menu.transaction_accept, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.transaction_update:
                                //bấm nút ghi nhận
                                MyMethod.isCheckInInTransactionDetail = true;
                                MyMethod.isFinishInTransactionDetail = false;
                                MyMethod.isNoteInStore = false;
                                MyMethod.isReportStore = false;
                                MyMethod.isOrderInStorePressed = false;
                                MyMethod.isReportNotOrder = false;
                                MyMethod.CheckInCustomer = false;
                                showLayout(Layouts.CheckIn, context);
                                break;
                            case R.id.transaction_finish:
                                MyMethod.isCheckInInTransactionDetail = false;
                                MyMethod.isFinishInTransactionDetail = true;
                                MyMethod.isNoteInStore = false;
                                MyMethod.isReportStore = false;
                                MyMethod.isOrderInStorePressed = false;
                                MyMethod.isReportNotOrder = false;
                                MyMethod.CheckInCustomer = false;
                                showLayout(Layouts.CheckIn, context);
                                break;
                            case R.id.transaction_cancel:
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setTitle(context.getString(R.string.confirm_transaction_cancel));
                                builder.setIcon(R.drawable.edit_btn);
                                builder.setMessage(getString(R.string.confirm_cancel) + nowTransaction.description);
                                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        LayoutLoadingManager.Show_OnLoading(Home.bindingRight.transactionDetail.TransactionLineLoadingView,getString(R.string.rejecting),30);
                                        EventPool.control().enQueue(new EventType.EventRejectWorkRequest(nowTransaction.rowId));
                                        dialog.dismiss();
                                    }
                                }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                    // Create the AlertDialog object and return it
                                });
                                builder.show();
                                break;
                            default:
                                break;
                        }
                        return true;
                    }
                });

                break;
            default:
                break;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            popup.setGravity(Gravity.CENTER_HORIZONTAL);
        }
        popup.show();


    }

    private void inputValueUpdateOrderAccept(int position) {
        int quantity = Integer.parseInt(editQuantity.getText().toString());
        float price = Float.parseFloat(Utils.formatLocale(editPrice.getText().toString()));
        if (quantity > 0 && price > 0) {
            Home.orderDetailArrayList.get(position).quantity = quantity;
            Home.orderDetailArrayList.get(position).unitprice = price;
        }
        Home.nowAmountSale = 0;
        Home.nowAmount = 0;
        Home.nowDiscount = 0;
        for (OrderDetail od : Home.orderDetailArrayList) {
            Home.nowAmountSale = Home.nowAmountSale + od.unitprice * od.quantity;
            Home.nowAmount = Home.nowAmount + od.discountAmount + od.unitprice * od.quantity;
            Home.nowDiscount = Home.nowDiscount + od.discountAmount;
        }
        Home.txtOrderDetailAmount.setText(Utils.formatFloat(Home.nowAmount));
        Home.txtOrderDetailAmountSale.setText(Utils.formatFloat(Home.nowAmountSale));
        Home.txtOrderDetailDiscount.setText(Utils.formatFloat(Home.nowDiscount));
        Home.orderListOrderDetailAdapter.setItems(Home.orderDetailArrayList);
        Home.orderListOrderDetailAdapter.notifyDataSetChanged();
        Home.bindingHome.btnComeBack.setSoundEffectsEnabled(false);
        Home.bindingHome.btnComeBack.performClick();
    }

    private void editValueAccept(int position) {
        Home.orderDetailArrayList.get(position).quantity = Integer.parseInt(editQuantity.getText().toString());
        Home.orderDetailArrayList.get(position).unitprice = Float.parseFloat(Utils.formatLocale(editPrice.getText().toString()));
        Home.nowAmount = 0;
        Home.nowAmountSale = 0;
        Home.nowDiscount = 0;
        for (OrderDetail od : Home.orderDetailArrayList) {
            Home.nowAmountSale = Home.nowAmountSale + od.unitprice * od.quantity;
            Home.nowAmount = Home.nowAmount + od.discountAmount + od.unitprice * od.quantity;
            Home.nowDiscount = Home.nowDiscount + od.discountAmount;
        }
        Home.txtOrderAmount.setText(Utils.formatFloat(Home.nowAmount) + context.getString(R.string.money));
        Home.txtOrderAmountSale.setText(Utils.formatFloat(Home.nowAmountSale) + context.getString(R.string.money));
        Home.txtOrderDiscount.setText(Utils.formatFloat(Home.nowDiscount) + context.getString(R.string.money));
        Home.orderListProductAdapter.notifyDataSetChanged();
        Home.bindingHome.btnComeBack.setSoundEffectsEnabled(false);
        Home.bindingHome.btnComeBack.performClick();
    }

    private void inputValueAccept() {
        if (MyMethod.isInventoryReport) { // Nếu báo cáo tồn thì có thể nhập số lượng = 0
            if (!editQuantity.getText().toString().isEmpty())
                Home.hashListQuantity.put(nowNo_, Integer.parseInt(editQuantity.getText().toString()));
        } else if (MyMethod.isInventoryInput || MyMethod.isOrder || MyMethod.isProductSample || MyMethod.isProductDisplay) { // Nếu đặt hàng hoặc nhập tồn thì không được nhập số lượng = 0
            if (!editQuantity.getText().toString().isEmpty() && !editQuantity.getText().toString().equals("0"))
                Home.hashListQuantity.put(nowNo_, Integer.parseInt(editQuantity.getText().toString()));

        }
        if (!editPrice.getText().toString().isEmpty())
            Home.hashListPrice.put(nowNo_, Float.parseFloat(Utils.formatLocale(editPrice.getText().toString())));
        adapterProductOfOrder.notifyDataSetChanged();
        Home.bindingHome.btnComeBack.setSoundEffectsEnabled(false);
        Home.bindingHome.btnComeBack.performClick();
    }


    private void cancelOrder() {
        Home.hashListQuantity.clear();
        Home.hashListPrice.clear();

    }

    private void destroyOrder() {
        Home.orderDetailArrayList.clear();
        Home.orderListProductAdapter.notifyDataSetChanged();
        Home.txtOrderAmount.setText("0" + context.getString(R.string.money));
        Home.edOrderNote.setText("");
        Home.txtOrderDiscount.setText("0" + context.getString(R.string.money));
        Home.txtOrderAmountSale.setText("0" + context.getString(R.string.money));
        Home.nowAmount = 0;
        Home.nowAmountSale = 0;
        Home.nowDiscount = 0;
    }

    private void loadMoreTimeLine() {
        // Tải thêm timeline
        //LayoutLoadingManager.Show_OnLoading(Home.loadingTimeLine, context.getString(R.string.load_more_transactin), 30);
        int positionStaff = Home.bindingRight.history.spSelectStaff.getSelectedItemPosition() - 1;
        if (positionStaff >= 0)
            EventPool.control().enQueue(new EventType.EventLoadTimeLinesRequest(Home.arrStaff.get(positionStaff).id_employee, Home.bindingRight.history.spStyleView.getSelectedItemPosition(), Home.lastIdTimeLine));
        else
            EventPool.control().enQueue(new EventType.EventLoadTimeLinesRequest(positionStaff, Home.bindingRight.history.spStyleView.getSelectedItemPosition(), Home.lastIdTimeLine));
        Home.swipeTimeLineBottom.setRefreshing(false);
    }

    private void reFreshTimeLine() {
        //Tải lại timeline
        // LayoutLoadingManager.Show_OnLoading(Home.loadingTimeLine, context.getString(R.string.refresh), 30);
        Home.timelinesArrayList.clear();
        Home.adapterTimeLine.notifyDataSetChanged();
        int positionStaff = Home.bindingRight.history.spSelectStaff.getSelectedItemPosition() - 1;
        LayoutLoadingManager.Show_OnLoading(Home.loadingHistory, context.getString(R.string.load_timeline), 30);
        if (positionStaff >= 0)
            EventPool.control().enQueue(new EventType.EventLoadTimeLinesRequest(Home.arrStaff.get(positionStaff).id_employee, Home.bindingRight.history.spStyleView.getSelectedItemPosition(), -1));
        else
            EventPool.control().enQueue(new EventType.EventLoadTimeLinesRequest(positionStaff, Home.bindingRight.history.spStyleView.getSelectedItemPosition(), -1));
        Home.swipeTimeLine.setRefreshing(false);
    }

    private void reFreshInventoryEmployee() {
        inventoryEmployeesArrayList.clear();
        adapterInventoryEmployee.notifyDataSetChanged();
        LayoutLoadingManager.Show_OnLoading(Home.loadingInventoryEmployee, context.getString(R.string.load_inventory_employee), 30);
        EventPool.control().enQueue(new EventType.EventLoadInventoryEmployeesRequest(-1, toDatenventoryEmployee, nowIdInventoryEmployee, nowInventoryGroup, Home.bindingRight.inventoryEmployee.spInventoryStock.getSelectedItemPosition(), filterInventoryEmployee));
        Home.swipeInventoryEmployee.setRefreshing(false);
    }

    private void acceptOrder() {
        //XU LY LUU DON HANG HIEN LIST ITEM
        int size = productArrayList.size();
        for (int i = 0; i < size; i++) {
            Product p = productArrayList.get(i);
            if (Home.hashListQuantity.get(p.no_) != null) {
                OrderDetail detail = new OrderDetail();
                if (Home.hashListPrice.get(p.no_) != null) {
                    detail.unitprice = Home.hashListPrice.get(p.no_);
                } else {
                    detail.unitprice = p.price;
                }
                int quantity = Home.hashListQuantity.get(p.no_);
                detail.id_item = p.id;
                detail.quantity = quantity;
                detail.discountPercent = 0;
                detail.discountAmount = 0;
                detail.itemType = 0;
                detail.note = "";
                detail.status = 0;
                detail.itemNo_ = p.no_;
                detail.name = p.name;
                Home.orderDetailArrayList.add(detail);
                Home.nowAmountSale += quantity * detail.unitprice;
                Home.nowAmount = Home.nowAmountSale - Home.nowDiscount;
            }
        }
        if (MyMethod.isOrderEditing) {
            Home.orderListOrderDetailAdapter.setItems(Home.orderDetailArrayList);
            Home.orderListOrderDetailAdapter.notifyDataSetChanged();
            Home.txtOrderDetailAmountSale.setText(Utils.formatFloat(Home.nowAmountSale) + context.getString(R.string.money));
            Home.txtOrderDetailAmount.setText(Utils.formatFloat(Home.nowAmount) + context.getString(R.string.money));
            Home.txtOrderDetailDiscount.setText(Utils.formatFloat(Home.nowDiscount) + context.getString(R.string.money));
        } else {
            Home.orderListProductAdapter.setItems(Home.orderDetailArrayList);
            Home.orderListProductAdapter.notifyDataSetChanged();
            Home.txtOrderAmountSale.setText(Utils.formatFloat(Home.nowAmountSale) + context.getString(R.string.money));
            Home.txtOrderAmount.setText(Utils.formatFloat(Home.nowAmount) + context.getString(R.string.money));
            Home.txtOrderDiscount.setText(Utils.formatFloat(Home.nowDiscount) + context.getString(R.string.money));
        }

    }

    private void viewImage(String urlRoot, String url, String name) {
        final File file = MyMethod.createFolder(Const.REPORTIMAGEDMS, url);
        final Dialog dlg = new Dialog(getContext(), android.R.style.Theme_DeviceDefault_Light);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(R.layout.dialog_image);
        TextView txtTitle = (TextView) dlg.findViewById(R.id.txt_title_dialog);
        final SubsamplingScaleImageView img = (SubsamplingScaleImageView) dlg.findViewById(R.id.imageReport);
        Button btnClose = (Button) dlg.findViewById(R.id.btnDialogCloseImage);
        final ImageButton imgDownload = (ImageButton) dlg.findViewById(R.id.img_download);
        ImageButton imgOpenDownload = (ImageButton) dlg.findViewById(R.id.img_open_download);
        ImageButton imgLeftArrow = (ImageButton) dlg.findViewById(R.id.img_back_dialog_image);
        LoadingView imgLoadingView = (LoadingView) dlg.findViewById(R.id.ImageReportLoadingView);
        imgLoadingView.setLoading(true);
        final ImageDownloader imageDownloader = new ImageDownloader(img, imgLoadingView);
        imageDownloader.execute(urlRoot + url);
        txtTitle.setText(name);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (file.length() == 0) file.delete();
                imageDownloader.cancel(true);
                dlg.dismiss();
            }
        });
        imgDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OutputStream out = null;
                try {
                    if (file.length() == 0) {
                        out = new BufferedOutputStream(new FileOutputStream(file));
                        bitmapCustomerDownload.compress(Bitmap.CompressFormat.PNG, 100, out);
                        if (out != null) {
                            out.close();
                        }
                        Toast.makeText(context, context.getString(R.string.saved_image) + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, context.getString(R.string.image_exist), Toast.LENGTH_SHORT).show();
                    }
                    imgDownload.setEnabled(false);
                } catch (Exception e) {
                    Log.e("imgDownload", e.toString());
                    Toast.makeText(context, context.getString(R.string.error_download), Toast.LENGTH_SHORT).show();
                }
            }
        });
        imgOpenDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (file.length() != 0) {
                        Intent i = new Intent();
                        i.setAction(Intent.ACTION_VIEW);
                        i.setDataAndType(Uri.fromFile(file), "image/*");
                        context.startActivity(i);
                    } else {
                        Toast.makeText(context, context.getString(R.string.please_download_after_view), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e("imgOpenDownload", e.toString());
                }
            }
        });
        imgLeftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (file.length() == 0) file.delete();
                imageDownloader.cancel(true);
                dlg.dismiss();
            }
        });
        dlg.show();
    }

    private void viewCustomerImage() {
        final File file = MyMethod.createFolder(Const.REPORTIMAGEDMS, nowCustomer.imageUrl);
        final Dialog dlg = new Dialog(getContext(), android.R.style.Theme_DeviceDefault_Light);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(R.layout.dialog_image);
        TextView txtTitle = (TextView) dlg.findViewById(R.id.txt_title_dialog);
        final SubsamplingScaleImageView img = (SubsamplingScaleImageView) dlg.findViewById(R.id.imageReport);
        Button btnClose = (Button) dlg.findViewById(R.id.btnDialogCloseImage);
        final ImageButton imgDownload = (ImageButton) dlg.findViewById(R.id.img_download);
        ImageButton imgOpenDownload = (ImageButton) dlg.findViewById(R.id.img_open_download);
        ImageButton imgLeftArrow = (ImageButton) dlg.findViewById(R.id.img_back_dialog_image);
        LoadingView imgLoadingView = (LoadingView) dlg.findViewById(R.id.ImageReportLoadingView);
        imgLoadingView.setLoading(true);
        final ImageDownloader imageDownloader = new ImageDownloader(img, imgLoadingView);
        imageDownloader.execute(Const.LinkCustomerImage + nowCustomer.imageUrl);


        txtTitle.setText(nowCustomer.name);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (file.length() == 0) file.delete();
                imageDownloader.cancel(true);
                dlg.dismiss();
            }
        });
        imgDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OutputStream out = null;
                try {
                    if (file.length() == 0) {
                        out = new BufferedOutputStream(new FileOutputStream(file));
                        bitmapCustomerDownload.compress(Bitmap.CompressFormat.PNG, 100, out);
                        if (out != null) {
                            out.close();
                        }
                        Toast.makeText(context, context.getString(R.string.saved_image) + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, context.getString(R.string.image_exist), Toast.LENGTH_SHORT).show();

                    }
                    imgDownload.setEnabled(false);
                } catch (Exception e) {
                    Log.e("imgDownload", e.toString());
                    Toast.makeText(context, context.getString(R.string.error_download), Toast.LENGTH_SHORT).show();
                }
            }
        });
        imgOpenDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (file.length() != 0) {

                        Intent i = new Intent();
                        i.setAction(Intent.ACTION_VIEW);
                        i.setDataAndType(Uri.fromFile(file), "image/*");
                        context.startActivity(i);
                    } else {
                        Toast.makeText(context, context.getString(R.string.please_download_after_view), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e("imgOpenDownload", e.toString());
                }
            }
        });
        imgLeftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (file.length() == 0) file.delete();
                imageDownloader.cancel(true);
                dlg.dismiss();
            }
        });
        dlg.show();
    }

    public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    //Xử lí khi nhấn lâu vào cái gì đó
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        switch (parent.getId()) {
            case R.id.order_list_product:
                showPopup(POPUP.Order, position);

                break;
            case R.id.order_detail_list:
                showPopup(POPUP.OrderDetail, position);

                break;
            default:
                break;
        }
        return false;
    }

    private void editValue(final POPUP popup, final int position) {
        positionEditOrder = position;
        MyMethod.isEdit_Product = true;
        switch (popup) {
            case Order:
                MyMethod.isInputInOrder = true;
                break;
            case OrderDetail:
                MyMethod.isInputInOrder = false;
                break;
            default:
                break;
        }
        updateInputValueProduct(Home.orderDetailArrayList.get(position).no_, Home.orderDetailArrayList.get(position).name, Home.orderDetailArrayList.get(position).unitprice);
        Home.LayoutMyManager.ShowDialog(Layouts.InputValue);
        MyMethod.requestFocus(editQuantity);

    }

    //Xác nhận xóa sản phẩm ra khỏi đơn hàng
    private void confirmDelete(final POPUP popup, final int i) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(context.getString(R.string.confirm));
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Home.nowAmountSale = Home.nowAmountSale - Home.orderDetailArrayList.get(i).unitprice * Home.orderDetailArrayList.get(i).quantity;
                Home.nowAmount = Home.nowAmountSale - Home.nowDiscount;
                Home.orderDetailArrayList.remove(i);
                switch (popup) {
                    case Order:
                        Home.orderListProductAdapter.setItems(Home.orderDetailArrayList);
                        Home.orderListProductAdapter.notifyDataSetChanged();
                        Home.txtOrderAmountSale.setText(Utils.formatFloat(Home.nowAmountSale) + context.getString(R.string.money));
                        Home.txtOrderAmount.setText(Utils.formatFloat(Home.nowAmount) + context.getString(R.string.money));
                        break;
                    case OrderDetail:
                        Home.orderListOrderDetailAdapter.setItems(Home.orderDetailArrayList);
                        Home.orderListOrderDetailAdapter.notifyDataSetChanged();
                        Home.txtOrderDetailAmountSale.setText(Utils.formatFloat(Home.nowAmountSale) + context.getString(R.string.money));
                        Home.txtOrderDetailAmount.setText(Utils.formatFloat(Home.nowAmount) + context.getString(R.string.money));
                        break;
                }


                dialog.dismiss();
            }
        })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                    // Create the AlertDialog object and return it
                });
        builder.show();
    }

    private void confirmCreateCustomer() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(context.getString(R.string.confirm_create_customer));
        builder.setIcon(R.drawable.edit_btn);
        builder.setMessage(Home.bindingRight.createCustomer.edCustomerName.getText().toString() + "\n" + Home.bindingRight.createCustomer.edCustomerAddress.getText().toString() + "\n" + Home.bindingRight.createCustomer.edCustomerPhone.getText().toString());
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                submitCreateCustomer();
                dialog.dismiss();
            }
        })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                    // Create the AlertDialog object and return it
                });
        builder.show();
    }

    private void confirmReset() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(context.getString(R.string.confirm_reset));
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Home.bindingRight.createCustomer.edCustomerName.setText("");
                Home.bindingRight.createCustomer.edCustomerPhone.setText("");
                Home.bindingRight.createCustomer.edCustomerAddress.setText("");
                dialog.dismiss();
            }
        })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                    // Create the AlertDialog object and return it
                });
        builder.show();
    }


    class ImageDownloader extends AsyncTask<String, Void, Bitmap> {
        SubsamplingScaleImageView bmImage;
        LoadingView loadingView;

        public ImageDownloader(SubsamplingScaleImageView bmImage, LoadingView loadingView) {
            this.bmImage = bmImage;
            this.loadingView = loadingView;
        }

        protected Bitmap doInBackground(String... urls) {
            String url = urls[0];
            Bitmap mIcon = null;
            try {
                InputStream in = new java.net.URL(url).openStream();
                mIcon = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
            }
            return mIcon;
        }

        protected void onPostExecute(Bitmap result) {
            loadingView.setLoading(false);
            if (result != null) {
                bmImage.setImage(ImageSource.bitmap(result));
                bitmapCustomerDownload = result;
            } else {
                Toast.makeText(context, context.getString(R.string.error_download), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateCustomer() {
        if (isDataCustomer()) {
            LayoutLoadingManager.Show_OnLoading(Home.loadingUpdateCustomer, context.getString(R.string.updating), 30);
            Home.isUpdateCustomerInMap = false;
            EventPool.control().enQueue(new EventType.EventUpdateCustomerRequest(nowCustomer));
        }

    }

    private boolean isDataCustomer() {
        if (Home.bindingRight.customerUpdate.dialogCustomerName.getText().toString().isEmpty()) {
            MyMethod.showToast(context, context.getString(R.string.customer_name_empty));
            return false;
        }
        if (Home.bindingRight.customerUpdate.dialogCustomerAddress.getText().toString().isEmpty()) {
            MyMethod.showToast(context, context.getString(R.string.customer_address_empty));
            return false;
        }
        if (((BitmapDrawable) Home.bindingRight.customerUpdate.dialogCustomerPhoto.getDrawable()).getBitmap() == null) {
            MyMethod.showToast(context, context.getString(R.string.customer_photo_empty));
            return false;
        }
        if (Home.bindingRight.customerUpdate.dialogCustomerLocation.getText().toString().contains("0.0,0.0")) {
            MyMethod.showToast(context, context.getString(R.string.customer_location_empty));
            return false;
        }
        return true;
    }

    //Kiem tra du lieu customer ok

    public enum TYPE {
        NAME, ADDRESS, PHONE, PHOTO, LOCATION
    }

    private void openEdit(TYPE type) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
        builder.setTitle("Chỉnh sửa");
        final EditText input = new EditText(this.getActivity());
        builder.setView(input);
        switch (type) {
            case NAME:
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                input.setText(nameCustomer);
                builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        nameCustomer = input.getText().toString();
                        nowCustomer.name = nameCustomer;
                        Home.bindingRight.customerUpdate.dialogCustomerName.setText(nameCustomer);
                    }
                });
                builder.setNegativeButton("Hủy bỏ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
                break;
            case ADDRESS:
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                input.setText(addressCustomer);
                builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addressCustomer = input.getText().toString();
                        nowCustomer.address = addressCustomer;
                        Home.bindingRight.customerUpdate.dialogCustomerAddress.setText(addressCustomer);
                    }
                });
                builder.setNegativeButton("Hủy bỏ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
                break;
            case PHONE:
                input.setInputType(InputType.TYPE_CLASS_PHONE);
                input.setText(phoneCustomer);
                builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        phoneCustomer = input.getText().toString();
                        nowCustomer.phoneNumber = phoneCustomer;
                        Home.bindingRight.customerUpdate.dialogCustomerPhone.setText(phoneCustomer);
                    }
                });
                builder.setNegativeButton("Hủy bỏ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
                break;
            case PHOTO:
                takePhoto(PHOTOS.Customer);
                break;
            case LOCATION:
                LocationDetector.inst().setHighPrecision(true);
                Home.bindingHome.btnComeBack.setText(context.getString(R.string.update_location) + " " + nowCustomer.name);
                MyMethod.isGetLocationCreateCustomer = false;
                Home.bindingRight.customerUpdateMap.btnUpdateLocationCustomer.setText(context.getString(R.string.update_location));
                showLayout(Layouts.MapUpdate, context);
                Home.mapUpdateFragment.getMapAsync(this);
                break;
            default:
                break;
        }

    }

    @Override
    public void onItemClick(View view, int position) {

        if (MyMethod.isVisible(Home.bindingRight.gcm.linearListGcm)) {
            Home.positionGCM = position;
            showLayout(Layouts.MenuGCMClick, context);
        } else if (MyMethod.isVisible(Home.bindingRight.transaction.linearTransaction)) {
            nowTransaction = transactionArrayList.get(position);
            Home.nowTransactionLine.id_transaction = nowTransaction.rowId;
            Home.positionTransaction = position;
            updateValueTransaction(nowTransaction);
            MyMethod.isTransactionDetailOfHistory = false;
            showLayout(Layouts.TransactionDetail, context);
            transactionLineArrayList.clear();
            adapterTransactionLine.notifyDataSetChanged();
            LayoutLoadingManager.Show_OnLoading(Home.bindingRight.transactionDetail.TransactionLineLoadingView, context.getString(R.string.load_transaction_line), 30);
            EventPool.control().enQueue(new EventType.EventLoadTransactionLinesRequest(nowTransaction.rowId));
            try {
                if (nowTransaction.rowId == MyMethod.IDFromMessageService) {
                    ((NotificationManager) context.getSystemService(Service.NOTIFICATION_SERVICE)).cancel(MessageService.NOTIFICATION_ID);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (MyMethod.isVisible(Home.bindingRight.customer.linearCustomer)) {
            if (PhoneState.inst().isGPS() != 1) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(context.getString(R.string.please_open_gps))
                        .setCancelable(true)
                        .setPositiveButton(context.getString(R.string.turn_on_gps), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Home.isAppLockStop = true;
                                startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), OPENGPS);
                                dialog.dismiss();
                            }
                        })
                ;
                AlertDialog alert = builder.create();
                alert.show();
            } else {
                //Xóa ảnh cửa hàng
                if (imageStorePath != null) imageStorePath = "";
                if (photoStore != null) photoStore = null;
                //--
                if (position == -1) position = 0;
                Home.positionCustomer = position;
                nowCustomer = customerArrayList.get(position);
                updateValueCustomer();
                MyMethod.CheckInCustomer = true;
                MyMethod.isReportNotOrder = false;
                MyMethod.isFinishInTransactionDetail = false;
                MyMethod.isCheckInInTransactionDetail = false;
                MyMethod.isNoteInStore = false;
                MyMethod.isReportStore = false;
                MyMethod.isOrderInStorePressed = false;
                Home.location = null;
                if (googleMap != null) MyMethod.refreshMap(context, googleMap);
                MyMethod.isMapViewImageLocation = false;
                MyMethod.isCheckInCustomerTransactionDetail = false;
                showLayout(Layouts.MapCustomerCheckIn, context);
                Home.mapCustomerCheckinFragment.getMapAsync(this);
                Home.bindingHome.btnComeBack.setText(context.getString(R.string.check_in_customer) + " " + nameCustomer);
            }
        } else if (MyMethod.isVisible(Home.bindingRight.product.linearProduct)) {
            Home.positionProduct = position;
            nowProduct = arrTempProduct.get(position);
            updateValueProduct();
            Home.bindingHome.btnComeBack.setText(nowProduct.name);
            showLayout(Layouts.ProductDetail, context);
        } else if (MyMethod.isVisible(Home.bindingRight.orderMain.linearOrderMain)) {
            Home.positionOrder = position;
            nowOrder = ordersArrayList.get(position);
            if (nowOrder.status == 0 || nowOrder.status == 2) {
                MyMethod.setVisible(Home.bindingRight.orderDetail.orderDetailSaveSend);
                MyMethod.setVisible(Home.bindingRight.orderDetail.orderDetailAddProduct);
                Home.lstOrderDetail.setOnItemLongClickListener(this);
            } else {
                MyMethod.setGone(Home.bindingRight.orderDetail.orderDetailSaveSend);
                MyMethod.setGone(Home.bindingRight.orderDetail.orderDetailAddProduct);
                Home.lstOrderDetail.setOnItemLongClickListener(null);
            }
            Home.LayoutMyManager.ShowLayout(Layouts.OrderDetail);
            if (MyMethod.isInventory) {
                updateInventoryDetailView(context);
                LayoutLoadingManager.Show_OnLoading(Home.loadingOrderDetail, context.getString(R.string.load_inventory), 30);

            } else if (MyMethod.isLoadOrder) {
                updateOrderDetailView(context);
                LayoutLoadingManager.Show_OnLoading(Home.loadingOrderDetail, context.getString(R.string.load_order_detail), 30);

            } else if (MyMethod.isInventoryEmployee) {
                updateInventoryEmployeeDetailView(context);
                LayoutLoadingManager.Show_OnLoading(Home.loadingOrderDetail, context.getString(R.string.load_inventory_employee), 30);

            } else if (MyMethod.isInventoryBill) {
                updateInventoryBillDetailView(context);
                LayoutLoadingManager.Show_OnLoading(Home.loadingOrderDetail, context.getString(R.string.load_inventory_bill), 30);
            }
            Home.orderDetailArrayList.clear();//Xóa dữ liệu cũ trước khi load
            Home.orderListProductAdapter.notifyDataSetChanged();
            EventPool.control().enQueue(new EventType.EventLoadOrderDetailsRequest(nowOrder.rowId));
            Home.bindingHome.txtTile.setVisibility(View.VISIBLE);
            Home.bindingHome.btnComeBack.setVisibility(View.GONE);
            Home.bindingHome.btnComeBack.setText(ordersArrayList.get(position).no_);
            MyMethod.isOrderInTransactionLine = false;

        } else if (MyMethod.isVisible(Home.bindingRight.orderProduct.linearProductOfOrder) && !MyMethod.isVisible(Home.bindingRight.inputValue.linearInputValue)) {
            nowNo_ = productArrayList.get(position).no_;
            MyMethod.isInputInOrder = true;
            updateInputValueProduct(productArrayList.get(position).no_, productArrayList.get(position).name, productArrayList.get(position).price);
            MyMethod.isEdit_Product = false;
            Home.LayoutMyManager.ShowDialog(Layouts.InputValue);
            MyMethod.requestFocus(editQuantity);
        } else if (MyMethod.isVisible(Home.bindingRight.history.linearHistory)) {
            //now transaction
            MyMethod.isTransactionDetailOfHistory = true;
            showLayout(Layouts.TransactionDetail, context);
            LayoutLoadingManager.Show_OnLoading(Home.bindingRight.transactionDetail.TransactionLineLoadingView, context.getString(R.string.load_transaction_line), 30);
            MyMethod.isLoadTransactionByID = true;
            MyMethod.isLoadTransactionByIDInMessage = false;
            EventPool.control().enQueue(new EventType.EventLoadTransactionsRequest(Home.timelinesArrayList.get(position).id_transaction, fromDateTransaction, nowIdEmployeeTransaction, filterTransaction, true, nowTransactionStatus));
            EventPool.control().enQueue(new EventType.EventLoadTransactionLinesRequest(Home.timelinesArrayList.get(position).id_transaction));
        } else if (MyMethod.isVisible(Home.bindingRight.approvalAppLock.linearAppprovalAppLock)) {
            //now approval
            nowApproval = approvalArrayList.get(position);
            updateApprovalView();
            Home.LayoutMyManager.ShowDialog(Layouts.ApprovalButton);
        }
    }

    private void updateApprovalView() {
        Home.bindingRight.approvalButton.txtAppNameApproval.setText(context.getString(R.string.approval_from) + " " + nowApproval.appName + " " + context.getString(R.string.from) + " " + nowApproval.employeeName);
    }

    //Cap nhat thong tin header trong xem chi tiet giao dich
    private void updateValueTransaction(Transaction transaction) {
        //Chi tiết giao dịch
        Home.bindingRight.transactionDetail.refTransactionNameNo.setText(transaction.description + " - " + transaction.no_);
        txtTransactionHeaderTime.setText(Utils.long2String(transaction.createdate));
        txtTransactionHeaderStatus.setText(Utils.statusTransaction(transaction.status));
        txtTransactionHeaderPhone.setText(transaction.phone_no_);
        txtTransactionHeaderAddress.setText(transaction.trans_address);
        txtTransactionHeaderNote.setText(transaction.note);

    }

    private void updateInputValueProduct(String no_, String name, float price) {
        txtTitleInputValueDialog.setText(no_ + " - " + name);
        if (Home.hashListQuantity.get(nowNo_) != null)
            editQuantity.setText(Home.hashListQuantity.get(nowNo_) + "");
        else editQuantity.setText("");
        if (Home.hashListPrice.get(nowNo_) != null)
            editPrice.setText(Utils.formatLocale(Home.hashListPrice.get(nowNo_)));
        else editPrice.setText(Utils.formatLocale(price));

    }

    private void updateValueOrderDetail(ArrayList<OrderDetail> orderDetailArrayList, boolean inTransactionLine) {
        if (inTransactionLine) {
            Home.txtOrderDetailNoName.setText(Home.bindingRight.transactionDetail.refTransactionNameNo.getText().toString());
            Home.txtOrderDetailAmount.setText(MyMethod.getAmount(orderDetailArrayList) + context.getString(R.string.money));
            Home.txtOrderDetailTime.setText(txtTransactionHeaderTime.getText().toString());
            Home.txtOrderDetailAmountSale.setText(MyMethod.getAmountSale(orderDetailArrayList) + context.getString(R.string.money));
            Home.edOrderDetailNote.setText("");//Chưa hiện ghi chú đặt hàng do chưa load order
        } else {
            Home.nowAmountSale = 0;
            Home.nowAmount = 0;
            for (OrderDetail od : orderDetailArrayList) {
                Home.nowAmountSale = Home.nowAmountSale + od.unitprice * od.quantity;
                Home.nowAmount = Home.nowAmount + od.discountAmount + od.unitprice * od.quantity;
            }
            Home.txtOrderDetailNoName.setText(nowOrder.no_ + " - " + nowOrder.name);
            Home.txtOrderDetailAmount.setText(Utils.formatFloat(nowOrder.amount) + context.getString(R.string.money));
            Home.txtOrderDetailTime.setText(Utils.long2DateFull(nowOrder.time));
            Home.txtOrderDetailAmountSale.setText(Utils.formatFloat(nowOrder.amount) + context.getString(R.string.money));
            Home.edOrderDetailNote.setText(nowOrder.note);
        }
        if (nowOrder.document_type == 3) {
            MyMethod.setVisible(Home.img_pre_sale_detail);
        } else {
            MyMethod.setInVisible(Home.img_pre_sale_detail);
        }
        Home.orderListOrderDetailAdapter.setItems(orderDetailArrayList);
        Home.orderListOrderDetailAdapter.notifyDataSetChanged();
    }

    private void updateValueCustomer() {
        try {
            routeCustomer = "T2(S),T3(C),T4(S)";
            nameCustomer = nowCustomer.name;
            addressCustomer = nowCustomer.address;
            phoneCustomer = nowCustomer.phoneNumber;
            photoCustomer = nowCustomer.imageThumb;
            latitudeCustomer = nowCustomer.latitude;
            longtitudeCustomer = nowCustomer.longitude;
            Home.bindingRight.customerUpdate.dialogCustomerNo.setText(nowCustomer.no_);
            Home.bindingRight.customerUpdate.dialogCustomerRoute.setText(routeCustomer);
            Home.bindingRight.customerUpdate.dialogCustomerName.setText(nameCustomer);
            Home.bindingRight.customerUpdate.dialogCustomerAddress.setText(addressCustomer);
            Home.bindingRight.customerUpdate.dialogCustomerPhone.setText(phoneCustomer);
            if (photoCustomer != null)
                Home.bindingRight.customerUpdate.dialogCustomerPhoto.setImageBitmap(photoCustomer);
            else
                Home.bindingRight.customerUpdate.dialogCustomerPhoto.setImageResource(R.mipmap.ic_launcher);
            Home.bindingRight.customerUpdate.dialogCustomerLocation.setText(latitudeCustomer + "," + longtitudeCustomer);
            Home.bindingRight.customerDetail.dialogCustomerNoDetail.setText(nowCustomer.no_);
            Home.bindingRight.customerDetail.dialogCustomerRouteDetail.setText(routeCustomer);
            Home.bindingRight.customerDetail.dialogCustomerNameDetail.setText(nameCustomer);
            Home.bindingRight.customerDetail.dialogCustomerAddressDetail.setText(addressCustomer);
            Home.bindingRight.customerDetail.dialogCustomerPhoneDetail.setText(phoneCustomer);
            if (photoCustomer != null)
                Home.bindingRight.customerDetail.dialogCustomerPhotoDetail.setImageBitmap(photoCustomer);
            else
                Home.bindingRight.customerDetail.dialogCustomerPhotoDetail.setImageResource(R.mipmap.ic_launcher);
            Home.bindingRight.customerDetail.dialogCustomerLocationDetail.setText(latitudeCustomer + "," + longtitudeCustomer);
            latLngCustomer = new LatLng(nowCustomer.latitude, nowCustomer.longitude);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateValueProduct() {
        Home.bindingRight.productDetail.dialogProductNoDetail.setText(nowProduct.no_);
        Home.bindingRight.productDetail.dialogProductNameDetail.setText(nowProduct.name);
        Home.bindingRight.productDetail.dialogProductPriceDetail.setText(Utils.formatFloat(nowProduct.price) + context.getString(R.string.money));
        Home.bindingRight.productDetail.dialogProductUnitDetail.setText(nowProduct.unit);
        Home.bindingRight.productDetail.dialogProductDescriptionDetail.setText(nowProduct.description);
        Glide.with(context).load(MyMethod.getUrlProductImage(nowProduct.imageUrl)).error(R.drawable.product_sample_btn).override(200, 200).centerCrop().into(Home.bindingRight.productDetail.dialogProductPhotoDetail);

    }

    private void submitFormLogin(View v) {
        if (!validateName()) {
            return;
        }
        if (!validatePassword()) {
            return;
        }
        LayoutLoadingManager.Show_OnLoading(Home.loadingLogin, context.getString(R.string.log_in), 30);
        EventPool.control().enQueue(new EventType.EventLoginRequest(editName.getText().toString(), editPass.getText().toString(), editNote.getText().toString()));
        MyMethod.closeFocus(v);

        //showLayout(Layouts.Main);
    }


    private void showLayout(Layouts layout, final Context context) {
        MyMethod.hideKeyboardAll((Activity) context);
        Home.LayoutMyManager.ShowLayout(layout);
        //region OldProcess

        switch (layout) {
            case CheckIn:
                LocationDetector.inst().stopLocationUpdates();
                Home.bindingRight.checkin.relaLayoutCheckin.setVisibility(View.VISIBLE);
                Home.imagePhotoIn.setImageBitmap(Home.bitmapImage);
                Home.bindingHome.txtTile.setVisibility(View.VISIBLE);
                Home.bindingHome.txtTile.setText(context.getString(R.string.check_in_title));
                Home.bindingHome.btnComeBack.setVisibility(View.GONE);
                break;
            case Transaction:
                Home.bindingHome.txtTile.setText(context.getString(R.string.transaction));
                Home.bindingHome.txtTile.setVisibility(View.VISIBLE);
                Home.bindingHome.btnComeBack.setVisibility(View.GONE);

                break;
            case Customer:
                Home.bindingHome.txtTile.setVisibility(View.VISIBLE);
                Home.bindingHome.txtTile.setText(context.getString(R.string.customer));
                Home.bindingHome.btnComeBack.setVisibility(View.GONE);

                break;
            case CustomerDetail:
                Home.bindingHome.txtTile.setVisibility(View.VISIBLE);
                Home.bindingHome.btnComeBack.setVisibility(View.GONE);
                break;
            case ProductDetail:
                Home.bindingHome.txtTile.setVisibility(View.VISIBLE);
                Home.bindingHome.btnComeBack.setVisibility(View.GONE);
                break;
            case CustomerUpdate:
                Home.bindingHome.txtTile.setVisibility(View.VISIBLE);
                Home.bindingHome.btnComeBack.setVisibility(View.GONE);
                break;
            case Product:
                Home.bindingRight.customerUpdate.relaCustomerUpdate.setVisibility(View.GONE);
                Home.bindingHome.txtTile.setVisibility(View.VISIBLE);
                Home.bindingHome.txtTile.setText(context.getString(R.string.product));
                Home.bindingHome.btnComeBack.setVisibility(View.GONE);
                break;
            case Order:
                if (MyMethod.isOrder || MyMethod.isProductSample || MyMethod.isProductDisplay) {
                    Home.bindingHome.txtTile.setText(context.getString(R.string.order) + " " + nowCustomer.name);
                    Home.txtOrderCustomerName.setText(nowCustomer.name);
                    Home.txtOrderCustomerAddress.setText(nowCustomer.address);
                    Home.bindingRight.order.orderCustomerTransDay.setText(Utils.long2DateFull(Model.getServerTime()));
                } else if (MyMethod.isInventoryInput) {
                    Home.bindingHome.txtTile.setText(context.getString(R.string.order) + " " + Model.inst().getConfigValue(Const.ConfigKeys.EmployeeName));
                    Home.txtOrderCustomerName.setText(Model.inst().getConfigValue(Const.ConfigKeys.EmployeeName));
                    Home.txtOrderCustomerAddress.setText("");//Nhập kho không cần địa điểm
                    Home.bindingRight.order.orderCustomerTransDay.setText(Utils.long2DateFull(Model.getServerTime()));

                }
                Home.bindingRight.customerUpdate.relaCustomerUpdate.setVisibility(View.GONE);
                Home.bindingHome.txtTile.setVisibility(View.VISIBLE);
                Home.bindingHome.btnComeBack.setVisibility(View.GONE);
                break;
            case ProductOfOrder:
                Home.bindingRight.customerUpdate.relaCustomerUpdate.setVisibility(View.GONE);
                Home.bindingHome.txtTile.setVisibility(View.VISIBLE);
                Home.bindingHome.btnComeBack.setVisibility(View.GONE);
                break;
            case Setting:
                Home.bindingHome.txtTile.setVisibility(View.VISIBLE);
                Home.bindingHome.txtTile.setText(context.getString(R.string.setting));
                Home.bindingHome.btnComeBack.setVisibility(View.GONE);

                break;
            case LogIn:
                //show hide layouts....
                LayoutLoadingManager.Show_OffLoading(Home.loadingLogin);
                Home.bindingHome.txtTile.setVisibility(View.VISIBLE);
                Home.bindingHome.txtTile.setText(context.getString(R.string.log_in));
                Home.bindingHome.btnComeBack.setVisibility(View.GONE);

                break;

            case MapUpdate:
                Home.bindingHome.txtTile.setVisibility(View.VISIBLE);
                Home.bindingHome.btnComeBack.setVisibility(View.GONE);

                break;
            case MapCustomerView:
                Home.bindingHome.txtTile.setVisibility(View.VISIBLE);
                Home.bindingHome.btnComeBack.setVisibility(View.GONE);


                break;
            case ListGCM:
                //
                Home.bindingHome.txtTile.setText(context.getString(R.string.list_gcm));
                Home.bindingHome.txtTile.setVisibility(View.VISIBLE);
                Home.bindingHome.btnComeBack.setVisibility(View.GONE);

                break;
            case Main:

                loadMenu(context);
                MyMethod.hideKeyboardAll((Activity) context);
                LayoutLoadingManager.Show_OffLoading(Home.loadingLogin);
                Home.bindingHome.btnComeBack.setVisibility(View.GONE);
                Home.bindingHome.txtTile.setVisibility(View.VISIBLE);
                Home.bindingHome.txtTile.setText(context.getString(R.string.main));
                if (Home.error.isEmpty())
                    Home.bindingRight.relaLayoutMain.setVisibility(View.VISIBLE);
                break;
            case MenuGCMClick:
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(arrGCM.get(Home.positionGCM).title).setMessage(arrGCM.get(Home.positionGCM).content)
                        .setCancelable(false)
                        .setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
                break;
            case TransactionDetail:
                if (googleMap != null) googleMap.clear();
                break;
        }

        //endregion
    }

    private boolean isVisible(Layouts layouts) {
        boolean result = false;
        switch (layouts) {
            case Setting:
                result = (MyMethod.isVisible(Home.bindingRight.setting.linearSetting));
                break;
            case LogIn:
                result = (MyMethod.isVisible(Home.bindingRight.login.relativeLayoutLogin));
                break;
        }
        return result;
    }


    private boolean validateName() {
        if (editName.getText().toString().trim().isEmpty() && isVisible(Layouts.LogIn)) {
            inputLayoutName.setError(getString(R.string.err_msg_name));
            MyMethod.requestFocus(editName);
            return false;
        } else {
            inputLayoutName.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateCustomerName() {
        if (Home.bindingRight.createCustomer.edCustomerName.getText().toString().trim().isEmpty()) {
            inputLayoutCustomerName.setError(getString(R.string.customer_name_empty));
            MyMethod.requestFocus(Home.bindingRight.createCustomer.edCustomerName);
            return false;
        } else {
            nowCustomer.name = Home.bindingRight.createCustomer.edCustomerName.getText().toString();
            inputLayoutCustomerName.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateCustomerNo() {
        if (Home.bindingRight.createCustomer.edCustomerNo.getText().toString().trim().isEmpty()) {
            inputLayoutCustomerNo.setError(getString(R.string.customer_no_empty));
            MyMethod.requestFocus(Home.bindingRight.createCustomer.edCustomerNo);
            return false;
        } else {
            nowCustomer.no_ = Home.bindingRight.createCustomer.edCustomerNo.getText().toString();
            inputLayoutCustomerNo.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateCustomerPhone() {
        if (Home.bindingRight.createCustomer.edCustomerPhone.getText().toString().trim().isEmpty()) {
            inputLayoutCustomerPhone.setError(getString(R.string.customer_phone_empty));
            MyMethod.requestFocus(Home.bindingRight.createCustomer.edCustomerPhone);
            return false;
        } else {
            nowCustomer.phoneNumber = Home.bindingRight.createCustomer.edCustomerPhone.getText().toString();
            inputLayoutCustomerPhone.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateCustomerAddress() {
        if (Home.bindingRight.createCustomer.edCustomerAddress.getText().toString().trim().isEmpty()) {
            inputLayoutCustomerAddress.setError(getString(R.string.customer_address_empty));
            MyMethod.requestFocus(Home.bindingRight.createCustomer.edCustomerAddress);
            return false;
        } else {
            nowCustomer.address = Home.bindingRight.createCustomer.edCustomerAddress.getText().toString();
            inputLayoutCustomerAddress.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateCustomerLocation() {
        if (Home.bindingRight.createCustomer.btnCustomerGetLocation.getText().toString().contains(context.getString(R.string.get_location))) {
            MyMethod.showToast(context, context.getString(R.string.please_get_location));
            return false;
        }
        return true;
    }

    private boolean validatePassword() {
        if (editPass.getText().toString().trim().isEmpty() && isVisible(Layouts.LogIn)) {
            inputLayoutPassword.setError(getString(R.string.err_msg_password));
            MyMethod.requestFocus(editPass);
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateNote() {
        if (editNote.getText().toString().trim().isEmpty() && isVisible(Layouts.LogIn)) {
            inputLayoutNote.setError(getString(R.string.err_msg_note));
            MyMethod.requestFocus(editNote);
            return false;
        } else {
            inputLayoutNote.setErrorEnabled(false);
        }
        return true;
    }


    //
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            EventType.EventBase event = EventPool.view().deQueue();
            try {
                while (event != null) {
                    processEvent(event);
                    event = EventPool.view().deQueue();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                //SystemLog.addLog(context, SystemLog.Type.Exception, ex.toString());
            }
        }
    };

    private void processEvent(EventType.EventBase event) {
        switch (event.type) {

            case LogInRoute:
                EventType.EventLogInRouteResult logInRouteResult = (EventType.EventLogInRouteResult) event;
                if (logInRouteResult.success) {
                    if (!MyMethod.logInRouteInSetting) {
                        LayoutLoadingManager.Show_OnLoading(Home.loadingLogin, context.getString(R.string.log_in), 30);
                        EventPool.control().enQueue(new EventType.EventLoginRequest(editName.getText().toString(), editPass.getText().toString(), editNote.getText().toString()));
                    } else
                        Home.LayoutMyManager.ShowLayout(Layouts.Main);
                    Home.bindingRight.setting.txtRouteNow.setText(logInRouteResult.routeName);
                } else {
                    MyMethod.showToast(context, logInRouteResult.message);
                }
                LayoutLoadingManager.Show_OffLoading(Home.bindingRight.loginRoute.LogInRouteLoading);
                break;
            case Logout:
                EventType.EventLogoutResult logoutResult = (EventType.EventLogoutResult) event;
                if (logoutResult.success) {
                    if (Model.inst().getStatusWorking() == Const.StatusWorking.Stopped) {
                        showLayout(Layouts.LogIn, context);
                        LocationDetector.inst().setRequest(false);
                    }
                }
                if (logoutResult.message != null && !logoutResult.message.isEmpty()) {
                    MyMethod.showToast(context, logoutResult.message);
                }
                break;
            case Login:
                EventType.EventLoginResult loginResult = (EventType.EventLoginResult) event;
                switch (loginResult.result) {
                    case 0:
                        if (Model.inst().getStatusWorking() == Const.StatusWorking.Stopped) {
                            MyMethod.showToast(getContext(), getString(R.string.sign_in_fail) + " : " + loginResult.message);
                            LayoutLoadingManager.Show_OffLoading(Home.loadingLogin);
                            showLayout(Layouts.LogIn, context);
                        }
                        break;
                    case 1:
                        if (Model.inst().getStatusWorking() == Const.StatusWorking.Pending) {
                            LayoutLoadingManager.Show_OffLoading(Home.loadingLogin);
                            MyMethod.setVisible(Home.bindingRight.login.linearRestart);
                            MyMethod.setGone(Home.bindingRight.login.linearSignIn);
                            Home.bindingRight.login.txtMessageLogin.setText(loginResult.message);
                        }
                        break;
                    case 2:
                        if (Model.inst().getStatusWorking() == Const.StatusWorking.Tracking) {
                            MyMethod.showToast(getContext(), getString(R.string.sign_in_success));
                            LayoutLoadingManager.Show_OffLoading(Home.loadingLogin);

                            showLayout(Layouts.Main, context);
                            //Home.LayoutMyManager.ShowLayout(Layouts.Main);
                            //NewWay
                            Home.bindingRight.setting.txtCompanyName.setText(Model.inst().getConfigValue(Const.ConfigKeys.CompanyName));
                            Home.bindingRight.setting.txtEmployeeName.setText(Model.inst().getConfigValue(Const.ConfigKeys.EmployeeName) + Utils.getSaleType(Model.inst().getConfigValue(Const.ConfigKeys.PreSale)));
                            LocationDetector.inst().setRequest(true);
                        }
                        break;
                    default:
                        break;
                }
                break;
            case ChangePass:
                EventType.EventChangeResult changeResult = (EventType.EventChangeResult) event;
                if (changeResult.success) {
                    MyMethod.showToast(getContext(), getString(R.string.change_pass_success));
                    LayoutLoadingManager.Show_OffLoading(Home.loadingLogin);
                    showLayout(Layouts.LogIn, context);
                } else {
                    MyMethod.showToast(getContext(), getString(R.string.change_pass_fail));
                    LayoutLoadingManager.Show_OffLoading(Home.loadingLogin);
                    showLayout(Layouts.Setting, context);
                }
                break;
            case LoadGCM:
                arrGCM.clear();
                EventType.EventLoadGCMResult loadGCMResult = (EventType.EventLoadGCMResult) event;
                if (loadGCMResult.arrGCM == null) {
                    MyMethod.showToast(context, context.getString(R.string.error_connect));
                } else if (loadGCMResult.arrGCM.size() == 0) {
                    MyMethod.showToast(context, context.getString(R.string.error_connect));
                } else {
                    showLayout(Layouts.ListGCM, context);
                    for (int i = 0; i < loadGCMResult.arrGCM.size(); i++) {
                        GCM gcm = new GCM();
                        gcm.title = loadGCMResult.arrGCM.get(i).title;
                        gcm.content = loadGCMResult.arrGCM.get(i).content;
                        gcm.date = loadGCMResult.arrGCM.get(i).date;
                        gcm.status = loadGCMResult.arrGCM.get(i).status;
                        arrGCM.add(gcm);
                    }
                    adapterGCM.notifyDataSetChanged();
                }
                LayoutLoadingManager.Show_OffLoading(Home.loadingListGCM);

                break;
            case LoadTransactions:
                EventType.EventLoadTransactionsResult transactionsResult = (EventType.EventLoadTransactionsResult) event;
                if (transactionsResult != null && transactionsResult.success) {
                    if (MyMethod.isLoadTransactionByID) {
                        nowTransaction = transactionsResult.arrTransactions.get(0);
                        updateValueTransaction(nowTransaction);
                    } else {
                        lastRowIdTransaction = transactionsResult.arrTransactions.get(transactionsResult.arrTransactions.size() - 1).rowId;
                        if (MyMethod.isLoadTransactionByIDInMessage) {
                            //Nếu load theo id từ tin nhắn thì xóa trắng
                            transactionArrayList.clear();
                        }
                        transactionArrayList.addAll(transactionsResult.arrTransactions);
                    }
                } else {
                    MyMethod.showToast(context, transactionsResult.message);
                }
                LayoutLoadingManager.Show_OffLoading(Home.loadingTransaction);
                Collections.sort(transactionArrayList, new Comparator<Transaction>() {
                    @Override
                    public int compare(Transaction lhs, Transaction rhs) {
                        if (lhs.modifieddate > rhs.modifieddate) {
                            return -1;
                        } else {
                            if (lhs.modifieddate == rhs.modifieddate) {
                                return 0;
                            } else {
                                return 1;
                            }
                        }
                    }
                });
                // load lai theo loai giao dich
                adapterTransaction.notifyDataSetChanged();
                onItemSelected(Home.bindingRight.transaction.sptransactionStatus, null, Home.bindingRight.transaction.sptransactionStatus.getSelectedItemPosition(), 0);
                break;
            case LoadTransactionLines:
                EventType.EventLoadTransactionLinesResult transactionLinesResult = (EventType.EventLoadTransactionLinesResult) event;
                if (transactionLinesResult != null && transactionLinesResult.success) {
                    transactionLineArrayList.clear();
                    transactionLineArrayList = transactionLinesResult.arrTransactionLine;
                    //Cập nhật trạng thái giao dịch thành đã đọc
                    try {
                        transactionArrayList.get(Home.positionTransaction).is_read = false;
                        adapterTransaction.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    MyMethod.showToast(context, getString(R.string.dont_have_transaction_line));
                    LayoutLoadingManager.Show_OffLoading(Home.bindingRight.transactionDetail.TransactionLineLoadingView);
                }
                Collections.sort(transactionLineArrayList, new Comparator<TransactionLine>() {
                    @Override
                    public int compare(TransactionLine lhs, TransactionLine rhs) {
                        if (lhs.modified_date < rhs.modified_date) {
                            return -1;
                        } else {
                            if (lhs.modified_date == rhs.modified_date) {
                                return 0;
                            } else {
                                return 1;
                            }
                        }
                    }
                });
                Home.nowTransactionLine = transactionLineArrayList.get(0);
                updatePermissionLine(transactionLinesResult.permission);//Cap nhat quyen tuong tac
                latLngTransactionLine = new LatLng(Home.nowTransactionLine.latitude, Home.nowTransactionLine.longitude);
                adapterTransactionLine.setItems(transactionLineArrayList);
                adapterTransactionLine.notifyDataSetChanged();
                LayoutLoadingManager.Show_OffLoading(Home.bindingRight.transactionDetail.TransactionLineLoadingView);
                break;
            case LoadTransactionLinesInStore:
                EventType.EventLoadTransactionLinesInStoreResult transactionLinesInStoreResult = (EventType.EventLoadTransactionLinesInStoreResult) event;
                if (transactionLinesInStoreResult != null && transactionLinesInStoreResult.success) {
                    transactionLineInStoreArrayList.clear();
                    transactionLineInStoreArrayList = transactionLinesInStoreResult.arrTransactionLine;
                } else {
                    MyMethod.showToast(context, getString(R.string.no_connect_load_local));
                    transactionLineInStoreArrayList.clear();
                    transactionLineInStoreArrayList = LocalDB.inst().loadTransactionLine(nowCustomer.id);
                }
                Collections.sort(transactionLineArrayList, new Comparator<TransactionLine>() {
                    @Override
                    public int compare(TransactionLine lhs, TransactionLine rhs) {
                        if (lhs.modified_date > rhs.modified_date) {
                            return -1;
                        } else {
                            if (lhs.modified_date == rhs.modified_date) {
                                return 0;
                            } else {
                                return 1;
                            }
                        }
                    }
                });
                adapterTransactionLineInStore.setItems(transactionLineInStoreArrayList);
                adapterTransactionLineInStore.notifyDataSetChanged();
                LayoutLoadingManager.Show_OffLoading(Home.loadingTransactionLineInStore);
                break;
            case LoadReasonNotOrder:
                EventType.EventLoadReasonNotOrdersResult reasonNotOrdersResult = (EventType.EventLoadReasonNotOrdersResult) event;
                if (reasonNotOrdersResult != null && reasonNotOrdersResult.success) {
                    reasonArrayList.clear();
                    reasonArrayList = reasonNotOrdersResult.arrReasonNotOrder;
                } else {
                    MyMethod.showToast(context, reasonNotOrdersResult.message);
                }
                adapterReasonNotOrder.setItems(reasonArrayList);
                adapterReasonNotOrder.notifyDataSetChanged();
                LayoutLoadingManager.Show_OffLoading(Home.bindingRight.reasonNotOrder.ReasonLoadingView);
                break;
            case LoadCustomers:
                EventType.EventLoadCustomersResult customersResult = (EventType.EventLoadCustomersResult) event;
                if (customersResult.success) {
                    customerArrayList.addAll(customersResult.arrCustomer);
                    lastRowIdCustomer = customersResult.arrCustomer.get(customersResult.arrCustomer.size() - 1).id;

                } else {
                    MyMethod.showToast(context, customersResult.message);

                }

                Collections.sort(customerArrayList, new Comparator<Customer>() {
                    @Override
                    public int compare(Customer lhs, Customer rhs) {
                        if (lhs.distance < rhs.distance) {
                            return -1;
                        } else {
                            if (lhs.distance == rhs.distance) {
                                return 0;
                            } else {
                                return 1;
                            }
                        }
                    }
                });

                adapterCustomer.setItems(customerArrayList);
                adapterCustomer.notifyDataSetChanged();
                LayoutLoadingManager.Show_OffLoading(Home.loadingCustomer);
//                if (!Home.bindingRight.svCustomer.getQuery().toString().isEmpty()) {
//                    adapterCustomer.getFilter().filter(Home.bindingRight.svCustomer.getQuery());
//                }
                break;
            case LoadProducts:
                EventType.EventLoadProductsResult productsResult = (EventType.EventLoadProductsResult) event;
                if (productsResult != null && productsResult.success) {
                    productArrayList.addAll(productsResult.arrProduct);
                    lastRowIdProduct = productsResult.arrProduct.get(productsResult.arrProduct.size() - 1).id;
                } else {
                    MyMethod.showToast(context, productsResult.message);
                }
                if (MyMethod.isProductOfOrder) {
                    LayoutLoadingManager.Show_OffLoading(Home.loadingProductOfOrder);
                    adapterProductOfOrder.setItems(productArrayList);
                    adapterProductOfOrder.notifyDataSetChanged();
                    if (!Home.bindingRight.orderProduct.svProductOfOrder.getQuery().toString().isEmpty()) {
                        adapterProductOfOrder.getFilter().filter(Home.bindingRight.orderProduct.svProductOfOrder.getQuery());
                    }
                    Home.txtOrderProductAmountItem.setText(Html.fromHtml("Tổng số " + "<font color=red>" + productArrayList.size() + " " + "</font>"
                            + "mặt hàng"));
                } else {
                    LayoutLoadingManager.Show_OffLoading(Home.loadingProduct);
                    // load lai theo loai san pham
                    onItemSelected(Home.bindingRight.product.spProductGroup, null, Home.bindingRight.product.spProductGroup.getSelectedItemPosition(), 0);

//                    if (!svProduct.getQuery().toString().isEmpty()) {
//                        adapterProduct.getFilter().filter(svProduct.getQuery());
//                    }


                }


                break;
            case LoadOrders:
                EventType.EventLoadOrdersResult orderMainsResult = (EventType.EventLoadOrdersResult) event;
                if (orderMainsResult != null && orderMainsResult.success) {
//                    ordersArrayList.clear();
//                    Home.hashMapOrderDetails.clear();
                    lastRowIdOrder = orderMainsResult.arrOrders.get(orderMainsResult.arrOrders.size() - 1).rowId;
                    ordersArrayList.addAll(orderMainsResult.arrOrders);

                } else {
                    MyMethod.showToast(context, orderMainsResult.message);
                    //load offline
//                    ordersArrayList.addAll(LocalDB.inst().loadOrder(lastRowIdOrder, filterOrderMain));
//                    lastRowIdOrder = ordersArrayList.get(ordersArrayList.size() - 1).rowId;

                }
                LayoutLoadingManager.Show_OffLoading(Home.loadingOrderMain);
                adapterOrder.setItems(ordersArrayList);
                adapterOrder.notifyDataSetChanged();
                if (!Home.bindingRight.orderMain.svOrderMain.getQuery().toString().isEmpty()) {
                    adapterOrder.getFilter().filter(Home.bindingRight.orderMain.svOrderMain.getQuery());
                }
                //load lại theo loại đơn hàng
                onItemSelected(Home.bindingRight.orderMain.spOrderStatus, null, Home.bindingRight.orderMain.spOrderStatus.getSelectedItemPosition(), 0);
                break;

            case LoadOrderDetails:
                EventType.EventLoadOrderDetailsResult orderDetailsResult = (EventType.EventLoadOrderDetailsResult) event;
                if (orderDetailsResult != null && orderDetailsResult.success) {
                    Home.orderDetailArrayList.clear();
                    Home.orderListProductAdapter.notifyDataSetChanged();
                    Home.orderDetailArrayList = orderDetailsResult.arrOrderDetails;
                    updateValueOrderDetail(Home.orderDetailArrayList, MyMethod.isOrderInTransactionLine);
                } else {
                    MyMethod.showToast(context, orderDetailsResult.message);
                }
                LayoutLoadingManager.Show_OffLoading(Home.loadingOrderDetail);
                break;
            case LoadProductGroups:
                EventType.EventLoadProductGroupsResult productGroupsResult = (EventType.EventLoadProductGroupsResult) event;
                if (productGroupsResult != null && productGroupsResult.success) {
                    arrSpProductGroup.clear();
                    arrSpProductGroupName.clear();
                    arrSpProductGroup = productGroupsResult.arrProductGroup;
                    for (ProductGroup group : arrSpProductGroup)
                        arrSpProductGroupName.add(group.name);
                } else
                    MyMethod.showToast(context, productGroupsResult.message);
                LayoutLoadingManager.Show_OffLoading(Home.loadingProduct);
                adapterSpProductGroup.notifyDataSetChanged();
                break;

            case LoadLocationVisited:
                EventType.EventLoadLocationVisitedResult locationVisitedResult = (EventType.EventLoadLocationVisitedResult) event;
                if (locationVisitedResult != null && locationVisitedResult.success) {
                    imageUrlImageTransactionLine = locationVisitedResult.arrLocationVisited.get(0).url_image;
                    createDateTransactionLine = locationVisitedResult.arrLocationVisited.get(0).created_date;
                    accuracyTransactionLine = locationVisitedResult.arrLocationVisited.get(0).accuracy;
                    latLngTransactionLine = new LatLng(locationVisitedResult.arrLocationVisited.get(0).latitude, locationVisitedResult.arrLocationVisited.get(0).longitude);
                    if (MyMethod.isTransactionMapView) {
                        Home.mapCustomerViewFragment.getMapAsync(this);
                    } else {
                        Home.mapCustomerCheckinFragment.getMapAsync(this);
                    }
                } else MyMethod.showToast(context, locationVisitedResult.message);
                break;
            case LoadCitys:
                EventType.EventLoadCitysResult citysResult = (EventType.EventLoadCitysResult) event;
                if (citysResult != null && citysResult.success) {
                    arrCitys.clear();
                    arrSpCityName.clear();
                    arrCitys = citysResult.arrCitys;
                    for (City city : arrCitys)
                        arrSpCityName.add(city.name);

                } else MyMethod.showToast(context, citysResult.message);
                adapterSpCity.notifyDataSetChanged();
                onItemSelected(Home.bindingRight.createCustomer.spCustomerCity, null, Home.bindingRight.createCustomer.spCustomerCity.getSelectedItemPosition(), 0);
                break;
            case LoadCountys:
                EventType.EventLoadCountysResult countysResult = (EventType.EventLoadCountysResult) event;
                if (countysResult != null && countysResult.success) {
                    arrCountys.clear();
                    arrSpCountyName.clear();
                    arrCountys = countysResult.arrCountys;
                    for (County county : arrCountys)
                        arrSpCountyName.add(county.name);

                } else MyMethod.showToast(context, countysResult.message);
                adapterSpCounty.notifyDataSetChanged();
                onItemSelected(Home.bindingRight.createCustomer.spCustomerCounty, null, Home.bindingRight.createCustomer.spCustomerCounty.getSelectedItemPosition(), 0);
                break;
            case LoadRoutes:
                EventType.EventLoadRoutesResult routesResult = (EventType.EventLoadRoutesResult) event;
                if (routesResult != null && routesResult.success) {
                    arrSpRoute.clear();
                    arrSpRouteName.clear();
                    arrSpRoute = routesResult.arrRoute;
                    for (Route route : arrSpRoute)
                        arrSpRouteName.add(route.name + " (" + route.count + ")");
                    Home.bindingRight.customer.txtCustomerCount.setText(arrSpRoute.get(0).count + " " + context.getString(R.string.customer));
                    reFreshCustomer();//Tải lại khách hàng theo tuyến vừa lấy
                } else {
                    arrSpRoute.clear();
                    arrSpRouteName.clear();
                    Home.bindingRight.customer.txtCustomerCount.setText(routesResult.message);
                    MyMethod.showToast(context, routesResult.message);

                }
                LayoutLoadingManager.Show_OffLoading(Home.loadingCustomer);
                adapterSpCustomer.notifyDataSetChanged();
                break;
            case LocationUpdate:
                EventType.EventLocationUpdateResult locationUpdateResult = (EventType.EventLocationUpdateResult) event;
                if (locationUpdateResult.location != null) {
                    Home.location = locationUpdateResult.location;
                    float oldAccuracy = locationUpdateResult.location.getAccuracy();
                    if (MyMethod.isVisible(Home.bindingRight.mapCheckInCustomer.relaMapCheckinOrder)) {// Nếu đang là màn hình bản đồ chuẩn bị ghé thăm
                        // Getting latitude of the current location
                        double latitude = locationUpdateResult.location.getLatitude();
                        // Getting longitude of the current location
                        double longitude = locationUpdateResult.location.getLongitude();
                        // Creating a LatLng object for the current location
                        LatLng latLng = new LatLng(latitude, longitude);
                        double distance = MyMethod.getDistance(latLngCustomer, latLng);
                        //So sánh vị trí
                        if (distance - oldAccuracy >= Double.parseDouble(Model.inst().getConfigValue(Const.ConfigKeys.DistanceCheckInCustomer))) {
                            Home.bindingRight.mapCheckInCustomer.btnCheckinOrder.setText(context.getString(R.string.get_order_phone));
                        } else {
                            Home.bindingRight.mapCheckInCustomer.btnCheckinOrder.setText(context.getString(R.string.go_store));
                        }
                        Home.bindingRight.mapCheckInCustomer.txtCheckinTitle.setText(context.getString(R.string.distance));
                        Home.bindingRight.mapCheckInCustomer.txtCheckinDistance.setText(Math.round(distance) + "m");
                        if (Home.markerNow != null) Home.markerNow.setPosition(latLng);
                        if (Home.lineNow != null) {
                            Home.lineNow.remove();
                            Home.lineNow = googleMap.addPolyline(new PolylineOptions()
                                    .add(new LatLng(locationUpdateResult.location.getLatitude(), locationUpdateResult.location.getLongitude()), new LatLng(nowCustomer.latitude, nowCustomer.longitude))
                                    .width(5)
                                    .color(Color.BLUE));
                        }
                    } else {
                        double latitude = locationUpdateResult.location.getLatitude();

                        // Getting longitude of the current location
                        double longitude = locationUpdateResult.location.getLongitude();

                        // Creating a LatLng object for the current location
                        LatLng latLng = new LatLng(latitude, longitude);
                        if (Home.markerNow != null) Home.markerNow.setPosition(latLng);

                    }
                } else {
                }
                break;
            case HighPrecisionLocation:
                EventType.EventLoadHighPrecisionLocationResult locationResult = (EventType.EventLoadHighPrecisionLocationResult) event;
                if (locationResult.location != null) {
                    Home.location = locationResult.location;
                    if (MyMethod.isVisible(Home.bindingRight.customerUpdateMap.linearMapUpdate)) {
                        MyMethod.loadMap(googleMap, Home.location, context, true);
                    } else if (MyMethod.isVisible(Home.bindingRight.mapCheckInCustomer.relaMapCheckinOrder)) {
                        Home.mapCustomerCheckinFragment.getMapAsync(this);
                        MyMethod.loadMapPositionCheckInOrder(googleMap, Home.location, context);
                        double distance = MyMethod.getDistance(latLngCustomer, new LatLng(Home.location.getLatitude(), Home.location.getLongitude()));
                        Home.bindingRight.mapCheckInCustomer.txtCheckinTitle.setText(context.getString(R.string.distance));
                        Home.bindingRight.mapCheckInCustomer.txtCheckinDistance.setText(Math.round(distance) + "m");
                        Home.bindingRight.mapCheckInCustomer.btnCheckinOrder.setEnabled(true);
                        float distanceCheckIn = Model.inst().getConfigValue(Const.ConfigKeys.DistanceCheckInCustomer) == null ? 100 : Float.parseFloat(Model.inst().getConfigValue(Const.ConfigKeys.DistanceCheckInCustomer));
                        if (distance <= distanceCheckIn) {
                            Home.bindingRight.mapCheckInCustomer.btnCheckinOrder.setText(context.getString(R.string.get_order));
                        } else {
                            Home.bindingRight.mapCheckInCustomer.btnCheckinOrder.setText(context.getString(R.string.get_order_phone));
                        }
                    }

                } else {
                    MyMethod.showToast(context, context.getString(R.string.location_none));
                }
                LayoutLoadingManager.Show_OffLoading(Home.loadingMapCustomerCheckIn);

                break;
            case GCMMessage:
                if (MyMethod.isCanNotice()) {
                    EventType.EventGCMMessageToView gcmMessage = (EventType.EventGCMMessageToView) event;
                    Log.w("Message", "tin nhắn thông báo : " + gcmMessage.message + "vào lúc " + gcmMessage.sendDate);
                    Intent messageService = new Intent(context, MessageService.class);
                    messageService.putExtra("MessageBody", gcmMessage.message + gcmMessage.sendDate);
                    messageService.putExtra("API", Const.UpdateVersion);
                    context.startService(messageService);
                    if (Home.bindingCenter.txtNotifyFragment != null)
                        Home.bindingCenter.txtNotifyFragment.setText(gcmMessage.message);
                }
                break;
            case GetUsers:
                EventType.EventGetUsersResult eventGetUsersResult = (EventType.EventGetUsersResult) event;
                Home.arrStaff.clear();
                if (eventGetUsersResult.arrayUsers == null || eventGetUsersResult.arrayUsers.length <= 0) {
                    MyMethod.showToast(context, context.getString(R.string.none_staff));
                } else {
                    Collections.addAll(Home.arrStaff, eventGetUsersResult.arrayUsers);

                }
                LayoutLoadingManager.Show_OffLoading(Home.loadingCustomer);
                if (!MyMethod.isUpdateLocation) {
                    Home.adapterStaff.notifyDataSetChanged();
                }

                break;
            case Error:
                EventType.EventError eventError = (EventType.EventError) event;
                if (eventError.message.length() > 0) {
                    Home.error = eventError.message;
                    MyMethod.setVisible(Home.bindingRight.linearError);
                    MyMethod.setGone(Home.bindingRight.relaLayoutMain);
                    Home.bindingRight.txtMessageError.setText(eventError.message);
                }
                break;
            case Report:
                EventType.EventReportResult eventReportResult = (EventType.EventReportResult) event;
                if (eventReportResult.success) {
                    MyMethod.showToast(context, context.getString(R.string.send_success));
                    Home.nowTransactionLine.create_date = Model.getServerTime();
                    Home.nowTransactionLine.modified_date = Model.getServerTime();
                    if (MyMethod.isReportStore) {
                        MyMethod.isReportedStore = true;
                        if (MyMethod.isOrderInStorePressed) {
                            showLayout(Layouts.Order, context);
                        }
                        Home.nowTransactionLine.location_ref_id = Integer.parseInt(eventReportResult.message);
                    } else if (MyMethod.CheckInCustomer) {
                        Home.bindingHome.btnComeBack.setText(nowCustomer.name);
                        showLayout(Layouts.CustomerDetail, context);
                    } else if (MyMethod.isCheckInInTransactionDetail)//Nếu đang ghi nhận trong chi tiết giao dịch
                    {
                        Home.nowTransactionLine.id_transaction = nowTransaction.rowId;
                        Home.nowTransactionLine.id_customer = nowTransaction.id_customer;
                        Home.nowTransactionLine.location_ref_id = Integer.parseInt(eventReportResult.message);
                        Home.nowTransactionLine.status = 99;
                        Home.nowTransactionLine.note = Home.bindingRight.checkin.editCheckIn.getText().toString();
                        Home.nowTransactionLine.id_transaction_define = Const.TransactionType.Transaction.getValue();
                        showLayout(Layouts.TransactionDetail, context);
                        EventPool.control().enQueue(new EventType.EventSendTransactionRequest(Home.nowTransactionLine));
                    } else if (MyMethod.isFinishInTransactionDetail) {
                        Home.nowTransactionLine.id_transaction = nowTransaction.rowId;
                        Home.nowTransactionLine.id_customer = nowTransaction.id_customer;
                        Home.nowTransactionLine.location_ref_id = Integer.parseInt(eventReportResult.message);
                        Home.nowTransactionLine.status = 99;
                        Home.nowTransactionLine.note = Home.bindingRight.checkin.editCheckIn.getText().toString();
                        Home.nowTransactionLine.id_transaction_define = Const.TransactionType.End.getValue();
                        showLayout(Layouts.TransactionDetail, context);
                        EventPool.control().enQueue(new EventType.EventSendTransactionRequest(Home.nowTransactionLine));
                    } else if (MyMethod.isReportNotOrder) {
                        if (Home.handler != null && Home.runnableStore != null) {
                            Home.handler.removeCallbacks(Home.runnableStore);
                        }
                        Home.nowTransactionLine.location_ref_id = Integer.parseInt(eventReportResult.message);
                        Home.LayoutMyManager.ShowLayout(Layouts.Customer);
                        EventPool.control().enQueue(new EventType.EventSendTransactionRequest(Home.nowTransactionLine));
                    } else if (MyMethod.isNoteInStore) {
                        Home.nowTransactionLine.id_transaction = 0;
                        Home.nowTransactionLine.id_customer = nowCustomer.id;
                        Home.nowTransactionLine.location_ref_id = Integer.parseInt(eventReportResult.message);
                        Home.nowTransactionLine.status = 99;
                        Home.nowTransactionLine.note = Home.bindingRight.checkin.editCheckIn.getText().toString();
                        Home.nowTransactionLine.id_transaction_define = Const.TransactionType.Note.getValue();
                        showLayout(Layouts.GoStore, context);
                        EventPool.control().enQueue(new EventType.EventSendTransactionRequest(Home.nowTransactionLine));

                    } else {
                        //Nếu ghi nhận bình thường
                        Home.nowTransactionLine.id_transaction = 0;
                        Home.nowTransactionLine.id_customer = 0;
                        Home.nowTransactionLine.location_ref_id = Integer.parseInt(eventReportResult.message);
                        Home.nowTransactionLine.status = 99;
                        Home.nowTransactionLine.note = Home.bindingRight.checkin.editCheckIn.getText().toString();
                        Home.nowTransactionLine.id_transaction_define = Const.TransactionType.CheckReport.getValue();
                        showLayout(Layouts.Main, context);
                        EventPool.control().enQueue(new EventType.EventSendTransactionRequest(Home.nowTransactionLine));


                    }


                } else {
                    MyMethod.showToast(context, eventReportResult.message);

                }
                LayoutLoadingManager.Show_OffLoading(Home.loadingCheckIn);

                break;
            case ListApps:
                EventType.EventListAppResult eventListApp = (EventType.EventListAppResult) event;
                if (eventListApp.arrApps != null) {
                    // XU LI APP HAY CHAY
                    if (eventListApp.recentApps) {
                        Home.recentItems = eventListApp.arrApps;
                        Home.adapterRecentGripView = new CustomAdapterGripView(context, Home.recentItems);
                        Home.bindingCenter.gridListRecentApp.setAdapter(Home.adapterRecentGripView);
                        Home.bindingCenter.searchBoxs.gridListAppSearchBox.setAdapter(Home.adapterRecentGripView);
                        Home.adapterRecentGripView.notifyDataSetChanged();
                    } else {
                        Home.allItems = eventListApp.arrApps;
                        Home.adapterGripView = new CustomAdapterGripView(context, Home.allItems);
                        Home.bindingCenter.listApps.gridListApp.setAdapter(Home.adapterGripView);
                        Home.adapterGripView.notifyDataSetChanged();
                    }

                }
                break;
            case UpdateMenus:
                loadMenu(context);
                break;
            case UpdateCustomer:
                EventType.EventUpdateCustomerResult updateResult = (EventType.EventUpdateCustomerResult) event;
                Home.nowTransactionLine.refNo_ = Utils.createRefNo_(20);
                if (MyMethod.UpdateCustomerLocation) {
                    Home.nowTransactionLine.id_transaction = 0;
                    Home.nowTransactionLine.note = "Cập nhật vị trí";
                    Home.nowTransactionLine.id_customer = nowCustomer.id;
                    Home.nowTransactionLine.id_transaction_define = Const.TransactionType.UpdateLocation.getValue();

                } else if (MyMethod.UpdateCustomerImage) {
                    Home.nowTransactionLine.id_transaction = 0;
                    Home.nowTransactionLine.note = "Cập nhật hình ảnh";
                    Home.nowTransactionLine.id_customer = nowCustomer.id;
                    Home.nowTransactionLine.id_transaction_define = Const.TransactionType.UpdateImage.getValue();
                } else if (MyMethod.UpdateCustomerRoute) {
                    Home.nowTransactionLine.id_transaction = 0;
                    Home.nowTransactionLine.note = "Cập nhật tuyến khách hàng";
                    Home.nowTransactionLine.id_customer = nowCustomer.id;
                    Home.nowTransactionLine.id_transaction_define = Const.TransactionType.UpdateRoute.getValue();

                }

                Home.nowTransactionLine.status = 0;
                if (Home.location != null) {
                    Home.nowTransactionLine.location_ref_id = (int) LocalDB.inst().addTracking(new TrackingItem(Home.location, (byte) 2));
                }
                Home.nowTransactionLine.latitude = nowCustomer.latitude;
                Home.nowTransactionLine.longitude = nowCustomer.longitude;
                if (updateResult.success) {
                    if (Home.isUpdateCustomerInMap) {
                        latLngCustomer = new LatLng(nowCustomer.latitude, nowCustomer.longitude);
                        Home.mapCustomerCheckinFragment.getMapAsync(this);
                    } else {
                        Home.bindingHome.btnComeBack.setText(nowCustomer.name);
                        // onItemClick(recyclerCustomer,Home.positionCustomer);
                        updateValueCustomer();
                        showLayout(Layouts.CustomerDetail, context);
                    }
                    nowCustomer = updateResult.customer;//Cập nhật lại thông tin KH
                    try {
                        LocalDB.inst().updateCustomer(customerArrayList.get(Home.positionCustomer).id, nowCustomer, 1);
                        customerArrayList.get(Home.positionCustomer).imageUrl = nowCustomer.imageUrl;
                        customerArrayList.get(Home.positionCustomer).imageThumb = nowCustomer.imageThumb;
                        adapterCustomer.notifyDataSetChanged();
                        Home.LayoutMyManager.ShowLayout(Layouts.MapCustomerCheckIn);

                    } catch (Exception e) {
                        //Neu loi thi la di tu man hinh giao dich
                        LocalDB.inst().updateCustomer(nowCustomer.id, nowCustomer, 1);


                        Home.LayoutMyManager.ShowLayout(Layouts.MapCustomerCheckIn);
                    }
                    MyMethod.showToast(context, context.getString(R.string.update_customer_success));
                    //Cập nhật giao dịch
                    Home.nowTransactionLine.create_date = Model.getServerTime();
                    Home.nowTransactionLine.modified_date = Model.getServerTime();
                    LocalDB.inst().addTransactionLine(Home.nowTransactionLine, 1);
                    EventPool.control().enQueue(new EventType.EventSendTransactionRequest(Home.nowTransactionLine));

                } else {
                    //Cập nhật kh
                    latLngCustomer = new LatLng(nowCustomer.latitude, nowCustomer.longitude);
                    Home.mapCustomerCheckinFragment.getMapAsync(this);
                    LocalDB.inst().updateCustomer(customerArrayList.get(Home.positionCustomer).id, nowCustomer, 0);
                    customerArrayList.get(Home.positionCustomer).imageUrl = nowCustomer.imageUrl;
                    customerArrayList.get(Home.positionCustomer).imageThumb = nowCustomer.imageThumb;
                    adapterCustomer.notifyDataSetChanged();
                    MyMethod.showToast(context, getString(R.string.no_connect_saved_local));
                    Home.nowTransactionLine.create_date = Model.getServerTime();
                    Home.nowTransactionLine.modified_date = Model.getServerTime();
                    LocalDB.inst().addTransactionLine(Home.nowTransactionLine, 0);
                    Home.LayoutMyManager.ShowLayout(Layouts.MapCustomerCheckIn);
                }
                LayoutLoadingManager.Show_OffLoading(Home.loadingUpdateImage);
                LayoutLoadingManager.Show_OffLoading(Home.loadingMapCustomerCheckIn);
                LayoutLoadingManager.Show_OffLoading(Home.loadingUpdateCustomer);
                break;

            case AddCustomer:
                EventType.EventAddCustomerResult addResult = (EventType.EventAddCustomerResult) event;
                if (addResult.success) {
                    //Cậƒp nhật offline
                    nowCustomer.id = addResult.rowID;
                    LocalDB.inst().addCustomer(nowCustomer, 1);
                    clearDataCreateCustomer();
                    Home.bindingHome.btnComeBack.setText(context.getString(R.string.customer));
                    Home.LayoutMyManager.ShowLayout(Layouts.Customer);
                    MyMethod.showToast(context, context.getString(R.string.add_customer_success));
                    reFreshCustomer();
                } else {
                    LocalDB.inst().addCustomer(nowCustomer, 0);
                    MyMethod.showToast(context, getString(R.string.no_connect_saved_local));
                }
                LayoutLoadingManager.Show_OffLoading(Home.loadingCreateCustomer);
                break;

            case SendOrder:
                EventType.EventSendOrderResult orderResult = (EventType.EventSendOrderResult) event;
                if (orderResult.success) {
                    flagOutStore = false;
                    Home.nowIdExtNo = Integer.parseInt(orderResult.message);
                    MyMethod.showToast(context, context.getString(R.string.send_order_success));
                    if (nowCustomer != null) {
                        LocalDB.inst().updateCustomer(nowCustomer.id, Model.getServerTime(), 1);//Cập nhật thời gian đặt hàng
                    }
                    //luu don hang vao local
//                    LocalDB.inst().addOrder(nowOrder, 1);
//                    LocalDB.inst().addOrderDetail(nowOrder.rowId, Home.orderDetailArrayList);
                    //Chuyển thành đã có đơn hàng
                    MyMethod.isHasOrder = true;
                    destroyOrder();
                    if (MyMethod.isInventoryReport) {
                        showLayout(Layouts.GoStore, context);
                        Home.nowTransactionLine.note = "Báo cáo tồn kho";
                        Home.nowTransactionLine.id_customer = nowCustomer.id;
                        Home.nowTransactionLine.id_transaction_define = Const.TransactionType.CheckInventory.getValue();
                    } else if (MyMethod.isOrder) {
                        showLayout(Layouts.GoStore, context);
                        Home.nowTransactionLine.id_customer = nowCustomer.id;
                        if (MyMethod.isOrderPhone) {
                            MyMethod.isInStore = false;
                            if (MyMethod.isCheckInCustomerTransactionDetail) {
                                showLayout(Layouts.TransactionDetail, context);
                                EventPool.control().enQueue(new EventType.EventLoadTransactionLinesRequest(nowTransaction.rowId));
                            } else {
                                showLayout(Layouts.Customer, context);
                            }
                            Home.nowTransactionLine.note = "Đặt hàng qua điện thoại";
                            Home.nowTransactionLine.id_transaction_define = Const.TransactionType.MakerOrderByPhone.getValue();
                        } else {
                            Home.nowTransactionLine.note = "Đặt hàng";
                            Home.nowTransactionLine.id_transaction_define = Const.TransactionType.MakeOrder.getValue();
                        }
                    } else if (MyMethod.isInventoryInput) {
                        showLayout(Layouts.Main, context);
                        Home.nowTransactionLine.note = "Nhập tồn kho";
                        Home.nowTransactionLine.id_customer = 0;
                        Home.nowTransactionLine.id_transaction_define = Const.TransactionType.InventoryInput.getValue();
                    } else if (MyMethod.isProductSample) {
                        showLayout(Layouts.GoStore, context);
                        Home.nowTransactionLine.note = "Hàng mẫu";
                        Home.nowTransactionLine.id_customer = nowCustomer.id;
                        Home.nowTransactionLine.id_transaction_define = Const.TransactionType.ProductSample.getValue();
                    } else if (MyMethod.isProductDisplay) {
                        showLayout(Layouts.GoStore, context);
                        Home.nowTransactionLine.note = "Hàng trưng bày";
                        Home.nowTransactionLine.id_customer = nowCustomer.id;
                        Home.nowTransactionLine.id_transaction_define = Const.TransactionType.ProductDisplay.getValue();

                    } else if (MyMethod.isOrderIncurred) {
                        showLayout(Layouts.GoStore, context);
                        //btnIncurredOrder.setEnabled(false);
                        Home.nowTransactionLine.note = "Phát sinh đơn hàng";
                        Home.nowTransactionLine.id_transaction_define = Const.TransactionType.OrderIncurred.getValue();
                    }
                    Home.nowTransactionLine.status = 0;

                    Home.nowTransactionLine.id_ExtNo_ = Home.nowIdExtNo;
                    if (!MyMethod.isReportStore) {
                        Home.nowTransactionLine.location_ref_id = (int) LocalDB.inst().addTracking(new TrackingItem(Home.location, (byte) 2));
                    }
                    if (Home.location != null) {
                        Home.nowTransactionLine.latitude = Home.location.getLatitude();
                        Home.nowTransactionLine.longitude = Home.location.getLongitude();
                    }
                    EventPool.control().enQueue(new EventType.EventSendTransactionRequest(Home.nowTransactionLine));
                } else {
                    //Chuyển thành đã có đơn hàng
                    MyMethod.isHasOrder = true;
                    MyMethod.showToast(context, getString(R.string.no_connect_saved_local));
//                    LocalDB.inst().addOrder(nowOrder, 0);
//                    LocalDB.inst().addOrderDetail(nowOrder.rowId, Home.orderDetailArrayList);
                    updateMenu();
                    Home.nowTransactionLine.note = "Đặt hàng";
                    Home.nowTransactionLine.id_customer = nowCustomer.id;
                    Home.nowTransactionLine.id_transaction_define = Const.TransactionType.MakeOrder.getValue();
                    Home.nowTransactionLine.status = 0;
                    EventPool.control().enQueue(new EventType.EventSendTransactionRequest(Home.nowTransactionLine));
                    showLayout(Layouts.GoStore, context);

                }
                LayoutLoadingManager.Show_OffLoading(Home.loadingSendOrder);
                break;
            case UpdateOrder:
                EventType.EventUpdateOrderResult updateOrderResult = (EventType.EventUpdateOrderResult) event;
                if (updateOrderResult.success) {
                    MyMethod.showToast(context, context.getString(R.string.update_order_success));
                    Home.LayoutMyManager.ShowLayout(Layouts.OrderList);
                } else
                    MyMethod.showToast(context, updateOrderResult.message);
                break;
            case UpdateData:
                EventType.EventUpdateDataResult eventUpdateDataResult = (EventType.EventUpdateDataResult) event;
                if (eventUpdateDataResult.success) {
                    int type = eventUpdateDataResult.type;
                    switch (type) {
                        case 0:
                            LocalDB.inst().updateSyncTransaction(1);//Cập nhật trạng thái đã gửi
                            ArrayList<TransactionLine> dataTransaction = LocalDB.inst().getTransactionUnsent();
                            int countTransactionUnsent = dataTransaction.size();
                            if (countTransactionUnsent > 0) {
                                ArrayList<Object> arrData = new ArrayList<>(countTransactionUnsent);
                                arrData.addAll(dataTransaction);
                                EventPool.control().enQueue(new EventType.EventUpdateDataRequest(arrData, 0));
                            } else {
                                ArrayList<Customer> dataCustomer = LocalDB.inst().getCustomerUnsent();
                                int countCustomerUnSent = dataCustomer.size();
                                if (countCustomerUnSent > 0) {
                                    ArrayList<Object> arrData = new ArrayList<>(countCustomerUnSent);
                                    arrData.addAll(dataCustomer);
                                    EventPool.control().enQueue(new EventType.EventUpdateDataRequest(arrData, 1));
                                } else {
                                    ArrayList<Order> dataOrder = LocalDB.inst().getOrderUnsents();
                                    int countOrderUnsent = dataOrder.size();
                                    if (countOrderUnsent > 0) {
                                        ArrayList<Object> arrData = new ArrayList<>();
                                        arrData.addAll(dataOrder);
                                        EventPool.control().enQueue(new EventType.EventUpdateDataRequest(arrData, 2));
                                    }
                                }
                            }

                            break;
                        case 1:
                            LocalDB.inst().updateSyncCustomer(1);//Cập nhật trạng thái đã gửi khách hàng
                            ArrayList<Customer> dataCustomer = LocalDB.inst().getCustomerUnsent();
                            int countCustomerUnSent = dataCustomer.size();
                            if (countCustomerUnSent > 0) {
                                ArrayList<Object> arrData = new ArrayList<>(countCustomerUnSent);
                                arrData.addAll(dataCustomer);
                                EventPool.control().enQueue(new EventType.EventUpdateDataRequest(arrData, 1));
                            } else {
                                ArrayList<Order> dataOrder = LocalDB.inst().getOrderUnsents();
                                int countOrderUnsent = dataOrder.size();
                                if (countOrderUnsent > 0) {
                                    ArrayList<Object> arrData = new ArrayList<>();
                                    arrData.addAll(dataOrder);
                                    EventPool.control().enQueue(new EventType.EventUpdateDataRequest(arrData, 2));
                                }
                            }

                            break;
                        case 2:

                            LocalDB.inst().updateSyncOrder(1);//Cap nhat trang thai da gui don hang
                            ArrayList<Order> dataOrder = LocalDB.inst().getOrderUnsents();
                            int countOrderUnsent = dataOrder.size();
                            if (countOrderUnsent > 0) {
                                ArrayList<Object> arrData = new ArrayList<>();
                                arrData.addAll(dataOrder);
                                EventPool.control().enQueue(new EventType.EventUpdateDataRequest(arrData, 2));
                            }
                            MyMethod.showToast(context, getString(R.string.update_data_success));
                            break;
                        default:
                            break;
                    }

                    if (MyMethod.updateBeforeSync) {
                        Home.bindingRight.setting.btnSyncData.setState(AnimDownloadProgressButton.DOWNLOADING);
                        Home.bindingRight.setting.btnSyncData.setMaxProgress(100);
                        LocalDB.inst().deleteSyncData();
                        EventPool.control().enQueue(new EventType.EventSyncDataRequest(0, -1));
                        MyMethod.isSyncDating = true;
                    }

                } else {
                    MyMethod.showToast(context, eventUpdateDataResult.message);
                }
                Home.bindingRight.setting.btnUpdateData.setState(AnimDownloadProgressButton.NORMAL);
                break;
            case SendTransaction:
                EventType.EventSendTransactionResult transactionResult = (EventType.EventSendTransactionResult) event;
                if (transactionResult.success) {
                    Home.nowTransactionLine.create_date = Model.getServerTime();
                    Home.nowTransactionLine.modified_date = Model.getServerTime();
                    LocalDB.inst().addTransactionLine(Home.nowTransactionLine, 1);
                    MyMethod.showToast(context, context.getString(R.string.send_transaction_success));
                    nowOrder.id_parent = Integer.parseInt(transactionResult.message);
                    Home.nowTransactionLine.id_transaction = Integer.parseInt(transactionResult.message);
                    //    destroyTransaction();
                    if (MyMethod.isVisible(Home.bindingRight.mapCheckInCustomer.relaMapCheckinOrder)) {//Nếu đang ở màn hình vị trí KH
                        updateStoreValue();
                        flagOutStore = false;

                    } else if (MyMethod.isVisible(Home.bindingRight.customer.linearCustomer)) {
                        LayoutLoadingManager.Show_OffLoading(Home.loadingSendOrder);
                    } else if (MyMethod.isVisible(Home.bindingRight.inStore.linearInStore)) {//Nếu đang ở trong màn hình cửa hàng
                        if (flagOutStore || MyMethod.isOrderPhone) {
                            MyMethod.isInStore = false;
                            flagOutStore = false;
                        } else {
                            MyMethod.showToast(context, "Vào cửa hàng");
                            LocalDB.inst().updateCustomer(nowCustomer.id, Model.getServerTime(), 0);//Cập nhật thời gian ghé thăm
                        }
                        Home.loadingInStore.setLoading(false);
                    } else if (MyMethod.isVisible(Home.bindingRight.order.linearOrder)) {
                        Home.loadingSendOrder.setLoading(false);
                    } else if (MyMethod.isVisible(Home.bindingRight.transactionDetail.relaTransactionDetail)) {
                        transactionLineArrayList.clear();
                        adapterTransactionLine.notifyDataSetChanged();
                        LayoutLoadingManager.Show_OnLoading(Home.bindingRight.transactionDetail.TransactionLineLoadingView, context.getString(R.string.load_transaction_line), 30);
                        EventPool.control().enQueue(new EventType.EventLoadTransactionLinesRequest(nowTransaction.rowId));
                    } else if (MyMethod.isVisible(Home.bindingRight.reasonNotOrder.linearReasonNotOrder)) {

                        LayoutLoadingManager.Show_OffLoading(Home.bindingRight.reasonNotOrder.ReasonLoadingView);
                    }
                } else {
                    if (Home.nowTransactionLine.create_date == 0)
                        Home.nowTransactionLine.create_date = Model.getServerTime();
                    if (Home.nowTransactionLine.modified_date == 0)
                        Home.nowTransactionLine.modified_date = Model.getServerTime();

                    LocalDB.inst().addTransactionLine(Home.nowTransactionLine, 0);
                    MyMethod.showToast(context, getString(R.string.no_connect_saved_local));
                }
                //Nếu là rời cửa hàng thì set refNo = rỗng
                if (Home.nowTransactionLine.id_transaction_define == Const.TransactionType.CheckOut.getValue()) {
                    Home.nowTransactionLine.refNo_ = "";
                }
                if (MyMethod.isVisible(Home.bindingRight.note.linearInputNote))
                    MyMethod.setGone(Home.bindingRight.note.linearInputNote);
                //Lưu database
                LayoutLoadingManager.Show_OffLoading(Home.loadingMapCustomerCheckIn);

                break;
            case BI_DailyReport: {
                EventType.EventLoadBI_DailyReportResult BIResult = (EventType.EventLoadBI_DailyReportResult) event;
                if (BIResult.success) {
                    LeftFragment.UpdateVisitedView(BIResult.arrData);

                } else
                    MyMethod.showToast(context, BIResult.message);

            }
            break;
            case LoadTimeLines:
                EventType.EventLoadTimeLinesResult timeLinesResult = (EventType.EventLoadTimeLinesResult) event;
                if (timeLinesResult != null && timeLinesResult.success) {
//                    ordersArrayList.clear();
//                    Home.hashMapOrderDetails.clear();
                    Home.lastIdTimeLine = timeLinesResult.arrTimelines.get(timeLinesResult.arrTimelines.size() - 1).id;
                    Home.timelinesArrayList.addAll(timeLinesResult.arrTimelines);
                    Collections.sort(Home.timelinesArrayList, new Comparator<TimeLine>() {
                        @Override
                        public int compare(TimeLine lhs, TimeLine rhs) {
                            if (lhs.time > rhs.time) {
                                return -1;
                            } else {
                                if (lhs.time == rhs.time) {
                                    return 0;
                                } else {
                                    return 1;
                                }
                            }
                        }
                    });
                } else {
                    MyMethod.showToast(context, timeLinesResult.message);
                }
                Home.adapterTimeLine.setItems(Home.timelinesArrayList);
                Home.adapterTimeLine.notifyDataSetChanged();
                if (!Home.bindingRight.history.svTimeLine.getQuery().toString().isEmpty()) {
                    Home.adapterTimeLine.getFilter().filter(Home.bindingRight.history.svTimeLine.getQuery());
                }
                LayoutLoadingManager.Show_OffLoading(Home.loadingHistory);
                break;
            case LoadInventoryEmployees:
                EventType.EventLoadInventoryEmployeesResult inventoryEmployeesResult = (EventType.EventLoadInventoryEmployeesResult) event;
                if (inventoryEmployeesResult != null && inventoryEmployeesResult.success) {
//                    ordersArrayList.clear();
//                    Home.hashMapOrderDetails.clear();
                    lastRowIdInventoryEmployee = inventoryEmployeesResult.arrInventoryEmployees.get(inventoryEmployeesResult.arrInventoryEmployees.size() - 1).id;
                    inventoryEmployeesArrayList.addAll(inventoryEmployeesResult.arrInventoryEmployees);

                } else {
                    MyMethod.showToast(context, inventoryEmployeesResult.message);
                }
                LayoutLoadingManager.Show_OffLoading(Home.loadingInventoryEmployee);
                adapterInventoryEmployee.setItems(inventoryEmployeesArrayList);
                adapterInventoryEmployee.notifyDataSetChanged();
                //load lại theo loại đơn hàng
                onItemSelected(Home.bindingRight.inventoryEmployee.spInventoryGroup, null, Home.bindingRight.inventoryEmployee.spInventoryGroup.getSelectedItemPosition(), 0);
                break;
            case SendRequestGrant:
                EventType.EventSendRequestGrantResult sendRequestGrantResult = (EventType.EventSendRequestGrantResult) event;
                if (sendRequestGrantResult != null && sendRequestGrantResult.success) {
                    MyMethod.showToast(context, context.getString(R.string.send_request_success));
                } else {
                    MyMethod.showToast(context, sendRequestGrantResult.message);
                }
                break;
            case LoadRequestGrants:
                EventType.EventLoadRequestGrantResult loadRequestGrantResult = (EventType.EventLoadRequestGrantResult) event;
                if (loadRequestGrantResult != null && loadRequestGrantResult.success) {
                    approvalArrayList = loadRequestGrantResult.arrApprovals;
                    lastRowIdApproval = approvalArrayList.get(approvalArrayList.size() - 1).id_request;
                } else {
                    MyMethod.showToast(context, loadRequestGrantResult.message);
                }
                LayoutLoadingManager.Show_OffLoading(Home.loadingApprovalAppLock);
                adapterApproval.setItems(approvalArrayList);
                adapterApproval.notifyDataSetChanged();
                break;
            case ApprovalApplock:
                EventType.EventApprovalApplockResult approvalApplockResult = (EventType.EventApprovalApplockResult) event;
                if (approvalApplockResult != null && approvalApplockResult.success) {
                    MyMethod.showToast(context, context.getString(R.string.approval_success));
                    LayoutLoadingManager.Show_OnLoading(Home.loadingApprovalAppLock, context.getString(R.string.load_approval), 30);
                    EventPool.control().enQueue(new EventType.EventLoadRequestGrantRequest(-1, nowIdApproval, Home.bindingRight.approvalAppLock.spApprovalStatus.getSelectedItemPosition(), filtersvApproval));
                } else {
                    MyMethod.showToast(context, approvalApplockResult.message);
                }
                LayoutLoadingManager.Show_OffLoading(Home.loadingApprovalButton);
                Home.bindingHome.btnComeBack.setSoundEffectsEnabled(false);
                Home.bindingHome.btnComeBack.performClick();
                break;
            case RealTimeTracking:
                EventType.EventRealTimeTrackingResult realTimeTrackingResult = (EventType.EventRealTimeTrackingResult) event;
                if (realTimeTrackingResult != null && realTimeTrackingResult.success) {
                    MyMethod.isRealTime = true;
                } else {
                    MyMethod.isRealTime = false;
                    MyMethod.showToast(context, realTimeTrackingResult.message);
                }
                break;
            case LoadReportCheckIn:
                EventType.EventLoadReportCheckInsResult loadReportCheckInsResult = (EventType.EventLoadReportCheckInsResult) event;
                if (loadReportCheckInsResult != null && loadReportCheckInsResult.success) {
                    reportCheckInArrayList.addAll(loadReportCheckInsResult.arrayReportCheckIn);
                    ArrayList<ReportCheckIn> arrTemp = loadReportCheckInsResult.arrayReportCheckIn;
                    Collections.sort(arrTemp, new Comparator<ReportCheckIn>() {
                        @Override
                        public int compare(ReportCheckIn lhs, ReportCheckIn rhs) {
                            if (lhs.id < rhs.id) {
                                return -1;
                            } else {
                                if (lhs.id == rhs.id) {
                                    return 0;
                                } else {
                                    return 1;
                                }
                            }
                        }
                    });
                    lastRowIdReportCheckIn = arrTemp.get(arrTemp.size() - 1).id;
                } else {
                    MyMethod.showToast(context, loadReportCheckInsResult.message);
                }
                LayoutLoadingManager.Show_OffLoading(Home.loadingReportCheckIn);
                Home.visibleTitleRC = 0;
                adapterReportCheckIn.setItems(reportCheckInArrayList);
                adapterReportCheckIn.notifyDataSetChanged();
                break;
            case SyncData:
                EventType.EventSyncDataResult syncDataResult = (EventType.EventSyncDataResult) event;
                if (syncDataResult != null && syncDataResult.success) {
                    int type = syncDataResult.type;
                    int lastId = syncDataResult.lastId;
                    int countData = syncDataResult.countData;// Tổng số dòng cần load

                    ArrayList<Object> arrData = syncDataResult.arrData;
                    switch (type) {
                        case 0://Sản phẩm
                            //Lưu vào SQLite


                            lastRowIdSyncData = ((Product) arrData.get(arrData.size() - 1)).id;
                            if (lastRowIdSyncData > lastId) {//Nếu chưa hết thì mần tiếp
                                EventPool.control().enQueue(new EventType.EventSyncDataRequest(0, lastRowIdSyncData));
                            } else {//hết rồi thì load khách hàng
                                EventPool.control().enQueue(new EventType.EventSyncDataRequest(1, -1));

                            }
                            for (int i = 0; i < arrData.size(); i++) {
                                Product product = (Product) arrData.get(i);
                                LocalDB.inst().addProduct(product);
                                Home.bindingRight.setting.btnSyncData.setProgressText("Sản phẩm", LocalDB.inst().countProduct() * 100 / countData);
                            }
                            break;
                        case 1://Khách hàng

                            lastRowIdSyncData = ((Customer) arrData.get(arrData.size() - 1)).id;
                            if (lastRowIdSyncData > lastId) {//Nếu chưa hết thì mần tiếp
                                EventPool.control().enQueue(new EventType.EventSyncDataRequest(1, lastRowIdSyncData));
                            } else {
                                EventPool.control().enQueue(new EventType.EventSyncDataRequest(2, -1));
                            }
                            for (int i = 0; i < arrData.size(); i++) {
                                Customer customer = (Customer) arrData.get(i);

                                LocalDB.inst().addCustomer(customer, 1);
                                Home.bindingRight.setting.btnSyncData.setProgressText("Khách hàng", LocalDB.inst().countCustomer(-1) * 100 / countData);
                            }
                            break;
                        case 2://Nhan vien

                            Home.arrStaff.clear();
                            Home.arrStaff = LocalDB.inst().loadUser();
                            Home.adapterStaff.setArrayList(Home.arrStaff);
                            if (MyMethod.isVisible(Home.bindingRight.customer.linearCustomer)) {
                                arrSpRoute.clear();
                                arrSpRouteName.clear();
                                arrSpRoute = LocalDB.inst().loadRoute(Home.nowIdCustomer);
                                for (Route route : arrSpRoute)
                                    arrSpRouteName.add(route.name + " (" + route.count + ")");
                                Home.bindingRight.customer.txtCustomerCount.setText(arrSpRoute.get(0).count + " " + context.getString(R.string.customer));
                                reFreshCustomer();//Tải lại khách hàng theo tuyến vừa lấy
                                adapterSpCustomer.notifyDataSetChanged();
                            }
                            //}
                            EventPool.control().enQueue(new EventType.EventSyncDataRequest(3, -1));
                            for (int i = 0; i < arrData.size(); i++) {
                                UserInfo userInfo = (UserInfo) arrData.get(i);
                                LocalDB.inst().addUserInfo(userInfo);
                            }
                            break;
                        case 3:
                            //lý do rời cửa hàng
                            for (int i = 0; i < arrData.size(); i++) {
                                ReasonNotOrder reasonNotOrder = (ReasonNotOrder) arrData.get(i);
                                LocalDB.inst().addReason(reasonNotOrder);

                            }
                            //EventPool.control().enQueue(new EventType.EventSyncDataRequest(4, -1));
                            MyMethod.isSyncDating = false;//dong bo thanh cong
                            break;
                        case 4://Order
                            for (int i = 0; i < arrData.size(); i++) {
                                Order order = (Order) arrData.get(i);
                                LocalDB.inst().addOrder(order, 1);
                                LocalDB.inst().addOrderDetail(order.rowId, order.orderDetails);
                                Home.bindingRight.setting.btnSyncData.setProgressText("Đơn hàng", LocalDB.inst().countOrder(-1) * 100 / countData);
                            }
                            lastRowIdSyncData = ((Order) arrData.get(arrData.size() - 1)).rowId;
                            if (lastRowIdSyncData > lastId) {//Nếu chưa hết thì mần tiếp
                                EventPool.control().enQueue(new EventType.EventSyncDataRequest(4, lastRowIdSyncData));
                            } else {
                                MyMethod.isSyncDating = false;//dong bo thanh cong
                            }
                            break;

                        default:
                            break;
                    }


                } else {
                    if (syncDataResult.type == 0) {//Nếu ko load được sp thì load khách hàng
                        EventPool.control().enQueue(new EventType.EventSyncDataRequest(1, -1));
                    } else {
                        MyMethod.showToast(context, syncDataResult.message);
                    }
                }
                LayoutLoadingManager.Show_OffLoading(Home.loadingCustomer);
                if (Home.bindingRight.setting.btnSyncData.getProgress() >= 100 && syncDataResult.type == 3) {
                    Home.bindingRight.setting.btnSyncData.setState(AnimDownloadProgressButton.INSTALLING);
                    Home.bindingRight.setting.btnSyncData.setCurrentText("Đang lưu ");
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            Home.bindingRight.setting.btnSyncData.setState(AnimDownloadProgressButton.NORMAL);
                            Home.bindingRight.setting.btnSyncData.setCurrentText("Đồng bộ thành công");
                        }
                    }, 1000);
                }
                break;
            case LoadSurveyDataByID:
                EventType.EventLoadSurveyDataByIDResult eventLoadSurveyDataByIDResult = (EventType.EventLoadSurveyDataByIDResult) event;
                if (eventLoadSurveyDataByIDResult.success) {
                    ArrayList<SurveyHeader> arrSurveyHeaders = eventLoadSurveyDataByIDResult.arrSurveyHeaders;
                    ArrayList<SurveyLine> arrSurveyLines = eventLoadSurveyDataByIDResult.arrSurveyLines;
                    Intent intent = new Intent(getActivity(), SurveyQAActivity.class);
                    int ID_Customer = MyMethod.idCustomer;
                    int Root_Customer = MyMethod.rootCustomer;
                    SurveyCampaign selectedSurvey = eventLoadSurveyDataByIDResult.surveyCampaign;
                    intent.putExtra("Campaign", selectedSurvey);
                    intent.putParcelableArrayListExtra("Headers", arrSurveyHeaders);
                    intent.putParcelableArrayListExtra("Lines", arrSurveyLines);
                    intent.putExtra("IDCustomer", ID_Customer);
                    intent.putExtra("RootIDCustomer", Root_Customer);
                    startActivity(intent);
                } else {
                    MyMethod.showToast(context, getString(R.string.no_connect_load_local));
                    SurveyCampaign selectedSurvey = LocalDB.inst().getListCampaigns(MyMethod.idCampaign);
                    ArrayList<SurveyHeader> arrSurveyHeaders = LocalDB.inst().loadSurveyHeader(selectedSurvey.id);
                    List<Integer> idHeaders = MyMethod.getListIDHeader(arrSurveyHeaders);
                    ArrayList<SurveyLine> arrSurveyLines = LocalDB.inst().loadSurveyLine(idHeaders, MyMethod.idCustomer);
                    Intent intent = new Intent(getActivity(), SurveyQAActivity.class);
                    int ID_Customer = MyMethod.idCustomer;
                    int Root_Customer = MyMethod.rootCustomer;
                    intent.putExtra("Campaign", selectedSurvey);
                    intent.putParcelableArrayListExtra("Headers", arrSurveyHeaders);
                    intent.putParcelableArrayListExtra("Lines", arrSurveyLines);
                    intent.putExtra("IDCustomer", ID_Customer);
                    intent.putExtra("RootIDCustomer", Root_Customer);
                    startActivity(intent);
                }
                break;

            case AcceptWork:
                EventType.EventAcceptWorkResult eventAcceptWorkResult = (EventType.EventAcceptWorkResult) event;
                if (eventAcceptWorkResult.success) {
                    updatePermissionLine(2);
                    MyMethod.showToast(context, eventAcceptWorkResult.message);
                } else {
                    if (eventAcceptWorkResult.result == 2) {//Neu cong viec da co nguoi nhan
                        MyMethod.showToast(context, eventAcceptWorkResult.message);
                        updatePermissionLine(0);
                    } else {
                        updatePermissionLine(1);
                        MyMethod.showToast(context, eventAcceptWorkResult.message);
                    }

                }
                EventPool.control().enQueue(new EventType.EventLoadTransactionLinesRequest(eventAcceptWorkResult.id));
                break;
            case RejectWork:
                EventType.EventRejectWorkResult eventRejectWorkResult = (EventType.EventRejectWorkResult) event;
                if (eventRejectWorkResult.success) {
                    MyMethod.showToast(context, eventRejectWorkResult.message);
                    Home.bindingHome.btnComeBack.setSoundEffectsEnabled(false);
                    Home.bindingHome.btnComeBack.performClick();

                } else {
                        MyMethod.showToast(context, eventRejectWorkResult.message);


                }
                LayoutLoadingManager.Show_OffLoading(Home.bindingRight.transactionDetail.TransactionLineLoadingView);
                break;
            default:
                Log.w(TAG, "unhandled " + event.type);
                break;
        }
    }

    private void updatePermissionLine(int permission) {
        switch (permission) {
            case 0://chi ghi chu
                Home.bindingRight.transactionDetail.linearCheckinNoteRecord.setVisibility(View.VISIBLE);
                Home.bindingRight.transactionDetail.btnTransactionNote.setVisibility(View.VISIBLE);
                Home.bindingRight.transactionDetail.btnTransactionWrite.setVisibility(View.GONE);
                Home.bindingRight.transactionDetail.btnTransactionCheckIn.setVisibility(View.GONE);
                Home.bindingRight.transactionDetail.btnAcceptWork.setVisibility(View.GONE);
                break;
            case 1://Chap nhan giao dich
                Home.bindingRight.transactionDetail.linearCheckinNoteRecord.setVisibility(View.GONE);
                Home.bindingRight.transactionDetail.btnAcceptWork.setVisibility(View.VISIBLE);
                break;
            case 2:
                Home.bindingRight.transactionDetail.linearCheckinNoteRecord.setVisibility(View.VISIBLE);
                Home.bindingRight.transactionDetail.btnTransactionNote.setVisibility(View.VISIBLE);
                Home.bindingRight.transactionDetail.btnTransactionWrite.setVisibility(View.VISIBLE);
                Home.bindingRight.transactionDetail.btnTransactionCheckIn.setVisibility(View.VISIBLE);
                Home.bindingRight.transactionDetail.btnAcceptWork.setVisibility(View.GONE);

                break;
            default:
                break;
        }
    }

    private void clearDataCreateCustomer() {
        nowCustomer = new Customer();
        Home.bindingRight.createCustomer.edCustomerName.setText("");
        Home.bindingRight.createCustomer.edCustomerPhone.setText("");
        Home.bindingRight.createCustomer.edCustomerAddress.setText("");
        edCustomerNo.setText("");
        Home.bindingRight.createCustomer.btnCustomerGetLocation.setText(context.getString(R.string.get_location));
        imageCreateCustomerPath = "";
        photoCreateCustomer = null;
        Home.bindingRight.createCustomer.imageCreateCustomer.setImageResource(R.drawable.camera_btn);
    }

    private void updateStoreValue() {
        transactionLineInStoreArrayList.clear();
        adapterTransactionLineInStore.notifyDataSetChanged();
        txtInStoreName.setText(nowCustomer.name);
        txtInStoreAddress.setText(nowCustomer.address);
        txtInStorePhone.setText(nowCustomer.phoneNumber);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (googleMap != null) {
            this.googleMap = googleMap;
            Marker transactionLineMarker;
            Marker transactionHeaderMarker;
            if (MyMethod.isVisible(Home.bindingRight.customerViewMap.linearMapView) && (latLngCustomer != null || latLngTransaction != null)) {
                if (googleMap != null) googleMap.clear();
                if (!MyMethod.isTransactionMapView) {
                    googleMap.addMarker(new MarkerOptions()
                            .position(latLngCustomer)
                            .title(nowCustomer.no_)
                            .snippet(nameCustomer + "\n" + phoneCustomer + "\n" + addressCustomer)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_dms_btn))).showInfoWindow();
                    googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                        @Override
                        public View getInfoWindow(Marker arg0) {
                            return null;
                        }

                        @Override
                        public View getInfoContents(Marker marker) {
                            View v = LayoutInflater.from(context).inflate(R.layout.custom_info_window, null);
                            TextView title = (TextView) v.findViewById(R.id.title);
                            title.setText(marker.getTitle());
                            TextView snippet = (TextView) v.findViewById(R.id.snippet);
                            snippet.setText(marker.getSnippet());
                            ImageView imageView = (ImageView) v.findViewById(R.id.imageMarker);
                            imageView.setImageBitmap(((BitmapDrawable) Home.bindingRight.customerDetail.dialogCustomerPhotoDetail.getDrawable()).getBitmap());
                            return v;
                        }
                    });
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngCustomer, 5.5f));
                } else {// Xem bản đồ trong chi tiết giao dịch (nút bản đồ)
                    try {
                        transactionHeaderMarker = googleMap.addMarker(new MarkerOptions()
                                .position(latLngTransaction)
                                .title(nowTransaction.no_)
                                .snippet(nowTransaction.description + "\n" + nowTransaction.phone_no_ + "\n" + nowTransaction.trans_address)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_dms_btn))
                        );
                        transactionLineMarker = googleMap.addMarker(new MarkerOptions()
                                .position(latLngTransactionLine)
                                .title(Home.nowTransactionLine.name_employee)
                                .snippet(Utils.long2String(Home.nowTransactionLine.create_date) + "\n" + MyMethod.getAddress(Home.nowTransactionLine.latitude, Home.nowTransactionLine.longitude, context) + "\n" + Home.nowTransactionLine.userid)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.now_btn)));
                        //Vẽ khoảng cách
                        googleMap.addPolyline(new PolylineOptions()
                                .add(latLngTransactionLine, latLngTransaction)
                                .width(5)
                                .color(Color.BLUE));
                        transactionHeaderMarker.showInfoWindow();
                        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                            @Override
                            public View getInfoWindow(Marker arg0) {
                                return null;
                            }

                            @Override
                            public View getInfoContents(Marker marker) {
                                View v = LayoutInflater.from(context).inflate(R.layout.custom_info_window, null);
                                TextView title = (TextView) v.findViewById(R.id.title);
                                title.setText(marker.getTitle());
                                TextView snippet = (TextView) v.findViewById(R.id.snippet);
                                snippet.setText(marker.getSnippet());

                                return v;
                            }
                        });
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngTransaction, 15.5f));
                    } catch (Exception e) {
                        MyMethod.showToast(context, context.getString(R.string.transaction_location_none));
                    }
                }

            } else if (MyMethod.isVisible(Home.bindingRight.mapCheckInCustomer.relaMapCheckinOrder)) {
                if (googleMap != null) googleMap.clear();
                if (MyMethod.isMapViewImageLocation) {
                    //Nếu là map hiển thị ảnh và vị trí
                    Home.bindingRight.mapCheckInCustomer.btnCheckinOrder.setVisibility(View.INVISIBLE);
                    MyMethod.setGone(Home.btnMapCustomerUpdateLocation);
                    MyMethod.setGone(Home.btnMapCustomerUpdateImage);
                    LatLng line = new LatLng(Home.nowTransactionLine.latitude, Home.nowTransactionLine.longitude);
                    transactionLineMarker = googleMap.addMarker(new MarkerOptions()
                            .position(line)
                            .title(Home.nowTransactionLine.name_employee)
                            .snippet(Utils.long2String(createDateTransactionLine) + "\n" + MyMethod.getAddress(Home.nowTransactionLine.latitude, Home.nowTransactionLine.longitude, context) + "\n" + Home.nowTransactionLine.userid)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.now_btn)));
                    LatLng header = null;
                    try {
                        if (nowTransaction.latitude != -1) {
                            //Neu co vi tri header thi ve ra
                            header = new LatLng(nowTransaction.latitude, nowTransaction.longtitude);
                        } else {
                            //khong co thi gan vi tri line vao luon ( truong hop ghi nhan checkin,checkout)
                            header = line;
                        }
                        transactionHeaderMarker = googleMap.addMarker(new MarkerOptions()
                                .position(header)
                                .title(nowTransaction.description + "\n" + Html.fromHtml("<font color='red'>" + Home.nowTransactionLine.note + "</font>"))
                                .snippet(Utils.long2String(createDateTransactionLine) + "\n" + MyMethod.getAddress(Home.nowTransactionLine.latitude, Home.nowTransactionLine.longitude, context) + "\n" + Home.nowTransactionLine.userid)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_dms_btn)));

                    } catch (Exception e) {
                        header = new LatLng(nowCustomer.latitude, nowCustomer.longitude);

                        transactionHeaderMarker = googleMap.addMarker(new MarkerOptions()
                                .position(header)
                                .title(nowCustomer.name)
                                .snippet(Utils.long2String(createDateTransactionLine) + "\n" + MyMethod.getAddress(Home.nowTransactionLine.latitude, Home.nowTransactionLine.longitude, context) + "\n" + Home.nowTransactionLine.userid)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_dms_btn)));
                    }
                    DecimalFormat df = new DecimalFormat("#");
                    Home.bindingRight.mapCheckInCustomer.txtCheckinDistance.setText(df.format(MyMethod.getDistance(line, header)) + " m");

                    googleMap.addPolyline(new PolylineOptions()
                            .add(line, header)
                            .width(5)
                            .color(Color.BLUE));
                    googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                        @Override
                        public View getInfoWindow(Marker marker) {
                            if (marker != null && marker.isInfoWindowShown()) {
                                marker.hideInfoWindow();
                                marker.showInfoWindow();
                            }
                            return null;
                        }

                        @Override
                        public View getInfoContents(final Marker marker) {
                            View v = LayoutInflater.from(context).inflate(R.layout.custom_info_window, null);
                            TextView title = (TextView) v.findViewById(R.id.title);
                            title.setText(marker.getTitle());
                            TextView snippet = (TextView) v.findViewById(R.id.snippet);
                            snippet.setText(marker.getSnippet());
                            ImageView imageView = (ImageView) v.findViewById(R.id.imageMarker);
                            if (!imageUrlImageTransactionLine.isEmpty()) {//Nếu có hình ảnh
                                Glide.with(context).load(MyMethod.getUrlImage(imageUrlImageTransactionLine)).override(200, 200).centerCrop().error(R.drawable.noavatar_btn).listener(new RequestListener<String, GlideDrawable>() {
                                    @Override
                                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                        getInfoWindow(marker);
                                        return false;
                                    }
                                })
                                        .into(imageView);
                            } else {//Không có hình ảnh
                                MyMethod.setGone(imageView);
                            }
                            return v;
                        }
                    });
                    transactionHeaderMarker.showInfoWindow();//hiện thông tin khách hàng
                    hashMarker.put(transactionLineMarker, accuracyTransactionLine);
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(line, 15.5f));
                    LayoutLoadingManager.Show_OffLoading(Home.loadingMapCustomerCheckIn);
                    googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                        @Override
                        public void onInfoWindowClick(Marker marker) {
                            if (!imageUrlImageTransactionLine.isEmpty())
                                viewImage(Const.LinkReportImage, imageUrlImageTransactionLine.replace("\\", "/"), txtTransactionHeaderNameNo.getText().toString());
                        }
                    });

                } else {
                    Home.bindingRight.mapCheckInCustomer.btnCheckinOrder.setVisibility(View.VISIBLE);
                    if (Model.inst().getConfigValue(Const.ConfigKeys.ShowUpdateCustomer, 0) == 0) {//Nếu cờ server trả về 0
                        if (nowCustomer.latitude == 0) {
                            MyMethod.setVisible(Home.btnMapCustomerUpdateLocation);
                            //Chưa có vị trí
                        } else {
                            MyMethod.setGone(Home.btnMapCustomerUpdateLocation);
                        }
                        if (nowCustomer.imageThumb == null) {
                            //Chua co hinh
                            MyMethod.setVisible(Home.btnMapCustomerUpdateImage);
                        } else {
                            MyMethod.setGone(Home.btnMapCustomerUpdateImage);
                        }
                    } else {
                        MyMethod.setVisible(Home.btnMapCustomerUpdateLocation);
                        MyMethod.setVisible(Home.btnMapCustomerUpdateImage);
                    }

                    googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                        @Override
                        public void onInfoWindowClick(Marker marker) {

                            viewImage(Const.LinkCustomerImage, nowCustomer.imageUrl.replace("\\", "/"), nowCustomer.name);

                        }
                    });

                    try {

                        customerMarker = googleMap.addMarker(new MarkerOptions()
                                .position(latLngCustomer)
                                .title(nowCustomer.no_)
                                .snippet(nowCustomer.name + "\n" + nowCustomer.phoneNumber + "\n" + nowCustomer.address)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_dms_btn)));
                    } catch (Exception e) {
                        MyMethod.showToast(context, e.getMessage());
                    }

                    googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                        @Override
                        public View getInfoWindow(Marker marker) {
                            if (marker != null && marker.isInfoWindowShown()) {
                                marker.hideInfoWindow();
                                marker.showInfoWindow();
                            }
                            return null;
                        }

                        @Override
                        public View getInfoContents(final Marker marker) {
                            View v = LayoutInflater.from(context).inflate(R.layout.custom_info_window, null);
                            TextView title = (TextView) v.findViewById(R.id.title);
                            title.setText(marker.getTitle());
                            TextView snippet = (TextView) v.findViewById(R.id.snippet);
                            snippet.setText(marker.getSnippet());
                            ImageView imageView = (ImageView) v.findViewById(R.id.imageMarker);
                            Bitmap bitmap;
                            if (nowCustomer != null) {
                                if (nowCustomer.imageThumb != null) {
                                    bitmap = nowCustomer.imageThumb;
                                    imageView.setImageBitmap(bitmap);
                                } else {
                                    Glide.with(context).load(MyMethod.getUrlCustomerImage(nowCustomer.imageUrl)).override(200, 200).centerCrop().error(R.drawable.noavatar_btn).listener(new RequestListener<String, GlideDrawable>() {
                                        @Override
                                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                            getInfoWindow(marker);
                                            return false;
                                        }
                                    })
                                            .into(imageView);
                                }
                            } else {
                                bitmap =
                                        BitmapFactory.decodeResource(context.getResources(),
                                                R.drawable.marker_dms_btn);
                                imageView.setImageBitmap(bitmap);
                            }


                            return v;
                        }
                    });
                    customerMarker.showInfoWindow();
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngCustomer, 15.5f));
                    Location lastlocation = Model.inst().getLastLocation();
                    if (lastlocation != null) {
                        Home.location = lastlocation;
                        MyMethod.loadMapCheckInOrder(googleMap, lastlocation, context);
                        double distance = MyMethod.getDistance(latLngCustomer, new LatLng(lastlocation.getLatitude(), lastlocation.getLongitude()));
                        Home.bindingRight.mapCheckInCustomer.txtCheckinTitle.setText(context.getString(R.string.distance));
                        Home.bindingRight.mapCheckInCustomer.txtCheckinDistance.setText(Math.round(distance) + "m");
                        Home.bindingRight.mapCheckInCustomer.btnCheckinOrder.setEnabled(true);
                        float distanceCheckIn = Model.inst().getConfigValue(Const.ConfigKeys.DistanceCheckInCustomer) == null ? 100 : Float.parseFloat(Model.inst().getConfigValue(Const.ConfigKeys.DistanceCheckInCustomer));
                        if (distance <= distanceCheckIn) {
                            Home.bindingRight.mapCheckInCustomer.btnCheckinOrder.setText(context.getString(R.string.go_store));
                        } else {
                            Home.bindingRight.mapCheckInCustomer.btnCheckinOrder.setText(context.getString(R.string.get_order_phone));
                        }

                    } else {
                        LayoutLoadingManager.Show_OnLoading(Home.loadingMapCustomerCheckIn, context.getString(R.string.get_location), 30);
                        LocationDetector.inst().setHighPrecision(true);
                    }
                    // Request map update location
                    LocationDetector.inst().startLocationUpdates();
                }

            } else if (MyMethod.isVisible(Home.bindingRight.customerUpdateMap.linearMapUpdate)) {
                if (MyMethod.isGetLocationCreateCustomer) {// Nếu là màn hình lấy vị trí tạo khách hàng
                    Location lastlocation = Model.inst().getLastLocation();
                    if (lastlocation != null) {
                        Home.location = lastlocation;
                        MyMethod.loadMap(googleMap, lastlocation, context, true);
                        // Home.bindingRight.map.fab.setEnabled(true);
                    } else {
                        MyMethod.showToast(context, context.getString(R.string.location_wait));
                        LocationDetector.inst().setHighPrecision(true);
                    }
                    // Request map update location
                    LocationDetector.inst().startLocationUpdates();
                } else {
                    // Nếu là màn hình cập nhật vị trí khách hàng
                }
            } else {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(17, 107), 5.5f));
                //Home.bindingRight.map.fab.setEnabled(false);
            }
            googleMap.setOnMarkerClickListener(this);
        }
    }

    void setSpinnerColorWhite(AdapterView<?> parent, int Color) {
        if (MyMethod.isVisible(Home.bindingRight.createCustomer.relaCreateCustomer))
            return;
        TextView textSpinner = null;
        try {
            textSpinner = (TextView) (((RelativeLayout) (((RelativeLayout) ((parent.getChildAt(0)))).getChildAt(0))).getChildAt(0));
        } catch (Exception e) {
            try {
                textSpinner = (TextView) parent.getChildAt(0);
            } catch (Exception ex) {
            }
        }
        try {
            if (textSpinner != null) {
                textSpinner.setTextColor(Color);
                textSpinner.setTextSize(parent.getResources().getDimensionPixelSize(R.dimen.textFontSize4) / parent.getResources().getDisplayMetrics().density);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        setSpinnerColorWhite(parent, Color.WHITE);
        switch (parent.getId()) {

            case R.id.spCustomer://Chọn tuyến KH
                Home.nowRoute = arrSpRoute.get(position).id;
                //Chon xong tim luon
                Home.bindingRight.customer.customerLoad.setSoundEffectsEnabled(false);
                Home.bindingRight.customer.customerLoad.performClick();
                break;
            case R.id.spProductGroup:
                nowProductGroup = arrSpProductGroup.get(position).id;
                arrTempProduct = new ArrayList<>();
                // Xử lí load product theo id group
                if (nowProductGroup == -1) {
                    arrTempProduct = productArrayList;
                } else {
                    for (Product product : productArrayList) {
                        if (product.id_ProductGroup == arrSpProductGroup.get(position).id)
                            arrTempProduct.add(product);
                    }
                }
                adapterProduct.setItems(arrTempProduct);
                adapterProduct.notifyDataSetChanged();
                break;
            case R.id.sptransaction_status:
                nowTransactionStatus = arrSpTransactionStatus.get(position).id;
                break;
            case R.id.sp_order_status:
                nowOrderStatus = arrSpOrderStatus.get(position).id;
                arrTempOrder = new ArrayList<>();
                //Xử lí load Transaction theo id status
                for (Order od : ordersArrayList) {
                    if (nowOrderStatus == 99) {
                        arrTempOrder.add(od);

                    } else if (od.status == nowOrderStatus) {
                        arrTempOrder.add(od);
                    }
                }
                adapterOrder.setItems(arrTempOrder);
                adapterOrder.notifyDataSetChanged();
                break;
            case R.id.spCustomerCity:
                Home.nowCity = arrCitys.get(position).id;
                EventPool.control().enQueue(new EventType.EventLoadCountysRequest(Home.nowCity));
                break;
            case R.id.spCustomerCounty:
                Home.nowCounty = arrCountys.get(position).id;
                break;
            case R.id.spSelectStaff:
                //Xử lí chọn nhân viên
                Home.lastIdTimeLine = -1;
                Home.timelinesArrayList.clear();
                Home.adapterTimeLine.notifyDataSetChanged();
                nowIdEmployeeTransaction = -1;
                if (position >= 0) {
                    EventPool.control().enQueue(new EventType.EventLoadTimeLinesRequest(Home.arrStaff.get(position).id_employee, Home.bindingRight.history.spStyleView.getSelectedItemPosition(), Home.lastIdTimeLine));
                } else { // Chọn tất cả nhân viên
                    EventPool.control().enQueue(new EventType.EventLoadTimeLinesRequest(position, Home.bindingRight.history.spStyleView.getSelectedItemPosition(), Home.lastIdTimeLine));
                }

                break;
            case R.id.spStyleView:
                Home.lastIdTimeLine = -1;
                Home.timelinesArrayList.clear();
                Home.adapterTimeLine.notifyDataSetChanged();
                int positionStaff = Home.bindingRight.history.spSelectStaff.getSelectedItemPosition() - 1;
                if (positionStaff >= 0)
                    EventPool.control().enQueue(new EventType.EventLoadTimeLinesRequest(Home.arrStaff.get(positionStaff).id_employee, position, Home.lastIdTimeLine));
                else
                    EventPool.control().enQueue(new EventType.EventLoadTimeLinesRequest(positionStaff, position, Home.lastIdTimeLine));

                break;
            case R.id.spStaff_Transaction:
                nowIdEmployeeTransaction = (position >= 0) ? Home.arrStaff.get(position).id_employee : position;
                break;
            case R.id.spStaff_Order:
                nowIdEmployeeOrder = (position >= 0) ? Home.arrStaff.get(position).id_employee : position;
                break;
            case R.id.spStaff_Report_CheckIn:
                nowIdEmployeeReportCheckIn = (position >= 0) ? Home.arrStaff.get(position).id_employee : position;
                break;
            case R.id.spStaff_InventoryEmployee:
                nowIdInventoryEmployee = (position >= 0) ? Home.arrStaff.get(position).id_employee : position;
                break;
            case R.id.spStaff_Approval:
                nowIdApproval = (position >= 0) ? Home.arrStaff.get(position).id_employee : position;
                break;
            case R.id.sp_inventory_group:
                nowInventoryGroup = arrSpProductGroup.get(position).id;
                break;
            case R.id.spStaff_Customer:
                Home.nowIdCustomer = (position >= 0) ? Home.arrStaff.get(position).id_employee : position;
                customerArrayList.clear();
                adapterCustomer.notifyDataSetChanged();
                if (position >= 0) {
                    int countCustomer = LocalDB.inst().countCustomer(Home.nowIdCustomer);
                    if (countCustomer > 0) {
                        arrSpRoute.clear();
                        arrSpRouteName.clear();
                        arrSpRoute = LocalDB.inst().loadRoute(Home.nowIdCustomer);
                        for (Route route : arrSpRoute)
                            arrSpRouteName.add(route.name + " (" + route.count + ")");
                        Home.bindingRight.customer.txtCustomerCount.setText(arrSpRoute.get(0).count + " " + context.getString(R.string.customer));
                        reFreshCustomer();//Tải lại khách hàng theo tuyến vừa lấy
                        adapterSpCustomer.notifyDataSetChanged();
                    } else if (countCustomer == 0) {
                        //load online
                        LayoutLoadingManager.Show_OnLoading(Home.loadingCustomer, context.getString(R.string.load_route), 30);
                        EventPool.control().enQueue(new EventType.EventLoadRoutesRequest(Home.nowIdCustomer));
                    } else if (countCustomer == -1) {
                        //sync data
                        MyMethod.showToast(context, "Yêu cầu đồng bộ dữ liệu");
                        LocalDB.inst().deleteSyncData();
                        LayoutLoadingManager.Show_OnLoading(Home.loadingCustomer, context.getString(R.string.load_route), 30);
                        EventPool.control().enQueue(new EventType.EventSyncDataRequest(0, -1));
                    }
                }
                break;
            default:
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
        DateFormat formatDate = new SimpleDateFormat("dd/MM/yy");
        switch (MyMethod.Date) {
            case 3:
                toOrder = new Date(year - 1900, monthOfYear, dayOfMonth);
                toDateOrder = toOrder.getTime();
                btnToDateOrder.setText(formatDate.format(toOrder));
                break;
            case 4:
                fromTransaction = new Date(year - 1900, monthOfYear, dayOfMonth);
                fromDateTransaction = fromTransaction.getTime();
                btnFromDateTransaction.setText(formatDate.format(fromTransaction));
                break;
            case 6:
                toInventoryEmployee = new Date(year - 1900, monthOfYear, dayOfMonth);
                toDatenventoryEmployee = toInventoryEmployee.getTime();
                btnToDateInventoryEmployee.setText(formatDate.format(toInventoryEmployee));
                break;
            case 7:
                fromReportCheckIn = new Date(year - 1900, monthOfYear, dayOfMonth);
                fromDateReportCheckIn = fromReportCheckIn.getTime();
                btnFromDateReportCheckIn.setText(formatDate.format(fromDateReportCheckIn));
                break;
            case 8:
                toReportCheckIn = new Date(year - 1900, monthOfYear, dayOfMonth);
                toDateReportCheckIn = toReportCheckIn.getTime();
                btnToDateReportCheckIn.setText(formatDate.format(toDateReportCheckIn));
                break;
            case 9:
                toReportWeb = new Date(year - 1900, monthOfYear, dayOfMonth);
                toDateReportWeb = toReportWeb.getTime();
                btnDateReportWeb.setText(formatDate.format(toDateReportWeb));
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        for (int i = 0; i < arrCircles.size(); i++) {
            arrCircles.get(i).remove();
        }
        double accuracy;
        try {
            accuracy = (double) hashMarker.get(marker);
        } catch (Exception e) {
            Log.e("onMarkerClick", e.getMessage(), e);
            accuracy = 0;
        }
        if (accuracy != 0) {
            CircleOptions co = new CircleOptions();
            co.center(marker.getPosition());
            co.radius(accuracy);
            co.fillColor(Color.parseColor("#4D2E9AFE")); // mờ 70%
            co.strokeColor(Color.parseColor("#2E9AFE"));
            co.strokeWidth(1.0f);
            arrCircles.add(googleMap.addCircle(co));
        }

        return false;
    }


    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                (keyCode == KeyEvent.KEYCODE_ENTER)) {
            // Perform action on key press
            switch (v.getId()) {
                case R.id.input_note:
                    Home.bindingRight.login.btnSignin.performClick();
                    break;
                default:
                    break;
            }
            return true;
        }
        return false;
    }

    //region EventOnClick
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.gridMenuManager:
                //region >>IconEventOnClick
                switch (adapterGridListManger.getObjectClick(position)) {
                    case "Ghi nhận":
                        if (PhoneState.inst().isWifi() != 1 && PhoneState.inst().is3G() != 1) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage(context.getString(R.string.please_open_network))
                                    .setCancelable(true)
                                    .setPositiveButton(context.getString(R.string.wifi), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            Home.isAppLockStop = true;
                                            startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS), OPENWIFI);
                                            dialog.dismiss();
                                        }
                                    })
                                    .setNegativeButton(context.getString(R.string.threeG), new DialogInterface.OnClickListener() {
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
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();
                        } else if (PhoneState.inst().isGPS() != 1) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage(context.getString(R.string.please_open_gps))
                                    .setCancelable(true)
                                    .setPositiveButton(context.getString(R.string.turn_on_gps), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            Home.isAppLockStop = true;
                                            startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), OPENGPS);
                                            dialog.dismiss();
                                        }
                                    })
                            ;
                            AlertDialog alert = builder.create();
                            alert.show();
                        } else {
                            imagePath = "";
                            Home.txtAddressIn.setText("");
                            Home.editCheckIn.setText("");
                            MyMethod.CheckInCustomer = false;
                            MyMethod.isFinishInTransactionDetail = false;
                            MyMethod.isNoteInStore = false;
                            MyMethod.isCheckInInTransactionDetail = false;
                            MyMethod.isReportNotOrder = false;
                            MyMethod.isReportStore = false;
                            MyMethod.isOrderInStorePressed = false;
                            showLayout(Layouts.CheckIn, context);
                        }
                        break;
                    case "Giao dịch":
                        if (PhoneState.inst().isWifi() != 1 && PhoneState.inst().is3G() != 1) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage(context.getString(R.string.please_open_network))
                                    .setCancelable(true)
                                    .setOnCancelListener(null)
                                    .setPositiveButton(context.getString(R.string.wifi), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            Home.isAppLockStop = true;
                                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
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
                            });
                            AlertDialog alert = builder.create();
                            alert.show();
                        } else {
                            EventPool.control().enQueue(new EventType.EventGetUsersRequest());
                            showLayout(Layouts.Transaction, context);
                            LayoutLoadingManager.Show_OnLoading(Home.loadingTransaction, context.getString(R.string.load_transaction), 30);
                            lastRowIdTransaction = -1;
                            fromDateTransaction = Utils.getDayEnd(Model.getServerTime());
                            //    toDateTransaction = Utils.getDayEnd(Model.getServerTime());
                            btnFromDateTransaction.setText(Utils.long2DateFull(fromDateTransaction));
                            //    btnToDateTransaction.setText(Utils.long2DateFull(toDateTransaction));
                            transactionArrayList.clear();
                            transactionLineArrayList.clear();
                            MyMethod.isLoadTransactionByID = false;
                            MyMethod.isLoadTransactionByIDInMessage = false;
                            EventPool.control().enQueue(new EventType.EventLoadTransactionsRequest(lastRowIdTransaction, fromDateTransaction, nowIdEmployeeTransaction, filterTransaction, false, Const.TransactionStatus.All.getValue()));
                        }
                        break;
                    case "Giám sát":
                        if (PhoneState.inst().isWifi() != 1 && PhoneState.inst().is3G() != 1) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage(context.getString(R.string.please_open_network))
                                    .setCancelable(true)
                                    .setPositiveButton(context.getString(R.string.wifi), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            Home.isAppLockStop = true;
                                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
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
                            });
                            AlertDialog alert = builder.create();
                            alert.show();
                        } else {
                            startActivity(new Intent(context, ManagerActivity.class));
                        }
                        break;
                    case "Cài đặt":
                        showLayout(Layouts.Setting, context);
                        break;
                    case "Nhật ký":
                        if (PhoneState.inst().isWifi() != 1 && PhoneState.inst().is3G() != 1) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage(context.getString(R.string.please_open_network))
                                    .setCancelable(true)
                                    .setPositiveButton(context.getString(R.string.wifi), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            Home.isAppLockStop = true;
                                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
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
                            });
                            AlertDialog alert = builder.create();
                            alert.show();
                        } else {
                            EventPool.control().enQueue(new EventType.EventGetUsersRequest());
                            Home.LayoutMyManager.ShowLayout(Layouts.History);
                            reFreshTimeLine();
                        }
                        break;
                    case "Đề nghị nhập kho":
                        if (PhoneState.inst().isWifi() != 1 && PhoneState.inst().is3G() != 1) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage(context.getString(R.string.please_open_network))
                                    .setCancelable(true)
                                    .setPositiveButton(context.getString(R.string.wifi), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            Home.isAppLockStop = true;
                                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
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
                            });
                            AlertDialog alert = builder.create();
                            alert.show();
                        } else {
                            Home.orderDetailArrayList.clear();
                            Home.orderListProductAdapter.notifyDataSetChanged();
                            updateInventoryInputView();
                            MyMethod.isInventoryReport = false;
                            MyMethod.isInventoryInput = true;
                            MyMethod.isOrder = false;
                            showLayout(Layouts.Order, context);
                        }
                        break;
                    case "Phân quyền truy cập":
                        if (PhoneState.inst().isWifi() != 1 && PhoneState.inst().is3G() != 1) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage(context.getString(R.string.please_open_network))
                                    .setCancelable(true)
                                    .setPositiveButton(context.getString(R.string.wifi), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            Home.isAppLockStop = true;
                                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
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
                            });
                            AlertDialog alert = builder.create();
                            alert.show();
                        } else {
                            approvalArrayList.clear();
                            Home.LayoutMyManager.ShowLayout(Layouts.Approval);
                            lastRowIdApproval = -1;
                            LayoutLoadingManager.Show_OnLoading(Home.loadingApprovalAppLock, context.getString(R.string.load_approval), 30);
                            EventPool.control().enQueue(new EventType.EventLoadRequestGrantRequest(lastRowIdApproval, nowIdApproval, Home.bindingRight.approvalAppLock.spApprovalStatus.getSelectedItemPosition(), filtersvApproval));
                        }
                        break;
                    default:
                        break;
                }
                break;
            //endregion
            case R.id.gridListMenu:
                switch (adapterGridListMenu.getObjectClick(position)) {
                    case "Khách hàng":
                        if (!MyMethod.isSyncDating) {
                            Home.arrStaff.clear();
                            Home.arrStaff = LocalDB.inst().loadUser();
                            Home.adapterStaff.setArrayList(Home.arrStaff);
                            Home.adapterStaff.notifyDataSetChanged();
                            showLayout(Layouts.Customer, context);
                            if (Home.arrStaff.size() > 0) {//Nếu đã có nhân viên
                                if (Home.bindingRight.customer.spStaffCustomer.getSelectedItemPosition() == 0) {
                                    //set nhân viên là nhân viên trên thiết bị
                                    Home.bindingRight.customer.spStaffCustomer.setSelection(1);
                                    Home.nowIdCustomer = Home.arrStaff.get(position).id_employee;
                                } else {
                                    int countCustomer = LocalDB.inst().countCustomer(Home.nowIdCustomer);
                                    if (countCustomer > 0) {
                                        //load route offline
                                        arrSpRoute.clear();
                                        arrSpRouteName.clear();
                                        arrSpRoute = LocalDB.inst().loadRoute(Home.nowIdCustomer);
                                        for (Route route : arrSpRoute)
                                            arrSpRouteName.add(route.name + " (" + route.count + ")");
                                        Home.bindingRight.customer.txtCustomerCount.setText(arrSpRoute.get(0).count + " " + context.getString(R.string.customer));
                                        reFreshCustomer();//Tải lại khách hàng theo tuyến vừa lấy
                                        adapterSpCustomer.notifyDataSetChanged();
                                    } else if (countCustomer == 0) {
                                        //load online
                                        if (PhoneState.inst().isWifi() != 1 && PhoneState.inst().is3G() != 1) {
                                            MyMethod.showToast(context, getString(R.string.not_have_customer));
                                        } else {
                                            //Nếu có mạng thì load
                                            LayoutLoadingManager.Show_OnLoading(Home.loadingCustomer, context.getString(R.string.load_route), 30);
                                            EventPool.control().enQueue(new EventType.EventLoadRoutesRequest(Home.nowIdCustomer));
                                        }
                                    } else if (countCustomer == -1) {
                                        //sync data
                                        MyMethod.showToast(context, "Yêu cầu đồng bộ dữ liệu");
                                        LocalDB.inst().deleteSyncData();
                                        LayoutLoadingManager.Show_OnLoading(Home.loadingCustomer, context.getString(R.string.load_route), 30);
                                        EventPool.control().enQueue(new EventType.EventSyncDataRequest(2, -1));
                                    }
                                }
                            } else {//Nếu chưa có thì load nhân viên
                                MyMethod.showToast(context, "Yêu cầu đồng bộ nhân viên");
                                LocalDB.inst().deleteSyncData();
                                LayoutLoadingManager.Show_OnLoading(Home.loadingCustomer, context.getString(R.string.load_route), 30);
                                EventPool.control().enQueue(new EventType.EventSyncDataRequest(0, -1));
                                MyMethod.isSyncDating = true;
//                            LayoutLoadingManager.Show_OnLoading(Home.loadingCustomer, context.getString(R.string.load_employee), 30);
//                            EventPool.control().enQueue(new EventType.EventGetUsersRequest());

                            }
                            //p}
                            //}
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage(context.getString(R.string.sync_dating_please_wait))
                                    .setCancelable(true)
                                    .setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            Home.LayoutMyManager.ShowLayout(Layouts.Setting);
                                            dialog.dismiss();
                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();
                        }
                        break;
                    case "Sản phẩm":
                        if (!MyMethod.isSyncDating) {
                            if (PhoneState.inst().isWifi() != 1 && PhoneState.inst().is3G() != 1) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setMessage(context.getString(R.string.please_open_network))
                                        .setCancelable(true)
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
                                });
                                AlertDialog alert = builder.create();
                                alert.show();
                            } else {
                                showLayout(Layouts.Product, context);
                                LayoutLoadingManager.Show_OnLoading(Home.loadingProduct, context.getString(R.string.load_product_group), 30);
                                EventPool.control().enQueue(new EventType.EventLoadProductGroupsRequest());
                                LayoutLoadingManager.Show_OnLoading(Home.loadingProduct, context.getString(R.string.load_product), 30);
                                MyMethod.isProductOfOrder = false;
                                refreshProduct();
                            }
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage(context.getString(R.string.sync_dating_please_wait))
                                    .setCancelable(true)
                                    .setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            Home.LayoutMyManager.ShowLayout(Layouts.Setting);
                                            dialog.dismiss();
                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();
                        }
                        break;
                    case "Đơn hàng":
//                        if (PhoneState.inst().isWifi() != 1 && PhoneState.inst().is3G() != 1) {
//                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                            builder.setMessage(context.getString(R.string.please_open_network))
//                                    .setCancelable(true)
//                                    .setPositiveButton(context.getString(R.string.wifi), new DialogInterface.OnClickListener() {
//                                        public void onClick(DialogInterface dialog, int id) {
//                                            Home.isAppLockStop = true;
//                                            startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS), OPENWIFI);
//                                            dialog.dismiss();
//                                        }
//                                    }).setNegativeButton(context.getString(R.string.threeG), new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    Home.isAppLockStop = true;
//                                    try {
//                                        Intent intent = new Intent(Intent.ACTION_MAIN);
//                                        intent.setComponent(new ComponentName("com.android.settings",
//                                                "com.android.settings.Settings$DataUsageSummaryActivity"));
//                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                        startActivityForResult(intent, OPEN3G);
//                                    } catch (ActivityNotFoundException e) {
//                                        startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), OPEN3G);
//                                    }
//                                    dialog.dismiss();
//                                }
//                            });
//                            AlertDialog alert = builder.create();
//                            alert.show();
//                        } else {
                        EventPool.control().enQueue(new EventType.EventGetUsersRequest());
                        Home.bindingHome.btnComeBack.setVisibility(View.GONE);
                        Home.bindingHome.txtTile.setVisibility(View.VISIBLE);
                        Home.bindingHome.txtTile.setText(context.getString(R.string.order_list));
                        MyMethod.isInventory = false;
                        MyMethod.isLoadOrder = true;
                        MyMethod.isInventoryEmployee = false;
                        MyMethod.isInventoryBill = false;
                        MyMethod.setVisible(btnToDateOrder);
                        showLayout(Layouts.OrderList, context);
                        LayoutLoadingManager.Show_OnLoading(Home.loadingOrderMain, context.getString(R.string.load_order), 30);
                        lastRowIdOrder = -1;
                        toDateOrder = Utils.getDayEnd(Model.getServerTime());
                        btnToDateOrder.setText(Utils.long2DateFull(toDateOrder));
                        ordersArrayList.clear();
                        EventPool.control().enQueue(new EventType.EventLoadOrdersRequest(lastRowIdOrder, toDateOrder, 0, filterOrderMain, nowIdEmployeeOrder));
                        //}
                        break;
                    case "Tồn kho cửa hàng":
                        if (PhoneState.inst().isWifi() != 1 && PhoneState.inst().is3G() != 1) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage(context.getString(R.string.please_open_network))
                                    .setCancelable(true)
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
                            });
                            AlertDialog alert = builder.create();
                            alert.show();
                        } else {
                            EventPool.control().enQueue(new EventType.EventGetUsersRequest());
                            Home.bindingHome.btnComeBack.setVisibility(View.GONE);
                            Home.bindingHome.txtTile.setVisibility(View.VISIBLE);
                            Home.bindingHome.txtTile.setText(context.getString(R.string.report_inventory));
                            MyMethod.isInventory = true;
                            MyMethod.isLoadOrder = false;
                            MyMethod.isInventoryEmployee = false;
                            MyMethod.isInventoryBill = false;
                            MyMethod.setGone(btnToDateOrder);
                            showLayout(Layouts.OrderList, context);
                            LayoutLoadingManager.Show_OnLoading(Home.loadingOrderMain, context.getString(R.string.load_inventory), 30);
                            lastRowIdOrder = -1;
                            ordersArrayList.clear();
                            EventPool.control().enQueue(new EventType.EventLoadOrdersRequest(lastRowIdOrder, 0, 1, filterOrderMain, nowIdEmployeeOrder));
                        }
                        break;
                    case "Tồn kho nhân viên":
                        if (PhoneState.inst().isWifi() != 1 && PhoneState.inst().is3G() != 1) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage(context.getString(R.string.please_open_network))
                                    .setCancelable(true)
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
                            });
                            AlertDialog alert = builder.create();
                            alert.show();
                        } else {
                            if (arrSpProductGroup.size() == 0) {
                                LayoutLoadingManager.Show_OnLoading(Home.loadingProduct, context.getString(R.string.load_product_group), 30);
                                EventPool.control().enQueue(new EventType.EventLoadProductGroupsRequest());
                            }
                            EventPool.control().enQueue(new EventType.EventGetUsersRequest());
                            Home.bindingHome.btnComeBack.setVisibility(View.GONE);
                            Home.bindingHome.txtTile.setVisibility(View.VISIBLE);
                            Home.bindingHome.txtTile.setText(context.getString(R.string.inventory_employee));
                            MyMethod.isInventory = false;
                            MyMethod.isLoadOrder = false;
                            MyMethod.isInventoryEmployee = true;
                            MyMethod.isInventoryBill = false;
                            MyMethod.setVisible(btnToDateInventoryEmployee);
                            showLayout(Layouts.InventoryEmployee, context);
                            LayoutLoadingManager.Show_OnLoading(Home.loadingInventoryEmployee, context.getString(R.string.load_inventory_employee), 30);
                            lastRowIdInventoryEmployee = -1;
                            toDatenventoryEmployee = Utils.getDayEnd(Model.getServerTime());
                            btnToDateInventoryEmployee.setText(Utils.long2DateFull(toDatenventoryEmployee));
                            EventPool.control().enQueue(new EventType.EventLoadInventoryEmployeesRequest(-1, toDatenventoryEmployee, nowIdInventoryEmployee, nowInventoryGroup, 0, filterInventoryEmployee));
                        }
                        break;
                    case "Phiếu nhập kho":
                        Home.bindingHome.btnComeBack.setVisibility(View.GONE);
                        Home.bindingHome.txtTile.setVisibility(View.VISIBLE);
                        Home.bindingHome.txtTile.setText(context.getString(R.string.inventory_bill));
                        MyMethod.isInventory = false;
                        MyMethod.isLoadOrder = false;
                        MyMethod.isInventoryEmployee = false;
                        MyMethod.isInventoryBill = true;
                        MyMethod.setVisible(btnToDateOrder);
                        Home.LayoutMyManager.ShowLayout(Layouts.OrderList, context, R.menu.edit_delete);
                        showLayout(Layouts.OrderList, context);
                        LayoutLoadingManager.Show_OnLoading(Home.loadingOrderMain, context.getString(R.string.load_inventory_bill), 30);
                        lastRowIdOrder = -1;
                        toDateOrder = Utils.getDayEnd(Model.getServerTime());
                        btnToDateOrder.setText(Utils.long2DateFull(toDateOrder));
                        ordersArrayList.clear();
                        EventPool.control().enQueue(new EventType.EventLoadOrdersRequest(lastRowIdOrder, toDateOrder, 2, filterOrderMain, nowIdEmployeeOrder));

                        break;
                    case "Thư viện":
                        if (PhoneState.inst().isWifi() != 1 && PhoneState.inst().is3G() != 1) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage(context.getString(R.string.please_open_network))
                                    .setCancelable(true)
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
                            });
                            AlertDialog alert = builder.create();
                            alert.show();
                        } else {
                            Intent serverIntent = new Intent(context, LibraryActivity.class);
                            startActivity(serverIntent);
                        }
                        break;
                    case "Phiếu khảo sát":
                        if (PhoneState.inst().isWifi() != 1 && PhoneState.inst().is3G() != 1) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage(context.getString(R.string.please_open_network))
                                    .setCancelable(true)
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
                            });
                            AlertDialog alert = builder.create();
                            alert.show();
                        } else {
                            startActivity(new Intent(context, SurveyActivity.class));
                        }

                        break;
                    default:
                        break;
                }
                break;
            case R.id.gridReport:
                switch (adapterGridListReport.getObjectClick(position)) {
                    case "Báo cáo":
                        if (PhoneState.inst().isWifi() != 1 && PhoneState.inst().is3G() != 1) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage(context.getString(R.string.please_open_network))
                                    .setCancelable(true)
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
                            });
                            AlertDialog alert = builder.create();
                            alert.show();
                        } else {
                            Intent webReportIntent = new Intent(context, WebReport_Overview.class);
                            startActivity(webReportIntent);
                        }
                        break;
                    case "Báo cáo ghé thăm":
                        if (PhoneState.inst().isWifi() != 1 && PhoneState.inst().is3G() != 1) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage(context.getString(R.string.please_open_network))
                                    .setCancelable(true)
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
                            });
                            AlertDialog alert = builder.create();
                            alert.show();
                        } else {
                            Home.LayoutMyManager.ShowLayout(Layouts.ReportCheckIn);
                            reportCheckInArrayList.clear();
                            adapterReportCheckIn.notifyDataSetChanged();
                            fromDateReportCheckIn = Utils.getMonthBegin(Model.getServerTime());
                            btnFromDateReportCheckIn.setText(Utils.long2DateFull(fromDateReportCheckIn));
                            toDateReportCheckIn = Utils.getDayEnd(Model.getServerTime());
                            btnToDateReportCheckIn.setText(Utils.long2DateFull(toDateReportCheckIn));
                            LayoutLoadingManager.Show_OnLoading(Home.loadingReportCheckIn, context.getString(R.string.load_report_checkin), 30);
                            EventPool.control().enQueue(new EventType.EventLoadReportCheckInsRequest(fromDateReportCheckIn, toDateReportCheckIn, nowIdEmployeeReportCheckIn, -1));

                        }

                        break;
                    case "Tuyến bán hàng":
                        if (PhoneState.inst().isWifi() != 1 && PhoneState.inst().is3G() != 1) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage(context.getString(R.string.please_open_network))
                                    .setCancelable(true)
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
                            });
                            AlertDialog alert = builder.create();
                            alert.show();
                        } else {
                            Intent reportRouteIntent = new Intent(context, ReportRoute.class);
                            startActivity(reportRouteIntent);
                        }
                        break;
                    case "Giao dịch nhân viên":
                        if (PhoneState.inst().isWifi() != 1 && PhoneState.inst().is3G() != 1) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage(context.getString(R.string.please_open_network))
                                    .setCancelable(true)
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
                            });
                            AlertDialog alert = builder.create();
                            alert.show();
                        } else {
                            Intent reportTransactionEmployee = new Intent(context, ReportTransactionEmployee.class);
                            startActivity(reportTransactionEmployee);
                        }
                        break;
                    case "Giao dịch tổng hợp":
                        if (PhoneState.inst().isWifi() != 1 && PhoneState.inst().is3G() != 1) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage(context.getString(R.string.please_open_network))
                                    .setCancelable(true)
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
                            });
                            AlertDialog alert = builder.create();
                            alert.show();
                        } else {
                            Intent reportTransactionGeneral = new Intent(context, ReportTransactionGeneral.class);
                            startActivity(reportTransactionGeneral);
                        }
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }


    }
//endregion

    public enum Layouts {
        CheckIn,//Ghi nhận
        Transaction,//Giao dịch
        Customer,//Khách hàng
        Product,//Sản phẩm
        Setting,//Cài đặt
        LogIn,//Đăng nhập
        ListGCM,//Danh sách thông báo
        Main,//Màn hình chính
        MenuGCMClick,//Chi tiết thông báo
        MapUpdate,//Bản đồ cập nhật thông tin khách hàng
        MapCustomerView,//Bản đồ xem khách hàng
        TransactionDetail,//Chi tiết giao dịch
        CustomerDetail,//Chi tiết khách hàng
        CustomerUpdate,//Cập nhật khách hàng
        ProductDetail,//Chi tiết sản phẩm
        Order,//Đặt hàng
        ProductOfOrder,// Sản phẩm trong đặt hàng
        OrderDetail,//Chi tiết đơn hàng
        OrderList,//Danh sách đơn hàng
        InputValue,//Nhập số lượng đơn giá
        GoStore,//Cửa hàng
        MapCustomerCheckIn,//Bản đồ vào cửa hàng
        CreateCustomer,//Tạo khách hàng
        History,//Nhật ký
        InputNote,//Nhập ghi chú
        SelectRoute,//Chọn tuyến bán hàng
        InventoryEmployee,//Đề nghị nhập kho nhân viên
        Approval,//Phân quyền
        ApprovalButton,//Hộp thoại chức năng phân quyền
        ReportWeb,//Báo cáo
        ReportCheckIn,//Báo cáo ghé thăm
        UpdateImage,//Cập nhật hình ảnh khách hàng
        ReasonNotOrder,//Lý do không đặt hàng
        LogInRoute
    }

    public enum PHOTOS {
        CheckIn,//Ghi nhận vào
        CheckOut,//Ghi nhận ra
        Store,//Cửa hàng
        Customer,//Khách hàng
        CustomerUpdate,//Cập nhật khách hàng
        CreateCustomer,//Tạo mới khách hàng
        NotOrder//Không đặt hàng
    }

    public enum POPUP {
        Store,//Cửa hàng
        Order,//Đặt hàng
        OrderDetail,//Chi tiết đơn hàng
        CreateCustomer,//Tạo mới khách hàng
        TransactionAccept// Ghi nhận giao dịch
    }


    private class MyTextWatcher implements TextWatcher {
        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.input_name:
                    validateName();
                    break;
                case R.id.input_password:
                    validatePassword();
                    break;
                case R.id.input_note:
                    validateNote();
                    break;
                default:
                    break;
            }
        }
    }
}
