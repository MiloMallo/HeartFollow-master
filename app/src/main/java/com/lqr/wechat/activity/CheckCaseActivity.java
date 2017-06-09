package com.lqr.wechat.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.lqr.wechat.model.Image;
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

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
    private ArrayList<String> mImageId = null;

    private DBManager mgr;

    private Intent mIntent;

    private int imgArrayCnt = 0;
    private int g_position = 0;

    private ImageView add_IB;
    private LayoutInflater inflater;
    private int screen_widthOffset;
    private List<PhotoModel> single_photos = new ArrayList<PhotoModel>();

    public volatile static Bitmap g_previewImage;
    GridImgHistoryAdapter gridImgsAdapter4history;
    GridImgAssayAdapter gridImgsAdapter4assay;
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
        mImageId  = new ArrayList<>();
        //初始化DBManager
        mgr = new DBManager(CheckCaseActivity.this);
        Cursor cursor = mgr.queryCaseRecountCursor(mContact.getAccount());
        while(cursor.moveToNext()){
            ListItem listItem = new ListItem();
            listItem.date = cursor.getString(cursor.getColumnIndex("date"));
            String imageId = null;
            for(int i=1;i<=4;i++){
                imageId = i+mContact.getAccount()+listItem.date+cursor.getString(cursor.getColumnIndex("imgRand"));
                Cursor imgCursor = mgr.queryImageCursor(imageId);
                while(imgCursor.moveToNext()){
                    Bitmap bmp = cursorToBmp(imgCursor, imgCursor.getColumnIndex("img"));
                    if(i==1){listItem.historyListImgs.add(bmp);}
                    if(i==2){listItem.assayListImgs.add(bmp);}
                    //if(i==1){listItem.historyListImgs.add(bmp);}
                    //if(i==1){listItem.historyListImgs.add(bmp);}
                }
            }
            mDataList.add(listItem);
            if(imageId == null){
                mImageId.add("#"+mContact.getAccount()+listItem.date);
            }else{
                mImageId.add(imageId);
            }
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
        public ArrayList<Bitmap> assayListImgs  = new ArrayList<Bitmap>();
        public String historyRecount;
        public String AskedTitle;
        public Class<?> activity;
    }
    @Override
    public void initView() {
        setContentView(R.layout.activity_check_case);


        mRecyclerView = (RecyclerView) findViewById(R.id.check_case_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mCheckCaseAdapter = new CheckCaseAdapter(this,new MyItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                g_position = position;
            }
        });
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
        private MyItemClickListener mItemClickListener;
        private int l_position;
        //private ArrayList<UploadGoodsBean> img_uri4history = new ArrayList<UploadGoodsBean>();
        public CheckCaseAdapter(Context context,MyItemClickListener clickListener) {
            this.mLayoutInflater = LayoutInflater.from(context);
            this.mItemClickListener = clickListener;
        }

        public void setData(ArrayList<ListItem> list) {
            this.mDataList = list;
            notifyDataSetChanged();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //return new ViewHolder(mLayoutInflater.inflate(R.layout.activity_check_case_item, parent, false));
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_check_case_item, parent,false);
            ViewHolder vh = new ViewHolder(itemView,mItemClickListener);
            return vh;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            ListItem listItem = mDataList.get(position);

            ViewHolder viewHolder = (ViewHolder) holder;

            viewHolder.tv_date.setText(listItem.date);

            l_position = position;


            /*ArrayList<UploadGoodsBean> img_uriItem = new ArrayList<UploadGoodsBean>();
            img_uriItem.add(null);
            img_uriArray.add(img_uriItem);*/


            gridImgsAdapter4history = new GridImgHistoryAdapter(listItem.historyListImgs,position);
            viewHolder.check_imgs_history.setClickable(false);
            viewHolder.check_imgs_history.setPressed(false);
            viewHolder.check_imgs_history.setEnabled(false);
            viewHolder.check_imgs_history.setTag(position);
            viewHolder.check_imgs_history.setAdapter(gridImgsAdapter4history);

            gridImgsAdapter4assay = new GridImgAssayAdapter(listItem.assayListImgs,position);
            viewHolder.check_imgs_assay.setClickable(false);
            viewHolder.check_imgs_assay.setPressed(false);
            viewHolder.check_imgs_assay.setEnabled(false);
            viewHolder.check_imgs_assay.setTag(position);
            viewHolder.check_imgs_assay.setAdapter(gridImgsAdapter4assay);

            /*View convertView = inflater.inflate(R.layout.activity_addstory_img_item, null);
            add_IB = (ImageView) convertView.findViewById(R.id.add_IB);
            ImageView delete_IV = (ImageView) convertView.findViewById(R.id.delete_IV);
            AbsListView.LayoutParams param = new AbsListView.LayoutParams(screen_widthOffset, screen_widthOffset);
            convertView.setLayoutParams(param);
            add_IB.setImageBitmap(listItem.historyListImgs.get(0));*/
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
        private class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            private TextView tv_date;
            private TextView tv_askedInfo;
            private MyItemClickListener mListener;
            private MyGridView check_imgs_history;
            private MyGridView check_imgs_assay;

            public ViewHolder(View itemView,MyItemClickListener listener) {
                super(itemView);
                itemView.setOnClickListener(this);
                this.mListener = listener;

                check_imgs_history = (MyGridView) itemView.findViewById(R.id.check_images_history);
                check_imgs_assay = (MyGridView) itemView.findViewById(R.id.check_images_assay);


                tv_date = (TextView) itemView.findViewById(R.id.check_case_date);
                tv_askedInfo = (TextView) itemView.findViewById(R.id.check_case_askedInfo);
                tv_askedInfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mIntent = new Intent(CheckCaseActivity.this, EditCheckCase.class);
                        String textBody = mDataList.get(l_position).historyRecount;
                        mIntent.putExtra("textBody", textBody);
                        startActivityForResult(mIntent, EditCheckCase.REQ_CHANGE_EDIT_TEXT);
                       /* ListItem listItem = mDataList.get(RecyclerViewUtils.getAdapterPosition(mRecyclerView, ViewHolder.this));
                        startActivity(new Intent(CheckCaseActivity.this, listItem.activity));*/
                    }
                });
            }
            @Override
            public void onClick(View v) {
                if(mListener != null){
                    mListener.onItemClick(v,getLayoutPosition());
                }
            }
        }
    }
    public interface MyItemClickListener {
        public void onItemClick(View view,int postion);
    }
//////////////-----------------------------------------------------------------------------------------------
    class GridImgHistoryAdapter extends BaseAdapter implements ListAdapter {
        private ArrayList<Bitmap> imgArray = new ArrayList<Bitmap>();
        private int l_position=0;
        @Override
        public int getCount() {
            return imgArray.size();
        }
        GridImgHistoryAdapter(ArrayList<Bitmap> imgArray,int position){
            this.imgArray = imgArray;
            if(this.imgArray.isEmpty() || this.imgArray.get(this.imgArray.size()-1)!=null){
                this.imgArray.add(null);
            }
            this.l_position = position;
/*            for(Bitmap image : imgArray){
                add_IB.setImageBitmap(imgArray.get(0));
            }*/
        }
        @Override
        public Object getItem(int position) {
            return null;
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
            if (imgArray.get(position) == null) {
                delete_IV.setVisibility(View.GONE);
                ImageLoader.getInstance().displayImage("drawable://" + R.drawable.iv_add_the_pic, add_IB);
                //添加
                add_IB.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        int pos = (int)((ViewGroup) arg0.getParent()).getTag();
                        Intent intent = new Intent(CheckCaseActivity.this, PhotoSelectorActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        intent.putExtra("limit", 9 - (mDataList.get(pos).historyListImgs.size() - 1));
                        intent.putExtra("item",pos);
                        intent.putExtra("channel",1);
                        startActivityForResult(intent, 0);
                    }
                });

            } else {
                add_IB.setImageBitmap(imgArray.get(position));//ImageLoader.getInstance().displayImage("file://" + img_uriArray.get(imgArrayCnt).get(position).getUrl(), add_IB);
                //删除
                delete_IV.setOnClickListener(new View.OnClickListener() {
                    private boolean is_addNull;
                    @Override
                    public void onClick(View arg0) {
                        is_addNull = true;
                        //imgArray.remove(position);
                        //int pos = (int)arg0.getTag();
                        int pos = (int)((ViewGroup) arg0.getParent()).getTag();
                        mDataList.get(pos).historyListImgs.remove(position);
                        String imgId = "1" +mImageId.get(pos).substring(1);
                        mgr.deleteImage(imgId,position);
                        for (int i = 0; i < mDataList.get(pos).historyListImgs.size(); i++) {
                            if (mDataList.get(pos).historyListImgs.get(i) == null) {
                                is_addNull = false;
                                continue;
                            }
                        }
                        if (is_addNull) {
                            mDataList.get(pos).historyListImgs.add(null);
                        }
//						FileUtils.DeleteFolder(img_url);
                        //gridImgsAdapter4history.notifyDataSetChanged();
                        mCheckCaseAdapter.notifyDataSetChanged();
                    }
                });
                //预览
                add_IB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = (int)((ViewGroup) v.getParent()).getTag();
                        Bundle bundle = new Bundle();
                        PhotoModel photoModel = new PhotoModel();
                        //saveBitmapFile(mDataList.get(pos).historyListImgs.get(position));
                        //Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), mDataList.get(pos).historyListImgs.get(position), null,null));
                        g_previewImage = mDataList.get(pos).historyListImgs.get(position);
                        startPreviewActivity();
                        /*photoModel.setOriginalPath(null);
                        photoModel.setChecked(true);
                        if(single_photos.size()==0) {
                            single_photos.add(photoModel);
                        }
                        bundle.putSerializable("photos",(Serializable)single_photos);
                        bundle.putInt("position", position);
                        bundle.putString("save","save");
                        CommonUtils.launchActivity(CheckCaseActivity.this, PhotoPreviewActivity.class, bundle);*/
                    }
                });
            }
            convertView.setTag(l_position);
            return convertView;
        }
        class ViewHolder {
            ImageView add_IB;
            ImageView delete_IV;
        }
    }
//--------------------assay-------------------------------------------------------
    class GridImgAssayAdapter extends BaseAdapter implements ListAdapter {
        private ArrayList<Bitmap> imgArray = new ArrayList<Bitmap>();
        private int l_position=0;
        @Override
        public int getCount() {
            return imgArray.size();
        }
        GridImgAssayAdapter(ArrayList<Bitmap> imgArray,int position){
            this.imgArray = imgArray;
            if(this.imgArray.isEmpty() || this.imgArray.get(this.imgArray.size()-1)!=null){
                this.imgArray.add(null);
            }
            this.l_position = position;
/*            for(Bitmap image : imgArray){
                add_IB.setImageBitmap(imgArray.get(0));
            }*/
        }
        @Override
        public Object getItem(int position) {
            return null;
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
            if (imgArray.get(position) == null) {
                delete_IV.setVisibility(View.GONE);
                ImageLoader.getInstance().displayImage("drawable://" + R.drawable.iv_add_the_pic, add_IB);
                //添加
                add_IB.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        int pos = (int)((ViewGroup) arg0.getParent()).getTag();
                        Intent intent = new Intent(CheckCaseActivity.this, PhotoSelectorActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        intent.putExtra("limit", 9 - (mDataList.get(pos).assayListImgs.size() - 1));
                        intent.putExtra("item",pos);
                        intent.putExtra("channel",2);
                        startActivityForResult(intent, 0);
                    }
                });

            } else {
                add_IB.setImageBitmap(imgArray.get(position));//ImageLoader.getInstance().displayImage("file://" + img_uriArray.get(imgArrayCnt).get(position).getUrl(), add_IB);
                //删除
                delete_IV.setOnClickListener(new View.OnClickListener() {
                    private boolean is_addNull;
                    @Override
                    public void onClick(View arg0) {
                        is_addNull = true;
                        //imgArray.remove(position);
                        //int pos = (int)arg0.getTag();
                        int pos = (int)((ViewGroup) arg0.getParent()).getTag();
                        mDataList.get(pos).assayListImgs.remove(position);
                        String imgId = "2" +mImageId.get(pos).substring(1);
                        mgr.deleteImage(imgId,position);
                        for (int i = 0; i < mDataList.get(pos).assayListImgs.size(); i++) {
                            if (mDataList.get(pos).assayListImgs.get(i) == null) {
                                is_addNull = false;
                                continue;
                            }
                        }
                        if (is_addNull) {
                            mDataList.get(pos).assayListImgs.add(null);
                        }
//						FileUtils.DeleteFolder(img_url);
                        //gridImgsAdapter4history.notifyDataSetChanged();
                        mCheckCaseAdapter.notifyDataSetChanged();
                    }
                });
                //预览
                add_IB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = (int)((ViewGroup) v.getParent()).getTag();
                        Bundle bundle = new Bundle();
                        PhotoModel photoModel = new PhotoModel();
                        //saveBitmapFile(mDataList.get(pos).historyListImgs.get(position));
                        //Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), mDataList.get(pos).historyListImgs.get(position), null,null));
                        g_previewImage = mDataList.get(pos).historyListImgs.get(position);
                        startPreviewActivity();
                        /*photoModel.setOriginalPath(saveBitmapFile(mDataList.get(pos).assayListImgs.get(position)));
                        photoModel.setChecked(true);
                        if(single_photos.size()==0) {
                            single_photos.add(photoModel);
                        }
                        bundle.putSerializable("photos",(Serializable)single_photos);
                        bundle.putInt("position", position);
                        bundle.putString("save","save");
                        CommonUtils.launchActivity(CheckCaseActivity.this, PhotoPreviewActivity.class, bundle);*/
                    }
                });
            }
            convertView.setTag(l_position);
            return convertView;
        }
        class ViewHolder {
            ImageView add_IB;
            ImageView delete_IV;
        }
    }

    private ArrayList<Bitmap> getImgItem(int channel,int item){
            if(channel == 1){return mDataList.get(item).historyListImgs;}
            else if(channel == 2){return mDataList.get(item).assayListImgs;}
            return null;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                if (data != null) {
                    List<String> paths = (List<String>) data.getExtras().getSerializable("photos");
                    //int local_Channel = (int) data.getExtras().getSerializable("channel");
                    int local_Channel = data.getIntExtra("channel", 0);
                    int local_item = data.getIntExtra("item", 0);
                    if (getImgItem(local_Channel,local_item).size() > 0) {
                        getImgItem(local_Channel,local_item).remove(getImgItem(local_Channel,local_item).size() - 1);
                    }
                    List<Image> loadImage = new ArrayList<>();
                    for (int i = 0; i < paths.size(); i++) {
                        getImgItem(local_Channel,local_item).add(BitmapFactory.decodeFile(paths.get(i)));
                        ByteArrayOutputStream os = new ByteArrayOutputStream();
                        String imgId;
                        if(mImageId.get(local_item).charAt(0)=='#'){
                            imgId = "" + local_Channel + mImageId.get(local_item).substring(1)+mgr.getRandomString(4);
                        }else {
                            imgId = "" + local_Channel + mImageId.get(local_item).substring(1);
                        }
                        BitmapFactory.decodeFile(paths.get(i)).compress(Bitmap.CompressFormat.JPEG, 100, os);
                        Image image = new Image(imgId,os.toByteArray());
                        loadImage.add(image);
                    }
                    mgr.addImage(loadImage);
                    getImgItem(local_Channel,local_item).add(null);
                    mCheckCaseAdapter.notifyDataSetChanged();
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void startPreviewActivity(){
        mIntent = new Intent(CheckCaseActivity.this, PreviewActivity.class);
        startActivityForResult(mIntent, PreviewActivity.REQ_CHANGE_PREVIEW);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        imgArrayCnt = 0;
    }
    public String saveBitmapFile(Bitmap bitmap){
        File file=new File("/storage/sdcard/01.jpg");//将要保存图片的路径
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file.getPath();
    }
}


