package com.wushiqian.activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.wushiqian.one_wushiqian.R;
import com.wushiqian.adapter.FilmAdapter;
import com.wushiqian.bean.Film;
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
* 电影列表
* @author wushiqian
* created at 2018/5/25 20:13
*/
public class FilmActivity extends BaseActivity{

    private LoadMoreListView mListView ;
    private Toolbar toolbar;
    private List<Film> mFilmList = new ArrayList<>();
    private SwipeRefreshLayout swipeRefresh;
    private  static final int TOAST = 1;
    private  static final int UPDATE = 2;
    private int nextList = 0;
    private FilmAdapter adapter;
    private CacheUtil mCache;
    private float scaledTouchSlop;
    private float firstY = 0;
    private ObjectAnimator animtor;

    //报错原因： 非静态内部类持有外部类的匿名引用，导致外部activity无法得到释放。
    //解决方法：handler内部持有外部的弱引用，并把handler改为静态内部类，
    // 在activity的onDestory()中调用handler的removeCallback()方法。
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case  TOAST:
                    Toast.makeText(FilmActivity.this,"error",Toast.LENGTH_SHORT).show();
                    break;
                case  UPDATE:
                    if(mFilmList.size() == 10) mListView.setAdapter(adapter);
                default: break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        initView();
        initFilm();
    }

    private void initView() {
        mListView = findViewById(R.id.activity_list_view);
        toolbar = findViewById(R.id.toolBar);
        //设置成actionbar
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.drawable.ic_video_library_black_24dp);
        //设置返回图标
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
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
                Film film = mFilmList.get(position);
                Toast.makeText(FilmActivity.this,film.getTitle(),Toast.LENGTH_SHORT).show();
                int itemId = film.getItemId();
                Intent intent = new Intent(FilmActivity.this,ContentActivity.class);
                intent.putExtra("extra_data",itemId);
                intent.putExtra("url",ApiUtil.MOVIE_DETAIL_URL_PRE + itemId + ApiUtil.MOVIE_DETAIL_URL_SUF);
                intent.putExtra("type","movie");
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
        mListView.setONLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            @Override
            public void onloadMore() {
                loadMore();
            }
        });
        //判断认为是滑动的最小距离(乘以系数调整滑动灵敏度)
        scaledTouchSlop = ViewConfiguration.get(this).getScaledTouchSlop()*3.0f;
        mCache = CacheUtil.get(this);
        adapter = new FilmAdapter(mFilmList);

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
        });//设置触摸事件
        if(Build.VERSION.SDK_INT >= 21){
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
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
        initFilm();
        adapter.notifyDataSetChanged();
        mListView.setLoadCompleted();
    }

    private void initFilm() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mCache.getAsJSONArray(ApiUtil.MOVIE_LIST_URL_PRE + nextList + ApiUtil.MOVIE_LIST_URL_SUF) != null) {
                    LogUtil.d("MusicActivity","缓存加载");
                    try{
                        JSONArray jsonArray = mCache.getAsJSONArray(ApiUtil.MOVIE_LIST_URL_PRE + nextList + ApiUtil.MOVIE_LIST_URL_SUF);
                        mCache.put(ApiUtil.MOVIE_LIST_URL_PRE + nextList + ApiUtil.MOVIE_LIST_URL_SUF,jsonArray,CacheUtil.TIME_DAY);
                        for(int i = 0; i < jsonArray.length();i++){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            nextList = jsonObject.getInt("id");
                            Film film = JSONUtil.parseJSONFilm(jsonObject);
                            mFilmList.add(film);
                        }
                    } catch(Exception e){
                        e.printStackTrace();
                    }
                    Message message = new Message();
                    message.what = UPDATE;
                    handler.sendMessage(message);
                }else {
                    HttpUtil.sendHttpRequest(ApiUtil.MOVIE_LIST_URL_PRE + nextList
                            + ApiUtil.MOVIE_LIST_URL_SUF, new HttpCallbackListener() {
                        @Override
                        public void onFinish(final String response) {
                            try {
                                JSONArray jsonArray = new JSONArray(response);
                                mCache.put("http://v3.wufazhuce.com:8000/api/channel/movie/more/"
                                        + nextList + "?channel=wdj&version=4.0.2&uuid=ffffffff-a90e-706a-63f7-ccf973aae5ee&platform=android",jsonArray,CacheUtil.TIME_DAY);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    nextList = jsonObject.getInt("id");
                                    Film film = JSONUtil.parseJSONFilm(jsonObject);
                                    mFilmList.add(film);
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
        }).start();
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
    * created at 2018/5/26 17:34
    */
    private void refreshArticle() {
        nextList = 0;
        mFilmList.clear();
        initFilm();
        adapter.notifyDataSetChanged();
        swipeRefresh.setRefreshing(false);
    }

}
