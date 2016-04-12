package alibaba.man_android_demo;

import android.content.Context;

import com.alibaba.sdk.android.man.MANHitBuilders;
import com.alibaba.sdk.android.man.MANPageHitBuilder;
import com.alibaba.sdk.android.man.MANService;
import com.alibaba.sdk.android.man.MANServiceProvider;
import com.alibaba.sdk.android.man.customperf.MANCustomPerformanceHitBuilder;
import com.alibaba.sdk.android.man.network.MANNetworkErrorCodeBuilder;
import com.alibaba.sdk.android.man.network.MANNetworkErrorInfo;
import com.alibaba.sdk.android.man.network.MANNetworkPerformanceHitBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class MANAndroidDemo {

    public static void main(Context ctx) {

        // 获取MAN服务
        MANService manService = MANServiceProvider.getService();

        /**
         * 业务数据埋点
         */
        // 注册用户埋点
        manService.getMANAnalytics().userRegister("usernick");
        // 用户登录埋点
        manService.getMANAnalytics().updateUserAccount("usernick", "userid");
        // 用户注销埋点
        manService.getMANAnalytics().updateUserAccount("", "");

        /**
         * 使用页面基础打点类进行页面事件埋点，见文档4.2.3
         */
        // 获取基础页面打点对象
        MANPageHitBuilder pageHitBuilder = new MANPageHitBuilder("pageName");
        // 设置来源页面名称
        pageHitBuilder.setReferPage("referPageName");
        // 设置页面停留时间
        pageHitBuilder.setDurationOnPage(100);
        // 设置页面属性
        pageHitBuilder.setProperty("pageKey", "pageValue");
        // Map<String, String> properties = new HashMap<>();
        // properties.put("key1", "key2");
        // pageHitBuilder.setProperties(properties);
        // 上报页面埋点数据
        manService.getMANAnalytics().getDefaultTracker().send(pageHitBuilder.build());

        /**
         * 进行网络事件埋点，见文档5.1和5.2
         */
        String urlString = "http://www.aliyun.com";
        MANNetworkPerformanceHitBuilder networkPerformanceHitBuilder = new MANNetworkPerformanceHitBuilder("www.aliyun.com", "GET");

        try {
            URL url = new URL(urlString);
            byte[] buf = new byte[64 * 1024];

            // 打点记录请求开始
            networkPerformanceHitBuilder.hitRequestStart();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // 建立连接
            conn.connect();
            // 打点记录建连时间
            networkPerformanceHitBuilder.hitConnectFinished();
            // 开始获取响应内容
            int responseCode = conn.getResponseCode();
            // 打点记录首包时间
            networkPerformanceHitBuilder.hitRecievedFirstByte();
            long totalBytes = 0;
            int len = 0;
            if (responseCode == 200) {
                // 读尽响应内容
                InputStream in = conn.getInputStream();
                while ((len = in.read(buf)) != -1) {
                    totalBytes += len;
                }
                in.close();
            }
            // 附带额外需要上报信息
            networkPerformanceHitBuilder.withExtraInfo("name1", "value1");
            // 打点标记请求结束
            networkPerformanceHitBuilder.hitRequestEndWithLoadBytes(totalBytes);
        } catch (IOException e) {
            e.printStackTrace();
            // 按照文档5.1.3的说明选择默认网络异常类型或者自定义网络异常类型来上报网络异常
            MANNetworkErrorInfo errorInfo = MANNetworkErrorCodeBuilder.buildIOException()
                    .withExtraInfo("error_url", urlString)
                    .withExtraInfo("other_info", "value");
            // MANNetworkErrorInfo errorInfo = MANNetworkErrorCodeBuilder.buildCustomErrorCode(1001)
            //        .withExtraInfo("error_url", urlString)
            //        .withExtraInfo("other_info", "value");
            // 打点，记录出错情况
            networkPerformanceHitBuilder.hitRequestEndWithError(errorInfo);
        }
        // 上报网络性能事件打点数据
        manService.getMANAnalytics().getDefaultTracker().send(networkPerformanceHitBuilder.build());

        /**
         * 进行自定义性能事件埋点
         */
        String labelKey = "fibonacci";
        MANCustomPerformanceHitBuilder performanceHitBuilder = new MANCustomPerformanceHitBuilder(labelKey);
        // 记录自定义性能事件起始时间
        performanceHitBuilder.hitStart();
        fibonacci(30);
        // 记录自定义性能事件结束时间
        performanceHitBuilder.hitEnd();
        // 设置时长方法2
        // long timeCost = 0;
        // performanceHitBuilder.setDuration(timeCost);
        performanceHitBuilder.withExtraInfo("EXTRA_INFO_KEY1", "EXTRA_INFO_VALUE")
                .withExtraInfo("EXTRA_INFO_KEY2", "EXTRA_INFO_VALUE");
        // 上报自定义性能事件打点数据
        manService.getMANAnalytics().sendCustomPerformance(performanceHitBuilder.build());

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
        // 上报自定义事件打点数据
        manService.getMANAnalytics().getDefaultTracker().send(hitBuilder.build());
    }

    public static int fibonacci(int n) {
        if(n <= 1) return 1;
        return fibonacci(n-1) + fibonacci(n-2);
    }
}
