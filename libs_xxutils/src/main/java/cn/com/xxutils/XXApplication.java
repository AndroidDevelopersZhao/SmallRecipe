package cn.com.xxutils;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;
import android.util.Log;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.PlatformConfig;

import cn.smssdk.SMSSDK;


/**
 * Created by Administrator on 2016/2/22.
 */
public class XXApplication extends Application {
    public static final String TAG = "libs-xxutils";

    //    private RefWatcher refWatcher;
//    public static RefWatcher getRefWatcher(Context context) {
//        XXApplication application = (XXApplication) context
//                .getApplicationContext();
//        return application.refWatcher;
//    }
    @Override
    public void onCreate() {
        super.onCreate();
        //创建默认的ImageLoader配置参数
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration
                .createDefault(this);

        //Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(configuration);
        Log.d(TAG, "Universal-Image-Loader 在Application初始化成功");
//        mRefWatcher = LeakCanary.install(this);
        SMSSDK.initSDK(this, "1003e1393046d", "07a19ef8a4ea4f02c699bc4399d4bda4");
        Log.d(TAG, "SMSSDK 在Application初始化成功");
        initSharedInfo();
        AnalyticsConfig.enableEncrypt(true);
        Log.d(TAG, "日志加密已开启");
        MobclickAgent.setDebugMode(true);
        Log.d(TAG, "集成测试开启");
    }

    private void initSharedInfo() {
        //微信APP_ID S
        PlatformConfig.setWeixin("wx220e16bd4df59c89", "b9fda74227172b69a55316e9c0367bfc");
        //QQ
        PlatformConfig.setQQZone("1105221610", "IL55aeCXCBfJVPwJ");
        Log.w(TAG, "微信、QQ授权友盟成功");
    }

    private void enabledStrictMode() {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .penaltyDeath()
                .build());
    }
}
