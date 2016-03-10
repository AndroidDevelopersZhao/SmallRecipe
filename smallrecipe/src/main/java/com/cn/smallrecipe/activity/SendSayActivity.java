package com.cn.smallrecipe.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.cn.smallrecipe.MyActivity;
import com.cn.smallrecipe.R;
import com.cn.smallrecipe.view.HorizontalListView;

import cn.com.xxutils.adapter.XXListViewAdapter;
import cn.com.xxutils.alerterview.OnItemClickListener;
import cn.com.xxutils.alerterview.XXAlertView;
import cn.com.xxutils.likebutton.XXUtils;

/**
 * Created by Administrator on 2016/3/10.
 */
public class SendSayActivity extends MyActivity implements AdapterView.OnItemClickListener, View.OnClickListener {
    private HorizontalListView lv_h_sendsay;
    private LinearLayout layout_img;
    private XXListViewAdapter<Bitmap> adapter = new XXListViewAdapter<Bitmap>(this, R.layout.item_listview_h_sendsay) {
        @Override
        public void initGetView(int position, View convertView, ViewGroup parent) {
            ImageView item_iv_listview_h_sendsay = (ImageView) convertView.findViewById(R.id.item_iv_listview_h_sendsay);
            item_iv_listview_h_sendsay.setImageBitmap(getItem(position));
        }
    };
    private Button bt_add_img;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sendsay);
        initView();
        Log.d(TAG, "intent.get ID:" + getIntent().getStringExtra("id"));
        Log.d(TAG, "intent.get com_name:" + getIntent().getStringExtra("com_name"));
    }

    private void initView() {
        bt_add_img = (Button) findViewById(R.id.bt_add_img);
        bt_add_img.setOnClickListener(this);
        lv_h_sendsay = (HorizontalListView) findViewById(R.id.lv_h_sendsay);
        lv_h_sendsay.setAdapter(adapter);
        lv_h_sendsay.setOnItemClickListener(this);
        layout_img = (LinearLayout) findViewById(R.id.layout_img);
//        adapter.addItem(cn.com.xxutils.util.XXUtils.getBitmapFromResources(this, R.drawable.delete_default_qq_avatar));
//        adapter.addItem(cn.com.xxutils.util.XXUtils.getBitmapFromResources(this, R.drawable.delete_default_qq_avatar));
//        adapter.addItem(cn.com.xxutils.util.XXUtils.getBitmapFromResources(this, R.drawable.delete_default_qq_avatar));
//        adapter.addItem(cn.com.xxutils.util.XXUtils.getBitmapFromResources(this, R.drawable.delete_default_qq_avatar));
//        adapter.addItem(cn.com.xxutils.util.XXUtils.getBitmapFromResources(this, R.drawable.delete_default_qq_avatar));
//        adapter.notifyDataSetChanged();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "position---------" + position);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_add_img:
                new XXAlertView("提示", "请选择图片来源", "取消", null,
                        new String[]{"图库", "相机"}, this, XXAlertView.Style.ActionSheet,
                        new OnItemClickListener() {
                            @Override
                            public void onItemClick(Object o, int position) {
                                Log.d(TAG, "position:" + position);
                                if (position == 0) {
                                    //图库
                                    layout_img.setVisibility(View.VISIBLE);
                                    adapter.addItem(cn.com.xxutils.util.XXUtils.getBitmapFromResources(SendSayActivity.this,
                                            R.drawable.delete_default_qq_avatar));
                                    adapter.notifyDataSetChanged();
                                } else if (position == 1) {
                                    //相机
                                }
                            }
                        }).show();
                break;
        }
    }
}
