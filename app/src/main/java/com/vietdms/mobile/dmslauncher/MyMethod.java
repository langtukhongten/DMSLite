package com.vietdms.mobile.dmslauncher;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.WallpaperManager;
import android.app.admin.DevicePolicyManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.CallLog;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.vietdms.mobile.dmslauncher.CustomView.LoadingView;
import com.vietdms.mobile.dmslauncher.Fragment.RightFragment;
import com.vietdms.mobile.dmslauncher.Service.AppLockService;
import com.vietdms.mobile.dmslauncher.Service.BackgroundService;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import CommonLib.Const;
import CommonLib.LocalDB;
import CommonLib.Model;
import CommonLib.MyLocation;
import CommonLib.Order;
import CommonLib.OrderDetail;
import CommonLib.SurveyCampaign;
import CommonLib.SurveyHeader;
import CommonLib.SurveyLine;
import CommonLib.UserInfo;
import CommonLib.Utils;
import fr.ganfra.materialspinner.MaterialSpinner;
//

/**
 * Created by ${LTG} was born ${10/12/1994}.
 */
public class MyMethod {
    public static int Date = 0;//0 is from date , 1 is to date
    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static Drawable cacheBackground = null;
    public static ArrayList<String> cacheApps = null;
    public static boolean CheckInCustomer;// Ghi nhận trong (true) ? khách hàng : màn hình chính;
    public static boolean isTransactionMapView;// hiện map xem vị trí (true) ? giao dịch : khách hàng;
    public static boolean isOrderInTransactionLine;// hiện chi tiết đơn hàng trong (true) ? giao dịch : màn hình đơn hàng;
    public static boolean isProductOfOrder; // load danh sách sản phẩm trong (true) ? đặt hàng : màn hình chính
    public static boolean isReportStore; // Ghi nhận trong (true) ? cửa hàng : khác
    public static boolean isReportNotOrder; // Chụp ảnh trong (true) ? không đặt hàng : khác
    public static boolean isOrderPhone; //Đặt hàng (true) ? qua điện thoại : trực tiếp
    public static boolean isHasImage; // (true) ? có : không có | Hình ảnh
    public static boolean isOrderInStorePressed;// (true) ? Nhấn đặt hàng : nhấn rời cửa hàng
    public static boolean isMapViewImageLocation; // (true) ? Map hiển thị ảnh với vị trí trong chi tiết giao dịch : Map khi nhấn khách hàng
    public static boolean isReportedStore;  //Đã gửi ảnh trong  (true) ?cửa hàng : bên ngoài màn hình
    public static boolean isGetLocationCreateCustomer;//Lấy vị trí khách hàng trong (true) ? Tạo mới KH : Cập nhật KH
    public static boolean isInventory; // Tải Tồn kho
    public static boolean isLoadOrder;//Tải  Đơn hàng
    public static boolean isInventoryEmployee; // Tải Kho nhân viên
    public static boolean isInventoryBill; // Tải Phiếu nhập kho
    public static boolean isTransactionDetailOfHistory; // Chi tiết giao dịch trong (true) ? Nhật ký : Giao dịch
    public static boolean isLoadTransactionByID; // Tải giao dịch theo (true) ? ID : (LastID,from,to)
    public static boolean isOrderEditing; // Đang ở màn hình  (true) ? chỉnh sửa đơn hàng : đặt hàng
    public static boolean isInputInOrder;// Nhập liệu ở (true) ? màn hình đặt đơn: màn hình cập nhật đơn hàng
    public static boolean isInventoryInput; // Đề nghị nhập kho
    public static boolean isInventoryReport; // Báo cáo tồn kho
    public static boolean isOrder;//Đặt hàng
    public static boolean isEdit_Product; // Đang nhập sl đg trong (true) ? sửa đơn hàng : đặt hàng;
    public static boolean isProductSample;// hàng mẫu
    public static boolean isProductDisplay;// hàng trưng bày
    public static boolean isInStore;//Đang ở trong khách hàng
    public static boolean isRealTime;//Bật tắt realtime tracking
    public static boolean isUpdateLocation;//Bật tắt update Location nhân viên
    public static boolean isOrderIncurred;//Phát sinh đơn hàng
    public static boolean UpdateCustomerLocation;//Cập nhật vị trí kh
    public static boolean UpdateCustomerImage;//Cập nhật hình ảnh kh
    public static boolean isOpenTransactionLineFromStore; // Mở từ cửa hàng : giao dịch
    public static boolean isCheckInInTransactionDetail;//Ghi nhận trong chi tiết giao dịch
    public static boolean isFinishInTransactionDetail;//Ghi nhận trong chi tiết giao dịch
    public static int IDFromMessageService; // Id tin nhắn nhận từ MessageService
    public static boolean isHasOrder; // Có đơn hàng

    public static boolean isLocationUpdate = false;// Cờ set request GPS của tiên
    public static boolean isNoteInStore;//Ghi nhận trong cửa hàng
    public static int rootCustomer;// Phiếu khảo sát từ khách hàng nào
    public static int idCustomer;//Phiếu khảo sát của khách hàng lẻ nào
    public static boolean logInRouteInSetting;// đăng nhập tuyến ở màn hình (true) ? cài đặt : đăng nhập
    public static String tempLink;//Link cập nhật tạm
    public static int idCampaign;//id campaign khi xem lại
    public static boolean isSyncDating;//dang chay dong bo du lieu
    public static boolean isCheckInCustomerTransactionDetail;//Ghe tham khach hang tu chi tiet giao dich
    public static boolean updateBeforeSync; // cap nhat truoc khi dong bo
    public static boolean isFirstOff = true; // lan dau vao receiver tat mang
    public static boolean selectRouteInCreateCustomer;//Chon tuyen trong tao khach hangf / cap nhat kh
    public static boolean UpdateCustomerRoute; // cap nhat tuyen kh
    public static boolean isLoadTransactionInMessage;//Load giao dich trong message


    /**
     * Hiển thị thông báo trong ứng dụng không bị tràn ra ngoài màn hình
     * truyền vào context và thông báo muốn hiển thị
     */
    public static void showToast(Context context, String toast) {// show toast so cool
        try {
            Snackbar snackbar = Snackbar
                    .make(Home.bindingRight.getRoot(), toast, Snackbar.LENGTH_SHORT);
            View view = snackbar.getView();
            view.setBackgroundColor(Color.parseColor("#FF8C00"));
            TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
            tv.setGravity(Gravity.CENTER_HORIZONTAL);
            tv.setTextColor(Color.WHITE);
            snackbar.show();
        } catch (Exception e) {
            Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * Lấy icon cho các menu tương ứng
     * truyền vào tên menu và context
     */
    public static synchronized Drawable getIconMenu(String menuName, Context context) {
        switch (menuName) {
            case "Ghi nhận":
                return ContextCompat.getDrawable(context, R.drawable.report_btn);
            case "Giao dịch":
                return ContextCompat.getDrawable(context, R.drawable.transaction_btn);
            case "Giám sát":
                return ContextCompat.getDrawable(context, R.drawable.manager_btn);
            case "Cài đặt":
                return ContextCompat.getDrawable(context, R.drawable.setting_btn);
            case "Nhật ký":
                return ContextCompat.getDrawable(context, R.drawable.history_btn);
            case "Khách hàng":
                return ContextCompat.getDrawable(context, R.drawable.customer_btn);
            case "Sản phẩm":
                return ContextCompat.getDrawable(context, R.drawable.product_btn);
            case "Đơn hàng":
                return ContextCompat.getDrawable(context, R.drawable.ic_order);
            case "Tồn kho cửa hàng":
                return ContextCompat.getDrawable(context, R.drawable.btn_inventory);
            case "Đề nghị nhập kho":
                return ContextCompat.getDrawable(context, R.drawable.input_inventory_btn);
            case "Tồn kho nhân viên":
                return ContextCompat.getDrawable(context, R.drawable.inventory_employee_btn);
            case "Phiếu nhập kho":
                return ContextCompat.getDrawable(context, R.drawable.inventory_paper_btn);
            case "Phân quyền truy cập":
                return ContextCompat.getDrawable(context, R.drawable.approval_btn);
            case "Thư viện":
                return ContextCompat.getDrawable(context, R.drawable.library_btn);
            case "Báo cáo":
                return ContextCompat.getDrawable(context, R.drawable.report_web_btn);
            case "Báo cáo ghé thăm":
                return ContextCompat.getDrawable(context, R.drawable.report_check_in_btn);
            case "Phiếu khảo sát":
                return ContextCompat.getDrawable(context, R.drawable.survey_btn);
            case "Tuyến bán hàng":
                return ContextCompat.getDrawable(context, R.drawable.report_route);
            case "Giao dịch nhân viên":
                return ContextCompat.getDrawable(context, R.drawable.report_transaction_employee);
            case "Giao dịch tổng hợp":
                return ContextCompat.getDrawable(context, R.drawable.report_transaction_general);

            default:
                return ContextCompat.getDrawable(context, R.mipmap.android);
        }
    }

    /**
     * Tạo tập tin và thư mục vào bộ nhớ thiết bị
     * truyền vào tên thư mục và tập tin
     */
    public static File createFolder(String foldername, String filename) {
        try {
            File folder = new File(Environment.getExternalStorageDirectory() +
                    File.separator + foldername);
            File f = null;
            boolean success = true;
            if (!folder.exists()) {
                success = folder.mkdir();
            }
            if (success) {
                Log.w(foldername, "Tạo folder thành công");
                f = new File(folder, filename);
                try {
                    f.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                // Do something else on failure
                Log.w(foldername, "Tạo folder thất bại");
            }
            return f;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * Hiển thị bàn phím ảo focus vào view
     * truyền vào view
     */
    public static void requestFocus(View view) {
        try {
            if (view.requestFocus()) {
                InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Ẩn bàn phím ảo trên một view xác định
     * truyền vào view
     */
    public static void closeFocus(View view) {
        try {
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isAcceptingText())
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }


    public static boolean isVisible(View v) {
        return (v == null) ? false : (v.getVisibility() == View.VISIBLE);
    }

    public static void setVisible(View v) {
        if (v == null) return;
        else v.setVisibility(View.VISIBLE);
    }

    public static void setInVisible(View v) {
        if (v == null) return;
        else v.setVisibility(View.INVISIBLE);
    }

    public static void setGone(View v) {
        if (v == null) return;
        else v.setVisibility(View.GONE);
    }

    /**
     * Kiểm tra chế độ quản trị thiết bị đã được kích hoạt chưa
     * truyền vào devicePolicyManager và componentName
     */
    public static boolean checkAdminActive(DevicePolicyManager devicePolicyManager, ComponentName componentName) {
        return devicePolicyManager.isAdminActive(componentName);
    }

    //MAIN MENU

    /**
     * Gọi giao diện gọi thoại
     * truyền vào context
     */
    public static void callPhone(Context context) {// Show call app
        try {

            Intent i = new Intent(Intent.ACTION_DIAL, null);
            context.startActivity(i);
        } catch (Exception e) {
            Log.d(context.getString(R.string.tagEx), e.toString());
            Intent i = context.getPackageManager().getLaunchIntentForPackage(context.getString(R.string.phone_location));
            context.startActivity(i);
        }
    }

    public static void callPhone(Context context, String number) {// Show call app
        try {

            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            context.startActivity(intent);
        } catch (Exception e) {
            Log.d(context.getString(R.string.tagEx), e.toString());
            Intent i = context.getPackageManager().getLaunchIntentForPackage(context.getString(R.string.phone_location));
            context.startActivity(i);
        }
    }


    /**
     * Gọi giao diện nhắn tin
     * truyền vào context
     */
    public static void showSms(Context context) {// Show sms app
        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setType(context.getString(R.string.sms_location));
            context.startActivity(intent);
        } catch (Exception e) {
            Log.d(context.getString(R.string.tagEx), e.toString());
        }
    }

    /**
     * Khóa màn hình thiết bị
     * truyền vào context và devicePolicyManager
     */
    public static void lockDevice(Context context, DevicePolicyManager devicePolicyManager) {// Lock screen
        try {
            devicePolicyManager.lockNow();
        } catch (Exception e) {
            Log.d(context.getString(R.string.tagEx), e.toString());
        }
    }

    /**
     * Gọi giao diện gmail
     * truyền vào context
     */
    public static void showGmail(Context context) {//show mail app
        try {
            Intent i = context.getPackageManager().getLaunchIntentForPackage(context.getString(R.string.gmail_location));
            context.startActivity(i);
        } catch (Exception e) {
            Log.d(context.getString(R.string.tagEx), e.toString());
        }
    }


    /**
     * Lấy tên đường
     * truyền vào latitude và longtitude và context
     */
    public static String getAddress(double latitude, double longitude, Context context) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            String add = "";
            Address obj = addresses.get(0);
            for (int i = 0; i < obj.getMaxAddressLineIndex(); i++)
                add += obj.getAddressLine(i) + " ";
            return add;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("getAddress", e.toString());
            return "-";
        }

    }

    /**
     * Gọi giao diện map theo location có sẵn
     * truyền vào googleMap và location và context
     */
    public static void loadMap(GoogleMap googleMap, Location location, final Context context, boolean isMoveCamera) {
        try {
            Bundle extras = location.getExtras();
            String address = "";
            if (extras != null) {
                address = extras.getString("address", "");
            }
            if (address.isEmpty())
                address = MyMethod.getAddress(location.getLatitude(), location.getLongitude(), context);
            Home.markerNow = googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(location.getLatitude(), location.getLongitude())
                    )
                    .title(context.getString(R.string.location_here))
                    .snippet(address)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.now_btn))
            );
            Home.markerNow.showInfoWindow();
            Home.currentPostition = Home.markerNow.getPosition();
            RightFragment.hashMarker.put(Home.markerNow, location.getAccuracy());
            googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                @Override
                public View getInfoWindow(Marker arg0) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    LinearLayout info = new LinearLayout(context);
                    info.setOrientation(LinearLayout.VERTICAL);
                    TextView title = new TextView(context);
                    title.setTextColor(Color.BLUE);
                    title.setGravity(Gravity.CENTER);
                    title.setTypeface(null, Typeface.BOLD);
                    title.setText(marker.getTitle());

                    TextView snippet = new TextView(context);
                    snippet.setTextColor(Color.GRAY);
                    snippet.setText(marker.getSnippet());
                    info.addView(title);
                    info.addView(snippet);

                    return info;
                }
            });
            if (isMoveCamera) {
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 16);
                googleMap.animateCamera(cameraUpdate);
            }
        } catch (Exception e) {
            Log.d(context.getString(R.string.tagEx), e.toString());
        }
    }

    /**
     * Gọi giao diện map customer checkin theo location có sẵn
     * truyền vào googleMap và location và context
     */
    public static void loadMapCheckInOrder(GoogleMap googleMap, Location location, final Context context) {
        try {
            Bundle extras = location.getExtras();
            String address = "";
            if (extras != null) {
                address = extras.getString("address", "");
            }
            if (address.isEmpty())
                address = MyMethod.getAddress(location.getLatitude(), location.getLongitude(), context);
            Home.markerNow = googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(location.getLatitude(), location.getLongitude())
                    )
                    .title(context.getString(R.string.location_here))
                    .snippet(address)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.now_btn))
            );
            RightFragment.hashMarker.put(Home.markerNow, location.getAccuracy());
            Home.lineNow = googleMap.addPolyline(new PolylineOptions()
                    .add(new LatLng(location.getLatitude(), location.getLongitude()), new LatLng(RightFragment.nowCustomer.latitude, RightFragment.nowCustomer.longitude))
                    .width(5)
                    .color(Color.BLUE));
//            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 16);
//            googleMap.animateCamera(cameraUpdate);
        } catch (Exception e) {
            Log.d(context.getString(R.string.tagEx), e.toString());
        }
    }

    //Như loadMapCheckInOrder nhưng không di chuyển camera
    public static void loadMapPositionCheckInOrder(GoogleMap googleMap, Location location, final Context context) {
        try {
            Bundle extras = location.getExtras();
            String address = "";
            if (extras != null) {
                address = extras.getString("address", "");
            }
            if (address.isEmpty())
                address = MyMethod.getAddress(location.getLatitude(), location.getLongitude(), context);
            Home.markerNow = googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(location.getLatitude(), location.getLongitude())
                    )
                    .title(context.getString(R.string.location_here))
                    .snippet(address)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.now_btn))
            );
            RightFragment.hashMarker.put(Home.markerNow, location.getAccuracy());
            Home.lineNow = googleMap.addPolyline(new PolylineOptions()
                    .add(new LatLng(location.getLatitude(), location.getLongitude()), new LatLng(RightFragment.nowCustomer.latitude, RightFragment.nowCustomer.longitude))
                    .width(5)
                    .color(Color.BLUE));
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 16);
            googleMap.animateCamera(cameraUpdate);
        } catch (Exception e) {
            Log.d(context.getString(R.string.tagEx), e.toString());
        }
    }

    public static double getDistance(LatLng LatLng1, LatLng LatLng2) {
        double distance;
        try {
            Location locationA = new Location("A");
            locationA.setLatitude(LatLng1.latitude);
            locationA.setLongitude(LatLng1.longitude);
            Location locationB = new Location("B");
            locationB.setLatitude(LatLng2.latitude);
            locationB.setLongitude(LatLng2.longitude);
            distance = locationA.distanceTo(locationB);
        } catch (Exception e) {
            distance = 501;
        }
        return distance;
    }

    /**
     * Gọi giao diện làm mới map
     * truyền vào context và googleMap
     */
    public static void refreshMap(Context context, GoogleMap googleMap) {
        try {
            if (googleMap != null) googleMap.clear();
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(17, 107), 5.5f);
            googleMap.animateCamera(cameraUpdate);
        } catch (Exception e) {
            Log.d(context.getString(R.string.tagEx), e.toString());
        }
    }

    /**
     * Kiểm tra thông số đầu vào nhập báo cáo
     * truyền vào context
     */
    public static boolean checkInputInSaveSend(Context context) {
        try {
            if (Home.editCheckIn.getText().toString().isEmpty()) {
                MyMethod.showToast(context, context.getString(R.string.please_input_report));
                return false;
            }
            return true;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * Gửi một thông báo lên statusbar thiết bị
     * truyền vào context và thông báo
     */
    public static void sendNotification(Context context, String message) {
        try {
            Intent intent = new Intent(context, Home.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
                    PendingIntent.FLAG_ONE_SHOT);

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.notice_btn)
                    .setContentTitle("Thông báo bởi eDMS")
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
        } catch (Exception e) {
            Log.d("sendNotification", e.toString());
        }
    }

    /**
     * Lấy Drawable hình nền hiện tại của thiết bị
     * truyền vào context
     */
    public static Drawable getWallpaper(Context context) {
        try {
            final WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
            return wallpaperManager.getDrawable();
        } catch (Exception e) {
            Log.d("getWallpaper", e.toString());
            return ContextCompat.getDrawable(context, R.drawable.background);
        }
    }


    public static Drawable getBackground(Context context) {
        try {
            if (cacheBackground != null) return cacheBackground;
            Bitmap perfect = Bitmap.createBitmap(MyMethod.drawableToBitmap(MyMethod.getWallpaper(context)), 0, getStatusBarHeight(context), MyMethod.drawableToBitmap(MyMethod.getWallpaper(context)).getWidth(), MyMethod.drawableToBitmap(MyMethod.getWallpaper(context)).getHeight() - MyMethod.getStatusBarHeight(context) - MyMethod.getNavigationBarHeight(context));
            cacheBackground = new BitmapDrawable(context.getResources(), perfect);
            return cacheBackground;
        } catch (Exception e) {
            Log.e("getBackground", e.toString());
            return null;
        }
    }

    /**
     * Lấy Drawable hình nền cho statusbar
     * truyền vào context
     */
    public static Drawable getStatusBackground(Context context) {
        try {
            Bitmap wallpaper = MyMethod.drawableToBitmap(MyMethod.getWallpaper(context));
            Bitmap statusBarBitmap = Bitmap.createBitmap(wallpaper, 0, 0, wallpaper.getWidth(), MyMethod.getStatusBarHeight(context));

            return new BitmapDrawable(context.getResources(), statusBarBitmap);
        } catch (Exception e) {
            Log.e("getStatusBackground", e.toString());
            return null;
        }
    }

    /**
     * Lấy Drawable hình nền cho navigationbar
     * truyền vào context
     */
    public static Drawable getNavigationBackground(Context context) {
        try {
            Bitmap navigationBarBitmap = Bitmap.createBitmap(MyMethod.drawableToBitmap(MyMethod.getWallpaper(context)), 0, MyMethod.drawableToBitmap(MyMethod.getWallpaper(context)).getHeight() - MyMethod.getNavigationBarHeight(context), MyMethod.drawableToBitmap(MyMethod.getWallpaper(context)).getWidth(), MyMethod.getNavigationBarHeight(context));
            return new BitmapDrawable(context.getResources(), navigationBarBitmap);
        } catch (Exception e) {
            Log.e("getNavigationBackground", e.toString());
            return null;
        }
    }

    /**
     * Gọi giao diện thay đổi hình nền thiết bị
     * truyền vào context
     */
    public static void showChangeWallpaper(Context context) {
        try {
            context.startActivity(new Intent(Intent.ACTION_SET_WALLPAPER));
        } catch (Exception e) {
            Log.d("showChangeWallpaper", e.toString());
        }
    }

    /**
     * Vẽ chữ lên map
     * truyền vào context và text và id và color
     */
    public static Bitmap drawText(Context context, String text, int id, int color) {
        try {
            BitmapDrawable bd = (BitmapDrawable) ContextCompat.getDrawable(context, id);
            int height = bd.getBitmap().getHeight();
            int width = bd.getBitmap().getWidth();
            Bitmap.Config conf = Bitmap.Config.ARGB_8888;
            Paint paint = new Paint();
            paint.setTextSize(width / 2);
            paint.setFakeBoldText(true);
            paint.setColor(color);
            Bitmap bmp = Bitmap.createBitmap(width, height, conf);
            Canvas canvas1 = new Canvas(bmp);
            canvas1.drawBitmap(BitmapFactory.decodeResource(context.getResources(),
                    id), 0, 0, paint);
            canvas1.drawText(text, width / 3, height - width, paint);
            return bmp;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Vẽ map bằng các thông số đầu vào
     * truyền vào loadingView,context,map,arrayLocations,infoArrayList và spinner
     */
    public static void drawMap(final LoadingView loadingView, final Context context, final SupportMapFragment map, final MyLocation[] arrayLocations, final ArrayList<UserInfo> infoArrayList, final MaterialSpinner spinner) {
        try {
            map.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    new LoadingAll(loadingView, googleMap, context, infoArrayList, spinner).execute();
                    new Loading(loadingView, googleMap, context, arrayLocations, spinner).execute();

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Vẽ map bằng các thông số đầu vào
     * truyền vào loadingView, context, map, infoArrayList, spinner
     */
    public static void drawMap(final LoadingView loadingView, final Context context, final SupportMapFragment map, final ArrayList<UserInfo> infoArrayList, final MaterialSpinner spinner) {
        try {
            map.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {

                    new LoadingAll(loadingView, googleMap, context, infoArrayList, spinner).execute();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static void drawMapNotLoading(final Context context, final SupportMapFragment map, final ArrayList<UserInfo> infoArrayList, final MaterialSpinner spinner, final boolean isMoveMap) {
        try {
            map.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {

                    new LoadingAllNotLoading(googleMap, context, infoArrayList, spinner, isMoveMap).execute();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    //  Ham tra ve so thong bao cua menu neu co
    public static int getBadgeNumMenu(String s) {
        switch (s) {
            case "Giao dịch":
                return Model.inst().getConfigValue(Const.ConfigKeys.TransactionWorking, 0);
            case "Đơn hàng":
                //return LocalDB.inst().countOrderUnSent();
                return 0;
            default:
                return 0;
        }
    }


    public static void callLog(Context context) {
        try {
            Intent showCallLog = new Intent();
            showCallLog.setAction(Intent.ACTION_VIEW);
            showCallLog.setType(CallLog.Calls.CONTENT_TYPE);
            context.startActivity(showCallLog);
        } catch (Exception e) {
            callPhone(context);
        }
    }

    public static Bitmap cropCircleBitmap(Bitmap bitmap) {
        try {
            Bitmap output;
            if (bitmap.getWidth() > bitmap.getHeight()) {
                output = Bitmap.createBitmap(bitmap.getHeight(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            } else {
                output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getWidth(), Bitmap.Config.ARGB_8888);
            }
            Canvas canvas = new Canvas(output);
            final int color = 0xff424242;
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

            float r = 0;

            if (bitmap.getWidth() > bitmap.getHeight()) {
                r = bitmap.getHeight() / 2;
            } else {
                r = bitmap.getWidth() / 2;
            }
            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawCircle(r, r, r, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);
            return output;
        } catch (Exception e) {
            e.printStackTrace();
            return bitmap;
        }
    }

    public static String getAmount(ArrayList<OrderDetail> arr) {
        try {
            float result = 0;
            for (int i = 0; i < arr.size(); i++) {
                result = result + (arr.get(i).quantity * arr.get(i).unitprice - arr.get(i).discountAmount) * ((100 - arr.get(i).discountPercent) / 100);
            }
            return Utils.formatFloat(result);
        } catch (NullPointerException e) {
            e.printStackTrace();
            return "0";
        }
    }

    public static String getAmountSale(ArrayList<OrderDetail> arr) {
        try {
            float result = 0;
            for (int i = 0; i < arr.size(); i++) {
                result = result + arr.get(i).quantity * arr.get(i).unitprice;
            }
            return Utils.formatFloat(result);
        } catch (NullPointerException e) {
            e.printStackTrace();
            return "0";
        }
    }

    public static String getUrlImage(String link) {
        return link == null ? "" : Const.LinkReportImage + link.replace("\\", "/");
    }

    public static String getUrlCustomerImage(String link) {

        return link == null ? "" : Const.LinkCustomerImage + link.replace("\\", "/");
    }

    public static String getUrlSurveyImage(String link) {

        return link == null ? "" : Const.LinkSurveyImage + link.replace("\\", "/");
    }

    public static String getUrlProductImage(String link) {
        return link == null ? "" : Const.LinkItemImage + link.replace("\\", "/");
    }

    public static void restartApp(Context context) {
        try {
            BackgroundService.stopInstance();
            Intent intent = new Intent(context, Home.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            ((Activity) context).finish();
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static List<Integer> getListIDHeader(ArrayList<SurveyHeader> arrData) {
        List<Integer> ids = new ArrayList<>(arrData.size());
        for (SurveyHeader header : arrData) {
            ids.add(header.id);
        }
        return ids;
    }

    public static List<Integer> getListIDLine(ArrayList<SurveyLine> arrData) {
        List<Integer> ids = new ArrayList<>(arrData.size());
        for (SurveyLine line : arrData) {
            ids.add(line.id);
        }
        return ids;
    }

    public static List<Integer> getListID(int type, ArrayList<Object> arrData) {
        List<Integer> ids = new ArrayList<>(arrData.size());
        switch (type) {
            case 0:
                for (Object campaign : arrData) {
                    ids.add(((SurveyCampaign) campaign).id);
                }
                break;
            case 1:
                for (Object header : arrData) {
                    ids.add(((SurveyHeader) header).id);
                }
                break;
            case 2:
                for (Object line : arrData) {
                    ids.add(((SurveyLine) line).id);
                }
                break;
            default:
                break;
        }

        return ids;
    }

    public static boolean isCanNotice() {
        try {
            if (isVisible(Home.bindingRight.inStore.linearInStore) || isVisible(Home.bindingRight.orderProduct.linearProductOfOrder) || isVisible(Home.bindingRight.reasonNotOrder.linearReasonNotOrder)) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    public static List<Integer> getListIDOrder(ArrayList<Order> dataOrder) {
        List<Integer> result = new ArrayList<>(dataOrder.size());
        for (Order order : dataOrder) {
            result.add(order.rowId);
        }
        return result;
    }


    private static class Loading extends AsyncTask<PolylineOptions, Void, PolylineOptions> {
        LoadingView lv;
        GoogleMap googleMap;
        Context context;
        boolean flag = true;// kiểm tra nếu ở 1 chỗ
        MyLocation[] arrayLocations;
        ArrayList<MyLocation> myLocations = new ArrayList<>();
        ArrayList<String> myAddress = new ArrayList<>();
        ArrayList<MarkerOptions> myMarker = new ArrayList<>();
        ArrayList<Float> myAccuracy = new ArrayList<>();
        MaterialSpinner spinner;

        public Loading(LoadingView loadingView, GoogleMap googleMap, Context context, MyLocation[] arrayLocations, MaterialSpinner spinner) {
            lv = loadingView;
            this.googleMap = googleMap;
            this.context = context;
            this.arrayLocations = arrayLocations;
            this.spinner = spinner;
        }

        @Override
        protected PolylineOptions doInBackground(PolylineOptions... params) {
            ArrayList<LatLng> points = new ArrayList<>();
            PolylineOptions lineOptions = new PolylineOptions();

            for (MyLocation location : arrayLocations) {
                if (location.locationDate != 0 && location.distanceMeter != 0) {
                    points.add(new LatLng(location.latitude, location.longitude));
                    flag = false;
                    myLocations.add(location);
                    if (location.address == null || location.address.isEmpty()) {
                        location.address = MyMethod.getAddress(location.latitude, location.longitude, context);
                    }
                    myAddress.add(location.address);
                    if (myLocations.size() == 1) {
                        if (location.milisecFreezed / 60000 > 0) {
                            myMarker.add(new MarkerOptions()
                                    .position(new LatLng(location.latitude, location.longitude))
                                    .title(myAddress.get(myAddress.size() - 1) + "\n" + Utils.long2String(location.locationDate))
                                    .snippet("Điểm đầu - Thời gian dừng : " + Utils.minute2String((location.milisecFreezed / 60000)))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_btn)));
                        } else {
                            if (location.milisecElapsed != 0) {
                                myMarker.add(new MarkerOptions()
                                        .position(new LatLng(location.latitude, location.longitude))
                                        .title(myAddress.get(myAddress.size() - 1) + "\n" + Utils.long2String(location.locationDate))
                                        .snippet("Điểm đầu - " + "Tốc độ : " + location.distanceMeter * 3600 / location.milisecElapsed + "km/h ")
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_btn)));
                            } else {
                                myMarker.add(new MarkerOptions()
                                        .position(new LatLng(location.latitude, location.longitude))
                                        .title(myAddress.get(myAddress.size() - 1) + "\n" + Utils.long2String(location.locationDate))
                                        .snippet("Điểm đầu - " + "Tốc độ : " + "0 km/h ")
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_btn)));
                            }
                        }

                        myAccuracy.add(location.accuracy);
                    } else {
                        if ((myLocations.get(myLocations.size() - 1).milisecFreezed / 60000) > 0) {
                            myMarker.add(new MarkerOptions()
                                    .position(new LatLng(location.latitude, location.longitude))
                                    .title(myAddress.get(myAddress.size() - 1) + "\n" + Utils.long2String(location.locationDate))
                                    .snippet("Thời gian dừng : " + Utils.minute2String((myLocations.get(myLocations.size() - 1).milisecFreezed / 60000)))
                            );
                            myAccuracy.add(location.accuracy);
                        } else {
                            if (location.milisecElapsed != 0) {
                                myMarker.add(new MarkerOptions()
                                        .position(new LatLng(location.latitude, location.longitude))
                                        .title(myAddress.get(myAddress.size() - 1) + "\n" + Utils.long2String(location.locationDate))
                                        .snippet("Tốc độ : " + location.distanceMeter * 3600 / location.milisecElapsed + "km/h ")
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.dot_btn)));
                            } else {
                                myMarker.add(new MarkerOptions()
                                        .position(new LatLng(location.latitude, location.longitude))
                                        .title(myAddress.get(myAddress.size() - 1) + "\n" + Utils.long2String(location.locationDate))
                                        .snippet("Tốc độ : " + "0 km/h ")
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.dot_btn)));
                            }
                            myAccuracy.add(location.accuracy);
                        }

                    }
                    // }
                }
            }
            if (flag) {
                myMarker.add(new MarkerOptions()
                        .position(new LatLng(arrayLocations[0].latitude, arrayLocations[0].longitude))
                        .title(MyMethod.getAddress(arrayLocations[0].latitude, arrayLocations[0].longitude, context))
                        .snippet("Điểm cuối : " + Utils.minute2String((arrayLocations[0].milisecFreezed / 60000)))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.here_btn)));
                myAccuracy.add(arrayLocations[0].accuracy);
            }
            lineOptions.color(Color.BLUE);
            lineOptions.addAll(points);
            lineOptions.width(3);
            return lineOptions;
        }

        @Override
        protected void onPostExecute(PolylineOptions result) {
            googleMap.addPolyline(result);
            for (int i = 0; i < myMarker.size(); i++) {
                if (!flag && i == myMarker.size() - 1) {
                    String snippet = myMarker.get(i).getSnippet();
                    myMarker.get(i).snippet("Điểm cuối - " + snippet).icon(BitmapDescriptorFactory.fromResource(R.drawable.here_btn));
                    Marker markerEnd = googleMap.addMarker(myMarker.get(i));
                    Home.endMarker = markerEnd;
                    markerEnd.showInfoWindow();
                    Home.currentPostition = markerEnd.getPosition();
                    RightFragment.hashMarker.put(markerEnd, myAccuracy.get(i));
                } else if (!flag && i == myMarker.size() - 1) {
                    RightFragment.hashMarker.put(googleMap.addMarker(myMarker.get(i)), myAccuracy.get(i));
                } else if (myMarker.get(i).getIcon() == null) {
                    myMarker.get(i).icon(BitmapDescriptorFactory.fromBitmap(drawText(context, String.valueOf(i), R.drawable.stop_btn, Color.RED)));
                    RightFragment.hashMarker.put(googleMap.addMarker(myMarker.get(i)), myAccuracy.get(i));
                } else {
                    RightFragment.hashMarker.put(googleMap.addMarker(myMarker.get(i)), myAccuracy.get(i));
                }
            }
            CameraPosition newCamPos;
            if (!flag) {
                newCamPos = new CameraPosition(new LatLng(myLocations.get(myLocations.size() - 1).latitude, myLocations.get(myLocations.size() - 1).longitude),
                        13f,
                        googleMap.getCameraPosition().tilt, //use old tilt
                        googleMap.getCameraPosition().bearing); //use old bearing
            } else {
                newCamPos = new CameraPosition(new LatLng(arrayLocations[arrayLocations.length - 1].latitude, arrayLocations[arrayLocations.length - 1].longitude),
                        13f,
                        googleMap.getCameraPosition().tilt, //use old tilt
                        googleMap.getCameraPosition().bearing); //use old bearing
            }
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
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(newCamPos), 4000, null);
            spinner.setEnabled(true);
            lv.setLoading(false);

        }


//    public static void blur(Context context, ViewGroup viewGroup) {
//        Blurry.with(context)
//                .radius(25)
//                .sampling(2)
//                .async()
//                .animate(500)
//                .onto(viewGroup);
//    }
    }

    private static class LoadingAll extends AsyncTask<Void, Void, Void> {
        LoadingView lv;
        GoogleMap googleMap;
        Context context;
        ArrayList<UserInfo> infoArrayList = new ArrayList<>();
        MaterialSpinner spinner;
        ArrayList<MarkerOptions> myMarkers = new ArrayList<>();
        ArrayList<Float> myAccuracy = new ArrayList<>();
        HashMap<Integer, Integer> tempId = new HashMap<>();//ew

        public LoadingAll(LoadingView loadingView, GoogleMap googleMap, Context context, ArrayList<UserInfo> infoArrayList, MaterialSpinner spinner) {
            lv = loadingView;
            this.googleMap = googleMap;
            this.context = context;
            this.infoArrayList = infoArrayList;
            this.spinner = spinner;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            for (int i = 0; i < myMarkers.size(); i++) {
                Marker marker = googleMap.addMarker(myMarkers.get(i));
                RightFragment.hashUserMarker.put(tempId.get(i), marker);
                RightFragment.hashMarker.put(marker, myAccuracy.get(i));
            }
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
            googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    if (marker.getSnippet().contains(context.getString(R.string.newlocation))) {
                        int position = getIndex(spinner, marker.getTitle().split("\n")[0]) + 1;
                        spinner.setSelection(position);
                    }
                }
            });
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(17, 107), 5.5f);
            googleMap.animateCamera(cameraUpdate);
            spinner.setEnabled(true);
            lv.setLoading(false);
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... params) {
            for (int i = 0; i < infoArrayList.size(); i++) {
                if (infoArrayList.get(i).locationDate > 0) {
                    String address = infoArrayList.get(i).address;
                    if (address == null || address.isEmpty()) {
                        address = MyMethod.getAddress(infoArrayList.get(i).latitude, infoArrayList.get(i).longitude, context);
                    }

                    myMarkers.add(new MarkerOptions()
                            .position(new LatLng(infoArrayList.get(i).latitude, infoArrayList.get(i).longitude))
                            .title(infoArrayList.get(i).fullname + "\n" + address + "\n" + Utils.long2String(infoArrayList.get(i).trackingDate))
                            .snippet(context.getString(R.string.newlocation))
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_dms_btn)));
                    tempId.put(myMarkers.size() - 1, infoArrayList.get(i).id_employee);
                    myAccuracy.add(infoArrayList.get(i).accuracy);
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            spinner.setEnabled(false);
        }
    }

    private static class LoadingAllNotLoading extends AsyncTask<Void, Void, Void> {
        GoogleMap googleMap;
        Context context;
        ArrayList<UserInfo> infoArrayList = new ArrayList<>();
        MaterialSpinner spinner;
        ArrayList<MarkerOptions> myMarkers = new ArrayList<>();
        ArrayList<Float> myAccuracy = new ArrayList<>();
        HashMap<Integer, Integer> tempId = new HashMap<>();
        boolean isMoveMap;

        public LoadingAllNotLoading(GoogleMap googleMap, Context context, ArrayList<UserInfo> infoArrayList, MaterialSpinner spinner, boolean isMoveMap) {
            this.googleMap = googleMap;
            this.context = context;
            this.infoArrayList = infoArrayList;
            this.spinner = spinner;
            this.isMoveMap = isMoveMap;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (googleMap != null) googleMap.clear();
            for (int i = 0; i < myMarkers.size(); i++) {
                Marker marker = googleMap.addMarker(myMarkers.get(i));
                RightFragment.hashUserMarker.put(tempId.get(i), marker);
                RightFragment.hashMarker.put(marker, myAccuracy.get(i));
            }
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
            googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    if (marker.getSnippet().contains(context.getString(R.string.newlocation))) {
                        int position = getIndex(spinner, marker.getTitle().split("\n")[0]) + 1;
                        spinner.setSelection(position);
                    }
                }
            });
            if (isMoveMap) {
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(17, 107), 5.5f);
                googleMap.animateCamera(cameraUpdate);
            }

            spinner.setEnabled(true);
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... params) {
            for (int i = 0; i < infoArrayList.size(); i++) {
                if (infoArrayList.get(i).locationDate > 0) {
                    String address = infoArrayList.get(i).address;
                    if (address == null || address.isEmpty()) {
                        address = MyMethod.getAddress(infoArrayList.get(i).latitude, infoArrayList.get(i).longitude, context);
                    }
                    myMarkers.add(new MarkerOptions()
                            .position(new LatLng(infoArrayList.get(i).latitude, infoArrayList.get(i).longitude))
                            .title(infoArrayList.get(i).fullname + "\n" + address + "\n" + Utils.long2String(infoArrayList.get(i).trackingDate))
                            .snippet(context.getString(R.string.newlocation))
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_dms_btn)));
                    tempId.put(myMarkers.size() - 1, infoArrayList.get(i).id_employee);
                    myAccuracy.add(infoArrayList.get(i).accuracy);
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            spinner.setEnabled(false);
        }
    }

    public static int getIndex(MaterialSpinner spinner, String myString) {
        int index = 0;
        try {
            for (int i = 0; i < spinner.getAdapter().getCount() - 1; i++) {
                if (((UserInfo) spinner.getItemAtPosition(i)).fullname.equalsIgnoreCase(myString)) {
                    index = i;
                    break;
                }
            }
        } catch (Exception e) {
            Log.e("getIndex", e.toString());
            return index;
        }
        return index;
    }


    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return fastBlur(bitmap, 50);
    }

    public static int getStatusBarHeight(Context context) {
        try {
            int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                return context.getResources().getDimensionPixelSize(resourceId);
            }
        } catch (Exception e) {
            Log.e("", e.toString());
            return 0;
        }
        return 0;
    }

    private static Bitmap fastBlur(Bitmap sentBitmap, int radius) {
        Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        Log.e("pix", w + " " + h + " " + pix.length);
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        Log.e("pix", w + " " + h + " " + pix.length);
        bitmap.setPixels(pix, 0, w, 0, 0, w, h);

        return (bitmap);
    }

    public static int getNavigationBarHeight(Context context) {
        try {
            int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0) {
                return context.getResources().getDimensionPixelSize(resourceId);
            }
        } catch (Exception e) {
            Log.e("getNavigationBarHeight", e.toString());
            return 0;
        }
        return 0;
    }


    public static void hideKeyboardAll(Activity activity) {
        try {
            InputMethodManager imm = (InputMethodManager) activity
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm.isAcceptingText()) {
                View view = activity.getCurrentFocus();
                if (view != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int pxToDp(int px, Context context) {
        try {
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
            return dp;
        } catch (Exception e) {
            e.printStackTrace();
            return px;
        }
    }

    public static void hideKeyboardFromView(Context context, View v) {
        try {
            InputMethodManager imm = (InputMethodManager) context
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm.isAcceptingText()) {
                if (v != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isAccessibilitySettingsOn(Context mContext) {
        int accessibilityEnabled = 0;
        final String service = mContext.getPackageName() + "/" + AppLockService.class.getCanonicalName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ACCESSIBILITY_ENABLED);
            Log.v("isAccessibility", "accessibilityEnabled = " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            Log.e("isAccessibility", "Error finding setting, default accessibility to not found: "
                    + e.getMessage());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            Log.v("isAccessibility", "***ACCESSIBILITY IS ENABLED*** -----------------");
            String settingValue = Settings.Secure.getString(
                    mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();

                    Log.v("isAccessibility", "-------------- > accessibilityService :: " + accessibilityService + " " + service);
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        Log.v("isAccessibility", "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }
        } else {
            Log.v("isAccessibility", "***ACCESSIBILITY IS DISABLED***");
        }

        return false;
    }

    public static void animateMarker(final Marker marker, final UserInfo user,
                                     final boolean hideMarker, final GoogleMap map) {
        try {
            final LatLng toPosition = new LatLng(user.latitude, user.longitude);
            final Handler handler = new Handler();
            final long start = SystemClock.uptimeMillis();
            Projection proj = map.getProjection();
            Point startPoint = proj.toScreenLocation(marker.getPosition());
            final LatLng startLatLng = proj.fromScreenLocation(startPoint);
            final long duration = 500;
            final Interpolator interpolator = new LinearInterpolator();
            Marker temp = marker;
            temp.setPosition(toPosition);
            RightFragment.hashUserMarker.put(user.id_employee, temp);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    long elapsed = SystemClock.uptimeMillis() - start;
                    float t = interpolator.getInterpolation((float) elapsed
                            / duration);
                    double lng = t * toPosition.longitude + (1 - t)
                            * startLatLng.longitude;
                    double lat = t * toPosition.latitude + (1 - t)
                            * startLatLng.latitude;
                    marker.setPosition(new LatLng(lat, lng));
                    marker.hideInfoWindow();
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(marker.getPosition(), map.getCameraPosition().zoom);
                    map.animateCamera(cameraUpdate);
                    marker.showInfoWindow();
                    Home.currentPostition = marker.getPosition();

                    if (t < 1.0) {
                        // Post again 16ms later.
                        handler.postDelayed(this, 16);
                    } else {
                        if (hideMarker) {
                            marker.setVisible(false);
                        } else {
                            marker.setVisible(true);
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void openCHPlay(Context context, String s) {
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=" + s)));
        } catch (android.content.ActivityNotFoundException anfe) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/search?q=" + s)));
        }
    }

    //for android 6.0
}
