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

import com.lqr.wechat.model.CaseInfo;
import com.lqr.wechat.model.Image;
import com.lqr.wechat.model.Person;

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

    /**
     * add persons
     * @param person
     */
    public void addPerson(Person person) {
        db.beginTransaction();  //开始事务
        try {
            Log.d(TAG,"create person table ----------------------------->");
            db.execSQL("INSERT INTO person VALUES(null, ?, ?, ?,?,?,?,?,?)", new Object[]{person.account,person.name, person.sex, person.age,person.historyImgRand,person.arrayImgRand,person.imageImgRand,person.medicationImgRand});
            db.setTransactionSuccessful();  //设置事务成功完成
        } finally {
            db.endTransaction();    //结束事务
        }
    }
    public void addImage(Image image){
        db.beginTransaction();  //开始事务
        try {
            Log.d(TAG,"create image table ----------------------------->");
            db.execSQL("INSERT INTO image VALUES(null, ?, ?)", new Object[]{image.imgId,image.img});
            db.setTransactionSuccessful();  //设置事务成功完成
        } finally {
            db.endTransaction();    //结束事务
        }
    }
    public void addCaseInfo(CaseInfo caseInfo){
        db.beginTransaction();  //开始事务
        try {
            Log.d(TAG,"create image table ----------------------------->");
            db.execSQL("INSERT INTO caseInfo VALUES(?,?,?,?,?,?,?,?)", new Object[]{caseInfo.imgRand,caseInfo.historyRecount,caseInfo.historyCurCase,caseInfo.historyPastCase,caseInfo.historySigns,caseInfo.assayRecount,caseInfo.imageRecount,caseInfo.medicationRecount});
            db.setTransactionSuccessful();  //设置事务成功完成
        } finally {
            db.endTransaction();    //结束事务
        }
    }

    /**
     * update person's age
     * @param person
     */
    public void updateAge(Person person) {
        ContentValues cv = new ContentValues();
        cv.put("age", person.age);
        db.update("person", cv, "name = ?", new String[]{person.name});
    }

    /**
     * delete old person
     * @param person
     */
    public void deleteOldPerson(Person person) {
        db.delete("person", "age >= ?", new String[]{String.valueOf(person.age)});
    }

    /**
     * query all persons, return list
     * @return List<Person>
     */
    public List<Person> query() {
        ArrayList<Person> persons = new ArrayList<Person>();
        Cursor c = queryTheCursor();
        while (c.moveToNext()) {
            Person person = new Person();
            person._id = c.getInt(c.getColumnIndex("_account"));
            person.name = c.getString(c.getColumnIndex("name"));
            person.sex = c.getString(c.getColumnIndex("sex"));
            person.age = c.getInt(c.getColumnIndex("age"));

            persons.add(person);
        }
        c.close();
        return persons;
    }

    /**
     * query all persons, return cursor
     * @return  Cursor
     */
    public Cursor queryTheCursor() {
        Cursor c = db.rawQuery("SELECT * FROM person", null);
        return c;
    }

    /**
     * close database
     */
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
