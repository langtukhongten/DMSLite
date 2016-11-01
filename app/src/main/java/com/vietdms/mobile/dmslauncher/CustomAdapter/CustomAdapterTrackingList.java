package com.vietdms.mobile.dmslauncher.CustomAdapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.vietdms.mobile.dmslauncher.CustomView.LoadingView;
import com.vietdms.mobile.dmslauncher.Home;
import com.vietdms.mobile.dmslauncher.MyMethod;
import com.vietdms.mobile.dmslauncher.R;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import CommonLib.Const;
import CommonLib.MyLocation;
import CommonLib.Utils;

/**
 * Created by Dms.Tien on 17/03/2016.
 */
public class CustomAdapterTrackingList extends
        ArrayAdapter<MyLocation> {

    Activity context = null;
    ArrayList<MyLocation> myArray = null;
    int layoutId;
    private Bitmap bitmap = null;

    public CustomAdapterTrackingList(Activity context,
                                     int layoutId,
                                     ArrayList<MyLocation> arr) {
        super(context, layoutId, arr);
        this.context = context;
        this.layoutId = layoutId;
        this.myArray = arr;
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
            if (result != null)
                bmImage.setImage(ImageSource.bitmap(result));
            bitmap = result;
        }
    }

    public View getView(int position, View convertView,
                        ViewGroup parent) {
        ViewHolder holder;
        View myView;
        final MyLocation loc = myArray.get(position);
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            myView = inflater.inflate(layoutId, null);
            holder = new ViewHolder();
            holder.txtTT = (TextView)
                    myView.findViewById(R.id.txtTT);
            holder.txtTime = (TextView)
                    myView.findViewById(R.id.txtTime);
            holder.txtTenDuong = (TextView)
                    myView.findViewById(R.id.txtTenDuong);
            holder.txtPin = (TextView)
                    myView.findViewById(R.id.txtPin);
            holder.txtTimeStop = (TextView)
                    myView.findViewById(R.id.txtTimeStop);
            holder.txtGPS = (TextView)
                    myView.findViewById(R.id.txtGPS);
            holder.txtMang = (TextView)
                    myView.findViewById(R.id.txtMang);
            holder.txtAir = (TextView)
                    myView.findViewById(R.id.txtAir);
            holder.imgViewReport = (ImageView) myView.findViewById(R.id.img_report);
            holder.txtReport = (TextView) myView.findViewById(R.id.txt_report);
            holder.lnReport = (LinearLayout) myView.findViewById(R.id.linear_report_image);
            myView.setTag(holder);
        } else {
            myView = convertView;
            holder = (ViewHolder) myView.getTag();

        }
        if (myArray.size() > 0 && position >= 0) {


            holder.txtTime.setText(Utils.long2HourMinute(loc.trackingDate));
            if (loc.getMethod > 0) {
                //co bao cao
                holder.lnReport.setVisibility(View.VISIBLE);
                holder.txtTimeStop.setVisibility(View.GONE);

                if (loc.imageUrl != null && !loc.imageUrl.isEmpty()) {
                    //co image
                    holder.imgViewReport.setVisibility(View.VISIBLE);
                    holder.txtReport.setVisibility(View.VISIBLE);
                    if (loc.imageThumb != null) {

                        holder.imgViewReport.setImageBitmap(loc.imageThumb);
                        holder.txtReport.setText(loc.note);
                        //hien thi imageThumb (luu y kich thuoc anh toi da), click vao mo imageUrl
                    } else {
                        //hien thi thumb mac dinh, click vao mo imageUrl
                        holder.txtReport.setText(loc.note);
                    }
                } else {
                    // khong co image, chi hien thi note
                    holder.imgViewReport.destroyDrawingCache();
                    holder.imgViewReport.setVisibility(View.GONE);
                    holder.txtReport.setVisibility(View.VISIBLE);
                    holder.txtReport.setText(loc.note);
                }
            } else {
                // ko co bao cao
                holder.imgViewReport.destroyDrawingCache();
                holder.txtReport.destroyDrawingCache();
                holder.lnReport.setVisibility(View.GONE);
                holder.txtTimeStop.setVisibility(View.VISIBLE);
                if (loc.milisecFreezed / 60000 > 0) {
                    holder.txtTimeStop.setText(Utils.minute2HHmm(loc.milisecFreezed / 60000));
                    holder.txtTimeStop.setTextColor(Color.RED);
                } else {
                    holder.txtTimeStop.setTextColor(Color.BLACK);
                    holder.txtTimeStop.setText((loc.milisecElapsed == 0 ? 0 : loc.distanceMeter * 3600 / loc.milisecElapsed) + "km/h ");


                }
            }

            if (!loc.address.isEmpty())
                holder.txtTenDuong.setText(loc.address);
            else if (loc.latitude != 0)
                holder.txtTenDuong.setText(MyMethod.getAddress(loc.latitude, loc.longitude, context));
            else holder.txtTenDuong.setText(context.getString(R.string.unlocation));
            holder.txtPin.setText(loc.batteryLevel + "");
            holder.txtTT.setText(position + 1 + "");

            holder.imgViewReport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        final File file = MyMethod.createFolder(Const.REPORTIMAGEDMS, loc.imageUrl);
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
                        imageDownloader.execute(Const.LinkReportImage + loc.imageUrl);
                        txtTitle.setText(loc.note);
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
                                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
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
                    } catch (Exception e) {
                        MyMethod.showToast(Home.bindingRight,context, e.getMessage());
                    }
                }
            });

            if (loc.isGPS == -9) {
                holder.txtGPS.setText("");

            } else {
                if (loc.isGPS == 1) {
                    holder.txtGPS.setText("Bật");
                    holder.txtGPS.setTextColor(Color.GREEN);
                } else {
                    holder.txtGPS.setText("Tắt");
                    holder.txtGPS.setTextColor(Color.RED);
                }
            }
            if (loc.is3G == -9 || loc.isWifi == -9) holder.txtMang.setText("");
            else if (loc.is3G == 1 || loc.isWifi == 1) {
                holder.txtMang.setText("Bật");
                holder.txtMang.setTextColor(Color.GREEN);
            } else {
                holder.txtMang.setText("Tắt");
                holder.txtMang.setTextColor(Color.RED);

            }

            if (loc.isAirplaneMode == -9) holder.txtAir.setText("");
            else if (loc.isAirplaneMode != 1) {
                holder.txtAir.setText("Tắt");
                holder.txtAir.setTextColor(Color.GREEN);
            } else {
                holder.txtAir.setText("Bật");
                holder.txtAir.setTextColor(Color.RED);
            }
        }

        return myView;
    }

    static class ViewHolder {
        TextView txtTT;
        TextView txtTime;
        TextView txtTenDuong;
        TextView txtPin;
        TextView txtTimeStop;
        TextView txtGPS;
        TextView txtMang;
        TextView txtAir;
        ImageView imgViewReport;
        TextView txtReport;
        LinearLayout lnReport;
    }
}
