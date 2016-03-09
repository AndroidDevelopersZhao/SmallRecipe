package com.cn.smallrecipe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cn.smallrecipe.MyActivity;
import com.cn.smallrecipe.R;
import com.cn.smallrecipe.Util;
import com.cn.smallrecipe.datainfo.search.AllInfo;
import com.cn.smallrecipe.datainfo.search.Data;
import com.cn.smallrecipe.datainfo.search.Item;
import com.google.gson.Gson;

import java.util.ArrayList;

import cn.com.xxutils.adapter.XXListViewAdapter;
import cn.com.xxutils.progress.XXSVProgressHUD;
import cn.com.xxutils.util.XXHttpClient;
import cn.com.xxutils.util.XXImagesLoader;
import cn.com.xxutils.util.XXListViewAnimationMode;
import cn.com.xxutils.view.XXCustomListView;

/**
 * Created by Administrator on 2016/2/24.
 */
public class SearchActivity extends MyActivity implements View.OnClickListener {
    public static final String SEARCH_KEY = "key_search";//传值的key
    private LinearLayout layout_icon_back;
    private XXSVProgressHUD xxsvProgressHUD = null;
    private XXCustomListView listview_searched;

    private XXListViewAdapter<Item> adapter = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_main);
        initView();
    }

    private void initView() {
        xxsvProgressHUD = new XXSVProgressHUD();

        layout_icon_back = (LinearLayout) findViewById(R.id.layout_icon_back);
        layout_icon_back.setOnClickListener(this);
        listview_searched = (XXCustomListView) findViewById(R.id.listview_searched);

        listview_searched.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //跳入详情页面，带过去菜谱ID
                Log.d(TAG, "position:" + position);
                Log.d(TAG, "OnItemClickListener-ListView-Search" + adapter.getItem(position - 1).getId());
                Intent intent = new Intent(SearchActivity.this, RecipeDetail.class);
                intent.putExtra("id", adapter.getItem(position - 1).getId());
                startActivity(intent);
                overridePendingTransition(R.anim.pp_enter, R.anim.pp_exit);
            }
        });
        adapter = new XXListViewAdapter<Item>(this, R.layout.item_listview_searched) {
            @Override
            public void initGetView(int position, View convertView, ViewGroup parent) {
                TextView title = (TextView) convertView.findViewById(R.id.item_tv_search_title);
                TextView ingredients = (TextView) convertView.findViewById(R.id.item_tv_search_ingredients);
                TextView burden = (TextView) convertView.findViewById(R.id.item_tv_search_burden);
                ImageView imageView = (ImageView) convertView.findViewById(R.id.item_iv_search);
                title.setText(getItem(position).getTitle());
                ingredients.setText(getItem(position).getIngredients());
                burden.setText(getItem(position).getBurden());
                XXImagesLoader loder = new XXImagesLoader(null, true, R.drawable.ic_stub, R.drawable.ic_empty, R.drawable.ic_error);
                loder.disPlayImage(getItem(position).getUrl(), imageView);
            }
        };
        listview_searched.setListViewAnimation(adapter, XXListViewAnimationMode.ANIIMATION_RIGHT);
        listview_searched.setAdapter(adapter);
        //上啦加载暂时不做
//        final View footer = View.inflate(this, R.layout.footer, null);
//        listview_searched.setOnAddFootListener(new XXCustomListView.OnAddFootListener() {
//            @Override
//            public void addFoot() {
//                listview_searched.addFooterView(footer);
//                footer.setVisibility(View.INVISIBLE);
//            }
//        });
        listview_searched.setOnRefreshListner(new XXCustomListView.OnRefreshListner() {
            @Override
            public void onRefresh() {
                adapter.removeAll();
                adapter.notifyDataSetChanged();
                startSearch(str_search);
            }
        });

        initData();

    }

    private String str_search;

    private void initData() {
        this.str_search = getIntent().getStringExtra(SEARCH_KEY);
        if (str_search == null || str_search.equals("")) {
            Toast.makeText(this, "搜索被异常终止", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d(TAG, "will start search menu" + str_search);
        xxsvProgressHUD.showWithStatus(this, "start searching……");
        startSearch(str_search);

    }

    /**
     * 请求菜单
     *
     * @param str
     */
    private Handler handler_search = null;

    private void startSearch(String str) {

        handler_search = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                dismiss();
                listview_searched.onRefreshComplete();
                switch (msg.what) {
                    case 1:
                        AllInfo allInfo = (AllInfo) msg.getData().getSerializable("data");
                        Log.d(TAG, "search was successful,ErrorCode:" + allInfo.getError_code());
                        for (int i = 0; i < allInfo.getResult().getData().size(); i++) {
                            if (allInfo.getResult().getData().get(i).getBurden().split(";").length > 12)
                                Log.w(TAG, "-----------" + allInfo.getResult().getData().get(i).getBurden().split(";").length
                                        + ",----" + allInfo.getResult().getData().get(i).getTitle());
                        }

                        setAdapterData(allInfo);

                        break;

                    case -1:

                        Toast.makeText(SearchActivity.this, "请求的食谱数据不存在，请重新搜索", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "search error，" + msg.getData().getString("data"));
                        break;
                }
            }
        };

        XXHttpClient client = new XXHttpClient(Util.URL_SEARCH, true, new XXHttpClient.XXHttpResponseListener() {
            @Override
            public void onSuccess(int i, byte[] bytes) {
                AllInfo allInfo = null;
                try {
                    allInfo = new Gson().fromJson(new String(bytes), AllInfo.class);
                    if (allInfo.getError_code() == 0 && allInfo.getResultcode().equals("200")) {
                        Util.sendMsgToHandler(handler_search, allInfo, true);
                    } else {
                        Util.sendMsgToHandler(handler_search, allInfo.getReason(), false);
                    }
                } catch (Throwable throwable) {
                    Util.sendMsgToHandler(handler_search, "数据解析异常", false);
                }

            }

            @Override
            public void onError(int i, Throwable throwable) {
                Log.e(TAG, "网络异常");
                Util.sendMsgToHandler(handler_search, "网络异常", false);
            }

            @Override
            public void onProgress(long l, long l1) {

            }
        });
        client.put("menu", str);
        client.put("key", Util.APPKEY);
        client.doGet(15000);
    }

    private void setAdapterData(AllInfo allInfo) {
        ArrayList<Data> datas = allInfo.getResult().getData();

        for (int i = 0; i < datas.size(); i++) {
            Item item = new Item();
            item.setTitle(datas.get(i).getTitle());
            item.setIngredients(datas.get(i).getIngredients());
            item.setBurden(datas.get(i).getBurden());
            item.setId(datas.get(i).getId().toString());
            ArrayList<String> urls = datas.get(i).getAlbums();
            if (urls.size() == 0) {
                item.setUrl("http://www.2cto.com/uploadfile/2012/0207/20120207012945988.jpg");//
            } else {
                item.setUrl(urls.get(0));
            }

            adapter.addItem(item);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_icon_back:
                startActivity(new Intent(SearchActivity.this, MainActivity.class));
                this.finish();
                break;
        }
    }

    private void dismiss() {
        if (xxsvProgressHUD != null) {
            if (xxsvProgressHUD.isShowing(this)) {
                xxsvProgressHUD.dismiss(this);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "SearchActivity was destroyed");
    }
}
