package com.cn.smallrecipe.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cn.smallrecipe.MyActivity;
import com.cn.smallrecipe.R;
import com.cn.smallrecipe.Util;
import com.cn.smallrecipe.datainfo.sendsayinfo.Data_Say_Result;
import com.cn.smallrecipe.datainfo.sendsayinfo.Resp_Say;
import com.cn.smallrecipe.view.HorizontalListView;
import com.google.gson.Gson;

import cn.com.xxutils.adapter.XXListViewAdapter;
import cn.com.xxutils.progress.XXSVProgressHUD;
import cn.com.xxutils.util.XXHttpClient;
import cn.com.xxutils.util.XXImagesLoader;
import cn.com.xxutils.view.XXCustomListView;
import cn.com.xxutils.view.XXListView;

/**
 * Created by Administrator on 2016/3/14.
 */
public class MySayActivity extends MyActivity {
    private XXCustomListView listview_say;
    private XXListViewAdapter<Data_Say_Result> adapter_home;
    private XXListViewAdapter<String> adapter_horit;
    private XXImagesLoader xxImagesLoader_image;
    private XXListViewAdapter<String> adapter_comment;
    private XXImagesLoader xxImagesLoader;
    private int page = 2;
    private View footer;
    private Handler handler;
    private String usernumber = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mysay);
        usernumber = getIntent().getStringExtra("usernumber");
        Log.d(TAG, "得到的 usernumber：" + usernumber);
        initView();
    }

    private void initView() {
        listview_say = (XXCustomListView) findViewById(R.id.listview_mysay);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                listview_say.onRefreshComplete();
                listview_say.removeFooterView(footer);
                listview_say.onFootLoadingComplete();
                Log.d(TAG, "隐藏下拉view");
//                footer.setVisibility(View.INVISIBLE);
                switch (msg.what) {
                    case -1:
                        Toast.makeText(MySayActivity.this, msg.getData().getString("data"), Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Resp_Say say = (Resp_Say) msg.getData().getSerializable("data");
                        Log.d(TAG, say.getResultMsg());
                        if (say.getData().size() != 0) {
                            for (int i = 0; i < say.getData().size(); i++) {
                                Log.d(TAG, "ID:" + say.getData().get(i).getSay_id());
                                adapter_home.addItem(say.getData().get(i));
                            }
                        } else {
                            Toast.makeText(MySayActivity.this, "没有更多数据", Toast.LENGTH_LONG).show();
                        }
                        adapter_home.notifyDataSetChanged();
                        break;
                }
            }
        };
        /**
         * 主adapter
         */


        adapter_home = new XXListViewAdapter<Data_Say_Result>(this, R.layout.item_listview_friend) {
            @Override
            public void initGetView(final int position, View convertView, ViewGroup parent) {
                //评论输入的view默认隐藏
                LinearLayout layout_say = (LinearLayout) convertView.findViewById(R.id.layout_say);
                layout_say.setVisibility(View.GONE);
                /*发表该说说的用户头像*/
                ImageView item_iv_say_user_logo = (ImageView) convertView.findViewById(R.id.item_iv_say_user_logo);
                /*发表该说说的用户的昵称*/
                TextView item_tv_say_user_name = (TextView) convertView.findViewById(R.id.item_tv_say_user_name);
                item_tv_say_user_name.setText(getItem(position).getUser_name());

                TextView item_tv_time = (TextView) convertView.findViewById(R.id.item_tv_time);
                item_tv_time.setText(getItem(position).getSay_time());

                /*该说说的文本域*/
                TextView item_tv_say_text = (TextView) convertView.findViewById(R.id.item_tv_say_text);
                item_tv_say_text.setText("          " + getItem(position).getSay_text());

                /*点赞按钮*/
                ImageView item_iv_like = (ImageView) convertView.findViewById(R.id.item_iv_like);
                /*评论按钮*/
//                ImageView item_iv_say = (ImageView) convertView.findViewById(R.id.item_iv_say);
                /*横向的展示用户发表的图片的listview,默认隐藏*/ //TODO 横向listview命名--item_lv1_images
                HorizontalListView item_lv1_images = (HorizontalListView) convertView.findViewById(R.id.item_lv1_images);
                adapter_horit = new XXListViewAdapter<String>(MySayActivity.this, R.layout.item_hor) {
                    @Override
                    public void initGetView(int position, View convertView, ViewGroup parent) {
                        ImageView hor_1 = (ImageView) convertView.findViewById(R.id.hor_1);
                        if (getItem(position) != null) {
                            xxImagesLoader_image = new XXImagesLoader(null, true,
                                    R.drawable.downlode,
                                    R.drawable.downlode,
                                    R.drawable.downlodeerror);
                            xxImagesLoader_image.disPlayImage(getItem(position),
                                    hor_1);
                            Log.d(TAG, "填充图片的URL：" + getItem(position));
                            adapter_horit.notifyDataSetChanged();
//                    adapter_home.notifyDataSetChanged();
                        }

                    }
                };
                item_lv1_images.setAdapter(adapter_horit);
                String[] ss = getItem(position).getSay_image_url().split("ф");
                if (ss.length > 1) {
                    item_lv1_images.setVisibility(View.VISIBLE);
                } else {
                    item_lv1_images.setVisibility(View.GONE);
                }
                for (int j = 0; j < ss.length; j++) {
                    if (!ss[j].equals("") && ss[j] != null) {
                        Log.d(TAG, "给图片的listview添加一个图片：" + ss[j]);

                        adapter_horit.addItem(ss[j]);
                    }

                }

                /*城市*/
                TextView item_tv_location = (TextView) convertView.findViewById(R.id.item_tv_location);
                if (getItem(position).getCity().equals("")) {
                    item_tv_location.setText("该用户不想告诉你们他在哪");
                } else {
                    item_tv_location.setText(getItem(position).getCity());
                }
                /*点赞的人数*/
                TextView item_tv_star_number = (TextView) convertView.findViewById(R.id.item_tv_star_number);
                /*评论输入框*/
                final EditText item_et_say_down = (EditText) convertView.findViewById(R.id.item_et_say_down);

//                item_et_say_down.setImeOptions(EditorInfo.IME_ACTION_SEND);
//                item_et_say_down.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//                    @Override
//                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                        if (actionId == EditorInfo.IME_ACTION_SEND) {
//                            item_et_say_down.setText("");
//                            // 在这里编写自己想要实现的功能
//                        }
//                        return false;
//                    }
//                });
//                Button item_phone_images = (Button) convertView.findViewById(R.id.item_phone_images);
//                item_phone_images.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
////                        item_et_say_down.setText("");
//                        final Handler handler1 = new Handler() {
//                            @Override
//                            public void handleMessage(Message msg) {
//                                if (XXSVProgressHUD.isShowing(getActivity())) {
//                                    XXSVProgressHUD.dismiss(getActivity());
//                                }
//                                switch (msg.what) {
//                                    case -1:
//
//                                        break;
//                                    case 1:
//                                        item_et_say_down.setText("");
//                                        adapter_home.removeAll();
//                                        adapter_comment.removeAll();
//                                        getAllSay(handler, 1);
//                                        break;
//                                }
//                                Toast.makeText(getActivity(), msg.getData().getString("data"), Toast.LENGTH_LONG).show();
//                            }
//                        };
//                        XXSVProgressHUD.showWithStatus(getActivity(), "正在发表评论...");
//                        say_comment(getItem(position).getSay_id(), item_et_say_down.getText().toString().trim(), handler1);
//                    }
//                });
                /*对该条动态评论过的用户和评论内容显示的listview，该view默认隐藏*///TODO 显示评论块listview命名--item_lv2_say_user
                XXListView item_lv2_comment = (XXListView) convertView.findViewById(R.id.item_lv2_comment);
                adapter_comment = new XXListViewAdapter<String>(MySayActivity.this, R.layout.item_listview_comment) {
                    @Override
                    public void initGetView(int position, View convertView, ViewGroup parent) {
                        TextView item_tv_comm_username = (TextView) convertView.findViewById(R.id.item_tv_comm_username);
                        TextView item_tv_comm_text = (TextView) convertView.findViewById(R.id.item_tv_comm_text);
                        TextView item_tv_comm_time = (TextView) convertView.findViewById(R.id.item_tv_comm_time);
                        String[] ss = getItem(position).split("ф");
                        for (int i = 0; i < ss.length; i++) {
                            if (ss[i] != null && !ss[i].equals("")) {
                                item_tv_comm_username.setText(ss[1] + ":");
                                item_tv_comm_text.setText(ss[2]);
                                item_tv_comm_time.setText("评论时间：" + ss[3]);
                            }
                        }

                    }
                };
                item_lv2_comment.setAdapter(adapter_comment);
                if (getItem(position).getSay_comment() != null && !getItem(position).getSay_comment().equals("")) {
                    item_lv2_comment.setVisibility(View.VISIBLE);
                } else {
                    item_lv2_comment.setVisibility(View.GONE);
                }
//                adapter_comment.addItem("фAфBфC");
//                adapter_comment.addItem("фBфBфC");
//                adapter_comment.addItem("фCфBфC");
                if (getItem(position).getSay_comment() != null) {
                    Log.w(TAG, "all----" + getItem(position).getSay_comment());
                    String allData = getItem(position).getSay_comment();
                    String[] qq = allData.split("\\$");
                    for (int q = 0; q < qq.length; q++) {
                        Log.w(TAG, "QQQQQ" + qq[q]);
                        if (!qq[q].equals("")) {
                            adapter_comment.addItem(qq[q]);

//                            for (int i = 0; i < qq[q].split("^").length; i++) {
//                                adapter_comment.addItem( qq[q].split("^")[i]);
//                                Log.w(TAG, "QQQQQ" + qq[q].split("^")[i]);
//                            }


                        }

                    }
                }
//                if (getItem(position).getSay_comment() != null) {
//                    String[] oo = getItem(position).getSay_comment().split("^");
//
////                adapter_comment.addItem(getItem(position));
//                    for (int i = 0; i < oo.length; i++) {
////                        if (oo[i] != null && !oo[i].equals("")) {
//                            adapter_comment.addItem(oo[i]);
////                            Log.d(TAG, "评论的listview添加一次数据");
////                        }
//                    }
////                    adapter_home.notifyDataSetChanged();
////                    adapter_comment.notifyDataSetChanged();
//                }

                xxImagesLoader = new XXImagesLoader(null, true,
                        R.drawable.downlode,
                        R.drawable.downlode,
                        R.drawable.downlodeerror);
                xxImagesLoader.disPlayImage(getItem(position).getUser_img(),
                        item_iv_say_user_logo);


//                item_lv1_images.setAdapter(adapter_horit);
            }


        };
        //

        listview_say.setAdapter(adapter_home);
        footer = View.inflate(MySayActivity.this, R.layout.footer, null);
        listview_say.setOnAddFootListener(new XXCustomListView.OnAddFootListener() {
            @Override
            public void addFoot() {
                listview_say.addFooterView(footer);
                footer.setVisibility(View.INVISIBLE);
            }
        });
        listview_say.setOnFootLoadingListener(new XXCustomListView.OnFootLoadingListener() {
            @Override
            public void onFootLoading() {
//                footer.setVisibility(View.VISIBLE);
                footer.setVisibility(View.VISIBLE);
                getMySay(handler, page++);
//                footer.setVisibility(View.GONE);
            }
        });
        adapter_home.notifyDataSetChanged();
        getMySay(handler, 1);
        listview_say.setOnRefreshListner(new XXCustomListView.OnRefreshListner() {
            @Override
            public void onRefresh() {
                if (xxImagesLoader != null) {
                    xxImagesLoader.clearCache();
                    Log.d(TAG, "刷新，清除缓存");
                }
                if (xxImagesLoader_image != null) {
                    xxImagesLoader_image.clearCache();
                    Log.d(TAG, "刷新，清除缓存");
                }
                adapter_home.removeAll();
                adapter_home.notifyDataSetChanged();
                page = 2;
                getMySay(handler, 1);
            }
        });
    }

    private void getMySay(final Handler handler, int page) {
        Log.d(TAG, "开始请求我的动态");
        XXHttpClient client = new XXHttpClient(Util.URL_GETMYSAY, true, new XXHttpClient.XXHttpResponseListener() {
            @Override
            public void onSuccess(int i, byte[] bytes) {
                Log.e(TAG,"请求个人说说返回："+new String(bytes));
                if (bytes.length > 0) {
                    Log.d(TAG, new String(bytes));
                    Resp_Say say = new Gson().fromJson(new String(bytes), Resp_Say.class);
                    if (say.getResultCode() == 9000) {
                        Util.sendMsgToHandler(handler, say, true);
                    } else {
                        Util.sendMsgToHandler(handler, say.getResultMsg(), false);
                    }
                }
            }

            @Override
            public void onError(int i, Throwable throwable) {
                Util.sendMsgToHandler(handler, "网络异常", false);
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {

            }
        });
        client.put("usernumber", usernumber);
        client.put("page", page);
        Log.d(TAG, "上送获取个人说说的字段：" + client.getAllParams());
        client.doGet(15000);

    }
}
