package com.cn.smallrecipe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.cn.smallrecipe.MyActivity;
import com.cn.smallrecipe.R;

/**
 * Created by Administrator on 2016/2/25.
 */
public class LoginActivity extends MyActivity implements View.OnClickListener {
    private LinearLayout layout_register;
    private LinearLayout layout_login_back;
    private static final int RESULT_CODE = 0x01;
    private static final int REQUEST_CODE = 0x02;

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
    }

    private void findId() {
        layout_register = (LinearLayout) findViewById(R.id.layout_register);
        layout_login_back = (LinearLayout) findViewById(R.id.layout_login_back);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "LoginActivity was destroyed");
    }

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
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            //从注册页面返回
            Log.d(TAG, "onActivityResult-LoginActivity,requestCode:" + requestCode + ",resultCode:" + resultCode + ",data:" + data);
        }
    }
}
