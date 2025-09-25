package com.aliyun.httpdns.android.demo.webview;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.webkit.ProxyConfig;
import androidx.webkit.ProxyController;
import androidx.webkit.WebViewFeature;
import java.util.concurrent.Executor;

/**
 * 主Activity，提供WebView代理演示功能
 */
public class MainActivity extends AppCompatActivity implements ProxyManager.StatusUpdateListener {

    private static final String TAG = "MainActivity";

    private Button btnToggleProxy;
    private EditText etUrl;
    private Button btnLoad;
    private Button btnTestUrls;
    private WebView webView;
    private ProxyManager proxyManager;

    private final Executor mainExecutor = new Executor() {
        private final Handler handler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(Runnable command) {
            handler.post(command);
        }
    };

    private boolean isProxyRunning = false;
    private boolean isProxyConfigured = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initProxyManager();
        setupWebView();
        setupClickListeners();
    }

    private void initViews() {
        btnToggleProxy = findViewById(R.id.btnToggleProxy);
        etUrl = findViewById(R.id.etUrl);
        btnLoad = findViewById(R.id.btnLoad);
        btnTestUrls = findViewById(R.id.btnTestUrls);
        webView = findViewById(R.id.webView);
    }

    private void initProxyManager() {
        proxyManager = new ProxyManager(this);
        proxyManager.setStatusUpdateListener(this);
        proxyManager.bindService();
    }

    private void setupWebView() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.d(TAG, "Page started loading: " + url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d(TAG, "Page finished loading: " + url);
            }
        });
    }

    private void setupClickListeners() {
        btnToggleProxy.setOnClickListener(v -> toggleProxy());
        btnLoad.setOnClickListener(v -> loadUrl());
        btnTestUrls.setOnClickListener(v -> showTestUrlDialog());
    }

    private void toggleProxy() {
        if (isProxyRunning) {
            proxyManager.stopProxy();
        } else {
            boolean success = proxyManager.startProxy();
            if (!success) {
                Toast.makeText(this, "代理启动失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 加载URL
     */
    private void loadUrl() {
        String url = etUrl.getText().toString().trim();
        if (url.isEmpty()) {
            Toast.makeText(this, "请输入网址", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "https://" + url;
            etUrl.setText(url);
        }

        Log.i(TAG, "Loading URL: " + url);
        webView.loadUrl(url);
        configureWebViewProxy();
    }

    /**
     * 显示测试URL对话框
     */
    private void showTestUrlDialog() {
        final String[] testUrls = {
            "https://www.baidu.com",
            "https://www.aliyun.com",
            "https://www.taobao.com"
        };

        final String[] testNames = {
            "百度搜索",
            "阿里云",
            "淘宝网"
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择测试URL");
        builder.setItems(testNames, (dialog, which) -> {
            etUrl.setText(testUrls[which]);
            loadUrl();
        });
        builder.setNegativeButton("关闭", null);
        builder.show();
    }
    
    /**
     * 配置WebView代理
     */
    private void configureWebViewProxy() {
        configureWebViewProxy(false);
    }

    /**
     * 配置WebView代理
     * @param force 是否强制重新配置
     */
    private void configureWebViewProxy(boolean force) {
        if (isFinishing() || isDestroyed()) {
            Log.d(TAG, "Activity destroyed, skip proxy configuration");
            return;
        }

        if (!WebViewFeature.isFeatureSupported(WebViewFeature.PROXY_OVERRIDE)) {
            Log.w(TAG, "WebView proxy override not supported");
            return;
        }

        if (webView == null) {
            Log.w(TAG, "WebView is null, skip proxy configuration");
            return;
        }

        // 如果proxy状态没有改变且已经配置过，跳过重复配置
        if (!force && isProxyConfigured == isProxyRunning) {
            Log.d(TAG, "Proxy already configured correctly, skip reconfiguration");
            return;
        }

        if (isProxyRunning) {
            String proxyHost = proxyManager.getProxyHost();
            int proxyPort = proxyManager.getProxyPort();
            String proxyAddress = proxyHost + ":" + proxyPort;

            Log.i(TAG, "Configuring WebView proxy: " + proxyAddress);

            try {
                ProxyConfig proxyConfig = new ProxyConfig.Builder()
                    .addProxyRule(proxyAddress, ProxyConfig.MATCH_HTTP)
                    .addProxyRule(proxyAddress, ProxyConfig.MATCH_HTTPS)
                    .addDirect(ProxyConfig.MATCH_ALL_SCHEMES)
                    .build();

                ProxyController.getInstance().setProxyOverride(proxyConfig, mainExecutor, () -> {
                    Log.i(TAG, "WebView proxy configured successfully: " + proxyAddress);
                    isProxyConfigured = true;
                });
            } catch (Exception e) {
                Log.e(TAG, "Failed to configure WebView proxy", e);
                isProxyConfigured = false;
            }
        } else {
            Log.i(TAG, "Clearing WebView proxy configuration");

            try {
                ProxyController.getInstance().clearProxyOverride(mainExecutor, () -> {
                    Log.i(TAG, "WebView proxy configuration cleared");
                    isProxyConfigured = false;
                });
            } catch (Exception e) {
                Log.e(TAG, "Failed to clear WebView proxy", e);
                isProxyConfigured = true; // 清理失败，保持原有状态
            }
        }
    }
    

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (proxyManager != null) {
            proxyManager.unbindService();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called");

        if (proxyManager == null) {
            Log.w(TAG, "ProxyManager is null, skipping proxy check");
            return;
        }

        // 检查并恢复代理服务（热启动恢复逻辑）
        proxyManager.checkAndRecoverProxy();
    }

    @Override
    public void onProxyStatusChanged(boolean isRunning) {
        this.isProxyRunning = isRunning;

        if (!isFinishing() && !isDestroyed()) {
            runOnUiThread(() -> {
                if (!isFinishing() && !isDestroyed() && btnToggleProxy != null) {
                    if (isRunning) {
                        btnToggleProxy.setText(getString(R.string.stop_proxy));
                    } else {
                        btnToggleProxy.setText(getString(R.string.start_proxy));
                    }
                    // 代理状态改变时强制重新配置
                    configureWebViewProxy(true);
                }
            });
        }
    }

    @Override
    public void onProxyAddressChanged(String address) {
        // 地址变更处理
        Log.d(TAG, "Proxy address changed: " + address);
    }

    @Override
    public void onProxyStatusUpdated(ProxyStatus status) {
        // 状态对象更新处理
        Log.d(TAG, "Proxy status updated: " + (status != null ? status.isRunning() : "null"));
    }
}