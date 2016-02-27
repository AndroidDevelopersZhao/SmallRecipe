package com.cn.smallrecipe.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cn.smallrecipe.MyActivity;
import com.cn.smallrecipe.ParentFragment;
import com.cn.smallrecipe.R;
import com.cn.smallrecipe.Util;
import com.cn.smallrecipe.activity.LoginActivity;
import com.cn.smallrecipe.activity.MainActivity;
import com.cn.smallrecipe.activity.PersonalActivity;
import com.cn.smallrecipe.datainfo.myinfo.MyInfos;
import com.cn.smallrecipe.datainfo.myinfo.MyInfosTitle;
import com.cn.smallrecipe.datainfo.register.ResultToApp;
import com.google.gson.Gson;

import cn.com.xxutils.adapter.XXListViewAdapter;
import cn.com.xxutils.alerterview.OnItemClickListener;
import cn.com.xxutils.alerterview.XXAlertView;
import cn.com.xxutils.util.XXHttpClient;
import cn.com.xxutils.util.XXSharedPreferences;
import cn.com.xxutils.view.XXListView;

/**
 * //TODO 个人中心
 * Created by Administrator on 2016/2/24.
 */
public class F_My extends ParentFragment{
    private View view;
    private XXListView listView1, listView2, listView3;
    private XXListViewAdapter<MyInfosTitle> adapter1 = null;
    private XXListViewAdapter<MyInfos> adapter2 = null;
    private XXListViewAdapter<MyInfos> adapter3 = null;

    private String[] text = {"我的动态", "我的收藏", "扫一扫",
            "晒厨艺"};
    private int[] ImageIds = {R.drawable.icon_userprofile, R.drawable.icon_like, R.drawable.icon_search,
            R.drawable.icon_photo};


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.f_my, null);
        initView();

        return view;
    }

    private void initView() {
        listView1 = (XXListView) view.findViewById(R.id.listview_myinfo_title_1);
        listView2 = (XXListView) view.findViewById(R.id.listview_myinfo_title);
        listView3 = (XXListView) view.findViewById(R.id.listview_myinfo_title_2);


        initAdapter1();
        initAdapter2();
        initAdapter3();

        listView1.setAdapter(adapter1);
        listView2.setAdapter(adapter2);
        listView3.setAdapter(adapter3);


        addDataToAdpter1();
        addDataToAdpter2();
        addDataToAdpter3();

        listView1.setOnItemClickListener(listview_1_listener);

        setData();
    }

    AdapterView.OnItemClickListener listview_1_listener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.d(TAG, "position:" + position);
            if (position == 0) {
                if (MyActivity.LOGIN_STATE) {
                    //登陆状态时，打开个人中心页面
                    startActivity(new Intent(getActivity(), PersonalActivity.class));
                } else {
                    new XXAlertView("提示", "您还未登陆，是否登陆", "取消", null, new String[]{"登陆"},
                            getActivity(), XXAlertView.Style.Alert, new OnItemClickListener() {
                        @Override
                        public void onItemClick(Object o, int position) {
                            Log.d(TAG, "position:" + position);

                        }
                    }).show();
                }
            }
        }
    };

    private void setData() {
        XXSharedPreferences sharedPreferences = new XXSharedPreferences(MainActivity.SHAREDSESSIONIDSAVEEDNAME);
        String usernumber = String.valueOf(sharedPreferences.get(getActivity(), "usernumber", ""));
        String userid = String.valueOf(sharedPreferences.get(getActivity(), "userid", ""));
        String username = String.valueOf(sharedPreferences.get(getActivity(), "username", ""));
        String sessionid = String.valueOf(sharedPreferences.get(getActivity(), "sessionid", ""));
        if (MyActivity.LOGIN_STATE) {

            adapter1.removeAll();
            adapter1.addItem(new MyInfosTitle(R.drawable.userlogodefult, username,
                    R.drawable.icon_my_title, userid));
            adapter1.notifyDataSetChanged();
        }
//        AuthUserInfo(usernumber, sessionid);
    }

    private void addDataToAdpter3() {
        adapter3.addItem(new MyInfos(R.drawable.icon_comment, "反馈信息", R.drawable.icon_my_title));
        adapter3.addItem(new MyInfos(R.drawable.icon_setting, "设置", R.drawable.icon_my_title));
        adapter3.notifyDataSetChanged();
    }

    private void initAdapter3() {
        adapter3 = new XXListViewAdapter<MyInfos>(getActivity(), R.layout.item_listview_myinfo) {
            @Override
            public void initGetView(int position, View convertView, ViewGroup parent) {
                ImageView imageView_left = (ImageView) convertView.findViewById(R.id.item_iv_myinfo_left);
                ImageView imageView_right = (ImageView) convertView.findViewById(R.id.item_iv_myinfo_right);
                TextView text = (TextView) convertView.findViewById(R.id.item_tv_myinfo);

                imageView_left.setImageResource(getItem(position).getImage_left());
                imageView_right.setImageResource(getItem(position).getImage_right());
                text.setText(getItem(position).getText());

            }
        };
    }

    private void initAdapter2() {
        adapter2 = new XXListViewAdapter<MyInfos>(getActivity(), R.layout.item_listview_myinfo) {
            @Override
            public void initGetView(int position, View convertView, ViewGroup parent) {
                ImageView imageView_left = (ImageView) convertView.findViewById(R.id.item_iv_myinfo_left);
                ImageView imageView_right = (ImageView) convertView.findViewById(R.id.item_iv_myinfo_right);
                TextView text = (TextView) convertView.findViewById(R.id.item_tv_myinfo);

                imageView_left.setImageResource(getItem(position).getImage_left());
                imageView_right.setImageResource(getItem(position).getImage_right());
                text.setText(getItem(position).getText());

            }
        };
    }

    private void addDataToAdpter2() {
        for (int i = 0; i < ImageIds.length; i++) {
            adapter2.addItem(new MyInfos(ImageIds[i], text[i], R.drawable.icon_my_title));
        }
        adapter2.notifyDataSetChanged();

    }

    private void addDataToAdpter1() {
        adapter1.addItem(new MyInfosTitle(R.drawable.userlogodefult, "未登录", R.drawable.icon_my_title, ""));
        adapter1.notifyDataSetChanged();

    }

    private void initAdapter1() {
        adapter1 = new XXListViewAdapter<MyInfosTitle>(getActivity(), R.layout.item_myinfo_title) {
            @Override
            public void initGetView(int position, View convertView, ViewGroup parent) {
                ImageView imageView_left = (ImageView) convertView.findViewById(R.id.item_iv_left_title_1);
                ImageView imageView_right = (ImageView) convertView.findViewById(R.id.item_iv_right_title_1);
                TextView text = (TextView) convertView.findViewById(R.id.item_tv_title_1);
                TextView id = (TextView) convertView.findViewById(R.id.item_tv_id_title_1);

                imageView_left.setImageResource(getItem(position).getImage_left());
                imageView_right.setImageResource(getItem(position).getImage_right());
                text.setText(getItem(position).getText());
                id.setText(getItem(position).getId());

            }
        };
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
                        new XXAlertView("提示", "登陆状态失效，请重新登陆，" + msg.getData().getString("data"), "重新登陆", new String[]{"稍后再试"}, null, getActivity(),
                                XXAlertView.Style.Alert, new OnItemClickListener() {
                            @Override
                            public void onItemClick(Object o, int position) {
                                Log.d(TAG, "position:" + position);
                                if (position == -1) {
                                    //重新登陆
                                    startActivityForResult(new Intent(getActivity(), LoginActivity.class), 0x01);
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
                        Toast.makeText(getActivity(), "系统已为您自动登陆成功", Toast.LENGTH_SHORT).show();
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
}
