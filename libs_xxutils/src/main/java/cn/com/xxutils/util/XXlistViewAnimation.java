package cn.com.xxutils.util;

import android.widget.BaseAdapter;
import android.widget.ListView;

import cn.com.xxutils.swinginadapters.AnimationAdapter;
import cn.com.xxutils.swinginadapters.prepared.AlphaInAnimationAdapter;
import cn.com.xxutils.swinginadapters.prepared.ScaleInAnimationAdapter;
import cn.com.xxutils.swinginadapters.prepared.SwingBottomInAnimationAdapter;
import cn.com.xxutils.swinginadapters.prepared.SwingLeftInAnimationAdapter;
import cn.com.xxutils.swinginadapters.prepared.SwingRightInAnimationAdapter;

/**
 * Created by Administrator on 2016/2/23.
 */
public class XXlistViewAnimation {
    private ListView listView;
    private BaseAdapter adapter;

    public XXlistViewAnimation(ListView listView, BaseAdapter adapter) {
        this.listView = listView;
        this.adapter = adapter;
    }

    public void setAlphaAdapter() {
        AnimationAdapter animAdapter = new AlphaInAnimationAdapter(adapter);
        animAdapter.setAbsListView(listView);
        listView.setAdapter(animAdapter);
    }

    public void setLeftAdapter() {
        AnimationAdapter animAdapter = new SwingLeftInAnimationAdapter(adapter);
        animAdapter.setAbsListView(listView);
        listView.setAdapter(animAdapter);
    }

    public void setRightAdapter() {
        AnimationAdapter animAdapter = new SwingRightInAnimationAdapter(adapter);
        animAdapter.setAbsListView(listView);
        listView.setAdapter(animAdapter);
    }

    public void setBottomAdapter() {
        AnimationAdapter animAdapter = new SwingBottomInAnimationAdapter(adapter);
        animAdapter.setAbsListView(listView);
        listView.setAdapter(animAdapter);
    }

    public void setBottomRightAdapter() {
        AnimationAdapter animAdapter = new SwingBottomInAnimationAdapter(new SwingRightInAnimationAdapter(adapter));
        animAdapter.setAbsListView(listView);
        listView.setAdapter(animAdapter);
    }

    public void setScaleAdapter() {
        AnimationAdapter animAdapter = new ScaleInAnimationAdapter(adapter);
        animAdapter.setAbsListView(listView);
        listView.setAdapter(animAdapter);
    }

}
