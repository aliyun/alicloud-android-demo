package com.aliyun.httpdns.android.demo.webview;

import android.app.Application;
import android.util.Log;
import com.alibaba.sdk.android.httpdns.HttpDns;
import com.alibaba.sdk.android.httpdns.HttpDnsService;
import com.alibaba.sdk.android.httpdns.InitConfig;

/**
 * 应用程序入口类，负责全局初始化HTTPDNS等服务
 */
public class WebviewProxyApplication extends Application {

    private static final String TAG = "WebviewProxyApp";

    public static String accountId = "139450";
    public static String secretKey = "807a19762f8eaefa8563489baf198535";
    public static boolean enabled = true;
    public static int connectTimeout = 5000;
    public static int readTimeout = 5000;
    public static long cacheTtl = 300_000L;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Application starting, initializing services");
        initHttpDns();
        Log.i(TAG, "Application initialization completed");
    }

    /**
     * 检查配置是否有效
     */
    public static boolean isValid() {
        return enabled && accountId != null && !accountId.trim().isEmpty() &&
               !accountId.equals("YOUR_ACCOUNT_ID");
    }
    
    /**
     * 获取配置信息
     */
    public static String getConfigInfo() {
        return "HTTPDNS Configuration:\n" +
               "- Enabled: " + enabled + "\n" +
               "- AccountID: " + (accountId.equals("YOUR_ACCOUNT_ID") ? "NOT_CONFIGURED" : "***") + "\n" +
               "- Timeout: " + connectTimeout + "ms / " + readTimeout + "ms\n" +
               "- Cache TTL: " + (cacheTtl / 1000) + "s";
    }

    /**
     * 初始化阿里云HTTPDNS服务
     */
    private void initHttpDns() {
        try {
            if (!isValid()) {
                Log.w(TAG, "HTTPDNS config invalid, skipping initialization");
                return;
            }

            Log.i(TAG, "Initializing HTTPDNS");
            Log.i(TAG, "Config: " + getConfigInfo());

            InitConfig.Builder configBuilder = new InitConfig.Builder()
                .setContext(this)
                .setTimeout(connectTimeout)
                .setEnableCacheIp(true)
                .setEnableExpiredIp(true);

            if (secretKey != null && !secretKey.trim().isEmpty()) {
                try {
                    configBuilder.setSecretKey(secretKey);
                    Log.d(TAG, "SecretKey configured successfully");
                } catch (Exception e) {
                    Log.w(TAG, "SecretKey configuration failed: " + e.getMessage());
                }
            } else {
                Log.w(TAG, "SecretKey not configured");
            }

            InitConfig config = configBuilder.build();
            HttpDns.init(accountId, config);
            HttpDnsService httpdnsService = HttpDns.getService(accountId);

            if (httpdnsService != null) {
                Log.i(TAG, "HTTPDNS SDK initialized successfully");
                Log.i(TAG, "AccountID: " + accountId);
            } else {
                Log.e(TAG, "HTTPDNS service instance creation failed");
            }

        } catch (Exception e) {
            Log.e(TAG, "HTTPDNS initialization failed", e);
        }
    }
}
