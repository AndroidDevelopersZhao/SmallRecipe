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
    public static final String[] keywords = {"红烧肉", "水煮鱼", "糖醋排骨",
            "糖醋里脊", "白灼虾", "北京烤鸭", "丝瓜面筋", "松鼠鱼", "酿茄子",
            "洗澡泡菜", "龙井虾仁", "糖醋鲤鱼", "鱼香茄子", "五花肉炒西兰花",
            "剁椒鱼头", "九转大肠", "清蒸咸鱼", "汤爆双脆", "奶汤蒲菜",
            "拔丝山药"
            , "红烧肉", "水煮鱼", "糖醋排骨",
            "糖醋里脊", "白灼虾", "北京烤鸭", "丝瓜面筋", "松鼠鱼", "酿茄子",
            "洗澡泡菜", "龙井虾仁", "糖醋鲤鱼", "鱼香茄子", "五花肉炒西兰花",
            "剁椒鱼头", "九转大肠", "清蒸咸鱼", "汤爆双脆", "奶汤蒲菜",
            "拔丝山药"};
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
