package CommonLib;

/**
 * Created by My PC on 01/12/2015.
 */
public abstract class Const {
    public static final String SURVEYIMAGE = "DMSData/SurveyImage";
    public static final String FOLDERDATA = "DMSData";
    public static final String REPORTIMAGEDMS = "DMSData/ReportImageDMS";
    public static final String LIBRARYFOLDER = "DMSData/Library";
    public static final int UpdateVersion = 8; // Phiên bản cập nhật
    public static final int APILevel = 26;// Thêm createdate modifieddate
    public static final int DefaultAlarmIntervalBoostedInSeconds = 1 * 60; // chu kì cập nhật location
    public static final int DefaultAlarmIntervalBoostedMultiplier = 3;
    public static final int DefaultAlarmIntervalNormalInSeconds = DefaultAlarmIntervalBoostedInSeconds * DefaultAlarmIntervalBoostedMultiplier; // chu kì cập nhật location
    public static final int DefaultLocationAccuracyMode = 0; //0=highAccuracy, 1=balance, 2=batterySave, 3=noPower
    public static final int DefaultMaxRecordsPerPacket = 30;
    public static final int DefaultMaxRecordsLastSend = 1000; // số bản ghi cuối cùng tối đa gửi lên server mỗi lần đọc csdl
    public static final int DefaultMaxSystemLogLastSend = 100;
    public static final int DefaultMaxFileSizeImageReport = 300000;
    public static final int DefaultMaxLongEdgeImageReport = 1024;
    public static final int DefaultMaxFileSizeImageCustomer = 300000;
    public static final int DefaultMaxLongEdgeImageCustomer = 1024;
    public static final int DefaultAutoTurnOn3G = 0; // 0=off, 1=on
    public static final int DefaultCleanTrackingRecords = 0; // auto clean records cũ khi số records cách id hiện tại giá trị này, 0 = disable
    public static final int DefaultMaxTimeGetLocationAddressInSeconds = 10;
    public static final int DefaultWorkingTime = 0x7FFFFFFF;
    public static final int DefaultDropLocationTimeInSeconds = 300;
    public static final int DefaultShowMenus = 0x7;
    public static final int DefaultRealtimeTrackingTimeInSeconds = 0;
    public static final int DefaultRealtimeTrackingIntervalInSeconds = 0;
    public static final int DefaultRealtimeTrackingIntervalIdleMultiplier = 20;
    public static final int DefaultRealtimeTrackingIntervalLocationMultiplier = 2;
    public static final int DefaultRealtimeTrackingIntervalSleepingInSeconds = 0;
    public static final int DefaultGcmCommandTimeoutInSeconds = 60;
    public static final float DefaultHighPrecisionAccuracy = 30.0f;
    public static final float DefaultBoostedSpeedMPS = 1.0f;
    public static final float DefaultMaxPrecisionGetAddress = 1000.0f;
    public static final int HighPrecisionIntervalInSeconds = 30;
    public static final int MaxRetryHighPrecision = 3;
    public static final int MaxRecentApps = 8;
    public static final int checkStateRealtime = 10; // số chu kì check realtime nhân viên
    public static final int checkStateUpdateLocation = 20;//số chu kì check cập nhật vị trí nhân viên
    public static final int PaperTextNumber = 48; // Số chữ trong 1 dòng hóa đơn
    public static float ZoomInMapLevel = 20; // Độ zoom map
    public static float ZoomOutMapLevel = 10; // Độ zoom map


    public static final String HttpReportEmployeeWork = "http://v3.dinhvidienthoai.com/EmployeesWork.aspx?a=";
    public static final String HttpReportRoute = "http://v3.dinhvidienthoai.com/ReportRouteDatePR.aspx?a=";
    public static final String HttpReportTransactionEmployee = "http://v3.dinhvidienthoai.com/ReportEmployeeDateAllPR.aspx?a=";
    public static final String HttpReportTransactionGeneral = "http://v3.dinhvidienthoai.com/ReportEmployeeDateAllSPR.aspx?cn=";

    public static final String DateParameter = "&d=";
    public static final String DateFromParameter = "&dF=";
    public static final String DateToParameter = "&dT=";
    public static final String GroupParameter = "&nhom=";
    public static final String HttpEndpoint = "http://v3.dinhvidienthoai.com//HttpService.aspx";
    public static final String LinkReportImage = "http://v3.dinhvidienthoai.com//ReportImages/";
    public static final String LinkCustomerImage = "http://v3.dinhvidienthoai.com//CustomerImages/";
    public static final String LinkItemImage = "http://v3.dinhvidienthoai.com//ItemImages/";
    public static final String LinkSurveyImage = "http://v3.dinhvidienthoai.com//SurveyImages/";

//        public static final String HttpEndpoint = "http://192.168.1.213:82/HttpService.aspx";
//        public static final String LinkReportImage = "http://192.168.1.213:82/ReportImages/";
//        public static final String LinkCustomerImage = "http://192.168.1.213:82/CustomerImages/";
//        public static final String LinkItemImage = "http://192.168.1.213:82/ItemImages/";

////
//    public static final String HttpEndpoint = "http://192.168.1.166:1012//HttpService.aspx";
//    public static final String LinkReportImage = "http://192.168.1.166:1012//ReportImages/";
//    public static final String LinkCustomerImage = "http://192.168.1.166:1012//CustomerImages/";
//    public static final String LinkSurveyImage = "http://192.168.1.166:1012//SurveyImages/";
//    public static final String LinkItemImage = "http://192.168.1.166:1012//ItemImages/";
    public static final String[] Weekdays = {"Chủ nhật", "Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7"};


    public enum SyncKeys {
        Customer(0), //Khách hàng
        Product(1), // Sản phẩm
        User(2);
        private final int id;

        SyncKeys(int id) {
            this.id = id;
        }

        public int getValue() {
            return id;
        }
    }

    public enum ConfigKeys { //
        DeviceID,
        LoginToken,
        CompanyName,
        EmployeeName,
        Kickout,
        InitTrackingRowID,
        ServerTime,
        AlarmIntervalNormal,
        AlarmIntervalBoosted,
        LocationAccuracyMode,
        AlarmIntervalBoostedMultiplier,
        HighPrecisionAccuracy,
        BoostedSpeedMPS,
        MaxRecordsPerPacket,
        MaxRecordsLastSend,
        MaxSystemLogLastSend,
        MaxFileSizeImageReport,
        MaxLongEdgeImageReport,
        AutoTurnOn3G,
        CleanTrackingRecords,
        MaxTimeGetLocationAddress,
        MaxPrecisionGetAddress,
        RestartApp,
        SendSystemLog,
        SendSystemInfo,
        WorkingTime,
        HideApps,
        LockApps,
        PassApps,
        DropLocationTime,
        ShowMenus,
        RealtimeTrackingTime,
        RealtimeTrackingInterval,
        RealtimeTrackingIntervalIdleMultiplier,
        RealtimeTrackingIntervalSleeping,
        GcmCommandTimeout,
        RealtimeTrackingIntervalLocationMultiplier,
        MaxFileSizeImageCustomer,
        MaxLongEdgeImageCustomer,
        PreSale,
        ShowUpdateCustomer,
        WhiteListApp,
        DistanceCheckInCustomer,
        DistanceCheckOutCustomer,
        SystemAdminPassword,
        ID_Device,
        iTurnOffDisplayType,
        iTimeOffCount,
        MessageBody,
        EmployeeID,
        TransactionWorking,
        isManager
    }

    public enum LocationVisitedType {
        Unknown,
        CheckInCustomer,
        CheckInStore
    }

    public enum OrderDeliveryStatus {
        Unknown,
        Assigned,
        Completed,
        Canceled,
        Other
    }

    public enum StatusWorking {
        Stopped,
        Pending,
        Tracking
    }

    public enum GCMMessageType {
        NotifyMessage,
        SystemLog,
        SystemInfo,
        SendTracking,
        RestartApp,
        StartRealtimeTracking,
        StopRealtimeTracking,
        Transaction
    }

    public enum Menu {
        Setting(0), //Cài đặt
        Tracking(1), // Giám sát
        SendReport(2), // Báo cáo
        Transactions(3), // Giao dịch
        Customers(4), // Khách hàng
        Orders(5), // Đơn hàng
        Products(6), // Sản phẩm
        Inventory(7), // Tồn kho
        History(8), // Nhật ký
        InventoryInPut(9), // Nhập kho
        InventoryEmployee(10), // Kho nhân viên
        InventoryBill(11),//Phiếu nhập kho
        ApproVal(12), // Quyền truy cập
        ReportWeb(13), // Xem báo cáo
        ReportCheckIn(14), // Xem báo cáo ghé thăm
        Library(15),//Thư viện
        Survey(16),//Phiếu khảo sát
        ReportRoute(17),//Bao cao tuyen ban hang
        ReportTransactionEmployee(18),//Bao cao giao dich nhan vien
        ReportTransactionGeneral(19);//Bao cao giao dich tong hop
        private final int id;

        Menu(int id) {
            this.id = id;
        }
    }

    public enum TransactionType {
        New(0),//Mới
        CheckIn(1),//Ghé thăm
        MakeOrder(2),//Đặt hàng
        MakerOrderByPhone(3),//Đặt hàng qua điện thoại
        NotOrder(4),//Không đặt hàng
        CheckOut(5),//Rời cửa hàng
        CheckInventory(6),//Cập nhật tồn kho
        Note(7),//Ghi chú
        InventoryInput(8),//Nhập tồn kho nhân viên
        ProductSample(9),//Hàng mẫu
        ProductDisplay(10),//Hàng trưng bày
        OrderIncurred(11),//Phát sinh đơn hàng
        UpdateLocation(12),//Cập nhật vị trí
        UpdateImage(13),//Cập nhật hình ảnh
        Transaction(14),//Giao dịch
        CheckReport(15),// Báo cáo
        Survey(16),// Phiếu khảo sát
        Message(17),//Tin nhan thong bao
        UpdateRoute(18),// Cap nhat tuyen
        End(99); //Kết thúc
        private final int id;

        TransactionType(int id) {
            this.id = id;
        }

        public int getValue() {
            return id;
        }
    }

    public enum DocumentType {
        Sale(0),//Bán hàng trực tiếp
        InventoryStore(1),//Tồn kho cửa hàng
        InventoryEmployee(2),//Tồn kho nhân viên
        SaleEmployee(3),//Bán hàng trên tồn nhân viên
        ProductSample(4),//Hàng mẫu
        ProductDisplay(5);//Hàng trưng bày
        private final int id;

        DocumentType(int id) {
            this.id = id;
        }

        public int getValue() {
            return id;
        }
    }

    public enum TransactionID {
        SalesProcess(1),//Luồng Bán hàng
        DeliveryProcess(2);//Luồng giao hàng
        private final int id;

        TransactionID(int id) {
            this.id = id;
        }

        public int getValue() {
            return id;
        }
    }

    public enum TransactionStatus {
        All(-1), End(0), Working(1), Message(2);
        private final int id;

        TransactionStatus(int id) {
            this.id = id;
        }

        public int getValue() {
            return id;
        }
    }


    public enum SurveyType {
        Checkbox(0), Radio(1), EditText(2), Camera(3), Location(4), ImageView(5), VideoView(6);
        private final int id;

        SurveyType(int id) {
            this.id = id;
        }

        public int getValue() {
            return id;
        }
    }


}
