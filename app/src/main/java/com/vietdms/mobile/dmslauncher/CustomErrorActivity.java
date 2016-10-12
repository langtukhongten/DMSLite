package com.vietdms.mobile.dmslauncher;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;



import CommonLib.SystemLog;
import cat.ereza.customactivityoncrash.CustomActivityOnCrash;

/**
 * Created by ${LTG} was born ${10/12/1994}.
 */
public class CustomErrorActivity extends Activity {
    private ProgressBar progressBar;
    private Intent intent;
    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_custom_error);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        intent = new Intent(getApplicationContext(), Home.class);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        SystemLog.addLog(getApplicationContext(), SystemLog.Type.AppCrash, CustomActivityOnCrash.getAllErrorDetailsFromIntent(getApplicationContext(), getIntent()));
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        session = Session.getDefaultInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("vietdmsreport@gmail.com", "vietdms.com");
            }
        });

        sendLogCrash send = new sendLogCrash();
        send.execute();

    }

    class sendLogCrash extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... params) {
            String message_info = CustomActivityOnCrash.getAllErrorDetailsFromIntent(getApplicationContext(), getIntent());
            Log.e("sendLogCrash", message_info);
            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("vietdmsreport@gmail.com"));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("dmsreport2016@gmail.com"));
                message.setSubject("Báo lỗi phần mềm eDMS Tracking v" + BuildConfig.VERSION_CODE + " từ " + Build.MODEL + " phiên bản Android : " + Build.VERSION.RELEASE +" - "+Build.DEVICE);
                message.setContent(message_info, "text/html; charset=utf-8");
                Transport.send(message);
            } catch (MessagingException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.GONE);
            CustomActivityOnCrash.restartApplicationWithIntent(CustomErrorActivity.this, intent);//restart app

        }

    }
}
