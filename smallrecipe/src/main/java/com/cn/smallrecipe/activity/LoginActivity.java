package com.cn.smallrecipe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.cn.smallrecipe.MyActivity;
import com.cn.smallrecipe.R;

/**
 * Created by Administrator on 2016/2/25.
 */
public class LoginActivity extends MyActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "inter LoginActivity view");
        setContentView(R.layout.activity_login);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "LoginActivity was destroyed");
    }
}
