package cn.com.xxutils;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;
import android.util.Log;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

/**
 * Created by Administrator on 2016/2/22.
 */
public class XXApplication extends Application {
    public static final String TAG = "libs-xxutils";
    private RefWatcher mRefWatcher;

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
        mRefWatcher = LeakCanary.install(this);
    }


    private void enabledStrictMode() {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .penaltyDeath()
                    .build());
    }
}
