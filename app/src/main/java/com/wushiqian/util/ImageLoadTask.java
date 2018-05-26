package com.wushiqian.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.LruCache;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
* 列表的图片加载
* @author wushiqian
* created at 2018/5/25 20:22
*/
public class ImageLoadTask extends AsyncTask<String, Void, BitmapDrawable> {

    private String imageUrl;
    private ImageView bmImage;
    private ListView listView;
    private LruCache<String, BitmapDrawable> mImageCache;

    public ImageLoadTask(ListView listView,LruCache<String, BitmapDrawable> imageCache){
        this.listView = listView;
        this.mImageCache = imageCache;
    }

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
         * 先查看有没有缓存，如果没有缓存就根据url从网络上下载图片
         * @return Bitmap
         */
        private Bitmap downloadImage() {
            CacheUtil mCache = CacheUtil.get(MyApplication.getContext());
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
