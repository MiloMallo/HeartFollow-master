package com.lqr.wechat.model;

import java.sql.Date;
import java.util.ArrayList;

/**
 * Created by wangbingbing on 2017/5/11.
 */

public class UserInfo {
    public int _id;
    public String account;
    public String userName;
    public String sex;
    public int age;

    public UserInfo() {
    }

    public UserInfo(String account, String userName, String sex, int age) {
        this.account = account;
        this.userName = userName;
        this.sex = sex;
        this.age = age;
    }
}
