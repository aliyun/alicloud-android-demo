package alibaba.man_android_demo;

import android.content.Context;

import com.alibaba.sdk.android.AlibabaSDK;
import com.alibaba.sdk.android.man.MANHitBuilders;
import com.alibaba.sdk.android.man.MANService;
import com.alibaba.sdk.android.man.model.MANCustomPerformance;
import com.alibaba.sdk.android.man.model.MANNetworkErrorInfo;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class MANAndroidDemo {

    public static void main(Context ctx) {

        /**
         * 初始化Mobile Analytics服务
         */
        // 初始化OneSDK
        AlibabaSDK.asyncInit(ctx);
        // 获取MAN服务
        MANService manService = AlibabaSDK.getService(MANService.class);

        // 若需要关闭 SDK 的自动异常捕获功能可进行如下操作,详见文档5.4
        // manService.getMANAnalytics().turnOffCrashHandler();

        // 通过此接口关闭页面自动打点功能，详见文档4.2
        // manService.getMANAnalytics().turnOffAutoPageTrack();

        // 打开调试日志
        manService.getMANAnalytics().turnOnDebug();

        // 设置渠道（用以标记该app的分发渠道名称），如果不关心可以不设置即不调用该接口，渠道设置将影响控制台【渠道分析】栏目的报表展现。如果文档3.3章节更能满足您渠道配置的需求，就不要调用此方法，按照3.3进行配置即可
        // manService.getMANAnalytics().setChannel("某渠道");

        // 若AndroidManifest.xml 中的 android:versionName 不能满足需求，可在此指定
        // manService.getMANAnalytics().setAppVersion("3.1.1");

        /**
         * 业务数据埋点
         */
        // 注册用户埋点
        manService.getMANAnalytics().userRegister("usernick");
        // 用户登录埋点
        manService.getMANAnalytics().updateUserAccount("usernick", "user id");
        // 用户注销埋点
        manService.getMANAnalytics().updateUserAccount("", "");

        /**
         * 进行网络事件埋点
         */
        try {
            URL url = new URL("http://www.aliyun.com");
            Map<String, String> requestInfo = new HashMap<String, String>();
            requestInfo.put("Host", url.getHost());
            requestInfo.put("Method", "GET");

            // 打点记录请求开始
            manService.getMANNetwork().requestStart(requestInfo);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // 建立连接
            conn.connect();
            // 打点记录建连时间
            manService.getMANNetwork().requestFinishConnect();
            // 开始获取响应内容
            int responseCode = conn.getResponseCode();
            // 打点记录首包时间
            manService.getMANNetwork().requestReceiveFirstByte();
            int totalBytes = 0, loadBytes;
            if (responseCode == 200) {
                // 读尽响应内容
                InputStream in = conn.getInputStream();
                while ((loadBytes = in.read()) != -1) {
                    totalBytes += loadBytes;
                }
                in.close();
            }

            // 打点标记请求结束
            manService.getMANNetwork().requestEnd(totalBytes);
        } catch (IOException e) {
            e.printStackTrace();
            // 按照文档5.1.3的说明选择默认网络异常类型或者自定义网络异常类型来上报网络异常
            MANNetworkErrorInfo errorInfo = MANNetworkErrorInfo.MANNetworkErrorCodeBuilder.buildIOException()
                    .withExtraInfo("error_url", "www.aliyun.com");
            //打点，记录出错情况
            manService.getMANNetwork().reportRequestError(errorInfo);
        }


        /**
         * 进行自定义性能事件埋点
         */
        String labelKey = "fibonacci";
        MANCustomPerformance.MANCustomPerformanceBuilder performanceBuilder = new MANCustomPerformance.MANCustomPerformanceBuilder(labelKey);
        // 记录自定义性能事件起始时间
        performanceBuilder.hitStart();
        fibonacci(100);
        // 记录自定义性能事件结束时间
        performanceBuilder.hitEnd();
        // 上报自定义
        manService.getMANAnalytics().sendCustomPerformance(performanceBuilder.build());

        /**
         * 进行自定义事件（一般上报事件）埋点
         */
        // 事件名称：play_music
        MANHitBuilders.MANCustomHitBuilder hitBuilder = new MANHitBuilders.MANCustomHitBuilder("playmusic");
        // 可使用如下接口设置时长：3分钟
        hitBuilder.setDurationOnEvent(3 * 60 * 1000);
        // 设置关联的页面名称：聆听
        hitBuilder.setEventPage("Listen");
        // 设置属性：类型摇滚
        hitBuilder.setProperty("type", "rock");
        // 设置属性：歌曲标题
        hitBuilder.setProperty("title", "wonderful tonight");
        // 发送自定义事件打点
        manService.getMANAnalytics().getDefaultTracker().send(hitBuilder.build());
    }

    public static int fibonacci(int n) {
        if(n <= 1) return 1;
        return fibonacci(n-1) + fibonacci(n-2);
    }
}
