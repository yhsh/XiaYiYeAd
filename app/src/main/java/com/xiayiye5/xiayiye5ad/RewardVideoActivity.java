package com.xiayiye5.xiayiye5ad;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTRewardVideoAd;

/**
 * Created by bytedance on 2018/2/1.
 */

public class RewardVideoActivity extends Activity {
    private static final String TAG = "RewardVideoActivity";
    private Button mLoadAd;
    private Button mLoadAdVertical;
    private Button mShowAd;
    private TTAdNative mTTAdNative;
    private TTRewardVideoAd mttRewardVideoAd;
    private String mHorizontalCodeId = "945457302";
    private String mVerticalCodeId = "945457299";
    //是否请求模板广告
    private boolean mIsExpress = true;
    //视频是否加载完成
    private boolean mIsLoaded = false;


    @SuppressWarnings("RedundantCast")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置锁屏下可展示，此配置仅限测试调试使用，正式代码慎用
        UIUtils.setShowOnLocked(this);
        setContentView(R.layout.activity_reward_video);
        mLoadAd = (Button) findViewById(R.id.btn_reward_load);
        mLoadAdVertical = (Button) findViewById(R.id.btn_reward_load_vertical);
        mShowAd = (Button) findViewById(R.id.btn_reward_show);
        //step1:初始化sdk
        TTAdManager ttAdManager = TTAdManagerHolder.get();
        //step2:(可选，强烈建议在合适的时机调用):申请部分权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题。
        TTAdManagerHolder.get().requestPermissionIfNecessary(this);
        //step3:创建TTAdNative对象,用于调用广告请求接口
        mTTAdNative = ttAdManager.createAdNative(getApplicationContext());
        initClickEvent();
    }

    private void initClickEvent() {
        mLoadAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadAd(mHorizontalCodeId, TTAdConstant.HORIZONTAL);
            }
        });
        mLoadAdVertical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadAd(mVerticalCodeId, TTAdConstant.VERTICAL);
            }
        });
    }

    private boolean mHasShowDownloadActive = false;

    private void loadAd(final String codeId, int orientation) {
        //step4:创建广告请求参数AdSlot,具体参数含义参考文档
        AdSlot adSlot;
        if (mIsExpress) {
            //个性化模板广告需要传入期望广告view的宽、高，单位dp，
            adSlot = new AdSlot.Builder()
                    .setCodeId(codeId)
                    .setAdCount(5)
                    .setSupportDeepLink(true)
                    .setRewardName("原力奖励") //奖励的名称
                    .setRewardAmount(109)  //奖励的数量
                    //模板广告需要设置期望个性化模板广告的大小,单位dp,激励视频场景，只要设置的值大于0即可
                    .setExpressViewAcceptedSize(500, 500)
                    .setUserID("user123")//用户id,必传参数
                    .setMediaExtra("media_extra") //附加参数，可选
                    .setOrientation(orientation) //必填参数，期望视频的播放方向：TTAdConstant.HORIZONTAL 或 TTAdConstant.VERTICAL
                    .build();
        } else {
            //模板广告需要设置期望个性化模板广告的大小,单位dp,代码位是否属于个性化模板广告，请在穿山甲平台查看
            adSlot = new AdSlot.Builder()
                    .setCodeId(codeId)
                    .setSupportDeepLink(true)
                    .setRewardName("原力奖励") //奖励的名称
                    .setRewardAmount(3000)  //奖励的数量
                    .setUserID("user123")//用户id,必传参数
                    .setMediaExtra("media_extra") //附加参数，可选
                    .setOrientation(orientation) //必填参数，期望视频的播放方向：TTAdConstant.HORIZONTAL 或 TTAdConstant.VERTICAL
                    .build();
        }

        //step5:请求广告
        mTTAdNative.loadRewardVideoAd(adSlot, new TTAdNative.RewardVideoAdListener() {
            @Override
            public void onError(int code, String message) {
                Log.e(TAG, "Callback --> onError: " + code + ", " + String.valueOf(message));
                Toast.makeText(RewardVideoActivity.this, message, Toast.LENGTH_LONG).show();
            }

            //视频广告加载后，视频资源缓存到本地的回调，在此回调后，播放本地视频，流畅不阻塞。
            @Override
            public void onRewardVideoCached() {
                Log.e(TAG, "Callback --> onRewardVideoCached");
                mIsLoaded = true;
                Toast.makeText(RewardVideoActivity.this, "Callback --> rewardVideoAd video cached", Toast.LENGTH_LONG).show();
                //开始播放广告
                if (mttRewardVideoAd != null && mIsLoaded) {
                    //step6:在获取到广告后展示,强烈建议在onRewardVideoCached回调后，展示广告，提升播放体验
                    //该方法直接展示广告
//                    mttRewardVideoAd.showRewardVideoAd(RewardVideoActivity.this);
                    //展示广告，并传入广告展示的场景
                    mttRewardVideoAd.showRewardVideoAd(RewardVideoActivity.this, TTAdConstant.RitScenes.CUSTOMIZE_SCENES, "scenes_test");

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Log.i("kk", ActivityManager.getInstance().getCurrentActivity().getComponentName().getClassName() + "pppp");
                            addViewToContent(ActivityManager.getInstance().getCurrentActivity());
                        }
                    }, 300);

                    mttRewardVideoAd = null;
                } else {
                    Toast.makeText(RewardVideoActivity.this, "请先加载广告", Toast.LENGTH_LONG).show();
                }
            }

            //视频广告的素材加载完毕，比如视频url等，在此回调后，可以播放在线视频，网络不好可能出现加载缓冲，影响体验。
            @Override
            public void onRewardVideoAdLoad(TTRewardVideoAd ad) {
                Log.e(TAG, "Callback --> onRewardVideoAdLoad");

                Toast.makeText(RewardVideoActivity.this, "rewardVideoAd loaded 广告类型：" + getAdType(ad.getRewardVideoAdType()), Toast.LENGTH_LONG).show();
                mIsLoaded = false;
                mttRewardVideoAd = ad;
                mttRewardVideoAd.setRewardAdInteractionListener(new TTRewardVideoAd.RewardAdInteractionListener() {

                    @Override
                    public void onAdShow() {
                        Log.d(TAG, "Callback --> rewardVideoAd show");
                        Toast.makeText(RewardVideoActivity.this, "rewardVideoAd show", Toast.LENGTH_LONG).show();
                        //添加View到视频上面
//                        startActivity(new Intent(RewardVideoActivity.this, MyDialogActivity.class));
                    }

                    @Override
                    public void onAdVideoBarClick() {
                        Log.d(TAG, "Callback --> rewardVideoAd bar click");
                        Toast.makeText(RewardVideoActivity.this, "rewardVideoAd bar click", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onAdClose() {
                        Log.d(TAG, "Callback --> rewardVideoAd close");
                        Toast.makeText(RewardVideoActivity.this, "rewardVideoAd close", Toast.LENGTH_LONG).show();
                    }

                    //视频播放完成回调
                    @Override
                    public void onVideoComplete() {
                        Log.d(TAG, "Callback --> rewardVideoAd complete");
                        Toast.makeText(RewardVideoActivity.this, "rewardVideoAd complete", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onVideoError() {
                        Log.e(TAG, "Callback --> rewardVideoAd error");
                        Toast.makeText(RewardVideoActivity.this, "rewardVideoAd error", Toast.LENGTH_LONG).show();
                    }

                    //视频播放完成后，奖励验证回调，rewardVerify：是否有效，rewardAmount：奖励梳理，rewardName：奖励名称
                    @Override
                    public void onRewardVerify(boolean rewardVerify, int rewardAmount, String rewardName) {
                        String logString = "verify:" + rewardVerify + " amount:" + rewardAmount +
                                " name:" + rewardName;
                        Log.e(TAG, "Callback --> " + logString);
                        Toast.makeText(RewardVideoActivity.this, logString, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onSkippedVideo() {
                        Log.e(TAG, "Callback --> rewardVideoAd has onSkippedVideo");
                        Toast.makeText(RewardVideoActivity.this, "rewardVideoAd has onSkippedVideo", Toast.LENGTH_LONG).show();
                    }
                });
                mttRewardVideoAd.setDownloadListener(new TTAppDownloadListener() {
                    @Override
                    public void onIdle() {
                        mHasShowDownloadActive = false;
                    }

                    @Override
                    public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
                        Log.d("DML", "onDownloadActive==totalBytes=" + totalBytes + ",currBytes=" + currBytes + ",fileName=" + fileName + ",appName=" + appName);

                        if (!mHasShowDownloadActive) {
                            mHasShowDownloadActive = true;
                            Toast.makeText(RewardVideoActivity.this, "下载中，点击下载区域暂停", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
                        Log.d("DML", "onDownloadPaused===totalBytes=" + totalBytes + ",currBytes=" + currBytes + ",fileName=" + fileName + ",appName=" + appName);
                        Toast.makeText(RewardVideoActivity.this, "下载暂停，点击下载区域继续", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
                        Log.d("DML", "onDownloadFailed==totalBytes=" + totalBytes + ",currBytes=" + currBytes + ",fileName=" + fileName + ",appName=" + appName);
                        Toast.makeText(RewardVideoActivity.this, "下载失败，点击下载区域重新下载", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onDownloadFinished(long totalBytes, String fileName, String appName) {
                        Log.d("DML", "onDownloadFinished==totalBytes=" + totalBytes + ",fileName=" + fileName + ",appName=" + appName);
                        Toast.makeText(RewardVideoActivity.this, "下载完成，点击下载区域重新下载", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onInstalled(String fileName, String appName) {
                        Log.d("DML", "onInstalled==" + ",fileName=" + fileName + ",appName=" + appName);
                        Toast.makeText(RewardVideoActivity.this, "安装完成，点击下载区域打开", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    public void addViewToContent(final Activity activity) {
        if (activity == null) {
            return;
        }

        activity.getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                FrameLayout frameLayout = (FrameLayout) activity.findViewById(Window.ID_ANDROID_CONTENT);
                View addView = View.inflate(RewardVideoActivity.this, R.layout.add_view, null);
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        Gravity.RIGHT);
                layoutParams.topMargin = 300;
                addView.setLayoutParams(layoutParams);
                frameLayout.addView(addView);
                addView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activity.finish();
                    }
                });
            }
        });
    }

    private String getAdType(int type) {
        switch (type) {
            case TTAdConstant.AD_TYPE_COMMON_VIDEO:
                return "普通激励视频，type=" + type;
            case TTAdConstant.AD_TYPE_PLAYABLE_VIDEO:
                return "Playable激励视频，type=" + type;
            case TTAdConstant.AD_TYPE_PLAYABLE:
                return "纯Playable，type=" + type;
            default:
                break;
        }

        return "未知类型+type=" + type;
    }

    public void xuanRan(View view) {
        startActivity(new Intent(this, DrawNativeExpressVideoActivity.class));
    }
}
