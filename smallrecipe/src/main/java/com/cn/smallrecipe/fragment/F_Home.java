package com.cn.smallrecipe.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cn.smallrecipe.ParentFragment;
import com.cn.smallrecipe.R;
import com.cn.smallrecipe.activity.SearchActivity;

import java.util.Random;

import cn.com.xxutils.util.XXSharedPreferences;
import cn.com.xxutils.view.XXKeywordsFlow;

/**
 * Created by Administrator on 2016/2/23.
 */
public class F_Home extends ParentFragment implements View.OnClickListener {
    private View view;
    private AutoCompleteTextView et_search;
    XXSharedPreferences sharedPreferences = null;//定义保存/获取提示框的text
    private ImageView bt_search;
    private static final String SAVE_TEXT_NAME = "saveText";
    private static final String KEY_SAVE_TEST = "text";
    private ArrayAdapter<String> adapter;
    private XXKeywordsFlow view_home;
    public static final String[] keywords = {"四角蛋包饭", "薯香蛋饼", "水煮白菜", "糖醋莲子藕",
            "蒜蓉棒棒糖", "香辣双丝", "杂粮鲑鱼饭团", "牛油果素食pizza", "素蒸五彩豆皮卷",
            "袖珍版小披萨", "冰爽捞汁菜", "老醋花生仁", "凉拌蕨根粉", "针菇拌干丝",
            "香辣皮蛋拌豆腐", "香甜草莓可可卷", "香橙瑞士卷", "香芋芝士蛋糕", "香橙果仁蛋糕",
            "瓜子酥", "方便面蛋葱饼", "四川凉面", "红烧肉", "水煮鱼", "糖醋排骨",
            "糖醋里脊", "白灼虾", "北京烤鸭", "松鼠鱼", "酿茄子", "糖醋鲤鱼",
            "鱼香茄子", "五花肉炒西兰花", "剁椒鱼头", "九转大肠", "泰式柠檬蒸鲈鱼",
            "海米烧冬瓜", "糖醋小排", "秘制红烧肉", "经典红烧肉", "茄汁牛肉面",
            "日式咖喱炒面", "鸡蛋火腿面", "炸酱面", "蚝油茄汁素炒面", "杂蔬肉丝炒面",
            "肉丝面", "手擀面", "什锦拌面", "鸡蛋西红柿拌面", "茄子肉酱干拌面", "意大利面",
            "花生酱拌意面", "洋葱火腿炒面", "甜面酱香菇猪蹄", "意面酱", "面条葱花饼",
            "泰式柠檬蒸鲈鱼", "海米烧冬瓜", "苦瓜煎蛋", "白菜烩脆皮豆腐", "椒盐敲扁迷你土豆",
            "酸甜排骨", "香菇鸡肉粥", "豆豉蒸排骨", "原只南瓜蒸排骨", "田园小南瓜", "三明治",
            "圆白菜蔬菜卷", "麻辣鸡丁"};
    private boolean isRestText = true;//是否不断更换显示文字
    private Thread thread_resetText;

    /**
     * 加载
     */
    @Override
    public void onResume() {
        isRestText = true;
        super.onResume();
        Log.d(TAG, "onResume--isRestText：" + isRestText);
        //开启线程更换显示的内容
        thread_resetText = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRestText) {
                    SystemClock.sleep(10000);
                    //更换
                    if (F_Home.this.isVisible() && isRestText)
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            synchronized public void run() {
                                if (F_Home.this.isVisible())
                                    resetTextWithOut();
                            }
                        });
                }
            }
        });
        thread_resetText.start();
    }

    /**
     * 暂停
     */
    @Override
    public void onPause() {
        isRestText = false;
        super.onPause();
        Log.d(TAG, "onPause--isRestText：" + isRestText);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, null);
        initView();
        return view;

    }


    private void initView() {
        et_search = (AutoCompleteTextView) view.findViewById(R.id.et_search);
        initAutoComplete(et_search);//为EditText设置输入提示
        bt_search = (ImageView) view.findViewById(R.id.bt_search);
        bt_search.setOnClickListener(this);
        view_home = (XXKeywordsFlow) view.findViewById(R.id.view_home);
        initXXKeyVordsFlow(view_home);
    }

    synchronized private void resetTextWithOut() {
        Log.d(TAG, "Reset text at home view");
        view_home.rubKeywords();
        // keywordsFlow.rubAllViews();
        view_home.setDuration(5000);
        feedKeywordsFlow(view_home, keywords);
        view_home.go2Show(view_home.ANIMATION_OUT);

    }

    private void initXXKeyVordsFlow(XXKeywordsFlow view_home) {
//        view_home.setDuration(8000);
        view_home.setOnItemClickListener(this);
        // 添加
        feedKeywordsFlow(view_home, keywords);
        view_home.go2Show(view_home.ANIMATION_OUT);
    }

    private void feedKeywordsFlow(XXKeywordsFlow keywordsFlow, String[] arr) {
        Random random = new Random();
        for (int i = 0; i < XXKeywordsFlow.MAX; i++) {
            int ran = random.nextInt(arr.length);
            String tmp = arr[ran];
            keywordsFlow.feedKeyword(tmp);
        }
    }

    /**
     * 为EditText设置输入提示
     *
     * @param et_search
     */
    private void initAutoComplete(AutoCompleteTextView et_search) {
        sharedPreferences = new XXSharedPreferences(SAVE_TEXT_NAME);
        String longhistory = String.valueOf(sharedPreferences.get(getActivity(), KEY_SAVE_TEST, ""));
        String[] hisArrays = longhistory.split(",");
        adapter = new ArrayAdapter<String>(getActivity(),
                R.layout.autocomplttext, hisArrays);
        //只保留最近的50条的记录
        if (hisArrays.length > 50) {
            String[] newArrays = new String[50];
            System.arraycopy(hisArrays, 0, newArrays, 0, 50);
            adapter = new ArrayAdapter<String>(getActivity(),
                    R.layout.autocomplttext, newArrays);
        }
        et_search.setAdapter(adapter);
//        et_search.setDropDownHeight(340);
        et_search.setThreshold(1);
        et_search.setCompletionHint("您最近的搜索记录");
//        auto.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                AutoCompleteTextView view = (AutoCompleteTextView) v;
//                if (hasFocus) {
//                    view.showDropDown();
//                }
//            }
//        });
    }

    @Override
    public void onClick(View v) {

        if (v instanceof TextView && !((TextView) v).getText().toString().trim().equals("搜索")) {
            String keyword = ((TextView) v).getText().toString();
            StringBuffer sb = new StringBuffer();
            keyword = et_search.getText().toString().trim().equals("")
                    ? keyword : sb.append(et_search.getText().toString().trim()).append(keyword).toString();
            et_search.setText(keyword);
        }
        switch (v.getId()) {

            case R.id.bt_search:
                String text = et_search.getText().toString().trim();
                String text_sharedPreferences = String.valueOf(sharedPreferences.get(getActivity(), KEY_SAVE_TEST, ""));
                StringBuffer sb = new StringBuffer();
                boolean isInsert = true;
                if (text != null && !text.equals("")) {
                    String[] ss = text_sharedPreferences.split(",");
                    for (int i = 0; i < ss.length; i++) {
                        if (text.equals(ss[i])) {
                            isInsert = false;
                        }
                    }
                    if (isInsert) {
                        if (text_sharedPreferences.equals("") || text_sharedPreferences.equals(",")) {
                            sb.append(text);
                        } else {

                            sb.append(text_sharedPreferences).append(",").append(text);
                        }
                        sharedPreferences.put(getActivity(), KEY_SAVE_TEST, sb.toString());
                        initAutoComplete(et_search);
                    }
                    Intent intent = new Intent(getActivity(), SearchActivity.class);
                    intent.putExtra(SearchActivity.SEARCH_KEY, text);
                    et_search.setText("");
                    startActivity(intent);
                }

                break;


        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "F_Home destroyed");
        super.onDestroy();
    }
}
