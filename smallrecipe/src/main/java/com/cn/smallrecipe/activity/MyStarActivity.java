package com.cn.smallrecipe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.cn.smallrecipe.MyActivity;
import com.cn.smallrecipe.R;
import com.cn.smallrecipe.Util;
import com.cn.smallrecipe.datainfo.mystarinfo.Data_DataNum;
import com.cn.smallrecipe.datainfo.mystarinfo.Data_GetUserStarRecipe;
import com.cn.smallrecipe.datainfo.search.AllInfo;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.com.xxutils.XXApplication;
import cn.com.xxutils.adapter.XXSlideAdapter;
import cn.com.xxutils.interfac.ItemDeleteButtonOnTochListener;
import cn.com.xxutils.progress.XXSVProgressHUD;
import cn.com.xxutils.util.XXHttpClient;
import cn.com.xxutils.util.XXListViewAnimationMode;
import cn.com.xxutils.util.XXSharedPreferences;
import cn.com.xxutils.view.MessageItem;
import cn.com.xxutils.view.XXListViewCompat;

/**
 * Created by Administrator on 2016/3/10.
 */
public class MyStarActivity extends MyActivity implements ItemDeleteButtonOnTochListener {
    private XXListViewCompat lv_mystar;
    private XXSlideAdapter adapter;
    private Handler handler_getRecipeIDFromService = null;
    private XXSharedPreferences saveUsers = null;
    private String usernumber = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mystar);
        getData();
        initView();
        Log.w(TAG, "收藏页面初始化完成，开始获取该用户收藏的菜谱ID");
        handler_getRecipeIDFromService = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case -1:
                        dissmiss();
                        Toast.makeText(MyStarActivity.this, msg.getData().getString("data"), Toast.LENGTH_SHORT).show();
                        break;

                    case 1:
                        Data_GetUserStarRecipe recipe = (Data_GetUserStarRecipe) msg.getData().getSerializable("data");
                        for (int i = 0; i < recipe.getIds().length; i++) {
                            Log.w(TAG, "获取id成功：" + recipe.getIds()[i].getId());
                        }
                        getDataFromJUHE(recipe.getIds());
                        break;
                }
            }
        };
        getRecipeID(usernumber);
    }

    /**
     * 从聚合获取该菜谱的所有信息
     *
     * @param ids
     */
    private void getDataFromJUHE(Data_DataNum[] ids) {
        final Handler h = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                dissmiss();
                switch (msg.what) {
                    case -1:

                        Toast.makeText(MyStarActivity.this, msg.getData().getString("data"), Toast.LENGTH_SHORT).show();
                        break;

                    case 1:
                        AllInfo allInfo = (AllInfo) msg.getData().getSerializable("data");
                        Log.w(TAG, "根id索引的聚合返回数据：" + allInfo.getResult().getData().get(0).toString());
                        MessageItem item = new MessageItem();
                        item.img = allInfo.getResult().getData().get(0).getAlbums().get(0);
                        item.msg = "点击查看详情";
                        item.title = allInfo.getResult().getData().get(0).getTitle();
                        item.time = "侧滑取消收藏";
                        item.iconRes = Integer.valueOf(allInfo.getResult().getData().get(0).getId());
                        adapter.addItem(item);
                        adapter.notifyDataSetChanged();
                        break;
                }
            }
        };
        for (final Data_DataNum b : ids) {
            XXHttpClient client = new XXHttpClient(Util.URL_GETRECIPEDETAILS_JUHE, true, new XXHttpClient.XXHttpResponseListener() {
                @Override
                public void onSuccess(int i, byte[] bytes) {
                    Log.d(TAG, "******************" + new String(bytes));
                    if (bytes.length > 0) {

                        AllInfo allInfo = new Gson().fromJson(new String(bytes), AllInfo.class);
                        if (allInfo.getError_code() == 0) {
                            Util.sendMsgToHandler(h, allInfo, true);
                        } else {
                            Util.sendMsgToHandler(h, allInfo.getReason(), false);
                        }
                    } else {
                        Util.sendMsgToHandler(h, "聚合返回数据异常", false);
                    }
                }

                @Override
                public void onError(int i, Throwable throwable) {
                    Log.e(TAG, "根据ID索引详情异常一次");
                    Util.sendMsgToHandler(h, "网络异常", false);
                }

                @Override
                public void onProgress(long bytesWritten, long totalSize) {

                }
            });
            client.put("id", b.getId());
            client.put("key", Util.APPKEY);
            client.doGet(15000);
        }
    }

    private void getData() {
        saveUsers = new XXSharedPreferences(MainActivity.SHAREDSESSIONIDSAVEEDNAME);
        usernumber = saveUsers.get(MyStarActivity.this, "usernumber", "").toString();
    }

    public void dissmiss() {
        if (XXSVProgressHUD.isShowing(MyStarActivity.this)) {
            XXSVProgressHUD.dismiss(MyStarActivity.this);
        }
    }

    private void getRecipeID(String usernumber) {
        XXSVProgressHUD.showWithStatus(MyStarActivity.this, "正在获取您收藏的菜谱");
        XXHttpClient client = new XXHttpClient(Util.URL_GETMYSTAR, true, new XXHttpClient.XXHttpResponseListener() {
            @Override
            public void onSuccess(int i, byte[] bytes) {
                Log.d(TAG, "请求用户收藏的菜谱：" + new String(bytes));
                if (bytes.length > 0) {
                    Data_GetUserStarRecipe recipe = new Gson().fromJson(new String(bytes), Data_GetUserStarRecipe.class);
                    if (recipe.getRespCode() == -23) {
                        Util.sendMsgToHandler(handler_getRecipeIDFromService, "您还没有收藏菜谱哦", false);
                    } else if (recipe.getRespCode() == 9000) {
                        Util.sendMsgToHandler(handler_getRecipeIDFromService, recipe, true);
                    } else {
                        Util.sendMsgToHandler(handler_getRecipeIDFromService, "后台数据库异常,请稍后再试", false);
                    }
                } else {
                    Util.sendMsgToHandler(handler_getRecipeIDFromService, "后台数据库异常,请稍后再试", false);
                }
            }

            @Override
            public void onError(int i, Throwable throwable) {
                Log.e(TAG, "请求用户收藏的菜谱失败。网络异常");
                Util.sendMsgToHandler(handler_getRecipeIDFromService, "网络异常,请稍后再试", false);
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {

            }
        });
        client.put("usernumber", usernumber);
        client.doPost(15000);
    }

    private void initView() {
        lv_mystar = (XXListViewCompat) findViewById(R.id.lv_mystar);
        ArrayList mMessageItems = new ArrayList<MessageItem>();
        adapter = new XXSlideAdapter(this, mMessageItems);
        lv_mystar.setAdapter(adapter);//设置适配器
        lv_mystar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(XXApplication.TAG, "点击了第：" + position + "个," + adapter.getItem(position).getTitle() + "," + adapter.getItem(position).getIconRes());
                Intent intent = new Intent(MyStarActivity.this, RecipeDetail.class);
                intent.putExtra("id", String.valueOf(adapter.getItem(position).getIconRes()));
                startActivity(intent);
                finish();
            }
        });
        adapter.setItemDeleteListener(this);//侧滑的删除按钮点击事件
        lv_mystar.setListViewAnimation(adapter,
                XXListViewAnimationMode.ANIIMATION_SCALE);//设置ListViewItem出现的动画

//        for (int i = 0; i < 100; i++) {
//            MessageItem item = new MessageItem();
//            item.iconRes = R.drawable.delete_default_qq_avatar;
//            item.title = "腾讯新闻" + i;
//            item.msg = "青岛爆炸满月：大量鱼虾死亡";
//            item.time = "晚上18:18";
//            item.img = "http://b.hiphotos.baidu.com/lvpics/h=800/sign=49d737112bf5e0fef11884016c6134e5/c9fcc3cec3fdfc032b8ddbb0d23f8794a5c226c4.jpg";
//            mMessageItems.add(item);
//        }
//        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemDelete(View v, final int position) {
        Log.d(TAG, "删除第" + position + "个");
        final Handler handler_canclestar = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                dissmiss();
                switch (msg.what) {
                    case -1:
                        Toast.makeText(MyStarActivity.this, msg.getData().getString("data"), Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Toast.makeText(MyStarActivity.this, "取消成功", Toast.LENGTH_SHORT).show();
                        adapter.removeItem(position);
                        adapter.notifyDataSetChanged();
                        break;
                }
            }
        };
        XXSVProgressHUD.showWithStatus(MyStarActivity.this, "正在为您努力取消这个收藏");
        XXHttpClient client = new XXHttpClient(Util.URL_SERVICE_STARORUNSTAR, true, new XXHttpClient.XXHttpResponseListener() {
            @Override
            public void onSuccess(int i, byte[] bytes) {
                Log.d(TAG, "取消收藏结果：" + new String(bytes));
                try {
                    JSONObject jo = new JSONObject(new String(bytes));
                    if (jo.getInt("errorCode") == 9000) {
                        Util.sendMsgToHandler(handler_canclestar, "成功", true);
                    } else {
                        Util.sendMsgToHandler(handler_canclestar, jo.getString("resultMsg"), false);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Util.sendMsgToHandler(handler_canclestar, "请稍后再试", false);
                }
            }

            @Override
            public void onError(int i, Throwable throwable) {
                Log.d(TAG, "取消收藏结果：" + throwable);
                Util.sendMsgToHandler(handler_canclestar, "网络异常,请稍后再试", false);
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {

            }
        });
        XXSharedPreferences preferences = new XXSharedPreferences(MainActivity.SHAREDSESSIONIDSAVEEDNAME);
        client.put("usernumber", preferences.get(MyStarActivity.this, "usernumber", "").toString());
        client.put("sessionid", preferences.get(MyStarActivity.this, "sessionid", "").toString());
        client.put("com_id", adapter.getItem(position).getIconRes());
        client.put("type", "2");
        client.doPost(15000);
    }
}
