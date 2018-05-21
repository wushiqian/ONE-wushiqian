package com.wushiqian.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wushiqian.one_wushiqian.R;
import com.wushiqian.adapter.Myadaper;
import com.wushiqian.adapter.PictureAdapter;
import com.wushiqian.bean.ArticleListItem;
import com.wushiqian.bean.Picture;
import com.wushiqian.ui.MyViewPager;
import com.wushiqian.util.HttpCallbackListener;
import com.wushiqian.util.HttpUtil;
import com.wushiqian.util.LogUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PictureActivity extends AppCompatActivity {

    ListView mlistView ;
    Toolbar mtoolbar;
//    TextView mTvMessage;
    PictureAdapter adapter;
    private List<Picture> mPictureList = new LinkedList<>();
    private List<Integer> mitemIdList = new ArrayList<>();
    private SwipeRefreshLayout swipeRefresh;
    public static final int TOAST = 1;
    public static final int UPDATE = 2;
    private int nextList = 0;
    String imageUrl;
    int itemId = 0;
    String message;
    Picture picture = null;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case  TOAST:
                    Toast.makeText(PictureActivity.this,"error",Toast.LENGTH_SHORT).show();
                    break;
                case  UPDATE:
                    adapter = new PictureAdapter(mPictureList);
                    mlistView.setAdapter(adapter);
                    break;
                default: break;
            }
        }
    };
        @Override
         protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.picture_list);
            mlistView = findViewById(R.id.picture_lv);
//            mTvMessage = findViewById(R.id.picture_tv_message);
            initPicture();
            mtoolbar = findViewById(R.id.picture_toolBar);
            //设置成actionbar
            setSupportActionBar(mtoolbar);
            mtoolbar.setLogo(R.drawable.nav_picture);
            //设置返回图标
            mtoolbar.setNavigationIcon(R.drawable.back2);
            //返回事件
            mtoolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            swipeRefresh = findViewById(R.id.picture_swipe_refresh);
            swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
            swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    refreshPicture();
                }
            });
        }

        private void refreshPicture() {
            new Thread(new Runnable() {
                @Override
                public void run() {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initPicture();
                            adapter.notifyDataSetChanged();
                            swipeRefresh.setRefreshing(false);
                        }
                    });
                }
            }).start();
        }

        private void initPicture() {
            HttpUtil.sendHttpRequest("http://v3.wufazhuce.com:8000/api/hp/idlist/" + nextList + "?version=3.5.0&platform=android", new HttpCallbackListener() {
                @Override
                public void onFinish(final String response) {
                    try{
                        JSONArray jsonArray = new JSONArray(response);
                        for(int i = 0; i < jsonArray.length();i++){
                            itemId = jsonArray.getInt(i);
                            HttpUtil.sendHttpRequest("http://v3.wufazhuce.com:8000/api/hp/detail/" + itemId + "?version=3.5.0&platform=android", new HttpCallbackListener() {
                                @Override
                                public void onFinish(final String data) {
                                    try{
                                        picture = null;
                                        JSONObject jsonObject = new JSONObject(data);
                                        imageUrl = jsonObject.getString("hp_img_url");
                                        message = jsonObject.getString("hp_author");
                                        itemId = jsonObject.getInt("hpcontent_id");
                                        picture = new Picture(imageUrl,itemId,message);
                                        mPictureList.add(picture);
//                                        adapter.notifyDataSetChanged();
                                    } catch(Exception e){
                                        e.printStackTrace();
                                    }
                                }
                                @Override
                                public void onError(Exception e) {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Message message = new Message();
                                            message.what = TOAST;
                                            handler.sendMessage(message);
                                        }
                                    }).start();
                                }
                            });
//                            mitemIdList.add(itemId);
                        }
                        nextList = itemId;
                    } catch(Exception e){
                        e.printStackTrace();
                    }
                    mitemIdList.clear();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter = new PictureAdapter(mPictureList);
                                    mlistView.setAdapter(adapter);
                                }
                            });
                        }
                    }).start();
//                    Message message = new Message();
//                    message.what = UPDATE;
//                    handler.sendMessage(message);
                }

                @Override
                public void onError(Exception e) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Message message = new Message();
                            message.what = TOAST;
                            handler.sendMessage(message);
                        }
                    }).start();
                }
            });


        }

    }
