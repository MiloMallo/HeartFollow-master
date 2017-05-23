package com.lqr.wechat;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
    private static final String TAG = "DBHelperActivity";
    private static final String DATABASE_NAME = "heartFollow.db";
    private static final int DATABASE_VERSION = 1;

    public DBHelper(Context context) {
        //CursorFactory设置为null,使用默认值
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //数据库第一次被创建时onCreate会被调用
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG,"create table ----------------------------->");
        db.execSQL("DROP TABLE IF EXISTS UserInfo");
        db.execSQL("DROP TABLE IF EXISTS CaseRecount");
        db.execSQL("DROP TABLE IF EXISTS Image");
        db.execSQL("CREATE TABLE IF NOT EXISTS UserInfo" +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT,account VARCHAR, name VARCHAR, age INTEGER, sex VARCHAR)");
        db.execSQL("CREATE TABLE IF NOT EXISTS CaseRecount" +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT,userAccount VARCHAR,date VARCHAR, imgRand VARCHAR, historyRecount VARCHAR, historyCurCase VARCHAR, historyPastCase VARCHAR, historySigns VARCHAR, assayRecount VARCHAR, imageRecount VARCHAR, medicationRecount VARCHAR)");
        db.execSQL("CREATE TABLE IF NOT EXISTS Image" +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT,imgId VARCHAR,img BLOB)");
    }

    //如果DATABASE_VERSION值被改为2,系统发现现有数据库版本不同,即会调用onUpgrade
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("ALTER TABLE UserInfo ADD COLUMN other STRING");
    }
}
