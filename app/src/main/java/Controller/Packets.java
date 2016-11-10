package Controller;

import android.graphics.Bitmap;

import com.vietdms.mobile.dmslauncher.MyMethod;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import CommonLib.ApproVal;
import CommonLib.BI_ReportCompare;
import CommonLib.CellInfo;
import CommonLib.City;
import CommonLib.Const;
import CommonLib.County;
import CommonLib.Customer;
import CommonLib.IdStatus;
import CommonLib.LibraryDetail;
import CommonLib.LibraryGroup;
import CommonLib.LocationVisited;
import CommonLib.Model;
import CommonLib.MyLocation;
import CommonLib.Order;
import CommonLib.OrderDeliveryItem;
import CommonLib.OrderDetail;
import CommonLib.PhoneState;
import CommonLib.Product;
import CommonLib.ProductGroup;
import CommonLib.PromotionDetail;
import CommonLib.PromotionHeader;
import CommonLib.ReasonNotOrder;
import CommonLib.ReportCheckIn;
import CommonLib.Route;
import CommonLib.Status;
import CommonLib.SurveyCampaign;
import CommonLib.SurveyHeader;
import CommonLib.SurveyLine;
import CommonLib.SurveyResult;
import CommonLib.SystemLogItem;
import CommonLib.TimeLine;
import CommonLib.TrackingItem;
import CommonLib.Transaction;
import CommonLib.TransactionLine;
import CommonLib.UserInfo;
import CommonLib.Utils;

/**
 * Created by My PC on 19/12/2015.
 */
abstract class Packets {
    public static abstract class FromServer {
        public static class Packet {
            protected final Inflater inflater;

            public Packet(byte[] data) throws Exception {
                if (data == null || data.length < 4) {
                    throw new Exception("Invalid packet header");
                }
                int size = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).getInt();
                if (size < 0 || size > data.length - 4) {
                    throw new Exception("Invalid packet body");
                }
                inflater = new Inflater();
                inflater.setInput(data, 4, size);
            }

            protected boolean readBool() throws Exception {
                byte[] data = new byte[1];
                if (inflater.inflate(data) != data.length)
                    throw new Exception("Invalid packet data");
                return data[0] != 0;
            }

            protected byte readByte() throws Exception {
                byte[] data = new byte[1];
                if (inflater.inflate(data) != data.length)
                    throw new Exception("Invalid packet data");
                return data[0];
            }

            protected byte[] readBytes() throws Exception {
                int len = readInt();
                byte[] data = new byte[len];
                if (len > 0) {
                    if (inflater.inflate(data) != data.length)
                        throw new Exception("Invalid packet data");
                }
                return data;
            }

            protected short readShort() throws Exception {
                byte[] data = new byte[2];
                if (inflater.inflate(data) != data.length)
                    throw new Exception("Invalid packet data");
                return ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).getShort();
            }

            protected int readInt() throws Exception {
                byte[] data = new byte[4];
                if (inflater.inflate(data) != data.length)
                    throw new Exception("Invalid packet data");
                return ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).getInt();
            }

            protected long readLong() throws Exception {
                byte[] data = new byte[8];
                if (inflater.inflate(data) != data.length)
                    throw new Exception("Invalid packet data");
                return ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).getLong();
            }

            protected float readFloat() throws Exception {
                byte[] data = new byte[4];
                if (inflater.inflate(data) != data.length)
                    throw new Exception("Invalid packet data");
                return ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).getFloat();
            }

            protected double readDouble() throws Exception {
                byte[] data = new byte[8];
                if (inflater.inflate(data) != data.length)
                    throw new Exception("Invalid packet data");
                return ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).getDouble();
            }

            protected String readString() throws Exception {
                short len = readShort();
                String result = "";
                try {
                    if (len < 0) {
                        len = (short) -(len * 2);
                        byte[] data = new byte[len];
                        if (inflater.inflate(data) != data.length)
                            throw new Exception("Invalid packet data");
                        result = new String(data, "UTF-16LE");
                    } else if (len > 0) {
                        byte[] data = new byte[len];
                        if (inflater.inflate(data) != data.length)
                            throw new Exception("Invalid packet data");
                        result = new String(data, "US-ASCII");
                    }
                } catch (UnsupportedEncodingException ex) {
                }
                return result;
            }
        }

        public static class PacketIntAck extends Packet {
            public PacketIntAck(byte[] data) throws Exception {
                super(data);
                intValue = readInt();
                inflater.end();
            }

            public final int intValue;
        }

        public static class PacketArrayIntsAck extends Packet {
            public PacketArrayIntsAck(byte[] data) throws Exception {
                super(data);
                int len = readInt();
                intValues = new int[len];
                for (int i = 0; i < len; i++) {
                    intValues[i] = readInt();
                }
                inflater.end();
            }

            public final int[] intValues;
        }

        public static class PacketSystemLog extends Packet {
            public PacketSystemLog(byte[] data) throws Exception {
                super(data);
                int len = readInt();
                intValues = new int[len];
                for (int i = 0; i < len; i++) {
                    intValues[i] = readInt();
                }
                len = readInt();
                for (int i = 0; i < len; i++) {
                    int key = readInt();
                    String value = readString();
                    for (int j = 0; j < Const.ConfigKeys.values().length; j++) {
                        if (Const.ConfigKeys.values()[j].ordinal() == key) {
                            map.put(Const.ConfigKeys.values()[j], value);
                            break;
                        }
                    }
                }
                inflater.end();
            }

            public final int[] intValues;
            public final HashMap<Const.ConfigKeys, String> map = new HashMap<Const.ConfigKeys, String>();
        }

        public static class PacketGetConfig extends Packet {
            public PacketGetConfig(byte[] data) throws Exception {
                super(data);
                int len = readInt();
                for (int i = 0; i < len; i++) {
                    int key = readInt();
                    String value = readString();
                    for (int j = 0; j < Const.ConfigKeys.values().length; j++) {
                        if (Const.ConfigKeys.values()[j].ordinal() == key) {
                            map.put(Const.ConfigKeys.values()[j], value);
                            break;
                        }
                    }
                }
                inflater.end();
            }

            public final HashMap<Const.ConfigKeys, String> map = new HashMap<Const.ConfigKeys, String>();
        }

        public static class PacketSendTracking extends Packet {
            public PacketSendTracking(byte[] data) throws Exception {
                super(data);
                valueAck = readInt();
                int len = readInt();
                for (int i = 0; i < len; i++) {
                    int key = readInt();
                    String value = readString();
                    for (int j = 0; j < Const.ConfigKeys.values().length; j++) {
                        if (Const.ConfigKeys.values()[j].ordinal() == key) {
                            map.put(Const.ConfigKeys.values()[j], value);
                            break;
                        }
                    }
                }
                inflater.end();
            }

            public final int valueAck;
            public final HashMap<Const.ConfigKeys, String> map = new HashMap<Const.ConfigKeys, String>();
        }

        public static class PacketLogin extends Packet {
            public PacketLogin(byte[] data) throws Exception {
                super(data);
                success = readBool();
                message = readString();
                int len = readInt();
                for (int i = 0; i < len; i++) {
                    int key = readInt();
                    String value = readString();
                    for (int j = 0; j < Const.ConfigKeys.values().length; j++) {
                        if (Const.ConfigKeys.values()[j].ordinal() == key) {
                            map.put(Const.ConfigKeys.values()[j], value);
                            break;
                        }
                    }
                }
                inflater.end();
            }

            public final boolean success;
            public final String message;
            public final HashMap<Const.ConfigKeys, String> map = new HashMap<Const.ConfigKeys, String>();
        }

        public static class PacketLoginRoute extends Packet {
            public PacketLoginRoute(byte[] data) throws Exception {
                super(data);
                success = readBool();
                message = readString();
                routeName = readString();
            }

            public boolean success;
            public String message;
            public String routeName;
        }

        public static class PacketGetLocations extends Packet {
            public PacketGetLocations(byte[] data) throws Exception {
                super(data);
                message = readString();
                int len = readInt();
                arrayLocations = new MyLocation[len];
                for (int i = 0; i < len; i++) {
                    MyLocation loc = new MyLocation();
                    loc.longitude = readDouble();
                    loc.latitude = readDouble();
                    loc.accuracy = readFloat();
                    loc.speed = readFloat();
                    loc.distanceMeter = readInt();
                    loc.milisecElapsed = readInt();
                    loc.milisecFreezed = readInt();
                    loc.locationDate = readLong();
                    loc.trackingDate = readLong();
                    loc.isWifi = readByte();
                    loc.is3G = readByte();
                    loc.isAirplaneMode = readByte();
                    loc.isGPS = readByte();
                    loc.batteryLevel = readInt();
                    boolean hasCellInfo = readBool();
                    if (hasCellInfo) {
                        loc.cellInfo = new CellInfo();
                        loc.cellInfo.cellID = readInt();
                        loc.cellInfo.LAC = readInt();
                        loc.cellInfo.MCC = readInt();
                        loc.cellInfo.MNC = readInt();
                    }
                    loc.address = readString();
                    loc.getType = readByte();
                    loc.getMethod = readByte();
                    if (loc.getMethod > 0) {
                        loc.note = readString();
                        loc.imageUrl = readString();
                        loc.imageThumb = Utils.decompressBitmap(readBytes());
                    }
                    arrayLocations[i] = loc;
                }
                inflater.end();
            }

            public String message;
            public MyLocation[] arrayLocations;
        }

        public static class PacketGetUsers extends Packet {
            public PacketGetUsers(byte[] data) throws Exception {
                super(data);
                message = readString();
                int len = readInt();
                arrayUsers = new UserInfo[len];
                for (int i = 0; i < len; i++) {
                    UserInfo user = new UserInfo();
                    user.id_employee = readInt();
                    user.fullname = readString();
                    user.latitude = readDouble();
                    user.longitude = readDouble();
                    user.accuracy = readFloat();
                    user.locationDate = readLong();
                    user.trackingDate = readLong();
                    user.address = readString();
                    user.isWifi = readByte();
                    user.is3G = readByte();
                    user.isGPS = readByte();
                    user.isAirplaneMode = readByte();
                    user.batteryLevel = readInt();
                    arrayUsers[i] = user;
                }
                inflater.end();
            }

            public String message;
            public UserInfo[] arrayUsers;
        }

        public static class PacketGetOrderDelivery extends Packet {
            public PacketGetOrderDelivery(byte[] data) throws Exception {
                super(data);
                message = readString();
                int len = readInt();
                arrayOrders = new OrderDeliveryItem[len];
                for (int i = 0; i < len; i++) {
                    OrderDeliveryItem item = new OrderDeliveryItem();
                    item.orderNo = readString();
                    item.nameAddress = readString();
                    item.phoneNumber = readString();
                    item.description = readString();
                    item.createdDate = readLong();
                    item.status = readByte();
                    item.orderPrice = readInt();
                    item.paymentMethod = readByte();
                    arrayOrders[i] = item;
                }
                inflater.end();
            }

            public String message;
            public OrderDeliveryItem[] arrayOrders;
        }

        public static class PacketResultWithMessage extends Packet {
            public PacketResultWithMessage(byte[] data) throws Exception {
                super(data);
                success = readBool();
                message = readString();
                inflater.end();
            }

            public boolean success;
            public String message;
        }

        public static class PacketSendOrders extends Packet {
            public PacketSendOrders(byte[] data) throws Exception {
                super(data);
                success = readBool();
                message = readString();
                int len = readInt();
                orderDetails = new ArrayList<>(len);
                for (int i = 0; i < len; i++) {
                    OrderDetail item = new OrderDetail();
                    item.itemNo_ = readString();
                    item.id_item = readInt();
                    item.name = readString();
                    item.quantity = readInt();
                    item.unitprice = readFloat();
                    item.status = readInt();
                    item.discountPercent = readFloat();
                    item.discountAmount = readFloat();
                    item.itemType = readInt();
                    item.note = readString();
                    item.loadNo_ = readString();
                    item.type = readInt();
                    item.promotionNo_ = readString();
                    orderDetails.add(item);
                }
                inflater.end();
            }

            public boolean success;
            public String message;
            public ArrayList<OrderDetail> orderDetails;
        }
        public static class PacketUpdateOrder extends Packet {
            public PacketUpdateOrder(byte[] data) throws Exception {
                super(data);
                success = readBool();
                message = readString();
                int len = readInt();
                orderDetails = new ArrayList<>(len);
                for (int i = 0; i < len; i++) {
                    OrderDetail item = new OrderDetail();
                    item.itemNo_ = readString();
                    item.id_item = readInt();
                    item.name = readString();
                    item.quantity = readInt();
                    item.unitprice = readFloat();
                    item.status = readInt();
                    item.discountPercent = readFloat();
                    item.discountAmount = readFloat();
                    item.itemType = readInt();
                    item.note = readString();
                    item.loadNo_ = readString();
                    item.type = readInt();
                    item.promotionNo_ = readString();
                    orderDetails.add(item);
                }
                inflater.end();
            }

            public boolean success;
            public String message;
            public ArrayList<OrderDetail> orderDetails;
        }


        public static class PacketAddCustomer extends Packet {
            public PacketAddCustomer(byte[] data) throws Exception {
                super(data);
                success = readBool();
                message = readString();
                rowID = readInt();
                inflater.end();
            }

            public boolean success;
            public String message;
            public int rowID;
        }

        public static class PacketLoadRoutes extends Packet {
            public PacketLoadRoutes(byte[] data) throws Exception {
                super(data);
                message = readString();
                int len = readInt();
                arrayRoutes = new ArrayList<>(len);
                for (int i = 0; i < len; i++) {
                    Route route = new Route();
                    route.id = readInt();
                    route.name = readString();
                    route.count = readInt();
                    arrayRoutes.add(route);
                }
                inflater.end();
            }

            public final String message;
            public final ArrayList<Route> arrayRoutes;
        }

        public static class PacketUpdateCustomer extends Packet {
            public PacketUpdateCustomer(byte[] data) throws Exception {
                super(data);
                message = readString();
                customer.id = readInt();
                customer.no_ = readString();
                customer.name = readString();
                customer.address = readString();
                customer.phoneNumber = readString();
                customer.imageUrl = readString();
                customer.imageThumb = Utils.decompressBitmap(readBytes());
                customer.longitude = readDouble();
                customer.latitude = readDouble();
                customer.distance = readInt();
                customer.amount_last_month = readFloat();
                customer.last_order_day = readLong();
                customer.last_visited = readLong();
                customer.workingtime = readInt();
                inflater.end();
            }

            public final String message;
            public final Customer customer = new Customer();
        }

        public static class PacketLoadAllCustomers extends Packet {
            public PacketLoadAllCustomers(byte[] data) throws Exception {
                super(data);
                message = readString();
                int len = readInt();
                arrayCustomers = new ArrayList<>(len);
                for (int i = 0; i < len; i++) {
                    Customer customer = new Customer();
                    customer.id = readInt();
                    customer.no_ = readString();
                    customer.name = readString();
                    customer.address = readString();
                    customer.phoneNumber = readString();
                    customer.imageUrl = readString();
                    customer.imageThumb = Utils.decompressBitmap(readBytes());
                    customer.longitude = readDouble();
                    customer.latitude = readDouble();
                    customer.distance = readInt();
                    arrayCustomers.add(customer);
                }
                inflater.end();
            }

            public final String message;
            public final ArrayList<Customer> arrayCustomers;

        }

        public static class PacketLoadCustomers extends Packet {
            public PacketLoadCustomers(byte[] data) throws Exception {
                super(data);
                message = readString();
                int len = readInt();
                arrayCustomers = new ArrayList<>(len);
                for (int i = 0; i < len; i++) {
                    Customer customer = new Customer();
                    customer.id = readInt();
                    customer.no_ = readString();
                    customer.name = readString();
                    customer.address = readString();
                    customer.phoneNumber = readString();
                    customer.imageUrl = readString();
                    customer.imageThumb = Utils.decompressBitmap(readBytes());
                    customer.longitude = readDouble();
                    customer.latitude = readDouble();
                    customer.distance = readInt();
                    arrayCustomers.add(customer);
                }
                inflater.end();
            }

            public final String message;
            public final ArrayList<Customer> arrayCustomers;
        }

        public static class PacketLoadProducts extends Packet {
            public PacketLoadProducts(byte[] data) throws Exception {
                super(data);
                message = readString();
                int len = readInt();
                arrayProducts = new ArrayList<>(len);
                for (int i = 0; i < len; i++) {
                    Product product = new Product();
                    product.id = readInt();
                    product.no_ = readString();
                    product.name = readString();
                    product.unit = readString();
                    product.price = readFloat();
                    product.imageUrl = readString();
                    product.imageThumb = Utils.decompressBitmap(readBytes());
                    product.description = readString();
                    product.id_ProductGroup = readInt();
                    product.name_ProductGroup = readString();
                    product.inventory = readInt();
                    arrayProducts.add(product);
                }
                inflater.end();
            }

            public final String message;
            public final ArrayList<Product> arrayProducts;
        }

        public static class PacketLoadInventoryEmployees extends Packet {
            public PacketLoadInventoryEmployees(byte[] data) throws Exception {
                super(data);
                message = readString();
                int len = readInt();
                arrayInventoryEmployees = new ArrayList<>(len);
                for (int i = 0; i < len; i++) {
                    Product product = new Product();
                    product.id = readInt();
                    product.no_ = readString();
                    product.name = readString();
                    product.unit = readString();
                    product.price = readFloat();
                    product.imageUrl = readString();
                    product.imageThumb = Utils.decompressBitmap(readBytes());
                    product.description = readString();
                    product.id_ProductGroup = readInt();
                    product.name_ProductGroup = readString();
                    product.inventory = readInt();
                    arrayInventoryEmployees.add(product);
                }
                inflater.end();
            }

            public final String message;
            public final ArrayList<Product> arrayInventoryEmployees;
        }

        public static class PacketGetLastLocation extends Packet {
            public PacketGetLastLocation(byte[] data) throws Exception {
                super(data);
                message = readString();
                user.id_employee = readInt();
                user.fullname = readString();
                user.latitude = readDouble();
                user.longitude = readDouble();
                user.accuracy = readFloat();
                user.locationDate = readLong();
                user.trackingDate = readLong();
                user.address = readString();
                user.isWifi = readByte();
                user.is3G = readByte();
                user.isGPS = readByte();
                user.isAirplaneMode = readByte();
                user.batteryLevel = readInt();
                inflater.end();
            }

            public final String message;
            public final UserInfo user = new UserInfo();
        }

        public static class PacketLoadReportCheckIn extends Packet {
            public PacketLoadReportCheckIn(byte[] data) throws Exception {
                super(data);
                message = readString();
                int len = readInt();
                arrayReportCheckIn = new ArrayList<>(len);
                for (int i = 0; i < len; i++) {
                    ReportCheckIn rc = new ReportCheckIn();
                    rc.id = readInt();
                    rc.employee = readString();
                    rc.store = readInt();
                    rc.smaller15 = readInt();
                    rc.bigger15 = readInt();
                    rc.order = readInt();
                    rc.sales = readDouble();
                    arrayReportCheckIn.add(rc);
                }
                inflater.end();
            }

            public final String message;
            public final ArrayList<ReportCheckIn> arrayReportCheckIn;
        }

        public static class PacketRealTimeTracking extends Packet {
            public PacketRealTimeTracking(byte[] data) throws Exception {
                super(data);
                success = readBool();
                message = readString();
                inflater.end();
            }

            public final String message;
            public final boolean success;
        }

        public static class PacketLoadOrders extends Packet {
            public PacketLoadOrders(byte[] data) throws Exception {
                super(data);
                message = readString();
                int len = readInt();
                arrayOrders = new ArrayList<>(len);
                for (int i = 0; i < len; i++) {
                    Order order = new Order();
                    order.no_ = readString();
                    order.name = readString();
                    order.note = readString();
                    order.time = readLong();
                    order.amount = readFloat();
                    order.status = readInt();
                    order.document_type = readInt();
                    order.imageUrl = readString();
                    order.employeeName = readString();
                    order.ref_id = readLong();
                    order.id_employee = readInt();
                    order.id_customer = readInt();
                    arrayOrders.add(order);
                }
                inflater.end();
            }

            public final String message;
            public final ArrayList<Order> arrayOrders;
        }

        public static class PacketLoadOrderDetails extends Packet {
            public PacketLoadOrderDetails(byte[] data) throws Exception {
                super(data);
                message = readString();
                String no_ = readString();
                int len = readInt();
                arrayOrderDetails = new ArrayList<>(len);
                for (int i = 0; i < len; i++) {
                    OrderDetail orderDetail = new OrderDetail();
                    orderDetail.no_ = no_;
                    orderDetail.id_item = readInt();
                    orderDetail.itemNo_ = readString();
                    orderDetail.name = readString();
                    orderDetail.quantity = readInt();
                    orderDetail.unitprice = readFloat();
                    orderDetail.discountPercent = readFloat();
                    orderDetail.discountAmount = readFloat();
                    orderDetail.itemType = readInt();
                    orderDetail.note = readString();
                    orderDetail.status = readInt();
                    orderDetail.promotionNo_ = readString();
                    arrayOrderDetails.add(orderDetail);
                }
                inflater.end();
            }

            public final String message;
            public final ArrayList<OrderDetail> arrayOrderDetails;
        }

        public static class PacketLoadProductGroups extends Packet {
            public PacketLoadProductGroups(byte[] data) throws Exception {
                super(data);
                message = readString();
                int len = readInt();
                arrayProductGroups = new ArrayList<>(len);
                for (int i = 0; i < len; i++) {
                    ProductGroup group = new ProductGroup();
                    group.id = readInt();
                    group.no_ = readString();
                    group.name = readString();
                    arrayProductGroups.add(group);
                }
                inflater.end();
            }

            public final String message;
            public final ArrayList<ProductGroup> arrayProductGroups;
        }

        public static class PacketLoadTransactions extends Packet {
            public PacketLoadTransactions(byte[] data) throws Exception {
                super(data);
                message = readString();
                int len = readInt();
                arrayTransactions = new ArrayList<>(len);
                for (int i = 0; i < len; i++) {
                    Transaction transaction = new Transaction();
                    transaction.rowId = readInt();
                    transaction.no_ = readString();
                    transaction.description = readString();
                    transaction.trans_address = readString();
                    transaction.transaction_no = readString();
                    transaction.status = readInt();
                    transaction.note = readString();
                    transaction.latitude = readDouble();
                    transaction.longtitude = readDouble();
                    transaction.document_type = readInt();
                    transaction.contact_person = readString();
                    transaction.phone_no_ = readString();
                    transaction.priority = readInt();
                    transaction.userid = readString();
                    transaction.id_customer = readInt();
                    transaction.id_company = readInt();
                    transaction.id_region = readInt();
                    transaction.id_branch = readInt();
                    transaction.id_employee = readInt();
                    transaction.createdate = readLong();
                    transaction.modifieddate = readLong();
                    transaction.id_transaction_define = readInt();
                    transaction.is_read = readBool();
                    transaction.root_status = readInt();
                    arrayTransactions.add(transaction);
                }
                inflater.end();
            }

            public final String message;
            public final ArrayList<Transaction> arrayTransactions;
        }

        public static class PacketRejectWork extends Packet {
            public PacketRejectWork(byte[] data) throws Exception {
                super(data);
                success = readBool();
                message = readString();
            }

            public final boolean success;
            public final String message;
        }

        public static class PacketAcceptWork extends Packet {
            public PacketAcceptWork(byte[] data) throws Exception {
                super(data);
                success = readBool();
                message = readString();
                result = readInt();
            }

            public final boolean success;
            public final String message;
            public final int result;
        }

        public static class PacketBranchGroups extends Packet {
            public PacketBranchGroups(byte[] data) throws Exception {
                super(data);
                message = readString();
                int len = readInt();
                int len2 = readInt();
                listBranch = new ArrayList<>(len);
                for (int i = 0; i < len; i++) {
                    Status st = new Status();
                    st.id = readInt();
                    st.name = readString();
                    listBranch.add(st);
                }
                listGroup = new ArrayList<>(len2);
                for (int i = 0; i < len2; i++) {
                    IdStatus ist = new IdStatus();
                    ist.rootId = readInt();
                    ist.id = readInt();
                    ist.name = readString();
                    listGroup.add(ist);
                }
                inflater.end();
            }

            public final String message;
            public final ArrayList<Status> listBranch;
            public final ArrayList<IdStatus> listGroup;
        }

        public static class PacketLoadTransactionLines extends Packet {
            public PacketLoadTransactionLines(byte[] data) throws Exception {
                super(data);
                message = readString();
                int len = readInt();
                arrayTransactionLines = new ArrayList<>(len);
                for (int i = 0; i < len; i++) {
                    TransactionLine tl = new TransactionLine();
                    tl.urlImage = readInt();
                    tl.id_transaction = readInt();
                    tl.note = readString();
                    tl.location_ref_id = readInt();
                    tl.status = readInt();
                    tl.userid = readString();
                    tl.id_employee = readInt();
                    tl.create_date = readLong();
                    tl.modified_date = readLong();
                    tl.latitude = readDouble();
                    tl.longitude = readDouble();
                    tl.id_ExtNo_ = readInt();
                    tl.id_transaction_define = readInt();
                    tl.name_employee = readString();
                    tl.id_ExtNoNew_ = readLong();
                    arrayTransactionLines.add(tl);
                }
                permission = readInt();
                inflater.end();
            }

            public final String message;
            public final ArrayList<TransactionLine> arrayTransactionLines;
            public final int permission;
        }


        public static class PacketLoadSurveyDataByID extends Packet {
            public PacketLoadSurveyDataByID(byte[] data) throws Exception {
                super(data);
                message = readString();
                int lenH = readInt();
                arrSurveyHeaders = new ArrayList<>(lenH);
                for (int i = 0; i < lenH; i++) {
                    SurveyHeader h = new SurveyHeader();
                    h.id = readInt();
                    h.sort = readInt();
                    h.name = readString();
                    h.description = readString();
                    h.type = readInt();
                    h.id_campaign = readInt();
                    h.status = readInt();
                    h.requireField = readInt();
                    arrSurveyHeaders.add(h);
                }
                int lenL = readInt();
                arrSurveyLines = new ArrayList<>(lenL);
                for (int i = 0; i < lenL; i++) {
                    SurveyLine l = new SurveyLine();
                    l.id = readInt();
                    l.id_survey = readInt();
                    l.refNo_ = readString();
                    l.sort = readInt();
                    l.question = readString();
                    l.type = readInt();
                    l.result = readString();
                    l.status = readInt();
                    arrSurveyLines.add(l);
                }
                surveyCampaign = new SurveyCampaign();
                surveyCampaign.id = readInt();
                surveyCampaign.name = readString();
                surveyCampaign.no_ = readString();
                surveyCampaign.beginDate = readLong();
                surveyCampaign.endDate = readLong();
                surveyCampaign.documentDate = readLong();
                surveyCampaign.description = readString();

                inflater.end();
            }

            public final String message;
            public final ArrayList<SurveyHeader> arrSurveyHeaders;
            public final ArrayList<SurveyLine> arrSurveyLines;
            public final SurveyCampaign surveyCampaign;

        }

        public static class PacketLoadSurveyData extends Packet {
            public PacketLoadSurveyData(byte[] data) throws Exception {
                super(data);
                message = readString();
                int lenH = readInt();
                arrSurveyHeaders = new ArrayList<>(lenH);
                for (int i = 0; i < lenH; i++) {
                    SurveyHeader h = new SurveyHeader();
                    h.id = readInt();
                    h.sort = readInt();
                    h.name = readString();
                    h.description = readString();
                    h.type = readInt();
                    h.id_campaign = readInt();
                    h.status = readInt();
                    h.requireField = readInt();
                    arrSurveyHeaders.add(h);
                }
                int lenL = readInt();
                arrSurveyLines = new ArrayList<>(lenL);
                for (int i = 0; i < lenL; i++) {
                    SurveyLine l = new SurveyLine();
                    l.id = readInt();
                    l.id_survey = readInt();
                    l.refNo_ = readString();
                    l.sort = readInt();
                    l.question = readString();
                    l.type = readInt();
                    l.result = readString();
                    l.status = readInt();
                    arrSurveyLines.add(l);
                }
                inflater.end();
            }

            public final String message;
            public final ArrayList<SurveyHeader> arrSurveyHeaders;
            public final ArrayList<SurveyLine> arrSurveyLines;
        }

        public static class PacketLoadSurveyCampaign extends Packet {
            public PacketLoadSurveyCampaign(byte[] data) throws Exception {
                super(data);
                message = readString();
                int len = readInt();
                arrSurveyCampaigns = new ArrayList<>(len);
                for (int i = 0; i < len; i++) {
                    SurveyCampaign s = new SurveyCampaign();
                    s.id = readInt();
                    s.no_ = readString();
                    s.name = readString();
                    s.documentDate = readLong();
                    s.beginDate = readLong();
                    s.endDate = readLong();
                    s.description = readString();
                    arrSurveyCampaigns.add(s);
                }
                inflater.end();

            }

            public final String message;
            public final ArrayList<SurveyCampaign> arrSurveyCampaigns;
        }

        public static class PacketUpdateData extends Packet {
            public PacketUpdateData(byte[] data) throws Exception {
                super(data);
                success = readBool();
                message = readString();
                type = readInt();// Lấy loại dữ liệu
                inflater.end();
            }

            public final boolean success;
            public final String message;
            public final int type;
        }


        public static class PacketSyncSurveyDetail extends Packet {
            public PacketSyncSurveyDetail(byte[] data) throws Exception {
                super(data);
                int len = readInt();
                message = readString();
                type = readInt();// Lấy loại dữ liệu
                arrayData = new ArrayList<>(len);
                switch (type) {
                    case 0://Header
                        for (int i = 0; i < len; i++) {
                            SurveyHeader header = new SurveyHeader();
                            header.id = readInt();
                            header.sort = readInt();
                            header.name = readString();
                            header.description = readString();
                            header.type = readInt();
                            header.id_campaign = readInt();
                            header.status = readInt();
                            header.requireField = readInt();
                            arrayData.add(header);
                        }
                        break;
                    case 1://Line
                        for (int i = 0; i < len; i++) {
                            SurveyLine line = new SurveyLine();
                            line.id = readInt();
                            line.id_survey = readInt();
                            line.refNo_ = readString();
                            line.sort = readInt();
                            line.question = readString();
                            line.type = readInt();
                            line.result = readString();
                            line.status = readInt();
                            arrayData.add(line);
                        }
                        break;
                    case 2://Result
                        for (int i = 0; i < len; i++) {
                            SurveyResult result = new SurveyResult();
                            result.id = readInt();
                            result.id_customer = readInt();
                            result.id_survey_line = readInt();
                            result.answer = readString();
                            result.imageThumb = Utils.decompressBitmap(readBytes());
                            result.status = readInt();
                            arrayData.add(result);
                        }
                        break;
                    default:
                        break;
                }
                lastId = readInt();
                countData = readInt();
                inflater.end();
            }

            public final String message;
            public final int type, lastId, countData;
            public final ArrayList<Object> arrayData;
        }

        public static class PacketSyncSurvey extends Packet {
            public PacketSyncSurvey(byte[] data) throws Exception {
                super(data);
                int len = readInt();
                message = readString();
                arrayData = new ArrayList<>(len);
                for (int i = 0; i < len; i++) {
                    SurveyCampaign campaign = new SurveyCampaign();
                    campaign.id = readInt();
                    campaign.no_ = readString();
                    campaign.name = readString();
                    campaign.documentDate = readLong();
                    campaign.beginDate = readLong();
                    campaign.endDate = readLong();
                    campaign.description = readString();
                    arrayData.add(campaign);
                }
                lastId = readInt();
                countData = readInt();
                inflater.end();
            }

            public final String message;
            public final int lastId, countData;
            public final ArrayList<Object> arrayData;
        }

        public static class PacketSyncData extends Packet {
            public PacketSyncData(byte[] data) throws Exception {
                super(data);
                int len = readInt();
                message = readString();
                type = readInt();// Lấy loại dữ liệu
                arrayData = new ArrayList<>(len);
                switch (type) {
                    case 0://Sản phẩm
                        for (int i = 0; i < len; i++) {
                            Product p = new Product();
                            try {
                                p.id = readInt();
                                p.no_ = readString();
                                p.name = readString();
                                p.unit = readString();
                                p.price = readFloat();
                                p.imageUrl = readString();
                                p.description = readString();
                                p.id_ProductGroup = readInt();
                                p.name_ProductGroup = readString();
                                p.inventory = readInt();
                                arrayData.add(p);
                            } catch (Exception e) {
                                arrayData.add(p);
                                e.printStackTrace();
                            }

                        }
                        break;
                    case 1://Khách hàng
                        for (int i = 0; i < len; i++) {
                            Customer c = new Customer();
                            try {
                                c.id = readInt();
                                c.no_ = readString();
                                c.name = readString();
                                c.address = readString();
                                c.phoneNumber = readString();
                                c.imageUrl = readString();
                                c.longitude = readDouble();
                                c.latitude = readDouble();
                                c.city = readInt();
                                c.county = readInt();
                                c.amount_last_month = readFloat();
                                c.last_order_day = readLong();
                                c.last_visited = readLong();
                                c.workingtime = readInt();
                                arrayData.add(c);
                            } catch (Exception e) {
                                arrayData.add(c);
                                e.printStackTrace();
                            }

                        }
                        break;
                    case 2://Nhan vien
                        for (int i = 0; i < len; i++) {
                            UserInfo user = new UserInfo();
                            try {
                                user.id_employee = readInt();
                                user.fullname = readString();
                                user.latitude = readDouble();
                                user.longitude = readDouble();
                                user.accuracy = readFloat();
                                user.locationDate = readLong();
                                user.trackingDate = readLong();
                                user.address = readString();
                                user.isWifi = readByte();
                                user.is3G = readByte();
                                user.isGPS = readByte();
                                user.isAirplaneMode = readByte();
                                user.batteryLevel = readInt();
                                arrayData.add(user);
                            } catch (Exception e) {
                                arrayData.add(user);
                                e.printStackTrace();

                            }

                        }
                        break;
                    case 3:
                        //Lý do rời cửa hàng
                        for (int i = 0; i < len; i++) {
                            ReasonNotOrder reasonNotOrder = new ReasonNotOrder();
                            reasonNotOrder.id = readInt();
                            reasonNotOrder.content = readString();
                            reasonNotOrder.type = readInt();
                            arrayData.add(reasonNotOrder);
                        }
                        break;
                    case 4:
                        //Order
                        for (int i = 0; i < len; i++) {
                            Order o = new Order();
                            try {
                                o.no_ = readString();
                                o.name = readString();
                                o.time = readLong();
                                o.id_customer = readInt();
                                o.status = readInt();
                                o.note = readString();
                                o.amount = readFloat();
                                o.id_company = readInt();
                                o.id_region = readInt();
                                o.id_branch = readInt();
                                o.id_employee = readInt();
                                o.id_parent = readInt();
                                o.document_type = readInt();
                                o.imageUrl = readString();
                                o.employeeName = readString();
                                o.ref_id = readLong();
                                int lenDetail = readInt();
                                o.orderDetails = new ArrayList<>();
                                for (int j = 0; j < lenDetail; j++) {
                                    OrderDetail od = new OrderDetail();
                                    od.id_item = readInt();
                                    od.itemNo_ = readString();
                                    od.name = readString();
                                    od.quantity = readInt();
                                    od.unitprice = readFloat();
                                    od.discountPercent = readFloat();
                                    od.discountAmount = readFloat();
                                    od.itemType = readInt();
                                    od.note = readString();
                                    od.status = readInt();
                                    o.orderDetails.add(od);
                                }
                                arrayData.add(o);
                            } catch (Exception e) {
                                arrayData.add(o);
                                e.printStackTrace();
                            }

                        }
                        break;
                    case 5://Cap nhat khuyen mai header
                        for (int i = 0; i < len; i++) {
                            PromotionHeader header = new PromotionHeader();
                            header.id = readInt();
                            header.no_ = readString();
                            header.isGroup = readInt();
                            header.itemNo_ = readString();
                            header.refItem = readString();
                            header.kyHieu = readString();
                            header.headerNo_ = readString();
                            header.headerName = readString();
                            arrayData.add(header);
                        }
                        break;
                    case 6://cap nhat khuyen mai detail
                        for (int i = 0; i < len; i++) {
                            PromotionDetail detail = new PromotionDetail();
                            detail.id = readInt();
                            detail.promotionNo_ = readString();
                            detail.promotionDescription = readString();
                            detail.isDiscountMore = readInt();
                            arrayData.add(detail);
                        }
                        break;
                    default:
                        break;
                }
                lastId = readInt();
                countData = readInt();
                inflater.end();

            }

            public final String message;
            public final int type, lastId, countData;
            public final ArrayList<Object> arrayData;
        }

        public static class PacketLoadFileManagerDetails extends Packet {
            public PacketLoadFileManagerDetails(byte[] data) throws Exception {
                super(data);
                message = readString();
                int len = readInt();
                arrayLibraryDetails = new ArrayList<>(len);
                for (int i = 0; i < len; i++) {
                    LibraryDetail l = new LibraryDetail();
                    l.id = readInt();
                    l.description = readString();
                    l.type = readInt();
                    l.url = readString();
                    arrayLibraryDetails.add(l);
                }
                inflater.end();
            }

            public final String message;
            public final ArrayList<LibraryDetail> arrayLibraryDetails;
        }

        public static class PacketLoadFileManagerGroups extends Packet {
            public PacketLoadFileManagerGroups(byte[] data) throws Exception {
                super(data);
                message = readString();
                int len = readInt();
                arrayLibraryGroups = new ArrayList<>(len);
                for (int i = 0; i < len; i++) {
                    LibraryGroup l = new LibraryGroup();
                    l.id = readInt();
                    l.description = readString();
                    l.url = readString();
                    arrayLibraryGroups.add(l);
                }
                inflater.end();
            }

            public final String message;
            public final ArrayList<LibraryGroup> arrayLibraryGroups;
        }

        public static class PacketLoadTransactionLinesInStore extends Packet {
            public PacketLoadTransactionLinesInStore(byte[] data) throws Exception {
                super(data);
                message = readString();
                int len = readInt();
                arrayTransactionLines = new ArrayList<>(len);
                for (int i = 0; i < len; i++) {
                    TransactionLine tl = new TransactionLine();
                    tl.urlImage = readInt();
                    tl.id_transaction = readInt();
                    tl.note = readString();
                    tl.location_ref_id = readInt();
                    tl.status = readInt();
                    tl.userid = readString();
                    tl.id_employee = readInt();
                    tl.create_date = readLong();
                    tl.modified_date = readLong();
                    tl.latitude = readDouble();
                    tl.longitude = readDouble();
                    tl.id_ExtNo_ = readInt();
                    tl.id_transaction_define = readInt();
                    tl.name_employee = readString();
                    tl.id_ExtNoNew_ = readLong();
                    arrayTransactionLines.add(tl);
                }
                inflater.end();
            }

            public final String message;
            public final ArrayList<TransactionLine> arrayTransactionLines;
        }

        public static class PacketLoadReasonNotOrder extends Packet {
            public PacketLoadReasonNotOrder(byte[] data) throws Exception {
                super(data);
                message = readString();
                int len = readInt();
                arrayReasonNotOrders = new ArrayList<>(len);
                for (int i = 0; i < len; i++) {
                    ReasonNotOrder r = new ReasonNotOrder();
                    r.id = readInt();
                    r.content = readString();
                    r.type = readInt();
                    arrayReasonNotOrders.add(r);
                }
                inflater.end();
            }

            public final String message;
            public final ArrayList<ReasonNotOrder> arrayReasonNotOrders;
        }

        public static class PacketLoadLocationVisited extends Packet {
            public PacketLoadLocationVisited(byte[] data) throws Exception {
                super(data);
                message = readString();
                int len = readInt();
                arrLocationVisited = new ArrayList<>(len);
                for (int i = 0; i < len; i++) {
                    LocationVisited location = new LocationVisited();
                    location.url_image = readString();
                    location.created_date = readLong();
                    location.latitude = readDouble();
                    location.longitude = readDouble();
                    location.accuracy = (float) readDouble();
                    arrLocationVisited.add(location);
                }
                inflater.end();
            }

            public final String message;
            public final ArrayList<LocationVisited> arrLocationVisited;
        }

        public static class PacketLoadCitys extends Packet {
            public PacketLoadCitys(byte[] data) throws Exception {
                super(data);
                message = readString();
                int len = readInt();
                arrCitys = new ArrayList<>(len);
                for (int i = 0; i < len; i++) {
                    City city = new City();
                    city.id = readInt();
                    city.no_ = readString();
                    city.name = readString();
                    arrCitys.add(city);
                }
                inflater.end();
            }

            public final String message;
            public final ArrayList<City> arrCitys;
        }

        public static class PacketSendTransactionMessage extends Packet {
            public PacketSendTransactionMessage(byte[] data) throws Exception {
                super(data);
                success = readBool();
                message = readString();
            }

            public final boolean success;
            public final String message;
        }

        public static class PacketLoadCountys extends Packet {
            public PacketLoadCountys(byte[] data) throws Exception {
                super(data);
                message = readString();
                int len = readInt();
                arrCountys = new ArrayList<>(len);
                for (int i = 0; i < len; i++) {
                    County county = new County();
                    county.id = readInt();
                    county.name = readString();
                    county.description = readString();
                    arrCountys.add(county);
                }
                inflater.end();
            }

            public final String message;
            public final ArrayList<County> arrCountys;
        }


        public static class PacketLoadBI_Report extends Packet {
            public PacketLoadBI_Report(byte[] data) throws Exception {
                super(data);
                message = readString();
                int len = readInt();
                arBI_Compare = new ArrayList<>(len);
                for (int i = 0; i < len; i++) {
                    BI_ReportCompare biCom = new BI_ReportCompare();
                    biCom.sDate = readString();
                    biCom.Tagert.INumVisit = readInt();
                    biCom.Tagert.INumOrdered = readInt();
                    biCom.Tagert.IAmount = readDouble();
                    biCom.Tagert.ISKU = readInt();

                    biCom.Working.INumVisit = readInt();
                    biCom.Working.INumOrdered = readInt();
                    biCom.Working.IAmount = readDouble();
                    biCom.Working.ISKU = readInt();
                    arBI_Compare.add(biCom);
                }
                inflater.end();
            }

            public final String message;
            public final ArrayList<BI_ReportCompare> arBI_Compare;
        }

        public static class PacketLoadTimeLines extends Packet {
            public PacketLoadTimeLines(byte[] data) throws Exception {
                super(data);
                message = readString();
                int len = readInt();
                arrayTimeLines = new ArrayList<>(len);
                for (int i = 0; i < len; i++) {
                    TimeLine tl = new TimeLine();
                    tl.id = readInt();
                    tl.name = readString();
                    tl.time = readLong();
                    tl.distance = readInt();
                    tl.id_transaction = readInt();
                    tl.action = readString();
                    tl.customer = readString();
                    tl.address = readString();
                    arrayTimeLines.add(tl);

                }
                inflater.end();
            }

            public final String message;
            public final ArrayList<TimeLine> arrayTimeLines;
        }

        public static class PacketLoadRequestGrants extends Packet {
            public PacketLoadRequestGrants(byte[] data) throws Exception {
                super(data);
                message = readString();
                int len = readInt();
                arrayApprovals = new ArrayList<>(len);
                for (int i = 0; i < len; i++) {
                    ApproVal approVal = new ApproVal();
                    approVal.id_employee = readInt();
                    approVal.employeeName = readString();
                    approVal.id_request = readInt();
                    approVal.appName = readString();
                    approVal.packageName = readString();
                    approVal.description = readString();
                    approVal.created_date = readLong();
                    approVal.status = readInt();
                    arrayApprovals.add(approVal);
                }
            }

            public final String message;
            public final ArrayList<ApproVal> arrayApprovals;
        }

    }

    public static abstract class ToServer {
        public enum PacketType {
            None(0),
            SystemLog(1),
            GetConfig(2),
            Login(3),
            SendTracking(4),
            SendGcmToken(5),
            GetLocations(6),
            GetUsers(7),
            GetOrderDelivery(8),
            SetOrderDeliveryStatus(9),
            SendReport(10),
            VisitLocation(11),
            SystemInfo(12),
            LoadRoutes(13),
            LoadCustomers(14),
            UpdateCustomer(15),
            AddCustomer(16),
            LoadProducts(17),
            SendOrder(18),
            LoadOrders(19),
            LoadOrderDetails(20),
            UpdateOrder(21),
            SendTransaction(22),
            LoadTransaction(23),
            LoadTransactionLine(24),
            LoadProductGroups(25),
            LoadLocationVisited(26),
            LoadCitys(27),
            LoadCountys(28),
            BI_DailyReport(29),
            LoadTimeLines(30),
            LoadInventoryEmployees(31),
            SendRequestGrant(32),
            LoadRequestGrant(33),
            ApprovalApplock(34),
            RealTimeTracking(35),
            GetLastLocation(36),
            LoadReportCheckIn(37),
            LoadTransactionLineInStore(38),
            LoadReasonNotOrder(39),
            LoadFileManagerGroups(40),
            LoadFileManagerDetails(41),
            SyncData(42),
            LoadSurveyCampaign(43),
            LoadSurveyData(44),
            SendSurveyData(45),
            LoadSurveyDataByID(46),
            LogInRoute(47),
            SyncSurvey(48),
            SyncSurveyDetail(49),
            UpdateData(50),
            BranchGroup(51),
            AcceptWork(52),
            RejectWork(53),
            LoadAllCustomer(54),
            SendTransactionMessage(55);
            private final int id;

            PacketType(int id) {
                this.id = id;
            }

            public int getValue() {
                return id;
            }
        }

        public static class Packet {
            public final PacketType type;
            private ByteArrayOutputStream data;
            private boolean finished = false;

            public Packet(PacketType type) throws IOException {
                this(type, 0);
            }

            public Packet(PacketType type, int extrasize) throws IOException {
                this.type = type;
                String deviceId = Model.inst().getDeviceId();
                if (deviceId == null) deviceId = "";
                String loginToken = Model.inst().getConfigValue(Const.ConfigKeys.LoginToken);
                if (loginToken == null) loginToken = "";
                data = new ByteArrayOutputStream(12 + deviceId.length() + loginToken.length() + extrasize);
                write((short) type.ordinal());
                write((short) Const.APILevel);
                write(deviceId, true);
                write(loginToken, true);
                //API 17
                byte battery = (byte) Model.inst().getLastBatteryLevel();
                write(battery);
                write(PhoneState.inst().isGPS());
                byte network = 0;
                if (PhoneState.inst().is3G() == 1)
                    network += 1;
                if (PhoneState.inst().isWifi() == 1)
                    network += 2;
                write(network);
                write(Model.getServerTime());
                write(PhoneState.inst().isAirplaneMode());
                write((short) Const.UpdateVersion);
            }

            public byte[] getData() throws IOException {
                if (!finished) {
                    finished = true;
                    Deflater deflater = new Deflater(Deflater.DEFAULT_COMPRESSION);
                    byte[] buff = data.toByteArray();
                    data.reset();
                    data.write(buff, 0, 4);
                    deflater.setInput(buff, 4, buff.length - 4);
                    deflater.finish();
                    byte[] temp = new byte[4096];
                    while (!deflater.finished()) {
                        int n = deflater.deflate(temp);
                        data.write(temp, 0, n);
                    }
                    deflater.end();
                }
                return data.toByteArray();
            }

            protected void write(boolean value) throws IOException {
                data.write((byte) (value ? 1 : 0));
            }

            protected void write(byte value) throws IOException {
                data.write(value);
            }

            protected void write(byte[] buffer) throws IOException {
                int len = buffer == null ? 0 : buffer.length;
                write(len);
                if (len > 0) {
                    data.write(buffer);
                }
            }

            protected void write(short value) throws IOException {
                data.write(ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(value).array());
            }

            protected void write(int value) throws IOException {
                data.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(value).array());
            }

            protected void write(long value) throws IOException {
                data.write(ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putLong(value).array());
            }

            protected void write(float value) throws IOException {
                data.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putFloat(value).array());
            }

            protected void write(double value) throws IOException {
                data.write(ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putDouble(value).array());
            }

            protected void write(String value, boolean isASCII) throws IOException {
                if (value == null) {
                    write((short) 0);
                } else {
                    write((short) (isASCII ? value.length() : -value.length()));
                    for (int i = 0; i < value.length(); i++) {
                        write((byte) value.charAt(i));
                        if (!isASCII) write((byte) (((short) value.charAt(i)) >> 8));
                    }
                }
            }

            protected void write(String value) throws IOException {
                write(value, false);
            }
        }

        public static class PacketSystemLog extends Packet {
            public PacketSystemLog(SystemLogItem[] items) throws IOException {
                super(PacketType.SystemLog);
                write(items.length);
                for (int i = 0; i < items.length; i++) {
                    SystemLogItem item = items[i];
                    write(item.appVersion);
                    write(item.date);
                    write(item.type);
                    write(item.note);
                }
            }
        }

        public static class PacketSystemInfo extends Packet {
            public PacketSystemInfo(String info) throws IOException {
                super(PacketType.SystemInfo);
                write(info);
            }
        }

        public static class PacketLogin extends Packet {
            public PacketLogin(String username, String password, String note) throws IOException {
                super(PacketType.Login);
                write(username);
                write(password);
                write(note);
            }
        }

        public static class PacketSendTracking extends Packet {
            public PacketSendTracking(TrackingItem[] items) throws IOException {
                super(PacketType.SendTracking);
                write(items.length);
                for (int i = 0; i < items.length; i++) {
                    TrackingItem item = items[i];
                    write(item.rowID);
                    write(item.deviceId);
                    write(item.visitedId);
                    write(item.visitedType);
                    write(item.latitude);
                    write(item.longitude);
                    write(item.accuracy);
                    write(item.speed);
                    write(item.distanceMeter);
                    write(item.milisecElapsed);
                    write(item.locationDate);
                    write(item.trackingDate);
                    write(item.note);
                    write(item.getType);
                    write(item.getMethod);
                    write(item.isWifi);
                    write(item.is3G);
                    write(item.isAirplaneMode);
                    write(item.isGPS);
                    if (item.cellInfo != null) {
                        write(true);
                        write(item.cellInfo.cellID);
                        write(item.cellInfo.LAC);
                        write(item.cellInfo.MCC);
                        write(item.cellInfo.MNC);
                    } else {
                        write(false);
                    }
                    write(item.batteryLevel);
                }
            }
        }

        public static class PacketSendGcmToken extends Packet {
            public PacketSendGcmToken(String gcmToken) throws IOException {
                super(PacketType.SendGcmToken);
                write(gcmToken, true);
            }
        }

        public static class PacketGetLocations extends Packet {
            public PacketGetLocations(int id_employee, long fromTime, long toTime, int max, boolean filtered) throws IOException {
                super(PacketType.GetLocations);
                write(max);
                write(fromTime);
                write(toTime);
                write(id_employee);
                write(filtered);
            }
        }

        public static class PacketLoginRoute extends Packet {
            public PacketLoginRoute(String user, String pass) throws IOException {
                super(PacketType.LogInRoute);
                write(user);
                write(pass);
            }
        }

        public static class PacketBranchGroups extends Packet {
            public PacketBranchGroups() throws IOException {
                super(PacketType.BranchGroup);
            }
        }

        public static class PacketAcceptWork extends Packet {
            public PacketAcceptWork(int id) throws IOException {
                super(PacketType.AcceptWork);
                write(id);
            }
        }

        public static class PacketRejectWork extends Packet {
            public PacketRejectWork(int id) throws IOException {
                super(PacketType.RejectWork);
                write(id);
            }
        }

        public static class PacketGetUsers extends Packet {
            public PacketGetUsers() throws IOException {
                super(PacketType.GetUsers);
            }
        }

        public static class PacketGetOrderDelivery extends Packet {
            public PacketGetOrderDelivery() throws IOException {
                super(PacketType.GetOrderDelivery);
            }
        }

        public static class PacketSetOrderDeliveryStatus extends Packet {
            public PacketSetOrderDeliveryStatus(String OrderNo, byte status, String note, int location_id) throws IOException {
                super(PacketType.SetOrderDeliveryStatus);
                write(OrderNo);
                write(status);
                write(note);
                write(location_id);
            }
        }

        public static class PacketVisitLocation extends Packet {
            public PacketVisitLocation(byte visitedType, String visitedID, int location_id, String note, String imageUrl, Bitmap thumb) throws IOException {
                super(PacketType.VisitLocation);
                write(visitedType);
                write(visitedID);
                write(location_id);
                write(note);
                byte[] buffer = null;
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    buffer = Utils.getScaledImage(imageUrl, Model.inst().getConfigValue(Const.ConfigKeys.MaxLongEdgeImageReport, Const.DefaultMaxLongEdgeImageReport), Model.inst().getConfigValue(Const.ConfigKeys.MaxFileSizeImageReport, Const.DefaultMaxFileSizeImageReport), Utils.long2String(Model.getServerTime()) + " - " + note);
                    imageLoadFailed = buffer == null;
                } else imageLoadFailed = false;
                write(buffer);
                buffer = Utils.compressBitmap(thumb);
                write(buffer);
            }

            public final boolean imageLoadFailed;
        }

        public static class PacketRoute extends Packet {
            public PacketRoute(int id_employee_viewed) throws IOException {
                super(PacketType.LoadRoutes);
                write(id_employee_viewed);
            }
        }

        public static class PacketLoadCustomers extends Packet {
            public PacketLoadCustomers(int routeId, String filter, int lastId, int id_employee_viewed) throws IOException {
                super(PacketType.LoadCustomers);
                write(routeId);
                write(filter);
                write(lastId);
                write(id_employee_viewed);
            }
        }

        public static class PacketLoadAllCustomers extends Packet {
            public PacketLoadAllCustomers(String filter, int lastID) throws IOException {
                super(PacketType.LoadAllCustomer);
                write(filter);
                write(lastID);
            }
        }

        public static class PacketUpdateCustomer extends Packet {
            public PacketUpdateCustomer(Customer customer) throws IOException {
                super(PacketType.UpdateCustomer);
                write(customer.no_);
                write(customer.name);
                write(customer.address);
                write(customer.phoneNumber);
                write(customer.longitude);
                write(customer.latitude);
                write(customer.city);
                write(customer.county);
                byte[] buffer = null;
                if (customer.imageUrl != null && !customer.imageUrl.isEmpty() && customer.imageUrl.contains(Const.FOLDERDATA)) {
                    buffer = Utils.getScaledImage(customer.imageUrl, Model.inst().getConfigValue(Const.ConfigKeys.MaxLongEdgeImageCustomer, Const.DefaultMaxLongEdgeImageCustomer), Model.inst().getConfigValue(Const.ConfigKeys.MaxFileSizeImageCustomer, Const.DefaultMaxFileSizeImageCustomer), Utils.long2String(Model.getServerTime()) + " - " + customer.name);
                    imageLoadFailed = buffer == null;
                } else imageLoadFailed = false;
                write(buffer);
                buffer = Utils.compressBitmap(customer.imageThumb);
                write(buffer);
                write(customer.workingtime);
            }

            public final boolean imageLoadFailed;
        }

        public static class PacketAddCustomer extends Packet {
            public PacketAddCustomer(Customer customer) throws IOException {
                super(PacketType.AddCustomer);
                write(customer.no_);
                write(customer.name);
                write(customer.address);
                write(customer.phoneNumber);
                write(customer.longitude);
                write(customer.latitude);
                write(customer.city);
                write(customer.county);
                write(customer.workingtime);
                byte[] buffer = null;
                if (customer.imageUrl != null && !customer.imageUrl.isEmpty()) {
                    buffer = Utils.getScaledImage(customer.imageUrl, Model.inst().getConfigValue(Const.ConfigKeys.MaxLongEdgeImageCustomer, Const.DefaultMaxLongEdgeImageCustomer), Model.inst().getConfigValue(Const.ConfigKeys.MaxFileSizeImageCustomer, Const.DefaultMaxFileSizeImageCustomer), Utils.long2String(Model.getServerTime()) + " - " + customer.name);
                    imageLoadFailed = buffer == null;
                } else imageLoadFailed = false;
                write(buffer);
                buffer = Utils.compressBitmap(customer.imageThumb);
                write(buffer);
            }

            public final boolean imageLoadFailed;
        }

        public static class PacketSendOrder extends Packet {
            public PacketSendOrder(Order order, ArrayList<OrderDetail> orderDetails, int type, long ref_id) throws IOException {
                super(PacketType.SendOrder);
                write(order.id_customer);
                write(order.status);
                write(order.note);
                write(order.amount);
                write(order.id_parent);
                write(order.document_type);
                write(type);
                write(ref_id);
                write(orderDetails.size());
                for (OrderDetail orderDetail : orderDetails
                        ) {
                    write(orderDetail.id_item);
                    write(orderDetail.quantity);
                    write(orderDetail.unitprice);
                    write(orderDetail.discountPercent);
                    write(orderDetail.discountAmount);
                    write(orderDetail.itemType);
                    write(orderDetail.note);
                    write(orderDetail.status);
                    write(orderDetail.promotionNo_);
                }

            }

        }

        public static class PacketLoadProducts extends Packet {
            public PacketLoadProducts(int type, int lastId, String filter) throws IOException {
                super(PacketType.LoadProducts);
                write(type);
                write(lastId);
                write(filter);
            }
        }

        public static class PacketLoadOrders extends Packet {
            public PacketLoadOrders(int rowId, long msTo, int document_type, String filter, int id_employee_viewed) throws IOException {
                super(PacketType.LoadOrders);
                write(rowId);
                write(msTo);
                write(document_type);
                write(filter);
                write(id_employee_viewed);
            }
        }

        public static class PacketRealTimeTracking extends Packet {
            public PacketRealTimeTracking(List<Integer> listEmployees) throws IOException {
                super(PacketType.RealTimeTracking);
                write((listEmployees == null) ? 0 : listEmployees.size());
                for (Integer id : listEmployees) {
                    write(id);
                }
            }
        }

        public static class PacketGetLastLocation extends Packet {
            public PacketGetLastLocation(int id_employee) throws IOException {
                super(PacketType.GetLastLocation);
                write(id_employee);
            }
        }

        public static class PacketLoadReportCheckIn extends Packet {
            public PacketLoadReportCheckIn(long fromDate, long toDate, int id_employee_viewed, int lastId) throws IOException {
                super(PacketType.LoadReportCheckIn);
                write(fromDate);
                write(toDate);
                write(id_employee_viewed);
                write(lastId);
            }
        }

        public static class PacketLoadInventoryEmployees extends Packet {
            public PacketLoadInventoryEmployees(int rowId, long msTo, int id_employee_viewed, int inventory_group, int on_stock, String filter) throws IOException {
                super(PacketType.LoadInventoryEmployees);
                write(rowId);
                write(msTo);
                write(id_employee_viewed);
                write(inventory_group);
                write(on_stock);
                write(filter);
            }
        }

        public static class PacketLoadOrderDetails extends Packet {
            public PacketLoadOrderDetails(long ref_id, int type) throws IOException {
                super(PacketType.LoadOrderDetails);
                write(ref_id);
                write(type);
            }
        }

        public static class PacketUpdateOrder extends Packet {
            public PacketUpdateOrder(Order order, ArrayList<OrderDetail> orderDetails, int type) throws IOException {
                super(PacketType.UpdateOrder);
                write(order.rowId);
                write(order.note);
                write(order.amount);
                write(order.ref_id);
                write(order.id_customer);
                write((orderDetails == null) ? 0 : orderDetails.size());
                for (OrderDetail od : orderDetails) {
                    write(od.id_item);
                    write(od.quantity);
                    write(od.unitprice);
                    write(od.discountPercent);
                    write(od.discountAmount);
                    write(od.itemType);
                    write(od.note);
                    write(od.status);
                }
                write(type);
            }

        }

        public static class PacketUpdateData extends Packet {
            public PacketUpdateData(ArrayList<Object> arrData, int type) throws IOException {
                super(PacketType.UpdateData);
                write(type);
                write(arrData.size());
                switch (type) {
                    case 0://Giao dịch
                        for (Object object : arrData) {
                            TransactionLine transactionLine = (TransactionLine) object;
                            write(transactionLine.id_customer);
                            write(transactionLine.note);
                            write(transactionLine.location_ref_id);
                            write(transactionLine.status);
                            write(transactionLine.userid);
                            write(transactionLine.id_ExtNo_);
                            write(transactionLine.id_transaction_define);
                            write(transactionLine.id_employee);
                            write(transactionLine.id_transaction);
                            write(transactionLine.refNo_);
                            write(transactionLine.create_date);
                            write(transactionLine.modified_date);
                            write(transactionLine.id_ExtNoNew_);
                        }
                        break;
                    case 1://Khách hàng
                        for (Object object : arrData) {
                            Customer customer = (Customer) object;
                            write(customer.no_);
                            write(customer.name);
                            write(customer.address);
                            write(customer.phoneNumber);
                            write(customer.longitude);
                            write(customer.latitude);
                            write(customer.city);
                            write(customer.county);
                            byte[] buffer = null;
                            if (customer.imageUrl != null && !customer.imageUrl.isEmpty()) {
                                buffer = Utils.getScaledImage(customer.imageUrl, Model.inst().getConfigValue(Const.ConfigKeys.MaxLongEdgeImageCustomer, Const.DefaultMaxLongEdgeImageCustomer), Model.inst().getConfigValue(Const.ConfigKeys.MaxFileSizeImageCustomer, Const.DefaultMaxFileSizeImageCustomer), Utils.long2String(Model.getServerTime()) + " - " + customer.name);
                            }
                            write(buffer);
                            buffer = Utils.compressBitmap(customer.imageThumb);
                            write(buffer);
                            write(customer.workingtime);
                        }
                        break;
                    case 2:
                        for (Object object : arrData) {
                            Order order = (Order) object;
                            write(order.no_);
                            write(order.name);
                            write(order.time);
                            write(order.id_customer);
                            write(order.status);
                            write(order.note);
                            write(order.amount);
                            write(order.id_company);
                            write(order.id_region);
                            write(order.id_branch);
                            write(order.id_employee);
                            write(order.id_parent);
                            write(order.document_type);
                            write(order.imageUrl);
                            write(order.employeeName);
                            write(order.ref_id);
                            write(order.orderDetails.size());
                            for (OrderDetail detail : order.orderDetails) {
                                write(detail.id_item);
                                write(detail.quantity);
                                write(detail.unitprice);
                                write(detail.discountPercent);
                                write(detail.discountAmount);
                                write(detail.itemType);
                                write(detail.note);
                                write(detail.status);
                            }
                        }
                        break;
                    default:
                        break;
                }


            }

        }

        public static class PacketSendTransaction extends Packet {
            public PacketSendTransaction(TransactionLine transactionLine) throws IOException {
                super(PacketType.SendTransaction);
                write(transactionLine.id_customer);
                write(transactionLine.note);
                write(transactionLine.location_ref_id);
                write(transactionLine.status);
                write(transactionLine.userid);
                write(transactionLine.id_ExtNo_);
                write(transactionLine.id_transaction_define);
                write(transactionLine.id_employee);
                write(transactionLine.id_transaction);
                write(transactionLine.refNo_);
                write(transactionLine.create_date);
                write(transactionLine.modified_date);
                write(transactionLine.id_ExtNoNew_);
            }

        }

        public static class PacketLoadTransactions extends Packet {
            public PacketLoadTransactions(int rowId, long msFrom, int id_employee, String filter, boolean loadById, int group) throws IOException {
                super(PacketType.LoadTransaction);
                write(rowId);
                write(msFrom);
                write(id_employee);//API =11 thay thế cho msTo
                write(filter);
                write(loadById);
                write(group);
            }
        }

        public static class PacketLoadTransactionLines extends Packet {
            public PacketLoadTransactionLines(int rowId) throws IOException {
                super(PacketType.LoadTransactionLine);
                write(rowId);
            }
        }


        public static class PacketLoadTransactionLinesInStore extends Packet {
            public PacketLoadTransactionLinesInStore(int id_customer, int lastId) throws IOException {
                super(PacketType.LoadTransactionLineInStore);
                write(id_customer);
                write(lastId);
            }
        }

        public static class PacketLoadFileManagerGroups extends Packet {
            public PacketLoadFileManagerGroups(int id_employee) throws IOException {
                super(PacketType.LoadFileManagerGroups);
                write(id_employee);
            }
        }

        public static class PacketLoadFileManagerDetails extends Packet {
            public PacketLoadFileManagerDetails(int id) throws IOException {
                super(PacketType.LoadFileManagerDetails);
                write(id);
            }
        }

        public static class PacketSyncSurvey extends Packet {
            public PacketSyncSurvey(int id) throws IOException {
                super(PacketType.SyncSurvey);
                write(id);
            }
        }

        public static class PacketSyncSurveyDetail extends Packet {
            public PacketSyncSurveyDetail(int type, int id, List<Integer> ids) throws IOException {
                super(PacketType.SyncSurveyDetail);
                write(type);
                write(id);
                write(ids.size());
                for (int i : ids) {
                    write(i);
                }
            }
        }

        public static class PacketSyncData extends Packet {
            public PacketSyncData(int type, int id) throws IOException {
                super(PacketType.SyncData);
                write(type);
                write(id);
            }
        }

        public static class PacketLoadSurveyCampaign extends Packet {
            public PacketLoadSurveyCampaign() throws IOException {
                super(PacketType.LoadSurveyCampaign);
            }
        }

        public static class PacketLoadSurveyData extends Packet {
            public PacketLoadSurveyData(int id_customer, int root_customer, int id_campaign) throws IOException {
                super(PacketType.LoadSurveyData);
                write(id_customer);
                write(root_customer);
                write(id_campaign);

            }
        }

        public static class PacketLoadSurveyDataByID extends Packet {
            public PacketLoadSurveyDataByID(int id, int id_customer, int root_customer) throws IOException {
                super(PacketType.LoadSurveyDataByID);
                write(id);
                write(id_customer);
                write(root_customer);
            }
        }

        public static class PacketSendSurveyData extends Packet {
            public PacketSendSurveyData(ArrayList<SurveyResult> surveyResults, int rootCustomer) throws IOException {
                super(PacketType.SendSurveyData);
                write(rootCustomer);
                write(surveyResults.size());
                for (SurveyResult item : surveyResults) {
                    write(item.id_customer);
                    write(item.id_survey_line);
                    write(item.answer);
                    write(item.status);
                    byte[] buffer = null;
                    if (item.answer.contains(".jpg") && item.imageThumb != null) {
                        buffer = Utils.getScaledImage(item.answer, Model.inst().getConfigValue(Const.ConfigKeys.MaxLongEdgeImageCustomer, Const.DefaultMaxLongEdgeImageCustomer), Model.inst().getConfigValue(Const.ConfigKeys.MaxFileSizeImageCustomer, Const.DefaultMaxFileSizeImageCustomer), Utils.long2String(Model.getServerTime()) + " - " + item.id_survey_line);
                        imageLoadFailed = buffer == null;
                    } else imageLoadFailed = false;
                    write(buffer);
                    buffer = Utils.compressBitmap(item.imageThumb);
                    write(buffer);
                }

            }

            public boolean imageLoadFailed;


        }

        public static class PacketLoadLocationVisited extends Packet {
            public PacketLoadLocationVisited(int location_id, int id_employee) throws IOException {
                super(PacketType.LoadLocationVisited);
                write(location_id);
                write(id_employee);
            }
        }

        public static class PacketLoadCitys extends Packet {
            public PacketLoadCitys() throws IOException {
                super(PacketType.LoadCitys);

            }
        }

        public static class PacketLoadCountys extends Packet {
            public PacketLoadCountys(int id_city) throws IOException {
                super(PacketType.LoadCountys);
                write(id_city);

            }
        }

        public static class PacketSendTransactionMessage extends Packet {
            public PacketSendTransactionMessage(int type, int id_customer, int id_employee, String content, String note, String phone) throws IOException {
                super(PacketType.SendTransactionMessage);
                write(type);
                write(id_customer);
                write(id_employee);
                write(content);
                write(note);
                write(phone);
            }
        }

        public static class PacketLoadBI_Report extends Packet {
            public PacketLoadBI_Report(long lDate, int iEmployee, int iMonth) throws IOException {
                super(PacketType.BI_DailyReport);
                write(lDate);
                write((iEmployee));
                write(iMonth);
            }
        }

        public static class PacketLoadTimeLines extends Packet {
            public PacketLoadTimeLines(int id_employee, int get_type, int last_id) throws IOException {
                super(PacketType.LoadTimeLines);

                write(id_employee);
                write(get_type);
                write(last_id);
            }
        }

        public static class PacketSendRequestGrant extends Packet {
            public PacketSendRequestGrant(String packageName, String appName, String description) throws IOException {
                super(PacketType.SendRequestGrant);
                write(packageName);
                write(appName);
                write(description);
            }
        }

        public static class PacketLoadRequestGrants extends Packet {
            public PacketLoadRequestGrants(int rowId, int id_employee_viewed, int status, String filter) throws IOException {
                super(PacketType.LoadRequestGrant);
                write(rowId);
                write(id_employee_viewed);
                write(status);
                write(filter);
            }
        }

        public static class PacketApprovalApplock extends Packet {
            public PacketApprovalApplock(int id_request, int result_approval) throws IOException {
                super(PacketType.ApprovalApplock);
                write(id_request);
                write(result_approval);
            }
        }


    }
}
