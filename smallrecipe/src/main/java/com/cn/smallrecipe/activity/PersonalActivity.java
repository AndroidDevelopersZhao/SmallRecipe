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
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cn.smallrecipe.MyActivity;
import com.cn.smallrecipe.R;
import com.cn.smallrecipe.Util;
import com.cn.smallrecipe.datainfo.register.ResultToApp;
import com.google.gson.Gson;


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
    private XXRoundImageView login;
    private int REQUSETCODE_CAM = 0x01;
    private int REQUSETCODE_PIC = 0x02;
    private TextView tv_username, tv_uid, tv_usernumber, tv_username_2;
    private String userLogoUrl = null;
    private String username = null;
    private String userid = null;
    private String usernumber = null;
    private Handler handler_getUserLogo = null;
    private Button bt_reLogin;

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
        setData();
    }

    private void setData() {
        if (MainActivity.LOGIN_STATE) {
            XXSharedPreferences file = new XXSharedPreferences(MainActivity.SHAREDSESSIONIDSAVEEDNAME);
            userLogoUrl = file.get(this, "userlogo", "").toString();
            username = file.get(this, "username", "").toString();
            userid = file.get(this, "userid", "").toString();
            usernumber = file.get(this, "usernumber", "").toString();
            tv_username.setText(username);
            tv_username_2.setText(username);
            tv_uid.setText(userid);
            tv_usernumber.setText(usernumber);
            getUserLogo(userLogoUrl);
        }
    }

    /**
     * 获取用户头像并设置到view
     *
     * @param url_userlogo
     */
    private void getUserLogo(String url_userlogo) {
        handler_getUserLogo = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case -1:
                        Toast.makeText(PersonalActivity.this, "用户头像为默认头像，该消息将在您设置完自定义头像后不再提示", Toast.LENGTH_LONG).show();
                        break;
                    case 1:
                        byte[] bytes = msg.getData().getByteArray("data");
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        login.setImageBitmap(bitmap);
                        break;
                }
            }
        };
        Log.d(TAG, "请求的用户头像url=" + url_userlogo);
        XXHttpClient client = new XXHttpClient(url_userlogo, true, new XXHttpClient.XXHttpResponseListener() {
            @Override
            public void onSuccess(int i, final byte[] bytes) {
                Log.d(TAG, "用户头像获取成功");
                Util.sendMsgToHandler(handler_getUserLogo, bytes, true);
            }

            @Override
            public void onError(int i, Throwable throwable) {
                Log.e(TAG, "网络异常");
                Util.sendMsgToHandler(handler_getUserLogo, "网络异常", false);
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {

            }
        });
        client.doPost(15000);
    }

    private void setListener() {
        login.setOnClickListener(this);
        bt_reLogin.setOnClickListener(this);
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
        tv_username = (TextView) findViewById(R.id.tv_username);
        tv_uid = (TextView) findViewById(R.id.tv_uid);
        tv_usernumber = (TextView) findViewById(R.id.tv_uernumber);
        tv_username_2 = (TextView) findViewById(R.id.tv_username_2);
        bt_reLogin = (Button) findViewById(R.id.bt_reLogin);
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
            userlogo = XXUtils.bitmapToBase64(bitmap);
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
        if (userlogo != null) {
            sendUserLogoToService(userlogo);
        }
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
                String Msg = msg.getData().getString("data");
                Toast.makeText(PersonalActivity.this, Msg, Toast.LENGTH_LONG).show();
            }
        };

        XXHttpClient client = new XXHttpClient(Util.URL_SERVICE_AUTH_UPDATEUSERLOGO, true, new XXHttpClient.XXHttpResponseListener() {
            @Override
            public void onSuccess(int i, byte[] bytes) {
                Log.d(TAG, "用户头像上送成功，返回：" + new String(bytes));
                ResultToApp resultToApp = new Gson().fromJson(new String(bytes), ResultToApp.class);
                if (resultToApp.getErrorCode() == 9000) {
                    Util.sendMsgToHandler(handler_senUserlogo, resultToApp.getResultMsg(), true);
                } else {
                    Util.sendMsgToHandler(handler_senUserlogo, resultToApp.getResultMsg(), false);
                }

            }

            @Override
            public void onError(int i, Throwable throwable) {
                Log.d(TAG, "用户头像上送失败，网络异常");
                Util.sendMsgToHandler(handler_senUserlogo, "网络异常", false);
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {

            }
        });
        String userNumber = String.valueOf(new XXSharedPreferences(MainActivity.SHAREDSESSIONIDSAVEEDNAME)
                .get(this, "usernumber", "").toString());
        String sessionid = String.valueOf(new XXSharedPreferences(MainActivity.SHAREDSESSIONIDSAVEEDNAME)
                .get(this, "sessionid", "").toString());

        client.put("userlogo", userlogo);
        client.put("sessionid", sessionid);
        client.put("usernumber", userNumber);
        Log.d(TAG, "上送的参数：usernumber=" + userNumber + ",sessionid=" + sessionid + ",userlogo=" + userlogo);
        Log.d(TAG, "上传图片大小：" + userlogo.length());
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

            case R.id.bt_reLogin:
                XXSharedPreferences file = new XXSharedPreferences(MainActivity.SHAREDSESSIONIDSAVEEDNAME);
                usernumber = file.get(this, "usernumber", "").toString();
                String sessionid = file.get(this, "sessionid", "").toString();
                exitAppAndReLogin(usernumber, sessionid);

                break;


        }
    }

    private Handler handler_exitApp = null;

    private void exitAppAndReLogin(String usernumber, String sessionid) {
        handler_exitApp = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                XXSharedPreferences file = new XXSharedPreferences(MainActivity.SHAREDSESSIONIDSAVEEDNAME);
                switch (msg.what) {
                    case -1:
                        Toast.makeText(PersonalActivity.this, msg.getData().getString("data"), Toast.LENGTH_LONG).show();
                        break;

                    case 1:
                        break;
                }
                MainActivity.LOGIN_STATE = false;
                file.clear(PersonalActivity.this);
                startActivity(new Intent(PersonalActivity.this, LoginActivity.class));
                finish();
            }
        };

        XXHttpClient client = new XXHttpClient(Util.URL_SERVICE_UNLOGIN, true, new XXHttpClient.XXHttpResponseListener() {
            @Override
            public void onSuccess(int i, byte[] bytes) {
                ResultToApp resultToApp = new Gson().fromJson(new String(bytes), ResultToApp.class);
                if (resultToApp.getErrorCode() == 9000) {
                    Util.sendMsgToHandler(handler_exitApp, "退出成功", true);
                } else {
                    Util.sendMsgToHandler(handler_exitApp, "您的账户在别的地方登陆，请重新登陆", false);
                }
            }

            @Override
            public void onError(int i, Throwable throwable) {
                Util.sendMsgToHandler(handler_exitApp, "网络异常", false);
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {

            }
        });
        client.put("usernumber", usernumber);
        client.put("sessionid", sessionid);
        client.doPost(15000);
    }


}
