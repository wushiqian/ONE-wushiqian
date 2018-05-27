package com.wushiqian.activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spanned;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wushiqian.one_wushiqian.R;
import com.wushiqian.adapter.CommentAdapter;
import com.wushiqian.bean.ArticleListItem;
import com.wushiqian.bean.Author;
import com.wushiqian.bean.Comment;
import com.wushiqian.bean.Film;
import com.wushiqian.bean.Music;
import com.wushiqian.ui.LoadMoreListView;
import com.wushiqian.util.ApiUtil;
import com.wushiqian.util.CacheUtil;
import com.wushiqian.util.DensityUtil;
import com.wushiqian.util.HttpCallbackListener;
import com.wushiqian.util.HttpUtil;
import com.wushiqian.util.ImageManager;
import com.wushiqian.util.JSONUtil;
import com.wushiqian.util.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
* 内容详情
* @author wushiqian
* created at 2018/5/25 20:15
*/
public class ContentActivity extends BaseActivity {

    private static final String TAG = "ContentActivity";
    private static final int MUSIC_CONTENT = 5;
    private static final int MOVIE_CONTENT = 6;
    private android.support.v7.widget.Toolbar mToolbar;
    private static final int ARTICLE_TEXT = 1;
    private static final int TOAST = 2;
    private static final int UPDATE_COMMENT = 3;
    private static final int UPDATE_AUTHOR = 4;
    private List<Comment> mCommentList = new ArrayList<>();
    private CommentAdapter adapter;
    private TextView mTextView;
    private TextView mTvTitle;
    private TextView mTvAuthor;
    private TextView mTvIntroduce;
    private LoadMoreListView mTvComment;
    private ImageView mIvauthor;
    private ImageView mIvCover;
    private TextView mTvright;
    private TextView mTvAuthorName;
    private TextView mTvDesc;
    private int itemId;
    private String address = "";
    private String type = "";
    private CacheUtil mCache;
    private Music music;
    private Film film;
    private ArticleListItem article;
    private Author mAuthor;
    private float scaledTouchSlop;
    private float firstY = 0;
    private ObjectAnimator animtor;
    private ScrollView mScrollView;

    private Handler handler = new Handler() {
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case ARTICLE_TEXT:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final Spanned text = Html.fromHtml(article.getContent(),imageGetter,null);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    mTextView.setText(text);
                                }
                            });
                        }
                    }).start();
                    mToolbar.setTitle(article.getTitle());
                    mTvright.setText(article.getCopyright());
                    mTvIntroduce.setText(article.getIntroauthor());
                    mTvTitle.setText(article.getTitle());
                    mTvAuthor.setText(article.getTitleInfo());
                    break;
                case  TOAST:
                    Toast.makeText(ContentActivity.this,"error",Toast.LENGTH_SHORT).show();
                    break;
                case UPDATE_COMMENT:
                    adapter = new CommentAdapter(mCommentList);
                    mTvComment.setAdapter(adapter);
                    break;
                case UPDATE_AUTHOR:
                    mTvDesc.setText(mAuthor.getAuthorDesc());
                    mTvAuthorName.setText(mAuthor.getAuthor());
                    new ImageManager(mIvauthor)
                        .execute(mAuthor.getAuthorImaUrl());
                    break;
                case MUSIC_CONTENT:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final Spanned text = Html.fromHtml(music.getContent(),imageGetter,null);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    mTextView.setText(text);
                                }
                            });
                        }
                    }).start();
                    mToolbar.setTitle(music.getTitle());
                    mTvright.setText(music.getCopyright());
                    mTvIntroduce.setText(music.getIntroauthor());
                    mTvTitle.setText(music.getTitle());
                    mTvAuthor.setText(music.getTitleInfo());
                    new ImageManager(mIvCover)
                            .execute(music.getCoverUrl());
                    break;
                case MOVIE_CONTENT:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final Spanned text = Html.fromHtml(film.getContent(),imageGetter,null);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    mTextView.setText(text);
                                }
                            });
                        }
                    }).start();
                    mToolbar.setTitle(film.getTitle());
                    mTvright.setText(film.getCopyright());
                    mTvIntroduce.setText(film.getIntroauthor());
                    mTvTitle.setText(film.getTitle());
                    mTvAuthor.setText(film.getTitleInfo());
                    break;
                default: break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_content);
        Intent intent = getIntent();
        itemId = intent.getIntExtra("extra_data",0);
        address = intent.getStringExtra("url");
        type = intent.getStringExtra("type");
        LogUtil.d("ContentActivity","" + itemId);
        initView();
        initAuthor();
        initComment();
        if(type.equals("essay")){
            initArticle();
        }else if(type.equals("music")){
            initMusic();
        }else if(type.equals("movie")){
            initMovie();
        }
    }

    private void initView() {
        mCache = CacheUtil.get(this);
        mToolbar = findViewById(R.id.content_toolBar);
        setSupportActionBar(mToolbar);      //设置成actionbar
        //设置返回图标
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        //判断认为是滑动的最小距离(乘以系数调整滑动灵敏度)
        scaledTouchSlop = ViewConfiguration.get(this).getScaledTouchSlop()*3.0f;
        //返回事件
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //找到个控件
        mTextView = findViewById(R.id.article_content);
        mTvTitle = findViewById(R.id.content_tv_title);
        mTvAuthor = findViewById(R.id.content_tv_author);
        mTvIntroduce = findViewById(R.id.content_tv_introauthor);
        mTvright = findViewById(R.id.content_copyright);
        mTvComment = findViewById(R.id.content_lv_comment);
        mIvauthor = findViewById(R.id.content_iv_author);
        mTvAuthorName = findViewById(R.id.author_name);
        mTvDesc = findViewById(R.id.content_desc);
        mIvCover = findViewById(R.id.content_iv_cover);
        mScrollView = findViewById(R.id.content_sv);
        mScrollView.setOnTouchListener(new View.OnTouchListener() {
            private float currentY;
            private int direction=-1;
            private boolean mShow = true;

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        firstY = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        currentY = event.getY();
                        //向下滑动
                        if (currentY - firstY > scaledTouchSlop) {
                            direction = 0;
                            mToolbar.setVisibility(View.VISIBLE);
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
        if(Build.VERSION.SDK_INT >= 21){        // 沉浸式状态栏
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
        float translationY=mToolbar.getTranslationY();
        if (direction == 0) {
            animtor = ObjectAnimator.ofFloat(mToolbar, "translationY", translationY, 0);
            animtor.start();
        } else if (direction == 1) {
            animtor = ObjectAnimator.ofFloat(mToolbar, "translationY", translationY, -mToolbar.getHeight());
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
                            mToolbar.setVisibility(View.GONE);
                        }
                    });
                }
            }).start();
        }


    }

    private void initMovie() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(mCache.getAsJSONObject(address) == null) {
                    HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
                        @Override
                        public void onFinish(final String response) {
                            try {
                                JSONObject jsonObjectMovie = new JSONObject(response);
                                mCache.put(address,jsonObjectMovie,CacheUtil.TIME_HOUR);
                                film = JSONUtil.parseJSONMovieDetail(jsonObjectMovie);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Message message = new Message();
                            message.what = MOVIE_CONTENT;
                            handler.sendMessage(message);
                        }

                        @Override
                        public void onError(Exception e) {
                            Message message = new Message();
                            message.what = TOAST;
                            handler.sendMessage(message);
                        }
                    });
                }else{
                    JSONObject jsonObjectMovie = mCache.getAsJSONObject(address);
                    mCache.put(address,jsonObjectMovie,CacheUtil.TIME_HOUR);
                    try{
                        film = JSONUtil.parseJSONMovieDetail(jsonObjectMovie);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Message message = new Message();
                    message.what = MOVIE_CONTENT;
                    handler.sendMessage(message);
                }
            }
        }).start();

    }

    private void initMusic() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mCache.getAsJSONObject(address) != null) {
                    LogUtil.d("ContentActivity", "缓存加载");
                    try {
                        JSONObject jsonObject = mCache.getAsJSONObject(address);
                        mCache.put(address, jsonObject, 12 * CacheUtil.TIME_HOUR);
                        music = JSONUtil.parseJSONMusicDetail(jsonObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Message message = new Message();
                    message.what = MUSIC_CONTENT;
                    handler.sendMessage(message);
                } else {
                    LogUtil.d("ContentActivity", "网络加载");
                    HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
                        @Override
                        public void onFinish(final String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                mCache.put(address, jsonObject, CacheUtil.TIME_HOUR);
                                music = JSONUtil.parseJSONMusicDetail(jsonObject);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Message message = new Message();
                            message.what = MUSIC_CONTENT;
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

    private void initArticle() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(mCache.getAsJSONObject(address) == null) {
                    HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
                        @Override
                        public void onFinish(final String response) {
                            try {
                                JSONObject jsonObjectArticle = new JSONObject(response);
                                mCache.put(address,jsonObjectArticle,CacheUtil.TIME_HOUR);
                                article = JSONUtil.parseJSONArticleDetail(jsonObjectArticle);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Message message = new Message();
                            message.what = ARTICLE_TEXT;
                            handler.sendMessage(message);
                        }

                        @Override
                        public void onError(Exception e) {
                            Message message = new Message();
                            message.what = TOAST;
                            handler.sendMessage(message);
                        }
                    });
                }else{
                    try {
                        JSONObject jsonObjectArticle = mCache.getAsJSONObject(address);
                        mCache.put(address,jsonObjectArticle,CacheUtil.TIME_HOUR);
                        article = JSONUtil.parseJSONArticleDetail(jsonObjectArticle);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Message message = new Message();
                    message.what = ARTICLE_TEXT;
                    handler.sendMessage(message);
                }
            }
        }).start();
    }

    /**
    * 加载作者栏
    * @author wushiqian
    * created at 2018/5/26 17:33
    */
    private void initAuthor() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(mCache.getAsJSONObject(address + type) == null){
                    HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
                        @Override
                        public void onFinish(final String response) {
                            try {
                                JSONObject jsonAuthor = new JSONObject(response);
                                mCache.put(address + type,jsonAuthor,CacheUtil.TIME_HOUR);
                                mAuthor = JSONUtil.parseJSONAuthor(jsonAuthor,type);
                            }catch (JSONException e){
                                e.printStackTrace();
                            }
                            Message message = new Message();
                            message.what = UPDATE_AUTHOR;
                            handler.sendMessage(message);
                        }
                        @Override
                        public void onError(Exception e) {
                            Message message = new Message();
                            message.what = TOAST;
                            handler.sendMessage(message);
                        }
                    });
                }else {
                    JSONObject jsonAuthor = mCache.getAsJSONObject(address + type);
                    mCache.put(address + type,jsonAuthor,CacheUtil.TIME_HOUR);
                    mAuthor = JSONUtil.parseJSONAuthor(jsonAuthor,type);
                    Message message = new Message();
                    message.what = UPDATE_AUTHOR;
                    handler.sendMessage(message);
                }
            }
        }).start();
    }

    /**
    * 加载评论列表
    * @author wushiqian
    * created at 2018/5/26 17:33
    */
    private void initComment() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(mCache.getAsJSONObject(ApiUtil.COMMENT_URL_PRE + type + "/" + itemId
                        + ApiUtil.COMMENT_URL_SUF) == null){
                    HttpUtil.sendHttpRequest(ApiUtil.COMMENT_URL_PRE + type + "/" + itemId
                            + ApiUtil.COMMENT_URL_SUF, new HttpCallbackListener() {
                        @Override
                        public void onFinish(final String response) {
                            try{
                                JSONObject jsonComment = new JSONObject(response);
                                mCache.put(ApiUtil.COMMENT_URL_PRE + type + "/" + itemId
                                        + ApiUtil.COMMENT_URL_SUF,jsonComment,12 * CacheUtil.TIME_HOUR);
                                    mCommentList.addAll(JSONUtil.parseJSONComment(jsonComment));
                            } catch(Exception e){
                                e.printStackTrace();
                            }
                            Message message = new Message();
                            message.what = UPDATE_COMMENT;
                            handler.sendMessage(message);
                        }
                        @Override
                        public void onError(Exception e) {
                            Message message = new Message();
                            message.what = TOAST;
                            handler.sendMessage(message);
                        }
                    });
                }else{
                    JSONObject jsonComment = mCache.getAsJSONObject(ApiUtil.COMMENT_URL_PRE + type + "/" + itemId
                            + ApiUtil.COMMENT_URL_SUF);
                    try{
                        mCache.put(ApiUtil.COMMENT_URL_PRE + type + "/" + itemId
                                + ApiUtil.COMMENT_URL_SUF,jsonComment,CacheUtil.TIME_HOUR);
                        mCommentList.addAll(JSONUtil.parseJSONComment(jsonComment));
                    } catch(Exception e){
                        e.printStackTrace();
                    }
                    Message message = new Message();
                    message.what = UPDATE_COMMENT;
                    handler.sendMessage(message);
                }
            }
        }).start();
    }

    /**
    * 加载html里的图片
    * @author wushiqian
    * created at 2018/5/26 17:31
    */
    Html.ImageGetter imageGetter = new Html.ImageGetter() {
        @Nullable
        @Override
        public Drawable getDrawable(String s) {
            Drawable drawable = mCache.getAsDrawable(s);
            if(drawable == null) {
                LogUtil.d("ContentActivity","文章图片网络加载");
                InputStream is = null;
                try {
                    is = (InputStream) new URL(s).getContent();
                    drawable = Drawable.createFromStream(is, "src");
                    mCache.put(s,drawable,12 * CacheUtil.TIME_HOUR);
                    is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
            drawable.setBounds(0, 0, DensityUtil.dip2px(ContentActivity.this,
                    drawable.getIntrinsicWidth()+ 280),  DensityUtil.dip2px(ContentActivity.this,
                    //由于getIntrinsicWidth()和getIntrinsicHeight()返回的宽高应该是dp为单位
                    drawable.getIntrinsicHeight()+200));         //所以转化为px，但还是很少，，，再设大一点
            return drawable;
        }
    };

}
