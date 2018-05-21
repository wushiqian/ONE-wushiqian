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

    private Context mcontext;

    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                            int version){
        super(context, name, factory, version);
        mcontext = context;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
//        db.execSQL("drop table if exists Illustration");
//        db.execSQL("drop table if exists Article");
//        onCreate(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS "
                + CACHE + " ("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + URL + " TEXT, "
                + TIME + " TEXT, "
                + DATA + " TEXT)";
        Toast.makeText(mcontext,"Create succeeded",Toast.LENGTH_SHORT).show();
        db.execSQL(sql);

    }

}
