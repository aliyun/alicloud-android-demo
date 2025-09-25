package com.aliyun.httpdns.android.demo.webview;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import org.littleshoot.proxy.HttpFilters;
import org.littleshoot.proxy.HttpFiltersAdapter;
import org.littleshoot.proxy.HttpFiltersSourceAdapter;
import org.littleshoot.proxy.HttpProxyServer;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;

import java.net.InetSocketAddress;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

/**
 * 代理服务类，提供本地HTTP代理服务器功能
 */
public class ProxyService extends Service {

    private static final String TAG = "ProxyService";

    // 端口配置常量
    public static final int DEFAULT_PORT = 8888; // 默认端口，用于兼容性
    private static final int PROXY_PORT_MIN = 22000;
    private static final int PROXY_PORT_MAX = 32000;
    private static final int MAX_PORT_RETRY_ATTEMPTS = 3;

    // 当前使用的端口
    private int currentPort = DEFAULT_PORT;

    private HttpProxyServer proxyServer;
    private final ProxyBinder binder = new ProxyBinder();
    private boolean isRunning = false;
    private HttpDnsResolver httpDnsResolver;

    public class ProxyBinder extends Binder {
        public ProxyService getService() {
            return ProxyService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        httpDnsResolver = new HttpDnsResolver(this);
        Log.i(TAG, "ProxyService created");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopProxyServer();
        if (httpDnsResolver != null) {
            httpDnsResolver.cleanup();
        }
        Log.i(TAG, "ProxyService destroyed");
    }

    /**
     * 启动代理服务器（使用动态端口）
     */
    public boolean startProxyServer() {
        return startProxyServerWithRetry();
    }

    /**
     * 启动代理服务器，支持端口重试
     */
    private boolean startProxyServerWithRetry() {
        if (isRunning) {
            Log.w(TAG, "Proxy server is already running on port " + currentPort);
            return true;
        }

        for (int attempt = 0; attempt < MAX_PORT_RETRY_ATTEMPTS; attempt++) {
            int port = generateRandomPort();
            Log.i(TAG, "Attempting to start proxy server (attempt " + (attempt + 1) + "/" + MAX_PORT_RETRY_ATTEMPTS + ") on port: " + port);

            if (startProxyServerOnPort(port)) {
                return true;
            }

            cleanup();
            if (attempt < MAX_PORT_RETRY_ATTEMPTS - 1) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        Log.e(TAG, "Failed to start proxy server after " + MAX_PORT_RETRY_ATTEMPTS + " attempts");
        return false;
    }

    /**
     * 在指定端口启动代理服务器
     */
    private boolean startProxyServerOnPort(int port) {
        try {
            Log.i(TAG, "Starting LittleProxy server on port: " + port);
            Log.i(TAG, "Using DNS resolver: " + (httpDnsResolver.isHttpDnsReady() ? "HTTPDNS" : "System DNS"));

            currentPort = port;

            proxyServer = DefaultHttpProxyServer.bootstrap()
                .withPort(port)
                .withAddress(new InetSocketAddress("127.0.0.1", port))
                .withServerResolver(httpDnsResolver)
                .withConnectTimeout(10000)
                .withIdleConnectionTimeout(30000)
                .withFiltersSource(new HttpFiltersSourceAdapter() {
                    public HttpFilters filterRequest(HttpRequest originalRequest, InetSocketAddress clientAddress) {
                        return new HttpFiltersAdapter(originalRequest) {
                            @Override
                            public HttpResponse clientToProxyRequest(HttpObject httpObject) {
                                if (httpObject instanceof HttpRequest) {
                                    HttpRequest request = (HttpRequest) httpObject;
                                    String method = request.method().name();
                                    String uri = request.uri();
                                    String host = getHostFromRequest(request);
                                    Log.i(TAG, "[Proxy Request] " + method + " " + uri + " (Host: " + host + ")");
                                }
                                return null;
                            }

                            @Override
                            public HttpObject serverToProxyResponse(HttpObject httpObject) {
                                if (httpObject instanceof HttpResponse) {
                                    HttpResponse response = (HttpResponse) httpObject;
                                    if (response.status().code() >= 400) {
                                        Log.w(TAG, "[Server Response ERROR] " + response.status().code() + " " + response.status().reasonPhrase());
                                    }
                                }
                                return httpObject;
                            }

                            @Override
                            public void proxyToServerConnectionFailed() {
                                Log.e(TAG, "[Proxy Connection Failed] " + originalRequest.uri());
                                super.proxyToServerConnectionFailed();
                            }
                        };
                    }
                })
                .start();

            isRunning = true;
            Log.i(TAG, "LittleProxy server started successfully on port " + port);
            Log.i(TAG, "Proxy listening address: 127.0.0.1:" + port);
            return true;

        } catch (Exception e) {
            Log.e(TAG, "Failed to start LittleProxy server on port " + port, e);
            return false;
        }
    }

    /**
     * 生成随机端口
     */
    private int generateRandomPort() {
        int range = PROXY_PORT_MAX - PROXY_PORT_MIN;
        return PROXY_PORT_MIN + (int) (Math.random() * range);
    }

    /**
     * 停止代理服务器
     */
    public void stopProxyServer() {
        if (proxyServer != null && isRunning) {
            try {
                proxyServer.stop();
                Log.i(TAG, "LittleProxy server stopped");
            } catch (Exception e) {
                Log.e(TAG, "Error stopping LittleProxy server", e);
            } finally {
                proxyServer = null;
                isRunning = false;
            }
        }
    }

    /**
     * 清理代理服务器资源
     */
    private void cleanup() {
        if (proxyServer != null) {
            try {
                proxyServer.stop();
                Log.i(TAG, "LittleProxy server cleaned up");
            } catch (Exception e) {
                Log.e(TAG, "Error cleaning up LittleProxy server", e);
            } finally {
                proxyServer = null;
                isRunning = false;
            }
        }
    }

    /**
     * 从HTTP请求中提取Host
     */
    private String getHostFromRequest(HttpRequest request) {
        String host = request.headers().get("Host");
        if (host == null || host.isEmpty()) {
            // 尝试从URI中提取
            String uri = request.uri();
            if (uri != null && uri.startsWith("http")) {
                try {
                    java.net.URL url = new java.net.URL(uri);
                    host = url.getHost();
                } catch (Exception e) {
                    host = "unknown";
                }
            } else {
                host = "unknown";
            }
        }
        return host;
    }

    /**
     * 检查代理是否运行中
     */
    public boolean isProxyRunning() {
        return isRunning;
    }

    /**
     * 获取代理端口
     */
    public int getProxyPort() {
        return isRunning ? currentPort : -1;
    }

    /**
     * 获取代理地址
     */
    public String getProxyAddress() {
        return isRunning ? "127.0.0.1:" + currentPort : "";
    }
}
