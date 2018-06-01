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
import com.wushiqian.bean.Film;
import com.wushiqian.util.ImageLoadTask;

import java.util.List;

/**
* 电影列表的适配器
* @author wushiqian
* created at 2018/5/25 20:17
*/
public class FilmAdapter extends BaseAdapter{

    private List<Film> list;
    private ListView listView;

    public FilmAdapter(List<Film> list) {
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
                    R.layout.film_item, null);
            holder = new ViewHolder();
            holder.iv = convertView.findViewById(R.id.film_iv);
            holder.title = convertView.findViewById(R.id.film_title);
            holder.forward = convertView.findViewById(R.id.film_forward);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Film film = list.get(position);
        holder.title.setText(film.getTitle());
        holder.forward.setText(film.getForward());
        holder.iv.setImageResource(R.drawable.one);//还未加载完成时默认显示这张图片
        holder.iv.setTag(film.getImageUrl());
        // 如果本地已有缓存，就从本地读取，否则从网络请求数据
        if(holder.iv.getTag() != null && holder.iv.getTag().equals(list.get(position).getImageUrl())) { //解决错位，闪烁的问题
                ImageLoadTask it = new ImageLoadTask(listView);
                it.execute(film.getImageUrl());
        }
        return convertView;
    }

    class ViewHolder {
        ImageView iv;
        TextView title, forward;
    }

}
