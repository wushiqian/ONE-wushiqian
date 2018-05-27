package com.wushiqian.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.wushiqian.bean.Picture;
import com.wushiqian.util.CacheUtil;
import com.wushiqian.util.ImageManager;

import java.util.List;

/**
* 轮播图的适配器
* @author wushiqian
* created at 2018/5/25 20:18
*/
public class ViewPagerAdapter extends PagerAdapter {

    private static final String TAG = "PagerAdapter";
    private List<Picture> mPics = null;
    private CacheUtil mCache;

    /**
     * 获取View的总数
     * @return View总数
     */
    @Override
    public int getCount() {
        if (mPics != null) {
            return Integer.MAX_VALUE;
        }
        return 0;
    }

    /**
     * 为给定的位置创建相应的View。创建View之后,需要在该方法中自行添加到container中。
     * @param container ViewPager本身
     * @param position  给定的位置
     * @return 提交给ViewPager进行保存的实例对象
     */
    @Override
    public Object instantiateItem(final ViewGroup container, int position) {
        final int realPosition = position % mPics.size();
        final ImageView imageView = new ImageView(container.getContext());
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        new ImageManager(imageView)
                .execute(mPics.get(realPosition).getImageUrl());
        //设置完数据以后,就添加到容器里
        container.addView(imageView);
        return imageView;
    }

    /**
     * 为给定的位置移除相应的View。
     * @param container ViewPager本身
     * @param position  给定的位置
     * @param object    在instantiateItem中提交给ViewPager进行保存的实例对象
     */
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    /**
     * 确认View与实例对象是否相互对应。ViewPager内部用于获取View对应的ItemInfo。
     * @param view   ViewPager显示的View内容
     * @param object 在instantiateItem中提交给ViewPager进行保存的实例对象
     * @return 是否相互对应
     */
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    /**
     *设置数据
     * @param list  数据List
     */
    public void setData(List<Picture> list) {
        this.mPics = list;
    }

    /**
     * get数据的真实长度
     * @return 数据的真实长度
     */
    public int getDataRealSize() {
        if (mPics != null) {
            return mPics.size();
        }
        return 0;
    }

}
