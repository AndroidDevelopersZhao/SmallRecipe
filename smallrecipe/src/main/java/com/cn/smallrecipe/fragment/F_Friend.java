package com.cn.smallrecipe.fragment;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cn.smallrecipe.Mode;
import com.cn.smallrecipe.ParentFragment;
import com.cn.smallrecipe.R;
import com.cn.smallrecipe.Util;
import com.cn.smallrecipe.datainfo.sendsayinfo.Data_Say_Result;
import com.cn.smallrecipe.datainfo.sendsayinfo.Resp_Say;
import com.cn.smallrecipe.view.HorizontalListView;
import com.google.gson.Gson;

import cn.com.xxutils.adapter.XXListViewAdapter;
import cn.com.xxutils.util.XXHttpClient;
import cn.com.xxutils.util.XXImagesLoader;
import cn.com.xxutils.util.XXListViewAnimationMode;
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
    int isc = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.f_friend, null);
        Log.d(TAG, "进入厨艺圈页面");
        initView();
        return view;
    }

    private void initView() {
        listview_friend = (XXCustomListView) view.findViewById(R.id.listview_friend);
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case -1:
                        Toast.makeText(getActivity(), msg.getData().getString("data"), Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Resp_Say say = (Resp_Say) msg.getData().getSerializable("data");
                        Log.d(TAG, say.getResultMsg());
                        for (int i = 0; i < say.getData().size(); i++) {
                            adapter_home.addItem(say.getData().get(i));
                            Log.d(TAG, "给主Listview添加一条数据，用户名：" + say.getData().get(i).getUser_name());
                            String[] ss = say.getData().get(i).getSay_image_url().split("ф");
                            for (int j = 0; j < ss.length; j++) {
                                if (!ss[j].equals("") && ss[j] != null) {
                                    Log.d(TAG, "给图片的listview添加一个图片：" + ss[j]);
                                    adapter_horit.addItem(ss[j]);
                                }

                            }
                        }
                        adapter_home.notifyDataSetChanged();
                        adapter_horit.notifyDataSetChanged();
//                        setListViewHeight(listview_friend);
                        break;
                }
            }
        };

        adapter_horit = new XXListViewAdapter<String>(getActivity(), R.layout.item_hor) {
            @Override
            public void initGetView(int position, View convertView, ViewGroup parent) {
                ImageView hor_1 = (ImageView) convertView.findViewById(R.id.hor_1);
                if (getItem(position) != null) {
                    new XXImagesLoader(null, true,
                            R.drawable.delete_default_qq_avatar,
                            R.drawable.delete_default_qq_avatar,
                            R.drawable.delete_default_qq_avatar).disPlayImage(getItem(position),
                            hor_1);
                    Log.d(TAG, "填充图片的URL：" + getItem(position));
                    adapter_horit.notifyDataSetChanged();
                    adapter_home.notifyDataSetChanged();
                }

            }
        };
        /**
         * 主adapter
         */

        adapter_home = new XXListViewAdapter<Data_Say_Result>(getActivity(), R.layout.item_listview_friend) {
            @Override
            public void initGetView(int position, View convertView, ViewGroup parent) {
                /*发表该说说的用户头像*/
                ImageView item_iv_say_user_logo = (ImageView) convertView.findViewById(R.id.item_iv_say_user_logo);
                /*发表该说说的用户的昵称*/
                TextView item_tv_say_user_name = (TextView) convertView.findViewById(R.id.item_tv_say_user_name);
                item_tv_say_user_name.setText(getItem(position).getUser_name());

                TextView item_tv_time = (TextView) convertView.findViewById(R.id.item_tv_time);
                item_tv_time.setText(getItem(position).getSay_time());

                /*该说说的文本域*/
                TextView item_tv_say_text = (TextView) convertView.findViewById(R.id.item_tv_say_text);
                item_tv_say_text.setText(getItem(position).getSay_text());

                /*点赞按钮*/
                ImageView item_iv_like = (ImageView) convertView.findViewById(R.id.item_iv_like);
                /*评论按钮*/
                ImageView item_iv_say = (ImageView) convertView.findViewById(R.id.item_iv_say);
                /*横向的展示用户发表的图片的listview,默认隐藏*/ //TODO 横向listview命名--item_lv1_images

                HorizontalListView item_lv1_images = (HorizontalListView) convertView.findViewById(R.id.item_lv1_images);
                    item_lv1_images.setAdapter(adapter_horit);
//                if (adapter_horit.getItem(position) != null && !adapter_horit.getItem(position).equals("")) {
//                    item_lv1_images.setVisibility(View.VISIBLE);
//                    Log.d(TAG, "设置显示图片的listview可见");
////                    setListViewHeight(listview_friend);
////                    adapter_home.notifyDataSetChanged();
//                } else {
//                    item_lv1_images.setVisibility(View.GONE);
//                    Log.d(TAG, "设置显示图片的listview不可见");
//                }

                /*城市*/
                TextView item_tv_location = (TextView) convertView.findViewById(R.id.item_tv_location);
                item_tv_location.setText(getItem(position).getCity());
                /*点赞的人数*/
                TextView item_tv_star_number = (TextView) convertView.findViewById(R.id.item_tv_star_number);
                /*评论输入框*/
                EditText item_et_say_down = (EditText) convertView.findViewById(R.id.item_et_say_down);
                /*表情选择按钮（图片）*/
                ImageView item_phone_images = (ImageView) convertView.findViewById(R.id.item_phone_images);
                /*对该条动态评论过的用户和评论内容显示的listview，该view默认隐藏*///TODO 显示评论块listview命名--item_lv2_say_user
                XXListView item_lv2_say_user = (XXListView) convertView.findViewById(R.id.item_lv2_say_user);
//                setUserLogo(getItem(position).getUser_img(), item_iv_say_user_logo);
//                item_iv_say_user_logo.setImageResource(R.drawable.delete_default_qq_avatar);
                new XXImagesLoader(null, true,
                        R.drawable.delete_default_qq_avatar,
                        R.drawable.delete_default_qq_avatar,
                        R.drawable.delete_default_qq_avatar).disPlayImage(getItem(position).getUser_img(),
                        item_iv_say_user_logo);
                adapter_home.notifyDataSetChanged();
            }
        };
//        adapter_home.removeAll();
        listview_friend.setAdapter(adapter_home);
//        listview_friend.setListViewAnimation(adapter_home, XXListViewAnimationMode.ANIIMATION_ALPHA);
        getAllSay(handler);
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
                Log.d(TAG, new String(bytes));
                Resp_Say say = new Gson().fromJson(new String(bytes), Resp_Say.class);
                if (say.getResultCode() == 9000) {
                    Util.sendMsgToHandler(handler, say, true);
                } else {
                    Util.sendMsgToHandler(handler, say.getResultMsg(), false);
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
