/*
 * Copyright (c) 2016, ZCJ All Rights Reserved.
 * Project Name: zcj_android-V0.21
 */
package com.zzti.fengyongge.imagepicker.model;
import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by fengyongge on 2016/5/24
 */
public class PhotoModel implements Serializable {

	private static final long serialVersionUID = 1L;
	private String originalPath;
	private boolean isChecked;
	private Bitmap bitmap;
	public PhotoModel(String originalPath, boolean isChecked) {
		super();
		this.originalPath = originalPath;
		this.isChecked = isChecked;
	}

	public PhotoModel(String originalPath) {
		this.originalPath = originalPath;
	}

	public PhotoModel() {
	}
	public void setBitmap(Bitmap bitmap){
		this.bitmap = bitmap;
	}
	public Bitmap getBitmap(){
		return bitmap;
	}
	public String getOriginalPath() {
		return originalPath;
	}

	public void setOriginalPath(String originalPath) {
		this.originalPath = originalPath;
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

}

