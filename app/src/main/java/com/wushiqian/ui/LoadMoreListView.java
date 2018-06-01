package com.wushiqian.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.example.wushiqian.one_wushiqian.R;

public class LoadMoreListView extends ListView implements AbsListView.OnScrollListener{
    private View mFootView;
    private int mTotalItemCount;//item总数
    private OnLoadMoreListener mLoadMoreListener;
    private boolean mIsLoading = false;//是否正在加载

    public LoadMoreListView (Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public LoadMoreListView (Context context) {
        super(context);
        init(context);
    }

    public LoadMoreListView (Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //解决显示不全的问题
        heightMeasureSpec=MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE>>2,MeasureSpec.AT_MOST);
        //在这里将宽高模式改为MeasureSpec.AT_MOST这样就会去测量计算所有条目的高度
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void init(Context context){
        mFootView = LayoutInflater.from(context).inflate(R.layout.foot_view,null);
        setOnScrollListener(this);
    }

    @Override
    public void onScrollStateChanged(AbsListView listView, int scrollState) {
        // 滑到底部后，判断listview已经停止滚动并且最后可视的条目等于adapter的条目
        int lastVisibleIndex = listView.getLastVisiblePosition();
        if (!mIsLoading && scrollState == OnScrollListener.SCROLL_STATE_IDLE//停止滚动
                && lastVisibleIndex == mTotalItemCount-1) {//滑动到最后一项
            mIsLoading = true;
            addFooterView(mFootView);
            if (mLoadMoreListener != null) {
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mLoadMoreListener.onloadMore();
                    }
                }, 1000);
            }
        }
    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        mTotalItemCount = totalItemCount;
    }

    public void setLoadCompleted(){
        mIsLoading = false;
        removeFooterView(mFootView);
    }

    public void setONLoadMoreListener(OnLoadMoreListener listener){
        mLoadMoreListener = listener;
    }

    public interface OnLoadMoreListener{
        void onloadMore();
    }

}

