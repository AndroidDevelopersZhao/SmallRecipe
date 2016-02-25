package com.cn.smallrecipe.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

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

public class MainActivity extends MyActivity {
    private TabLayout tabs;
    private ViewPager vp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        tabs = (TabLayout) findViewById(R.id.tabs);
        vp = (ViewPager) findViewById(R.id.vp);
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
        XXTableFragmentAdapter adapter = new XXTableFragmentAdapter(getSupportFragmentManager(), strs, fragments);
        vp.setAdapter(adapter);
        tabs.setupWithViewPager(vp);
        tabs.setTabsFromPagerAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
