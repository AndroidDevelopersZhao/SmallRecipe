package com.cn.smallrecipe.fragment;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cn.smallrecipe.Mode;
import com.cn.smallrecipe.ParentFragment;
import com.cn.smallrecipe.R;
import com.cn.smallrecipe.Util;
import com.cn.smallrecipe.activity.MainActivity;
import com.cn.smallrecipe.datainfo.sendsayinfo.Data_Say_Result;
import com.cn.smallrecipe.datainfo.sendsayinfo.Resp_Say;
import com.cn.smallrecipe.view.FaceRelativeLayout;
import com.cn.smallrecipe.view.HorizontalListView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.com.xxutils.adapter.XXListViewAdapter;
import cn.com.xxutils.progress.XXSVProgressHUD;
import cn.com.xxutils.util.XXHttpClient;
import cn.com.xxutils.util.XXImagesLoader;
import cn.com.xxutils.util.XXListViewAnimationMode;
import cn.com.xxutils.util.XXSharedPreferences;
import cn.com.xxutils.view.XXCustomListView;
import cn.com.xxutils.view.XXListView;
import cn.com.xxutils.view.XXRoundImageView;
import cn.com.xxutils.volley.toolbox.Volley;

/**
 * Created by Administrator on 2016/2/24.
 */
public class F_Friend extends ParentFragment {
    private View view;
    private XXCustomListView listview_friend;
    private XXListViewAdapter<Data_Say_Result> adapter_home;
    private XXListViewAdapter<String> adapter_horit;
    private XXListViewAdapter<String> adapter_comment;
    int isc = 0;
    private Handler handler;

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "F-Friend-------------onPause");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "F-Friend-------------onResume");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "F-Friend-------------onStart");
        if (adapter_home != null) {
            if (isc == 0) {

                adapter_home.removeAll();
                adapter_home.notifyDataSetChanged();
                getAllSay(handler);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "F-Friend-------------onStop");
        isc = 0;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.f_friend, null);
        Log.d(TAG, "F-Friend-------------onCreateView");
        Log.d(TAG, "进入厨艺圈页面");
        initView();
        isc++;
        return view;
    }

    XXImagesLoader xxImagesLoader;
    XXImagesLoader xxImagesLoader_image;


    private void initView() {
        listview_friend = (XXCustomListView) view.findViewById(R.id.listview_friend);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                listview_friend.onRefreshComplete();
                switch (msg.what) {
                    case -1:
                        Toast.makeText(getActivity(), msg.getData().getString("data"), Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Resp_Say say = (Resp_Say) msg.getData().getSerializable("data");
                        Log.d(TAG, say.getResultMsg());
                        for (int i = 0; i < say.getData().size(); i++) {
                            Log.d(TAG, "ID:" + say.getData().get(i).getSay_id());
                            adapter_home.addItem(say.getData().get(i));

                        }
                        adapter_home.notifyDataSetChanged();
                        break;
                }
            }
        };


        /**
         * 主adapter
         */

        adapter_home = new XXListViewAdapter<Data_Say_Result>(getActivity(), R.layout.item_listview_friend) {

            @Override
            public void initGetView(final int position, View convertView, ViewGroup parent) {
                //评论输入的view默认隐藏
                LinearLayout layout_say = (LinearLayout) convertView.findViewById(R.id.layout_say);

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
                adapter_horit = new XXListViewAdapter<String>(getActivity(), R.layout.item_hor) {
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
                /*表情选择按钮（图片）*/
//                ImageView item_phone_images = (ImageView) convertView.findViewById(R.id.item_phone_images);
                Button item_phone_images = (Button) convertView.findViewById(R.id.item_phone_images);
                item_phone_images.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        item_et_say_down.setText("");
                        final Handler handler1 = new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                if (XXSVProgressHUD.isShowing(getActivity())) {
                                    XXSVProgressHUD.dismiss(getActivity());
                                }
                                switch (msg.what) {
                                    case -1:

                                        break;
                                    case 1:
                                        item_et_say_down.setText("");
                                        adapter_home.removeAll();
                                        adapter_comment.removeAll();
                                        getAllSay(handler);
                                        break;
                                }
                                Toast.makeText(getActivity(), msg.getData().getString("data"), Toast.LENGTH_LONG).show();
                            }
                        };
                        XXSVProgressHUD.showWithStatus(getActivity(), "正在发表评论...");
                        say_comment(getItem(position).getSay_id(), item_et_say_down.getText().toString().trim(), handler1);
                    }
                });
                /*对该条动态评论过的用户和评论内容显示的listview，该view默认隐藏*///TODO 显示评论块listview命名--item_lv2_say_user
                XXListView item_lv2_comment = (XXListView) convertView.findViewById(R.id.item_lv2_comment);
                adapter_comment = new XXListViewAdapter<String>(getActivity(), R.layout.item_listview_comment) {
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
                    Log.w(TAG,"all----"+getItem(position).getSay_comment());
                    String allData =getItem(position).getSay_comment();
                    String[]qq=allData.split("\\$");
                    for (int q = 0; q < qq.length; q++) {
                        Log.w(TAG, "QQQQQ" + qq[q]);
                        if (!qq[q].equals("")){
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
        listview_friend.setAdapter(adapter_home);
        adapter_home.notifyDataSetChanged();
        getAllSay(handler);
        listview_friend.setOnRefreshListner(new XXCustomListView.OnRefreshListner() {
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
                getAllSay(handler);
            }
        });

    }

    private void say_comment(String say_id, String text, final Handler handler1) {
        XXHttpClient client = new XXHttpClient(Util.URL_COMMENT, true, new XXHttpClient.XXHttpResponseListener() {
            @Override
            public void onSuccess(int i, byte[] bytes) {
                Log.w(TAG, "发表评论返回：" + new String(bytes));
                try {
                    JSONObject jo = new JSONObject(new String(bytes));
                    if (jo
                            .getInt("resultCode") == 9000) {
                        Util.sendMsgToHandler(handler1, "评论成功", true);
                    } else {
                        Util.sendMsgToHandler(handler1, "评论失败，" + jo.getString("resultMsg"), false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(int i, Throwable throwable) {
                Log.w(TAG, "发表评论网络异常");
                Util.sendMsgToHandler(handler, "评论失败，网络异常", false);
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {

            }
        });
        client.put("say_id", say_id);
        client.put("usernumber", new XXSharedPreferences(MainActivity.SHAREDSESSIONIDSAVEEDNAME).get(getActivity(), "usernumber", "").toString());
        client.put("text", text);
        client.put("time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        Log.w(TAG, "发表评论上送：" + client.getAllParams());
        client.doGet(15000);
    }

    public void setListViewHeight(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }


        int totalHeight = 0;


        for (int i = 0; i < listAdapter.getCount(); i++) {


            View listItem = listAdapter.getView(i, null, listView);


            listItem.measure(0, 0);


            totalHeight += listItem.getMeasuredHeight();


        }


        ViewGroup.LayoutParams params = listView.getLayoutParams();


        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));


        listView.setLayoutParams(params);

    }

    private void setUserLogo(String user_img, final XXRoundImageView item_iv_say_user_logo) {
        XXHttpClient client = new XXHttpClient(user_img, true, new XXHttpClient.XXHttpResponseListener() {
            @Override
            public void onSuccess(int i, final byte[] bytes) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        item_iv_say_user_logo.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                        adapter_home.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onError(int i, Throwable throwable) {

            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {

            }
        });
        client.doGet(15000);
    }

    private void getAllSay(final Handler handler) {
        Log.d(TAG, "开始请求所有动态");
        XXHttpClient client = new XXHttpClient(Util.URL_GETALLSAY, true, new XXHttpClient.XXHttpResponseListener() {
            @Override
            public void onSuccess(int i, byte[] bytes) {
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
        client.doGet(15000);
    }
}
