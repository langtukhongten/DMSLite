package Controller;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.util.Log;

import com.vietdms.mobile.dmslauncher.MyMethod;
import com.vietdms.mobile.dmslauncher.Service.BackgroundService;
import com.vietdms.mobile.dmslauncher.Service.MessageService;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import CommonLib.AlarmTimer;
import CommonLib.AppManager;
import CommonLib.Const;
import CommonLib.Customer;
import CommonLib.EventPool;
import CommonLib.EventType;
import CommonLib.LocalDB;
import CommonLib.LocationDetector;
import CommonLib.Model;
import CommonLib.Order;
import CommonLib.OrderDetail;
import CommonLib.PhoneState;
import CommonLib.SurveyResult;
import CommonLib.SystemLog;
import CommonLib.SystemLogItem;
import CommonLib.TrackingItem;
import CommonLib.TransactionLine;

/**
 * Created by My PC on 04/12/2015.
 */
public class NetworkTransaction {
    private static NetworkTransaction instance = null;
    private Context context;

    private NetworkTransaction(Context context) {
        try {
            defaultUrl = new URL(Const.HttpEndpoint);
            this.context = context;
        } catch (Exception ex) {
            SystemLog.inst().addLog(SystemLog.Type.Exception, ex.toString());
        }
    }

    public synchronized static NetworkTransaction inst(Context context) {
        if (instance == null) {
            instance = new NetworkTransaction(context);
            Log.d("NetworkTransaction", "Create new instance");
        }
        return instance;
    }

    private URL defaultUrl = null;

    public synchronized boolean sendTracking(boolean checkDuplicate) {
        try {
            int lastAckLocationID = Model.inst().getConfigValue(Const.ConfigKeys.InitTrackingRowID, -1);
            if (lastAckLocationID < 0) {
                Log.w("sendTracking", "lastAckLocationID not set");
                if (!getConfigs()) {
                    if (Model.inst().getConfigValue(Const.ConfigKeys.AutoTurnOn3G, Const.DefaultAutoTurnOn3G) == 1) {
                        if (PhoneState.inst().isWifi() != 1 && PhoneState.inst().is3G() != 1) {
                            PhoneState.inst().turn3GOnOff(true);
                        }
                    }
                    return false;
                }
                lastAckLocationID = Model.inst().getConfigValue(Const.ConfigKeys.InitTrackingRowID, -1);
                if (lastAckLocationID < 0) {
                    return false;
                }
            }
            TrackingItem item = null;
            int nUnAcked = 0;
            TrackingItem lastValidLocationSaved = Model.inst().getLastTrackingValidLocationSaved();
            int period = 30;//Model.inst().isRealtimeValid() ? Model.inst().getConfigValue(Const.ConfigKeys.RealtimeTrackingInterval, Const.DefaultRealtimeTrackingIntervalInSeconds) * Model.inst().getConfigValue(Const.ConfigKeys.RealtimeTrackingIntervalLocationMultiplier, Const.DefaultRealtimeTrackingIntervalLocationMultiplier) : Model.inst().getConfigValue(Const.ConfigKeys.AlarmIntervalBoosted, Const.DefaultAlarmIntervalBoostedInSeconds);
            if (!checkDuplicate || lastValidLocationSaved == null || Math.abs(lastValidLocationSaved.trackingDate - Model.inst().getServerTime()) > period * 1000) {
                TrackingItem lastItemSaved = Model.inst().getLastTrackingItemSaved();
                Location lastLocation = Model.inst().getLastLocation();
                item = new TrackingItem(lastLocation, (byte) 0);
                if (!checkDuplicate || lastItemSaved == null || Math.abs(lastItemSaved.trackingDate - Model.inst().getServerTime()) > period * 1000) {
                    if (LocalDB.inst().addTracking(item) < 0) {
                        Log.e("sendTracking", "cannot add TrackingItem to LocalDB");
                        SystemLog.inst().addLog(SystemLog.Type.Warning, "Không thể ghi thông tin tracking vào csdl cục bộ");
                        //return false;
                    } else {
                        nUnAcked = item.rowID - lastAckLocationID;
                        if (nUnAcked <= 0) {
                            Log.e("sendTracking", "lastAckLocationID mismatch");
                            SystemLog.inst().addLog(SystemLog.Type.Warning, "Không thể đồng bộ id của csdl cục bộ");
                            //return false;
                        }
                    }
                }
            }
            TrackingItem[] items;
            if (nUnAcked == 1) {
                items = new TrackingItem[1];
                items[0] = item;
            } else {
                int maxRecordsLastSend = Model.inst().getConfigValue(Const.ConfigKeys.MaxRecordsLastSend, Const.DefaultMaxRecordsLastSend);
                items = LocalDB.inst().getLastTrackingRecords(nUnAcked > 0 && nUnAcked < maxRecordsLastSend ? nUnAcked : maxRecordsLastSend);
            }
            int remain = items.length;
            while (remain >= 0) {
                int maxRecordsPerPacket = Model.inst().getConfigValue(Const.ConfigKeys.MaxRecordsPerPacket, Const.DefaultMaxRecordsPerPacket);
                int nsend = remain > maxRecordsPerPacket ? maxRecordsPerPacket : remain;
                TrackingItem[] itemsToSend = new TrackingItem[nsend];
                for (int i = 0; i < nsend; i++) {
                    itemsToSend[i] = items[items.length - remain + i];
                }
                byte[] result = sendPostRequest(defaultUrl, new Packets.ToServer.PacketSendTracking(itemsToSend).getData(), true);
                if (result != null) {
                    Log.i("sendTracking", "success " + nsend + "/" + remain);
                    Packets.FromServer.PacketSendTracking packetFromServer = new Packets.FromServer.PacketSendTracking(result);
                    if (packetFromServer.valueAck < 0) {
                        Log.w("sendTracking", "server cannot add TrackingItem");
                    } else {
                        if (LocalDB.inst().setAckLocationID(packetFromServer.valueAck)) {
                            Log.i("sendTracking", "lastAckLocationID=" + packetFromServer.valueAck);
                        } else {
                            Log.w("sendTracking", "cannot set lastAckLocationID=" + packetFromServer.valueAck);
                        }
                    }
                    parseConfig(packetFromServer.map);
                } else {
                    Log.w("sendTracking", "fail " + nsend + "/" + remain);
                    return false;
                }
                remain -= nsend;
                if (remain <= 0) break;
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            SystemLog.inst().addLog(SystemLog.Type.Exception, ex.toString());
            return false;
        }
    }

    public synchronized boolean sendGcmToken(String gcmToken) {
        try {
            byte[] result = sendPostRequest(defaultUrl, new Packets.ToServer.PacketSendGcmToken(gcmToken).getData(), true);
            if (result != null) {
                Packets.FromServer.PacketIntAck packetFromServer = new Packets.FromServer.PacketIntAck(result);
                if (packetFromServer.intValue != 0) {
                    Log.i("sendGcmToken", "success");
                } else {
                    Log.w("sendGcmToken", "server cannot save token");
                }
                return true;
            } else {
                Log.w("sendGcmToken", "fail");
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            SystemLog.inst().addLog(SystemLog.Type.Exception, ex.toString());
            return false;
        }
    }

    public synchronized boolean doLogin(String username, String password, String note) {
        try {
            byte[] result = sendPostRequest(defaultUrl, new Packets.ToServer.PacketLogin(username, password, note).getData(), true);
            if (result != null) {
                Packets.FromServer.PacketLogin packetLogin = new Packets.FromServer.PacketLogin(result);
                if (packetLogin.success) {
                    Log.i("doLogin", "success");
                    if (parseConfig(packetLogin.map)) {
                        if (packetLogin.message.isEmpty()) {
                            Model.inst().setStatusWorking(Const.StatusWorking.Tracking);
                            EventPool.view().enQueue(new EventType.EventLoginResult(2, packetLogin.message));
                        } else {
                            Model.inst().setStatusWorking(Const.StatusWorking.Pending);
                            EventPool.view().enQueue(new EventType.EventLoginResult(1, packetLogin.message));
                        }
                        AlarmTimer.inst().setTimer(3);
                    }
                } else {
                    Log.i("doLogin", "access denied");
                    EventPool.view().enQueue(new EventType.EventLoginResult(0, packetLogin.message));
                }
                return true;
            } else {
                Log.w("doLogin", "fail");
                EventPool.view().enQueue(new EventType.EventLoginResult(0, "Không thể kết nối đến máy chủ"));
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            EventPool.view().enQueue(new EventType.EventLoginResult(0, "Lỗi không xác định"));
            SystemLog.inst().addLog(SystemLog.Type.Exception, ex.toString());
            return false;
        }
    }

    public synchronized boolean getConfigs() {
        try {
            byte[] result = sendPostRequest(defaultUrl, new Packets.ToServer.Packet(Packets.ToServer.PacketType.GetConfig).getData(), true);
            if (result != null) {
                Log.i("getConfigs", "success");
                Packets.FromServer.PacketGetConfig packetGetConfig = new Packets.FromServer.PacketGetConfig(result);
                parseConfig(packetGetConfig.map);
                return true;
            } else {
                Log.w("getConfigs", "fail");
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            SystemLog.inst().addLog(SystemLog.Type.Exception, ex.toString());
            return false;
        }
    }

    private boolean parseConfig(HashMap<Const.ConfigKeys, String> map) {
        try {
            Log.i("parseConfig", "count = " + map.values().size());
            boolean needUpdateLocationRequest = false;
            for (Map.Entry<Const.ConfigKeys, String> entry : map.entrySet()) {
                Log.i("parseConfig", entry.getKey() + ":" + entry.getValue());
                switch (entry.getKey()) {
                    case Kickout:
                        ControlThread.inst().logout(entry.getValue());
                        return false;
                    case ServerTime:
                        long timestamp = Long.parseLong(entry.getValue());
                        if (timestamp > 0) {
                            Model.inst().setServerTime(timestamp);
                        }
                        break;
                    case InitTrackingRowID:
                        int lastTrackingID = Integer.parseInt(entry.getValue());
                        if (lastTrackingID >= 0) {
                            if (lastTrackingID != Model.inst().getConfigValue(Const.ConfigKeys.InitTrackingRowID, -1)) {
                                LocalDB.inst().setTrackingRowIDSeed(lastTrackingID);
                            }
                        }
                        break;
                    case SendSystemInfo:
                        if (!sendSystemInfo()) {
                            SystemLog.inst().addLog(SystemLog.Type.Error, "Không thể gửi thông tin toàn hệ thống theo yêu cầu");
                        }
                        break;
                    case SendSystemLog:
                        if (!sendSystemLog()) {
                            SystemLog.inst().addLog(SystemLog.Type.Error, "Không thể gửi nhật ký hệ thống theo yêu cầu");
                        }
                        break;
                    case RestartApp:
                        BackgroundService.stopInstance();
                        break;
                    case RealtimeTrackingTime: {
                        Model.inst().setLastRealtimeClock();
                        int time = Integer.parseInt(entry.getValue());
                        if (time > 0) {
                            LocationDetector.inst().updateRealtime(true);
                        } else {
                            LocationDetector.inst().updateRealtime(false);
                        }
                    }
                    break;
                    case MessageBody: {
                        String message = entry.getValue();
                        if (!message.isEmpty()) //Kiểm tra có tin nhắn hay không
                        {
                            if (Model.inst().getStatusWorking() == Const.StatusWorking.Tracking && Model.inst().getNextWokingTimer() == 0 && MyMethod.isCanNotice()) {
                                Log.w("Message", "tin nhan la : " + message);
                                Intent messageService = new Intent(context, MessageService.class);
                                messageService.putExtra("MessageBody", message);
                                messageService.putExtra("API", Const.UpdateVersion);
                                context.startService(messageService);
                            }
                        }
                    }
                    break;
                    default:
                        if (!entry.getValue().equals(Model.inst().getConfigValue(entry.getKey()))) {
                            if (entry.getKey() == Const.ConfigKeys.AlarmIntervalBoosted || entry.getKey() == Const.ConfigKeys.AlarmIntervalNormal) {
                                needUpdateLocationRequest = true;
                            }
                            LocalDB.inst().setConfigValue(entry.getKey(), entry.getValue());
                        }
                        break;
                }

                switch (entry.getKey()) {
                    case HideApps:
                        AppManager.inst().hideApps(entry.getValue());
                        break;
                    case RealtimeTrackingInterval: {
                        int interval = Integer.parseInt(entry.getValue());
                        int currentInterval = Model.inst().getConfigValue(Const.ConfigKeys.RealtimeTrackingInterval, Const.DefaultRealtimeTrackingIntervalInSeconds);
                        if (interval != currentInterval) {
                            if (interval > 0) {
                                if (Model.inst().getNextWokingTimer() <= 0) {
                                    AlarmTimer.inst().setRealtimeTimer(interval);
                                }
                            } else {
                                AlarmTimer.inst().clrRealtimeTimer();
                            }
                        }
                    }
                    break;
                    case RealtimeTrackingIntervalSleeping: {
                        int interval = Integer.parseInt(entry.getValue());
                        int currentInterval = Model.inst().getConfigValue(Const.ConfigKeys.RealtimeTrackingIntervalSleeping, Const.DefaultRealtimeTrackingIntervalSleepingInSeconds);
                        if (interval != currentInterval) {
                            if (Model.inst().getNextWokingTimer() > 0) {
                                if (interval > 0) {
                                    AlarmTimer.inst().setRealtimeTimer(interval);
                                } else {
                                    AlarmTimer.inst().clrRealtimeTimer();
                                }
                            }
                        }
                    }
                    break;
                    case ShowMenus: {
                        int showMenus = Integer.parseInt(entry.getValue());
                        if (showMenus != Model.inst().getConfigValue(Const.ConfigKeys.ShowMenus, Const.DefaultShowMenus)) {
                            EventPool.view().enQueue(new EventType.EventBase(EventType.Type.UpdateMenus));
                        }
                    }
                    break;
                }

                Model.inst().setConfigValue(entry.getKey(), entry.getValue());
            }
            if (Model.inst().getStatusWorking() == Const.StatusWorking.Tracking) {
                if (needUpdateLocationRequest) {
                    LocationDetector.inst().updateRequest(true, true);
                }
            } else if (Model.inst().getStatusWorking() == Const.StatusWorking.Pending) {
                String companyName = Model.inst().getConfigValue(Const.ConfigKeys.CompanyName);
                String employeeName = Model.inst().getConfigValue(Const.ConfigKeys.EmployeeName);
                if (companyName != null && !companyName.isEmpty() && employeeName != null && !employeeName.isEmpty()) {
                    Model.inst().setStatusWorking(Const.StatusWorking.Tracking);
                    EventPool.view().enQueue(new EventType.EventLoginResult(2, ""));
                }
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            SystemLog.inst().addLog(SystemLog.Type.Exception, ex.toString());
            return false;
        }
    }

    public synchronized boolean getLocations(int id_employee, long fromTime, long toTime, int max, boolean filtered) {
        try {
            byte[] result = sendPostRequest(defaultUrl, new Packets.ToServer.PacketGetLocations(id_employee, fromTime, toTime, max, filtered).getData(), true);
            if (result != null) {
                Log.i("getLastLocations", "success");
                Packets.FromServer.PacketGetLocations packetGetLocations = new Packets.FromServer.PacketGetLocations(result);
                EventPool.view().enQueue(new EventType.EventGetLocationsResult(packetGetLocations.arrayLocations, packetGetLocations.message));
                return true;
            } else {
                Log.w("getLastLocations", "fail");
                EventPool.view().enQueue(new EventType.EventGetLocationsResult(null, "Không thể kết nối đến máy chủ"));
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            EventPool.view().enQueue(new EventType.EventGetLocationsResult(null, "Lỗi không xác định"));
            SystemLog.inst().addLog(SystemLog.Type.Exception, ex.toString());
            return false;
        }
    }

    public synchronized boolean loginRoute(String user, String pass) {
        try {
            byte[] result = sendPostRequest(defaultUrl, new Packets.ToServer.PacketLoginRoute(user, pass).getData(), true);
            if (result != null) {
                Log.i("loginRoute", "success");
                Packets.FromServer.PacketLoginRoute packetLoginRoute = new Packets.FromServer.PacketLoginRoute(result);
                EventPool.view().enQueue(new EventType.EventLogInRouteResult(packetLoginRoute.success, packetLoginRoute.message, packetLoginRoute.routeName));
                return true;
            } else {
                Log.w("loginRoute", "fail");
                EventPool.view().enQueue(new EventType.EventLogInRouteResult(false, "Không thể kết nối đến máy chủ", "-"));
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            EventPool.view().enQueue(new EventType.EventLogInRouteResult(false, "Lỗi không xác định", "-"));
            SystemLog.inst().addLog(SystemLog.Type.Exception, ex.toString());
            return false;
        }
    }

    public synchronized boolean getUsers() {
        try {
            byte[] result = sendPostRequest(defaultUrl, new Packets.ToServer.PacketGetUsers().getData(), true);
            if (result != null) {
                Log.i("getUsers", "success");
                Packets.FromServer.PacketGetUsers packetGetUsers = new Packets.FromServer.PacketGetUsers(result);
                EventPool.view().enQueue(new EventType.EventGetUsersResult(packetGetUsers.arrayUsers, packetGetUsers.message));
                return true;
            } else {
                Log.w("getUsers", "fail");
                EventPool.view().enQueue(new EventType.EventGetUsersResult(null, "Không thể kết nối đến máy chủ"));
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            EventPool.view().enQueue(new EventType.EventGetUsersResult(null, "Lỗi không xác định"));
            SystemLog.inst().addLog(SystemLog.Type.Exception, ex.toString());
            return false;
        }
    }

    public synchronized boolean loadRoutes(int id_employee_viewed) {
        try {
            byte[] result = sendPostRequest(defaultUrl, new Packets.ToServer.PacketRoute(id_employee_viewed).getData(), true);
            if (result != null) {
                Log.i("loadRoutes", "success");
                Packets.FromServer.PacketLoadRoutes packetLoadRoutes = new Packets.FromServer.PacketLoadRoutes(result);
                if (packetLoadRoutes.arrayRoutes.size() > 0) {
                    EventPool.view().enQueue(new EventType.EventLoadRoutesResult(true, packetLoadRoutes.message, packetLoadRoutes.arrayRoutes));
                } else {
                    EventPool.view().enQueue(new EventType.EventLoadRoutesResult(false, "Không có tuyến nào", null));
                }
                return true;
            } else {
                Log.w("loadRoutes", "fail");
                EventPool.view().enQueue(new EventType.EventLoadRoutesResult(false, "Không thể kết nối đến máy chủ", null));
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            EventPool.view().enQueue(new EventType.EventLoadRoutesResult(false, "Lỗi không xác định", null));
            SystemLog.inst().addLog(SystemLog.Type.Exception, ex.toString());
            return false;
        }
    }

    public synchronized boolean loadCustomers(int routeId, String filter, int lastId, int id_employee_viewed) {
        try {
            byte[] result = sendPostRequest(defaultUrl, new Packets.ToServer.PacketLoadCustomers(routeId, filter, lastId, id_employee_viewed).getData(), true);
            if (result != null) {
                Log.i("loadCustomers", "success");
                Packets.FromServer.PacketLoadCustomers packetLoadCustomers = new Packets.FromServer.PacketLoadCustomers(result);

                if (packetLoadCustomers.arrayCustomers.size() != 0) {
                    EventPool.view().enQueue(new EventType.EventLoadCustomersResult(true, packetLoadCustomers.message, packetLoadCustomers.arrayCustomers));
                } else {
                    EventPool.view().enQueue(new EventType.EventLoadCustomersResult(false, packetLoadCustomers.message, null));
                }
                return true;
            } else {
                Log.w("loadCustomers", "fail");
                EventPool.view().enQueue(new EventType.EventLoadCustomersResult(false, "Không thể kết nối đến máy chủ", null));
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            EventPool.view().enQueue(new EventType.EventLoadCustomersResult(false, "Lỗi không xác định", null));
            SystemLog.inst().addLog(SystemLog.Type.Exception, ex.toString());
            return false;
        }
    }


    public synchronized boolean loadAllCustomers(String filter, int lastID) {
        try {
            byte[] result = sendPostRequest(defaultUrl, new Packets.ToServer.PacketLoadAllCustomers(filter, lastID).getData(), true);
            if (result != null) {
                Log.i("loadAllCustomers", "success");
                Packets.FromServer.PacketLoadAllCustomers packetLoadAllCustomers = new Packets.FromServer.PacketLoadAllCustomers(result);

                if (packetLoadAllCustomers.arrayCustomers.size() != 0) {
                    EventPool.view().enQueue(new EventType.EventLoadAllCustomersResult(true, packetLoadAllCustomers.message, packetLoadAllCustomers.arrayCustomers));
                } else {
                    EventPool.view().enQueue(new EventType.EventLoadAllCustomersResult(false, packetLoadAllCustomers.message, null));
                }
                return true;
            } else {
                Log.w("loadAllCustomers", "fail");
                EventPool.view().enQueue(new EventType.EventLoadAllCustomersResult(false, "Không thể kết nối đến máy chủ", null));
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            EventPool.view().enQueue(new EventType.EventLoadAllCustomersResult(false, "Lỗi không xác định", null));
            SystemLog.inst().addLog(SystemLog.Type.Exception, ex.toString());
            return false;
        }
    }


    public synchronized boolean updateCustomer(Customer customer) {
        try {
            Packets.ToServer.PacketUpdateCustomer packetUpdateCustomer = new Packets.ToServer.PacketUpdateCustomer(customer);
            if (packetUpdateCustomer.imageLoadFailed) {
                Log.w("updateCustomer", "image load failed");
                EventPool.view().enQueue(new EventType.EventUpdateCustomerResult(false, "Không thể gửi vì ảnh quá lớn", null));
                return false;
            } else {
                byte[] result = sendPostRequest(defaultUrl, packetUpdateCustomer.getData(), true);
                if (result != null) {
                    Log.i("updateCustomer", "success");
                    Packets.FromServer.PacketUpdateCustomer updateCustomer = new Packets.FromServer.PacketUpdateCustomer(result);
                    EventPool.view().enQueue(new EventType.EventUpdateCustomerResult(true, updateCustomer.message, updateCustomer.customer));
                    return true;
                } else {
                    Log.w("updateCustomer", "fail");
                    EventPool.view().enQueue(new EventType.EventUpdateCustomerResult(false, "Không thể kết nối đến máy chủ", null));
                    return false;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            EventPool.view().enQueue(new EventType.EventUpdateCustomerResult(false, "Lỗi không xác định", null));
            SystemLog.inst().addLog(SystemLog.Type.Exception, ex.toString());
            return false;
        }
    }

    public synchronized boolean addCustomer(Customer customer) {
        try {
            Packets.ToServer.PacketAddCustomer packetAddCustomer = new Packets.ToServer.PacketAddCustomer(customer);
            if (packetAddCustomer.imageLoadFailed) {
                Log.w("addCustomer", "image load failed");
                EventPool.view().enQueue(new EventType.EventAddCustomerResult(false, "Không thể gửi vì ảnh quá lớn", 0));
                return false;
            } else {
                byte[] result = sendPostRequest(defaultUrl, packetAddCustomer.getData(), true);
                if (result != null) {
                    Log.i("addCustomer", "success");
                    Packets.FromServer.PacketAddCustomer packet = new Packets.FromServer.PacketAddCustomer(result);
                    EventPool.view().enQueue(new EventType.EventAddCustomerResult(packet.success, packet.message, packet.rowID));
                    return true;
                } else {
                    Log.w("addCustomer", "fail");
                    EventPool.view().enQueue(new EventType.EventAddCustomerResult(false, "Không thể kết nối đến máy chủ", 0));
                    return false;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            EventPool.view().enQueue(new EventType.EventAddCustomerResult(false, "Lỗi không xác định", 0));
            SystemLog.inst().addLog(SystemLog.Type.Exception, ex.toString());
            return false;
        }
    }

    public synchronized boolean sendOrder(Order order, ArrayList<OrderDetail> orderDetails, int type) {
        try {
            Packets.ToServer.PacketSendOrder packetSendOrder = new Packets.ToServer.PacketSendOrder(order, orderDetails, type);
            byte[] result = sendPostRequest(defaultUrl, packetSendOrder.getData(), true);
            if (result != null) {
                Log.i("sendOrder", "success");
                Packets.FromServer.PacketSendOrders packetSendOrders = new Packets.FromServer.PacketSendOrders(result);
                EventPool.view().enQueue(new EventType.EventSendOrderResult(packetSendOrders.success, packetSendOrders.message, packetSendOrders.orderDetails));
                return true;
            } else {
                Log.w("sendOrder", "fail");
                EventPool.view().enQueue(new EventType.EventSendOrderResult(false, "Không thể kết nối đến máy chủ", null));
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            EventPool.view().enQueue(new EventType.EventSendOrderResult(false, "Lỗi không xác định", null));
            SystemLog.inst().addLog(SystemLog.Type.Exception, ex.toString());
            return false;
        }
    }

    public synchronized boolean updateOrder(Order order, ArrayList<OrderDetail> orderDetails) {
        try {
            Packets.ToServer.PacketUpdateOrder packetUpdateOrder = new Packets.ToServer.PacketUpdateOrder(order, orderDetails);

            byte[] result = sendPostRequest(defaultUrl, packetUpdateOrder.getData(), true);
            if (result != null) {
                Log.i("updateOrder", "success");
                Packets.FromServer.PacketResultWithMessage packetResultWithMessage = new Packets.FromServer.PacketResultWithMessage(result);
                EventPool.view().enQueue(new EventType.EventUpdateOrderResult(packetResultWithMessage.success, packetResultWithMessage.message));
                return true;
            } else {
                Log.w("updateOrder", "fail");
                EventPool.view().enQueue(new EventType.EventUpdateOrderResult(false, "Không thể kết nối đến máy chủ"));
                return false;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            EventPool.view().enQueue(new EventType.EventUpdateCustomerResult(false, "Lỗi không xác định", null));
            SystemLog.inst().addLog(SystemLog.Type.Exception, ex.toString());
            return false;
        }
    }

    public synchronized boolean loadProducts(int type, int lastId, String filter) {
        try {
            byte[] result = sendPostRequest(defaultUrl, new Packets.ToServer.PacketLoadProducts(type, lastId, filter).getData(), true);
            if (result != null) {
                Log.i("loadProducts", "success");
                Packets.FromServer.PacketLoadProducts packetLoadProducts = new Packets.FromServer.PacketLoadProducts(result);

                if (packetLoadProducts.arrayProducts.size() != 0) {
                    EventPool.view().enQueue(new EventType.EventLoadProductsResult(true, packetLoadProducts.message, packetLoadProducts.arrayProducts));

                } else {
                    EventPool.view().enQueue(new EventType.EventLoadProductsResult(false, "Không có dữ liệu", null));

                }
                return true;
            } else {
                Log.w("loadProducts", "fail");
                EventPool.view().enQueue(new EventType.EventLoadProductsResult(false, "Không thể kết nối đến máy chủ", null));
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            EventPool.view().enQueue(new EventType.EventLoadProductsResult(false, "Lỗi không xác định", null));
            SystemLog.inst().addLog(SystemLog.Type.Exception, ex.toString());
            return false;
        }
    }

    public synchronized boolean loadOrder(int rowId, long msTo, int document_type, String filter, int id_employee_viewed) {
        try {
            byte[] result = sendPostRequest(defaultUrl, new Packets.ToServer.PacketLoadOrders(rowId, msTo, document_type, filter, id_employee_viewed).getData(), true);
            if (result != null) {
                Log.i("loadOrder", "success");
                Packets.FromServer.PacketLoadOrders packetLoadOrders = new Packets.FromServer.PacketLoadOrders(result);
                if (packetLoadOrders.arrayOrders.size() != 0) {
                    EventPool.view().enQueue(new EventType.EventLoadOrdersResult(true, packetLoadOrders.message, packetLoadOrders.arrayOrders));

                } else {
                    EventPool.view().enQueue(new EventType.EventLoadOrdersResult(false, "Không có dữ liệu", null));

                }
                return true;
            } else {
                Log.w("loadOrder", "fail");
                EventPool.view().enQueue(new EventType.EventLoadOrdersResult(false, "Không thể kết nối đến máy chủ", null));
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            EventPool.view().enQueue(new EventType.EventLoadOrdersResult(false, "Lỗi không xác định", null));
            SystemLog.inst().addLog(SystemLog.Type.Exception, ex.toString());
            return false;
        }
    }

    public synchronized boolean approvalApplock(int id_request, int result_approval) {
        try {
            byte[] result = sendPostRequest(defaultUrl, new Packets.ToServer.PacketApprovalApplock(id_request, result_approval).getData(), true);
            if (result != null) {
                Log.i("approvalApplock", "success");
                Packets.FromServer.PacketResultWithMessage packetResultWithMessage = new Packets.FromServer.PacketResultWithMessage(result);
                EventPool.view().enQueue(new EventType.EventApprovalApplockResult(packetResultWithMessage.success, packetResultWithMessage.message));
                return true;
            } else {
                Log.w("approvalApplock", "fail");
                EventPool.view().enQueue(new EventType.EventApprovalApplockResult(false, "Không thể kết nối đến máy chủ"));
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            EventPool.view().enQueue(new EventType.EventApprovalApplockResult(false, "Lỗi không xác định"));
            SystemLog.inst().addLog(SystemLog.Type.Exception, e.toString());
            return false;
        }
    }

    public synchronized boolean getLastLocation(int id_employee) {
        try {
            byte[] result = sendPostRequest(defaultUrl, new Packets.ToServer.PacketGetLastLocation(id_employee).getData(), true);
            if (result != null) {
                Log.i("getLastLocation", "success");
                Packets.FromServer.PacketGetLastLocation packetGetLastLocation = new Packets.FromServer.PacketGetLastLocation(result);
                if (packetGetLastLocation.user != null) {
                    EventPool.view().enQueue(new EventType.EventGetLastLocationResult(true, packetGetLastLocation.message, packetGetLastLocation.user));

                } else {
                    EventPool.view().enQueue(new EventType.EventGetLastLocationResult(false, "Không có dữ liệu", null));

                }
                return true;
            } else {
                Log.w("getLastLocation", "fail");
                EventPool.view().enQueue(new EventType.EventGetLastLocationResult(false, "Không thể kết nối đến máy chủ", null));
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            EventPool.view().enQueue(new EventType.EventGetLastLocationResult(false, "Lỗi không xác định", null));
            SystemLog.inst().addLog(SystemLog.Type.Exception, e.toString());
            return false;
        }
    }

    public synchronized boolean loadReportCheckIn(long fromDate, long toDate, int id_employee_viewed, int lastId) {
        try {
            byte[] result = sendPostRequest(defaultUrl, new Packets.ToServer.PacketLoadReportCheckIn(fromDate, toDate, id_employee_viewed, lastId).getData(), true);
            if (result != null) {
                Log.i("loadReportCheckIn", "success");
                Packets.FromServer.PacketLoadReportCheckIn packetLoadReportCheckIn = new Packets.FromServer.PacketLoadReportCheckIn(result);
                if (packetLoadReportCheckIn.arrayReportCheckIn != null) {
                    EventPool.view().enQueue(new EventType.EventLoadReportCheckInsResult(true, packetLoadReportCheckIn.message, packetLoadReportCheckIn.arrayReportCheckIn));

                } else {
                    EventPool.view().enQueue(new EventType.EventLoadReportCheckInsResult(false, "Không có dữ liệu", null));

                }
                return true;
            } else {
                Log.w("loadReportCheckIn", "fail");
                EventPool.view().enQueue(new EventType.EventLoadReportCheckInsResult(false, "Không thể kết nối đến máy chủ", null));
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            EventPool.view().enQueue(new EventType.EventLoadReportCheckInsResult(false, "Lỗi không xác định", null));
            SystemLog.inst().addLog(SystemLog.Type.Exception, e.toString());
            return false;
        }
    }

    public synchronized boolean realTimeTracking(List<Integer> listEmployees) {
        try {
            byte[] result = sendPostRequest(defaultUrl, new Packets.ToServer.PacketRealTimeTracking(listEmployees).getData(), true);
            if (result != null) {
                Log.i("realTimeTracking", "success");
                Packets.FromServer.PacketRealTimeTracking packetRealTimeTracking = new Packets.FromServer.PacketRealTimeTracking(result);
                if (packetRealTimeTracking.success) {
                    EventPool.view().enQueue(new EventType.EventRealTimeTrackingResult(true, packetRealTimeTracking.message));

                } else {
                    EventPool.view().enQueue(new EventType.EventRealTimeTrackingResult(false, "Không có dữ liệu"));

                }
                return true;
            } else {
                Log.w("realTimeTracking", "fail");
                EventPool.view().enQueue(new EventType.EventRealTimeTrackingResult(false, "Không thể kết nối đến máy chủ"));
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            EventPool.view().enQueue(new EventType.EventRealTimeTrackingResult(false, "Lỗi không xác định"));
            SystemLog.inst().addLog(SystemLog.Type.Exception, e.toString());
            return false;
        }
    }

    public synchronized boolean loadRequestGrants(int rowId, int id_employee_viewed, int status, String filter) {
        try {
            byte[] result = sendPostRequest(defaultUrl, new Packets.ToServer.PacketLoadRequestGrants(rowId, id_employee_viewed, status, filter).getData(), true);
            if (result != null) {
                Log.i("loadOrder", "success");
                Packets.FromServer.PacketLoadRequestGrants packetLoadRequestGrants = new Packets.FromServer.PacketLoadRequestGrants(result);
                if (packetLoadRequestGrants.arrayApprovals.size() != 0) {
                    EventPool.view().enQueue(new EventType.EventLoadRequestGrantResult(true, packetLoadRequestGrants.message, packetLoadRequestGrants.arrayApprovals));

                } else {
                    EventPool.view().enQueue(new EventType.EventLoadRequestGrantResult(false, "Không có dữ liệu", null));

                }
                return true;
            } else {
                Log.w("loadOrder", "fail");
                EventPool.view().enQueue(new EventType.EventLoadRequestGrantResult(false, "Không thể kết nối đến máy chủ", null));
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            EventPool.view().enQueue(new EventType.EventLoadRequestGrantResult(false, "Lỗi không xác định", null));
            SystemLog.inst().addLog(SystemLog.Type.Exception, ex.toString());
            return false;
        }
    }


    public synchronized boolean loadInventoryEmployees(int rowId, long msTo, int id_employee_viewed, int inventory_group, int on_stock, String filter) {
        try {
            byte[] result = sendPostRequest(defaultUrl, new Packets.ToServer.PacketLoadInventoryEmployees(rowId, msTo, id_employee_viewed, inventory_group, on_stock, filter).getData(), true);
            if (result != null) {
                Log.i("loadInventoryEmployees", "success");
                Packets.FromServer.PacketLoadInventoryEmployees packetLoadInventoryEmployees = new Packets.FromServer.PacketLoadInventoryEmployees(result);
                if (packetLoadInventoryEmployees.arrayInventoryEmployees.size() != 0) {
                    EventPool.view().enQueue(new EventType.EventLoadInventoryEmployeesResult(true, packetLoadInventoryEmployees.message, packetLoadInventoryEmployees.arrayInventoryEmployees));

                } else {
                    EventPool.view().enQueue(new EventType.EventLoadInventoryEmployeesResult(false, "Không có dữ liệu", null));

                }
                return true;
            } else {
                Log.w("loadInventoryEmployees", "fail");
                EventPool.view().enQueue(new EventType.EventLoadInventoryEmployeesResult(false, "Không thể kết nối đến máy chủ", null));
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            EventPool.view().enQueue(new EventType.EventLoadInventoryEmployeesResult(false, "Lỗi không xác định", null));
            SystemLog.inst().addLog(SystemLog.Type.Exception, ex.toString());
            return false;
        }
    }

    public synchronized boolean loadOrderDetail(int rowId) {
        try {
            byte[] result = sendPostRequest(defaultUrl, new Packets.ToServer.PacketLoadOrderDetails(rowId).getData(), true);
            if (result != null) {
                Log.i("loadOrderDetail", "success");
                Packets.FromServer.PacketLoadOrderDetails packetLoadOrderDetails = new Packets.FromServer.PacketLoadOrderDetails(result);
                if (packetLoadOrderDetails.arrayOrderDetails.size() != 0) {
                    EventPool.view().enQueue(new EventType.EventLoadOrderDetailsResult(true, packetLoadOrderDetails.message, packetLoadOrderDetails.arrayOrderDetails));

                } else {
                    EventPool.view().enQueue(new EventType.EventLoadOrderDetailsResult(false, "Không có dữ liệu", null));

                }
                return true;
            } else {
                Log.w("loadOrderDetail", "fail");
                EventPool.view().enQueue(new EventType.EventLoadOrderDetailsResult(false, "Không thể kết nối đến máy chủ", null));
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            EventPool.view().enQueue(new EventType.EventLoadOrdersResult(false, "Lỗi không xác định", null));
            SystemLog.inst().addLog(SystemLog.Type.Exception, ex.toString());
            return false;
        }
    }

    public synchronized boolean loadProductGroups() {
        try {
            byte[] result = sendPostRequest(defaultUrl, new Packets.ToServer.Packet(Packets.ToServer.PacketType.LoadProductGroups).getData(), true);
            if (result != null) {
                Log.i("loadProductGroups", "success");
                Packets.FromServer.PacketLoadProductGroups packetLoadProductGroups = new Packets.FromServer.PacketLoadProductGroups(result);
                EventPool.view().enQueue(new EventType.EventLoadProductGroupsResult(true, packetLoadProductGroups.message, packetLoadProductGroups.arrayProductGroups));
                return true;
            } else {
                Log.w("loadProductGroups", "fail");
                EventPool.view().enQueue(new EventType.EventLoadProductGroupsResult(false, "Không thể kết nối đến máy chủ", null));
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            EventPool.view().enQueue(new EventType.EventLoadProductGroupsResult(false, "Lỗi không xác định", null));
            SystemLog.inst().addLog(SystemLog.Type.Exception, ex.toString());
            return false;
        }
    }

    public synchronized boolean sendTransaction(TransactionLine transactionLine) {
        try {
            Packets.ToServer.PacketSendTransaction packetSendTransaction = new Packets.ToServer.PacketSendTransaction(transactionLine);
            byte[] result = sendPostRequest(defaultUrl, packetSendTransaction.getData(), true);
            if (result != null) {
                Log.i("sendTransaction", "success");
                Packets.FromServer.PacketResultWithMessage packetResultWithMessage = new Packets.FromServer.PacketResultWithMessage(result);
                EventPool.view().enQueue(new EventType.EventSendTransactionResult(packetResultWithMessage.success, packetResultWithMessage.message));
                return true;
            } else {
                Log.w("sendTransaction", "fail");
                EventPool.view().enQueue(new EventType.EventSendTransactionResult(false, "Không có kết nối! đã lưu vào dữ liệu cục bộ!"));
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            EventPool.view().enQueue(new EventType.EventSendTransactionResult(false, "Lỗi không xác định"));
            SystemLog.inst().addLog(SystemLog.Type.Exception, ex.toString());
            return false;
        }
    }

    public synchronized boolean loadTransaction(int rowId, long msFrom, int id_employee, String filter, boolean loadById, int group) {
        try {
            byte[] result = sendPostRequest(defaultUrl, new Packets.ToServer.PacketLoadTransactions(rowId, msFrom, id_employee, filter, loadById, group).getData(), true);
            if (result != null) {
                Log.i("loadTransaction", "success");
                Packets.FromServer.PacketLoadTransactions packetLoadTransactions = new Packets.FromServer.PacketLoadTransactions(result);
                if (packetLoadTransactions.arrayTransactions.size() != 0) {
                    EventPool.view().enQueue(new EventType.EventLoadTransactionsResult(true, packetLoadTransactions.message, packetLoadTransactions.arrayTransactions));
                } else {
                    EventPool.view().enQueue(new EventType.EventLoadTransactionsResult(false, "Không có dữ liệu", null));
                }
                return true;
            } else {
                Log.w("loadTransaction", "fail");
                EventPool.view().enQueue(new EventType.EventLoadTransactionsResult(false, "Không thể kết nối đến máy chủ", null));
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            EventPool.view().enQueue(new EventType.EventLoadTransactionsResult(false, "Lỗi không xác định", null));
            SystemLog.inst().addLog(SystemLog.Type.Exception, ex.toString());
            return false;
        }
    }

    public synchronized boolean acceptWork(int id) {
        try {
            byte[] result = sendPostRequest(defaultUrl, new Packets.ToServer.PacketAcceptWork(id).getData(), true);
            if (result != null) {
                Log.i("acceptWork", "success");
                Packets.FromServer.PacketAcceptWork packetAcceptWork = new Packets.FromServer.PacketAcceptWork(result);
                EventPool.view().enQueue(new EventType.EventAcceptWorkResult(packetAcceptWork.success, packetAcceptWork.message, packetAcceptWork.result, id));

                return true;
            } else {
                Log.w("acceptWork", "fail");
                EventPool.view().enQueue(new EventType.EventAcceptWorkResult(false, "Không thể kết nối đến máy chủ", 0, id));
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            EventPool.view().enQueue(new EventType.EventAcceptWorkResult(false, "Lỗi không xác định", 0, id));
            return false;
        }
    }

    public synchronized boolean rejectWork(int id) {
        try {
            byte[] result = sendPostRequest(defaultUrl, new Packets.ToServer.PacketRejectWork(id).getData(), true);
            if (result != null) {
                Log.i("rejectWork", "success");
                Packets.FromServer.PacketRejectWork packetRejectWork = new Packets.FromServer.PacketRejectWork(result);
                EventPool.view().enQueue(new EventType.EventRejectWorkResult(packetRejectWork.success, packetRejectWork.message));

                return true;
            } else {
                Log.w("rejectWork", "fail");
                EventPool.view().enQueue(new EventType.EventRejectWorkResult(false, "Không thể kết nối đến máy chủ"));
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            EventPool.view().enQueue(new EventType.EventRejectWorkResult(false, "Lỗi không xác định"));
            return false;
        }
    }

    public synchronized boolean loadBranchGroups() {
        try {
            byte[] result = sendPostRequest(defaultUrl, new Packets.ToServer.PacketBranchGroups().getData(), true);
            if (result != null) {
                Log.i("loadBranchGroups", "success");
                Packets.FromServer.PacketBranchGroups packetBranchGroups = new Packets.FromServer.PacketBranchGroups(result);
                if (packetBranchGroups.listBranch.size() != 0) {
                    EventPool.view().enQueue(new EventType.EventBranchGroupResult(true, packetBranchGroups.message, packetBranchGroups.listBranch, packetBranchGroups.listGroup));

                } else {
                    EventPool.view().enQueue(new EventType.EventBranchGroupResult(false, "Không có dữ liệu", null, null));

                }
                return true;
            } else {
                Log.w("loadBranchGroups", "fail");
                EventPool.view().enQueue(new EventType.EventBranchGroupResult(false, "Không thể kết nối đến máy chủ", null, null));
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            EventPool.view().enQueue(new EventType.EventBranchGroupResult(false, "Lỗi không xác định", null, null));
            return false;
        }
    }

    public synchronized boolean loadTransactionLine(int id) {
        try {
            byte[] result = sendPostRequest(defaultUrl, new Packets.ToServer.PacketLoadTransactionLines(id).getData(), true);
            if (result != null) {
                Log.i("loadTransactionLine", "success");
                Packets.FromServer.PacketLoadTransactionLines packetLoadTransactionLines = new Packets.FromServer.PacketLoadTransactionLines(result);
                if (packetLoadTransactionLines.arrayTransactionLines.size() != 0) {
                    EventPool.view().enQueue(new EventType.EventLoadTransactionLinesResult(true, packetLoadTransactionLines.message, packetLoadTransactionLines.arrayTransactionLines, packetLoadTransactionLines.permission));

                } else {
                    EventPool.view().enQueue(new EventType.EventLoadTransactionLinesResult(false, "Không có dữ liệu", null, packetLoadTransactionLines.permission));

                }
                return true;
            } else {
                Log.w("loadTransactionLine", "fail");
                EventPool.view().enQueue(new EventType.EventLoadTransactionLinesResult(false, "Không thể kết nối đến máy chủ", null, 0));
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            EventPool.view().enQueue(new EventType.EventLoadTransactionLinesResult(false, "Lỗi không xác định", null, 0));
            SystemLog.inst().addLog(SystemLog.Type.Exception, ex.toString());
            return false;
        }
    }

    public synchronized boolean loadTransactionLineInStore(int id_customer, int lastId) {
        try {
            byte[] result = sendPostRequest(defaultUrl, new Packets.ToServer.PacketLoadTransactionLinesInStore(id_customer, lastId).getData(), true);
            if (result != null) {
                Log.i("loadTransactionLine", "success");
                Packets.FromServer.PacketLoadTransactionLinesInStore packetLoadTransactionLinesInStore = new Packets.FromServer.PacketLoadTransactionLinesInStore(result);
                if (packetLoadTransactionLinesInStore.arrayTransactionLines.size() != 0) {
                    EventPool.view().enQueue(new EventType.EventLoadTransactionLinesInStoreResult(true, packetLoadTransactionLinesInStore.message, packetLoadTransactionLinesInStore.arrayTransactionLines));

                } else {
                    EventPool.view().enQueue(new EventType.EventLoadTransactionLinesInStoreResult(false, "Không có dữ liệu", null));

                }
                return true;
            } else {
                Log.w("loadTransactionLine", "fail");
                EventPool.view().enQueue(new EventType.EventLoadTransactionLinesInStoreResult(false, "Không thể kết nối đến máy chủ", null));
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            EventPool.view().enQueue(new EventType.EventLoadTransactionLinesInStoreResult(false, "Lỗi không xác định", null));
            SystemLog.inst().addLog(SystemLog.Type.Exception, ex.toString());
            return false;
        }
    }


    public synchronized boolean loadFileManagerDetails(int id) {
        try {
            byte[] result = sendPostRequest(defaultUrl, new Packets.ToServer.PacketLoadFileManagerDetails(id).getData(), true);
            if (result != null) {
                Log.i("loadFileManagerDetails", "success");
                Packets.FromServer.PacketLoadFileManagerDetails packetLoadFileManagerDetails = new Packets.FromServer.PacketLoadFileManagerDetails(result);
                if (packetLoadFileManagerDetails.arrayLibraryDetails.size() != 0) {
                    EventPool.view().enQueue(new EventType.EventLoadFileManagerDetailsResult(true, packetLoadFileManagerDetails.message, packetLoadFileManagerDetails.arrayLibraryDetails));

                } else {
                    EventPool.view().enQueue(new EventType.EventLoadFileManagerDetailsResult(false, "Không có dữ liệu", null));

                }
                return true;
            } else {
                Log.w("loadFileManagerDetails", "fail");
                EventPool.view().enQueue(new EventType.EventLoadFileManagerDetailsResult(false, "Không thể kết nối đến máy chủ", null));
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            EventPool.view().enQueue(new EventType.EventLoadFileManagerDetailsResult(false, "Lỗi không xác định", null));
            SystemLog.inst().addLog(SystemLog.Type.Exception, ex.toString());
            return false;
        }
    }


    public synchronized boolean updateData(ArrayList<Object> arrData, int type) {
        try {
            byte[] result = sendPostRequest(defaultUrl, new Packets.ToServer.PacketUpdateData(arrData, type).getData(), true);
            if (result != null) {
                Log.i("updateData", "success");
                Packets.FromServer.PacketUpdateData packetUpdateData = new Packets.FromServer.PacketUpdateData(result);
                EventPool.view().enQueue(new EventType.EventUpdateDataResult(packetUpdateData.success, packetUpdateData.message, packetUpdateData.type));
                return true;
            } else {
                Log.w("updateData", "fail");
                EventPool.view().enQueue(new EventType.EventUpdateDataResult(false, "Không thể kết nối đến máy chủ", 1));
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            EventPool.view().enQueue(new EventType.EventUpdateDataResult(false, "Lỗi không xác định", 1));
            SystemLog.inst().addLog(SystemLog.Type.Exception, ex.toString());
            return false;
        }
    }

    public synchronized boolean syncSurveyDetail(int type, int id, List<Integer> ids) {
        try {
            byte[] result = sendPostRequest(defaultUrl, new Packets.ToServer.PacketSyncSurveyDetail(type, id, ids).getData(), true);
            if (result != null) {
                Log.i("syncSurvey", "success");
                Packets.FromServer.PacketSyncSurveyDetail packetSyncSurveyDetail = new Packets.FromServer.PacketSyncSurveyDetail(result);
                if (packetSyncSurveyDetail.arrayData.size() != 0) {
                    EventPool.view().enQueue(new EventType.EventSyncSurveyDetailResult(true, packetSyncSurveyDetail.message, packetSyncSurveyDetail.type, packetSyncSurveyDetail.lastId, packetSyncSurveyDetail.countData, packetSyncSurveyDetail.arrayData));
                } else {
                    EventPool.view().enQueue(new EventType.EventSyncSurveyDetailResult(false, packetSyncSurveyDetail.message, packetSyncSurveyDetail.type, 0, 0, null));
                }
                return true;
            } else {
                Log.w("syncSurvey", "fail");
                EventPool.view().enQueue(new EventType.EventSyncSurveyDetailResult(false, "Không thể kết nối đến máy chủ", 0, 0, 0, null));
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            EventPool.view().enQueue(new EventType.EventSyncSurveyDetailResult(false, "Lỗi không xác định", 0, 0, 0, null));
            SystemLog.inst().addLog(SystemLog.Type.Exception, ex.toString());
            return false;
        }
    }

    public synchronized boolean syncSurvey(int id) {
        try {
            byte[] result = sendPostRequest(defaultUrl, new Packets.ToServer.PacketSyncSurvey(id).getData(), true);
            if (result != null) {
                Log.i("syncSurvey", "success");
                Packets.FromServer.PacketSyncSurvey packetSyncSurvey = new Packets.FromServer.PacketSyncSurvey(result);
                if (packetSyncSurvey.arrayData.size() != 0) {
                    EventPool.view().enQueue(new EventType.EventSyncSurveyResult(true, packetSyncSurvey.message, packetSyncSurvey.lastId, packetSyncSurvey.countData, packetSyncSurvey.arrayData));
                } else {
                    EventPool.view().enQueue(new EventType.EventSyncSurveyResult(false, packetSyncSurvey.message, 0, 0, null));
                }
                return true;
            } else {
                Log.w("syncSurvey", "fail");
                EventPool.view().enQueue(new EventType.EventSyncSurveyResult(false, "Không thể kết nối đến máy chủ", 0, 0, null));
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            EventPool.view().enQueue(new EventType.EventSyncSurveyResult(false, "Lỗi không xác định", 0, 0, null));
            SystemLog.inst().addLog(SystemLog.Type.Exception, ex.toString());
            return false;
        }
    }

    public synchronized boolean syncData(int type, int id) {
        try {
            byte[] result = sendPostRequest(defaultUrl, new Packets.ToServer.PacketSyncData(type, id).getData(), true);
            if (result != null) {
                Log.i("syncData", "success");
                Packets.FromServer.PacketSyncData packetSyncDatas = new Packets.FromServer.PacketSyncData(result);
                if (packetSyncDatas.arrayData.size() != 0) {
                    EventPool.view().enQueue(new EventType.EventSyncDataResult(true, packetSyncDatas.message, packetSyncDatas.type, packetSyncDatas.lastId, packetSyncDatas.countData, packetSyncDatas.arrayData));
                } else {
                    EventPool.view().enQueue(new EventType.EventSyncDataResult(false, packetSyncDatas.message, packetSyncDatas.type, 0, 0, null));
                }
                return true;
            } else {
                Log.w("syncData", "fail");
                EventPool.view().enQueue(new EventType.EventSyncDataResult(false, "Không thể kết nối đến máy chủ", 3, 0, 0, null));
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            EventPool.view().enQueue(new EventType.EventSyncDataResult(false, "Lỗi không xác định", 3, 0, 0, null));
            SystemLog.inst().addLog(SystemLog.Type.Exception, ex.toString());
            return false;
        }
    }

    public synchronized boolean sendSurveyDatas(ArrayList<SurveyResult> surveyResults, int rootCustomer) {
        try {
            Packets.ToServer.PacketSendSurveyData packetSendSurveyData = new Packets.ToServer.PacketSendSurveyData(surveyResults, rootCustomer);
            if (packetSendSurveyData.imageLoadFailed) {
                Log.w("sendSurveyDatas", "image load failed");
                EventPool.view().enQueue(new EventType.EventSendSurveyDataResult(false, "Không thể gửi vì ảnh quá lớn"));
                return false;
            } else {
                byte[] result = sendPostRequest(defaultUrl, packetSendSurveyData.getData(), true);
                if (result != null) {
                    Log.i("sendSurveyDatas", "success");
                    Packets.FromServer.PacketResultWithMessage packetResultWithMessage = new Packets.FromServer.PacketResultWithMessage(result);
                    EventPool.view().enQueue(new EventType.EventSendSurveyDataResult(packetResultWithMessage.success, packetResultWithMessage.message));
                    return true;
                } else {
                    Log.w("sendSurveyDatas", "fail");
                    EventPool.view().enQueue(new EventType.EventSendSurveyDataResult(false, "Không thể kết nối đến máy chủ"));
                    return false;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            EventPool.view().enQueue(new EventType.EventSendSurveyDataResult(false, "Lỗi không xác định"));
            SystemLog.inst().addLog(SystemLog.Type.Exception, ex.toString());
            return false;
        }
    }

    public synchronized boolean loadSurveyDatas(int id_customer, int root_customer, int id_campaign) {
        try {
            byte[] result = sendPostRequest(defaultUrl, new Packets.ToServer.PacketLoadSurveyData(id_customer, root_customer, id_campaign).getData(), true);
            if (result != null) {
                Log.i("loadSurveyDatas", "success");
                Packets.FromServer.PacketLoadSurveyData packetLoadSurveyData = new Packets.FromServer.PacketLoadSurveyData(result);
                if (packetLoadSurveyData.arrSurveyHeaders.size() != 0) {
                    EventPool.view().enQueue(new EventType.EventLoadSurveyDataResult(true, packetLoadSurveyData.message, packetLoadSurveyData.arrSurveyHeaders, packetLoadSurveyData.arrSurveyLines));
                } else {
                    EventPool.view().enQueue(new EventType.EventLoadSurveyDataResult(false, "Không có dữ liệu", null, null));
                }
                return true;
            } else {
                Log.w("loadSurveyDatas", "fail");
                EventPool.view().enQueue(new EventType.EventLoadSurveyDataResult(false, "Không thể kết nối đến máy chủ", null, null));
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            EventPool.view().enQueue(new EventType.EventLoadSurveyDataResult(false, "Lỗi không xác định", null, null));
            SystemLog.inst().addLog(SystemLog.Type.Exception, ex.toString());
            return false;
        }
    }

    public synchronized boolean loadSurveyDataByIDs(int id, int id_customer, int root_customer) {
        try {
            byte[] result = sendPostRequest(defaultUrl, new Packets.ToServer.PacketLoadSurveyDataByID(id, id_customer, root_customer).getData(), true);
            if (result != null) {
                Log.i("loadSurveyDataByIDs", "success");
                Packets.FromServer.PacketLoadSurveyDataByID packetLoadSurveyDataByID = new Packets.FromServer.PacketLoadSurveyDataByID(result);
                if (packetLoadSurveyDataByID.arrSurveyHeaders.size() != 0) {
                    EventPool.view().enQueue(new EventType.EventLoadSurveyDataByIDResult(true, packetLoadSurveyDataByID.message, packetLoadSurveyDataByID.arrSurveyHeaders, packetLoadSurveyDataByID.arrSurveyLines, packetLoadSurveyDataByID.surveyCampaign));
                } else {
                    EventPool.view().enQueue(new EventType.EventLoadSurveyDataByIDResult(false, "Không có dữ liệu", null, null, null));
                }
                return true;
            } else {
                Log.w("loadSurveyDataByIDs", "fail");
                EventPool.view().enQueue(new EventType.EventLoadSurveyDataByIDResult(false, "Không thể kết nối đến máy chủ", null, null, null));
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            EventPool.view().enQueue(new EventType.EventLoadSurveyDataByIDResult(false, "Lỗi không xác định", null, null, null));
            SystemLog.inst().addLog(SystemLog.Type.Exception, ex.toString());
            return false;
        }
    }

    public synchronized boolean loadSurveyCampaigns() {
        try {
            byte[] result = sendPostRequest(defaultUrl, new Packets.ToServer.PacketLoadSurveyCampaign().getData(), true);
            if (result != null) {
                Log.i("loadSurveyCampaigns", "success");
                Packets.FromServer.PacketLoadSurveyCampaign packetLoadSurveyCampaign = new Packets.FromServer.PacketLoadSurveyCampaign(result);
                if (packetLoadSurveyCampaign.arrSurveyCampaigns.size() != 0) {
                    EventPool.view().enQueue(new EventType.EventLoadSurveyCampaignResult(true, packetLoadSurveyCampaign.message, packetLoadSurveyCampaign.arrSurveyCampaigns));
                } else {
                    EventPool.view().enQueue(new EventType.EventLoadSurveyCampaignResult(false, "Không có dữ liệu", null));
                }
                return true;
            } else {
                Log.w("loadSurveyCampaigns", "fail");
                EventPool.view().enQueue(new EventType.EventLoadSurveyCampaignResult(false, "Không thể kết nối đến máy chủ", null));
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            EventPool.view().enQueue(new EventType.EventLoadSurveyCampaignResult(false, "Lỗi không xác định", null));
            SystemLog.inst().addLog(SystemLog.Type.Exception, ex.toString());
            return false;
        }
    }

    public synchronized boolean loadFileManagerGroups(int id_employee) {
        try {
            byte[] result = sendPostRequest(defaultUrl, new Packets.ToServer.PacketLoadFileManagerGroups(id_employee).getData(), true);
            if (result != null) {
                Log.i("loadFileManagerGroups", "success");
                Packets.FromServer.PacketLoadFileManagerGroups packetLoadFileManagerGroups = new Packets.FromServer.PacketLoadFileManagerGroups(result);
                if (packetLoadFileManagerGroups.arrayLibraryGroups.size() != 0) {
                    EventPool.view().enQueue(new EventType.EventLoadFileManagerGroupsResult(true, packetLoadFileManagerGroups.message, packetLoadFileManagerGroups.arrayLibraryGroups));

                } else {
                    EventPool.view().enQueue(new EventType.EventLoadFileManagerGroupsResult(false, "Không có dữ liệu", null));

                }
                return true;
            } else {
                Log.w("loadFileManagerGroups", "fail");
                EventPool.view().enQueue(new EventType.EventLoadFileManagerGroupsResult(false, "Không thể kết nối đến máy chủ", null));
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            EventPool.view().enQueue(new EventType.EventLoadFileManagerGroupsResult(false, "Lỗi không xác định", null));
            SystemLog.inst().addLog(SystemLog.Type.Exception, ex.toString());
            return false;
        }
    }

    public synchronized boolean loadReasonNotOrders() {
        try {
            byte[] result = sendPostRequest(defaultUrl, new Packets.ToServer.Packet(Packets.ToServer.PacketType.LoadReasonNotOrder).getData(), true);
            if (result != null) {
                Log.i("loadReasonNotOrders", "success");
                Packets.FromServer.PacketLoadReasonNotOrder packetLoadReasonNotOrder = new Packets.FromServer.PacketLoadReasonNotOrder(result);
                if (packetLoadReasonNotOrder.arrayReasonNotOrders.size() != 0) {
                    EventPool.view().enQueue(new EventType.EventLoadReasonNotOrdersResult(true, packetLoadReasonNotOrder.message, packetLoadReasonNotOrder.arrayReasonNotOrders));

                } else {
                    EventPool.view().enQueue(new EventType.EventLoadReasonNotOrdersResult(false, "Không có dữ liệu", null));

                }
                return true;
            } else {
                Log.w("loadReasonNotOrders", "fail");
                EventPool.view().enQueue(new EventType.EventLoadReasonNotOrdersResult(false, "Không thể kết nối đến máy chủ", null));
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            EventPool.view().enQueue(new EventType.EventLoadReasonNotOrdersResult(false, "Lỗi không xác định", null));
            SystemLog.inst().addLog(SystemLog.Type.Exception, ex.toString());
            return false;
        }
    }

//    public synchronized boolean getOrderDelivery() {
//        try {
//            byte[] result = sendPostRequest(defaultUrl, new Packets.ToServer.PacketGetOrderDelivery().getData(), true);
//            if (result != null) {
//                Log.i("getOrderDelivery", "success");
//                Packets.FromServer.PacketGetOrderDelivery packetGetOrderDelivery = new Packets.FromServer.PacketGetOrderDelivery(result);
//                EventPool.view().enQueue(new EventType.EventLoadOrderResult(packetGetOrderDelivery.arrayOrders, packetGetOrderDelivery.message));
//                return true;
//            } else {
//                Log.w("getOrderDelivery", "fail");
//                EventPool.view().enQueue(new EventType.EventLoadOrderResult(null, "Không thể kết nối đến máy chủ"));
//                return false;
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            EventPool.view().enQueue(new EventType.EventLoadOrderResult(null, "Lỗi không xác định"));
//            SystemLog.inst().addLog(SystemLog.Type.Exception, ex.toString());
//            return false;
//        }
//    }

    public synchronized boolean setOrderDeliveryStatus(String OrderNo, byte status, String note, int location_id) {
        try {
            byte[] result = sendPostRequest(defaultUrl, new Packets.ToServer.PacketSetOrderDeliveryStatus(OrderNo, status, note, location_id).getData(), true);
            if (result != null) {
                Log.i("setOrderDeliveryStatus", "success");
                Packets.FromServer.PacketResultWithMessage packetResultWithMessage = new Packets.FromServer.PacketResultWithMessage(result);
                EventPool.view().enQueue(new EventType.EventSetOrderDeliveryStatusResult(packetResultWithMessage.success, packetResultWithMessage.message));
                return true;
            } else {
                Log.w("setOrderDeliveryStatus", "fail");
                EventPool.view().enQueue(new EventType.EventSetOrderDeliveryStatusResult(false, "Không thể kết nối đến máy chủ"));
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            EventPool.view().enQueue(new EventType.EventSetOrderDeliveryStatusResult(false, "Lỗi không xác định"));
            SystemLog.inst().addLog(SystemLog.Type.Exception, ex.toString());
            return false;
        }
    }

    public synchronized boolean sendLocationVisited(Const.LocationVisitedType visitedType, String visitedID, int location_id, String note, String imageUrl, Bitmap thumb) {
        try {
            Packets.ToServer.PacketVisitLocation packetVisitLocation = new Packets.ToServer.PacketVisitLocation((byte) visitedType.ordinal(), visitedID, location_id, note, imageUrl, thumb);
            if (packetVisitLocation.imageLoadFailed) {
                Log.w("sendLocationVisited", "image load failed");
                EventPool.view().enQueue(new EventType.EventReportResult(false, "Không thể gửi vì ảnh quá lớn"));
                return false;
            } else {
                byte[] result = sendPostRequest(defaultUrl, packetVisitLocation.getData(), true);
                if (result != null) {
                    Log.i("sendLocationVisited", "success");
                    Packets.FromServer.PacketResultWithMessage packetResultWithMessage = new Packets.FromServer.PacketResultWithMessage(result);
                    EventPool.view().enQueue(new EventType.EventReportResult(packetResultWithMessage.success, packetResultWithMessage.message));
                    return true;
                } else {
                    Log.w("sendLocationVisited", "fail");
                    EventPool.view().enQueue(new EventType.EventReportResult(false, "Không thể kết nối đến máy chủ"));
                    return false;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            EventPool.view().enQueue(new EventType.EventReportResult(false, "Lỗi không xác định"));
            SystemLog.inst().addLog(SystemLog.Type.Exception, ex.toString());
            return false;
        }
    }

    public synchronized boolean loadLocationVisited(int location_id, int id_employee) {
        try {
            byte[] result = sendPostRequest(defaultUrl, new Packets.ToServer.PacketLoadLocationVisited(location_id, id_employee).getData(), true);
            if (result != null) {
                Log.i("loadLocationVisited", "success");
                Packets.FromServer.PacketLoadLocationVisited packetLoadLocationVisited = new Packets.FromServer.PacketLoadLocationVisited(result);
                if (packetLoadLocationVisited.arrLocationVisited.size() != 0) {
                    EventPool.view().enQueue(new EventType.EventLoadLocationVisitedResult(true, packetLoadLocationVisited.message, packetLoadLocationVisited.arrLocationVisited));

                } else {
                    EventPool.view().enQueue(new EventType.EventLoadLocationVisitedResult(false, "Không có dữ liệu", null));

                }
                return true;
            } else {
                Log.w("loadLocationVisited", "fail");
                EventPool.view().enQueue(new EventType.EventLoadLocationVisitedResult(false, "Không thể kết nối đến máy chủ", null));
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            EventPool.view().enQueue(new EventType.EventLoadLocationVisitedResult(false, "Lỗi không xác định", null));
            SystemLog.inst().addLog(SystemLog.Type.Exception, ex.toString());
            return false;
        }
    }

    public synchronized boolean loadBI_Report(long lDate, int ID_Employee, int iMonth) {
        try {
            byte[] result = sendPostRequest(defaultUrl, new Packets.ToServer.PacketLoadBI_Report(lDate, ID_Employee, iMonth).getData(), true);
            if (result != null) {
                Log.i("BI_Report", "success");
                Packets.FromServer.PacketLoadBI_Report packetLoadBI_report = new Packets.FromServer.PacketLoadBI_Report(result);
                if (packetLoadBI_report.arBI_Compare.size() != 0) {
                    EventPool.view().enQueue(new EventType.EventLoadBI_DailyReportResult(true, packetLoadBI_report.message, packetLoadBI_report.arBI_Compare));

                } else {
                    EventPool.view().enQueue(new EventType.EventLoadBI_DailyReportResult(false, "Không có dữ liệu", null));

                }
                return true;
            } else {
                Log.w("BI_Report", "fail");
                EventPool.view().enQueue(new EventType.EventLoadBI_DailyReportResult(false, "Không thể kết nối đến máy chủ", null));
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            EventPool.view().enQueue(new EventType.EventLoadLocationVisitedResult(false, "Lỗi không xác định", null));
            SystemLog.inst().addLog(SystemLog.Type.Exception, ex.toString());
            return false;
        }
    }

    public synchronized boolean loadTimeLines(int id_employee, int get_type, int last_id) {
        try {
            byte[] result = sendPostRequest(defaultUrl, new Packets.ToServer.PacketLoadTimeLines(id_employee, get_type, last_id).getData(), true);
            if (result != null) {
                Log.i("loadTimeLines", "success");
                Packets.FromServer.PacketLoadTimeLines packetLoadTimeLines = new Packets.FromServer.PacketLoadTimeLines(result);
                if (packetLoadTimeLines.arrayTimeLines.size() != 0) {
                    EventPool.view().enQueue(new EventType.EventLoadTimeLinesResult(true, packetLoadTimeLines.message, packetLoadTimeLines.arrayTimeLines));

                } else {
                    EventPool.view().enQueue(new EventType.EventLoadTimeLinesResult(false, "Không có dữ liệu", null));

                }
                return true;
            } else {
                Log.w("loadTimeLines", "fail");
                EventPool.view().enQueue(new EventType.EventLoadTimeLinesResult(false, "Không thể kết nối đến máy chủ", null));
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            EventPool.view().enQueue(new EventType.EventLoadTimeLinesResult(false, "Lỗi không xác định", null));
            SystemLog.inst().addLog(SystemLog.Type.Exception, ex.toString());
            return false;
        }
    }

    public synchronized boolean sendRequestGrant(String packageName, String appName, String description) {
        try {
            Packets.ToServer.PacketSendRequestGrant packetSendRequestGrant = new Packets.ToServer.PacketSendRequestGrant(packageName, appName, description);
            byte[] result = sendPostRequest(defaultUrl, packetSendRequestGrant.getData(), true);
            if (result != null) {
                Log.i("sendRequestGrant:", "success");
                Packets.FromServer.PacketResultWithMessage packetResultWithMessage = new Packets.FromServer.PacketResultWithMessage(result);
                EventPool.view().enQueue(new EventType.EventSendRequestGrantResult(packetResultWithMessage.success, packetResultWithMessage.message));
                return true;
            } else {
                Log.w("sendRequestGrant", "fail");
                EventPool.view().enQueue(new EventType.EventSendRequestGrantResult(false, "Không thể kết nối đến máy chủ"));
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            EventPool.view().enQueue(new EventType.EventSendRequestGrantResult(false, "Lỗi không xác định"));
            SystemLog.inst().addLog(SystemLog.Type.Exception, ex.toString());
            return false;
        }
    }

    public synchronized boolean loadCitys() {
        try {
            byte[] result = sendPostRequest(defaultUrl, new Packets.ToServer.PacketLoadCitys().getData(), true);
            if (result != null) {
                Log.i("loadCitys", "success");
                Packets.FromServer.PacketLoadCitys packetLoadCitys = new Packets.FromServer.PacketLoadCitys(result);
                EventPool.view().enQueue(new EventType.EventLoadCitysResult(true, packetLoadCitys.message, packetLoadCitys.arrCitys));
                return true;
            } else {
                Log.w("loadCitys", "fail");
                EventPool.view().enQueue(new EventType.EventLoadCitysResult(false, "Không thể kết nối đến máy chủ", null));
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            EventPool.view().enQueue(new EventType.EventLoadCitysResult(false, "Lỗi không xác định", null));
            SystemLog.inst().addLog(SystemLog.Type.Exception, ex.toString());
            return false;
        }
    }

    public synchronized boolean loadCountys(int id_city) {
        try {
            byte[] result = sendPostRequest(defaultUrl, new Packets.ToServer.PacketLoadCountys(id_city).getData(), true);
            if (result != null) {
                Log.i("loadCountys", "success");
                Packets.FromServer.PacketLoadCountys packetLoadCountys = new Packets.FromServer.PacketLoadCountys(result);
                EventPool.view().enQueue(new EventType.EventLoadCountysResult(true, packetLoadCountys.message, packetLoadCountys.arrCountys));
                return true;
            } else {
                Log.w("loadCountys", "fail");
                EventPool.view().enQueue(new EventType.EventLoadCountysResult(false, "Không thể kết nối đến máy chủ", null));
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            EventPool.view().enQueue(new EventType.EventLoadCountysResult(false, "Lỗi không xác định", null));
            SystemLog.inst().addLog(SystemLog.Type.Exception, ex.toString());
            return false;
        }
    }

    public synchronized boolean sendTransactionMessage(int type, int id_customer, int id_employee, String content, String note, String phone) {
        try {
            byte[] result = sendPostRequest(defaultUrl, new Packets.ToServer.PacketSendTransactionMessage(type, id_customer, id_employee, content, note, phone).getData(), true);
            if (result != null) {
                Log.i("loadCountys", "success");
                Packets.FromServer.PacketSendTransactionMessage packetSendTransactionMessage = new Packets.FromServer.PacketSendTransactionMessage(result);
                EventPool.view().enQueue(new EventType.EventSendTransactionMessageResult(packetSendTransactionMessage.success, packetSendTransactionMessage.message));
                return true;
            } else {
                Log.w("loadCountys", "fail");
                EventPool.view().enQueue(new EventType.EventSendTransactionMessageResult(false, "Không thể kết nối đến máy chủ"));
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            EventPool.view().enQueue(new EventType.EventSendTransactionMessageResult(false, "Lỗi không xác định"));
            SystemLog.inst().addLog(SystemLog.Type.Exception, ex.toString());
            return false;
        }
    }

    public synchronized boolean sendSystemLog() {
        try {
            SystemLogItem[] items = SystemLog.inst().getAllUnAck(Model.inst().getConfigValue(Const.ConfigKeys.MaxSystemLogLastSend, Const.DefaultMaxSystemLogLastSend));
            byte[] result = sendPostRequest(defaultUrl, new Packets.ToServer.PacketSystemLog(items).getData(), true);
            if (result != null) {
                Log.i("sendSystemLog", "success");
                Packets.FromServer.PacketSystemLog packetFromServer = new Packets.FromServer.PacketSystemLog(result);
                for (int i = 0; i < packetFromServer.intValues.length; i++) {
                    int value = packetFromServer.intValues[i];
                    packetFromServer.intValues[i] = items[value].rowID;
                }
                SystemLog.inst().setAck(packetFromServer.intValues);
                parseConfig(packetFromServer.map);
                return true;
            } else {
                Log.w("sendSystemLog", "fail");
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            SystemLog.inst().addLog(SystemLog.Type.Exception, ex.toString());
            return false;
        }
    }

    public synchronized boolean sendSystemInfo() {
        try {
            String info = PhoneState.inst().getAllInfoDevice();
            byte[] result = sendPostRequest(defaultUrl, new Packets.ToServer.PacketSystemInfo(info).getData(), false);
            if (result != null) {
                Log.i("sendSystemLog", "success");
                return true;
            } else {
                Log.w("sendSystemLog", "fail");
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            SystemLog.inst().addLog(SystemLog.Type.Exception, ex.toString());
            return false;
        }
    }

    private static byte[] sendPostRequest(URL url, byte[] send, boolean recvNeeded) {
        if (recvNeeded) {
            ByteArrayOutputStream recv = new ByteArrayOutputStream();
            if (sendPostRequest(url, send, recv)) return recv.toByteArray();
        } else {
            if (sendPostRequest(url, send, null)) return new byte[0];
        }
        return null;
    }

    private static boolean sendPostRequest(URL url, byte[] send, ByteArrayOutputStream recv) {

        HttpURLConnection conn = null;
        InputStream reader = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            if (!url.getHost().equals(conn.getURL().getHost())) {
                throw new Exception("Host redirected!");
            }
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/octet-stream");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setFixedLengthStreamingMode(send.length);
            conn.connect();
            OutputStream wr = conn.getOutputStream();
            wr.write(send);
            wr.flush();
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                reader = conn.getInputStream();
                byte[] buff = new byte[4096];
                int read = reader.read(buff);
                while (read > 0) {
                    if (recv != null) recv.write(buff, 0, read);
                    read = reader.read(buff);
                }
                return true;
            }
            Log.w("sendPostRequest", "Response Code=" + responseCode);
            return false;
        } catch (Exception ex) {
            Log.e("sendPostRequest", ex.toString());
            return false;
        } finally {
            try {
                if (reader != null)
                    reader.close();
                if (conn != null)
                    conn.disconnect();
            } catch (Exception ex) {
            }
        }
    }


}
