package com.cn.smallrecipe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.cn.smallrecipe.MyActivity;
import com.cn.smallrecipe.R;
import com.cn.smallrecipe.fragment.F_Friend;
import com.cn.smallrecipe.fragment.F_Home;
import com.cn.smallrecipe.fragment.F_My;
import com.cn.smallrecipe.fragment.F_Sniff;
import com.cn.smallrecipe.fragment.F_Star;

import java.util.ArrayList;
import java.util.List;

import cn.com.xxutils.adapter.XXTableFragmentAdapter;
import cn.com.xxutils.view.XXRoundImageView;

public class MainActivity extends MyActivity implements View.OnClickListener {
    private TabLayout tabs;
    private ViewPager vp;
    private XXTableFragmentAdapter adapter;
    private XXRoundImageView userLogo_main;
    private TextView userName_main;
    private boolean isLogin = false;
    public static final int ACTIVITY_REQUEST_CODE_LOGIN = 0x01;//启动登陆页面的请求码

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

    }

    private void findId() {
        tabs = (TabLayout) findViewById(R.id.tabs);
        vp = (ViewPager) findViewById(R.id.vp);
        userLogo_main = (XXRoundImageView) findViewById(R.id.userLogo_main);
        userName_main = (TextView) findViewById(R.id.userName_main);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.userLogo_main:
                Log.d(TAG, "user logo clicked,isLogin=" + isLogin);
                if (isLogin) {
                    //已经登陆
                } else {
                    //未登陆,打开登陆页面
                    startLogin();
                }
                break;
            case R.id.userName_main:
                Log.d(TAG, "user name clicked,isLogin=" + isLogin);
                if (!isLogin) {
                    //未登录,打开登陆页面
                    startLogin();
                }
                break;
        }
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
