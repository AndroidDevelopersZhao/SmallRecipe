package com.cn.smallrecipe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cn.smallrecipe.MyActivity;
import com.cn.smallrecipe.R;
import com.cn.smallrecipe.Util;
import com.cn.smallrecipe.datainfo.register.ResultToApp;
import com.cn.smallrecipe.fragment.F_Friend;
import com.cn.smallrecipe.fragment.F_Home;
import com.cn.smallrecipe.fragment.F_My;
import com.cn.smallrecipe.fragment.F_Sniff;
import com.cn.smallrecipe.fragment.F_Star;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import cn.com.xxutils.adapter.XXTableFragmentAdapter;
import cn.com.xxutils.alerterview.OnItemClickListener;
import cn.com.xxutils.alerterview.XXAlertView;
import cn.com.xxutils.util.XXHttpClient;
import cn.com.xxutils.util.XXSharedPreferences;
import cn.com.xxutils.util.XXUtils;
import cn.com.xxutils.view.XXRoundImageView;

public class MainActivity extends MyActivity implements View.OnClickListener {
    private TabLayout tabs;
    private ViewPager vp;
    private XXTableFragmentAdapter adapter;
    private XXRoundImageView userLogo_main;
    private TextView userName_main;
    private boolean isLogin = false;
    public static final int ACTIVITY_REQUEST_CODE_LOGIN = 0x01;//启动登陆页面的请求码
    public static final String SHAREDSESSIONIDSAVEEDNAME = "sessionId";
    private XXSharedPreferences sharedPreferences_sessionId = null;//存储在本地的sessionid

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        findId();
        initData();
        setData();
        setListener();
    }

    private void setListener() {
        userLogo_main.setOnClickListener(this);
        userName_main.setOnClickListener(this);
    }

    private void setData() {
        vp.setAdapter(adapter);
        tabs.setupWithViewPager(vp);
        tabs.setTabsFromPagerAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void initData() {
        List<Fragment> fragments = new ArrayList<>();

        List<String> strs = new ArrayList<>();
        strs.add("首页");
        strs.add("秀色可餐");
        strs.add("我的收藏");
        strs.add("朋友圈");
        strs.add("个人中心");

        fragments.add(new F_Home());
        fragments.add(new F_Sniff());
        fragments.add(new F_Star());
        fragments.add(new F_Friend());
        fragments.add(new F_My());
        adapter = new XXTableFragmentAdapter(getSupportFragmentManager(), strs, fragments);
        sharedPreferences_sessionId = new XXSharedPreferences(SHAREDSESSIONIDSAVEEDNAME);
        String file_session = String.valueOf(sharedPreferences_sessionId.get(this, "sessionid", ""));
        if (file_session != null && !file_session.equals("")) {
            MyActivity.LOGIN_STATE = true;
            //获取该用户对应的头像、昵称等个人信息
            Log.d(TAG, "系统检测到该用户已经登陆，保存的用户名：" + sharedPreferences_sessionId.get(MainActivity.this, "usernumber", "") + ",sessionId=" + sharedPreferences_sessionId.get(MainActivity.this, "sessionid", ""));
            setUserConfig();
            AuthUserInfo(String.valueOf(sharedPreferences_sessionId.get(MainActivity.this, "usernumber", "")),
                    String.valueOf(sharedPreferences_sessionId.get(MainActivity.this, "sessionid", "")));
        } else {
            MyActivity.LOGIN_STATE = false;
        }
    }

    /**
     * 验证用户身份是否可用
     *
     * @param usernumber
     * @param sessionid
     */
    private Handler handler_authSessionId = null;

    private void AuthUserInfo(final String usernumber, final String sessionid) {
        handler_authSessionId = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case -1:
                        new XXAlertView("提示", "登陆状态失效，请重新登陆，" + msg.getData().getString("data"), "重新登陆", new String[]{"稍后再试"}, null, MainActivity.this,
                                XXAlertView.Style.Alert, new OnItemClickListener() {
                            @Override
                            public void onItemClick(Object o, int position) {
                                Log.d(TAG, "position:" + position);
                                if (position == -1) {
                                    //重新登陆
                                    startActivityForResult(new Intent(MainActivity.this, LoginActivity.class), 0x01);
                                } else {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            SystemClock.sleep(5000);
                                            AuthUserInfo(usernumber, sessionid);
                                        }
                                    }).start();
                                }
                            }
                        }).show();
                        break;

                    case 1:
//                        Toast.makeText(MainActivity.this, "系统已为您自动登陆成功", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };


        XXHttpClient client = new XXHttpClient(Util.URL_SERVICE_AUTH_SESSIONID, true, new XXHttpClient.XXHttpResponseListener() {
            @Override
            public void onSuccess(int i, byte[] bytes) {
                Log.d(TAG, "验证Auth的返回，" + new String(bytes));
                ResultToApp resultToApp = new Gson().fromJson(new String(bytes), ResultToApp.class);
                if (resultToApp.getErrorCode() == 9000) {
                    Util.sendMsgToHandler(handler_authSessionId, "验证成功，状态有效", true);
                } else {
                    Util.sendMsgToHandler(handler_authSessionId, resultToApp.getResultMsg(), false);
                }
            }

            @Override
            public void onError(int i, Throwable throwable) {
                Log.e(TAG, "验证Auth网络异常");
                Util.sendMsgToHandler(handler_authSessionId, "验证Auth失败，网络异常", false);
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {

            }
        });

        client.put("usernumber", usernumber);
        client.put("sessionid", sessionid);
        client.doPost(15000);
        Log.d(TAG, "上送的验证sessionif数据,usernumber=" + usernumber + ",sessionid=" + sessionid);
    }

    private void findId() {
        tabs = (TabLayout) findViewById(R.id.tabs);
        vp = (ViewPager) findViewById(R.id.vp);
        userLogo_main = (XXRoundImageView) findViewById(R.id.userLogo_main);
        userName_main = (TextView) findViewById(R.id.userName_main);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        sharedPreferences_sessionId=new XXSharedPreferences(MainActivity.SHAREDSESSIONIDSAVEEDNAME);
        String file_session = String.valueOf(sharedPreferences_sessionId.get(this, "sessionid", ""));
        if (file_session != null && !file_session.equals("")) {
            MyActivity.LOGIN_STATE = true;
            //获取该用户对应的头像、昵称等个人信息
            Log.d(TAG, "系统检测到该用户已经登陆，保存的用户名：" + sharedPreferences_sessionId.get(MainActivity.this, "usernumber", "") + ",sessionId=" + sharedPreferences_sessionId.get(MainActivity.this, "sessionid", ""));
            setUserConfig();
            AuthUserInfo(String.valueOf(sharedPreferences_sessionId.get(MainActivity.this, "usernumber", "")),file_session);
        } else {
            MyActivity.LOGIN_STATE = false;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            new XXAlertView("提示", "您确定要退出吗？", "退出登陆", new String[]{"取消"}, null, MainActivity.this,
                    XXAlertView.Style.Alert, new OnItemClickListener() {
                @Override
                public void onItemClick(Object o, int position) {
                    Log.d(TAG, "position:" + position);
                    if (position == -1) {
                        //注销登陆
                        finish();
                    }
                }
            }).show();
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.userLogo_main:
                Log.d(TAG, "user logo clicked,isLogin=" + MyActivity.LOGIN_STATE);
                if (MyActivity.LOGIN_STATE) {
                    //已经登陆

                } else {
                    //未登陆,打开登陆页面
                    startLogin();
                }
                break;
            case R.id.userName_main:
                Log.d(TAG, "user name clicked,isLogin=" + MyActivity.LOGIN_STATE);
                if (!MyActivity.LOGIN_STATE) {
                    //未登录,打开登陆页面
                    startLogin();
                }
                break;
        }
    }

    private void setUserConfig() {
        XXSharedPreferences sharedPreferences = new XXSharedPreferences(MainActivity.SHAREDSESSIONIDSAVEEDNAME);
        String username = String.valueOf(sharedPreferences.get(MainActivity.this, "username", ""));
        String userid = String.valueOf(sharedPreferences.get(MainActivity.this, "userid", ""));
        String userlogo_url = String.valueOf(sharedPreferences.get(MainActivity.this, "userlogo", ""));
        userName_main.setText(username);
//        XXUtils.bitmapToBytes()
    }

    private void startLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivityForResult(intent, ACTIVITY_REQUEST_CODE_LOGIN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "OnActivityResult-MainActivity,requestCode:" + requestCode + ",resultCode:" + resultCode + ",data:" + data);

        if (requestCode == ACTIVITY_REQUEST_CODE_LOGIN) {

            Log.d(TAG, "back from login activity");
        }
    }
}
