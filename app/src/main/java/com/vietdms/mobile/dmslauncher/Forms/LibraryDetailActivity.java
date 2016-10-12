package com.vietdms.mobile.dmslauncher.Forms;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.vietdms.mobile.dmslauncher.CustomAdapter.CustomAdapterLibraryDetail;
import com.vietdms.mobile.dmslauncher.CustomClass.LayoutLoadingManager;
import com.vietdms.mobile.dmslauncher.CustomView.LoadingView;
import com.vietdms.mobile.dmslauncher.MyMethod;
import com.vietdms.mobile.dmslauncher.R;
import com.vietdms.mobile.dmslauncher.databinding.ActivityLibraryDetailBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import CommonLib.Const;
import CommonLib.EventPool;
import CommonLib.EventType;
import CommonLib.LibraryDetail;

public class LibraryDetailActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private static final String TAG = "LibraryDetailActivity";
    private ActivityLibraryDetailBinding binding;
    private CustomAdapterLibraryDetail adapter;
    private ArrayList<LibraryDetail> arrayLib;
    private Handler handler;
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
    private int id; // Id của group

    private void processEvent(EventType.EventBase event) {
        switch (event.type) {
            case LoadFileManagerDetails:
                EventType.EventLoadFileManagerDetailsResult fileManagerDetailsResult = (EventType.EventLoadFileManagerDetailsResult) event;
                if (fileManagerDetailsResult.success) {
                    arrayLib.addAll(fileManagerDetailsResult.arrLibraryDetails);
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(this, fileManagerDetailsResult.message, Toast.LENGTH_SHORT).show();
                }
                LayoutLoadingManager.Show_OffLoading(binding.LibraryDetailLoadingView);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_library_detail);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("OnFire"));
        handler = new Handler();
        init();
        getData();
    }

    private void getData() {
        Log.w(TAG, "getData");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                clearAdapter();
                LayoutLoadingManager.Show_OnLoading(binding.LibraryDetailLoadingView, LibraryDetailActivity.this.getString(R.string.load_list_file), 30);
                EventPool.control().enQueue(new EventType.EventLoadFileManagerDetailsRequest(id));
            }
        }, 500);

    }

    private void clearAdapter() {
        arrayLib.clear();
        adapter.notifyDataSetChanged();
    }


    private void init() {
        try {
            Intent intent = getIntent();
            id = intent.getIntExtra("ID", 0);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationIcon(R.drawable.left_arrow_btn);
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        arrayLib = new ArrayList<>();
        adapter = new CustomAdapterLibraryDetail(this, arrayLib);
        binding.content.gridLibraryDetail.setAdapter(adapter);
        binding.content.gridLibraryDetail.setOnItemClickListener(this);
    }

    @Override
    protected void onResume() {
        Log.w(TAG, "onResume");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.w(TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.w(TAG, "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.w(TAG, "onStop");
        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
            //unregisterReceiver(receiver);
            Log.w(TAG, "unregisterReceiver");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        switch (adapterView.getId()) {
            case R.id.gridLibraryDetail:
                //Mở thư mục
                //type = 0 : ảnh = 1: pdf = 2 :youtube
                LibraryDetail temp = adapter.getObjectClick(position);
                Toast.makeText(this, "Mở tập tin : " + temp.description, Toast.LENGTH_SHORT).show();
                Intent detail;
                switch (temp.type) {
                    case 0:
                        detail = new Intent(this, ImageDetailActivity.class);
                        detail.putExtra("ID", temp.id);
                        detail.putExtra("Description", temp.description);
                        detail.putExtra("Url", temp.url);
                        startActivity(detail);
                        break;
                    case 1:
                        //Mở pdf
                        String[] filePath = temp.url.split("\\/");
                        String name = filePath[filePath.length - 1];
                        new DownloadPDF(this, binding.LibraryDetailLoadingView, name).execute(temp.url);
                        break;
                    case 2:
                        detail = new Intent(this, VideoDetailActivity.class);
                        detail.putExtra("ID", temp.id);
                        detail.putExtra("Description", temp.description);
                        detail.putExtra("Url", temp.url);
                        startActivity(detail);
                        break;
                    default:
                        detail = new Intent(this, VideoDetailActivity.class);
                        detail.putExtra("ID", temp.id);
                        detail.putExtra("Description", temp.description);
                        detail.putExtra("Url", temp.url);
                        startActivity(detail);
                        break;
                }

                break;
            default:
                break;

        }
    }

    class DownloadPDF extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;
        String UrlDownload = "";
        String PATH = "";
        private LoadingView loadingView;
        private File fileCheck = null;

        public DownloadPDF(Context context, LoadingView loadingView, String fileName) {
            this.context = context;
            this.loadingView = loadingView;
            PATH = Environment.getExternalStorageDirectory() + "/" + Const.LIBRARYFOLDER + "/" + fileName;
            this.fileCheck = new File(PATH);
        }

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            if (!fileCheck.isFile()) {
                try {
                    URL url = new URL(sUrl[0]);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.connect();
                    if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        return "Server returned HTTP " + connection.getResponseCode()
                                + " " + connection.getResponseMessage();
                    }
                    int fileLength = connection.getContentLength();
                    String[] savePath = sUrl[0].split("\\/");
                    String name = savePath[savePath.length - 1];
                    // download the file
                    input = connection.getInputStream();
                    UrlDownload = MyMethod.createFolder(Const.LIBRARYFOLDER, name).getAbsolutePath();
                    //UrlDownload = Environment.getExternalStorageDirectory() + "/" + Const.LIBRARYFOLDER + "/" + name;
                    output = new FileOutputStream(UrlDownload);
                    byte data[] = new byte[4096];
                    long total = 0;
                    int count;
                    int OldPercent = 0;
                    while ((count = input.read(data)) != -1) {
                        if (isCancelled()) {
                            input.close();
                            return null;
                        }
                        total += count;
                        if (fileLength > 0) // only if total length is known
                        {
                            int newPercent = (int) (total * 100 / fileLength);
                            if (newPercent > OldPercent) {
                                OldPercent = newPercent;
                                publishProgress(newPercent);
                            }
                        }
                        output.write(data, 0, count);
                    }
                } catch (Exception e) {
                    return e.toString();
                } finally {
                    try {
                        if (output != null)
                            output.close();
                        if (input != null)
                            input.close();
                    } catch (IOException ignored) {
                    }
                    if (connection != null)
                        connection.disconnect();
                }
                return null;
            } else
                return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();

        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            //Toast.makeText(context,"Downloading...: "+progress[0] +" %", Toast.LENGTH_SHORT).show();
            // if we get here, length is known, now set indeterminate to false
            LayoutLoadingManager.Show_OnLoading(loadingView, "Đang tải xuống ..", 100);
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            LayoutLoadingManager.Show_OffLoading(loadingView);
            if (!fileCheck.isFile()) {//Nếu chưa tồn tại
                if (result != null) {
                    Toast.makeText(context, "Lỗi tải xuống PDF: " + result, Toast.LENGTH_LONG).show();
                    Log.w(TAG, "Lỗi tải xuống PDF: " + result);
                } else {
                    Log.w(TAG, "Tải xuống PDF thành công");
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    String filename = UrlDownload;
                    Uri uri = Uri.fromFile(new File(filename));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setDataAndType(uri, "application/pdf");
                    try {
                        context.startActivity(intent);
                    } catch (Exception ex) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage(context.getString(R.string.device_not_have_pdf_reader))
                                .setCancelable(false)
                                .setPositiveButton(context.getString(R.string.setup), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        MyMethod.openCHPlay(context, "pdf reader");
                                    }
                                })
                                .setNegativeButton(context.getString(R.string.close), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                }
            } else {//Nếu tồn tại
                Log.w(TAG, "File PDF tồn tại");
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.fromFile(fileCheck);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(uri, "application/pdf");
                try {
                    context.startActivity(intent);
                } catch (Exception ex) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage(context.getString(R.string.device_not_have_pdf_reader))
                            .setCancelable(false)
                            .setPositiveButton(context.getString(R.string.setup), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    MyMethod.openCHPlay(context, "pdf reader");
                                }
                            })
                            .setNegativeButton(context.getString(R.string.close), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        }
    }
}
