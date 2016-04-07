package com.cn.smallrecipe.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;

import com.cn.smallrecipe.MyActivity;
import com.cn.smallrecipe.R;
import com.cn.smallrecipe.Util;
import com.umeng.message.proguard.K;

import cn.com.xinfusdk.XinFuSDK;
import cn.com.xxutils.util.XXUtils;

/**
 * Created by Administrator on 2016/3/13.
 */
public class WaitActivity extends MyActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.wait);
        startActivity(new Intent(WaitActivity.this, WelcomeActivity.class));
        finish();
//        XinFuSDK xinFuSDK =XinFuSDK.getInstance();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                SystemClock.sleep(1000);
//                startActivity(new Intent(WaitActivity.this, WelcomeActivity.class));
//                finish();
//            }
//        }).start();
    }

}
