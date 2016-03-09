package com.cn.smallrecipe;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.cn.smallrecipe.datainfo.register.RespData;
import com.cn.smallrecipe.datainfo.register.ResultToApp;
import com.cn.smallrecipe.datainfo.search.AllInfo;
import com.cn.smallrecipe.datainfo.search.Data;
import com.cn.smallrecipe.datainfo.wechat.WXUserInfo;
import com.cn.smallrecipe.datainfo.wechat.WeChatRefreshTokenInfo;
import com.cn.smallrecipe.datainfo.wechat.WeChatTokenInfo;
import com.cn.smallrecipe.wxapi.AuthWXTokenListener;
import com.cn.smallrecipe.wxapi.GetWXTokenListener;
import com.cn.smallrecipe.wxapi.GetWXUserInfoListener;
import com.cn.smallrecipe.wxapi.RefreshWXTokenListener;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.com.xxutils.util.XXHttpClient;

/**
 * Created by Administrator on 2016/2/24.
 */
public class Util {
    public static final String TEXT = "我在使用这个软件为亲爱的做晚饭，推荐给你们哦";
    public static final String SHAREDURL = "http://a.app.qq.com/o/simple.jsp?pkgname=com.cn.smallrecipe";
    public static final String SHAREDIMAGEURL = "http://221.228.88.249:8080/SmallRecipeService/108.png";

    public static final String TAG = "SmallRecipe";
    public static final String APPKEY = "2fcb58bbc897587691627b81093c63d0";
    public static final String URL_SEARCH = "http://apis.juhe.cn/cook/query.php";//关键字索引菜单
    public static final String URL_GETRECIPEDETAILS_JUHE = "http://apis.juhe.cn/cook/queryid";//根据ID索引菜谱详细信息

//    private static String url = "http://221.228.88.249:8080/SmallRecipeService/";
    private static String url = "http://192.168.13.105:8080/SmallRecipeService/";//公司
//    private static String url = "http://192.168.12.106:8080/SmallRecipeService/";//家


    public static final String URL_SERVICE_REGISTER = url + "register";//注册URL
    public static final String URL_SERVICE_LOGIN = url + "login";//登陆URL
    public static final String URL_SERVICE_RELOGIN = url + "relogin";//重新登陆URL
    public static final String URL_SERVICE_UNLOGIN = url + "unlogin";//退出登陆URL
    public static final String URL_SERVICE_AUTH_SESSIONID = url + "authsessionid";//退出登陆URL
    public static final String URL_SERVICE_AUTH_UPDATEUSERLOGO = url + "updateuserlogo";//更新用户头像
    public static final String URL_SERVICE_AUTH_GETUSERLOGO = url + "getuserlogo";//获取用户头像
    public static final String URL_SERVICE_GETALLRECIPEDATA = url + "allrecipe";//获取菜谱所有信息
    public static final String URL_SERVICE_STARORUNSTAR = url + "starorunstr";//收藏或者取消收藏
    public static final String URL_SERVICE_REGISTERFORQQ = url + "registerqq";//检测该qq用户是否已经注册
    public static final String URL_SERVICE_BONIDUSERFORQQ = url + "boinduserforqq";//qq注册

    public static final String URL_CHECK_USER_WX = url + "registerfrowx";//检测该微信用户是否已经注册
    public static final String URL_SERVICE_BONIDUSERFORWX = url + "boinduserforwx";//微信注册

    public static final String APP_ID = "wx220e16bd4df59c89";
    public static final String SECRET = "b9fda74227172b69a55316e9c0367bfc";

    public static final String URL_GETWCHATTOKEN = "https://api.weixin.qq.com/sns/oauth2/access_token";//获取Token
    public static final String URL_REFRESHWCHATTOKEN = "https://api.weixin.qq.com/sns/oauth2/refresh_token";//刷新Token
    public static final String URL_AUTHTOKEN =
            " https://api.weixin.qq.com/sns/auth";//验证Token
    public static final String URL_GETWXUSERINFO = "https://api.weixin.qq.com/sns/userinfo";

    public static void sendMsgToHandler(Handler handler, Object object, boolean isSucc) {
        if (handler == null || object == null) {
            Log.e(TAG, "传入参数不能为空");
            return;
        }
        Message message = handler.obtainMessage();
        Bundle bundle = new Bundle();

        if (object instanceof String) {
            bundle.putString("data", object.toString());
        } else if (object instanceof ArrayList<?>) {
            bundle.putStringArrayList("data", (ArrayList) object);
        } else if (object instanceof AllInfo) {
            bundle.putSerializable("data", (AllInfo) object);
        } else if (object instanceof byte[]) {
            bundle.putSerializable("data", (byte[]) object);
        } else if (object instanceof Data) {
            bundle.putSerializable("data", (Data) object);
        } else if (object instanceof RespData) {
            bundle.putSerializable("data", (RespData) object);
        } else if (object instanceof WeChatTokenInfo) {
            bundle.putSerializable("data", (WeChatTokenInfo) object);
        } else if (object instanceof WXUserInfo) {
            bundle.putSerializable("data", (WXUserInfo) object);
        } else if (object instanceof Bitmap) {
            bundle.putParcelable("data", (Bitmap) object);
        } else if (object instanceof ResultToApp) {
            bundle.putSerializable("data", (ResultToApp) object);
        } else {
            bundle.putString(TAG, "参数类型未定义,请至工具类定义");
        }
        message.setData(bundle);
        if (isSucc) {
            message.what = 1;
        } else {
            message.what = -1;
        }
        handler.sendMessage(message);
    }

    /**
     * 通过code获取Token
     *
     * @param appid    APP_ID
     * @param secret   安全码
     * @param code     code
     * @param listener 返回数据
     */
    public static void getWXToken(String appid, String secret, String code, final GetWXTokenListener listener) {
        XXHttpClient client = new XXHttpClient(Util.URL_GETWCHATTOKEN, true, new XXHttpClient.XXHttpResponseListener() {
            @Override
            public void onSuccess(int i, byte[] bytes) {
                try {
                    WeChatTokenInfo tokenInfo = new Gson().fromJson(new String(bytes), WeChatTokenInfo.class);
                    listener.onSucc(tokenInfo);
                } catch (Throwable throwable) {
                    try {
                        JSONObject jo = new JSONObject(new String(bytes));
                        listener.onError(jo.getString("errmsg"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        listener.onError("获取失败,请稍后再试");
                    }


                }
            }

            @Override
            public void onError(int i, Throwable throwable) {
//                Log.e(TAG, "请求Token网络异常");
                listener.onError("网络异常");
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {

            }
        });
        client.put("appid", appid);
        client.put("secret", secret);
        client.put("code", code);
        client.put("grant_type", "authorization_code");
        client.doGet(15000);

    }

    /**
     * 刷新/续期Token
     *
     * @param appid                  APP_ID
     * @param refresh_token          由获取token成功后返回的refresh_token
     * @param refreshWXTokenListener 刷新结果回调
     */
    public static void refreshWXToken(String appid, String refresh_token,
                                      final RefreshWXTokenListener refreshWXTokenListener) {
        XXHttpClient client = new XXHttpClient(Util.URL_REFRESHWCHATTOKEN, true, new XXHttpClient.XXHttpResponseListener() {
            @Override
            public void onSuccess(int i, byte[] bytes) {
                Log.w(TAG, "刷新Token返回：" + new String(bytes));
                try {
                    WeChatRefreshTokenInfo refreshTokenInfo = new Gson().fromJson(new String(bytes), WeChatRefreshTokenInfo.class);
                    if (refreshWXTokenListener != null)
                        refreshWXTokenListener.onSucc(refreshTokenInfo);
                } catch (Throwable throwable) {
                    try {
                        JSONObject jo = new JSONObject(new String(bytes));
                        refreshWXTokenListener.onError(jo.getString("errmsg"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        refreshWXTokenListener.onError("刷新失败，请稍后再试");
                    }
                }

            }

            @Override
            public void onError(int i, Throwable throwable) {
                Log.e(TAG, "刷新Token网络异常");
                refreshWXTokenListener.onError("刷新Token失败，网络异常");
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {

            }
        });
        client.put("appid", appid);
        client.put("grant_type", "refresh_token");
        client.put("refresh_token", refresh_token);
        client.doGet(15000);
    }

    /**
     * 验证token是否有效
     *
     * @param openid   openid
     * @param token    验证的token
     * @param listener 验证结果回调
     */
    public static void authWeChatTokenIsEff(String openid, String token, final AuthWXTokenListener listener) {
        XXHttpClient client = new XXHttpClient(Util.URL_AUTHTOKEN, true, new XXHttpClient.XXHttpResponseListener() {
            @Override
            public void onSuccess(int i, byte[] bytes) {
                try {
                    JSONObject ji = new JSONObject(new String(bytes));
                    if (ji.getInt("errcode") == 0) {
                        if (listener != null)
                            listener.onSucc();
                    } else {
                        if (listener != null)
                            listener.onError(ji.getString("errmsg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    listener.onError("验证失败，请稍后再试");
                }
            }

            @Override
            public void onError(int i, Throwable throwable) {
                Log.e(TAG, "验证token失败，网络异常");
                listener.onError("验证token失败，网络异常");
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {

            }
        });
        client.put("openid", openid);
        client.put("access_token", token);
        client.doGet(15000);
    }

    /**
     * 获取用户个人信息（UnionID机制）lang为国家地区语言版本表示，默认（zh-CN）请传null
     *
     * @param access_token 调用凭证
     * @param openid       普通用户的标识，对当前开发者帐号唯一
     * @param lang         国家地区语言版本，zh_CN 简体，zh_TW 繁体，en 英语，默认为zh-CN
     * @param listener     获取结果回调
     */
    public static void getWXUserInfo(String access_token, String openid, String lang, final GetWXUserInfoListener listener) {
        XXHttpClient client = new XXHttpClient(URL_GETWXUSERINFO, true, new XXHttpClient.XXHttpResponseListener() {
            @Override
            public void onSuccess(int i, byte[] bytes) {
                Log.w(TAG, "获取微信用户个人信息的返回：" + new String(bytes));
                try {
                    WXUserInfo wxUserInfo = new Gson().fromJson(new String(bytes), WXUserInfo.class);
                    if (listener != null)
                        listener.onSucc(wxUserInfo);
                } catch (Throwable throwable) {
                    try {
                        JSONObject jo = new JSONObject(new String(bytes));
                        if (listener != null)
                            listener.onError(jo.getString("errmsg"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        if (listener != null)
                            listener.onError("获取失败，请稍后再试");
                    }
                }

            }

            @Override
            public void onError(int i, Throwable throwable) {
                Log.w(TAG, "获取微信用户个人信息网络异常");
                if (listener != null)
                    listener.onError("获取失败，网络异常");
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {

            }
        });
        client.put("access_token", access_token);
        client.put("openid", openid);
        if (lang != null)
            client.put("lang", lang);
        client.doGet(15000);
    }
}
