package com.cn.smallrecipe;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.tencent.tauth.Tencent;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * Created by Administrator on 2016/2/23.
 */
public class MyActivity extends AppCompatActivity {
    public static final String TAG = "SmallRecipe";
    public static boolean LOGIN_STATE =false;//登陆状态，默认为false
    public static Tencent TENCENT=null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "The parent class is inherited one times," + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        super.onCreate(savedInstanceState);


    }

}
