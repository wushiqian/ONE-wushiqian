package com.wushiqian.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.LruCache;
import android.widget.ImageView;

import java.io.InputStream;

/**
* 图片加载类
* @author wushiqian
* created at 2018/5/27 1:36
*/
public class ImageManager extends AsyncTask<String, Void, Bitmap> {

    private final static String TAG = "ImageManager";
    private LruCache<String, Bitmap> mImageCache;
    private CacheUtil mCacheUtil;
    private ImageView mImageView;

    public ImageManager(ImageView imageView){
        this.mImageView = imageView;
        mCacheUtil = CacheUtil.get(MyApplication.getContext());
        int maxCache = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxCache / 8;
        mImageCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
    }

    protected Bitmap doInBackground(String... urls) {
        Bitmap bitmap = null;
        String urldisplay = urls[0];
        // 如果本地已有缓存，就从本地读取，否则从网络请求数据
        if (mImageCache.get(urldisplay) != null) {
            bitmap = mImageCache.get(urldisplay);
        } else if(mCacheUtil.getAsBitmap(urldisplay) != null){
            bitmap = mCacheUtil.getAsBitmap(urldisplay);
        }else{
            LogUtil.d(TAG,"网络加载的图片");
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                bitmap = BitmapFactory.decodeStream(in);
                mCacheUtil.put(urldisplay,bitmap,12 * CacheUtil.TIME_HOUR);
                mImageCache.put(urldisplay,bitmap);
            } catch (Exception e) {
                LogUtil.e("Error", e.getMessage());
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    protected void onPostExecute(Bitmap result) {
        mImageView.setImageBitmap(result);
    }

}
