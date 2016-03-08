package com.cn.smallrecipe.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.cn.smallrecipe.MyActivity;
import com.cn.smallrecipe.R;
import com.cn.smallrecipe.Util;
import com.cn.smallrecipe.datainfo.recipedetail.RecipeDetailsInfo;
import com.cn.smallrecipe.datainfo.register.RespData;
import com.cn.smallrecipe.datainfo.register.ResultToApp;
import com.cn.smallrecipe.datainfo.search.AllInfo;
import com.cn.smallrecipe.datainfo.search.Data;
import com.cn.smallrecipe.datainfo.search.Result;
import com.cn.smallrecipe.datainfo.search.Steps;
import com.google.gson.Gson;

import java.util.ArrayList;

import cn.com.xxutils.adapter.XXListViewAdapter;
import cn.com.xxutils.likebutton.OnLikeListener;
import cn.com.xxutils.likebutton.XXLikeButton;
import cn.com.xxutils.util.XXHttpClient;
import cn.com.xxutils.util.XXImagesLoader;
import cn.com.xxutils.util.XXListViewAnimationMode;
import cn.com.xxutils.util.XXSharedPreferences;
import cn.com.xxutils.view.XXListView;

public class RecipeDetail extends MyActivity {
    private XXListView lv_recipe_detail;
    private XXListViewAdapter<RecipeDetailsInfo> adapter = null;
    private TextView tv_recipe_title, tv_recipe_detail;
    private LinearLayout lt_main_1, lt_main_2, lt_main_3, lt_main_4, lt_main_5, lt_main_6, lt_main_7, lt_main_8, lt_main_9, lt_main_10;//主料view
    private LinearLayout lt_main_1_xian, lt_main_2_xian, lt_main_3_xian, lt_main_4_xian,
            lt_main_5_xian, lt_main_6_xian, lt_main_7_xian, lt_main_8_xian, lt_main_9_xian, lt_main_10_xian;//主料下线
    private TextView tv_main_1_title, tv_main_2_title, tv_main_3_title, tv_main_4_title,
            tv_main_5_title, tv_main_6_title, tv_main_7_title, tv_main_8_title, tv_main_9_title, tv_main_10_title;
    private TextView tv_main_1_value, tv_main_2_value, tv_main_3_value, tv_main_4_value,
            tv_main_5_value, tv_main_6_value, tv_main_7_value, tv_main_8_value, tv_main_9_value, tv_main_10_value;


    private LinearLayout lt_fu_1, lt_fu_2, lt_fu_3, lt_fu_4, lt_fu_5, lt_fu_6,
            lt_fu_7, lt_fu_8, lt_fu_9, lt_fu_10, lt_fu_11, lt_fu_12, lt_fu_13, lt_fu_14, lt_fu_15, lt_fu_16, lt_fu_17, lt_fu_18, lt_fu_19, lt_fu_20;//辅料view
    private LinearLayout lt_fu_1_xian, lt_fu_2_xian, lt_fu_3_xian, lt_fu_4_xian,
            lt_fu_5_xian, lt_fu_6_xian, lt_fu_7_xian, lt_fu_8_xian, lt_fu_9_xian,
            lt_fu_10_xian, lt_fu_11_xian, lt_fu_12_xian, lt_fu_13_xian, lt_fu_14_xian, lt_fu_15_xian, lt_fu_16_xian, lt_fu_17_xian, lt_fu_18_xian, lt_fu_19_xian, lt_fu_20_xian;//辅料下线

    private TextView tv_fu_1_title, tv_fu_2_title, tv_fu_3_title, tv_fu_4_title, tv_fu_5_title, tv_fu_6_title,
            tv_fu_7_title, tv_fu_8_title, tv_fu_9_title, tv_fu_10_title, tv_fu_11_title, tv_fu_12_title, tv_fu_13_title, tv_fu_14_title, tv_fu_15_title, tv_fu_16_title, tv_fu_17_title, tv_fu_18_title, tv_fu_19_title, tv_fu_20_title;
    private TextView tv_fu_1_value, tv_fu_2_value, tv_fu_3_value, tv_fu_4_value, tv_fu_5_value, tv_fu_6_value,
            tv_fu_7_value, tv_fu_8_value, tv_fu_9_value, tv_fu_10_value, tv_fu_11_value, tv_fu_12_value, tv_fu_13_value, tv_fu_14_value, tv_fu_15_value, tv_fu_16_value, tv_fu_17_value, tv_fu_18_value, tv_fu_19_value, tv_fu_20_value;
    private TextView tv_star_number;
    private TextView tv_like_number;
    private TextView tv_comment_number;
    private XXLikeButton star_button;
    private XXLikeButton like_button;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        initView();
    }

    private String id;

    private void getData() {
        id = getIntent().getStringExtra("id");
        if (id == null) {
            Toast.makeText(this, "获取菜谱ID失败", Toast.LENGTH_LONG).show();
            return;
        }
        getRecipeDetailsUseID(id);
    }

    /**
     * 根据ID查询该菜谱的所有信息--查询聚合
     *
     * @param id
     */
    private Handler handler_getRecipeDetailsFromJuHe = null;

    private void getRecipeDetailsUseID(String id) {
        handler_getRecipeDetailsFromJuHe = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case -1:
                        String Msg = msg.getData().getString("data");
                        Toast.makeText(RecipeDetail.this, Msg, Toast.LENGTH_LONG).show();
                        break;


                    case 1:
                        Log.d(TAG, "即将拆分数据，并设置到VIew");
                        Data data = (Data) msg.getData().getSerializable("data");
                        setView(data);

                        //后台获取用户状态--是否可以收藏点赞，以及数据
                        XXSharedPreferences shared_file = new XXSharedPreferences(MainActivity.SHAREDSESSIONIDSAVEEDNAME);
                        String usernumber = shared_file.get(RecipeDetail.this, "usernumber", "").toString();
                        String sessionid = shared_file.get(RecipeDetail.this, "sessionid", "").toString();

                        getUserLikeState(usernumber, sessionid, data.getId(), data.getTitle());
                        break;
                }
            }
        };

        XXHttpClient client = new XXHttpClient(Util.URL_GETRECIPEDETAILS_JUHE, true,
                new XXHttpClient.XXHttpResponseListener() {
                    @Override
                    public void onSuccess(int i, byte[] bytes) {
                        Log.d(TAG, "根据ID索引的返回," + new String(bytes));
                        AllInfo allInfo = new Gson().fromJson(new String(bytes), AllInfo.class);
                        Log.d(TAG, allInfo.getResult().getData().size() + "size()");
                        if (allInfo.getResultcode().equals("200")) {
                            if (allInfo.getResult().getData().size() > 0) {
                                Data data = allInfo.getResult().getData().get(0);//因为是单ID索引，所以只取一个数据
                                Util.sendMsgToHandler(handler_getRecipeDetailsFromJuHe, data, true);
                            } else {
                                Util.sendMsgToHandler(handler_getRecipeDetailsFromJuHe, allInfo.getReason(), false);
                            }
//
                        } else {
                            Util.sendMsgToHandler(handler_getRecipeDetailsFromJuHe, allInfo.getReason(), false);
                        }
                    }

                    @Override
                    public void onError(int i, Throwable throwable) {
                        Log.e(TAG, "根据ID索引数据网络异常");
                        Util.sendMsgToHandler(handler_getRecipeDetailsFromJuHe, "网络异常", false);
                    }

                    @Override
                    public void onProgress(long bytesWritten, long totalSize) {

                    }
                });
        client.put("key", Util.APPKEY);
        client.put("id", String.valueOf(id));
        client.doGet(15000);
    }

    private Handler handler_getUserLikeState = null;

    private void getUserLikeState(String usernumber, String sessionid, String id, String title) {

        handler_getUserLikeState = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case -1:
                        Toast.makeText(RecipeDetail.this, msg.getData().getString("data"), Toast.LENGTH_SHORT).show();
                        break;

                    case 1:
                        RespData respData = (RespData) msg.getData().getSerializable("data");
                        tv_star_number.setText(respData.getCom_star_numbers());
                        tv_like_number.setText(respData.getCom_like_number());
                        tv_comment_number.setText(respData.getCom_comment_number());
                        if (!respData.isIsUserStar()) {
                            //点亮收藏按钮，设置不可点击
                            star_button.setLiked(true);
                        }

                        if (!respData.isIsUserLike()) {
                            like_button.setLiked(true);
                        }
                        break;
                }
            }
        };

        XXHttpClient client = new XXHttpClient(Util.URL_SERVICE_GETALLRECIPEDATA
                , true, new XXHttpClient.XXHttpResponseListener() {
            @Override
            public void onSuccess(int i, byte[] bytes) {
                Log.d(TAG, "获取所有菜单信息成功，" + new String(bytes));
                ResultToApp resultToApp = new Gson().fromJson(new String(bytes), ResultToApp.class);
                if (resultToApp.getErrorCode() == 9000) {
                    Util.sendMsgToHandler(handler_getUserLikeState, resultToApp.getRespData(), true);
                } else {
                    Util.sendMsgToHandler(handler_getUserLikeState, resultToApp.getResultMsg(), false);
                }
            }

            @Override
            public void onError(int i, Throwable throwable) {
                Log.d(TAG, "获取所有菜单信息失败，网络异常");
                Util.sendMsgToHandler(handler_getUserLikeState, "网络异常", false);
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {

            }
        });

        client.put("usernumber", usernumber);
        client.put("sessionid", sessionid);
        client.put("com_id", id);
        client.put("com_name", title);

        client.doPost(15000);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return false;
    }

    /**
     * 使用获取到的数据对象填充view
     *
     * @param data
     */
    private void setView(Data data) {
        String image_url = null;
        if (data.getAlbums().size() > 0) {
            image_url = data.getAlbums().get(0);
        }
        new XXImagesLoader(null, true, cn.com.xxutils.R.drawable.ic_stub,
                cn.com.xxutils.R.drawable.ic_stub,
                cn.com.xxutils.R.drawable.ic_stub)
                .disPlayImage(image_url, (ImageView) findViewById(R.id.iv_recipedetail));
        tv_recipe_title.setText(data.getTitle());
        tv_recipe_detail.setText(data.getImtro());
        String[] Ingredients = data.getIngredients().split(";");//主料
        String[] Burden = data.getBurden().split(";");
        Log.d(TAG, "返回主料长度：" + Ingredients.length);
        for (int i = 0; i < data.getIngredients().split(";").length; i++) {
            Log.d(TAG, "所有主料信息：" + data.getIngredients().split(";")[i]);
        }
        ArrayList<Steps> stepses = data.getSteps();

        adapter.removeAll();
        for (int i = 0; i < stepses.size(); i++) {
            adapter.addItem(new RecipeDetailsInfo(stepses.get(i).getImg(), stepses.get(i).getStep()));
            Log.d(TAG, "-------------" + stepses.get(i).getImg() + "----------" + stepses.get(i).getStep());
        }
        setListViewHeightBasedOnChildren(lv_recipe_detail);
        ScrollView sv_recipe = (ScrollView) findViewById(R.id.sv_recipe);
        sv_recipe.smoothScrollTo(0, 0);
        adapter.notifyDataSetChanged();

        //主料相关设置
//        if (Ingredients.length > 0) {
//            tv_main_1_title.setText(Ingredients[0].split(",")[0]);
//            tv_main_1_value.setText(Ingredients[0].split(",")[1]);
////            lt_main_2.setVisibility(Ingredients.length >= 2 ? View.INVISIBLE : View.GONE);
//            tv_main_2_title.setText(Ingredients.length >= 2 ? Ingredients[1].split(",")[0] : "");
//            tv_main_2_value.setText(Ingredients.length >= 2 ? Ingredients[1].split(",")[1] : "");
//
//            lt_main_3.setVisibility(Ingredients.length >= 3 ? View.VISIBLE : View.GONE);
////            lt_main_3_xian.setVisibility(Ingredients.length >= 3 ? View.VISIBLE : View.GONE);
//            lt_main_4.setVisibility(Ingredients.length >= 3 ? View.VISIBLE : View.GONE);
////            lt_main_4_xian.setVisibility(Ingredients.length >= 3 ? View.VISIBLE : View.GONE);
//
//            tv_main_3_title.setText(Ingredients.length >= 3 ? Ingredients[2].split(",")[0] : "");
//            tv_main_3_value.setText(Ingredients.length >= 3 ? Ingredients[2].split(",")[1] : "");
//            tv_main_4_title.setText(Ingredients.length >= 4 ? Ingredients[3].split(",")[0] : "");
//            tv_main_4_value.setText(Ingredients.length >= 4 ? Ingredients[3].split(",")[1] : "");
//        }

        switch (Ingredients.length) {
            case 1://如果返回主料为一个时，设置第一个主料的标题和value，将第二个标题的title和value设置为空
                tv_main_1_title.setText(Ingredients[0].split(",")[0]);
                tv_main_1_value.setText(Ingredients[0].split(",")[1]);

                tv_main_2_title.setText("");
                tv_main_2_value.setText("");

                break;
            case 2:
                tv_main_1_title.setText(Ingredients[0].split(",")[0]);
                tv_main_1_value.setText(Ingredients[0].split(",")[1]);

                tv_main_2_title.setText("");
                tv_main_2_value.setText("");
                //
                tv_main_2_title.setText(Ingredients[1].split(",")[0]);
                tv_main_2_value.setText(Ingredients[1].split(",")[1]);
                break;
            case 3:
                tv_main_1_title.setText(Ingredients[0].split(",")[0]);
                tv_main_1_value.setText(Ingredients[0].split(",")[1]);

                tv_main_2_title.setText("");
                tv_main_2_value.setText("");
                //
                tv_main_2_title.setText(Ingredients[1].split(",")[0]);
                tv_main_2_value.setText(Ingredients[1].split(",")[1]);
                //
                lt_main_1_xian.setVisibility(View.VISIBLE);
                lt_main_2_xian.setVisibility(View.VISIBLE);
                lt_main_3.setVisibility(View.VISIBLE);
                lt_main_4.setVisibility(View.VISIBLE);
                tv_main_3_title.setText(Ingredients[2].split(",")[0]);
                tv_main_3_value.setText(Ingredients[2].split(",")[1]);
                tv_main_4_title.setText("");
                tv_main_4_value.setText("");
                lt_main_9_xian.setVisibility(View.VISIBLE);
                lt_main_10_xian.setVisibility(View.VISIBLE);
                break;
            case 4:
                tv_main_1_title.setText(Ingredients[0].split(",")[0]);
                tv_main_1_value.setText(Ingredients[0].split(",")[1]);

                tv_main_2_title.setText("");
                tv_main_2_value.setText("");
                //
                tv_main_2_title.setText(Ingredients[1].split(",")[0]);
                tv_main_2_value.setText(Ingredients[1].split(",")[1]);
                //
                lt_main_1_xian.setVisibility(View.VISIBLE);
                lt_main_2_xian.setVisibility(View.VISIBLE);
                lt_main_3.setVisibility(View.VISIBLE);
                lt_main_4.setVisibility(View.VISIBLE);
                tv_main_3_title.setText(Ingredients[2].split(",")[0]);
                tv_main_3_value.setText(Ingredients[2].split(",")[1]);
//                tv_main_4_title.setText("");
//                tv_main_4_value.setText("");
                lt_main_9_xian.setVisibility(View.VISIBLE);
                lt_main_10_xian.setVisibility(View.VISIBLE);
                //
                tv_main_4_title.setText(Ingredients[3].split(",")[0]);
                tv_main_4_value.setText(Ingredients[3].split(",")[1]);
                break;
            case 5:
                tv_main_1_title.setText(Ingredients[0].split(",")[0]);
                tv_main_1_value.setText(Ingredients[0].split(",")[1]);

                tv_main_2_title.setText("");
                tv_main_2_value.setText("");
                //
                tv_main_2_title.setText(Ingredients[1].split(",")[0]);
                tv_main_2_value.setText(Ingredients[1].split(",")[1]);
                //
                lt_main_1_xian.setVisibility(View.VISIBLE);
                lt_main_2_xian.setVisibility(View.VISIBLE);
                lt_main_3.setVisibility(View.VISIBLE);
                lt_main_4.setVisibility(View.VISIBLE);
                tv_main_3_title.setText(Ingredients[2].split(",")[0]);
                tv_main_3_value.setText(Ingredients[2].split(",")[1]);
//                tv_main_4_title.setText("");
//                tv_main_4_value.setText("");
                lt_main_9_xian.setVisibility(View.VISIBLE);
                lt_main_10_xian.setVisibility(View.VISIBLE);
                //
                tv_main_4_title.setText(Ingredients[3].split(",")[0]);
                tv_main_4_value.setText(Ingredients[3].split(",")[1]);

                //5
                lt_main_3_xian.setVisibility(View.VISIBLE);
                lt_main_4_xian.setVisibility(View.VISIBLE);
                lt_main_5.setVisibility(View.VISIBLE);
                lt_main_6.setVisibility(View.VISIBLE);
                tv_main_5_title.setText(Ingredients[4].split(",")[0]);
                tv_main_5_value.setText(Ingredients[4].split(",")[1]);
                tv_main_6_title.setText("");
                tv_main_6_value.setText("");

                break;
            case 6:

                tv_main_1_title.setText(Ingredients[0].split(",")[0]);
                tv_main_1_value.setText(Ingredients[0].split(",")[1]);

                tv_main_2_title.setText("");
                tv_main_2_value.setText("");
                //
                tv_main_2_title.setText(Ingredients[1].split(",")[0]);
                tv_main_2_value.setText(Ingredients[1].split(",")[1]);
                //
                lt_main_1_xian.setVisibility(View.VISIBLE);
                lt_main_2_xian.setVisibility(View.VISIBLE);
                lt_main_3.setVisibility(View.VISIBLE);
                lt_main_4.setVisibility(View.VISIBLE);
                tv_main_3_title.setText(Ingredients[2].split(",")[0]);
                tv_main_3_value.setText(Ingredients[2].split(",")[1]);
//                tv_main_4_title.setText("");
//                tv_main_4_value.setText("");
                lt_main_9_xian.setVisibility(View.VISIBLE);
                lt_main_10_xian.setVisibility(View.VISIBLE);
                //
                tv_main_4_title.setText(Ingredients[3].split(",")[0]);
                tv_main_4_value.setText(Ingredients[3].split(",")[1]);

                //5
                lt_main_3_xian.setVisibility(View.VISIBLE);
                lt_main_4_xian.setVisibility(View.VISIBLE);
                lt_main_5.setVisibility(View.VISIBLE);
                lt_main_6.setVisibility(View.VISIBLE);
                tv_main_5_title.setText(Ingredients[4].split(",")[0]);
                tv_main_5_value.setText(Ingredients[4].split(",")[1]);
//                tv_main_6_title.setText("");
//                tv_main_6_value.setText("");
                tv_main_6_title.setText(Ingredients[5].split(",")[0]);
                tv_main_6_value.setText(Ingredients[5].split(",")[1]);
                break;
            case 7:
                tv_main_1_title.setText(Ingredients[0].split(",")[0]);
                tv_main_1_value.setText(Ingredients[0].split(",")[1]);

                tv_main_2_title.setText("");
                tv_main_2_value.setText("");
                //
                tv_main_2_title.setText(Ingredients[1].split(",")[0]);
                tv_main_2_value.setText(Ingredients[1].split(",")[1]);
                //
                lt_main_1_xian.setVisibility(View.VISIBLE);
                lt_main_2_xian.setVisibility(View.VISIBLE);
                lt_main_3.setVisibility(View.VISIBLE);
                lt_main_4.setVisibility(View.VISIBLE);
                tv_main_3_title.setText(Ingredients[2].split(",")[0]);
                tv_main_3_value.setText(Ingredients[2].split(",")[1]);
//                tv_main_4_title.setText("");
//                tv_main_4_value.setText("");
                lt_main_9_xian.setVisibility(View.VISIBLE);
                lt_main_10_xian.setVisibility(View.VISIBLE);
                //
                tv_main_4_title.setText(Ingredients[3].split(",")[0]);
                tv_main_4_value.setText(Ingredients[3].split(",")[1]);

                //5
                lt_main_3_xian.setVisibility(View.VISIBLE);
                lt_main_4_xian.setVisibility(View.VISIBLE);
                lt_main_5.setVisibility(View.VISIBLE);
                lt_main_6.setVisibility(View.VISIBLE);
                tv_main_5_title.setText(Ingredients[4].split(",")[0]);
                tv_main_5_value.setText(Ingredients[4].split(",")[1]);
//                tv_main_6_title.setText("");
//                tv_main_6_value.setText("");
                tv_main_6_title.setText(Ingredients[5].split(",")[0]);
                tv_main_6_value.setText(Ingredients[5].split(",")[1]);
                //
                lt_main_5_xian.setVisibility(View.VISIBLE);
                lt_main_6_xian.setVisibility(View.VISIBLE);
                lt_main_7.setVisibility(View.VISIBLE);
                lt_main_8.setVisibility(View.VISIBLE);
                tv_main_7_title.setText(Ingredients[6].split(",")[0]);
                tv_main_7_value.setText(Ingredients[6].split(",")[1]);
                tv_main_8_title.setText("");
                tv_main_8_value.setText("");
                break;
            case 8:

                tv_main_1_title.setText(Ingredients[0].split(",")[0]);
                tv_main_1_value.setText(Ingredients[0].split(",")[1]);

                tv_main_2_title.setText("");
                tv_main_2_value.setText("");
                //
                tv_main_2_title.setText(Ingredients[1].split(",")[0]);
                tv_main_2_value.setText(Ingredients[1].split(",")[1]);
                //
                lt_main_1_xian.setVisibility(View.VISIBLE);
                lt_main_2_xian.setVisibility(View.VISIBLE);
                lt_main_3.setVisibility(View.VISIBLE);
                lt_main_4.setVisibility(View.VISIBLE);
                tv_main_3_title.setText(Ingredients[2].split(",")[0]);
                tv_main_3_value.setText(Ingredients[2].split(",")[1]);
//                tv_main_4_title.setText("");
//                tv_main_4_value.setText("");
                lt_main_9_xian.setVisibility(View.VISIBLE);
                lt_main_10_xian.setVisibility(View.VISIBLE);
                //
                tv_main_4_title.setText(Ingredients[3].split(",")[0]);
                tv_main_4_value.setText(Ingredients[3].split(",")[1]);

                //5
                lt_main_3_xian.setVisibility(View.VISIBLE);
                lt_main_4_xian.setVisibility(View.VISIBLE);
                lt_main_5.setVisibility(View.VISIBLE);
                lt_main_6.setVisibility(View.VISIBLE);
                tv_main_5_title.setText(Ingredients[4].split(",")[0]);
                tv_main_5_value.setText(Ingredients[4].split(",")[1]);
//                tv_main_6_title.setText("");
//                tv_main_6_value.setText("");
                tv_main_6_title.setText(Ingredients[5].split(",")[0]);
                tv_main_6_value.setText(Ingredients[5].split(",")[1]);
                //
                lt_main_5_xian.setVisibility(View.VISIBLE);
                lt_main_6_xian.setVisibility(View.VISIBLE);
                lt_main_7.setVisibility(View.VISIBLE);
                lt_main_8.setVisibility(View.VISIBLE);
                tv_main_7_title.setText(Ingredients[6].split(",")[0]);
                tv_main_7_value.setText(Ingredients[6].split(",")[1]);
//                tv_main_8_title.setText("");
//                tv_main_8_value.setText("");
                tv_main_8_title.setText(Ingredients[7].split(",")[0]);
                tv_main_8_value.setText(Ingredients[7].split(",")[1]);

                break;

            case 9:

                tv_main_1_title.setText(Ingredients[0].split(",")[0]);
                tv_main_1_value.setText(Ingredients[0].split(",")[1]);

                tv_main_2_title.setText("");
                tv_main_2_value.setText("");
                //
                tv_main_2_title.setText(Ingredients[1].split(",")[0]);
                tv_main_2_value.setText(Ingredients[1].split(",")[1]);
                //
                lt_main_1_xian.setVisibility(View.VISIBLE);
                lt_main_2_xian.setVisibility(View.VISIBLE);
                lt_main_3.setVisibility(View.VISIBLE);
                lt_main_4.setVisibility(View.VISIBLE);
                tv_main_3_title.setText(Ingredients[2].split(",")[0]);
                tv_main_3_value.setText(Ingredients[2].split(",")[1]);
//                tv_main_4_title.setText("");
//                tv_main_4_value.setText("");
                lt_main_9_xian.setVisibility(View.VISIBLE);
                lt_main_10_xian.setVisibility(View.VISIBLE);
                //
                tv_main_4_title.setText(Ingredients[3].split(",")[0]);
                tv_main_4_value.setText(Ingredients[3].split(",")[1]);

                //5
                lt_main_3_xian.setVisibility(View.VISIBLE);
                lt_main_4_xian.setVisibility(View.VISIBLE);
                lt_main_5.setVisibility(View.VISIBLE);
                lt_main_6.setVisibility(View.VISIBLE);
                tv_main_5_title.setText(Ingredients[4].split(",")[0]);
                tv_main_5_value.setText(Ingredients[4].split(",")[1]);
//                tv_main_6_title.setText("");
//                tv_main_6_value.setText("");
                tv_main_6_title.setText(Ingredients[5].split(",")[0]);
                tv_main_6_value.setText(Ingredients[5].split(",")[1]);
                //
                lt_main_5_xian.setVisibility(View.VISIBLE);
                lt_main_6_xian.setVisibility(View.VISIBLE);
                lt_main_7.setVisibility(View.VISIBLE);
                lt_main_8.setVisibility(View.VISIBLE);
                tv_main_7_title.setText(Ingredients[6].split(",")[0]);
                tv_main_7_value.setText(Ingredients[6].split(",")[1]);
//                tv_main_8_title.setText("");
//                tv_main_8_value.setText("");
                tv_main_8_title.setText(Ingredients[7].split(",")[0]);
                tv_main_8_value.setText(Ingredients[7].split(",")[1]);
                //
                lt_main_9.setVisibility(View.VISIBLE);
                lt_main_10.setVisibility(View.VISIBLE);
                tv_main_9_title.setText(Ingredients[8].split(",")[0]);
                tv_main_9_value.setText(Ingredients[8].split(",")[1]);
                tv_main_10_title.setText("");
                tv_main_10_value.setText("");
                lt_main_9_xian.setVisibility(View.VISIBLE);
                lt_main_10_xian.setVisibility(View.VISIBLE);
                break;

            case 10:
                tv_main_1_title.setText(Ingredients[0].split(",")[0]);
                tv_main_1_value.setText(Ingredients[0].split(",")[1]);

                tv_main_2_title.setText("");
                tv_main_2_value.setText("");
                //
                tv_main_2_title.setText(Ingredients[1].split(",")[0]);
                tv_main_2_value.setText(Ingredients[1].split(",")[1]);
                //
                lt_main_1_xian.setVisibility(View.VISIBLE);
                lt_main_2_xian.setVisibility(View.VISIBLE);
                lt_main_3.setVisibility(View.VISIBLE);
                lt_main_4.setVisibility(View.VISIBLE);
                tv_main_3_title.setText(Ingredients[2].split(",")[0]);
                tv_main_3_value.setText(Ingredients[2].split(",")[1]);
//                tv_main_4_title.setText("");
//                tv_main_4_value.setText("");
                lt_main_7_xian.setVisibility(View.VISIBLE);
                lt_main_8_xian.setVisibility(View.VISIBLE);
                //
                tv_main_4_title.setText(Ingredients[3].split(",")[0]);
                tv_main_4_value.setText(Ingredients[3].split(",")[1]);

                //5
                lt_main_3_xian.setVisibility(View.VISIBLE);
                lt_main_4_xian.setVisibility(View.VISIBLE);
                lt_main_5.setVisibility(View.VISIBLE);
                lt_main_6.setVisibility(View.VISIBLE);
                tv_main_5_title.setText(Ingredients[4].split(",")[0]);
                tv_main_5_value.setText(Ingredients[4].split(",")[1]);
//                tv_main_6_title.setText("");
//                tv_main_6_value.setText("");
                tv_main_6_title.setText(Ingredients[5].split(",")[0]);
                tv_main_6_value.setText(Ingredients[5].split(",")[1]);
                //
                lt_main_5_xian.setVisibility(View.VISIBLE);
                lt_main_6_xian.setVisibility(View.VISIBLE);
                lt_main_7.setVisibility(View.VISIBLE);
                lt_main_8.setVisibility(View.VISIBLE);
                tv_main_7_title.setText(Ingredients[6].split(",")[0]);
                tv_main_7_value.setText(Ingredients[6].split(",")[1]);
//                tv_main_8_title.setText("");
//                tv_main_8_value.setText("");
                tv_main_8_title.setText(Ingredients[7].split(",")[0]);
                tv_main_8_value.setText(Ingredients[7].split(",")[1]);
                //
                lt_main_9.setVisibility(View.VISIBLE);
                lt_main_10.setVisibility(View.VISIBLE);
                tv_main_9_title.setText(Ingredients[8].split(",")[0]);
                tv_main_9_value.setText(Ingredients[8].split(",")[1]);
//                tv_main_10_title.setText("");
//                tv_main_10_value.setText("");
                tv_main_10_title.setText(Ingredients[9].split(",")[0]);
                tv_main_10_value.setText(Ingredients[9].split(",")[1]);
                lt_main_9_xian.setVisibility(View.VISIBLE);
                lt_main_10_xian.setVisibility(View.VISIBLE);
                break;
        }

        switch (Burden.length) {
            case 1:
                lt_fu_1.setVisibility(View.VISIBLE);
                lt_fu_2.setVisibility(View.VISIBLE);
                lt_fu_1_xian.setVisibility(View.VISIBLE);
                lt_fu_2_xian.setVisibility(View.VISIBLE);
                tv_fu_1_title.setText(Burden[0].split(",")[0]);
                tv_fu_1_value.setText(Burden[0].split(",")[1]);
                tv_fu_2_title.setText("");
                tv_fu_2_value.setText("");
                break;
            case 2:
                lt_fu_1.setVisibility(View.VISIBLE);
                lt_fu_2.setVisibility(View.VISIBLE);
                lt_fu_1_xian.setVisibility(View.VISIBLE);
                lt_fu_2_xian.setVisibility(View.VISIBLE);
                tv_fu_1_title.setText(Burden[0].split(",")[0]);
                tv_fu_1_value.setText(Burden[0].split(",")[1]);
//                tv_fu_2_title.setText("");
//                tv_fu_2_value.setText("");
                tv_fu_2_title.setText(Burden[1].split(",")[0]);
                tv_fu_2_value.setText(Burden[1].split(",")[1]);
                break;
            case 3:
                lt_fu_1.setVisibility(View.VISIBLE);
                lt_fu_2.setVisibility(View.VISIBLE);
                lt_fu_1_xian.setVisibility(View.VISIBLE);
                lt_fu_2_xian.setVisibility(View.VISIBLE);
                tv_fu_1_title.setText(Burden[0].split(",")[0]);
                tv_fu_1_value.setText(Burden[0].split(",")[1]);
//                tv_fu_2_title.setText("");
//                tv_fu_2_value.setText("");
                tv_fu_2_title.setText(Burden[1].split(",")[0]);
                tv_fu_2_value.setText(Burden[1].split(",")[1]);
                //
                lt_fu_3.setVisibility(View.VISIBLE);
                lt_fu_4.setVisibility(View.VISIBLE);
                lt_fu_3_xian.setVisibility(View.VISIBLE);
                lt_fu_4_xian.setVisibility(View.VISIBLE);
                tv_fu_3_title.setText(Burden[2].split(",")[0]);
                tv_fu_3_value.setText(Burden[2].split(",")[1]);
                tv_fu_4_title.setText("");
                tv_fu_4_value.setText("");

                break;
            case 4:
                lt_fu_1.setVisibility(View.VISIBLE);
                lt_fu_2.setVisibility(View.VISIBLE);
                lt_fu_1_xian.setVisibility(View.VISIBLE);
                lt_fu_2_xian.setVisibility(View.VISIBLE);
                tv_fu_1_title.setText(Burden[0].split(",")[0]);
                tv_fu_1_value.setText(Burden[0].split(",")[1]);
//                tv_fu_2_title.setText("");
//                tv_fu_2_value.setText("");
                tv_fu_2_title.setText(Burden[1].split(",")[0]);
                tv_fu_2_value.setText(Burden[1].split(",")[1]);
                //
                lt_fu_3.setVisibility(View.VISIBLE);
                lt_fu_4.setVisibility(View.VISIBLE);
                lt_fu_3_xian.setVisibility(View.VISIBLE);
                lt_fu_4_xian.setVisibility(View.VISIBLE);
                tv_fu_3_title.setText(Burden[2].split(",")[0]);
                tv_fu_3_value.setText(Burden[2].split(",")[1]);
//                tv_fu_4_title.setText("");
//                tv_fu_4_value.setText("");
                tv_fu_4_title.setText(Burden[3].split(",")[0]);
                try {

                    tv_fu_4_value.setText(Burden[3].split(",")[1]);
                } catch (Throwable throwable) {
                    Log.e(TAG, tv_fu_4_value + "");
                    Log.e(TAG, Burden[4] + "");
                    Log.e(TAG, Burden[4].split(",")[1] + "");

                    Log.e(TAG, "设置第四个数据时跑抛出异常");
                }
                break;
            case 5:
                lt_fu_1.setVisibility(View.VISIBLE);
                lt_fu_2.setVisibility(View.VISIBLE);
                lt_fu_1_xian.setVisibility(View.VISIBLE);
                lt_fu_2_xian.setVisibility(View.VISIBLE);
                tv_fu_1_title.setText(Burden[0].split(",")[0]);
                tv_fu_1_value.setText(Burden[0].split(",")[1]);
//                tv_fu_2_title.setText("");
//                tv_fu_2_value.setText("");
                tv_fu_2_title.setText(Burden[1].split(",")[0]);
                tv_fu_2_value.setText(Burden[1].split(",")[1]);
                //
                lt_fu_3.setVisibility(View.VISIBLE);
                lt_fu_4.setVisibility(View.VISIBLE);
                lt_fu_3_xian.setVisibility(View.VISIBLE);
                lt_fu_4_xian.setVisibility(View.VISIBLE);
                tv_fu_3_title.setText(Burden[2].split(",")[0]);
                tv_fu_3_value.setText(Burden[2].split(",")[1]);
//                tv_fu_4_title.setText("");
//                tv_fu_4_value.setText("");
                tv_fu_4_title.setText(Burden[3].split(",")[0]);
                tv_fu_4_value.setText(Burden[3].split(",")[1]);
                //
                lt_fu_5.setVisibility(View.VISIBLE);
                lt_fu_6.setVisibility(View.VISIBLE);
                lt_fu_5_xian.setVisibility(View.VISIBLE);
                lt_fu_6_xian.setVisibility(View.VISIBLE);
                tv_fu_5_title.setText(Burden[4].split(",")[0]);
                tv_fu_5_value.setText(Burden[4].split(",")[1]);
                tv_fu_6_title.setText("");
                tv_fu_6_value.setText("");
                break;
            case 6:
                lt_fu_1.setVisibility(View.VISIBLE);
                lt_fu_2.setVisibility(View.VISIBLE);
                lt_fu_1_xian.setVisibility(View.VISIBLE);
                lt_fu_2_xian.setVisibility(View.VISIBLE);
                tv_fu_1_title.setText(Burden[0].split(",")[0]);
                tv_fu_1_value.setText(Burden[0].split(",")[1]);
//                tv_fu_2_title.setText("");
//                tv_fu_2_value.setText("");
                tv_fu_2_title.setText(Burden[1].split(",")[0]);
                tv_fu_2_value.setText(Burden[1].split(",")[1]);
                //
                lt_fu_3.setVisibility(View.VISIBLE);
                lt_fu_4.setVisibility(View.VISIBLE);
                lt_fu_3_xian.setVisibility(View.VISIBLE);
                lt_fu_4_xian.setVisibility(View.VISIBLE);
                tv_fu_3_title.setText(Burden[2].split(",")[0]);
                tv_fu_3_value.setText(Burden[2].split(",")[1]);
//                tv_fu_4_title.setText("");
//                tv_fu_4_value.setText("");
                tv_fu_4_title.setText(Burden[3].split(",")[0]);
                tv_fu_4_value.setText(Burden[3].split(",")[1]);
                //
                lt_fu_5.setVisibility(View.VISIBLE);
                lt_fu_6.setVisibility(View.VISIBLE);
                lt_fu_5_xian.setVisibility(View.VISIBLE);
                lt_fu_6_xian.setVisibility(View.VISIBLE);
                tv_fu_5_title.setText(Burden[4].split(",")[0]);
                tv_fu_5_value.setText(Burden[4].split(",")[1]);
//                tv_fu_6_title.setText("");
//                tv_fu_6_value.setText("");
                tv_fu_6_title.setText(Burden[5].split(",")[0]);
                tv_fu_6_value.setText(Burden[5].split(",")[1]);
                break;
            case 7:
                lt_fu_1.setVisibility(View.VISIBLE);
                lt_fu_2.setVisibility(View.VISIBLE);
                lt_fu_1_xian.setVisibility(View.VISIBLE);
                lt_fu_2_xian.setVisibility(View.VISIBLE);
                tv_fu_1_title.setText(Burden[0].split(",")[0]);
                tv_fu_1_value.setText(Burden[0].split(",")[1]);
//                tv_fu_2_title.setText("");
//                tv_fu_2_value.setText("");
                tv_fu_2_title.setText(Burden[1].split(",")[0]);
                tv_fu_2_value.setText(Burden[1].split(",")[1]);
                //
                lt_fu_3.setVisibility(View.VISIBLE);
                lt_fu_4.setVisibility(View.VISIBLE);
                lt_fu_3_xian.setVisibility(View.VISIBLE);
                lt_fu_4_xian.setVisibility(View.VISIBLE);
                tv_fu_3_title.setText(Burden[2].split(",")[0]);
                tv_fu_3_value.setText(Burden[2].split(",")[1]);
//                tv_fu_4_title.setText("");
//                tv_fu_4_value.setText("");
                tv_fu_4_title.setText(Burden[3].split(",")[0]);
                tv_fu_4_value.setText(Burden[3].split(",")[1]);
                //
                lt_fu_5.setVisibility(View.VISIBLE);
                lt_fu_6.setVisibility(View.VISIBLE);
                lt_fu_5_xian.setVisibility(View.VISIBLE);
                lt_fu_6_xian.setVisibility(View.VISIBLE);
                tv_fu_5_title.setText(Burden[4].split(",")[0]);
                tv_fu_5_value.setText(Burden[4].split(",")[1]);
//                tv_fu_6_title.setText("");
//                tv_fu_6_value.setText("");
                tv_fu_6_title.setText(Burden[5].split(",")[0]);
                tv_fu_6_value.setText(Burden[5].split(",")[1]);
                //
                lt_fu_7.setVisibility(View.VISIBLE);
                lt_fu_8.setVisibility(View.VISIBLE);
                lt_fu_7_xian.setVisibility(View.VISIBLE);
                lt_fu_8_xian.setVisibility(View.VISIBLE);
                tv_fu_7_title.setText(Burden[6].split(",")[0]);
                tv_fu_7_value.setText(Burden[6].split(",")[1]);
                tv_fu_8_title.setText("");
                tv_fu_8_value.setText("");
                break;
            case 8:
                lt_fu_1.setVisibility(View.VISIBLE);
                lt_fu_2.setVisibility(View.VISIBLE);
                lt_fu_1_xian.setVisibility(View.VISIBLE);
                lt_fu_2_xian.setVisibility(View.VISIBLE);
                tv_fu_1_title.setText(Burden[0].split(",")[0]);
                tv_fu_1_value.setText(Burden[0].split(",")[1]);
                tv_fu_2_title.setText(Burden[1].split(",")[0]);
                tv_fu_2_value.setText(Burden[1].split(",")[1]);
                //
                lt_fu_3.setVisibility(View.VISIBLE);
                lt_fu_4.setVisibility(View.VISIBLE);
                lt_fu_3_xian.setVisibility(View.VISIBLE);
                lt_fu_4_xian.setVisibility(View.VISIBLE);
                tv_fu_3_title.setText(Burden[2].split(",")[0]);
                tv_fu_3_value.setText(Burden[2].split(",")[1]);
                tv_fu_4_title.setText(Burden[3].split(",")[0]);
                tv_fu_4_value.setText(Burden[3].split(",")[1]);
                //
                lt_fu_5.setVisibility(View.VISIBLE);
                lt_fu_6.setVisibility(View.VISIBLE);
                lt_fu_5_xian.setVisibility(View.VISIBLE);
                lt_fu_6_xian.setVisibility(View.VISIBLE);
                tv_fu_5_title.setText(Burden[4].split(",")[0]);
                tv_fu_5_value.setText(Burden[4].split(",")[1]);
                tv_fu_6_title.setText(Burden[5].split(",")[0]);
                tv_fu_6_value.setText(Burden[5].split(",")[1]);
                //
                lt_fu_7.setVisibility(View.VISIBLE);
                lt_fu_8.setVisibility(View.VISIBLE);
                lt_fu_7_xian.setVisibility(View.VISIBLE);
                lt_fu_8_xian.setVisibility(View.VISIBLE);
                tv_fu_7_title.setText(Burden[6].split(",")[0]);
                tv_fu_7_value.setText(Burden[6].split(",")[1]);
                tv_fu_8_title.setText(Burden[7].split(",")[0]);
                tv_fu_8_value.setText(Burden[7].split(",")[1]);
                break;
            case 9:
                lt_fu_1.setVisibility(View.VISIBLE);
                lt_fu_2.setVisibility(View.VISIBLE);
                lt_fu_1_xian.setVisibility(View.VISIBLE);
                lt_fu_2_xian.setVisibility(View.VISIBLE);
                tv_fu_1_title.setText(Burden[0].split(",")[0]);
                tv_fu_1_value.setText(Burden[0].split(",")[1]);
                tv_fu_2_title.setText(Burden[1].split(",")[0]);
                tv_fu_2_value.setText(Burden[1].split(",")[1]);
                //
                lt_fu_3.setVisibility(View.VISIBLE);
                lt_fu_4.setVisibility(View.VISIBLE);
                lt_fu_3_xian.setVisibility(View.VISIBLE);
                lt_fu_4_xian.setVisibility(View.VISIBLE);
                tv_fu_3_title.setText(Burden[2].split(",")[0]);
                tv_fu_3_value.setText(Burden[2].split(",")[1]);
                tv_fu_4_title.setText(Burden[3].split(",")[0]);
                tv_fu_4_value.setText(Burden[3].split(",")[1]);
                //
                lt_fu_5.setVisibility(View.VISIBLE);
                lt_fu_6.setVisibility(View.VISIBLE);
                lt_fu_5_xian.setVisibility(View.VISIBLE);
                lt_fu_6_xian.setVisibility(View.VISIBLE);
                tv_fu_5_title.setText(Burden[4].split(",")[0]);
                tv_fu_5_value.setText(Burden[4].split(",")[1]);
                tv_fu_6_title.setText(Burden[5].split(",")[0]);
                tv_fu_6_value.setText(Burden[5].split(",")[1]);
                //
                lt_fu_7.setVisibility(View.VISIBLE);
                lt_fu_8.setVisibility(View.VISIBLE);
                lt_fu_7_xian.setVisibility(View.VISIBLE);
                lt_fu_8_xian.setVisibility(View.VISIBLE);
                tv_fu_7_title.setText(Burden[6].split(",")[0]);
                tv_fu_7_value.setText(Burden[6].split(",")[1]);
                tv_fu_8_title.setText(Burden[7].split(",")[0]);
                tv_fu_8_value.setText(Burden[7].split(",")[1]);
                //
                lt_fu_9.setVisibility(View.VISIBLE);
                lt_fu_10.setVisibility(View.VISIBLE);
                lt_fu_9_xian.setVisibility(View.VISIBLE);
                lt_fu_10_xian.setVisibility(View.VISIBLE);
                tv_fu_9_title.setText(Burden[8].split(",")[0]);
                tv_fu_9_value.setText(Burden[8].split(",")[1]);
                tv_fu_10_title.setText("");
                tv_fu_10_value.setText("");
                break;
            case 10:
                lt_fu_1.setVisibility(View.VISIBLE);
                lt_fu_2.setVisibility(View.VISIBLE);
                lt_fu_1_xian.setVisibility(View.VISIBLE);
                lt_fu_2_xian.setVisibility(View.VISIBLE);
                tv_fu_1_title.setText(Burden[0].split(",")[0]);
                tv_fu_1_value.setText(Burden[0].split(",")[1]);
                tv_fu_2_title.setText(Burden[1].split(",")[0]);
                tv_fu_2_value.setText(Burden[1].split(",")[1]);
                //
                lt_fu_3.setVisibility(View.VISIBLE);
                lt_fu_4.setVisibility(View.VISIBLE);
                lt_fu_3_xian.setVisibility(View.VISIBLE);
                lt_fu_4_xian.setVisibility(View.VISIBLE);
                tv_fu_3_title.setText(Burden[2].split(",")[0]);
                tv_fu_3_value.setText(Burden[2].split(",")[1]);
                tv_fu_4_title.setText(Burden[3].split(",")[0]);
                tv_fu_4_value.setText(Burden[3].split(",")[1]);
                //
                lt_fu_5.setVisibility(View.VISIBLE);
                lt_fu_6.setVisibility(View.VISIBLE);
                lt_fu_5_xian.setVisibility(View.VISIBLE);
                lt_fu_6_xian.setVisibility(View.VISIBLE);
                tv_fu_5_title.setText(Burden[4].split(",")[0]);
                tv_fu_5_value.setText(Burden[4].split(",")[1]);
                tv_fu_6_title.setText(Burden[5].split(",")[0]);
                tv_fu_6_value.setText(Burden[5].split(",")[1]);
                //
                lt_fu_7.setVisibility(View.VISIBLE);
                lt_fu_8.setVisibility(View.VISIBLE);
                lt_fu_7_xian.setVisibility(View.VISIBLE);
                lt_fu_8_xian.setVisibility(View.VISIBLE);
                tv_fu_7_title.setText(Burden[6].split(",")[0]);
                tv_fu_7_value.setText(Burden[6].split(",")[1]);
                tv_fu_8_title.setText(Burden[7].split(",")[0]);
                tv_fu_8_value.setText(Burden[7].split(",")[1]);
                //
                lt_fu_9.setVisibility(View.VISIBLE);
                lt_fu_10.setVisibility(View.VISIBLE);
                lt_fu_9_xian.setVisibility(View.VISIBLE);
                lt_fu_10_xian.setVisibility(View.VISIBLE);
                tv_fu_9_title.setText(Burden[8].split(",")[0]);
                tv_fu_9_value.setText(Burden[8].split(",")[1]);
//                tv_fu_10_title.setText("");
//                tv_fu_10_value.setText("");
                tv_fu_10_title.setText(Burden[9].split(",")[0]);
                tv_fu_10_value.setText(Burden[9].split(",")[1]);
                break;
            case 11:
                lt_fu_1.setVisibility(View.VISIBLE);
                lt_fu_2.setVisibility(View.VISIBLE);
                lt_fu_1_xian.setVisibility(View.VISIBLE);
                lt_fu_2_xian.setVisibility(View.VISIBLE);
                tv_fu_1_title.setText(Burden[0].split(",")[0]);
                tv_fu_1_value.setText(Burden[0].split(",")[1]);
                tv_fu_2_title.setText(Burden[1].split(",")[0]);
                tv_fu_2_value.setText(Burden[1].split(",")[1]);
                //
                lt_fu_3.setVisibility(View.VISIBLE);
                lt_fu_4.setVisibility(View.VISIBLE);
                lt_fu_3_xian.setVisibility(View.VISIBLE);
                lt_fu_4_xian.setVisibility(View.VISIBLE);
                tv_fu_3_title.setText(Burden[2].split(",")[0]);
                tv_fu_3_value.setText(Burden[2].split(",")[1]);
                tv_fu_4_title.setText(Burden[3].split(",")[0]);
                tv_fu_4_value.setText(Burden[3].split(",")[1]);
                //
                lt_fu_5.setVisibility(View.VISIBLE);
                lt_fu_6.setVisibility(View.VISIBLE);
                lt_fu_5_xian.setVisibility(View.VISIBLE);
                lt_fu_6_xian.setVisibility(View.VISIBLE);
                tv_fu_5_title.setText(Burden[4].split(",")[0]);
                tv_fu_5_value.setText(Burden[4].split(",")[1]);
                tv_fu_6_title.setText(Burden[5].split(",")[0]);
                tv_fu_6_value.setText(Burden[5].split(",")[1]);
                //
                lt_fu_7.setVisibility(View.VISIBLE);
                lt_fu_8.setVisibility(View.VISIBLE);
                lt_fu_7_xian.setVisibility(View.VISIBLE);
                lt_fu_8_xian.setVisibility(View.VISIBLE);
                tv_fu_7_title.setText(Burden[6].split(",")[0]);
                tv_fu_7_value.setText(Burden[6].split(",")[1]);
                tv_fu_8_title.setText(Burden[7].split(",")[0]);
                tv_fu_8_value.setText(Burden[7].split(",")[1]);
                //
                lt_fu_9.setVisibility(View.VISIBLE);
                lt_fu_10.setVisibility(View.VISIBLE);
                lt_fu_9_xian.setVisibility(View.VISIBLE);
                lt_fu_10_xian.setVisibility(View.VISIBLE);
                tv_fu_9_title.setText(Burden[8].split(",")[0]);
                tv_fu_9_value.setText(Burden[8].split(",")[1]);
//                tv_fu_10_title.setText("");
//                tv_fu_10_value.setText("");
                tv_fu_10_title.setText(Burden[9].split(",")[0]);
                tv_fu_10_value.setText(Burden[9].split(",")[1]);
                //
                lt_fu_11.setVisibility(View.VISIBLE);
                lt_fu_12.setVisibility(View.VISIBLE);
                lt_fu_11_xian.setVisibility(View.VISIBLE);
                lt_fu_12_xian.setVisibility(View.VISIBLE);
                tv_fu_11_title.setText(Burden[10].split(",")[0]);
                tv_fu_11_value.setText(Burden[10].split(",")[1]);
                tv_fu_12_title.setText("");
                tv_fu_12_value.setText("");
                break;
            case 12:
                lt_fu_1.setVisibility(View.VISIBLE);
                lt_fu_2.setVisibility(View.VISIBLE);
                lt_fu_1_xian.setVisibility(View.VISIBLE);
                lt_fu_2_xian.setVisibility(View.VISIBLE);
                tv_fu_1_title.setText(Burden[0].split(",")[0]);
                tv_fu_1_value.setText(Burden[0].split(",")[1]);
                tv_fu_2_title.setText(Burden[1].split(",")[0]);
                tv_fu_2_value.setText(Burden[1].split(",")[1]);
                //
                lt_fu_3.setVisibility(View.VISIBLE);
                lt_fu_4.setVisibility(View.VISIBLE);
                lt_fu_3_xian.setVisibility(View.VISIBLE);
                lt_fu_4_xian.setVisibility(View.VISIBLE);
                tv_fu_3_title.setText(Burden[2].split(",")[0]);
                tv_fu_3_value.setText(Burden[2].split(",")[1]);
                tv_fu_4_title.setText(Burden[3].split(",")[0]);
                tv_fu_4_value.setText(Burden[3].split(",")[1]);
                //
                lt_fu_5.setVisibility(View.VISIBLE);
                lt_fu_6.setVisibility(View.VISIBLE);
                lt_fu_5_xian.setVisibility(View.VISIBLE);
                lt_fu_6_xian.setVisibility(View.VISIBLE);
                tv_fu_5_title.setText(Burden[4].split(",")[0]);
                tv_fu_5_value.setText(Burden[4].split(",")[1]);
                tv_fu_6_title.setText(Burden[5].split(",")[0]);
                tv_fu_6_value.setText(Burden[5].split(",")[1]);
                //
                lt_fu_7.setVisibility(View.VISIBLE);
                lt_fu_8.setVisibility(View.VISIBLE);
                lt_fu_7_xian.setVisibility(View.VISIBLE);
                lt_fu_8_xian.setVisibility(View.VISIBLE);
                tv_fu_7_title.setText(Burden[6].split(",")[0]);
                tv_fu_7_value.setText(Burden[6].split(",")[1]);
                tv_fu_8_title.setText(Burden[7].split(",")[0]);
                tv_fu_8_value.setText(Burden[7].split(",")[1]);
                //
                lt_fu_9.setVisibility(View.VISIBLE);
                lt_fu_10.setVisibility(View.VISIBLE);
                lt_fu_9_xian.setVisibility(View.VISIBLE);
                lt_fu_10_xian.setVisibility(View.VISIBLE);
                tv_fu_9_title.setText(Burden[8].split(",")[0]);
                tv_fu_9_value.setText(Burden[8].split(",")[1]);
//                tv_fu_10_title.setText("");
//                tv_fu_10_value.setText("");
                tv_fu_10_title.setText(Burden[9].split(",")[0]);
                tv_fu_10_value.setText(Burden[9].split(",")[1]);
                //
                lt_fu_11.setVisibility(View.VISIBLE);
                lt_fu_12.setVisibility(View.VISIBLE);
                lt_fu_11_xian.setVisibility(View.VISIBLE);
                lt_fu_12_xian.setVisibility(View.VISIBLE);
                tv_fu_11_title.setText(Burden[10].split(",")[0]);
                tv_fu_11_value.setText(Burden[10].split(",")[1]);
//                tv_fu_12_title.setText("");
//                tv_fu_12_value.setText("");
                tv_fu_12_title.setText(Burden[11].split(",")[0]);
                tv_fu_12_value.setText(Burden[11].split(",")[1]);
                break;
            case 13:
                lt_fu_1.setVisibility(View.VISIBLE);
                lt_fu_2.setVisibility(View.VISIBLE);
                lt_fu_1_xian.setVisibility(View.VISIBLE);
                lt_fu_2_xian.setVisibility(View.VISIBLE);
                tv_fu_1_title.setText(Burden[0].split(",")[0]);
                tv_fu_1_value.setText(Burden[0].split(",")[1]);
                tv_fu_2_title.setText(Burden[1].split(",")[0]);
                tv_fu_2_value.setText(Burden[1].split(",")[1]);
                //
                lt_fu_3.setVisibility(View.VISIBLE);
                lt_fu_4.setVisibility(View.VISIBLE);
                lt_fu_3_xian.setVisibility(View.VISIBLE);
                lt_fu_4_xian.setVisibility(View.VISIBLE);
                tv_fu_3_title.setText(Burden[2].split(",")[0]);
                tv_fu_3_value.setText(Burden[2].split(",")[1]);
                tv_fu_4_title.setText(Burden[3].split(",")[0]);
                tv_fu_4_value.setText(Burden[3].split(",")[1]);
                //
                lt_fu_5.setVisibility(View.VISIBLE);
                lt_fu_6.setVisibility(View.VISIBLE);
                lt_fu_5_xian.setVisibility(View.VISIBLE);
                lt_fu_6_xian.setVisibility(View.VISIBLE);
                tv_fu_5_title.setText(Burden[4].split(",")[0]);
                tv_fu_5_value.setText(Burden[4].split(",")[1]);
                tv_fu_6_title.setText(Burden[5].split(",")[0]);
                tv_fu_6_value.setText(Burden[5].split(",")[1]);
                //
                lt_fu_7.setVisibility(View.VISIBLE);
                lt_fu_8.setVisibility(View.VISIBLE);
                lt_fu_7_xian.setVisibility(View.VISIBLE);
                lt_fu_8_xian.setVisibility(View.VISIBLE);
                tv_fu_7_title.setText(Burden[6].split(",")[0]);
                tv_fu_7_value.setText(Burden[6].split(",")[1]);
                tv_fu_8_title.setText(Burden[7].split(",")[0]);
                tv_fu_8_value.setText(Burden[7].split(",")[1]);
                //
                lt_fu_9.setVisibility(View.VISIBLE);
                lt_fu_10.setVisibility(View.VISIBLE);
                lt_fu_9_xian.setVisibility(View.VISIBLE);
                lt_fu_10_xian.setVisibility(View.VISIBLE);
                tv_fu_9_title.setText(Burden[8].split(",")[0]);
                tv_fu_9_value.setText(Burden[8].split(",")[1]);
//                tv_fu_10_title.setText("");
//                tv_fu_10_value.setText("");
                tv_fu_10_title.setText(Burden[9].split(",")[0]);
                tv_fu_10_value.setText(Burden[9].split(",")[1]);
                //
                lt_fu_11.setVisibility(View.VISIBLE);
                lt_fu_12.setVisibility(View.VISIBLE);
                lt_fu_11_xian.setVisibility(View.VISIBLE);
                lt_fu_12_xian.setVisibility(View.VISIBLE);
                tv_fu_11_title.setText(Burden[10].split(",")[0]);
                tv_fu_11_value.setText(Burden[10].split(",")[1]);
//                tv_fu_12_title.setText("");
//                tv_fu_12_value.setText("");
                tv_fu_12_title.setText(Burden[11].split(",")[0]);
                tv_fu_12_value.setText(Burden[11].split(",")[1]);
                //
                lt_fu_13.setVisibility(View.VISIBLE);
                lt_fu_14.setVisibility(View.VISIBLE);
                lt_fu_13_xian.setVisibility(View.VISIBLE);
                lt_fu_14_xian.setVisibility(View.VISIBLE);
                tv_fu_13_title.setText(Burden[12].split(",")[0]);
                tv_fu_13_value.setText(Burden[12].split(",")[1]);
                tv_fu_14_title.setText("");
                tv_fu_14_value.setText("");
                break;
            case 14:
                lt_fu_1.setVisibility(View.VISIBLE);
                lt_fu_2.setVisibility(View.VISIBLE);
                lt_fu_1_xian.setVisibility(View.VISIBLE);
                lt_fu_2_xian.setVisibility(View.VISIBLE);
                tv_fu_1_title.setText(Burden[0].split(",")[0]);
                tv_fu_1_value.setText(Burden[0].split(",")[1]);
                tv_fu_2_title.setText(Burden[1].split(",")[0]);
                tv_fu_2_value.setText(Burden[1].split(",")[1]);
                //
                lt_fu_3.setVisibility(View.VISIBLE);
                lt_fu_4.setVisibility(View.VISIBLE);
                lt_fu_3_xian.setVisibility(View.VISIBLE);
                lt_fu_4_xian.setVisibility(View.VISIBLE);
                tv_fu_3_title.setText(Burden[2].split(",")[0]);
                tv_fu_3_value.setText(Burden[2].split(",")[1]);
                tv_fu_4_title.setText(Burden[3].split(",")[0]);
                tv_fu_4_value.setText(Burden[3].split(",")[1]);
                //
                lt_fu_5.setVisibility(View.VISIBLE);
                lt_fu_6.setVisibility(View.VISIBLE);
                lt_fu_5_xian.setVisibility(View.VISIBLE);
                lt_fu_6_xian.setVisibility(View.VISIBLE);
                tv_fu_5_title.setText(Burden[4].split(",")[0]);
                tv_fu_5_value.setText(Burden[4].split(",")[1]);
                tv_fu_6_title.setText(Burden[5].split(",")[0]);
                tv_fu_6_value.setText(Burden[5].split(",")[1]);
                //
                lt_fu_7.setVisibility(View.VISIBLE);
                lt_fu_8.setVisibility(View.VISIBLE);
                lt_fu_7_xian.setVisibility(View.VISIBLE);
                lt_fu_8_xian.setVisibility(View.VISIBLE);
                tv_fu_7_title.setText(Burden[6].split(",")[0]);
                tv_fu_7_value.setText(Burden[6].split(",")[1]);
                tv_fu_8_title.setText(Burden[7].split(",")[0]);
                tv_fu_8_value.setText(Burden[7].split(",")[1]);
                //
                lt_fu_9.setVisibility(View.VISIBLE);
                lt_fu_10.setVisibility(View.VISIBLE);
                lt_fu_9_xian.setVisibility(View.VISIBLE);
                lt_fu_10_xian.setVisibility(View.VISIBLE);
                tv_fu_9_title.setText(Burden[8].split(",")[0]);
                tv_fu_9_value.setText(Burden[8].split(",")[1]);
                tv_fu_10_title.setText(Burden[9].split(",")[0]);
                tv_fu_10_value.setText(Burden[9].split(",")[1]);
                //
                lt_fu_11.setVisibility(View.VISIBLE);
                lt_fu_12.setVisibility(View.VISIBLE);
                lt_fu_11_xian.setVisibility(View.VISIBLE);
                lt_fu_12_xian.setVisibility(View.VISIBLE);
                tv_fu_11_title.setText(Burden[10].split(",")[0]);
                tv_fu_11_value.setText(Burden[10].split(",")[1]);
                tv_fu_12_title.setText(Burden[11].split(",")[0]);
                tv_fu_12_value.setText(Burden[11].split(",")[1]);
                //
                lt_fu_13.setVisibility(View.VISIBLE);
                lt_fu_14.setVisibility(View.VISIBLE);
                lt_fu_13_xian.setVisibility(View.VISIBLE);
                lt_fu_14_xian.setVisibility(View.VISIBLE);
                tv_fu_13_title.setText(Burden[12].split(",")[0]);
                tv_fu_13_value.setText(Burden[12].split(",")[1]);
//                tv_fu_14_title.setText("");
//                tv_fu_14_value.setText("");
                tv_fu_14_title.setText(Burden[13].split(",")[0]);
                tv_fu_14_value.setText(Burden[13].split(",")[1]);
                break;
            case 15:
                lt_fu_1.setVisibility(View.VISIBLE);
                lt_fu_2.setVisibility(View.VISIBLE);
                lt_fu_1_xian.setVisibility(View.VISIBLE);
                lt_fu_2_xian.setVisibility(View.VISIBLE);
                tv_fu_1_title.setText(Burden[0].split(",")[0]);
                tv_fu_1_value.setText(Burden[0].split(",")[1]);
                tv_fu_2_title.setText(Burden[1].split(",")[0]);
                tv_fu_2_value.setText(Burden[1].split(",")[1]);
                //
                lt_fu_3.setVisibility(View.VISIBLE);
                lt_fu_4.setVisibility(View.VISIBLE);
                lt_fu_3_xian.setVisibility(View.VISIBLE);
                lt_fu_4_xian.setVisibility(View.VISIBLE);
                tv_fu_3_title.setText(Burden[2].split(",")[0]);
                tv_fu_3_value.setText(Burden[2].split(",")[1]);
                tv_fu_4_title.setText(Burden[3].split(",")[0]);
                tv_fu_4_value.setText(Burden[3].split(",")[1]);
                //
                lt_fu_5.setVisibility(View.VISIBLE);
                lt_fu_6.setVisibility(View.VISIBLE);
                lt_fu_5_xian.setVisibility(View.VISIBLE);
                lt_fu_6_xian.setVisibility(View.VISIBLE);
                tv_fu_5_title.setText(Burden[4].split(",")[0]);
                tv_fu_5_value.setText(Burden[4].split(",")[1]);
                tv_fu_6_title.setText(Burden[5].split(",")[0]);
                tv_fu_6_value.setText(Burden[5].split(",")[1]);
                //
                lt_fu_7.setVisibility(View.VISIBLE);
                lt_fu_8.setVisibility(View.VISIBLE);
                lt_fu_7_xian.setVisibility(View.VISIBLE);
                lt_fu_8_xian.setVisibility(View.VISIBLE);
                tv_fu_7_title.setText(Burden[6].split(",")[0]);
                tv_fu_7_value.setText(Burden[6].split(",")[1]);
                tv_fu_8_title.setText(Burden[7].split(",")[0]);
                tv_fu_8_value.setText(Burden[7].split(",")[1]);
                //
                lt_fu_9.setVisibility(View.VISIBLE);
                lt_fu_10.setVisibility(View.VISIBLE);
                lt_fu_9_xian.setVisibility(View.VISIBLE);
                lt_fu_10_xian.setVisibility(View.VISIBLE);
                tv_fu_9_title.setText(Burden[8].split(",")[0]);
                tv_fu_9_value.setText(Burden[8].split(",")[1]);
                tv_fu_10_title.setText(Burden[9].split(",")[0]);
                tv_fu_10_value.setText(Burden[9].split(",")[1]);
                //
                lt_fu_11.setVisibility(View.VISIBLE);
                lt_fu_12.setVisibility(View.VISIBLE);
                lt_fu_11_xian.setVisibility(View.VISIBLE);
                lt_fu_12_xian.setVisibility(View.VISIBLE);
                tv_fu_11_title.setText(Burden[10].split(",")[0]);
                tv_fu_11_value.setText(Burden[10].split(",")[1]);
                tv_fu_12_title.setText(Burden[11].split(",")[0]);
                tv_fu_12_value.setText(Burden[11].split(",")[1]);
                //
                lt_fu_13.setVisibility(View.VISIBLE);
                lt_fu_14.setVisibility(View.VISIBLE);
                lt_fu_13_xian.setVisibility(View.VISIBLE);
                lt_fu_14_xian.setVisibility(View.VISIBLE);
                tv_fu_13_title.setText(Burden[12].split(",")[0]);
                tv_fu_13_value.setText(Burden[12].split(",")[1]);
//                tv_fu_14_title.setText("");
//                tv_fu_14_value.setText("");
                tv_fu_14_title.setText(Burden[13].split(",")[0]);
                tv_fu_14_value.setText(Burden[13].split(",")[1]);
                //
                lt_fu_15.setVisibility(View.VISIBLE);
                lt_fu_16.setVisibility(View.VISIBLE);
                lt_fu_15_xian.setVisibility(View.VISIBLE);
                lt_fu_16_xian.setVisibility(View.VISIBLE);
                tv_fu_15_title.setText(Burden[14].split(",")[0]);
                tv_fu_15_value.setText(Burden[14].split(",")[1]);
                tv_fu_16_title.setText("");
                tv_fu_16_value.setText("");
                break;
            case 16:
                lt_fu_1.setVisibility(View.VISIBLE);
                lt_fu_2.setVisibility(View.VISIBLE);
                lt_fu_1_xian.setVisibility(View.VISIBLE);
                lt_fu_2_xian.setVisibility(View.VISIBLE);
                tv_fu_1_title.setText(Burden[0].split(",")[0]);
                tv_fu_1_value.setText(Burden[0].split(",")[1]);
                tv_fu_2_title.setText(Burden[1].split(",")[0]);
                tv_fu_2_value.setText(Burden[1].split(",")[1]);
                //
                lt_fu_3.setVisibility(View.VISIBLE);
                lt_fu_4.setVisibility(View.VISIBLE);
                lt_fu_3_xian.setVisibility(View.VISIBLE);
                lt_fu_4_xian.setVisibility(View.VISIBLE);
                tv_fu_3_title.setText(Burden[2].split(",")[0]);
                tv_fu_3_value.setText(Burden[2].split(",")[1]);
                tv_fu_4_title.setText(Burden[3].split(",")[0]);
                tv_fu_4_value.setText(Burden[3].split(",")[1]);
                //
                lt_fu_5.setVisibility(View.VISIBLE);
                lt_fu_6.setVisibility(View.VISIBLE);
                lt_fu_5_xian.setVisibility(View.VISIBLE);
                lt_fu_6_xian.setVisibility(View.VISIBLE);
                tv_fu_5_title.setText(Burden[4].split(",")[0]);
                tv_fu_5_value.setText(Burden[4].split(",")[1]);
                tv_fu_6_title.setText(Burden[5].split(",")[0]);
                tv_fu_6_value.setText(Burden[5].split(",")[1]);
                //
                lt_fu_7.setVisibility(View.VISIBLE);
                lt_fu_8.setVisibility(View.VISIBLE);
                lt_fu_7_xian.setVisibility(View.VISIBLE);
                lt_fu_8_xian.setVisibility(View.VISIBLE);
                tv_fu_7_title.setText(Burden[6].split(",")[0]);
                tv_fu_7_value.setText(Burden[6].split(",")[1]);
                tv_fu_8_title.setText(Burden[7].split(",")[0]);
                tv_fu_8_value.setText(Burden[7].split(",")[1]);
                //
                lt_fu_9.setVisibility(View.VISIBLE);
                lt_fu_10.setVisibility(View.VISIBLE);
                lt_fu_9_xian.setVisibility(View.VISIBLE);
                lt_fu_10_xian.setVisibility(View.VISIBLE);
                tv_fu_9_title.setText(Burden[8].split(",")[0]);
                tv_fu_9_value.setText(Burden[8].split(",")[1]);
                tv_fu_10_title.setText(Burden[9].split(",")[0]);
                tv_fu_10_value.setText(Burden[9].split(",")[1]);
                //
                lt_fu_11.setVisibility(View.VISIBLE);
                lt_fu_12.setVisibility(View.VISIBLE);
                lt_fu_11_xian.setVisibility(View.VISIBLE);
                lt_fu_12_xian.setVisibility(View.VISIBLE);
                tv_fu_11_title.setText(Burden[10].split(",")[0]);
                tv_fu_11_value.setText(Burden[10].split(",")[1]);
                tv_fu_12_title.setText(Burden[11].split(",")[0]);
                tv_fu_12_value.setText(Burden[11].split(",")[1]);
                //
                lt_fu_13.setVisibility(View.VISIBLE);
                lt_fu_14.setVisibility(View.VISIBLE);
                lt_fu_13_xian.setVisibility(View.VISIBLE);
                lt_fu_14_xian.setVisibility(View.VISIBLE);
                tv_fu_13_title.setText(Burden[12].split(",")[0]);
                tv_fu_13_value.setText(Burden[12].split(",")[1]);
//                tv_fu_14_title.setText("");
//                tv_fu_14_value.setText("");
                tv_fu_14_title.setText(Burden[13].split(",")[0]);
                tv_fu_14_value.setText(Burden[13].split(",")[1]);
                //
                lt_fu_15.setVisibility(View.VISIBLE);
                lt_fu_16.setVisibility(View.VISIBLE);
                lt_fu_15_xian.setVisibility(View.VISIBLE);
                lt_fu_16_xian.setVisibility(View.VISIBLE);
                tv_fu_15_title.setText(Burden[14].split(",")[0]);
                tv_fu_15_value.setText(Burden[14].split(",")[1]);
//                tv_fu_16_title.setText("");
//                tv_fu_16_value.setText("");
                tv_fu_16_title.setText(Burden[15].split(",")[0]);
                tv_fu_16_value.setText(Burden[15].split(",")[1]);
                break;
            case 17:
                lt_fu_1.setVisibility(View.VISIBLE);
                lt_fu_2.setVisibility(View.VISIBLE);
                lt_fu_1_xian.setVisibility(View.VISIBLE);
                lt_fu_2_xian.setVisibility(View.VISIBLE);
                tv_fu_1_title.setText(Burden[0].split(",")[0]);
                tv_fu_1_value.setText(Burden[0].split(",")[1]);
                tv_fu_2_title.setText(Burden[1].split(",")[0]);
                tv_fu_2_value.setText(Burden[1].split(",")[1]);
                //
                lt_fu_3.setVisibility(View.VISIBLE);
                lt_fu_4.setVisibility(View.VISIBLE);
                lt_fu_3_xian.setVisibility(View.VISIBLE);
                lt_fu_4_xian.setVisibility(View.VISIBLE);
                tv_fu_3_title.setText(Burden[2].split(",")[0]);
                tv_fu_3_value.setText(Burden[2].split(",")[1]);
                tv_fu_4_title.setText(Burden[3].split(",")[0]);
                tv_fu_4_value.setText(Burden[3].split(",")[1]);
                //
                lt_fu_5.setVisibility(View.VISIBLE);
                lt_fu_6.setVisibility(View.VISIBLE);
                lt_fu_5_xian.setVisibility(View.VISIBLE);
                lt_fu_6_xian.setVisibility(View.VISIBLE);
                tv_fu_5_title.setText(Burden[4].split(",")[0]);
                tv_fu_5_value.setText(Burden[4].split(",")[1]);
                tv_fu_6_title.setText(Burden[5].split(",")[0]);
                tv_fu_6_value.setText(Burden[5].split(",")[1]);
                //
                lt_fu_7.setVisibility(View.VISIBLE);
                lt_fu_8.setVisibility(View.VISIBLE);
                lt_fu_7_xian.setVisibility(View.VISIBLE);
                lt_fu_8_xian.setVisibility(View.VISIBLE);
                tv_fu_7_title.setText(Burden[6].split(",")[0]);
                tv_fu_7_value.setText(Burden[6].split(",")[1]);
                tv_fu_8_title.setText(Burden[7].split(",")[0]);
                tv_fu_8_value.setText(Burden[7].split(",")[1]);
                //
                lt_fu_9.setVisibility(View.VISIBLE);
                lt_fu_10.setVisibility(View.VISIBLE);
                lt_fu_9_xian.setVisibility(View.VISIBLE);
                lt_fu_10_xian.setVisibility(View.VISIBLE);
                tv_fu_9_title.setText(Burden[8].split(",")[0]);
                tv_fu_9_value.setText(Burden[8].split(",")[1]);
                tv_fu_10_title.setText(Burden[9].split(",")[0]);
                tv_fu_10_value.setText(Burden[9].split(",")[1]);
                //
                lt_fu_11.setVisibility(View.VISIBLE);
                lt_fu_12.setVisibility(View.VISIBLE);
                lt_fu_11_xian.setVisibility(View.VISIBLE);
                lt_fu_12_xian.setVisibility(View.VISIBLE);
                tv_fu_11_title.setText(Burden[10].split(",")[0]);
                tv_fu_11_value.setText(Burden[10].split(",")[1]);
                tv_fu_12_title.setText(Burden[11].split(",")[0]);
                tv_fu_12_value.setText(Burden[11].split(",")[1]);
                //
                lt_fu_13.setVisibility(View.VISIBLE);
                lt_fu_14.setVisibility(View.VISIBLE);
                lt_fu_13_xian.setVisibility(View.VISIBLE);
                lt_fu_14_xian.setVisibility(View.VISIBLE);
                tv_fu_13_title.setText(Burden[12].split(",")[0]);
                tv_fu_13_value.setText(Burden[12].split(",")[1]);
//                tv_fu_14_title.setText("");
//                tv_fu_14_value.setText("");
                tv_fu_14_title.setText(Burden[13].split(",")[0]);
                tv_fu_14_value.setText(Burden[13].split(",")[1]);
                //
                lt_fu_15.setVisibility(View.VISIBLE);
                lt_fu_16.setVisibility(View.VISIBLE);
                lt_fu_15_xian.setVisibility(View.VISIBLE);
                lt_fu_16_xian.setVisibility(View.VISIBLE);
                tv_fu_15_title.setText(Burden[14].split(",")[0]);
                tv_fu_15_value.setText(Burden[14].split(",")[1]);
//                tv_fu_16_title.setText("");
//                tv_fu_16_value.setText("");
                tv_fu_16_title.setText(Burden[15].split(",")[0]);
                tv_fu_16_value.setText(Burden[15].split(",")[1]);
                //
                lt_fu_17.setVisibility(View.VISIBLE);
                lt_fu_18.setVisibility(View.VISIBLE);
                lt_fu_17_xian.setVisibility(View.VISIBLE);
                lt_fu_18_xian.setVisibility(View.VISIBLE);
                tv_fu_17_title.setText(Burden[16].split(",")[0]);
                tv_fu_17_value.setText(Burden[16].split(",")[1]);
                tv_fu_18_title.setText("");
                tv_fu_18_value.setText("");
                break;
            case 18:
                lt_fu_1.setVisibility(View.VISIBLE);
                lt_fu_2.setVisibility(View.VISIBLE);
                lt_fu_1_xian.setVisibility(View.VISIBLE);
                lt_fu_2_xian.setVisibility(View.VISIBLE);
                tv_fu_1_title.setText(Burden[0].split(",")[0]);
                tv_fu_1_value.setText(Burden[0].split(",")[1]);
                tv_fu_2_title.setText(Burden[1].split(",")[0]);
                tv_fu_2_value.setText(Burden[1].split(",")[1]);
                //
                lt_fu_3.setVisibility(View.VISIBLE);
                lt_fu_4.setVisibility(View.VISIBLE);
                lt_fu_3_xian.setVisibility(View.VISIBLE);
                lt_fu_4_xian.setVisibility(View.VISIBLE);
                tv_fu_3_title.setText(Burden[2].split(",")[0]);
                tv_fu_3_value.setText(Burden[2].split(",")[1]);
                tv_fu_4_title.setText(Burden[3].split(",")[0]);
                tv_fu_4_value.setText(Burden[3].split(",")[1]);
                //
                lt_fu_5.setVisibility(View.VISIBLE);
                lt_fu_6.setVisibility(View.VISIBLE);
                lt_fu_5_xian.setVisibility(View.VISIBLE);
                lt_fu_6_xian.setVisibility(View.VISIBLE);
                tv_fu_5_title.setText(Burden[4].split(",")[0]);
                tv_fu_5_value.setText(Burden[4].split(",")[1]);
                tv_fu_6_title.setText(Burden[5].split(",")[0]);
                tv_fu_6_value.setText(Burden[5].split(",")[1]);
                //
                lt_fu_7.setVisibility(View.VISIBLE);
                lt_fu_8.setVisibility(View.VISIBLE);
                lt_fu_7_xian.setVisibility(View.VISIBLE);
                lt_fu_8_xian.setVisibility(View.VISIBLE);
                tv_fu_7_title.setText(Burden[6].split(",")[0]);
                tv_fu_7_value.setText(Burden[6].split(",")[1]);
                tv_fu_8_title.setText(Burden[7].split(",")[0]);
                tv_fu_8_value.setText(Burden[7].split(",")[1]);
                //
                lt_fu_9.setVisibility(View.VISIBLE);
                lt_fu_10.setVisibility(View.VISIBLE);
                lt_fu_9_xian.setVisibility(View.VISIBLE);
                lt_fu_10_xian.setVisibility(View.VISIBLE);
                tv_fu_9_title.setText(Burden[8].split(",")[0]);
                tv_fu_9_value.setText(Burden[8].split(",")[1]);
                tv_fu_10_title.setText(Burden[9].split(",")[0]);
                tv_fu_10_value.setText(Burden[9].split(",")[1]);
                //
                lt_fu_11.setVisibility(View.VISIBLE);
                lt_fu_12.setVisibility(View.VISIBLE);
                lt_fu_11_xian.setVisibility(View.VISIBLE);
                lt_fu_12_xian.setVisibility(View.VISIBLE);
                tv_fu_11_title.setText(Burden[10].split(",")[0]);
                tv_fu_11_value.setText(Burden[10].split(",")[1]);
                tv_fu_12_title.setText(Burden[11].split(",")[0]);
                tv_fu_12_value.setText(Burden[11].split(",")[1]);
                //
                lt_fu_13.setVisibility(View.VISIBLE);
                lt_fu_14.setVisibility(View.VISIBLE);
                lt_fu_13_xian.setVisibility(View.VISIBLE);
                lt_fu_14_xian.setVisibility(View.VISIBLE);
                tv_fu_13_title.setText(Burden[12].split(",")[0]);
                tv_fu_13_value.setText(Burden[12].split(",")[1]);
//                tv_fu_14_title.setText("");
//                tv_fu_14_value.setText("");
                tv_fu_14_title.setText(Burden[13].split(",")[0]);
                tv_fu_14_value.setText(Burden[13].split(",")[1]);
                //
                lt_fu_15.setVisibility(View.VISIBLE);
                lt_fu_16.setVisibility(View.VISIBLE);
                lt_fu_15_xian.setVisibility(View.VISIBLE);
                lt_fu_16_xian.setVisibility(View.VISIBLE);
                tv_fu_15_title.setText(Burden[14].split(",")[0]);
                tv_fu_15_value.setText(Burden[14].split(",")[1]);
//                tv_fu_16_title.setText("");
//                tv_fu_16_value.setText("");
                tv_fu_16_title.setText(Burden[15].split(",")[0]);
                tv_fu_16_value.setText(Burden[15].split(",")[1]);
                //
                lt_fu_17.setVisibility(View.VISIBLE);
                lt_fu_18.setVisibility(View.VISIBLE);
                lt_fu_17_xian.setVisibility(View.VISIBLE);
                lt_fu_18_xian.setVisibility(View.VISIBLE);
                tv_fu_17_title.setText(Burden[16].split(",")[0]);
                tv_fu_17_value.setText(Burden[16].split(",")[1]);
//                tv_fu_16_title.setText("");
//                tv_fu_16_value.setText("");
                tv_fu_18_title.setText(Burden[17].split(",")[0]);
                tv_fu_18_value.setText(Burden[17].split(",")[1]);
                break;
            case 19:
                lt_fu_1.setVisibility(View.VISIBLE);
                lt_fu_2.setVisibility(View.VISIBLE);
                lt_fu_1_xian.setVisibility(View.VISIBLE);
                lt_fu_2_xian.setVisibility(View.VISIBLE);
                tv_fu_1_title.setText(Burden[0].split(",")[0]);
                tv_fu_1_value.setText(Burden[0].split(",")[1]);
                tv_fu_2_title.setText(Burden[1].split(",")[0]);
                tv_fu_2_value.setText(Burden[1].split(",")[1]);
                //
                lt_fu_3.setVisibility(View.VISIBLE);
                lt_fu_4.setVisibility(View.VISIBLE);
                lt_fu_3_xian.setVisibility(View.VISIBLE);
                lt_fu_4_xian.setVisibility(View.VISIBLE);
                tv_fu_3_title.setText(Burden[2].split(",")[0]);
                tv_fu_3_value.setText(Burden[2].split(",")[1]);
                tv_fu_4_title.setText(Burden[3].split(",")[0]);
                tv_fu_4_value.setText(Burden[3].split(",")[1]);
                //
                lt_fu_5.setVisibility(View.VISIBLE);
                lt_fu_6.setVisibility(View.VISIBLE);
                lt_fu_5_xian.setVisibility(View.VISIBLE);
                lt_fu_6_xian.setVisibility(View.VISIBLE);
                tv_fu_5_title.setText(Burden[4].split(",")[0]);
                tv_fu_5_value.setText(Burden[4].split(",")[1]);
                tv_fu_6_title.setText(Burden[5].split(",")[0]);
                tv_fu_6_value.setText(Burden[5].split(",")[1]);
                //
                lt_fu_7.setVisibility(View.VISIBLE);
                lt_fu_8.setVisibility(View.VISIBLE);
                lt_fu_7_xian.setVisibility(View.VISIBLE);
                lt_fu_8_xian.setVisibility(View.VISIBLE);
                tv_fu_7_title.setText(Burden[6].split(",")[0]);
                tv_fu_7_value.setText(Burden[6].split(",")[1]);
                tv_fu_8_title.setText(Burden[7].split(",")[0]);
                tv_fu_8_value.setText(Burden[7].split(",")[1]);
                //
                lt_fu_9.setVisibility(View.VISIBLE);
                lt_fu_10.setVisibility(View.VISIBLE);
                lt_fu_9_xian.setVisibility(View.VISIBLE);
                lt_fu_10_xian.setVisibility(View.VISIBLE);
                tv_fu_9_title.setText(Burden[8].split(",")[0]);
                tv_fu_9_value.setText(Burden[8].split(",")[1]);
                tv_fu_10_title.setText(Burden[9].split(",")[0]);
                tv_fu_10_value.setText(Burden[9].split(",")[1]);
                //
                lt_fu_11.setVisibility(View.VISIBLE);
                lt_fu_12.setVisibility(View.VISIBLE);
                lt_fu_11_xian.setVisibility(View.VISIBLE);
                lt_fu_12_xian.setVisibility(View.VISIBLE);
                tv_fu_11_title.setText(Burden[10].split(",")[0]);
                tv_fu_11_value.setText(Burden[10].split(",")[1]);
                tv_fu_12_title.setText(Burden[11].split(",")[0]);
                tv_fu_12_value.setText(Burden[11].split(",")[1]);
                //
                lt_fu_13.setVisibility(View.VISIBLE);
                lt_fu_14.setVisibility(View.VISIBLE);
                lt_fu_13_xian.setVisibility(View.VISIBLE);
                lt_fu_14_xian.setVisibility(View.VISIBLE);
                tv_fu_13_title.setText(Burden[12].split(",")[0]);
                tv_fu_13_value.setText(Burden[12].split(",")[1]);
//                tv_fu_14_title.setText("");
//                tv_fu_14_value.setText("");
                tv_fu_14_title.setText(Burden[13].split(",")[0]);
                tv_fu_14_value.setText(Burden[13].split(",")[1]);
                //
                lt_fu_15.setVisibility(View.VISIBLE);
                lt_fu_16.setVisibility(View.VISIBLE);
                lt_fu_15_xian.setVisibility(View.VISIBLE);
                lt_fu_16_xian.setVisibility(View.VISIBLE);
                tv_fu_15_title.setText(Burden[14].split(",")[0]);
                tv_fu_15_value.setText(Burden[14].split(",")[1]);
//                tv_fu_16_title.setText("");
//                tv_fu_16_value.setText("");
                tv_fu_16_title.setText(Burden[15].split(",")[0]);
                tv_fu_16_value.setText(Burden[15].split(",")[1]);
                //
                lt_fu_17.setVisibility(View.VISIBLE);
                lt_fu_18.setVisibility(View.VISIBLE);
                lt_fu_17_xian.setVisibility(View.VISIBLE);
                lt_fu_18_xian.setVisibility(View.VISIBLE);
                tv_fu_17_title.setText(Burden[16].split(",")[0]);
                tv_fu_17_value.setText(Burden[16].split(",")[1]);
//                tv_fu_16_title.setText("");
//                tv_fu_16_value.setText("");
                tv_fu_18_title.setText(Burden[17].split(",")[0]);
                tv_fu_18_value.setText(Burden[17].split(",")[1]);
                //
                lt_fu_19.setVisibility(View.VISIBLE);
                lt_fu_20.setVisibility(View.VISIBLE);
                lt_fu_19_xian.setVisibility(View.VISIBLE);
                lt_fu_20_xian.setVisibility(View.VISIBLE);
                tv_fu_19_title.setText(Burden[18].split(",")[0]);
                tv_fu_19_value.setText(Burden[18].split(",")[1]);
                tv_fu_20_title.setText("");
                tv_fu_20_value.setText("");
                break;
            case 20:
                lt_fu_1.setVisibility(View.VISIBLE);
                lt_fu_2.setVisibility(View.VISIBLE);
                lt_fu_1_xian.setVisibility(View.VISIBLE);
                lt_fu_2_xian.setVisibility(View.VISIBLE);
                tv_fu_1_title.setText(Burden[0].split(",")[0]);
                tv_fu_1_value.setText(Burden[0].split(",")[1]);
                tv_fu_2_title.setText(Burden[1].split(",")[0]);
                tv_fu_2_value.setText(Burden[1].split(",")[1]);
                //
                lt_fu_3.setVisibility(View.VISIBLE);
                lt_fu_4.setVisibility(View.VISIBLE);
                lt_fu_3_xian.setVisibility(View.VISIBLE);
                lt_fu_4_xian.setVisibility(View.VISIBLE);
                tv_fu_3_title.setText(Burden[2].split(",")[0]);
                tv_fu_3_value.setText(Burden[2].split(",")[1]);
                tv_fu_4_title.setText(Burden[3].split(",")[0]);
                tv_fu_4_value.setText(Burden[3].split(",")[1]);
                //
                lt_fu_5.setVisibility(View.VISIBLE);
                lt_fu_6.setVisibility(View.VISIBLE);
                lt_fu_5_xian.setVisibility(View.VISIBLE);
                lt_fu_6_xian.setVisibility(View.VISIBLE);
                tv_fu_5_title.setText(Burden[4].split(",")[0]);
                tv_fu_5_value.setText(Burden[4].split(",")[1]);
                tv_fu_6_title.setText(Burden[5].split(",")[0]);
                tv_fu_6_value.setText(Burden[5].split(",")[1]);
                //
                lt_fu_7.setVisibility(View.VISIBLE);
                lt_fu_8.setVisibility(View.VISIBLE);
                lt_fu_7_xian.setVisibility(View.VISIBLE);
                lt_fu_8_xian.setVisibility(View.VISIBLE);
                tv_fu_7_title.setText(Burden[6].split(",")[0]);
                tv_fu_7_value.setText(Burden[6].split(",")[1]);
                tv_fu_8_title.setText(Burden[7].split(",")[0]);
                tv_fu_8_value.setText(Burden[7].split(",")[1]);
                //
                lt_fu_9.setVisibility(View.VISIBLE);
                lt_fu_10.setVisibility(View.VISIBLE);
                lt_fu_9_xian.setVisibility(View.VISIBLE);
                lt_fu_10_xian.setVisibility(View.VISIBLE);
                tv_fu_9_title.setText(Burden[8].split(",")[0]);
                tv_fu_9_value.setText(Burden[8].split(",")[1]);
                tv_fu_10_title.setText(Burden[9].split(",")[0]);
                tv_fu_10_value.setText(Burden[9].split(",")[1]);
                //
                lt_fu_11.setVisibility(View.VISIBLE);
                lt_fu_12.setVisibility(View.VISIBLE);
                lt_fu_11_xian.setVisibility(View.VISIBLE);
                lt_fu_12_xian.setVisibility(View.VISIBLE);
                tv_fu_11_title.setText(Burden[10].split(",")[0]);
                tv_fu_11_value.setText(Burden[10].split(",")[1]);
                tv_fu_12_title.setText(Burden[11].split(",")[0]);
                tv_fu_12_value.setText(Burden[11].split(",")[1]);
                //
                lt_fu_13.setVisibility(View.VISIBLE);
                lt_fu_14.setVisibility(View.VISIBLE);
                lt_fu_13_xian.setVisibility(View.VISIBLE);
                lt_fu_14_xian.setVisibility(View.VISIBLE);
                tv_fu_13_title.setText(Burden[12].split(",")[0]);
                tv_fu_13_value.setText(Burden[12].split(",")[1]);
//                tv_fu_14_title.setText("");
//                tv_fu_14_value.setText("");
                tv_fu_14_title.setText(Burden[13].split(",")[0]);
                tv_fu_14_value.setText(Burden[13].split(",")[1]);
                //
                lt_fu_15.setVisibility(View.VISIBLE);
                lt_fu_16.setVisibility(View.VISIBLE);
                lt_fu_15_xian.setVisibility(View.VISIBLE);
                lt_fu_16_xian.setVisibility(View.VISIBLE);
                tv_fu_15_title.setText(Burden[14].split(",")[0]);
                tv_fu_15_value.setText(Burden[14].split(",")[1]);
//                tv_fu_16_title.setText("");
//                tv_fu_16_value.setText("");
                tv_fu_16_title.setText(Burden[15].split(",")[0]);
                tv_fu_16_value.setText(Burden[15].split(",")[1]);
                //
                lt_fu_17.setVisibility(View.VISIBLE);
                lt_fu_18.setVisibility(View.VISIBLE);
                lt_fu_17_xian.setVisibility(View.VISIBLE);
                lt_fu_18_xian.setVisibility(View.VISIBLE);
                tv_fu_17_title.setText(Burden[16].split(",")[0]);
                tv_fu_17_value.setText(Burden[16].split(",")[1]);
//                tv_fu_16_title.setText("");
//                tv_fu_16_value.setText("");
                tv_fu_18_title.setText(Burden[17].split(",")[0]);
                tv_fu_18_value.setText(Burden[17].split(",")[1]);
                //
                lt_fu_19.setVisibility(View.VISIBLE);
                lt_fu_20.setVisibility(View.VISIBLE);
                lt_fu_19_xian.setVisibility(View.VISIBLE);
                lt_fu_20_xian.setVisibility(View.VISIBLE);
                tv_fu_19_title.setText(Burden[18].split(",")[0]);
                tv_fu_19_value.setText(Burden[18].split(",")[1]);
//                tv_fu_16_title.setText("");
//                tv_fu_16_value.setText("");
                tv_fu_20_title.setText(Burden[19].split(",")[0]);
                tv_fu_20_value.setText(Burden[19].split(",")[1]);
                break;
        }
    }

    private void setData() {

//        for (int i = 1; i < 7; i++) {
//            Log.d(TAG, "i:" + i);
//            adapter.addItem(new RecipeDetailsInfo("http://e.hiphotos.baidu.com/image/pic/item/6d81800a19d8bc3eada0bcca808ba61ea8d34501.jpg",
//                    "步骤" + i));
//        }
//        adapter.notifyDataSetChanged();

    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0); // 计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);

    }

    private void initAdapter() {
        adapter = new XXListViewAdapter<RecipeDetailsInfo>(this, R.layout.item_lv_recipedetail) {
            @Override
            public void initGetView(int position, View convertView, ViewGroup parent) {
                ImageView item_iv_recipedetail = (ImageView) convertView.findViewById(R.id.item_iv_recipedetail);
                TextView item_tv_recipedetail = (TextView) convertView.findViewById(R.id.item_tv_recipedetail);
                item_tv_recipedetail.setText(getItem(position).getMsg_info());
                new XXImagesLoader(null, true, cn.com.xxutils.R.drawable.ic_stub,
                        cn.com.xxutils.R.drawable.ic_stub,
                        cn.com.xxutils.R.drawable.ic_stub).disPlayImage(getItem(position).getImage_url(),
                        item_iv_recipedetail);

            }
        };
        lv_recipe_detail.setAdapter(adapter);
        lv_recipe_detail.setListViewAnimation(adapter, XXListViewAnimationMode.ANIIMATION_ALPHA);

        star_button.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(XXLikeButton XXLikeButton) {
                //当按钮状态为灰色被点击时触发
                Log.d(TAG, "用户申请收藏");
                XXSharedPreferences preferences = new XXSharedPreferences(MainActivity.SHAREDSESSIONIDSAVEEDNAME);

                XXHttpClient client = new XXHttpClient(Util.URL_SERVICE_STARORUNSTAR, true, new XXHttpClient.XXHttpResponseListener() {
                    @Override
                    public void onSuccess(int i, byte[] bytes) {
                        Log.d(TAG, "" + new String(bytes));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                star_button.setLiked(true);
                                star_button.setEnabled(false);
                                int a = Integer.valueOf(tv_star_number.getText().toString());
                                a++;
                                tv_star_number.setText(String.valueOf(a));
                                Toast.makeText(RecipeDetail.this, "收藏成功", Toast.LENGTH_SHORT).show();
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
                client.put("usernumber", preferences.get(RecipeDetail.this, "usernumber", "").toString());
                client.put("sessionid", preferences.get(RecipeDetail.this, "sessionid", "").toString());
                client.put("com_id", id);
                client.put("type", "1");

                client.doPost(15000);
            }

            @Override
            public void unLiked(XXLikeButton XXLikeButton) {
                Log.d(TAG, "用户申请取消收藏");
                XXSharedPreferences preferences = new XXSharedPreferences(MainActivity.SHAREDSESSIONIDSAVEEDNAME);

                XXHttpClient client = new XXHttpClient(Util.URL_SERVICE_STARORUNSTAR, true, new XXHttpClient.XXHttpResponseListener() {
                    @Override
                    public void onSuccess(int i, byte[] bytes) {
                        Log.d(TAG, "" + new String(bytes));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                star_button.setLiked(false);
                                int a = Integer.valueOf(tv_star_number.getText().toString());
                                a--;
                                tv_star_number.setText(String.valueOf(a));
                                Toast.makeText(RecipeDetail.this, "取消成功", Toast.LENGTH_SHORT).show();
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
                client.put("usernumber", preferences.get(RecipeDetail.this, "usernumber", "").toString());
                client.put("sessionid", preferences.get(RecipeDetail.this, "sessionid", "").toString());
                client.put("com_id", id);
                client.put("type", "2");

                client.doPost(15000);
            }
        });
    }

    private void initView() {
        findID();
        initAdapter();
        getData();
        setData();
    }

    private void findID() {
        lv_recipe_detail = (XXListView) findViewById(R.id.lv_recipe_detail);
        tv_recipe_title = (TextView) findViewById(R.id.tv_recipe_title);
        tv_recipe_detail = (TextView) findViewById(R.id.tv_recipe_detail);
        //主料viewID
        lt_main_1 = (LinearLayout) findViewById(R.id.lt_main_1);
        lt_main_2 = (LinearLayout) findViewById(R.id.lt_main_2);
        lt_main_3 = (LinearLayout) findViewById(R.id.lt_main_3);
        lt_main_4 = (LinearLayout) findViewById(R.id.lt_main_4);
        lt_main_5 = (LinearLayout) findViewById(R.id.lt_main_5);
        lt_main_6 = (LinearLayout) findViewById(R.id.lt_main_6);
        lt_main_7 = (LinearLayout) findViewById(R.id.lt_main_7);
        lt_main_8 = (LinearLayout) findViewById(R.id.lt_main_8);
        lt_main_9 = (LinearLayout) findViewById(R.id.lt_main_9);
        lt_main_10 = (LinearLayout) findViewById(R.id.lt_main_10);


        //主料线ID
        lt_main_1_xian = (LinearLayout) findViewById(R.id.lt_main_1_xian);
        lt_main_2_xian = (LinearLayout) findViewById(R.id.lt_main_2_xian);
        lt_main_3_xian = (LinearLayout) findViewById(R.id.lt_main_3_xian);
        lt_main_4_xian = (LinearLayout) findViewById(R.id.lt_main_4_xian);
        lt_main_5_xian = (LinearLayout) findViewById(R.id.lt_main_5_xian);
        lt_main_6_xian = (LinearLayout) findViewById(R.id.lt_main_6_xian);
        lt_main_7_xian = (LinearLayout) findViewById(R.id.lt_main_7_xian);
        lt_main_8_xian = (LinearLayout) findViewById(R.id.lt_main_8_xian);
        lt_main_9_xian = (LinearLayout) findViewById(R.id.lt_main_9_xian);
        lt_main_10_xian = (LinearLayout) findViewById(R.id.lt_main_10_xian);

//        lt_main_7_xian= (LinearLayout) findViewById(R.id.lt_main_7_xian);
//        lt_main_8_xian= (LinearLayout) findViewById(R.id.lt_main_8_xian);
        //辅料viewID
        lt_fu_1 = (LinearLayout) findViewById(R.id.lt_fu_1);
        lt_fu_2 = (LinearLayout) findViewById(R.id.lt_fu_2);
        lt_fu_3 = (LinearLayout) findViewById(R.id.lt_fu_3);
        lt_fu_4 = (LinearLayout) findViewById(R.id.lt_fu_4);
        lt_fu_5 = (LinearLayout) findViewById(R.id.lt_fu_5);
        lt_fu_6 = (LinearLayout) findViewById(R.id.lt_fu_6);
        lt_fu_7 = (LinearLayout) findViewById(R.id.lt_fu_7);
        lt_fu_8 = (LinearLayout) findViewById(R.id.lt_fu_8);
        lt_fu_9 = (LinearLayout) findViewById(R.id.lt_fu_9);
        lt_fu_10 = (LinearLayout) findViewById(R.id.lt_fu_10);
        lt_fu_11 = (LinearLayout) findViewById(R.id.lt_fu_11);
        lt_fu_12 = (LinearLayout) findViewById(R.id.lt_fu_12);
        lt_fu_13 = (LinearLayout) findViewById(R.id.lt_fu_13);
        lt_fu_14 = (LinearLayout) findViewById(R.id.lt_fu_14);
        lt_fu_15 = (LinearLayout) findViewById(R.id.lt_fu_15);
        lt_fu_16 = (LinearLayout) findViewById(R.id.lt_fu_16);
        lt_fu_17 = (LinearLayout) findViewById(R.id.lt_fu_17);
        lt_fu_18 = (LinearLayout) findViewById(R.id.lt_fu_18);
        lt_fu_19 = (LinearLayout) findViewById(R.id.lt_fu_19);
        lt_fu_20 = (LinearLayout) findViewById(R.id.lt_fu_20);
        //辅料线
        lt_fu_1_xian = (LinearLayout) findViewById(R.id.lt_fu_1_xian);
        lt_fu_2_xian = (LinearLayout) findViewById(R.id.lt_fu_2_xian);
        lt_fu_3_xian = (LinearLayout) findViewById(R.id.lt_fu_3_xian);
        lt_fu_4_xian = (LinearLayout) findViewById(R.id.lt_fu_4_xian);
        lt_fu_5_xian = (LinearLayout) findViewById(R.id.lt_fu_5_xian);
        lt_fu_6_xian = (LinearLayout) findViewById(R.id.lt_fu_6_xian);
        lt_fu_7_xian = (LinearLayout) findViewById(R.id.lt_fu_7_xian);
        lt_fu_8_xian = (LinearLayout) findViewById(R.id.lt_fu_8_xian);
        lt_fu_9_xian = (LinearLayout) findViewById(R.id.lt_fu_9_xian);
        lt_fu_10_xian = (LinearLayout) findViewById(R.id.lt_fu_10_xian);
        lt_fu_11_xian = (LinearLayout) findViewById(R.id.lt_fu_11_xian);
        lt_fu_12_xian = (LinearLayout) findViewById(R.id.lt_fu_12_xian);
        lt_fu_13_xian = (LinearLayout) findViewById(R.id.lt_fu_13_xian);
        lt_fu_14_xian = (LinearLayout) findViewById(R.id.lt_fu_14_xian);
        lt_fu_15_xian = (LinearLayout) findViewById(R.id.lt_fu_15_xian);
        lt_fu_16_xian = (LinearLayout) findViewById(R.id.lt_fu_16_xian);
        lt_fu_17_xian = (LinearLayout) findViewById(R.id.lt_fu_17_xian);
        lt_fu_18_xian = (LinearLayout) findViewById(R.id.lt_fu_18_xian);
        lt_fu_19_xian = (LinearLayout) findViewById(R.id.lt_fu_19_xian);
        lt_fu_20_xian = (LinearLayout) findViewById(R.id.lt_fu_20_xian);

        //主料title
        tv_main_1_title = (TextView) findViewById(R.id.tv_main_1_title);
        tv_main_2_title = (TextView) findViewById(R.id.tv_main_2_title);
        tv_main_3_title = (TextView) findViewById(R.id.tv_main_3_title);
        tv_main_4_title = (TextView) findViewById(R.id.tv_main_4_title);
        tv_main_5_title = (TextView) findViewById(R.id.tv_main_5_title);
        tv_main_6_title = (TextView) findViewById(R.id.tv_main_6_title);
        tv_main_7_title = (TextView) findViewById(R.id.tv_main_7_title);
        tv_main_8_title = (TextView) findViewById(R.id.tv_main_8_title);
        tv_main_9_title = (TextView) findViewById(R.id.tv_main_9_title);
        tv_main_10_title = (TextView) findViewById(R.id.tv_main_10_title);

        //主料value
        tv_main_1_value = (TextView) findViewById(R.id.tv_main_1_value);
        tv_main_2_value = (TextView) findViewById(R.id.tv_main_2_value);
        tv_main_3_value = (TextView) findViewById(R.id.tv_main_3_value);
        tv_main_4_value = (TextView) findViewById(R.id.tv_main_4_value);
        tv_main_5_value = (TextView) findViewById(R.id.tv_main_5_value);
        tv_main_6_value = (TextView) findViewById(R.id.tv_main_6_value);
        tv_main_7_value = (TextView) findViewById(R.id.tv_main_7_value);
        tv_main_8_value = (TextView) findViewById(R.id.tv_main_8_value);
        tv_main_9_value = (TextView) findViewById(R.id.tv_main_9_value);
        tv_main_10_value = (TextView) findViewById(R.id.tv_main_10_value);


        //辅料Title
        tv_fu_1_title = (TextView) findViewById(R.id.tv_fu_1_title);
        tv_fu_2_title = (TextView) findViewById(R.id.tv_fu_2_title);
        tv_fu_3_title = (TextView) findViewById(R.id.tv_fu_3_title);
        tv_fu_4_title = (TextView) findViewById(R.id.tv_fu_4_title);
        tv_fu_5_title = (TextView) findViewById(R.id.tv_fu_5_title);
        tv_fu_6_title = (TextView) findViewById(R.id.tv_fu_6_title);
        tv_fu_7_title = (TextView) findViewById(R.id.tv_fu_7_title);
        tv_fu_8_title = (TextView) findViewById(R.id.tv_fu_8_title);
        tv_fu_9_title = (TextView) findViewById(R.id.tv_fu_9_title);
        tv_fu_10_title = (TextView) findViewById(R.id.tv_fu_10_title);
        tv_fu_11_title = (TextView) findViewById(R.id.tv_fu_11_title);
        tv_fu_12_title = (TextView) findViewById(R.id.tv_fu_12_title);
        tv_fu_13_title = (TextView) findViewById(R.id.tv_fu_13_title);
        tv_fu_14_title = (TextView) findViewById(R.id.tv_fu_14_title);
        tv_fu_15_title = (TextView) findViewById(R.id.tv_fu_15_title);
        tv_fu_16_title = (TextView) findViewById(R.id.tv_fu_16_title);
        tv_fu_17_title = (TextView) findViewById(R.id.tv_fu_17_title);
        tv_fu_18_title = (TextView) findViewById(R.id.tv_fu_18_title);
        tv_fu_19_title = (TextView) findViewById(R.id.tv_fu_19_title);
        tv_fu_20_title = (TextView) findViewById(R.id.tv_fu_20_title);


        //辅料value
        tv_fu_1_value = (TextView) findViewById(R.id.tv_fu_1_value);
        tv_fu_2_value = (TextView) findViewById(R.id.tv_fu_2_value);
        tv_fu_3_value = (TextView) findViewById(R.id.tv_fu_3_value);
        tv_fu_4_value = (TextView) findViewById(R.id.tv_fu_4_value);
        tv_fu_5_value = (TextView) findViewById(R.id.tv_fu_5_value);
        tv_fu_6_value = (TextView) findViewById(R.id.tv_fu_6_value);
        tv_fu_7_value = (TextView) findViewById(R.id.tv_fu_7_value);
        tv_fu_8_value = (TextView) findViewById(R.id.tv_fu_8_value);
        tv_fu_9_value = (TextView) findViewById(R.id.tv_fu_9_value);
        tv_fu_10_value = (TextView) findViewById(R.id.tv_fu_10_value);
        tv_fu_11_value = (TextView) findViewById(R.id.tv_fu_11_value);
        tv_fu_12_value = (TextView) findViewById(R.id.tv_fu_12_value);
        tv_fu_13_value = (TextView) findViewById(R.id.tv_fu_13_value);
        tv_fu_14_value = (TextView) findViewById(R.id.tv_fu_14_value);
        tv_fu_15_value = (TextView) findViewById(R.id.tv_fu_15_value);
        tv_fu_16_value = (TextView) findViewById(R.id.tv_fu_16_value);
        tv_fu_17_value = (TextView) findViewById(R.id.tv_fu_17_value);
        tv_fu_18_value = (TextView) findViewById(R.id.tv_fu_18_value);
        tv_fu_19_value = (TextView) findViewById(R.id.tv_fu_19_value);
        tv_fu_20_value = (TextView) findViewById(R.id.tv_fu_20_value);
        tv_star_number = (TextView) findViewById(R.id.tv_star_number);
        tv_like_number = (TextView) findViewById(R.id.tv_like_number);
        tv_comment_number = (TextView) findViewById(R.id.tv_comment_number);
        star_button = (XXLikeButton) findViewById(R.id.star_button);
        like_button = (XXLikeButton) findViewById(R.id.like_button);
    }

}
