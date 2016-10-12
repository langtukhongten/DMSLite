package com.vietdms.mobile.dmslauncher.Forms;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.vietdms.mobile.dmslauncher.CustomClass.LayoutLoadingManager;
import com.vietdms.mobile.dmslauncher.CustomView.LoadingView;
import com.vietdms.mobile.dmslauncher.R;
import com.vietdms.mobile.dmslauncher.databinding.ActivityImageDetailBinding;

import java.io.InputStream;

public class ImageDetailActivity extends AppCompatActivity {
    private ActivityImageDetailBinding binding;
    private String url = "";
    private String name = "";
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_image_detail);
        try {
            Intent intent = getIntent();
            id = intent.getIntExtra("ID", 0);
            name = intent.getStringExtra("Description");
            url = intent.getStringExtra("Url");
        } catch (Exception e) {
            e.printStackTrace();
        }
        binding.title.setText(name);
        LayoutLoadingManager.Show_OnLoading(binding.ImageLoadingView, this.getString(R.string.load_image), 30);
        final ImageDownloader imageDownloader = new ImageDownloader(binding.imageView, binding.ImageLoadingView);
        imageDownloader.execute(url);
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
            if (result != null) {
                bmImage.setImage(ImageSource.bitmap(result));
            } else {
                Toast.makeText(ImageDetailActivity.this, "Lỗi tải xuống ảnh", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
