package com.wushiqian.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wushiqian.one_wushiqian.R;
import com.wushiqian.bean.Picture;
import com.wushiqian.util.CacheUtil;
import com.wushiqian.util.HttpCallbackListener;
import com.wushiqian.util.HttpUtil;
import com.wushiqian.util.ImageManager;
import com.wushiqian.util.JSONUtil;

import org.json.JSONObject;

/**
* 图片详情
* @author wushiqian
* created at 2018/5/25 20:12
*/
public class PictureDetailActivity extends BaseActivity{

    private String address ;
    private TextView mTvMessage;
    private TextView mTvContent;
    private TextView mTvText;
    private ImageView mIvPic;
    private android.support.v7.widget.Toolbar toolbar;
    private CacheUtil mCacheUtil;
    private Picture mPicture;

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
        initView();
        initPicture();
    }

    private void initView() {
        toolbar = findViewById(R.id.content_toolBar);
        //设置成actionbar
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.drawable.ic_photo_library_black_24dp);
        //设置返回图标
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        //返回事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mCacheUtil = CacheUtil.get(this);
        if(Build.VERSION.SDK_INT >= 21){
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    private void initPicture() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(mCacheUtil.getAsJSONObject(address) == null){
                    HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
                        @Override
                        public void onFinish(final String data) {
                            try{
                                JSONObject jsonObject = new JSONObject(data);
                                mCacheUtil.put(address,jsonObject, CacheUtil.TIME_DAY);
                                mPicture = JSONUtil.praseJSONPictureDetail(jsonObject);
                            } catch(Exception e){
                                e.printStackTrace();
                            }
                            Message message = new Message();
                            message.what = DATA;
                            mHandler.sendMessage(message);
                        }
                        @Override
                        public void onError(Exception e) {
                            Message message = new Message();
                            message.what = TOAST;
                            mHandler.sendMessage(message);
                        }
                    });
                }else{
                    try {
                        JSONObject jsonObject = mCacheUtil.getAsJSONObject(address);
                        mCacheUtil.put(address,jsonObject, CacheUtil.TIME_DAY);
                        mPicture = JSONUtil.praseJSONPictureDetail(jsonObject);
                    } catch(Exception e){
                        e.printStackTrace();
                    }
                    Message message = new Message();
                    message.what = DATA;
                    mHandler.sendMessage(message);
                }
            }
        }).start();

    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case  TOAST:
                    Toast.makeText(PictureDetailActivity.this,"error",Toast.LENGTH_SHORT).show();
                    break;
                case DATA:
                    mTvMessage.setText(mPicture.getMessage());
                    mTvContent.setText(mPicture.getContent());
                    mTvText.setText(mPicture.getText());
                    new ImageManager(mIvPic)
                            .execute(mPicture.getImageUrl());
                default: break;
            }
        }
    };

}
