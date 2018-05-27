package com.wushiqian.adapter;

import android.graphics.drawable.BitmapDrawable;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.wushiqian.one_wushiqian.R;
import com.wushiqian.bean.Comment;
import com.wushiqian.util.CacheUtil;
import com.wushiqian.util.ImageLoadTask;

import java.util.List;

/**
* 评论列表的适配器
* @author wushiqian
* created at 2018/5/25 20:17
*/
public class CommentAdapter extends BaseAdapter {

    private List<Comment> list;
    private ListView listView;
    private LruCache<String, BitmapDrawable> mImageCache;
    private CacheUtil mCache;

    public CommentAdapter(List<Comment> list) {
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
                    R.layout.comment_item , null);
            holder = new ViewHolder();
            holder.mIvUser = convertView.findViewById(R.id.comment_iv_user);
            holder.mTvUserName = convertView.findViewById(R.id.comment_tv_user);
            holder.mTvtime = convertView.findViewById(R.id.comment_tv_time);
            holder.mTvcomment = convertView.findViewById(R.id.comment_tv_comment);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Comment comment = list.get(position);
        holder.mTvUserName.setText(comment.getUserName());
        holder.mTvtime.setText(comment.getCommentTime());
        holder.mTvcomment.setText(comment.getComment());
        holder.mIvUser.setTag(comment.getImageUrl());
        // 如果本地已有缓存，就从本地读取，否则从网络请求数据
        if (mImageCache.get(comment.getImageUrl()) != null) {
            holder.mIvUser.setImageDrawable(mImageCache.get(comment.getImageUrl()));
        } else {
            ImageLoadTask it = new ImageLoadTask(listView,mImageCache);
            it.execute(comment.getImageUrl());
        }
        return convertView;
    }

    class ViewHolder {
        ImageView mIvUser;
        TextView mTvUserName, mTvtime , mTvcomment;
    }

}
