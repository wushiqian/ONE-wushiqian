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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.wushiqian.one_wushiqian.R;
import com.wushiqian.adapter.MusicAdapter;
import com.wushiqian.bean.Music;
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
* 音乐列表
* @author wushiqian
* created at 2018/5/25 20:12
*/
public class MusicActivity extends BaseActivity{

    private static final String TAG = "MusicActivity";
    private LoadMoreListView mListView ;
    private Toolbar toolbar;
    private List<Music> mMusicList = new ArrayList<>();
    private SwipeRefreshLayout swipeRefresh;
    private  static final int TOAST = 1;
    private  static final int UPDATE = 2;
    private int nextList = 0;
    private MusicAdapter adapter;
    private CacheUtil mCache;
    private float scaledTouchSlop;
    private float firstY = 0;
    private ObjectAnimator animtor;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case  TOAST:
                    Toast.makeText(MusicActivity.this,"error",Toast.LENGTH_SHORT).show();
                    break;
                case  UPDATE:
                    adapter = new MusicAdapter(mMusicList);
                    mListView.setAdapter(adapter);
                    mListView.setSelection(mMusicList.size()-10);
                    mListView.smoothScrollToPosition(mMusicList.size()-10);
                default: break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        mListView = findViewById(R.id.activity_list_view);
        initView();
        //判断认为是滑动的最小距离(乘以系数调整滑动灵敏度)
        scaledTouchSlop = ViewConfiguration.get(this).getScaledTouchSlop()*3.0f;
        mCache = CacheUtil.get(this);
        initMusic();
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


    @Override
    protected void setToolbar() {
        super.setToolbar();
        toolbar = findViewById(R.id.toolBar);
        toolbar.setLogo(R.drawable.nav_music);
        //设置返回图标
        toolbar.setNavigationIcon(R.drawable.back2);
        //返回事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {
        setToolbar();
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Music music = mMusicList.get(position);
                Toast.makeText(MusicActivity.this,music.getTitle(),Toast.LENGTH_SHORT).show();
                int itemId = music.getItemId();
                Intent intent = new Intent(MusicActivity.this,ContentActivity.class);
                intent.putExtra("extra_data",itemId);
                intent.putExtra("url",ApiUtil.MUSIC_DETAIL_URL_PRE
                        + itemId + ApiUtil.MUSIC_DETAIL_URL_SUF );
                intent.putExtra("type","music");
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

        if(Build.VERSION.SDK_INT >= 21){
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    private void loadMore() {
        initMusic();
        adapter.notifyDataSetChanged();
        mListView.setLoadCompleted();
    }

    private void initMusic() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if ( mCache.getAsJSONArray(ApiUtil.MUSIC_LIST_URL_PRE + nextList
                        + ApiUtil.MUSIC_LIST_URL_SUF) != null) {
                    LogUtil.d("MusicActivity","缓存加载");
                    try{
                        JSONArray jsonArray = mCache.getAsJSONArray(ApiUtil.MUSIC_LIST_URL_PRE + nextList
                                + ApiUtil.MUSIC_LIST_URL_SUF);
                        mCache.put(ApiUtil.MUSIC_LIST_URL_PRE + nextList
                                + ApiUtil.MUSIC_LIST_URL_SUF,jsonArray, CacheUtil.TIME_HOUR);
                        for(int i = 0; i < jsonArray.length();i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Music music = JSONUtil.parseJSONmusic(jsonObject);
                            mMusicList.add(music);
                            nextList = jsonObject.getInt("id");
                        }
                    } catch(Exception e){
                        e.printStackTrace();
                    }
                    Message message = new Message();
                    message.what = UPDATE;
                    handler.sendMessage(message);
                }else{
                    LogUtil.d("MusicActivity","网络加载");
                    HttpUtil.sendHttpRequest(ApiUtil.MUSIC_LIST_URL_PRE + nextList
                            + ApiUtil.MUSIC_LIST_URL_SUF, new HttpCallbackListener() {
                        @Override
                        public void onFinish(final String response) {
                            try{
                                JSONArray jsonArray = new JSONArray(response);
                                mCache.put("http://v3.wufazhuce.com:8000/api/channel/music/more/"
                                        + nextList + "?channel=wdj&version=4.0.2&uuid=ffffffff-a90e-706a-63f7-ccf973aae5ee&platform=android",jsonArray,CacheUtil.TIME_HOUR);
                                for(int i = 0; i < jsonArray.length();i++){
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    Music music = JSONUtil.parseJSONmusic(jsonObject);
                                    mMusicList.add(music);
                                    nextList = jsonObject.getInt("id");
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

    private void refreshArticle() {
        nextList = 0;
        mMusicList.clear();
        initMusic();
        adapter.notifyDataSetChanged();
        swipeRefresh.setRefreshing(false);
    }


}
