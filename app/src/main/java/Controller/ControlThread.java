package Controller;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.Settings;
import android.util.Log;

import com.vietdms.mobile.dmslauncher.CustomAdapter.CustomAdapterMenu;
import com.vietdms.mobile.dmslauncher.Fragment.RightFragment;
import com.vietdms.mobile.dmslauncher.Home;
import com.vietdms.mobile.dmslauncher.MyMethod;
import com.vietdms.mobile.dmslauncher.R;
import com.vietdms.mobile.dmslauncher.Service.MessageService;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import CommonLib.AlarmTimer;
import CommonLib.AppManager;
import CommonLib.Const;
import CommonLib.EventPool;
import CommonLib.EventType;
import CommonLib.GCM;
import CommonLib.LocalDB;
import CommonLib.LocationDetector;
import CommonLib.Model;
import CommonLib.PhoneState;
import CommonLib.SystemLog;
import CommonLib.TrackingItem;
import CommonLib.Utils;
import CommonLib.WakeLock;

/**
 * Created by My PC on 27/11/2015.
 */
public class ControlThread extends Thread {
    private static ControlThread instance = null;
    private boolean isInitialized = false;
    private boolean isRunning = false;
    private boolean firstSend = false;
    private Context context;
    private String HomePackage = "com.vietdms.mobile.dmslauncher";

    private ControlThread() {
        super();
    }

    public synchronized static ControlThread inst() {
        if (instance == null) {
            instance = new ControlThread();
            Log.d("ControlThread", "Create new instance");
        }
        return instance;
    }

    public void restart() {
//        isInitialized = false;
//        isRunning = false;
//        firstSend = false;
//        init(context);
    }

    public void init(Context context) {
        this.context = context;

        if (!isInitialized) {
            EventPool.control().InitContext(context);
            IntentFilter filter = new IntentFilter();
            filter.addAction(Home.BROADCAST_ACTION_CONTROL);
            context.registerReceiver(receiver, filter);
            isInitialized = true;
            if (Model.inst().getDeviceId(context) == null) {
                Model.inst().setFaltalError("Không thể lấy thông tin thiết bị!");
                SystemLog.addLog(context, SystemLog.Type.Error, Model.inst().getFatalError());
                EventPool.view().enQueue(new EventType.EventError(Model.inst().getFatalError()));
                return;
            }
            if (!SystemLog.inst().init(context)) {
                SystemLog.addLog(context, SystemLog.Type.Error, "Không thể khởi tạo SystemLog");
            }
            if (!LocalDB.inst().init(context)) {
                Model.inst().setFaltalError("Lỗi dữ liệu cục bộ! Vui lòng gỡ bỏ và cài lại ứng dụng.");
                SystemLog.addLog(context, SystemLog.Type.Error, Model.inst().getFatalError());
                EventPool.view().enQueue(new EventType.EventError(Model.inst().getFatalError()));
                return;
            }
            if (!PhoneState.inst().init(context)) {
                SystemLog.addLog(context, SystemLog.Type.Error, "Không thể khởi tạo PhoneState");
            }
            if (!WakeLock.inst().init(context)) {
                SystemLog.addLog(context, SystemLog.Type.Error, "Không thể khởi tạo WakeLock");
            }
            if (!LocationDetector.inst().start(context)) {
                SystemLog.addLog(context, SystemLog.Type.Error, "Không thể khởi tạo LocationDetector");
            }
            if (!AlarmTimer.inst().start(context)) {
                SystemLog.addLog(context, SystemLog.Type.Error, "Không thể khởi tạo AlarmTimer");
            }
            if (!AppManager.inst().init(context)) {
                SystemLog.addLog(context, SystemLog.Type.Error, "Không thể khởi tạo AppManager");
            }
            super.start();
        }
    }

    public void requestStop() {
        isRunning = false;
        LocationDetector.inst().stop();
        context.unregisterReceiver(receiver);
    }

    public void onDestroy() {

        context.unregisterReceiver(receiver);
    }

    public void logout(String message) {
        LocalDB.inst().setConfigValue(Const.ConfigKeys.LoginToken, "");
        LocalDB.inst().setConfigValue(Const.ConfigKeys.CompanyName, "");
        LocalDB.inst().setConfigValue(Const.ConfigKeys.EmployeeName, "");
        Model.inst().setStatusWorking(Const.StatusWorking.Stopped);
        EventPool.view().enQueue(new EventType.EventLogoutResult(true, message));
    }

    //
    static boolean isRunningBroadCast = false;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isRunningBroadCast)
                return;
            isRunningBroadCast = true;
            //Toast.makeText(context,"On Data Fired",Toast.LENGTH_SHORT).show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    EventType.EventBase event = EventPool.control().deQueue();
                    while (event != null) {
                        try {
                            processEvent(event);
                        } catch (Exception e) {
                            Log.e("QueueTimerControl", "error: " + e.getMessage());
                        }
                        event = EventPool.control().deQueue();
                    }
                    isRunningBroadCast = false;
                }
            }).start();

        }
    };

    @Override
    public void run() {

        isRunning = true;
        initInWorkingThread();
        return;
        /*
        while (isRunning) {

                Log.v("QueueTimerControl", "timedout");
                //Kiểm tra tắt màn hình
                int typeLock = Model.inst().getConfigValue(Const.ConfigKeys.iTurnOffDisplayType, 1);
                switch (typeLock) {
                    case 0://Không khóa màn hình
                        break;
                    case 1://
                        if (AppLockService.lastFrontAppPkg != null && AppLockService.lastFrontAppPkg.contains(HomePackage)) {
                            long result1 = Model.getServerTime() - Home.timeTurnOffScreen;
                            if (TimeUnit.MILLISECONDS.toSeconds(result1) >= Model.inst().getConfigValue(Const.ConfigKeys.iTimeOffCount, 180)) {
                                Home.timeTurnOffScreen = Model.getServerTime();
                                MyMethod.lockDevice(context, Home.devicePolicyManager);
                            }
                        }
                        break;
                    case 2:
                        long result2 = Model.getServerTime() - Home.timeTurnOffScreen;
                        if (TimeUnit.MILLISECONDS.toSeconds(result2) >= Model.inst().getConfigValue(Const.ConfigKeys.iTimeOffCount, 180)) {
                            Home.timeTurnOffScreen = Model.getServerTime();
                            MyMethod.lockDevice(context, Home.devicePolicyManager);

                        }
                        break;
                    default:
                        break;
                }
                EventType.EventBase event = EventPool.control().deQueue();
                if (event == null) {
                    try {
                        sleep(Const.QueueTimerControl);
                    } catch (InterruptedException ex) {
                        Log.e("QueueTimerControl", "error: " + ex.getMessage());
                        isRunning = false;
                    }
                } else {
                    try {
                        processEvent(event);
                    } catch (Exception e) {
                        Log.e("QueueTimerControl", "error: " + e.getMessage());
                    }
                }
        }*/
    }

    private void initInWorkingThread() {
        NetworkTransaction.inst(context).getConfigs();
    }

    private void processEvent(EventType.EventBase event) {
        //check 2g
        int networkType = Utils.getNetworkType(context);
        Log.w("processEvent: ", "NetWork Type : " + networkType);
        if (networkType == 2) {//Mang 2G
            Log.w("Message", "thông báo mạng ");
            Intent messageService = new Intent(context, MessageService.class);
            messageService.putExtra("MessageBody", "CMD►2G►");
            messageService.putExtra("API", Const.UpdateVersion);
            context.startService(messageService);
        }

        switch (event.type) {
            case RejectWork: {
                EventType.EventRejectWorkRequest eventRejectWorkRequest = (EventType.EventRejectWorkRequest) event;
                NetworkTransaction.inst(context).rejectWork(eventRejectWorkRequest.id);
            }
            break;
            case AcceptWork: {
                EventType.EventAcceptWorkRequest eventAcceptWorkRequest = (EventType.EventAcceptWorkRequest) event;
                NetworkTransaction.inst(context).acceptWork(eventAcceptWorkRequest.id);
            }
            break;

            case BranchGroup: {
                NetworkTransaction.inst(context).loadBranchGroups();
            }
            break;
            case Login: {
                String fatalError = Model.inst().getFatalError();
                if (fatalError != null) {
                    EventPool.view().enQueue(new EventType.EventError(fatalError));
                } else {
                    EventType.EventLoginRequest loginRequest = (EventType.EventLoginRequest) event;
                    NetworkTransaction.inst(context).doLogin(loginRequest.userName, loginRequest.passWord, loginRequest.note);
                }
            }
            break;
            case Logout: {
                logout(null);
            }
            break;
            case ChangePass:
                EventPool.view().enQueue(new EventType.EventChangeResult(true, "OK"));
                break;
//            case LoadOrders:
//                NetworkTransaction.inst(context).getOrderDelivery();
            //break;
            case LoadGCM:
                ArrayList<GCM> test = new ArrayList<>();
                GCM gcm = new GCM();
                gcm.title = "Tiên chưa đọc";
                gcm.content = "Tiên test chưa đọc";
                gcm.date = System.currentTimeMillis();
                gcm.status = 0;
                test.add(gcm);
                gcm = new GCM();
                gcm.title = "GCM 1";
                gcm.content = "GCM Content 1";
                gcm.date = System.currentTimeMillis();
                gcm.status = 1;
                test.add(gcm);
                gcm = new GCM();
                gcm.title = "GCM 2";
                gcm.content = "GCM Content 2";
                gcm.date = System.currentTimeMillis();
                gcm.status = 0;
                test.add(gcm);
                EventPool.view().enQueue(new EventType.EventLoadGCMResult(test, "OK"));
                break;
            case LoadTransactions:
                EventType.EventLoadTransactionsRequest eventLoadTransactionsRequest = (EventType.EventLoadTransactionsRequest) event;
                NetworkTransaction.inst(context).loadTransaction(eventLoadTransactionsRequest.rowId, eventLoadTransactionsRequest.msFrom, eventLoadTransactionsRequest.id_employee, eventLoadTransactionsRequest.filter, eventLoadTransactionsRequest.loadById, eventLoadTransactionsRequest.group);
                break;
            case LoadTransactionLines:
                EventType.EventLoadTransactionLinesRequest eventLoadTransactionLinesRequest = (EventType.EventLoadTransactionLinesRequest) event;
                NetworkTransaction.inst(context).loadTransactionLine(eventLoadTransactionLinesRequest.rowId);
                break;
            case LoadTransactionLinesInStore:
                EventType.EventLoadTransactionLinesInStoreRequest eventLoadTransactionLinesInStoreRequest = (EventType.EventLoadTransactionLinesInStoreRequest) event;
                NetworkTransaction.inst(context).loadTransactionLineInStore(eventLoadTransactionLinesInStoreRequest.id_customer, eventLoadTransactionLinesInStoreRequest.lastId);
                break;
            case LoadReasonNotOrder:
                NetworkTransaction.inst(context).loadReasonNotOrders();
                break;
            case LoadFileManagerGroups:
                EventType.EventLoadFileManagerGroupsRequest eventLoadFileManagerGroupsRequest = (EventType.EventLoadFileManagerGroupsRequest) event;
                NetworkTransaction.inst(context).loadFileManagerGroups(eventLoadFileManagerGroupsRequest.id_employee);
                break;
            case LoadFileManagerDetails:
                EventType.EventLoadFileManagerDetailsRequest eventLoadFileManagerDetailsRequest = (EventType.EventLoadFileManagerDetailsRequest) event;
                NetworkTransaction.inst(context).loadFileManagerDetails(eventLoadFileManagerDetailsRequest.id);
                break;
            case SyncSurveyDetail:
                EventType.EventSyncSurveyDetailRequest eventSyncSurveyDetailRequest = (EventType.EventSyncSurveyDetailRequest) event;
                NetworkTransaction.inst(context).syncSurveyDetail(eventSyncSurveyDetailRequest.type, eventSyncSurveyDetailRequest.id, eventSyncSurveyDetailRequest.ids);
                break;
            case UpdateData:
                EventType.EventUpdateDataRequest eventUpdateData = (EventType.EventUpdateDataRequest) event;
                NetworkTransaction.inst(context).updateData(eventUpdateData.arrData, eventUpdateData.type);
                break;
            case SyncSurvey:
                EventType.EventSyncSurveyRequest eventSyncSurveyRequest = (EventType.EventSyncSurveyRequest) event;
                NetworkTransaction.inst(context).syncSurvey(eventSyncSurveyRequest.id);
                break;
            case SyncData:
                EventType.EventSyncDataRequest eventSyncDataRequest = (EventType.EventSyncDataRequest) event;
                NetworkTransaction.inst(context).syncData(eventSyncDataRequest.type, eventSyncDataRequest.id);
                break;
            case LoadSurveyCampaign:
                NetworkTransaction.inst(context).loadSurveyCampaigns();
                break;
            case LoadSurveyData:
                EventType.EventLoadSurveyDataRequest eventLoadSurveyDataRequest = (EventType.EventLoadSurveyDataRequest) event;
                NetworkTransaction.inst(context).loadSurveyDatas(eventLoadSurveyDataRequest.id_customer, eventLoadSurveyDataRequest.root_customer, eventLoadSurveyDataRequest.id_campaign);
                break;
            case LoadSurveyDataByID:
                EventType.EventLoadSurveyDataByIDRequest eventLoadSurveyDataByIDRequest = (EventType.EventLoadSurveyDataByIDRequest) event;
                NetworkTransaction.inst(context).loadSurveyDataByIDs(eventLoadSurveyDataByIDRequest.id, eventLoadSurveyDataByIDRequest.id_customer, eventLoadSurveyDataByIDRequest.root_customer);
                break;
            case SendSurveyData:
                EventType.EventSendSurveyDataRequest eventSendSurveyDataRequest = (EventType.EventSendSurveyDataRequest) event;
                NetworkTransaction.inst(context).sendSurveyDatas(eventSendSurveyDataRequest.surveyResults, eventSendSurveyDataRequest.rootCustomer);
                break;
            case LoadRoutes:
                EventType.EventLoadRoutesRequest eventLoadRoutesRequest = (EventType.EventLoadRoutesRequest) event;
                NetworkTransaction.inst(context).loadRoutes(eventLoadRoutesRequest.id_employee_viewed);
                break;
            case LoadCustomers:
                EventType.EventLoadCustomersRequest eventLoadCustomersRequest = (EventType.EventLoadCustomersRequest) event;
                NetworkTransaction.inst(context).loadCustomers(eventLoadCustomersRequest.routeId, eventLoadCustomersRequest.filter, eventLoadCustomersRequest.lastId, eventLoadCustomersRequest.id_employee_viewed);
                break;
            case LoadAllCustomers:
                EventType.EventLoadAllCustomersRequest eventLoadAllCustomersRequest = (EventType.EventLoadAllCustomersRequest) event;
                NetworkTransaction.inst(context).loadAllCustomers(eventLoadAllCustomersRequest.filter, eventLoadAllCustomersRequest.lastID);
                break;
            case UpdateCustomer:
                EventType.EventUpdateCustomerRequest eventUpdateCustomerRequest = (EventType.EventUpdateCustomerRequest) event;
                NetworkTransaction.inst(context).updateCustomer(eventUpdateCustomerRequest.customer);
                break;
            case AddCustomer:
                EventType.EventAddCustomerRequest eventAddCustomerRequest = (EventType.EventAddCustomerRequest) event;
                NetworkTransaction.inst(context).addCustomer(eventAddCustomerRequest.customer);
                break;
            case SendOrder:
                EventType.EventSendOrderRequest eventSendOrderRequest = (EventType.EventSendOrderRequest) event;
                NetworkTransaction.inst(context).sendOrder(eventSendOrderRequest.order, eventSendOrderRequest.orderDetails, eventSendOrderRequest.type, eventSendOrderRequest.ref_id);
                break;
            case UpdateOrder:
                EventType.EventUpdateOrderRequest eventUpdateOrderRequest = (EventType.EventUpdateOrderRequest) event;
                NetworkTransaction.inst(context).updateOrder(eventUpdateOrderRequest.order, eventUpdateOrderRequest.orderDetails);
                break;
            case LoadProducts:
                EventType.EventLoadProductsRequest eventLoadProductsRequest = (EventType.EventLoadProductsRequest) event;
                NetworkTransaction.inst(context).loadProducts(eventLoadProductsRequest.type, eventLoadProductsRequest.lastId, eventLoadProductsRequest.filter);
                break;
            case LoadOrders:
                EventType.EventLoadOrdersRequest eventLoadOrdersRequest = (EventType.EventLoadOrdersRequest) event;
                NetworkTransaction.inst(context).loadOrder(eventLoadOrdersRequest.rowId, eventLoadOrdersRequest.msTo, eventLoadOrdersRequest.document_type, eventLoadOrdersRequest.filter, eventLoadOrdersRequest.id_employee_viewed);
                break;
            case LoadInventoryEmployees:
                EventType.EventLoadInventoryEmployeesRequest eventLoadInventoryEmployeesRequest = (EventType.EventLoadInventoryEmployeesRequest) event;
                NetworkTransaction.inst(context).loadInventoryEmployees(eventLoadInventoryEmployeesRequest.rowId, eventLoadInventoryEmployeesRequest.msTo, eventLoadInventoryEmployeesRequest.id_employee_viewed, eventLoadInventoryEmployeesRequest.inventory_group, eventLoadInventoryEmployeesRequest.on_stock, eventLoadInventoryEmployeesRequest.filter);
                break;
            case SendRequestGrant:
                EventType.EventSendRequestGrantRequest eventSendRequestGrantRequest = (EventType.EventSendRequestGrantRequest) event;
                NetworkTransaction.inst(context).sendRequestGrant(eventSendRequestGrantRequest.packageName, eventSendRequestGrantRequest.appName, eventSendRequestGrantRequest.description);
                break;
            case LoadOrderDetails:
                EventType.EventLoadOrderDetailsRequest eventLoadOrderDetailsRequest = (EventType.EventLoadOrderDetailsRequest) event;
                NetworkTransaction.inst(context).loadOrderDetail(eventLoadOrderDetailsRequest.ref_id);
                break;
            case LoadProductGroups:
                NetworkTransaction.inst(context).loadProductGroups();
                break;
            case SendTransaction:
                EventType.EventSendTransactionRequest eventSendTransactionRequest = (EventType.EventSendTransactionRequest) event;
                NetworkTransaction.inst(context).sendTracking(true);
                NetworkTransaction.inst(context).sendTransaction(eventSendTransactionRequest.transactionLine);
                break;
            case LoadLocationVisited:
                EventType.EventLoadLocationVisitedRequest eventLoadLocationVisitedRequest = (EventType.EventLoadLocationVisitedRequest) event;
                NetworkTransaction.inst(context).loadLocationVisited(eventLoadLocationVisitedRequest.location_id, eventLoadLocationVisitedRequest.id_employee);
                break;
            case LoadCitys:
                NetworkTransaction.inst(context).loadCitys();
                break;
            case LoadCountys:
                EventType.EventLoadCountysRequest eventLoadCountysRequest = (EventType.EventLoadCountysRequest) event;
                NetworkTransaction.inst(context).loadCountys(eventLoadCountysRequest.id_city);
                break;
            case BI_DailyReport:
                EventType.EventLoadBI_DailyReportRequest eventLoadBI = (EventType.EventLoadBI_DailyReportRequest) event;
                NetworkTransaction.inst(context).loadBI_Report(eventLoadBI.lDateLoad, eventLoadBI.iID_Empoyee, eventLoadBI.iMonth);
                break;
            case LoadTimeLines:
                EventType.EventLoadTimeLinesRequest eventLoadTimeLinesRequest = (EventType.EventLoadTimeLinesRequest) event;
                NetworkTransaction.inst(context).loadTimeLines(eventLoadTimeLinesRequest.id_employee, eventLoadTimeLinesRequest.get_type, eventLoadTimeLinesRequest.last_id);

                break;
            case LoadRequestGrants:
                EventType.EventLoadRequestGrantRequest eventLoadRequestGrantRequest = (EventType.EventLoadRequestGrantRequest) event;
                NetworkTransaction.inst(context).loadRequestGrants(eventLoadRequestGrantRequest.rowId, eventLoadRequestGrantRequest.id_employee_viewed, eventLoadRequestGrantRequest.status, eventLoadRequestGrantRequest.filter);
                break;
            case ApprovalApplock:
                EventType.EventApprovalApplockRequest eventApprovalApplockRequest = (EventType.EventApprovalApplockRequest) event;
                NetworkTransaction.inst(context).approvalApplock(eventApprovalApplockRequest.id_request, eventApprovalApplockRequest.result_approval);
                break;
            case RealTimeTracking:
                EventType.EventRealTimeTrackingRequest eventRealTimeTrackingRequest = (EventType.EventRealTimeTrackingRequest) event;
                NetworkTransaction.inst(context).realTimeTracking(eventRealTimeTrackingRequest.listEmployees);
                break;
            case GetLastLocation:
                EventType.EventGetLastLocationRequest eventGetLastLocationRequest = (EventType.EventGetLastLocationRequest) event;
                NetworkTransaction.inst(context).getLastLocation(eventGetLastLocationRequest.id_employee);
                break;
            case LoadReportCheckIn:
                EventType.EventLoadReportCheckInsRequest eventLoadReportCheckInsRequest = (EventType.EventLoadReportCheckInsRequest) event;
                NetworkTransaction.inst(context).loadReportCheckIn(eventLoadReportCheckInsRequest.fromDate, eventLoadReportCheckInsRequest.toDate, eventLoadReportCheckInsRequest.id_employee_viewed, eventLoadReportCheckInsRequest.lastId);
                break;
            case AlarmTrigger: {
                Log.i("Control_processEvent", "AlarmTrigger");
                if (Model.inst().getStatusWorking() == Const.StatusWorking.Stopped) {
                    AlarmTimer.inst().clrTimer();
                    AlarmTimer.inst().clrRealtimeTimer();
                } else {
                    if (Model.inst().getStatusWorking() == Const.StatusWorking.Tracking && Model.inst().getConfigValue(Const.ConfigKeys.isActive, 1) == 1) {
                        if (Model.inst().getNextWokingTimer() == 0) {
                            MyMethod.isLocationUpdate = false;
                            NetworkTransaction.inst(context).sendSystemLog();
                            LocationDetector.inst().updateRequest(true, true);
                            NetworkTransaction.inst(context).sendTracking(true);


                        } else {
                            LocationDetector.inst().updateRequest(false, false);
                            NetworkTransaction.inst(context).sendSystemLog();
                        }
                        //Thông báo bật tắt
                        if (PhoneState.inst().isGPS() != 1 && MyMethod.isCanNotice())//GPS
                        {
                            if (Model.inst().getStatusWorking() == Const.StatusWorking.Tracking && Model.inst().getNextWokingTimer() == 0 && MyMethod.isCanNotice()) {
                                Log.w("Message", "yêu cầu bật GPS");
                                Intent messageService = new Intent(context, MessageService.class);
                                messageService.putExtra("MessageBody", "CMD►GPS");
                                messageService.putExtra("API", Const.UpdateVersion);
                                context.startService(messageService);
                            }
                        }
                        if (PhoneState.inst().isWifi() != 1) {
                            if (PhoneState.inst().is3G() != 1) {
                                if (Model.inst().getStatusWorking() == Const.StatusWorking.Tracking && Model.inst().getNextWokingTimer() == 0 && MyMethod.isCanNotice()) {
                                    Log.w("Message", "yêu cầu bật mạng");
                                    Intent messageService = new Intent(context, MessageService.class);
                                    messageService.putExtra("MessageBody", "CMD►NETWORK");
                                    messageService.putExtra("API", Const.UpdateVersion);
                                    context.startService(messageService);
                                }
                            }
                        }
                        int transactionworking;
                        try {
                            transactionworking = Model.inst().getConfigValue(Const.ConfigKeys.TransactionWorking, 0);
                        } catch (NumberFormatException e) {
                            transactionworking = 0;
                        }
                        if (transactionworking > 0) {
                            if (Model.inst().getStatusWorking() == Const.StatusWorking.Tracking && Model.inst().getNextWokingTimer() == 0 && MyMethod.isCanNotice()) {
                                Log.w("Message", "thông báo giao dịch chưa kết thúc");
                                Intent messageService = new Intent(context, MessageService.class);
                                messageService.putExtra("MessageBody", "CMD►TRANSACTION►" + transactionworking);
                                messageService.putExtra("API", Const.UpdateVersion);
                                context.startService(messageService);
                            }
                        }
                    } else {
                        NetworkTransaction.inst(context).getConfigs();
                    }
                }
                if (!firstSend) {
                    firstSend = NetworkTransaction.inst(context).sendSystemLog();
                }
                AlarmTimer.inst().continueTimer();
                WakeLock.inst().release();
            }
            break;
            case AlarmTriggerRealtime: {
                Log.i("Control_processEvent", "AlarmTriggerRealtime");
                if (Model.inst().getConfigValue(Const.ConfigKeys.isActive, 1) == 1) {
                    //Nếu không phải giám sát
                    if (Model.inst().isRealtimeValid()) {
                        LocationDetector.inst().updateRealtime(true);
                        NetworkTransaction.inst(context).sendTracking(true);
                        AlarmTimer.inst().setRealtimeTimer(Model.inst().getLastRealtimeTimer(1));
                    } else {
                        LocationDetector.inst().updateRealtime(false);
                        NetworkTransaction.inst(context).sendSystemLog();
                        AlarmTimer.inst().setRealtimeTimer(Model.inst().getLastRealtimeTimer(Model.inst().getConfigValue(Const.ConfigKeys.RealtimeTrackingIntervalIdleMultiplier, Const.DefaultRealtimeTrackingIntervalIdleMultiplier)));
                    }
                }//
                WakeLock.inst().release();
            }
            break;
            case GCMToken: {
                EventType.EventGCMToken gcmToken = (EventType.EventGCMToken) event;
                NetworkTransaction.inst(context).sendGcmToken(gcmToken.token);
            }
            break;
            case GetLocations: {
                EventType.EventGetLocationsRequest getLocationsRequest = (EventType.EventGetLocationsRequest) event;
                NetworkTransaction.inst(context).getLocations(getLocationsRequest.id_employee, getLocationsRequest.fromTime, getLocationsRequest.toTime, getLocationsRequest.max, getLocationsRequest.filtered);
            }
            break;
            case LogInRoute: {
                EventType.EventLogInRouteRequest eventLogInRouteRequest = (EventType.EventLogInRouteRequest) event;
                NetworkTransaction.inst(context).loginRoute(eventLogInRouteRequest.user, eventLogInRouteRequest.pass);
            }
            break;
            case GetUsers: {
                NetworkTransaction.inst(context).getUsers();
            }
            break;
            case SetOrderDeliveryStatus: {
                EventType.EventSetOrderDeliveryStatusRequest setOrderDeliveryStatusRequest = (EventType.EventSetOrderDeliveryStatusRequest) event;
                byte status = 0;
                switch (setOrderDeliveryStatusRequest.status) {
                    case Assigned:
                        status = 1;
                        break;
                    case Completed:
                        status = 2;
                        break;
                    case Canceled:
                        status = 3;
                        break;
                    case Other:
                        status = 4;
                        break;
                    default:
                        break;
                }
                NetworkTransaction.inst(context).setOrderDeliveryStatus(setOrderDeliveryStatusRequest.OrderNo, status, setOrderDeliveryStatusRequest.note, setOrderDeliveryStatusRequest.location_id);
            }
            break;
            case Report: {
                EventType.EventReportRequest eventReport = (EventType.EventReportRequest) event;
                TrackingItem item = new TrackingItem(eventReport.location, (byte) 1);
                int location_id = (int) LocalDB.inst().addTracking(item);
                if (NetworkTransaction.inst(context).sendLocationVisited(eventReport.visitedType, eventReport.visitedId, location_id, eventReport.note, eventReport.imagePath, eventReport.thumb)) {
                    NetworkTransaction.inst(context).sendTracking(false);
                }
            }
            break;
            case RunApp: {
                EventType.EventRunAppRequest eventRunAppRequest = (EventType.EventRunAppRequest) event;
                AppManager.inst().runApp(eventRunAppRequest.packageName);
            }
            break;
            case ListApps: {
                EventType.EventListAppRequest eventListAppRequest = (EventType.EventListAppRequest) event;
                EventPool.view().enQueue(new EventType.EventListAppResult(AppManager.inst().getListPackages(eventListAppRequest.recentApps), eventListAppRequest.recentApps));
            }
            break;
            case SendTransactionMessage: {
                EventType.EventSendTransactionMessageRequest eventSendTransactionMessageRequest = (EventType.EventSendTransactionMessageRequest) event;
                NetworkTransaction.inst(context).sendTransactionMessage(eventSendTransactionMessageRequest.type, eventSendTransactionMessageRequest.id_customer, eventSendTransactionMessageRequest.id_employee, eventSendTransactionMessageRequest.content, eventSendTransactionMessageRequest.note, eventSendTransactionMessageRequest.phone);

            }
            break;
            default:
                Log.w("Control_processEvent", "unhandled " + event.type);
                break;
        }
    }

    public void checkConnect() {
        //check 3g
        if (PhoneState.inst().isWifi() != 1 && PhoneState.inst().is3G() != 1) {
            Log.w("Message", "thông báo bat mạng ");
            Intent messageService = new Intent(context, MessageService.class);
            messageService.putExtra("MessageBody", "CMD►NETWORK►");
            messageService.putExtra("API", Const.UpdateVersion);
            context.startService(messageService);
        }
    }
}
