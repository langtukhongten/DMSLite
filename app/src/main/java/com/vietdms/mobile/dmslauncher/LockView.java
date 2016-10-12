package com.vietdms.mobile.dmslauncher;

import android.app.ActivityManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vietdms.mobile.dmslauncher.Service.AppLockService;

import java.util.zip.Adler32;
import java.util.zip.CRC32;

import CommonLib.AppManager;
import CommonLib.Const;
import CommonLib.EventPool;
import CommonLib.EventType;
import CommonLib.Model;

public class LockView extends AppCompatActivity implements View.OnClickListener {
    private ImageView imgIcon;
    private TextView txtLabel;
    private ImageButton btnCancel, btnAccept;
    private EditText edPass;
    private String temp = "", lastPackageName = "";
    private LinearLayout passwordView;
    private String packageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_lock_number);
        packageName = getIntent().getBundleExtra("data").getString("lock");
        lastPackageName = getIntent().getBundleExtra("data").getString("pre");
        getId();
        event();


    }

    private void getId() {
        passwordView = (LinearLayout) findViewById(R.id.passwordView);
        btnAccept = (ImageButton) findViewById(R.id.numlock_bRight);
        btnCancel = (ImageButton) findViewById(R.id.numlock_bLeft);
        edPass = (EditText) findViewById(R.id.ed_pass_lock);
        imgIcon = (ImageView) findViewById(R.id.imgIcon);
        txtLabel = (TextView) findViewById(R.id.txtlabel);
        try {
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            lp.setMargins(0, MyMethod.getStatusBarHeight(getApplicationContext()), 0, MyMethod.getNavigationBarHeight(getApplicationContext()));
            passwordView.setLayoutParams(lp);
        } catch (Exception e) {
            Log.e("passwordView", e.toString());
        }
    }

    private void event() {
        findViewById(R.id.request_unlock_btn).setOnClickListener(this);
        findViewById(R.id.numlock_b0).setOnClickListener(this);
        findViewById(R.id.numlock_b1).setOnClickListener(this);
        findViewById(R.id.numlock_b2).setOnClickListener(this);
        findViewById(R.id.numlock_b3).setOnClickListener(this);
        findViewById(R.id.numlock_b4).setOnClickListener(this);
        findViewById(R.id.numlock_b5).setOnClickListener(this);
        findViewById(R.id.numlock_b6).setOnClickListener(this);
        findViewById(R.id.numlock_b7).setOnClickListener(this);
        findViewById(R.id.numlock_b8).setOnClickListener(this);
        findViewById(R.id.numlock_b9).setOnClickListener(this);
        btnAccept.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        try {
            imgIcon.setImageDrawable(AppManager.inst().getIcon(packageName));
            txtLabel.setText(AppManager.inst().getLabel(packageName));
        } catch (Exception e) {
            Log.e("getIconLabel", e.toString());
        }


    }

    public void KillApplication(String KillPackage) {
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        Intent startMain;
        try {
            startMain = Home.manager.getLaunchIntentForPackage(getApplicationContext().getPackageName());
            this.startActivity(startMain);
        } catch (Exception e) {
            startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(startMain);
        }
        am.killBackgroundProcesses(KillPackage);
    }

    @Override
    public void onBackPressed() {
        KillApplication(packageName);
        Log.w("APPLOCK", "Quay lại không mở");
        finish();
    }


    private boolean isOK(String password) {
        Adler32 adler32 = new Adler32();
        CRC32 crc32 = new CRC32();
        int len = password == null ? 0 : password.length();
        byte[] buffer = new byte[len * 2];
        for (int i = 0; i < len; i++) {
            buffer[i * 2] = ((byte) password.charAt(i));
            buffer[i * 2 + 1] = ((byte) (((short) password.charAt(i)) >> 8));
        }
        adler32.update(buffer);
        crc32.update(buffer);
        String hash = String.valueOf(adler32.getValue()) + String.valueOf(crc32.getValue());
        Log.i("PassHash", hash);
        return hash.equals(Model.inst().getConfigValue(Const.ConfigKeys.PassApps));
    }

    private boolean isOKAdmin(String password) {
        Adler32 adler32 = new Adler32();
        CRC32 crc32 = new CRC32();
        int len = password == null ? 0 : password.length();
        byte[] buffer = new byte[len * 2];
        for (int i = 0; i < len; i++) {
            buffer[i * 2] = ((byte) password.charAt(i));
            buffer[i * 2 + 1] = ((byte) (((short) password.charAt(i)) >> 8));
        }
        adler32.update(buffer);
        crc32.update(buffer);
        String hash = String.valueOf(adler32.getValue()) + String.valueOf(crc32.getValue());
        Log.i("PassHash", hash);
        return hash.equals(Model.inst().getConfigValue(Const.ConfigKeys.SystemAdminPassword));
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.request_unlock_btn:
                final AlertDialog.Builder inputAlert = new AlertDialog.Builder(LockView.this);
                inputAlert.setTitle("Yêu cầu mở khóa ứng dụng");
                final EditText userInput = new EditText(LockView.this);
                inputAlert.setView(userInput);
                inputAlert.setPositiveButton("Gửi yêu cầu", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EventPool.control().enQueue(new EventType.EventSendRequestGrantRequest(packageName, AppManager.inst().getLabel(packageName), userInput.getText().toString()));
                        dialog.dismiss();
                        onBackPressed();
                    }
                });
                inputAlert.setNegativeButton("Hủy bỏ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog = inputAlert.create();
                alertDialog.show();
                break;
            case R.id.numlock_b0:
                temp += "0";
                edPass.setText(temp);
                break;
            case R.id.numlock_b1:
                temp += "1";
                edPass.setText(temp);
                break;
            case R.id.numlock_b2:
                temp += "2";
                edPass.setText(temp);
                break;
            case R.id.numlock_b3:
                temp += "3";
                edPass.setText(temp);
                break;
            case R.id.numlock_b4:
                temp += "4";
                edPass.setText(temp);
                break;
            case R.id.numlock_b5:
                temp += "5";
                edPass.setText(temp);
                break;
            case R.id.numlock_b6:
                temp += "6";
                edPass.setText(temp);
                break;
            case R.id.numlock_b7:
                temp += "7";
                edPass.setText(temp);
                break;
            case R.id.numlock_b8:
                temp += "8";
                edPass.setText(temp);
                break;
            case R.id.numlock_b9:
                temp += "9";
                edPass.setText(temp);
                break;
            case R.id.numlock_bLeft:
                if (!temp.isEmpty()) {
                    temp = "";
                    edPass.setText(temp);
                } else onBackPressed();

                break;
            case R.id.numlock_bRight:
                if (isOK(temp) || isOKAdmin(temp)) {
                    EventPool.control().enQueue(new EventType.EventRunAppRequest(packageName));
                    AppLockService.mUnlockMap.put(packageName, true);
                    Log.w("APPLOCK", "Mở khóa ứng dụng");
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Sai mật khẩu", Toast.LENGTH_SHORT).show();
                    temp = "";
                    Log.w("APPLOCK", "Nhập sai mật khẩu");
                    edPass.setText("");
                }
                break;


        }
    }
}
