package com.wushiqian.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    public static final int version = 1;
    public static final String CACHE = "cache";
    public static final String ID = "id";
    public static final String URL = "url";
    public static final String DATA = "data";
    public static final String TIME = "time";

    public static final String CREATE_ESSAY = "create table Essay ("
            + "title text primary key,"
            + "author text,"
            + "imageUrl text )";

    public static final String CREATE_MUSIC = "create table Music ("
            + "id integer primary key autoincrement,"
            + "listid integer,"
            + "itemId integer,"
            + "title text,"
            + "forward text,"
            + "imageUrl text )";

    private Context mcontext;

    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                            int version){
        super(context, name, factory, version);
        mcontext = context;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("drop table if exists Essay");
        db.execSQL("drop table if exists Music");
        onCreate(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_ESSAY);
        db.execSQL(CREATE_MUSIC);
//        Toast.makeText(mcontext,"Create succeeded",Toast.LENGTH_SHORT).show();
    }

}
