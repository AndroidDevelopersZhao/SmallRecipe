package com.cn.smallrecipe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cn.smallrecipe.MyActivity;
import com.cn.smallrecipe.R;
import com.cn.smallrecipe.Util;
import com.cn.smallrecipe.datainfo.register.ResultToApp;
import com.cn.smallrecipe.qqinfo.BaseUiListener;
import com.google.gson.Gson;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import cn.com.xxutils.alerterview.OnItemClickListener;
import cn.com.xxutils.alerterview.XXAlertView;
import cn.com.xxutils.progress.XXSVProgressHUD;
import cn.com.xxutils.util.XXHttpClient;
import cn.com.xxutils.util.XXSharedPreferences;

/**
 * Created by Administrator on 2016/2/25.
 */
public class LoginActivity extends MyActivity implements View.OnClickListener {
    private LinearLayout layout_register;
    private LinearLayout layout_login_back;
    private static final int RESULT_CODE = 0x01;
    private static final int REQUEST_CODE = 0x02;
    private AutoCompleteTextView auto_text_usernumber;
    private EditText et_text_password;
    private ImageView bt_login_login;
    private Button bt_login_with_qq;
    private final String APP_ID = "1105221610";
    private Tencent mTencent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "inter LoginActivity view");
        setContentView(R.layout.activity_login);

        findId();
        setOclick();
    }

    private void setOclick() {
        layout_register.setOnClickListener(this);
        layout_login_back.setOnClickListener(this);
        bt_login_login.setOnClickListener(this);
        bt_login_with_qq.setOnClickListener(this);
    }

    private void findId() {
        layout_register = (LinearLayout) findViewById(R.id.layout_register);
        layout_login_back = (LinearLayout) findViewById(R.id.layout_login_back);
        auto_text_usernumber = (AutoCompleteTextView) findViewById(R.id.auto_text_usernumber);
        et_text_password = (EditText) findViewById(R.id.et_text_password);
        bt_login_login = (ImageView) findViewById(R.id.bt_login_login);
        bt_login_with_qq = (Button) findViewById(R.id.bt_login_with_qq);
    }

    @Override
    protected void onDestroy() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("userid", uid);
        setResult(RESULT_CODE, intent);
        super.onDestroy();
        Log.d(TAG, "LoginActivity was destroyed");
    }

    private String usernumber = null;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_register:
                //跳入注册页面
                startActivityForResult(new Intent(LoginActivity.this, RegisterActivity.class), REQUEST_CODE);
                break;
            case R.id.layout_login_back:
                setResult(RESULT_CODE);
                this.finish();
                break;
            case R.id.bt_login_login:

                usernumber = auto_text_usernumber.getText().toString().trim();
                String password = et_text_password.getText().toString().trim();
                if (usernumber != null && !usernumber.equals("")
                        && password != null && !password.equals("")) {
                    XXSVProgressHUD.showWithStatus(this, "正在登陆。。。");
                    login(usernumber, password);
                } else {
                    Toast.makeText(this, "用户名密码不能为空", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.bt_login_with_qq:
                //QQ登陆
                loginWithQQ();
                break;
        }
    }

    /**
     * ｓｈｉｙｏｎｇＱＱ登陆
     */
    private void loginWithQQ() {
        // Tencent类是SDK的主要实现类，开发者可通过Tencent类访问腾讯开放的OpenAPI。
        // 其中APP_ID是分配给第三方应用的appid，类型为String。
        mTencent = Tencent.createInstance(APP_ID, this.getApplicationContext());
        // 1.4版本:此处需新增参数，传入应用程序的全局context，可通过activity的getApplicationContext方法获取
        login();
    }

    public void login() {
        mTencent = Tencent.createInstance(APP_ID, this.getApplicationContext());
        if (!mTencent.isSessionValid()) {
            mTencent.login(this, "all", new IUiListener() {
                @Override
                public void onComplete(Object o) {
                    Log.d(TAG, "授权登陆成功：" + o.toString());

                    try {
                        JSONObject jsonObject = new JSONObject(o.toString());
                        initOpenidAndToken(jsonObject);
                        UserInfo info = new UserInfo(LoginActivity.this, mTencent.getQQToken());
                        info.getUserInfo(new IUiListener() {
                            @Override
                            public void onComplete(Object o) {
                                Log.d(TAG, "用户信息获取成功：" + o.toString());
                                try {
                                    JSONObject jo=new JSONObject(o.toString());

                                    Toast.makeText(LoginActivity.this,"授权登陆成功：\n是否为年费黄钻："
                                            +jo.getString("is_yellow_year_vip")+
                                            "\nQQ头像URL："+jo.getString("figureurl_qq_1")+
                                            "\n昵称："+jo.getString("nickname")+
                                            "\n城市："+jo.getString("city")+
                                            "\n城市："+jo.getString("province")+
                                            "\n性别："+jo.getString("gender")+
                                            "\n",Toast.LENGTH_SHORT).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(UiError uiError) {
                                Log.e(TAG, "用户信息获取失败：" + uiError.errorMessage);
                            }

                            @Override
                            public void onCancel() {
                                Log.e(TAG, "用户取消获取");
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onError(UiError uiError) {

                }

                @Override
                public void onCancel() {

                }
            });
        }


    }


    public void initOpenidAndToken(JSONObject jsonObject) {
        try {
            String token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN);
            String expires = jsonObject.getString(Constants.PARAM_EXPIRES_IN);
            String openId = jsonObject.getString(Constants.PARAM_OPEN_ID);
            if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires)
                    && !TextUtils.isEmpty(openId)) {
                mTencent.setAccessToken(token, expires);
                mTencent.setOpenId(openId);
            }
        } catch (Exception e) {
        }
    }


    private Handler handler_login = null;
    private String uid = null;
    private XXSharedPreferences sharedPreferences;

    /**
     * 登陆
     *
     * @param usernumber
     * @param password
     */
    private void login(final String usernumber, String password) {
        handler_login = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (XXSVProgressHUD.isShowing(LoginActivity.this)) {
                    XXSVProgressHUD.dismiss(LoginActivity.this);
                }
                String Msg = msg.getData().getString("data");
                switch (msg.what) {
                    case -1:
                        Toast.makeText(LoginActivity.this, Msg, Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("userid", uid);
                        setResult(RESULT_CODE, intent);
                        finish();
                        break;
                }
            }
        };


        XXHttpClient client = new XXHttpClient(Util.URL_SERVICE_LOGIN, true, new XXHttpClient.XXHttpResponseListener() {
            @Override
            public void onSuccess(int i, byte[] bytes) {
                Log.d(TAG, "登陆返回：" + new String(bytes));
                ResultToApp resultToApp = new Gson().fromJson(new String(bytes), ResultToApp.class);
                Log.d(TAG, "登陆返回状态码：" + resultToApp.getErrorCode());
                Log.d(TAG, "登陆返回状态信息：" + resultToApp.getResultMsg());
                if (resultToApp.getErrorCode() == 9000) {
                    Log.d(TAG, "返回sessionID:" + resultToApp.getRespData().getSessionId());
                    sharedPreferences = new XXSharedPreferences(MainActivity.SHAREDSESSIONIDSAVEEDNAME);
                    sharedPreferences.put(LoginActivity.this, "sessionid", resultToApp.getRespData().getSessionId());
                    sharedPreferences.put(LoginActivity.this, "usernumber", usernumber);

                    sharedPreferences.put(LoginActivity.this, "username", resultToApp.getRespData().getUsername());
                    sharedPreferences.put(LoginActivity.this, "userid", resultToApp.getRespData().getUserid());
                    sharedPreferences.put(LoginActivity.this, "userlogo", resultToApp.getRespData().getUserlogo());
                    uid = sharedPreferences.get(LoginActivity.this, "userid", resultToApp.getRespData().getUserid()).toString();
                    Log.d(TAG, "用户数据存入缓存成功");
                    Util.sendMsgToHandler(handler_login, resultToApp.getResultMsg(), true);
                } else if (resultToApp.getErrorCode() == -3) {
                    final String reLoginId = resultToApp.getRespData().getReLoginId();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (XXSVProgressHUD.isShowing(LoginActivity.this)) {
                                XXSVProgressHUD.dismiss(LoginActivity.this);
                            }
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    SystemClock.sleep(500);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            new XXAlertView("提示", "该账户已经登陆，是否重新登陆", "强制登陆", null, new String[]{"取消"}, LoginActivity.this, XXAlertView.Style.Alert, new OnItemClickListener() {
                                                @Override
                                                public void onItemClick(Object o, int position) {
                                                    Log.d(TAG, "position:" + position);
                                                    if (position == -1) {
                                                        //重新登陆
                                                        Log.d(TAG, "开始重新登陆。当前传入的usernumber=" + usernumber);
                                                        reLogin(usernumber, reLoginId);
                                                    } else {
                                                        Util.sendMsgToHandler(handler_login, "当账号被使用时您可以强制登陆迫使对方下线", false);
                                                    }
                                                }
                                            }).show();
                                        }
                                    });

                                }
                            }).start();

                        }
                    });
                } else {
                    Util.sendMsgToHandler(handler_login, resultToApp.getResultMsg(), false);
                }
            }

            @Override
            public void onError(int i, Throwable throwable) {
                Log.e(TAG, "登陆失败，网络异常");
                Util.sendMsgToHandler(handler_login, "网络异常", false);
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {

            }
        });
        client.put("usernumber", usernumber);
        client.put("password", password);
        client.doPost(15000);
    }

    /**
     * 重新登陆
     *
     * @param usernumber
     * @param reLoginId
     */
    private Handler handler_reLogin = null;

    private void reLogin(final String usernumber, String reLoginId) {
        handler_reLogin = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (XXSVProgressHUD.isShowing(LoginActivity.this)) {
                    XXSVProgressHUD.dismiss(LoginActivity.this);
                }
                String Msg = msg.getData().getString("data");
                switch (msg.what) {
                    case -1:
                        Toast.makeText(LoginActivity.this, Msg, Toast.LENGTH_SHORT).show();
                        break;

                    case 1:
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("userid", uid);
                        setResult(RESULT_CODE, intent);
                        finish();
                        break;
                }
            }
        };
        XXSVProgressHUD.showWithStatus(LoginActivity.this, "正在强制登陆...");
        XXHttpClient client = new XXHttpClient(Util.URL_SERVICE_RELOGIN, true, new XXHttpClient.XXHttpResponseListener() {
            @Override
            public void onSuccess(int i, byte[] bytes) {
                Log.d(TAG, "强制登陆结果：" + new String(bytes));
                ResultToApp app = new Gson().fromJson(new String(bytes), ResultToApp.class);
                if (app.getErrorCode() == 9000) {

                    sharedPreferences = new XXSharedPreferences(MainActivity.SHAREDSESSIONIDSAVEEDNAME);

                    sharedPreferences.put(LoginActivity.this, "sessionid", app.getRespData().getSessionId());
                    sharedPreferences.put(LoginActivity.this, "usernumber", usernumber);

                    sharedPreferences.put(LoginActivity.this, "username", app.getRespData().getUsername());
                    sharedPreferences.put(LoginActivity.this, "userid", app.getRespData().getUserid());
                    sharedPreferences.put(LoginActivity.this, "userlogo", app.getRespData().getUserlogo());
                    Log.d(TAG, "强制登陆成功，已写入共享参数，读取测试sessionid：" + sharedPreferences.get(LoginActivity.this, "sessionid", "") + "\nusernumber=" +
                            usernumber + "\nuid=" + sharedPreferences.get(LoginActivity.this, "userid", app.getRespData().getUserid().toString()));
                    uid = sharedPreferences.get(LoginActivity.this, "userid", "").toString();
                    Util.sendMsgToHandler(handler_reLogin, app.getResultMsg(), true);

                } else {
                    Util.sendMsgToHandler(handler_reLogin, app.getResultMsg(), false);
                }
            }

            @Override
            public void onError(int i, Throwable throwable) {
                Log.e(TAG, "强制登陆网络异常");
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {

            }
        });
        client.put("usernumber", usernumber);
        client.put("reloginid", reLoginId);
        client.doPost(15000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Tencent.onActivityResultData(requestCode, resultCode, data, new BaseUiListener());
        if (requestCode == REQUEST_CODE) {
            //从注册页面返回
            Log.d(TAG, "onActivityResult-LoginActivity,requestCode:" + requestCode + ",resultCode:" + resultCode + ",data:" + data);
            if (data != null) {
                String usernumber = data.getStringExtra("usernumber");
                String password = data.getStringExtra("password");
                Log.d(TAG, "注册页面返回的账号信息：" + "usernumber=" + usernumber + ",password=" + password);
                auto_text_usernumber.setText(usernumber);
                et_text_password.setText(password);
            }
        }
    }


}
