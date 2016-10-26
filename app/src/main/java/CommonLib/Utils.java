package CommonLib;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.vietdms.mobile.dmslauncher.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;
import java.util.regex.Pattern;

/**
 * Created by My PC on 30/01/2016.
 */
public class Utils {
    public static boolean getBit(int value, int index) {
        return (value & (1 << index)) != 0;
    }

    public static long date2long(Date date) {
        return date.getTime() - 25200000;
    }

    public static Date long2date(long epochMs) {
        return new Date(epochMs + 25200000);
    }

    public static String long2String(long epochMs) {
        Date date = long2date(epochMs);
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        return df.format(date);
    }

    public static int long2Day(long epochMs) {
        return (int) (epochMs / (24 * 60 * 60 * 1000));
    }

    public static String long2Date(long epochMs) {
        Date date = long2date(epochMs);
        DateFormat df = new SimpleDateFormat("dd/MM");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        return df.format(date);
    }

    public static String long2DateFull(long epochMs) {
        Date date = long2date(epochMs);
        DateFormat df = new SimpleDateFormat("dd/MM/yy");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        return df.format(date);
    }

    public static String long2HourMinute(long epochMs) {
        Date date = long2date(epochMs);
        DateFormat df = new SimpleDateFormat("dd/MM HH:mm");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        return df.format(date);
    }

    public static String long2HourMinuteYear(long epochMs) {
        Date date = long2date(epochMs);
        DateFormat df = new SimpleDateFormat("dd/MM/y HH:mm");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        return df.format(date);
    }

    public static String long2HourMinuteDate(long epochMs) {
        Date date = long2date(epochMs);
        DateFormat df = new SimpleDateFormat("HH:mm dd/MM/yy");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        return df.format(date);
    }

    public static String long2HourMinuteSecond(long epochMs) {
        Date date = long2date(epochMs);
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        return df.format(date);
    }

    public static String minute2String(long minute) {
        int day = (int) minute / 1440;
        int hour = (int) minute / 60;
        int minutes = (int) minute % 60;
        if (minute >= 1440) return day + " ngày " + hour + " giờ " + minutes + " phút";
        else if (minute >= 60)
            return hour + " giờ " + minutes + " phút";
        else return minutes + " phút";
    }

    public static String minute2HHmm(long minute) {
        int day = (int) minute / 1440;
        int hour = (int) minute / 60 + day * 24;
        int minutes = (int) minute % 60;
        if (minutes < 10)
            return hour + ":0" + minutes;
        else return hour + ":" + minutes;
    }


    public static String statusOrder(int status) {
        switch (status) {
            case 0:
                return "Mới";
            case 1:
                return "Xác nhận";
            case 2:
                return "Mở lại";
            case 3:
                return "Đã duyệt";
            case 4:
                return "Đã hủy";
            default:
                return "Chưa rõ";
        }
    }

    public static String statusApproval(int status) {
        switch (status) {
            case 0:
                return "Chưa duyệt";
            case 1:
                return "Đã duyệt";
            case 2:
                return "Từ chối";
            default:
                return "Chưa rõ";
        }
    }

    public static Drawable statusTransaction(Context context, int status) {
        switch (status) {
            case 0://mới
                return ContextCompat.getDrawable(context, R.drawable.transaction_btn);
            case 1://ghé thăm
                return ContextCompat.getDrawable(context, R.drawable.report_btn);
            case 2://đặt hàng
                return ContextCompat.getDrawable(context, R.drawable.btn_order_list);
            case 3://đặt hàng điện thoại
                return ContextCompat.getDrawable(context, R.drawable.phoneaction_btn);
            case 4://không đặt hàng
                return ContextCompat.getDrawable(context, R.drawable.none_order_btn);
            case 5://rời cửa hàng
                return ContextCompat.getDrawable(context, android.R.drawable.sym_call_missed);
            case 6://cập nhật tồn kho
                return ContextCompat.getDrawable(context, R.drawable.btn_inventory);
            case 7://ghi chú
                return ContextCompat.getDrawable(context, R.drawable.note_btn);
            case 8:// nhập tồn kho cá nhân
                return ContextCompat.getDrawable(context, R.drawable.input_inventory_btn);
            case 9://hàng mẫu
                return ContextCompat.getDrawable(context, R.drawable.product_sample_btn);
            case 10://hàng trưng bày
                return ContextCompat.getDrawable(context, R.drawable.product_poster_btn);
            case 11://phát sinh đơn hàng
                return ContextCompat.getDrawable(context, R.drawable.ordering_btn);
            case 12://cập nhật vị trí
                return ContextCompat.getDrawable(context, R.drawable.getlocation_btn);
            case 13://cập nhật hình ảnh
                return ContextCompat.getDrawable(context, R.drawable.image_btn);
            case 14://giao dịch
                return ContextCompat.getDrawable(context, R.drawable.transaction_btn);
            case 99://kết thúc
                return ContextCompat.getDrawable(context, R.drawable.check_btn);


            default:
                return ContextCompat.getDrawable(context, R.drawable.transaction_btn);
        }
    }

    public static String statusTransaction(int status, int id_employee) {

        switch (status) {
            case 0:
                return "Chưa GD";
            case 1:
                return "Đang GD";
            case 2:
                return "Có ĐH";
            case 3:
                return "Không ĐH";
            case 5:
                return "Đã kết thúc";
            case 15:
                return "Đang GD";
            case 99:
                return "Đã kết thúc";
            case 7://ghi chu
                return id_employee == 0 ? "Chưa có người nhận" : "Đã có người nhận";
            default:
                return "Chưa xác định : " + status;
        }
    }


    public static Bitmap drawText(Bitmap bm, String text) {
        try {
            Bitmap bm_new = bm.copy(Bitmap.Config.ARGB_8888, true);
            Canvas canvas = new Canvas(bm_new);
            Paint paint = new Paint();
            paint.setColor(Color.RED); // Text Color
            paint.setTextSize(20); // Text Size
            paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);
            canvas.drawText(text, 10, bm.getHeight() - 10, paint);
            return bm_new;
        } catch (Exception ex) {
            return bm;
        }
    }

    public static byte[] getScaledImage(String filename, int maxLongEdge, int maxSize, String text) {
        byte[] result = null;
        FileInputStream fis = null;
        ByteArrayOutputStream stream = null;
        try {
            File file = new File(filename);
//            long filesize = file.length();
//            if (filesize <= maxSize) {
//                result = new byte[(int) filesize];
//                fis = new FileInputStream(file);
//                fis.read(result);
//                return result;
//            }
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filename, options);
            int width = options.outWidth;
            int height = options.outHeight;
            if (width <= 0 || height <= 0) {
                return null;
            }
            options = new BitmapFactory.Options();
            options.inSampleSize = (width > height ? width : height) / maxLongEdge;
            fis = new FileInputStream(file);
            Bitmap bm = BitmapFactory.decodeStream(fis, null, options);
            bm = drawText(bm, text);
            stream = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 90, stream);
            if (stream.size() <= maxSize) {
                result = stream.toByteArray();
                return result;
            }
            int w = bm.getWidth();
            int h = bm.getHeight();
            boolean canScale = w > maxLongEdge || h > maxLongEdge;
            if (canScale) {
                if (w > h) {
                    h = h * maxLongEdge / w;
                    w = maxLongEdge;
                } else {
                    w = w * maxLongEdge / h;
                    h = maxLongEdge;
                }
                bm = Bitmap.createScaledBitmap(bm, w, h, true);
                bm = drawText(bm, text);
                stream.reset();
                bm.compress(Bitmap.CompressFormat.JPEG, 90, stream);
                if (stream.size() <= maxSize) {
                    result = stream.toByteArray();
                    return result;
                }
            }
            stream.reset();
            bm.compress(Bitmap.CompressFormat.JPEG, 60, stream);
            if (stream.size() <= maxSize) {
                result = stream.toByteArray();
                return result;
            }
        } catch (Exception ex) {
            Log.e("getScaledImage", ex.toString());
        } finally {
            try {
                if (fis != null) fis.close();
                if (stream != null) stream.close();
            } catch (Exception ex) {
            }
        }
        return result;
    }

    public static byte[] compressBitmap(Bitmap bitmap) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
            return stream.toByteArray();
        } catch (Exception ex) {
            return null;
        }
    }


    public static Bitmap decompressBitmap(byte[] data) {
        try {
            return BitmapFactory.decodeByteArray(data, 0, data.length);
        } catch (Exception ex) {
            return null;
        }
    }

    public static String strToSort(String s) {
        int len = s == null ? 0 : s.length();
        if (len == 0) return "";
        do {
            char c = s.charAt(0);
            if (c <= 32 || c == 160) {
                s = s.substring(1);
            } else break;
        }
        while (s.length() > 0);
        s = unAccent(s);
        return s.toLowerCase();
    }

    private static Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");

    public static String unAccent(String s) {
        if (s == null) {
            return "";
        } else {
            String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
            char[] arr = pattern.matcher(temp).replaceAll("").toCharArray();
            for (int i = 0; i < arr.length; i++) {
                if (arr[i] == 'Đ') arr[i] = 'D';
                else if (arr[i] == 'đ') arr[i] = 'd';
            }
            return String.valueOf(arr);
        }
    }

    public static String formatLocale(float price) {
        String result = Float.toString(price);
        try {
            switch (Locale.getDefault().getCountry().toLowerCase()) {
                case "us":
                    result = result.replace(",", "").replace(".", ".");
                    break;
                case "vn":
                    result = result.replace(",", "").replace(".", ",");
                    break;
                default:
                    break;


            }
        } catch (Exception e) {
            return result;
        }
        return result;
    }

    public static String formatLocale(String price) {
        String result = "";
        if (price.charAt(price.length() - 1) == '.' || price.charAt(price.length() - 1) == ',')
            result = price.substring(0, price.length() - 1);
        else result = price;
        try {
            switch (Locale.getDefault().getCountry().toLowerCase()) {
                case "us":
                    result = price.replace(",", "").replace(".", ".");
                    break;
                case "vn":
                    result = price.replace(".", "").replace(",", ".");
                    break;
                default:
                    break;


            }
        } catch (Exception e) {
            return result;
        }
        return result;
    }

    public static String formatFloat(float value) {
        String result;
        try {
            DecimalFormat formatter = new DecimalFormat("#,###,###.##");
            result = formatter.format(value);
        } catch (Exception e) {
            return Float.toString(value);
        }

        return result;
    }

    public static long getDayBegin(long ms) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(ms);
        Calendar end = Calendar.getInstance();
        end.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        return end.getTimeInMillis();
    }

    public static long getMonthBegin(long ms) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(ms);
        Calendar end = Calendar.getInstance();
        end.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1, 0, 0, 0);
        return end.getTimeInMillis();
    }

    public static long getDayEnd(long ms) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(ms);
        Calendar end = Calendar.getInstance();
        end.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
        return end.getTimeInMillis();
    }

    public static String long2OverTime(long time) {
        final int SECOND_MILLIS = 1000;
        final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
        final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = Model.getServerTime();
        if (time > now || time <= 0) {
            return null;
        }

        // TODO: localize
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "Ngay bây giờ";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "1 phút trước";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " phút trước";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "1 giờ trước";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " giờ trước";
        } else {
            return long2HourMinuteDate(time);
        }
    }

    public static String getSaleType(String type) {
        if (type == null) return "Sale";
        return Integer.parseInt(type) == 0 ? " Sale" : " PreSale";
    }

    //Conver Co dau khong dau
    private static char[] charA = {'à', 'á', 'ạ', 'ả', 'ã',// 0-&gt;16
            'â', 'ầ', 'ấ', 'ậ', 'ẩ', 'ẫ', 'ă', 'ằ', 'ắ', 'ặ', 'ẳ', 'ẵ'};// a,// ă,// â
    private static char[] charE = {'ê', 'ề', 'ế', 'ệ', 'ể', 'ễ',// 17-&gt;27
            'è', 'é', 'ẹ', 'ẻ', 'ẽ'};// e
    private static char[] charI = {'ì', 'í', 'ị', 'ỉ', 'ĩ'};// i 28-&gt;32
    private static char[] charO = {'ò', 'ó', 'ọ', 'ỏ', 'õ',// o 33-&gt;49
            'ô', 'ồ', 'ố', 'ộ', 'ổ', 'ỗ',// ô
            'ơ', 'ờ', 'ớ', 'ợ', 'ở', 'ỡ'};// ơ
    private static char[] charU = {'ù', 'ú', 'ụ', 'ủ', 'ũ',// u 50-&gt;60
            'ư', 'ừ', 'ứ', 'ự', 'ử', 'ữ'};// ư
    private static char[] charY = {'ỳ', 'ý', 'ỵ', 'ỷ', 'ỹ'};// y 61-&gt;65
    private static char[] charD = {'đ', ' '}; // 66-67
    private static String charact = String.valueOf(charA, 0, charA.length)
            + String.valueOf(charE, 0, charE.length)
            + String.valueOf(charI, 0, charI.length)
            + String.valueOf(charO, 0, charO.length)
            + String.valueOf(charU, 0, charU.length)
            + String.valueOf(charY, 0, charY.length)
            + String.valueOf(charD, 0, charD.length);

    private static char GetAlterChar(char pC) {
        if ((int) pC == 32) {
            return ' ';
        }

        char tam = pC;// Character.toLowerCase(pC);

        int i = 0;
        while (i < charact.length() && charact.charAt(i) != tam) {
            i++;
        }
        if (i < 0 || i > 67)
            return pC;

        if (i == 66 || i == 67) {
            return 'd';
        }
        if (i >= 0 && i <= 16) {
            return 'a';
        }
        if (i >= 17 && i <= 27) {
            return 'e';
        }
        if (i >= 28 && i <= 32) {
            return 'i';
        }
        if (i >= 33 && i <= 49) {
            return 'o';
        }
        if (i >= 50 && i <= 60) {
            return 'u';
        }
        if (i >= 61 && i <= 65) {
            return 'y';
        }
        return pC;
    }

    // 1 : khong biet, 2: 2g, 3: 3g,4: 4g,5 :wifi
    public static int getNetworkType(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                // connected to wifi
                return 5;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                // connected to the mobile provider's data plan
                TelephonyManager mTelephonyManager = (TelephonyManager)
                        context.getSystemService(Context.TELEPHONY_SERVICE);
                int networkType = mTelephonyManager.getNetworkType();
                switch (networkType) {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN:
                        return 2;
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                        /**
                         From this link https://goo.gl/R2HOjR ..NETWORK_TYPE_EVDO_0 & NETWORK_TYPE_EVDO_A
                         EV-DO is an evolution of the CDMA2000 (IS-2000) standard that supports high data rates.

                         Where CDMA2000 https://goo.gl/1y10WI .CDMA2000 is a family of 3G[1] mobile technology standards for sending voice,
                         data, and signaling data between mobile phones and cell sites.
                         */
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                        //Log.d("Type", "3g");
                        //For 3g HSDPA , HSPAP(HSPA+) are main  networktype which are under 3g Network
                        //But from other constants also it will 3g like HSPA,HSDPA etc which are in 3g case.
                        //Some cases are added after  testing(real) in device with 3g enable data
                        //and speed also matters to decide 3g network type
                        //http://goo.gl/bhtVT
                        return 3;
                    case TelephonyManager.NETWORK_TYPE_LTE:
                        //No specification for the 4g but from wiki
                        //I found(LTE (Long-Term Evolution, commonly marketed as 4G LTE))
                        //https://goo.gl/9t7yrR
                        return 4;
                    default:
                        return 1;
                }
            }
        } else {
            return 1;
        }
        return 1;

    }


    public static String to2Character(long l) {
        return l < 10 ? "0" + l : l + "";
    }

    public static String centerString(String str) {//Căn giữa text hóa đơn in
        int len = str.length();
        String output = "";
        if (len < Const.PaperTextNumber) {
            int align = (Const.PaperTextNumber - len) / 2;
            String alignStr = "";
            for (int i = 0; i < align; i++) {
                alignStr += " ";
            }
            output = alignStr + str;
        } else if (len == Const.PaperTextNumber) {
            return str;
        } else {
            int line = len / Const.PaperTextNumber;
            //Xu li tung dong
            String topResult = "";
            int tempLine = 0;
            int tempDownLine = 0;
            for (int i = 1; i <= line; i++) {
                tempLine = i * Const.PaperTextNumber;
                if (!(str.charAt(tempLine) + "").contains(" ") && !(str.charAt(tempLine + 1) + "").contains(" ")) // Nếu xuống dòng không đúng
                {
                    String tempTop = str.substring(0, tempLine - tempDownLine);
                    int postionDownLine = tempTop.lastIndexOf(" ") + 1;
                    topResult += str.substring((i - 1) * Const.PaperTextNumber - tempDownLine, postionDownLine);
                    tempDownLine = tempLine - postionDownLine;
                } else {
                    topResult += str.substring((i - 1) * Const.PaperTextNumber - tempDownLine, tempLine - tempDownLine);
                    tempDownLine = 0;
                }

            }
            output = topResult;
        }
        return output;
    }

    //Định dạng meter -> kilometer
    public static String formatMeter(int totalMeter) {
        final int KILOMETER = 1000;
        if (totalMeter < KILOMETER) {
            return totalMeter + "m";
        } else {
            return totalMeter / KILOMETER + "," + totalMeter % KILOMETER + "km";
        }
    }

    private final static Pattern LTRIM = Pattern.compile("^\\s+");

    public static String ltrim(String s) {
        return LTRIM.matcher(s).replaceAll("");
    }

    private final static Pattern RTRIM = Pattern.compile("\\s+$");

    public static String rtrim(String s) {
        return RTRIM.matcher(s).replaceAll("");
    }

    public static String createRefNo_(int len) {
        final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        SecureRandom rnd = new SecureRandom();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }

}
