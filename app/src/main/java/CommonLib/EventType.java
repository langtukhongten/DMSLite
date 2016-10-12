package CommonLib;

import android.graphics.Bitmap;
import android.location.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by My PC on 26/11/2015.
 */
public abstract class EventType {
    public static enum Type {
        Error,
        Login,
        Logout,
        ChangePass,
        //        LoadOrders,
        LoadGCM,
        LoadTransactions,
        LoadTransactionLines,
        LoadRoutes,
        LoadCustomers,
        UpdateCustomer,
        AddCustomer,
        LoadProducts,
        AlarmTrigger,
        AlarmTriggerRealtime,
        Report,
        HighPrecisionLocation,
        GCMToken,
        GCMMessage,
        GetLocations,
        GetUsers,
        SetOrderDeliveryStatus,
        ListApps,
        RunApp,
        UpdateMenus,
        SendOrder,
        LoadOrders,
        LoadOrderDetails,
        UpdateOrder,
        SendTransaction,
        LoadProductGroups,
        LoadLocationVisited,
        LoadCitys,
        LoadCountys,
        BI_DailyReport,
        LoadTimeLines,
        LoadInventoryEmployees,
        SendRequestGrant,
        LoadRequestGrants,
        ApprovalApplock,
        RealTimeTracking,
        GetLastLocation,
        LoadReportCheckIn,
        LocationUpdate,
        LoadTransactionLinesInStore,
        LoadReasonNotOrder,
        LoadFileManagerGroups,
        LoadFileManagerDetails,
        SyncData,
        LoadSurveyCampaign,
        LoadSurveyData,
        SendSurveyData,
        LoadSurveyDataByID,
        LogInRoute,
        SyncSurvey,
        SyncSurveyDetail,
        UpdateData
    }

    public static class EventBase {
        public final Type type;

        public EventBase(Type type) {
            this.type = type;
        }

    }

    //SEND FROM VIEW
    public static class EventGCMToken extends EventBase {
        public final String token;

        public EventGCMToken(String token) {
            super(Type.GCMToken);
            this.token = token;
        }
    }

    public static class EventError extends EventBase {
        public final String message;

        public EventError(String message) {
            super(Type.Error);
            this.message = message;
        }
    }

    public static class EventReportRequest extends EventBase {
        public final Const.LocationVisitedType visitedType;
        public final String visitedId;
        public final String imagePath;
        public final Location location;
        public final String note;
        public final Bitmap thumb;

        public EventReportRequest(Const.LocationVisitedType visitedType, String visitedId, String imagePath, Location location, String note, Bitmap thumb) {
            super(Type.Report);
            this.visitedType = visitedType;
            this.visitedId = visitedId;
            this.imagePath = imagePath;
            this.location = location;
            this.note = note;
            this.thumb = thumb;
        }
    }

    public static class EventLoginRequest extends EventBase {
        public final String userName, passWord, note;

        public EventLoginRequest(String username, String password, String note) {
            super(Type.Login);
            this.userName = username;
            this.passWord = password;
            this.note = note;
        }
    }

    public static class EventLogoutRequest extends EventBase {
        public EventLogoutRequest() {
            super(Type.Logout);
        }
    }


    public static class EventLoadGCMRequest extends EventBase {
        public EventLoadGCMRequest() {
            super(Type.LoadGCM);
        }
    }

    public static class EventLoadHighPrecisionLocationRequest extends EventBase {
        public EventLoadHighPrecisionLocationRequest() {
            super(Type.HighPrecisionLocation);
        }
    }

    public static class EventLoadTransactionsRequest extends EventBase {
        public EventLoadTransactionsRequest(int rowId, long msFrom, int id_employee, String filter, boolean loadById, int group) {
            super(Type.LoadTransactions);
            this.rowId = rowId;
            this.msFrom = msFrom;
            this.id_employee = id_employee;
            this.filter = filter;
            this.loadById = loadById;
            this.group = group;

        }

        public final int rowId;
        public final long msFrom;
        public final int id_employee;
        public final String filter;
        public final boolean loadById;
        public final int group;
    }

    public static class EventLoadTransactionLinesRequest extends EventBase {
        public EventLoadTransactionLinesRequest(int rowId) {
            super(Type.LoadTransactionLines);
            this.rowId = rowId;
        }

        public final int rowId;

    }

    public static class EventLoadTransactionLinesInStoreRequest extends EventBase {
        public EventLoadTransactionLinesInStoreRequest(int id_customer, int lastId) {
            super(Type.LoadTransactionLinesInStore);
            this.id_customer = id_customer;
            this.lastId = lastId;
        }

        public final int id_customer;
        public final int lastId;

    }

    public static class EventLoadReasonNotOrdersRequest extends EventBase {
        public EventLoadReasonNotOrdersRequest() {
            super(Type.LoadReasonNotOrder);
        }
    }

    public static class EventLoadFileManagerGroupsRequest extends EventBase {
        public EventLoadFileManagerGroupsRequest(int id_employee) {
            super(Type.LoadFileManagerGroups);
            this.id_employee = id_employee;
        }

        public final int id_employee;
    }

    public static class EventLoadFileManagerDetailsRequest extends EventBase {
        public EventLoadFileManagerDetailsRequest(int id) {
            super(Type.LoadFileManagerDetails);
            this.id = id;
        }

        public final int id;
    }

    public static class EventSyncSurveyRequest extends EventBase {
        public EventSyncSurveyRequest(int id) {
            super(Type.SyncSurvey);
            this.id = id;
        }

        public final int id;
    }

    public static class EventUpdateDataRequest extends EventBase {
        public EventUpdateDataRequest(ArrayList<Object> arrData, int type) {
            super(Type.UpdateData);
            this.arrData = arrData;
            this.type = type;
        }

        public final ArrayList<Object> arrData;
        public final int type;
    }

    public static class EventSyncSurveyDetailRequest extends EventBase {
        public EventSyncSurveyDetailRequest(int type, int id, List<Integer> ids) {
            super(Type.SyncSurveyDetail);
            this.type = type;
            this.id = id;
            this.ids = ids;
        }

        public final int type;
        public final int id;
        public final List<Integer> ids;
    }

    public static class EventSyncDataRequest extends EventBase {
        public EventSyncDataRequest(int type, int id) {
            super(Type.SyncData);
            this.type = type;
            this.id = id;
        }

        public final int type;
        public final int id;
    }

    public static class EventLoadSurveyCampaignRequest extends EventBase {
        public EventLoadSurveyCampaignRequest() {
            super(Type.LoadSurveyCampaign);
        }
    }

    public static class EventLoadSurveyDataRequest extends EventBase {
        public EventLoadSurveyDataRequest(int id_customer, int root_customer, int id_campaign) {
            super(Type.LoadSurveyData);
            this.id_customer = id_customer;
            this.id_campaign = id_campaign;
            this.root_customer = root_customer;
        }

        public final int id_campaign;
        public final int id_customer;
        public final int root_customer;
    }

    public static class EventLoadSurveyDataByIDRequest extends EventBase {
        public EventLoadSurveyDataByIDRequest(int id, int id_customer, int root_customer) {
            super(Type.LoadSurveyDataByID);
            this.id = id;
            this.id_customer = id_customer;
            this.root_customer = root_customer;
        }

        public final int id;
        public final int id_customer;
        public final int root_customer;
    }

    public static class EventLogInRouteRequest extends EventBase {
        public EventLogInRouteRequest(String user, String pass) {
            super(Type.LogInRoute);
            this.user = user;
            this.pass = pass;
        }

        public final String user;
        public final String pass;
    }

    public static class EventSendSurveyDataRequest extends EventBase {
        public EventSendSurveyDataRequest(ArrayList<SurveyResult> surveyResults, int rootCustomer) {
            super(Type.SendSurveyData);
            this.surveyResults = surveyResults;
            this.rootCustomer = rootCustomer;
        }

        public final ArrayList<SurveyResult> surveyResults;
        public final int rootCustomer;
    }

    public static class EventLoadRoutesRequest extends EventBase {
        public EventLoadRoutesRequest(int id_employee_viewed) {
            super(Type.LoadRoutes);
            this.id_employee_viewed = id_employee_viewed;
        }

        public final int id_employee_viewed;
    }

    public static class EventLoadCustomersRequest extends EventBase {
        public EventLoadCustomersRequest(int routeId, String filter, int lastId, int id_employee_viewed) {
            super(Type.LoadCustomers);
            this.routeId = routeId;
            this.filter = filter;
            this.lastId = lastId;
            this.id_employee_viewed = id_employee_viewed;
        }

        public final int routeId;
        public final String filter;
        public final int lastId;
        public final int id_employee_viewed;
    }

    public static class EventUpdateCustomerRequest extends EventBase {
        public EventUpdateCustomerRequest(Customer customer) {
            super(Type.UpdateCustomer);
            this.customer = customer;
        }

        public final Customer customer;
    }

    public static class EventAddCustomerRequest extends EventBase {
        public EventAddCustomerRequest(Customer customer) {
            super(Type.AddCustomer);
            this.customer = customer;
        }

        public final Customer customer;
    }

    public static class EventLoadProductsRequest extends EventBase {
        public EventLoadProductsRequest(int type, int lastId, String filter) {
            super(Type.LoadProducts);
            this.type = type;
            this.lastId = lastId;
            this.filter = filter;
        }

        public final int type;
        public final int lastId;
        public final String filter;
    }

    public static class EventLoadOrdersRequest extends EventBase {
        public EventLoadOrdersRequest(int rowId, long msTo, int document_type, String filter, int id_employee_viewed) {
            super(Type.LoadOrders);
            this.rowId = rowId;
            this.msTo = msTo;
            this.document_type = document_type;
            this.filter = filter;
            this.id_employee_viewed = id_employee_viewed;
        }

        public final int rowId;
        public final long msTo;
        public final int document_type;
        public final String filter;
        public final int id_employee_viewed;
    }

    public static class EventLoadInventoryEmployeesRequest extends EventBase {
        public EventLoadInventoryEmployeesRequest(int rowId, long msTo, int id_employee_viewed, int inventory_group, int on_stock, String filter) {
            super(Type.LoadInventoryEmployees);
            this.rowId = rowId;
            this.msTo = msTo;
            this.id_employee_viewed = id_employee_viewed;
            this.inventory_group = inventory_group;
            this.on_stock = on_stock;
            this.filter = filter;
        }

        public final int rowId;
        public final long msTo;
        public final int id_employee_viewed;
        public final int inventory_group;
        public final int on_stock;
        public final String filter;
    }


    public static class EventLoadOrderDetailsRequest extends EventBase {
        public EventLoadOrderDetailsRequest(int rowId) {
            super(Type.LoadOrderDetails);
            this.rowId = rowId;
        }

        public final int rowId;

    }

    public static class EventUpdateOrderRequest extends EventBase {
        public EventUpdateOrderRequest(Order order, ArrayList<OrderDetail> orderDetails) {
            super(Type.UpdateOrder);
            this.order = order;
            this.orderDetails = orderDetails;
        }

        public final Order order;
        public final ArrayList<OrderDetail> orderDetails;
    }

    public static class EventGetLocationsRequest extends EventBase {
        public EventGetLocationsRequest(int id_employee, long fromTime, long toTime, int max, boolean filtered) {
            super(Type.GetLocations);
            this.id_employee = id_employee;
            this.fromTime = fromTime;
            this.toTime = toTime;
            this.max = max;
            this.filtered = filtered;
        }

        public final int id_employee;
        public final long fromTime;
        public final long toTime;
        public final int max;
        public final boolean filtered;
    }

    public static class EventGetUsersRequest extends EventBase {
        public EventGetUsersRequest() {
            super(Type.GetUsers);
        }
    }

    public static class EventSetOrderDeliveryStatusRequest extends EventBase {
        public EventSetOrderDeliveryStatusRequest(String OrderNo, Const.OrderDeliveryStatus status, String note, int location_id) {
            super(Type.SetOrderDeliveryStatus);
            this.OrderNo = OrderNo;
            this.status = status;
            this.note = note;
            this.location_id = location_id;
        }

        public final String OrderNo;
        public final Const.OrderDeliveryStatus status;
        public final String note;
        public final int location_id;
    }

    public static class EventRunAppRequest extends EventBase {
        public EventRunAppRequest(String packageName) {
            super(Type.RunApp);
            this.packageName = packageName;
        }

        public final String packageName;
    }

    public static class EventListAppRequest extends EventBase {
        public EventListAppRequest(boolean recentApps) {
            super(Type.ListApps);
            this.recentApps = recentApps;
        }

        public final boolean recentApps;
    }

    public static class EventSendOrderRequest extends EventBase {
        public EventSendOrderRequest(Order order, ArrayList<OrderDetail> orderDetails) {
            super(Type.SendOrder);
            this.order = order;
            this.orderDetails = orderDetails;
        }

        public final Order order;
        public final ArrayList<OrderDetail> orderDetails;
    }

    public static class EventSendTransactionRequest extends EventBase {
        public EventSendTransactionRequest(TransactionLine transactionLine) {
            super(Type.SendTransaction);
            if(transactionLine.create_date==0){
                transactionLine.create_date = Model.getServerTime();
                transactionLine.modified_date = Model.getServerTime();
            }
            this.transactionLine = transactionLine;
        }

        public final TransactionLine transactionLine;
    }

    public static class EventLoadProductGroupsRequest extends EventBase {
        public EventLoadProductGroupsRequest() {
            super(Type.LoadProductGroups);
        }
    }

    public static class EventLoadLocationVisitedRequest extends EventBase {
        public EventLoadLocationVisitedRequest(int location_id, int id_employee) {
            super(Type.LoadLocationVisited);
            this.location_id = location_id;
            this.id_employee = id_employee;
        }

        public final int location_id;
        public final int id_employee;

    }

    public static class EventLoadCitysRequest extends EventBase {
        public EventLoadCitysRequest() {
            super(Type.LoadCitys);
        }

    }

    public static class EventLoadCountysRequest extends EventBase {
        public EventLoadCountysRequest(int id_city) {
            super(Type.LoadCountys);
            this.id_city = id_city;
        }

        public final int id_city;

    }

    public static class EventLoadBI_DailyReportRequest extends EventBase {
        public EventLoadBI_DailyReportRequest(long lDateLoad, int ID_Employee, int iMonth) {
            super(Type.BI_DailyReport);
            this.lDateLoad = lDateLoad;
            this.iID_Empoyee = ID_Employee;
            this.iMonth = iMonth;
        }

        public final int iMonth;
        public final int iID_Empoyee;
        public final long lDateLoad;

    }

    public static class EventLoadTimeLinesRequest extends EventBase {
        public EventLoadTimeLinesRequest(int id_employee, int get_type, int last_id) {
            super(Type.LoadTimeLines);
            this.id_employee = id_employee;
            this.get_type = get_type;
            this.last_id = last_id;
        }

        public final int id_employee;
        public final int get_type;
        public final int last_id;
    }

    public static class EventSendRequestGrantRequest extends EventBase {
        public EventSendRequestGrantRequest(String packageName, String appName, String description) {
            super(Type.SendRequestGrant);
            this.packageName = packageName;
            this.appName = appName;
            this.description = description;
        }

        public final String packageName;
        public final String appName;
        public final String description;
    }

    public static class EventLoadRequestGrantRequest extends EventBase {
        public EventLoadRequestGrantRequest(int rowId, int id_employee_viewed, int status, String filter) {
            super(Type.LoadRequestGrants);
            this.rowId = rowId;
            this.id_employee_viewed = id_employee_viewed;
            this.status = status;
            this.filter = filter;
        }

        public final int rowId;
        public final int id_employee_viewed;
        public final int status;
        public final String filter;
    }

    public static class EventApprovalApplockRequest extends EventBase {
        public EventApprovalApplockRequest(int id_request, int result_approval) {
            super(Type.ApprovalApplock);
            this.id_request = id_request;
            this.result_approval = result_approval;
        }

        public final int id_request;
        public final int result_approval;
    }

    public static class EventRealTimeTrackingRequest extends EventBase {
        public EventRealTimeTrackingRequest(List<Integer> listEmployees) {
            super(Type.RealTimeTracking);
            this.listEmployees = listEmployees;
        }

        public final List<Integer> listEmployees;
    }

    public static class EventGetLastLocationRequest extends EventBase {
        public EventGetLastLocationRequest(int id_employee) {
            super(Type.GetLastLocation);
            this.id_employee = id_employee;
        }

        public final int id_employee;
    }

    public static class EventLoadReportCheckInsRequest extends EventBase {
        public EventLoadReportCheckInsRequest(long fromDate, long toDate, int id_employee_viewed, int lastId) {
            super(Type.LoadReportCheckIn);
            this.fromDate = fromDate;
            this.toDate = toDate;
            this.id_employee_viewed = id_employee_viewed;
            this.lastId = lastId;
        }

        public final long fromDate, toDate;
        public final int id_employee_viewed, lastId;
    }

//RETURN FROM CONTROL

    public static class EventGCMMessageToView extends EventBase {
        public final String message;
        public final long sendDate;

        public EventGCMMessageToView(String message, long sendDate) {
            super(Type.GCMMessage);
            this.message = message;
            this.sendDate = sendDate;
        }
    }

    public static class EventLogInRouteResult extends EventBase {
        public final boolean success;
        public final String message;
        public final String routeName;

        public EventLogInRouteResult(boolean success, String message, String routeName) {
            super(Type.LogInRoute);
            this.success = success;
            this.message = message;
            this.routeName = routeName;
        }
    }

    public static class EventLoginResult extends EventBase {
        public final int result;// 0=fail, 1=pending, 2=success
        public final String message;

        public EventLoginResult(int result, String message) {
            super(Type.Login);
            this.result = result;
            this.message = message;
        }
    }

    public static class EventLogoutResult extends EventBase {
        public final boolean success;
        public final String message;

        public EventLogoutResult(boolean success, String message) {
            super(Type.Logout);
            this.success = success;
            this.message = message;
        }
    }

    public static class EventChangeResult extends EventBase {
        public final boolean success;
        public final String message;

        public EventChangeResult(boolean success, String message) {
            super(Type.ChangePass);
            this.success = success;
            this.message = message;
        }
    }

//    public static class EventLoadOrdersResult extends EventBase {
//        public final String message;
//        public final OrderDeliveryItem[] arrOrder;
//
//        public EventLoadOrdersResult(OrderDeliveryItem[] arrOrder, String message) {
//            super(Type.LoadOrders);
//            this.message = message;
//            this.arrOrder = arrOrder;
//        }
//    }

    public static class EventLoadGCMResult extends EventBase {
        public final String message;
        public final ArrayList<GCM> arrGCM;

        public EventLoadGCMResult(ArrayList<GCM> arrGCM, String message) {
            super(Type.LoadGCM);
            this.message = message;
            this.arrGCM = arrGCM;
        }
    }

    public static class EventLoadTransactionsResult extends EventBase {
        public final boolean success;
        public final String message;
        public final ArrayList<Transaction> arrTransactions;

        public EventLoadTransactionsResult(boolean success, String message, ArrayList<Transaction> arrTransactions) {
            super(Type.LoadTransactions);
            this.success = success;
            this.message = message;
            this.arrTransactions = arrTransactions;
        }
    }

    public static class EventLoadTransactionLinesResult extends EventBase {
        public final boolean success;
        public final String message;
        public final ArrayList<TransactionLine> arrTransactionLine;

        public EventLoadTransactionLinesResult(boolean success, String message, ArrayList<TransactionLine> arrTransactionLine) {
            super(Type.LoadTransactionLines);
            this.success = success;
            this.message = message;
            this.arrTransactionLine = arrTransactionLine;
        }
    }

    public static class EventLoadTransactionLinesInStoreResult extends EventBase {
        public final boolean success;
        public final String message;
        public final ArrayList<TransactionLine> arrTransactionLine;

        public EventLoadTransactionLinesInStoreResult(boolean success, String message, ArrayList<TransactionLine> arrTransactionLine) {
            super(Type.LoadTransactionLinesInStore);
            this.success = success;
            this.message = message;
            this.arrTransactionLine = arrTransactionLine;
        }
    }

    public static class EventLoadReasonNotOrdersResult extends EventBase {
        public final boolean success;
        public final String message;
        public final ArrayList<ReasonNotOrder> arrReasonNotOrder;

        public EventLoadReasonNotOrdersResult(boolean success, String message, ArrayList<ReasonNotOrder> arrReasonNotOrder) {
            super(Type.LoadReasonNotOrder);
            this.success = success;
            this.message = message;
            this.arrReasonNotOrder = arrReasonNotOrder;
        }
    }

    public static class EventLoadFileManagerGroupsResult extends EventBase {
        public final boolean success;
        public final String message;
        public final ArrayList<LibraryGroup> arrLibraryGroups;

        public EventLoadFileManagerGroupsResult(boolean success, String message, ArrayList<LibraryGroup> arrLibraryGroups) {
            super(Type.LoadFileManagerGroups);
            this.success = success;
            this.message = message;
            this.arrLibraryGroups = arrLibraryGroups;
        }
    }

    public static class EventLoadFileManagerDetailsResult extends EventBase {
        public final boolean success;
        public final String message;
        public final ArrayList<LibraryDetail> arrLibraryDetails;

        public EventLoadFileManagerDetailsResult(boolean success, String message, ArrayList<LibraryDetail> arrLibraryDetails) {
            super(Type.LoadFileManagerDetails);
            this.success = success;
            this.message = message;
            this.arrLibraryDetails = arrLibraryDetails;
        }
    }

    public static class EventSyncSurveyResult extends EventBase {
        public final boolean success;
        public final String message;
        public final int lastId;
        public final int countData;
        public final ArrayList<Object> arrData;

        public EventSyncSurveyResult(boolean success, String message, int lastId, int countData, ArrayList<Object> arrData) {
            super(Type.SyncSurvey);
            this.success = success;
            this.message = message;
            this.lastId = lastId;
            this.countData = countData;
            this.arrData = arrData;
        }
    }

    public static class EventUpdateDataResult extends EventBase {
        public final boolean success;
        public final String message;
        public final int type;

        public EventUpdateDataResult(boolean success, String message,int type) {
            super(Type.UpdateData);
            this.success = success;
            this.message = message;
            this.type = type;
        }
    }

    public static class EventSyncSurveyDetailResult extends EventBase {
        public final boolean success;
        public final String message;
        public final int type;
        public final int lastId;
        public final int countData;
        public final ArrayList<Object> arrData;

        public EventSyncSurveyDetailResult(boolean success, String message, int type, int lastId, int countData, ArrayList<Object> arrData) {
            super(Type.SyncSurveyDetail);
            this.success = success;
            this.message = message;
            this.type = type;
            this.lastId = lastId;
            this.countData = countData;
            this.arrData = arrData;
        }
    }

    public static class EventSyncDataResult extends EventBase {
        public final boolean success;
        public final String message;
        public final int type;
        public final int lastId;
        public final int countData;
        public final ArrayList<Object> arrData;

        public EventSyncDataResult(boolean success, String message, int type, int lastId, int countData, ArrayList<Object> arrData) {
            super(Type.SyncData);
            this.success = success;
            this.message = message;
            this.type = type;
            this.lastId = lastId;
            this.countData = countData;
            this.arrData = arrData;
        }
    }

    public static class EventLoadSurveyCampaignResult extends EventBase {
        public final boolean success;
        public final String message;
        public final ArrayList<SurveyCampaign> arrSurveyCampaigns;

        public EventLoadSurveyCampaignResult(boolean success, String message, ArrayList<SurveyCampaign> arrSurveyCampaigns) {
            super(Type.LoadSurveyCampaign);
            this.success = success;
            this.message = message;
            this.arrSurveyCampaigns = arrSurveyCampaigns;
        }
    }

    public static class EventLoadSurveyDataResult extends EventBase {
        public final boolean success;
        public final String message;
        public final ArrayList<SurveyHeader> arrSurveyHeaders;
        public final ArrayList<SurveyLine> arrSurveyLines;

        public EventLoadSurveyDataResult(boolean success, String message, ArrayList<SurveyHeader> arrSurveyHeaders, ArrayList<SurveyLine> arrSurveyLines) {
            super(Type.LoadSurveyData);
            this.success = success;
            this.message = message;
            this.arrSurveyHeaders = arrSurveyHeaders;
            this.arrSurveyLines = arrSurveyLines;
        }
    }

    public static class EventLoadSurveyDataByIDResult extends EventBase {
        public final boolean success;
        public final String message;
        public final ArrayList<SurveyHeader> arrSurveyHeaders;
        public final ArrayList<SurveyLine> arrSurveyLines;
        public final SurveyCampaign surveyCampaign;


        public EventLoadSurveyDataByIDResult(boolean success, String message, ArrayList<SurveyHeader> arrSurveyHeaders, ArrayList<SurveyLine> arrSurveyLines, SurveyCampaign surveyCampaign) {
            super(Type.LoadSurveyDataByID);
            this.success = success;
            this.message = message;
            this.arrSurveyHeaders = arrSurveyHeaders;
            this.arrSurveyLines = arrSurveyLines;
            this.surveyCampaign = surveyCampaign;

        }
    }


    public static class EventLoadRoutesResult extends EventBase {
        public final boolean success;
        public final String message;
        public final ArrayList<Route> arrRoute;

        public EventLoadRoutesResult(boolean success, String message, ArrayList<Route> arrRoute) {
            super(Type.LoadRoutes);
            this.success = success;
            this.message = message;
            this.arrRoute = arrRoute;
        }

    }

    public static class EventLoadCustomersResult extends EventBase {
        public final boolean success;
        public final String message;
        public final ArrayList<Customer> arrCustomer;

        public EventLoadCustomersResult(boolean success, String message, ArrayList<Customer> arrCustomer) {
            super(Type.LoadCustomers);
            this.success = success;
            this.message = message;
            this.arrCustomer = arrCustomer;
        }
    }

    public static class EventLoadProductsResult extends EventBase {
        public final boolean success;
        public final String message;
        public final ArrayList<Product> arrProduct;

        public EventLoadProductsResult(boolean success, String message, ArrayList<Product> arrProduct) {
            super(Type.LoadProducts);
            this.success = success;
            this.message = message;
            this.arrProduct = arrProduct;
        }
    }

    public static class EventLoadOrdersResult extends EventBase {
        public final boolean success;
        public final String message;
        public final ArrayList<Order> arrOrders;

        public EventLoadOrdersResult(boolean success, String message, ArrayList<Order> arrOrders) {
            super(Type.LoadOrders);
            this.success = success;
            this.message = message;
            this.arrOrders = arrOrders;
        }
    }

    public static class EventLoadInventoryEmployeesResult extends EventBase {
        public final boolean success;
        public final String message;
        public final ArrayList<Product> arrInventoryEmployees;

        public EventLoadInventoryEmployeesResult(boolean success, String message, ArrayList<Product> arrInventoryEmployees) {
            super(Type.LoadInventoryEmployees);
            this.success = success;
            this.message = message;
            this.arrInventoryEmployees = arrInventoryEmployees;
        }
    }


    public static class EventLoadOrderDetailsResult extends EventBase {
        public final boolean success;
        public final String message;
        public final ArrayList<OrderDetail> arrOrderDetails;

        public EventLoadOrderDetailsResult(boolean success, String message, ArrayList<OrderDetail> arrOrderDetails) {
            super(Type.LoadOrderDetails);
            this.success = success;
            this.message = message;
            this.arrOrderDetails = arrOrderDetails;
        }
    }

    public static class EventLoadHighPrecisionLocationResult extends EventBase {
        public final Location location;
        public final String message;

        public EventLoadHighPrecisionLocationResult(Location location, String message) {
            super(Type.HighPrecisionLocation);
            this.location = location;
            this.message = message;
        }
    }

    public static class EventLocationUpdateResult extends EventBase {
        public final Location location;
        public final String message;

        public EventLocationUpdateResult(Location location, String message) {
            super(Type.LocationUpdate);
            this.location = location;
            this.message = message;
        }
    }


    public static class EventGetLocationsResult extends EventBase {
        public EventGetLocationsResult(MyLocation[] arrayLocations, String message) {
            super(Type.GetLocations);
            this.arrayLocations = arrayLocations;
            this.message = message;
        }

        public final MyLocation[] arrayLocations;
        public final String message;
    }

    public static class EventGetUsersResult extends EventBase {
        public EventGetUsersResult(UserInfo[] arrayUsers, String message) {
            super(Type.GetUsers);
            this.arrayUsers = arrayUsers;
            this.message = message;
        }

        public final UserInfo[] arrayUsers;
        public final String message;
    }

    public static class EventSetOrderDeliveryStatusResult extends EventBase {
        public final boolean success;
        public final String message;

        public EventSetOrderDeliveryStatusResult(boolean success, String message) {
            super(Type.SetOrderDeliveryStatus);
            this.success = success;
            this.message = message;
        }
    }

    public static class EventReportResult extends EventBase {
        public final boolean success;
        public final String message;

        public EventReportResult(boolean success, String message) {
            super(Type.Report);
            this.success = success;
            this.message = message;
        }
    }

    public static class EventListAppResult extends EventBase {
        public final ArrayList<String> arrApps;
        public final boolean recentApps;

        public EventListAppResult(ArrayList<String> arrApps, boolean recentApps) {
            super(Type.ListApps);
            this.arrApps = arrApps;
            this.recentApps = recentApps;
        }
    }

    public static class EventUpdateCustomerResult extends EventBase {
        public final boolean success;
        public final String message;
        public final Customer customer;

        public EventUpdateCustomerResult(boolean success, String message, Customer customer) {
            super(Type.UpdateCustomer);
            this.success = success;
            this.message = message;
            this.customer = customer;
        }
    }


    public static class EventAddCustomerResult extends EventBase {
        public final boolean success;
        public final String message;
        public final int rowID;

        public EventAddCustomerResult(boolean success, String message, int rowID) {
            super(Type.AddCustomer);
            this.success = success;
            this.message = message;
            this.rowID = rowID;
        }
    }

    public static class EventSendOrderResult extends EventBase {
        public final boolean success;
        public final String message;

        public EventSendOrderResult(boolean success, String message) {
            super(Type.SendOrder);
            this.success = success;
            this.message = message;
        }
    }

    public static class EventUpdateOrderResult extends EventBase {
        public final boolean success;
        public final String message;

        public EventUpdateOrderResult(boolean success, String message) {
            super(Type.UpdateOrder);
            this.success = success;
            this.message = message;
        }
    }

    public static class EventSendTransactionResult extends EventBase {
        public final boolean success;
        public final String message;

        public EventSendTransactionResult(boolean success, String message) {
            super(Type.SendTransaction);
            this.success = success;
            this.message = message;
        }
    }

    public static class EventSendSurveyDataResult extends EventBase {
        public final boolean success;
        public final String message;

        public EventSendSurveyDataResult(boolean success, String message) {
            super(Type.SendSurveyData);
            this.success = success;
            this.message = message;
        }
    }


    public static class EventLoadProductGroupsResult extends EventBase {
        public final boolean success;
        public final String message;
        public final ArrayList<ProductGroup> arrProductGroup;

        public EventLoadProductGroupsResult(boolean success, String message, ArrayList<ProductGroup> arrProductGroup) {
            super(Type.LoadProductGroups);
            this.success = success;
            this.message = message;
            this.arrProductGroup = arrProductGroup;
        }
    }

    public static class EventLoadLocationVisitedResult extends EventBase {
        public final boolean success;
        public final String message;
        public final ArrayList<LocationVisited> arrLocationVisited;

        public EventLoadLocationVisitedResult(boolean success, String message, ArrayList<LocationVisited> arrLocationVisited) {
            super(Type.LoadLocationVisited);
            this.success = success;
            this.message = message;
            this.arrLocationVisited = arrLocationVisited;
        }
    }

    public static class EventLoadCitysResult extends EventBase {
        public final boolean success;
        public final String message;
        public final ArrayList<City> arrCitys;

        public EventLoadCitysResult(boolean success, String message, ArrayList<City> arrCitys) {
            super(Type.LoadCitys);
            this.success = success;
            this.message = message;
            this.arrCitys = arrCitys;
        }
    }

    public static class EventLoadCountysResult extends EventBase {
        public final boolean success;
        public final String message;
        public final ArrayList<County> arrCountys;

        public EventLoadCountysResult(boolean success, String message, ArrayList<County> arrCountys) {
            super(Type.LoadCountys);
            this.success = success;
            this.message = message;
            this.arrCountys = arrCountys;
        }
    }

    public static class EventLoadBI_DailyReportResult extends EventBase {
        public final boolean success;
        public final String message;
        public final ArrayList<BI_ReportCompare> arrData;

        public EventLoadBI_DailyReportResult(boolean success, String message, ArrayList<BI_ReportCompare> arrData) {
            super(Type.BI_DailyReport);
            this.success = success;
            this.message = message;
            this.arrData = arrData;
        }
    }

    public static class EventLoadTimeLinesResult extends EventBase {
        public final boolean success;
        public final String message;
        public final ArrayList<TimeLine> arrTimelines;

        public EventLoadTimeLinesResult(boolean success, String message, ArrayList<TimeLine> arrTimelines) {
            super(Type.LoadTimeLines);
            this.success = success;
            this.message = message;
            this.arrTimelines = arrTimelines;
        }
    }

    public static class EventSendRequestGrantResult extends EventBase {
        public final boolean success;
        public final String message;

        public EventSendRequestGrantResult(boolean success, String message) {
            super(Type.SendRequestGrant);
            this.success = success;
            this.message = message;
        }
    }

    public static class EventLoadRequestGrantResult extends EventBase {
        public final boolean success;
        public final String message;
        public final ArrayList<ApproVal> arrApprovals;

        public EventLoadRequestGrantResult(boolean success, String message, ArrayList<ApproVal> arrApprovals) {
            super(Type.LoadRequestGrants);
            this.success = success;
            this.message = message;
            this.arrApprovals = arrApprovals;
        }
    }

    public static class EventApprovalApplockResult extends EventBase {
        public final boolean success;
        public final String message;

        public EventApprovalApplockResult(boolean success, String message) {
            super(Type.ApprovalApplock);
            this.success = success;
            this.message = message;
        }
    }

    public static class EventRealTimeTrackingResult extends EventBase {
        public final boolean success;
        public final String message;

        public EventRealTimeTrackingResult(boolean success, String message) {
            super(Type.RealTimeTracking);
            this.success = success;
            this.message = message;
        }
    }

    public static class EventGetLastLocationResult extends EventBase {
        public final boolean success;
        public final String message;
        public final UserInfo user;

        public EventGetLastLocationResult(boolean success, String message, UserInfo user) {
            super(Type.GetLastLocation);
            this.success = success;
            this.message = message;
            this.user = user;
        }
    }

    public static class EventLoadReportCheckInsResult extends EventBase {
        public final boolean success;
        public final String message;
        public final ArrayList<ReportCheckIn> arrayReportCheckIn;

        public EventLoadReportCheckInsResult(boolean success, String message, ArrayList<ReportCheckIn> arrayReportCheckIn) {
            super(Type.LoadReportCheckIn);
            this.success = success;
            this.message = message;
            this.arrayReportCheckIn = arrayReportCheckIn;
        }
    }
}

