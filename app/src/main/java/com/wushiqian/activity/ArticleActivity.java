package com.wushiqian.activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.wushiqian.one_wushiqian.R;
import com.wushiqian.adapter.ArticleAdaper;
import com.wushiqian.bean.ArticleListItem;
import com.wushiqian.ui.LoadMoreListView;
import com.wushiqian.util.ApiUtil;
import com.wushiqian.util.CacheUtil;
import com.wushiqian.util.HttpCallbackListener;
import com.wushiqian.util.HttpUtil;
import com.wushiqian.util.JSONUtil;
import com.wushiqian.util.LogUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
* 文章列表
* @author wushiqian
* created at 2018/5/25 20:15
*/
public class ArticleActivity extends AppCompatActivity {

    private static final String TAG = "ArticleActivity";
    private LoadMoreListView mListView ;
    private Toolbar toolbar;
    private List<ArticleListItem> articleList = new ArrayList<>();
    private SwipeRefreshLayout swipeRefresh;
    public static final int TOAST = 1;
    public static final int UPDATE = 2;
    private int nextList = 0;
    private ArticleAdaper adapter;
    private CacheUtil mCache;
    private float scaledTouchSlop;
    private float firstY = 0;
    private ObjectAnimator animtor;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case  TOAST:
                    Toast.makeText(ArticleActivity.this,"error",Toast.LENGTH_SHORT).show();
                    break;
                case  UPDATE:
                    if(articleList.size() == 10 ) mListView.setAdapter(adapter);
                default: break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        //设置控件
        mListView = findViewById(R.id.activity_list_view);
        mCache = CacheUtil.get(this);
        initView();
        initArticle();
    }

    /**
     * ToolBar显示隐藏动画
     * @param direction
     */
    public void toobarAnim(int direction) {
        //开始新的动画之前要先取消以前的动画
        if (animtor != null && animtor.isRunning()) {
            animtor.cancel();
        }
        //toolbar.getTranslationY()获取的是Toolbar距离自己顶部的距离
        float translationY=toolbar.getTranslationY();
        if (direction == 0) {
            animtor = ObjectAnimator.ofFloat(toolbar, "translationY", translationY, 0);
            animtor.start();
        } else if (direction == 1) {
            animtor = ObjectAnimator.ofFloat(toolbar, "translationY", translationY, -toolbar.getHeight());
            animtor.start();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        Thread.sleep(150);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toolbar.setVisibility(View.GONE);
                        }
                    });
                }
            }).start();
        }


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

    /**
    * 加载界面
    * @author wushiqian
    * @pram
    * @return
    * created at 2018/5/25 23:37
    */
    private void initView() {
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);       //设置成actionbar
        toolbar.setLogo(R.drawable.ic_library_books_24dp);              //设置logo
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);     //设置返回图标
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });         //返回事件
        if(Build.VERSION.SDK_INT >= 21){            //沉浸式状态栏
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        //判断认为是滑动的最小距离(乘以系数调整滑动灵敏度)
        scaledTouchSlop = ViewConfiguration.get(this).getScaledTouchSlop()*3.0f;
        mListView.setOnTouchListener(new View.OnTouchListener() {
            private float currentY;
            private int direction=-1;
            private boolean mShow = true;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        firstY = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        currentY = event.getY();
                        //向下滑动
                        if (currentY - firstY > scaledTouchSlop) {
                            direction = 0;
                            toolbar.setVisibility(View.VISIBLE);
                        }
                        //向上滑动
                        else if (firstY - currentY > scaledTouchSlop) {
                            direction = 1;
                        }
                        //如果是向上滑动，并且ToolBar是显示的，就隐藏ToolBar
                        if (direction == 1) {
                            if (mShow) {
                                toobarAnim(1);
                                mShow = !mShow;
                            }
                        } else if (direction == 0) {
                            if (!mShow) {
                                toobarAnim(0);
                                mShow = !mShow;
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }
                return false;//注意此处不能返回true，因为如果返回true,onTouchEvent就无法执行，导致的后果是ListView无法滑动
            }
        });         //设置触摸事件

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {      //设置点击事件
                ArticleListItem articleListItem = articleList.get(position);
                Toast.makeText(ArticleActivity.this,articleListItem.getTitle(),Toast.LENGTH_SHORT).show();
                int itemId = articleListItem.getItemId();
                Intent intent = new Intent(ArticleActivity.this,ContentActivity.class);
                intent.putExtra("extra_data",itemId);
                intent.putExtra("url",ApiUtil.ARTICLE_DETAIL_URL_PRE + itemId + ApiUtil.ARTICLE_DETAIL_URL_SUF);
                intent.putExtra("type","essay");
                startActivity(intent);
            }
        });
        mListView.setONLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {     //设置加载更多监听事件
            @Override
            public void onloadMore() {
                loadMore();
            }
        });

        swipeRefresh = findViewById(R.id.swipe_refresh);        //设置下拉刷新组件
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshArticle();
            }
        });
        adapter = new ArticleAdaper(articleList);
    }

    /**
    * 从本地储存读取文章列表，若读不到则从网络加载
    * @author wushiqian
    * @pram
    * @return
    * created at 2018/5/25 23:38
    */
    private void initArticle() {
        if (mCache.getAsJSONArray(ApiUtil.ARTICLE_LIST_URL_PRE + nextList + ApiUtil.ARTICLE_LIST_URL_SUF) != null) {
            LogUtil.d(TAG,"缓存加载");
            try{
                JSONArray jsonArray = mCache.getAsJSONArray(ApiUtil.ARTICLE_LIST_URL_PRE + nextList + ApiUtil.ARTICLE_LIST_URL_SUF);
                mCache.put(ApiUtil.ARTICLE_LIST_URL_PRE + nextList + ApiUtil.ARTICLE_LIST_URL_SUF,jsonArray, CacheUtil.TIME_HOUR);
                for(int i = 0; i < jsonArray.length();i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    nextList = jsonObject.getInt("id");
                    ArticleListItem articleListItem = JSONUtil.parseJSONArticle(jsonObject);
                    articleList.add(articleListItem);
                }
            } catch(Exception e){
                e.printStackTrace();
            }
            Message message = new Message();
            message.what = UPDATE;
            handler.sendMessage(message);
        }else{
            HttpUtil.sendHttpRequest(ApiUtil.ARTICLE_LIST_URL_PRE + nextList + ApiUtil.ARTICLE_LIST_URL_SUF, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    mCache.put(ApiUtil.ARTICLE_LIST_URL_PRE + nextList + ApiUtil.ARTICLE_LIST_URL_SUF,jsonArray,CacheUtil.TIME_HOUR);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        nextList = jsonObject.getInt("id");
                        ArticleListItem articleListItem = JSONUtil.parseJSONArticle(jsonObject);
                        articleList.add(articleListItem);
                    }
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

    /**
    * 刷新文章列表
    * @author wushiqian
    * @pram
    * @return
    * created at 2018/5/25 23:39
    */
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

}
