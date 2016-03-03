package com.cn.smallrecipe;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.cn.smallrecipe.datainfo.register.RespData;
import com.cn.smallrecipe.datainfo.search.AllInfo;
import com.cn.smallrecipe.datainfo.search.Data;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/2/24.
 */
public class Util {
    public static final String TAG = "SmallRecipe";
    public static final String APPKEY = "2fcb58bbc897587691627b81093c63d0";
    public static final String URL_SEARCH = "http://apis.juhe.cn/cook/query.php";//关键字索引菜单
    public static final String URL_GETRECIPEDETAILS_JUHE = "http://apis.juhe.cn/cook/queryid";//根据ID索引菜谱详细信息

        private static String url = "http://221.228.88.249:8080/SmallRecipeService/";
//    private static String url = "http://192.168.13.107:8080/SmallRecipeService/";//公司
//    private static String url = "http://192.168.51.109:8080/SmallRecipeService/";//家


    public static final String URL_SERVICE_REGISTER = url + "register";//注册URL
    public static final String URL_SERVICE_LOGIN = url + "login";//登陆URL
    public static final String URL_SERVICE_RELOGIN = url + "relogin";//重新登陆URL
    public static final String URL_SERVICE_UNLOGIN = url + "unlogin";//退出登陆URL
    public static final String URL_SERVICE_AUTH_SESSIONID = url + "authsessionid";//退出登陆URL
    public static final String URL_SERVICE_AUTH_UPDATEUSERLOGO = url + "updateuserlogo";//更新用户头像
    public static final String URL_SERVICE_AUTH_GETUSERLOGO = url + "getuserlogo";//获取用户头像
    public static final String URL_SERVICE_GETALLRECIPEDATA = url + "allrecipe";//获取菜谱所有信息
    public static final String URL_SERVICE_STARORUNSTAR = url + "starorunstr";//收藏或者取消收藏

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
}
