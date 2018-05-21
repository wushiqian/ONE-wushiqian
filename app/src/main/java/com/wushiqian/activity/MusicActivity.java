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
import android.widget.Toast;

import com.example.wushiqian.one_wushiqian.R;
import com.wushiqian.adapter.MusicAdapter;
import com.wushiqian.bean.Music;
import com.wushiqian.ui.LoadMoreListView;
import com.wushiqian.util.ACache;
import com.wushiqian.util.HttpCallbackListener;
import com.wushiqian.util.HttpUtil;
import com.wushiqian.util.LogUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MusicActivity extends AppCompatActivity{

    LoadMoreListView mListView ;
    Toolbar toolbar;
    private List<Music> mMusicList = new ArrayList<>();
    private SwipeRefreshLayout swipeRefresh;
    public static final int TOAST = 1;
    public static final int UPDATE = 2;
    private int nextList = 0;
    MusicAdapter adapter;
    private ACache mCache;
    Music music = null;
    JSONArray jsonArray ;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case  TOAST:
                    Toast.makeText(MusicActivity.this,"error",Toast.LENGTH_SHORT).show();
                    break;
                case  UPDATE:
                    adapter = new MusicAdapter(mMusicList);
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
        mCache = ACache.get(this);
        initArticle();
        initView();
    }

    private void initView() {
        toolbar = findViewById(R.id.toolBar);
        //设置成actionbar
        setSupportActionBar(toolbar);
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
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Music music = mMusicList.get(position);
                Toast.makeText(MusicActivity.this,music.getTitle(),Toast.LENGTH_SHORT).show();
                int itemId = music.getItemId();
                Intent intent = new Intent(MusicActivity.this,ContentActivity.class);
                intent.putExtra("extra_data",itemId);
                intent.putExtra("url","http://v3.wufazhuce.com:8000/api/music/detail/" + itemId + "?channel=wdj&version=4.0.2&uuid=ffffffff-a90e-706a-63f7-ccf973aae5ee&platform=android" );
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

    private void initArticle() {
        jsonArray = mCache.getAsJSONArray("http://v3.wufazhuce.com:8000/api/channel/music/more/"
                + nextList + "?channel=wdj&version=4.0.2&uuid=ffffffff-a90e-706a-63f7-ccf973aae5ee&platform=android");
        if (jsonArray != null) {
            LogUtil.d("MusicActivity","缓存加载");
            try{
                mCache.put("http://v3.wufazhuce.com:8000/api/channel/music/more/"
                        + nextList + "?channel=wdj&version=4.0.2&uuid=ffffffff-a90e-706a-63f7-ccf973aae5ee&platform=android",jsonArray,20);
                for(int i = 0; i < jsonArray.length();i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String title = jsonObject.getString("title");
                    String musicName = jsonObject.getString("music_name");
                    String musicer = jsonObject.getString("audio_author");
                    String forward = "" + musicName + "     歌手/" + musicer;
                    String imageUrl = jsonObject.getString("img_url");
                    int itemId = jsonObject.getInt("item_id");
                    nextList = jsonObject.getInt("id");
                    music = new Music(title,forward,itemId,imageUrl);
                    mMusicList.add(music);
                }
            } catch(Exception e){
                e.printStackTrace();
            }
            Message message = new Message();
            message.what = UPDATE;
            handler.sendMessage(message);
        }else{
            LogUtil.d("MusicActivity","网络加载");
            HttpUtil.sendHttpRequest("http://v3.wufazhuce.com:8000/api/channel/music/more/"
                    + nextList + "?channel=wdj&version=4.0.2&uuid=ffffffff-a90e-706a-63f7-ccf973aae5ee&platform=android", new HttpCallbackListener() {
                @Override
                public void onFinish(final String response) {
                    try{
                        jsonArray = new JSONArray(response);
                        mCache.put("http://v3.wufazhuce.com:8000/api/channel/music/more/"
                                + nextList + "?channel=wdj&version=4.0.2&uuid=ffffffff-a90e-706a-63f7-ccf973aae5ee&platform=android",jsonArray);
                        for(int i = 0; i < jsonArray.length();i++){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String title = jsonObject.getString("title");
                            String musicName = jsonObject.getString("music_name");
                            String musicer = jsonObject.getString("audio_author");
                            String forward = "" + musicName + "     歌手/" + musicer;
                            String imageUrl = jsonObject.getString("img_url");
                            int itemId = jsonObject.getInt("item_id");
                            nextList = jsonObject.getInt("id");
                            music = new Music(title,forward,itemId,imageUrl);
                            mMusicList.add(music);
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
                        mMusicList.clear();
                        initArticle();
                        adapter.notifyDataSetChanged();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        }).start();
    }

}
