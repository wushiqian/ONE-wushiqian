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
import com.wushiqian.bean.ArticleListItem;
import com.wushiqian.util.ImageLoadTask;

import java.util.List;

/**
* 文章列表的适配器
* @author wushiqian
* created at 2018/5/25 20:16
*/
public class ArticleAdaper extends BaseAdapter {

    private List<ArticleListItem> list;
    private ListView listView;
    private LruCache<String, BitmapDrawable> mImageCache;

    public ArticleAdaper(List<ArticleListItem> list) {
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
                    R.layout.article_item, null);
            holder = new ViewHolder();
            holder.iv = convertView.findViewById(R.id.iv);
            holder.title = convertView.findViewById(R.id.title);
            holder.summary = convertView.findViewById(R.id.summary);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ArticleListItem articleListItem = list.get(position);
        holder.title.setText(articleListItem.getTitle());
        String author = "文/" + articleListItem.getAuthor();
        holder.summary.setText(author);
        holder.iv.setTag(articleListItem.getImageUrl());
        // 如果本地已有缓存，就从本地读取，否则从网络请求数据
        if(holder.iv.getTag()!=null && holder.iv.getTag().equals(list.get(position).getImageUrl())) { //解决错位，闪烁的问题
            if (mImageCache.get(articleListItem.getImageUrl()) != null) {
                holder.iv.setImageDrawable(mImageCache.get(articleListItem.getImageUrl()));
            } else {
                ImageLoadTask it = new ImageLoadTask(listView, mImageCache);
                it.execute(articleListItem.getImageUrl());
            }
        }
        return convertView;
    }

    class ViewHolder {
        ImageView iv;
        TextView title, summary;
    }

}
