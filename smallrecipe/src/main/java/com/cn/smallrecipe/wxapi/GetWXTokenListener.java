package com.cn.smallrecipe.wxapi;

import com.cn.smallrecipe.datainfo.wechat.WeChatTokenInfo;

/**
 * Created by Administrator on 2016/3/7.
 */
public interface GetWXTokenListener {
    void onSucc(WeChatTokenInfo weChatTokenInfo);

    void onError(String errorMsg);
}
