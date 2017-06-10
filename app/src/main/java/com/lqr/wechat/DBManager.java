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
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
            db.execSQL("INSERT INTO UserInfo VALUES(null, ?, ?, ?,?,?,?,?,?)", new Object[]{user.account,user.userName, user.sex, user.age});
            db.setTransactionSuccessful();  //设置事务成功完成
        } finally {
            db.endTransaction();    //结束事务
        }
    }
    public void addImage(List<Image> images){
        db.beginTransaction();  //开始事务
        try {
            for(Image image : images){
                db.execSQL("INSERT INTO Image VALUES(null, ?, ?)", new Object[]{image.imgId,image.img});
            }
            db.setTransactionSuccessful();  //设置事务成功完成
        } finally {
            db.endTransaction();    //结束事务
        }
    }
    public void deleteImage(String imageId,int pos) {
        //db.execSQL("DELETE FROM Image WHERE imgId=? ORDER BY _id LIMIT ?,1", new Object[]{imageId,pos});
        db.execSQL("DELETE FROM Image WHERE _id IN (SELECT _id FROM Image WHERE imgId=? ORDER BY _id LIMIT ?,1)", new Object[]{imageId,pos});
        //db.delete("Image", "imgId = ?", new String[]{imageId});
        //db.execSQL("DELETE FROM Image WHERE imgId = ?", new Object[]{imageId});
    }
    public void updateAskCaseText(String recount,int position,String textBody){
        db.execSQL("UPDATE CaseRecount SET historyRecount=? WHERE _id IN (SELECT _id FROM CaseRecount WHERE userAccount=? ORDER BY _id LIMIT ?,1)", new Object[]{textBody,recount,position});
    }
    public void addCaseRecount(CaseRecount caseRecount){
        db.beginTransaction();  //开始事务
        try {
            db.execSQL("INSERT INTO CaseRecount VALUES(null,?,?,?,?,?,?,?,?,?,?)", new Object[]{caseRecount.userAccount, caseRecount.date, caseRecount.imgRand, caseRecount.historyRecount, caseRecount.historyCurCase, caseRecount.historyPastCase, caseRecount.historySigns, caseRecount.assayRecount, caseRecount.imageRecount, caseRecount.medicationRecount});
            db.setTransactionSuccessful();  //设置事务成功完成
        } finally {
            db.endTransaction();    //结束事务
        }
    }


    public void updateAge(CaseRecount caseRecount) {
        ContentValues cv = new ContentValues();
        cv.put("historyRecount", caseRecount.historyRecount);
        db.update("UserInfo", cv, "userAccount = ?,date = ?,imgRand = ?", new String[]{caseRecount.userAccount,caseRecount.date,caseRecount.imgRand});
    }

    public List<UserInfo> query() {
        ArrayList<UserInfo> userInfos = new ArrayList<UserInfo>();
        Cursor c = queryUserInfoCursor();
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

    public Cursor queryUserInfoCursor() {
        Cursor c = db.rawQuery("SELECT * FROM UserInfo", null);
        return c;
    }

    public Cursor queryImageCursor(String imageId) {
        Cursor c = db.rawQuery("SELECT * FROM Image WHERE imgId = ?", new String[]{imageId});
        return c;
    }
    public Cursor queryAllImageCursor() {
        Cursor c = db.rawQuery("SELECT * FROM Image", null);
        return c;
    }
    public Cursor queryCaseRecountCursor(String userAccount) {
        //Cursor c = db.rawQuery("SELECT * FROM CaseRecount", null);
        Cursor c = db.rawQuery("SELECT * FROM CaseRecount WHERE userAccount=? ORDER BY date ASC", new String[]{userAccount});
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
