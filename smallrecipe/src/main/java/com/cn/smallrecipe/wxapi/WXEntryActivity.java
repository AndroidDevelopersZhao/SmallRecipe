package com.cn.smallrecipe.wxapi;


import com.cn.smallrecipe.MyActivity;
import com.cn.smallrecipe.R;
import com.cn.smallrecipe.Util;
import com.cn.smallrecipe.datainfo.wechat.WXUserInfo;
import com.cn.smallrecipe.datainfo.wechat.WeChatRefreshTokenInfo;
import com.cn.smallrecipe.datainfo.wechat.WeChatTokenInfo;
import com.google.gson.Gson;
import com.tencent.connect.common.Constants;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.modelmsg.ShowMessageFromWX;
import com.tencent.mm.sdk.modelmsg.WXAppExtendObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import cn.com.xxutils.progress.XXSVProgressHUD;
import cn.com.xxutils.util.XXBase64Utils;
import cn.com.xxutils.util.XXHttpClient;
import cn.com.xxutils.util.XXImagesLoader;
import cn.com.xxutils.util.XXUtils;
import cn.com.xxutils.volley.RequestQueue;
import cn.com.xxutils.volley.Response;
import cn.com.xxutils.volley.VolleyError;
import cn.com.xxutils.volley.toolbox.ImageLoader;
import cn.com.xxutils.volley.toolbox.ImageRequest;
import cn.com.xxutils.volley.toolbox.Volley;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.w(MyActivity.TAG, "进入WXEntryActivity");
        api = WXAPIFactory.createWXAPI(this, Util.APP_ID, true);
//        getActionBar().setDisplayShowTitleEnabled(false);
        api.handleIntent(getIntent(), this);
    }
//
//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//
//        setIntent(intent);
//        api.handleIntent(intent, this);
//    }

    @Override
    public void onReq(BaseReq req) {
        Log.w(MyActivity.TAG, "进入onReq：" + req);
    }

    @Override
    public void onResp(BaseResp resp) {
        Log.w(MyActivity.TAG, "进入onResp：" + resp);
        String result = null;
        resp.fromBundle(getIntent().getExtras());
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                XXSVProgressHUD.showWithStatus(this, "正在授权登陆...");
                result = "成功";

                String code = ((SendAuth.Resp) resp).code;
                String state = ((SendAuth.Resp) resp).state;
                String lang = ((SendAuth.Resp) resp).lang;
                String country = ((SendAuth.Resp) resp).country;
                String openid = ((SendAuth.Resp) resp).openId;
                getToken(code);
                Log.w(MyActivity.TAG, "openid:" + openid);
                Log.w(MyActivity.TAG, "country:" + country);
                Log.w(MyActivity.TAG, "lang:" + lang);
                Log.w(MyActivity.TAG, "state:" + state);
                Log.w(MyActivity.TAG, "code:" + code);

                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = "用户取消";
                Toast.makeText(this, result, Toast.LENGTH_LONG).show();
                this.finish();
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                Toast.makeText(this, result, Toast.LENGTH_LONG).show();
                result = "验证失败";
                this.finish();
                break;
            default:
                result = "未知错误";
                Toast.makeText(this, result, Toast.LENGTH_LONG).show();
                this.finish();
                break;

        }

    }

    /**
     * 获取Token
     *
     * @param code
     */
    private Handler handler_getToken = null;

    private void getToken(String code) {

        //获取token
        handler_getToken = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case -1:
                        Toast.makeText(WXEntryActivity.this, msg.getData().getString("data"), Toast.LENGTH_SHORT).show();
                        if (XXSVProgressHUD.isShowing(WXEntryActivity.this)) {
                            XXSVProgressHUD.dismiss(WXEntryActivity.this);
                        }
                        break;

                    case 1:
                        WeChatTokenInfo weChatTokenInfo = (WeChatTokenInfo) msg.getData().getSerializable("data");
                        getWXUserinfo(weChatTokenInfo.getOpenid(), weChatTokenInfo.getAccess_token());

                        break;
                }
            }
        };
        Util.getWXToken(Util.APP_ID, Util.SECRET, code, new GetWXTokenListener() {
            @Override
            public void onSucc(WeChatTokenInfo tokenInfo) {
                Log.w(TAG, "Token获取成功");
                Log.w(TAG, "Token:" + tokenInfo.getAccess_token());
                Log.w(TAG, "expires_in:" + tokenInfo.getExpires_in());
                Log.w(TAG, "refresh_token:" + tokenInfo.getRefresh_token());
                Log.w(TAG, "openid:" + tokenInfo.getOpenid());
                Log.w(TAG, "scope:" + tokenInfo.getScope());
                Log.w(TAG, "unionid:" + tokenInfo.getUnionid());
                Util.sendMsgToHandler(handler_getToken, tokenInfo, true);


//                        Log.w(TAG, "测试刷新Token的回调...");
//                        Log.w(TAG, "测试验证Token的回调...");
//                        Util.authWeChatTokenIsEff(tokenInfo.getOpenid(), tokenInfo.getAccess_token(), new AuthWXTokenListener() {
//                            @Override
//                            public void onSucc() {
//                                Log.w(TAG, "验证成功");
//                            }
//
//                            @Override
//                            public void onError(String msg) {
//                                Log.e(TAG, msg);
//                            }
//                        });

//                        Util.refreshWXToken(Util.APP_ID, tokenInfo.getRefresh_token(), new RefreshWXTokenListener() {
//                            @Override
//                            public void onSucc(WeChatRefreshTokenInfo info) {
//
//                            }
//
//                            @Override
//                            public void onError(String errorMsg) {
//
//                            }
//                        });
            }

            @Override
            public void onError(String errorMsg) {
                Log.e(TAG, errorMsg);
                Util.sendMsgToHandler(handler_getToken, errorMsg, false);
            }
        });
    }

    /*
    获取用户个人信息
     */
    private Handler handler_getWXUserInfo = null;

    private void getWXUserinfo(String openid, String token) {
        handler_getWXUserInfo = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case -1:
                        Toast.makeText(WXEntryActivity.this, msg.getData().getString("data"), Toast.LENGTH_SHORT).show();
                        if (XXSVProgressHUD.isShowing(WXEntryActivity.this)) {
                            XXSVProgressHUD.dismiss(WXEntryActivity.this);
                        }
                        break;

                    case 1:
                        Log.w(TAG, "开始获取用户头像");
                        WXUserInfo wxUserInfo = (WXUserInfo) msg.getData().getSerializable("data");
                        getWXUserLogo(wxUserInfo);
                        break;
                }
            }
        };
        Log.w(TAG, "获取用户个人数据：");
        Util.getWXUserInfo(token, openid, null, new GetWXUserInfoListener() {
            @Override
            public void onSucc(final WXUserInfo wxUserInfo) {
                Log.w(TAG, "获取成功");
                /**
                 * 读取测试
                 */
                Log.w(TAG, "openid：" + wxUserInfo.getOpenid());
                Log.w(TAG, "nickname：" + wxUserInfo.getNickname());
                Log.w(TAG, "sex：" + wxUserInfo.getSex());
                Log.w(TAG, "language：" + wxUserInfo.getLanguage());
                Log.w(TAG, "city：" + wxUserInfo.getCity());
                Log.w(TAG, "province：" + wxUserInfo.getProvince());
                Log.w(TAG, "country：" + wxUserInfo.getCountry());
                Log.w(TAG, "headimgurl：" + wxUserInfo.getHeadimgurl());
                Log.w(TAG, "unionid：" + wxUserInfo.getUnionid());
                Log.w(TAG, "privilege：" + wxUserInfo.getPrivilege().toString());
                //登陆操作业务逻辑
                Util.sendMsgToHandler(handler_getWXUserInfo, wxUserInfo, true);
            }

            @Override
            public void onError(String errorMsg) {
                Log.e(TAG, "获取失败，" + errorMsg);
                Util.sendMsgToHandler(handler_getWXUserInfo, errorMsg, false);
            }
        });
    }

    /**
     * 获取微信用户头像
     *
     * @param wxUserInfo
     */
    private Handler handler_getWXUserLogo = null;

    private void getWXUserLogo(final WXUserInfo wxUserInfo) {
        handler_getWXUserLogo = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case -1:
                        Toast.makeText(WXEntryActivity.this, msg.getData().getString("data"), Toast.LENGTH_SHORT).show();
                        if (XXSVProgressHUD.isShowing(WXEntryActivity.this)) {
                            XXSVProgressHUD.dismiss(WXEntryActivity.this);
                        }
                        break;

                    case 1:
                        Bitmap bitmap = (Bitmap) msg.getData().getParcelable("data");
                        Log.w(TAG, bitmap != null ? "头像获取成功" : "头像获取失败");
                        Log.w(TAG, "Base64编码后：" + XXUtils.bitmapToBase64(bitmap));
                        Log.e(TAG, "*******************************开始上送注册信息到后台*******************************");
                        registerWithWeChat(wxUserInfo, bitmap);

                        break;
                }
            }
        };

        XXHttpClient client = new XXHttpClient(wxUserInfo.getHeadimgurl(), true, new XXHttpClient.XXHttpResponseListener() {
            @Override
            public void onSuccess(int i, byte[] bytes) {
                if (bytes.length != 0) {
                    Util.sendMsgToHandler(handler_getWXUserLogo, BitmapFactory.decodeByteArray(bytes, 0, bytes.length), true);
                } else {
                    Util.sendMsgToHandler(handler_getWXUserLogo, "获取异常，请稍后再试", false);
                }
            }

            @Override
            public void onError(int i, Throwable throwable) {
                Util.sendMsgToHandler(handler_getWXUserLogo, "网络异常，请稍后再试", false);
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {

            }
        });
        client.doGet(15000);
    }

    /**
     * 通过微信注册
     *
     * @param wxUserInfo
     * @param bitmap
     */
    private void registerWithWeChat(WXUserInfo wxUserInfo, Bitmap bitmap) {
        /**
         *验证当前用户是否注册----所需参数：openid------结果：（返回用户不存在/登陆成功的用户信息）
         * 用户不存在时调用注册接口-----所需参数：openid、username、userlogo、usernumber --
         * --结果：注册成功，登陆成功的用户信息/注册失败的失败信息
         */
        Log.w(TAG, "查询该账户是否已经存在,即将上送数据(openid:)：" + wxUserInfo.getOpenid());

    }


    private String TAG = MyActivity.TAG;


//    private void goToGetMsg() {
//        Intent intent = new Intent(this, GetFromWXActivity.class);
//        intent.putExtras(getIntent());
//        startActivity(intent);
//        finish();
//    }

//    private void goToShowMsg(ShowMessageFromWX.Req showReq) {
//        WXMediaMessage wxMsg = showReq.message;
//        WXAppExtendObject obj = (WXAppExtendObject) wxMsg.mediaObject;
//
//        StringBuffer msg = new StringBuffer(); // ��֯һ������ʾ����Ϣ����
//        msg.append("description: ");
//        msg.append(wxMsg.description);
//        msg.append("\n");
//        msg.append("extInfo: ");
//        msg.append(obj.extInfo);
//        msg.append("\n");
//        msg.append("filePath: ");
//        msg.append(obj.filePath);
//
//        Intent intent = new Intent(this, ShowFromWXActivity.class);
//        intent.putExtra(Constants.ShowMsgActivity.STitle, wxMsg.title);
//        intent.putExtra(Constants.ShowMsgActivity.SMessage, msg.toString());
//        intent.putExtra(Constants.ShowMsgActivity.BAThumbData, wxMsg.thumbData);
//        startActivity(intent);
//        finish();
//    }
}