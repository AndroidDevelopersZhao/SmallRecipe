package com.cn.smallrecipe.wxapi;

import com.cn.smallrecipe.datainfo.wechat.WeChatRefreshTokenInfo;

/**
 * Created by Administrator on 2016/3/7.
 */
public interface RefreshWXTokenListener {
    void onSucc(WeChatRefreshTokenInfo refreshTokenInfo);

    void onError(String errorMsg);
}
