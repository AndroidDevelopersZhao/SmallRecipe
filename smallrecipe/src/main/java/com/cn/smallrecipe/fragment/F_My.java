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

import cn.com.xxutils.adapter.XXListViewAdapter;
import cn.com.xxutils.view.XXListView;

/**
 * //TODO 个人中心
 * Created by Administrator on 2016/2/24.
 */
public class F_My extends ParentFragment {
    private View view;
    private LinearLayout layout_myinfo_title;
    private XXListView listView,listView2;
    private XXListViewAdapter<MyInfos> adapter = null;
    private XXListViewAdapter<MyInfos> adapter2 = null;

    private String[] text = {"功能展示区", "功能展示区", "功能展示区",
            "功能展示区"};
    private int[] ImageIds = {R.drawable.uerlogo, R.drawable.uerlogo, R.drawable.uerlogo,
            R.drawable.uerlogo};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.f_my, null);
        initView();
        return view;
    }

    private void initView() {
        layout_myinfo_title = (LinearLayout) view.findViewById(R.id.layout_myinfo_title);
        listView = (XXListView) view.findViewById(R.id.listview_myinfo_title);
        listView2= (XXListView) view.findViewById(R.id.listview_myinfo_title_2);
        initAdapter1();
        initAdapter2();
        listView.setAdapter(adapter);
        listView2.setAdapter(adapter2);
        addDataToAdpter1();
        addDataToAdpter2();

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
        adapter2.addItem(new MyInfos(R.drawable.uerlogo,"撒旦撒旦撒",R.drawable.icon_my_title));
        adapter2.addItem(new MyInfos(R.drawable.uerlogo,"撒旦撒旦撒",R.drawable.icon_my_title));
    }

    private void addDataToAdpter1() {
        for (int i = 0; i < ImageIds.length; i++) {
            adapter.addItem(new MyInfos(ImageIds[i], text[i], R.drawable.icon_my_title));
        }
        adapter.notifyDataSetChanged();
    }

    private void initAdapter1() {
        adapter = new XXListViewAdapter<MyInfos>(getActivity(), R.layout.item_listview_myinfo) {
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
}
