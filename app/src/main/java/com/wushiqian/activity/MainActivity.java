package com.wushiqian.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wushiqian.one_wushiqian.R;
import com.wushiqian.adapter.ViewPagerAdapter;
import com.wushiqian.bean.ArticleListItem;
import com.wushiqian.bean.Picture;
import com.wushiqian.ui.MyViewPager;
import com.wushiqian.util.ApiUtil;
import com.wushiqian.util.CacheUtil;
import com.wushiqian.util.HttpCallbackListener;
import com.wushiqian.util.HttpUtil;
import com.wushiqian.util.ImageManager;
import com.wushiqian.util.JSONUtil;
import com.wushiqian.util.LogUtil;
import com.wushiqian.util.TimeUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

//TODO 用数据库代替CacheUtil
//TODO 线程池
//TODO 首页显示当天的信息
//TODO 使用Material Design Icons
//TODO 设置
//TODO 适配18:9分辨率手机
//TODO 启动页
//TODO 图片压缩

/**
* 主界面，首页
* @author wushiqian
* created at 2018/5/25 20:13
*/
public class MainActivity extends BaseActivity implements MyViewPager.OnViewPagerTouchListener,
        ViewPager.OnPageChangeListener{

    private static final String TAG = "MainActivity";
    private MyViewPager mViewPager;
    private ViewPagerAdapter mPagerAdapter;

    private static List<com.wushiqian.bean.Picture> sPics = new ArrayList<>();

    public static final int PICS = 1;
    public static final int TOAST = 2;
    public static final int DATA = 3;
    public static final int ARTICLE = 4;
    private DrawerLayout mDrawerLayout;
    private boolean mIsTouch = false;
    private LinearLayout mPointContainer;
    private CacheUtil mCache;
    private String mdate = "";
    private TextView mTvMessage;
    private TextView mTvContent;
    private TextView mTvText;
    private ImageView mIvPic;
    private TextView mTvArticleTitle;
    private TextView mTvArticleAuthor;
    private TextView mTvArticleForward;
    private Picture mPicture;
    private ArticleListItem mArticleListItem;
    private List<String> imageUrlList = new ArrayList<>();
    private MyHandler mHandler = new MyHandler(MainActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mdate = TimeUtil.getTimeByCalendar();
        LogUtil.d(MainActivity.TAG,"今天是" + mdate);
        //找到各控件
        mIvPic = findViewById(R.id.main_iv);
        mTvMessage = findViewById(R.id.main_tv_message);
        mTvContent = findViewById(R.id.mian_pic_content);
        mTvText = findViewById(R.id.main_tv_text);
        mTvArticleAuthor = findViewById(R.id.main_summary);
        mTvArticleTitle = findViewById(R.id.main_title);
        mTvArticleForward = findViewById(R.id.main_forward);
        //初始化
        initView();
        init();
        initPicture();
        initPics();
        initArticle();
    }

    /**
    * 加载轮播图的图片
    * @author wushiqian
    * @pram void
    * @return void
    * created at 2018/5/26 13:38
    */
    private void initPics() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(mCache.getAsJSONArray(ApiUtil.MAIN_PICTURE_URL_PRE + mdate
                        + ApiUtil.MAIN_PICTURE_URL_SUF) == null) {
                    HttpUtil.sendHttpRequest(ApiUtil.MAIN_PICTURE_URL_PRE + mdate
                            + ApiUtil.MAIN_PICTURE_URL_SUF, new HttpCallbackListener() {
                        @Override
                        public void onFinish(final String data) {
                            try {
                                JSONArray jsonArray = new JSONArray(data);
                                mCache.put(ApiUtil.MAIN_PICTURE_URL_PRE + mdate
                                        + ApiUtil.MAIN_PICTURE_URL_SUF,jsonArray,CacheUtil.TIME_DAY);
                                for(int i = 0; i < 5; i++){
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    String imageUrl = jsonObject.getString("hp_img_url");
                                    imageUrlList.add(imageUrl);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Message message = new Message();
                            message.what = PICS;
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
                        JSONArray jsonArray = mCache.getAsJSONArray(ApiUtil.MAIN_PICTURE_URL_PRE + mdate + ApiUtil.MAIN_PICTURE_URL_SUF);
                        mCache.put(ApiUtil.MAIN_PICTURE_URL_PRE + mdate + ApiUtil.MAIN_PICTURE_URL_SUF,jsonArray,CacheUtil.TIME_DAY);
                        for(int i = 0; i < 5; i++){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String imageUrl = jsonObject.getString("hp_img_url");
                            imageUrlList.add(imageUrl);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Message message = new Message();
                    message.what = PICS;
                    mHandler.sendMessage(message);
                }
            }
        }).start();
        sPics.add(new com.wushiqian.bean.Picture("https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=4265651613,3028249006&fm=27&gp=0.jpg"));
    }

    private void initArticle() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(mCache.getAsJSONArray(ApiUtil.MAIN_ARTICLE_URL_PRE + mdate
                        + ApiUtil.MAIN_ARTICLE_URL_SUF) == null) {
                    HttpUtil.sendHttpRequest(ApiUtil.MAIN_ARTICLE_URL_PRE + mdate
                            + ApiUtil.MAIN_ARTICLE_URL_SUF, new HttpCallbackListener() {
                        @Override
                        public void onFinish(final String response) {
                            try {
                                JSONArray jsonArray = new JSONArray(response);
                                mCache.put(ApiUtil.MAIN_ARTICLE_URL_PRE + mdate
                                        + ApiUtil.MAIN_ARTICLE_URL_SUF,jsonArray,CacheUtil.TIME_DAY);
                                JSONObject jsonObj = jsonArray.getJSONObject(0);
                                mArticleListItem = JSONUtil.parseJSONMainArticle(jsonObj);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Message message = new Message();
                            message.what = ARTICLE;
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
                        JSONArray jsonArray = mCache.getAsJSONArray(ApiUtil.MAIN_ARTICLE_URL_PRE + mdate
                                + ApiUtil.MAIN_ARTICLE_URL_SUF);
                        mCache.put(ApiUtil.MAIN_ARTICLE_URL_PRE + mdate
                                + ApiUtil.MAIN_ARTICLE_URL_SUF,jsonArray,CacheUtil.TIME_DAY);
                        JSONObject jsonObj = jsonArray.getJSONObject(0);
                        mArticleListItem = JSONUtil.parseJSONMainArticle(jsonObj);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Message message = new Message();
                    message.what = ARTICLE;
                    mHandler.sendMessage(message);
                }
            }
        }).start();
    }

    private void initPicture() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(mCache.getAsJSONArray(ApiUtil.MAIN_PICTURE_URL_PRE + mdate
                        + ApiUtil.MAIN_PICTURE_URL_SUF) == null ) {
                    HttpUtil.sendHttpRequest(ApiUtil.MAIN_PICTURE_URL_PRE + mdate
                            + ApiUtil.MAIN_PICTURE_URL_SUF, new HttpCallbackListener() {
                        @Override
                        public void onFinish(final String data) {
                            try {
                                JSONArray jsonArray = new JSONArray(data);
                                mCache.put(ApiUtil.MAIN_PICTURE_URL_PRE + mdate
                                        + ApiUtil.MAIN_PICTURE_URL_SUF,jsonArray,CacheUtil.TIME_DAY);
                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                                mPicture = JSONUtil.praseJSONMainPicture(jsonObject);

                            } catch (Exception e) {
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
                        JSONArray jsonArray = mCache.getAsJSONArray(ApiUtil.MAIN_PICTURE_URL_PRE + mdate
                                + ApiUtil.MAIN_PICTURE_URL_SUF);
                        mCache.put(ApiUtil.MAIN_PICTURE_URL_PRE + mdate
                                + ApiUtil.MAIN_PICTURE_URL_SUF,jsonArray,CacheUtil.TIME_DAY);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        mPicture = JSONUtil.praseJSONMainPicture(jsonObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Message message = new Message();
                    message.what = DATA;
                    mHandler.sendMessage(message);
                }
            }
        }).start();
    }

    private void init() {
        mCache = CacheUtil.get(this);
        super.setToolbar();
        mDrawerLayout = findViewById(R.id.drawer_layout);//滑动菜单
        ActionBar actionBar = getSupportActionBar();
        NavigationView navView = findViewById(R.id.nav_view);
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        }
        mDrawerLayout.setScrimColor(Color.TRANSPARENT); //去除侧滑时的阴影遮罩效果

        navView.setItemIconTintList(null);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected( MenuItem item) {//自动关闭滑动菜单
                switch (item.getItemId()) {
                    case R.id.nav_article:
                        Toast.makeText(MainActivity.this, "文章列表界面", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, ArticleActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_picture:
                        Toast.makeText(MainActivity.this, "插画列表界面", Toast.LENGTH_SHORT).show();
                        intent = new Intent(MainActivity.this, PictureActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_music:
                        Toast.makeText(MainActivity.this, "音乐列表界面", Toast.LENGTH_SHORT).show();
                        intent = new Intent(MainActivity.this, MusicActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_film:
                        Toast.makeText(MainActivity.this, "影视列表界面", Toast.LENGTH_SHORT).show();
                        intent = new Intent(MainActivity.this, FilmActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_about:
                        Toast.makeText(MainActivity.this, "关于", Toast.LENGTH_SHORT).show();
                        intent = new Intent(MainActivity.this,AboutActivity.class);
                        startActivity(intent);
                        break;
                }
                    mDrawerLayout.closeDrawers();
                return true;
            }
        });
    }

    //  报错原因： 非静态内部类持有外部类的匿名引用，导致外部activity无法得到释放。
    //  解决方法：handler内部持有外部的弱引用，并把handler改为静态内部类，
    //  在activity的onDestory()中调用handler的removeCallback()方法。
    private static class MyHandler extends Handler {

        private final WeakReference<MainActivity> mActivity;

        private MyHandler(MainActivity activity) {
            this.mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MainActivity activity = mActivity.get();
            switch (msg.what) {
                case PICS:
                    if (activity != null) {
                        activity.showPics();
                    }
                    break;
                case  TOAST:
                    if (activity != null) {
                        activity.toast();
                    }
                    break;
                case DATA:
                    if (activity != null) {
                        activity.showPicture();
                    }
                    break;
                case ARTICLE:
                    if (activity != null) {
                        activity.showArticle();
                    }
                    break;
                default: break;
            }
        }
    }

    /**
    * 把各种画面展示到ui中
    * @author wushiqian
    * created at 2018/5/26 17:37
    */
    private void showArticle() {
        mTvArticleAuthor.setText(mArticleListItem.getAuthor());
        mTvArticleForward.setText(mArticleListItem.getForward());
        mTvArticleTitle.setText(mArticleListItem.getTitle());
    }

    private void showPicture() {
        mTvMessage.setText(mPicture.getMessage());
        mTvContent.setText(mPicture.getContent());
        mTvText.setText(mPicture.getText());
        new ImageManager(mIvPic)
                .execute(mPicture.getImageUrl());
    }

    private void toast() {
        Toast.makeText(MainActivity.this,"error",Toast.LENGTH_SHORT).show();
    }

    private void showPics() {
        int len = imageUrlList.size();
        for(int i = 0 ; i < len; i++){
            sPics.add(new com.wushiqian.bean.Picture(imageUrlList.get(i)));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu){
        getMenuInflater().inflate(R.menu.toolbar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case R.id.settings:
                Toast.makeText(this,"Next version",Toast.LENGTH_SHORT).show();
                break;
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
             default:
        }
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        //当视图可见时继续滚动
        mHandler.post(mLooperViewPagerTask);
    }

    @Override
    public void onStop() {
        super.onStop();
        //当视图不可见时停止滚动
        mHandler.removeCallbacks(mLooperViewPagerTask);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActvity(this);
        mHandler.removeCallbacks(mLooperViewPagerTask); //XXX
    }

    private Runnable mLooperViewPagerTask = new Runnable() {
        @Override
        public void run() {
            if (!mIsTouch) {
                //切换viewPager里的图片到下一个
                int currentItem = mViewPager.getCurrentItem();
                mViewPager.setCurrentItem( ++currentItem , true);
            }
            mHandler.postDelayed(this,5000);//5秒换一次
        }
    };

    private void initView() {
        //就是找到这个viewPager控件
        mViewPager = this.findViewById(R.id.looper_pager);
        //设置适配器
        mPagerAdapter = new ViewPagerAdapter();
        mPagerAdapter.setData(sPics);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOnViewPagerTouchListener(MainActivity.this);
        mViewPager.addOnPageChangeListener(MainActivity.this);
        mPointContainer = this.findViewById(R.id.points_container);
        //根据图片的个数,去添加点的个数
        insertPoint();
        mViewPager.setCurrentItem(mPagerAdapter.getDataRealSize() * 100, true);
        if(Build.VERSION.SDK_INT >= 21){        //设置沉浸式状态栏
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * 根据图片的个数,去添加点的个数
     */
    private void insertPoint() {
        for (int i = 0; i < 6; i++) {
            View point = new View(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(40, 40);
            point.setBackground(getResources().getDrawable(R.drawable.shape_point_normal));
            layoutParams.leftMargin = 20;
            point.setLayoutParams(layoutParams);
            mPointContainer.addView(point);
        }
    }

    @Override
    public void onPagerTouch(boolean isTouch) {
        mIsTouch = isTouch;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        //这个方法的调用,其实是viewPager停下来以后选中的位置
        int realPosition;
        if (mPagerAdapter.getDataRealSize() != 0) {
            realPosition = position % mPagerAdapter.getDataRealSize();
        } else {
            realPosition = 0;
        }
        setSelectPoint(realPosition);
    }

    private void setSelectPoint(int realPosition) {
        //设置颜色
       for (int i = 0; i < mPointContainer.getChildCount(); i++) {
           View point = mPointContainer.getChildAt(i);
           if (i != realPosition) {
               //那就是白色
               point.setBackgroundResource(R.drawable.shape_point_normal);
               } else {
               //选中的颜色
               point.setBackgroundResource(R.drawable.shape_point_selected);
           }
       }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

}
