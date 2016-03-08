package com.cn.smallrecipe;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.tencent.tauth.Tencent;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.MsgConstant;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengRegistrar;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * Created by Administrator on 2016/2/23.
 */
public class MyActivity extends AppCompatActivity {
    public static final String TAG = "SmallRecipe";
    public static boolean LOGIN_STATE = false;//登陆状态，默认为false
    public static Tencent TENCENT = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "The parent class is inherited one times," + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        super.onCreate(savedInstanceState);

        PushAgent mPushAgent = PushAgent.getInstance(this);
        mPushAgent.setDebugMode(true);
        mPushAgent.setNotificationPlaySound(MsgConstant.NOTIFICATION_PLAY_SDK_ENABLE);//sdk开启通知声音
        mPushAgent.onAppStart();
        mPushAgent.enable(new IUmengRegisterCallback() {
            @Override
            public void onRegistered(String s) {
                Log.w(TAG, "推送回调：" + s);
            }
        });
//        PushAgent.getInstance(this).onAppStart();
        String device_token = UmengRegistrar.getRegistrationId(this);
        Log.w(TAG, "友盟推送已成功开启");

    }

}
