package com.cn.smallrecipe.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cn.smallrecipe.ParentFragment;
import com.cn.smallrecipe.R;

/**
 * Created by Administrator on 2016/2/24.
 */
public class F_Sniff extends ParentFragment {
    private View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.f_sniff,null);
        return view;
    }
}
