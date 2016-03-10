package com.cn.smallrecipe.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cn.smallrecipe.MyActivity;
import com.cn.smallrecipe.R;
import com.cn.smallrecipe.Util;
import com.cn.smallrecipe.datainfo.register.ResultToApp;
import com.cn.smallrecipe.datainfo.search.Result;
import com.google.gson.Gson;

import cn.com.xxutils.progress.XXSVProgressHUD;
import cn.com.xxutils.util.XXHttpClient;
import cn.com.xxutils.util.XXSharedPreferences;

/**
 * Created by Administrator on 2016/3/9.
 */
public class RespMsgActivity extends MyActivity implements View.OnClickListener {
    private EditText et_respmsg;
    private Button bt_send_respmsg;
    private ImageView iv_back;
    private TextView tv_back;
    private XXSharedPreferences preferences = new XXSharedPreferences(MainActivity.SHAREDSESSIONIDSAVEEDNAME);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_respmsg);
        et_respmsg = (EditText) findViewById(R.id.et_respmsg);
        bt_send_respmsg = (Button) findViewById(R.id.bt_send_respmsg);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        tv_back = (TextView) findViewById(R.id.tv_back);
        bt_send_respmsg.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        tv_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_send_respmsg:
                if (et_respmsg.getText() != null && !et_respmsg.getText().toString().equals("")) {

                    sendRespMsgToService(et_respmsg.getText().toString().trim());
                } else {
                    Toast.makeText(this, "您还没有输入内容哦", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.iv_back:
                this.finish();
                break;

            case R.id.tv_back:
                this.finish();
                break;
        }
    }

    /**
     * 上送用户意见建议到服务器
     *
     * @param trim
     */
    private Handler handler_sendUserRespMsg = null;

    private void sendRespMsgToService(String trim) {
        handler_sendUserRespMsg = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (XXSVProgressHUD.isShowing(RespMsgActivity.this)) {
                    XXSVProgressHUD.dismiss(RespMsgActivity.this);
                }
                switch (msg.what) {
                    case -1:
                        Toast.makeText(RespMsgActivity.this, msg.getData().getString("data"), Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        et_respmsg.setText("");
                        Toast.makeText(RespMsgActivity.this, "提交成功，感谢您的支持", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
        XXSVProgressHUD.showWithStatus(this, "正在上送您的反馈信息...");
        XXHttpClient client = new XXHttpClient(Util.URL_SENDUSERRESPMSG, true, new XXHttpClient.XXHttpResponseListener() {
            @Override
            public void onSuccess(int i, byte[] bytes) {
                if (bytes.length > 0) {
                    ResultToApp app = new Gson().fromJson(new String(bytes), ResultToApp.class);
                    if (app.getErrorCode() == 9000) {
                        Util.sendMsgToHandler(handler_sendUserRespMsg, "成功", true);
                    } else {
                        Util.sendMsgToHandler(handler_sendUserRespMsg, app.getResultMsg(), false);
                    }
                } else {
                    Util.sendMsgToHandler(handler_sendUserRespMsg, "后台数据库异常,请稍后再试", false);
                }
                Log.d(TAG, "反馈信息接口返回：" + new String(bytes));
            }

            @Override
            public void onError(int i, Throwable throwable) {
                Log.e(TAG, "反馈信息接口网络异常");
                Util.sendMsgToHandler(handler_sendUserRespMsg, "网络异常,请稍后再试", false);
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {

            }
        });
        client.put("usernumber", preferences.get(RespMsgActivity.this, "usernumber", "").toString());
        client.put("msg", trim);

        client.doPost(15000);
    }
}
