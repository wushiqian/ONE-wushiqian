package com.wushiqian.db;

import android.app.Application;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wushiqian.util.MyApplication;

public class DBManager {
    private SQLiteDatabase db;
    private MyDatabaseHelper dbHelper;

    public DBManager() {
//        dbHelper = new MyDatabaseHelper(MyApplication.getContext());
    }

    /**
     * 插入缓存，没有就插入，有就替换
     *
     * @param url  地址
     * @param data json数据
     */
    public synchronized void insertData(String url, String data) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MyDatabaseHelper.URL, url);
        values.put(MyDatabaseHelper.DATA, data);
        values.put(MyDatabaseHelper.TIME, System.currentTimeMillis());
        db.replace(MyDatabaseHelper.CACHE, null, values);
        db.close();
    }

    /**
     * 根据url获取缓存数据
     * @param url 地址
     * @return 数据
     */
    public synchronized String getData(String url) {
        String result = "";
        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + MyDatabaseHelper.CACHE + " WHERE URL = ?", new String[]{url});
        while (cursor.moveToNext()) {
            result = cursor.getString(cursor.getColumnIndex(MyDatabaseHelper.DATA));
        }
        cursor.close();
        db.close();
        return result;
    }


}