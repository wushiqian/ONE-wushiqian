package com.wushiqian.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.wushiqian.one_wushiqian.R;
import com.wushiqian.adapter.Myadaper;
import com.wushiqian.bean.ArticleListItem;
import com.wushiqian.ui.LoadMoreListView;
import com.wushiqian.util.HttpCallbackListener;
import com.wushiqian.util.HttpUtil;
import com.wushiqian.util.LogUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ArticleActivity extends AppCompatActivity {

    LoadMoreListView mListView ;
    Toolbar toolbar;
    private List<ArticleListItem> articleList = new ArrayList<>();
    private SwipeRefreshLayout swipeRefresh;
    public static final int TOAST = 1;
    public static final int UPDATE = 2;
    private int nextList = 0;
    Myadaper adapter;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case  TOAST:
                    Toast.makeText(ArticleActivity.this,"error",Toast.LENGTH_SHORT).show();
                    break;
                case  UPDATE:
                    adapter = new Myadaper(articleList);
                    mListView.setAdapter(adapter);
                default: break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        mListView = findViewById(R.id.activity_list_view);
        initArticle();
        initView();
        mListView.setONLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            @Override
            public void onloadMore() {
                loadMore();
            }

        });

    }

    private void loadMore() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initArticle();
                        adapter.notifyDataSetChanged();
                        mListView.setLoadCompleted();
                    }
                });
            }
        }).start();
    }

    private void initView() {
        toolbar = findViewById(R.id.toolBar);
        //设置成actionbar
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.drawable.article2);
        //设置返回图标
        toolbar.setNavigationIcon(R.drawable.back2);
        //返回事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArticleListItem articleListItem = articleList.get(position);
                Toast.makeText(ArticleActivity.this,articleListItem.getTitle(),Toast.LENGTH_SHORT).show();
                int itemId = articleListItem.getItemId();
                Intent intent = new Intent(ArticleActivity.this,ContentActivity.class);
                intent.putExtra("extra_data",itemId);
                intent.putExtra("url","http://v3.wufazhuce.com:8000/api/essay/" + itemId + "?channel=wdj&source=channel_reading&source_id=9264&version=4.0.2&uuid=ffffffff-a90e-706a-63f7-ccf973aae5ee&platform=android");
                intent.putExtra("type","essay");
                startActivity(intent);
            }
        });
        swipeRefresh = findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshArticle();
            }
        });
    }

    private void initArticle() {
        HttpUtil.sendHttpRequest("http://v3.wufazhuce.com:8000/api/channel/reading/more/" + nextList + "?channel=wdj&version=4.0.2&uuid=ffffffff-a90e-706a-63f7-ccf973aae5ee&platform=android", new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                ArticleListItem articleListItem = null;
                try{
                    JSONArray jsonArray = new JSONArray(response);
                    for(int i = 0; i < jsonArray.length();i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String title = jsonObject.getString("title");
                        String author = jsonObject.getString("author");
                        JSONObject jo = new JSONObject(author);
                        String userName = jo.getString("user_name");
                        String imageUrl = jsonObject.getString("img_url");
                        int itemId = jsonObject.getInt("item_id");
                        nextList = jsonObject.getInt("id");
                        articleListItem = new ArticleListItem(title,userName,imageUrl,itemId);
                        articleList.add(articleListItem);
                        LogUtil.d("ArticleActivity","title is" + title);
                    }
                } catch(Exception e){
                    e.printStackTrace();
                }
                Message message = new Message();
                message.what = UPDATE;
                handler.sendMessage(message);
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

    // 重写
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
    //重写
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void refreshArticle() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        nextList = 0;
                        articleList.clear();
                        initArticle();
                        adapter.notifyDataSetChanged();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        }).start();
    }

    public boolean isListViewReachBottomEdge(final ListView listView) {
        boolean result=false;
        if (listView.getLastVisiblePosition() == (listView.getCount() - 1)) {
            final View bottomChildView = listView.getChildAt(listView.getLastVisiblePosition() - listView.getFirstVisiblePosition());
            result= (listView.getHeight() >= bottomChildView.getBottom());
        };
        return  result;
    }

}
