package com.wushiqian.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.wushiqian.one_wushiqian.R;
import com.wushiqian.bean.RotateBean;
import com.wushiqian.util.HttpCallbackListener;
import com.wushiqian.util.HttpUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;

class PictureDetailActivity extends AppCompatActivity{

    String address ;
    String imageUrl = "";
    String message = "";
    String content = "";
    String text = "";
    TextView mTvMessage;
    TextView mTvContent;
    TextView mTvText;
    ImageView mIvPic;
    android.support.v7.widget.Toolbar toolbar;

    public static final int TOAST = 1;
    public static final int DATA = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picture_content);
        Intent intent = getIntent();
        address = intent.getStringExtra("url");
        mTvMessage = findViewById(R.id.main_tv_message);
        mTvContent = findViewById(R.id.mian_pic_content);
        mTvText = findViewById(R.id.main_tv_text);
        mIvPic = findViewById(R.id.main_iv);
        toolbar = findViewById(R.id.content_toolBar);
        //设置成actionbar
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.drawable.nav_picture);
        //设置返回图标
        toolbar.setNavigationIcon(R.drawable.back2);
        //返回事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        init();
    }

    private void init() {
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(final String data) {
                try{
                    JSONObject jsonObject = new JSONObject(data);
                    message = jsonObject.getString("hp_author");
                    imageUrl = jsonObject.getString("hp_img_url");
//                    message = "" + author ;
                    content = jsonObject.getString("hp_content");
                    text = jsonObject.getString("text_authors");
                } catch(Exception e){
                    e.printStackTrace();
                }
                Message message = new Message();
                message.what = DATA;
                mHandler.sendMessage(message);
            }
            @Override
            public void onError(Exception e) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Message message = new Message();
                        message.what = TOAST;
                        mHandler.sendMessage(message);
                    }
                }).start();
            }
        });
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case  TOAST:
                    Toast.makeText(PictureDetailActivity.this,"error",Toast.LENGTH_SHORT).show();
                    break;
                case DATA:
                    mTvMessage.setText(message);
                    mTvContent.setText(content);
                    mTvText.setText(text);
                    new DownloadImageTask(mIvPic)
                            .execute("" + imageUrl);
                default: break;
            }
        }
    };

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
