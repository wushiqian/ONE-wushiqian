package com.wushiqian.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.wushiqian.bean.Picture;
import com.wushiqian.util.CacheUtil;
import com.wushiqian.util.LogUtil;
import com.wushiqian.util.MyApplication;

import java.io.InputStream;
import java.util.List;

public class ViewPagerAdapter extends PagerAdapter {

    private static final String TAG = "PagerAdapter";
    private List<Picture> mPics = null;
    private CacheUtil mCache;

    @Override
    public int getCount() {
        if (mPics != null) {
            return Integer.MAX_VALUE;
        }
        return 0;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        int realPosition = position % mPics.size();
        ImageView imageView = new ImageView(container.getContext());
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        //imageView.setBackgroundColor(mPics.get(position));
        new DownloadImageTask(imageView)
                .execute("" + mPics.get(realPosition).getImageUrl());
//        imageView.setImageResource(mPics.get(realPosition).getImgId());
        //设置完数据以后,就添加到容器里
        container.addView(imageView);
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public void setData(List<Picture> colos) {
        this.mPics = colos;
    }

    public int getDataRealSize() {
        if (mPics != null) {
            return mPics.size();
        }
        return 0;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            mCache = CacheUtil.get(MyApplication.getContext());
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
