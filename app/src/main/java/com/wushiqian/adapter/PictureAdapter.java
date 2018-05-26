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
import com.wushiqian.bean.Picture;
import com.wushiqian.util.ImageLoadTask;

import java.util.List;

/**
* 插画列表的适配器
* @author wushiqian
* created at 2018/5/25 20:18
*/
public class PictureAdapter extends BaseAdapter {

    private List<Picture> list;
    private ListView listView;
    private LruCache<String, BitmapDrawable> mImageCache;

    public PictureAdapter(List<Picture> list) {
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
                    R.layout.picture_item, null);
            holder = new ViewHolder();
            holder.iv = convertView.findViewById(R.id.picture_iv);
            holder.iv.setImageResource(R.drawable.one);
            holder.message = convertView.findViewById(R.id.picture_tv_message);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Picture picture = list.get(position);
        holder.message.setText(picture.getMessage());
        holder.iv.setTag(picture.getImageUrl());
        // 如果本地已有缓存，就从本地读取，否则从网络请求数据
        if(holder.iv.getTag() != null && holder.iv.getTag().equals(list.get(position).getImageUrl())) { //解决错位，闪烁的问题
            if (mImageCache.get(picture.getImageUrl()) != null) {
                holder.iv.setImageDrawable(mImageCache.get(picture.getImageUrl()));
            } else {
                ImageLoadTask it = new ImageLoadTask(listView, mImageCache);
                it.execute(picture.getImageUrl());
            }
        }
        return convertView;
    }

    class ViewHolder {
        ImageView iv;
        TextView message;
    }

}
