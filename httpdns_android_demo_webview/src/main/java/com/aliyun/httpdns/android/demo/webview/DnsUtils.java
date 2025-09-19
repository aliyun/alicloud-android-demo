package com.aliyun.httpdns.android.demo.webview;

/**
 * DNS工具类，包含DNS解析相关的数据类
 */
public class DnsUtils {
    
    /**
     * IP轮询信息 - 核心IP轮询功能
     */
    public static class IpRotationInfo {
        private final java.util.List<String> ips;
        private final int currentIndex;

        public IpRotationInfo(java.util.List<String> ips, int currentIndex) {
            this.ips = new java.util.ArrayList<>(ips);
            this.currentIndex = currentIndex;
        }

        public IpRotationInfo(java.util.List<String> ips) {
            this(ips, 0);
        }

        public String getNextIp() {
            return (!ips.isEmpty()) ? ips.get(currentIndex) : null;
        }

        public IpRotationInfo getNextRotation() {
            if (ips.isEmpty()) return this;
            int nextIndex = (currentIndex + 1) % ips.size();
            return new IpRotationInfo(ips, nextIndex);
        }

        public boolean hasMultipleIps() {
            return ips.size() > 1;
        }
    }
}
