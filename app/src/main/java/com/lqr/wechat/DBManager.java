package com.lqr.wechat;

/**
 * Created by wangbingbing on 2017/5/11.
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.lqr.wechat.model.CaseRecount;
import com.lqr.wechat.model.Image;
import com.lqr.wechat.model.UserInfo;

public class DBManager {
    private DBHelper helper;
    private SQLiteDatabase db;

    private static final String TAG = "DBManagerActivity";
    public DBManager(Context context) {
        helper = new DBHelper(context);
        //因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0, mFactory);
        //所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
        db = helper.getWritableDatabase();
    }

    public void addUser(UserInfo user) {
        db.beginTransaction();  //开始事务
        try {
            Log.d(TAG,"create userInfo table ----------------------------->");
            db.execSQL("INSERT INTO userInfo VALUES(null, ?, ?, ?,?,?,?,?,?)", new Object[]{user.account,user.userName, user.sex, user.age});
            db.setTransactionSuccessful();  //设置事务成功完成
        } finally {
            db.endTransaction();    //结束事务
        }
    }
    public void addImage(List<Image> images){
        db.beginTransaction();  //开始事务
        try {
            Log.d(TAG,"create image table ----------------------------->");
            for(Image image : images){
                db.execSQL("INSERT INTO image VALUES(?, ?)", new Object[]{image.imgId,image.img});
            }
            db.setTransactionSuccessful();  //设置事务成功完成
        } finally {
            db.endTransaction();    //结束事务
        }
    }
    public void addCaseRecount(CaseRecount caseRecount){
        db.beginTransaction();  //开始事务
        try {
            Log.d(TAG,"create caseRecount table ----------------------------->");
            db.execSQL("INSERT INTO caseRecount VALUES(null,?,?,?,?,?,?,?,?,?,?)", new Object[]{caseRecount.userAccount, caseRecount.date, caseRecount.imgRand, caseRecount.historyRecount, caseRecount.historyCurCase, caseRecount.historyPastCase, caseRecount.historySigns, caseRecount.assayRecount, caseRecount.imageRecount, caseRecount.medicationRecount});
            db.setTransactionSuccessful();  //设置事务成功完成
        } finally {
            db.endTransaction();    //结束事务
        }
    }


    public void updateAge(CaseRecount caseRecount) {
        ContentValues cv = new ContentValues();
        cv.put("historyRecount", caseRecount.historyRecount);
        db.update("person", cv, "userName = ?,date = ?,imgRand = ?", new String[]{caseRecount.userAccount,caseRecount.date,caseRecount.imgRand});
    }

    public void deleteImage(Image image) {
        db.delete("image", "imgId = ?", new String[]{String.valueOf(image.imgId)});
    }

    public List<UserInfo> query() {
        ArrayList<UserInfo> userInfos = new ArrayList<UserInfo>();
        Cursor c = queryTheCursor();
        while (c.moveToNext()) {
            UserInfo userInfo = new UserInfo();
            userInfo.account = c.getString(c.getColumnIndex("account"));
            userInfo.userName = c.getString(c.getColumnIndex("userName"));
            userInfo.sex = c.getString(c.getColumnIndex("sex"));
            userInfo.age = c.getInt(c.getColumnIndex("age"));

            userInfos.add(userInfo);
        }
        c.close();
        return userInfos;
    }

    public Cursor queryTheCursor() {
        Cursor c = db.rawQuery("SELECT * FROM image", null);
        return c;
    }

    public void closeDB() {
        db.close();
    }

    public static String getRandomString(int len) {
        String t = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        String n = "";
        for (int  r = 0; len > r; ++r){
            n+=t.charAt(new Random().nextInt(t.length()));
        }
        return n;
    }
}
