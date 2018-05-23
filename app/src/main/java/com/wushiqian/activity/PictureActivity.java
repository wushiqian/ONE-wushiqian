package com.wushiqian.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wushiqian.one_wushiqian.R;
import com.wushiqian.adapter.Myadaper;
import com.wushiqian.adapter.PictureAdapter;
import com.wushiqian.bean.ArticleListItem;
import com.wushiqian.bean.Music;
import com.wushiqian.bean.Picture;
import com.wushiqian.ui.LoadMoreListView;
import com.wushiqian.ui.MyViewPager;
import com.wushiqian.util.HttpCallbackListener;
import com.wushiqian.util.HttpUtil;
import com.wushiqian.util.LogUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

public class PictureActivity extends AppCompatActivity {

    private final String TAG = "PictureActivity";
    LoadMoreListView mlistView ;
    Toolbar mtoolbar;
    PictureAdapter adapter;
    private List<Picture> mPictureList = new Vector<>();
    private List<Integer> mitemIdList = new ArrayList<>();
    private SwipeRefreshLayout swipeRefresh;
    public static final int TOAST = 1;
    public static final int UPDATE = 2;
    private int nextList = 0;
    String imageUrl = "";
    int itemId = 0;
    String message = "";
    Picture picture = null;
    JSONArray jsonArray;


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
//                    adapter.notifyDataSetChanged();
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
            initView();
            initPicture();
        }

    private void initView() {
        mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Picture picture = mPictureList.get(position);
//                Toast.makeText(PictureActivity.this,"test",Toast.LENGTH_SHORT).show();
                int itemId = picture.getItemId();
                Intent intent = new Intent(PictureActivity.this,PictureDetailActivity.class);
                intent.putExtra("url","http://v3.wufazhuce.com:8000/api/hp/detail/" + itemId + "?version=3.5.0&platform=android" );
                startActivity(intent);
            }
        });
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
        mlistView.setONLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            @Override
            public void onloadMore() {
                loadMore();
            }
        });
    }

    private void loadMore() {
            initPicture();
            adapter.notifyDataSetChanged();
            mlistView.setLoadCompleted();
        }

    private void refreshPicture() {
            nextList = 0;
            mPictureList.clear();
            initPicture();
            adapter.notifyDataSetChanged();
            swipeRefresh.setRefreshing(false);
        }

    private void initPicture() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpUtil.sendHttpRequest("http://v3.wufazhuce.com:8000/api/hp/idlist/"
                        + nextList + "?version=3.5.0&platform=android", new HttpCallbackListener() {
                    @Override
                    public void onFinish(final String response) {
                        try {
                            jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                itemId = jsonArray.getInt(i);
                                mitemIdList.add(itemId);
                            }
                            nextList = itemId;
                            for (int i = 0; i < mitemIdList.size(); i++) {
                                itemId = mitemIdList.get(i);
                                HttpUtil.sendHttpRequest("http://v3.wufazhuce.com:8000/api/hp/detail/"
                                        + itemId + "?version=3.5.0&platform=android", new HttpCallbackListener() {
                                            @Override
                                            public void onFinish(final String data) {
                                                try {
                                                    JSONObject jsonObject = new JSONObject(data);
                                                    imageUrl = jsonObject.getString("hp_img_url");
                                                    message = jsonObject.getString("hp_author");
                                                    LogUtil.d(TAG,message);
                                                    itemId = jsonObject.getInt("hpcontent_id");
                                                    picture = new Picture(imageUrl, itemId, message);
                                                    mPictureList.add(picture);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                                Message message = new Message();
                                                message.what = UPDATE;
                                                handler.sendMessage(message);

                                            }

                                            @Override
                                            public void onError(Exception e) {
                                                Message message = new Message();
                                                message.what = TOAST;
                                                handler.sendMessage(message);
                                            }
                                        });

                            }
                            mitemIdList.clear();
                        } catch (Exception e) {
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
            }
        }).start();
    }

    }
