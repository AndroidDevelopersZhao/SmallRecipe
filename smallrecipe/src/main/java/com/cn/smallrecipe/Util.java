package com.cn.smallrecipe;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.cn.smallrecipe.datainfo.AllInfo;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/2/24.
 */
public class Util {
    public static final String TAG = "SmallRecipe";
    public static final String APPKEY = "2fcb58bbc897587691627b81093c63d0";
    public static final String URL_SEARCH = "http://apis.juhe.cn/cook/query.php";

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