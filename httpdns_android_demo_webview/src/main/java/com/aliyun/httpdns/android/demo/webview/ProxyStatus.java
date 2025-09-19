package com.aliyun.httpdns.android.demo.webview;

/**
 * 代理状态数据类
 */
public class ProxyStatus {
    
    private boolean isRunning = false;

    public ProxyStatus() {}

    public ProxyStatus(boolean isRunning) {
        this.isRunning = isRunning;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }
}
