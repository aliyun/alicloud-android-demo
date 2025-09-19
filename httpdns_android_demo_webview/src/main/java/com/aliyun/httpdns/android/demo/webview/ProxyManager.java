package com.aliyun.httpdns.android.demo.webview;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.os.Handler;
import android.os.Looper;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Objects;

/**
 * 代理管理器，管理代理服务的绑定和状态
 */
public class ProxyManager {

    private static final String TAG = "ProxyManager";

    private final Context context;
    private ProxyService proxyService;
    private boolean isBound = false;
    private StatusUpdateListener statusUpdateListener;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private boolean currentIsRunning = false;
    private String currentProxyAddress = "";
    private ProxyStatus currentProxyStatus = new ProxyStatus();

    public interface StatusUpdateListener {
        void onProxyStatusChanged(boolean isRunning);
        void onProxyAddressChanged(String address);
        void onProxyStatusUpdated(ProxyStatus status);
    }
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ProxyService.ProxyBinder binder = (ProxyService.ProxyBinder) service;
            proxyService = binder.getService();
            isBound = true;
            updateProxyStatus();
            Log.d(TAG, "Connected to ProxyService");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            proxyService = null;
            isBound = false;
            notifyStatusChange(false, "", new ProxyStatus());
            Log.d(TAG, "Disconnected from ProxyService");
        }
    };

    public ProxyManager(Context context) {
        this.context = context.getApplicationContext();
    }

    /**
     * 设置状态更新监听器
     */
    public void setStatusUpdateListener(StatusUpdateListener listener) {
        this.statusUpdateListener = listener;
    }

    /**
     * 绑定服务
     */
    public void bindService() {
        if (!isBound) {
            Intent intent = new Intent(context, ProxyService.class);
            context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    /**
     * 解绑服务
     */
    public void unbindService() {
        if (isBound) {
            context.unbindService(serviceConnection);
            isBound = false;
            proxyService = null;
        }
        // 清理监听器，避免Activity销毁后还在回调
        statusUpdateListener = null;
    }

    /**
     * 启动代理
     */
    public boolean startProxy() {
        if (proxyService != null) {
            boolean success = proxyService.startProxyServer();
            updateProxyStatus();
            return success;
        }
        return false;
    }

    /**
     * 停止代理
     */
    public void stopProxy() {
        if (proxyService != null) {
            proxyService.stopProxyServer();
            updateProxyStatus();
        }
    }

    /**
     * 更新代理状态
     */
    private void updateProxyStatus() {
        if (proxyService != null) {
            boolean isRunning = proxyService.isProxyRunning();
            String address = proxyService.getProxyAddress();
            notifyStatusChange(isRunning, address, new ProxyStatus(isRunning));
        }
    }

    /**
     * 通知状态变化（类似StateFlow发射）
     */
    private void notifyStatusChange(final boolean isRunning, final String address, final ProxyStatus status) {
        // 更新当前状态缓存
        boolean isRunningChanged = this.currentIsRunning != isRunning;
        boolean addressChanged = !Objects.equals(this.currentProxyAddress, address);
        boolean statusChanged = !Objects.equals(this.currentProxyStatus, status);
        this.currentIsRunning = isRunning;
        this.currentProxyAddress = address;
        this.currentProxyStatus = status;
        if (statusUpdateListener != null) {
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    // 再次检查监听器是否仍然有效
                    if (statusUpdateListener != null) {
                        if (isRunningChanged) {
                            statusUpdateListener.onProxyStatusChanged(isRunning);
                        }
                        if (addressChanged) {
                            statusUpdateListener.onProxyAddressChanged(address);
                        }
                        if (statusChanged) {
                            statusUpdateListener.onProxyStatusUpdated(status);
                        }
                    }
                }
            });
        }
    }

    /**
     * 获取代理主机
     */
    public String getProxyHost() {
        return "127.0.0.1";
    }

    /**
     * 获取代理端口
     */
    public int getProxyPort() {
        if (proxyService != null && isBound) {
            try {
                return proxyService.getProxyPort();
            } catch (Exception e) {
                Log.w(TAG, "Failed to get proxy port from service: " + e.getMessage());
            }
        }
        Log.d(TAG, "Service not available, returning default port");
        return ProxyService.DEFAULT_PORT;
    }

    /**
     * 检查代理是否运行中
     */
    public boolean isProxyRunning() {
        return proxyService != null && proxyService.isProxyRunning();
    }
    
    /**
     * 获取代理地址
     */
    public String getProxyAddress() {
        return proxyService != null ? proxyService.getProxyAddress() : "";
    }

    /**
     * 检查并恢复代理服务（热启动时调用）
     */
    public void checkAndRecoverProxy() {
        if (!currentIsRunning) {
            Log.d(TAG, "Proxy not expected to run, skipping check");
            return;
        }

        if (!isServiceRunning()) {
            Log.i(TAG, "ProxyService not running, attempting to restart");
            attemptRestartProxy();
            return;
        }

        try {
            boolean isRunning = proxyService.isProxyRunning();
            if (!isRunning) {
                Log.i(TAG, "ProxyService reports not running, attempting to restart");
                attemptRestartProxy();
            } else {
                Log.d(TAG, "Proxy is running normally");
            }
        } catch (Exception e) {
            Log.w(TAG, "Error checking proxy status: " + e.getMessage());
            attemptRestartProxy();
        }
    }

    /**
     * 检测ProxyService是否正在运行
     */
    private boolean isServiceRunning() {
        if (proxyService == null || !isBound) {
            Log.d(TAG, "Service reference is null or not bound");
            return false;
        }

        try {
            proxyService.getProxyPort();
        } catch (Exception e) {
            Log.w(TAG, "Service method call failed: " + e.getMessage());
            return false;
        }

        if (!isProxyPortReachable()) {
            Log.w(TAG, "Proxy port is not reachable");
            return false;
        }

        return true;
    }

    /**
     * 检查代理端口是否可连接
     */
    private boolean isProxyPortReachable() {
        if (proxyService == null) {
            return false;
        }

        int port = proxyService.getProxyPort();
        if (port == -1) {
            return false;
        }

        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress("127.0.0.1", port), 1000);
            Log.d(TAG, "Proxy port connection successful on port " + port);
            return true;
        } catch (Exception e) {
            Log.d(TAG, "Proxy port connection failed on port " + port + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * 尝试重启代理服务
     */
    private void attemptRestartProxy() {
        new Thread(() -> {
            try {
                boolean success = startProxy();
                if (!success) {
                    Log.e(TAG, "Proxy restart failed, clearing WebView proxy configuration");
                    clearWebViewProxy();
                }
            } catch (Exception e) {
                Log.e(TAG, "Proxy restart exception: " + e.getMessage());
                clearWebViewProxy();
            }
        }).start();
    }

    /**
     * 清除WebView的代理配置
     */
    private void clearWebViewProxy() {
        // 通知状态改变，确保WebView配置被清理
        mainHandler.post(() -> {
            if (statusUpdateListener != null) {
                statusUpdateListener.onProxyStatusChanged(false);
            }
        });
    }
}
