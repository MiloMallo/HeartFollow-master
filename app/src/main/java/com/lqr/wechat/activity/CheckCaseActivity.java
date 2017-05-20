package com.lqr.wechat.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.lqr.wechat.R;
import com.lqr.wechat.model.Contact;
import com.lqr.wechat.model.UploadGoodsBean;
import com.lqr.wechat.utils.Config;
import com.lqr.wechat.utils.DbTOPxUtil;
import com.lqr.wechat.view.MyGridView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.zzti.fengyongge.imagepicker.PhotoPreviewActivity;
import com.zzti.fengyongge.imagepicker.PhotoSelectorActivity;
import com.zzti.fengyongge.imagepicker.model.PhotoModel;
import com.zzti.fengyongge.imagepicker.util.CommonUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017-04-08.
 */

public class CheckCaseActivity extends BaseActivity {

    public static final int REQ_CHANGE_CHECK_CASE = 200;

    private RecyclerView mRecyclerView = null;
    private CheckCaseAdapter mCheckCaseAdapter = null;
    private Contact mContact;
    private ArrayList<ListItem> mDataList = null;
    @Override
    public void init() {
        mContact = (Contact) getIntent().getSerializableExtra("contact");
        if (mContact == null) {
            interrupt();
            return;
        }
        //初始化数据mDataList
    }
    private static class ListItem {
        public String title;
        public Class<?> activity;
    }
    @Override
    public void initView() {
        setContentView(R.layout.activity_check_case);

        mRecyclerView = (RecyclerView) findViewById(R.id.check_case_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mCheckCaseAdapter = new CheckCaseAdapter(this);
        mCheckCaseAdapter.setData(mDataList);
        mRecyclerView.setAdapter(mCheckCaseAdapter);
        /*DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisc(true).build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getApplicationContext()).defaultDisplayImageOptions(
                defaultOptions).build();
        ImageLoader.getInstance().init(config);
        Config.ScreenMap = Config.getScreenSize(this, this);
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        screen_widthOffset = (display.getWidth() - (3* DbTOPxUtil.dip2px(this, 2)))/4;
        inflater = LayoutInflater.from(this);
        my_imgs_GV = (MyGridView) findViewById(R.id.my_goods_history);
        gridImgsAdapter = new GridImgAdapter();
        my_imgs_GV.setAdapter(gridImgsAdapter);
        img_uri.add(null);
        gridImgsAdapter.notifyDataSetChanged();*/
    }
    private class CheckCaseAdapter extends RecyclerView.Adapter {

        private LayoutInflater mLayoutInflater;
        private ArrayList<ListItem> mDataList = new ArrayList<>();

        public CheckCaseAdapter(Context context) {
            mLayoutInflater = LayoutInflater.from(context);
        }

        public void setData(ArrayList<ListItem> list) {
            this.mDataList = list;
            notifyDataSetChanged();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(mLayoutInflater.inflate(R.layout.activity_check_case_item, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            ListItem listItem = mDataList.get(position);

            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.textView.setText(listItem.title);
        }

        @Override
        public int getItemCount() {
            return mDataList.size();
        }

        private class ViewHolder extends RecyclerView.ViewHolder {

            private TextView textView;

            public ViewHolder(View itemView) {
                super(itemView);
                textView = (TextView) itemView.findViewById(R.id.check_case_time);

                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                       /* ListItem listItem = mDataList.get(RecyclerViewUtils.getAdapterPosition(mRecyclerView, ViewHolder.this));
                        startActivity(new Intent(CheckCaseActivity.this, listItem.activity));*/
                    }
                });
            }
        }
    }
}
