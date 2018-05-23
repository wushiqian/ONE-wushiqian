package com.wushiqian.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.wushiqian.one_wushiqian.R;
import com.wushiqian.bean.Music;
import com.wushiqian.util.CacheUtil;
import com.wushiqian.util.MyApplication;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static android.app.PendingIntent.getActivity;

public class MusicAdapter extends BaseAdapter {

    private List<Music> list;
    private ListView listView;
    private LruCache<String, BitmapDrawable> mImageCache;
    private CacheUtil mCache;

    public MusicAdapter(List<Music> list) {
        super();
        this.list = list;
        int maxCache = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxCache / 8;
        mImageCache = new LruCache<String, BitmapDrawable>(cacheSize) {
            @Override
            protected int sizeOf(String key, BitmapDrawable value) {
                return value.getBitmap().getByteCount();
            }
        };

    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (listView == null) {
            listView = (ListView) parent;
        }
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.music_item, null);
            holder = new ViewHolder();
            holder.iv = convertView.findViewById(R.id.music_iv);
            holder.title = convertView.findViewById(R.id.music_title);
            holder.forward = convertView.findViewById(R.id.music_forward);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Music music = list.get(position);
        holder.title.setText(music.getTitle());
        holder.forward.setText(music.getForward());
        holder.iv.setTag(music.getImageUrl());
        // 如果本地已有缓存，就从本地读取，否则从网络请求数据
        if (mImageCache.get(music.getImageUrl()) != null) {
            holder.iv.setImageDrawable(mImageCache.get(music.getImageUrl()));
        } else {
            ImageTask it = new ImageTask();
            it.execute(music.getImageUrl());
        }
        return convertView;
    }

    class ViewHolder {
        ImageView iv;
        TextView title, forward;
    }

    class ImageTask extends AsyncTask<String, Void, BitmapDrawable> {

        private String imageUrl;

        @Override
        protected BitmapDrawable doInBackground(String... params) {
            imageUrl = params[0];
            Bitmap bitmap = downloadImage();
            BitmapDrawable db = new BitmapDrawable(listView.getResources(),
                    bitmap);
            // 如果本地还没缓存该图片，就缓存
            if (mImageCache.get(imageUrl) == null) {
                mImageCache.put(imageUrl, db);
            }
            return db;
        }

        @Override
        protected void onPostExecute(BitmapDrawable result) {
            // 通过Tag找到我们需要的ImageView，如果该ImageView所在的item已被移出页面，就会直接返回null
            ImageView iv = listView.findViewWithTag(imageUrl);
            if (iv != null && result != null) {
                iv.setImageDrawable(result);
            }
        }

        /**
         * 根据url从网络上下载图片
         * @return
         */
        private Bitmap downloadImage() {
            mCache = CacheUtil.get(MyApplication.getContext());
            HttpURLConnection con = null;
            Bitmap bitmap = mCache.getAsBitmap(imageUrl);
            if(bitmap == null) {
                try {
                    URL url = new URL(imageUrl);
                    con = (HttpURLConnection) url.openConnection();
                    con.setConnectTimeout(5 * 1000);
                    con.setReadTimeout(10 * 1000);
                    bitmap = BitmapFactory.decodeStream(con.getInputStream());
                    mCache.put(imageUrl, bitmap, CacheUtil.TIME_DAY);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (con != null) {
                        con.disconnect();
                    }
                }
            }
            return bitmap;
        }
    }



}
