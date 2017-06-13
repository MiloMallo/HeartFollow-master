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
    GridImgImageAdapter gridImgsAdapter4image;
    GridImgMedicationAdapter gridImgsAdapter4medication;
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
                    if(i==3){listItem.imageListImgs.add(bmp);}
                    if(i==4){listItem.medicationListImgs.add(bmp);}
                }
            }
            listItem.historyRecount=cursor.getString(cursor.getColumnIndex("historyRecount"));
            listItem.historyCurCase=cursor.getString(cursor.getColumnIndex("historyCurCase"));
            listItem.historyPastCase=cursor.getString(cursor.getColumnIndex("historyPastCase"));
            listItem.historySigns=cursor.getString(cursor.getColumnIndex("historySigns"));

            listItem.assayRecount=cursor.getString(cursor.getColumnIndex("assayRecount"));
            listItem.imageRecount=cursor.getString(cursor.getColumnIndex("imageRecount"));
            listItem.medicationRecount=cursor.getString(cursor.getColumnIndex("medicationRecount"));

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
        public ArrayList<Bitmap> imageListImgs  = new ArrayList<Bitmap>();
        public ArrayList<Bitmap> medicationListImgs  = new ArrayList<Bitmap>();

        public String historyRecount;
        public String historyCurCase;
        public String historyPastCase;
        public String historySigns;

        public String assayRecount;
        public String imageRecount;
        public String medicationRecount;

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

    }
    private class CheckCaseAdapter extends RecyclerView.Adapter {
        private LayoutInflater mLayoutInflater;
        private ArrayList<ListItem> mDataList = new ArrayList<>();
        private MyItemClickListener mItemClickListener;
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

            String info= mDataList.get(position).historyRecount;
            int len = 0;
            if(info!=null&&info.length()!=0){
                len = Math.min(info.length(),12);
                viewHolder.tv_askedInfo.setText(mDataList.get(position).historyRecount.substring(0,len)+"...");
            }else{
                viewHolder.tv_askedInfo.setText("无");
            }
            info= mDataList.get(position).historyCurCase;
            if(info!=null&&info.length()!=0){
                len = Math.min(info.length(),12);
                viewHolder.tv_historyCurCaseInfo.setText(mDataList.get(position).historyCurCase.substring(0,len)+"...");
            }else{
                viewHolder.tv_historyCurCaseInfo.setText("无");
            }
            info= mDataList.get(position).historyPastCase;
            if(info!=null&&info.length()!=0){
                len = Math.min(info.length(),12);
                viewHolder.tv_historyPastCaseInfo.setText(mDataList.get(position).historyPastCase.substring(0,len)+"...");
            }else{
                viewHolder.tv_historyPastCaseInfo.setText("无");
            }
            info= mDataList.get(position).historySigns;
            if(info!=null&&info.length()!=0){
                len = Math.min(info.length(),12);
                viewHolder.tv_historySignsInfo.setText(mDataList.get(position).historySigns.substring(0,len)+"...");
            }else{
                viewHolder.tv_historySignsInfo.setText("无");
            }
            info= mDataList.get(position).assayRecount;
            if(info!=null&&info.length()!=0){
                len = Math.min(info.length(),12);
                viewHolder.tv_assayRecountInfo.setText(mDataList.get(position).assayRecount.substring(0,len)+"...");
            }else{
                viewHolder.tv_assayRecountInfo.setText("无");
            }
            info= mDataList.get(position).imageRecount;
            if(info!=null&&info.length()!=0){
                len = Math.min(info.length(),12);
                viewHolder.tv_imageRecountInfo.setText(mDataList.get(position).imageRecount.substring(0,len)+"...");
            }else{
                viewHolder.tv_imageRecountInfo.setText("无");
            }
            info= mDataList.get(position).medicationRecount;
            if(info!=null&&info.length()!=0){
                len = Math.min(info.length(),12);
                viewHolder.tv_medicationRecountInfo.setText(mDataList.get(position).medicationRecount.substring(0,len)+"...");
            }else{
                viewHolder.tv_medicationRecountInfo.setText("无");
            }

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

            gridImgsAdapter4image = new GridImgImageAdapter(listItem.imageListImgs,position);
            viewHolder.check_imgs_image.setClickable(false);
            viewHolder.check_imgs_image.setPressed(false);
            viewHolder.check_imgs_image.setEnabled(false);
            viewHolder.check_imgs_image.setTag(position);
            viewHolder.check_imgs_image.setAdapter(gridImgsAdapter4image);

            gridImgsAdapter4medication = new GridImgMedicationAdapter(listItem.medicationListImgs,position);
            viewHolder.check_imgs_medication.setClickable(false);
            viewHolder.check_imgs_medication.setPressed(false);
            viewHolder.check_imgs_medication.setEnabled(false);
            viewHolder.check_imgs_medication.setTag(position);
            viewHolder.check_imgs_medication.setAdapter(gridImgsAdapter4medication);

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
            private MyGridView check_imgs_image;
            private MyGridView check_imgs_medication;

            private TextView tv_historyCurCaseInfo;
            private TextView tv_historyPastCaseInfo;
            private TextView tv_historySignsInfo;

            private TextView tv_assayRecountInfo;
            private TextView tv_imageRecountInfo;
            private TextView tv_medicationRecountInfo;

            public ViewHolder(View itemView,MyItemClickListener listener) {
                super(itemView);
                itemView.setOnClickListener(this);
                this.mListener = listener;

                check_imgs_history = (MyGridView) itemView.findViewById(R.id.check_images_history);
                check_imgs_assay = (MyGridView) itemView.findViewById(R.id.check_images_assay);
                check_imgs_image = (MyGridView) itemView.findViewById(R.id.check_images_image);
                check_imgs_medication = (MyGridView) itemView.findViewById(R.id.check_images_medication);


                tv_date = (TextView) itemView.findViewById(R.id.check_case_date);
                tv_askedInfo = (TextView) itemView.findViewById(R.id.check_case_askedInfo);
                tv_askedInfo.setTag(g_position);
                tv_askedInfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mIntent = new Intent(CheckCaseActivity.this, EditCheckCase.class);
                        String textBody = mDataList.get((int)v.getTag()).historyRecount;
                        mIntent.putExtra("textType",1 );
                        mIntent.putExtra("textBody", textBody);
                        mIntent.putExtra("position", (int)v.getTag());
                        startActivityForResult(mIntent, EditCheckCase.REQ_CHANGE_EDIT_TEXT);
                    }
                });

                tv_historyCurCaseInfo = (TextView) itemView.findViewById(R.id.check_case_historyCurCaseInfo);
                tv_historyCurCaseInfo.setTag(g_position);
                tv_historyCurCaseInfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mIntent = new Intent(CheckCaseActivity.this, EditCheckCase.class);
                        String textBody = mDataList.get((int)v.getTag()).historyCurCase;
                        mIntent.putExtra("textType",2 );
                        mIntent.putExtra("textBody", textBody);
                        mIntent.putExtra("position", (int)v.getTag());
                        startActivityForResult(mIntent, EditCheckCase.REQ_CHANGE_EDIT_TEXT);
                    }
                });
                tv_historyPastCaseInfo = (TextView) itemView.findViewById(R.id.check_case_historyPastCaseInfo);
                tv_historyPastCaseInfo.setTag(g_position);
                tv_historyPastCaseInfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mIntent = new Intent(CheckCaseActivity.this, EditCheckCase.class);
                        String textBody = mDataList.get((int)v.getTag()).historyPastCase;
                        mIntent.putExtra("textType",3);
                        mIntent.putExtra("textBody", textBody);
                        mIntent.putExtra("position", (int)v.getTag());
                        startActivityForResult(mIntent, EditCheckCase.REQ_CHANGE_EDIT_TEXT);
                    }
                });
                tv_historySignsInfo = (TextView) itemView.findViewById(R.id.check_case_historySignsInfo);
                tv_historySignsInfo.setTag(g_position);
                tv_historySignsInfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mIntent = new Intent(CheckCaseActivity.this, EditCheckCase.class);
                        String textBody = mDataList.get((int)v.getTag()).historySigns;
                        mIntent.putExtra("textType",4);
                        mIntent.putExtra("textBody", textBody);
                        mIntent.putExtra("position", (int)v.getTag());
                        startActivityForResult(mIntent, EditCheckCase.REQ_CHANGE_EDIT_TEXT);
                    }
                });

                tv_assayRecountInfo = (TextView) itemView.findViewById(R.id.check_case_askedInfo_assay);
                tv_assayRecountInfo.setTag(g_position);
                tv_assayRecountInfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mIntent = new Intent(CheckCaseActivity.this, EditCheckCase.class);
                        String textBody = mDataList.get((int)v.getTag()).assayRecount;
                        mIntent.putExtra("textType",5 );
                        mIntent.putExtra("textBody", textBody);
                        mIntent.putExtra("position", (int)v.getTag());
                        startActivityForResult(mIntent, EditCheckCase.REQ_CHANGE_EDIT_TEXT);
                    }
                });

                tv_imageRecountInfo = (TextView) itemView.findViewById(R.id.check_case_askedInfo_image);
                tv_imageRecountInfo.setTag(g_position);
                tv_imageRecountInfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mIntent = new Intent(CheckCaseActivity.this, EditCheckCase.class);
                        String textBody = mDataList.get((int)v.getTag()).imageRecount;
                        mIntent.putExtra("textType",6 );
                        mIntent.putExtra("textBody", textBody);
                        mIntent.putExtra("position", (int)v.getTag());
                        startActivityForResult(mIntent, EditCheckCase.REQ_CHANGE_EDIT_TEXT);
                    }
                });

                tv_medicationRecountInfo = (TextView) itemView.findViewById(R.id.check_case_askedInfo_medication);
                tv_medicationRecountInfo.setTag(g_position);
                tv_medicationRecountInfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mIntent = new Intent(CheckCaseActivity.this, EditCheckCase.class);
                        String textBody = mDataList.get((int)v.getTag()).medicationRecount;
                        mIntent.putExtra("textType",7 );
                        mIntent.putExtra("textBody", textBody);
                        mIntent.putExtra("position", (int)v.getTag());
                        startActivityForResult(mIntent, EditCheckCase.REQ_CHANGE_EDIT_TEXT);
                    }
                });
                g_position++;
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
////---------------------------------history--------------------------------------------------------------
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
                        g_previewImage = mDataList.get(pos).assayListImgs.get(position);
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
    //--------------------image-------------------------------------------------------
    class GridImgImageAdapter extends BaseAdapter implements ListAdapter {
        private ArrayList<Bitmap> imgArray = new ArrayList<Bitmap>();
        private int l_position=0;
        @Override
        public int getCount() {
            return imgArray.size();
        }
        GridImgImageAdapter(ArrayList<Bitmap> imgArray,int position){
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
                        intent.putExtra("limit", 9 - (mDataList.get(pos).imageListImgs.size() - 1));
                        intent.putExtra("item",pos);
                        intent.putExtra("channel",3);
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
                        mDataList.get(pos).imageListImgs.remove(position);
                        String imgId = "3" +mImageId.get(pos).substring(1);
                        mgr.deleteImage(imgId,position);
                        for (int i = 0; i < mDataList.get(pos).imageListImgs.size(); i++) {
                            if (mDataList.get(pos).imageListImgs.get(i) == null) {
                                is_addNull = false;
                                continue;
                            }
                        }
                        if (is_addNull) {
                            mDataList.get(pos).imageListImgs.add(null);
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
                        g_previewImage = mDataList.get(pos).imageListImgs.get(position);
                        startPreviewActivity();
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
    //--------------------medication-------------------------------------------------------
    class GridImgMedicationAdapter extends BaseAdapter implements ListAdapter {
        private ArrayList<Bitmap> imgArray = new ArrayList<Bitmap>();
        private int l_position=0;
        @Override
        public int getCount() {
            return imgArray.size();
        }
        GridImgMedicationAdapter(ArrayList<Bitmap> imgArray,int position){
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
                        intent.putExtra("limit", 9 - (mDataList.get(pos).medicationListImgs.size() - 1));
                        intent.putExtra("item",pos);
                        intent.putExtra("channel",4);
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
                        mDataList.get(pos).medicationListImgs.remove(position);
                        String imgId = "4" +mImageId.get(pos).substring(1);
                        mgr.deleteImage(imgId,position);
                        for (int i = 0; i < mDataList.get(pos).medicationListImgs.size(); i++) {
                            if (mDataList.get(pos).medicationListImgs.get(i) == null) {
                                is_addNull = false;
                                continue;
                            }
                        }
                        if (is_addNull) {
                            mDataList.get(pos).medicationListImgs.add(null);
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
                        g_previewImage = mDataList.get(pos).medicationListImgs.get(position);
                        startPreviewActivity();
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
            else if(channel == 3){return mDataList.get(item).imageListImgs;}
            else if(channel == 4){return mDataList.get(item).medicationListImgs;}
            return null;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        int mPosition;
        String mTextBody;
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
            case 102:
                if (data!= null) {
                    mPosition = data.getIntExtra("position", 0);
                    mTextBody = data.getStringExtra("textBody");
                    switch(data.getIntExtra("textType", 0)){
                        case 1:
                            mgr.updateHistoryRecountText(mContact.getAccount(),mPosition,mTextBody);
                            mDataList.get(mPosition).historyRecount = mTextBody;
                            break;
                        case 2:
                            mgr.updateHistoryCurCaseText(mContact.getAccount(),mPosition,mTextBody);
                            mDataList.get(mPosition).historyCurCase = mTextBody;
                            break;
                        case 3:
                            mgr.updateHistoryPastCaseText(mContact.getAccount(),mPosition,mTextBody);
                            mDataList.get(mPosition).historyPastCase = mTextBody;
                            break;
                        case 4:
                            mgr.updateHistorySignsText(mContact.getAccount(),mPosition,mTextBody);
                            mDataList.get(mPosition).historySigns = mTextBody;
                            break;
                        case 5:
                            mgr.updateAssayRecountText(mContact.getAccount(),mPosition,mTextBody);
                            mDataList.get(mPosition).assayRecount = mTextBody;
                            break;
                        case 6:
                            mgr.updateImageRecountText(mContact.getAccount(),mPosition,mTextBody);
                            mDataList.get(mPosition).imageRecount = mTextBody;
                            break;
                        case 7:
                            mgr.updateMedicationRecountText(mContact.getAccount(),mPosition,mTextBody);
                            mDataList.get(mPosition).medicationRecount = mTextBody;
                            break;
                    }
                }
                break;
            default:
                break;
        }
        mCheckCaseAdapter.notifyDataSetChanged();
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


