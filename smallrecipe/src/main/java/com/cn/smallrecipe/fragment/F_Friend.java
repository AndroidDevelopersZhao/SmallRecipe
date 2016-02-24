package com.cn.smallrecipe.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cn.smallrecipe.ParentFragment;
import com.cn.smallrecipe.R;

/**
 * Created by Administrator on 2016/2/24.
 */
public class F_Friend extends ParentFragment {
    private View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.f_friend,null);
        return view;
    }
}
