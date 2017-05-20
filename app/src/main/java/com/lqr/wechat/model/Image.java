package com.lqr.wechat.model;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017-05-14.
 */

public class Image {
    public int _id;
    public String imgId;
    public byte[] img;

    public Image() {
    }

    public Image(String imgId, byte[] img) {
        this.imgId = imgId;
        this.img = img;
    }
}
