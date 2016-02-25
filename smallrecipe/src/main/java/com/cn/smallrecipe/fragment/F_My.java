package com.cn.smallrecipe.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cn.smallrecipe.ParentFragment;
import com.cn.smallrecipe.R;
import com.cn.smallrecipe.datainfo.myinfo.MyInfos;
import com.cn.smallrecipe.datainfo.myinfo.MyInfosTitle;

import cn.com.xxutils.adapter.XXListViewAdapter;
import cn.com.xxutils.view.XXListView;

/**
 * //TODO 个人中心
 * Created by Administrator on 2016/2/24.
 */
public class F_My extends ParentFragment {
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
        adapter1.addItem(new MyInfosTitle(R.drawable.uerlogo, "飞翔的企鹅", R.drawable.icon_my_title, "235894856"));
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
}
