package com.cn.smallrecipe.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cn.smallrecipe.MyActivity;
import com.cn.smallrecipe.R;
import com.cn.smallrecipe.datainfo.recipedetail.RecipeDetailsInfo;

import cn.com.xxutils.adapter.XXListViewAdapter;
import cn.com.xxutils.util.XXImagesLoader;
import cn.com.xxutils.util.XXListViewAnimationMode;
import cn.com.xxutils.view.XXListView;

public class RecipeDetail extends MyActivity {
    private XXListView lv_recipe_detail;
    private XXListViewAdapter<RecipeDetailsInfo> adapter = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        initView();
        initAdapter();
        setData();
    }

    private void setData() {
        lv_recipe_detail.setListViewAnimation(adapter, XXListViewAnimationMode.ANIIMATION_ALPHA);
        lv_recipe_detail.setAdapter(adapter);
        for (int i = 1; i < 7; i++) {
            Log.d(TAG, "i:" + i);
            adapter.addItem(new RecipeDetailsInfo("http://e.hiphotos.baidu.com/image/pic/item/6d81800a19d8bc3eada0bcca808ba61ea8d34501.jpg",
                    "步骤" + i));
        }
        adapter.notifyDataSetChanged();

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
    }

    private void initView() {
        findID();
    }

    private void findID() {
        lv_recipe_detail = (XXListView) findViewById(R.id.lv_recipe_detail);
    }

}
