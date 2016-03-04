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

import org.json.JSONException;
import org.json.JSONObject;

import cn.com.xxutils.progress.XXSVProgressHUD;
import cn.com.xxutils.util.XXHttpClient;
import cn.com.xxutils.util.XXUtils;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * Created by Administrator on 2016/2/25.
 */
public class RegisterActivity extends MyActivity implements View.OnClickListener {
    private LinearLayout layout_register_back;
    private static final int RESULT_CODE = 0x01;
    private Button bt_register_register;
    private EditText et_register_phoneNumber, et_register_password;
    private EventHandler eh;
    private Button bt_send_sms;
    private EditText et_sms_code;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "inter register activity");
        setContentView(R.layout.activity_register);
        setSMSListener();
        findId();
        setOclick();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterAllEventHandler();
    }

    private void setOclick() {
        layout_register_back.setOnClickListener(this);
        bt_register_register.setOnClickListener(this);
        bt_send_sms.setOnClickListener(this);
    }

    private void findId() {
        layout_register_back = (LinearLayout) findViewById(R.id.layout_register_back);
        bt_register_register = (Button) findViewById(R.id.bt_register_register);
        et_register_phoneNumber = (EditText) findViewById(R.id.et_register_phoneNumber);
        et_register_password = (EditText) findViewById(R.id.et_register_password);
        bt_send_sms = (Button) findViewById(R.id.bt_send_sms);
        et_sms_code = (EditText) findViewById(R.id.et_sms_code);
    }

    private Handler handler_sms_getSMSCode = null;//获取验证码的handle
    private Handler handler_sms_AuthCode = null;//验证证码的handle

    private String finalNumber = null;

    public void setSMSListener() {
        handler_sms_getSMSCode = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                dissmiss();
                switch (msg.what) {
                    case 1:
                        //短信验证码发送成功
                        et_register_phoneNumber.setEnabled(false);
                        bt_send_sms.setEnabled(false);
                        bt_send_sms.setText("发送成功");
                        Toast.makeText(RegisterActivity.this, "发送成功，延迟10秒左右,请耐心等待...", Toast.LENGTH_SHORT).show();
                        finalNumber = et_register_phoneNumber.getText().toString().trim();
                        break;

                    case -1:
                        //短信验证码发送失败
                        Toast.makeText(RegisterActivity.this, "发送失败，" + msg.getData().getString("data"), Toast.LENGTH_SHORT).show();
//                        finalNumber = null;
                        break;
                }
            }
        };

        handler_sms_AuthCode = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                dissmiss();
                switch (msg.what) {
                    case -1:

                        //短信验证码验证失败
                        XXSVProgressHUD.showErrorWithStatus(RegisterActivity.this, msg.getData().getString("data"));
                        break;
                    case 1:
                        //短信验证码验证成功
                        XXSVProgressHUD.showWithStatus(RegisterActivity.this, "正在注册");
                        String phone_number = finalNumber;
                        String password = et_register_password.getText().toString().trim();
                        if (phone_number != null && !phone_number.equals("")
                                && password != null && !password.equals("")) {
                            register(phone_number, password);
                        } else {
                            Toast.makeText(RegisterActivity.this, "用户名密码不能为空", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            }
        };

        eh = new EventHandler() {

            @Override
            public void afterEvent(int event, int result, Object data) {

                if (result == SMSSDK.RESULT_COMPLETE) {
                    //回调完成
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        //提交验证码成功
                        Log.d(TAG, "提交验证码成功");
                        Util.sendMsgToHandler(handler_sms_AuthCode, "验证成功", true);
                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        //获取验证码成功
                        Log.d(TAG, "获取验证码成功");
                        Util.sendMsgToHandler(handler_sms_getSMSCode, "获取成功", true);
                    } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                        //返回支持发送验证码的国家列表
                    }
                } else {
                    ((Throwable) data).printStackTrace();
                    Log.d(TAG, "ErrorMsh:" + ((Throwable) data).getMessage().toString());
                    String json = ((Throwable) data).getMessage().toString();
                    try {
                        JSONObject jo = new JSONObject(json);
                        int status = jo.getInt("status");
                        if (status == 468 || status == 603) {
                            Util.sendMsgToHandler(handler_sms_AuthCode, jo.getString("detail"), false);
                        } else if (status == 467) {
                            Util.sendMsgToHandler(handler_sms_AuthCode, jo.getString("description"), false);
                        } else if (status == 478) {
                            Util.sendMsgToHandler(handler_sms_getSMSCode, jo.getString("description"), false);
                        } else if (status == 462) {
                            Util.sendMsgToHandler(handler_sms_getSMSCode, jo.getString("description"), false);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                }
            }
        };
        SMSSDK.registerEventHandler(eh); //注册短信回调
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_register_back:
                setResult(RESULT_CODE);
                this.finish();
                break;
            case R.id.bt_register_register:
                //TODO 上送注册信息部分代码
                //验证验证码
//                Log.d(TAG, et_sms_code.getText().toString());
//                if (et_register_phoneNumber != null
//                        && !et_register_phoneNumber.getText().toString().equals("")
//                        && et_sms_code != null
//                        && !et_sms_code.getText().toString().equals("")
//                        && et_register_password != null
//                        && !et_register_password.getText().toString().equals("")) {
//
//                    XXSVProgressHUD.showWithStatus(RegisterActivity.this, "正在验证验证码，请稍后...");
//                    SMSSDK.submitVerificationCode("+86", finalNumber, et_sms_code.getText().toString().trim());
//                } else {
//                    Toast.makeText(RegisterActivity.this, "验证码和密码不可为空", Toast.LENGTH_SHORT).show();
//                }
                register(et_register_phoneNumber.getText().toString(), et_register_password.getText().toString());

                break;

            case R.id.bt_send_sms:
                //发送验证码按钮
                if (!et_register_phoneNumber.getText().toString().trim().equals("")
                        && et_register_phoneNumber.getText().toString() != null) {

                    if (XXUtils.checkMobileNumberValid(et_register_phoneNumber.getText().toString().trim())) {
                        //发送验证码
                        XXSVProgressHUD.showWithStatus(RegisterActivity.this, "正在发送验证码...");
                        SMSSDK.getVerificationCode("+86", et_register_phoneNumber.getText().toString().trim());
                    } else {
                        Toast.makeText(RegisterActivity.this, "电话号码格式不合法", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "电话号码不能为空", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void dissmiss() {
        if (XXSVProgressHUD.isShowing(RegisterActivity.this)) {
            XXSVProgressHUD.dismiss(RegisterActivity.this);
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
                        et_register_password.setText("");
                        et_register_password.setEnabled(true);
                        et_sms_code.setEnabled(true);
                        et_sms_code.setText("");
                        et_register_phoneNumber.setEnabled(true);
                        et_register_phoneNumber.setText("");
                        bt_send_sms.setEnabled(true);
                        bt_send_sms.setText("发送验证码");
                        break;

                    case 1:
                        Toast.makeText(RegisterActivity.this, Msg, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        intent.putExtra("usernumber", RegisterActivity.usernumber);
                        intent.putExtra("password", RegisterActivity.password);
                        setResult(RESULT_CODE, intent);
                        et_register_phoneNumber.setText("");
                        et_register_password.setText("");
                        et_sms_code.setText("");
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
