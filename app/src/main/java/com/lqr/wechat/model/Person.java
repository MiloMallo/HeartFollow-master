package com.lqr.wechat.model;

import java.sql.Date;
import java.util.ArrayList;

/**
 * Created by wangbingbing on 2017/5/11.
 */

public class Person {
    public int _id;
    public String account;
    public String name;
    public String sex;
    public int age;

    public String historyImgRand;
    public String arrayImgRand;
    public String imageImgRand;
    public String medicationImgRand;
    public Person() {
    }

    public Person(String account,String name, String sex,int age,String historyImgRand,String arrayImgRand,String imageImgRand,String medicationImgRand) {
        this.account = account;
        this.name = name;
        this.sex = sex;
        this.age = age;
        this.historyImgRand = historyImgRand;
        this.arrayImgRand = arrayImgRand;
        this.imageImgRand = imageImgRand;
        this.medicationImgRand = medicationImgRand;
    }
    class followTemplate_str{
        Date time;
        String plan;
    }
}
