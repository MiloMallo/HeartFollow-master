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
        //db.execSQL("DROP TABLE IF EXISTS person");
        db.execSQL("CREATE TABLE IF NOT EXISTS person" +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT,account VARCHAR, name VARCHAR, age INTEGER, sex VARCHAR,historyImgRand VARCHAR,arrayImgRand VARCHAR,imageImgRand VARCHAR,medicationImgRand VARCHAR)");
        db.execSQL("CREATE TABLE IF NOT EXISTS image" +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT,String imgId,img BLOB)");
        db.execSQL("CREATE TABLE IF NOT EXISTS caseInfo" +
                "(imgRand VARCHAR PRIMARY KEY,historyRecount VARCHAR, historyCurCase VARCHAR, historyPastCase VARCHAR, historySigns VARCHAR, assayRecount VARCHAR, imageRecount VARCHAR, medicationRecount VARCHAR)");
    }

    //如果DATABASE_VERSION值被改为2,系统发现现有数据库版本不同,即会调用onUpgrade
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("ALTER TABLE person ADD COLUMN other STRING");
    }
}
