package com.xiayiye5.xiayiye5ad;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by bytedance on 2019/08/13.
 * 竖版模板个性化视频
 */
public class DrawNativeExpressVideoActivity extends Activity {

    private static final String TAG = "DrawExpressActivity";
    private static final int TYPE_COMMON_ITEM = 1;
    private static final int TYPE_AD_ITEM = 2;
    private LinearLayout mBottomLayout;
    private RelativeLayout mTopLayout;
    private int[] videos = {R.raw.video11, R.raw.video12, R.raw.video13, R.raw.video14, R.raw.video_2};
    private TTAdNative mTTAdNative;
    private Context mContext;
    private List<Item> datas = new ArrayList<>();
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private FrameLayout frameLayout;
    private ProgressBar pb;
    private LinearLayout tvStoneGive;
    private ImageView ivClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置锁屏下可展示，此配置仅限测试调试使用，正式代码慎用
        UIUtils.setShowOnLocked(this);
        try {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } catch (Throwable ignore) {
        }
        setContentView(R.layout.activity_draw_native_video);
        pb = findViewById(R.id.pb);
        tvStoneGive = findViewById(R.id.tvStoneGive);
        frameLayout = findViewById(R.id.frameLayout);
        ivClose = findViewById(R.id.ivClose);
        if (NetworkUtils.getNetworkType(this) == NetworkUtils.NetworkType.NONE) {
            return;
        }
        mTTAdNative = TTAdManagerHolder.get().createAdNative(this);
        //在合适的时机申请权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题
        TTAdManagerHolder.get().requestPermissionIfNecessary(this);
        //加载开屏广告
        mContext = this;

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadExpressDrawNativeAd();
            }
        }, 500);
    }


    private void loadExpressDrawNativeAd() {
        //step3:创建广告请求参数AdSlot,具体参数含义参考文档
        float expressViewWidth = UIUtils.getScreenWidthDp(this);
        float expressViewHeight = UIUtils.getHeight(this);
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId("945457300")
                .setExpressViewAcceptedSize(500, 500)
                .setSupportDeepLink(true)
                .setExpressViewAcceptedSize(expressViewWidth, expressViewHeight) //期望模板广告view的size,单位dp
                .setAdCount(3) //请求广告数量为1到3条
                .build();
        //step4:请求广告,对请求回调的广告作渲染处理
        mTTAdNative.loadExpressDrawFeedAd(adSlot, new TTAdNative.NativeExpressAdListener() {
            @Override
            public void onError(int code, String message) {
                Log.d(TAG, message);
                showToast(message);
            }

            @Override
            public void onNativeExpressAdLoad(List<TTNativeExpressAd> ads) {
                if (ads == null || ads.isEmpty()) {
                    Toast.makeText(DrawNativeExpressVideoActivity.this, " ad is null!", Toast.LENGTH_LONG).show();
                    return;
                }
                //显示到主页面
                View expressAdView = ads.get(0).getExpressAdView();
                frameLayout.addView(expressAdView);
                for (final TTNativeExpressAd ad : ads) {
                    //点击监听器必须在getAdView之前调
                    ad.setVideoAdListener(new TTNativeExpressAd.ExpressVideoAdListener() {
                        @Override
                        public void onVideoLoad() {

                        }

                        @Override
                        public void onVideoError(int errorCode, int extraCode) {

                        }

                        @Override
                        public void onVideoAdStartPlay() {

                        }

                        @Override
                        public void onVideoAdPaused() {

                        }

                        @Override
                        public void onVideoAdContinuePlay() {

                        }

                        @Override
                        public void onProgressUpdate(long current, long duration) {
                            pb.setMax((int) duration);
                            pb.setProgress((int) current);
                        }

                        @Override
                        public void onVideoAdComplete() {
                            Toast.makeText(DrawNativeExpressVideoActivity.this, "视频广告播放进度完成", Toast.LENGTH_LONG).show();
                            Log.e("视频广告播放进度", "完成");
                            //显示关闭按钮
                            ivClose.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onClickRetry() {
                            Toast.makeText(DrawNativeExpressVideoActivity.this, " onClickRetry !", Toast.LENGTH_LONG).show();
                            Log.d("drawss", "onClickRetry!");
                        }
                    });
                    ad.setCanInterruptVideoPlay(true);
                    ad.setExpressInteractionListener(new TTNativeExpressAd.ExpressAdInteractionListener() {
                        @Override
                        public void onAdClicked(View view, int type) {

                        }

                        @Override
                        public void onAdShow(View view, int type) {
                            tvStoneGive.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onRenderFail(View view, String msg, int code) {

                        }

                        @Override
                        public void onRenderSuccess(View view, float width, float height) {
                            Toast.makeText(DrawNativeExpressVideoActivity.this, "渲染成功", Toast.LENGTH_LONG).show();
//                            int random = (int) (Math.random() * 100);
//                            int index = random % videos.length;
//                            if (index == 0) {
//                                index++;
//                            }
//                            datas.add(index, new Item(TYPE_AD_ITEM, ad, -1, -1));
                        }
                    });
                    ad.render();
                }


            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    public void closeMySelf(View view) {
        finish();
    }

    private static class Item {
        public int type = 0;
        public TTNativeExpressAd ad;
        public int videoId;
        public int ImgId;

        public Item(int type, TTNativeExpressAd ad, int videoId, int imgId) {
            this.type = type;
            this.ad = ad;
            this.videoId = videoId;
            ImgId = imgId;
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
}
