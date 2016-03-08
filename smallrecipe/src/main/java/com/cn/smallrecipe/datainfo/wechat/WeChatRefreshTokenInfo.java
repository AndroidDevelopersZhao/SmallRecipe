package com.cn.smallrecipe.datainfo.wechat;

/**
 * Created by Administrator on 2016/3/7.
 */
public final class WeChatRefreshTokenInfo {
    private String access_token = null;//接口调用凭证
    private String expires_in = null;//access_token接口调用凭证超时时间，单位（秒）
    private String refresh_token = null;//用户刷新access_token
    private String openid = null;//授权用户唯一标识
    private String scope = null;//用户授权的作用域，使用逗号（,）分隔

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(String expires_in) {
        this.expires_in = expires_in;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

}
