package CommonLib;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.vietdms.mobile.dmslauncher.MyMethod;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by My PC on 04/12/2015.
 */
public class LocalDB {
    private static final String TAG = "LocalDB";
    private static LocalDB instance = null;

    private LocalDB() {
    }

    public synchronized static LocalDB inst() {
        if (instance == null) {
            instance = new LocalDB();
            Log.d("LocalDB", "Create new instance");
        }
        return instance;
    }

    public synchronized boolean init(Context context) {
        try {
            dbHelper = new DbHelper(context);
            db = dbHelper.getWritableDatabase();
            Log.i("LocalDB", "database initialized");
            String deviceId = getConfigValue(Const.ConfigKeys.DeviceID);
            if (deviceId == null) {
                if (!setConfigValue(Const.ConfigKeys.DeviceID, Model.inst().getDeviceId())) {
                    Log.e("LocalDB", "cannot save DeviceID");
                    return false;
                }
            } else {
                if (!deviceId.equals(Model.inst().getDeviceId())) {
                    Log.e("LocalDB", "DeviceID mismatched");
                    return false;
                }
                String loginToken = getConfigValue(Const.ConfigKeys.LoginToken);
                if (loginToken != null && !loginToken.isEmpty()) {
                    HashMap<Const.ConfigKeys, String> mapConfigValues = getMapConfigValues();
                    for (Map.Entry<Const.ConfigKeys, String> entry : mapConfigValues.entrySet()) {
                        Model.inst().setConfigValue(entry.getKey(), entry.getValue());
                    }
                    String companyName = Model.inst().getConfigValue(Const.ConfigKeys.CompanyName);
                    String employeeName = Model.inst().getConfigValue(Const.ConfigKeys.EmployeeName);
                    if (companyName == null || companyName.isEmpty() || employeeName == null || employeeName.isEmpty()) {
                        Model.inst().setStatusWorking(Const.StatusWorking.Pending);
                        EventPool.view().enQueue(new EventType.EventLoginResult(1, "Thiết bị đã được đăng ký. Vui lòng liên hệ với quản trị viên để kích hoạt."));
                    } else {
                        Model.inst().setStatusWorking(Const.StatusWorking.Tracking);
                        EventPool.view().enQueue(new EventType.EventLoginResult(2, ""));
                    }
                    lastId = getLastAckedId(false);
                }
            }
            try {
                db.execSQL("ALTER TABLE " + dbHelper.CUSTOMER_NAME + " ADD COLUMN IsSend INTEGER DEFAULT 0");
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                db.execSQL(dbHelper.SQL_CREATE_REASON_EXIST);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                db.execSQL(dbHelper.SQL_CREATE_TRANSACTION_LINE_EXIST);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                db.execSQL(DbHelper.SQL_CREATE_ORDERS_EXIST);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                db.execSQL(DbHelper.SQL_CREATE_ORDER_DETAILS_EXIST);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public synchronized HashMap<Const.ConfigKeys, String> getMapConfigValues() {
        try {
            Cursor cursor = db.rawQuery("select Key,Value from " + DbHelper.CONFIG_NAME, null);
            int len = cursor.getCount();
            HashMap<Const.ConfigKeys, String> map = new HashMap<Const.ConfigKeys, String>(len);
            cursor.moveToFirst();
            for (int i = 0; i < len; i++) {
                int key = cursor.getInt(0);
                String value = cursor.getString(1);
                for (int j = 0; j < Const.ConfigKeys.values().length; j++) {
                    if (Const.ConfigKeys.values()[j].ordinal() == key) {
                        map.put(Const.ConfigKeys.values()[j], value);
                        break;
                    }
                }
                cursor.moveToNext();
            }
            return map;
        } catch (Exception ex) {
            return null;
        }
    }

    public synchronized String getConfigValue(Const.ConfigKeys key) {
        try {
            Cursor cursor = db.rawQuery("select Value from " + DbHelper.CONFIG_NAME + " where Key=" + String.valueOf(key.ordinal()), null);
            if (cursor.getCount() == 0) return null;
            cursor.moveToFirst();
            return cursor.getString(0);
        } catch (Exception ex) {
            return null;
        }
    }

    public synchronized boolean setConfigValue(Const.ConfigKeys key, Object value) {
        try {
            ContentValues cv = new ContentValues();
            cv.put("Value", value.toString());
            if (getConfigValue(key) == null) {
                cv.put("Key", key.ordinal());
                return db.insert(DbHelper.CONFIG_NAME, null, cv) >= 0;
            } else {
                return db.update(DbHelper.CONFIG_NAME, cv, "Key=" + String.valueOf(key.ordinal()), null) > 0;
            }
        } catch (Exception ex) {
            return false;
        }
    }

    public synchronized long addTransactionLine(TransactionLine line, int isSend) {
        try {
            ContentValues cv = new ContentValues();
            cv.put("UrlImage", line.urlImage);
            cv.put("IDCustomer", line.id_customer);
            cv.put("IDTransaction", line.id_transaction);
            cv.put("Note", line.note);
            cv.put("LocationRefID", line.location_ref_id);
            cv.put("Status", line.status);
            cv.put("UserID", line.userid);
            cv.put("IDEmployee", line.id_employee);
            cv.put("CreateDate", line.create_date);
            cv.put("ModifiedDate", line.modified_date);
            BigDecimal longitude = new BigDecimal(line.longitude);
            cv.put("Longitude", longitude.floatValue());
            BigDecimal latitude = new BigDecimal(line.latitude);
            cv.put("Latitude", latitude.floatValue());
            cv.put("IDExtNo", line.id_ExtNo_);
            cv.put("IDTransactionDefine", line.id_transaction_define);
            cv.put("NameEmployee", line.name_employee);
            cv.put("RefNo_", line.refNo_);
            cv.put("IsSend", isSend);
            long rowId = db.insert(DbHelper.TRANSACTION_LINE_NAME, null, cv);
            if (rowId >= 0) {
                Log.w(TAG, "addTransactionLine:  " + line.note + "<->" + isSend);
            }
            return rowId;
        } catch (Exception e) {
            return -1;
        }
    }

    public synchronized long addOrderDetail(int idOrder, ArrayList<OrderDetail> orderDetails) {
        try {
            long rowId = 0;
            for (OrderDetail orderDetail : orderDetails) {
                ContentValues cv = new ContentValues();

                cv.put("IDOrder", idOrder);
                cv.put("No_", orderDetail.no_);
                cv.put("IDItem", orderDetail.id_item);
                cv.put("ItemNo_", orderDetail.itemNo_);
                cv.put("Name", orderDetail.name);
                cv.put("Quantity", orderDetail.quantity);
                cv.put("UnitPrice", orderDetail.unitprice);
                cv.put("DiscountPercent", orderDetail.discountPercent);
                cv.put("ItemType", orderDetail.itemType);
                cv.put("Note", orderDetail.note);
                cv.put("Status", orderDetail.status);


                rowId = db.insert(DbHelper.ORDER_DETAIL_NAME, null, cv);
                if (rowId >= 0) {
                    Log.w(TAG, "addOrderDetail:  " + orderDetail.name);
                }
            }

            return rowId;
        } catch (Exception e) {
            return -1;
        }
    }

    public synchronized long addOrder(Order order, int IsSend) {
        try {
            ContentValues cv = new ContentValues();
            cv.put("No_", order.no_);
            cv.put("Name", order.name);
            cv.put("Time", order.time);
            cv.put("IDCustomer", order.id_customer);
            cv.put("Status", order.status);
            cv.put("Note", order.note);
            cv.put("Amount", order.amount);
            cv.put("IDCompany", order.id_company);
            cv.put("IDRegion", order.id_region);
            cv.put("IDBranch", order.id_branch);
            cv.put("IDEmployee", order.id_employee);
            cv.put("IDParent", order.id_parent);
            cv.put("DocumentType", order.document_type);
            cv.put("ImageUrl", order.imageUrl);
            cv.put("EmployeeName", order.employeeName);
            cv.put("SearchField", Utils.unAccent(order.name));
            cv.put("IsSend", IsSend);

            long rowId = db.insert(DbHelper.ORDER_NAME, null, cv);
            if (rowId >= 0) {
                Log.w(TAG, "addOrder:  " + order.name);
            }
            return rowId;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public synchronized long addCustomer(Customer customer, int isSend) {
        try {
            ContentValues cv = new ContentValues();
            cv.put("RowID", customer.id);
            cv.put("No_", customer.no_);
            cv.put("Name", customer.name);
            cv.put("Address", customer.address);
            cv.put("PhoneNumber", customer.phoneNumber);
            cv.put("ImageUrl", customer.imageUrl);
            BigDecimal longitude = new BigDecimal(customer.longitude);
            cv.put("Longitude", longitude.floatValue());
            BigDecimal latitude = new BigDecimal(customer.latitude);
            cv.put("Latitude", latitude.floatValue());
            cv.put("ID_City", customer.city);
            cv.put("ID_County", customer.county);
            cv.put("Distance", customer.distance);
            cv.put("Amount_Last_Month", customer.amount_last_month);
            cv.put("Last_Order_Day", customer.last_order_day);
            cv.put("Last_Visited", customer.last_visited);
            cv.put("Working_Time", customer.workingtime);
            cv.put("SearchField", Utils.unAccent(customer.name) + " " + Utils.unAccent(customer.address));
            cv.put("IsSend", isSend);
            long rowId = db.insert(DbHelper.CUSTOMER_NAME, null, cv);
            if (rowId >= 0) {
                Log.w(TAG, "addCustomer:  " + customer.name);
                updateSync(Const.SyncKeys.Customer);
            }
            return rowId;
        } catch (Exception e) {
            return -1;
        }
    }

    public synchronized long addReason(ReasonNotOrder reason) {
        try {
            ContentValues cv = new ContentValues();
            cv.put("Description", reason.content);
            cv.put("Type", reason.type);
            long rowId = db.insert(DbHelper.REASON_NAME, null, cv);
            if (rowId >= 0) {
                Log.w(TAG, "addReason:  " + reason.content);
            }
            return rowId;
        } catch (Exception e) {
            return -1;
        }
    }


    public synchronized void deleteSyncData() {
        try {
            createSyncIfNotExist();
            db.execSQL("delete from " + DbHelper.SYNC_NAME);
            db.execSQL("delete from " + DbHelper.PRODUCT_NAME);
            db.execSQL("delete from " + DbHelper.CUSTOMER_NAME);
            db.execSQL("delete from " + DbHelper.USER_NAME);
            db.execSQL("delete from " + DbHelper.REASON_NAME);
            db.execSQL("delete from " + DbHelper.PROMOTION_HEADER_NAME);
            db.execSQL("delete from " + DbHelper.PROMOTION_DETAIL_NAME);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void deleteSyncSurvey() {
        try {
            createSurveyIfNotExist();
            db.execSQL("delete from " + DbHelper.CAMPAIGN_NAME);
            db.execSQL("delete from " + DbHelper.HEADER_NAME);
            db.execSQL("delete from " + DbHelper.LINE_NAME);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void deleteConfig() {
        try {
            db.execSQL("delete from " + DbHelper.CONFIG_NAME);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public synchronized int countCustomerByRoute(int id, int routeId) {
        try {
            int i = -routeId - 1;
            String query = "SELECT Count(RowID) FROM " + DbHelper.CUSTOMER_NAME
                    + " WHERE ((Working_Time &  (1 << (" + routeId + " * 2))) != 0 OR (Working_Time & (1 << (" + i + " * 2 + 1))) != 0 OR " + i + " <= 0 ) ";
            if (Model.inst().getConfigValue(Const.ConfigKeys.EmployeeID) == "") {
                Cursor c = db.rawQuery(query, null);
                c.moveToFirst();
                int count = c.getInt(0);
                c.close();
                return count;
            } else {
                if (id == Integer.parseInt(Model.inst().getConfigValue(Const.ConfigKeys.EmployeeID)) || id == -1) {
                    Cursor c = db.rawQuery(query, null);
                    c.moveToFirst();
                    int count = c.getInt(0);
                    c.close();
                    return count;
                } else return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public synchronized int countTransactionLine(int id_employee, int id_customer) {
        try {
            String query = "SELECT Count(RowID) FROM " + DbHelper.TRANSACTION_LINE_NAME + " WHERE IDEmployee = " + id_employee + " AND IDCustomer = " + id_customer;
            Cursor c = db.rawQuery(query, null);
            c.moveToFirst();
            int count = c.getInt(0);
            c.close();
            return count == 0 ? -1 : count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public synchronized int countReason() {
        try {
            String query = "SELECT Count(RowID) FROM " + DbHelper.REASON_NAME;
            Cursor c = db.rawQuery(query, null);
            c.moveToFirst();
            int count = c.getInt(0);
            c.close();
            return count == 0 ? -1 : count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public synchronized int countOrder(int id) {
        try {
            String query = "SELECT Count(RowID) FROM " + DbHelper.ORDER_NAME;
            Cursor c = db.rawQuery(query,null);
            c.moveToFirst();
            int count = c.getInt(0);
            c.close();
            return count == 0 ? -1 : count;
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    public synchronized int countPromotionHeader(int id){
        try{
            String query = "SELECT Count(RowID) FROM " + DbHelper.PROMOTION_HEADER_NAME;
            Cursor c = db.rawQuery(query,null);
            c.moveToFirst();
            int count = c.getInt(0);
            c.close();
            return count == 0 ? -1 : count;
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    public synchronized int countPromotionDetail(int id){
        try{
            String query = "SELECT Count(RowID) FROM " + DbHelper.PROMOTION_DETAIL_NAME;
            Cursor c = db.rawQuery(query,null);
            c.moveToFirst();
            int count = c.getInt(0);
            c.close();
            return count == 0 ? -1 : count;
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }


    public synchronized int countCustomer(int id) {
        //-1 :  cần sync
        //0 : load online  int countCustomer = LocalDB.inst().countCustomer(Home.nowIdCustomer);
        //>0 : load offline

        //CHECK THỜI GIAN SYNC
        try {
            Cursor sync = db.rawQuery("SELECT Time FROM " + DbHelper.SYNC_NAME + " WHERE RowID = 1", null);
            sync.moveToFirst();
            long time = sync.getLong(sync.getColumnIndex("Time"));
            sync.close();

            if (TimeUnit.MILLISECONDS.toHours(Model.getServerTime() - time) >= 24) {
                //quá 1 ngày
                return -1;
            } else {
                try {
                    if (Model.inst().getConfigValue(Const.ConfigKeys.EmployeeID) == "") {
                        Cursor c = db.rawQuery("SELECT Count(RowID) FROM " + DbHelper.CUSTOMER_NAME, null);
                        c.moveToFirst();
                        int count = c.getInt(0);
                        c.close();
                        return count == 0 ? -1 : count;
                    } else {
                        if (id == Integer.parseInt(Model.inst().getConfigValue(Const.ConfigKeys.EmployeeID)) || id == -1) {
                            Cursor c = db.rawQuery("SELECT Count(RowID) FROM " + DbHelper.CUSTOMER_NAME, null);
                            c.moveToFirst();
                            int count = c.getInt(0);
                            c.close();
                            return count == 0 ? -1 : count;
                        } else return 0;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        } catch (Exception e) {
            try {
                if (Model.inst().getConfigValue(Const.ConfigKeys.EmployeeID) == "") {
                    Cursor c = db.rawQuery("SELECT Count(RowID) FROM " + DbHelper.CUSTOMER_NAME, null);
                    c.moveToFirst();
                    int count = c.getInt(0);
                    c.close();
                    return count == 0 ? -1 : count;
                } else {
                    if (id == Integer.parseInt(Model.inst().getConfigValue(Const.ConfigKeys.EmployeeID)) || id == -1) {
                        Cursor c = db.rawQuery("SELECT Count(RowID) FROM " + DbHelper.CUSTOMER_NAME, null);
                        c.moveToFirst();
                        int count = c.getInt(0);
                        c.close();
                        return count == 0 ? -1 : count;
                    } else return 0;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                return 0;
            }
        }
    }

    public synchronized int countProduct() {
        //CHECK THỜI GIAN SYNC
        try {
            Cursor sync = db.rawQuery("SELECT Time FROM " + DbHelper.SYNC_NAME + " WHERE RowID = 0", null);
            sync.moveToFirst();
            long time = sync.getLong(sync.getColumnIndex("Time"));
            sync.close();
            if (TimeUnit.MILLISECONDS.toHours(Model.getServerTime() - time) >= 24) {
                //quá 1 ngày
                return -1;
            } else {
                try {
                    Cursor c = db.rawQuery("SELECT Count(RowID) FROM " + DbHelper.PRODUCT_NAME, null);
                    c.moveToFirst();
                    int count = c.getInt(0);
                    c.close();
                    return count;
                } catch (Exception e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        } catch (Exception e) {
            try {
                Cursor c = db.rawQuery("SELECT Count(RowID) FROM " + DbHelper.PRODUCT_NAME, null);
                c.moveToFirst();
                int count = c.getInt(0);
                c.close();
                return count;
            } catch (Exception ez) {
                ez.printStackTrace();
                return 0;
            }
        }
    }

    public synchronized int countUser() {
        //CHECK THỜI GIAN SYNC
        try {
            Cursor sync = db.rawQuery("SELECT Time FROM " + DbHelper.SYNC_NAME + " WHERE RowID = 2", null);
            sync.moveToFirst();
            long time = sync.getLong(sync.getColumnIndex("Time"));
            sync.close();
            if (TimeUnit.MILLISECONDS.toHours(Model.getServerTime() - time) >= 24) {
                //quá 1 ngày
                return -1;
            } else {
                try {
                    Cursor c = db.rawQuery("SELECT Count(RowID) FROM " + DbHelper.USER_NAME, null);
                    c.moveToFirst();
                    int count = c.getInt(0);
                    c.close();
                    return count;
                } catch (Exception e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        } catch (Exception e) {
            try {
                Cursor c = db.rawQuery("SELECT Count(RowID) FROM " + DbHelper.USER_NAME, null);
                c.moveToFirst();
                int count = c.getInt(0);
                c.close();
                return count;
            } catch (Exception ex) {
                ex.printStackTrace();
                return 0;
            }
        }
    }

    public synchronized int countSurvey() {
        try {
            Cursor c = db.rawQuery("SELECT Count(RowID) FROM " + DbHelper.CAMPAIGN_NAME, null);
            c.moveToFirst();
            int count = c.getInt(0);
            c.close();
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    public synchronized ArrayList<Promotion> getPromotion(String no_) {
        ArrayList<Promotion> result = new ArrayList<>();
        try {
            String query = "SELECT * FROM "+ DbHelper.
        } catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }
    public synchronized ArrayList<Product> loadProduct(int lastId, String filter) {
        ArrayList<Product> result = new ArrayList<>();
        try {
            String query = "SELECT * FROM " + DbHelper.PRODUCT_NAME
                    + " WHERE RowID < " + lastId + "AND SearchField LIKE '%" + Utils.rtrim(filter) + "%' order by RowID desc";
            Cursor c = db.rawQuery(query, null);
            if (c.moveToFirst()) {
                do {
                    Product product = new Product();
                    product.id = c.getInt(c.getColumnIndex("RowID"));
                    product.no_ = c.getString(c.getColumnIndex("No_"));
                    product.name = c.getString(c.getColumnIndex("Name"));
                    product.unit = c.getString(c.getColumnIndex("Unit"));
                    product.price = c.getFloat(c.getColumnIndex("Price"));
                    product.imageUrl = c.getString(c.getColumnIndex("ImageUrl"));
                    product.description = c.getString(c.getColumnIndex("Description"));
                    product.id_ProductGroup = c.getInt(c.getColumnIndex("ID_ProductGroup"));
                    product.name_ProductGroup = c.getString(c.getColumnIndex("Name_ProductGroup"));
                    product.inventory = c.getInt(c.getColumnIndex("Inventory"));
                    result.add(product);
                } while (c.moveToNext());
            }
            c.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public synchronized ArrayList<Product> loadProduct(String filter) {
        ArrayList<Product> result = new ArrayList<>();
        try {
            String query = "SELECT * FROM " + DbHelper.PRODUCT_NAME
                    + " WHERE SearchField LIKE '%" + Utils.rtrim(filter) + "%' order by RowID desc";
            Cursor c = db.rawQuery(query, null);
            if (c.moveToFirst()) {
                do {
                    Product product = new Product();
                    product.id = c.getInt(c.getColumnIndex("RowID"));
                    product.no_ = c.getString(c.getColumnIndex("No_"));
                    product.name = c.getString(c.getColumnIndex("Name"));
                    product.unit = c.getString(c.getColumnIndex("Unit"));
                    product.price = c.getFloat(c.getColumnIndex("Price"));
                    product.imageUrl = c.getString(c.getColumnIndex("ImageUrl"));
                    product.description = c.getString(c.getColumnIndex("Description"));
                    product.id_ProductGroup = c.getInt(c.getColumnIndex("ID_ProductGroup"));
                    product.name_ProductGroup = c.getString(c.getColumnIndex("Name_ProductGroup"));
                    product.inventory = c.getInt(c.getColumnIndex("Inventory"));
                    result.add(product);
                } while (c.moveToNext());
            }
            c.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public synchronized ArrayList<UserInfo> loadUser() {
        ArrayList<UserInfo> result = new ArrayList<>();
        try {
            String query = "SELECT * FROM " + DbHelper.USER_NAME;
            Cursor c = db.rawQuery(query, null);
            if (c.moveToFirst()) {
                do {
                    UserInfo userInfo = new UserInfo();
                    userInfo.id_employee = c.getInt(c.getColumnIndex("IDEmployee"));
                    userInfo.fullname = c.getString(c.getColumnIndex("FullName"));
                    userInfo.latitude = c.getDouble(c.getColumnIndex("Latitude"));
                    userInfo.longitude = c.getDouble(c.getColumnIndex("Longitude"));
                    userInfo.accuracy = c.getFloat(c.getColumnIndex("Accuracy"));
                    userInfo.locationDate = c.getLong(c.getColumnIndex("LocationDate"));
                    userInfo.trackingDate = c.getLong(c.getColumnIndex("TrackingDate"));
                    userInfo.address = c.getString(c.getColumnIndex("Address"));
                    userInfo.isWifi = (byte) c.getInt(c.getColumnIndex("IsWifi"));
                    userInfo.is3G = (byte) c.getInt(c.getColumnIndex("Is3G"));
                    userInfo.isGPS = (byte) c.getInt(c.getColumnIndex("IsGPS"));
                    userInfo.isAirplaneMode = (byte) c.getInt(c.getColumnIndex("IsAirplaneMode"));
                    userInfo.batteryLevel = c.getInt(c.getColumnIndex("BatteryLevel"));
                    result.add(userInfo);
                } while (c.moveToNext());
            }
            c.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public synchronized ArrayList<Customer> loadCustomer(int routeID, String filter) {
        ArrayList<Customer> result = new ArrayList<>();
        try {
            int i = -routeID - 1;
            String query = "SELECT * FROM " + DbHelper.CUSTOMER_NAME
                    + " WHERE ((Working_Time &  (1 << (" + i + " * 2))) != 0 OR (Working_Time & (1 << (" + i + " * 2 + 1))) != 0 OR " + i + " <= 0 ) "
                    + " AND SearchField LIKE '%" + Utils.rtrim(filter) + "%'";
            Cursor c = db.rawQuery(query, null);
            if (c.moveToFirst()) {
                do {
                    Customer customer = new Customer();
                    customer.id = c.getInt(c.getColumnIndex("RowID"));
                    customer.no_ = c.getString(c.getColumnIndex("No_"));
                    customer.name = c.getString(c.getColumnIndex("Name"));
                    customer.address = c.getString(c.getColumnIndex("Address"));
                    customer.phoneNumber = c.getString(c.getColumnIndex("PhoneNumber"));
                    customer.imageUrl = c.getString(c.getColumnIndex("ImageUrl"));
                    customer.longitude = c.getFloat(c.getColumnIndex("Longitude"));
                    customer.latitude = c.getFloat(c.getColumnIndex("Latitude"));
                    customer.city = c.getInt(c.getColumnIndex("ID_City"));
                    customer.county = c.getInt(c.getColumnIndex("ID_County"));
                    customer.distance = c.getInt(c.getColumnIndex("Distance"));
                    customer.amount_last_month = c.getFloat(c.getColumnIndex("Amount_Last_Month"));
                    customer.last_order_day = c.getLong(c.getColumnIndex("Last_Order_Day"));
                    customer.last_visited = c.getLong(c.getColumnIndex("Last_Visited"));
                    customer.workingtime = c.getInt(c.getColumnIndex("Working_Time"));
                    result.add(customer);
                } while (c.moveToNext());
            }
            c.close();
            Collections.sort(result, new Comparator<Customer>() {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public synchronized ArrayList<ReasonNotOrder> loadReason() {
        ArrayList<ReasonNotOrder> result = new ArrayList<>();
        try {
            String query = "SELECT * FROM " + DbHelper.REASON_NAME;
            Cursor c = db.rawQuery(query, null);
            if (c.moveToFirst()) {
                do {
                    ReasonNotOrder reasonNotOrder = new ReasonNotOrder();
                    reasonNotOrder.id = c.getInt(c.getColumnIndex("RowID"));
                    reasonNotOrder.content = c.getString(c.getColumnIndex("Description"));
                    reasonNotOrder.type = c.getInt(c.getColumnIndex("Type"));
                    result.add(reasonNotOrder);
                } while (c.moveToNext());
            }
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public synchronized ArrayList<TransactionLine> loadTransactionLine(int id_customer) {
        ArrayList<TransactionLine> result = new ArrayList<>();
        try {
            String query = "SELECT * FROM " + DbHelper.TRANSACTION_LINE_NAME + " WHERE IDCustomer = " + id_customer + " Order by ModifiedDate desc";
            Cursor c = db.rawQuery(query, null);
            if (c.moveToFirst()) {
                do {
                    TransactionLine line = new TransactionLine();
                    line.urlImage = c.getInt(c.getColumnIndex("UrlImage"));
                    line.id_customer = c.getInt(c.getColumnIndex("IDCustomer"));
                    line.id_transaction = c.getInt(c.getColumnIndex("IDTransaction"));
                    line.note = c.getString(c.getColumnIndex("Note"));
                    line.location_ref_id = c.getInt(c.getColumnIndex("LocationRefID"));
                    line.status = c.getInt(c.getColumnIndex("Status"));
                    line.userid = c.getString(c.getColumnIndex("UserID"));
                    line.id_employee = c.getInt(c.getColumnIndex("IDEmployee"));
                    line.create_date = c.getLong(c.getColumnIndex("CreateDate"));
                    line.modified_date = c.getInt(c.getColumnIndex("ModifiedDate"));
                    line.latitude = c.getDouble(c.getColumnIndex("Latitude"));
                    line.longitude = c.getDouble(c.getColumnIndex("Longitude"));
                    line.id_ExtNo_ = c.getInt(c.getColumnIndex("IDExtNo"));
                    line.id_transaction_define = c.getInt(c.getColumnIndex("IDTransactionDefine"));
                    line.name_employee = c.getString(c.getColumnIndex("NameEmployee"));
                    result.add(line);
                } while (c.moveToNext());
            }
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public synchronized long addHeader(SurveyHeader header) {
        try {
            ContentValues cv = new ContentValues();
            cv.put("RowID", header.id);
            cv.put("Sort", header.sort);
            cv.put("Name", header.name);
            cv.put("Description", header.description);
            cv.put("Type", header.type);
            cv.put("ID_Campaign", header.id_campaign);
            cv.put("Status", header.status);
            cv.put("RequireField", header.requireField);
            long rowId = db.insert(DbHelper.HEADER_NAME, null, cv);
            if (rowId >= 0) {
                Log.w(TAG, "addHeader: " + header.name);
            }
            return rowId;
        } catch (Exception e) {
            return -1;
        }
    }

    public synchronized long addPromotionHeader(PromotionHeader header) {
        try {
            ContentValues cv = new ContentValues();
            cv.put("RowID",header.id);
            cv.put("No_", header.no_);
            cv.put("IsGroup", header.isGroup);
            cv.put("ItemNo_", header.itemNo_);
            cv.put("Ref_Item", header.refItem);
            cv.put("KyHieu", header.kyHieu);
            cv.put("HeaderNo_", header.headerNo_);
            cv.put("HeaderName", header.headerName);
            long rowId = db.insert(DbHelper.PROMOTION_HEADER_NAME, null, cv);
            if (rowId >= 0) {
                Log.w(TAG, "addPromotionHeader: " + header.headerName);
            }
            return rowId;
        } catch (Exception e) {
            return -1;
        }
    }



    public synchronized long addLine(SurveyLine line) {
        try {
            ContentValues cv = new ContentValues();
            cv.put("RowID", line.id);
            cv.put("ID_Survey", line.id_survey);
            cv.put("RefNo_", line.refNo_);
            cv.put("Sort", line.sort);
            cv.put("Question", line.question);
            cv.put("Type", line.type);
            cv.put("Result", line.result);
            cv.put("Status", line.status);
            long rowId = db.insert(DbHelper.LINE_NAME, null, cv);
            if (rowId >= 0) {
                Log.w(TAG, "addLine: " + line.question);
            }
            return rowId;
        } catch (Exception e) {
            return -1;
        }
    }

    public synchronized long addPromotionDetail(PromotionDetail line) {
        try {
            ContentValues cv = new ContentValues();
            cv.put("RowID",line.id);
            cv.put("PromotionNo_", line.promotionNo_);
            cv.put("PromotionDescription", line.promotionDescription);
            cv.put("IsDiscountMore", line.isDiscountMore);

            long rowId = db.insert(DbHelper.PROMOTION_DETAIL_NAME, null, cv);
            if (rowId >= 0) {
                Log.w(TAG, "addPromotionDetail: " + line.promotionDescription);
            }
            return rowId;
        } catch (Exception e) {
            return -1;
        }
    }

    public synchronized long addResult(SurveyResult result, int isSend) {
        try {
            //kiểm tra nếu có dữ trùng Source ID và ID_SurveyLine thì xóa insert lại
            deleteResult(result.id_customer, result.id_survey_line);
            ContentValues cv = new ContentValues();
            cv.put("SourceID", result.id_customer);
            cv.put("ID_SurveyLine", result.id_survey_line);
            cv.put("Answer", result.answer);
            cv.put("Status", result.status);
            cv.put("DocumentDate", Model.getServerTime());
            cv.put("isSend", isSend);
            long rowId = db.insertWithOnConflict(DbHelper.RESULT_NAME, "", cv, SQLiteDatabase.CONFLICT_REPLACE);
            if (rowId >= 0) {
                Log.w(TAG, "addResult: " + result.answer);
            }
            return rowId;
        } catch (Exception e) {
            return -1;
        }
    }

    private void deleteResult(int id_customer, int id_survey_line) {
        try {
            db.execSQL("delete from " + DbHelper.RESULT_NAME + " where SourceID = " + id_customer + " and ID_SurveyLine = " + id_survey_line);
            Log.w(TAG, "deleteResult: success");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public synchronized long addCampaign(SurveyCampaign campaign) {
        try {
            ContentValues cv = new ContentValues();
            cv.put("RowID", campaign.id);
            cv.put("No_", campaign.no_);
            cv.put("Name", campaign.name);
            cv.put("DocumentDate", campaign.documentDate);
            cv.put("BeginDate", campaign.beginDate);
            cv.put("EndDate", campaign.endDate);
            cv.put("Description", campaign.description);
            long rowId = db.insert(DbHelper.CAMPAIGN_NAME, null, cv);
            if (rowId >= 0) {
                Log.w(TAG, "addCampaign: " + campaign.name);
            }
            return rowId;
        } catch (Exception e) {
            return -1;
        }
    }


    public synchronized long addProduct(Product product) {
        try {
            ContentValues cv = new ContentValues();
            cv.put("RowID", product.id);
            cv.put("No_", product.no_);
            cv.put("Name", product.name);
            cv.put("Unit", product.unit);
            cv.put("Price", product.price);
            cv.put("ImageUrl", product.imageUrl);
            cv.put("Description", product.description);
            cv.put("ID_ProductGroup", product.id_ProductGroup);
            cv.put("Name_ProductGroup", product.name_ProductGroup);
            cv.put("Inventory", product.inventory);
            cv.put("SearchField", Utils.unAccent(product.name));
            long rowId = db.insert(DbHelper.PRODUCT_NAME, null, cv);
            if (rowId >= 0) {
                Log.w(TAG, "addProduct: " + product.name);
                updateSync(Const.SyncKeys.Product);
            }
            return rowId;
        } catch (Exception e) {
            return -1;
        }
    }

    public synchronized long addUserInfo(UserInfo userInfo) {
        try {
            ContentValues cv = new ContentValues();
            cv.put("IDEmployee", userInfo.id_employee);
            cv.put("FullName", userInfo.fullname);
            cv.put("Latitude", userInfo.latitude);
            cv.put("Longitude", userInfo.longitude);
            cv.put("Accuracy", userInfo.accuracy);
            cv.put("LocationDate", userInfo.locationDate);
            cv.put("TrackingDate", userInfo.trackingDate);
            cv.put("Address", userInfo.address);
            cv.put("IsWifi", userInfo.isWifi);
            cv.put("Is3G", userInfo.is3G);
            cv.put("IsGPS", userInfo.isGPS);
            cv.put("IsAirplaneMode", userInfo.isAirplaneMode);
            cv.put("BatteryLevel", userInfo.batteryLevel);
            long rowId = db.insert(DbHelper.USER_NAME, null, cv);
            if (rowId >= 0) {
                Log.w(TAG, "addUser: " + userInfo.fullname);
                updateSync(Const.SyncKeys.User);
            }
            return rowId;
        } catch (Exception e) {
            return -1;
        }
    }

    public synchronized long updateSyncTransaction(int isSend) {
        ContentValues cv = new ContentValues();
        cv.put("IsSend", isSend);
        cv.put("ModifiedDate", Model.getServerTime());
        return db.update(DbHelper.TRANSACTION_LINE_NAME, cv, null, null);  // number 1 is the _id here, update to variable for your code
    }

    public synchronized long updateSyncCustomer(int isSend) {
        ContentValues cv = new ContentValues();
        cv.put("IsSend", isSend);
        return db.update(DbHelper.CUSTOMER_NAME, cv, null, null);  //
    }
    public synchronized long updateSyncOrder(int isSend) {
        ContentValues cv = new ContentValues();
        cv.put("IsSend", isSend);
        return db.update(DbHelper.ORDER_NAME, cv, null, null);  //
    }

    public synchronized long updateSync(Const.SyncKeys keys) {
        ContentValues cv = new ContentValues();
        cv.put("RowID", keys.getValue());
        cv.put("Time", Model.getServerTime());
        int id = (int) db.insertWithOnConflict(DbHelper.SYNC_NAME, null, cv, SQLiteDatabase.CONFLICT_IGNORE);
        if (id == -1) {
            return db.update(DbHelper.SYNC_NAME, cv, "RowID = " + keys.getValue(), null);  // number 1 is the _id here, update to variable for your code
        } else {
            return id;
        }

    }


    public synchronized long addTracking(TrackingItem trackingItem) {
        try {
            ContentValues cv = new ContentValues();
            cv.put("DeviceID", trackingItem.deviceId);
            cv.put("VisitedID", trackingItem.visitedId);
            cv.put("VisitedType", trackingItem.visitedType);
            cv.put("Latitude", trackingItem.latitude);
            cv.put("Longitude", trackingItem.longitude);
            cv.put("Accuracy", trackingItem.accuracy);
            cv.put("Speed", trackingItem.speed);
            cv.put("DistanceMeter", trackingItem.distanceMeter);
            cv.put("MilisecElapsed", trackingItem.milisecElapsed);
            cv.put("LocationDate", trackingItem.locationDate);
            cv.put("TrackingDate", trackingItem.trackingDate);
            cv.put("IsWifi", trackingItem.isWifi);
            cv.put("Is3G", trackingItem.is3G);
            cv.put("IsAirplaneMode", trackingItem.isAirplaneMode);
            cv.put("IsGPS", trackingItem.isGPS);
            cv.put("Note", trackingItem.note);
            cv.put("GetType", trackingItem.getType);
            cv.put("GetMethod", trackingItem.getMethod);
            if (trackingItem.cellInfo != null) {
                cv.put("CellID", trackingItem.cellInfo.cellID);
                cv.put("LAC", trackingItem.cellInfo.LAC);
                cv.put("MCC", trackingItem.cellInfo.MCC);
                cv.put("MNC", trackingItem.cellInfo.MNC);
            }
            cv.put("BatteryLevel", trackingItem.batteryLevel);
            long rowId = db.insert(DbHelper.TABLE_NAME, null, cv);
            if (rowId >= 0) {
                trackingItem.rowID = (int) rowId;
                Model.inst().setLastTrackingItemSaved(trackingItem);
                int cleanTrackingRecords = Model.inst().getConfigValue(Const.ConfigKeys.CleanTrackingRecords, Const.DefaultCleanTrackingRecords);
                if (cleanTrackingRecords > 0) {
                    if (rowId - lastId > cleanTrackingRecords) {
                        if (!cleanTrackingRecords()) {
                            SystemLog.inst().addLog(SystemLog.Type.Warning, "Việc làm sạch dữ liệu cũ thất bại");
                        }
                    }
                }
            }
            return rowId;
        } catch (Exception ex) {
            return -1;
        }
    }

    public synchronized boolean setTrackingRowIDSeed(int seed) {
        try {
            TrackingItem[] items = getLastTrackingRecords(1);
            if (items.length > 0) {
                if (items[0].rowID >= seed) {
                    Log.i("LocalDB", "no need to setTrackingRowIDSeed");
                    return false;
                }
                Log.w("LocalDB", "setTrackingRowIDSeed reseed from " + items[0].rowID + " to " + seed);
            } else {
                Log.i("LocalDB", "setTrackingRowIDSeed to " + seed);
            }
            db.execSQL("insert into " + DbHelper.TABLE_NAME + "(RowID) values(" + seed + "); delete from " + DbHelper.TABLE_NAME + " where RowID=" + seed);
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    public synchronized TrackingItem[] getLastTrackingRecords(int n) {
        if (n <= 0) return new TrackingItem[0];
        int lastAckedId = getLastAckedId(true);
        Cursor cursor = db.rawQuery("select * from " + DbHelper.TABLE_NAME + " where RowID > " + lastAckedId + " order by RowID desc limit " + n, null);
        TrackingItem[] result = new TrackingItem[cursor.getCount()];
        try {
            cursor.moveToFirst();
            for (int i = result.length - 1; i >= 0; i--) {
                TrackingItem trackingItem = new TrackingItem();
                trackingItem.rowID = cursor.getInt(cursor.getColumnIndex("RowID"));
                trackingItem.deviceId = cursor.getString(cursor.getColumnIndex("DeviceID"));
                trackingItem.visitedId = cursor.getString(cursor.getColumnIndex("VisitedID"));
                trackingItem.visitedType = (byte) cursor.getInt(cursor.getColumnIndex("VisitedType"));
                trackingItem.latitude = cursor.getDouble(cursor.getColumnIndex("Latitude"));
                trackingItem.longitude = cursor.getDouble(cursor.getColumnIndex("Longitude"));
                trackingItem.accuracy = cursor.getFloat(cursor.getColumnIndex("Accuracy"));
                trackingItem.speed = cursor.getFloat(cursor.getColumnIndex("Speed"));
                trackingItem.distanceMeter = cursor.getInt(cursor.getColumnIndex("DistanceMeter"));
                trackingItem.milisecElapsed = cursor.getInt(cursor.getColumnIndex("MilisecElapsed"));
                trackingItem.locationDate = cursor.getLong(cursor.getColumnIndex("LocationDate"));
                trackingItem.trackingDate = cursor.getLong(cursor.getColumnIndex("TrackingDate"));
                trackingItem.isWifi = (byte) cursor.getInt(cursor.getColumnIndex("IsWifi"));
                trackingItem.is3G = (byte) cursor.getInt(cursor.getColumnIndex("Is3G"));
                trackingItem.isAirplaneMode = (byte) cursor.getInt(cursor.getColumnIndex("IsAirplaneMode"));
                trackingItem.isGPS = (byte) cursor.getInt(cursor.getColumnIndex("IsGPS"));
                trackingItem.note = cursor.getString(cursor.getColumnIndex("Note"));
                trackingItem.getType = (byte) cursor.getInt(cursor.getColumnIndex("GetType"));
                trackingItem.getMethod = (byte) cursor.getInt(cursor.getColumnIndex("GetMethod"));
                if (!cursor.isNull(cursor.getColumnIndex("CellID"))) {
                    trackingItem.cellInfo = new CellInfo();
                    trackingItem.cellInfo.cellID = cursor.getInt(cursor.getColumnIndex("CellID"));
                    trackingItem.cellInfo.LAC = cursor.getInt(cursor.getColumnIndex("LAC"));
                    trackingItem.cellInfo.MCC = cursor.getInt(cursor.getColumnIndex("MCC"));
                    trackingItem.cellInfo.MNC = cursor.getInt(cursor.getColumnIndex("MNC"));
                }
                trackingItem.batteryLevel = cursor.getInt(cursor.getColumnIndex("BatteryLevel"));
                result[i] = trackingItem;
                cursor.moveToNext();
            }
        } catch (Exception ex) {
            return new TrackingItem[0];
        }
        return result;
    }

    public synchronized boolean setAckLocationID(int id) {
        try {
            ContentValues cv = new ContentValues();
            cv.put("Acked", 1);
            if (db.update(DbHelper.TABLE_NAME, cv, "RowID=" + id, null) > 0) {
                Model.inst().setConfigValue(Const.ConfigKeys.InitTrackingRowID, id);
                return true;
            }
        } catch (Exception ex) {
        }
        return false;
    }

    private int lastId = 0;

    public synchronized int getLastAckedId(boolean last) {
        try {
            Cursor cursor = db.rawQuery("select RowID from " + DbHelper.TABLE_NAME + (last ? " where Acked=1" : "") + " order by RowID " + (last ? "desc" : "asc") + " limit 1", null);
            if (cursor.getCount() == 1) {
                cursor.moveToFirst();
                return cursor.getInt(0);
            } else {
                if (!last)
                    return Model.inst().getConfigValue(Const.ConfigKeys.InitTrackingRowID, 0);
            }
        } catch (Exception ex) {
            Log.e("getLastAckedId", ex.toString());
        }
        return 0;
    }

    public synchronized boolean cleanTrackingRecords() {
        int lastAckedId = getLastAckedId(true);
        if (lastAckedId > 0) {
            try {
                int n = db.delete(DbHelper.TABLE_NAME, "RowID <= " + lastAckedId, null);
                Log.i("cleanTrackingRecords", n + " rows deleted");
                this.lastId = lastAckedId;
                return true;
            } catch (Exception ex) {
                Log.e("cleanTrackingRecords", ex.toString());
            }
        }
        return false;
    }

    private SQLiteDatabase db = null;
    private DbHelper dbHelper = null;

    public synchronized void updateCustomer(int id, Customer customer, int isSend) {
        //Cập nhật vị trí, hình ảnh
        try {
            ContentValues cv = new ContentValues();
            cv.put("ImageUrl", customer.imageUrl);
            cv.put("Longitude", customer.longitude);
            cv.put("Latitude", customer.latitude);
            cv.put("Working_Time",customer.workingtime);
            cv.put("IsSend", isSend);
            db.update(DbHelper.CUSTOMER_NAME, cv, "RowID=" + id, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void updateCustomer(int id, long time, int type) {
        //Cập nhật thời gian cuối ghé thăm , đặt hàng
        try {
            ContentValues cv = new ContentValues();
            switch (type) {
                case 0:
                    cv.put("Last_Visited", time);
                    break;
                case 1:
                    cv.put("Last_Order_Day", time);
                    break;
                default:
                    break;
            }

            int rowId = db.update(DbHelper.CUSTOMER_NAME, cv, "RowID=" + id, null);
            Log.w(TAG, "updateCustomer : " + rowId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized ArrayList<Route> loadRoute(int nowIdCustomer) {
        ArrayList<Route> result = new ArrayList<>();
        Route routeAll = new Route();
        routeAll.id = 100;
        routeAll.name = "Tất cả";
        routeAll.count = countCustomerByRoute(nowIdCustomer, routeAll.id);
        result.add(routeAll);
        for (int i = 0; i < 7; i++) {
            Route route = new Route();
            route.id = -i - 1;
            route.name = Const.Weekdays[i];
            route.count = countCustomerByRoute(nowIdCustomer, route.id);
            result.add(route);
        }
        return result;
    }

    public ArrayList<SurveyCampaign> getListCampaigns() {
        ArrayList<SurveyCampaign> result = new ArrayList<>();
        try {
            String query = "SELECT * FROM " + DbHelper.CAMPAIGN_NAME;
            Cursor c = db.rawQuery(query, null);
            if (c.moveToFirst()) {
                do {
                    SurveyCampaign campaign = new SurveyCampaign();
                    campaign.id = c.getInt(c.getColumnIndex("RowID"));
                    campaign.no_ = c.getString(c.getColumnIndex("No_"));
                    campaign.name = c.getString(c.getColumnIndex("Name"));
                    campaign.documentDate = c.getLong(c.getColumnIndex("DocumentDate"));
                    campaign.beginDate = c.getLong(c.getColumnIndex("BeginDate"));
                    campaign.endDate = c.getLong(c.getColumnIndex("EndDate"));
                    campaign.description = c.getString(c.getColumnIndex("Description"));
                    result.add(campaign);
                } while (c.moveToNext());
            }
            c.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public ArrayList<Order> getOrderUnsents() {
        ArrayList<Order> result = new ArrayList<>();
        try {
            String query = "SELECT * FROM " + DbHelper.ORDER_NAME + " WHERE IsSend = 0";
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    Order order = new Order();
                    order.no_ = cursor.getString(cursor.getColumnIndex("No_"));
                    order.name = cursor.getString(cursor.getColumnIndex("Name"));
                    order.time = cursor.getLong(cursor.getColumnIndex("Time"));
                    order.id_customer = cursor.getInt(cursor.getColumnIndex("IDCustomer"));
                    order.status = cursor.getInt(cursor.getColumnIndex("Status"));
                    order.note = cursor.getString(cursor.getColumnIndex("Note"));
                    order.amount = cursor.getFloat(cursor.getColumnIndex("Amount"));
                    order.id_company = cursor.getInt(cursor.getColumnIndex("IDCompany"));
                    order.id_region = cursor.getInt(cursor.getColumnIndex("IDRegion"));
                    order.id_branch = cursor.getInt(cursor.getColumnIndex("IDBranch"));
                    order.id_employee = cursor.getInt(cursor.getColumnIndex("IDEmployee"));
                    order.id_parent = cursor.getInt(cursor.getColumnIndex("IDParent"));
                    order.document_type = cursor.getInt(cursor.getColumnIndex("DocumentType"));
                    order.imageUrl = cursor.getString(cursor.getColumnIndex("ImageUrl"));
                    order.employeeName = cursor.getString(cursor.getColumnIndex("EmployeeName"));
                    order.orderDetails = loadOrderDetail(order.rowId);
                    result.add(order);
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {

        }
        return result;
    }

    public ArrayList<OrderDetail> getOrderDetails(List<Integer> idOrders) {
        ArrayList<OrderDetail> result = new ArrayList<>();
        try {
            for(int idOrder : idOrders)
            {
                result.addAll(loadOrderDetail(idOrder));
            }

        } catch (Exception e) {

        }
        return result;
    }

    public SurveyCampaign getListCampaigns(int id) {
        ArrayList<SurveyCampaign> result = new ArrayList<>();
        try {
            String query = "SELECT * FROM " + DbHelper.CAMPAIGN_NAME + " WHERE RowID = " + id;
            Cursor c = db.rawQuery(query, null);
            if (c.moveToFirst()) {
                do {
                    SurveyCampaign campaign = new SurveyCampaign();
                    campaign.id = c.getInt(c.getColumnIndex("RowID"));
                    campaign.no_ = c.getString(c.getColumnIndex("No_"));
                    campaign.name = c.getString(c.getColumnIndex("Name"));
                    campaign.documentDate = c.getLong(c.getColumnIndex("DocumentDate"));
                    campaign.beginDate = c.getLong(c.getColumnIndex("BeginDate"));
                    campaign.endDate = c.getLong(c.getColumnIndex("EndDate"));
                    campaign.description = c.getString(c.getColumnIndex("Description"));
                    result.add(campaign);
                } while (c.moveToNext());
            }
            c.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.get(0);
    }

    public void createSyncIfNotExist() {
        try {
            db.execSQL(dbHelper.SQL_CREATE_SYNCS_EXIST);
            db.execSQL(dbHelper.SQL_CREATE_CUSTOMERS_EXIST);
            db.execSQL(dbHelper.SQL_CREATE_PRODUCTS_EXIST);
            db.execSQL(dbHelper.SQL_CREATE_USER_EXIST);
            Log.w(TAG, "createSyncIfNotExist: success");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createSurveyIfNotExist() {
        try {
            db.execSQL(dbHelper.SQL_CREATE_CAMPAIGN_EXIST);
            db.execSQL(dbHelper.SQL_CREATE_HEADER_EXIST);
            db.execSQL(dbHelper.SQL_CREATE_LINE_EXIST);
            db.execSQL(dbHelper.SQL_CREATE_RESULT_EXIST);

            Log.w(TAG, "createSurveyIfNotExist: success");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<SurveyHeader> loadSurveyHeader(int id) {
        ArrayList<SurveyHeader> result = new ArrayList<>();
        try {
            String query = "SELECT * FROM " + DbHelper.HEADER_NAME + " WHERE ID_Campaign = " + id;
            Cursor c = db.rawQuery(query, null);
            if (c.moveToFirst()) {
                do {
                    SurveyHeader header = new SurveyHeader();
                    header.id = c.getInt(c.getColumnIndex("RowID"));
                    header.sort = c.getInt(c.getColumnIndex("Sort"));
                    header.name = c.getString(c.getColumnIndex("Name"));
                    header.description = c.getString(c.getColumnIndex("Description"));
                    header.type = c.getInt(c.getColumnIndex("Type"));
                    header.id_campaign = c.getInt(c.getColumnIndex("ID_Campaign"));
                    header.status = c.getInt(c.getColumnIndex("Status"));
                    header.requireField = c.getInt(c.getColumnIndex("RequireField"));
                    result.add(header);
                } while (c.moveToNext());
            }
            c.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public ArrayList<SurveyLine> loadSurveyLine(List<Integer> ids, int id_Customer) {
        ArrayList<SurveyLine> result = new ArrayList<>();
        for (int i : ids) {
            try {
                String query = "SELECT * FROM " + DbHelper.LINE_NAME + " WHERE ID_Survey = " + i;
                Cursor c = db.rawQuery(query, null);
                if (c.moveToFirst()) {
                    do {
                        SurveyLine line = new SurveyLine();
                        line.id = c.getInt(c.getColumnIndex("RowID"));
                        line.id_survey = c.getInt(c.getColumnIndex("ID_Survey"));
                        line.refNo_ = c.getString(c.getColumnIndex("RefNo_"));
                        line.sort = c.getInt(c.getColumnIndex("Sort"));
                        line.question = c.getString(c.getColumnIndex("Question"));
                        line.type = c.getInt(c.getColumnIndex("Type"));
                        line.status = c.getInt(c.getColumnIndex("Status"));
                        String queryResult = "SELECT Answer FROM " + DbHelper.RESULT_NAME + " WHERE SourceID = " + id_Customer + " AND ID_SurveyLine = " + line.id;
                        Cursor cResult = null;
                        try {
                            cResult = db.rawQuery(queryResult, null);
                            if (cResult.getCount() > 0) {

                                cResult.moveToFirst();
                                line.result = cResult.getString(cResult.getColumnIndex("Answer"));
                            } else {
                                line.result = "";
                            }
                        } finally {

                            cResult.close();
                        }
                        result.add(line);
                    } while (c.moveToNext());
                }
                c.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return result;
    }


    public ArrayList<SurveyLine> loadSurveyLine(int i) {
        ArrayList<SurveyLine> result = new ArrayList<>();
        try {
            String query = "SELECT * FROM " + DbHelper.LINE_NAME + " WHERE ID_Survey = " + i;
            Cursor c = db.rawQuery(query, null);
            if (c.moveToFirst()) {
                do {
                    SurveyLine line = new SurveyLine();
                    line.id = c.getInt(c.getColumnIndex("RowID"));
                    line.id_survey = c.getInt(c.getColumnIndex("ID_Survey"));
                    line.refNo_ = c.getString(c.getColumnIndex("RefNo_"));
                    line.sort = c.getInt(c.getColumnIndex("Sort"));
                    line.question = c.getString(c.getColumnIndex("Question"));
                    line.type = c.getInt(c.getColumnIndex("Type"));
                    line.status = c.getInt(c.getColumnIndex("Status"));
                    line.result = c.getString(c.getColumnIndex("Result"));
                    result.add(line);
                } while (c.moveToNext());
            }
            c.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }


    public ArrayList<TransactionLine> getTransactionUnsent() {
        ArrayList<TransactionLine> result = new ArrayList<>();
        try {
            Cursor c = db.rawQuery("SELECT * FROM " + DbHelper.TRANSACTION_LINE_NAME + " WHERE isSend = 0", null);
            if (c.moveToFirst()) {
                do {
                    TransactionLine t = new TransactionLine();
                    t.urlImage = c.getInt(c.getColumnIndex("UrlImage"));
                    t.id_customer = c.getInt(c.getColumnIndex("IDCustomer"));
                    t.id_transaction = c.getInt(c.getColumnIndex("IDTransaction"));
                    t.note = c.getString(c.getColumnIndex("Note"));
                    t.location_ref_id = c.getInt(c.getColumnIndex("LocationRefID"));
                    t.status = c.getInt(c.getColumnIndex("Status"));
                    t.userid = c.getString(c.getColumnIndex("UserID"));
                    t.id_employee = c.getInt(c.getColumnIndex("IDEmployee"));
                    t.create_date = c.getLong(c.getColumnIndex("CreateDate"));
                    t.modified_date = c.getLong(c.getColumnIndex("ModifiedDate"));
                    t.latitude = c.getDouble(c.getColumnIndex("Latitude"));
                    t.longitude = c.getDouble(c.getColumnIndex("Longitude"));
                    t.id_ExtNo_ = c.getInt(c.getColumnIndex("IDExtNo"));
                    t.id_transaction_define = c.getInt(c.getColumnIndex("IDTransactionDefine"));
                    t.name_employee = c.getString(c.getColumnIndex("NameEmployee"));
                    t.refNo_ = c.getString(c.getColumnIndex("RefNo_"));
                    if (t.id_transaction_define != Const.TransactionType.Survey.getValue())
                        result.add(t);
                } while (c.moveToNext());
            }
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public Customer getCustomer(int id_customer) {
        Customer customer = new Customer();

        try {
            Cursor c = db.rawQuery("SELECT * FROM " + DbHelper.CUSTOMER_NAME + " WHERE RowID = "+id_customer, null);
            if (c.moveToFirst()) {
                do {
                    customer.id = c.getInt(c.getColumnIndex("RowID"));
                    customer.no_ = c.getString(c.getColumnIndex("No_"));
                    customer.name = c.getString(c.getColumnIndex("Name"));
                    customer.address = c.getString(c.getColumnIndex("Address"));
                    customer.phoneNumber = c.getString(c.getColumnIndex("PhoneNumber"));
                    customer.imageUrl = c.getString(c.getColumnIndex("ImageUrl"));
                    customer.longitude = c.getFloat(c.getColumnIndex("Longitude"));
                    customer.latitude = c.getFloat(c.getColumnIndex("Latitude"));
                    customer.city = c.getInt(c.getColumnIndex("ID_City"));
                    customer.county = c.getInt(c.getColumnIndex("ID_County"));
                    customer.distance = c.getInt(c.getColumnIndex("Distance"));
                    customer.amount_last_month = c.getFloat(c.getColumnIndex("Amount_Last_Month"));
                    customer.last_order_day = c.getLong(c.getColumnIndex("Last_Order_Day"));
                    customer.last_visited = c.getLong(c.getColumnIndex("Last_Visited"));
                    customer.workingtime = c.getInt(c.getColumnIndex("Working_Time"));

                } while (c.moveToNext());
            }
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return customer;
    }
    public ArrayList<Customer> getCustomerUnsent() {
        ArrayList<Customer> result = new ArrayList<>();
        try {
            Cursor c = db.rawQuery("SELECT * FROM " + DbHelper.CUSTOMER_NAME + " WHERE IsSend = 0", null);
            if (c.moveToFirst()) {
                do {
                    Customer customer = new Customer();
                    customer.id = c.getInt(c.getColumnIndex("RowID"));
                    customer.no_ = c.getString(c.getColumnIndex("No_"));
                    customer.name = c.getString(c.getColumnIndex("Name"));
                    customer.address = c.getString(c.getColumnIndex("Address"));
                    customer.phoneNumber = c.getString(c.getColumnIndex("PhoneNumber"));
                    customer.imageUrl = c.getString(c.getColumnIndex("ImageUrl"));
                    customer.longitude = c.getFloat(c.getColumnIndex("Longitude"));
                    customer.latitude = c.getFloat(c.getColumnIndex("Latitude"));
                    customer.city = c.getInt(c.getColumnIndex("ID_City"));
                    customer.county = c.getInt(c.getColumnIndex("ID_County"));
                    customer.distance = c.getInt(c.getColumnIndex("Distance"));
                    customer.amount_last_month = c.getFloat(c.getColumnIndex("Amount_Last_Month"));
                    customer.last_order_day = c.getLong(c.getColumnIndex("Last_Order_Day"));
                    customer.last_visited = c.getLong(c.getColumnIndex("Last_Visited"));
                    customer.workingtime = c.getInt(c.getColumnIndex("Working_Time"));
                    result.add(customer);
                } while (c.moveToNext());
            }
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    public ArrayList<Order> loadOrder(int lastId, String filter) {
        ArrayList<Order> result = new ArrayList<>();
        try {
            String query = "SELECT * FROM " + DbHelper.ORDER_NAME
                    + " WHERE (RowID < " + lastId + " OR " + lastId + "= -1) AND SearchField LIKE '%" + Utils.rtrim(filter) + "%'"
                    + " ORDER BY ROWID DESC";
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    Order order = new Order();
                    order.no_ = cursor.getString(cursor.getColumnIndex("No_"));
                    order.name = cursor.getString(cursor.getColumnIndex("Name"));
                    order.time = cursor.getLong(cursor.getColumnIndex("Time"));
                    order.id_customer = cursor.getInt(cursor.getColumnIndex("IDCustomer"));
                    order.status = cursor.getInt(cursor.getColumnIndex("Status"));
                    order.note = cursor.getString(cursor.getColumnIndex("Note"));
                    order.amount = cursor.getFloat(cursor.getColumnIndex("Amount"));
                    order.id_company = cursor.getInt(cursor.getColumnIndex("IDCompany"));
                    order.id_region = cursor.getInt(cursor.getColumnIndex("IDRegion"));
                    order.id_branch = cursor.getInt(cursor.getColumnIndex("IDBranch"));
                    order.id_employee = cursor.getInt(cursor.getColumnIndex("IDEmployee"));
                    order.id_parent = cursor.getInt(cursor.getColumnIndex("IDParent"));
                    order.document_type = cursor.getInt(cursor.getColumnIndex("DocumentType"));
                    order.imageUrl = cursor.getString(cursor.getColumnIndex("ImageUrl"));
                    order.employeeName = cursor.getString(cursor.getColumnIndex("Employee"));

                    result.add(order);
                } while (cursor.moveToNext());

            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public ArrayList<OrderDetail> loadOrderDetail(int idOrder) {
        ArrayList<OrderDetail> result = new ArrayList<>();
        try {
            String query = "SELECT * FROM " + DbHelper.ORDER_DETAIL_NAME + " WHERE IDOrder = " + idOrder;
            Cursor c = db.rawQuery(query, null);
            if (c.moveToFirst()) {
                do {
                    OrderDetail orderDetail = new OrderDetail();
                    orderDetail.no_ = c.getString(c.getColumnIndex("No_"));
                    orderDetail.id_item = c.getInt(c.getColumnIndex("IDItem"));
                    orderDetail.itemNo_ = c.getString(c.getColumnIndex("ItemNo_"));
                    orderDetail.name = c.getString(c.getColumnIndex("Name"));
                    orderDetail.quantity = c.getInt(c.getColumnIndex("Quantity"));
                    orderDetail.unitprice = c.getFloat(c.getColumnIndex("UnitPrice"));
                    orderDetail.discountPercent = c.getFloat(c.getColumnIndex("DiscountPercent"));
                    orderDetail.discountAmount = c.getFloat(c.getColumnIndex("DiscountAmount"));
                    orderDetail.itemType = c.getInt(c.getColumnIndex("ItemType"));
                    orderDetail.note = c.getString(c.getColumnIndex("Note"));
                    orderDetail.status = c.getInt(c.getColumnIndex("Status"));
                    result.add(orderDetail);
                } while (c.moveToNext());

            }
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public ArrayList<SurveyResult> getResultUnsent() {
        ArrayList<SurveyResult> result = new ArrayList<>();
        try {
            Cursor c = db.rawQuery("SELECT * FROM " + DbHelper.RESULT_NAME + " WHERE isSend = 0", null);
            if (c.moveToFirst()) {
                do {
                    SurveyResult sr = new SurveyResult();
                    sr.id_customer = c.getInt(c.getColumnIndex("SourceID"));
                    sr.id_survey_line = c.getInt(c.getColumnIndex("ID_SurveyLine"));
                    sr.answer = c.getString(c.getColumnIndex("Answer"));
                    sr.status = c.getInt(c.getColumnIndex("Status"));
                    if (sr.answer.contains(".jpg")) {
                        BitmapFactory.Options options;
                        try {
                            options = new BitmapFactory.Options();
                            options.inSampleSize = 16;// 1/16 of origin image size from width and height
                            sr.imageThumb = BitmapFactory.decodeFile(sr.answer, options);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    result.add(sr);
                } while (c.moveToNext());
            }
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public int countTransactionUnSent() {
        int count = 0;
        try {
            Cursor c = db.rawQuery("SELECT Count(RowID) FROM " + DbHelper.TRANSACTION_LINE_NAME + " WHERE isSend = 0 ", null);
            c.moveToFirst();
            count += c.getInt(0);
            c.close();
            return count;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    public int countCustomerUnSent() {
        int count = 0;

        try {
            Cursor c = db.rawQuery("SELECT Count(RowID) FROM " + DbHelper.CUSTOMER_NAME + " WHERE isSend = 0 ", null);
            c.moveToFirst();
            count += c.getInt(0);
            c.close();
            return count;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return count;
    }

    public int countResultUnSent() {
        int count = 0;

        try {
            Cursor c = db.rawQuery("SELECT Count(DISTINCT SourceID) FROM " + DbHelper.RESULT_NAME + " WHERE isSend = 0 ", null);
            c.moveToFirst();
            count += c.getInt(0);
            c.close();
            return count;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    public int countResultUnSent(int id_campain) {
        //Count Result Unsend by id_campaign

        int count = 0;
        try {
            Cursor c = db.rawQuery("SELECT Count(DISTINCT SourceID) FROM " + DbHelper.RESULT_NAME + " R"
                    + " inner join " + DbHelper.LINE_NAME + " L on R.ID_SurveyLine = L.RowID "
                    + " inner join " + dbHelper.HEADER_NAME + " H on L.ID_Survey = H.RowID"
                    + " inner join " + dbHelper.CAMPAIGN_NAME + " C on H.ID_Campaign = C.RowID"
                    + " WHERE R.isSend = 0 AND C.RowID = " + id_campain, null);
            c.moveToFirst();

            count += c.getInt(0);


            c.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return count;


    }


    public int countOrderUnSent() {
        int count = 0;
        try {
            Cursor c = db.rawQuery("SELECT Count(RowID) FROM " + DbHelper.ORDER_NAME + " WHERE IsSend = 0", null);
            c.moveToFirst();
            count += c.getInt(0);
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    public void updateResult(int status) {
        try {
            ContentValues cv = new ContentValues();
            cv.put("isSend", status);
            long row = db.update(DbHelper.RESULT_NAME, cv, null, null);
            Log.w(TAG, "updateResult: " + row + " success");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    private class DbHelper extends SQLiteOpenHelper {
        // If you change the database schema, you must increment the database version.
        public static final int DATABASE_VERSION = 9;
        public static final String DATABASE_NAME = "edms_local.db";
        public static final String TABLE_NAME = "tblTracking";
        public static final String CONFIG_NAME = "tblConfig";
        public static final String CUSTOMER_NAME = "tblCustomer";
        public static final String PRODUCT_NAME = "tblProduct";
        public static final String ORDER_NAME = "tblOrder";
        public static final String ORDER_DETAIL_NAME = "tblOrderDetail";
        public static final String USER_NAME = "tblUser";
        public static final String TRANSACTION_LINE_NAME = "tblTransactionLine";
        public static final String REASON_NAME = "tblReason";
        public static final String HEADER_NAME = "tblHeader";
        public static final String LINE_NAME = "tblLine";
        public static final String CAMPAIGN_NAME = "tblCampaign";
        public static final String RESULT_NAME = "tblResult";
        public static final String SYNC_NAME = "tblSync";
        public static final String PROMOTION_HEADER_NAME = "tblPromotionHeader";
        public static final String PROMOTION_DETAIL_NAME = "tblPromotionDetail";
        public static final String SQL_CREATE_CUSTOMERS_EXIST = "CREATE TABLE IF NOT EXISTS " + CUSTOMER_NAME + " ("
                + "RowID integer primary key autoincrement"
                + ",No_ nvarchar(30)"
                + ",Name nvarchar(500)"
                + ",Address nvarchar(500)"
                + ",PhoneNumber nvarchar(60)"
                + ",ImageUrl nvarchar(300)"
                + ",Longitude float"
                + ",Latitude float"
                + ",ID_City int"
                + ",ID_County int"
                + ",Distance int"
                + ",Amount_Last_Month float"
                + ",Last_Order_Day bigint"
                + ",Last_Visited bigint"
                + ",Working_Time int"
                + ",SearchField nvarchar(500)"
                + ",IsSend int"
                + ");";
        public static final String SQL_CREATE_TRANSACTION_LINE_EXIST = "CREATE TABLE IF NOT EXISTS " + TRANSACTION_LINE_NAME + " ("
                + "RowID integer primary key autoincrement"
                + ",UrlImage int"
                + ",IDCustomer int"
                + ",IDTransaction int"
                + ",Note nvarchar(300)"
                + ",LocationRefID int"
                + ",Status int"
                + ",UserID nvarchar(300)"
                + ",IDEmployee int"
                + ",CreateDate bigint"
                + ",ModifiedDate bigint"
                + ",Latitude float"
                + ",Longitude float"
                + ",IDExtNo int"
                + ",IDTransactionDefine int"
                + ",NameEmployee nvarchar(500)"
                + ",RefNo_ nvarchar(100)"
                + ",IsSend int"
                + ");";
        public static final String SQL_CREATE_REASON_EXIST = "CREATE TABLE IF NOT EXISTS " + REASON_NAME + " ("
                + "RowID integer primary key autoincrement"
                + ",Description nvarchar(500)"
                + ",Type int"
                + ",IDCompany int"
                + ");";
        public static final String SQL_CREATE_HEADER_EXIST = "create table if not exists " + HEADER_NAME + " ("
                + "RowID integer primary key autoincrement"
                + ",Sort integer"
                + ",Name nvarchar(50)"
                + ",Description nvarchar(50)"
                + ",Type integer"
                + ",ID_Campaign integer"
                + ",Status integer"
                + ",RequireField integer"
                + ");";
        public static final String SQL_CREATE_LINE_EXIST = "create table if not exists " + LINE_NAME + " ("
                + "RowID integer primary key autoincrement"
                + ",ID_Survey integer"
                + ",RefNo_ nvarchar(50)"
                + ",Sort integer"
                + ",Question nvarchar(50)"
                + ",Type integer"
                + ",Result nvarchar(50)"
                + ",Status integer"
                + ");";
        public static final String SQL_CREATE_CAMPAIGN_EXIST = "create table if not exists " + CAMPAIGN_NAME + " ("
                + "RowID integer primary key autoincrement"
                + ",No_ nvarchar(50)"
                + ",Name nvarchar(50)"
                + ",DocumentDate bigint"
                + ",BeginDate bigint"
                + ",EndDate bigint"
                + ",Description nvarchar(50)"
                + ");";
        public static final String SQL_CREATE_RESULT_EXIST = "create table if not exists " + RESULT_NAME + " ("
                + "RowID integer primary key autoincrement"
                + ",SourceID integer"
                + ",ID_SurveyLine integer"
                + ",Answer nvarchar(500)"
                + ",Status integer"
                + ",DocumentDate bigint"
                + ",isSend int"
                + ");";
        public static final String SQL_CREATE_USER_EXIST = "CREATE TABLE IF NOT EXISTS " + USER_NAME + " ("
                + "RowID integer primary key autoincrement"
                + ",IDEmployee int"
                + ",FullName nvarchar(200)"
                + ",Latitude float"
                + ",Longitude float"
                + ",Accuracy float"
                + ",LocationDate bigint"
                + ",TrackingDate bigint"
                + ",Address nvarchar(500)"
                + ",IsWifi byte"
                + ",Is3G byte"
                + ",IsGPS byte"
                + ",IsAirplaneMode byte"
                + ",BatteryLevel int"
                + ");";
        public static final String SQL_CREATE_PRODUCTS_EXIST = "CREATE TABLE IF NOT EXISTS " + PRODUCT_NAME + " ("
                + "RowID integer primary key autoincrement"
                + ",No_ nvarchar(30)"
                + ",Name nvarchar(50)"
                + ",Unit nvarchar(50)"
                + ",Price float"
                + ",ImageUrl nvarchar(300)"
                + ",Description nvarchar(500)"
                + ",ID_ProductGroup int"
                + ",Name_ProductGroup nvarchar(50)"
                + ",Inventory int"
                + ",SearchField nvarchar(50)"
                + ");";
        public static final String SQL_CREATE_ORDERS_EXIST = "CREATE TABLE IF NOT EXISTS " + ORDER_NAME + " ("
                + "RowID integer primary key autoincrement"
                + ",No_ nvarchar(30)"
                + ",Name nvarchar(500)"
                + ",Time long"
                + ",IDCustomer int"
                + ",Status int"
                + ",Note nvarchar(500)"
                + ",Amount float"
                + ",IDCompany int"
                + ",IDRegion int"
                + ",IDBranch int"
                + ",IDEmployee int"
                + ",IDParent int"
                + ",DocumentType int"
                + ",ImageUrl nvarchar(300)"
                + ",EmployeeName nvarchar(500)"
                + ",SearchField nvarchar(500)"
                + ",IsSend int"
                + ");";
        public static final String SQL_CREATE_ORDER_DETAILS_EXIST = "CREATE TABLE IF NOT EXISTS " + ORDER_DETAIL_NAME + " ("
                + "RowID integer primary key autoincrement"
                + ",IDOrder int"
                + ",No_ nvarchar(30)"
                + ",IDItem int"
                + ",ItemNo_ nvarchar(100)"
                + ",Name nvarchar(500)"
                + ",Quantity int"
                + ",UnitPrice float"
                + ",DiscountPercent float"
                + ",DiscountAmount float"
                + ",ItemType int"
                + ",Note nvarchar(500)"
                + ",Status int"
                + ");";

        public static final String SQL_CREATE_PROMOTION_HEADER_EXIST = "CREATE TABLE IF NOT EXISTS " + PROMOTION_HEADER_NAME + " ("
                + "RowID integer primary key"
                + ",No_ nvarchar(50)"
                + ",isGroup int"
                + ",itemNo_ nvarchar(50)"
                + ",Ref_Item nvarchar(50)"
                + ",KyHieu nvarchar(50)"
                + ",HeaderNo_ nvarchar(50)"
                + ",HeaderName nvarchar(50)"
                + ");";
        public static final String SQL_CREATE_PROMOTION_DETAIL_EXIST = "CREATE TABLE IF NOT EXISTS " + PROMOTION_DETAIL_NAME + " ("
                + "RowID integer primary key"
                + ",PromotionNo_ nvarchar(50)"
                + ",PromotionDescription nvarchar(50)"
                + ",isDiscountMore int"
                + ");";

        public static final String SQL_CREATE_SYNCS_EXIST = "CREATE TABLE IF NOT EXISTS " + SYNC_NAME + " ("
                + "RowID integer primary key autoincrement"
                + ",Time long"
                + ");";
        public static final String SQL_CREATE_ENTRIES_EXIST = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                + "RowID integer primary key autoincrement"
                + ",DeviceID nvarchar(30)"
                + ",VisitedID nvarchar(30)"
                + ",VisitedType tinyint"
                + ",Latitude float"
                + ",Longitude float"
                + ",Accuracy float"
                + ",Speed float"
                + ",DistanceMeter int"
                + ",MilisecElapsed int"
                + ",LocationDate bigint"
                + ",TrackingDate bigint"
                + ",IsWifi bit"
                + ",Is3G bit"
                + ",IsAirplaneMode bit"
                + ",IsGPS bit"
                + ",Note nvarchar(250)"
                + ",GetType tinyint"
                + ",GetMethod tinyint"
                + ",CellID int"
                + ",LAC int"
                + ",MCC int"
                + ",MNC int"
                + ",BatteryLevel int"
                + ",Acked tinyint"
                + ");";
        public static final String SQL_CREATE_CONFIG_EXIST = "CREATE TABLE IF NOT EXISTS " + CONFIG_NAME + " ("
                + "RowID integer primary key autoincrement"
                + ",Key integer unique not null"
                + ",Value nvarchar(550) not null"
                + ");";
        public static final String SQL_DELETE_ENTRIES = "drop table if exists " + TABLE_NAME + ";";
        public static final String SQL_DELETE_CONFIG = "drop table if exists " + CONFIG_NAME + ";";
        public static final String SQL_DELETE_CUSTOMER = "drop table if exists " + CUSTOMER_NAME + ";";
        public static final String SQL_DELETE_PRODUCT = "drop table if exists " + PRODUCT_NAME + ";";
        public static final String SQL_DELETE_SYNC = "drop table if exists " + SYNC_NAME + ";";
        public static final String SQL_DELETE_PROMOTION_HEADER = "drop table if exists " + PROMOTION_HEADER_NAME + ";";
        public static final String SQL_DELETE_PROMOTION_DETAIL = "drop table if exists " + PROMOTION_DETAIL_NAME + ";";

        public DbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES_EXIST);
            db.execSQL(SQL_CREATE_CONFIG_EXIST);
            db.execSQL(SQL_CREATE_CUSTOMERS_EXIST);
            db.execSQL(SQL_CREATE_PRODUCTS_EXIST);
            db.execSQL(SQL_CREATE_TRANSACTION_LINE_EXIST);
            db.execSQL(SQL_CREATE_REASON_EXIST);
            db.execSQL(SQL_CREATE_USER_EXIST);
            db.execSQL(SQL_CREATE_SYNCS_EXIST);
            db.execSQL(SQL_CREATE_CAMPAIGN_EXIST);
            db.execSQL(SQL_CREATE_HEADER_EXIST);
            db.execSQL(SQL_CREATE_LINE_EXIST);
            db.execSQL(SQL_CREATE_RESULT_EXIST);
            db.execSQL(SQL_CREATE_ORDERS_EXIST);
            db.execSQL(SQL_CREATE_ORDER_DETAILS_EXIST);
            db.execSQL(SQL_CREATE_PROMOTION_HEADER_EXIST);
            db.execSQL(SQL_CREATE_PROMOTION_DETAIL_EXIST);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            Log.i("DbHelper", "Change version from " + oldVersion + " to " + newVersion);
            db.execSQL(SQL_DELETE_ENTRIES);
            db.execSQL(SQL_DELETE_CONFIG);
            db.execSQL(SQL_DELETE_CUSTOMER);
            db.execSQL(SQL_DELETE_PRODUCT);
            db.execSQL(SQL_DELETE_SYNC);
            db.execSQL(SQL_DELETE_PROMOTION_HEADER);
            db.execSQL(SQL_DELETE_PROMOTION_DETAIL);
            onCreate(db);
        }

        @Override
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }

}
