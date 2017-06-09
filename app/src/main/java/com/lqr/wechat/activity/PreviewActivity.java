package com.lqr.wechat.activity;

import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.ImageView;

import com.lqr.wechat.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by wangbingbing on 2017/6/8.
 */

public class PreviewActivity extends BaseActivity{
    public static final int  REQ_CHANGE_PREVIEW = 201;
    @InjectView(R.id.toolbar)
    Toolbar mToolbar;
    @Override
    public void init() {
    }
    @Override
    public void initView() {
        setContentView(R.layout.activity_checkcase_preview);
        ButterKnife.inject(this);
        initToolbar();
        //initAnimation();
        ((ImageView)findViewById(R.id.iv_content_preview)).setImageBitmap(CheckCaseActivity.g_previewImage);;
    }
    private void initToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("浏览图片");
        mToolbar.setNavigationIcon(R.mipmap.ic_back);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
