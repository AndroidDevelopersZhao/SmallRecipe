package com.cn.smallrecipe.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cn.smallrecipe.MyActivity;
import com.cn.smallrecipe.R;
import com.cn.smallrecipe.Util;
import com.cn.smallrecipe.datainfo.register.ResultToApp;
import com.google.gson.Gson;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.com.xxutils.alerterview.OnItemClickListener;
import cn.com.xxutils.alerterview.XXAlertView;
import cn.com.xxutils.progress.XXSVProgressHUD;
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
    private LinearLayout lt_upDateUserName, lt_upDateUserEmail, lt_upDateName, lt_upDateUserSex, lt_upDateUserSign;//用户昵称、邮箱、姓名、性别、签名的selector
    private PopupWindow popupWindow;
    private View popview;
    private TextView tv_email_per, tv_name_per, tv_sex_per, tv_sign_per;
    private Button bt_savedData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "inter personal activity");
        setContentView(R.layout.personal_content);
        initVIew();
    }

    private void initVIew() {
        Log.d(TAG, "进入个人中心页面");
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
        getUserInfo(usernumber, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case -1:
                        Toast.makeText(PersonalActivity.this, msg.getData().getString("data"), Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Log.d(TAG, "准备设置view：" + msg.getData().getString("data"));
                        try {
                            JSONObject jsonObject = new JSONObject(msg.getData().getString("data"));
                            tv_username.setText(jsonObject.getString("username"));
                            tv_uid.setText(jsonObject.getString("userid"));
                            tv_usernumber.setText(jsonObject.getString("usernumber"));
                            tv_username_2.setText(jsonObject.getString("username"));
                            tv_email_per.setText(jsonObject.getString("email"));
                            tv_name_per.setText(jsonObject.getString("name"));
                            tv_sex_per.setText(jsonObject.getString("sex"));
                            tv_sign_per.setText(jsonObject.getString("signtext"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(PersonalActivity.this, "用户个人数据不完整，请补充资料", Toast.LENGTH_SHORT).show();
                        }

                        break;
                }
            }
        });
    }

    private void getUserInfo(String usernumber, final Handler handler) {
        XXHttpClient client = new XXHttpClient(Util.URL_GETUSERINFO, true, new XXHttpClient.XXHttpResponseListener() {
            @Override
            public void onSuccess(int i, byte[] bytes) {
                Log.w(TAG, "获取用户数据返回：" + new String(bytes));
                if (bytes.length > 0) {
                    try {
                        JSONObject jsonObject = new JSONObject(new String(bytes));
                        if (jsonObject.getInt("errorCode") == 9000) {
                            JSONObject jsonObject1 = new JSONObject(jsonObject.get("userinfo").toString());
                            XXSharedPreferences s = new XXSharedPreferences(MainActivity.SHAREDSESSIONIDSAVEEDNAME);
                            s.put(PersonalActivity.this, "username", jsonObject1.getString("username"));
                            Util.sendMsgToHandler(handler, jsonObject1.toString(), true);
                        } else {
                            Util.sendMsgToHandler(handler, jsonObject.getString("resultMsg"), false);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Util.sendMsgToHandler(handler, "数据解析出错", false);
                    }
                }
            }

            @Override
            public void onError(int i, Throwable throwable) {
                Log.w(TAG, "获取用户数据返回网络异常");
                Util.sendMsgToHandler(handler, "网络异常", false);
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {

            }
        });
        client.put("usernumber", usernumber);
        client.doPost(15000);
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
//        tv_username_2.addTextChangedListener(this);
//        tv_email_per.addTextChangedListener(this);
//        tv_sex_per.addTextChangedListener(this);
//        tv_name_per.addTextChangedListener(this);
//        tv_sign_per.addTextChangedListener(this);
        bt_savedData.setOnClickListener(this);
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

        lt_upDateUserName = (LinearLayout) findViewById(R.id.lt_upDateUserName);
        lt_upDateUserEmail = (LinearLayout) findViewById(R.id.lt_upDateUserEmail);
        lt_upDateName = (LinearLayout) findViewById(R.id.lt_upDateName);
        lt_upDateUserSex = (LinearLayout) findViewById(R.id.lt_upDateUserSex);
        lt_upDateUserSign = (LinearLayout) findViewById(R.id.lt_upDateUserSign);

        lt_upDateUserName.setOnClickListener(this);
        lt_upDateUserEmail.setOnClickListener(this);
        lt_upDateName.setOnClickListener(this);
        lt_upDateUserSex.setOnClickListener(this);
        lt_upDateUserSign.setOnClickListener(this);
        tv_email_per = (TextView) findViewById(R.id.tv_email_per);
        tv_name_per = (TextView) findViewById(R.id.tv_name_per);
        tv_sex_per = (TextView) findViewById(R.id.tv_sex_per);
        tv_sign_per = (TextView) findViewById(R.id.tv_sign_per);
        bt_savedData = (Button) findViewById(R.id.bt_savedData);
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
                Toast.makeText(PersonalActivity.this, "正在注销，请稍后...", Toast.LENGTH_LONG).show();
                break;
            case R.id.lt_upDateUserName:
                //弹出设置用户昵称的popwindows
                bt_savedData.setEnabled(true);
                showPopwindosView("用户名", tv_username_2);
                break;
            case R.id.lt_upDateUserEmail:
                //弹出设置用户E-Mail的popwindows
                bt_savedData.setEnabled(true);
                showPopwindosView("邮箱地址", tv_email_per);
                break;

            case R.id.lt_upDateName:
                //弹出设置用户姓名的popwindows
                bt_savedData.setEnabled(true);
                showPopwindosView("姓名", tv_name_per);
                break;
            case R.id.lt_upDateUserSex:
                //弹出设置用户性别的popwindows
                bt_savedData.setEnabled(true);
                showPopwindosView("性别", tv_sex_per);
                break;
            case R.id.lt_upDateUserSign:
                //弹出设置用户签名的popwindows
                bt_savedData.setEnabled(true);
                showPopwindosView("签名", tv_sign_per);
                break;
            case R.id.bt_savedData:
                //上送新的个人信息资料到后台
                XXSVProgressHUD.showWithStatus(PersonalActivity.this, "正在更新资料，请稍后...");
                updateNewsData();
                break;

        }
    }

    private Handler handler;

    private void updateNewsData() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (XXSVProgressHUD.isShowing(PersonalActivity.this)) {
                    XXSVProgressHUD.dismiss(PersonalActivity.this);
                }
                switch (msg.what) {
                    case -1:
                        Toast.makeText(PersonalActivity.this, msg.getData().getString("data"), Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        bt_savedData.setEnabled(false);
                        Toast.makeText(PersonalActivity.this, "用户资料更新成功", Toast.LENGTH_SHORT).show();
                        break;
                }

            }
        };
        String usernumber = new XXSharedPreferences(MainActivity.SHAREDSESSIONIDSAVEEDNAME).get(PersonalActivity.this, "usernumber", "").toString();
        final String username = tv_username_2.getText().toString().trim();
        String name = tv_name_per.getText().toString().trim();
        String sex = tv_sex_per.getText().toString().trim().equals("男") ? "1" : "女";
        String email = tv_email_per.getText().toString().trim();
        String signtext = tv_sign_per.getText().toString().trim();

        XXHttpClient client = new XXHttpClient(Util.URL_UPDATEUSERINFO, true, new XXHttpClient.XXHttpResponseListener() {
            @Override
            public void onSuccess(int i, byte[] bytes) {
                Log.d(TAG, "用户个人信息更新的返回：" + new String(bytes));
                try {
                    JSONObject jsonObject = new JSONObject(new String(bytes));
                    int errorCode = jsonObject.getInt("errorCode");
                    if (errorCode == 9000) {
                        XXSharedPreferences s = new XXSharedPreferences(MainActivity.SHAREDSESSIONIDSAVEEDNAME);
                        s.put(PersonalActivity.this, "username", username);
                        Util.sendMsgToHandler(handler, "更新成功", true);
                    } else {
                        Util.sendMsgToHandler(handler, jsonObject.getString("resultMsg"), false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(int i, Throwable throwable) {
                Log.d(TAG, "用户个人信息更新的返回：网络异常");
                Util.sendMsgToHandler(handler, "更新失败，网络异常", false);
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {

            }
        });
        client.put("usernumber", usernumber);
        client.put("username", username);
        client.put("name", name);
        client.put("sex", sex);
        client.put("email", email);
        client.put("signtext", signtext);
        Log.w(TAG, "上送最新个人资料：" + client.getAllParams());
        client.doPost(15000);
    }

    private void showPopwindosView(String hite, final TextView textView) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View prarentView = findViewById(R.id.view_snacbay);
        if (popview == null) {
            popview = inflater.inflate(R.layout.pop_window, null);
        }
        final EditText et_pop = (EditText) popview.findViewById(R.id.et_pop);
        Button bt_pop = (Button) popview.findViewById(R.id.bt_pop);
        et_pop.setHint("请输入新的" + hite);
        bt_pop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_pop.getText() != null && !et_pop.getText().toString().trim().equals("")) {
                    textView.setText(et_pop.getText().toString().trim());
                    et_pop.setText("");
                    popupWindow.dismiss();
                } else {
                    Toast.makeText(PersonalActivity.this, "您还没有输入", Toast.LENGTH_SHORT).show();
                }
            }
        });
        popupWindow = new PopupWindow(popview,
                ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT, true);

        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.showAtLocation(prarentView, Gravity.RIGHT, 0, 0);
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
                if (MyActivity.TENCENT != null) {
                    MyActivity.TENCENT.logout(PersonalActivity.this);
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
