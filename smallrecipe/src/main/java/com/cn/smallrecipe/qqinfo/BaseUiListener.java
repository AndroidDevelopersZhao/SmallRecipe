package com.cn.smallrecipe.qqinfo;

import android.util.Log;

import com.cn.smallrecipe.Util;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;

/**
 * Created by Administrator on 2016/3/3.
 */
public class BaseUiListener implements IUiListener {
    @Override
    public void onComplete(Object o) {
        Log.d(Util.TAG, "QQ-onComplete:" + o.toString());
    }

    @Override
    public void onError(UiError uiError) {
        Log.e(Util.TAG, "QQ-onError:" + uiError.errorMessage);
    }

    @Override
    public void onCancel() {
        Log.d(Util.TAG, "QQ-onCancel");
    }
}
