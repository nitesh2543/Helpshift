package com.example.nitesh.myapplication.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.nitesh.myapplication.util.Constants;

/**
 * Created by nitesh on 6/10/17.
 */

public class DBHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "question.db";
    public static final int DB_VERSION = 1;
    public static final String TABLE_NAME = "question";

    public static final String COL_PRIMARY_KEY = "_id";


    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table "+TABLE_NAME+" ("+ Constants.Key.TITLE+" text,"+Constants.Key.UP_VOTES+" text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists "+TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
