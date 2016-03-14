package com.cn.smallrecipe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cn.smallrecipe.MyActivity;
import com.cn.smallrecipe.R;

import java.util.ArrayList;
import java.util.List;

import cn.com.xxutils.XXApplication;
import cn.com.xxutils.adapter.XXViewPagerAdapter;
import cn.com.xxutils.util.XXSharedPreferences;

/**
 * 欢迎页面
 * Created by Administrator on 2016/2/23.
 */
public class WelcomeActivity extends MyActivity implements ViewPager.OnPageChangeListener {
    private List<ImageView> imageViewList;
    private TextView tvDescription;
    private LinearLayout llPoints;
    private String[] imageDescriptions;
    private int previousSelectPosition = 0;
    private ViewPager mViewPager;
    private boolean isLoop = true;
    private int times = 1;
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);

        }
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        XXSharedPreferences sharedPreferences = new XXSharedPreferences("isFirstStart");
        String isFirstStart = String.valueOf(sharedPreferences.get(this, "isTrue", ""));
        if (isFirstStart.equals("")) {
            setContentView(R.layout.activity_splash_viewpager);
            initView();
            sharedPreferences.put(this, "isTrue", "123");
            new Thread(new Runnable() {

                @Override
                public void run() {
                    while (isLoop) {
                        SystemClock.sleep(2000);
                        if (times < imageViewList.size()) {
                            times++;
                            handler.sendEmptyMessage(0);

                        } else {
                            isLoop = false;
                            Log.d(XXApplication.TAG, "will go home");
                            startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                            WelcomeActivity.this.finish();
                        }

                    }
                }
            }).start();
        } else {
            startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
            WelcomeActivity.this.finish();
        }

    }

    public void initView() {
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        tvDescription = (TextView) findViewById(R.id.tv_image_description);
        llPoints = (LinearLayout) findViewById(R.id.ll_points);

        prepareData();

        XXViewPagerAdapter adapter = new XXViewPagerAdapter(imageViewList);
        mViewPager.setAdapter(adapter);
        mViewPager.setOnPageChangeListener(this);

        tvDescription.setText(imageDescriptions[previousSelectPosition]);
        llPoints.getChildAt(previousSelectPosition).setEnabled(true);

        /**
         * 2147483647 / 2 = 1073741820 - 1
         */
        int n = Integer.MAX_VALUE / 2 % imageViewList.size();
        int itemPosition = Integer.MAX_VALUE / 2 - n;

        mViewPager.setCurrentItem(itemPosition);
    }

    private void prepareData() {
        imageViewList = new ArrayList<ImageView>();
        int[] imageResIDs = getImageResIDs();
        imageDescriptions = getImageDescription();

        ImageView iv;
        View view;
        for (int i = 0; i < imageResIDs.length; i++) {
            iv = new ImageView(this);
            iv.setBackgroundResource(imageResIDs[i]);
            imageViewList.add(iv);

            // 添加点view对象
            view = new View(this);
            view.setBackgroundDrawable(getResources().getDrawable(R.drawable.point_background));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(5, 5);
            lp.leftMargin = 10;
            view.setLayoutParams(lp);
            view.setEnabled(false);
            llPoints.addView(view);
        }
    }

    private int[] getImageResIDs() {
        return new int[]{
                R.drawable.weclome_1,
                R.drawable.welcome_2,
                R.drawable.welcome_3,
                R.drawable.welcome_4,
                R.drawable.welcome_5
        };
    }

    private String[] getImageDescription() {
        return new String[]{
                "只需输入一个字，让您更方便搜索",
                "每份菜谱都有最详细的作业步骤哦",
                "新增朋友圈玩法，等你来试",
                "一目了然的收藏夹",
                "晒出自己的手艺，让别人为您点赞"
        };
    }


    @Override
    public void onPageScrollStateChanged(int arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPageSelected(int position) {
        // 改变图片的描述信息
        tvDescription.setText(imageDescriptions[position % imageViewList.size()]);
        // 切换选中的点
        llPoints.getChildAt(previousSelectPosition).setEnabled(false);    // 把前一个点置为normal状态
        llPoints.getChildAt(position % imageViewList.size()).setEnabled(true);        // 把当前选中的position对应的点置为enabled状态
        previousSelectPosition = position % imageViewList.size();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "WelcomeActivity was destroyed");
        super.onDestroy();

    }
}
