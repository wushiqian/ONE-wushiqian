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
import com.wushiqian.bean.Music;
import com.wushiqian.util.ImageLoadTask;

import java.util.List;

/**
* 音乐列表的适配器
* @author wushiqian
* created at 2018/5/25 20:17
*/
public class MusicAdapter extends BaseAdapter {

    private List<Music> list;
    private ListView listView;

    public MusicAdapter(List<Music> list) {
        super();
        this.list = list;
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
        holder.iv.setImageResource(R.drawable.one);
        // 如果本地已有缓存，就从本地读取，否则从网络请求数据
        if(holder.iv.getTag()!=null && holder.iv.getTag().equals(list.get(position).getImageUrl())) {
            //解决错位，闪烁的问题
                ImageLoadTask it = new ImageLoadTask(listView);
                it.execute(music.getImageUrl());
        }
        return convertView;
    }

    class ViewHolder {
        ImageView iv;
        TextView title, forward;
    }

}
