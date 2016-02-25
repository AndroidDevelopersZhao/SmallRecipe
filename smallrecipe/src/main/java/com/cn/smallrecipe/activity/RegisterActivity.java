package com.cn.smallrecipe.activity;

import android.os.Bundle;
import android.util.Log;

import com.cn.smallrecipe.MyActivity;
import com.cn.smallrecipe.R;

/**
 * Created by Administrator on 2016/2/25.
 */
public class RegisterActivity extends MyActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "inter register activity");
        setContentView(R.layout.activity_register);
    }
}
