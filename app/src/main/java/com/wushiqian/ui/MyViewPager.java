package com.wushiqian.ui;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
* 自定义ViewPager
* @author wushiqian
* created at 2018/5/26 13:42
*/
public class MyViewPager extends ViewPager {

    private OnViewPagerTouchListener mTouchListener = null;

    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyViewPager(Context context) {
        super(context);
    }


    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mTouchListener != null) {
                    mTouchListener.onPagerTouch(true);
                }
//                float downX =  ev.getX()
//                long downTime = ev.getDownTime();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                performClick();//主动点击事件，同时实现onTouch和onClick监听器时消除不必要错误。
                if (mTouchListener != null) {
                    mTouchListener.onPagerTouch(false);
                }
//                if (System.currentTimeMillis() - downTime < 500
//                        && Math.abs(downX - ev.getX()) < 30) {// 考虑到手按下和抬起时的坐标不可能完全重合，这里给出30的坐标偏差
//                    // 点击事件被触发
//                    Toast.makeText(MyApplication.getContext(),"error",Toast.LENGTH_SHORT).show();
//                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    public void setOnViewPagerTouchListener(OnViewPagerTouchListener listener) {
        this.mTouchListener = listener;
    }

    public interface OnViewPagerTouchListener {
        void onPagerTouch(boolean isTouch);
    }

}