package com.cn.smallrecipe.activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.cn.smallrecipe.MyActivity;
import com.cn.smallrecipe.R;
import com.cn.smallrecipe.Util;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.com.xxutils.alerterview.OnItemClickListener;
import cn.com.xxutils.alerterview.XXAlertView;
import cn.com.xxutils.util.XXHttpClient;
import cn.com.xxutils.util.XXSharedPreferences;
import cn.com.xxutils.util.XXUtils;
import cn.com.xxutils.view.XXRoundImageView;

/**
 * Created by Administrator on 2016/2/26.
 */
public class PersonalActivity extends MyActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private CollapsingToolbarLayout toolbarLayout;
    private RelativeLayout rlayout_myself_name;
    private XXRoundImageView login;
    private int REQUSETCODE_CAM = 0x01;
    private int REQUSETCODE_PIC = 0x02;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "inter personal activity");
        setContentView(R.layout.personal_content);
        initVIew();
    }

    private void initVIew() {
        findId();
        setListener();
    }

    private void setListener() {
        login.setOnClickListener(this);
    }

    private void findId() {
        toolbar = (Toolbar) findViewById(R.id.tb);
        setSupportActionBar(toolbar);
        toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.cool);
//        toolbarLayout.setTitle("2013");
        getSupportActionBar().setTitle("个人中心");
        toolbarLayout.setCollapsedTitleGravity(Gravity.LEFT);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
        login = (XXRoundImageView) findViewById(R.id.login);
        rlayout_myself_name=(RelativeLayout)findViewById(R.id.rlayout_myself_name);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "personal activity destroyed");
    }

    private String userlogo = null;//Base64编码后的用户头像字符串

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUSETCODE_CAM) {
            //来源为相机
            Bundle bundle = data.getExtras();
            //获取相机返回的数据，并转换为图片格式
            Bitmap bitmap = (Bitmap) bundle.get("data");
            login.setImageBitmap(bitmap);
//            //对图片进行编码-上送服务器
//            FileOutputStream b = null;
//            //???????????????????????????????为什么不能直接保存在系统相册位置呢？？？？？？？？？？？？
//            File file = new File(Environment.getDataDirectory().getPath()+"123.jpg");
//            file.mkdirs();// 创建文件夹
//            try {
//                b = new FileOutputStream(file.getPath());
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            } finally {
//                try {
//                    b.flush();
//                    b.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            userlogo= Base64.encodeToString(XXUtils.bitmapToBytes(bitmap),0,XXUtils.bitmapToBytes(bitmap).length,0x01);
            userlogo = XXUtils.bitmapToBase64(bitmap);

//                    login.setImageBitmap(XXUtils.base64ToBitmap(userlogo));
        } else if (requestCode == REQUSETCODE_PIC) {
            Uri selectedImage = data.getData();
            String[] filePathColumns = {MediaStore.Images.Media.DATA};
            Cursor c = this.getContentResolver().query(selectedImage, filePathColumns, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePathColumns[0]);
            String picturePath = c.getString(columnIndex);
            c.close();
            login.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            userlogo = XXUtils.bitmapToBase64(BitmapFactory.decodeFile(picturePath));
        }
        sendUserLogoToService(userlogo);
    }

    private Handler handler_senUserlogo = null;

    /**
     * 上送用户头像到服务器
     *
     * @param userlogo
     */
    private void sendUserLogoToService(String userlogo) {
        handler_senUserlogo = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case -1:

                        break;
                    case 1:

                        break;
                }
            }
        };

        XXHttpClient client = new XXHttpClient(Util.URL_SERVICE_AUTH_UPDATEUSERLOGO, true, new XXHttpClient.XXHttpResponseListener() {
            @Override
            public void onSuccess(int i, byte[] bytes) {
                Log.d(TAG, "用户头像上送成功，返回：" + new String(bytes));
            }

            @Override
            public void onError(int i, Throwable throwable) {
                Log.d(TAG, "用户头像上送失败，网络异常");
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {

            }
        });
        String userNumber =String.valueOf(new XXSharedPreferences(MainActivity.SHAREDSESSIONIDSAVEEDNAME)
                .get(this, "usernumber", "").toString());
        String sessionid =String.valueOf(new XXSharedPreferences(MainActivity.SHAREDSESSIONIDSAVEEDNAME)
                .get(this, "sessionid", "").toString());

        client.put("userlogo", userlogo);
        client.put("sessionid",sessionid);
        client.put("usernumber",userNumber );
        Log.d(TAG, "上送的参数：usernumber=" + userNumber + ",sessionid=" + sessionid + ",userlogo=" + userlogo);
        Log.d(TAG,"上传图片大小："+userlogo.length());
        client.doPost(15000);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login:
                new XXAlertView("提示", "请选择头像来源", "取消", null,
                        new String[]{"图库", "相机"}, this, XXAlertView.Style.ActionSheet,
                        new OnItemClickListener() {
                            @Override
                            public void onItemClick(Object o, int position) {
                                Log.d(TAG, "position:" + position);
                                if (position == 0) {
                                    //图库
                                    Intent picture = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                    startActivityForResult(picture, REQUSETCODE_PIC);
                                } else if (position == 1) {
                                    //相机
                                    Intent cameraintent = new Intent(
                                            MediaStore.ACTION_IMAGE_CAPTURE);
//                                    cameraintent.putExtra(MediaStore.EXTRA_OUTPUT,
//                                            Uri.fromFile(tempFile));
                                    startActivityForResult(cameraintent, REQUSETCODE_CAM);
                                }
                            }
                        }).show();
                break;

            case R.id.rlayout_myself_name:

                Toast.makeText(PersonalActivity.this,"点击了",Toast.LENGTH_SHORT).show();

                break;


        }
    }


}
