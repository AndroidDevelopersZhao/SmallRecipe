package com.cn.smallrecipe.wxapi;


import com.cn.smallrecipe.MyActivity;
import com.cn.smallrecipe.R;
import com.cn.smallrecipe.Util;
import com.cn.smallrecipe.activity.LoginActivity;
import com.cn.smallrecipe.activity.MainActivity;
import com.cn.smallrecipe.datainfo.register.ResultToApp;
import com.cn.smallrecipe.datainfo.search.Result;
import com.cn.smallrecipe.datainfo.wechat.WXUserInfo;
import com.cn.smallrecipe.datainfo.wechat.WeChatRefreshTokenInfo;
import com.cn.smallrecipe.datainfo.wechat.WeChatTokenInfo;
import com.google.gson.Gson;
import com.tencent.connect.common.Constants;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.ShowMessageFromWX;
import com.tencent.mm.sdk.modelmsg.WXAppExtendObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.handler.UMWXHandler;
import com.umeng.socialize.weixin.view.WXCallbackActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import cn.com.xxutils.alerterview.OnItemClickListener;
import cn.com.xxutils.alerterview.XXAlertView;
import cn.com.xxutils.progress.XXSVProgressHUD;
import cn.com.xxutils.util.XXBase64Utils;
import cn.com.xxutils.util.XXHttpClient;
import cn.com.xxutils.util.XXImagesLoader;
import cn.com.xxutils.util.XXSharedPreferences;
import cn.com.xxutils.util.XXUtils;
import cn.com.xxutils.volley.RequestQueue;
import cn.com.xxutils.volley.Response;
import cn.com.xxutils.volley.VolleyError;
import cn.com.xxutils.volley.toolbox.ImageLoader;
import cn.com.xxutils.volley.toolbox.ImageRequest;
import cn.com.xxutils.volley.toolbox.Volley;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    private IWXAPI api;
    private XXSharedPreferences sharedPreferences;
    protected UMWXHandler mWxHandler = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.w(MyActivity.TAG, "进入WXEntryActivity");
        api = WXAPIFactory.createWXAPI(this, Util.APP_ID, true);
//        getActionBar().setDisplayShowTitleEnabled(false);
        api.handleIntent(getIntent(), this);
        UMShareAPI api = UMShareAPI.get(this);
        this.mWxHandler = (UMWXHandler)api.getHandler(SHARE_MEDIA.WEIXIN);
        com.umeng.socialize.utils.Log.e("xxxx wxhandler=" + this.mWxHandler);
        this.mWxHandler.onCreate(this, PlatformConfig.getPlatform(SHARE_MEDIA.WEIXIN));
        this.mWxHandler.getWXApi().handleIntent(this.getIntent(), this);
    }
//
//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//
//        setIntent(intent);
//        api.handleIntent(intent, this);
//    }

    protected final void onNewIntent(Intent paramIntent) {
        com.umeng.socialize.utils.Log.d(this.TAG, "### WXCallbackActivity   onNewIntent");
        super.onNewIntent(paramIntent);
        this.setIntent(paramIntent);
        UMShareAPI api = UMShareAPI.get(this);
        this.mWxHandler = (UMWXHandler)api.getHandler(SHARE_MEDIA.WEIXIN);
        this.mWxHandler.onCreate(this, PlatformConfig.getPlatform(SHARE_MEDIA.WEIXIN));
        this.mWxHandler.getWXApi().handleIntent(paramIntent, this);
    }
    @Override
    public void onReq(BaseReq req) {
        if(this.mWxHandler != null) {
            this.mWxHandler.getWXEventHandler().onReq(req);
        }
        Log.w(MyActivity.TAG, "进入onReq：" + req);
    }

    @Override
    public void onResp(BaseResp resp) {
        if(this.mWxHandler != null) {
            this.mWxHandler.getWXEventHandler().onResp(resp);
        }
        Log.w(MyActivity.TAG, "进入onResp：" + resp);
        String result = null;
        resp.fromBundle(getIntent().getExtras());
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:

                result = "成功";
                try {
                    String code = ((SendAuth.Resp) resp).code;
                    String state = ((SendAuth.Resp) resp).state;
                    String lang = ((SendAuth.Resp) resp).lang;
                    String country = ((SendAuth.Resp) resp).country;
                    String openid = ((SendAuth.Resp) resp).openId;
                    XXSVProgressHUD.showWithStatus(this, "正在授权登陆...");
                    getToken(code);
                    Log.w(MyActivity.TAG, "openid:" + openid);
                    Log.w(MyActivity.TAG, "country:" + country);
                    Log.w(MyActivity.TAG, "lang:" + lang);
                    Log.w(MyActivity.TAG, "state:" + state);
                    Log.w(MyActivity.TAG, "code:" + code);
                } catch (Throwable throwable) {
                    Log.d(TAG, "AAAAAAAAAAAAAAAAAAAA" + ((SendMessageToWX.Resp) resp).toString());
                    finish();

                }


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
//                        Toast.makeText(WXEntryActivity.this, msg.getData().getString("data"), Toast.LENGTH_SHORT).show();
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
    private Handler handler_checkWXUserIsExist = null;

    /**
     * 检测当前登陆的微信用户是否已经注册
     *
     * @param wxUserInfo
     * @param bitmap
     */
    private void registerWithWeChat(final WXUserInfo wxUserInfo, final Bitmap bitmap) {
        /**
         *验证当前用户是否注册----所需参数：openid------结果：（返回用户不存在/登陆成功的用户信息）
         * 用户不存在时调用注册接口-----所需参数：openid、username、userlogo、usernumber --
         * --结果：注册成功，登陆成功的用户信息/注册失败的失败信息
         */
        Log.w(TAG, "查询该账户是否已经存在,即将上送数据(openid:)：" + wxUserInfo.getOpenid());

        handler_checkWXUserIsExist = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (XXSVProgressHUD.isShowing(WXEntryActivity.this)) {
                    XXSVProgressHUD.dismiss(WXEntryActivity.this);
                }
                switch (msg.what) {
                    case -1:
                        //查询异常，无结果时进入的回调
                        Toast.makeText(WXEntryActivity.this, msg.getData().getString("data"), Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        //查询有结果时进入该回调
                        final ResultToApp app = (ResultToApp) msg.getData().getSerializable("data");
                        if (app.getErrorCode() == -22) {
                            //该账号未注册
                            showAlertView(wxUserInfo, bitmap);
                        } else if (app.getErrorCode() == 9000) {
                            //登陆成功
                            sharedPreferences = new XXSharedPreferences(MainActivity.SHAREDSESSIONIDSAVEEDNAME);
                            sharedPreferences.put(WXEntryActivity.this, "sessionid", app.getRespData().getSessionId());
                            sharedPreferences.put(WXEntryActivity.this, "usernumber", app.getRespData().getUsernumber());

                            sharedPreferences.put(WXEntryActivity.this, "username", app.getRespData().getUsername());
                            sharedPreferences.put(WXEntryActivity.this, "userid", app.getRespData().getUserid());
                            sharedPreferences.put(WXEntryActivity.this, "userlogo", app.getRespData().getUserlogo());
                            uid = sharedPreferences.get(WXEntryActivity.this, "userid", app.getRespData().getUserid()).toString();
                            Log.d(TAG, "用户数据存入缓存成功");
                            startActivity(new Intent(WXEntryActivity.this, MainActivity.class));
                            finish();
                        } else if (app.getErrorCode() == -3) {
                            final String reLoginId = app.getRespData().getReLoginId();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (XXSVProgressHUD.isShowing(WXEntryActivity.this)) {
                                        XXSVProgressHUD.dismiss(WXEntryActivity.this);
                                    }
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            SystemClock.sleep(500);
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    new XXAlertView("提示", "该账户已经登陆，是否重新登陆", "强制登陆", null, new String[]{"取消"}, WXEntryActivity.this, XXAlertView.Style.Alert, new OnItemClickListener() {
                                                        @Override
                                                        public void onItemClick(Object o, int position) {
                                                            Log.d(TAG, "position:" + position);
                                                            if (position == -1) {
                                                                //重新登陆
                                                                Log.d(TAG, "开始重新登陆。当前传入的usernumber=" + app.getRespData().getUsernumber());
                                                                Log.d(TAG, "开始重新登陆。当前传入的ReloginID=" + reLoginId);
                                                                reLogin(app.getRespData().getUsernumber(), reLoginId);
                                                            } else {
                                                                Util.sendMsgToHandler(handler_registerwx, "当账号被使用时您可以强制登陆迫使对方下线", false);
                                                            }
                                                        }
                                                    }).show();
                                                }
                                            });

                                        }
                                    }).start();

                                }
                            });
                        }
                        break;
                }
            }
        };

        XXHttpClient client = new XXHttpClient(Util.URL_CHECK_USER_WX, true, new XXHttpClient.XXHttpResponseListener() {
            @Override
            public void onSuccess(int i, byte[] bytes) {
                Log.w(TAG, "检测微信账号是否注册的返回信息：" + new String(bytes));
                ResultToApp app = new Gson().fromJson(new String(bytes), ResultToApp.class);
                Util.sendMsgToHandler(handler_checkWXUserIsExist, app, true);
            }

            @Override
            public void onError(int i, Throwable throwable) {
                Log.e(TAG, "检测微信账号是否注册的返回异常，网络故障");
                Util.sendMsgToHandler(handler_checkWXUserIsExist, "网络异常", false);
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {

            }
        });
        client.put("openid", wxUserInfo.getOpenid());
        client.doPost(15000);
    }

    private void showAlertView(final WXUserInfo wxUserInfo, final Bitmap bitmap) {
        final EditText et = new EditText(WXEntryActivity.this);

        AlertDialog.Builder alerter = new AlertDialog.Builder(WXEntryActivity.this).setTitle("请绑定手机号")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String input = et.getText().toString();
                        if (!input.equals("")) {
                            //绑定手机号并登陆
                            if (XXUtils.checkMobileNumberValid(input)) {
                                XXSVProgressHUD.showWithStatus(WXEntryActivity.this, "正在绑定账号并登陆");
                                boindPhoneNumber(input, wxUserInfo.getOpenid(), wxUserInfo.getNickname(), bitmap);
                            } else {
                                Toast.makeText(WXEntryActivity.this, "手机号码格式不正确", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(WXEntryActivity.this, "手机号码不能为空，请重新绑定", Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.e(TAG, "取消操作");
                    }
                });
        alerter.setCancelable(false);
        alerter.show();
    }

    /**
     * 通过微信openid绑定账号
     *
     * @param usernumber
     * @param openid
     * @param username
     * @param userlogo
     */
    private String uid = null;
    private Handler handler_registerwx = null;

    private void boindPhoneNumber(final String usernumber, String openid, String username, Bitmap userlogo) {

        handler_registerwx = new Handler() {
            @Override
            public void handleMessage(Message msg) {

                if (XXSVProgressHUD.isShowing(WXEntryActivity.this)) {
                    XXSVProgressHUD.dismiss(WXEntryActivity.this);
                }
                switch (msg.what) {
                    case -1:
                        Toast.makeText(WXEntryActivity.this, msg.getData().getString("data"), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(WXEntryActivity.this, LoginActivity.class));
                        finish();
                        break;

                    case 1:
                        startActivity(new Intent(WXEntryActivity.this, MainActivity.class));
                        finish();
                        break;
                }
            }
        };
        //绑定手机号并登陆
        XXHttpClient client = new XXHttpClient(Util.URL_SERVICE_BONIDUSERFORWX, true, new XXHttpClient.XXHttpResponseListener() {
            @Override
            public void onSuccess(int i, byte[] bytes) {

                Log.d(TAG, "绑定的回调：" + new String(bytes));
                ResultToApp resultToApp = new Gson().fromJson(new String(bytes), ResultToApp.class);

                Log.d(TAG, "登陆返回状态码：" + resultToApp.getErrorCode());
                Log.d(TAG, "登陆返回状态信息：" + resultToApp.getResultMsg());
                if (resultToApp.getErrorCode() == -10) {
                    Util.sendMsgToHandler(handler_registerwx, resultToApp.getResultMsg(), false);
                } else if (resultToApp.getErrorCode() == 9000) {

                    sharedPreferences = new XXSharedPreferences(MainActivity.SHAREDSESSIONIDSAVEEDNAME);
                    sharedPreferences.put(WXEntryActivity.this, "sessionid", resultToApp.getRespData().getSessionId());
                    sharedPreferences.put(WXEntryActivity.this, "usernumber", usernumber);

                    sharedPreferences.put(WXEntryActivity.this, "username", resultToApp.getRespData().getUsername());
                    sharedPreferences.put(WXEntryActivity.this, "userid", resultToApp.getRespData().getUserid());
                    sharedPreferences.put(WXEntryActivity.this, "userlogo", resultToApp.getRespData().getUserlogo());
                    uid = sharedPreferences.get(WXEntryActivity.this, "userid", resultToApp.getRespData().getUserid()).toString();
                    Log.d(TAG, "用户数据存入缓存成功");
                    Util.sendMsgToHandler(handler_registerwx, resultToApp.getResultMsg(), true);
                } else {
                    Util.sendMsgToHandler(handler_registerwx, resultToApp.getResultMsg(), false);
                }
            }

            @Override
            public void onError(int i, Throwable throwable) {
                Util.sendMsgToHandler(handler_registerwx, "网络异常", false);
                Log.e(TAG, "网络异常");
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {

            }
        });
        client.put("openid", openid);
        client.put("username", username);
        client.put("userlogo", XXUtils.bitmapToBase64(userlogo));
        client.put("usernumber", usernumber);
        Log.d(TAG, "绑定微信用户号上送数据：" + client.getAllParams());
        client.doPost(15000);
    }


    /**
     * 重新登陆
     *
     * @param usernumber
     * @param reLoginId
     */
    private Handler Handler_reLogin = null;

    private void reLogin(final String usernumber, String reLoginId) {
        Handler_reLogin = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                if (XXSVProgressHUD.isShowing(WXEntryActivity.this)) {
                    XXSVProgressHUD.dismiss(WXEntryActivity.this);
                }
                switch (msg.what) {
                    case -1:
                        Toast.makeText(WXEntryActivity.this, "登陆失败," + msg.getData().getString("data"), Toast.LENGTH_SHORT).show();
                        break;

                    case 1:
                        startActivity(new Intent(WXEntryActivity.this, MainActivity.class));
                        finish();
                        break;
                }
            }
        };
        XXSVProgressHUD.showWithStatus(WXEntryActivity.this, "正在强制登陆...");
        XXHttpClient client = new XXHttpClient(Util.URL_SERVICE_RELOGIN, true, new XXHttpClient.XXHttpResponseListener() {
            @Override
            public void onSuccess(int i, byte[] bytes) {
                Log.d(TAG, "强制登陆结果：" + new String(bytes));
                ResultToApp app = new Gson().fromJson(new String(bytes), ResultToApp.class);
                if (app.getErrorCode() == 9000) {

                    sharedPreferences = new XXSharedPreferences(MainActivity.SHAREDSESSIONIDSAVEEDNAME);

                    sharedPreferences.put(WXEntryActivity.this, "sessionid", app.getRespData().getSessionId());
                    sharedPreferences.put(WXEntryActivity.this, "usernumber", usernumber);

                    sharedPreferences.put(WXEntryActivity.this, "username", app.getRespData().getUsername());
                    sharedPreferences.put(WXEntryActivity.this, "userid", app.getRespData().getUserid());
                    sharedPreferences.put(WXEntryActivity.this, "userlogo", app.getRespData().getUserlogo());
                    Log.d(TAG, "强制登陆成功，已写入共享参数，读取测试sessionid：" + sharedPreferences.get(WXEntryActivity.this, "sessionid", "") + "\nusernumber=" +
                            usernumber + "\nuid=" + sharedPreferences.get(WXEntryActivity.this, "userid", app.getRespData().getUserid().toString()));
                    uid = sharedPreferences.get(WXEntryActivity.this, "userid", "").toString();
                    Util.sendMsgToHandler(Handler_reLogin, app.getResultMsg(), true);

                } else {
                    Util.sendMsgToHandler(Handler_reLogin, app.getResultMsg(), false);
                }
            }

            @Override
            public void onError(int i, Throwable throwable) {
                Log.e(TAG, "强制登陆网络异常");
                Util.sendMsgToHandler(Handler_reLogin, "网络异常", false);
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {

            }
        });
        client.put("usernumber", usernumber);
        client.put("reloginid", reLoginId);
        client.doPost(15000);
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