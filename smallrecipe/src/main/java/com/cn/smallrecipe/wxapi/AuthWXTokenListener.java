package com.cn.smallrecipe.wxapi;

/**
 * Created by Administrator on 2016/3/7.
 */
public interface AuthWXTokenListener {
    void onSucc();

    void onError(String msg);
}
