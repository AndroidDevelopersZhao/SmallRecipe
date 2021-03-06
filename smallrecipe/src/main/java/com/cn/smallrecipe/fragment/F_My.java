
package com.cn.smallrecipe.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cn.smallrecipe.MyActivity;
import com.cn.smallrecipe.ParentFragment;
import com.cn.smallrecipe.R;
import com.cn.smallrecipe.Util;
import com.cn.smallrecipe.activity.CaptureActivity;
import com.cn.smallrecipe.activity.LoginActivity;
import com.cn.smallrecipe.activity.MainActivity;
import com.cn.smallrecipe.activity.MySayActivity;
import com.cn.smallrecipe.activity.MyStarActivity;
import com.cn.smallrecipe.activity.PersonalActivity;
import com.cn.smallrecipe.activity.QRCodeActivity;
import com.cn.smallrecipe.activity.RespMsgActivity;
import com.cn.smallrecipe.activity.SendSayActivity;
import com.cn.smallrecipe.datainfo.myinfo.MyInfos;
import com.cn.smallrecipe.datainfo.myinfo.MyInfosTitle;
import com.cn.smallrecipe.datainfo.mystarinfo.Data_GetUserStarRecipe;
import com.cn.smallrecipe.datainfo.register.ResultToApp;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.Hashtable;

import cn.com.xxutils.adapter.XXListViewAdapter;
import cn.com.xxutils.alerterview.OnItemClickListener;
import cn.com.xxutils.alerterview.XXAlertView;
import cn.com.xxutils.progress.XXSVProgressHUD;
import cn.com.xxutils.util.XXHttpClient;
import cn.com.xxutils.util.XXSharedPreferences;
import cn.com.xxutils.view.XXListView;
import cn.com.xxutils.view.XXRoundImageView;

/**
 * //TODO 个人中心
 * Created by Administrator on 2016/2/24.
 */
public class F_My extends ParentFragment {
    private View view;
    private XXListView listView1, listView2, listView3;
    private static XXListViewAdapter<MyInfosTitle> adapter1 = null;
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
        Log.d(TAG, "进入个人中心首页--onCreateView");
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


//        addDataToAdpter1();
        addDataToAdpter2();
        addDataToAdpter3();

        listView1.setOnItemClickListener(listview_1_listener);
        listView2.setOnItemClickListener(listview_2_listener);
        listView3.setOnItemClickListener(listview_3_listener);
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
                            if (position == 0) {
                                startActivity(new Intent(getActivity(), LoginActivity.class));
                            }
                        }
                    }).show();
                }
            }
        }
    };


    //listView2的点击事件
    AdapterView.OnItemClickListener listview_2_listener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.d(TAG, "position:" + position);
            switch (position) {
                case 0:
                    //TODO 我的动态
                    if (MyActivity.LOGIN_STATE) {
//                        Toast.makeText(getActivity(), "我的动态", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity(), MySayActivity.class);
                        intent.putExtra("usernumber", new XXSharedPreferences(MainActivity.SHAREDSESSIONIDSAVEEDNAME).get(getActivity(), "usernumber", "").toString());
                        startActivity(intent);
                    } else {
                        Toast.makeText(getActivity(), "请先登陆", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 1:
                    //TODO 我的收藏
                    if (MyActivity.LOGIN_STATE) {
                        Log.d(TAG, "我的收藏");
                        startActivity(new Intent(getActivity(), MyStarActivity.class));
                        getActivity().overridePendingTransition(R.anim.pp_enter, R.anim.pp_exit);
                    } else {
                        Toast.makeText(getActivity(), "请先登陆", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 2:
                    //TODO 扫一扫
                    if (MyActivity.LOGIN_STATE) {
                        new XXAlertView("提示", "请选择操作类型", "取消", new String[]{"扫一扫", "我的二维码"},
                                null, getActivity(), XXAlertView.Style.ActionSheet, new OnItemClickListener() {
                            @Override
                            public void onItemClick(Object o, int position) {
                                Log.d(TAG, "*********" + position);
                                if (position == 0) {
                                    //扫一扫
                                    Intent intent =new Intent(getActivity(), CaptureActivity.class);
                                    startActivity(intent);
                                } else if (position == 1) {
                                    //我的二维码
                                    createQRImage("http://www.baidu.com");
                                }
                            }
                        }).show();
                    } else {
                        Toast.makeText(getActivity(), "请先登陆", Toast.LENGTH_SHORT).show();
                    }

                    break;
                case 3:
                    //TODO 晒厨艺
                    //获取当前用户收藏的菜谱列表
                    if (MyActivity.LOGIN_STATE) {
                        getMyStarRecipe();
                    } else {
                        Toast.makeText(getActivity(), "请先登陆", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    private int QR_WIDTH =256;
    private int QR_HEIGHT =256;

    public void createQRImage(String url)
    {
        try
        {
            //判断URL合法性
            if (url == null || "".equals(url) || url.length() < 1)
            {
                return;
            }
            Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            //图像数据转换，使用了矩阵转换
            BitMatrix bitMatrix = new QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
            int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
            //下面这里按照二维码的算法，逐个生成二维码的图片，
            //两个for循环是图片横列扫描的结果
            for (int y = 0; y < QR_HEIGHT; y++)
            {
                for (int x = 0; x < QR_WIDTH; x++)
                {
                    if (bitMatrix.get(x, y))
                    {
                        pixels[y * QR_WIDTH + x] = 0xff000000;
                    }
                    else
                    {
                        pixels[y * QR_WIDTH + x] = 0xffffffff;
                    }
                }
            }
            //生成二维码图片的格式，使用ARGB_8888
            Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);
            //显示到一个ImageView上面
            if (bitmap!=null){
                Log.d(TAG, "二维码生成成功");
                Intent intent =new Intent(getActivity(), QRCodeActivity.class);
                intent.putExtra("qr",bitmap);
                startActivity(intent);
            }else {
                XXSVProgressHUD.showErrorWithStatus(getActivity(),"二维码生成失败，请稍后再试");
            }

//            sweepIV.setImageBitmap(bitmap);
        }
        catch (WriterException e)
        {
            e.printStackTrace();
        }
    }
    public void dissmiss() {
        if (XXSVProgressHUD.isShowing(getActivity())) {
            XXSVProgressHUD.dismiss(getActivity());
        }
    }

    private void getMyStarRecipe() {
        XXSVProgressHUD.showWithStatus(getActivity(), "请稍后");
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                dissmiss();
                switch (msg.what) {
                    case -1:
                        Toast.makeText(getActivity(), msg.getData().getString("data"), Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Data_GetUserStarRecipe recipe = (Data_GetUserStarRecipe) msg.getData().getSerializable("data");
                        final String[] str = new String[recipe.getIds().length];
                        for (int i = 0; i < recipe.getIds().length; i++) {
                            Log.w(TAG, "获取id成功：" + recipe.getIds()[i].getId());
                            Log.w(TAG, "com_name:" + recipe.getIds()[i].getCom_name());
                            str[i] = recipe.getIds()[i].getId() + "," + recipe.getIds()[i].getCom_name();
                        }
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                SystemClock.sleep(500);
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        new XXAlertView("提示", "您需要选择一个您收藏的商品方可发表动态",
                                                "取消", str, null, getActivity(), XXAlertView.Style.ActionSheet,
                                                new OnItemClickListener() {
                                                    @Override
                                                    public void onItemClick(Object o, int position) {
                                                        Log.d(TAG, "possion:" + position);
                                                        if (position != -1) {
                                                            String data = XXAlertView.getAdapter().getItem(position).toString();
                                                            Log.w(TAG, "o:" + data);
                                                            String[] datas = data.split(",");

                                                            Intent intent = new Intent(getActivity(), SendSayActivity.class);
                                                            intent.putExtra("id", datas[0]);
                                                            intent.putExtra("com_name", datas[1]);
                                                            startActivity(intent);
                                                        }
                                                    }
                                                }).show();
                                    }
                                });
                            }
                        }).start();
                        break;
                }
            }
        };

        XXHttpClient client = new XXHttpClient(Util.URL_GETMYSTAR, true, new XXHttpClient.XXHttpResponseListener() {
            @Override
            public void onSuccess(int i, byte[] bytes) {
                Log.d(TAG, "获取成功的返回：" + new String(bytes));
                if (bytes.length > 0) {
                    Data_GetUserStarRecipe recipe = new Gson().fromJson(new String(bytes), Data_GetUserStarRecipe.class);
                    if (recipe.getRespCode() == -23) {
                        Util.sendMsgToHandler(handler, "您还没有收藏菜谱哦", false);
                    } else if (recipe.getRespCode() == 9000) {
                        Util.sendMsgToHandler(handler, recipe, true);
                    } else {
                        Util.sendMsgToHandler(handler, "后台数据库异常,请稍后再试", false);
                    }
                } else {
                    Util.sendMsgToHandler(handler, "后台数据库异常,请稍后再试", false);
                }
            }

            @Override
            public void onError(int i, Throwable throwable) {
                Log.d(TAG, "网络异常");
                Util.sendMsgToHandler(handler, "网络异常,请稍后再试", false);
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {

            }
        });
        client.put("usernumber", usernumber);
        client.doGet(15000);
    }

    //listView3的点击事件
    AdapterView.OnItemClickListener listview_3_listener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.d(TAG, "position:" + position);
            if (position == 0) {
                //TODO 反馈信息
                startActivity(new Intent(getActivity(), RespMsgActivity.class));
                getActivity().overridePendingTransition(R.anim.pp_enter, R.anim.pp_exit);
            } else {
                //TODO 设置
                Toast.makeText(getActivity(), "设置", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private XXSharedPreferences sharedPreferences;
    private String sessionid = null;
    private String usernumber = null;

    private void setData() {
        sharedPreferences = new XXSharedPreferences(MainActivity.SHAREDSESSIONIDSAVEEDNAME);
        String userid = null;
        String username = null;
        String userlogourl = null;

        try {
            usernumber = String.valueOf(sharedPreferences.get(getActivity(), "usernumber", ""));
            userid = String.valueOf(sharedPreferences.get(getActivity(), "userid", ""));
            username = String.valueOf(sharedPreferences.get(getActivity(), "username", ""));
            sessionid = String.valueOf(sharedPreferences.get(getActivity(), "sessionid", ""));
            userlogourl = String.valueOf(sharedPreferences.get(getActivity(), "userlogo", ""));
        } catch (Throwable throwable) {
            //上下文空时会有异常跑出
        }

        if (MyActivity.LOGIN_STATE) {

            adapter1.removeAll();

            adapter1.addItem(new MyInfosTitle(userlogourl, username,
                    R.drawable.icon_my_title, userid));
            adapter1.notifyDataSetChanged();
        } else {
            //设置默认
            addDataToAdpter1();
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

    private static void addDataToAdpter1() {
        adapter1.removeAll();
        adapter1.addItem(new MyInfosTitle("", "未登录", R.drawable.icon_my_title, ""));
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
                if (getItem(position).getImage_left().equals("")) {
                    imageView_left.setImageResource(R.drawable.userlogodefult);
                } else {
                    setUserLogo(getItem(position).getImage_left(), imageView_left);
                }

                imageView_right.setImageResource(getItem(position).getImage_right());
                text.setText(getItem(position).getText());
                id.setText(getItem(position).getId());

            }
        };
    }

    private Handler handler_getUserLogo = null;

    private void setUserLogo(String image_left, final ImageView imageView) {
        handler_getUserLogo = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case -1:
                        if (F_My.this.isVisible())
                            Toast.makeText(getActivity(), "请设置用户头像", Toast.LENGTH_LONG).show();
                        imageView.setImageResource(R.drawable.userlogodefult);
                        break;
                    case 1:
                        byte[] bytes = msg.getData().getByteArray("data");
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        imageView.setImageBitmap(bitmap);
                        break;
                }
            }
        };
        Log.d(TAG, "请求的用户头像url=" + image_left);
        XXHttpClient client = new XXHttpClient(image_left, true, new XXHttpClient.XXHttpResponseListener() {
            @Override
            public void onSuccess(int i, final byte[] bytes) {
                Log.d(TAG, "用户头像获取成功");
                Util.sendMsgToHandler(handler_getUserLogo, bytes, true);
            }

            @Override
            public void onError(int i, Throwable throwable) {
                Log.e(TAG, "网络异常");
                Util.sendMsgToHandler(handler_getUserLogo, "网络异常", false);
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {

            }
        });
        client.doPost(15000);
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
                        MainActivity.LOGIN_STATE = false;
                        if (MyActivity.TENCENT != null)
                            MyActivity.TENCENT.logout(getActivity());
                        setData();
                        if (F_My.this.isVisible()) {
                            XXRoundImageView xxRoundImageView = (XXRoundImageView) getActivity().findViewById(R.id.userLogo_main);
                            xxRoundImageView.setImageResource(R.drawable.userlogodefult);
                            TextView textView = (TextView) getActivity().findViewById(R.id.userName_main);
                            textView.setText("点我登陆");
                        }
                        break;

                    case 1:
                        MainActivity.LOGIN_STATE = true;
                        setData();
                        break;
                }

            }
        };


        XXHttpClient client = new XXHttpClient(Util.URL_SERVICE_AUTH_SESSIONID, true, new XXHttpClient.XXHttpResponseListener() {
            @Override
            public void onSuccess(int i, byte[] bytes) {
                Log.d(TAG, "F_My页面验证Auth的返回，" + new String(bytes));
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

    @Override
    public void onResume() {
        Log.d(TAG, "进入个人中心onResume");
        sharedPreferences = new XXSharedPreferences(MainActivity.SHAREDSESSIONIDSAVEEDNAME);
        usernumber = String.valueOf(sharedPreferences.get(getActivity(), "usernumber", ""));
        sessionid = String.valueOf(sharedPreferences.get(getActivity(), "sessionid", ""));
        AuthUserInfo(usernumber, sessionid);
        super.onResume();

    }
}