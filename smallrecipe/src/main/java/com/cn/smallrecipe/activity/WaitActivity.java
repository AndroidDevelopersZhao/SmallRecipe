package com.cn.smallrecipe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;

import com.cn.smallrecipe.MyActivity;
import com.cn.smallrecipe.R;

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
