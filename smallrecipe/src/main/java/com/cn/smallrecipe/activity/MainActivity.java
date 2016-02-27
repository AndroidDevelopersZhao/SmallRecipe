package com.cn.smallrecipe.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Base64;
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

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import cn.com.xxutils.adapter.XXTableFragmentAdapter;
import cn.com.xxutils.alerterview.OnItemClickListener;
import cn.com.xxutils.alerterview.XXAlertView;
import cn.com.xxutils.util.XXHttpClient;
import cn.com.xxutils.util.XXImagesLoader;
import cn.com.xxutils.util.XXSharedPreferences;
import cn.com.xxutils.util.XXUtils;
import cn.com.xxutils.view.XXRoundImageView;

public class MainActivity extends MyActivity implements View.OnClickListener {
    private TabLayout tabs;
    private ViewPager vp;
    private XXTableFragmentAdapter adapter;
    private XXRoundImageView userLogo_main;
    private TextView userName_main;
    public static final int ACTIVITY_REQUEST_CODE_LOGIN = 0x01;//启动登陆页面的请求码
    public static final String SHAREDSESSIONIDSAVEEDNAME = "sessionId";
    private XXSharedPreferences sharedPreferences_sessionId = null;//存储在本地的sessionid
    private Handler handler_authSessionId = null;//验证用户登陆状态
    private Handler handler_getUserLogo = null;//获取用户头像
    private String sessionid_file = null;//保存在共享参数的sessionid
    private String usernumber_file = null;//保存在共享参数的usernumber
    private String username_file = null;//保存在共享参数的username
    private String userid_file = null;//保存在共享参数的userid
    private String userlogourl_file = null;//保存在共享参数的userlogourl

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

    //设置监听
    private void setListener() {
        userLogo_main.setOnClickListener(this);
        userName_main.setOnClickListener(this);
    }

    /**
     * 将数据填充到adapter
     */
    private void setData() {
        vp.setAdapter(adapter);
        tabs.setupWithViewPager(vp);
        tabs.setTabsFromPagerAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    /**
     * 初始化数据
     */
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
        sessionid_file = String.valueOf(sharedPreferences_sessionId.get(this, "sessionid", ""));
        usernumber_file = String.valueOf(sharedPreferences_sessionId.get(this, "usernumber", ""));
        Log.d(TAG,"读取到本地存储的usernumber="+usernumber_file);
        if (sessionid_file != null && !sessionid_file.equals("")) {
            MyActivity.LOGIN_STATE = true;
            //获取该用户对应的头像、昵称等个人信息
            AuthUserInfo(usernumber_file, sessionid_file);//验证该用户的登陆状态是否有效
        } else {
            MyActivity.LOGIN_STATE = false;
        }
    }

    /**
     * 验证用户身份是否可用
     *
     * @param usernumber 用户账号
     * @param sessionid  sessionid
     */
    private void AuthUserInfo(final String usernumber, final String sessionid) {
        Log.d(TAG, "传入验证的usernumber=" + usernumber + "\nsessionid=" + sessionid);

        handler_authSessionId = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case -1:
                        //登陆状态失效
                        sharedPreferences_sessionId.clear(MainActivity.this);
                        MainActivity.LOGIN_STATE = false;
                        userLogo_main.setImageResource(R.drawable.userlogodefult);
                        userName_main.setText("点我登陆");
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
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    AuthUserInfo(usernumber, sessionid);
                                                }
                                            });
                                        }
                                    }).start();
                                }
                            }
                        }).show();
                        break;

                    case 1:
                        MainActivity.LOGIN_STATE = true;
                        setUserConfig();//根据登陆页面保存的用户参数设置用户信息到页面
                        getUserLogo(userlogourl_file);//登陆状态有效时获取用户头像
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

    //寻找ID
    private void findId() {
        tabs = (TabLayout) findViewById(R.id.tabs);
        vp = (ViewPager) findViewById(R.id.vp);
        userLogo_main = (XXRoundImageView) findViewById(R.id.userLogo_main);
        userName_main = (TextView) findViewById(R.id.userName_main);
    }

    @Override
    protected void onRestart() {
        sharedPreferences_sessionId = new XXSharedPreferences(MainActivity.SHAREDSESSIONIDSAVEEDNAME);
        sessionid_file = String.valueOf(sharedPreferences_sessionId.get(this, "sessionid", ""));
        usernumber_file=String.valueOf(sharedPreferences_sessionId.get(this, "usernumber", ""));
        if (sessionid_file != null && !sessionid_file.equals("")) {
            MyActivity.LOGIN_STATE = true;
            //获取该用户对应的头像、昵称等个人信息
//            Log.d(TAG, "系统检测到该用户已经登陆，保存的用户名：" + sharedPreferences_sessionId.get(MainActivity.this, "usernumber", "") + ",sessionId=" + sharedPreferences_sessionId.get(MainActivity.this, "sessionid", ""));
            AuthUserInfo(usernumber_file, sessionid_file);
        } else {
            MyActivity.LOGIN_STATE = false;
        }
        super.onRestart();
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

    /**
     * 获取用户头像并设置到view
     *
     * @param url_userlogo
     */
    private void getUserLogo(String url_userlogo) {
        handler_getUserLogo = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case -1:
                        Toast.makeText(MainActivity.this, "用户头像为默认头像，该消息将在您设置完自定义头像后不再提示", Toast.LENGTH_LONG).show();
                        userLogo_main.setImageResource(R.drawable.userlogodefult);
                        break;
                    case 1:
                        byte[] bytes = msg.getData().getByteArray("data");
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        userLogo_main.setImageBitmap(bitmap);
                        break;
                }
            }
        };
        Log.d(TAG, "请求的用户头像url=" + url_userlogo);
        XXHttpClient client = new XXHttpClient(url_userlogo, true, new XXHttpClient.XXHttpResponseListener() {
            @Override
            public void onSuccess(int i, final byte[] bytes) {
                Log.d(TAG, "用户头像获取成功");
                Util.sendMsgToHandler(handler_getUserLogo, bytes, true);
            }

            @Override
            public void onError(int i, Throwable throwable) {
                Log.e(TAG, "网络异常");
                Util.sendMsgToHandler(handler_getUserLogo, "网络异常", false);
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {

            }
        });
        client.doPost(15000);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.userLogo_main:
                Log.d(TAG, "user logo clicked,isLogin=" + MyActivity.LOGIN_STATE);
                if (MyActivity.LOGIN_STATE) {
                    //已经登陆
                    startActivity(new Intent(MainActivity.this, PersonalActivity.class));
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
                }else {
                    //已经登陆
                    startActivity(new Intent(MainActivity.this, PersonalActivity.class));
                }
                break;
        }
    }

    /**
     * 从缓存文件设置用户数据
     */
    private void setUserConfig() {
        //由于调用该方法有可能是在restart生命周期，所以需要重新获取缓存数据
        sharedPreferences_sessionId = new XXSharedPreferences(MainActivity.SHAREDSESSIONIDSAVEEDNAME);

        sessionid_file = sharedPreferences_sessionId.get(this, "sessionid", "").toString();
        usernumber_file = sharedPreferences_sessionId.get(this, "usernumber", "").toString();
        username_file = sharedPreferences_sessionId.get(this, "username", "").toString();
        userid_file = sharedPreferences_sessionId.get(this, "userid", "").toString();
        userlogourl_file = sharedPreferences_sessionId.get(this, "userlogo", "").toString();

        Log.d(TAG, "检测到用户登陆缓存数据：\n用户账号：" + usernumber_file
                + "\nsessionid：" + sessionid_file
                + "\nusername:" + username_file
                + "\nuserid:" + userid_file
                + "\nuserlogorl:" + userlogourl_file);
        userName_main.setText(username_file);//用户名设置到界面
    }

    private void startLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivityForResult(intent, ACTIVITY_REQUEST_CODE_LOGIN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "OnActivityResult-MainActivity,requestCode:" + requestCode + ",resultCode:" + resultCode + ",data:" + data);

        if (requestCode == ACTIVITY_REQUEST_CODE_LOGIN) {
//            Toast.makeText(MainActivity.this, "恭喜您登陆成功", Toast.LENGTH_LONG).show();
//            if (data != null) {
//                Toast.makeText(MainActivity.this, "恭喜您获得靓号" + data.getStringExtra("data"), Toast.LENGTH_LONG).show();
//            }
            Log.d(TAG, "back from login activity");
        }
    }
}
