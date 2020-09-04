package com.xiayiye5.xiayiye5ad;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import com.bytedance.sdk.openadsdk.TTAdConfig;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdSdk;

public class DemoApplication extends Application {
    public static String PROCESS_NAME_XXXX = "process_name_xxxx";
    private int activityCount=0;

    @Override
    public void onCreate() {
        super.onCreate();
        initActivityLifecycleCallbacks();
        //强烈建议在应用对应的Application#onCreate()方法中调用，避免出现content为null的异常
        TTAdSdk.init(this,
                new TTAdConfig.Builder()
                        .appId("5102993")
                        .useTextureView(true) //使用TextureView控件播放视频,默认为SurfaceView,当有SurfaceView冲突的场景，可以使用TextureView
                        .appName("下一页广告")
                        .titleBarTheme(TTAdConstant.TITLE_BAR_THEME_DARK)
                        .allowShowNotify(true) //是否允许sdk展示通知栏提示
                        .allowShowPageWhenScreenLock(true) //是否在锁屏场景支持展示广告落地页
                        .debug(true) //测试阶段打开，可以通过日志排查问题，上线时去除该调用
                        .directDownloadNetworkType(TTAdConstant.NETWORK_STATE_WIFI) //允许直接下载的网络状态集合
                        .supportMultiProcess(true) //是否支持多进程，true支持
                        .asyncInit(true) //异步初始化sdk，开启可减少初始化耗时
                        //.httpStack(new MyOkStack3())//自定义网络库，demo中给出了okhttp3版本的样例，其余请自行开发或者咨询工作人员。
                        .build());

        //穿山甲SDK初始化
        //强烈建议在应用对应的Application#onCreate()方法中调用，避免出现content为null的异常
        TTAdManagerHolder.init(this);
        //如果明确某个进程不会使用到广告SDK，可以只针对特定进程初始化广告SDK的content
        //if (PROCESS_NAME_XXXX.equals(processName)) {
        //   TTAdSdk.init(context, config);
        //}
    }
    /**
     * 监听Activity变化
     */
    private void initActivityLifecycleCallbacks() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                ActivityManager.getInstance().addActivity(activity);
                Log.i("wsc", String.format("ActivityLifecycleCallbacks ---- onActivityCreated %s", activity.getLocalClassName()));
            }

            @Override
            public void onActivityStarted(Activity activity) {
                if (activityCount == 0) {
                    ActivityManager.getInstance().setAppInBackGround(false);
                    Log.i("wsc", String.format("ActivityLifecycleCallbacks ---- onActivityStarted 回到前台 %s ", activity.getLocalClassName()));
                }
                activityCount++;

                Log.i("wsc", String.format("ActivityLifecycleCallbacks ---- onActivityStarted %s ", activity.getLocalClassName()));


            }

            @Override
            public void onActivityResumed(Activity activity) {
                Log.i("wsc", String.format("ActivityLifecycleCallbacks ---- onActivityResumed %s ", activity.getLocalClassName()));


            }

            @Override
            public void onActivityPaused(Activity activity) {
                Log.i("wsc", String.format("ActivityLifecycleCallbacks ---- onActivityPaused %s ", activity.getLocalClassName()));

            }

            @Override
            public void onActivityStopped(Activity activity) {
                activityCount--;
                if (activityCount == 0) {
                    ActivityManager.getInstance().setAppInBackGround(true);
                    Log.i("wsc", String.format("ActivityLifecycleCallbacks ---- onActivityStopped app 在后台运行%s ", activity.getLocalClassName()));
                }

                Log.i("wsc", String.format("ActivityLifecycleCallbacks ---- onActivityStopped %s ", activity.getLocalClassName()));

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                ActivityManager.getInstance().removeActivity(activity);
                Log.i("wsc", String.format("ActivityLifecycleCallbacks ---- onActivityDestroyed %s ", activity.getLocalClassName()));

            }
        });
    }
}


