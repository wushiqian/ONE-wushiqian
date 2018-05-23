package com.wushiqian.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wushiqian.one_wushiqian.R;
import com.wushiqian.adapter.LooperPagerAdapter;
import com.wushiqian.bean.Picture;
import com.wushiqian.bean.RotateBean;
import com.wushiqian.db.DBManager;
import com.wushiqian.db.MyDatabaseHelper;
import com.wushiqian.ui.MyViewPager;
import com.wushiqian.util.HttpCallbackListener;
import com.wushiqian.util.HttpUtil;
import com.wushiqian.util.LogUtil;
import com.wushiqian.util.TimeUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MyViewPager.OnViewPagerTouchListener, ViewPager.OnPageChangeListener,View.OnClickListener{

    private static final String TAG = "MainActivity";
    private MyViewPager mLoopPager;
    private LooperPagerAdapter mLooperPagerAdapter;

    private static List<RotateBean> sPics = new ArrayList<>();
    private List<String> urlList = new ArrayList<>();


    public static final int UPDATE_TEXT = 1;
    public static final int TOAST = 2;
    public static final int DATA = 3;
    public static final int ARTICLE = 4;
    private Toolbar mtoolbar;
    private DrawerLayout mDrawerLayout;
    private boolean mIsTouch = false;
    private LinearLayout mPointContainer;
    private String mdate = "";
    private String imageUrl = "";
    private String imageUrl1 = "";
    private String imageUrl2 = "";
    private String message = "";
    private String content = "";
    private String text = "";
    private String articleTitle = "";
    private String articleAuthor = "";
    private String articleForward = "";
    private TextView mTvMessage;
    private TextView mTvContent;
    private TextView mTvText;
    private ImageView mIvPic;
    private TextView mTvarticleTitle;
    private TextView mTvarticleAuthor;
    private TextView mTvarticleForward;
    private ImageView mIvArticle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mdate = TimeUtil.getTimeByCalendar();
        LogUtil.d(MainActivity.TAG,"今天是" + mdate);
//        MyDatabaseHelper
        init();
        initPicture();
        initPics();
        mIvPic = findViewById(R.id.main_iv);
        mTvMessage = findViewById(R.id.main_tv_message);
        mTvContent = findViewById(R.id.mian_pic_content);
        mTvText = findViewById(R.id.main_tv_text);
        mTvarticleAuthor = findViewById(R.id.main_summary);
        mTvarticleTitle = findViewById(R.id.main_title);
        mTvarticleForward = findViewById(R.id.main_forward);
        mIvArticle = findViewById(R.id.main_iv_article);
        initView();
        initArticle();
    }

    private void initPics() {
        HttpUtil.sendHttpRequest("http://v3.wufazhuce.com:8000/api/hp/bymonth/" + mdate + "%2000:00:00?channel=wdj&version=4.0.2&uuid=ffffffff-a90e-706a-63f7-ccf973aae5ee&platform=android", new HttpCallbackListener() {
            @Override
            public void onFinish(final String data) {
                try{
                    JSONArray jsonArray = new JSONArray(data);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    imageUrl = jsonObject.getString("hp_img_url");
                    JSONObject jsonObject1 = jsonArray.getJSONObject(1);
                    imageUrl1 = jsonObject1.getString("hp_img_url");
                    JSONObject jsonObject2 = jsonArray.getJSONObject(2);
                    imageUrl2 = jsonObject2.getString("hp_img_url");
                } catch(Exception e){
                    e.printStackTrace();
                }
                Message message = new Message();
                message.what = UPDATE_TEXT;
                mHandler.sendMessage(message);
            }
            @Override
            public void onError(Exception e) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Message message = new Message();
                        message.what = TOAST;
                        mHandler.sendMessage(message);
                    }
                }).start();
            }
        });
        sPics.add(new RotateBean("https://www.baidu.com/img/bd_logo1.png?where=super"));
    }

    private void initArticle() {
        HttpUtil.sendHttpRequest("http://v3.wufazhuce.com:8000/api/essay/bymonth/" + mdate + "%2000:00:00?channel=wdj&version=4.0.2&uuid=ffffffff-a90e-706a-63f7-ccf973aae5ee&platform=android", new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                try{
                    JSONArray jsonArr = new JSONArray(response);
                    JSONObject jsonObj = jsonArr.getJSONObject(0);
                    articleTitle = jsonObj.getString("hp_title");
//                    articleUrl = jsonObj.getString("hp_img_url");
                    articleForward = jsonObj.getString("guide_word");
                    String jo = jsonObj.getString("author");
                    JSONArray jArray = new JSONArray(jo);
                    JSONObject jObject = jArray.getJSONObject(0);
                    articleAuthor = jObject.getString("user_name");
                } catch(Exception e){
                    e.printStackTrace();
                }
                Message message = new Message();
                message.what = ARTICLE;
                mHandler.sendMessage(message);
            }
            @Override
            public void onError(Exception e) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Message message = new Message();
                        message.what = TOAST;
                        mHandler.sendMessage(message);
                    }
                }).start();
            }
        });
    }

    private void initPicture() {
        HttpUtil.sendHttpRequest("http://v3.wufazhuce.com:8000/api/hp/bymonth/"
                + mdate + "%2000:00:00?channel=wdj&version=4.0.2&uuid=ffffffff-a90e-706a-63f7-ccf973aae5ee&platform=android", new HttpCallbackListener() {
            @Override
            public void onFinish(final String data) {
                try{
                    JSONArray jsonArray = new JSONArray(data);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    String author = jsonObject.getString("hp_author");
                    imageUrl = jsonObject.getString("hp_img_url");
                    String imageAuthor = jsonObject.getString("image_authors");
                    message = "" + author + "|" + imageAuthor;
                    content = jsonObject.getString("hp_content");
                    text = jsonObject.getString("text_authors");
                } catch(Exception e){
                    e.printStackTrace();
                }
                Message message = new Message();
                message.what = DATA;
                mHandler.sendMessage(message);
            }
            @Override
            public void onError(Exception e) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Message message = new Message();
                        message.what = TOAST;
                        mHandler.sendMessage(message);
                    }
                }).start();
            }
        });
    }

    private void init() {
//        dbHelper = new MyDatabaseHelper(this,"One.db",null,2);//数据库管理
//        mDBManager = new DBManager();
        mtoolbar =  findViewById(R.id.toolBar);//toolbar
        setSupportActionBar(mtoolbar);
        mDrawerLayout = findViewById(R.id.drawer_layout);//滑动菜单
        ActionBar actionBar = getSupportActionBar();
        NavigationView navView = findViewById(R.id.nav_view);
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.menu);
        }
        mDrawerLayout.setScrimColor(Color.TRANSPARENT); //去除侧滑时的阴影遮罩效果
        navView.setCheckedItem(R.id.nav_picture);
        navView.setCheckedItem(R.id.nav_article);

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
                        Toast.makeText(MainActivity.this, "next version", Toast.LENGTH_SHORT).show();
                        break;
                }
                    mDrawerLayout.closeDrawers();
                return true;
            }
        });
//        dbHelper = new MyDatabaseHelper(this,"One.db",null,2);//数据库管理
//        Button createDatabase = findViewById(R.id.create_database);
//        Button sendRequest = findViewById(R.id.send_request);
//        sendRequest.setOnClickListener(this);
//        responseText = findViewById(R.id.response_text);
//        createDatabase.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {//创建数据库
//                dbHelper.getWritableDatabase();
//            }
//        });
    }

    @Override
    public void onClick(View v) {

    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_TEXT:
                    sPics.add(new RotateBean(imageUrl));
                    sPics.add(new RotateBean(imageUrl1));
                    sPics.add(new RotateBean(imageUrl2));
                    break;
                case  TOAST:
                    Toast.makeText(MainActivity.this,"error",Toast.LENGTH_SHORT).show();
                    break;
                case DATA:
                    mTvMessage.setText(message);
                    mTvContent.setText(content);
                    mTvText.setText(text);
                    new DownloadImageTask(mIvPic)
                            .execute("" + imageUrl);
                case ARTICLE:
                    mTvarticleAuthor.setText(articleAuthor);
                    mTvarticleForward.setText(articleForward);
                    mTvarticleTitle.setText(articleTitle);
                default: break;
            }
        }
    };

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
                Toast.makeText(this,"Setting",Toast.LENGTH_SHORT).show();
                break;
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
             default:
        }
        return true;
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        //当我这个界面绑定到窗口的时候
        mHandler.post(mLooperTask);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        LogUtil.d(TAG, "onDetachedFromWindow");
        mHandler.removeCallbacks(mLooperTask);
    }

    private Runnable mLooperTask = new Runnable() {
        @Override
        public void run() {
            if (!mIsTouch) {
                //切换viewPager里的图片到下一个
                int currentItem = mLoopPager.getCurrentItem();
                mLoopPager.setCurrentItem(++currentItem, true);
            }
            mHandler.postDelayed(this,5000);
        }
    };

    private void initView() {
        //就是找到这个viewPager控件
        mLoopPager = this.findViewById(R.id.looper_pager);
        //设置适配器
        mLooperPagerAdapter = new LooperPagerAdapter();
        mLooperPagerAdapter.setData(sPics);
        mLoopPager.setAdapter(mLooperPagerAdapter);
        mLoopPager.setOnViewPagerTouchListener(MainActivity.this);
        mLoopPager.addOnPageChangeListener(MainActivity.this);
        mPointContainer = this.findViewById(R.id.points_container);
        //根据图片的个数,去添加点的个数
        insertPoint();
        mLoopPager.setCurrentItem(mLooperPagerAdapter.getDataRealSize() * 100, true);
    }

    private void insertPoint() {
        for (int i = 0; i < 4; i++) {
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
        if (mLooperPagerAdapter.getDataRealSize() != 0) {
            realPosition = position % mLooperPagerAdapter.getDataRealSize();
        } else {
            realPosition = 0;
        }
        setSelectPoint(realPosition);
    }

    private void setSelectPoint(int realPosition) {
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

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

}
