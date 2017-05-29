package com.lqr.wechat.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.lqr.wechat.DBManager;
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

    private DBManager mgr;

    private int imgArrayCnt = 0;

    private ImageView add_IB;
    private LayoutInflater inflater;
    private int screen_widthOffset;
    private List<PhotoModel> single_photos = new ArrayList<PhotoModel>();

    GridImgHistoryAdapter gridImgsAdapter4history;
    private ArrayList<ArrayList<UploadGoodsBean>> img_uriArray = new ArrayList<ArrayList<UploadGoodsBean>>();
    @Override
    public void init() {
        mContact = (Contact) getIntent().getSerializableExtra("contact");
        if (mContact == null) {
            interrupt();
            return;
        }
        //初始化数据mDataList
        mDataList = new ArrayList<ListItem>();
        //初始化DBManager
        mgr = new DBManager(CheckCaseActivity.this);
        Cursor cursor = mgr.queryCaseRecountCursor(mContact.getAccount());
        while(cursor.moveToNext()){
            ListItem listItem = new ListItem();
            listItem.date = cursor.getString(cursor.getColumnIndex("date"));
            for(int i=1;i<=4;i++){
                String imageId = mContact.getAccount()+i+listItem.date+cursor.getString(cursor.getColumnIndex("imgRand"));
                Cursor imgCursor = mgr.queryImageCursor(imageId);
                if(imgCursor.getCount()!=0 && i==1){
                    imgCursor.moveToLast();
                    Bitmap bmp = cursorToBmp(imgCursor, imgCursor.getColumnIndex("img"));
                    listItem.historyListImgs.add(bmp);
                }
            }
            mDataList.add(listItem);
        }
        cursor.close();
    }
    // Cursor to bitmap
    public Bitmap cursorToBmp(Cursor c, int columnIndex) {
        byte[] data = c.getBlob(columnIndex);
        try {
            return BitmapFactory.decodeByteArray(data, 0, data.length);
        } catch (Exception e) {
            return null;
        }
    }
    private static class ListItem {
        public String date = new String();
        public ArrayList<Bitmap> historyListImgs = new ArrayList<Bitmap>();
        public ArrayList<Bitmap> arrayListImgs  = new ArrayList<Bitmap>();;
        public String AskedTitle;
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



        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
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
        public Context context;//上下文
        //private ArrayList<UploadGoodsBean> img_uri4history = new ArrayList<UploadGoodsBean>();
        public CheckCaseAdapter(Context context) {
            this.context = context;
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

            viewHolder.tv_date.setText(listItem.date);


            ArrayList<UploadGoodsBean> img_uriItem = new ArrayList<UploadGoodsBean>();
            img_uriItem.add(null);
            img_uriArray.add(img_uriItem);

            gridImgsAdapter4history = new GridImgHistoryAdapter(context,mDataList.get(0).historyListImgs);
            viewHolder.check_imgs_history.setAdapter(gridImgsAdapter4history);

            //imgArrayCnt++;
            //gridImgsAdapter4history.notifyDataSetChanged();

            /*for(Bitmap image : listItem.historyListImgs){
                viewHolder.iv_history.setImageBitmap(image);
            }*/
        }

        @Override
        public int getItemCount() {
            return mDataList.size();
        }
        private class ViewHolder extends RecyclerView.ViewHolder {

            private TextView tv_date;
            //private ImageView iv_history;
            private MyGridView check_imgs_history;

            public ViewHolder(View itemView) {
                super(itemView);
                tv_date = (TextView) itemView.findViewById(R.id.check_case_date);
                //iv_history = (ImageView) itemView.findViewById(R.id.add_images);
                check_imgs_history = (MyGridView) itemView.findViewById(R.id.check_images_history);

                tv_date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                       /* ListItem listItem = mDataList.get(RecyclerViewUtils.getAdapterPosition(mRecyclerView, ViewHolder.this));
                        startActivity(new Intent(CheckCaseActivity.this, listItem.activity));*/
                    }
                });
            }
        }
    }

    class GridImgHistoryAdapter extends BaseAdapter implements ListAdapter {
        public Context context;//上下文
        public LayoutInflater mInflater;
        private int imgCnt;
        @Override
        public int getCount() {
            return img_uriArray.get(imgCnt).size();
        }
        @Override
        public Object getItem(int position) {
            return null;
        }

        public GridImgHistoryAdapter(Context context, int cnt) {
            this.mInflater = LayoutInflater.from(context);
            imgCnt=cnt;
        }
        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = inflater.inflate(R.layout.activity_addstory_img_item, null);
            add_IB = (ImageView) convertView.findViewById(R.id.add_IB);
            ImageView delete_IV = (ImageView) convertView.findViewById(R.id.delete_IV);
            AbsListView.LayoutParams param = new AbsListView.LayoutParams(screen_widthOffset, screen_widthOffset);
            convertView.setLayoutParams(param);
            if (img_uriArray.get(imgCnt).get(position) == null) {
                delete_IV.setVisibility(View.GONE);
                ImageLoader.getInstance().displayImage("drawable://" + R.drawable.iv_add_the_pic, add_IB);
                add_IB.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        Intent intent = new Intent(CheckCaseActivity.this, PhotoSelectorActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        intent.putExtra("limit", 9 - (img_uriArray.get(imgCnt).size() - 1));
                        intent.putExtra("channel",1);
                        startActivityForResult(intent, 0);
                    }
                });

            } else {
                ImageLoader.getInstance().displayImage("file://" + img_uriArray.get(imgCnt).get(position).getUrl(), add_IB);
                delete_IV.setOnClickListener(new View.OnClickListener() {
                    private boolean is_addNull;
                    @Override
                    public void onClick(View arg0) {
                        is_addNull = true;
                        String img_url = img_uriArray.get(imgCnt).remove(position).getUrl();
                        for (int i = 0; i < img_uriArray.get(imgCnt).size(); i++) {
                            if (img_uriArray.get(imgCnt).get(i) == null) {
                                is_addNull = false;
                                continue;
                            }
                        }
                        if (is_addNull) {
                            img_uriArray.get(imgCnt).add(null);
                        }
//						FileUtils.DeleteFolder(img_url);
                        gridImgsAdapter4history.notifyDataSetChanged();
                    }
                });

                add_IB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("photos",(Serializable)single_photos);
                        bundle.putInt("position", position);
                        bundle.putString("save","save");
                        CommonUtils.launchActivity(CheckCaseActivity.this, PhotoPreviewActivity.class, bundle);
                    }
                });
            }
            return convertView;
        }
        class ViewHolder {
            ImageView add_IB;
            ImageView delete_IV;
        }
    }

    private ArrayList<UploadGoodsBean> getImgUrl(int channel){
            return img_uriArray.get(imgArrayCnt);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                if (data != null) {
                    List<String> paths = (List<String>) data.getExtras().getSerializable("photos");
                    //int local_Channel = (int) data.getExtras().getSerializable("channel");
                    int local_Channel = data.getIntExtra("channel", 0);
                    if (getImgUrl(local_Channel).size() > 0) {

                        getImgUrl(local_Channel).remove(getImgUrl(local_Channel).size() - 1);
                    }

                    for (int i = 0; i < paths.size(); i++) {
                        getImgUrl(local_Channel).add(new UploadGoodsBean(paths.get(i), false));
                        //上传参数
                    }
                    for (int i = 0; i < paths.size(); i++) {
                        PhotoModel photoModel = new PhotoModel();
                        photoModel.setOriginalPath(paths.get(i));
                        photoModel.setChecked(true);
                        single_photos.add(photoModel);
                    }
                    if (getImgUrl(local_Channel).size() < 9) {
                        getImgUrl(local_Channel).add(null);
                    }
                    gridImgsAdapter4history.notifyDataSetChanged();
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        imgArrayCnt = 0;
    }
}
