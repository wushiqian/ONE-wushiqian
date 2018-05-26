package com.wushiqian.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.wushiqian.one_wushiqian.R;
import com.wushiqian.adapter.PictureAdapter;
import com.wushiqian.bean.Picture;
import com.wushiqian.ui.LoadMoreListView;
import com.wushiqian.util.ApiUtil;
import com.wushiqian.util.CacheUtil;
import com.wushiqian.util.HttpCallbackListener;
import com.wushiqian.util.HttpUtil;
import com.wushiqian.util.LogUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

/**
*图片列表
* @author wushiqian
* created at 2018/5/25 20:06
*/
public class PictureActivity extends BaseActivity {

    private final String TAG = "PictureActivity";
    private LoadMoreListView mlistView ;
    private Toolbar mtoolbar;
    private PictureAdapter adapter ;
    private List<Picture> mPictureList = new LinkedList<>();
    private List<Integer> mitemIdList = new LinkedList<>();
    private SwipeRefreshLayout swipeRefresh;
    private  static final int TOAST = 1;
    private  static final int UPDATE = 2;
    private static final int PIC = 3;
    private int nextList = 0;
    private int itemId ;
    private CacheUtil mCacheUtil;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case  PIC:
                    if(mPictureList.size() == 10 ) {
                        mlistView.setAdapter(adapter);
                    }
                    break;
                case  TOAST:
                    Toast.makeText(PictureActivity.this,"error",Toast.LENGTH_SHORT).show();
                    break;
                case  UPDATE:
                    for (int i = 0; i < mitemIdList.size(); i++) {
                        int itemId = mitemIdList.get(i);
                        initPic(ApiUtil.PICTURE_DETAIL_URL_PRE + itemId
                                + ApiUtil.PICTURE_DETAIL_URL_SUF);
                        }
                    break;
                default: break;
            }
        }

    };

    private void initPic(final String address) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(mCacheUtil.getAsJSONObject(address) == null){
                    HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
                        @Override
                        public void onFinish(final String data) {
                            try {
                                JSONObject jsonObject = new JSONObject(data);
                                mCacheUtil.put(address,jsonObject,CacheUtil.TIME_DAY);
                                String imageUrl = jsonObject.getString("hp_img_url");
                                String message = jsonObject.getString("hp_author");
                                LogUtil.d(TAG, message);
                                int itemId = jsonObject.getInt("hpcontent_id");
                                Picture picture = new Picture(imageUrl, itemId, message);
                                mPictureList.add(picture);
                                adapter.notifyDataSetChanged();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Message message = new Message();
                            message.what = PIC;
                            handler.sendMessage(message);
                        }

                        @Override
                        public void onError(Exception e) {
                            LogUtil.d(TAG,"这里出错了");
                            Message message = new Message();
                            message.what = TOAST;
                            handler.sendMessage(message);
                        }
                    });
                } else{
                    try {
                        JSONObject jsonObject = mCacheUtil.getAsJSONObject(address);
                        mCacheUtil.put(address,jsonObject,CacheUtil.TIME_DAY);
                        String imageUrl = jsonObject.getString("hp_img_url");
                        String message = jsonObject.getString("hp_author");
                        LogUtil.d(TAG, message);
                        int itemId = jsonObject.getInt("hpcontent_id");
                        Picture picture = new Picture(imageUrl, itemId, message);
                        mPictureList.add(picture);
                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Message message = new Message();
                    message.what = PIC;
                    handler.sendMessage(message);
                }
            }
        }).start();

    }

        @Override
         protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.picture_list);
            mlistView = findViewById(R.id.picture_lv);
            adapter = new PictureAdapter(mPictureList);
            mPictureList.clear();
            mCacheUtil = CacheUtil.get(this);
            initPicture();
            initView();
        }

    /**
    * 加载view
    * @author wushiqian
    * @pram  void
    * @return void
    */
    private void initView() {
        mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Picture picture = mPictureList.get(position);
                int itemId = picture.getItemId();
                Intent intent = new Intent(PictureActivity.this,PictureDetailActivity.class);
                intent.putExtra("url",ApiUtil.PICTURE_DETAIL_URL_PRE + itemId + ApiUtil.PICTURE_DETAIL_URL_SUF );
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
                mPictureList.clear();
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
            mitemIdList.clear();
            initPicture();
            mlistView.setLoadCompleted();
        }

    private void refreshPicture() {

        nextList = 0;
        mitemIdList.clear();
        initPicture();
        adapter.notifyDataSetChanged();
        swipeRefresh.setRefreshing(false);
        }

    private void initPicture() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(mCacheUtil.getAsJSONArray(ApiUtil.PICTURE_LIST_URL_PRE + nextList
                        + ApiUtil.PICTURE_LIST_URL_SUF ) == null) {
                    HttpUtil.sendHttpRequest(ApiUtil.PICTURE_LIST_URL_PRE + nextList
                            + ApiUtil.PICTURE_LIST_URL_SUF, new HttpCallbackListener() {
                        @Override
                        public void onFinish(final String response) {
                            try {
                                JSONArray jsonArray = new JSONArray(response);
                                mCacheUtil.put(ApiUtil.PICTURE_LIST_URL_PRE + nextList
                                        + ApiUtil.PICTURE_LIST_URL_SUF,jsonArray,CacheUtil.TIME_DAY);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    itemId = jsonArray.getInt(i);
                                    mitemIdList.add(itemId);
                                }
                                Message message = new Message();
                                message.what = UPDATE;
                                handler.sendMessage(message);
                                nextList = itemId;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(Exception e) {
                            LogUtil.d(TAG, "这里出错了");
                            Message message = new Message();
                            message.what = TOAST;
                            handler.sendMessage(message);
                        }
                    });
                }else{
                    try {
                        JSONArray jsonArray = mCacheUtil.getAsJSONArray(ApiUtil.PICTURE_LIST_URL_PRE + nextList
                                + ApiUtil.PICTURE_LIST_URL_SUF);
                        mCacheUtil.put(ApiUtil.PICTURE_LIST_URL_PRE + nextList
                                + ApiUtil.PICTURE_LIST_URL_SUF, jsonArray, CacheUtil.TIME_DAY);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            itemId = jsonArray.getInt(i);
                            mitemIdList.add(itemId);
                        }
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                    nextList = itemId;
                    Message message = new Message();
                    message.what = UPDATE;
                    handler.sendMessage(message);
                }
            }
        }).start();
        }

    }
