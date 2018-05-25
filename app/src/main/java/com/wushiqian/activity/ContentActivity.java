package com.wushiqian.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wushiqian.one_wushiqian.R;
import com.wushiqian.adapter.CommentAdapter;
import com.wushiqian.bean.Comment;
import com.wushiqian.ui.LoadMoreListView;
import com.wushiqian.util.ApiUtil;
import com.wushiqian.util.CacheUtil;
import com.wushiqian.util.HttpCallbackListener;
import com.wushiqian.util.HttpUtil;
import com.wushiqian.util.LogUtil;

import org.json.JSONArray;
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
public class ContentActivity extends AppCompatActivity {

    private static final String TAG = "ContentActivity";
    private android.support.v7.widget.Toolbar mToolbar;
    private static final int UPDATE_TEXT = 1;
    private static final int TOAST = 2;
    private static final int UPDATE_COMMENT = 3;
    private static final int UPDATE_AUTHOR = 4;
    private List<Comment> mCommentList = new ArrayList<>();
    private CommentAdapter adapter;
    private String result = "";
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
    private String author = "";
    private int itemId;
    private String title = "";
    private String introauthor = "";
    private String address = "";
    private String type = "";
    private String copyright = "";
    private String authorImaUrl = "";
    private String authorDesc = "";
    private String coverUrl = "";
    private String titleInfo = "";
    private CacheUtil mCache;

    private Handler handler = new Handler() {
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case UPDATE_TEXT:
//                    final Spanned text = Html.fromHtml(result,imageGetter,null);
//                    mTextView.setText(text);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        mTextView.setText(Html.fromHtml(result, Html.FROM_HTML_MODE_LEGACY, imageGetter, null));
                    } else {
                        mTextView.setText(Html.fromHtml(result, imageGetter, null));
                    }
                    mTvright.setText(copyright);
                    mTvIntroduce.setText(introauthor);
                    mTvTitle.setText(title);
                    mTvAuthor.setText(titleInfo);
                    new DownloadImageTask(mIvCover)
                            .execute(coverUrl);
                    break;
                case  TOAST:
                    Toast.makeText(ContentActivity.this,"error",Toast.LENGTH_SHORT).show();
                    break;
                case UPDATE_COMMENT:
                    adapter = new CommentAdapter(mCommentList);
                    mTvComment.setAdapter(adapter);
                case UPDATE_AUTHOR:
                    mTvDesc.setText(authorDesc);
                    mTvAuthorName.setText(author);
                    new DownloadImageTask(mIvauthor)
                        .execute("" + authorImaUrl);
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
            initContent();
        }else if(type.equals("music")){
            initMusic();
        }else if(type.equals("movie")){
            initMovie();
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
                                String data = jsonObjectMovie.getString("data");
                                JSONArray jsonArray = new JSONArray(data);
                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                                String user = jsonObject.getString("user");
                                JSONObject jsonObject1 = new JSONObject(user);
                                title = jsonObject.getString("title");
                                result = jsonObject.getString("content");
                                String userName = jsonObject1.getString("user_name");
                                titleInfo = "文/" + userName;
                                introauthor = jsonObject.getString("charge_edt");
                                copyright = jsonObject.getString("copyright");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Message message = new Message();
                                    message.what = UPDATE_TEXT;
                                    handler.sendMessage(message);
                                }
                            }).start();
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
                }else{
                    JSONObject jsonObjectMovie = mCache.getAsJSONObject(address);
                    mCache.put(address,jsonObjectMovie,CacheUtil.TIME_HOUR);
                    try{
                        String data = jsonObjectMovie.getString("data");
                        JSONArray jsonArray = new JSONArray(data);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        String user = jsonObject.getString("user");
                        JSONObject jsonObject1 = new JSONObject(user);
                        title = jsonObject.getString("title");
                        result = jsonObject.getString("content");
                        String userName = jsonObject1.getString("user_name");
                        titleInfo = "文/" + userName;
                        introauthor = jsonObject.getString("charge_edt");
                        copyright = jsonObject.getString("copyright");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Message message = new Message();
                    message.what = UPDATE_TEXT;
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
                        mCache.put(address, jsonObject, CacheUtil.TIME_HOUR);
                        title = jsonObject.getString("story_title");
                        result = jsonObject.getString("story");
                        coverUrl = jsonObject.getString("cover");
                        String info = jsonObject.getString("info");
                        String title = jsonObject.getString("title");
                        titleInfo = title + "\n" + info;
                        introauthor = jsonObject.getString("charge_edt");
                        copyright = jsonObject.getString("copyright");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Message message = new Message();
                    message.what = UPDATE_TEXT;
                    handler.sendMessage(message);
                } else {
                    LogUtil.d("ContentActivity", "网络加载");
                    HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
                        @Override
                        public void onFinish(final String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                mCache.put(address, jsonObject, CacheUtil.TIME_HOUR);
                                title = jsonObject.getString("story_title");
                                result = jsonObject.getString("story");
                                coverUrl = jsonObject.getString("cover");
                                String info = jsonObject.getString("info");
                                String title = jsonObject.getString("title");
                                titleInfo = title + "\n" + info;
                                introauthor = jsonObject.getString("charge_edt");
                                copyright = jsonObject.getString("copyright");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Message message = new Message();
                            message.what = UPDATE_TEXT;
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

    private void initView() {
        mCache = CacheUtil.get(this);
        mToolbar = findViewById(R.id.content_toolBar);
        //设置成actionbar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("一个");
        mToolbar.setLogo(R.drawable.article2);
        //设置返回图标
        mToolbar.setNavigationIcon(R.drawable.back2);
        //返回事件
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
    }

    private void initAuthor() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(mCache.getAsJSONObject(address + type) == null){
                    HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
                        @Override
                        public void onFinish(final String response) {
                            try {
                                if(type.equals("music")){
                                    JSONObject jsonAuthor = new JSONObject(response);
                                    mCache.put(address + type,jsonAuthor, CacheUtil.TIME_HOUR);
                                    String data = jsonAuthor.getString("story_author");
                                    JSONObject jsonObject = new JSONObject(data);
                                    author = jsonObject.getString("user_name");
                                    authorImaUrl = jsonObject.getString("web_url");
                                    authorDesc = jsonObject.getString("desc");
                                }else if(type.equals("movie")) {
                                    JSONObject jsonAuthor = new JSONObject(response);
                                    mCache.put(address + type,jsonAuthor,CacheUtil.TIME_HOUR);
                                    String data = jsonAuthor.getString("data");
                                    JSONArray jsonArray = new JSONArray(data);
                                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                                    String authorData = jsonObject.getString("user");
                                    JSONObject jsonObject1 = new JSONObject(authorData);
                                    author = jsonObject1.getString("user_name");
                                    authorImaUrl = jsonObject1.getString("web_url");
                                    authorDesc = jsonObject1.getString("desc");
                                }else {
                                    JSONObject jsonAuthor = new JSONObject(response);
                                    mCache.put(address + type,jsonAuthor,CacheUtil.TIME_HOUR);
                                    String data = jsonAuthor.getString("author");
                                    JSONArray jsonArray = new JSONArray(data);
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        author = jsonObject.getString("user_name");
                                        authorImaUrl = jsonObject.getString("web_url");
                                        authorDesc = jsonObject.getString("desc");
                                    }
                                }
                            }catch (JSONException e){
                                e.printStackTrace();
                            }

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Message message = new Message();
                                    message.what = UPDATE_AUTHOR;
                                    handler.sendMessage(message);
                                }
                            }).start();
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
                    try {
                        if(type.equals("music")){
                            mCache.put(address + type,jsonAuthor,CacheUtil.TIME_HOUR);
                            String data = jsonAuthor.getString("story_author");
                            JSONObject jsonObject = new JSONObject(data);
                            author = jsonObject.getString("user_name");
                            authorImaUrl = jsonObject.getString("web_url");
                            authorDesc = jsonObject.getString("desc");
                        }else if(type.equals("movie")) {
                            mCache.put(address + type,jsonAuthor,CacheUtil.TIME_HOUR);
                            String data = jsonAuthor.getString("data");
                            JSONArray jsonArray = new JSONArray(data);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            String authorData = jsonObject.getString("user");
                            JSONObject jsonObject1 = new JSONObject(authorData);
                            author = jsonObject1.getString("user_name");
                            authorImaUrl = jsonObject1.getString("web_url");
                            authorDesc = jsonObject1.getString("desc");
                        }else {
                            mCache.put(address + type,jsonAuthor,CacheUtil.TIME_HOUR);
                            String data = jsonAuthor.getString("author");
                            JSONArray jsonArray = new JSONArray(data);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                author = jsonObject.getString("user_name");
                                authorImaUrl = jsonObject.getString("web_url");
                                authorDesc = jsonObject.getString("desc");
                            }
                        }
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                    Message message = new Message();
                    message.what = UPDATE_AUTHOR;
                    handler.sendMessage(message);
                }
            }
        }).start();
    }

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
                            Comment comment = null;
                            try{
                                JSONObject jsonComment = new JSONObject(response);
                                mCache.put("http://v3.wufazhuce.com:8000/api/comment/praiseandtime/" + type
                                        + "/" + itemId + "/0?&platform=android",jsonComment,CacheUtil.TIME_HOUR);
                                String data = jsonComment.getString("data");
                                JSONArray jsonArray = new JSONArray(data);
                                for(int i = 0; i < jsonArray.length();i++){
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    String user = jsonObject.getString("user");
                                    JSONObject jo = new JSONObject(user);
                                    String imageUrl = jo.getString("web_url");
                                    String userName = jo.getString("user_name");
                                    String time = jsonObject.getString("input_date");
                                    String scomment = jsonObject.getString("content");
                                    int praiseNum = jsonObject.getInt("praisenum");
                                    comment = new Comment(imageUrl,userName,time,scomment,praiseNum);
                                    mCommentList.add(comment);
                                }
                            } catch(Exception e){
                                e.printStackTrace();
                            }
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Message message = new Message();
                                    message.what = UPDATE_COMMENT;
                                    handler.sendMessage(message);
                                }
                            }).start();
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
                }else{
                    JSONObject jsonComment = mCache.getAsJSONObject(ApiUtil.COMMENT_URL_PRE + type + "/" + itemId
                            + ApiUtil.COMMENT_URL_SUF);
                    try{
                        mCache.put(ApiUtil.COMMENT_URL_PRE + type + "/" + itemId
                                + ApiUtil.COMMENT_URL_SUF,jsonComment,CacheUtil.TIME_HOUR);
                        String data = jsonComment.getString("data");
                        JSONArray jsonArray = new JSONArray(data);
                        for(int i = 0; i < jsonArray.length();i++){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String user = jsonObject.getString("user");
                            JSONObject jo = new JSONObject(user);
                            String imageUrl = jo.getString("web_url");
                            String userName = jo.getString("user_name");
                            String time = jsonObject.getString("input_date");
                            String scomment = jsonObject.getString("content");
                            int praiseNum = jsonObject.getInt("praisenum");
                            Comment comment = new Comment(imageUrl,userName,time,scomment,praiseNum);
                            mCommentList.add(comment);
                        }
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

    private void initContent() {
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
                                title = jsonObjectArticle.optString("hp_title");
                                result = jsonObjectArticle.optString("hp_content");
                                String author = jsonObjectArticle.optString("hp_author");
                                titleInfo = "文/" + author;
                                introauthor = jsonObjectArticle.optString("hp_author_introduce");
                                copyright = jsonObjectArticle.optString("copyright");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Message message = new Message();
                                    message.what = UPDATE_TEXT;
                                    handler.sendMessage(message);
                                }
                            }).start();
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
                }else{
                    try {
                        JSONObject jsonObjectArticle = mCache.getAsJSONObject(address);
                        mCache.put(address,jsonObjectArticle,CacheUtil.TIME_HOUR);
                        title = jsonObjectArticle.optString("hp_title");
                        result = jsonObjectArticle.optString("hp_content");
                        String author = jsonObjectArticle.optString("hp_author");
                        titleInfo = "文/" + author;
                        introauthor = jsonObjectArticle.optString("hp_author_introduce");
                        copyright = jsonObjectArticle.optString("copyright");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Message message = new Message();
                    message.what = UPDATE_TEXT;
                    handler.sendMessage(message);
                }
            }
        }).start();
    }

    Html.ImageGetter imageGetter = new Html.ImageGetter() {
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
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth() + 700, drawable.getIntrinsicHeight() + 525);
            return drawable;
        }
    };

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

    public DownloadImageTask(ImageView bmImage) {
        this.bmImage = bmImage;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap mIcon11 = mCache.getAsBitmap(urldisplay);
        if (mIcon11 == null) {
            LogUtil.d(TAG,"网络加载的图片");
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
                mCache.put(urldisplay,mIcon11,12 * CacheUtil.TIME_HOUR);
            } catch (Exception e) {
                LogUtil.e("Error", e.getMessage());
                e.printStackTrace();
            }
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        bmImage.setImageBitmap(result);
    }
}

}
