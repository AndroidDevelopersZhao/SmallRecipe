package com.cn.smallrecipe.wxapi;

import com.cn.smallrecipe.datainfo.wechat.WXUserInfo;

/**
 * Created by Administrator on 2016/3/7.
 */
public interface GetWXUserInfoListener {
    void onSucc(WXUserInfo wxUserInfo);

    void onError(String errorMsg);
}
