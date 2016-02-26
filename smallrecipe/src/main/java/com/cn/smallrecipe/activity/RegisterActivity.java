package com.cn.smallrecipe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cn.smallrecipe.MyActivity;
import com.cn.smallrecipe.R;
import com.cn.smallrecipe.Util;
import com.cn.smallrecipe.datainfo.register.ResultToApp;
import com.google.gson.Gson;

import cn.com.xxutils.progress.XXSVProgressHUD;
import cn.com.xxutils.util.XXHttpClient;

/**
 * Created by Administrator on 2016/2/25.
 */
public class RegisterActivity extends MyActivity implements View.OnClickListener {
    private LinearLayout layout_register_back;
    private static final int RESULT_CODE = 0x01;
    private Button bt_register_register;
    private EditText et_register_phoneNumber, et_register_password;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "inter register activity");
        setContentView(R.layout.activity_register);
        findId();
        setOclick();
    }

    private void setOclick() {
        layout_register_back.setOnClickListener(this);
        bt_register_register.setOnClickListener(this);
    }

    private void findId() {
        layout_register_back = (LinearLayout) findViewById(R.id.layout_register_back);
        bt_register_register = (Button) findViewById(R.id.bt_register_register);
        et_register_phoneNumber = (EditText) findViewById(R.id.et_register_phoneNumber);
        et_register_password = (EditText) findViewById(R.id.et_register_password);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_register_back:
                setResult(RESULT_CODE);
                this.finish();
                break;
            case R.id.bt_register_register:

                String phone_number = et_register_phoneNumber.getText().toString().trim();
                String password = et_register_password.getText().toString().trim();
                if (phone_number != null && !phone_number.equals("")
                        && password != null && !password.equals("")) {
                    XXSVProgressHUD.showWithStatus(this, "正在注册");
                    register(phone_number, password);
                } else {
                    Toast.makeText(RegisterActivity.this, "用户名密码不能为空", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private Handler handler_register = null;
    private static String usernumber = null;
    private static String password = null;

    /**
     * 注册账号
     *
     * @param phone_number
     * @param password
     */
    private void register(String phone_number, String password) {
        handler_register = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (XXSVProgressHUD.isShowing(RegisterActivity.this)) {
                    XXSVProgressHUD.dismiss(RegisterActivity.this);
                }
                String Msg = msg.getData().getString("data").toString();
                switch (msg.what) {
                    case -1:
                        Toast.makeText(RegisterActivity.this, Msg, Toast.LENGTH_SHORT).show();
                        break;

                    case 1:
                        Toast.makeText(RegisterActivity.this, Msg, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        intent.putExtra("usernumber", RegisterActivity.usernumber);
                        intent.putExtra("password", RegisterActivity.password);
                        setResult(RESULT_CODE, intent);
                        et_register_phoneNumber.setText("");
                        et_register_password.setText("");
                        finish();
                        break;
                }
            }
        };

        //网络请求
        XXHttpClient client = new XXHttpClient(Util.URL_SERVICE_REGISTER, true, new XXHttpClient.XXHttpResponseListener() {
            @Override
            public void onSuccess(int i, byte[] bytes) {
                Log.d(TAG, "注册请求成功，返回：" + new String(bytes));
                ResultToApp resultToApp = new Gson().fromJson(new String(bytes), ResultToApp.class);
                Log.d(TAG, "返回状态码：" + resultToApp.getErrorCode());
                Log.d(TAG, "返回状态信息：" + resultToApp.getResultMsg());
                if (resultToApp.getErrorCode() == 9000) {
                    Util.sendMsgToHandler(handler_register, resultToApp.getResultMsg(), true);
                } else {
                    Util.sendMsgToHandler(handler_register, resultToApp.getResultMsg(), false);
                }
            }

            @Override
            public void onError(int i, Throwable throwable) {
                Log.d(TAG, "注册请求失败,网络异常");
                Util.sendMsgToHandler(handler_register, "网络异常", false);
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {

            }
        });
        client.put("usernumber", phone_number);
        client.put("password", password);
        this.usernumber = phone_number;
        this.password = password;

        client.doPost(15000);
    }
}
